package net.minecraft.client.renderer.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class Model implements Consumer<ModelRenderer> {
   protected final Function<ResourceLocation, RenderType> field_228281_q_;
   public int textureWidth = 64;
   public int textureHeight = 32;

   public Model(Function<ResourceLocation, RenderType> p_i225947_1_) {
      this.field_228281_q_ = p_i225947_1_;
   }

   public void accept(ModelRenderer p_accept_1_) {
   }

   public final RenderType func_228282_a_(ResourceLocation p_228282_1_) {
      return (RenderType)this.field_228281_q_.apply(p_228282_1_);
   }

   public abstract void func_225598_a_(MatrixStack var1, IVertexBuilder var2, int var3, int var4, float var5, float var6, float var7, float var8);

   // $FF: synthetic method
   public void accept(Object p_accept_1_) {
      this.accept((ModelRenderer)p_accept_1_);
   }
}
