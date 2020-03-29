package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.model.GhastModel;
import net.minecraft.entity.monster.GhastEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GhastRenderer extends MobRenderer<GhastEntity, GhastModel<GhastEntity>> {
   private static final ResourceLocation GHAST_TEXTURES = new ResourceLocation("textures/entity/ghast/ghast.png");
   private static final ResourceLocation GHAST_SHOOTING_TEXTURES = new ResourceLocation("textures/entity/ghast/ghast_shooting.png");

   public GhastRenderer(EntityRendererManager p_i46174_1_) {
      super(p_i46174_1_, new GhastModel(), 1.5F);
   }

   public ResourceLocation getEntityTexture(GhastEntity p_110775_1_) {
      return p_110775_1_.isAttacking() ? GHAST_SHOOTING_TEXTURES : GHAST_TEXTURES;
   }

   protected void func_225620_a_(GhastEntity p_225620_1_, MatrixStack p_225620_2_, float p_225620_3_) {
      float lvt_4_1_ = 1.0F;
      float lvt_5_1_ = 4.5F;
      float lvt_6_1_ = 4.5F;
      p_225620_2_.func_227862_a_(4.5F, 4.5F, 4.5F);
   }
}
