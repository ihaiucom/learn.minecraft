package net.minecraft.client.resources;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.IllegalFormatException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class Locale {
   private static final Gson GSON = new Gson();
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Pattern PATTERN = Pattern.compile("%(\\d+\\$)?[\\d\\.]*[df]");
   protected final Map<String, String> properties = Maps.newHashMap();

   public synchronized void func_195811_a(IResourceManager p_195811_1_, List<String> p_195811_2_) {
      this.properties.clear();
      Iterator var3 = p_195811_2_.iterator();

      while(var3.hasNext()) {
         String lvt_4_1_ = (String)var3.next();
         String lvt_5_1_ = String.format("lang/%s.json", lvt_4_1_);
         Iterator var6 = p_195811_1_.getResourceNamespaces().iterator();

         while(var6.hasNext()) {
            String lvt_7_1_ = (String)var6.next();

            try {
               ResourceLocation lvt_8_1_ = new ResourceLocation(lvt_7_1_, lvt_5_1_);
               this.loadLocaleData(p_195811_1_.getAllResources(lvt_8_1_));
            } catch (FileNotFoundException var9) {
            } catch (Exception var10) {
               LOGGER.warn("Skipped language file: {}:{} ({})", lvt_7_1_, lvt_5_1_, var10.toString());
            }
         }
      }

   }

   private void loadLocaleData(List<IResource> p_135028_1_) {
      Iterator var2 = p_135028_1_.iterator();

      while(var2.hasNext()) {
         IResource lvt_3_1_ = (IResource)var2.next();
         InputStream lvt_4_1_ = lvt_3_1_.getInputStream();

         try {
            this.loadLocaleData(lvt_4_1_);
         } finally {
            IOUtils.closeQuietly(lvt_4_1_);
         }
      }

   }

   private void loadLocaleData(InputStream p_135021_1_) {
      JsonElement lvt_2_1_ = (JsonElement)GSON.fromJson(new InputStreamReader(p_135021_1_, StandardCharsets.UTF_8), JsonElement.class);
      JsonObject lvt_3_1_ = JSONUtils.getJsonObject(lvt_2_1_, "strings");
      Iterator var4 = lvt_3_1_.entrySet().iterator();

      while(var4.hasNext()) {
         Entry<String, JsonElement> lvt_5_1_ = (Entry)var4.next();
         String lvt_6_1_ = PATTERN.matcher(JSONUtils.getString((JsonElement)lvt_5_1_.getValue(), (String)lvt_5_1_.getKey())).replaceAll("%$1s");
         this.properties.put(lvt_5_1_.getKey(), lvt_6_1_);
      }

   }

   private String translateKeyPrivate(String p_135026_1_) {
      String lvt_2_1_ = (String)this.properties.get(p_135026_1_);
      return lvt_2_1_ == null ? p_135026_1_ : lvt_2_1_;
   }

   public String formatMessage(String p_135023_1_, Object[] p_135023_2_) {
      String lvt_3_1_ = this.translateKeyPrivate(p_135023_1_);

      try {
         return String.format(lvt_3_1_, p_135023_2_);
      } catch (IllegalFormatException var5) {
         return "Format error: " + lvt_3_1_;
      }
   }

   public boolean hasKey(String p_188568_1_) {
      return this.properties.containsKey(p_188568_1_);
   }
}
