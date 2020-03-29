package net.minecraft.entity.item;

import java.util.Map.Entry;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SSpawnExperienceOrbPacket;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerXpEvent;

public class ExperienceOrbEntity extends Entity {
   public int xpColor;
   public int xpOrbAge;
   public int delayBeforeCanPickup;
   private int xpOrbHealth;
   public int xpValue;
   private PlayerEntity closestPlayer;
   private int xpTargetColor;

   public ExperienceOrbEntity(World p_i1585_1_, double p_i1585_2_, double p_i1585_4_, double p_i1585_6_, int p_i1585_8_) {
      this(EntityType.EXPERIENCE_ORB, p_i1585_1_);
      this.setPosition(p_i1585_2_, p_i1585_4_, p_i1585_6_);
      this.rotationYaw = (float)(this.rand.nextDouble() * 360.0D);
      this.setMotion((this.rand.nextDouble() * 0.20000000298023224D - 0.10000000149011612D) * 2.0D, this.rand.nextDouble() * 0.2D * 2.0D, (this.rand.nextDouble() * 0.20000000298023224D - 0.10000000149011612D) * 2.0D);
      this.xpValue = p_i1585_8_;
   }

   public ExperienceOrbEntity(EntityType<? extends ExperienceOrbEntity> p_i50382_1_, World p_i50382_2_) {
      super(p_i50382_1_, p_i50382_2_);
      this.xpOrbHealth = 5;
   }

   protected boolean func_225502_at_() {
      return false;
   }

   protected void registerData() {
   }

