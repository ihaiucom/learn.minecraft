package net.minecraftforge.common.crafting.conditions;

import com.google.gson.JsonObject;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.ModList;

public class ModLoadedCondition implements ICondition {
   private static final ResourceLocation NAME = new ResourceLocation("forge", "mod_loaded");
   private final String modid;

   public ModLoadedCondition(String modid) {
      this.modid = modid;
   }

   public ResourceLocation getID() {
      return NAME;
   }

   public boolean test() {
      return ModList.get().isLoaded(this.modid);
   }

   public String toString() {
      return "mod_loaded(\"" + this.modid + "\")";
   }

   public static class Serializer implements IConditionSerializer<ModLoadedCondition> {
      public static final ModLoadedCondition.Serializer INSTANCE = new ModLoadedCondition.Serializer();

      public void write(JsonObject json, ModLoadedCondition value) {
         json.addProperty("modid", value.modid);
      }

      public ModLoadedCondition read(JsonObject json) {
         return new ModLoadedCondition(JSONUtils.getString(json, "modid"));
      }

      public ResourceLocation getID() {
         return ModLoadedCondition.NAME;
      }
   }
}
