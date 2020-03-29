package net.minecraft.fluid;

import com.google.common.collect.UnmodifiableIterator;
import java.util.Iterator;
import net.minecraft.util.registry.Registry;

public class Fluids {
   public static final Fluid EMPTY = register("empty", new EmptyFluid());
   public static final FlowingFluid FLOWING_WATER = (FlowingFluid)register("flowing_water", new WaterFluid.Flowing());
   public static final FlowingFluid WATER = (FlowingFluid)register("water", new WaterFluid.Source());
   public static final FlowingFluid FLOWING_LAVA = (FlowingFluid)register("flowing_lava", new LavaFluid.Flowing());
   public static final FlowingFluid LAVA = (FlowingFluid)register("lava", new LavaFluid.Source());

   private static <T extends Fluid> T register(String p_215710_0_, T p_215710_1_) {
      return (Fluid)Registry.register((Registry)Registry.FLUID, (String)p_215710_0_, (Object)p_215710_1_);
   }

   static {
      Iterator var0 = Registry.FLUID.iterator();

      while(var0.hasNext()) {
         Fluid lvt_1_1_ = (Fluid)var0.next();
         UnmodifiableIterator var2 = lvt_1_1_.getStateContainer().getValidStates().iterator();

         while(var2.hasNext()) {
            IFluidState lvt_3_1_ = (IFluidState)var2.next();
            Fluid.STATE_REGISTRY.add(lvt_3_1_);
         }
      }

   }
}
