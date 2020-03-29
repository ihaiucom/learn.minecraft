package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.model.BoatModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BoatRenderer extends EntityRenderer<BoatEntity> {
   private static final ResourceLocation[] BOAT_TEXTURES = new ResourceLocation[]{new ResourceLocation("textures/entity/boat/oak.png"), new ResourceLocation("textures/entity/boat/spruce.png"), new ResourceLocation("textures/entity/boat/birch.png"), new ResourceLocation("textures/entity/boat/jungle.png"), new ResourceLocation("textures/entity/boat/acacia.png"), new ResourceLocation("textures/entity/boat/dark_oak.png")};
   protected final BoatModel field_76998_a = new BoatModel();

   public BoatRenderer(EntityRendererManager p_i46190_1_) {
      super(p_i46190_1_);
      this.shadowSize = 0.8F;
   }

   public void func_225623_a_(BoatEntity p_225623_1_, float p_225623_2_, float p_225623_3_, MatrixStack p_225623_4_, IRenderTypeBuffer p_225623_5_, int p_225623_6_) {
      p_225623_4_.func_227860_a_();
      p_225623_4_.func_227861_a_(0.0D, 0.375D, 0.0D);
      p_225623_4_.func_227863_a_(Vector3f.field_229181_d_.func_229187_a_(180.0F - p_225623_2_));
      float lvt_7_1_ = (float)p_225623_1_.getTimeSinceHit() - p_225623_3_;
      float lvt_8_1_ = p_225623_1_.getDamageTaken() - p_225623_3_;
      if (lvt_8_1_ < 0.0F) {
         lvt_8_1_ = 0.0F;
      }

      if (lvt_7_1_ > 0.0F) {
         p_225623_4_.func_227863_a_(Vector3f.field_229179_b_.func_229187_a_(MathHelper.sin(lvt_7_1_) * lvt_7_1_ * lvt_8_1_ / 10.0F * (float)p_225623_1_.getForwardDirection()));
      }

      float lvt_9_1_ = p_225623_1_.getRockingAngle(p_225623_3_);
      if (!MathHelper.epsilonEquals(lvt_9_1_, 0.0F)) {
         p_225623_4_.func_227863_a_(new Quaternion(new Vector3f(1.0F, 0.0F, 1.0F), p_225623_1_.getRockingAngle(p_225623_3_), true));
      }

      p_225623_4_.func_227862_a_(-1.0F, -1.0F, 1.0F);
      p_225623_4_.func_227863_a_(Vector3f.field_229181_d_.func_229187_a_(90.0F));
      this.field_76998_a.func_225597_a_(p_225623_1_, p_225623_3_, 0.0F, -0.1F, 0.0F, 0.0F);
      IVertexBuilder lvt_10_1_ = p_225623_5_.getBuffer(this.field_76998_a.func_228282_a_(this.getEntityTexture(p_225623_1_)));
      this.field_76998_a.func_225598_a_(p_225623_4_, lvt_10_1_, p_225623_6_, OverlayTexture.field_229196_a_, 1.0F, 1.0F, 1.0F, 1.0F);
      IVertexBuilder lvt_11_1_ = p_225623_5_.getBuffer(RenderType.func_228651_i_());
      this.field_76998_a.func_228245_c_().func_228308_a_(p_225623_4_, lvt_11_1_, p_225623_6_, OverlayTexture.field_229196_a_);
      p_225623_4_.func_227865_b_();
      super.func_225623_a_(p_225623_1_, p_225623_2_, p_225623_3_, p_225623_4_, p_225623_5_, p_225623_6_);
   }

   public ResourceLocation getEntityTexture(BoatEntity p_110775_1_) {
      return BOAT_TEXTURES[p_110775_1_.getBoatType().ordinal()];
   }
}
