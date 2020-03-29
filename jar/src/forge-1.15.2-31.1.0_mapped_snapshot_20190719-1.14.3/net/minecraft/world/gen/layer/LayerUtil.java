package net.minecraft.world.gen.layer;

import java.util.function.LongFunction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.IExtendedNoiseRandom;
import net.minecraft.world.gen.LazyAreaLayerContext;
import net.minecraft.world.gen.OverworldGenSettings;
import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.area.IAreaFactory;
import net.minecraft.world.gen.area.LazyArea;
import net.minecraft.world.gen.layer.traits.IAreaTransformer1;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.WorldTypeEvent;

public class LayerUtil {
   protected static final int WARM_OCEAN;
   protected static final int LUKEWARM_OCEAN;
   protected static final int OCEAN;
   protected static final int COLD_OCEAN;
   protected static final int FROZEN_OCEAN;
   protected static final int DEEP_WARM_OCEAN;
   protected static final int DEEP_LUKEWARM_OCEAN;
   protected static final int DEEP_OCEAN;
   protected static final int DEEP_COLD_OCEAN;
   protected static final int DEEP_FROZEN_OCEAN;

   public static <T extends IArea, C extends IExtendedNoiseRandom<T>> IAreaFactory<T> repeat(long p_202829_0_, IAreaTransformer1 p_202829_2_, IAreaFactory<T> p_202829_3_, int p_202829_4_, LongFunction<C> p_202829_5_) {
      IAreaFactory<T> iareafactory = p_202829_3_;

      for(int i = 0; i < p_202829_4_; ++i) {
         iareafactory = p_202829_2_.apply((IExtendedNoiseRandom)p_202829_5_.apply(p_202829_0_ + (long)i), iareafactory);
      }

      return iareafactory;
   }

