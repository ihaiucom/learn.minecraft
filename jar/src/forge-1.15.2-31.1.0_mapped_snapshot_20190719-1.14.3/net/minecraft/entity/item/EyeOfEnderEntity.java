package net.minecraft.entity.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IRendersAsItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(
   value = Dist.CLIENT,
   _interface = IRendersAsItem.class
)
public class EyeOfEnderEntity extends Entity implements IRendersAsItem {
   private static final DataParameter<ItemStack> field_213864_b;
   private double targetX;
   private double targetY;
   private double targetZ;
   private int despawnTimer;
   private boolean shatterOrDrop;

   public EyeOfEnderEntity(EntityType<? extends EyeOfEnderEntity> p_i50169_1_, World p_i50169_2_) {
      super(p_i50169_1_, p_i50169_2_);
   }

   public EyeOfEnderEntity(World p_i1758_1_, double p_i1758_2_, double p_i1758_4_, double p_i1758_6_) {
      this(EntityType.EYE_OF_ENDER, p_i1758_1_);
      this.despawnTimer = 0;
      this.setPosition(p_i1758_2_, p_i1758_4_, p_i1758_6_);
   }

   public void func_213863_b(ItemStack p_213863_1_) {
      if (p_213863_1_.getItem() != Items.ENDER_EYE || p_213863_1_.hasTag()) {
         this.getDataManager().set(field_213864_b, Util.make(p_213863_1_.copy(), (p_213862_0_) -> {
            p_213862_0_.setCount(1);
         }));
      }

   }

   private ItemStack func_213861_i() {
      return (ItemStack)this.getDataManager().get(field_213864_b);
   }

   public ItemStack getItem() {
      ItemStack lvt_1_1_ = this.func_213861_i();
      return lvt_1_1_.isEmpty() ? new ItemStack(Items.ENDER_EYE) : lvt_1_1_;
   }

