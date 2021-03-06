package net.minecraft.client.entity.player;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.BubbleColumnAmbientSoundHandler;
import net.minecraft.client.audio.ElytraSound;
import net.minecraft.client.audio.IAmbientSoundHandler;
import net.minecraft.client.audio.RidingMinecartTickableSound;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.audio.UnderwaterAmbientSoundHandler;
import net.minecraft.client.audio.UnderwaterAmbientSounds;
import net.minecraft.client.gui.screen.CommandBlockScreen;
import net.minecraft.client.gui.screen.EditBookScreen;
import net.minecraft.client.gui.screen.EditMinecartCommandBlockScreen;
import net.minecraft.client.gui.screen.EditSignScreen;
import net.minecraft.client.gui.screen.EditStructureScreen;
import net.minecraft.client.gui.screen.JigsawScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.client.util.ClientRecipeBook;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IJumpingMount;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.Pose;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.play.client.CAnimateHandPacket;
import net.minecraft.network.play.client.CChatMessagePacket;
import net.minecraft.network.play.client.CClientStatusPacket;
import net.minecraft.network.play.client.CCloseWindowPacket;
import net.minecraft.network.play.client.CEntityActionPacket;
import net.minecraft.network.play.client.CInputPacket;
import net.minecraft.network.play.client.CMoveVehiclePacket;
import net.minecraft.network.play.client.CPlayerAbilitiesPacket;
import net.minecraft.network.play.client.CPlayerDiggingPacket;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.network.play.client.CRecipeInfoPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.stats.StatisticsManager;
import net.minecraft.tags.FluidTags;
import net.minecraft.tileentity.CommandBlockLogic;
import net.minecraft.tileentity.CommandBlockTileEntity;
import net.minecraft.tileentity.JigsawTileEntity;
import net.minecraft.tileentity.SignTileEntity;
import net.minecraft.tileentity.StructureBlockTileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.MovementInput;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.PlayerSPPushOutOfBlocksEvent;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.PlaySoundAtEntityEvent;

@OnlyIn(Dist.CLIENT)
public class ClientPlayerEntity extends AbstractClientPlayerEntity {
   public final ClientPlayNetHandler connection;
   private final StatisticsManager stats;
   private final ClientRecipeBook recipeBook;
   private final List<IAmbientSoundHandler> ambientSoundHandlers = Lists.newArrayList();
   private int permissionLevel = 0;
   private double lastReportedPosX;
   private double lastReportedPosY;
   private double lastReportedPosZ;
   private float lastReportedYaw;
   private float lastReportedPitch;
   private boolean prevOnGround;
   private boolean field_228351_cj_;
   private boolean serverSprintState;
   private int positionUpdateTicks;
   private boolean hasValidHealth;
   private String serverBrand;
   public MovementInput movementInput;
   protected final Minecraft mc;
   protected int sprintToggleTimer;
   public int sprintingTicksLeft;
   public float renderArmYaw;
   public float renderArmPitch;
   public float prevRenderArmYaw;
   public float prevRenderArmPitch;
   private int horseJumpPowerCounter;
   private float horseJumpPower;
   public float timeInPortal;
   public float prevTimeInPortal;
   private boolean handActive;
   private Hand activeHand;
   private boolean rowingBoat;
   private boolean autoJumpEnabled = true;
   private int autoJumpTime;
   private boolean wasFallFlying;
   private int counterInWater;
   private boolean field_228352_cx_ = true;

   public ClientPlayerEntity(Minecraft p_i50990_1_, ClientWorld p_i50990_2_, ClientPlayNetHandler p_i50990_3_, StatisticsManager p_i50990_4_, ClientRecipeBook p_i50990_5_) {
      super(p_i50990_2_, p_i50990_3_.getGameProfile());
      this.connection = p_i50990_3_;
      this.stats = p_i50990_4_;
      this.recipeBook = p_i50990_5_;
      this.mc = p_i50990_1_;
      this.dimension = DimensionType.OVERWORLD;
      this.ambientSoundHandlers.add(new UnderwaterAmbientSoundHandler(this, p_i50990_1_.getSoundHandler()));
      this.ambientSoundHandlers.add(new BubbleColumnAmbientSoundHandler(this));
   }

   public boolean func_225510_bt_() {
      return super.func_225510_bt_() || this.mc.player.isSpectator() && this.mc.gameSettings.keyBindSpectatorOutlines.isKeyDown();
   }

   public boolean attackEntityFrom(DamageSource p_70097_1_, float p_70097_2_) {
      ForgeHooks.onPlayerAttack(this, p_70097_1_, p_70097_2_);
      return false;
   }

   public void heal(float p_70691_1_) {
   }

   public boolean startRiding(Entity p_184205_1_, boolean p_184205_2_) {
      if (!super.startRiding(p_184205_1_, p_184205_2_)) {
         return false;
      } else {
         if (p_184205_1_ instanceof AbstractMinecartEntity) {
            this.mc.getSoundHandler().play(new RidingMinecartTickableSound(this, (AbstractMinecartEntity)p_184205_1_));
         }

         if (p_184205_1_ instanceof BoatEntity) {
            this.prevRotationYaw = p_184205_1_.rotationYaw;
            this.rotationYaw = p_184205_1_.rotationYaw;
            this.setRotationYawHead(p_184205_1_.rotationYaw);
         }

         return true;
      }
   }

