package net.minecraft.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class LocationInput implements ILocationArgument {
   private final LocationPart x;
   private final LocationPart y;
   private final LocationPart z;

   public LocationInput(LocationPart p_i47962_1_, LocationPart p_i47962_2_, LocationPart p_i47962_3_) {
      this.x = p_i47962_1_;
      this.y = p_i47962_2_;
      this.z = p_i47962_3_;
   }

   public Vec3d getPosition(CommandSource p_197281_1_) {
      Vec3d lvt_2_1_ = p_197281_1_.getPos();
      return new Vec3d(this.x.get(lvt_2_1_.x), this.y.get(lvt_2_1_.y), this.z.get(lvt_2_1_.z));
   }

   public Vec2f getRotation(CommandSource p_197282_1_) {
      Vec2f lvt_2_1_ = p_197282_1_.getRotation();
      return new Vec2f((float)this.x.get((double)lvt_2_1_.x), (float)this.y.get((double)lvt_2_1_.y));
   }

   public boolean isXRelative() {
      return this.x.isRelative();
   }

   public boolean isYRelative() {
      return this.y.isRelative();
   }

   public boolean isZRelative() {
      return this.z.isRelative();
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (!(p_equals_1_ instanceof LocationInput)) {
         return false;
      } else {
         LocationInput lvt_2_1_ = (LocationInput)p_equals_1_;
         if (!this.x.equals(lvt_2_1_.x)) {
            return false;
         } else {
            return !this.y.equals(lvt_2_1_.y) ? false : this.z.equals(lvt_2_1_.z);
         }
      }
   }

   public static LocationInput parseInt(StringReader p_200148_0_) throws CommandSyntaxException {
      int lvt_1_1_ = p_200148_0_.getCursor();
      LocationPart lvt_2_1_ = LocationPart.parseInt(p_200148_0_);
      if (p_200148_0_.canRead() && p_200148_0_.peek() == ' ') {
         p_200148_0_.skip();
         LocationPart lvt_3_1_ = LocationPart.parseInt(p_200148_0_);
         if (p_200148_0_.canRead() && p_200148_0_.peek() == ' ') {
            p_200148_0_.skip();
            LocationPart lvt_4_1_ = LocationPart.parseInt(p_200148_0_);
            return new LocationInput(lvt_2_1_, lvt_3_1_, lvt_4_1_);
         } else {
            p_200148_0_.setCursor(lvt_1_1_);
            throw Vec3Argument.POS_INCOMPLETE.createWithContext(p_200148_0_);
         }
      } else {
         p_200148_0_.setCursor(lvt_1_1_);
         throw Vec3Argument.POS_INCOMPLETE.createWithContext(p_200148_0_);
      }
   }

   public static LocationInput parseDouble(StringReader p_200147_0_, boolean p_200147_1_) throws CommandSyntaxException {
      int lvt_2_1_ = p_200147_0_.getCursor();
      LocationPart lvt_3_1_ = LocationPart.parseDouble(p_200147_0_, p_200147_1_);
      if (p_200147_0_.canRead() && p_200147_0_.peek() == ' ') {
         p_200147_0_.skip();
         LocationPart lvt_4_1_ = LocationPart.parseDouble(p_200147_0_, false);
         if (p_200147_0_.canRead() && p_200147_0_.peek() == ' ') {
            p_200147_0_.skip();
            LocationPart lvt_5_1_ = LocationPart.parseDouble(p_200147_0_, p_200147_1_);
            return new LocationInput(lvt_3_1_, lvt_4_1_, lvt_5_1_);
         } else {
            p_200147_0_.setCursor(lvt_2_1_);
            throw Vec3Argument.POS_INCOMPLETE.createWithContext(p_200147_0_);
         }
      } else {
         p_200147_0_.setCursor(lvt_2_1_);
         throw Vec3Argument.POS_INCOMPLETE.createWithContext(p_200147_0_);
      }
   }

   public static LocationInput current() {
      return new LocationInput(new LocationPart(true, 0.0D), new LocationPart(true, 0.0D), new LocationPart(true, 0.0D));
   }

   public int hashCode() {
      int lvt_1_1_ = this.x.hashCode();
      lvt_1_1_ = 31 * lvt_1_1_ + this.y.hashCode();
      lvt_1_1_ = 31 * lvt_1_1_ + this.z.hashCode();
      return lvt_1_1_;
   }
}
