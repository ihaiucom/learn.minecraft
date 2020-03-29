package net.minecraft.client.renderer.entity.model;

import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractZombieModel<T extends MonsterEntity> extends BipedModel<T> {
   protected AbstractZombieModel(float p_i51070_1_, float p_i51070_2_, int p_i51070_3_, int p_i51070_4_) {
      super(p_i51070_1_, p_i51070_2_, p_i51070_3_, p_i51070_4_);
   }

   public void func_225597_a_(T p_225597_1_, float p_225597_2_, float p_225597_3_, float p_225597_4_, float p_225597_5_, float p_225597_6_) {
      super.func_225597_a_((LivingEntity)p_225597_1_, p_225597_2_, p_225597_3_, p_225597_4_, p_225597_5_, p_225597_6_);
      boolean lvt_7_1_ = this.func_212850_a_(p_225597_1_);
      float lvt_8_1_ = MathHelper.sin(this.swingProgress * 3.1415927F);
      float lvt_9_1_ = MathHelper.sin((1.0F - (1.0F - this.swingProgress) * (1.0F - this.swingProgress)) * 3.1415927F);
      this.bipedRightArm.rotateAngleZ = 0.0F;
      this.bipedLeftArm.rotateAngleZ = 0.0F;
      this.bipedRightArm.rotateAngleY = -(0.1F - lvt_8_1_ * 0.6F);
      this.bipedLeftArm.rotateAngleY = 0.1F - lvt_8_1_ * 0.6F;
      float lvt_10_1_ = -3.1415927F / (lvt_7_1_ ? 1.5F : 2.25F);
      this.bipedRightArm.rotateAngleX = lvt_10_1_;
      this.bipedLeftArm.rotateAngleX = lvt_10_1_;
      ModelRenderer var10000 = this.bipedRightArm;
      var10000.rotateAngleX += lvt_8_1_ * 1.2F - lvt_9_1_ * 0.4F;
      var10000 = this.bipedLeftArm;
      var10000.rotateAngleX += lvt_8_1_ * 1.2F - lvt_9_1_ * 0.4F;
      var10000 = this.bipedRightArm;
      var10000.rotateAngleZ += MathHelper.cos(p_225597_4_ * 0.09F) * 0.05F + 0.05F;
      var10000 = this.bipedLeftArm;
      var10000.rotateAngleZ -= MathHelper.cos(p_225597_4_ * 0.09F) * 0.05F + 0.05F;
      var10000 = this.bipedRightArm;
      var10000.rotateAngleX += MathHelper.sin(p_225597_4_ * 0.067F) * 0.05F;
      var10000 = this.bipedLeftArm;
      var10000.rotateAngleX -= MathHelper.sin(p_225597_4_ * 0.067F) * 0.05F;
   }

   public abstract boolean func_212850_a_(T var1);
}
