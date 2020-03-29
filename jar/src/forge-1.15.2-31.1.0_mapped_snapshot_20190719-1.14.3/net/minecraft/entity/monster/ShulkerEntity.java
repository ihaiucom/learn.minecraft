package net.minecraft.entity.monster;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.PistonHeadBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.Pose;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.controller.BodyController;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ShulkerBulletEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;

public class ShulkerEntity extends GolemEntity implements IMob {
   private static final UUID COVERED_ARMOR_BONUS_ID = UUID.fromString("7E0292F2-9434-48D5-A29F-9583AF7DF27F");
   private static final AttributeModifier COVERED_ARMOR_BONUS_MODIFIER;
   protected static final DataParameter<Direction> ATTACHED_FACE;
   protected static final DataParameter<Optional<BlockPos>> ATTACHED_BLOCK_POS;
   protected static final DataParameter<Byte> PEEK_TICK;
   protected static final DataParameter<Byte> COLOR;
   private float prevPeekAmount;
   private float peekAmount;
   private BlockPos currentAttachmentPosition;
   private int clientSideTeleportInterpolation;

   public ShulkerEntity(EntityType<? extends ShulkerEntity> p_i50196_1_, World p_i50196_2_) {
      super(p_i50196_1_, p_i50196_2_);
      this.prevRenderYawOffset = 180.0F;
      this.renderYawOffset = 180.0F;
      this.currentAttachmentPosition = null;
      this.experienceValue = 5;
   }

