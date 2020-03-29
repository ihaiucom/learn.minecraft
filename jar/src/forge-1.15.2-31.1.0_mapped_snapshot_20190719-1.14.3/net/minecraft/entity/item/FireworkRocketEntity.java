package net.minecraft.entity.item;

import java.util.Iterator;
import java.util.List;
import java.util.OptionalInt;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.IRendersAsItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(
   value = Dist.CLIENT,
   _interface = IRendersAsItem.class
)
public class FireworkRocketEntity extends Entity implements IRendersAsItem, IProjectile {
   private static final DataParameter<ItemStack> FIREWORK_ITEM;
   private static final DataParameter<OptionalInt> BOOSTED_ENTITY_ID;
   private static final DataParameter<Boolean> field_213895_d;
   private int fireworkAge;
   private int lifetime;
   private LivingEntity boostedEntity;

   public FireworkRocketEntity(EntityType<? extends FireworkRocketEntity> p_i50164_1_, World p_i50164_2_) {
      super(p_i50164_1_, p_i50164_2_);
   }

   protected void registerData() {
      this.dataManager.register(FIREWORK_ITEM, ItemStack.EMPTY);
      this.dataManager.register(BOOSTED_ENTITY_ID, OptionalInt.empty());
      this.dataManager.register(field_213895_d, false);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isInRangeToRenderDist(double p_70112_1_) {
      return p_70112_1_ < 4096.0D && !this.isAttachedToEntity();
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isInRangeToRender3d(double p_145770_1_, double p_145770_3_, double p_145770_5_) {
      return super.isInRangeToRender3d(p_145770_1_, p_145770_3_, p_145770_5_) && !this.isAttachedToEntity();
   }

   public FireworkRocketEntity(World p_i1763_1_, double p_i1763_2_, double p_i1763_4_, double p_i1763_6_, ItemStack p_i1763_8_) {
      super(EntityType.FIREWORK_ROCKET, p_i1763_1_);
      this.fireworkAge = 0;
      this.setPosition(p_i1763_2_, p_i1763_4_, p_i1763_6_);
      int lvt_9_1_ = 1;
      if (!p_i1763_8_.isEmpty() && p_i1763_8_.hasTag()) {
         this.dataManager.set(FIREWORK_ITEM, p_i1763_8_.copy());
         lvt_9_1_ += p_i1763_8_.getOrCreateChildTag("Fireworks").getByte("Flight");
      }

      this.setMotion(this.rand.nextGaussian() * 0.001D, 0.05D, this.rand.nextGaussian() * 0.001D);
      this.lifetime = 10 * lvt_9_1_ + this.rand.nextInt(6) + this.rand.nextInt(7);
   }

   public FireworkRocketEntity(World p_i47367_1_, ItemStack p_i47367_2_, LivingEntity p_i47367_3_) {
      this(p_i47367_1_, p_i47367_3_.func_226277_ct_(), p_i47367_3_.func_226278_cu_(), p_i47367_3_.func_226281_cx_(), p_i47367_2_);
      this.dataManager.set(BOOSTED_ENTITY_ID, OptionalInt.of(p_i47367_3_.getEntityId()));
      this.boostedEntity = p_i47367_3_;
   }

   public FireworkRocketEntity(World p_i50165_1_, ItemStack p_i50165_2_, double p_i50165_3_, double p_i50165_5_, double p_i50165_7_, boolean p_i50165_9_) {
      this(p_i50165_1_, p_i50165_3_, p_i50165_5_, p_i50165_7_, p_i50165_2_);
      this.dataManager.set(field_213895_d, p_i50165_9_);
   }

   @OnlyIn(Dist.CLIENT)
   public void setVelocity(double p_70016_1_, double p_70016_3_, double p_70016_5_) {
      this.setMotion(p_70016_1_, p_70016_3_, p_70016_5_);
      if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F) {
         float lvt_7_1_ = MathHelper.sqrt(p_70016_1_ * p_70016_1_ + p_70016_5_ * p_70016_5_);
         this.rotationYaw = (float)(MathHelper.atan2(p_70016_1_, p_70016_5_) * 57.2957763671875D);
         this.rotationPitch = (float)(MathHelper.atan2(p_70016_3_, (double)lvt_7_1_) * 57.2957763671875D);
         this.prevRotationYaw = this.rotationYaw;
         this.prevRotationPitch = this.rotationPitch;
      }

   }

   public void tick() {
      super.tick();
      Vec3d lvt_1_1_;
      if (this.isAttachedToEntity()) {
         if (this.boostedEntity == null) {
            ((OptionalInt)this.dataManager.get(BOOSTED_ENTITY_ID)).ifPresent((p_213891_1_) -> {
               Entity lvt_2_1_ = this.world.getEntityByID(p_213891_1_);
               if (lvt_2_1_ instanceof LivingEntity) {
                  this.boostedEntity = (LivingEntity)lvt_2_1_;
               }

            });
         }

         if (this.boostedEntity != null) {
            if (this.boostedEntity.isElytraFlying()) {
               lvt_1_1_ = this.boostedEntity.getLookVec();
               double lvt_2_1_ = 1.5D;
               double lvt_4_1_ = 0.1D;
               Vec3d lvt_6_1_ = this.boostedEntity.getMotion();
               this.boostedEntity.setMotion(lvt_6_1_.add(lvt_1_1_.x * 0.1D + (lvt_1_1_.x * 1.5D - lvt_6_1_.x) * 0.5D, lvt_1_1_.y * 0.1D + (lvt_1_1_.y * 1.5D - lvt_6_1_.y) * 0.5D, lvt_1_1_.z * 0.1D + (lvt_1_1_.z * 1.5D - lvt_6_1_.z) * 0.5D));
            }

            this.setPosition(this.boostedEntity.func_226277_ct_(), this.boostedEntity.func_226278_cu_(), this.boostedEntity.func_226281_cx_());
            this.setMotion(this.boostedEntity.getMotion());
         }
      } else {
         if (!this.func_213889_i()) {
            this.setMotion(this.getMotion().mul(1.15D, 1.0D, 1.15D).add(0.0D, 0.04D, 0.0D));
         }

         this.move(MoverType.SELF, this.getMotion());
      }

      lvt_1_1_ = this.getMotion();
      RayTraceResult lvt_2_2_ = ProjectileHelper.func_221267_a(this, this.getBoundingBox().expand(lvt_1_1_).grow(1.0D), (p_213890_0_) -> {
         return !p_213890_0_.isSpectator() && p_213890_0_.isAlive() && p_213890_0_.canBeCollidedWith();
      }, RayTraceContext.BlockMode.COLLIDER, true);
      if (!this.noClip) {
         this.func_213892_a(lvt_2_2_);
         this.isAirBorne = true;
      }

      float lvt_3_1_ = MathHelper.sqrt(func_213296_b(lvt_1_1_));
      this.rotationYaw = (float)(MathHelper.atan2(lvt_1_1_.x, lvt_1_1_.z) * 57.2957763671875D);

      for(this.rotationPitch = (float)(MathHelper.atan2(lvt_1_1_.y, (double)lvt_3_1_) * 57.2957763671875D); this.rotationPitch - this.prevRotationPitch < -180.0F; this.prevRotationPitch -= 360.0F) {
      }

      while(this.rotationPitch - this.prevRotationPitch >= 180.0F) {
         this.prevRotationPitch += 360.0F;
      }

      while(this.rotationYaw - this.prevRotationYaw < -180.0F) {
         this.prevRotationYaw -= 360.0F;
      }

      while(this.rotationYaw - this.prevRotationYaw >= 180.0F) {
         this.prevRotationYaw += 360.0F;
      }

      this.rotationPitch = MathHelper.lerp(0.2F, this.prevRotationPitch, this.rotationPitch);
      this.rotationYaw = MathHelper.lerp(0.2F, this.prevRotationYaw, this.rotationYaw);
      if (this.fireworkAge == 0 && !this.isSilent()) {
         this.world.playSound((PlayerEntity)null, this.func_226277_ct_(), this.func_226278_cu_(), this.func_226281_cx_(), SoundEvents.ENTITY_FIREWORK_ROCKET_LAUNCH, SoundCategory.AMBIENT, 3.0F, 1.0F);
      }

      ++this.fireworkAge;
      if (this.world.isRemote && this.fireworkAge % 2 < 2) {
         this.world.addParticle(ParticleTypes.FIREWORK, this.func_226277_ct_(), this.func_226278_cu_() - 0.3D, this.func_226281_cx_(), this.rand.nextGaussian() * 0.05D, -this.getMotion().y * 0.5D, this.rand.nextGaussian() * 0.05D);
      }

      if (!this.world.isRemote && this.fireworkAge > this.lifetime) {
         this.func_213893_k();
      }

   }

   private void func_213893_k() {
      this.world.setEntityState(this, (byte)17);
      this.dealExplosionDamage();
      this.remove();
   }

   protected void func_213892_a(RayTraceResult p_213892_1_) {
      if (p_213892_1_.getType() == RayTraceResult.Type.ENTITY && !this.world.isRemote) {
         this.func_213893_k();
      } else if (this.collided) {
         BlockPos lvt_2_2_;
         if (p_213892_1_.getType() == RayTraceResult.Type.BLOCK) {
            lvt_2_2_ = new BlockPos(((BlockRayTraceResult)p_213892_1_).getPos());
         } else {
            lvt_2_2_ = new BlockPos(this);
         }

         this.world.getBlockState(lvt_2_2_).onEntityCollision(this.world, lvt_2_2_, this);
         if (this.func_213894_l()) {
            this.func_213893_k();
         }
      }

   }

   private boolean func_213894_l() {
      ItemStack lvt_1_1_ = (ItemStack)this.dataManager.get(FIREWORK_ITEM);
      CompoundNBT lvt_2_1_ = lvt_1_1_.isEmpty() ? null : lvt_1_1_.getChildTag("Fireworks");
      ListNBT lvt_3_1_ = lvt_2_1_ != null ? lvt_2_1_.getList("Explosions", 10) : null;
      return lvt_3_1_ != null && !lvt_3_1_.isEmpty();
   }

   private void dealExplosionDamage() {
      float lvt_1_1_ = 0.0F;
      ItemStack lvt_2_1_ = (ItemStack)this.dataManager.get(FIREWORK_ITEM);
      CompoundNBT lvt_3_1_ = lvt_2_1_.isEmpty() ? null : lvt_2_1_.getChildTag("Fireworks");
      ListNBT lvt_4_1_ = lvt_3_1_ != null ? lvt_3_1_.getList("Explosions", 10) : null;
      if (lvt_4_1_ != null && !lvt_4_1_.isEmpty()) {
         lvt_1_1_ = 5.0F + (float)(lvt_4_1_.size() * 2);
      }

      if (lvt_1_1_ > 0.0F) {
         if (this.boostedEntity != null) {
            this.boostedEntity.attackEntityFrom(DamageSource.FIREWORKS, 5.0F + (float)(lvt_4_1_.size() * 2));
         }

         double lvt_5_1_ = 5.0D;
         Vec3d lvt_7_1_ = this.getPositionVec();
         List<LivingEntity> lvt_8_1_ = this.world.getEntitiesWithinAABB(LivingEntity.class, this.getBoundingBox().grow(5.0D));
         Iterator var9 = lvt_8_1_.iterator();

         while(true) {
            LivingEntity lvt_10_1_;
            do {
               do {
                  if (!var9.hasNext()) {
                     return;
                  }

                  lvt_10_1_ = (LivingEntity)var9.next();
               } while(lvt_10_1_ == this.boostedEntity);
            } while(this.getDistanceSq(lvt_10_1_) > 25.0D);

            boolean lvt_11_1_ = false;

            for(int lvt_12_1_ = 0; lvt_12_1_ < 2; ++lvt_12_1_) {
               Vec3d lvt_13_1_ = new Vec3d(lvt_10_1_.func_226277_ct_(), lvt_10_1_.func_226283_e_(0.5D * (double)lvt_12_1_), lvt_10_1_.func_226281_cx_());
               RayTraceResult lvt_14_1_ = this.world.rayTraceBlocks(new RayTraceContext(lvt_7_1_, lvt_13_1_, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, this));
               if (lvt_14_1_.getType() == RayTraceResult.Type.MISS) {
                  lvt_11_1_ = true;
                  break;
               }
            }

            if (lvt_11_1_) {
               float lvt_12_2_ = lvt_1_1_ * (float)Math.sqrt((5.0D - (double)this.getDistance(lvt_10_1_)) / 5.0D);
               lvt_10_1_.attackEntityFrom(DamageSource.FIREWORKS, lvt_12_2_);
            }
         }
      }
   }

   private boolean isAttachedToEntity() {
      return ((OptionalInt)this.dataManager.get(BOOSTED_ENTITY_ID)).isPresent();
   }

   public boolean func_213889_i() {
      return (Boolean)this.dataManager.get(field_213895_d);
   }

   @OnlyIn(Dist.CLIENT)
   public void handleStatusUpdate(byte p_70103_1_) {
      if (p_70103_1_ == 17 && this.world.isRemote) {
         if (!this.func_213894_l()) {
            for(int lvt_2_1_ = 0; lvt_2_1_ < this.rand.nextInt(3) + 2; ++lvt_2_1_) {
               this.world.addParticle(ParticleTypes.POOF, this.func_226277_ct_(), this.func_226278_cu_(), this.func_226281_cx_(), this.rand.nextGaussian() * 0.05D, 0.005D, this.rand.nextGaussian() * 0.05D);
            }
         } else {
            ItemStack lvt_2_2_ = (ItemStack)this.dataManager.get(FIREWORK_ITEM);
            CompoundNBT lvt_3_1_ = lvt_2_2_.isEmpty() ? null : lvt_2_2_.getChildTag("Fireworks");
            Vec3d lvt_4_1_ = this.getMotion();
            this.world.makeFireworks(this.func_226277_ct_(), this.func_226278_cu_(), this.func_226281_cx_(), lvt_4_1_.x, lvt_4_1_.y, lvt_4_1_.z, lvt_3_1_);
         }
      }

      super.handleStatusUpdate(p_70103_1_);
   }

   public void writeAdditional(CompoundNBT p_213281_1_) {
      p_213281_1_.putInt("Life", this.fireworkAge);
      p_213281_1_.putInt("LifeTime", this.lifetime);
      ItemStack lvt_2_1_ = (ItemStack)this.dataManager.get(FIREWORK_ITEM);
      if (!lvt_2_1_.isEmpty()) {
         p_213281_1_.put("FireworksItem", lvt_2_1_.write(new CompoundNBT()));
      }

      p_213281_1_.putBoolean("ShotAtAngle", (Boolean)this.dataManager.get(field_213895_d));
   }

   public void readAdditional(CompoundNBT p_70037_1_) {
      this.fireworkAge = p_70037_1_.getInt("Life");
      this.lifetime = p_70037_1_.getInt("LifeTime");
      ItemStack lvt_2_1_ = ItemStack.read(p_70037_1_.getCompound("FireworksItem"));
      if (!lvt_2_1_.isEmpty()) {
         this.dataManager.set(FIREWORK_ITEM, lvt_2_1_);
      }

      if (p_70037_1_.contains("ShotAtAngle")) {
         this.dataManager.set(field_213895_d, p_70037_1_.getBoolean("ShotAtAngle"));
      }

   }

   @OnlyIn(Dist.CLIENT)
   public ItemStack getItem() {
      ItemStack lvt_1_1_ = (ItemStack)this.dataManager.get(FIREWORK_ITEM);
      return lvt_1_1_.isEmpty() ? new ItemStack(Items.FIREWORK_ROCKET) : lvt_1_1_;
   }

   public boolean canBeAttackedWithItem() {
      return false;
   }

   public IPacket<?> createSpawnPacket() {
      return new SSpawnObjectPacket(this);
   }

   public void shoot(double p_70186_1_, double p_70186_3_, double p_70186_5_, float p_70186_7_, float p_70186_8_) {
      float lvt_9_1_ = MathHelper.sqrt(p_70186_1_ * p_70186_1_ + p_70186_3_ * p_70186_3_ + p_70186_5_ * p_70186_5_);
      p_70186_1_ /= (double)lvt_9_1_;
      p_70186_3_ /= (double)lvt_9_1_;
      p_70186_5_ /= (double)lvt_9_1_;
      p_70186_1_ += this.rand.nextGaussian() * 0.007499999832361937D * (double)p_70186_8_;
      p_70186_3_ += this.rand.nextGaussian() * 0.007499999832361937D * (double)p_70186_8_;
      p_70186_5_ += this.rand.nextGaussian() * 0.007499999832361937D * (double)p_70186_8_;
      p_70186_1_ *= (double)p_70186_7_;
      p_70186_3_ *= (double)p_70186_7_;
      p_70186_5_ *= (double)p_70186_7_;
      this.setMotion(p_70186_1_, p_70186_3_, p_70186_5_);
   }

   static {
      FIREWORK_ITEM = EntityDataManager.createKey(FireworkRocketEntity.class, DataSerializers.ITEMSTACK);
      BOOSTED_ENTITY_ID = EntityDataManager.createKey(FireworkRocketEntity.class, DataSerializers.OPTIONAL_VARINT);
      field_213895_d = EntityDataManager.createKey(FireworkRocketEntity.class, DataSerializers.BOOLEAN);
   }
}