   public void stopRiding() {
      super.stopRiding();
      this.rowingBoat = false;
   }

   public float getPitch(float p_195050_1_) {
      return this.rotationPitch;
   }

   public float getYaw(float p_195046_1_) {
      return this.isPassenger() ? super.getYaw(p_195046_1_) : this.rotationYaw;
   }

   public void tick() {
      if (this.world.isBlockLoaded(new BlockPos(this.func_226277_ct_(), 0.0D, this.func_226281_cx_()))) {
         super.tick();
         if (this.isPassenger()) {
            this.connection.sendPacket(new CPlayerPacket.RotationPacket(this.rotationYaw, this.rotationPitch, this.onGround));
            this.connection.sendPacket(new CInputPacket(this.moveStrafing, this.moveForward, this.movementInput.jump, this.movementInput.field_228350_h_));
            Entity entity = this.getLowestRidingEntity();
            if (entity != this && entity.canPassengerSteer()) {
               this.connection.sendPacket(new CMoveVehiclePacket(entity));
            }
         } else {
            this.onUpdateWalkingPlayer();
         }

         Iterator var3 = this.ambientSoundHandlers.iterator();

         while(var3.hasNext()) {
            IAmbientSoundHandler iambientsoundhandler = (IAmbientSoundHandler)var3.next();
            iambientsoundhandler.tick();
         }
      }

   }

   private void onUpdateWalkingPlayer() {
      boolean flag = this.isSprinting();
      if (flag != this.serverSprintState) {
         CEntityActionPacket.Action centityactionpacket$action = flag ? CEntityActionPacket.Action.START_SPRINTING : CEntityActionPacket.Action.STOP_SPRINTING;
         this.connection.sendPacket(new CEntityActionPacket(this, centityactionpacket$action));
         this.serverSprintState = flag;
      }

      boolean flag3 = this.func_225608_bj_();
      if (flag3 != this.field_228351_cj_) {
         CEntityActionPacket.Action centityactionpacket$action1 = flag3 ? CEntityActionPacket.Action.PRESS_SHIFT_KEY : CEntityActionPacket.Action.RELEASE_SHIFT_KEY;
         this.connection.sendPacket(new CEntityActionPacket(this, centityactionpacket$action1));
         this.field_228351_cj_ = flag3;
      }

      if (this.isCurrentViewEntity()) {
         double d4 = this.func_226277_ct_() - this.lastReportedPosX;
         double d0 = this.func_226278_cu_() - this.lastReportedPosY;
         double d1 = this.func_226281_cx_() - this.lastReportedPosZ;
         double d2 = (double)(this.rotationYaw - this.lastReportedYaw);
         double d3 = (double)(this.rotationPitch - this.lastReportedPitch);
         ++this.positionUpdateTicks;
         boolean flag1 = d4 * d4 + d0 * d0 + d1 * d1 > 9.0E-4D || this.positionUpdateTicks >= 20;
         boolean flag2 = d2 != 0.0D || d3 != 0.0D;
         if (this.isPassenger()) {
            Vec3d vec3d = this.getMotion();
            this.connection.sendPacket(new CPlayerPacket.PositionRotationPacket(vec3d.x, -999.0D, vec3d.z, this.rotationYaw, this.rotationPitch, this.onGround));
            flag1 = false;
         } else if (flag1 && flag2) {
            this.connection.sendPacket(new CPlayerPacket.PositionRotationPacket(this.func_226277_ct_(), this.func_226278_cu_(), this.func_226281_cx_(), this.rotationYaw, this.rotationPitch, this.onGround));
         } else if (flag1) {
            this.connection.sendPacket(new CPlayerPacket.PositionPacket(this.func_226277_ct_(), this.func_226278_cu_(), this.func_226281_cx_(), this.onGround));
         } else if (flag2) {
            this.connection.sendPacket(new CPlayerPacket.RotationPacket(this.rotationYaw, this.rotationPitch, this.onGround));
         } else if (this.prevOnGround != this.onGround) {
            this.connection.sendPacket(new CPlayerPacket(this.onGround));
         }

         if (flag1) {
            this.lastReportedPosX = this.func_226277_ct_();
            this.lastReportedPosY = this.func_226278_cu_();
            this.lastReportedPosZ = this.func_226281_cx_();
            this.positionUpdateTicks = 0;
         }

         if (flag2) {
            this.lastReportedYaw = this.rotationYaw;
            this.lastReportedPitch = this.rotationPitch;
         }

         this.prevOnGround = this.onGround;
         this.autoJumpEnabled = this.mc.gameSettings.autoJump;
      }

   }

   public boolean func_225609_n_(boolean p_225609_1_) {
      CPlayerDiggingPacket.Action cplayerdiggingpacket$action = p_225609_1_ ? CPlayerDiggingPacket.Action.DROP_ALL_ITEMS : CPlayerDiggingPacket.Action.DROP_ITEM;
      this.connection.sendPacket(new CPlayerDiggingPacket(cplayerdiggingpacket$action, BlockPos.ZERO, Direction.DOWN));
      return this.inventory.decrStackSize(this.inventory.currentItem, p_225609_1_ && !this.inventory.getCurrentItem().isEmpty() ? this.inventory.getCurrentItem().getCount() : 1) != ItemStack.EMPTY;
   }

