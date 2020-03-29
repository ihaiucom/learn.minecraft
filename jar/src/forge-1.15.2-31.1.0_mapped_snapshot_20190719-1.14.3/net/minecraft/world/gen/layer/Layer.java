package net.minecraft.world.gen.layer;

import net.minecraft.util.SharedConstants;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.area.IAreaFactory;
import net.minecraft.world.gen.area.LazyArea;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Layer {
   private static final Logger LOGGER = LogManager.getLogger();
   private final LazyArea field_215742_b;

   public Layer(IAreaFactory<LazyArea> p_i48639_1_) {
      this.field_215742_b = (LazyArea)p_i48639_1_.make();
   }

   private Biome func_215739_a(int p_215739_1_) {
      Biome lvt_2_1_ = (Biome)Registry.BIOME.getByValue(p_215739_1_);
      if (lvt_2_1_ == null) {
         if (SharedConstants.developmentMode) {
            throw (IllegalStateException)Util.func_229757_c_(new IllegalStateException("Unknown biome id: " + p_215739_1_));
         } else {
            LOGGER.warn("Unknown biome id: ", p_215739_1_);
            return Biomes.DEFAULT;
         }
      } else {
         return lvt_2_1_;
      }
   }

   public Biome func_215738_a(int p_215738_1_, int p_215738_2_) {
      return this.func_215739_a(this.field_215742_b.getValue(p_215738_1_, p_215738_2_));
   }
}
