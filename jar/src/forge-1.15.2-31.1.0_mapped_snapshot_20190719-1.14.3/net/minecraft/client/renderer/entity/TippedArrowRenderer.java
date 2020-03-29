package net.minecraft.client.renderer.entity;

import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TippedArrowRenderer extends ArrowRenderer<ArrowEntity> {
   public static final ResourceLocation RES_ARROW = new ResourceLocation("textures/entity/projectiles/arrow.png");
   public static final ResourceLocation RES_TIPPED_ARROW = new ResourceLocation("textures/entity/projectiles/tipped_arrow.png");

   public TippedArrowRenderer(EntityRendererManager p_i46547_1_) {
      super(p_i46547_1_);
   }

   public ResourceLocation getEntityTexture(ArrowEntity p_110775_1_) {
      return p_110775_1_.getColor() > 0 ? RES_TIPPED_ARROW : RES_ARROW;
   }
}
