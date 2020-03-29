package net.minecraftforge.fml;

import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraftforge.fml.loading.FMLConfig;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.versions.mcp.MCPVersion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.maven.artifact.versioning.ComparableVersion;

public class VersionChecker {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final int MAX_HTTP_REDIRECTS = Integer.getInteger("http.maxRedirects", 20);
   private static Map<IModInfo, VersionChecker.CheckResult> results = new ConcurrentHashMap();
   private static final VersionChecker.CheckResult PENDING_CHECK;

   public static void startVersionCheck() {
      (new Thread("Forge Version Check") {
         public void run() {
            if (!FMLConfig.runVersionCheck()) {
               VersionChecker.LOGGER.info("Global Forge version check system disabled, no further processing.");
            } else {
               VersionChecker.gatherMods().forEach(this::process);
            }
         }

         private InputStream openUrlStream(URL url) throws IOException {
            URL currentUrl = url;
            int redirects = 0;

            URLConnection c;
            while(true) {
               if (redirects >= VersionChecker.MAX_HTTP_REDIRECTS) {
                  throw new IOException("Too many redirects while trying to fetch " + url);
               }

               c = currentUrl.openConnection();
               if (!(c instanceof HttpURLConnection)) {
                  break;
               }

               HttpURLConnection huc = (HttpURLConnection)c;
               huc.setInstanceFollowRedirects(false);
               int responseCode = huc.getResponseCode();
               if (responseCode < 300 || responseCode > 399) {
                  break;
               }

               try {
                  String loc = huc.getHeaderField("Location");
                  currentUrl = new URL(currentUrl, loc);
               } finally {
                  huc.disconnect();
               }

               ++redirects;
            }

            return c.getInputStream();
         }

         private void process(IModInfo mod) {
            VersionChecker.Status status = VersionChecker.Status.PENDING;
            ComparableVersion target = null;
            Map<ComparableVersion, String> changes = null;
            String display_url = null;

            try {
               URL url = mod.getUpdateURL();
               VersionChecker.LOGGER.info("[{}] Starting version check at {}", mod.getModId(), url.toString());
               InputStream con = this.openUrlStream(url);
               String data = new String(ByteStreams.toByteArray(con), StandardCharsets.UTF_8);
               con.close();
               VersionChecker.LOGGER.debug("[{}] Received version check data:\n{}", mod.getModId(), data);
               Map<String, Object> json = (Map)(new Gson()).fromJson(data, Map.class);
               Map<String, String> promos = (Map)json.get("promos");
               display_url = (String)json.get("homepage");
               String mcVersion = MCPVersion.getMCVersion();
               String rec = (String)promos.get(mcVersion + "-recommended");
               String lat = (String)promos.get(mcVersion + "-latest");
               ComparableVersion current = new ComparableVersion(mod.getVersion().toString());
               ComparableVersion latest;
               if (rec != null) {
                  latest = new ComparableVersion(rec);
                  int diff = latest.compareTo(current);
                  if (diff == 0) {
                     status = VersionChecker.Status.UP_TO_DATE;
                  } else if (diff < 0) {
                     status = VersionChecker.Status.AHEAD;
                     if (lat != null) {
                        ComparableVersion latestx = new ComparableVersion(lat);
                        if (current.compareTo(latestx) < 0) {
                           status = VersionChecker.Status.OUTDATED;
                           target = latestx;
                        }
                     }
                  } else {
                     status = VersionChecker.Status.OUTDATED;
                     target = latest;
                  }
               } else if (lat != null) {
                  latest = new ComparableVersion(lat);
                  if (current.compareTo(latest) < 0) {
                     status = VersionChecker.Status.BETA_OUTDATED;
                  } else {
                     status = VersionChecker.Status.BETA;
                  }

                  target = latest;
               } else {
                  status = VersionChecker.Status.BETA;
               }

               VersionChecker.LOGGER.info("[{}] Found status: {} Current: {} Target: {}", mod.getModId(), status, current, target);
               changes = new LinkedHashMap();
               Map<String, String> tmp = (Map)json.get(mcVersion);
               if (tmp != null) {
                  List<ComparableVersion> ordered = new ArrayList();
                  Iterator var23 = tmp.keySet().iterator();

                  label63:
                  while(true) {
                     ComparableVersion verx;
                     do {
                        do {
                           if (!var23.hasNext()) {
                              Collections.sort(ordered);
                              var23 = ordered.iterator();

                              while(var23.hasNext()) {
                                 ComparableVersion ver = (ComparableVersion)var23.next();
                                 changes.put(ver, tmp.get(ver.toString()));
                              }
                              break label63;
                           }

                           String key = (String)var23.next();
                           verx = new ComparableVersion(key);
                        } while(verx.compareTo(current) <= 0);
                     } while(target != null && verx.compareTo(target) >= 1);

                     ordered.add(verx);
                  }
               }
            } catch (Exception var20) {
               VersionChecker.LOGGER.warn("Failed to process update information", var20);
               status = VersionChecker.Status.FAILED;
            }

            VersionChecker.results.put(mod, new VersionChecker.CheckResult(status, target, changes, display_url));
         }
      }).start();
   }

   private static List<IModInfo> gatherMods() {
      List<IModInfo> ret = new LinkedList();
      Iterator var1 = ModList.get().getMods().iterator();

      while(var1.hasNext()) {
         ModInfo info = (ModInfo)var1.next();
         URL url = info.getUpdateURL();
         if (url != null) {
            ret.add(info);
         }
      }

      return ret;
   }

   public static VersionChecker.CheckResult getResult(IModInfo mod) {
      return (VersionChecker.CheckResult)results.getOrDefault(mod, PENDING_CHECK);
   }

   static {
      PENDING_CHECK = new VersionChecker.CheckResult(VersionChecker.Status.PENDING, (ComparableVersion)null, (Map)null, (String)null);
   }

   public static class CheckResult {
      @Nonnull
      public final VersionChecker.Status status;
      @Nullable
      public final ComparableVersion target;
      @Nullable
      public final Map<ComparableVersion, String> changes;
      @Nullable
      public final String url;

      private CheckResult(@Nonnull VersionChecker.Status status, @Nullable ComparableVersion target, @Nullable Map<ComparableVersion, String> changes, @Nullable String url) {
         this.status = status;
         this.target = target;
         this.changes = changes == null ? Collections.emptyMap() : Collections.unmodifiableMap(changes);
         this.url = url;
      }

      // $FF: synthetic method
      CheckResult(VersionChecker.Status x0, ComparableVersion x1, Map x2, String x3, Object x4) {
         this(x0, x1, x2, x3);
      }
   }

   public static enum Status {
      PENDING,
      FAILED,
      UP_TO_DATE,
      OUTDATED(3, true),
      AHEAD,
      BETA,
      BETA_OUTDATED(6, true);

      final int sheetOffset;
      final boolean draw;
      final boolean animated;

      private Status() {
         this(0, false, false);
      }

      private Status(int sheetOffset) {
         this(sheetOffset, true, false);
      }

      private Status(int sheetOffset, boolean animated) {
         this(sheetOffset, true, animated);
      }

      private Status(int sheetOffset, boolean draw, boolean animated) {
         this.sheetOffset = sheetOffset;
         this.draw = draw;
         this.animated = animated;
      }

      public int getSheetOffset() {
         return this.sheetOffset;
      }

      public boolean shouldDraw() {
         return this.draw;
      }

      public boolean isAnimated() {
         return this.animated;
      }
   }
}
