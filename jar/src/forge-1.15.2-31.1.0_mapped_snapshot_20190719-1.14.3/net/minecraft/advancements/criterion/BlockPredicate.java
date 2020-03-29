package net.minecraft.advancements.criterion;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.server.ServerWorld;

public class BlockPredicate {
   public static final BlockPredicate field_226231_a_;
   @Nullable
   private final Tag<Block> field_226232_b_;
   @Nullable
   private final Block field_226233_c_;
   private final StatePropertiesPredicate field_226234_d_;
   private final NBTPredicate field_226235_e_;

   public BlockPredicate(@Nullable Tag<Block> p_i225708_1_, @Nullable Block p_i225708_2_, StatePropertiesPredicate p_i225708_3_, NBTPredicate p_i225708_4_) {
      this.field_226232_b_ = p_i225708_1_;
      this.field_226233_c_ = p_i225708_2_;
      this.field_226234_d_ = p_i225708_3_;
      this.field_226235_e_ = p_i225708_4_;
   }

   public boolean func_226238_a_(ServerWorld p_226238_1_, BlockPos p_226238_2_) {
      if (this == field_226231_a_) {
         return true;
      } else if (!p_226238_1_.isBlockPresent(p_226238_2_)) {
         return false;
      } else {
         BlockState lvt_3_1_ = p_226238_1_.getBlockState(p_226238_2_);
         Block lvt_4_1_ = lvt_3_1_.getBlock();
         if (this.field_226232_b_ != null && !this.field_226232_b_.contains(lvt_4_1_)) {
            return false;
         } else if (this.field_226233_c_ != null && lvt_4_1_ != this.field_226233_c_) {
            return false;
         } else if (!this.field_226234_d_.func_227181_a_(lvt_3_1_)) {
            return false;
         } else {
            if (this.field_226235_e_ != NBTPredicate.ANY) {
               TileEntity lvt_5_1_ = p_226238_1_.getTileEntity(p_226238_2_);
               if (lvt_5_1_ == null || !this.field_226235_e_.test((INBT)lvt_5_1_.write(new CompoundNBT()))) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   public static BlockPredicate func_226237_a_(@Nullable JsonElement p_226237_0_) {
      if (p_226237_0_ != null && !p_226237_0_.isJsonNull()) {
         JsonObject lvt_1_1_ = JSONUtils.getJsonObject(p_226237_0_, "block");
         NBTPredicate lvt_2_1_ = NBTPredicate.deserialize(lvt_1_1_.get("nbt"));
         Block lvt_3_1_ = null;
         if (lvt_1_1_.has("block")) {
            ResourceLocation lvt_4_1_ = new ResourceLocation(JSONUtils.getString(lvt_1_1_, "block"));
            lvt_3_1_ = (Block)Registry.BLOCK.getOrDefault(lvt_4_1_);
         }

         Tag<Block> lvt_4_2_ = null;
         if (lvt_1_1_.has("tag")) {
            ResourceLocation lvt_5_1_ = new ResourceLocation(JSONUtils.getString(lvt_1_1_, "tag"));
            lvt_4_2_ = BlockTags.getCollection().get(lvt_5_1_);
            if (lvt_4_2_ == null) {
               throw new JsonSyntaxException("Unknown block tag '" + lvt_5_1_ + "'");
            }
         }

         StatePropertiesPredicate lvt_5_2_ = StatePropertiesPredicate.func_227186_a_(lvt_1_1_.get("state"));
         return new BlockPredicate(lvt_4_2_, lvt_3_1_, lvt_5_2_, lvt_2_1_);
      } else {
         return field_226231_a_;
      }
   }

   public JsonElement func_226236_a_() {
      if (this == field_226231_a_) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject lvt_1_1_ = new JsonObject();
         if (this.field_226233_c_ != null) {
            lvt_1_1_.addProperty("block", Registry.BLOCK.getKey(this.field_226233_c_).toString());
         }

         if (this.field_226232_b_ != null) {
            lvt_1_1_.addProperty("tag", this.field_226232_b_.getId().toString());
         }

         lvt_1_1_.add("nbt", this.field_226235_e_.serialize());
         lvt_1_1_.add("state", this.field_226234_d_.func_227180_a_());
         return lvt_1_1_;
      }
   }

   static {
      field_226231_a_ = new BlockPredicate((Tag)null, (Block)null, StatePropertiesPredicate.field_227178_a_, NBTPredicate.ANY);
   }

   public static class Builder {
      @Nullable
      private Block field_226239_a_;
      @Nullable
      private Tag<Block> field_226240_b_;
      private StatePropertiesPredicate field_226241_c_;
      private NBTPredicate field_226242_d_;

      private Builder() {
         this.field_226241_c_ = StatePropertiesPredicate.field_227178_a_;
         this.field_226242_d_ = NBTPredicate.ANY;
      }

      public static BlockPredicate.Builder func_226243_a_() {
         return new BlockPredicate.Builder();
      }

      public BlockPredicate.Builder func_226244_a_(Tag<Block> p_226244_1_) {
         this.field_226240_b_ = p_226244_1_;
         return this;
      }

      public BlockPredicate func_226245_b_() {
         return new BlockPredicate(this.field_226240_b_, this.field_226239_a_, this.field_226241_c_, this.field_226242_d_);
      }
   }
}