   public void sendChatMessage(String p_71165_1_) {
      this.connection.sendPacket(new CChatMessagePacket(p_71165_1_));
   }

   public void swingArm(Hand p_184609_1_) {
      super.swingArm(p_184609_1_);
      this.connection.sendPacket(new CAnimateHandPacket(p_184609_1_));
   }

   public void respawnPlayer() {
      this.connection.sendPacket(new CClientStatusPacket(CClientStatusPacket.State.PERFORM_RESPAWN));
   }

   protected void damageEntity(DamageSource p_70665_1_, float p_70665_2_) {
      if (!this.isInvulnerableTo(p_70665_1_)) {
         this.setHealth(this.getHealth() - p_70665_2_);
      }

   }

   public void closeScreen() {
      this.connection.sendPacket(new CCloseWindowPacket(this.openContainer.windowId));
      this.closeScreenAndDropStack();
   }

   public void closeScreenAndDropStack() {
      this.inventory.setItemStack(ItemStack.EMPTY);
      super.closeScreen();
      this.mc.displayGuiScreen((Screen)null);
   }

   public void setPlayerSPHealth(float p_71150_1_) {
      if (this.hasValidHealth) {
         float f = this.getHealth() - p_71150_1_;
         if (f <= 0.0F) {
            this.setHealth(p_71150_1_);
            if (f < 0.0F) {
               this.hurtResistantTime = 10;
            }
         } else {
            this.lastDamage = f;
            this.setHealth(this.getHealth());
            this.hurtResistantTime = 20;
            this.damageEntity(DamageSource.GENERIC, f);
            this.maxHurtTime = 10;
            this.hurtTime = this.maxHurtTime;
         }
      } else {
         this.setHealth(p_71150_1_);
         this.hasValidHealth = true;
      }

   }

   public void sendPlayerAbilities() {
      this.connection.sendPacket(new CPlayerAbilitiesPacket(this.abilities));
   }

   public boolean isUser() {
      return true;
   }

   protected void sendHorseJump() {
      this.connection.sendPacket(new CEntityActionPacket(this, CEntityActionPacket.Action.START_RIDING_JUMP, MathHelper.floor(this.getHorseJumpPower() * 100.0F)));
   }

   public void sendHorseInventory() {
      this.connection.sendPacket(new CEntityActionPacket(this, CEntityActionPacket.Action.OPEN_INVENTORY));
   }

   public void setServerBrand(String p_175158_1_) {
      this.serverBrand = p_175158_1_;
   }

   public String getServerBrand() {
      return this.serverBrand;
   }

   public StatisticsManager getStats() {
      return this.stats;
   }

   public ClientRecipeBook getRecipeBook() {
      return this.recipeBook;
   }

   public void removeRecipeHighlight(IRecipe<?> p_193103_1_) {
      if (this.recipeBook.isNew(p_193103_1_)) {
         this.recipeBook.markSeen(p_193103_1_);
         this.connection.sendPacket(new CRecipeInfoPacket(p_193103_1_));
      }

   }

   protected int getPermissionLevel() {
      return this.permissionLevel;
   }

   public void setPermissionLevel(int p_184839_1_) {
      this.permissionLevel = p_184839_1_;
   }

   public void sendStatusMessage(ITextComponent p_146105_1_, boolean p_146105_2_) {
      if (p_146105_2_) {
         this.mc.ingameGUI.setOverlayMessage(p_146105_1_, false);
      } else {
         this.mc.ingameGUI.getChatGUI().printChatMessage(p_146105_1_);
      }

   }

   protected void pushOutOfBlocks(double p_213282_1_, double p_213282_3_, double p_213282_5_) {
      BlockPos blockpos = new BlockPos(p_213282_1_, p_213282_3_, p_213282_5_);
      if (this.func_205027_h(blockpos)) {
         double d0 = p_213282_1_ - (double)blockpos.getX();
         double d1 = p_213282_5_ - (double)blockpos.getZ();
         Direction direction = null;
         double d2 = 9999.0D;
         if (!this.func_205027_h(blockpos.west()) && d0 < d2) {
            d2 = d0;
            direction = Direction.WEST;
         }

         if (!this.func_205027_h(blockpos.east()) && 1.0D - d0 < d2) {
            d2 = 1.0D - d0;
            direction = Direction.EAST;
         }

         if (!this.func_205027_h(blockpos.north()) && d1 < d2) {
            d2 = d1;
            direction = Direction.NORTH;
         }

         if (!this.func_205027_h(blockpos.south()) && 1.0D - d1 < d2) {
            d2 = 1.0D - d1;
            direction = Direction.SOUTH;
         }

         if (direction != null) {
            Vec3d vec3d = this.getMotion();
            switch(direction) {
            case WEST:
               this.setMotion(-0.1D, vec3d.y, vec3d.z);
               break;
            case EAST:
               this.setMotion(0.1D, vec3d.y, vec3d.z);
               break;
            case NORTH:
               this.setMotion(vec3d.x, vec3d.y, -0.1D);
               break;
            case SOUTH:
               this.setMotion(vec3d.x, vec3d.y, 0.1D);
            }
         }
      }

   }

