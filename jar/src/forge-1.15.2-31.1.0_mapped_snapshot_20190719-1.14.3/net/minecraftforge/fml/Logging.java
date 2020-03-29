package net.minecraftforge.fml;

import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class Logging {
   public static final Marker CORE = MarkerManager.getMarker("CORE");
   public static final Marker LOADING = MarkerManager.getMarker("LOADING");
   public static final Marker SCAN = MarkerManager.getMarker("SCAN");
   public static final Marker SPLASH = MarkerManager.getMarker("SPLASH");
   public static final Marker CAPABILITIES = MarkerManager.getMarker("CAPABILITIES");
   public static final Marker MODELLOADING = MarkerManager.getMarker("MODELLOADING");
}
