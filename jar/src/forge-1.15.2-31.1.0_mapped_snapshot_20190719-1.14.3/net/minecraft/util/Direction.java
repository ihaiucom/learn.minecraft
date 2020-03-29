package net.minecraft.util;

import com.google.common.collect.Iterators;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.Vector4f;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public enum Direction implements IStringSerializable {
   DOWN(0, 1, -1, "down", Direction.AxisDirection.NEGATIVE, Direction.Axis.Y, new Vec3i(0, -1, 0)),
   UP(1, 0, -1, "up", Direction.AxisDirection.POSITIVE, Direction.Axis.Y, new Vec3i(0, 1, 0)),
   NORTH(2, 3, 2, "north", Direction.AxisDirection.NEGATIVE, Direction.Axis.Z, new Vec3i(0, 0, -1)),
   SOUTH(3, 2, 0, "south", Direction.AxisDirection.POSITIVE, Direction.Axis.Z, new Vec3i(0, 0, 1)),
   WEST(4, 5, 1, "west", Direction.AxisDirection.NEGATIVE, Direction.Axis.X, new Vec3i(-1, 0, 0)),
   EAST(5, 4, 3, "east", Direction.AxisDirection.POSITIVE, Direction.Axis.X, new Vec3i(1, 0, 0));

   private final int index;
   private final int opposite;
   private final int horizontalIndex;
   private final String name;
   private final Direction.Axis axis;
   private final Direction.AxisDirection axisDirection;
   private final Vec3i directionVec;
   private static final Direction[] VALUES = values();
   private static final Map<String, Direction> NAME_LOOKUP = (Map)Arrays.stream(VALUES).collect(Collectors.toMap(Direction::getName2, (p_lambda$static$0_0_) -> {
      return p_lambda$static$0_0_;
   }));
   private static final Direction[] BY_INDEX = (Direction[])Arrays.stream(VALUES).sorted(Comparator.comparingInt((p_lambda$static$1_0_) -> {
      return p_lambda$static$1_0_.index;
   })).toArray((p_lambda$static$2_0_) -> {
      return new Direction[p_lambda$static$2_0_];
   });
   private static final Direction[] BY_HORIZONTAL_INDEX = (Direction[])Arrays.stream(VALUES).filter((p_lambda$static$3_0_) -> {
      return p_lambda$static$3_0_.getAxis().isHorizontal();
   }).sorted(Comparator.comparingInt((p_lambda$static$4_0_) -> {
      return p_lambda$static$4_0_.horizontalIndex;
   })).toArray((p_lambda$static$5_0_) -> {
      return new Direction[p_lambda$static$5_0_];
   });
   private static final Long2ObjectMap<Direction> field_218387_r = (Long2ObjectMap)Arrays.stream(VALUES).collect(Collectors.toMap((p_lambda$static$6_0_) -> {
      return (new BlockPos(p_lambda$static$6_0_.getDirectionVec())).toLong();
   }, (p_lambda$static$7_0_) -> {
      return p_lambda$static$7_0_;
   }, (p_lambda$static$8_0_, p_lambda$static$8_1_) -> {
      throw new IllegalArgumentException("Duplicate keys");
   }, Long2ObjectOpenHashMap::new));

   private Direction(int p_i46016_3_, int p_i46016_4_, int p_i46016_5_, String p_i46016_6_, Direction.AxisDirection p_i46016_7_, Direction.Axis p_i46016_8_, Vec3i p_i46016_9_) {
      this.index = p_i46016_3_;
      this.horizontalIndex = p_i46016_5_;
      this.opposite = p_i46016_4_;
      this.name = p_i46016_6_;
      this.axis = p_i46016_8_;
      this.axisDirection = p_i46016_7_;
      this.directionVec = p_i46016_9_;
   }

   public static Direction[] getFacingDirections(Entity p_196054_0_) {
      float f = p_196054_0_.getPitch(1.0F) * 0.017453292F;
      float f1 = -p_196054_0_.getYaw(1.0F) * 0.017453292F;
      float f2 = MathHelper.sin(f);
      float f3 = MathHelper.cos(f);
      float f4 = MathHelper.sin(f1);
      float f5 = MathHelper.cos(f1);
      boolean flag = f4 > 0.0F;
      boolean flag1 = f2 < 0.0F;
      boolean flag2 = f5 > 0.0F;
      float f6 = flag ? f4 : -f4;
      float f7 = flag1 ? -f2 : f2;
      float f8 = flag2 ? f5 : -f5;
      float f9 = f6 * f3;
      float f10 = f8 * f3;
      Direction direction = flag ? EAST : WEST;
      Direction direction1 = flag1 ? UP : DOWN;
      Direction direction2 = flag2 ? SOUTH : NORTH;
      if (f6 > f8) {
         if (f7 > f9) {
            return compose(direction1, direction, direction2);
         } else {
            return f10 > f7 ? compose(direction, direction2, direction1) : compose(direction, direction1, direction2);
         }
      } else if (f7 > f10) {
         return compose(direction1, direction2, direction);
      } else {
         return f9 > f7 ? compose(direction2, direction, direction1) : compose(direction2, direction1, direction);
      }
   }

   private static Direction[] compose(Direction p_196053_0_, Direction p_196053_1_, Direction p_196053_2_) {
      return new Direction[]{p_196053_0_, p_196053_1_, p_196053_2_, p_196053_2_.getOpposite(), p_196053_1_.getOpposite(), p_196053_0_.getOpposite()};
   }

   @OnlyIn(Dist.CLIENT)
   public static Direction func_229385_a_(Matrix4f p_229385_0_, Direction p_229385_1_) {
      Vec3i vec3i = p_229385_1_.getDirectionVec();
      Vector4f vector4f = new Vector4f((float)vec3i.getX(), (float)vec3i.getY(), (float)vec3i.getZ(), 0.0F);
      vector4f.func_229372_a_(p_229385_0_);
      return getFacingFromVector(vector4f.getX(), vector4f.getY(), vector4f.getZ());
   }

   @OnlyIn(Dist.CLIENT)
   public Quaternion func_229384_a_() {
      Quaternion quaternion = Vector3f.field_229179_b_.func_229187_a_(90.0F);
      switch(this) {
      case DOWN:
         return Vector3f.field_229179_b_.func_229187_a_(180.0F);
      case UP:
         return Quaternion.field_227060_a_.func_227068_g_();
      case NORTH:
         quaternion.multiply(Vector3f.field_229183_f_.func_229187_a_(180.0F));
         return quaternion;
      case SOUTH:
         return quaternion;
      case WEST:
         quaternion.multiply(Vector3f.field_229183_f_.func_229187_a_(90.0F));
         return quaternion;
      case EAST:
      default:
         quaternion.multiply(Vector3f.field_229183_f_.func_229187_a_(-90.0F));
         return quaternion;
      }
   }

   public int getIndex() {
      return this.index;
   }

   public int getHorizontalIndex() {
      return this.horizontalIndex;
   }

   public Direction.AxisDirection getAxisDirection() {
      return this.axisDirection;
   }

   public Direction getOpposite() {
      return byIndex(this.opposite);
   }

   public Direction rotateY() {
      switch(this) {
      case NORTH:
         return EAST;
      case SOUTH:
         return WEST;
      case WEST:
         return NORTH;
      case EAST:
         return SOUTH;
      default:
         throw new IllegalStateException("Unable to get Y-rotated facing of " + this);
      }
   }

   public Direction rotateYCCW() {
      switch(this) {
      case NORTH:
         return WEST;
      case SOUTH:
         return EAST;
      case WEST:
         return SOUTH;
      case EAST:
         return NORTH;
      default:
         throw new IllegalStateException("Unable to get CCW facing of " + this);
      }
   }

   public int getXOffset() {
      return this.directionVec.getX();
   }

   public int getYOffset() {
      return this.directionVec.getY();
   }

   public int getZOffset() {
      return this.directionVec.getZ();
   }

   @OnlyIn(Dist.CLIENT)
   public Vector3f func_229386_k_() {
      return new Vector3f((float)this.getXOffset(), (float)this.getYOffset(), (float)this.getZOffset());
   }

   public String getName2() {
      return this.name;
   }

   public Direction.Axis getAxis() {
      return this.axis;
   }

   @Nullable
   public static Direction byName(@Nullable String p_176739_0_) {
      return p_176739_0_ == null ? null : (Direction)NAME_LOOKUP.get(p_176739_0_.toLowerCase(Locale.ROOT));
   }

   public static Direction byIndex(int p_82600_0_) {
      return BY_INDEX[MathHelper.abs(p_82600_0_ % BY_INDEX.length)];
   }

   public static Direction byHorizontalIndex(int p_176731_0_) {
      return BY_HORIZONTAL_INDEX[MathHelper.abs(p_176731_0_ % BY_HORIZONTAL_INDEX.length)];
   }

   @Nullable
   public static Direction func_218383_a(int p_218383_0_, int p_218383_1_, int p_218383_2_) {
      return (Direction)field_218387_r.get(BlockPos.pack(p_218383_0_, p_218383_1_, p_218383_2_));
   }

   public static Direction fromAngle(double p_176733_0_) {
      return byHorizontalIndex(MathHelper.floor(p_176733_0_ / 90.0D + 0.5D) & 3);
   }

   public static Direction getFacingFromAxisDirection(Direction.Axis p_211699_0_, Direction.AxisDirection p_211699_1_) {
      switch(p_211699_0_) {
      case X:
         return p_211699_1_ == Direction.AxisDirection.POSITIVE ? EAST : WEST;
      case Y:
         return p_211699_1_ == Direction.AxisDirection.POSITIVE ? UP : DOWN;
      case Z:
      default:
         return p_211699_1_ == Direction.AxisDirection.POSITIVE ? SOUTH : NORTH;
      }
   }

   public float getHorizontalAngle() {
      return (float)((this.horizontalIndex & 3) * 90);
   }

   public static Direction random(Random p_176741_0_) {
      return values()[p_176741_0_.nextInt(values().length)];
   }

   public static Direction getFacingFromVector(double p_210769_0_, double p_210769_2_, double p_210769_4_) {
      return getFacingFromVector((float)p_210769_0_, (float)p_210769_2_, (float)p_210769_4_);
   }

   public static Direction getFacingFromVector(float p_176737_0_, float p_176737_1_, float p_176737_2_) {
      Direction direction = NORTH;
      float f = Float.MIN_VALUE;
      Direction[] var5 = VALUES;
      int var6 = var5.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         Direction direction1 = var5[var7];
         float f1 = p_176737_0_ * (float)direction1.directionVec.getX() + p_176737_1_ * (float)direction1.directionVec.getY() + p_176737_2_ * (float)direction1.directionVec.getZ();
         if (f1 > f) {
            f = f1;
            direction = direction1;
         }
      }

      return direction;
   }

   public String toString() {
      return this.name;
   }

   public String getName() {
      return this.name;
   }

   public static Direction getFacingFromAxis(Direction.AxisDirection p_181076_0_, Direction.Axis p_181076_1_) {
      Direction[] var2 = values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Direction direction = var2[var4];
         if (direction.getAxisDirection() == p_181076_0_ && direction.getAxis() == p_181076_1_) {
            return direction;
         }
      }

      throw new IllegalArgumentException("No such direction: " + p_181076_0_ + " " + p_181076_1_);
   }

   public Vec3i getDirectionVec() {
      return this.directionVec;
   }

   public static enum Plane implements Iterable<Direction>, Predicate<Direction> {
      HORIZONTAL(new Direction[]{Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST}, new Direction.Axis[]{Direction.Axis.X, Direction.Axis.Z}),
      VERTICAL(new Direction[]{Direction.UP, Direction.DOWN}, new Direction.Axis[]{Direction.Axis.Y});

      private final Direction[] facingValues;
      private final Direction.Axis[] axisValues;

      private Plane(Direction[] p_i49393_3_, Direction.Axis[] p_i49393_4_) {
         this.facingValues = p_i49393_3_;
         this.axisValues = p_i49393_4_;
      }

      public Direction random(Random p_179518_1_) {
         return this.facingValues[p_179518_1_.nextInt(this.facingValues.length)];
      }

      public boolean test(@Nullable Direction p_test_1_) {
         return p_test_1_ != null && p_test_1_.getAxis().getPlane() == this;
      }

      public Iterator<Direction> iterator() {
         return Iterators.forArray(this.facingValues);
      }
   }

   public static enum AxisDirection {
      POSITIVE(1, "Towards positive"),
      NEGATIVE(-1, "Towards negative");

      private final int offset;
      private final String description;

      private AxisDirection(int p_i46014_3_, String p_i46014_4_) {
         this.offset = p_i46014_3_;
         this.description = p_i46014_4_;
      }

      public int getOffset() {
         return this.offset;
      }

      public String toString() {
         return this.description;
      }
   }

   public static enum Axis implements IStringSerializable, Predicate<Direction> {
      X("x") {
         public int getCoordinate(int p_196052_1_, int p_196052_2_, int p_196052_3_) {
            return p_196052_1_;
         }

         public double getCoordinate(double p_196051_1_, double p_196051_3_, double p_196051_5_) {
            return p_196051_1_;
         }
      },
      Y("y") {
         public int getCoordinate(int p_196052_1_, int p_196052_2_, int p_196052_3_) {
            return p_196052_2_;
         }

         public double getCoordinate(double p_196051_1_, double p_196051_3_, double p_196051_5_) {
            return p_196051_3_;
         }
      },
      Z("z") {
         public int getCoordinate(int p_196052_1_, int p_196052_2_, int p_196052_3_) {
            return p_196052_3_;
         }

         public double getCoordinate(double p_196051_1_, double p_196051_3_, double p_196051_5_) {
            return p_196051_5_;
         }
      };

      private static final Map<String, Direction.Axis> NAME_LOOKUP = (Map)Arrays.stream(values()).collect(Collectors.toMap(Direction.Axis::getName2, (p_lambda$static$0_0_) -> {
         return p_lambda$static$0_0_;
      }));
      private final String name;

      private Axis(String p_i49394_3_) {
         this.name = p_i49394_3_;
      }

      @Nullable
      public static Direction.Axis byName(String p_176717_0_) {
         return (Direction.Axis)NAME_LOOKUP.get(p_176717_0_.toLowerCase(Locale.ROOT));
      }

      public String getName2() {
         return this.name;
      }

      public boolean isVertical() {
         return this == Y;
      }

      public boolean isHorizontal() {
         return this == X || this == Z;
      }

      public String toString() {
         return this.name;
      }

      public static Direction.Axis random(Random p_218393_0_) {
         return values()[p_218393_0_.nextInt(values().length)];
      }

      public boolean test(@Nullable Direction p_test_1_) {
         return p_test_1_ != null && p_test_1_.getAxis() == this;
      }

      public Direction.Plane getPlane() {
         switch(this) {
         case X:
         case Z:
            return Direction.Plane.HORIZONTAL;
         case Y:
            return Direction.Plane.VERTICAL;
         default:
            throw new Error("Someone's been tampering with the universe!");
         }
      }

      public String getName() {
         return this.name;
      }

      public abstract int getCoordinate(int var1, int var2, int var3);

      public abstract double getCoordinate(double var1, double var3, double var5);

      // $FF: synthetic method
      Axis(String p_i49395_3_, Object p_i49395_4_) {
         this(p_i49395_3_);
      }
   }
}
