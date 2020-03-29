package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.model.ChickenModel;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ChickenRenderer extends MobRenderer<ChickenEntity, ChickenModel<ChickenEntity>> {
   private static final ResourceLocation CHICKEN_TEXTURES = new ResourceLocation("textures/entity/chicken.png");

   public ChickenRenderer(EntityRendererManager p_i47211_1_) {
      super(p_i47211_1_, new ChickenModel(), 0.3F);
   }

   public ResourceLocation getEntityTexture(ChickenEntity p_110775_1_) {
      return CHICKEN_TEXTURES;
   }

   protected float handleRotationFloat(ChickenEntity p_77044_1_, float p_77044_2_) {
      float lvt_3_1_ = MathHelper.lerp(p_77044_2_, p_77044_1_.oFlap, p_77044_1_.wingRotation);
      float lvt_4_1_ = MathHelper.lerp(p_77044_2_, p_77044_1_.oFlapSpeed, p_77044_1_.destPos);
      return (MathHelper.sin(lvt_3_1_) + 1.0F) * lvt_4_1_;
   }
}
