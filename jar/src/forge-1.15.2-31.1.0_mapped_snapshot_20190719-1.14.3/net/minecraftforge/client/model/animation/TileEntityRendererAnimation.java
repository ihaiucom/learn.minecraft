package net.minecraftforge.client.model.animation;

import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ILightReader;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.ModelDataManager;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.common.animation.Event;
import net.minecraftforge.common.animation.IEventHandler;
import net.minecraftforge.common.model.animation.CapabilityAnimation;
import net.minecraftforge.common.model.animation.IAnimationStateMachine;
import net.minecraftforge.common.property.Properties;
import net.minecraftforge.common.util.LazyOptional;

public class TileEntityRendererAnimation<T extends TileEntity> extends TileEntityRenderer<T> implements IEventHandler<T> {
   protected static BlockRendererDispatcher blockRenderer;

   public TileEntityRendererAnimation(TileEntityRendererDispatcher p_i226006_1_) {
      super(p_i226006_1_);
   }

   public void func_225616_a_(T te, float partialTick, MatrixStack mat, IRenderTypeBuffer renderer, int light, int otherlight) {
      LazyOptional<IAnimationStateMachine> cap = te.getCapability(CapabilityAnimation.ANIMATION_CAPABILITY);
      if (cap.isPresent()) {
         if (blockRenderer == null) {
            blockRenderer = Minecraft.getInstance().getBlockRendererDispatcher();
         }

         BlockPos pos = te.getPos();
         ILightReader world = MinecraftForgeClient.getRegionRenderCache(te.getWorld(), pos);
         BlockState state = world.getBlockState(pos);
         IBakedModel model = blockRenderer.getBlockModelShapes().getModel(state);
         IModelData data = model.getModelData(world, pos, state, ModelDataManager.getModelData(te.getWorld(), pos));
         if (data.hasProperty(Properties.AnimationProperty)) {
            float time = Animation.getWorldTime(Minecraft.getInstance().world, partialTick);
            cap.map((asm) -> {
               return asm.apply(time);
            }).ifPresent((pair) -> {
               this.handleEvents(te, time, (Iterable)pair.getRight());
               data.setData(Properties.AnimationProperty, pair.getLeft());
               blockRenderer.getBlockModelRenderer().renderModel(world, model, state, pos, mat, renderer.getBuffer(Atlases.func_228782_g_()), false, new Random(), 42L, light, data);
            });
         }

      }
   }

   public void handleEvents(T te, float time, Iterable<Event> pastEvents) {
   }
}
