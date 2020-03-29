package net.minecraft.client.renderer.entity.model;

import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.passive.CatEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CatModel<T extends CatEntity> extends OcelotModel<T> {
   private float field_217155_m;
   private float field_217156_n;
   private float field_217157_o;

   public CatModel(float p_i51069_1_) {
      super(p_i51069_1_);
   }

   public void setLivingAnimations(T p_212843_1_, float p_212843_2_, float p_212843_3_, float p_212843_4_) {
      this.field_217155_m = p_212843_1_.func_213408_v(p_212843_4_);
      this.field_217156_n = p_212843_1_.func_213421_w(p_212843_4_);
      this.field_217157_o = p_212843_1_.func_213424_x(p_212843_4_);
      if (this.field_217155_m <= 0.0F) {
         this.ocelotHead.rotateAngleX = 0.0F;
         this.ocelotHead.rotateAngleZ = 0.0F;
         this.ocelotFrontLeftLeg.rotateAngleX = 0.0F;
         this.ocelotFrontLeftLeg.rotateAngleZ = 0.0F;
         this.ocelotFrontRightLeg.rotateAngleX = 0.0F;
         this.ocelotFrontRightLeg.rotateAngleZ = 0.0F;
         this.ocelotFrontRightLeg.rotationPointX = -1.2F;
         this.ocelotBackLeftLeg.rotateAngleX = 0.0F;
         this.ocelotBackRightLeg.rotateAngleX = 0.0F;
         this.ocelotBackRightLeg.rotateAngleZ = 0.0F;
         this.ocelotBackRightLeg.rotationPointX = -1.1F;
         this.ocelotBackRightLeg.rotationPointY = 18.0F;
      }

      super.setLivingAnimations(p_212843_1_, p_212843_2_, p_212843_3_, p_212843_4_);
      if (p_212843_1_.isSitting()) {
         this.ocelotBody.rotateAngleX = 0.7853982F;
         ModelRenderer var10000 = this.ocelotBody;
         var10000.rotationPointY += -4.0F;
         var10000 = this.ocelotBody;
         var10000.rotationPointZ += 5.0F;
         var10000 = this.ocelotHead;
         var10000.rotationPointY += -3.3F;
         ++this.ocelotHead.rotationPointZ;
         var10000 = this.ocelotTail;
         var10000.rotationPointY += 8.0F;
         var10000 = this.ocelotTail;
         var10000.rotationPointZ += -2.0F;
         var10000 = this.ocelotTail2;
         var10000.rotationPointY += 2.0F;
         var10000 = this.ocelotTail2;
         var10000.rotationPointZ += -0.8F;
         this.ocelotTail.rotateAngleX = 1.7278761F;
         this.ocelotTail2.rotateAngleX = 2.670354F;
         this.ocelotFrontLeftLeg.rotateAngleX = -0.15707964F;
         this.ocelotFrontLeftLeg.rotationPointY = 16.1F;
         this.ocelotFrontLeftLeg.rotationPointZ = -7.0F;
         this.ocelotFrontRightLeg.rotateAngleX = -0.15707964F;
         this.ocelotFrontRightLeg.rotationPointY = 16.1F;
         this.ocelotFrontRightLeg.rotationPointZ = -7.0F;
         this.ocelotBackLeftLeg.rotateAngleX = -1.5707964F;
         this.ocelotBackLeftLeg.rotationPointY = 21.0F;
         this.ocelotBackLeftLeg.rotationPointZ = 1.0F;
         this.ocelotBackRightLeg.rotateAngleX = -1.5707964F;
         this.ocelotBackRightLeg.rotationPointY = 21.0F;
         this.ocelotBackRightLeg.rotationPointZ = 1.0F;
         this.state = 3;
      }

   }

   public void func_225597_a_(T p_225597_1_, float p_225597_2_, float p_225597_3_, float p_225597_4_, float p_225597_5_, float p_225597_6_) {
      super.func_225597_a_(p_225597_1_, p_225597_2_, p_225597_3_, p_225597_4_, p_225597_5_, p_225597_6_);
      if (this.field_217155_m > 0.0F) {
         this.ocelotHead.rotateAngleZ = ModelUtils.func_228283_a_(this.ocelotHead.rotateAngleZ, -1.2707963F, this.field_217155_m);
         this.ocelotHead.rotateAngleY = ModelUtils.func_228283_a_(this.ocelotHead.rotateAngleY, 1.2707963F, this.field_217155_m);
         this.ocelotFrontLeftLeg.rotateAngleX = -1.2707963F;
         this.ocelotFrontRightLeg.rotateAngleX = -0.47079635F;
         this.ocelotFrontRightLeg.rotateAngleZ = -0.2F;
         this.ocelotFrontRightLeg.rotationPointX = -0.2F;
         this.ocelotBackLeftLeg.rotateAngleX = -0.4F;
         this.ocelotBackRightLeg.rotateAngleX = 0.5F;
         this.ocelotBackRightLeg.rotateAngleZ = -0.5F;
         this.ocelotBackRightLeg.rotationPointX = -0.3F;
         this.ocelotBackRightLeg.rotationPointY = 20.0F;
         this.ocelotTail.rotateAngleX = ModelUtils.func_228283_a_(this.ocelotTail.rotateAngleX, 0.8F, this.field_217156_n);
         this.ocelotTail2.rotateAngleX = ModelUtils.func_228283_a_(this.ocelotTail2.rotateAngleX, -0.4F, this.field_217156_n);
      }

      if (this.field_217157_o > 0.0F) {
         this.ocelotHead.rotateAngleX = ModelUtils.func_228283_a_(this.ocelotHead.rotateAngleX, -0.58177644F, this.field_217157_o);
      }

   }
}
