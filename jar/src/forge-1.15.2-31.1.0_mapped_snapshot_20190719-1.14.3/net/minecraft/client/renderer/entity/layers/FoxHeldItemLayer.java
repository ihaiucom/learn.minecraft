package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.model.FoxModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FoxHeldItemLayer extends LayerRenderer<FoxEntity, FoxModel<FoxEntity>> {
   public FoxHeldItemLayer(IEntityRenderer<FoxEntity, FoxModel<FoxEntity>> p_i50938_1_) {
      super(p_i50938_1_);
   }

   public void func_225628_a_(MatrixStack p_225628_1_, IRenderTypeBuffer p_225628_2_, int p_225628_3_, FoxEntity p_225628_4_, float p_225628_5_, float p_225628_6_, float p_225628_7_, float p_225628_8_, float p_225628_9_, float p_225628_10_) {
      boolean lvt_11_1_ = p_225628_4_.isSleeping();
      boolean lvt_12_1_ = p_225628_4_.isChild();
      p_225628_1_.func_227860_a_();
      float lvt_13_2_;
      if (lvt_12_1_) {
         lvt_13_2_ = 0.75F;
         p_225628_1_.func_227862_a_(0.75F, 0.75F, 0.75F);
         p_225628_1_.func_227861_a_(0.0D, 0.5D, 0.20937499403953552D);
      }

      p_225628_1_.func_227861_a_((double)(((FoxModel)this.getEntityModel()).field_217115_a.rotationPointX / 16.0F), (double)(((FoxModel)this.getEntityModel()).field_217115_a.rotationPointY / 16.0F), (double)(((FoxModel)this.getEntityModel()).field_217115_a.rotationPointZ / 16.0F));
      lvt_13_2_ = p_225628_4_.func_213475_v(p_225628_7_);
      p_225628_1_.func_227863_a_(Vector3f.field_229183_f_.func_229193_c_(lvt_13_2_));
      p_225628_1_.func_227863_a_(Vector3f.field_229181_d_.func_229187_a_(p_225628_9_));
      p_225628_1_.func_227863_a_(Vector3f.field_229179_b_.func_229187_a_(p_225628_10_));
      if (p_225628_4_.isChild()) {
         if (lvt_11_1_) {
            p_225628_1_.func_227861_a_(0.4000000059604645D, 0.25999999046325684D, 0.15000000596046448D);
         } else {
            p_225628_1_.func_227861_a_(0.05999999865889549D, 0.25999999046325684D, -0.5D);
         }
      } else if (lvt_11_1_) {
         p_225628_1_.func_227861_a_(0.46000000834465027D, 0.25999999046325684D, 0.2199999988079071D);
      } else {
         p_225628_1_.func_227861_a_(0.05999999865889549D, 0.27000001072883606D, -0.5D);
      }

      p_225628_1_.func_227863_a_(Vector3f.field_229179_b_.func_229187_a_(90.0F));
      if (lvt_11_1_) {
         p_225628_1_.func_227863_a_(Vector3f.field_229183_f_.func_229187_a_(90.0F));
      }

      ItemStack lvt_14_1_ = p_225628_4_.getItemStackFromSlot(EquipmentSlotType.MAINHAND);
      Minecraft.getInstance().getFirstPersonRenderer().func_228397_a_(p_225628_4_, lvt_14_1_, ItemCameraTransforms.TransformType.GROUND, false, p_225628_1_, p_225628_2_, p_225628_3_);
      p_225628_1_.func_227865_b_();
   }
}
