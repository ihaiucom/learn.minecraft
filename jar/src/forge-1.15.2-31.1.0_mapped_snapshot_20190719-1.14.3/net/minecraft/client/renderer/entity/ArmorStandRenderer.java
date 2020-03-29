package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.layers.BipedArmorLayer;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.layers.HeadLayer;
import net.minecraft.client.renderer.entity.layers.HeldItemLayer;
import net.minecraft.client.renderer.entity.model.ArmorStandArmorModel;
import net.minecraft.client.renderer.entity.model.ArmorStandModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ArmorStandRenderer extends LivingRenderer<ArmorStandEntity, ArmorStandArmorModel> {
   public static final ResourceLocation TEXTURE_ARMOR_STAND = new ResourceLocation("textures/entity/armorstand/wood.png");

   public ArmorStandRenderer(EntityRendererManager p_i46195_1_) {
      super(p_i46195_1_, new ArmorStandModel(), 0.0F);
      this.addLayer(new BipedArmorLayer(this, new ArmorStandArmorModel(0.5F), new ArmorStandArmorModel(1.0F)));
      this.addLayer(new HeldItemLayer(this));
      this.addLayer(new ElytraLayer(this));
      this.addLayer(new HeadLayer(this));
   }

   public ResourceLocation getEntityTexture(ArmorStandEntity p_110775_1_) {
      return TEXTURE_ARMOR_STAND;
   }

   protected void func_225621_a_(ArmorStandEntity p_225621_1_, MatrixStack p_225621_2_, float p_225621_3_, float p_225621_4_, float p_225621_5_) {
      p_225621_2_.func_227863_a_(Vector3f.field_229181_d_.func_229187_a_(180.0F - p_225621_4_));
      float lvt_6_1_ = (float)(p_225621_1_.world.getGameTime() - p_225621_1_.punchCooldown) + p_225621_5_;
      if (lvt_6_1_ < 5.0F) {
         p_225621_2_.func_227863_a_(Vector3f.field_229181_d_.func_229187_a_(MathHelper.sin(lvt_6_1_ / 1.5F * 3.1415927F) * 3.0F));
      }

   }

   protected boolean canRenderName(ArmorStandEntity p_177070_1_) {
      double lvt_2_1_ = this.renderManager.func_229099_b_(p_177070_1_);
      float lvt_4_1_ = p_177070_1_.isCrouching() ? 32.0F : 64.0F;
      return lvt_2_1_ >= (double)(lvt_4_1_ * lvt_4_1_) ? false : p_177070_1_.isCustomNameVisible();
   }

   @Nullable
   protected RenderType func_230042_a_(ArmorStandEntity p_230042_1_, boolean p_230042_2_, boolean p_230042_3_) {
      if (!p_230042_1_.hasMarker()) {
         return super.func_230042_a_(p_230042_1_, p_230042_2_, p_230042_3_);
      } else {
         ResourceLocation lvt_4_1_ = this.getEntityTexture(p_230042_1_);
         if (p_230042_3_) {
            return RenderType.func_230168_b_(lvt_4_1_, false);
         } else {
            return p_230042_2_ ? RenderType.func_230167_a_(lvt_4_1_, false) : null;
         }
      }
   }

   // $FF: synthetic method
   protected boolean canRenderName(LivingEntity p_177070_1_) {
      return this.canRenderName((ArmorStandEntity)p_177070_1_);
   }
}
