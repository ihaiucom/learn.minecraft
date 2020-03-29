package net.minecraftforge.client.event;

import com.mojang.blaze3d.matrix.MatrixStack;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

@Cancelable
public class RenderHandEvent extends Event {
   private final Hand hand;
   private final MatrixStack mat;
   private final IRenderTypeBuffer buffers;
   private final int light;
   private final float partialTicks;
   private final float interpolatedPitch;
   private final float swingProgress;
   private final float equipProgress;
   @Nonnull
   private final ItemStack stack;

   public RenderHandEvent(Hand hand, MatrixStack mat, IRenderTypeBuffer buffers, int light, float partialTicks, float interpolatedPitch, float swingProgress, float equipProgress, @Nonnull ItemStack stack) {
      this.hand = hand;
      this.mat = mat;
      this.buffers = buffers;
      this.light = light;
      this.partialTicks = partialTicks;
      this.interpolatedPitch = interpolatedPitch;
      this.swingProgress = swingProgress;
      this.equipProgress = equipProgress;
      this.stack = stack;
   }

   public Hand getHand() {
      return this.hand;
   }

   public MatrixStack getMatrixStack() {
      return this.mat;
   }

   public IRenderTypeBuffer getBuffers() {
      return this.buffers;
   }

   public int getLight() {
      return this.light;
   }

   public float getPartialTicks() {
      return this.partialTicks;
   }

   public float getInterpolatedPitch() {
      return this.interpolatedPitch;
   }

   public float getSwingProgress() {
      return this.swingProgress;
   }

   public float getEquipProgress() {
      return this.equipProgress;
   }

   @Nonnull
   public ItemStack getItemStack() {
      return this.stack;
   }
}
