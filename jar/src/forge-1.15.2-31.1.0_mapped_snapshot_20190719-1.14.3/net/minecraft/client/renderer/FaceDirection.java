package net.minecraft.client.renderer;

import net.minecraft.util.Direction;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public enum FaceDirection {
   DOWN(new FaceDirection.VertexInformation[]{new FaceDirection.VertexInformation(FaceDirection.Constants.WEST_INDEX, FaceDirection.Constants.DOWN_INDEX, FaceDirection.Constants.SOUTH_INDEX), new FaceDirection.VertexInformation(FaceDirection.Constants.WEST_INDEX, FaceDirection.Constants.DOWN_INDEX, FaceDirection.Constants.NORTH_INDEX), new FaceDirection.VertexInformation(FaceDirection.Constants.EAST_INDEX, FaceDirection.Constants.DOWN_INDEX, FaceDirection.Constants.NORTH_INDEX), new FaceDirection.VertexInformation(FaceDirection.Constants.EAST_INDEX, FaceDirection.Constants.DOWN_INDEX, FaceDirection.Constants.SOUTH_INDEX)}),
   UP(new FaceDirection.VertexInformation[]{new FaceDirection.VertexInformation(FaceDirection.Constants.WEST_INDEX, FaceDirection.Constants.UP_INDEX, FaceDirection.Constants.NORTH_INDEX), new FaceDirection.VertexInformation(FaceDirection.Constants.WEST_INDEX, FaceDirection.Constants.UP_INDEX, FaceDirection.Constants.SOUTH_INDEX), new FaceDirection.VertexInformation(FaceDirection.Constants.EAST_INDEX, FaceDirection.Constants.UP_INDEX, FaceDirection.Constants.SOUTH_INDEX), new FaceDirection.VertexInformation(FaceDirection.Constants.EAST_INDEX, FaceDirection.Constants.UP_INDEX, FaceDirection.Constants.NORTH_INDEX)}),
   NORTH(new FaceDirection.VertexInformation[]{new FaceDirection.VertexInformation(FaceDirection.Constants.EAST_INDEX, FaceDirection.Constants.UP_INDEX, FaceDirection.Constants.NORTH_INDEX), new FaceDirection.VertexInformation(FaceDirection.Constants.EAST_INDEX, FaceDirection.Constants.DOWN_INDEX, FaceDirection.Constants.NORTH_INDEX), new FaceDirection.VertexInformation(FaceDirection.Constants.WEST_INDEX, FaceDirection.Constants.DOWN_INDEX, FaceDirection.Constants.NORTH_INDEX), new FaceDirection.VertexInformation(FaceDirection.Constants.WEST_INDEX, FaceDirection.Constants.UP_INDEX, FaceDirection.Constants.NORTH_INDEX)}),
   SOUTH(new FaceDirection.VertexInformation[]{new FaceDirection.VertexInformation(FaceDirection.Constants.WEST_INDEX, FaceDirection.Constants.UP_INDEX, FaceDirection.Constants.SOUTH_INDEX), new FaceDirection.VertexInformation(FaceDirection.Constants.WEST_INDEX, FaceDirection.Constants.DOWN_INDEX, FaceDirection.Constants.SOUTH_INDEX), new FaceDirection.VertexInformation(FaceDirection.Constants.EAST_INDEX, FaceDirection.Constants.DOWN_INDEX, FaceDirection.Constants.SOUTH_INDEX), new FaceDirection.VertexInformation(FaceDirection.Constants.EAST_INDEX, FaceDirection.Constants.UP_INDEX, FaceDirection.Constants.SOUTH_INDEX)}),
   WEST(new FaceDirection.VertexInformation[]{new FaceDirection.VertexInformation(FaceDirection.Constants.WEST_INDEX, FaceDirection.Constants.UP_INDEX, FaceDirection.Constants.NORTH_INDEX), new FaceDirection.VertexInformation(FaceDirection.Constants.WEST_INDEX, FaceDirection.Constants.DOWN_INDEX, FaceDirection.Constants.NORTH_INDEX), new FaceDirection.VertexInformation(FaceDirection.Constants.WEST_INDEX, FaceDirection.Constants.DOWN_INDEX, FaceDirection.Constants.SOUTH_INDEX), new FaceDirection.VertexInformation(FaceDirection.Constants.WEST_INDEX, FaceDirection.Constants.UP_INDEX, FaceDirection.Constants.SOUTH_INDEX)}),
   EAST(new FaceDirection.VertexInformation[]{new FaceDirection.VertexInformation(FaceDirection.Constants.EAST_INDEX, FaceDirection.Constants.UP_INDEX, FaceDirection.Constants.SOUTH_INDEX), new FaceDirection.VertexInformation(FaceDirection.Constants.EAST_INDEX, FaceDirection.Constants.DOWN_INDEX, FaceDirection.Constants.SOUTH_INDEX), new FaceDirection.VertexInformation(FaceDirection.Constants.EAST_INDEX, FaceDirection.Constants.DOWN_INDEX, FaceDirection.Constants.NORTH_INDEX), new FaceDirection.VertexInformation(FaceDirection.Constants.EAST_INDEX, FaceDirection.Constants.UP_INDEX, FaceDirection.Constants.NORTH_INDEX)});

   private static final FaceDirection[] FACINGS = (FaceDirection[])Util.make(new FaceDirection[6], (p_209235_0_) -> {
      p_209235_0_[FaceDirection.Constants.DOWN_INDEX] = DOWN;
      p_209235_0_[FaceDirection.Constants.UP_INDEX] = UP;
      p_209235_0_[FaceDirection.Constants.NORTH_INDEX] = NORTH;
      p_209235_0_[FaceDirection.Constants.SOUTH_INDEX] = SOUTH;
      p_209235_0_[FaceDirection.Constants.WEST_INDEX] = WEST;
      p_209235_0_[FaceDirection.Constants.EAST_INDEX] = EAST;
   });
   private final FaceDirection.VertexInformation[] vertexInfos;

   public static FaceDirection getFacing(Direction p_179027_0_) {
      return FACINGS[p_179027_0_.getIndex()];
   }

   private FaceDirection(FaceDirection.VertexInformation... p_i46272_3_) {
      this.vertexInfos = p_i46272_3_;
   }

   public FaceDirection.VertexInformation getVertexInformation(int p_179025_1_) {
      return this.vertexInfos[p_179025_1_];
   }

   @OnlyIn(Dist.CLIENT)
   public static class VertexInformation {
      public final int xIndex;
      public final int yIndex;
      public final int zIndex;

      private VertexInformation(int p_i46270_1_, int p_i46270_2_, int p_i46270_3_) {
         this.xIndex = p_i46270_1_;
         this.yIndex = p_i46270_2_;
         this.zIndex = p_i46270_3_;
      }

      // $FF: synthetic method
      VertexInformation(int p_i46271_1_, int p_i46271_2_, int p_i46271_3_, Object p_i46271_4_) {
         this(p_i46271_1_, p_i46271_2_, p_i46271_3_);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static final class Constants {
      public static final int SOUTH_INDEX;
      public static final int UP_INDEX;
      public static final int EAST_INDEX;
      public static final int NORTH_INDEX;
      public static final int DOWN_INDEX;
      public static final int WEST_INDEX;

      static {
         SOUTH_INDEX = Direction.SOUTH.getIndex();
         UP_INDEX = Direction.UP.getIndex();
         EAST_INDEX = Direction.EAST.getIndex();
         NORTH_INDEX = Direction.NORTH.getIndex();
         DOWN_INDEX = Direction.DOWN.getIndex();
         WEST_INDEX = Direction.WEST.getIndex();
      }
   }
}
