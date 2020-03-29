package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.model.HorseModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.entity.passive.horse.HorseEntity;
import net.minecraft.item.DyeableHorseArmorItem;
import net.minecraft.item.HorseArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LeatherHorseArmorLayer extends LayerRenderer<HorseEntity, HorseModel<HorseEntity>> {
   private final HorseModel<HorseEntity> field_215341_a = new HorseModel(0.1F);

   public LeatherHorseArmorLayer(IEntityRenderer<HorseEntity, HorseModel<HorseEntity>> p_i50937_1_) {
      super(p_i50937_1_);
   }

   public void func_225628_a_(MatrixStack p_225628_1_, IRenderTypeBuffer p_225628_2_, int p_225628_3_, HorseEntity p_225628_4_, float p_225628_5_, float p_225628_6_, float p_225628_7_, float p_225628_8_, float p_225628_9_, float p_225628_10_) {
      ItemStack lvt_11_1_ = p_225628_4_.func_213803_dV();
      if (lvt_11_1_.getItem() instanceof HorseArmorItem) {
         HorseArmorItem lvt_12_1_ = (HorseArmorItem)lvt_11_1_.getItem();
         ((HorseModel)this.getEntityModel()).setModelAttributes(this.field_215341_a);
         this.field_215341_a.setLivingAnimations((AbstractHorseEntity)p_225628_4_, p_225628_5_, p_225628_6_, p_225628_7_);
         this.field_215341_a.func_225597_a_((AbstractHorseEntity)p_225628_4_, p_225628_5_, p_225628_6_, p_225628_8_, p_225628_9_, p_225628_10_);
         float lvt_13_2_;
         float lvt_14_2_;
         float lvt_15_2_;
         if (lvt_12_1_ instanceof DyeableHorseArmorItem) {
            int lvt_16_1_ = ((DyeableHorseArmorItem)lvt_12_1_).getColor(lvt_11_1_);
            lvt_13_2_ = (float)(lvt_16_1_ >> 16 & 255) / 255.0F;
            lvt_14_2_ = (float)(lvt_16_1_ >> 8 & 255) / 255.0F;
            lvt_15_2_ = (float)(lvt_16_1_ & 255) / 255.0F;
         } else {
            lvt_13_2_ = 1.0F;
            lvt_14_2_ = 1.0F;
            lvt_15_2_ = 1.0F;
         }

         IVertexBuilder lvt_16_2_ = p_225628_2_.getBuffer(RenderType.func_228640_c_(lvt_12_1_.func_219976_d()));
         this.field_215341_a.func_225598_a_(p_225628_1_, lvt_16_2_, p_225628_3_, OverlayTexture.field_229196_a_, lvt_13_2_, lvt_14_2_, lvt_15_2_, 1.0F);
      }
   }
}
