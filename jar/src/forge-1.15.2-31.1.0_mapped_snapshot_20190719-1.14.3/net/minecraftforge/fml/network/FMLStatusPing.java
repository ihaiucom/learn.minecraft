package net.minecraftforge.fml.network;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModList;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FMLStatusPing {
   private static final Logger LOGGER = LogManager.getLogger();
   private transient Map<ResourceLocation, Pair<String, Boolean>> channels;
   private transient Map<String, String> mods;
   private transient int fmlNetworkVer;

   public FMLStatusPing() {
      this.channels = NetworkRegistry.buildChannelVersionsForListPing();
      this.mods = new HashMap();
      ModList.get().forEachModContainer((modid, mc) -> {
         String var10000 = (String)this.mods.put(modid, mc.getCustomExtension(ExtensionPoint.DISPLAYTEST).map(Pair::getLeft).map(Supplier::get).orElse("OHNOES\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31"));
      });
      this.fmlNetworkVer = 2;
   }

   private FMLStatusPing(Map<ResourceLocation, Pair<String, Boolean>> deserialized, Map<String, String> modMarkers, int fmlNetVer) {
      this.channels = ImmutableMap.copyOf(deserialized);
      this.mods = modMarkers;
      this.fmlNetworkVer = fmlNetVer;
   }

   public Map<ResourceLocation, Pair<String, Boolean>> getRemoteChannels() {
      return this.channels;
   }

   public Map<String, String> getRemoteModData() {
      return this.mods;
   }

   public int getFMLNetworkVersion() {
      return this.fmlNetworkVer;
   }

   // $FF: synthetic method
   FMLStatusPing(Map x0, Map x1, int x2, Object x3) {
      this(x0, x1, x2);
   }

   public static class Serializer {
      public static FMLStatusPing deserialize(JsonObject forgeData, JsonDeserializationContext ctx) {
         try {
            Map<ResourceLocation, Pair<String, Boolean>> channels = (Map)StreamSupport.stream(JSONUtils.getJsonArray(forgeData, "channels").spliterator(), false).map(JsonElement::getAsJsonObject).collect(Collectors.toMap((jo) -> {
               return new ResourceLocation(JSONUtils.getString(jo, "res"));
            }, (jo) -> {
               return Pair.of(JSONUtils.getString(jo, "version"), JSONUtils.getBoolean(jo, "required"));
            }));
            Map<String, String> mods = (Map)StreamSupport.stream(JSONUtils.getJsonArray(forgeData, "mods").spliterator(), false).map(JsonElement::getAsJsonObject).collect(Collectors.toMap((jo) -> {
               return JSONUtils.getString(jo, "modId");
            }, (jo) -> {
               return JSONUtils.getString(jo, "modmarker");
            }));
            int remoteFMLVersion = JSONUtils.getInt(forgeData, "fmlNetworkVersion");
            return new FMLStatusPing(channels, mods, remoteFMLVersion);
         } catch (JsonSyntaxException var5) {
            FMLStatusPing.LOGGER.debug(FMLNetworkConstants.NETWORK, "Encountered an error parsing status ping data", var5);
            return null;
         }
      }

      public static JsonObject serialize(FMLStatusPing forgeData, JsonSerializationContext ctx) {
         JsonObject obj = new JsonObject();
         JsonArray channels = new JsonArray();
         forgeData.channels.forEach((namespace, version) -> {
            JsonObject mi = new JsonObject();
            mi.addProperty("res", namespace.toString());
            mi.addProperty("version", (String)version.getLeft());
            mi.addProperty("required", (Boolean)version.getRight());
            channels.add(mi);
         });
         obj.add("channels", channels);
         JsonArray modTestValues = new JsonArray();
         forgeData.mods.forEach((modId, value) -> {
            JsonObject mi = new JsonObject();
            mi.addProperty("modId", modId);
            mi.addProperty("modmarker", value);
            modTestValues.add(mi);
         });
         obj.add("mods", modTestValues);
         obj.addProperty("fmlNetworkVersion", forgeData.fmlNetworkVer);
         return obj;
      }
   }
}
