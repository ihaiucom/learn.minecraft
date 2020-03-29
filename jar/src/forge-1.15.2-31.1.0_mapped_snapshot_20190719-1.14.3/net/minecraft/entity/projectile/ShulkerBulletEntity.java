package net.minecraft.entity.projectile;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.ForgeEventFactory;

public class ShulkerBulletEntity extends Entity {
   private LivingEntity owner;
   private Entity target;
   @Nullable
   private Direction direction;
   private int steps;
   private double targetDeltaX;
   private double targetDeltaY;
   private double targetDeltaZ;
   @Nullable
   private UUID ownerUniqueId;
   private BlockPos ownerBlockPos;
   @Nullable
   private UUID targetUniqueId;
   private BlockPos targetBlockPos;

   public ShulkerBulletEntity(EntityType<? extends ShulkerBulletEntity> p_i50161_1_, World p_i50161_2_) {
      super(p_i50161_1_, p_i50161_2_);
      this.noClip = true;
   }

   @OnlyIn(Dist.CLIENT)
   public ShulkerBulletEntity(World p_i46771_1_, double p_i46771_2_, double p_i46771_4_, double p_i46771_6_, double p_i46771_8_, double p_i46771_10_, double p_i46771_12_) {
      this(EntityType.SHULKER_BULLET, p_i46771_1_);
      this.setLocationAndAngles(p_i46771_2_, p_i46771_4_, p_i46771_6_, this.rotationYaw, this.rotationPitch);
      this.setMotion(p_i46771_8_, p_i46771_10_, p_i46771_12_);
   }

   public ShulkerBulletEntity(World p_i46772_1_, LivingEntity p_i46772_2_, Entity p_i46772_3_, Direction.Axis p_i46772_4_) {
      this(EntityType.SHULKER_BULLET, p_i46772_1_);
      this.owner = p_i46772_2_;
      BlockPos blockpos = new BlockPos(p_i46772_2_);
      double d0 = (double)blockpos.getX() + 0.5D;
      double d1 = (double)blockpos.getY() + 0.5D;
      double d2 = (double)blockpos.getZ() + 0.5D;
      this.setLocationAndAngles(d0, d1, d2, this.rotationYaw, this.rotationPitch);
      this.target = p_i46772_3_;
      this.direction = Direction.UP;
      this.selectNextMoveDirection(p_i46772_4_);
   }

   public SoundCategory getSoundCategory() {
      return SoundCategory.HOSTILE;
   }

   protected void writeAdditional(CompoundNBT p_213281_1_) {
      BlockPos blockpos1;
      CompoundNBT compoundnbt1;
      if (this.owner != null) {
         blockpos1 = new BlockPos(this.owner);
         compoundnbt1 = NBTUtil.writeUniqueId(this.owner.getUniqueID());
         compoundnbt1.putInt("X", blockpos1.getX());
         compoundnbt1.putInt("Y", blockpos1.getY());
         compoundnbt1.putInt("Z", blockpos1.getZ());
         p_213281_1_.put("Owner", compoundnbt1);
      }

      if (this.target != null) {
         blockpos1 = new BlockPos(this.target);
         compoundnbt1 = NBTUtil.writeUniqueId(this.target.getUniqueID());
         compoundnbt1.putInt("X", blockpos1.getX());
         compoundnbt1.putInt("Y", blockpos1.getY());
         compoundnbt1.putInt("Z", blockpos1.getZ());
         p_213281_1_.put("Target", compoundnbt1);
      }

      if (this.direction != null) {
         p_213281_1_.putInt("Dir", this.direction.getIndex());
      }

      p_213281_1_.putInt("Steps", this.steps);
      p_213281_1_.putDouble("TXD", this.targetDeltaX);
      p_213281_1_.putDouble("TYD", this.targetDeltaY);
      p_213281_1_.putDouble("TZD", this.targetDeltaZ);
   }

   protected void readAdditional(CompoundNBT p_70037_1_) {
      this.steps = p_70037_1_.getInt("Steps");
      this.targetDeltaX = p_70037_1_.getDouble("TXD");
      this.targetDeltaY = p_70037_1_.getDouble("TYD");
      this.targetDeltaZ = p_70037_1_.getDouble("TZD");
      if (p_70037_1_.contains("Dir", 99)) {
         this.direction = Direction.byIndex(p_70037_1_.getInt("Dir"));
      }

      CompoundNBT compoundnbt1;
      if (p_70037_1_.contains("Owner", 10)) {
         compoundnbt1 = p_70037_1_.getCompound("Owner");
         this.ownerUniqueId = NBTUtil.readUniqueId(compoundnbt1);
         this.ownerBlockPos = new BlockPos(compoundnbt1.getInt("X"), compoundnbt1.getInt("Y"), compoundnbt1.getInt("Z"));
      }

      if (p_70037_1_.contains("Target", 10)) {
         compoundnbt1 = p_70037_1_.getCompound("Target");
         this.targetUniqueId = NBTUtil.readUniqueId(compoundnbt1);
         this.targetBlockPos = new BlockPos(compoundnbt1.getInt("X"), compoundnbt1.getInt("Y"), compoundnbt1.getInt("Z"));
      }

   }

