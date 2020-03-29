package net.minecraftforge.client.model;

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.renderer.TransformationMatrix;
import net.minecraft.client.renderer.model.IModelTransform;

public final class SimpleModelTransform implements IModelTransform {
   public static final SimpleModelTransform IDENTITY = new SimpleModelTransform(TransformationMatrix.func_227983_a_());
   private final ImmutableMap<?, TransformationMatrix> map;
   private final TransformationMatrix base;

   public SimpleModelTransform(ImmutableMap<?, TransformationMatrix> map) {
      this(map, TransformationMatrix.func_227983_a_());
   }

   public SimpleModelTransform(TransformationMatrix base) {
      this(ImmutableMap.of(), base);
   }

   public SimpleModelTransform(ImmutableMap<?, TransformationMatrix> map, TransformationMatrix base) {
      this.map = map;
      this.base = base;
   }

   public TransformationMatrix func_225615_b_() {
      return this.base;
   }

   public TransformationMatrix getPartTransformation(Object part) {
      return (TransformationMatrix)this.map.getOrDefault(part, TransformationMatrix.func_227983_a_());
   }
}
