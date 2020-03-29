package net.minecraft.entity.boss.dragon.phase;

import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class ScanningSittingPhase extends SittingPhase {
   private static final EntityPredicate field_221115_b = (new EntityPredicate()).setDistance(150.0D);
   private final EntityPredicate field_221116_c;
   private int scanningTime;

   public ScanningSittingPhase(EnderDragonEntity p_i46785_1_) {
      super(p_i46785_1_);
      this.field_221116_c = (new EntityPredicate()).setDistance(20.0D).setCustomPredicate((p_221114_1_) -> {
         return Math.abs(p_221114_1_.func_226278_cu_() - p_i46785_1_.func_226278_cu_()) <= 10.0D;
      });
   }

   public void serverTick() {
      ++this.scanningTime;
      LivingEntity lvt_1_1_ = this.dragon.world.getClosestPlayer(this.field_221116_c, this.dragon, this.dragon.func_226277_ct_(), this.dragon.func_226278_cu_(), this.dragon.func_226281_cx_());
      if (lvt_1_1_ != null) {
         if (this.scanningTime > 25) {
            this.dragon.getPhaseManager().setPhase(PhaseType.SITTING_ATTACKING);
         } else {
            Vec3d lvt_2_1_ = (new Vec3d(lvt_1_1_.func_226277_ct_() - this.dragon.func_226277_ct_(), 0.0D, lvt_1_1_.func_226281_cx_() - this.dragon.func_226281_cx_())).normalize();
            Vec3d lvt_3_1_ = (new Vec3d((double)MathHelper.sin(this.dragon.rotationYaw * 0.017453292F), 0.0D, (double)(-MathHelper.cos(this.dragon.rotationYaw * 0.017453292F)))).normalize();
            float lvt_4_1_ = (float)lvt_3_1_.dotProduct(lvt_2_1_);
            float lvt_5_1_ = (float)(Math.acos((double)lvt_4_1_) * 57.2957763671875D) + 0.5F;
            if (lvt_5_1_ < 0.0F || lvt_5_1_ > 10.0F) {
               double lvt_6_1_ = lvt_1_1_.func_226277_ct_() - this.dragon.field_70986_h.func_226277_ct_();
               double lvt_8_1_ = lvt_1_1_.func_226281_cx_() - this.dragon.field_70986_h.func_226281_cx_();
               double lvt_10_1_ = MathHelper.clamp(MathHelper.wrapDegrees(180.0D - MathHelper.atan2(lvt_6_1_, lvt_8_1_) * 57.2957763671875D - (double)this.dragon.rotationYaw), -100.0D, 100.0D);
               EnderDragonEntity var10000 = this.dragon;
               var10000.field_226525_bB_ *= 0.8F;
               float lvt_12_1_ = MathHelper.sqrt(lvt_6_1_ * lvt_6_1_ + lvt_8_1_ * lvt_8_1_) + 1.0F;
               float lvt_13_1_ = lvt_12_1_;
               if (lvt_12_1_ > 40.0F) {
                  lvt_12_1_ = 40.0F;
               }

               var10000 = this.dragon;
               var10000.field_226525_bB_ = (float)((double)var10000.field_226525_bB_ + lvt_10_1_ * (double)(0.7F / lvt_12_1_ / lvt_13_1_));
               var10000 = this.dragon;
               var10000.rotationYaw += this.dragon.field_226525_bB_;
            }
         }
      } else if (this.scanningTime >= 100) {
         lvt_1_1_ = this.dragon.world.getClosestPlayer(field_221115_b, this.dragon, this.dragon.func_226277_ct_(), this.dragon.func_226278_cu_(), this.dragon.func_226281_cx_());
         this.dragon.getPhaseManager().setPhase(PhaseType.TAKEOFF);
         if (lvt_1_1_ != null) {
            this.dragon.getPhaseManager().setPhase(PhaseType.CHARGING_PLAYER);
            ((ChargingPlayerPhase)this.dragon.getPhaseManager().getPhase(PhaseType.CHARGING_PLAYER)).setTarget(new Vec3d(lvt_1_1_.func_226277_ct_(), lvt_1_1_.func_226278_cu_(), lvt_1_1_.func_226281_cx_()));
         }
      }

   }

   public void initPhase() {
      this.scanningTime = 0;
   }

   public PhaseType<ScanningSittingPhase> getType() {
      return PhaseType.SITTING_SCANNING;
   }
}