   private boolean func_205027_h(BlockPos p_205027_1_) {
      AxisAlignedBB axisalignedbb = this.getBoundingBox();
      BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable(p_205027_1_);

      for(int i = MathHelper.floor(axisalignedbb.minY); i < MathHelper.ceil(axisalignedbb.maxY); ++i) {
         blockpos$mutable.setY(i);
         if (!this.isNormalCube(blockpos$mutable)) {
            return true;
         }
      }

      return false;
   }

   public void setSprinting(boolean p_70031_1_) {
      super.setSprinting(p_70031_1_);
      this.sprintingTicksLeft = 0;
   }

   public void setXPStats(float p_71152_1_, int p_71152_2_, int p_71152_3_) {
      this.experience = p_71152_1_;
      this.experienceTotal = p_71152_2_;
      this.experienceLevel = p_71152_3_;
   }

   public void sendMessage(ITextComponent p_145747_1_) {
      this.mc.ingameGUI.getChatGUI().printChatMessage(p_145747_1_);
   }

   public void handleStatusUpdate(byte p_70103_1_) {
      if (p_70103_1_ >= 24 && p_70103_1_ <= 28) {
         this.setPermissionLevel(p_70103_1_ - 24);
      } else {
         super.handleStatusUpdate(p_70103_1_);
      }

   }

   public void func_228355_a_(boolean p_228355_1_) {
      this.field_228352_cx_ = p_228355_1_;
   }

   public boolean func_228353_F_() {
      return this.field_228352_cx_;
   }

   public void playSound(SoundEvent p_184185_1_, float p_184185_2_, float p_184185_3_) {
      PlaySoundAtEntityEvent event = ForgeEventFactory.onPlaySoundAtEntity(this, p_184185_1_, this.getSoundCategory(), p_184185_2_, p_184185_3_);
      if (!event.isCanceled() && event.getSound() != null) {
         p_184185_1_ = event.getSound();
         p_184185_2_ = event.getVolume();
         p_184185_3_ = event.getPitch();
         this.world.playSound(this.func_226277_ct_(), this.func_226278_cu_(), this.func_226281_cx_(), p_184185_1_, this.getSoundCategory(), p_184185_2_, p_184185_3_, false);
      }
   }

   public void func_213823_a(SoundEvent p_213823_1_, SoundCategory p_213823_2_, float p_213823_3_, float p_213823_4_) {
      this.world.playSound(this.func_226277_ct_(), this.func_226278_cu_(), this.func_226281_cx_(), p_213823_1_, p_213823_2_, p_213823_3_, p_213823_4_, false);
   }

   public boolean isServerWorld() {
      return true;
   }

   public void setActiveHand(Hand p_184598_1_) {
      ItemStack itemstack = this.getHeldItem(p_184598_1_);
      if (!itemstack.isEmpty() && !this.isHandActive()) {
         super.setActiveHand(p_184598_1_);
         this.handActive = true;
         this.activeHand = p_184598_1_;
      }

   }

   public boolean isHandActive() {
      return this.handActive;
   }

   public void resetActiveHand() {
      super.resetActiveHand();
      this.handActive = false;
   }

   public Hand getActiveHand() {
      return this.activeHand;
   }

   public void notifyDataManagerChange(DataParameter<?> p_184206_1_) {
      super.notifyDataManagerChange(p_184206_1_);
      if (LIVING_FLAGS.equals(p_184206_1_)) {
         boolean flag = ((Byte)this.dataManager.get(LIVING_FLAGS) & 1) > 0;
         Hand hand = ((Byte)this.dataManager.get(LIVING_FLAGS) & 2) > 0 ? Hand.OFF_HAND : Hand.MAIN_HAND;
         if (flag && !this.handActive) {
            this.setActiveHand(hand);
         } else if (!flag && this.handActive) {
            this.resetActiveHand();
         }
      }

      if (FLAGS.equals(p_184206_1_) && this.isElytraFlying() && !this.wasFallFlying) {
         this.mc.getSoundHandler().play(new ElytraSound(this));
      }

   }

   public boolean isRidingHorse() {
      Entity entity = this.getRidingEntity();
      return this.isPassenger() && entity instanceof IJumpingMount && ((IJumpingMount)entity).canJump();
   }

   public float getHorseJumpPower() {
      return this.horseJumpPower;
   }

   public void openSignEditor(SignTileEntity p_175141_1_) {
      this.mc.displayGuiScreen(new EditSignScreen(p_175141_1_));
   }

   public void openMinecartCommandBlock(CommandBlockLogic p_184809_1_) {
      this.mc.displayGuiScreen(new EditMinecartCommandBlockScreen(p_184809_1_));
   }

   public void openCommandBlock(CommandBlockTileEntity p_184824_1_) {
      this.mc.displayGuiScreen(new CommandBlockScreen(p_184824_1_));
   }

   public void openStructureBlock(StructureBlockTileEntity p_189807_1_) {
      this.mc.displayGuiScreen(new EditStructureScreen(p_189807_1_));
   }

   public void func_213826_a(JigsawTileEntity p_213826_1_) {
      this.mc.displayGuiScreen(new JigsawScreen(p_213826_1_));
   }

