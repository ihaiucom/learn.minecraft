package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.layers.CreeperChargeLayer;
import net.minecraft.client.renderer.entity.model.CreeperModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CreeperRenderer extends MobRenderer<CreeperEntity, CreeperModel<CreeperEntity>> {
   private static final ResourceLocation CREEPER_TEXTURES = new ResourceLocation("textures/entity/creeper/creeper.png");

   public CreeperRenderer(EntityRendererManager p_i46186_1_) {
      super(p_i46186_1_, new CreeperModel(), 0.5F);
      this.addLayer(new CreeperChargeLayer(this));
   }

   protected void func_225620_a_(CreeperEntity p_225620_1_, MatrixStack p_225620_2_, float p_225620_3_) {
      float lvt_4_1_ = p_225620_1_.getCreeperFlashIntensity(p_225620_3_);
      float lvt_5_1_ = 1.0F + MathHelper.sin(lvt_4_1_ * 100.0F) * lvt_4_1_ * 0.01F;
      lvt_4_1_ = MathHelper.clamp(lvt_4_1_, 0.0F, 1.0F);
      lvt_4_1_ *= lvt_4_1_;
      lvt_4_1_ *= lvt_4_1_;
      float lvt_6_1_ = (1.0F + lvt_4_1_ * 0.4F) * lvt_5_1_;
      float lvt_7_1_ = (1.0F + lvt_4_1_ * 0.1F) / lvt_5_1_;
      p_225620_2_.func_227862_a_(lvt_6_1_, lvt_7_1_, lvt_6_1_);
   }

   protected float func_225625_b_(CreeperEntity p_225625_1_, float p_225625_2_) {
      float lvt_3_1_ = p_225625_1_.getCreeperFlashIntensity(p_225625_2_);
      return (int)(lvt_3_1_ * 10.0F) % 2 == 0 ? 0.0F : MathHelper.clamp(lvt_3_1_, 0.5F, 1.0F);
   }

   public ResourceLocation getEntityTexture(CreeperEntity p_110775_1_) {
      return CREEPER_TEXTURES;
   }

   // $FF: synthetic method
   protected float func_225625_b_(LivingEntity p_225625_1_, float p_225625_2_) {
      return this.func_225625_b_((CreeperEntity)p_225625_1_, p_225625_2_);
   }
}
