package net.minecraft.util.text;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.server.LanguageHook;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LanguageMap {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Pattern NUMERIC_VARIABLE_PATTERN = Pattern.compile("%(\\d+\\$)?[\\d\\.]*[df]");
   private static final LanguageMap INSTANCE = new LanguageMap();
   private final Map<String, String> languageList = Maps.newHashMap();
   private long lastUpdateTimeInMilliseconds;

   public LanguageMap() {
      try {
         InputStream inputstream = LanguageMap.class.getResourceAsStream("/assets/minecraft/lang/en_us.json");
         Throwable var2 = null;

         try {
            JsonElement jsonelement = (JsonElement)(new Gson()).fromJson(new InputStreamReader(inputstream, StandardCharsets.UTF_8), JsonElement.class);
            JsonObject jsonobject = JSONUtils.getJsonObject(jsonelement, "strings");
            Iterator var5 = jsonobject.entrySet().iterator();

            while(var5.hasNext()) {
               Entry<String, JsonElement> entry = (Entry)var5.next();
               String s = NUMERIC_VARIABLE_PATTERN.matcher(JSONUtils.getString((JsonElement)entry.getValue(), (String)entry.getKey())).replaceAll("%$1s");
               this.languageList.put(entry.getKey(), s);
            }

            LanguageHook.captureLanguageMap(this.languageList);
            this.lastUpdateTimeInMilliseconds = Util.milliTime();
         } catch (Throwable var16) {
            var2 = var16;
            throw var16;
         } finally {
            if (inputstream != null) {
               if (var2 != null) {
                  try {
                     inputstream.close();
                  } catch (Throwable var15) {
                     var2.addSuppressed(var15);
                  }
               } else {
                  inputstream.close();
               }
            }

         }
      } catch (IOException | JsonParseException var18) {
         LOGGER.error("Couldn't read strings from /assets/minecraft/lang/en_us.json", var18);
      }

   }

   public static LanguageMap getInstance() {
      return INSTANCE;
   }

   @OnlyIn(Dist.CLIENT)
   public static synchronized void replaceWith(Map<String, String> p_135063_0_) {
      INSTANCE.languageList.clear();
      INSTANCE.languageList.putAll(p_135063_0_);
      INSTANCE.lastUpdateTimeInMilliseconds = Util.milliTime();
   }

   public synchronized String translateKey(String p_74805_1_) {
      return this.tryTranslateKey(p_74805_1_);
   }

   private String tryTranslateKey(String p_135064_1_) {
      String s = (String)this.languageList.get(p_135064_1_);
      return s == null ? p_135064_1_ : s;
   }

   public synchronized boolean exists(String p_210813_1_) {
      return this.languageList.containsKey(p_210813_1_);
   }

   public long getLastUpdateTimeInMilliseconds() {
      return this.lastUpdateTimeInMilliseconds;
   }
}
