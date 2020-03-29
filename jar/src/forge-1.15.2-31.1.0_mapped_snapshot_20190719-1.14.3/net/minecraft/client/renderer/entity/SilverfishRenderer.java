package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.model.SilverfishModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.SilverfishEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SilverfishRenderer extends MobRenderer<SilverfishEntity, SilverfishModel<SilverfishEntity>> {
   private static final ResourceLocation SILVERFISH_TEXTURES = new ResourceLocation("textures/entity/silverfish.png");

   public SilverfishRenderer(EntityRendererManager p_i46144_1_) {
      super(p_i46144_1_, new SilverfishModel(), 0.3F);
   }

   protected float getDeathMaxRotation(SilverfishEntity p_77037_1_) {
      return 180.0F;
   }

   public ResourceLocation getEntityTexture(SilverfishEntity p_110775_1_) {
      return SILVERFISH_TEXTURES;
   }

   // $FF: synthetic method
   protected float getDeathMaxRotation(LivingEntity p_77037_1_) {
      return this.getDeathMaxRotation((SilverfishEntity)p_77037_1_);
   }

   // $FF: synthetic method
   public ResourceLocation getEntityTexture(Entity p_110775_1_) {
      return this.getEntityTexture((SilverfishEntity)p_110775_1_);
   }
}
