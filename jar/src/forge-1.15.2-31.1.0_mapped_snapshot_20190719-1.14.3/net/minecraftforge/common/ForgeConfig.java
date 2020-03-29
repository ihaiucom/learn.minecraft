package net.minecraftforge.common;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.LogMarkers;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;

public class ForgeConfig {
   static final ForgeConfigSpec clientSpec;
   public static final ForgeConfig.Client CLIENT;
   static final ForgeConfigSpec serverSpec;
   public static final ForgeConfig.Server SERVER;

   @SubscribeEvent
   public static void onLoad(ModConfig.Loading configEvent) {
      LogManager.getLogger().debug(LogMarkers.FORGEMOD, "Loaded forge config file {}", configEvent.getConfig().getFileName());
   }

   @SubscribeEvent
   public static void onFileChange(ModConfig.Reloading configEvent) {
      LogManager.getLogger().debug(LogMarkers.FORGEMOD, "Forge config just got changed on the file system!");
   }

   static {
      Pair<ForgeConfig.Client, ForgeConfigSpec> specPair = (new ForgeConfigSpec.Builder()).configure(ForgeConfig.Client::new);
      clientSpec = (ForgeConfigSpec)specPair.getRight();
      CLIENT = (ForgeConfig.Client)specPair.getLeft();
      specPair = (new ForgeConfigSpec.Builder()).configure(ForgeConfig.Server::new);
      serverSpec = (ForgeConfigSpec)specPair.getRight();
      SERVER = (ForgeConfig.Server)specPair.getLeft();
   }

   public static class Client {
      public final ForgeConfigSpec.BooleanValue zoomInMissingModelTextInGui;
      public final ForgeConfigSpec.BooleanValue forgeCloudsEnabled;
      public final ForgeConfigSpec.BooleanValue disableStairSlabCulling;
      public final ForgeConfigSpec.BooleanValue alwaysSetupTerrainOffThread;
      public final ForgeConfigSpec.BooleanValue forgeLightPipelineEnabled;
      public final ForgeConfigSpec.BooleanValue selectiveResourceReloadEnabled;
      public final ForgeConfigSpec.BooleanValue showLoadWarnings;

      Client(ForgeConfigSpec.Builder builder) {
         builder.comment("Client only settings, mostly things related to rendering").push("client");
         this.zoomInMissingModelTextInGui = builder.comment("Toggle off to make missing model text in the gui fit inside the slot.").translation("forge.configgui.zoomInMissingModelTextInGui").define("zoomInMissingModelTextInGui", false);
         this.forgeCloudsEnabled = builder.comment("Enable uploading cloud geometry to the GPU for faster rendering.").translation("forge.configgui.forgeCloudsEnabled").define("forgeCloudsEnabled", true);
         this.disableStairSlabCulling = builder.comment("Disable culling of hidden faces next to stairs and slabs. Causes extra rendering, but may fix some resource packs that exploit this vanilla mechanic.").translation("forge.configgui.disableStairSlabCulling").define("disableStairSlabCulling", false);
         this.alwaysSetupTerrainOffThread = builder.comment("Enable Forge to queue all chunk updates to the Chunk Update thread.", "May increase FPS significantly, but may also cause weird rendering lag.", "Not recommended for computers without a significant number of cores available.").translation("forge.configgui.alwaysSetupTerrainOffThread").define("alwaysSetupTerrainOffThread", false);
         this.forgeLightPipelineEnabled = builder.comment("Enable the Forge block rendering pipeline - fixes the lighting of custom models.").translation("forge.configgui.forgeLightPipelineEnabled").define("forgeLightPipelineEnabled", true);
         this.selectiveResourceReloadEnabled = builder.comment("When enabled, makes specific reload tasks such as language changing quicker to run.").translation("forge.configgui.selectiveResourceReloadEnabled").define("selectiveResourceReloadEnabled", true);
         this.showLoadWarnings = builder.comment("When enabled, Forge will show any warnings that occurred during loading.").translation("forge.configgui.showLoadWarnings").define("showLoadWarnings", true);
         builder.pop();
      }
   }

