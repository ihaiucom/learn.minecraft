package net.minecraftforge.fml.network;

import java.util.function.Function;

public enum ConnectionType {
   MODDED((s) -> {
      return Integer.valueOf(s.substring("FML".length()));
   }),
   VANILLA((s) -> {
      return 0;
   });

   private final Function<String, Integer> versionExtractor;

   private ConnectionType(Function<String, Integer> versionExtractor) {
      this.versionExtractor = versionExtractor;
   }

   public static ConnectionType forVersionFlag(String vers) {
      return vers.startsWith("FML") ? MODDED : VANILLA;
   }

   public int getFMLVersionNumber(String fmlVersion) {
      return (Integer)this.versionExtractor.apply(fmlVersion);
   }
}
