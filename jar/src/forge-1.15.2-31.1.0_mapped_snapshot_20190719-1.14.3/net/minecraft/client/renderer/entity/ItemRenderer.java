package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.Random;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ItemRenderer extends EntityRenderer<ItemEntity> {
   private final net.minecraft.client.renderer.ItemRenderer itemRenderer;
   private final Random random = new Random();

   public ItemRenderer(EntityRendererManager p_i46167_1_, net.minecraft.client.renderer.ItemRenderer p_i46167_2_) {
      super(p_i46167_1_);
      this.itemRenderer = p_i46167_2_;
      this.shadowSize = 0.15F;
      this.shadowOpaque = 0.75F;
   }

   protected int getModelCount(ItemStack p_177078_1_) {
      int i = 1;
      if (p_177078_1_.getCount() > 48) {
         i = 5;
      } else if (p_177078_1_.getCount() > 32) {
         i = 4;
      } else if (p_177078_1_.getCount() > 16) {
         i = 3;
      } else if (p_177078_1_.getCount() > 1) {
         i = 2;
      }

      return i;
   }

   public void func_225623_a_(ItemEntity p_225623_1_, float p_225623_2_, float p_225623_3_, MatrixStack p_225623_4_, IRenderTypeBuffer p_225623_5_, int p_225623_6_) {
      p_225623_4_.func_227860_a_();
      ItemStack itemstack = p_225623_1_.getItem();
      int i = itemstack.isEmpty() ? 187 : Item.getIdFromItem(itemstack.getItem()) + itemstack.getDamage();
      this.random.setSeed((long)i);
      IBakedModel ibakedmodel = this.itemRenderer.getItemModelWithOverrides(itemstack, p_225623_1_.world, (LivingEntity)null);
      boolean flag = ibakedmodel.isGui3d();
      int j = this.getModelCount(itemstack);
      float f = 0.25F;
      float f1 = this.shouldBob() ? MathHelper.sin(((float)p_225623_1_.getAge() + p_225623_3_) / 10.0F + p_225623_1_.hoverStart) * 0.1F + 0.1F : 0.0F;
      float f2 = ibakedmodel.getItemCameraTransforms().getTransform(ItemCameraTransforms.TransformType.GROUND).scale.getY();
      p_225623_4_.func_227861_a_(0.0D, (double)(f1 + 0.25F * f2), 0.0D);
      float f3 = ((float)p_225623_1_.getAge() + p_225623_3_) / 20.0F + p_225623_1_.hoverStart;
      p_225623_4_.func_227863_a_(Vector3f.field_229181_d_.func_229193_c_(f3));
      float f11;
      float f13;
      if (!flag) {
         float f7 = -0.0F * (float)(j - 1) * 0.5F;
         f11 = -0.0F * (float)(j - 1) * 0.5F;
         f13 = -0.09375F * (float)(j - 1) * 0.5F;
         p_225623_4_.func_227861_a_((double)f7, (double)f11, (double)f13);
      }

      for(int k = 0; k < j; ++k) {
         p_225623_4_.func_227860_a_();
         if (k > 0) {
            if (flag) {
               f11 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
               f13 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
               float f10 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
               p_225623_4_.func_227861_a_(this.shouldSpreadItems() ? (double)f11 : 0.0D, this.shouldSpreadItems() ? (double)f13 : 0.0D, this.shouldSpreadItems() ? (double)f10 : 0.0D);
            } else {
               f11 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
               f13 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
               p_225623_4_.func_227861_a_(this.shouldSpreadItems() ? (double)f11 : 0.0D, this.shouldSpreadItems() ? (double)f13 : 0.0D, 0.0D);
            }
         }

         this.itemRenderer.func_229111_a_(itemstack, ItemCameraTransforms.TransformType.GROUND, false, p_225623_4_, p_225623_5_, p_225623_6_, OverlayTexture.field_229196_a_, ibakedmodel);
         p_225623_4_.func_227865_b_();
         if (!flag) {
            p_225623_4_.func_227861_a_(0.0D, 0.0D, 0.09375D);
         }
      }

      p_225623_4_.func_227865_b_();
      super.func_225623_a_(p_225623_1_, p_225623_2_, p_225623_3_, p_225623_4_, p_225623_5_, p_225623_6_);
   }

   public ResourceLocation getEntityTexture(ItemEntity p_110775_1_) {
      return AtlasTexture.LOCATION_BLOCKS_TEXTURE;
   }

   public boolean shouldSpreadItems() {
      return true;
   }

   public boolean shouldBob() {
      return true;
   }
}
