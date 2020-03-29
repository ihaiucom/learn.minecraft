package net.minecraftforge.common.crafting.conditions;

import com.google.common.base.Joiner;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;

public class AndCondition implements ICondition {
   private static final ResourceLocation NAME = new ResourceLocation("forge", "and");
   private final ICondition[] children;

   public AndCondition(ICondition... values) {
      if (values != null && values.length != 0) {
         ICondition[] var2 = values;
         int var3 = values.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            ICondition child = var2[var4];
            if (child == null) {
               throw new IllegalArgumentException("Value must not be null");
            }
         }

         this.children = values;
      } else {
         throw new IllegalArgumentException("Values must not be empty");
      }
   }

   public ResourceLocation getID() {
      return NAME;
   }

   public boolean test() {
      ICondition[] var1 = this.children;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         ICondition child = var1[var3];
         if (!child.test()) {
            return false;
         }
      }

      return true;
   }

   public String toString() {
      return Joiner.on(" && ").join(this.children);
   }

   public static class Serializer implements IConditionSerializer<AndCondition> {
      public static final AndCondition.Serializer INSTANCE = new AndCondition.Serializer();

      public void write(JsonObject json, AndCondition value) {
         JsonArray values = new JsonArray();
         ICondition[] var4 = value.children;
         int var5 = var4.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            ICondition child = var4[var6];
            values.add(CraftingHelper.serialize(child));
         }

         json.add("values", values);
      }

      public AndCondition read(JsonObject json) {
         List<ICondition> children = new ArrayList();
         Iterator var3 = JSONUtils.getJsonArray(json, "values").iterator();

         while(var3.hasNext()) {
            JsonElement j = (JsonElement)var3.next();
            if (!j.isJsonObject()) {
               throw new JsonSyntaxException("And condition values must be an array of JsonObjects");
            }

            children.add(CraftingHelper.getCondition(j.getAsJsonObject()));
         }

         return new AndCondition((ICondition[])children.toArray(new ICondition[children.size()]));
      }

      public ResourceLocation getID() {
         return AndCondition.NAME;
      }
   }
}
