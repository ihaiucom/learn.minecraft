package net.minecraft.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Objects;
import net.minecraft.command.CommandSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class LocalLocationArgument implements ILocationArgument {
   private final double left;
   private final double up;
   private final double forwards;

   public LocalLocationArgument(double p_i48240_1_, double p_i48240_3_, double p_i48240_5_) {
      this.left = p_i48240_1_;
      this.up = p_i48240_3_;
      this.forwards = p_i48240_5_;
   }

   public Vec3d getPosition(CommandSource p_197281_1_) {
      Vec2f lvt_2_1_ = p_197281_1_.getRotation();
      Vec3d lvt_3_1_ = p_197281_1_.getEntityAnchorType().apply(p_197281_1_);
      float lvt_4_1_ = MathHelper.cos((lvt_2_1_.y + 90.0F) * 0.017453292F);
      float lvt_5_1_ = MathHelper.sin((lvt_2_1_.y + 90.0F) * 0.017453292F);
      float lvt_6_1_ = MathHelper.cos(-lvt_2_1_.x * 0.017453292F);
      float lvt_7_1_ = MathHelper.sin(-lvt_2_1_.x * 0.017453292F);
      float lvt_8_1_ = MathHelper.cos((-lvt_2_1_.x + 90.0F) * 0.017453292F);
      float lvt_9_1_ = MathHelper.sin((-lvt_2_1_.x + 90.0F) * 0.017453292F);
      Vec3d lvt_10_1_ = new Vec3d((double)(lvt_4_1_ * lvt_6_1_), (double)lvt_7_1_, (double)(lvt_5_1_ * lvt_6_1_));
      Vec3d lvt_11_1_ = new Vec3d((double)(lvt_4_1_ * lvt_8_1_), (double)lvt_9_1_, (double)(lvt_5_1_ * lvt_8_1_));
      Vec3d lvt_12_1_ = lvt_10_1_.crossProduct(lvt_11_1_).scale(-1.0D);
      double lvt_13_1_ = lvt_10_1_.x * this.forwards + lvt_11_1_.x * this.up + lvt_12_1_.x * this.left;
      double lvt_15_1_ = lvt_10_1_.y * this.forwards + lvt_11_1_.y * this.up + lvt_12_1_.y * this.left;
      double lvt_17_1_ = lvt_10_1_.z * this.forwards + lvt_11_1_.z * this.up + lvt_12_1_.z * this.left;
      return new Vec3d(lvt_3_1_.x + lvt_13_1_, lvt_3_1_.y + lvt_15_1_, lvt_3_1_.z + lvt_17_1_);
   }

   public Vec2f getRotation(CommandSource p_197282_1_) {
      return Vec2f.ZERO;
   }

   public boolean isXRelative() {
      return true;
   }

   public boolean isYRelative() {
      return true;
   }

   public boolean isZRelative() {
      return true;
   }

   public static LocalLocationArgument parse(StringReader p_200142_0_) throws CommandSyntaxException {
      int lvt_1_1_ = p_200142_0_.getCursor();
      double lvt_2_1_ = parseCoord(p_200142_0_, lvt_1_1_);
      if (p_200142_0_.canRead() && p_200142_0_.peek() == ' ') {
         p_200142_0_.skip();
         double lvt_4_1_ = parseCoord(p_200142_0_, lvt_1_1_);
         if (p_200142_0_.canRead() && p_200142_0_.peek() == ' ') {
            p_200142_0_.skip();
            double lvt_6_1_ = parseCoord(p_200142_0_, lvt_1_1_);
            return new LocalLocationArgument(lvt_2_1_, lvt_4_1_, lvt_6_1_);
         } else {
            p_200142_0_.setCursor(lvt_1_1_);
            throw Vec3Argument.POS_INCOMPLETE.createWithContext(p_200142_0_);
         }
      } else {
         p_200142_0_.setCursor(lvt_1_1_);
         throw Vec3Argument.POS_INCOMPLETE.createWithContext(p_200142_0_);
      }
   }

   private static double parseCoord(StringReader p_200143_0_, int p_200143_1_) throws CommandSyntaxException {
      if (!p_200143_0_.canRead()) {
         throw LocationPart.EXPECTED_DOUBLE.createWithContext(p_200143_0_);
      } else if (p_200143_0_.peek() != '^') {
         p_200143_0_.setCursor(p_200143_1_);
         throw Vec3Argument.POS_MIXED_TYPES.createWithContext(p_200143_0_);
      } else {
         p_200143_0_.skip();
         return p_200143_0_.canRead() && p_200143_0_.peek() != ' ' ? p_200143_0_.readDouble() : 0.0D;
      }
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (!(p_equals_1_ instanceof LocalLocationArgument)) {
         return false;
      } else {
         LocalLocationArgument lvt_2_1_ = (LocalLocationArgument)p_equals_1_;
         return this.left == lvt_2_1_.left && this.up == lvt_2_1_.up && this.forwards == lvt_2_1_.forwards;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.left, this.up, this.forwards});
   }
}
