package net.minecraft.entity.boss.dragon.phase;

import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class FlamingSittingPhase extends SittingPhase {
   private int flameTicks;
   private int flameCount;
   private AreaEffectCloudEntity areaEffectCloud;

   public FlamingSittingPhase(EnderDragonEntity p_i46786_1_) {
      super(p_i46786_1_);
   }

   public void clientTick() {
      ++this.flameTicks;
      if (this.flameTicks % 2 == 0 && this.flameTicks < 10) {
         Vec3d lvt_1_1_ = this.dragon.getHeadLookVec(1.0F).normalize();
         lvt_1_1_.rotateYaw(-0.7853982F);
         double lvt_2_1_ = this.dragon.field_70986_h.func_226277_ct_();
         double lvt_4_1_ = this.dragon.field_70986_h.func_226283_e_(0.5D);
         double lvt_6_1_ = this.dragon.field_70986_h.func_226281_cx_();

         for(int lvt_8_1_ = 0; lvt_8_1_ < 8; ++lvt_8_1_) {
            double lvt_9_1_ = lvt_2_1_ + this.dragon.getRNG().nextGaussian() / 2.0D;
            double lvt_11_1_ = lvt_4_1_ + this.dragon.getRNG().nextGaussian() / 2.0D;
            double lvt_13_1_ = lvt_6_1_ + this.dragon.getRNG().nextGaussian() / 2.0D;

            for(int lvt_15_1_ = 0; lvt_15_1_ < 6; ++lvt_15_1_) {
               this.dragon.world.addParticle(ParticleTypes.DRAGON_BREATH, lvt_9_1_, lvt_11_1_, lvt_13_1_, -lvt_1_1_.x * 0.07999999821186066D * (double)lvt_15_1_, -lvt_1_1_.y * 0.6000000238418579D, -lvt_1_1_.z * 0.07999999821186066D * (double)lvt_15_1_);
            }

            lvt_1_1_.rotateYaw(0.19634955F);
         }
      }

   }

   public void serverTick() {
      ++this.flameTicks;
      if (this.flameTicks >= 200) {
         if (this.flameCount >= 4) {
            this.dragon.getPhaseManager().setPhase(PhaseType.TAKEOFF);
         } else {
            this.dragon.getPhaseManager().setPhase(PhaseType.SITTING_SCANNING);
         }
      } else if (this.flameTicks == 10) {
         Vec3d lvt_1_1_ = (new Vec3d(this.dragon.field_70986_h.func_226277_ct_() - this.dragon.func_226277_ct_(), 0.0D, this.dragon.field_70986_h.func_226281_cx_() - this.dragon.func_226281_cx_())).normalize();
         float lvt_2_1_ = 5.0F;
         double lvt_3_1_ = this.dragon.field_70986_h.func_226277_ct_() + lvt_1_1_.x * 5.0D / 2.0D;
         double lvt_5_1_ = this.dragon.field_70986_h.func_226281_cx_() + lvt_1_1_.z * 5.0D / 2.0D;
         double lvt_7_1_ = this.dragon.field_70986_h.func_226283_e_(0.5D);
         double lvt_9_1_ = lvt_7_1_;
         BlockPos.Mutable lvt_11_1_ = new BlockPos.Mutable(lvt_3_1_, lvt_7_1_, lvt_5_1_);

         while(this.dragon.world.isAirBlock(lvt_11_1_)) {
            --lvt_9_1_;
            if (lvt_9_1_ < 0.0D) {
               lvt_9_1_ = lvt_7_1_;
               break;
            }

            lvt_11_1_.setPos(lvt_3_1_, lvt_9_1_, lvt_5_1_);
         }

         lvt_9_1_ = (double)(MathHelper.floor(lvt_9_1_) + 1);
         this.areaEffectCloud = new AreaEffectCloudEntity(this.dragon.world, lvt_3_1_, lvt_9_1_, lvt_5_1_);
         this.areaEffectCloud.setOwner(this.dragon);
         this.areaEffectCloud.setRadius(5.0F);
         this.areaEffectCloud.setDuration(200);
         this.areaEffectCloud.setParticleData(ParticleTypes.DRAGON_BREATH);
         this.areaEffectCloud.addEffect(new EffectInstance(Effects.INSTANT_DAMAGE));
         this.dragon.world.addEntity(this.areaEffectCloud);
      }

   }

   public void initPhase() {
      this.flameTicks = 0;
      ++this.flameCount;
   }

   public void removeAreaEffect() {
      if (this.areaEffectCloud != null) {
         this.areaEffectCloud.remove();
         this.areaEffectCloud = null;
      }

   }

   public PhaseType<FlamingSittingPhase> getType() {
      return PhaseType.SITTING_FLAMING;
   }

   public void resetFlameCount() {
      this.flameCount = 0;
   }
}