   protected void registerData() {
   }

   private void setDirection(@Nullable Direction p_184568_1_) {
      this.direction = p_184568_1_;
   }

   private void selectNextMoveDirection(@Nullable Direction.Axis p_184569_1_) {
      double d0 = 0.5D;
      BlockPos blockpos;
      if (this.target == null) {
         blockpos = (new BlockPos(this)).down();
      } else {
         d0 = (double)this.target.getHeight() * 0.5D;
         blockpos = new BlockPos(this.target.func_226277_ct_(), this.target.func_226278_cu_() + d0, this.target.func_226281_cx_());
      }

      double d1 = (double)blockpos.getX() + 0.5D;
      double d2 = (double)blockpos.getY() + d0;
      double d3 = (double)blockpos.getZ() + 0.5D;
      Direction direction = null;
      if (!blockpos.withinDistance(this.getPositionVec(), 2.0D)) {
         BlockPos blockpos1 = new BlockPos(this);
         List<Direction> list = Lists.newArrayList();
         if (p_184569_1_ != Direction.Axis.X) {
            if (blockpos1.getX() < blockpos.getX() && this.world.isAirBlock(blockpos1.east())) {
               list.add(Direction.EAST);
            } else if (blockpos1.getX() > blockpos.getX() && this.world.isAirBlock(blockpos1.west())) {
               list.add(Direction.WEST);
            }
         }

         if (p_184569_1_ != Direction.Axis.Y) {
            if (blockpos1.getY() < blockpos.getY() && this.world.isAirBlock(blockpos1.up())) {
               list.add(Direction.UP);
            } else if (blockpos1.getY() > blockpos.getY() && this.world.isAirBlock(blockpos1.down())) {
               list.add(Direction.DOWN);
            }
         }

         if (p_184569_1_ != Direction.Axis.Z) {
            if (blockpos1.getZ() < blockpos.getZ() && this.world.isAirBlock(blockpos1.south())) {
               list.add(Direction.SOUTH);
            } else if (blockpos1.getZ() > blockpos.getZ() && this.world.isAirBlock(blockpos1.north())) {
               list.add(Direction.NORTH);
            }
         }

         direction = Direction.random(this.rand);
         if (list.isEmpty()) {
            for(int i = 5; !this.world.isAirBlock(blockpos1.offset(direction)) && i > 0; --i) {
               direction = Direction.random(this.rand);
            }
         } else {
            direction = (Direction)list.get(this.rand.nextInt(list.size()));
         }

         d1 = this.func_226277_ct_() + (double)direction.getXOffset();
         d2 = this.func_226278_cu_() + (double)direction.getYOffset();
         d3 = this.func_226281_cx_() + (double)direction.getZOffset();
      }

      this.setDirection(direction);
      double d6 = d1 - this.func_226277_ct_();
      double d7 = d2 - this.func_226278_cu_();
      double d4 = d3 - this.func_226281_cx_();
      double d5 = (double)MathHelper.sqrt(d6 * d6 + d7 * d7 + d4 * d4);
      if (d5 == 0.0D) {
         this.targetDeltaX = 0.0D;
         this.targetDeltaY = 0.0D;
         this.targetDeltaZ = 0.0D;
      } else {
         this.targetDeltaX = d6 / d5 * 0.15D;
         this.targetDeltaY = d7 / d5 * 0.15D;
         this.targetDeltaZ = d4 / d5 * 0.15D;
      }

      this.isAirBorne = true;
      this.steps = 10 + this.rand.nextInt(5) * 10;
   }

   public void checkDespawn() {
      if (this.world.getDifficulty() == Difficulty.PEACEFUL) {
         this.remove();
      }

   }

