package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.model.ParrotModel;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ParrotRenderer extends MobRenderer<ParrotEntity, ParrotModel> {
   public static final ResourceLocation[] PARROT_TEXTURES = new ResourceLocation[]{new ResourceLocation("textures/entity/parrot/parrot_red_blue.png"), new ResourceLocation("textures/entity/parrot/parrot_blue.png"), new ResourceLocation("textures/entity/parrot/parrot_green.png"), new ResourceLocation("textures/entity/parrot/parrot_yellow_blue.png"), new ResourceLocation("textures/entity/parrot/parrot_grey.png")};

   public ParrotRenderer(EntityRendererManager p_i47375_1_) {
      super(p_i47375_1_, new ParrotModel(), 0.3F);
   }

   public ResourceLocation getEntityTexture(ParrotEntity p_110775_1_) {
      return PARROT_TEXTURES[p_110775_1_.getVariant()];
   }

   public float handleRotationFloat(ParrotEntity p_77044_1_, float p_77044_2_) {
      float lvt_3_1_ = MathHelper.lerp(p_77044_2_, p_77044_1_.oFlap, p_77044_1_.flap);
      float lvt_4_1_ = MathHelper.lerp(p_77044_2_, p_77044_1_.oFlapSpeed, p_77044_1_.flapSpeed);
      return (MathHelper.sin(lvt_3_1_) + 1.0F) * lvt_4_1_;
   }
}