   public void openBook(ItemStack p_184814_1_, Hand p_184814_2_) {
      Item item = p_184814_1_.getItem();
      if (item == Items.WRITABLE_BOOK) {
         this.mc.displayGuiScreen(new EditBookScreen(this, p_184814_1_, p_184814_2_));
      }

   }

   public void onCriticalHit(Entity p_71009_1_) {
      this.mc.particles.addParticleEmitter(p_71009_1_, ParticleTypes.CRIT);
   }

   public void onEnchantmentCritical(Entity p_71047_1_) {
      this.mc.particles.addParticleEmitter(p_71047_1_, ParticleTypes.ENCHANTED_HIT);
   }

   public boolean func_225608_bj_() {
      return this.movementInput != null && this.movementInput.field_228350_h_;
   }

   public boolean isCrouching() {
      if (!this.abilities.isFlying && !this.isSwimming() && this.isPoseClear(Pose.CROUCHING)) {
         return this.func_225608_bj_() || !this.isSleeping() && !this.isPoseClear(Pose.STANDING);
      } else {
         return false;
      }
   }

   public boolean func_228354_I_() {
      return this.isCrouching() || this.func_213300_bk();
   }

   public void updateEntityActionState() {
      super.updateEntityActionState();
      if (this.isCurrentViewEntity()) {
         this.moveStrafing = this.movementInput.moveStrafe;
         this.moveForward = this.movementInput.moveForward;
         this.isJumping = this.movementInput.jump;
         this.prevRenderArmYaw = this.renderArmYaw;
         this.prevRenderArmPitch = this.renderArmPitch;
         this.renderArmPitch = (float)((double)this.renderArmPitch + (double)(this.rotationPitch - this.renderArmPitch) * 0.5D);
         this.renderArmYaw = (float)((double)this.renderArmYaw + (double)(this.rotationYaw - this.renderArmYaw) * 0.5D);
      }

   }

   protected boolean isCurrentViewEntity() {
      return this.mc.getRenderViewEntity() == this;
   }

