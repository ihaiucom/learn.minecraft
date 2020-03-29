package net.minecraftforge.common.crafting.conditions;

import com.google.gson.JsonObject;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;

public class NotCondition implements ICondition {
   private static final ResourceLocation NAME = new ResourceLocation("forge", "not");
   private final ICondition child;

   public NotCondition(ICondition child) {
      this.child = child;
   }

   public ResourceLocation getID() {
      return NAME;
   }

   public boolean test() {
      return !this.child.test();
   }

   public String toString() {
      return "!" + this.child;
   }

   public static class Serializer implements IConditionSerializer<NotCondition> {
      public static final NotCondition.Serializer INSTANCE = new NotCondition.Serializer();

      public void write(JsonObject json, NotCondition value) {
         json.add("value", CraftingHelper.serialize(value.child));
      }

      public NotCondition read(JsonObject json) {
         return new NotCondition(CraftingHelper.getCondition(JSONUtils.getJsonObject(json, "value")));
      }

      public ResourceLocation getID() {
         return NotCondition.NAME;
      }
   }
}
