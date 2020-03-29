package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.model.ElytraModel;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ElytraLayer<T extends LivingEntity, M extends EntityModel<T>> extends LayerRenderer<T, M> {
   private static final ResourceLocation TEXTURE_ELYTRA = new ResourceLocation("textures/entity/elytra.png");
   private final ElytraModel<T> modelElytra = new ElytraModel();

   public ElytraLayer(IEntityRenderer<T, M> p_i50942_1_) {
      super(p_i50942_1_);
   }

   public void func_225628_a_(MatrixStack p_225628_1_, IRenderTypeBuffer p_225628_2_, int p_225628_3_, T p_225628_4_, float p_225628_5_, float p_225628_6_, float p_225628_7_, float p_225628_8_, float p_225628_9_, float p_225628_10_) {
      ItemStack lvt_11_1_ = p_225628_4_.getItemStackFromSlot(EquipmentSlotType.CHEST);
      if (lvt_11_1_.getItem() == Items.ELYTRA) {
         ResourceLocation lvt_12_4_;
         if (p_225628_4_ instanceof AbstractClientPlayerEntity) {
            AbstractClientPlayerEntity lvt_13_1_ = (AbstractClientPlayerEntity)p_225628_4_;
            if (lvt_13_1_.isPlayerInfoSet() && lvt_13_1_.getLocationElytra() != null) {
               lvt_12_4_ = lvt_13_1_.getLocationElytra();
            } else if (lvt_13_1_.hasPlayerInfo() && lvt_13_1_.getLocationCape() != null && lvt_13_1_.isWearing(PlayerModelPart.CAPE)) {
               lvt_12_4_ = lvt_13_1_.getLocationCape();
            } else {
               lvt_12_4_ = TEXTURE_ELYTRA;
            }
         } else {
            lvt_12_4_ = TEXTURE_ELYTRA;
         }

         p_225628_1_.func_227860_a_();
         p_225628_1_.func_227861_a_(0.0D, 0.0D, 0.125D);
         this.getEntityModel().setModelAttributes(this.modelElytra);
         this.modelElytra.func_225597_a_(p_225628_4_, p_225628_5_, p_225628_6_, p_225628_8_, p_225628_9_, p_225628_10_);
         IVertexBuilder lvt_13_2_ = ItemRenderer.func_229113_a_(p_225628_2_, this.modelElytra.func_228282_a_(lvt_12_4_), false, lvt_11_1_.hasEffect());
         this.modelElytra.func_225598_a_(p_225628_1_, lvt_13_2_, p_225628_3_, OverlayTexture.field_229196_a_, 1.0F, 1.0F, 1.0F, 1.0F);
         p_225628_1_.func_227865_b_();
      }
   }
}
