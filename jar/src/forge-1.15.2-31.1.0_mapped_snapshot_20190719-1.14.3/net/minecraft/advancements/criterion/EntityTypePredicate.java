package net.minecraft.advancements.criterion;

import com.google.common.base.Joiner;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityType;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public abstract class EntityTypePredicate {
   public static final EntityTypePredicate ANY = new EntityTypePredicate() {
      public boolean test(EntityType<?> p_209368_1_) {
         return true;
      }

      public JsonElement serialize() {
         return JsonNull.INSTANCE;
      }
   };
   private static final Joiner field_209372_b = Joiner.on(", ");

   public abstract boolean test(EntityType<?> var1);

   public abstract JsonElement serialize();

   public static EntityTypePredicate deserialize(@Nullable JsonElement p_209370_0_) {
      if (p_209370_0_ != null && !p_209370_0_.isJsonNull()) {
         String lvt_1_1_ = JSONUtils.getString(p_209370_0_, "type");
         ResourceLocation lvt_2_2_;
         if (lvt_1_1_.startsWith("#")) {
            lvt_2_2_ = new ResourceLocation(lvt_1_1_.substring(1));
            Tag<EntityType<?>> lvt_3_1_ = EntityTypeTags.getCollection().getOrCreate(lvt_2_2_);
            return new EntityTypePredicate.TagPredicate(lvt_3_1_);
         } else {
            lvt_2_2_ = new ResourceLocation(lvt_1_1_);
            EntityType<?> lvt_3_2_ = (EntityType)Registry.ENTITY_TYPE.getValue(lvt_2_2_).orElseThrow(() -> {
               return new JsonSyntaxException("Unknown entity type '" + lvt_2_2_ + "', valid types are: " + field_209372_b.join(Registry.ENTITY_TYPE.keySet()));
            });
            return new EntityTypePredicate.TypePredicate(lvt_3_2_);
         }
      } else {
         return ANY;
      }
   }

   public static EntityTypePredicate func_217999_b(EntityType<?> p_217999_0_) {
      return new EntityTypePredicate.TypePredicate(p_217999_0_);
   }

   public static EntityTypePredicate func_217998_a(Tag<EntityType<?>> p_217998_0_) {
      return new EntityTypePredicate.TagPredicate(p_217998_0_);
   }

   static class TagPredicate extends EntityTypePredicate {
      private final Tag<EntityType<?>> field_218001_b;

      public TagPredicate(Tag<EntityType<?>> p_i50558_1_) {
         this.field_218001_b = p_i50558_1_;
      }

      public boolean test(EntityType<?> p_209368_1_) {
         return this.field_218001_b.contains(p_209368_1_);
      }

      public JsonElement serialize() {
         return new JsonPrimitive("#" + this.field_218001_b.getId().toString());
      }
   }

   static class TypePredicate extends EntityTypePredicate {
      private final EntityType<?> field_218000_b;

      public TypePredicate(EntityType<?> p_i50556_1_) {
         this.field_218000_b = p_i50556_1_;
      }

      public boolean test(EntityType<?> p_209368_1_) {
         return this.field_218000_b == p_209368_1_;
      }

      public JsonElement serialize() {
         return new JsonPrimitive(Registry.ENTITY_TYPE.getKey(this.field_218000_b).toString());
      }
   }
}
