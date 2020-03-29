package net.minecraftforge.client.event;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

public abstract class RenderLivingEvent<T extends LivingEntity, M extends EntityModel<T>> extends Event {
   private final LivingEntity entity;
   private final LivingRenderer<T, M> renderer;
   private final float partialRenderTick;
   private final MatrixStack matrixStack;
   private final IRenderTypeBuffer buffers;
   private final int light;

   public RenderLivingEvent(LivingEntity entity, LivingRenderer<T, M> renderer, float partialRenderTick, MatrixStack matrixStack, IRenderTypeBuffer buffers, int light) {
      this.entity = entity;
      this.renderer = renderer;
      this.partialRenderTick = partialRenderTick;
      this.matrixStack = matrixStack;
      this.buffers = buffers;
      this.light = light;
   }

   public LivingEntity getEntity() {
      return this.entity;
   }

   public LivingRenderer<T, M> getRenderer() {
      return this.renderer;
   }

   public float getPartialRenderTick() {
      return this.partialRenderTick;
   }

   public MatrixStack getMatrixStack() {
      return this.matrixStack;
   }

   public IRenderTypeBuffer getBuffers() {
      return this.buffers;
   }

   public int getLight() {
      return this.light;
   }

   public static class Post<T extends LivingEntity, M extends EntityModel<T>> extends RenderLivingEvent<T, M> {
      public Post(LivingEntity entity, LivingRenderer<T, M> renderer, float partialRenderTick, MatrixStack matrixStack, IRenderTypeBuffer buffers, int light) {
         super(entity, renderer, partialRenderTick, matrixStack, buffers, light);
      }
   }

   @Cancelable
   public static class Pre<T extends LivingEntity, M extends EntityModel<T>> extends RenderLivingEvent<T, M> {
      public Pre(LivingEntity entity, LivingRenderer<T, M> renderer, float partialRenderTick, MatrixStack matrixStack, IRenderTypeBuffer buffers, int light) {
         super(entity, renderer, partialRenderTick, matrixStack, buffers, light);
      }
   }
}