   public void livingTick() {
      ++this.sprintingTicksLeft;
      if (this.sprintToggleTimer > 0) {
         --this.sprintToggleTimer;
      }

      this.func_213839_ed();
      boolean flag = this.movementInput.jump;
      boolean flag1 = this.movementInput.field_228350_h_;
      boolean flag2 = this.func_223110_ee();
      this.movementInput.func_225607_a_(this.func_228354_I_());
      ForgeHooksClient.onInputUpdate(this, this.movementInput);
      this.mc.getTutorial().handleMovement(this.movementInput);
      if (this.isHandActive() && !this.isPassenger()) {
         MovementInput var10000 = this.movementInput;
         var10000.moveStrafe *= 0.2F;
         var10000 = this.movementInput;
         var10000.moveForward *= 0.2F;
         this.sprintToggleTimer = 0;
      }

      boolean flag3 = false;
      if (this.autoJumpTime > 0) {
         --this.autoJumpTime;
         flag3 = true;
         this.movementInput.jump = true;
      }

      PlayerSPPushOutOfBlocksEvent event = new PlayerSPPushOutOfBlocksEvent(this);
      if (!this.noClip && !MinecraftForge.EVENT_BUS.post(event)) {
         this.pushOutOfBlocks(this.func_226277_ct_() - (double)this.getWidth() * 0.35D, event.getMinY(), this.func_226281_cx_() + (double)this.getWidth() * 0.35D);
         this.pushOutOfBlocks(this.func_226277_ct_() - (double)this.getWidth() * 0.35D, event.getMinY(), this.func_226281_cx_() - (double)this.getWidth() * 0.35D);
         this.pushOutOfBlocks(this.func_226277_ct_() + (double)this.getWidth() * 0.35D, event.getMinY(), this.func_226281_cx_() - (double)this.getWidth() * 0.35D);
         this.pushOutOfBlocks(this.func_226277_ct_() + (double)this.getWidth() * 0.35D, event.getMinY(), this.func_226281_cx_() + (double)this.getWidth() * 0.35D);
      }

      boolean flag4 = (float)this.getFoodStats().getFoodLevel() > 6.0F || this.abilities.allowFlying;
      if ((this.onGround || this.canSwim()) && !flag1 && !flag2 && this.func_223110_ee() && !this.isSprinting() && flag4 && !this.isHandActive() && !this.isPotionActive(Effects.BLINDNESS)) {
         if (this.sprintToggleTimer <= 0 && !this.mc.gameSettings.keyBindSprint.isKeyDown()) {
            this.sprintToggleTimer = 7;
         } else {
            this.setSprinting(true);
         }
      }

      if (!this.isSprinting() && (!this.isInWater() || this.canSwim()) && this.func_223110_ee() && flag4 && !this.isHandActive() && !this.isPotionActive(Effects.BLINDNESS) && this.mc.gameSettings.keyBindSprint.isKeyDown()) {
         this.setSprinting(true);
      }

      boolean flag7;
      if (this.isSprinting()) {
         flag7 = !this.movementInput.func_223135_b() || !flag4;
         boolean flag6 = flag7 || this.collidedHorizontally || this.isInWater() && !this.canSwim();
         if (this.isSwimming()) {
            if (!this.onGround && !this.movementInput.field_228350_h_ && flag7 || !this.isInWater()) {
               this.setSprinting(false);
            }
         } else if (flag6) {
            this.setSprinting(false);
         }
      }

      flag7 = false;
      if (this.abilities.allowFlying) {
         if (this.mc.playerController.isSpectatorMode()) {
            if (!this.abilities.isFlying) {
               this.abilities.isFlying = true;
               flag7 = true;
               this.sendPlayerAbilities();
            }
         } else if (!flag && this.movementInput.jump && !flag3) {
            if (this.flyToggleTimer == 0) {
               this.flyToggleTimer = 7;
            } else if (!this.isSwimming()) {
               this.abilities.isFlying = !this.abilities.isFlying;
               flag7 = true;
               this.sendPlayerAbilities();
               this.flyToggleTimer = 0;
            }
         }
      }

      if (this.movementInput.jump && !flag7 && !flag && !this.abilities.isFlying && !this.isPassenger() && !this.isOnLadder()) {
         ItemStack itemstack = this.getItemStackFromSlot(EquipmentSlotType.CHEST);
         if (itemstack.getItem() == Items.ELYTRA && ElytraItem.isUsable(itemstack) && this.func_226566_ei_()) {
            this.connection.sendPacket(new CEntityActionPacket(this, CEntityActionPacket.Action.START_FALL_FLYING));
         }
      }

      this.wasFallFlying = this.isElytraFlying();
      if (this.isInWater() && this.movementInput.field_228350_h_) {
         this.handleFluidSneak();
      }

      int j;
      if (this.areEyesInFluid(FluidTags.WATER)) {
         j = this.isSpectator() ? 10 : 1;
         this.counterInWater = MathHelper.clamp(this.counterInWater + j, 0, 600);
      } else if (this.counterInWater > 0) {
         this.areEyesInFluid(FluidTags.WATER);
         this.counterInWater = MathHelper.clamp(this.counterInWater - 10, 0, 600);
      }

      if (this.abilities.isFlying && this.isCurrentViewEntity()) {
         j = 0;
         if (this.movementInput.field_228350_h_) {
            --j;
         }

         if (this.movementInput.jump) {
            ++j;
         }

         if (j != 0) {
            this.setMotion(this.getMotion().add(0.0D, (double)((float)j * this.abilities.getFlySpeed() * 3.0F), 0.0D));
         }
      }

      if (this.isRidingHorse()) {
         IJumpingMount ijumpingmount = (IJumpingMount)this.getRidingEntity();
         if (this.horseJumpPowerCounter < 0) {
            ++this.horseJumpPowerCounter;
            if (this.horseJumpPowerCounter == 0) {
               this.horseJumpPower = 0.0F;
            }
         }

         if (flag && !this.movementInput.jump) {
            this.horseJumpPowerCounter = -10;
            ijumpingmount.setJumpPower(MathHelper.floor(this.getHorseJumpPower() * 100.0F));
            this.sendHorseJump();
         } else if (!flag && this.movementInput.jump) {
            this.horseJumpPowerCounter = 0;
            this.horseJumpPower = 0.0F;
         } else if (flag) {
            ++this.horseJumpPowerCounter;
            if (this.horseJumpPowerCounter < 10) {
               this.horseJumpPower = (float)this.horseJumpPowerCounter * 0.1F;
            } else {
               this.horseJumpPower = 0.8F + 2.0F / (float)(this.horseJumpPowerCounter - 9) * 0.1F;
            }
         }
      } else {
         this.horseJumpPower = 0.0F;
      }

      super.livingTick();
      if (this.onGround && this.abilities.isFlying && !this.mc.playerController.isSpectatorMode()) {
         this.abilities.isFlying = false;
         this.sendPlayerAbilities();
      }

   }

   private void func_213839_ed() {
      this.prevTimeInPortal = this.timeInPortal;
      if (this.inPortal) {
         if (this.mc.currentScreen != null && !this.mc.currentScreen.isPauseScreen()) {
            if (this.mc.currentScreen instanceof ContainerScreen) {
               this.closeScreen();
            }

            this.mc.displayGuiScreen((Screen)null);
         }

         if (this.timeInPortal == 0.0F) {
            this.mc.getSoundHandler().play(SimpleSound.master(SoundEvents.BLOCK_PORTAL_TRIGGER, this.rand.nextFloat() * 0.4F + 0.8F));
         }

         this.timeInPortal += 0.0125F;
         if (this.timeInPortal >= 1.0F) {
            this.timeInPortal = 1.0F;
         }

         this.inPortal = false;
      } else if (this.isPotionActive(Effects.NAUSEA) && this.getActivePotionEffect(Effects.NAUSEA).getDuration() > 60) {
         this.timeInPortal += 0.006666667F;
         if (this.timeInPortal > 1.0F) {
            this.timeInPortal = 1.0F;
         }
      } else {
         if (this.timeInPortal > 0.0F) {
            this.timeInPortal -= 0.05F;
         }

         if (this.timeInPortal < 0.0F) {
            this.timeInPortal = 0.0F;
         }
      }

      this.decrementTimeUntilPortal();
   }

