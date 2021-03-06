package net.minecraft.util;

import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.Set;

public enum Direction8 {
   NORTH(new Direction[]{Direction.NORTH}),
   NORTH_EAST(new Direction[]{Direction.NORTH, Direction.EAST}),
   EAST(new Direction[]{Direction.EAST}),
   SOUTH_EAST(new Direction[]{Direction.SOUTH, Direction.EAST}),
   SOUTH(new Direction[]{Direction.SOUTH}),
   SOUTH_WEST(new Direction[]{Direction.SOUTH, Direction.WEST}),
   WEST(new Direction[]{Direction.WEST}),
   NORTH_WEST(new Direction[]{Direction.NORTH, Direction.WEST});

   private static final int field_208500_i = 1 << NORTH_WEST.ordinal();
   private static final int field_208501_j = 1 << WEST.ordinal();
   private static final int field_208502_k = 1 << SOUTH_WEST.ordinal();
   private static final int field_208503_l = 1 << SOUTH.ordinal();
   private static final int field_208504_m = 1 << SOUTH_EAST.ordinal();
   private static final int field_208505_n = 1 << EAST.ordinal();
   private static final int field_208506_o = 1 << NORTH_EAST.ordinal();
   private static final int field_208507_p = 1 << NORTH.ordinal();
   private final Set<Direction> directions;

   private Direction8(Direction... p_i47954_3_) {
      this.directions = Sets.immutableEnumSet(Arrays.asList(p_i47954_3_));
   }

   public Set<Direction> getDirections() {
      return this.directions;
   }
}
