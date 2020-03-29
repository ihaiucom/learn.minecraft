package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.model.SquidModel;
import net.minecraft.entity.passive.SquidEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SquidRenderer extends MobRenderer<SquidEntity, SquidModel<SquidEntity>> {
   private static final ResourceLocation SQUID_TEXTURES = new ResourceLocation("textures/entity/squid.png");

   public SquidRenderer(EntityRendererManager p_i47192_1_) {
      super(p_i47192_1_, new SquidModel(), 0.7F);
   }

   public ResourceLocation getEntityTexture(SquidEntity p_110775_1_) {
      return SQUID_TEXTURES;
   }

   protected void func_225621_a_(SquidEntity p_225621_1_, MatrixStack p_225621_2_, float p_225621_3_, float p_225621_4_, float p_225621_5_) {
      float lvt_6_1_ = MathHelper.lerp(p_225621_5_, p_225621_1_.prevSquidPitch, p_225621_1_.squidPitch);
      float lvt_7_1_ = MathHelper.lerp(p_225621_5_, p_225621_1_.prevSquidYaw, p_225621_1_.squidYaw);
      p_225621_2_.func_227861_a_(0.0D, 0.5D, 0.0D);
      p_225621_2_.func_227863_a_(Vector3f.field_229181_d_.func_229187_a_(180.0F - p_225621_4_));
      p_225621_2_.func_227863_a_(Vector3f.field_229179_b_.func_229187_a_(lvt_6_1_));
      p_225621_2_.func_227863_a_(Vector3f.field_229181_d_.func_229187_a_(lvt_7_1_));
      p_225621_2_.func_227861_a_(0.0D, -1.2000000476837158D, 0.0D);
   }

   protected float handleRotationFloat(SquidEntity p_77044_1_, float p_77044_2_) {
      return MathHelper.lerp(p_77044_2_, p_77044_1_.lastTentacleAngle, p_77044_1_.tentacleAngle);
   }
}