   public void updateRidden() {
      super.updateRidden();
      this.rowingBoat = false;
      if (this.getRidingEntity() instanceof BoatEntity) {
         BoatEntity boatentity = (BoatEntity)this.getRidingEntity();
         boatentity.updateInputs(this.movementInput.leftKeyDown, this.movementInput.rightKeyDown, this.movementInput.forwardKeyDown, this.movementInput.backKeyDown);
         this.rowingBoat |= this.movementInput.leftKeyDown || this.movementInput.rightKeyDown || this.movementInput.forwardKeyDown || this.movementInput.backKeyDown;
      }

   }

   public boolean isRowingBoat() {
      return this.rowingBoat;
   }

   @Nullable
   public EffectInstance removeActivePotionEffect(@Nullable Effect p_184596_1_) {
      if (p_184596_1_ == Effects.NAUSEA) {
         this.prevTimeInPortal = 0.0F;
         this.timeInPortal = 0.0F;
      }

      return super.removeActivePotionEffect(p_184596_1_);
   }

   public void move(MoverType p_213315_1_, Vec3d p_213315_2_) {
      double d0 = this.func_226277_ct_();
      double d1 = this.func_226281_cx_();
      super.move(p_213315_1_, p_213315_2_);
      this.updateAutoJump((float)(this.func_226277_ct_() - d0), (float)(this.func_226281_cx_() - d1));
   }

   public boolean isAutoJumpEnabled() {
      return this.autoJumpEnabled;
   }

   protected void updateAutoJump(float p_189810_1_, float p_189810_2_) {
      if (this.func_228356_eG_()) {
         Vec3d vec3d = this.getPositionVec();
         Vec3d vec3d1 = vec3d.add((double)p_189810_1_, 0.0D, (double)p_189810_2_);
         Vec3d vec3d2 = new Vec3d((double)p_189810_1_, 0.0D, (double)p_189810_2_);
         float f = this.getAIMoveSpeed();
         float f1 = (float)vec3d2.lengthSquared();
         float f13;
         if (f1 <= 0.001F) {
            Vec2f vec2f = this.movementInput.getMoveVector();
            float f2 = f * vec2f.x;
            float f3 = f * vec2f.y;
            f13 = MathHelper.sin(this.rotationYaw * 0.017453292F);
            float f5 = MathHelper.cos(this.rotationYaw * 0.017453292F);
            vec3d2 = new Vec3d((double)(f2 * f5 - f3 * f13), vec3d2.y, (double)(f3 * f5 + f2 * f13));
            f1 = (float)vec3d2.lengthSquared();
            if (f1 <= 0.001F) {
               return;
            }
         }

         float f12 = MathHelper.func_226165_i_(f1);
         Vec3d vec3d12 = vec3d2.scale((double)f12);
         Vec3d vec3d13 = this.getForward();
         f13 = (float)(vec3d13.x * vec3d12.x + vec3d13.z * vec3d12.z);
         if (f13 >= -0.15F) {
            ISelectionContext iselectioncontext = ISelectionContext.forEntity(this);
            BlockPos blockpos = new BlockPos(this.func_226277_ct_(), this.getBoundingBox().maxY, this.func_226281_cx_());
            BlockState blockstate = this.world.getBlockState(blockpos);
            if (blockstate.getCollisionShape(this.world, blockpos, iselectioncontext).isEmpty()) {
               blockpos = blockpos.up();
               BlockState blockstate1 = this.world.getBlockState(blockpos);
               if (blockstate1.getCollisionShape(this.world, blockpos, iselectioncontext).isEmpty()) {
                  float f6 = 7.0F;
                  float f7 = 1.2F;
                  if (this.isPotionActive(Effects.JUMP_BOOST)) {
                     f7 += (float)(this.getActivePotionEffect(Effects.JUMP_BOOST).getAmplifier() + 1) * 0.75F;
                  }

                  float f8 = Math.max(f * 7.0F, 1.0F / f12);
                  Vec3d vec3d4 = vec3d1.add(vec3d12.scale((double)f8));
                  float f9 = this.getWidth();
                  float f10 = this.getHeight();
                  AxisAlignedBB axisalignedbb = (new AxisAlignedBB(vec3d, vec3d4.add(0.0D, (double)f10, 0.0D))).grow((double)f9, 0.0D, (double)f9);
                  Vec3d lvt_19_1_ = vec3d.add(0.0D, 0.5099999904632568D, 0.0D);
                  vec3d4 = vec3d4.add(0.0D, 0.5099999904632568D, 0.0D);
                  Vec3d vec3d5 = vec3d12.crossProduct(new Vec3d(0.0D, 1.0D, 0.0D));
                  Vec3d vec3d6 = vec3d5.scale((double)(f9 * 0.5F));
                  Vec3d vec3d7 = lvt_19_1_.subtract(vec3d6);
                  Vec3d vec3d8 = vec3d4.subtract(vec3d6);
                  Vec3d vec3d9 = lvt_19_1_.add(vec3d6);
                  Vec3d vec3d10 = vec3d4.add(vec3d6);
                  Iterator<AxisAlignedBB> iterator = this.world.func_226667_c_(this, axisalignedbb, Collections.emptySet()).flatMap((p_lambda$updateAutoJump$0_0_) -> {
                     return p_lambda$updateAutoJump$0_0_.toBoundingBoxList().stream();
                  }).iterator();
                  float f11 = Float.MIN_VALUE;

                  label68: {
                     AxisAlignedBB axisalignedbb1;
                     do {
                        if (!iterator.hasNext()) {
                           break label68;
                        }

                        axisalignedbb1 = (AxisAlignedBB)iterator.next();
                     } while(!axisalignedbb1.intersects(vec3d7, vec3d8) && !axisalignedbb1.intersects(vec3d9, vec3d10));

                     f11 = (float)axisalignedbb1.maxY;
                     Vec3d vec3d11 = axisalignedbb1.getCenter();
                     BlockPos blockpos1 = new BlockPos(vec3d11);

                     for(int i = 1; (float)i < f7; ++i) {
                        BlockPos blockpos2 = blockpos1.up(i);
                        BlockState blockstate2 = this.world.getBlockState(blockpos2);
                        VoxelShape voxelshape;
                        if (!(voxelshape = blockstate2.getCollisionShape(this.world, blockpos2, iselectioncontext)).isEmpty()) {
                           f11 = (float)voxelshape.getEnd(Direction.Axis.Y) + (float)blockpos2.getY();
                           if ((double)f11 - this.func_226278_cu_() > (double)f7) {
                              return;
                           }
                        }

                        if (i > 1) {
                           blockpos = blockpos.up();
                           BlockState blockstate3 = this.world.getBlockState(blockpos);
                           if (!blockstate3.getCollisionShape(this.world, blockpos, iselectioncontext).isEmpty()) {
                              return;
                           }
                        }
                     }
                  }

                  if (f11 != Float.MIN_VALUE) {
                     float f14 = (float)((double)f11 - this.func_226278_cu_());
                     if (f14 > 0.5F && f14 <= f7) {
                        this.autoJumpTime = 1;
                     }
                  }
               }
            }
         }
      }

   }

