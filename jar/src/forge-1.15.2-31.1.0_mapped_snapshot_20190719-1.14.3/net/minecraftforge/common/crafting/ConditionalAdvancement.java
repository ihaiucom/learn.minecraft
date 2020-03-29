package net.minecraftforge.common.crafting;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.advancements.Advancement;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;

public class ConditionalAdvancement {
   public static ConditionalAdvancement.Builder builder() {
      return new ConditionalAdvancement.Builder();
   }

   public static Advancement.Builder read(Gson gson, ResourceLocation recipeId, JsonObject json) {
      JsonArray entries = JSONUtils.getJsonArray(json, "advancements", (JsonArray)null);
      if (entries == null) {
         return !CraftingHelper.processConditions(json, "conditions") ? null : (Advancement.Builder)gson.fromJson(json, Advancement.Builder.class);
      } else {
         int idx = 0;

         for(Iterator var5 = entries.iterator(); var5.hasNext(); ++idx) {
            JsonElement ele = (JsonElement)var5.next();
            if (!ele.isJsonObject()) {
               throw new JsonSyntaxException("Invalid advancement entry at index " + idx + " Must be JsonObject");
            }

            if (CraftingHelper.processConditions(JSONUtils.getJsonArray(ele.getAsJsonObject(), "conditions"))) {
               return (Advancement.Builder)gson.fromJson(JSONUtils.getJsonObject(ele.getAsJsonObject(), "advancement"), Advancement.Builder.class);
            }
         }

         return null;
      }
   }

   public static class Builder {
      private List<ICondition[]> conditions = new ArrayList();
      private List<Advancement.Builder> advancements = new ArrayList();
      private List<ICondition> currentConditions = new ArrayList();
      private boolean locked = false;

      public ConditionalAdvancement.Builder addCondition(ICondition condition) {
         if (this.locked) {
            throw new IllegalStateException("Attempted to modify finished builder");
         } else {
            this.currentConditions.add(condition);
            return this;
         }
      }

      public ConditionalAdvancement.Builder addAdvancement(Consumer<Consumer<Advancement.Builder>> callable) {
         if (this.locked) {
            throw new IllegalStateException("Attempted to modify finished builder");
         } else {
            callable.accept(this::addAdvancement);
            return this;
         }
      }

      public ConditionalAdvancement.Builder addAdvancement(Advancement.Builder recipe) {
         if (this.locked) {
            throw new IllegalStateException("Attempted to modify finished builder");
         } else if (this.currentConditions.isEmpty()) {
            throw new IllegalStateException("Can not add a advancement with no conditions.");
         } else {
            this.conditions.add(this.currentConditions.toArray(new ICondition[this.currentConditions.size()]));
            this.advancements.add(recipe);
            this.currentConditions.clear();
            return this;
         }
      }

      public JsonObject write() {
         if (!this.locked) {
            if (!this.currentConditions.isEmpty()) {
               throw new IllegalStateException("Invalid builder state: Orphaned conditions");
            }

            if (this.advancements.isEmpty()) {
               throw new IllegalStateException("Invalid builder state: No Advancements");
            }

            this.locked = true;
         }

         JsonObject json = new JsonObject();
         JsonArray array = new JsonArray();
         json.add("advancements", array);

         for(int x = 0; x < this.conditions.size(); ++x) {
            JsonObject holder = new JsonObject();
            JsonArray conds = new JsonArray();
            ICondition[] var6 = (ICondition[])this.conditions.get(x);
            int var7 = var6.length;

            for(int var8 = 0; var8 < var7; ++var8) {
               ICondition c = var6[var8];
               conds.add(CraftingHelper.serialize(c));
            }

            holder.add("conditions", conds);
            holder.add("advancement", ((Advancement.Builder)this.advancements.get(x)).serialize());
            array.add(holder);
         }

         return json;
      }
   }
}
