package net.minecraft.client.renderer.entity.model;

import java.util.function.Function;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class EntityModel<T extends Entity> extends Model {
   public float swingProgress;
   public boolean isSitting;
   public boolean isChild;

   protected EntityModel() {
      this(RenderType::func_228640_c_);
   }

   protected EntityModel(Function<ResourceLocation, RenderType> p_i225945_1_) {
      super(p_i225945_1_);
      this.isChild = true;
   }

   public abstract void func_225597_a_(T var1, float var2, float var3, float var4, float var5, float var6);

   public void setLivingAnimations(T p_212843_1_, float p_212843_2_, float p_212843_3_, float p_212843_4_) {
   }

   public void setModelAttributes(EntityModel<T> p_217111_1_) {
      p_217111_1_.swingProgress = this.swingProgress;
      p_217111_1_.isSitting = this.isSitting;
      p_217111_1_.isChild = this.isChild;
   }
}
