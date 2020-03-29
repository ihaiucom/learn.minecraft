package net.minecraftforge.client.model;

import com.google.common.base.Objects;
import net.minecraft.client.renderer.TransformationMatrix;
import net.minecraft.client.renderer.model.IModelTransform;

public class ModelTransformComposition implements IModelTransform {
   private final IModelTransform first;
   private final IModelTransform second;
   private final boolean uvLock;

   public ModelTransformComposition(IModelTransform first, IModelTransform second) {
      this(first, second, false);
   }

   public ModelTransformComposition(IModelTransform first, IModelTransform second, boolean uvLock) {
      this.first = first;
      this.second = second;
      this.uvLock = uvLock;
   }

   public boolean isUvLock() {
      return this.uvLock;
   }

   public TransformationMatrix func_225615_b_() {
      return this.first.func_225615_b_().compose(this.second.func_225615_b_());
   }

   public TransformationMatrix getPartTransformation(Object part) {
      return this.first.getPartTransformation(part).compose(this.second.getPartTransformation(part));
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         ModelTransformComposition that = (ModelTransformComposition)o;
         return Objects.equal(this.first, that.first) && Objects.equal(this.second, that.second);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hashCode(new Object[]{this.first, this.second});
   }
}
