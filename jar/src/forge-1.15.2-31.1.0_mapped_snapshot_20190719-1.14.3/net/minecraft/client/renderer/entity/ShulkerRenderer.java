package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.culling.ClippingHelperImpl;
import net.minecraft.client.renderer.entity.layers.ShulkerColorLayer;
import net.minecraft.client.renderer.entity.model.ShulkerModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.monster.ShulkerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ShulkerRenderer extends MobRenderer<ShulkerEntity, ShulkerModel<ShulkerEntity>> {
   public static final ResourceLocation field_204402_a;
   public static final ResourceLocation[] SHULKER_ENDERGOLEM_TEXTURE;

   public ShulkerRenderer(EntityRendererManager p_i47194_1_) {
      super(p_i47194_1_, new ShulkerModel(), 0.0F);
      this.addLayer(new ShulkerColorLayer(this));
   }

   public Vec3d func_225627_b_(ShulkerEntity p_225627_1_, float p_225627_2_) {
      int lvt_3_1_ = p_225627_1_.getClientTeleportInterp();
      if (lvt_3_1_ > 0 && p_225627_1_.isAttachedToBlock()) {
         BlockPos lvt_4_1_ = p_225627_1_.getAttachmentPos();
         BlockPos lvt_5_1_ = p_225627_1_.getOldAttachPos();
         double lvt_6_1_ = (double)((float)lvt_3_1_ - p_225627_2_) / 6.0D;
         lvt_6_1_ *= lvt_6_1_;
         double lvt_8_1_ = (double)(lvt_4_1_.getX() - lvt_5_1_.getX()) * lvt_6_1_;
         double lvt_10_1_ = (double)(lvt_4_1_.getY() - lvt_5_1_.getY()) * lvt_6_1_;
         double lvt_12_1_ = (double)(lvt_4_1_.getZ() - lvt_5_1_.getZ()) * lvt_6_1_;
         return new Vec3d(-lvt_8_1_, -lvt_10_1_, -lvt_12_1_);
      } else {
         return super.func_225627_b_(p_225627_1_, p_225627_2_);
      }
   }

   public boolean func_225626_a_(ShulkerEntity p_225626_1_, ClippingHelperImpl p_225626_2_, double p_225626_3_, double p_225626_5_, double p_225626_7_) {
      if (super.func_225626_a_((MobEntity)p_225626_1_, p_225626_2_, p_225626_3_, p_225626_5_, p_225626_7_)) {
         return true;
      } else {
         if (p_225626_1_.getClientTeleportInterp() > 0 && p_225626_1_.isAttachedToBlock()) {
            Vec3d lvt_9_1_ = new Vec3d(p_225626_1_.getAttachmentPos());
            Vec3d lvt_10_1_ = new Vec3d(p_225626_1_.getOldAttachPos());
            if (p_225626_2_.func_228957_a_(new AxisAlignedBB(lvt_10_1_.x, lvt_10_1_.y, lvt_10_1_.z, lvt_9_1_.x, lvt_9_1_.y, lvt_9_1_.z))) {
               return true;
            }
         }

         return false;
      }
   }

   public ResourceLocation getEntityTexture(ShulkerEntity p_110775_1_) {
      return p_110775_1_.getColor() == null ? field_204402_a : SHULKER_ENDERGOLEM_TEXTURE[p_110775_1_.getColor().getId()];
   }

   protected void func_225621_a_(ShulkerEntity p_225621_1_, MatrixStack p_225621_2_, float p_225621_3_, float p_225621_4_, float p_225621_5_) {
      super.func_225621_a_(p_225621_1_, p_225621_2_, p_225621_3_, p_225621_4_, p_225621_5_);
      p_225621_2_.func_227861_a_(0.0D, 0.5D, 0.0D);
      p_225621_2_.func_227863_a_(p_225621_1_.getAttachmentFacing().getOpposite().func_229384_a_());
      p_225621_2_.func_227861_a_(0.0D, -0.5D, 0.0D);
   }

   protected void func_225620_a_(ShulkerEntity p_225620_1_, MatrixStack p_225620_2_, float p_225620_3_) {
      float lvt_4_1_ = 0.999F;
      p_225620_2_.func_227862_a_(0.999F, 0.999F, 0.999F);
   }

   // $FF: synthetic method
   public Vec3d func_225627_b_(Entity p_225627_1_, float p_225627_2_) {
      return this.func_225627_b_((ShulkerEntity)p_225627_1_, p_225627_2_);
   }

   static {
      field_204402_a = new ResourceLocation("textures/" + Atlases.field_228748_g_.func_229313_b_().getPath() + ".png");
      SHULKER_ENDERGOLEM_TEXTURE = (ResourceLocation[])Atlases.field_228749_h_.stream().map((p_229125_0_) -> {
         return new ResourceLocation("textures/" + p_229125_0_.func_229313_b_().getPath() + ".png");
      }).toArray((p_229124_0_) -> {
         return new ResourceLocation[p_229124_0_];
      });
   }
}