   public static class Server {
      public final ForgeConfigSpec.BooleanValue removeErroringEntities;
      public final ForgeConfigSpec.BooleanValue removeErroringTileEntities;
      public final ForgeConfigSpec.BooleanValue fullBoundingBoxLadders;
      public final ForgeConfigSpec.DoubleValue zombieBaseSummonChance;
      public final ForgeConfigSpec.DoubleValue zombieBabyChance;
      public final ForgeConfigSpec.BooleanValue logCascadingWorldGeneration;
      public final ForgeConfigSpec.BooleanValue fixVanillaCascading;
      public final ForgeConfigSpec.IntValue dimensionUnloadQueueDelay;
      public final ForgeConfigSpec.IntValue clumpingThreshold;
      public final ForgeConfigSpec.BooleanValue treatEmptyTagsAsAir;

      Server(ForgeConfigSpec.Builder builder) {
         builder.comment("Server configuration settings").push("server");
         this.removeErroringEntities = builder.comment("Set this to true to remove any Entity that throws an error in its update method instead of closing the server and reporting a crash log. BE WARNED THIS COULD SCREW UP EVERYTHING USE SPARINGLY WE ARE NOT RESPONSIBLE FOR DAMAGES.").translation("forge.configgui.removeErroringEntities").worldRestart().define("removeErroringEntities", false);
         this.removeErroringTileEntities = builder.comment("Set this to true to remove any TileEntity that throws an error in its update method instead of closing the server and reporting a crash log. BE WARNED THIS COULD SCREW UP EVERYTHING USE SPARINGLY WE ARE NOT RESPONSIBLE FOR DAMAGES.").translation("forge.configgui.removeErroringTileEntities").worldRestart().define("removeErroringTileEntities", false);
         this.fullBoundingBoxLadders = builder.comment("Set this to true to check the entire entity's collision bounding box for ladders instead of just the block they are in. Causes noticeable differences in mechanics so default is vanilla behavior. Default: false.").translation("forge.configgui.fullBoundingBoxLadders").worldRestart().define("fullBoundingBoxLadders", false);
         this.zombieBaseSummonChance = builder.comment("Base zombie summoning spawn chance. Allows changing the bonus zombie summoning mechanic.").translation("forge.configgui.zombieBaseSummonChance").worldRestart().defineInRange("zombieBaseSummonChance", 0.1D, 0.0D, 1.0D);
         this.zombieBabyChance = builder.comment("Chance that a zombie (or subclass) is a baby. Allows changing the zombie spawning mechanic.").translation("forge.configgui.zombieBabyChance").worldRestart().defineInRange("zombieBabyChance", 0.05D, 0.0D, 1.0D);
         this.logCascadingWorldGeneration = builder.comment("Log cascading chunk generation issues during terrain population.").translation("forge.configgui.logCascadingWorldGeneration").define("logCascadingWorldGeneration", true);
         this.fixVanillaCascading = builder.comment("Fix vanilla issues that cause worldgen cascading. This DOES change vanilla worldgen so DO NOT report bugs related to world differences if this flag is on.").translation("forge.configgui.fixVanillaCascading").define("fixVanillaCascading", false);
         this.dimensionUnloadQueueDelay = builder.comment("The time in ticks the server will wait when a dimension was queued to unload. This can be useful when rapidly loading and unloading dimensions, like e.g. throwing items through a nether portal a few time per second.").translation("forge.configgui.dimensionUnloadQueueDelay").defineInRange((String)"dimensionUnloadQueueDelay", 0, 0, Integer.MAX_VALUE);
         this.clumpingThreshold = builder.comment("Controls the number threshold at which Packet51 is preferred over Packet52, default and minimum 64, maximum 1024.").translation("forge.configgui.clumpingThreshold").worldRestart().defineInRange((String)"clumpingThreshold", 64, 64, 1024);
         this.treatEmptyTagsAsAir = builder.comment("Vanilla will treat crafting recipes using empty tags as air, and allow you to craft with nothing in that slot. This changes empty tags to use BARRIER as the item. To prevent crafting with air.").translation("forge.configgui.treatEmptyTagsAsAir").define("treatEmptyTagsAsAir", false);
         builder.pop();
      }
   }
}
