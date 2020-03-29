package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ClockItem extends Item {
   public ClockItem(Item.Properties p_i48517_1_) {
      super(p_i48517_1_);
      this.addPropertyOverride(new ResourceLocation("time"), new IItemPropertyGetter() {
         @OnlyIn(Dist.CLIENT)
         private double rotation;
         @OnlyIn(Dist.CLIENT)
         private double rota;
         @OnlyIn(Dist.CLIENT)
         private long lastUpdateTick;

         @OnlyIn(Dist.CLIENT)
         public float call(ItemStack p_call_1_, @Nullable World p_call_2_, @Nullable LivingEntity p_call_3_) {
            boolean lvt_4_1_ = p_call_3_ != null;
            Entity lvt_5_1_ = lvt_4_1_ ? p_call_3_ : p_call_1_.getItemFrame();
            if (p_call_2_ == null && lvt_5_1_ != null) {
               p_call_2_ = ((Entity)lvt_5_1_).world;
            }

            if (p_call_2_ == null) {
               return 0.0F;
            } else {
               double lvt_6_2_;
               if (p_call_2_.dimension.isSurfaceWorld()) {
                  lvt_6_2_ = (double)p_call_2_.getCelestialAngle(1.0F);
               } else {
                  lvt_6_2_ = Math.random();
               }

               lvt_6_2_ = this.wobble(p_call_2_, lvt_6_2_);
               return (float)lvt_6_2_;
            }
         }

         @OnlyIn(Dist.CLIENT)
         private double wobble(World p_185087_1_, double p_185087_2_) {
            if (p_185087_1_.getGameTime() != this.lastUpdateTick) {
               this.lastUpdateTick = p_185087_1_.getGameTime();
               double lvt_4_1_ = p_185087_2_ - this.rotation;
               lvt_4_1_ = MathHelper.positiveModulo(lvt_4_1_ + 0.5D, 1.0D) - 0.5D;
               this.rota += lvt_4_1_ * 0.1D;
               this.rota *= 0.9D;
               this.rotation = MathHelper.positiveModulo(this.rotation + this.rota, 1.0D);
            }

            return this.rotation;
         }
      });
   }
}
