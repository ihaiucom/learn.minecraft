package net.minecraft.client.renderer.model;

import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ItemOverride {
   private final ResourceLocation location;
   private final Map<ResourceLocation, Float> mapResourceValues;

   public ItemOverride(ResourceLocation p_i46571_1_, Map<ResourceLocation, Float> p_i46571_2_) {
      this.location = p_i46571_1_;
      this.mapResourceValues = p_i46571_2_;
   }

   public ResourceLocation getLocation() {
      return this.location;
   }

   boolean matchesItemStack(ItemStack p_188027_1_, @Nullable World p_188027_2_, @Nullable LivingEntity p_188027_3_) {
      Item lvt_4_1_ = p_188027_1_.getItem();
      Iterator var5 = this.mapResourceValues.entrySet().iterator();

      Entry lvt_6_1_;
      IItemPropertyGetter lvt_7_1_;
      do {
         if (!var5.hasNext()) {
            return true;
         }

         lvt_6_1_ = (Entry)var5.next();
         lvt_7_1_ = lvt_4_1_.getPropertyGetter((ResourceLocation)lvt_6_1_.getKey());
      } while(lvt_7_1_ != null && lvt_7_1_.call(p_188027_1_, p_188027_2_, p_188027_3_) >= (Float)lvt_6_1_.getValue());

      return false;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Deserializer implements JsonDeserializer<ItemOverride> {
      public ItemOverride deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
         JsonObject lvt_4_1_ = p_deserialize_1_.getAsJsonObject();
         ResourceLocation lvt_5_1_ = new ResourceLocation(JSONUtils.getString(lvt_4_1_, "model"));
         Map<ResourceLocation, Float> lvt_6_1_ = this.makeMapResourceValues(lvt_4_1_);
         return new ItemOverride(lvt_5_1_, lvt_6_1_);
      }

      protected Map<ResourceLocation, Float> makeMapResourceValues(JsonObject p_188025_1_) {
         Map<ResourceLocation, Float> lvt_2_1_ = Maps.newLinkedHashMap();
         JsonObject lvt_3_1_ = JSONUtils.getJsonObject(p_188025_1_, "predicate");
         Iterator var4 = lvt_3_1_.entrySet().iterator();

         while(var4.hasNext()) {
            Entry<String, JsonElement> lvt_5_1_ = (Entry)var4.next();
            lvt_2_1_.put(new ResourceLocation((String)lvt_5_1_.getKey()), JSONUtils.getFloat((JsonElement)lvt_5_1_.getValue(), (String)lvt_5_1_.getKey()));
         }

         return lvt_2_1_;
      }

      // $FF: synthetic method
      public Object deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
         return this.deserialize(p_deserialize_1_, p_deserialize_2_, p_deserialize_3_);
      }
   }
}