   private boolean func_228356_eG_() {
      return this.isAutoJumpEnabled() && this.autoJumpTime <= 0 && this.onGround && !this.func_226565_dV_() && !this.isPassenger() && this.func_228357_eH_() && (double)this.func_226269_ah_() >= 1.0D;
   }

   private boolean func_228357_eH_() {
      Vec2f vec2f = this.movementInput.getMoveVector();
      return vec2f.x != 0.0F || vec2f.y != 0.0F;
   }

   private boolean func_223110_ee() {
      double d0 = 0.8D;
      return this.canSwim() ? this.movementInput.func_223135_b() : (double)this.movementInput.moveForward >= 0.8D;
   }

   public float getWaterBrightness() {
      if (!this.areEyesInFluid(FluidTags.WATER)) {
         return 0.0F;
      } else {
         float f = 600.0F;
         float f1 = 100.0F;
         if ((float)this.counterInWater >= 600.0F) {
            return 1.0F;
         } else {
            float f2 = MathHelper.clamp((float)this.counterInWater / 100.0F, 0.0F, 1.0F);
            float f3 = (float)this.counterInWater < 100.0F ? 0.0F : MathHelper.clamp(((float)this.counterInWater - 100.0F) / 500.0F, 0.0F, 1.0F);
            return f2 * 0.6F + f3 * 0.39999998F;
         }
      }
   }

   public boolean canSwim() {
      return this.eyesInWaterPlayer;
   }

   protected boolean updateEyesInWaterPlayer() {
      boolean flag = this.eyesInWaterPlayer;
      boolean flag1 = super.updateEyesInWaterPlayer();
      if (this.isSpectator()) {
         return this.eyesInWaterPlayer;
      } else {
         if (!flag && flag1) {
            this.world.playSound(this.func_226277_ct_(), this.func_226278_cu_(), this.func_226281_cx_(), SoundEvents.AMBIENT_UNDERWATER_ENTER, SoundCategory.AMBIENT, 1.0F, 1.0F, false);
            this.mc.getSoundHandler().play(new UnderwaterAmbientSounds.UnderWaterSound(this));
         }

         if (flag && !flag1) {
            this.world.playSound(this.func_226277_ct_(), this.func_226278_cu_(), this.func_226281_cx_(), SoundEvents.AMBIENT_UNDERWATER_EXIT, SoundCategory.AMBIENT, 1.0F, 1.0F, false);
         }

         return this.eyesInWaterPlayer;
      }
   }

   public void updateSyncFields(ClientPlayerEntity p_updateSyncFields_1_) {
      this.lastReportedPosX = p_updateSyncFields_1_.lastReportedPosX;
      this.lastReportedPosY = p_updateSyncFields_1_.lastReportedPosY;
      this.lastReportedPosZ = p_updateSyncFields_1_.lastReportedPosZ;
      this.lastReportedYaw = p_updateSyncFields_1_.lastReportedYaw;
      this.lastReportedPitch = p_updateSyncFields_1_.lastReportedPitch;
      this.prevOnGround = p_updateSyncFields_1_.prevOnGround;
      this.field_228351_cj_ = p_updateSyncFields_1_.field_228351_cj_;
      this.serverSprintState = p_updateSyncFields_1_.serverSprintState;
      this.positionUpdateTicks = p_updateSyncFields_1_.positionUpdateTicks;
   }
}
