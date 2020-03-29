package net.minecraft.world.gen.feature.structure;

import net.minecraft.util.math.MutableBoundingBox;

public abstract class MarginedStructureStart extends StructureStart {
   public MarginedStructureStart(Structure<?> p_i225874_1_, int p_i225874_2_, int p_i225874_3_, MutableBoundingBox p_i225874_4_, int p_i225874_5_, long p_i225874_6_) {
      super(p_i225874_1_, p_i225874_2_, p_i225874_3_, p_i225874_4_, p_i225874_5_, p_i225874_6_);
   }

   protected void recalculateStructureSize() {
      super.recalculateStructureSize();
      int lvt_1_1_ = true;
      MutableBoundingBox var10000 = this.bounds;
      var10000.minX -= 12;
      var10000 = this.bounds;
      var10000.minY -= 12;
      var10000 = this.bounds;
      var10000.minZ -= 12;
      var10000 = this.bounds;
      var10000.maxX += 12;
      var10000 = this.bounds;
      var10000.maxY += 12;
      var10000 = this.bounds;
      var10000.maxZ += 12;
   }
}
