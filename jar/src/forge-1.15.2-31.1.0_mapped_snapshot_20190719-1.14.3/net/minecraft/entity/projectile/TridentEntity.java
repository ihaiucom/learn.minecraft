package net.minecraft.entity.projectile;

import javax.annotation.Nullable;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class TridentEntity extends AbstractArrowEntity {
   private static final DataParameter<Byte> LOYALTY_LEVEL;
   private static final DataParameter<Boolean> field_226571_aq_;
   private ItemStack thrownStack;
   private boolean dealtDamage;
   public int returningTicks;

   public TridentEntity(EntityType<? extends TridentEntity> p_i50148_1_, World p_i50148_2_) {
      super(p_i50148_1_, p_i50148_2_);
      this.thrownStack = new ItemStack(Items.TRIDENT);
   }

   public TridentEntity(World p_i48790_1_, LivingEntity p_i48790_2_, ItemStack p_i48790_3_) {
      super(EntityType.TRIDENT, p_i48790_2_, p_i48790_1_);
      this.thrownStack = new ItemStack(Items.TRIDENT);
      this.thrownStack = p_i48790_3_.copy();
      this.dataManager.set(LOYALTY_LEVEL, (byte)EnchantmentHelper.getLoyaltyModifier(p_i48790_3_));
      this.dataManager.set(field_226571_aq_, p_i48790_3_.hasEffect());
   }

   @OnlyIn(Dist.CLIENT)
   public TridentEntity(World p_i48791_1_, double p_i48791_2_, double p_i48791_4_, double p_i48791_6_) {
      super(EntityType.TRIDENT, p_i48791_2_, p_i48791_4_, p_i48791_6_, p_i48791_1_);
      this.thrownStack = new ItemStack(Items.TRIDENT);
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(LOYALTY_LEVEL, (byte)0);
      this.dataManager.register(field_226571_aq_, false);
   }

   public void tick() {
      if (this.timeInGround > 4) {
         this.dealtDamage = true;
      }

      Entity lvt_1_1_ = this.getShooter();
      if ((this.dealtDamage || this.func_203047_q()) && lvt_1_1_ != null) {
         int lvt_2_1_ = (Byte)this.dataManager.get(LOYALTY_LEVEL);
         if (lvt_2_1_ > 0 && !this.shouldReturnToThrower()) {
            if (!this.world.isRemote && this.pickupStatus == AbstractArrowEntity.PickupStatus.ALLOWED) {
               this.entityDropItem(this.getArrowStack(), 0.1F);
            }

            this.remove();
         } else if (lvt_2_1_ > 0) {
            this.func_203045_n(true);
            Vec3d lvt_3_1_ = new Vec3d(lvt_1_1_.func_226277_ct_() - this.func_226277_ct_(), lvt_1_1_.func_226280_cw_() - this.func_226278_cu_(), lvt_1_1_.func_226281_cx_() - this.func_226281_cx_());
            this.func_226288_n_(this.func_226277_ct_(), this.func_226278_cu_() + lvt_3_1_.y * 0.015D * (double)lvt_2_1_, this.func_226281_cx_());
            if (this.world.isRemote) {
               this.lastTickPosY = this.func_226278_cu_();
            }

            double lvt_4_1_ = 0.05D * (double)lvt_2_1_;
            this.setMotion(this.getMotion().scale(0.95D).add(lvt_3_1_.normalize().scale(lvt_4_1_)));
            if (this.returningTicks == 0) {
               this.playSound(SoundEvents.ITEM_TRIDENT_RETURN, 10.0F, 1.0F);
            }

            ++this.returningTicks;
         }
      }

      super.tick();
   }

   private boolean shouldReturnToThrower() {
      Entity lvt_1_1_ = this.getShooter();
      if (lvt_1_1_ != null && lvt_1_1_.isAlive()) {
         return !(lvt_1_1_ instanceof ServerPlayerEntity) || !lvt_1_1_.isSpectator();
      } else {
         return false;
      }
   }

   protected ItemStack getArrowStack() {
      return this.thrownStack.copy();
   }

   @OnlyIn(Dist.CLIENT)
   public boolean func_226572_w_() {
      return (Boolean)this.dataManager.get(field_226571_aq_);
   }

   @Nullable
   protected EntityRayTraceResult func_213866_a(Vec3d p_213866_1_, Vec3d p_213866_2_) {
      return this.dealtDamage ? null : super.func_213866_a(p_213866_1_, p_213866_2_);
   }

   protected void func_213868_a(EntityRayTraceResult p_213868_1_) {
      Entity lvt_2_1_ = p_213868_1_.getEntity();
      float lvt_3_1_ = 8.0F;
      if (lvt_2_1_ instanceof LivingEntity) {
         LivingEntity lvt_4_1_ = (LivingEntity)lvt_2_1_;
         lvt_3_1_ += EnchantmentHelper.getModifierForCreature(this.thrownStack, lvt_4_1_.getCreatureAttribute());
      }

      Entity lvt_4_2_ = this.getShooter();
      DamageSource lvt_5_1_ = DamageSource.causeTridentDamage(this, (Entity)(lvt_4_2_ == null ? this : lvt_4_2_));
      this.dealtDamage = true;
      SoundEvent lvt_6_1_ = SoundEvents.ITEM_TRIDENT_HIT;
      if (lvt_2_1_.attackEntityFrom(lvt_5_1_, lvt_3_1_)) {
         if (lvt_2_1_.getType() == EntityType.ENDERMAN) {
            return;
         }

         if (lvt_2_1_ instanceof LivingEntity) {
            LivingEntity lvt_7_1_ = (LivingEntity)lvt_2_1_;
            if (lvt_4_2_ instanceof LivingEntity) {
               EnchantmentHelper.applyThornEnchantments(lvt_7_1_, lvt_4_2_);
               EnchantmentHelper.applyArthropodEnchantments((LivingEntity)lvt_4_2_, lvt_7_1_);
            }

            this.arrowHit(lvt_7_1_);
         }
      }

      this.setMotion(this.getMotion().mul(-0.01D, -0.1D, -0.01D));
      float lvt_7_2_ = 1.0F;
      if (this.world instanceof ServerWorld && this.world.isThundering() && EnchantmentHelper.hasChanneling(this.thrownStack)) {
         BlockPos lvt_8_1_ = lvt_2_1_.getPosition();
         if (this.world.func_226660_f_(lvt_8_1_)) {
            LightningBoltEntity lvt_9_1_ = new LightningBoltEntity(this.world, (double)lvt_8_1_.getX() + 0.5D, (double)lvt_8_1_.getY(), (double)lvt_8_1_.getZ() + 0.5D, false);
            lvt_9_1_.setCaster(lvt_4_2_ instanceof ServerPlayerEntity ? (ServerPlayerEntity)lvt_4_2_ : null);
            ((ServerWorld)this.world).addLightningBolt(lvt_9_1_);
            lvt_6_1_ = SoundEvents.ITEM_TRIDENT_THUNDER;
            lvt_7_2_ = 5.0F;
         }
      }

      this.playSound(lvt_6_1_, lvt_7_2_, 1.0F);
   }

   protected SoundEvent func_213867_k() {
      return SoundEvents.ITEM_TRIDENT_HIT_GROUND;
   }

   public void onCollideWithPlayer(PlayerEntity p_70100_1_) {
      Entity lvt_2_1_ = this.getShooter();
      if (lvt_2_1_ == null || lvt_2_1_.getUniqueID() == p_70100_1_.getUniqueID()) {
         super.onCollideWithPlayer(p_70100_1_);
      }
   }

   public void readAdditional(CompoundNBT p_70037_1_) {
      super.readAdditional(p_70037_1_);
      if (p_70037_1_.contains("Trident", 10)) {
         this.thrownStack = ItemStack.read(p_70037_1_.getCompound("Trident"));
      }

      this.dealtDamage = p_70037_1_.getBoolean("DealtDamage");
      this.dataManager.set(LOYALTY_LEVEL, (byte)EnchantmentHelper.getLoyaltyModifier(this.thrownStack));
   }

   public void writeAdditional(CompoundNBT p_213281_1_) {
      super.writeAdditional(p_213281_1_);
      p_213281_1_.put("Trident", this.thrownStack.write(new CompoundNBT()));
      p_213281_1_.putBoolean("DealtDamage", this.dealtDamage);
   }

   public void func_225516_i_() {
      int lvt_1_1_ = (Byte)this.dataManager.get(LOYALTY_LEVEL);
      if (this.pickupStatus != AbstractArrowEntity.PickupStatus.ALLOWED || lvt_1_1_ <= 0) {
         super.func_225516_i_();
      }

   }

   protected float getWaterDrag() {
      return 0.99F;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isInRangeToRender3d(double p_145770_1_, double p_145770_3_, double p_145770_5_) {
      return true;
   }

   static {
      LOYALTY_LEVEL = EntityDataManager.createKey(TridentEntity.class, DataSerializers.BYTE);
      field_226571_aq_ = EntityDataManager.createKey(TridentEntity.class, DataSerializers.BOOLEAN);
   }
}