   public void tick() {
      super.tick();
      Vec3d vec3d;
      if (!this.world.isRemote) {
         Iterator var1;
         LivingEntity livingentity1;
         if (this.target == null && this.targetUniqueId != null) {
            var1 = this.world.getEntitiesWithinAABB(LivingEntity.class, new AxisAlignedBB(this.targetBlockPos.add(-2, -2, -2), this.targetBlockPos.add(2, 2, 2))).iterator();

            while(var1.hasNext()) {
               livingentity1 = (LivingEntity)var1.next();
               if (livingentity1.getUniqueID().equals(this.targetUniqueId)) {
                  this.target = livingentity1;
                  break;
               }
            }

            this.targetUniqueId = null;
         }

         if (this.owner == null && this.ownerUniqueId != null) {
            var1 = this.world.getEntitiesWithinAABB(LivingEntity.class, new AxisAlignedBB(this.ownerBlockPos.add(-2, -2, -2), this.ownerBlockPos.add(2, 2, 2))).iterator();

            while(var1.hasNext()) {
               livingentity1 = (LivingEntity)var1.next();
               if (livingentity1.getUniqueID().equals(this.ownerUniqueId)) {
                  this.owner = livingentity1;
                  break;
               }
            }

            this.ownerUniqueId = null;
         }

         if (this.target == null || !this.target.isAlive() || this.target instanceof PlayerEntity && ((PlayerEntity)this.target).isSpectator()) {
            if (!this.hasNoGravity()) {
               this.setMotion(this.getMotion().add(0.0D, -0.04D, 0.0D));
            }
         } else {
            this.targetDeltaX = MathHelper.clamp(this.targetDeltaX * 1.025D, -1.0D, 1.0D);
            this.targetDeltaY = MathHelper.clamp(this.targetDeltaY * 1.025D, -1.0D, 1.0D);
            this.targetDeltaZ = MathHelper.clamp(this.targetDeltaZ * 1.025D, -1.0D, 1.0D);
            vec3d = this.getMotion();
            this.setMotion(vec3d.add((this.targetDeltaX - vec3d.x) * 0.2D, (this.targetDeltaY - vec3d.y) * 0.2D, (this.targetDeltaZ - vec3d.z) * 0.2D));
         }

         RayTraceResult raytraceresult = ProjectileHelper.func_221266_a(this, true, false, this.owner, RayTraceContext.BlockMode.COLLIDER);
         if (raytraceresult.getType() != RayTraceResult.Type.MISS && !ForgeEventFactory.onProjectileImpact((Entity)this, raytraceresult)) {
            this.bulletHit(raytraceresult);
         }
      }

      vec3d = this.getMotion();
      this.setPosition(this.func_226277_ct_() + vec3d.x, this.func_226278_cu_() + vec3d.y, this.func_226281_cx_() + vec3d.z);
      ProjectileHelper.rotateTowardsMovement(this, 0.5F);
      if (this.world.isRemote) {
         this.world.addParticle(ParticleTypes.END_ROD, this.func_226277_ct_() - vec3d.x, this.func_226278_cu_() - vec3d.y + 0.15D, this.func_226281_cx_() - vec3d.z, 0.0D, 0.0D, 0.0D);
      } else if (this.target != null && !this.target.removed) {
         if (this.steps > 0) {
            --this.steps;
            if (this.steps == 0) {
               this.selectNextMoveDirection(this.direction == null ? null : this.direction.getAxis());
            }
         }

         if (this.direction != null) {
            BlockPos blockpos1 = new BlockPos(this);
            Direction.Axis direction$axis = this.direction.getAxis();
            if (this.world.func_217400_a(blockpos1.offset(this.direction), this)) {
               this.selectNextMoveDirection(direction$axis);
            } else {
               BlockPos blockpos = new BlockPos(this.target);
               if (direction$axis == Direction.Axis.X && blockpos1.getX() == blockpos.getX() || direction$axis == Direction.Axis.Z && blockpos1.getZ() == blockpos.getZ() || direction$axis == Direction.Axis.Y && blockpos1.getY() == blockpos.getY()) {
                  this.selectNextMoveDirection(direction$axis);
               }
            }
         }
      }

   }

   public boolean isBurning() {
      return false;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isInRangeToRenderDist(double p_70112_1_) {
      return p_70112_1_ < 16384.0D;
   }

   public float getBrightness() {
      return 1.0F;
   }

   protected void bulletHit(RayTraceResult p_184567_1_) {
      if (p_184567_1_.getType() == RayTraceResult.Type.ENTITY) {
         Entity entity = ((EntityRayTraceResult)p_184567_1_).getEntity();
         boolean flag = entity.attackEntityFrom(DamageSource.causeIndirectDamage(this, this.owner).setProjectile(), 4.0F);
         if (flag) {
            this.applyEnchantments(this.owner, entity);
            if (entity instanceof LivingEntity) {
               ((LivingEntity)entity).addPotionEffect(new EffectInstance(Effects.LEVITATION, 200));
            }
         }
      } else {
         ((ServerWorld)this.world).spawnParticle(ParticleTypes.EXPLOSION, this.func_226277_ct_(), this.func_226278_cu_(), this.func_226281_cx_(), 2, 0.2D, 0.2D, 0.2D, 0.0D);
         this.playSound(SoundEvents.ENTITY_SHULKER_BULLET_HIT, 1.0F, 1.0F);
      }

      this.remove();
   }

   public boolean canBeCollidedWith() {
      return true;
   }

   public boolean attackEntityFrom(DamageSource p_70097_1_, float p_70097_2_) {
      if (!this.world.isRemote) {
         this.playSound(SoundEvents.ENTITY_SHULKER_BULLET_HURT, 1.0F, 1.0F);
         ((ServerWorld)this.world).spawnParticle(ParticleTypes.CRIT, this.func_226277_ct_(), this.func_226278_cu_(), this.func_226281_cx_(), 15, 0.2D, 0.2D, 0.2D, 0.0D);
         this.remove();
      }

      return true;
   }

   public IPacket<?> createSpawnPacket() {
      return new SSpawnObjectPacket(this);
   }
}
