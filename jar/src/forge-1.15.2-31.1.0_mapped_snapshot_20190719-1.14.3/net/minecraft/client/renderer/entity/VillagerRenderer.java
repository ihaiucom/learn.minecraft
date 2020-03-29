package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.layers.CrossedArmsItemLayer;
import net.minecraft.client.renderer.entity.layers.HeadLayer;
import net.minecraft.client.renderer.entity.layers.VillagerLevelPendantLayer;
import net.minecraft.client.renderer.entity.model.VillagerModel;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VillagerRenderer extends MobRenderer<VillagerEntity, VillagerModel<VillagerEntity>> {
   private static final ResourceLocation field_217779_a = new ResourceLocation("textures/entity/villager/villager.png");

   public VillagerRenderer(EntityRendererManager p_i50954_1_, IReloadableResourceManager p_i50954_2_) {
      super(p_i50954_1_, new VillagerModel(0.0F), 0.5F);
      this.addLayer(new HeadLayer(this));
      this.addLayer(new VillagerLevelPendantLayer(this, p_i50954_2_, "villager"));
      this.addLayer(new CrossedArmsItemLayer(this));
   }

   public ResourceLocation getEntityTexture(VillagerEntity p_110775_1_) {
      return field_217779_a;
   }

   protected void func_225620_a_(VillagerEntity p_225620_1_, MatrixStack p_225620_2_, float p_225620_3_) {
      float lvt_4_1_ = 0.9375F;
      if (p_225620_1_.isChild()) {
         lvt_4_1_ = (float)((double)lvt_4_1_ * 0.5D);
         this.shadowSize = 0.25F;
      } else {
         this.shadowSize = 0.5F;
      }

      p_225620_2_.func_227862_a_(lvt_4_1_, lvt_4_1_, lvt_4_1_);
   }
}