   @Nullable
   public ILivingEntityData onInitialSpawn(IWorld p_213386_1_, DifficultyInstance p_213386_2_, SpawnReason p_213386_3_, @Nullable ILivingEntityData p_213386_4_, @Nullable CompoundNBT p_213386_5_) {
      this.renderYawOffset = 180.0F;
      this.prevRenderYawOffset = 180.0F;
      this.rotationYaw = 180.0F;
      this.prevRotationYaw = 180.0F;
      this.rotationYawHead = 180.0F;
      this.prevRotationYawHead = 180.0F;
      return super.onInitialSpawn(p_213386_1_, p_213386_2_, p_213386_3_, p_213386_4_, p_213386_5_);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(1, new LookAtGoal(this, PlayerEntity.class, 8.0F));
      this.goalSelector.addGoal(4, new ShulkerEntity.AttackGoal());
      this.goalSelector.addGoal(7, new ShulkerEntity.PeekGoal());
      this.goalSelector.addGoal(8, new LookRandomlyGoal(this));
      this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, new Class[0])).setCallsForHelp());
      this.targetSelector.addGoal(2, new ShulkerEntity.AttackNearestGoal(this));
      this.targetSelector.addGoal(3, new ShulkerEntity.DefenseAttackGoal(this));
   }

   protected boolean func_225502_at_() {
      return false;
   }

   public SoundCategory getSoundCategory() {
      return SoundCategory.HOSTILE;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_SHULKER_AMBIENT;
   }

   public void playAmbientSound() {
      if (!this.isClosed()) {
         super.playAmbientSound();
      }

   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_SHULKER_DEATH;
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return this.isClosed() ? SoundEvents.ENTITY_SHULKER_HURT_CLOSED : SoundEvents.ENTITY_SHULKER_HURT;
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(ATTACHED_FACE, Direction.DOWN);
      this.dataManager.register(ATTACHED_BLOCK_POS, Optional.empty());
      this.dataManager.register(PEEK_TICK, (byte)0);
      this.dataManager.register(COLOR, (byte)16);
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(30.0D);
   }

   protected BodyController createBodyController() {
      return new ShulkerEntity.BodyHelperController(this);
   }

   public void readAdditional(CompoundNBT p_70037_1_) {
      super.readAdditional(p_70037_1_);
      this.dataManager.set(ATTACHED_FACE, Direction.byIndex(p_70037_1_.getByte("AttachFace")));
      this.dataManager.set(PEEK_TICK, p_70037_1_.getByte("Peek"));
      this.dataManager.set(COLOR, p_70037_1_.getByte("Color"));
      if (p_70037_1_.contains("APX")) {
         int i = p_70037_1_.getInt("APX");
         int j = p_70037_1_.getInt("APY");
         int k = p_70037_1_.getInt("APZ");
         this.dataManager.set(ATTACHED_BLOCK_POS, Optional.of(new BlockPos(i, j, k)));
      } else {
         this.dataManager.set(ATTACHED_BLOCK_POS, Optional.empty());
      }

   }

   public void writeAdditional(CompoundNBT p_213281_1_) {
      super.writeAdditional(p_213281_1_);
      p_213281_1_.putByte("AttachFace", (byte)((Direction)this.dataManager.get(ATTACHED_FACE)).getIndex());
      p_213281_1_.putByte("Peek", (Byte)this.dataManager.get(PEEK_TICK));
      p_213281_1_.putByte("Color", (Byte)this.dataManager.get(COLOR));
      BlockPos blockpos = this.getAttachmentPos();
      if (blockpos != null) {
         p_213281_1_.putInt("APX", blockpos.getX());
         p_213281_1_.putInt("APY", blockpos.getY());
         p_213281_1_.putInt("APZ", blockpos.getZ());
      }

   }

   public void tick() {
      super.tick();
      BlockPos blockpos = (BlockPos)((Optional)this.dataManager.get(ATTACHED_BLOCK_POS)).orElse((BlockPos)null);
      if (blockpos == null && !this.world.isRemote) {
         blockpos = new BlockPos(this);
         this.dataManager.set(ATTACHED_BLOCK_POS, Optional.of(blockpos));
      }

      float f1;
      if (this.isPassenger()) {
         blockpos = null;
         f1 = this.getRidingEntity().rotationYaw;
         this.rotationYaw = f1;
         this.renderYawOffset = f1;
         this.prevRenderYawOffset = f1;
         this.clientSideTeleportInterpolation = 0;
      } else if (!this.world.isRemote) {
         BlockState blockstate = this.world.getBlockState(blockpos);
         if (!blockstate.isAir(this.world, blockpos)) {
            Direction direction2;
            if (blockstate.getBlock() == Blocks.MOVING_PISTON) {
               direction2 = (Direction)blockstate.get(PistonBlock.FACING);
               if (this.world.isAirBlock(blockpos.offset(direction2))) {
                  blockpos = blockpos.offset(direction2);
                  this.dataManager.set(ATTACHED_BLOCK_POS, Optional.of(blockpos));
               } else {
                  this.tryTeleportToNewPosition();
               }
            } else if (blockstate.getBlock() == Blocks.PISTON_HEAD) {
               direction2 = (Direction)blockstate.get(PistonHeadBlock.FACING);
               if (this.world.isAirBlock(blockpos.offset(direction2))) {
                  blockpos = blockpos.offset(direction2);
                  this.dataManager.set(ATTACHED_BLOCK_POS, Optional.of(blockpos));
               } else {
                  this.tryTeleportToNewPosition();
               }
            } else {
               this.tryTeleportToNewPosition();
            }
         }

         BlockPos blockpos1 = blockpos.offset(this.getAttachmentFacing());
         if (!this.world.func_217400_a(blockpos1, this)) {
            boolean flag = false;
            Direction[] var5 = Direction.values();
            int var6 = var5.length;

            for(int var7 = 0; var7 < var6; ++var7) {
               Direction direction1 = var5[var7];
               blockpos1 = blockpos.offset(direction1);
               if (this.world.func_217400_a(blockpos1, this)) {
                  this.dataManager.set(ATTACHED_FACE, direction1);
                  flag = true;
                  break;
               }
            }

            if (!flag) {
               this.tryTeleportToNewPosition();
            }
         }

         BlockPos blockpos2 = blockpos.offset(this.getAttachmentFacing().getOpposite());
         if (this.world.func_217400_a(blockpos2, this)) {
            this.tryTeleportToNewPosition();
         }
      }

      f1 = (float)this.getPeekTick() * 0.01F;
      this.prevPeekAmount = this.peekAmount;
      if (this.peekAmount > f1) {
         this.peekAmount = MathHelper.clamp(this.peekAmount - 0.05F, f1, 1.0F);
      } else if (this.peekAmount < f1) {
         this.peekAmount = MathHelper.clamp(this.peekAmount + 0.05F, 0.0F, f1);
      }

      if (blockpos != null) {
         if (this.world.isRemote) {
            if (this.clientSideTeleportInterpolation > 0 && this.currentAttachmentPosition != null) {
               --this.clientSideTeleportInterpolation;
            } else {
               this.currentAttachmentPosition = blockpos;
            }
         }

         this.func_226286_f_((double)blockpos.getX() + 0.5D, (double)blockpos.getY(), (double)blockpos.getZ() + 0.5D);
         double d0 = 0.5D - (double)MathHelper.sin((0.5F + this.peekAmount) * 3.1415927F) * 0.5D;
         double d1 = 0.5D - (double)MathHelper.sin((0.5F + this.prevPeekAmount) * 3.1415927F) * 0.5D;
         if (this.isAddedToWorld() && this.world instanceof ServerWorld) {
            ((ServerWorld)this.world).chunkCheck(this);
         }

         Direction direction3 = this.getAttachmentFacing().getOpposite();
         this.setBoundingBox((new AxisAlignedBB(this.func_226277_ct_() - 0.5D, this.func_226278_cu_(), this.func_226281_cx_() - 0.5D, this.func_226277_ct_() + 0.5D, this.func_226278_cu_() + 1.0D, this.func_226281_cx_() + 0.5D)).expand((double)direction3.getXOffset() * d0, (double)direction3.getYOffset() * d0, (double)direction3.getZOffset() * d0));
         double d2 = d0 - d1;
         if (d2 > 0.0D) {
            List<Entity> list = this.world.getEntitiesWithinAABBExcludingEntity(this, this.getBoundingBox());
            if (!list.isEmpty()) {
               Iterator var11 = list.iterator();

               while(var11.hasNext()) {
                  Entity entity = (Entity)var11.next();
                  if (!(entity instanceof ShulkerEntity) && !entity.noClip) {
                     entity.move(MoverType.SHULKER, new Vec3d(d2 * (double)direction3.getXOffset(), d2 * (double)direction3.getYOffset(), d2 * (double)direction3.getZOffset()));
                  }
               }
            }
         }
      }

   }

   public void move(MoverType p_213315_1_, Vec3d p_213315_2_) {
      if (p_213315_1_ == MoverType.SHULKER_BOX) {
         this.tryTeleportToNewPosition();
      } else {
         super.move(p_213315_1_, p_213315_2_);
      }

   }

   public void setPosition(double p_70107_1_, double p_70107_3_, double p_70107_5_) {
      super.setPosition(p_70107_1_, p_70107_3_, p_70107_5_);
      if (this.dataManager != null && this.ticksExisted != 0) {
         Optional<BlockPos> optional = (Optional)this.dataManager.get(ATTACHED_BLOCK_POS);
         Optional<BlockPos> optional1 = Optional.of(new BlockPos(p_70107_1_, p_70107_3_, p_70107_5_));
         if (!optional1.equals(optional)) {
            this.dataManager.set(ATTACHED_BLOCK_POS, optional1);
            this.dataManager.set(PEEK_TICK, (byte)0);
            this.isAirBorne = true;
         }
      }

   }

   protected boolean tryTeleportToNewPosition() {
      if (!this.isAIDisabled() && this.isAlive()) {
         BlockPos blockpos = new BlockPos(this);

         for(int i = 0; i < 5; ++i) {
            BlockPos blockpos1 = blockpos.add(8 - this.rand.nextInt(17), 8 - this.rand.nextInt(17), 8 - this.rand.nextInt(17));
            if (blockpos1.getY() > 0 && this.world.isAirBlock(blockpos1) && this.world.getWorldBorder().contains(blockpos1) && this.world.func_226665_a__(this, new AxisAlignedBB(blockpos1))) {
               boolean flag = false;
               Direction[] var5 = Direction.values();
               int var6 = var5.length;

               for(int var7 = 0; var7 < var6; ++var7) {
                  Direction direction = var5[var7];
                  if (this.world.func_217400_a(blockpos1.offset(direction), this)) {
                     this.dataManager.set(ATTACHED_FACE, direction);
                     flag = true;
                     break;
                  }
               }

               if (flag) {
                  EnderTeleportEvent event = new EnderTeleportEvent(this, (double)blockpos1.getX(), (double)blockpos1.getY(), (double)blockpos1.getZ(), 0.0F);
                  if (MinecraftForge.EVENT_BUS.post(event)) {
                     flag = false;
                  }

                  blockpos1 = new BlockPos(event.getTargetX(), event.getTargetY(), event.getTargetZ());
               }

               if (flag) {
                  this.playSound(SoundEvents.ENTITY_SHULKER_TELEPORT, 1.0F, 1.0F);
                  this.dataManager.set(ATTACHED_BLOCK_POS, Optional.of(blockpos1));
                  this.dataManager.set(PEEK_TICK, (byte)0);
                  this.setAttackTarget((LivingEntity)null);
                  return true;
               }
            }
         }

         return false;
      } else {
         return true;
      }
   }

   public void livingTick() {
      super.livingTick();
      this.setMotion(Vec3d.ZERO);
      this.prevRenderYawOffset = 180.0F;
      this.renderYawOffset = 180.0F;
      this.rotationYaw = 180.0F;
   }

   public void notifyDataManagerChange(DataParameter<?> p_184206_1_) {
      if (ATTACHED_BLOCK_POS.equals(p_184206_1_) && this.world.isRemote && !this.isPassenger()) {
         BlockPos blockpos = this.getAttachmentPos();
         if (blockpos != null) {
            if (this.currentAttachmentPosition == null) {
               this.currentAttachmentPosition = blockpos;
            } else {
               this.clientSideTeleportInterpolation = 6;
            }

            this.func_226286_f_((double)blockpos.getX() + 0.5D, (double)blockpos.getY(), (double)blockpos.getZ() + 0.5D);
         }
      }

      super.notifyDataManagerChange(p_184206_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public void setPositionAndRotationDirect(double p_180426_1_, double p_180426_3_, double p_180426_5_, float p_180426_7_, float p_180426_8_, int p_180426_9_, boolean p_180426_10_) {
      this.newPosRotationIncrements = 0;
   }

   public boolean attackEntityFrom(DamageSource p_70097_1_, float p_70097_2_) {
      if (this.isClosed()) {
         Entity entity = p_70097_1_.getImmediateSource();
         if (entity instanceof AbstractArrowEntity) {
            return false;
         }
      }

      if (super.attackEntityFrom(p_70097_1_, p_70097_2_)) {
         if ((double)this.getHealth() < (double)this.getMaxHealth() * 0.5D && this.rand.nextInt(4) == 0) {
            this.tryTeleportToNewPosition();
         }

         return true;
      } else {
         return false;
      }
   }

   private boolean isClosed() {
      return this.getPeekTick() == 0;
   }

   @Nullable
   public AxisAlignedBB getCollisionBoundingBox() {
      return this.isAlive() ? this.getBoundingBox() : null;
   }

   public Direction getAttachmentFacing() {
      return (Direction)this.dataManager.get(ATTACHED_FACE);
   }

   @Nullable
   public BlockPos getAttachmentPos() {
      return (BlockPos)((Optional)this.dataManager.get(ATTACHED_BLOCK_POS)).orElse((BlockPos)null);
   }

   public void setAttachmentPos(@Nullable BlockPos p_184694_1_) {
      this.dataManager.set(ATTACHED_BLOCK_POS, Optional.ofNullable(p_184694_1_));
   }

   public int getPeekTick() {
      return (Byte)this.dataManager.get(PEEK_TICK);
   }

   public void updateArmorModifier(int p_184691_1_) {
      if (!this.world.isRemote) {
         this.getAttribute(SharedMonsterAttributes.ARMOR).removeModifier(COVERED_ARMOR_BONUS_MODIFIER);
         if (p_184691_1_ == 0) {
            this.getAttribute(SharedMonsterAttributes.ARMOR).applyModifier(COVERED_ARMOR_BONUS_MODIFIER);
            this.playSound(SoundEvents.ENTITY_SHULKER_CLOSE, 1.0F, 1.0F);
         } else {
            this.playSound(SoundEvents.ENTITY_SHULKER_OPEN, 1.0F, 1.0F);
         }
      }

      this.dataManager.set(PEEK_TICK, (byte)p_184691_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public float getClientPeekAmount(float p_184688_1_) {
      return MathHelper.lerp(p_184688_1_, this.prevPeekAmount, this.peekAmount);
   }

   @OnlyIn(Dist.CLIENT)
   public int getClientTeleportInterp() {
      return this.clientSideTeleportInterpolation;
   }

   @OnlyIn(Dist.CLIENT)
   public BlockPos getOldAttachPos() {
      return this.currentAttachmentPosition;
   }

   protected float getStandingEyeHeight(Pose p_213348_1_, EntitySize p_213348_2_) {
      return 0.5F;
   }

   public int getVerticalFaceSpeed() {
      return 180;
   }

   public int getHorizontalFaceSpeed() {
      return 180;
   }

   public void applyEntityCollision(Entity p_70108_1_) {
   }

   public float getCollisionBorderSize() {
      return 0.0F;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isAttachedToBlock() {
      return this.currentAttachmentPosition != null && this.getAttachmentPos() != null;
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public DyeColor getColor() {
      Byte obyte = (Byte)this.dataManager.get(COLOR);
      return obyte != 16 && obyte <= 15 ? DyeColor.byId(obyte) : null;
   }

   static {
      COVERED_ARMOR_BONUS_MODIFIER = (new AttributeModifier(COVERED_ARMOR_BONUS_ID, "Covered armor bonus", 20.0D, AttributeModifier.Operation.ADDITION)).setSaved(false);
      ATTACHED_FACE = EntityDataManager.createKey(ShulkerEntity.class, DataSerializers.DIRECTION);
      ATTACHED_BLOCK_POS = EntityDataManager.createKey(ShulkerEntity.class, DataSerializers.OPTIONAL_BLOCK_POS);
      PEEK_TICK = EntityDataManager.createKey(ShulkerEntity.class, DataSerializers.BYTE);
      COLOR = EntityDataManager.createKey(ShulkerEntity.class, DataSerializers.BYTE);
   }

   class PeekGoal extends Goal {
      private int peekTime;

      private PeekGoal() {
      }

      public boolean shouldExecute() {
         return ShulkerEntity.this.getAttackTarget() == null && ShulkerEntity.this.rand.nextInt(40) == 0;
      }

      public boolean shouldContinueExecuting() {
         return ShulkerEntity.this.getAttackTarget() == null && this.peekTime > 0;
      }

      public void startExecuting() {
         this.peekTime = 20 * (1 + ShulkerEntity.this.rand.nextInt(3));
         ShulkerEntity.this.updateArmorModifier(30);
      }

      public void resetTask() {
         if (ShulkerEntity.this.getAttackTarget() == null) {
            ShulkerEntity.this.updateArmorModifier(0);
         }

      }

      public void tick() {
         --this.peekTime;
      }

      // $FF: synthetic method
      PeekGoal(Object p_i47059_2_) {
         this();
      }
   }

   static class DefenseAttackGoal extends NearestAttackableTargetGoal<LivingEntity> {
      public DefenseAttackGoal(ShulkerEntity p_i47061_1_) {
         super(p_i47061_1_, LivingEntity.class, 10, true, false, (p_lambda$new$0_0_) -> {
            return p_lambda$new$0_0_ instanceof IMob;
         });
      }

      public boolean shouldExecute() {
         return this.goalOwner.getTeam() == null ? false : super.shouldExecute();
      }

      protected AxisAlignedBB getTargetableArea(double p_188511_1_) {
         Direction direction = ((ShulkerEntity)this.goalOwner).getAttachmentFacing();
         if (direction.getAxis() == Direction.Axis.X) {
            return this.goalOwner.getBoundingBox().grow(4.0D, p_188511_1_, p_188511_1_);
         } else {
            return direction.getAxis() == Direction.Axis.Z ? this.goalOwner.getBoundingBox().grow(p_188511_1_, p_188511_1_, 4.0D) : this.goalOwner.getBoundingBox().grow(p_188511_1_, 4.0D, p_188511_1_);
         }
      }
   }

   class BodyHelperController extends BodyController {
      public BodyHelperController(MobEntity p_i50612_2_) {
         super(p_i50612_2_);
      }

      public void updateRenderAngles() {
      }
   }

   class AttackNearestGoal extends NearestAttackableTargetGoal<PlayerEntity> {
      public AttackNearestGoal(ShulkerEntity p_i47060_2_) {
         super(p_i47060_2_, PlayerEntity.class, true);
      }

      public boolean shouldExecute() {
         return ShulkerEntity.this.world.getDifficulty() == Difficulty.PEACEFUL ? false : super.shouldExecute();
      }

      protected AxisAlignedBB getTargetableArea(double p_188511_1_) {
         Direction direction = ((ShulkerEntity)this.goalOwner).getAttachmentFacing();
         if (direction.getAxis() == Direction.Axis.X) {
            return this.goalOwner.getBoundingBox().grow(4.0D, p_188511_1_, p_188511_1_);
         } else {
            return direction.getAxis() == Direction.Axis.Z ? this.goalOwner.getBoundingBox().grow(p_188511_1_, p_188511_1_, 4.0D) : this.goalOwner.getBoundingBox().grow(p_188511_1_, 4.0D, p_188511_1_);
         }
      }
   }

   class AttackGoal extends Goal {
      private int attackTime;

      public AttackGoal() {
         this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
      }

      public boolean shouldExecute() {
         LivingEntity livingentity = ShulkerEntity.this.getAttackTarget();
         if (livingentity != null && livingentity.isAlive()) {
            return ShulkerEntity.this.world.getDifficulty() != Difficulty.PEACEFUL;
         } else {
            return false;
         }
      }

      public void startExecuting() {
         this.attackTime = 20;
         ShulkerEntity.this.updateArmorModifier(100);
      }

      public void resetTask() {
         ShulkerEntity.this.updateArmorModifier(0);
      }

      public void tick() {
         if (ShulkerEntity.this.world.getDifficulty() != Difficulty.PEACEFUL) {
            --this.attackTime;
            LivingEntity livingentity = ShulkerEntity.this.getAttackTarget();
            ShulkerEntity.this.getLookController().setLookPositionWithEntity(livingentity, 180.0F, 180.0F);
            double d0 = ShulkerEntity.this.getDistanceSq(livingentity);
            if (d0 < 400.0D) {
               if (this.attackTime <= 0) {
                  this.attackTime = 20 + ShulkerEntity.this.rand.nextInt(10) * 20 / 2;
                  ShulkerEntity.this.world.addEntity(new ShulkerBulletEntity(ShulkerEntity.this.world, ShulkerEntity.this, livingentity, ShulkerEntity.this.getAttachmentFacing().getAxis()));
                  ShulkerEntity.this.playSound(SoundEvents.ENTITY_SHULKER_SHOOT, 2.0F, (ShulkerEntity.this.rand.nextFloat() - ShulkerEntity.this.rand.nextFloat()) * 0.2F + 1.0F);
               }
            } else {
               ShulkerEntity.this.setAttackTarget((LivingEntity)null);
            }

            super.tick();
         }

      }
   }
}
