package net.minecraftforge.common.crafting.conditions;

import com.google.gson.JsonObject;
import net.minecraft.util.ResourceLocation;

public final class FalseCondition implements ICondition {
   public static final FalseCondition INSTANCE = new FalseCondition();
   private static final ResourceLocation NAME = new ResourceLocation("forge", "false");

   private FalseCondition() {
   }

   public ResourceLocation getID() {
      return NAME;
   }

   public boolean test() {
      return false;
   }

   public String toString() {
      return "false";
   }

   public static class Serializer implements IConditionSerializer<FalseCondition> {
      public static final FalseCondition.Serializer INSTANCE = new FalseCondition.Serializer();

      public void write(JsonObject json, FalseCondition value) {
      }

      public FalseCondition read(JsonObject json) {
         return FalseCondition.INSTANCE;
      }

      public ResourceLocation getID() {
         return FalseCondition.NAME;
      }
   }
}
