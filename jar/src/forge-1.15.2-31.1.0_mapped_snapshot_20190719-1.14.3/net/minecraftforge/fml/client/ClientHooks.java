package net.minecraftforge.fml.client;

import com.google.common.base.CharMatcher;
import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.MultiplayerScreen;
import net.minecraft.client.gui.screen.WorldSelectionScreen;
import net.minecraft.client.multiplayer.PlayerController;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.ServerStatusResponse;
import net.minecraft.resources.FallbackResourceManager;
import net.minecraft.resources.IResourcePack;
import net.minecraft.resources.ResourcePack;
import net.minecraft.resources.SimpleReloadableResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.storage.WorldSummary;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ForgeI18n;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.StartupQuery;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.packs.ModFileResourcePack;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.registries.GameData;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class ClientHooks {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Marker CLIENTHOOKS = MarkerManager.getMarker("CLIENTHOOKS");
   private static final String ALLOWED_CHARS = "ÀÁÂÈÊËÍÓÔÕÚßãõğİıŒœŞşŴŵžȇ\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000ÇüéâäàåçêëèïîìÄÅÉæÆôöòûùÿÖÜø£Ø×ƒáíóúñÑªº¿®¬½¼¡«»░▒▓│┤╡╢╖╕╣║╗╝╜╛┐└┴┬├─┼╞╟╚╔╩╦╠═╬╧╨╤╥╙╘╒╓╫╪┘┌█▄▌▐▀αβΓπΣσμτΦΘΩδ∞∅∈∩≡±≥≤⌠⌡÷≈°∙·√ⁿ²■\u0000";
   private static final CharMatcher DISALLOWED_CHAR_MATCHER = CharMatcher.anyOf("ÀÁÂÈÊËÍÓÔÕÚßãõğİıŒœŞşŴŵžȇ\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000ÇüéâäàåçêëèïîìÄÅÉæÆôöòûùÿÖÜø£Ø×ƒáíóúñÑªº¿®¬½¼¡«»░▒▓│┤╡╢╖╕╣║╗╝╜╛┐└┴┬├─┼╞╟╚╔╩╦╠═╬╧╨╤╥╙╘╒╓╫╪┘┌█▄▌▐▀αβΓπΣσμτΦΘΩδ∞∅∈∩≡±≥≤⌠⌡÷≈°∙·√ⁿ²■\u0000").negate();
   private static final ResourceLocation iconSheet = new ResourceLocation("forge", "textures/gui/icons.png");
   private static SetMultimap<String, ResourceLocation> missingTextures = HashMultimap.create();
   private static Set<String> badTextureDomains = Sets.newHashSet();
   private static Table<String, String, Set<ResourceLocation>> brokenTextures = HashBasedTable.create();

   @Nullable
   public static void processForgeListPingData(ServerStatusResponse packet, ServerData target) {
      if (packet.getForgeData() != null) {
         Map<String, String> mods = packet.getForgeData().getRemoteModData();
         Map<ResourceLocation, Pair<String, Boolean>> remoteChannels = packet.getForgeData().getRemoteChannels();
         int fmlver = packet.getForgeData().getFMLNetworkVersion();
         boolean fmlNetMatches = fmlver == 2;
         boolean channelsMatch = NetworkRegistry.checkListPingCompatibilityForClient(remoteChannels);
         AtomicBoolean result = new AtomicBoolean(true);
         ModList.get().forEachModContainer((modid, mc) -> {
            mc.getCustomExtension(ExtensionPoint.DISPLAYTEST).ifPresent((ext) -> {
               result.compareAndSet(true, ((BiPredicate)ext.getRight()).test(mods.get(modid), true));
            });
         });
         boolean modsMatch = result.get();
         Map<String, String> extraServerMods = (Map)mods.entrySet().stream().filter((e) -> {
            return !Objects.equals("OHNOES\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31", e.getValue());
         }).filter((e) -> {
            return !ModList.get().isLoaded((String)e.getKey());
         }).collect(Collectors.toMap(Entry::getKey, Entry::getValue));
         LOGGER.debug(CLIENTHOOKS, "Received FML ping data from server at {}: FMLNETVER={}, mod list is compatible : {}, channel list is compatible: {}, extra server mods: {}", target.serverIP, fmlver, modsMatch, channelsMatch, extraServerMods);
         String extraReason = null;
         if (!extraServerMods.isEmpty()) {
            extraReason = "fml.menu.multiplayer.extraservermods";
         }

         if (!modsMatch) {
            extraReason = "fml.menu.multiplayer.modsincompatible";
         }

         if (!channelsMatch) {
            extraReason = "fml.menu.multiplayer.networkincompatible";
         }

         if (fmlver < 2) {
            extraReason = "fml.menu.multiplayer.serveroutdated";
         }

         if (fmlver > 2) {
            extraReason = "fml.menu.multiplayer.clientoutdated";
         }

         target.forgeData = new ExtendedServerListData("FML", extraServerMods.isEmpty() && fmlNetMatches && channelsMatch && modsMatch, mods.size(), extraReason);
      } else {
         target.forgeData = new ExtendedServerListData("VANILLA", NetworkRegistry.canConnectToVanillaServer(), 0, (String)null);
      }

   }

   public static void drawForgePingInfo(MultiplayerScreen gui, ServerData target, int x, int y, int width, int relativeMouseX, int relativeMouseY) {
      if (target.forgeData != null) {
         String var9 = target.forgeData.type;
         byte var10 = -1;
         switch(var9.hashCode()) {
         case 69733:
            if (var9.equals("FML")) {
               var10 = 0;
            }
            break;
         case 951084891:
            if (var9.equals("VANILLA")) {
               var10 = 1;
            }
         }

         byte idx;
         String tooltip;
         switch(var10) {
         case 0:
            if (target.forgeData.isCompatible) {
               idx = 0;
               tooltip = ForgeI18n.parseMessage("fml.menu.multiplayer.compatible", target.forgeData.numberOfMods);
            } else {
               idx = 16;
               if (target.forgeData.extraReason != null) {
                  String extraReason = ForgeI18n.parseMessage(target.forgeData.extraReason);
                  tooltip = ForgeI18n.parseMessage("fml.menu.multiplayer.incompatible.extra", extraReason);
               } else {
                  tooltip = ForgeI18n.parseMessage("fml.menu.multiplayer.incompatible");
               }
            }
            break;
         case 1:
            if (target.forgeData.isCompatible) {
               idx = 48;
               tooltip = ForgeI18n.parseMessage("fml.menu.multiplayer.vanilla");
            } else {
               idx = 80;
               tooltip = ForgeI18n.parseMessage("fml.menu.multiplayer.vanilla.incompatible");
            }
            break;
         default:
            idx = 64;
            tooltip = ForgeI18n.parseMessage("fml.menu.multiplayer.unknown", target.forgeData.type);
         }

         Minecraft.getInstance().getTextureManager().bindTexture(iconSheet);
         AbstractGui.blit(x + width - 18, y + 10, 16, 16, 0.0F, (float)idx, 16, 16, 256, 256);
         if (relativeMouseX > width - 15 && relativeMouseX < width && relativeMouseY > 10 && relativeMouseY < 26) {
            gui.setHoveringText(tooltip);
         }

      }
   }

   public static String fixDescription(String description) {
      return description.endsWith(":NOFML§r") ? description.substring(0, description.length() - 8) + "§r" : description;
   }

   static File getSavesDir() {
      return new File(Minecraft.getInstance().gameDir, "saves");
   }

   public static void tryLoadExistingWorld(WorldSelectionScreen selectWorldGUI, WorldSummary comparator) {
      try {
         Minecraft.getInstance().launchIntegratedServer(comparator.getFileName(), comparator.getDisplayName(), (WorldSettings)null);
      } catch (StartupQuery.AbortedException var3) {
      }

   }

   private static NetworkManager getClientToServerNetworkManager() {
      return Minecraft.getInstance().getConnection() != null ? Minecraft.getInstance().getConnection().getNetworkManager() : null;
   }

   public static void handleClientWorldClosing(ClientWorld world) {
      NetworkManager client = getClientToServerNetworkManager();
      if (client != null && !client.isLocalChannel()) {
         GameData.revertToFrozen();
      }

   }

   public static String stripSpecialChars(String message) {
      return DISALLOWED_CHAR_MATCHER.removeFrom(StringUtils.stripControlCodes(message));
   }

   public static void trackMissingTexture(ResourceLocation resourceLocation) {
      badTextureDomains.add(resourceLocation.getNamespace());
      missingTextures.put(resourceLocation.getNamespace(), resourceLocation);
   }

   public static void trackBrokenTexture(ResourceLocation resourceLocation, String error) {
      badTextureDomains.add(resourceLocation.getNamespace());
      Set<ResourceLocation> badType = (Set)brokenTextures.get(resourceLocation.getNamespace(), error);
      if (badType == null) {
         badType = Sets.newHashSet();
         brokenTextures.put(resourceLocation.getNamespace(), MoreObjects.firstNonNull(error, "Unknown error"), badType);
      }

      ((Set)badType).add(resourceLocation);
   }

   public static void logMissingTextureErrors() {
      if (!missingTextures.isEmpty() || !brokenTextures.isEmpty()) {
         Logger logger = LogManager.getLogger("FML.TEXTURE_ERRORS");
         logger.error(Strings.repeat("+=", 25));
         logger.error("The following texture errors were found.");
         Map<String, FallbackResourceManager> resManagers = (Map)ObfuscationReflectionHelper.getPrivateValue(SimpleReloadableResourceManager.class, (SimpleReloadableResourceManager)Minecraft.getInstance().getResourceManager(), "field_199014_c");

         for(Iterator var2 = badTextureDomains.iterator(); var2.hasNext(); logger.error(Strings.repeat("=", 50))) {
            String resourceDomain = (String)var2.next();
            Set<ResourceLocation> missing = missingTextures.get(resourceDomain);
            logger.error(Strings.repeat("=", 50));
            logger.error("  DOMAIN {}", resourceDomain);
            logger.error(Strings.repeat("-", 50));
            logger.error("  domain {} is missing {} texture{}", resourceDomain, missing.size(), missing.size() != 1 ? "s" : "");
            FallbackResourceManager fallbackResourceManager = (FallbackResourceManager)resManagers.get(resourceDomain);
            Iterator var7;
            if (fallbackResourceManager == null) {
               logger.error("    domain {} is missing a resource manager - it is probably a side-effect of automatic texture processing", resourceDomain);
            } else {
               List<IResourcePack> resPacks = fallbackResourceManager.resourcePacks;
               logger.error("    domain {} has {} location{}:", resourceDomain, resPacks.size(), resPacks.size() != 1 ? "s" : "");
               var7 = resPacks.iterator();

               while(var7.hasNext()) {
                  IResourcePack resPack = (IResourcePack)var7.next();
                  if (resPack instanceof ModFileResourcePack) {
                     ModFileResourcePack modRP = (ModFileResourcePack)resPack;
                     List<IModInfo> mods = modRP.getModFile().getModInfos();
                     logger.error("      mod(s) {} resources at {}", mods.stream().map(IModInfo::getDisplayName).collect(Collectors.toList()), modRP.getModFile().getFilePath());
                  } else if (resPack instanceof ResourcePack) {
                     logger.error("      resource pack at path {}", ((ResourcePack)resPack).file.getPath());
                  } else {
                     logger.error("      unknown resourcepack type {} : {}", resPack.getClass().getName(), resPack.getName());
                  }
               }
            }

            logger.error(Strings.repeat("-", 25));
            if (missingTextures.containsKey(resourceDomain)) {
               logger.error("    The missing resources for domain {} are:", resourceDomain);
               Iterator var11 = missing.iterator();

               while(var11.hasNext()) {
                  ResourceLocation rl = (ResourceLocation)var11.next();
                  logger.error("      {}", rl.getPath());
               }

               logger.error(Strings.repeat("-", 25));
            }

            if (!brokenTextures.containsRow(resourceDomain)) {
               logger.error("    No other errors exist for domain {}", resourceDomain);
            } else {
               logger.error("    The following other errors were reported for domain {}:", resourceDomain);
               Map<String, Set<ResourceLocation>> resourceErrs = brokenTextures.row(resourceDomain);
               var7 = resourceErrs.keySet().iterator();

               while(var7.hasNext()) {
                  String error = (String)var7.next();
                  logger.error(Strings.repeat("-", 25));
                  logger.error("    Problem: {}", error);
                  Iterator var15 = ((Set)resourceErrs.get(error)).iterator();

                  while(var15.hasNext()) {
                     ResourceLocation rl = (ResourceLocation)var15.next();
                     logger.error("      {}", rl.getPath());
                  }
               }
            }
         }

         logger.error(Strings.repeat("+=", 25));
      }
   }

   public static void firePlayerLogin(PlayerController pc, ClientPlayerEntity player, NetworkManager networkManager) {
      MinecraftForge.EVENT_BUS.post(new ClientPlayerNetworkEvent.LoggedInEvent(pc, player, networkManager));
   }

   public static void firePlayerLogout(PlayerController pc, ClientPlayerEntity player) {
      MinecraftForge.EVENT_BUS.post(new ClientPlayerNetworkEvent.LoggedOutEvent(pc, player, player != null ? (player.connection != null ? player.connection.getNetworkManager() : null) : null));
   }

   public static void firePlayerRespawn(PlayerController pc, ClientPlayerEntity oldPlayer, ClientPlayerEntity newPlayer, NetworkManager networkManager) {
      MinecraftForge.EVENT_BUS.post(new ClientPlayerNetworkEvent.RespawnEvent(pc, oldPlayer, newPlayer, networkManager));
   }
}
