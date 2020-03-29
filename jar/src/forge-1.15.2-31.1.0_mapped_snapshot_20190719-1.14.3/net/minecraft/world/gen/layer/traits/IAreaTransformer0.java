package net.minecraft.world.gen.layer.traits;

import net.minecraft.world.gen.IExtendedNoiseRandom;
import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.area.IAreaFactory;

public interface IAreaTransformer0 {
   default <R extends IArea> IAreaFactory<R> apply(IExtendedNoiseRandom<R> p_202823_1_) {
      return () -> {
         return p_202823_1_.func_212861_a_((p_202820_2_, p_202820_3_) -> {
            p_202823_1_.setPosition((long)p_202820_2_, (long)p_202820_3_);
            return this.apply(p_202823_1_, p_202820_2_, p_202820_3_);
         });
      };
   }

   int apply(INoiseRandom var1, int var2, int var3);
}
