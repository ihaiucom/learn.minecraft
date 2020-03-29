package net.minecraft.advancements.criterion;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.server.ServerWorld;

public class LocationPredicate {
   public static final LocationPredicate ANY;
   private final MinMaxBounds.FloatBound x;
   private final MinMaxBounds.FloatBound y;
   private final MinMaxBounds.FloatBound z;
   @Nullable
   private final Biome biome;
   @Nullable
   private final Structure<?> feature;
   @Nullable
   private final DimensionType dimension;
   private final LightPredicate field_226864_h_;
   private final BlockPredicate field_226865_i_;
   private final FluidPredicate field_226866_j_;

   public LocationPredicate(MinMaxBounds.FloatBound p_i225755_1_, MinMaxBounds.FloatBound p_i225755_2_, MinMaxBounds.FloatBound p_i225755_3_, @Nullable Biome p_i225755_4_, @Nullable Structure<?> p_i225755_5_, @Nullable DimensionType p_i225755_6_, LightPredicate p_i225755_7_, BlockPredicate p_i225755_8_, FluidPredicate p_i225755_9_) {
      this.x = p_i225755_1_;
      this.y = p_i225755_2_;
      this.z = p_i225755_3_;
      this.biome = p_i225755_4_;
      this.feature = p_i225755_5_;
      this.dimension = p_i225755_6_;
      this.field_226864_h_ = p_i225755_7_;
      this.field_226865_i_ = p_i225755_8_;
      this.field_226866_j_ = p_i225755_9_;
   }

   public static LocationPredicate forBiome(Biome p_204010_0_) {
      return new LocationPredicate(MinMaxBounds.FloatBound.UNBOUNDED, MinMaxBounds.FloatBound.UNBOUNDED, MinMaxBounds.FloatBound.UNBOUNDED, p_204010_0_, (Structure)null, (DimensionType)null, LightPredicate.field_226854_a_, BlockPredicate.field_226231_a_, FluidPredicate.field_226643_a_);
   }

   public static LocationPredicate forDimension(DimensionType p_204008_0_) {
      return new LocationPredicate(MinMaxBounds.FloatBound.UNBOUNDED, MinMaxBounds.FloatBound.UNBOUNDED, MinMaxBounds.FloatBound.UNBOUNDED, (Biome)null, (Structure)null, p_204008_0_, LightPredicate.field_226854_a_, BlockPredicate.field_226231_a_, FluidPredicate.field_226643_a_);
   }

   public static LocationPredicate forFeature(Structure<?> p_218020_0_) {
      return new LocationPredicate(MinMaxBounds.FloatBound.UNBOUNDED, MinMaxBounds.FloatBound.UNBOUNDED, MinMaxBounds.FloatBound.UNBOUNDED, (Biome)null, p_218020_0_, (DimensionType)null, LightPredicate.field_226854_a_, BlockPredicate.field_226231_a_, FluidPredicate.field_226643_a_);
   }

   public boolean test(ServerWorld p_193452_1_, double p_193452_2_, double p_193452_4_, double p_193452_6_) {
      return this.test(p_193452_1_, (float)p_193452_2_, (float)p_193452_4_, (float)p_193452_6_);
   }

   public boolean test(ServerWorld p_193453_1_, float p_193453_2_, float p_193453_3_, float p_193453_4_) {
      if (!this.x.test(p_193453_2_)) {
         return false;
      } else if (!this.y.test(p_193453_3_)) {
         return false;
      } else if (!this.z.test(p_193453_4_)) {
         return false;
      } else if (this.dimension != null && this.dimension != p_193453_1_.dimension.getType()) {
         return false;
      } else {
         BlockPos lvt_5_1_ = new BlockPos((double)p_193453_2_, (double)p_193453_3_, (double)p_193453_4_);
         boolean lvt_6_1_ = p_193453_1_.isBlockPresent(lvt_5_1_);
         if (this.biome != null && (!lvt_6_1_ || this.biome != p_193453_1_.func_226691_t_(lvt_5_1_))) {
            return false;
         } else if (this.feature != null && (!lvt_6_1_ || !this.feature.isPositionInsideStructure(p_193453_1_, lvt_5_1_))) {
            return false;
         } else if (!this.field_226864_h_.func_226858_a_(p_193453_1_, lvt_5_1_)) {
            return false;
         } else if (!this.field_226865_i_.func_226238_a_(p_193453_1_, lvt_5_1_)) {
            return false;
         } else {
            return this.field_226866_j_.func_226649_a_(p_193453_1_, lvt_5_1_);
         }
      }
   }

