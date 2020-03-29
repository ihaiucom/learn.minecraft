package net.minecraft.client.multiplayer;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CommandBlockBlock;
import net.minecraft.block.JigsawBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.StructureBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.client.util.ClientRecipeBook;
import net.minecraft.client.util.PosAndRotation;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.IFluidState;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.network.play.client.CClickWindowPacket;
import net.minecraft.network.play.client.CCreativeInventoryActionPacket;
import net.minecraft.network.play.client.CEnchantItemPacket;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.network.play.client.CPickItemPacket;
import net.minecraft.network.play.client.CPlaceRecipePacket;
import net.minecraft.network.play.client.CPlayerDiggingPacket;
import net.minecraft.network.play.client.CPlayerTryUseItemOnBlockPacket;
import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
import net.minecraft.network.play.client.CUseEntityPacket;
import net.minecraft.stats.StatisticsManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class PlayerController {
   private static final Logger field_225325_a = LogManager.getLogger();
   private final Minecraft mc;
   private final ClientPlayNetHandler connection;
   private BlockPos currentBlock = new BlockPos(-1, -1, -1);
   private ItemStack currentItemHittingBlock;
   private float curBlockDamageMP;
   private float stepSoundTickCounter;
   private int blockHitDelay;
   private boolean isHittingBlock;
   private GameType currentGameType;
   private final Object2ObjectLinkedOpenHashMap<Pair<BlockPos, CPlayerDiggingPacket.Action>, PosAndRotation> field_225326_k;
   private int currentPlayerItem;

   public PlayerController(Minecraft p_i45062_1_, ClientPlayNetHandler p_i45062_2_) {
      this.currentItemHittingBlock = ItemStack.EMPTY;
      this.currentGameType = GameType.SURVIVAL;
      this.field_225326_k = new Object2ObjectLinkedOpenHashMap();
      this.mc = p_i45062_1_;
      this.connection = p_i45062_2_;
   }

   public static void clickBlockCreative(Minecraft p_178891_0_, PlayerController p_178891_1_, BlockPos p_178891_2_, Direction p_178891_3_) {
      if (!p_178891_0_.world.extinguishFire(p_178891_0_.player, p_178891_2_, p_178891_3_)) {
         p_178891_1_.onPlayerDestroyBlock(p_178891_2_);
      }

   }

   public void setPlayerCapabilities(PlayerEntity p_78748_1_) {
      this.currentGameType.configurePlayerCapabilities(p_78748_1_.abilities);
   }

   public void setGameType(GameType p_78746_1_) {
      this.currentGameType = p_78746_1_;
      this.currentGameType.configurePlayerCapabilities(this.mc.player.abilities);
   }

   public boolean shouldDrawHUD() {
      return this.currentGameType.isSurvivalOrAdventure();
   }

   public boolean onPlayerDestroyBlock(BlockPos p_187103_1_) {
      if (this.mc.player.getHeldItemMainhand().onBlockStartBreak(p_187103_1_, this.mc.player)) {
         return false;
      } else if (this.mc.player.func_223729_a(this.mc.world, p_187103_1_, this.currentGameType)) {
         return false;
      } else {
         World world = this.mc.world;
         BlockState blockstate = world.getBlockState(p_187103_1_);
         if (!this.mc.player.getHeldItemMainhand().getItem().canPlayerBreakBlockWhileHolding(blockstate, world, p_187103_1_, this.mc.player)) {
            return false;
         } else {
            Block block = blockstate.getBlock();
            if ((block instanceof CommandBlockBlock || block instanceof StructureBlock || block instanceof JigsawBlock) && !this.mc.player.canUseCommandBlock()) {
               return false;
            } else if (blockstate.isAir(world, p_187103_1_)) {
               return false;
            } else {
               block.onBlockHarvested(world, p_187103_1_, blockstate, this.mc.player);
               IFluidState ifluidstate = world.getFluidState(p_187103_1_);
               boolean flag = world.setBlockState(p_187103_1_, ifluidstate.getBlockState(), 11);
               if (flag) {
                  block.onPlayerDestroy(world, p_187103_1_, blockstate);
               }

               return flag;
            }
         }
      }
   }

   public boolean clickBlock(BlockPos p_180511_1_, Direction p_180511_2_) {
      if (this.mc.player.func_223729_a(this.mc.world, p_180511_1_, this.currentGameType)) {
         return false;
      } else if (!this.mc.world.getWorldBorder().contains(p_180511_1_)) {
         return false;
      } else {
         if (this.currentGameType.isCreative()) {
            BlockState blockstate = this.mc.world.getBlockState(p_180511_1_);
            this.mc.getTutorial().onHitBlock(this.mc.world, p_180511_1_, blockstate, 1.0F);
            this.func_225324_a(CPlayerDiggingPacket.Action.START_DESTROY_BLOCK, p_180511_1_, p_180511_2_);
            if (!ForgeHooks.onLeftClickBlock(this.mc.player, p_180511_1_, p_180511_2_).isCanceled()) {
               clickBlockCreative(this.mc, this, p_180511_1_, p_180511_2_);
            }

            this.blockHitDelay = 5;
         } else if (!this.isHittingBlock || !this.isHittingPosition(p_180511_1_)) {
            if (this.isHittingBlock) {
               this.func_225324_a(CPlayerDiggingPacket.Action.ABORT_DESTROY_BLOCK, this.currentBlock, p_180511_2_);
            }

            PlayerInteractEvent.LeftClickBlock event = ForgeHooks.onLeftClickBlock(this.mc.player, p_180511_1_, p_180511_2_);
            BlockState blockstate1 = this.mc.world.getBlockState(p_180511_1_);
            this.mc.getTutorial().onHitBlock(this.mc.world, p_180511_1_, blockstate1, 0.0F);
            this.func_225324_a(CPlayerDiggingPacket.Action.START_DESTROY_BLOCK, p_180511_1_, p_180511_2_);
            boolean flag = !blockstate1.isAir(this.mc.world, p_180511_1_);
            if (flag && this.curBlockDamageMP == 0.0F && event.getUseBlock() != Result.DENY) {
               blockstate1.onBlockClicked(this.mc.world, p_180511_1_, this.mc.player);
            }

            if (event.getUseItem() == Result.DENY) {
               return true;
            }

            if (flag && blockstate1.getPlayerRelativeBlockHardness(this.mc.player, this.mc.player.world, p_180511_1_) >= 1.0F) {
               this.onPlayerDestroyBlock(p_180511_1_);
            } else {
               this.isHittingBlock = true;
               this.currentBlock = p_180511_1_;
               this.currentItemHittingBlock = this.mc.player.getHeldItemMainhand();
               this.curBlockDamageMP = 0.0F;
               this.stepSoundTickCounter = 0.0F;
               this.mc.world.sendBlockBreakProgress(this.mc.player.getEntityId(), this.currentBlock, (int)(this.curBlockDamageMP * 10.0F) - 1);
            }
         }

         return true;
      }
   }

   public void resetBlockRemoving() {
      if (this.isHittingBlock) {
         BlockState blockstate = this.mc.world.getBlockState(this.currentBlock);
         this.mc.getTutorial().onHitBlock(this.mc.world, this.currentBlock, blockstate, -1.0F);
         this.func_225324_a(CPlayerDiggingPacket.Action.ABORT_DESTROY_BLOCK, this.currentBlock, Direction.DOWN);
         this.isHittingBlock = false;
         this.curBlockDamageMP = 0.0F;
         this.mc.world.sendBlockBreakProgress(this.mc.player.getEntityId(), this.currentBlock, -1);
         this.mc.player.resetCooldown();
      }

   }

   public boolean onPlayerDamageBlock(BlockPos p_180512_1_, Direction p_180512_2_) {
      this.syncCurrentPlayItem();
      if (this.blockHitDelay > 0) {
         --this.blockHitDelay;
         return true;
      } else {
         BlockState blockstate;
         if (this.currentGameType.isCreative() && this.mc.world.getWorldBorder().contains(p_180512_1_)) {
            this.blockHitDelay = 5;
            blockstate = this.mc.world.getBlockState(p_180512_1_);
            this.mc.getTutorial().onHitBlock(this.mc.world, p_180512_1_, blockstate, 1.0F);
            this.func_225324_a(CPlayerDiggingPacket.Action.START_DESTROY_BLOCK, p_180512_1_, p_180512_2_);
            if (!ForgeHooks.onLeftClickBlock(this.mc.player, p_180512_1_, p_180512_2_).isCanceled()) {
               clickBlockCreative(this.mc, this, p_180512_1_, p_180512_2_);
            }

            return true;
         } else if (this.isHittingPosition(p_180512_1_)) {
            blockstate = this.mc.world.getBlockState(p_180512_1_);
            if (blockstate.isAir(this.mc.world, p_180512_1_)) {
               this.isHittingBlock = false;
               return false;
            } else {
               this.curBlockDamageMP += blockstate.getPlayerRelativeBlockHardness(this.mc.player, this.mc.player.world, p_180512_1_);
               if (this.stepSoundTickCounter % 4.0F == 0.0F) {
                  SoundType soundtype = blockstate.getSoundType(this.mc.world, p_180512_1_, this.mc.player);
                  this.mc.getSoundHandler().play(new SimpleSound(soundtype.getHitSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 8.0F, soundtype.getPitch() * 0.5F, p_180512_1_));
               }

               ++this.stepSoundTickCounter;
               this.mc.getTutorial().onHitBlock(this.mc.world, p_180512_1_, blockstate, MathHelper.clamp(this.curBlockDamageMP, 0.0F, 1.0F));
               if (ForgeHooks.onLeftClickBlock(this.mc.player, p_180512_1_, p_180512_2_).getUseItem() == Result.DENY) {
                  return true;
               } else {
                  if (this.curBlockDamageMP >= 1.0F) {
                     this.isHittingBlock = false;
                     this.func_225324_a(CPlayerDiggingPacket.Action.STOP_DESTROY_BLOCK, p_180512_1_, p_180512_2_);
                     this.onPlayerDestroyBlock(p_180512_1_);
                     this.curBlockDamageMP = 0.0F;
                     this.stepSoundTickCounter = 0.0F;
                     this.blockHitDelay = 5;
                  }

                  this.mc.world.sendBlockBreakProgress(this.mc.player.getEntityId(), this.currentBlock, (int)(this.curBlockDamageMP * 10.0F) - 1);
                  return true;
               }
            }
         } else {
            return this.clickBlock(p_180512_1_, p_180512_2_);
         }
      }
   }

   public float getBlockReachDistance() {
      float attrib = (float)this.mc.player.getAttribute(PlayerEntity.REACH_DISTANCE).getValue();
      return this.currentGameType.isCreative() ? attrib : attrib - 0.5F;
   }

   public void tick() {
      this.syncCurrentPlayItem();
      if (this.connection.getNetworkManager().isChannelOpen()) {
         this.connection.getNetworkManager().tick();
      } else {
         this.connection.getNetworkManager().handleDisconnection();
      }

   }

   private boolean isHittingPosition(BlockPos p_178893_1_) {
      ItemStack itemstack = this.mc.player.getHeldItemMainhand();
      boolean flag = this.currentItemHittingBlock.isEmpty() && itemstack.isEmpty();
      if (!this.currentItemHittingBlock.isEmpty() && !itemstack.isEmpty()) {
         flag = !this.currentItemHittingBlock.shouldCauseBlockBreakReset(itemstack);
      }

      return p_178893_1_.equals(this.currentBlock) && flag;
   }

   private void syncCurrentPlayItem() {
      int i = this.mc.player.inventory.currentItem;
      if (i != this.currentPlayerItem) {
         this.currentPlayerItem = i;
         this.connection.sendPacket(new CHeldItemChangePacket(this.currentPlayerItem));
      }

   }

   public ActionResultType func_217292_a(ClientPlayerEntity p_217292_1_, ClientWorld p_217292_2_, Hand p_217292_3_, BlockRayTraceResult p_217292_4_) {
      this.syncCurrentPlayItem();
      BlockPos blockpos = p_217292_4_.getPos();
      if (!this.mc.world.getWorldBorder().contains(blockpos)) {
         return ActionResultType.FAIL;
      } else {
         ItemStack itemstack = p_217292_1_.getHeldItem(p_217292_3_);
         PlayerInteractEvent.RightClickBlock event = ForgeHooks.onRightClickBlock(p_217292_1_, p_217292_3_, blockpos, p_217292_4_.getFace());
         if (event.isCanceled()) {
            this.connection.sendPacket(new CPlayerTryUseItemOnBlockPacket(p_217292_3_, p_217292_4_));
            return event.getCancellationResult();
         } else if (this.currentGameType == GameType.SPECTATOR) {
            this.connection.sendPacket(new CPlayerTryUseItemOnBlockPacket(p_217292_3_, p_217292_4_));
            return ActionResultType.SUCCESS;
         } else {
            ItemUseContext itemusecontext = new ItemUseContext(p_217292_1_, p_217292_3_, p_217292_4_);
            if (event.getUseItem() != Result.DENY) {
               ActionResultType result = itemstack.onItemUseFirst(itemusecontext);
               if (result != ActionResultType.PASS) {
                  return result;
               }
            }

            boolean flag = !p_217292_1_.getHeldItemMainhand().doesSneakBypassUse(p_217292_2_, blockpos, p_217292_1_) || !p_217292_1_.getHeldItemOffhand().doesSneakBypassUse(p_217292_2_, blockpos, p_217292_1_);
            boolean flag1 = p_217292_1_.func_226563_dT_() && flag;
            ActionResultType actionresulttype1;
            if (event.getUseBlock() != Result.DENY && !flag1) {
               actionresulttype1 = p_217292_2_.getBlockState(blockpos).func_227031_a_(p_217292_2_, p_217292_1_, p_217292_3_, p_217292_4_);
               if (actionresulttype1.func_226246_a_()) {
                  this.connection.sendPacket(new CPlayerTryUseItemOnBlockPacket(p_217292_3_, p_217292_4_));
                  return actionresulttype1;
               }
            }

            this.connection.sendPacket(new CPlayerTryUseItemOnBlockPacket(p_217292_3_, p_217292_4_));
            if (event.getUseItem() == Result.DENY) {
               return ActionResultType.PASS;
            } else if (!itemstack.isEmpty() && !p_217292_1_.getCooldownTracker().hasCooldown(itemstack.getItem())) {
               if (this.currentGameType.isCreative()) {
                  int i = itemstack.getCount();
                  actionresulttype1 = itemstack.onItemUse(itemusecontext);
                  itemstack.setCount(i);
               } else {
                  actionresulttype1 = itemstack.onItemUse(itemusecontext);
               }

               return actionresulttype1;
            } else {
               return ActionResultType.PASS;
            }
         }
      }
   }

   public ActionResultType processRightClick(PlayerEntity p_187101_1_, World p_187101_2_, Hand p_187101_3_) {
      if (this.currentGameType == GameType.SPECTATOR) {
         return ActionResultType.PASS;
      } else {
         this.syncCurrentPlayItem();
         this.connection.sendPacket(new CPlayerTryUseItemPacket(p_187101_3_));
         ItemStack itemstack = p_187101_1_.getHeldItem(p_187101_3_);
         if (p_187101_1_.getCooldownTracker().hasCooldown(itemstack.getItem())) {
            return ActionResultType.PASS;
         } else {
            ActionResultType cancelResult = ForgeHooks.onItemRightClick(p_187101_1_, p_187101_3_);
            if (cancelResult != null) {
               return cancelResult;
            } else {
               int i = itemstack.getCount();
               ActionResult<ItemStack> actionresult = itemstack.useItemRightClick(p_187101_2_, p_187101_1_, p_187101_3_);
               ItemStack itemstack1 = (ItemStack)actionresult.getResult();
               if (itemstack1 != itemstack || itemstack1.getCount() != i) {
                  p_187101_1_.setHeldItem(p_187101_3_, itemstack1);
                  if (itemstack1.isEmpty()) {
                     ForgeEventFactory.onPlayerDestroyItem(p_187101_1_, itemstack, p_187101_3_);
                  }
               }

               return actionresult.getType();
            }
         }
      }
   }

   public ClientPlayerEntity createPlayer(ClientWorld p_199681_1_, StatisticsManager p_199681_2_, ClientRecipeBook p_199681_3_) {
      return new ClientPlayerEntity(this.mc, p_199681_1_, this.connection, p_199681_2_, p_199681_3_);
   }

   public void attackEntity(PlayerEntity p_78764_1_, Entity p_78764_2_) {
      this.syncCurrentPlayItem();
      this.connection.sendPacket(new CUseEntityPacket(p_78764_2_));
      if (this.currentGameType != GameType.SPECTATOR) {
         p_78764_1_.attackTargetEntityWithCurrentItem(p_78764_2_);
         p_78764_1_.resetCooldown();
      }

   }

   public ActionResultType interactWithEntity(PlayerEntity p_187097_1_, Entity p_187097_2_, Hand p_187097_3_) {
      this.syncCurrentPlayItem();
      this.connection.sendPacket(new CUseEntityPacket(p_187097_2_, p_187097_3_));
      if (this.currentGameType == GameType.SPECTATOR) {
         return ActionResultType.PASS;
      } else {
         ActionResultType cancelResult = ForgeHooks.onInteractEntity(p_187097_1_, p_187097_2_, p_187097_3_);
         if (cancelResult != null) {
            return cancelResult;
         } else {
            return this.currentGameType == GameType.SPECTATOR ? ActionResultType.PASS : p_187097_1_.interactOn(p_187097_2_, p_187097_3_);
         }
      }
   }

   public ActionResultType interactWithEntity(PlayerEntity p_187102_1_, Entity p_187102_2_, EntityRayTraceResult p_187102_3_, Hand p_187102_4_) {
      this.syncCurrentPlayItem();
      Vec3d vec3d = p_187102_3_.getHitVec().subtract(p_187102_2_.func_226277_ct_(), p_187102_2_.func_226278_cu_(), p_187102_2_.func_226281_cx_());
      this.connection.sendPacket(new CUseEntityPacket(p_187102_2_, p_187102_4_, vec3d));
      if (this.currentGameType == GameType.SPECTATOR) {
         return ActionResultType.PASS;
      } else {
         ActionResultType cancelResult = ForgeHooks.onInteractEntityAt(p_187102_1_, p_187102_2_, (RayTraceResult)p_187102_3_, p_187102_4_);
         if (cancelResult != null) {
            return cancelResult;
         } else {
            return this.currentGameType == GameType.SPECTATOR ? ActionResultType.PASS : p_187102_2_.applyPlayerInteraction(p_187102_1_, vec3d, p_187102_4_);
         }
      }
   }

   public ItemStack windowClick(int p_187098_1_, int p_187098_2_, int p_187098_3_, ClickType p_187098_4_, PlayerEntity p_187098_5_) {
      short short1 = p_187098_5_.openContainer.getNextTransactionID(p_187098_5_.inventory);
      ItemStack itemstack = p_187098_5_.openContainer.slotClick(p_187098_2_, p_187098_3_, p_187098_4_, p_187098_5_);
      this.connection.sendPacket(new CClickWindowPacket(p_187098_1_, p_187098_2_, p_187098_3_, p_187098_4_, itemstack, short1));
      return itemstack;
   }

   public void func_203413_a(int p_203413_1_, IRecipe<?> p_203413_2_, boolean p_203413_3_) {
      this.connection.sendPacket(new CPlaceRecipePacket(p_203413_1_, p_203413_2_, p_203413_3_));
   }

   public void sendEnchantPacket(int p_78756_1_, int p_78756_2_) {
      this.connection.sendPacket(new CEnchantItemPacket(p_78756_1_, p_78756_2_));
   }

   public void sendSlotPacket(ItemStack p_78761_1_, int p_78761_2_) {
      if (this.currentGameType.isCreative()) {
         this.connection.sendPacket(new CCreativeInventoryActionPacket(p_78761_2_, p_78761_1_));
      }

   }

   public void sendPacketDropItem(ItemStack p_78752_1_) {
      if (this.currentGameType.isCreative() && !p_78752_1_.isEmpty()) {
         this.connection.sendPacket(new CCreativeInventoryActionPacket(-1, p_78752_1_));
      }

   }

   public void onStoppedUsingItem(PlayerEntity p_78766_1_) {
      this.syncCurrentPlayItem();
      this.connection.sendPacket(new CPlayerDiggingPacket(CPlayerDiggingPacket.Action.RELEASE_USE_ITEM, BlockPos.ZERO, Direction.DOWN));
      p_78766_1_.stopActiveHand();
   }

   public boolean gameIsSurvivalOrAdventure() {
      return this.currentGameType.isSurvivalOrAdventure();
   }

   public boolean isNotCreative() {
      return !this.currentGameType.isCreative();
   }

   public boolean isInCreativeMode() {
      return this.currentGameType.isCreative();
   }

   public boolean extendedReach() {
      return this.currentGameType.isCreative();
   }

   public boolean isRidingHorse() {
      return this.mc.player.isPassenger() && this.mc.player.getRidingEntity() instanceof AbstractHorseEntity;
   }

   public boolean isSpectatorMode() {
      return this.currentGameType == GameType.SPECTATOR;
   }

   public GameType getCurrentGameType() {
      return this.currentGameType;
   }

   public boolean getIsHittingBlock() {
      return this.isHittingBlock;
   }

   public void pickItem(int p_187100_1_) {
      this.connection.sendPacket(new CPickItemPacket(p_187100_1_));
   }

   private void func_225324_a(CPlayerDiggingPacket.Action p_225324_1_, BlockPos p_225324_2_, Direction p_225324_3_) {
      ClientPlayerEntity clientplayerentity = this.mc.player;
      this.field_225326_k.put(Pair.of(p_225324_2_, p_225324_1_), new PosAndRotation(clientplayerentity.getPositionVec(), clientplayerentity.rotationPitch, clientplayerentity.rotationYaw));
      this.connection.sendPacket(new CPlayerDiggingPacket(p_225324_1_, p_225324_2_, p_225324_3_));
   }

   public void func_225323_a(ClientWorld p_225323_1_, BlockPos p_225323_2_, BlockState p_225323_3_, CPlayerDiggingPacket.Action p_225323_4_, boolean p_225323_5_) {
      PosAndRotation posandrotation = (PosAndRotation)this.field_225326_k.remove(Pair.of(p_225323_2_, p_225323_4_));
      if (posandrotation == null || !p_225323_5_ || p_225323_4_ != CPlayerDiggingPacket.Action.START_DESTROY_BLOCK && p_225323_1_.getBlockState(p_225323_2_) != p_225323_3_) {
         p_225323_1_.invalidateRegionAndSetBlock(p_225323_2_, p_225323_3_);
         if (posandrotation != null) {
            Vec3d vec3d = posandrotation.func_224783_a();
            this.mc.player.setPositionAndRotation(vec3d.x, vec3d.y, vec3d.z, posandrotation.func_224785_c(), posandrotation.func_224784_b());
         }
      }

      while(this.field_225326_k.size() >= 50) {
         Pair<BlockPos, CPlayerDiggingPacket.Action> pair = (Pair)this.field_225326_k.firstKey();
         this.field_225326_k.removeFirst();
         field_225325_a.error("Too many unacked block actions, dropping " + pair);
      }

   }
}
