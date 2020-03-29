package net.minecraft.world.gen.layer.traits;

import net.minecraft.world.gen.IExtendedNoiseRandom;
import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.area.IAreaFactory;

public interface IAreaTransformer2 extends IDimTransformer {
   default <R extends IArea> IAreaFactory<R> apply(IExtendedNoiseRandom<R> p_202707_1_, IAreaFactory<R> p_202707_2_, IAreaFactory<R> p_202707_3_) {
      return () -> {
         R lvt_4_1_ = p_202707_2_.make();
         R lvt_5_1_ = p_202707_3_.make();
         return p_202707_1_.makeArea((p_215724_4_, p_215724_5_) -> {
            p_202707_1_.setPosition((long)p_215724_4_, (long)p_215724_5_);
            return this.apply(p_202707_1_, lvt_4_1_, lvt_5_1_, p_215724_4_, p_215724_5_);
         }, lvt_4_1_, lvt_5_1_);
      };
   }

   int apply(INoiseRandom var1, IArea var2, IArea var3, int var4, int var5);
}
