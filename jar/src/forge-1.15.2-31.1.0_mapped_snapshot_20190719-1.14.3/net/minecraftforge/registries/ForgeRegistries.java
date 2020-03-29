package net.minecraftforge.registries;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.schedule.Activity;
import net.minecraft.entity.ai.brain.schedule.Schedule;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.item.PaintingType;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.particles.ParticleType;
import net.minecraft.potion.Effect;
import net.minecraft.potion.Potion;
import net.minecraft.stats.StatType;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.registry.Bootstrap;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProviderType;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.gen.ChunkGeneratorType;
import net.minecraft.world.gen.carver.WorldCarver;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;
import net.minecraftforge.common.ModDimension;

public class ForgeRegistries {
   public static final IForgeRegistry<Block> BLOCKS;
   public static final IForgeRegistry<Fluid> FLUIDS;
   public static final IForgeRegistry<Item> ITEMS;
   public static final IForgeRegistry<Effect> POTIONS;
   public static final IForgeRegistry<Biome> BIOMES;
   public static final IForgeRegistry<SoundEvent> SOUND_EVENTS;
   public static final IForgeRegistry<Potion> POTION_TYPES;
   public static final IForgeRegistry<Enchantment> ENCHANTMENTS;
   public static final IForgeRegistry<EntityType<?>> ENTITIES;
   public static final IForgeRegistry<TileEntityType<?>> TILE_ENTITIES;
   public static final IForgeRegistry<ParticleType<?>> PARTICLE_TYPES;
   public static final IForgeRegistry<ContainerType<?>> CONTAINERS;
   public static final IForgeRegistry<PaintingType> PAINTING_TYPES;
   public static final IForgeRegistry<IRecipeSerializer<?>> RECIPE_SERIALIZERS;
   public static final IForgeRegistry<StatType<?>> STAT_TYPES;
   public static final IForgeRegistry<VillagerProfession> PROFESSIONS;
   public static final IForgeRegistry<PointOfInterestType> POI_TYPES;
   public static final IForgeRegistry<MemoryModuleType<?>> MEMORY_MODULE_TYPES;
   public static final IForgeRegistry<SensorType<?>> SENSOR_TYPES;
   public static final IForgeRegistry<Schedule> SCHEDULES;
   public static final IForgeRegistry<Activity> ACTIVITIES;
   public static final IForgeRegistry<WorldCarver<?>> WORLD_CARVERS;
   public static final IForgeRegistry<SurfaceBuilder<?>> SURFACE_BUILDERS;
   public static final IForgeRegistry<Feature<?>> FEATURES;
   public static final IForgeRegistry<Placement<?>> DECORATORS;
   public static final IForgeRegistry<BiomeProviderType<?, ?>> BIOME_PROVIDER_TYPES;
   public static final IForgeRegistry<ChunkGeneratorType<?, ?>> CHUNK_GENERATOR_TYPES;
   public static final IForgeRegistry<ChunkStatus> CHUNK_STATUS;
   public static final IForgeRegistry<ModDimension> MOD_DIMENSIONS;
   public static final IForgeRegistry<DataSerializerEntry> DATA_SERIALIZERS;

   private static void init() {
      GameData.init();
      Bootstrap.register();
   }

   static {
      init();
      BLOCKS = RegistryManager.ACTIVE.getRegistry(Block.class);
      FLUIDS = RegistryManager.ACTIVE.getRegistry(Fluid.class);
      ITEMS = RegistryManager.ACTIVE.getRegistry(Item.class);
      POTIONS = RegistryManager.ACTIVE.getRegistry(Effect.class);
      BIOMES = RegistryManager.ACTIVE.getRegistry(Biome.class);
      SOUND_EVENTS = RegistryManager.ACTIVE.getRegistry(SoundEvent.class);
      POTION_TYPES = RegistryManager.ACTIVE.getRegistry(Potion.class);
      ENCHANTMENTS = RegistryManager.ACTIVE.getRegistry(Enchantment.class);
      ENTITIES = RegistryManager.ACTIVE.getRegistry(EntityType.class);
      TILE_ENTITIES = RegistryManager.ACTIVE.getRegistry(TileEntityType.class);
      PARTICLE_TYPES = RegistryManager.ACTIVE.getRegistry(ParticleType.class);
      CONTAINERS = RegistryManager.ACTIVE.getRegistry(ContainerType.class);
      PAINTING_TYPES = RegistryManager.ACTIVE.getRegistry(PaintingType.class);
      RECIPE_SERIALIZERS = RegistryManager.ACTIVE.getRegistry(IRecipeSerializer.class);
      STAT_TYPES = RegistryManager.ACTIVE.getRegistry(StatType.class);
      PROFESSIONS = RegistryManager.ACTIVE.getRegistry(VillagerProfession.class);
      POI_TYPES = RegistryManager.ACTIVE.getRegistry(PointOfInterestType.class);
      MEMORY_MODULE_TYPES = RegistryManager.ACTIVE.getRegistry(MemoryModuleType.class);
      SENSOR_TYPES = RegistryManager.ACTIVE.getRegistry(SensorType.class);
      SCHEDULES = RegistryManager.ACTIVE.getRegistry(Schedule.class);
      ACTIVITIES = RegistryManager.ACTIVE.getRegistry(Activity.class);
      WORLD_CARVERS = RegistryManager.ACTIVE.getRegistry(WorldCarver.class);
      SURFACE_BUILDERS = RegistryManager.ACTIVE.getRegistry(SurfaceBuilder.class);
      FEATURES = RegistryManager.ACTIVE.getRegistry(Feature.class);
      DECORATORS = RegistryManager.ACTIVE.getRegistry(Placement.class);
      BIOME_PROVIDER_TYPES = RegistryManager.ACTIVE.getRegistry(BiomeProviderType.class);
      CHUNK_GENERATOR_TYPES = RegistryManager.ACTIVE.getRegistry(ChunkGeneratorType.class);
      CHUNK_STATUS = RegistryManager.ACTIVE.getRegistry(ChunkStatus.class);
      MOD_DIMENSIONS = RegistryManager.ACTIVE.getRegistry(ModDimension.class);
      DATA_SERIALIZERS = RegistryManager.ACTIVE.getRegistry(DataSerializerEntry.class);
   }
}