   public static <T extends IArea, C extends IExtendedNoiseRandom<T>> IAreaFactory<T> func_227475_a_(WorldType p_227475_0_, OverworldGenSettings p_227475_1_, LongFunction<C> p_227475_2_) {
      IAreaFactory<T> iareafactory = IslandLayer.INSTANCE.apply((IExtendedNoiseRandom)p_227475_2_.apply(1L));
      iareafactory = ZoomLayer.FUZZY.apply((IExtendedNoiseRandom)p_227475_2_.apply(2000L), iareafactory);
      iareafactory = AddIslandLayer.INSTANCE.apply((IExtendedNoiseRandom)p_227475_2_.apply(1L), iareafactory);
      iareafactory = ZoomLayer.NORMAL.apply((IExtendedNoiseRandom)p_227475_2_.apply(2001L), iareafactory);
      iareafactory = AddIslandLayer.INSTANCE.apply((IExtendedNoiseRandom)p_227475_2_.apply(2L), iareafactory);
      iareafactory = AddIslandLayer.INSTANCE.apply((IExtendedNoiseRandom)p_227475_2_.apply(50L), iareafactory);
      iareafactory = AddIslandLayer.INSTANCE.apply((IExtendedNoiseRandom)p_227475_2_.apply(70L), iareafactory);
      iareafactory = RemoveTooMuchOceanLayer.INSTANCE.apply((IExtendedNoiseRandom)p_227475_2_.apply(2L), iareafactory);
      IAreaFactory<T> iareafactory1 = OceanLayer.INSTANCE.apply((IExtendedNoiseRandom)p_227475_2_.apply(2L));
      iareafactory1 = repeat(2001L, ZoomLayer.NORMAL, iareafactory1, 6, p_227475_2_);
      iareafactory = AddSnowLayer.INSTANCE.apply((IExtendedNoiseRandom)p_227475_2_.apply(2L), iareafactory);
      iareafactory = AddIslandLayer.INSTANCE.apply((IExtendedNoiseRandom)p_227475_2_.apply(3L), iareafactory);
      iareafactory = EdgeLayer.CoolWarm.INSTANCE.apply((IExtendedNoiseRandom)p_227475_2_.apply(2L), iareafactory);
      iareafactory = EdgeLayer.HeatIce.INSTANCE.apply((IExtendedNoiseRandom)p_227475_2_.apply(2L), iareafactory);
      iareafactory = EdgeLayer.Special.INSTANCE.apply((IExtendedNoiseRandom)p_227475_2_.apply(3L), iareafactory);
      iareafactory = ZoomLayer.NORMAL.apply((IExtendedNoiseRandom)p_227475_2_.apply(2002L), iareafactory);
      iareafactory = ZoomLayer.NORMAL.apply((IExtendedNoiseRandom)p_227475_2_.apply(2003L), iareafactory);
      iareafactory = AddIslandLayer.INSTANCE.apply((IExtendedNoiseRandom)p_227475_2_.apply(4L), iareafactory);
      iareafactory = AddMushroomIslandLayer.INSTANCE.apply((IExtendedNoiseRandom)p_227475_2_.apply(5L), iareafactory);
      iareafactory = DeepOceanLayer.INSTANCE.apply((IExtendedNoiseRandom)p_227475_2_.apply(4L), iareafactory);
      iareafactory = repeat(1000L, ZoomLayer.NORMAL, iareafactory, 0, p_227475_2_);
      int i = p_227475_0_ == WorldType.LARGE_BIOMES ? 6 : p_227475_1_.getBiomeSize();
      i = getModdedBiomeSize(p_227475_0_, i);
      int j = p_227475_1_.getRiverSize();
      IAreaFactory<T> lvt_7_1_ = repeat(1000L, ZoomLayer.NORMAL, iareafactory, 0, p_227475_2_);
      lvt_7_1_ = StartRiverLayer.INSTANCE.apply((IExtendedNoiseRandom)p_227475_2_.apply(100L), lvt_7_1_);
      IAreaFactory<T> lvt_8_1_ = p_227475_0_.getBiomeLayer(iareafactory, p_227475_1_, p_227475_2_);
      IAreaFactory<T> lvt_9_1_ = repeat(1000L, ZoomLayer.NORMAL, lvt_7_1_, 2, p_227475_2_);
      lvt_8_1_ = HillsLayer.INSTANCE.apply((IExtendedNoiseRandom)p_227475_2_.apply(1000L), lvt_8_1_, lvt_9_1_);
      lvt_7_1_ = repeat(1000L, ZoomLayer.NORMAL, lvt_7_1_, 2, p_227475_2_);
      lvt_7_1_ = repeat(1000L, ZoomLayer.NORMAL, lvt_7_1_, j, p_227475_2_);
      lvt_7_1_ = RiverLayer.INSTANCE.apply((IExtendedNoiseRandom)p_227475_2_.apply(1L), lvt_7_1_);
      lvt_7_1_ = SmoothLayer.INSTANCE.apply((IExtendedNoiseRandom)p_227475_2_.apply(1000L), lvt_7_1_);
      lvt_8_1_ = RareBiomeLayer.INSTANCE.apply((IExtendedNoiseRandom)p_227475_2_.apply(1001L), lvt_8_1_);

      for(int k = 0; k < i; ++k) {
         lvt_8_1_ = ZoomLayer.NORMAL.apply((IExtendedNoiseRandom)p_227475_2_.apply((long)(1000 + k)), lvt_8_1_);
         if (k == 0) {
            lvt_8_1_ = AddIslandLayer.INSTANCE.apply((IExtendedNoiseRandom)p_227475_2_.apply(3L), lvt_8_1_);
         }

         if (k == 1 || i == 1) {
            lvt_8_1_ = ShoreLayer.INSTANCE.apply((IExtendedNoiseRandom)p_227475_2_.apply(1000L), lvt_8_1_);
         }
      }

      lvt_8_1_ = SmoothLayer.INSTANCE.apply((IExtendedNoiseRandom)p_227475_2_.apply(1000L), lvt_8_1_);
      lvt_8_1_ = MixRiverLayer.INSTANCE.apply((IExtendedNoiseRandom)p_227475_2_.apply(100L), lvt_8_1_, lvt_7_1_);
      lvt_8_1_ = MixOceansLayer.INSTANCE.apply((IExtendedNoiseRandom)p_227475_2_.apply(100L), lvt_8_1_, iareafactory1);
      return lvt_8_1_;
   }

