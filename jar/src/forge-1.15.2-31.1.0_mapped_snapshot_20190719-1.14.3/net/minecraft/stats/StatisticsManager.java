package net.minecraft.stats;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class StatisticsManager {
   protected final Object2IntMap<Stat<?>> statsData = Object2IntMaps.synchronize(new Object2IntOpenHashMap());

   public StatisticsManager() {
      this.statsData.defaultReturnValue(0);
   }

   public void increment(PlayerEntity p_150871_1_, Stat<?> p_150871_2_, int p_150871_3_) {
      this.setValue(p_150871_1_, p_150871_2_, this.getValue(p_150871_2_) + p_150871_3_);
   }

   public void setValue(PlayerEntity p_150873_1_, Stat<?> p_150873_2_, int p_150873_3_) {
      this.statsData.put(p_150873_2_, p_150873_3_);
   }

   @OnlyIn(Dist.CLIENT)
   public <T> int getValue(StatType<T> p_199060_1_, T p_199060_2_) {
      return p_199060_1_.contains(p_199060_2_) ? this.getValue(p_199060_1_.get(p_199060_2_)) : 0;
   }

   public int getValue(Stat<?> p_77444_1_) {
      return this.statsData.getInt(p_77444_1_);
   }
}
