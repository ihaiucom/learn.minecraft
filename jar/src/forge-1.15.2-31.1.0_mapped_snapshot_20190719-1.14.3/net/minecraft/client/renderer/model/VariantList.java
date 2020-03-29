package net.minecraft.client.renderer.model;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Pair;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VariantList implements IUnbakedModel {
   private final List<Variant> variantList;

   public VariantList(List<Variant> p_i46568_1_) {
      this.variantList = p_i46568_1_;
   }

   public List<Variant> getVariantList() {
      return this.variantList;
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (p_equals_1_ instanceof VariantList) {
         VariantList lvt_2_1_ = (VariantList)p_equals_1_;
         return this.variantList.equals(lvt_2_1_.variantList);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.variantList.hashCode();
   }

   public Collection<ResourceLocation> getDependencies() {
      return (Collection)this.getVariantList().stream().map(Variant::getModelLocation).collect(Collectors.toSet());
   }

   public Collection<Material> func_225614_a_(Function<ResourceLocation, IUnbakedModel> p_225614_1_, Set<Pair<String, String>> p_225614_2_) {
      return (Collection)this.getVariantList().stream().map(Variant::getModelLocation).distinct().flatMap((p_228831_2_) -> {
         return ((IUnbakedModel)p_225614_1_.apply(p_228831_2_)).func_225614_a_(p_225614_1_, p_225614_2_).stream();
      }).collect(Collectors.toSet());
   }

   @Nullable
   public IBakedModel func_225613_a_(ModelBakery p_225613_1_, Function<Material, TextureAtlasSprite> p_225613_2_, IModelTransform p_225613_3_, ResourceLocation p_225613_4_) {
      if (this.getVariantList().isEmpty()) {
         return null;
      } else {
         WeightedBakedModel.Builder lvt_5_1_ = new WeightedBakedModel.Builder();
         Iterator var6 = this.getVariantList().iterator();

         while(var6.hasNext()) {
            Variant lvt_7_1_ = (Variant)var6.next();
            IBakedModel lvt_8_1_ = p_225613_1_.func_217845_a(lvt_7_1_.getModelLocation(), lvt_7_1_);
            lvt_5_1_.add(lvt_8_1_, lvt_7_1_.getWeight());
         }

         return lvt_5_1_.build();
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Deserializer implements JsonDeserializer<VariantList> {
      public VariantList deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
         List<Variant> lvt_4_1_ = Lists.newArrayList();
         if (p_deserialize_1_.isJsonArray()) {
            JsonArray lvt_5_1_ = p_deserialize_1_.getAsJsonArray();
            if (lvt_5_1_.size() == 0) {
               throw new JsonParseException("Empty variant array");
            }

            Iterator var6 = lvt_5_1_.iterator();

            while(var6.hasNext()) {
               JsonElement lvt_7_1_ = (JsonElement)var6.next();
               lvt_4_1_.add(p_deserialize_3_.deserialize(lvt_7_1_, Variant.class));
            }
         } else {
            lvt_4_1_.add(p_deserialize_3_.deserialize(p_deserialize_1_, Variant.class));
         }

         return new VariantList(lvt_4_1_);
      }

      // $FF: synthetic method
      public Object deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
         return this.deserialize(p_deserialize_1_, p_deserialize_2_, p_deserialize_3_);
      }
   }
}
