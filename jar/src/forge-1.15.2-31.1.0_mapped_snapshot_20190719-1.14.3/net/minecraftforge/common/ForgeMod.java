package net.minecraftforge.common;

import com.google.common.collect.UnmodifiableIterator;
import java.util.Arrays;
import java.util.List;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.storage.SaveHandler;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.common.crafting.CompoundIngredient;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.NBTIngredient;
import net.minecraftforge.common.crafting.VanillaIngredientSerializer;
import net.minecraftforge.common.crafting.conditions.AndCondition;
import net.minecraftforge.common.crafting.conditions.FalseCondition;
import net.minecraftforge.common.crafting.conditions.ItemExistsCondition;
import net.minecraftforge.common.crafting.conditions.ModLoadedCondition;
import net.minecraftforge.common.crafting.conditions.NotCondition;
import net.minecraftforge.common.crafting.conditions.OrCondition;
import net.minecraftforge.common.crafting.conditions.TagEmptyCondition;
import net.minecraftforge.common.crafting.conditions.TrueCondition;
import net.minecraftforge.common.data.ForgeBlockTagsProvider;
import net.minecraftforge.common.data.ForgeItemTagsProvider;
import net.minecraftforge.common.data.ForgeRecipeProvider;
import net.minecraftforge.common.model.animation.CapabilityAnimation;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.FMLWorldPersistenceHook;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.VersionChecker;
import net.minecraftforge.fml.WorldPersistenceHooks;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLModIdMappingEvent;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.progress.StartupMessageManager;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.server.command.ConfigCommand;
import net.minecraftforge.server.command.ForgeCommand;
import net.minecraftforge.versions.forge.ForgeVersion;
import net.minecraftforge.versions.mcp.MCPVersion;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

@Mod("forge")
public class ForgeMod implements WorldPersistenceHooks.WorldPersistenceHook {
   public static final String VERSION_CHECK_CAT = "version_checking";
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Marker FORGEMOD = MarkerManager.getMarker("FORGEMOD");
   private static ForgeMod INSTANCE;

   public static ForgeMod getInstance() {
      return INSTANCE;
   }

   public ForgeMod() {
      LOGGER.info(FORGEMOD, "Forge mod loading, version {}, for MC {} with MCP {}", ForgeVersion.getVersion(), MCPVersion.getMCVersion(), MCPVersion.getMCPVersion());
      INSTANCE = this;
      MinecraftForge.initialize();
      WorldPersistenceHooks.addHook(this);
      WorldPersistenceHooks.addHook(new FMLWorldPersistenceHook());
      IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
      modEventBus.addListener(this::preInit);
      modEventBus.addListener(this::gatherData);
      modEventBus.register(this);
      MinecraftForge.EVENT_BUS.addListener(this::serverStarting);
      MinecraftForge.EVENT_BUS.addListener(this::serverStopping);
      MinecraftForge.EVENT_BUS.addGenericListener(SoundEvent.class, this::missingSoundMapping);
      ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ForgeConfig.clientSpec);
      ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ForgeConfig.serverSpec);
      modEventBus.register(ForgeConfig.class);
      ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () -> {
         return Pair.of(() -> {
            return "ANY";
         }, (remote, isServer) -> {
            return true;
         });
      });
      StartupMessageManager.addModMessage("Forge version " + ForgeVersion.getVersion());
   }

   public void preInit(FMLCommonSetupEvent evt) {
      CapabilityItemHandler.register();
      CapabilityFluidHandler.register();
      CapabilityAnimation.register();
      CapabilityEnergy.register();
      MinecraftForge.EVENT_BUS.addListener(VillagerTradingManager::loadTrades);
      MinecraftForge.EVENT_BUS.register(MinecraftForge.INTERNAL_HANDLER);
      MinecraftForge.EVENT_BUS.register(this);
      VersionChecker.startVersionCheck();
   }

   public void serverStarting(FMLServerStartingEvent evt) {
      new ForgeCommand(evt.getCommandDispatcher());
      ConfigCommand.register(evt.getCommandDispatcher());
   }

   public void serverStopping(FMLServerStoppingEvent evt) {
      WorldWorkerManager.clear();
   }

   public CompoundNBT getDataForWriting(SaveHandler handler, WorldInfo info) {
      CompoundNBT forgeData = new CompoundNBT();
      CompoundNBT dims = new CompoundNBT();
      DimensionManager.writeRegistry(dims);
      if (!dims.isEmpty()) {
         forgeData.put("dims", dims);
      }

      return forgeData;
   }

   public void readData(SaveHandler handler, WorldInfo info, CompoundNBT tag) {
      if (tag.contains("dims", 10)) {
         DimensionManager.readRegistry(tag.getCompound("dims"));
      }

   }

   public void mappingChanged(FMLModIdMappingEvent evt) {
   }

   public String getModId() {
      return "forge";
   }

   public void gatherData(GatherDataEvent event) {
      DataGenerator gen = event.getGenerator();
      if (event.includeServer()) {
         gen.addProvider(new ForgeBlockTagsProvider(gen));
         gen.addProvider(new ForgeItemTagsProvider(gen));
         gen.addProvider(new ForgeRecipeProvider(gen));
      }

   }

   public void missingSoundMapping(RegistryEvent.MissingMappings<SoundEvent> event) {
      List<String> removedSounds = Arrays.asList("entity.parrot.imitate.panda", "entity.parrot.imitate.zombie_pigman", "entity.parrot.imitate.enderman", "entity.parrot.imitate.polar_bear", "entity.parrot.imitate.wolf");
      UnmodifiableIterator var3 = event.getAllMappings().iterator();

      while(var3.hasNext()) {
         RegistryEvent.MissingMappings.Mapping<SoundEvent> mapping = (RegistryEvent.MissingMappings.Mapping)var3.next();
         ResourceLocation regName = mapping.key;
         if (regName != null && regName.getNamespace().equals("minecraft")) {
            String path = regName.getPath();
            if (removedSounds.stream().anyMatch((s) -> {
               return s.equals(path);
            })) {
               LOGGER.info("Ignoring removed minecraft sound {}", regName);
               mapping.ignore();
            }
         }
      }

   }

   @SubscribeEvent
   public void registerRecipeSerialziers(RegistryEvent.Register<IRecipeSerializer<?>> event) {
      CraftingHelper.register(AndCondition.Serializer.INSTANCE);
      CraftingHelper.register(FalseCondition.Serializer.INSTANCE);
      CraftingHelper.register(ItemExistsCondition.Serializer.INSTANCE);
      CraftingHelper.register(ModLoadedCondition.Serializer.INSTANCE);
      CraftingHelper.register(NotCondition.Serializer.INSTANCE);
      CraftingHelper.register(OrCondition.Serializer.INSTANCE);
      CraftingHelper.register(TrueCondition.Serializer.INSTANCE);
      CraftingHelper.register(TagEmptyCondition.Serializer.INSTANCE);
      CraftingHelper.register(new ResourceLocation("forge", "compound"), CompoundIngredient.Serializer.INSTANCE);
      CraftingHelper.register(new ResourceLocation("forge", "nbt"), NBTIngredient.Serializer.INSTANCE);
      CraftingHelper.register(new ResourceLocation("minecraft", "item"), VanillaIngredientSerializer.INSTANCE);
      event.getRegistry().register((new ConditionalRecipe.Serializer()).setRegistryName(new ResourceLocation("forge", "conditional")));
   }
}
