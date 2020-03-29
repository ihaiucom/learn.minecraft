package net.minecraft.world.gen.layer.traits;

import net.minecraft.world.gen.IExtendedNoiseRandom;
import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.area.IAreaFactory;

public interface IAreaTransformer1 extends IDimTransformer {
   default <R extends IArea> IAreaFactory<R> apply(IExtendedNoiseRandom<R> p_202713_1_, IAreaFactory<R> p_202713_2_) {
      return () -> {
         R lvt_3_1_ = p_202713_2_.make();
         return p_202713_1_.func_212859_a_((p_202711_3_, p_202711_4_) -> {
            p_202713_1_.setPosition((long)p_202711_3_, (long)p_202711_4_);
            return this.func_215728_a(p_202713_1_, lvt_3_1_, p_202711_3_, p_202711_4_);
         }, lvt_3_1_);
      };
   }

   int func_215728_a(IExtendedNoiseRandom<?> var1, IArea var2, int var3, int var4);
}
