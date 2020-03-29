package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.model.EndermiteModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.EndermiteEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EndermiteRenderer extends MobRenderer<EndermiteEntity, EndermiteModel<EndermiteEntity>> {
   private static final ResourceLocation ENDERMITE_TEXTURES = new ResourceLocation("textures/entity/endermite.png");

   public EndermiteRenderer(EntityRendererManager p_i46181_1_) {
      super(p_i46181_1_, new EndermiteModel(), 0.3F);
   }

   protected float getDeathMaxRotation(EndermiteEntity p_77037_1_) {
      return 180.0F;
   }

   public ResourceLocation getEntityTexture(EndermiteEntity p_110775_1_) {
      return ENDERMITE_TEXTURES;
   }

   // $FF: synthetic method
   protected float getDeathMaxRotation(LivingEntity p_77037_1_) {
      return this.getDeathMaxRotation((EndermiteEntity)p_77037_1_);
   }

   // $FF: synthetic method
   public ResourceLocation getEntityTexture(Entity p_110775_1_) {
      return this.getEntityTexture((EndermiteEntity)p_110775_1_);
   }
}
