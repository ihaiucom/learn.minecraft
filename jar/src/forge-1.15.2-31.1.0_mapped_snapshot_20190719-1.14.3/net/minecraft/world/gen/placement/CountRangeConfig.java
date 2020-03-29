package net.minecraft.world.gen.placement;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class CountRangeConfig implements IPlacementConfig {
   public final int count;
   public final int bottomOffset;
   public final int topOffset;
   public final int maximum;

   public CountRangeConfig(int p_i48686_1_, int p_i48686_2_, int p_i48686_3_, int p_i48686_4_) {
      this.count = p_i48686_1_;
      this.bottomOffset = p_i48686_2_;
      this.topOffset = p_i48686_3_;
      this.maximum = p_i48686_4_;
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> p_214719_1_) {
      return new Dynamic(p_214719_1_, p_214719_1_.createMap(ImmutableMap.of(p_214719_1_.createString("count"), p_214719_1_.createInt(this.count), p_214719_1_.createString("bottom_offset"), p_214719_1_.createInt(this.bottomOffset), p_214719_1_.createString("top_offset"), p_214719_1_.createInt(this.topOffset), p_214719_1_.createString("maximum"), p_214719_1_.createInt(this.maximum))));
   }

   public static CountRangeConfig deserialize(Dynamic<?> p_214733_0_) {
      int lvt_1_1_ = p_214733_0_.get("count").asInt(0);
      int lvt_2_1_ = p_214733_0_.get("bottom_offset").asInt(0);
      int lvt_3_1_ = p_214733_0_.get("top_offset").asInt(0);
      int lvt_4_1_ = p_214733_0_.get("maximum").asInt(0);
      return new CountRangeConfig(lvt_1_1_, lvt_2_1_, lvt_3_1_, lvt_4_1_);
   }
}
