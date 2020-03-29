package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.layers.HeadLayer;
import net.minecraft.client.renderer.entity.layers.HeldItemLayer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BipedRenderer<T extends MobEntity, M extends BipedModel<T>> extends MobRenderer<T, M> {
   private static final ResourceLocation DEFAULT_RES_LOC = new ResourceLocation("textures/entity/steve.png");

   public BipedRenderer(EntityRendererManager p_i46168_1_, M p_i46168_2_, float p_i46168_3_) {
      super(p_i46168_1_, p_i46168_2_, p_i46168_3_);
      this.addLayer(new HeadLayer(this));
      this.addLayer(new ElytraLayer(this));
      this.addLayer(new HeldItemLayer(this));
   }

   public ResourceLocation getEntityTexture(T p_110775_1_) {
      return DEFAULT_RES_LOC;
   }
}
