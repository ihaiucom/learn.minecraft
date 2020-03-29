package net.minecraftforge.client.extensions;

import net.minecraft.client.renderer.TransformationMatrix;

public interface IForgeModelTransform {
   default TransformationMatrix getPartTransformation(Object part) {
      return TransformationMatrix.func_227983_a_();
   }
}
