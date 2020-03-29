package net.minecraft.world.storage.loot;

import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import java.util.Map;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

public class RandomRanges {
   private static final Map<ResourceLocation, Class<? extends IRandomRange>> field_216132_a = Maps.newHashMap();

   public static IRandomRange deserialize(JsonElement p_216130_0_, JsonDeserializationContext p_216130_1_) throws JsonParseException {
      if (p_216130_0_.isJsonPrimitive()) {
         return (IRandomRange)p_216130_1_.deserialize(p_216130_0_, ConstantRange.class);
      } else {
         JsonObject lvt_2_1_ = p_216130_0_.getAsJsonObject();
         String lvt_3_1_ = JSONUtils.getString(lvt_2_1_, "type", IRandomRange.UNIFORM.toString());
         Class<? extends IRandomRange> lvt_4_1_ = (Class)field_216132_a.get(new ResourceLocation(lvt_3_1_));
         if (lvt_4_1_ == null) {
            throw new JsonParseException("Unknown generator: " + lvt_3_1_);
         } else {
            return (IRandomRange)p_216130_1_.deserialize(lvt_2_1_, lvt_4_1_);
         }
      }
   }

   public static JsonElement serialize(IRandomRange p_216131_0_, JsonSerializationContext p_216131_1_) {
      JsonElement lvt_2_1_ = p_216131_1_.serialize(p_216131_0_);
      if (lvt_2_1_.isJsonObject()) {
         lvt_2_1_.getAsJsonObject().addProperty("type", p_216131_0_.func_215830_a().toString());
      }

      return lvt_2_1_;
   }

   static {
      field_216132_a.put(IRandomRange.UNIFORM, RandomValueRange.class);
      field_216132_a.put(IRandomRange.BINOMIAL, BinomialRange.class);
      field_216132_a.put(IRandomRange.CONSTANT, ConstantRange.class);
   }
}
