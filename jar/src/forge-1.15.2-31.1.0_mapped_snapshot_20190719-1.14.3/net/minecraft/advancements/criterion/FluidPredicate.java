package net.minecraft.advancements.criterion;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.server.ServerWorld;

public class FluidPredicate {
   public static final FluidPredicate field_226643_a_;
   @Nullable
   private final Tag<Fluid> field_226644_b_;
   @Nullable
   private final Fluid field_226645_c_;
   private final StatePropertiesPredicate field_226646_d_;

   public FluidPredicate(@Nullable Tag<Fluid> p_i225738_1_, @Nullable Fluid p_i225738_2_, StatePropertiesPredicate p_i225738_3_) {
      this.field_226644_b_ = p_i225738_1_;
      this.field_226645_c_ = p_i225738_2_;
      this.field_226646_d_ = p_i225738_3_;
   }

   public boolean func_226649_a_(ServerWorld p_226649_1_, BlockPos p_226649_2_) {
      if (this == field_226643_a_) {
         return true;
      } else if (!p_226649_1_.isBlockPresent(p_226649_2_)) {
         return false;
      } else {
         IFluidState lvt_3_1_ = p_226649_1_.getFluidState(p_226649_2_);
         Fluid lvt_4_1_ = lvt_3_1_.getFluid();
         if (this.field_226644_b_ != null && !this.field_226644_b_.contains(lvt_4_1_)) {
            return false;
         } else if (this.field_226645_c_ != null && lvt_4_1_ != this.field_226645_c_) {
            return false;
         } else {
            return this.field_226646_d_.func_227185_a_(lvt_3_1_);
         }
      }
   }

   public static FluidPredicate func_226648_a_(@Nullable JsonElement p_226648_0_) {
      if (p_226648_0_ != null && !p_226648_0_.isJsonNull()) {
         JsonObject lvt_1_1_ = JSONUtils.getJsonObject(p_226648_0_, "fluid");
         Fluid lvt_2_1_ = null;
         if (lvt_1_1_.has("fluid")) {
            ResourceLocation lvt_3_1_ = new ResourceLocation(JSONUtils.getString(lvt_1_1_, "fluid"));
            lvt_2_1_ = (Fluid)Registry.FLUID.getOrDefault(lvt_3_1_);
         }

         Tag<Fluid> lvt_3_2_ = null;
         if (lvt_1_1_.has("tag")) {
            ResourceLocation lvt_4_1_ = new ResourceLocation(JSONUtils.getString(lvt_1_1_, "tag"));
            lvt_3_2_ = FluidTags.func_226157_a_().get(lvt_4_1_);
            if (lvt_3_2_ == null) {
               throw new JsonSyntaxException("Unknown fluid tag '" + lvt_4_1_ + "'");
            }
         }

         StatePropertiesPredicate lvt_4_2_ = StatePropertiesPredicate.func_227186_a_(lvt_1_1_.get("state"));
         return new FluidPredicate(lvt_3_2_, lvt_2_1_, lvt_4_2_);
      } else {
         return field_226643_a_;
      }
   }

   public JsonElement func_226647_a_() {
      if (this == field_226643_a_) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject lvt_1_1_ = new JsonObject();
         if (this.field_226645_c_ != null) {
            lvt_1_1_.addProperty("fluid", Registry.FLUID.getKey(this.field_226645_c_).toString());
         }

         if (this.field_226644_b_ != null) {
            lvt_1_1_.addProperty("tag", this.field_226644_b_.getId().toString());
         }

         lvt_1_1_.add("state", this.field_226646_d_.func_227180_a_());
         return lvt_1_1_;
      }
   }

   static {
      field_226643_a_ = new FluidPredicate((Tag)null, (Fluid)null, StatePropertiesPredicate.field_227178_a_);
   }
}
