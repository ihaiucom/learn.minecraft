package net.minecraftforge.userdev;

import cpw.mods.modlauncher.api.INameMappingService;
import cpw.mods.modlauncher.api.INameMappingService.Domain;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import net.minecraftforge.fml.loading.LogMarkers;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MCPNamingService implements INameMappingService {
   private static final Logger LOGGER = LogManager.getLogger();
   private HashMap<String, String> methods;
   private HashMap<String, String> fields;

   public String mappingName() {
      return "srgtomcp";
   }

   public String mappingVersion() {
      return "1234";
   }

   public Entry<String, String> understanding() {
      return Pair.of("srg", "mcp");
   }

   public BiFunction<Domain, String, String> namingFunction() {
      return this::findMapping;
   }

   private String findMapping(Domain domain, String srgName) {
      switch(domain) {
      case CLASS:
         return srgName;
      case FIELD:
         return this.findFieldMapping(srgName);
      case METHOD:
         return this.findMethodMapping(srgName);
      default:
         return srgName;
      }
   }

   private String findMethodMapping(String origin) {
      if (this.methods == null) {
         HashMap<String, String> tmpmethods = new HashMap(1000);
         loadMappings("methods.csv", tmpmethods::put);
         this.methods = tmpmethods;
         LOGGER.debug(LogMarkers.CORE, "Loaded {} method mappings from methods.csv", this.methods.size());
      }

      return (String)this.methods.getOrDefault(origin, origin);
   }

   private String findFieldMapping(String origin) {
      if (this.fields == null) {
         HashMap<String, String> tmpfields = new HashMap(1000);
         loadMappings("fields.csv", tmpfields::put);
         this.fields = tmpfields;
         LOGGER.debug(LogMarkers.CORE, "Loaded {} field mappings from fields.csv", this.fields.size());
      }

      return (String)this.fields.getOrDefault(origin, origin);
   }

   private static void loadMappings(String mappingFileName, BiConsumer<String, String> mapStore) {
      URL path = ClassLoader.getSystemResource(mappingFileName);
      if (path != null) {
         try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(path.openStream()));
            Throwable var4 = null;

            try {
               reader.lines().skip(1L).map((e) -> {
                  return e.split(",");
               }).forEach((e) -> {
                  mapStore.accept(e[0], e[1]);
               });
            } catch (Throwable var14) {
               var4 = var14;
               throw var14;
            } finally {
               if (reader != null) {
                  if (var4 != null) {
                     try {
                        reader.close();
                     } catch (Throwable var13) {
                        var4.addSuppressed(var13);
                     }
                  } else {
                     reader.close();
                  }
               }

            }
         } catch (IOException var16) {
            LOGGER.error(LogMarkers.CORE, "Error reading mappings", var16);
         }

      }
   }
}
