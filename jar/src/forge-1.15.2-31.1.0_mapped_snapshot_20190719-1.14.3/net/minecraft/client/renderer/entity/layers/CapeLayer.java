package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CapeLayer extends LayerRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> {
   public CapeLayer(IEntityRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> p_i50950_1_) {
      super(p_i50950_1_);
   }

   public void func_225628_a_(MatrixStack p_225628_1_, IRenderTypeBuffer p_225628_2_, int p_225628_3_, AbstractClientPlayerEntity p_225628_4_, float p_225628_5_, float p_225628_6_, float p_225628_7_, float p_225628_8_, float p_225628_9_, float p_225628_10_) {
      if (p_225628_4_.hasPlayerInfo() && !p_225628_4_.isInvisible() && p_225628_4_.isWearing(PlayerModelPart.CAPE) && p_225628_4_.getLocationCape() != null) {
         ItemStack lvt_11_1_ = p_225628_4_.getItemStackFromSlot(EquipmentSlotType.CHEST);
         if (lvt_11_1_.getItem() != Items.ELYTRA) {
            p_225628_1_.func_227860_a_();
            p_225628_1_.func_227861_a_(0.0D, 0.0D, 0.125D);
            double lvt_12_1_ = MathHelper.lerp((double)p_225628_7_, p_225628_4_.prevChasingPosX, p_225628_4_.chasingPosX) - MathHelper.lerp((double)p_225628_7_, p_225628_4_.prevPosX, p_225628_4_.func_226277_ct_());
            double lvt_14_1_ = MathHelper.lerp((double)p_225628_7_, p_225628_4_.prevChasingPosY, p_225628_4_.chasingPosY) - MathHelper.lerp((double)p_225628_7_, p_225628_4_.prevPosY, p_225628_4_.func_226278_cu_());
            double lvt_16_1_ = MathHelper.lerp((double)p_225628_7_, p_225628_4_.prevChasingPosZ, p_225628_4_.chasingPosZ) - MathHelper.lerp((double)p_225628_7_, p_225628_4_.prevPosZ, p_225628_4_.func_226281_cx_());
            float lvt_18_1_ = p_225628_4_.prevRenderYawOffset + (p_225628_4_.renderYawOffset - p_225628_4_.prevRenderYawOffset);
            double lvt_19_1_ = (double)MathHelper.sin(lvt_18_1_ * 0.017453292F);
            double lvt_21_1_ = (double)(-MathHelper.cos(lvt_18_1_ * 0.017453292F));
            float lvt_23_1_ = (float)lvt_14_1_ * 10.0F;
            lvt_23_1_ = MathHelper.clamp(lvt_23_1_, -6.0F, 32.0F);
            float lvt_24_1_ = (float)(lvt_12_1_ * lvt_19_1_ + lvt_16_1_ * lvt_21_1_) * 100.0F;
            lvt_24_1_ = MathHelper.clamp(lvt_24_1_, 0.0F, 150.0F);
            float lvt_25_1_ = (float)(lvt_12_1_ * lvt_21_1_ - lvt_16_1_ * lvt_19_1_) * 100.0F;
            lvt_25_1_ = MathHelper.clamp(lvt_25_1_, -20.0F, 20.0F);
            if (lvt_24_1_ < 0.0F) {
               lvt_24_1_ = 0.0F;
            }

            float lvt_26_1_ = MathHelper.lerp(p_225628_7_, p_225628_4_.prevCameraYaw, p_225628_4_.cameraYaw);
            lvt_23_1_ += MathHelper.sin(MathHelper.lerp(p_225628_7_, p_225628_4_.prevDistanceWalkedModified, p_225628_4_.distanceWalkedModified) * 6.0F) * 32.0F * lvt_26_1_;
            if (p_225628_4_.isCrouching()) {
               lvt_23_1_ += 25.0F;
            }

            p_225628_1_.func_227863_a_(Vector3f.field_229179_b_.func_229187_a_(6.0F + lvt_24_1_ / 2.0F + lvt_23_1_));
            p_225628_1_.func_227863_a_(Vector3f.field_229183_f_.func_229187_a_(lvt_25_1_ / 2.0F));
            p_225628_1_.func_227863_a_(Vector3f.field_229181_d_.func_229187_a_(180.0F - lvt_25_1_ / 2.0F));
            IVertexBuilder lvt_27_1_ = p_225628_2_.getBuffer(RenderType.func_228634_a_(p_225628_4_.getLocationCape()));
            ((PlayerModel)this.getEntityModel()).func_228289_b_(p_225628_1_, lvt_27_1_, p_225628_3_, OverlayTexture.field_229196_a_);
            p_225628_1_.func_227865_b_();
         }
      }
   }
}
