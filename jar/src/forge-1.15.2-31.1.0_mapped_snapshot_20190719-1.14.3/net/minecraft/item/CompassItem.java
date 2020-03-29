package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CompassItem extends Item {
   public CompassItem(Item.Properties p_i48515_1_) {
      super(p_i48515_1_);
      this.addPropertyOverride(new ResourceLocation("angle"), new IItemPropertyGetter() {
         @OnlyIn(Dist.CLIENT)
         private double rotation;
         @OnlyIn(Dist.CLIENT)
         private double rota;
         @OnlyIn(Dist.CLIENT)
         private long lastUpdateTick;

         @OnlyIn(Dist.CLIENT)
         public float call(ItemStack p_call_1_, @Nullable World p_call_2_, @Nullable LivingEntity p_call_3_) {
            if (p_call_3_ == null && !p_call_1_.isOnItemFrame()) {
               return 0.0F;
            } else {
               boolean lvt_4_1_ = p_call_3_ != null;
               Entity lvt_5_1_ = lvt_4_1_ ? p_call_3_ : p_call_1_.getItemFrame();
               if (p_call_2_ == null) {
                  p_call_2_ = ((Entity)lvt_5_1_).world;
               }

               double lvt_6_2_;
               if (p_call_2_.dimension.isSurfaceWorld()) {
                  double lvt_8_1_ = lvt_4_1_ ? (double)((Entity)lvt_5_1_).rotationYaw : this.getFrameRotation((ItemFrameEntity)lvt_5_1_);
                  lvt_8_1_ = MathHelper.positiveModulo(lvt_8_1_ / 360.0D, 1.0D);
                  double lvt_10_1_ = this.getSpawnToAngle(p_call_2_, (Entity)lvt_5_1_) / 6.2831854820251465D;
                  lvt_6_2_ = 0.5D - (lvt_8_1_ - 0.25D - lvt_10_1_);
               } else {
                  lvt_6_2_ = Math.random();
               }

               if (lvt_4_1_) {
                  lvt_6_2_ = this.wobble(p_call_2_, lvt_6_2_);
               }

               return MathHelper.positiveModulo((float)lvt_6_2_, 1.0F);
            }
         }

         @OnlyIn(Dist.CLIENT)
         private double wobble(World p_185093_1_, double p_185093_2_) {
            if (p_185093_1_.getGameTime() != this.lastUpdateTick) {
               this.lastUpdateTick = p_185093_1_.getGameTime();
               double lvt_4_1_ = p_185093_2_ - this.rotation;
               lvt_4_1_ = MathHelper.positiveModulo(lvt_4_1_ + 0.5D, 1.0D) - 0.5D;
               this.rota += lvt_4_1_ * 0.1D;
               this.rota *= 0.8D;
               this.rotation = MathHelper.positiveModulo(this.rotation + this.rota, 1.0D);
            }

            return this.rotation;
         }

         @OnlyIn(Dist.CLIENT)
         private double getFrameRotation(ItemFrameEntity p_185094_1_) {
            return (double)MathHelper.wrapDegrees(180 + p_185094_1_.getHorizontalFacing().getHorizontalIndex() * 90);
         }

         @OnlyIn(Dist.CLIENT)
         private double getSpawnToAngle(IWorld p_185092_1_, Entity p_185092_2_) {
            BlockPos lvt_3_1_ = p_185092_1_.getSpawnPoint();
            return Math.atan2((double)lvt_3_1_.getZ() - p_185092_2_.func_226281_cx_(), (double)lvt_3_1_.getX() - p_185092_2_.func_226277_ct_());
         }
      });
   }
}
