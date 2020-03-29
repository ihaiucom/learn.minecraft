package net.minecraft.world.gen.feature.jigsaw;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class JigsawJunction {
   private final int sourceX;
   private final int sourceGroundY;
   private final int sourceZ;
   private final int deltaY;
   private final JigsawPattern.PlacementBehaviour destProjection;

   public JigsawJunction(int p_i51408_1_, int p_i51408_2_, int p_i51408_3_, int p_i51408_4_, JigsawPattern.PlacementBehaviour p_i51408_5_) {
      this.sourceX = p_i51408_1_;
      this.sourceGroundY = p_i51408_2_;
      this.sourceZ = p_i51408_3_;
      this.deltaY = p_i51408_4_;
      this.destProjection = p_i51408_5_;
   }

   public int getSourceX() {
      return this.sourceX;
   }

   public int getSourceGroundY() {
      return this.sourceGroundY;
   }

   public int getSourceZ() {
      return this.sourceZ;
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> p_214897_1_) {
      Builder<T, T> lvt_2_1_ = ImmutableMap.builder();
      lvt_2_1_.put(p_214897_1_.createString("source_x"), p_214897_1_.createInt(this.sourceX)).put(p_214897_1_.createString("source_ground_y"), p_214897_1_.createInt(this.sourceGroundY)).put(p_214897_1_.createString("source_z"), p_214897_1_.createInt(this.sourceZ)).put(p_214897_1_.createString("delta_y"), p_214897_1_.createInt(this.deltaY)).put(p_214897_1_.createString("dest_proj"), p_214897_1_.createString(this.destProjection.func_214936_a()));
      return new Dynamic(p_214897_1_, p_214897_1_.createMap(lvt_2_1_.build()));
   }

   public static <T> JigsawJunction deserialize(Dynamic<T> p_214894_0_) {
      return new JigsawJunction(p_214894_0_.get("source_x").asInt(0), p_214894_0_.get("source_ground_y").asInt(0), p_214894_0_.get("source_z").asInt(0), p_214894_0_.get("delta_y").asInt(0), JigsawPattern.PlacementBehaviour.func_214938_a(p_214894_0_.get("dest_proj").asString("")));
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) {
         JigsawJunction lvt_2_1_ = (JigsawJunction)p_equals_1_;
         if (this.sourceX != lvt_2_1_.sourceX) {
            return false;
         } else if (this.sourceZ != lvt_2_1_.sourceZ) {
            return false;
         } else if (this.deltaY != lvt_2_1_.deltaY) {
            return false;
         } else {
            return this.destProjection == lvt_2_1_.destProjection;
         }
      } else {
         return false;
      }
   }

   public int hashCode() {
      int lvt_1_1_ = this.sourceX;
      lvt_1_1_ = 31 * lvt_1_1_ + this.sourceGroundY;
      lvt_1_1_ = 31 * lvt_1_1_ + this.sourceZ;
      lvt_1_1_ = 31 * lvt_1_1_ + this.deltaY;
      lvt_1_1_ = 31 * lvt_1_1_ + this.destProjection.hashCode();
      return lvt_1_1_;
   }

   public String toString() {
      return "JigsawJunction{sourceX=" + this.sourceX + ", sourceGroundY=" + this.sourceGroundY + ", sourceZ=" + this.sourceZ + ", deltaY=" + this.deltaY + ", destProjection=" + this.destProjection + '}';
   }
}
