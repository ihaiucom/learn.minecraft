package net.minecraftforge.client.event;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

@Cancelable
public class DrawHighlightEvent extends Event {
   private final WorldRenderer context;
   private final ActiveRenderInfo info;
   private final RayTraceResult target;
   private final float partialTicks;
   private final MatrixStack matrix;
   private final IRenderTypeBuffer buffers;

   public DrawHighlightEvent(WorldRenderer context, ActiveRenderInfo info, RayTraceResult target, float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffers) {
      this.context = context;
      this.info = info;
      this.target = target;
      this.partialTicks = partialTicks;
      this.matrix = matrix;
      this.buffers = buffers;
   }

   public WorldRenderer getContext() {
      return this.context;
   }

   public ActiveRenderInfo getInfo() {
      return this.info;
   }

   public RayTraceResult getTarget() {
      return this.target;
   }

   public float getPartialTicks() {
      return this.partialTicks;
   }

   public MatrixStack getMatrix() {
      return this.matrix;
   }

   public IRenderTypeBuffer getBuffers() {
      return this.buffers;
   }

   @Cancelable
   public static class HighlightEntity extends DrawHighlightEvent {
      public HighlightEntity(WorldRenderer context, ActiveRenderInfo info, RayTraceResult target, float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffers) {
         super(context, info, target, partialTicks, matrix, buffers);
      }

      public EntityRayTraceResult getTarget() {
         return (EntityRayTraceResult)super.target;
      }
   }

   @Cancelable
   public static class HighlightBlock extends DrawHighlightEvent {
      public HighlightBlock(WorldRenderer context, ActiveRenderInfo info, RayTraceResult target, float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffers) {
         super(context, info, target, partialTicks, matrix, buffers);
      }

      public BlockRayTraceResult getTarget() {
         return (BlockRayTraceResult)super.target;
      }
   }
}
