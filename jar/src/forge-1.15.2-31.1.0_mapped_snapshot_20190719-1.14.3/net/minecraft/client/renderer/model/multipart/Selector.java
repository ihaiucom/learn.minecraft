package net.minecraft.client.renderer.model.multipart;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Streams;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.VariantList;
import net.minecraft.state.StateContainer;
import net.minecraft.util.JSONUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Selector {
   private final ICondition condition;
   private final VariantList variantList;

   public Selector(ICondition p_i46562_1_, VariantList p_i46562_2_) {
      if (p_i46562_1_ == null) {
         throw new IllegalArgumentException("Missing condition for selector");
      } else if (p_i46562_2_ == null) {
         throw new IllegalArgumentException("Missing variant for selector");
      } else {
         this.condition = p_i46562_1_;
         this.variantList = p_i46562_2_;
      }
   }

   public VariantList getVariantList() {
      return this.variantList;
   }

   public Predicate<BlockState> getPredicate(StateContainer<Block, BlockState> p_188166_1_) {
      return this.condition.getPredicate(p_188166_1_);
   }

   public boolean equals(Object p_equals_1_) {
      return this == p_equals_1_;
   }

   public int hashCode() {
      return System.identityHashCode(this);
   }

   @OnlyIn(Dist.CLIENT)
   public static class Deserializer implements JsonDeserializer<Selector> {
      public Selector deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
         JsonObject lvt_4_1_ = p_deserialize_1_.getAsJsonObject();
         return new Selector(this.getWhenCondition(lvt_4_1_), (VariantList)p_deserialize_3_.deserialize(lvt_4_1_.get("apply"), VariantList.class));
      }

      private ICondition getWhenCondition(JsonObject p_188159_1_) {
         return p_188159_1_.has("when") ? getOrAndCondition(JSONUtils.getJsonObject(p_188159_1_, "when")) : ICondition.TRUE;
      }

      @VisibleForTesting
      static ICondition getOrAndCondition(JsonObject p_188158_0_) {
         Set<Entry<String, JsonElement>> lvt_1_1_ = p_188158_0_.entrySet();
         if (lvt_1_1_.isEmpty()) {
            throw new JsonParseException("No elements found in selector");
         } else if (lvt_1_1_.size() == 1) {
            List lvt_2_2_;
            if (p_188158_0_.has("OR")) {
               lvt_2_2_ = (List)Streams.stream(JSONUtils.getJsonArray(p_188158_0_, "OR")).map((p_200692_0_) -> {
                  return getOrAndCondition(p_200692_0_.getAsJsonObject());
               }).collect(Collectors.toList());
               return new OrCondition(lvt_2_2_);
            } else if (p_188158_0_.has("AND")) {
               lvt_2_2_ = (List)Streams.stream(JSONUtils.getJsonArray(p_188158_0_, "AND")).map((p_200691_0_) -> {
                  return getOrAndCondition(p_200691_0_.getAsJsonObject());
               }).collect(Collectors.toList());
               return new AndCondition(lvt_2_2_);
            } else {
               return makePropertyValue((Entry)lvt_1_1_.iterator().next());
            }
         } else {
            return new AndCondition((Iterable)lvt_1_1_.stream().map(Selector.Deserializer::makePropertyValue).collect(Collectors.toList()));
         }
      }

      private static ICondition makePropertyValue(Entry<String, JsonElement> p_188161_0_) {
         return new PropertyValueCondition((String)p_188161_0_.getKey(), ((JsonElement)p_188161_0_.getValue()).getAsString());
      }

      // $FF: synthetic method
      public Object deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
         return this.deserialize(p_deserialize_1_, p_deserialize_2_, p_deserialize_3_);
      }
   }
}
