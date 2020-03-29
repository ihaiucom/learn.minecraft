package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.model.SheepModel;
import net.minecraft.client.renderer.entity.model.SheepWoolModel;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SheepWoolLayer extends LayerRenderer<SheepEntity, SheepModel<SheepEntity>> {
   private static final ResourceLocation TEXTURE = new ResourceLocation("textures/entity/sheep/sheep_fur.png");
   private final SheepWoolModel<SheepEntity> sheepModel = new SheepWoolModel();

   public SheepWoolLayer(IEntityRenderer<SheepEntity, SheepModel<SheepEntity>> p_i50925_1_) {
      super(p_i50925_1_);
   }

   public void func_225628_a_(MatrixStack p_225628_1_, IRenderTypeBuffer p_225628_2_, int p_225628_3_, SheepEntity p_225628_4_, float p_225628_5_, float p_225628_6_, float p_225628_7_, float p_225628_8_, float p_225628_9_, float p_225628_10_) {
      if (!p_225628_4_.getSheared() && !p_225628_4_.isInvisible()) {
         float lvt_11_2_;
         float lvt_12_2_;
         float lvt_13_2_;
         if (p_225628_4_.hasCustomName() && "jeb_".equals(p_225628_4_.getName().getUnformattedComponentText())) {
            int lvt_14_1_ = true;
            int lvt_15_1_ = p_225628_4_.ticksExisted / 25 + p_225628_4_.getEntityId();
            int lvt_16_1_ = DyeColor.values().length;
            int lvt_17_1_ = lvt_15_1_ % lvt_16_1_;
            int lvt_18_1_ = (lvt_15_1_ + 1) % lvt_16_1_;
            float lvt_19_1_ = ((float)(p_225628_4_.ticksExisted % 25) + p_225628_7_) / 25.0F;
            float[] lvt_20_1_ = SheepEntity.getDyeRgb(DyeColor.byId(lvt_17_1_));
            float[] lvt_21_1_ = SheepEntity.getDyeRgb(DyeColor.byId(lvt_18_1_));
            lvt_11_2_ = lvt_20_1_[0] * (1.0F - lvt_19_1_) + lvt_21_1_[0] * lvt_19_1_;
            lvt_12_2_ = lvt_20_1_[1] * (1.0F - lvt_19_1_) + lvt_21_1_[1] * lvt_19_1_;
            lvt_13_2_ = lvt_20_1_[2] * (1.0F - lvt_19_1_) + lvt_21_1_[2] * lvt_19_1_;
         } else {
            float[] lvt_14_2_ = SheepEntity.getDyeRgb(p_225628_4_.getFleeceColor());
            lvt_11_2_ = lvt_14_2_[0];
            lvt_12_2_ = lvt_14_2_[1];
            lvt_13_2_ = lvt_14_2_[2];
         }

         func_229140_a_(this.getEntityModel(), this.sheepModel, TEXTURE, p_225628_1_, p_225628_2_, p_225628_3_, p_225628_4_, p_225628_5_, p_225628_6_, p_225628_8_, p_225628_9_, p_225628_10_, p_225628_7_, lvt_11_2_, lvt_12_2_, lvt_13_2_);
      }
   }
}