   public JsonElement serialize() {
      if (this == ANY) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject lvt_1_1_ = new JsonObject();
         if (!this.x.isUnbounded() || !this.y.isUnbounded() || !this.z.isUnbounded()) {
            JsonObject lvt_2_1_ = new JsonObject();
            lvt_2_1_.add("x", this.x.serialize());
            lvt_2_1_.add("y", this.y.serialize());
            lvt_2_1_.add("z", this.z.serialize());
            lvt_1_1_.add("position", lvt_2_1_);
         }

         if (this.dimension != null) {
            lvt_1_1_.addProperty("dimension", DimensionType.getKey(this.dimension).toString());
         }

         if (this.feature != null) {
            lvt_1_1_.addProperty("feature", (String)Feature.STRUCTURES.inverse().get(this.feature));
         }

         if (this.biome != null) {
            lvt_1_1_.addProperty("biome", Registry.BIOME.getKey(this.biome).toString());
         }

         lvt_1_1_.add("light", this.field_226864_h_.func_226856_a_());
         lvt_1_1_.add("block", this.field_226865_i_.func_226236_a_());
         lvt_1_1_.add("fluid", this.field_226866_j_.func_226647_a_());
         return lvt_1_1_;
      }
   }

   public static LocationPredicate deserialize(@Nullable JsonElement p_193454_0_) {
      if (p_193454_0_ != null && !p_193454_0_.isJsonNull()) {
         JsonObject lvt_1_1_ = JSONUtils.getJsonObject(p_193454_0_, "location");
         JsonObject lvt_2_1_ = JSONUtils.getJsonObject(lvt_1_1_, "position", new JsonObject());
         MinMaxBounds.FloatBound lvt_3_1_ = MinMaxBounds.FloatBound.fromJson(lvt_2_1_.get("x"));
         MinMaxBounds.FloatBound lvt_4_1_ = MinMaxBounds.FloatBound.fromJson(lvt_2_1_.get("y"));
         MinMaxBounds.FloatBound lvt_5_1_ = MinMaxBounds.FloatBound.fromJson(lvt_2_1_.get("z"));
         DimensionType lvt_6_1_ = lvt_1_1_.has("dimension") ? DimensionType.byName(new ResourceLocation(JSONUtils.getString(lvt_1_1_, "dimension"))) : null;
         Structure<?> lvt_7_1_ = lvt_1_1_.has("feature") ? (Structure)Feature.STRUCTURES.get(JSONUtils.getString(lvt_1_1_, "feature")) : null;
         Biome lvt_8_1_ = null;
         if (lvt_1_1_.has("biome")) {
            ResourceLocation lvt_9_1_ = new ResourceLocation(JSONUtils.getString(lvt_1_1_, "biome"));
            lvt_8_1_ = (Biome)Registry.BIOME.getValue(lvt_9_1_).orElseThrow(() -> {
               return new JsonSyntaxException("Unknown biome '" + lvt_9_1_ + "'");
            });
         }

         LightPredicate lvt_9_2_ = LightPredicate.func_226857_a_(lvt_1_1_.get("light"));
         BlockPredicate lvt_10_1_ = BlockPredicate.func_226237_a_(lvt_1_1_.get("block"));
         FluidPredicate lvt_11_1_ = FluidPredicate.func_226648_a_(lvt_1_1_.get("fluid"));
         return new LocationPredicate(lvt_3_1_, lvt_4_1_, lvt_5_1_, lvt_8_1_, lvt_7_1_, lvt_6_1_, lvt_9_2_, lvt_10_1_, lvt_11_1_);
      } else {
         return ANY;
      }
   }

   static {
      ANY = new LocationPredicate(MinMaxBounds.FloatBound.UNBOUNDED, MinMaxBounds.FloatBound.UNBOUNDED, MinMaxBounds.FloatBound.UNBOUNDED, (Biome)null, (Structure)null, (DimensionType)null, LightPredicate.field_226854_a_, BlockPredicate.field_226231_a_, FluidPredicate.field_226643_a_);
   }

   public static class Builder {
      private MinMaxBounds.FloatBound x;
      private MinMaxBounds.FloatBound y;
      private MinMaxBounds.FloatBound z;
      @Nullable
      private Biome biome;
      @Nullable
      private Structure<?> feature;
      @Nullable
      private DimensionType dimension;
      private LightPredicate field_226867_g_;
      private BlockPredicate field_226868_h_;
      private FluidPredicate field_226869_i_;

      public Builder() {
         this.x = MinMaxBounds.FloatBound.UNBOUNDED;
         this.y = MinMaxBounds.FloatBound.UNBOUNDED;
         this.z = MinMaxBounds.FloatBound.UNBOUNDED;
         this.field_226867_g_ = LightPredicate.field_226854_a_;
         this.field_226868_h_ = BlockPredicate.field_226231_a_;
         this.field_226869_i_ = FluidPredicate.field_226643_a_;
      }

      public static LocationPredicate.Builder func_226870_a_() {
         return new LocationPredicate.Builder();
      }

      public LocationPredicate.Builder biome(@Nullable Biome p_218012_1_) {
         this.biome = p_218012_1_;
         return this;
      }

      public LocationPredicate build() {
         return new LocationPredicate(this.x, this.y, this.z, this.biome, this.feature, this.dimension, this.field_226867_g_, this.field_226868_h_, this.field_226869_i_);
      }
   }
}
