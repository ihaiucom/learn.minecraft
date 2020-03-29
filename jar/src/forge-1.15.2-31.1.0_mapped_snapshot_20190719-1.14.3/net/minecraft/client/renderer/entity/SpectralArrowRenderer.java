package net.minecraft.client.renderer.entity;

import net.minecraft.entity.projectile.SpectralArrowEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SpectralArrowRenderer extends ArrowRenderer<SpectralArrowEntity> {
   public static final ResourceLocation RES_SPECTRAL_ARROW = new ResourceLocation("textures/entity/projectiles/spectral_arrow.png");

   public SpectralArrowRenderer(EntityRendererManager p_i46549_1_) {
      super(p_i46549_1_);
   }

   public ResourceLocation getEntityTexture(SpectralArrowEntity p_110775_1_) {
      return RES_SPECTRAL_ARROW;
   }
}
