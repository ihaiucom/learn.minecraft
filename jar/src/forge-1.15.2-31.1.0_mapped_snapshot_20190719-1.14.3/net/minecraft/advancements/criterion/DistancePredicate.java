package net.minecraft.advancements.criterion;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.math.MathHelper;

public class DistancePredicate {
   public static final DistancePredicate ANY;
   private final MinMaxBounds.FloatBound x;
   private final MinMaxBounds.FloatBound y;
   private final MinMaxBounds.FloatBound z;
   private final MinMaxBounds.FloatBound horizontal;
   private final MinMaxBounds.FloatBound absolute;

   public DistancePredicate(MinMaxBounds.FloatBound p_i49724_1_, MinMaxBounds.FloatBound p_i49724_2_, MinMaxBounds.FloatBound p_i49724_3_, MinMaxBounds.FloatBound p_i49724_4_, MinMaxBounds.FloatBound p_i49724_5_) {
      this.x = p_i49724_1_;
      this.y = p_i49724_2_;
      this.z = p_i49724_3_;
      this.horizontal = p_i49724_4_;
      this.absolute = p_i49724_5_;
   }

   public static DistancePredicate forHorizontal(MinMaxBounds.FloatBound p_203995_0_) {
      return new DistancePredicate(MinMaxBounds.FloatBound.UNBOUNDED, MinMaxBounds.FloatBound.UNBOUNDED, MinMaxBounds.FloatBound.UNBOUNDED, p_203995_0_, MinMaxBounds.FloatBound.UNBOUNDED);
   }

   public static DistancePredicate forVertical(MinMaxBounds.FloatBound p_203993_0_) {
      return new DistancePredicate(MinMaxBounds.FloatBound.UNBOUNDED, p_203993_0_, MinMaxBounds.FloatBound.UNBOUNDED, MinMaxBounds.FloatBound.UNBOUNDED, MinMaxBounds.FloatBound.UNBOUNDED);
   }

   public boolean test(double p_193422_1_, double p_193422_3_, double p_193422_5_, double p_193422_7_, double p_193422_9_, double p_193422_11_) {
      float lvt_13_1_ = (float)(p_193422_1_ - p_193422_7_);
      float lvt_14_1_ = (float)(p_193422_3_ - p_193422_9_);
      float lvt_15_1_ = (float)(p_193422_5_ - p_193422_11_);
      if (this.x.test(MathHelper.abs(lvt_13_1_)) && this.y.test(MathHelper.abs(lvt_14_1_)) && this.z.test(MathHelper.abs(lvt_15_1_))) {
         if (!this.horizontal.testSquared((double)(lvt_13_1_ * lvt_13_1_ + lvt_15_1_ * lvt_15_1_))) {
            return false;
         } else {
            return this.absolute.testSquared((double)(lvt_13_1_ * lvt_13_1_ + lvt_14_1_ * lvt_14_1_ + lvt_15_1_ * lvt_15_1_));
         }
      } else {
         return false;
      }
   }

   public static DistancePredicate deserialize(@Nullable JsonElement p_193421_0_) {
      if (p_193421_0_ != null && !p_193421_0_.isJsonNull()) {
         JsonObject lvt_1_1_ = JSONUtils.getJsonObject(p_193421_0_, "distance");
         MinMaxBounds.FloatBound lvt_2_1_ = MinMaxBounds.FloatBound.fromJson(lvt_1_1_.get("x"));
         MinMaxBounds.FloatBound lvt_3_1_ = MinMaxBounds.FloatBound.fromJson(lvt_1_1_.get("y"));
         MinMaxBounds.FloatBound lvt_4_1_ = MinMaxBounds.FloatBound.fromJson(lvt_1_1_.get("z"));
         MinMaxBounds.FloatBound lvt_5_1_ = MinMaxBounds.FloatBound.fromJson(lvt_1_1_.get("horizontal"));
         MinMaxBounds.FloatBound lvt_6_1_ = MinMaxBounds.FloatBound.fromJson(lvt_1_1_.get("absolute"));
         return new DistancePredicate(lvt_2_1_, lvt_3_1_, lvt_4_1_, lvt_5_1_, lvt_6_1_);
      } else {
         return ANY;
      }
   }

   public JsonElement serialize() {
      if (this == ANY) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject lvt_1_1_ = new JsonObject();
         lvt_1_1_.add("x", this.x.serialize());
         lvt_1_1_.add("y", this.y.serialize());
         lvt_1_1_.add("z", this.z.serialize());
         lvt_1_1_.add("horizontal", this.horizontal.serialize());
         lvt_1_1_.add("absolute", this.absolute.serialize());
         return lvt_1_1_;
      }
   }

   static {
      ANY = new DistancePredicate(MinMaxBounds.FloatBound.UNBOUNDED, MinMaxBounds.FloatBound.UNBOUNDED, MinMaxBounds.FloatBound.UNBOUNDED, MinMaxBounds.FloatBound.UNBOUNDED, MinMaxBounds.FloatBound.UNBOUNDED);
   }
}