   public void tick() {
      super.tick();
      if (this.delayBeforeCanPickup > 0) {
         --this.delayBeforeCanPickup;
      }

      this.prevPosX = this.func_226277_ct_();
      this.prevPosY = this.func_226278_cu_();
      this.prevPosZ = this.func_226281_cx_();
      if (this.areEyesInFluid(FluidTags.WATER)) {
         this.applyFloatMotion();
      } else if (!this.hasNoGravity()) {
         this.setMotion(this.getMotion().add(0.0D, -0.03D, 0.0D));
      }

      if (this.world.getFluidState(new BlockPos(this)).isTagged(FluidTags.LAVA)) {
         this.setMotion((double)((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F), 0.20000000298023224D, (double)((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F));
         this.playSound(SoundEvents.ENTITY_GENERIC_BURN, 0.4F, 2.0F + this.rand.nextFloat() * 0.4F);
      }

      if (!this.world.func_226664_a_(this.getBoundingBox())) {
         this.pushOutOfBlocks(this.func_226277_ct_(), (this.getBoundingBox().minY + this.getBoundingBox().maxY) / 2.0D, this.func_226281_cx_());
      }

      double d0 = 8.0D;
      if (this.xpTargetColor < this.xpColor - 20 + this.getEntityId() % 100) {
         if (this.closestPlayer == null || this.closestPlayer.getDistanceSq(this) > 64.0D) {
            this.closestPlayer = this.world.getClosestPlayer(this, 8.0D);
         }

         this.xpTargetColor = this.xpColor;
      }

      if (this.closestPlayer != null && this.closestPlayer.isSpectator()) {
         this.closestPlayer = null;
      }

      if (this.closestPlayer != null) {
         Vec3d vec3d = new Vec3d(this.closestPlayer.func_226277_ct_() - this.func_226277_ct_(), this.closestPlayer.func_226278_cu_() + (double)this.closestPlayer.getEyeHeight() / 2.0D - this.func_226278_cu_(), this.closestPlayer.func_226281_cx_() - this.func_226281_cx_());
         double d1 = vec3d.lengthSquared();
         if (d1 < 64.0D) {
            double d2 = 1.0D - Math.sqrt(d1) / 8.0D;
            this.setMotion(this.getMotion().add(vec3d.normalize().scale(d2 * d2 * 0.1D)));
         }
      }

      this.move(MoverType.SELF, this.getMotion());
      float f = 0.98F;
      if (this.onGround) {
         BlockPos pos = new BlockPos(this.func_226277_ct_(), this.func_226278_cu_() - 1.0D, this.func_226281_cx_());
         f = this.world.getBlockState(pos).getSlipperiness(this.world, pos, this) * 0.98F;
      }

      this.setMotion(this.getMotion().mul((double)f, 0.98D, (double)f));
      if (this.onGround) {
         this.setMotion(this.getMotion().mul(1.0D, -0.9D, 1.0D));
      }

      ++this.xpColor;
      ++this.xpOrbAge;
      if (this.xpOrbAge >= 6000) {
         this.remove();
      }

   }

   private void applyFloatMotion() {
      Vec3d vec3d = this.getMotion();
      this.setMotion(vec3d.x * 0.9900000095367432D, Math.min(vec3d.y + 5.000000237487257E-4D, 0.05999999865889549D), vec3d.z * 0.9900000095367432D);
   }

   protected void doWaterSplashEffect() {
   }

   protected void dealFireDamage(int p_70081_1_) {
      this.attackEntityFrom(DamageSource.IN_FIRE, (float)p_70081_1_);
   }

   public boolean attackEntityFrom(DamageSource p_70097_1_, float p_70097_2_) {
      if (!this.world.isRemote && !this.removed) {
         if (this.isInvulnerableTo(p_70097_1_)) {
            return false;
         } else {
            this.markVelocityChanged();
            this.xpOrbHealth = (int)((float)this.xpOrbHealth - p_70097_2_);
            if (this.xpOrbHealth <= 0) {
               this.remove();
            }

            return false;
         }
      } else {
         return false;
      }
   }

   public void writeAdditional(CompoundNBT p_213281_1_) {
      p_213281_1_.putShort("Health", (short)this.xpOrbHealth);
      p_213281_1_.putShort("Age", (short)this.xpOrbAge);
      p_213281_1_.putShort("Value", (short)this.xpValue);
   }

   public void readAdditional(CompoundNBT p_70037_1_) {
      this.xpOrbHealth = p_70037_1_.getShort("Health");
      this.xpOrbAge = p_70037_1_.getShort("Age");
      this.xpValue = p_70037_1_.getShort("Value");
   }

   public void onCollideWithPlayer(PlayerEntity p_70100_1_) {
      if (!this.world.isRemote && this.delayBeforeCanPickup == 0 && p_70100_1_.xpCooldown == 0) {
         if (MinecraftForge.EVENT_BUS.post(new PlayerXpEvent.PickupXp(p_70100_1_, this))) {
            return;
         }

         p_70100_1_.xpCooldown = 2;
         p_70100_1_.onItemPickup(this, 1);
         Entry<EquipmentSlotType, ItemStack> entry = EnchantmentHelper.func_222189_b(Enchantments.MENDING, p_70100_1_);
         if (entry != null) {
            ItemStack itemstack = (ItemStack)entry.getValue();
            if (!itemstack.isEmpty() && itemstack.isDamaged()) {
               int i = Math.min((int)((float)this.xpValue * itemstack.getXpRepairRatio()), itemstack.getDamage());
               this.xpValue -= this.durabilityToXp(i);
               itemstack.setDamage(itemstack.getDamage() - i);
            }
         }

         if (this.xpValue > 0) {
            p_70100_1_.giveExperiencePoints(this.xpValue);
         }

         this.remove();
      }

   }

   private int durabilityToXp(int p_184515_1_) {
      return p_184515_1_ / 2;
   }

   private int xpToDurability(int p_184514_1_) {
      return p_184514_1_ * 2;
   }

   public int getXpValue() {
      return this.xpValue;
   }

   @OnlyIn(Dist.CLIENT)
   public int getTextureByXP() {
      if (this.xpValue >= 2477) {
         return 10;
      } else if (this.xpValue >= 1237) {
         return 9;
      } else if (this.xpValue >= 617) {
         return 8;
      } else if (this.xpValue >= 307) {
         return 7;
      } else if (this.xpValue >= 149) {
         return 6;
      } else if (this.xpValue >= 73) {
         return 5;
      } else if (this.xpValue >= 37) {
         return 4;
      } else if (this.xpValue >= 17) {
         return 3;
      } else if (this.xpValue >= 7) {
         return 2;
      } else {
         return this.xpValue >= 3 ? 1 : 0;
      }
   }

   public static int getXPSplit(int p_70527_0_) {
      if (p_70527_0_ >= 2477) {
         return 2477;
      } else if (p_70527_0_ >= 1237) {
         return 1237;
      } else if (p_70527_0_ >= 617) {
         return 617;
      } else if (p_70527_0_ >= 307) {
         return 307;
      } else if (p_70527_0_ >= 149) {
         return 149;
      } else if (p_70527_0_ >= 73) {
         return 73;
      } else if (p_70527_0_ >= 37) {
         return 37;
      } else if (p_70527_0_ >= 17) {
         return 17;
      } else if (p_70527_0_ >= 7) {
         return 7;
      } else {
         return p_70527_0_ >= 3 ? 3 : 1;
      }
   }

   public boolean canBeAttackedWithItem() {
      return false;
   }

   public IPacket<?> createSpawnPacket() {
      return new SSpawnExperienceOrbPacket(this);
   }
}