   public static Layer func_227474_a_(long p_227474_0_, WorldType p_227474_2_, OverworldGenSettings p_227474_3_) {
      int i = true;
      IAreaFactory<LazyArea> iareafactory = func_227475_a_(p_227474_2_, p_227474_3_, (p_lambda$func_227474_a_$0_2_) -> {
         return new LazyAreaLayerContext(25, p_227474_0_, p_lambda$func_227474_a_$0_2_);
      });
      return new Layer(iareafactory);
   }

   public static boolean areBiomesSimilar(int p_202826_0_, int p_202826_1_) {
      if (p_202826_0_ == p_202826_1_) {
         return true;
      } else {
         Biome biome = (Biome)Registry.BIOME.getByValue(p_202826_0_);
         Biome biome1 = (Biome)Registry.BIOME.getByValue(p_202826_1_);
         if (biome != null && biome1 != null) {
            if (biome != Biomes.WOODED_BADLANDS_PLATEAU && biome != Biomes.BADLANDS_PLATEAU) {
               if (biome.getCategory() != Biome.Category.NONE && biome1.getCategory() != Biome.Category.NONE && biome.getCategory() == biome1.getCategory()) {
                  return true;
               } else {
                  return biome == biome1;
               }
            } else {
               return biome1 == Biomes.WOODED_BADLANDS_PLATEAU || biome1 == Biomes.BADLANDS_PLATEAU;
            }
         } else {
            return false;
         }
      }
   }

   public static int getModdedBiomeSize(WorldType p_getModdedBiomeSize_0_, int p_getModdedBiomeSize_1_) {
      WorldTypeEvent.BiomeSize event = new WorldTypeEvent.BiomeSize(p_getModdedBiomeSize_0_, p_getModdedBiomeSize_1_);
      MinecraftForge.EVENT_BUS.post(event);
      return event.getNewSize();
   }

   protected static boolean isOcean(int p_202827_0_) {
      return p_202827_0_ == WARM_OCEAN || p_202827_0_ == LUKEWARM_OCEAN || p_202827_0_ == OCEAN || p_202827_0_ == COLD_OCEAN || p_202827_0_ == FROZEN_OCEAN || p_202827_0_ == DEEP_WARM_OCEAN || p_202827_0_ == DEEP_LUKEWARM_OCEAN || p_202827_0_ == DEEP_OCEAN || p_202827_0_ == DEEP_COLD_OCEAN || p_202827_0_ == DEEP_FROZEN_OCEAN;
   }

   protected static boolean isShallowOcean(int p_203631_0_) {
      return p_203631_0_ == WARM_OCEAN || p_203631_0_ == LUKEWARM_OCEAN || p_203631_0_ == OCEAN || p_203631_0_ == COLD_OCEAN || p_203631_0_ == FROZEN_OCEAN;
   }

   static {
      WARM_OCEAN = Registry.BIOME.getId(Biomes.WARM_OCEAN);
      LUKEWARM_OCEAN = Registry.BIOME.getId(Biomes.LUKEWARM_OCEAN);
      OCEAN = Registry.BIOME.getId(Biomes.OCEAN);
      COLD_OCEAN = Registry.BIOME.getId(Biomes.COLD_OCEAN);
      FROZEN_OCEAN = Registry.BIOME.getId(Biomes.FROZEN_OCEAN);
      DEEP_WARM_OCEAN = Registry.BIOME.getId(Biomes.DEEP_WARM_OCEAN);
      DEEP_LUKEWARM_OCEAN = Registry.BIOME.getId(Biomes.DEEP_LUKEWARM_OCEAN);
      DEEP_OCEAN = Registry.BIOME.getId(Biomes.DEEP_OCEAN);
      DEEP_COLD_OCEAN = Registry.BIOME.getId(Biomes.DEEP_COLD_OCEAN);
      DEEP_FROZEN_OCEAN = Registry.BIOME.getId(Biomes.DEEP_FROZEN_OCEAN);
   }
}
