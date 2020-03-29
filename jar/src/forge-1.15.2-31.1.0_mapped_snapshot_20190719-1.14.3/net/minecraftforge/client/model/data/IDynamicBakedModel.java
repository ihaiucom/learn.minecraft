package net.minecraftforge.client.model.data;

import java.util.List;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.util.Direction;

public interface IDynamicBakedModel extends IBakedModel {
   @Nonnull
   default List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand) {
      return this.getQuads(state, side, rand, EmptyModelData.INSTANCE);
   }

   @Nonnull
   List<BakedQuad> getQuads(@Nullable BlockState var1, @Nullable Direction var2, @Nonnull Random var3, @Nonnull IModelData var4);
}
