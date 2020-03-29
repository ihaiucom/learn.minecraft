package net.minecraftforge.client.event;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraftforge.eventbus.api.Event;

public class RenderWorldLastEvent extends Event {
   private final WorldRenderer context;
   private final MatrixStack mat;
   private final float partialTicks;

   public RenderWorldLastEvent(WorldRenderer context, MatrixStack mat, float partialTicks) {
      this.context = context;
      this.mat = mat;
      this.partialTicks = partialTicks;
   }

   public WorldRenderer getContext() {
      return this.context;
   }

   public MatrixStack getMatrixStack() {
      return this.mat;
   }

   public float getPartialTicks() {
      return this.partialTicks;
   }
}
