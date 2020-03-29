package net.minecraftforge.client.event;

import com.mojang.blaze3d.matrix.MatrixStack;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.ItemFrameRenderer;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

@Cancelable
public class RenderItemInFrameEvent extends Event {
   private final ItemStack item;
   private final ItemFrameEntity entityItemFrame;
   private final ItemFrameRenderer renderer;
   private final MatrixStack matrix;
   private final IRenderTypeBuffer buffers;
   private final int light;

   public RenderItemInFrameEvent(ItemFrameEntity itemFrame, ItemFrameRenderer renderItemFrame, MatrixStack matrix, IRenderTypeBuffer buffers, int light) {
      this.item = itemFrame.getDisplayedItem();
      this.entityItemFrame = itemFrame;
      this.renderer = renderItemFrame;
      this.matrix = matrix;
      this.buffers = buffers;
      this.light = light;
   }

   @Nonnull
   public ItemStack getItem() {
      return this.item;
   }

   public ItemFrameEntity getEntityItemFrame() {
      return this.entityItemFrame;
   }

   public ItemFrameRenderer getRenderer() {
      return this.renderer;
   }

   public MatrixStack getMatrix() {
      return this.matrix;
   }

   public IRenderTypeBuffer getBuffers() {
      return this.buffers;
   }

   public int getLight() {
      return this.light;
   }
}
