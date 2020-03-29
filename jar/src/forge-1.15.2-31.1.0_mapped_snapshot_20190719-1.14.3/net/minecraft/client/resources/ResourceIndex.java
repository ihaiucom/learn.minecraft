package net.minecraft.client.resources;

import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class ResourceIndex {
   protected static final Logger LOGGER = LogManager.getLogger();
   private final Map<String, File> field_229271_b_ = Maps.newHashMap();
   private final Map<ResourceLocation, File> field_229272_c_ = Maps.newHashMap();

   protected ResourceIndex() {
   }

   public ResourceIndex(File p_i1047_1_, String p_i1047_2_) {
      File lvt_3_1_ = new File(p_i1047_1_, "objects");
      File lvt_4_1_ = new File(p_i1047_1_, "indexes/" + p_i1047_2_ + ".json");
      BufferedReader lvt_5_1_ = null;

      try {
         lvt_5_1_ = Files.newReader(lvt_4_1_, StandardCharsets.UTF_8);
         JsonObject lvt_6_1_ = JSONUtils.fromJson((Reader)lvt_5_1_);
         JsonObject lvt_7_1_ = JSONUtils.getJsonObject(lvt_6_1_, "objects", (JsonObject)null);
         if (lvt_7_1_ != null) {
            Iterator var8 = lvt_7_1_.entrySet().iterator();

            while(var8.hasNext()) {
               Entry<String, JsonElement> lvt_9_1_ = (Entry)var8.next();
               JsonObject lvt_10_1_ = (JsonObject)lvt_9_1_.getValue();
               String lvt_11_1_ = (String)lvt_9_1_.getKey();
               String[] lvt_12_1_ = lvt_11_1_.split("/", 2);
               String lvt_13_1_ = JSONUtils.getString(lvt_10_1_, "hash");
               File lvt_14_1_ = new File(lvt_3_1_, lvt_13_1_.substring(0, 2) + "/" + lvt_13_1_);
               if (lvt_12_1_.length == 1) {
                  this.field_229271_b_.put(lvt_12_1_[0], lvt_14_1_);
               } else {
                  this.field_229272_c_.put(new ResourceLocation(lvt_12_1_[0], lvt_12_1_[1]), lvt_14_1_);
               }
            }
         }
      } catch (JsonParseException var19) {
         LOGGER.error("Unable to parse resource index file: {}", lvt_4_1_);
      } catch (FileNotFoundException var20) {
         LOGGER.error("Can't find the resource index file: {}", lvt_4_1_);
      } finally {
         IOUtils.closeQuietly(lvt_5_1_);
      }

   }

   @Nullable
   public File getFile(ResourceLocation p_188547_1_) {
      return (File)this.field_229272_c_.get(p_188547_1_);
   }

   @Nullable
   public File func_225638_a_(String p_225638_1_) {
      return (File)this.field_229271_b_.get(p_225638_1_);
   }

   public Collection<ResourceLocation> func_225639_a_(String p_225639_1_, String p_225639_2_, int p_225639_3_, Predicate<String> p_225639_4_) {
      return (Collection)this.field_229272_c_.keySet().stream().filter((p_229273_3_) -> {
         String lvt_4_1_ = p_229273_3_.getPath();
         return p_229273_3_.getNamespace().equals(p_225639_2_) && !lvt_4_1_.endsWith(".mcmeta") && lvt_4_1_.startsWith(p_225639_1_ + "/") && p_225639_4_.test(lvt_4_1_);
      }).collect(Collectors.toList());
   }
}