   protected void registerData() {
      this.getDataManager().register(field_213864_b, ItemStack.EMPTY);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isInRangeToRenderDist(double p_70112_1_) {
      double lvt_3_1_ = this.getBoundingBox().getAverageEdgeLength() * 4.0D;
      if (Double.isNaN(lvt_3_1_)) {
         lvt_3_1_ = 4.0D;
      }

      lvt_3_1_ *= 64.0D;
      return p_70112_1_ < lvt_3_1_ * lvt_3_1_;
   }

   public void moveTowards(BlockPos p_180465_1_) {
      double lvt_2_1_ = (double)p_180465_1_.getX();
      int lvt_4_1_ = p_180465_1_.getY();
      double lvt_5_1_ = (double)p_180465_1_.getZ();
      double lvt_7_1_ = lvt_2_1_ - this.func_226277_ct_();
      double lvt_9_1_ = lvt_5_1_ - this.func_226281_cx_();
      float lvt_11_1_ = MathHelper.sqrt(lvt_7_1_ * lvt_7_1_ + lvt_9_1_ * lvt_9_1_);
      if (lvt_11_1_ > 12.0F) {
         this.targetX = this.func_226277_ct_() + lvt_7_1_ / (double)lvt_11_1_ * 12.0D;
         this.targetZ = this.func_226281_cx_() + lvt_9_1_ / (double)lvt_11_1_ * 12.0D;
         this.targetY = this.func_226278_cu_() + 8.0D;
      } else {
         this.targetX = lvt_2_1_;
         this.targetY = (double)lvt_4_1_;
         this.targetZ = lvt_5_1_;
      }

      this.despawnTimer = 0;
      this.shatterOrDrop = this.rand.nextInt(5) > 0;
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
      Vec3d lvt_1_1_ = this.getMotion();
      double lvt_2_1_ = this.func_226277_ct_() + lvt_1_1_.x;
      double lvt_4_1_ = this.func_226278_cu_() + lvt_1_1_.y;
      double lvt_6_1_ = this.func_226281_cx_() + lvt_1_1_.z;
      float lvt_8_1_ = MathHelper.sqrt(func_213296_b(lvt_1_1_));
      this.rotationYaw = (float)(MathHelper.atan2(lvt_1_1_.x, lvt_1_1_.z) * 57.2957763671875D);

      for(this.rotationPitch = (float)(MathHelper.atan2(lvt_1_1_.y, (double)lvt_8_1_) * 57.2957763671875D); this.rotationPitch - this.prevRotationPitch < -180.0F; this.prevRotationPitch -= 360.0F) {
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
      if (!this.world.isRemote) {
         double lvt_9_1_ = this.targetX - lvt_2_1_;
         double lvt_11_1_ = this.targetZ - lvt_6_1_;
         float lvt_13_1_ = (float)Math.sqrt(lvt_9_1_ * lvt_9_1_ + lvt_11_1_ * lvt_11_1_);
         float lvt_14_1_ = (float)MathHelper.atan2(lvt_11_1_, lvt_9_1_);
         double lvt_15_1_ = MathHelper.lerp(0.0025D, (double)lvt_8_1_, (double)lvt_13_1_);
         double lvt_17_1_ = lvt_1_1_.y;
         if (lvt_13_1_ < 1.0F) {
            lvt_15_1_ *= 0.8D;
            lvt_17_1_ *= 0.8D;
         }

         int lvt_19_1_ = this.func_226278_cu_() < this.targetY ? 1 : -1;
         lvt_1_1_ = new Vec3d(Math.cos((double)lvt_14_1_) * lvt_15_1_, lvt_17_1_ + ((double)lvt_19_1_ - lvt_17_1_) * 0.014999999664723873D, Math.sin((double)lvt_14_1_) * lvt_15_1_);
         this.setMotion(lvt_1_1_);
      }

      float lvt_9_2_ = 0.25F;
      if (this.isInWater()) {
         for(int lvt_10_1_ = 0; lvt_10_1_ < 4; ++lvt_10_1_) {
            this.world.addParticle(ParticleTypes.BUBBLE, lvt_2_1_ - lvt_1_1_.x * 0.25D, lvt_4_1_ - lvt_1_1_.y * 0.25D, lvt_6_1_ - lvt_1_1_.z * 0.25D, lvt_1_1_.x, lvt_1_1_.y, lvt_1_1_.z);
         }
      } else {
         this.world.addParticle(ParticleTypes.PORTAL, lvt_2_1_ - lvt_1_1_.x * 0.25D + this.rand.nextDouble() * 0.6D - 0.3D, lvt_4_1_ - lvt_1_1_.y * 0.25D - 0.5D, lvt_6_1_ - lvt_1_1_.z * 0.25D + this.rand.nextDouble() * 0.6D - 0.3D, lvt_1_1_.x, lvt_1_1_.y, lvt_1_1_.z);
      }

      if (!this.world.isRemote) {
         this.setPosition(lvt_2_1_, lvt_4_1_, lvt_6_1_);
         ++this.despawnTimer;
         if (this.despawnTimer > 80 && !this.world.isRemote) {
            this.playSound(SoundEvents.ENTITY_ENDER_EYE_DEATH, 1.0F, 1.0F);
            this.remove();
            if (this.shatterOrDrop) {
               this.world.addEntity(new ItemEntity(this.world, this.func_226277_ct_(), this.func_226278_cu_(), this.func_226281_cx_(), this.getItem()));
            } else {
               this.world.playEvent(2003, new BlockPos(this), 0);
            }
         }
      } else {
         this.func_226288_n_(lvt_2_1_, lvt_4_1_, lvt_6_1_);
      }

   }

   public void writeAdditional(CompoundNBT p_213281_1_) {
      ItemStack lvt_2_1_ = this.func_213861_i();
      if (!lvt_2_1_.isEmpty()) {
         p_213281_1_.put("Item", lvt_2_1_.write(new CompoundNBT()));
      }

   }

   public void readAdditional(CompoundNBT p_70037_1_) {
      ItemStack lvt_2_1_ = ItemStack.read(p_70037_1_.getCompound("Item"));
      this.func_213863_b(lvt_2_1_);
   }

   public float getBrightness() {
      return 1.0F;
   }

   public boolean canBeAttackedWithItem() {
      return false;
   }

   public IPacket<?> createSpawnPacket() {
      return new SSpawnObjectPacket(this);
   }

   static {
      field_213864_b = EntityDataManager.createKey(EyeOfEnderEntity.class, DataSerializers.ITEMSTACK);
   }
}
