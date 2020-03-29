package net.minecraft.advancements.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.server.ServerWorld;

public class PlacedBlockTrigger extends AbstractCriterionTrigger<PlacedBlockTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("placed_block");

   public ResourceLocation getId() {
      return ID;
   }

   public PlacedBlockTrigger.Instance deserializeInstance(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      Block lvt_3_1_ = func_226950_a_(p_192166_1_);
      StatePropertiesPredicate lvt_4_1_ = StatePropertiesPredicate.func_227186_a_(p_192166_1_.get("state"));
      if (lvt_3_1_ != null) {
         lvt_4_1_.func_227183_a_(lvt_3_1_.getStateContainer(), (p_226948_1_) -> {
            throw new JsonSyntaxException("Block " + lvt_3_1_ + " has no property " + p_226948_1_ + ":");
         });
      }

      LocationPredicate lvt_5_1_ = LocationPredicate.deserialize(p_192166_1_.get("location"));
      ItemPredicate lvt_6_1_ = ItemPredicate.deserialize(p_192166_1_.get("item"));
      return new PlacedBlockTrigger.Instance(lvt_3_1_, lvt_4_1_, lvt_5_1_, lvt_6_1_);
   }

   @Nullable
   private static Block func_226950_a_(JsonObject p_226950_0_) {
      if (p_226950_0_.has("block")) {
         ResourceLocation lvt_1_1_ = new ResourceLocation(JSONUtils.getString(p_226950_0_, "block"));
         return (Block)Registry.BLOCK.getValue(lvt_1_1_).orElseThrow(() -> {
            return new JsonSyntaxException("Unknown block type '" + lvt_1_1_ + "'");
         });
      } else {
         return null;
      }
   }

   public void trigger(ServerPlayerEntity p_193173_1_, BlockPos p_193173_2_, ItemStack p_193173_3_) {
      BlockState lvt_4_1_ = p_193173_1_.getServerWorld().getBlockState(p_193173_2_);
      this.func_227070_a_(p_193173_1_.getAdvancements(), (p_226949_4_) -> {
         return p_226949_4_.test(lvt_4_1_, p_193173_2_, p_193173_1_.getServerWorld(), p_193173_3_);
      });
   }

   // $FF: synthetic method
   public ICriterionInstance deserializeInstance(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      return this.deserializeInstance(p_192166_1_, p_192166_2_);
   }

   public static class Instance extends CriterionInstance {
      private final Block block;
      private final StatePropertiesPredicate properties;
      private final LocationPredicate location;
      private final ItemPredicate item;

      public Instance(@Nullable Block p_i225765_1_, StatePropertiesPredicate p_i225765_2_, LocationPredicate p_i225765_3_, ItemPredicate p_i225765_4_) {
         super(PlacedBlockTrigger.ID);
         this.block = p_i225765_1_;
         this.properties = p_i225765_2_;
         this.location = p_i225765_3_;
         this.item = p_i225765_4_;
      }

      public static PlacedBlockTrigger.Instance placedBlock(Block p_203934_0_) {
         return new PlacedBlockTrigger.Instance(p_203934_0_, StatePropertiesPredicate.field_227178_a_, LocationPredicate.ANY, ItemPredicate.ANY);
      }

      public boolean test(BlockState p_193210_1_, BlockPos p_193210_2_, ServerWorld p_193210_3_, ItemStack p_193210_4_) {
         if (this.block != null && p_193210_1_.getBlock() != this.block) {
            return false;
         } else if (!this.properties.func_227181_a_(p_193210_1_)) {
            return false;
         } else if (!this.location.test(p_193210_3_, (float)p_193210_2_.getX(), (float)p_193210_2_.getY(), (float)p_193210_2_.getZ())) {
            return false;
         } else {
            return this.item.test(p_193210_4_);
         }
      }

      public JsonElement serialize() {
         JsonObject lvt_1_1_ = new JsonObject();
         if (this.block != null) {
            lvt_1_1_.addProperty("block", Registry.BLOCK.getKey(this.block).toString());
         }

         lvt_1_1_.add("state", this.properties.func_227180_a_());
         lvt_1_1_.add("location", this.location.serialize());
         lvt_1_1_.add("item", this.item.serialize());
         return lvt_1_1_;
      }
   }
}
