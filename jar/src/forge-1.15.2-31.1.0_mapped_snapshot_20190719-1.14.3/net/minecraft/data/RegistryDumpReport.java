package net.minecraft.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Iterator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.DefaultedRegistry;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;

public class RegistryDumpReport implements IDataProvider {
   private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
   private final DataGenerator field_218434_c;

   public RegistryDumpReport(DataGenerator p_i50790_1_) {
      this.field_218434_c = p_i50790_1_;
   }

   public void act(DirectoryCache p_200398_1_) throws IOException {
      JsonObject lvt_2_1_ = new JsonObject();
      Registry.REGISTRY.keySet().forEach((p_218431_1_) -> {
         lvt_2_1_.add(p_218431_1_.toString(), func_218432_a((MutableRegistry)Registry.REGISTRY.getOrDefault(p_218431_1_)));
      });
      Path lvt_3_1_ = this.field_218434_c.getOutputFolder().resolve("reports/registries.json");
      IDataProvider.save(GSON, p_200398_1_, lvt_2_1_, lvt_3_1_);
   }

   private static <T> JsonElement func_218432_a(MutableRegistry<T> p_218432_0_) {
      JsonObject lvt_1_1_ = new JsonObject();
      if (p_218432_0_ instanceof DefaultedRegistry) {
         ResourceLocation lvt_2_1_ = ((DefaultedRegistry)p_218432_0_).getDefaultKey();
         lvt_1_1_.addProperty("default", lvt_2_1_.toString());
      }

      int lvt_2_2_ = Registry.REGISTRY.getId(p_218432_0_);
      lvt_1_1_.addProperty("protocol_id", lvt_2_2_);
      JsonObject lvt_3_1_ = new JsonObject();
      Iterator var4 = p_218432_0_.keySet().iterator();

      while(var4.hasNext()) {
         ResourceLocation lvt_5_1_ = (ResourceLocation)var4.next();
         T lvt_6_1_ = p_218432_0_.getOrDefault(lvt_5_1_);
         int lvt_7_1_ = p_218432_0_.getId(lvt_6_1_);
         JsonObject lvt_8_1_ = new JsonObject();
         lvt_8_1_.addProperty("protocol_id", lvt_7_1_);
         lvt_3_1_.add(lvt_5_1_.toString(), lvt_8_1_);
      }

      lvt_1_1_.add("entries", lvt_3_1_);
      return lvt_1_1_;
   }

   public String getName() {
      return "Registry Dump";
   }
}
