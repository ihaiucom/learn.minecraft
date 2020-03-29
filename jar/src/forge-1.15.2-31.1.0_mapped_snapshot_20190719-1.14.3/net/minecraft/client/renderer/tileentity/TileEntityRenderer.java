package net.minecraft.client.renderer.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class TileEntityRenderer<T extends TileEntity> {
   protected final TileEntityRendererDispatcher field_228858_b_;

   public TileEntityRenderer(TileEntityRendererDispatcher p_i226006_1_) {
      this.field_228858_b_ = p_i226006_1_;
   }

   public abstract void func_225616_a_(T var1, float var2, MatrixStack var3, IRenderTypeBuffer var4, int var5, int var6);

   public boolean isGlobalRenderer(T p_188185_1_) {
      return false;
   }
}
