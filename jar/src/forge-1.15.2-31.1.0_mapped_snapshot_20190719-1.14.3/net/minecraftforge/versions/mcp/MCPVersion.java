package net.minecraftforge.versions.mcp;

import net.minecraftforge.fml.Logging;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MCPVersion {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final String mcVersion;
   private static final String mcpVersion;

   public static String getMCVersion() {
      return mcVersion;
   }

   public static String getMCPVersion() {
      return mcpVersion;
   }

   public static String getMCPandMCVersion() {
      return mcVersion + "-" + mcpVersion;
   }

   static {
      String vers = MCPVersion.class.getPackage().getSpecificationVersion();
      if (vers == null) {
         vers = System.getenv("MC_VERSION");
      }

      if (vers == null) {
         throw new RuntimeException("Missing MC version, cannot continue");
      } else {
         mcVersion = vers;
         vers = MCPVersion.class.getPackage().getImplementationVersion();
         if (vers == null) {
            vers = System.getenv("MCP_VERSION");
         }

         if (vers == null) {
            throw new RuntimeException("Missing MCP version, cannot continue");
         } else {
            mcpVersion = vers;
            LOGGER.debug(Logging.CORE, "Found MC version information {}", mcVersion);
            LOGGER.debug(Logging.CORE, "Found MCP version information {}", mcpVersion);
         }
      }
   }
}
