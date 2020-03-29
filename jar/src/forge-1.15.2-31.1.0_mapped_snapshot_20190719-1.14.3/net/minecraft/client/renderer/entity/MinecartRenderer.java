package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.MinecartModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MinecartRenderer<T extends AbstractMinecartEntity> extends EntityRenderer<T> {
   private static final ResourceLocation MINECART_TEXTURES = new ResourceLocation("textures/entity/minecart.png");
   protected final EntityModel<T> field_77013_a = new MinecartModel();

   public MinecartRenderer(EntityRendererManager p_i46155_1_) {
      super(p_i46155_1_);
      this.shadowSize = 0.7F;
   }

   public void func_225623_a_(T p_225623_1_, float p_225623_2_, float p_225623_3_, MatrixStack p_225623_4_, IRenderTypeBuffer p_225623_5_, int p_225623_6_) {
      super.func_225623_a_(p_225623_1_, p_225623_2_, p_225623_3_, p_225623_4_, p_225623_5_, p_225623_6_);
      p_225623_4_.func_227860_a_();
      long lvt_7_1_ = (long)p_225623_1_.getEntityId() * 493286711L;
      lvt_7_1_ = lvt_7_1_ * lvt_7_1_ * 4392167121L + lvt_7_1_ * 98761L;
      float lvt_9_1_ = (((float)(lvt_7_1_ >> 16 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
      float lvt_10_1_ = (((float)(lvt_7_1_ >> 20 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
      float lvt_11_1_ = (((float)(lvt_7_1_ >> 24 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
      p_225623_4_.func_227861_a_((double)lvt_9_1_, (double)lvt_10_1_, (double)lvt_11_1_);
      double lvt_12_1_ = MathHelper.lerp((double)p_225623_3_, p_225623_1_.lastTickPosX, p_225623_1_.func_226277_ct_());
      double lvt_14_1_ = MathHelper.lerp((double)p_225623_3_, p_225623_1_.lastTickPosY, p_225623_1_.func_226278_cu_());
      double lvt_16_1_ = MathHelper.lerp((double)p_225623_3_, p_225623_1_.lastTickPosZ, p_225623_1_.func_226281_cx_());
      double lvt_18_1_ = 0.30000001192092896D;
      Vec3d lvt_20_1_ = p_225623_1_.getPos(lvt_12_1_, lvt_14_1_, lvt_16_1_);
      float lvt_21_1_ = MathHelper.lerp(p_225623_3_, p_225623_1_.prevRotationPitch, p_225623_1_.rotationPitch);
      if (lvt_20_1_ != null) {
         Vec3d lvt_22_1_ = p_225623_1_.getPosOffset(lvt_12_1_, lvt_14_1_, lvt_16_1_, 0.30000001192092896D);
         Vec3d lvt_23_1_ = p_225623_1_.getPosOffset(lvt_12_1_, lvt_14_1_, lvt_16_1_, -0.30000001192092896D);
         if (lvt_22_1_ == null) {
            lvt_22_1_ = lvt_20_1_;
         }

         if (lvt_23_1_ == null) {
            lvt_23_1_ = lvt_20_1_;
         }

         p_225623_4_.func_227861_a_(lvt_20_1_.x - lvt_12_1_, (lvt_22_1_.y + lvt_23_1_.y) / 2.0D - lvt_14_1_, lvt_20_1_.z - lvt_16_1_);
         Vec3d lvt_24_1_ = lvt_23_1_.add(-lvt_22_1_.x, -lvt_22_1_.y, -lvt_22_1_.z);
         if (lvt_24_1_.length() != 0.0D) {
            lvt_24_1_ = lvt_24_1_.normalize();
            p_225623_2_ = (float)(Math.atan2(lvt_24_1_.z, lvt_24_1_.x) * 180.0D / 3.141592653589793D);
            lvt_21_1_ = (float)(Math.atan(lvt_24_1_.y) * 73.0D);
         }
      }

      p_225623_4_.func_227861_a_(0.0D, 0.375D, 0.0D);
      p_225623_4_.func_227863_a_(Vector3f.field_229181_d_.func_229187_a_(180.0F - p_225623_2_));
      p_225623_4_.func_227863_a_(Vector3f.field_229183_f_.func_229187_a_(-lvt_21_1_));
      float lvt_22_2_ = (float)p_225623_1_.getRollingAmplitude() - p_225623_3_;
      float lvt_23_2_ = p_225623_1_.getDamage() - p_225623_3_;
      if (lvt_23_2_ < 0.0F) {
         lvt_23_2_ = 0.0F;
      }

      if (lvt_22_2_ > 0.0F) {
         p_225623_4_.func_227863_a_(Vector3f.field_229179_b_.func_229187_a_(MathHelper.sin(lvt_22_2_) * lvt_22_2_ * lvt_23_2_ / 10.0F * (float)p_225623_1_.getRollingDirection()));
      }

      int lvt_24_2_ = p_225623_1_.getDisplayTileOffset();
      BlockState lvt_25_1_ = p_225623_1_.getDisplayTile();
      if (lvt_25_1_.getRenderType() != BlockRenderType.INVISIBLE) {
         p_225623_4_.func_227860_a_();
         float lvt_26_1_ = 0.75F;
         p_225623_4_.func_227862_a_(0.75F, 0.75F, 0.75F);
         p_225623_4_.func_227861_a_(-0.5D, (double)((float)(lvt_24_2_ - 8) / 16.0F), 0.5D);
         p_225623_4_.func_227863_a_(Vector3f.field_229181_d_.func_229187_a_(90.0F));
         this.func_225630_a_(p_225623_1_, p_225623_3_, lvt_25_1_, p_225623_4_, p_225623_5_, p_225623_6_);
         p_225623_4_.func_227865_b_();
      }

      p_225623_4_.func_227862_a_(-1.0F, -1.0F, 1.0F);
      this.field_77013_a.func_225597_a_(p_225623_1_, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F);
      IVertexBuilder lvt_26_2_ = p_225623_5_.getBuffer(this.field_77013_a.func_228282_a_(this.getEntityTexture(p_225623_1_)));
      this.field_77013_a.func_225598_a_(p_225623_4_, lvt_26_2_, p_225623_6_, OverlayTexture.field_229196_a_, 1.0F, 1.0F, 1.0F, 1.0F);
      p_225623_4_.func_227865_b_();
   }

   public ResourceLocation getEntityTexture(T p_110775_1_) {
      return MINECART_TEXTURES;
   }

   protected void func_225630_a_(T p_225630_1_, float p_225630_2_, BlockState p_225630_3_, MatrixStack p_225630_4_, IRenderTypeBuffer p_225630_5_, int p_225630_6_) {
      Minecraft.getInstance().getBlockRendererDispatcher().func_228791_a_(p_225630_3_, p_225630_4_, p_225630_5_, p_225630_6_, OverlayTexture.field_229196_a_);
   }
}
