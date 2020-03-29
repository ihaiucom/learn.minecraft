package net.minecraft.world.gen.surfacebuilders;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;
import net.minecraftforge.registries.ForgeRegistryEntry;

public abstract class SurfaceBuilder<C extends ISurfaceBuilderConfig> extends ForgeRegistryEntry<SurfaceBuilder<?>> {
   public static final BlockState AIR;
   public static final BlockState DIRT;
   public static final BlockState GRASS_BLOCK;
   public static final BlockState PODZOL;
   public static final BlockState GRAVEL;
   public static final BlockState STONE;
   public static final BlockState COARSE_DIRT;
   public static final BlockState SAND;
   public static final BlockState RED_SAND;
   public static final BlockState WHITE_TERRACOTTA;
   public static final BlockState MYCELIUM;
   public static final BlockState NETHERRACK;
   public static final BlockState END_STONE;
   public static final SurfaceBuilderConfig AIR_CONFIG;
   public static final SurfaceBuilderConfig PODZOL_DIRT_GRAVEL_CONFIG;
   public static final SurfaceBuilderConfig GRAVEL_CONFIG;
   public static final SurfaceBuilderConfig GRASS_DIRT_GRAVEL_CONFIG;
   public static final SurfaceBuilderConfig DIRT_DIRT_GRAVEL_CONFIG;
   public static final SurfaceBuilderConfig STONE_STONE_GRAVEL_CONFIG;
   public static final SurfaceBuilderConfig CORASE_DIRT_DIRT_GRAVEL_CONFIG;
   public static final SurfaceBuilderConfig SAND_SAND_GRAVEL_CONFIG;
   public static final SurfaceBuilderConfig GRASS_DIRT_SAND_CONFIG;
   public static final SurfaceBuilderConfig SAND_CONFIG;
   public static final SurfaceBuilderConfig RED_SAND_WHITE_TERRACOTTA_GRAVEL_CONFIG;
   public static final SurfaceBuilderConfig MYCELIUM_DIRT_GRAVEL_CONFIG;
   public static final SurfaceBuilderConfig NETHERRACK_CONFIG;
   public static final SurfaceBuilderConfig END_STONE_CONFIG;
   public static final SurfaceBuilder<SurfaceBuilderConfig> DEFAULT;
   public static final SurfaceBuilder<SurfaceBuilderConfig> MOUNTAIN;
   public static final SurfaceBuilder<SurfaceBuilderConfig> SHATTERED_SAVANNA;
   public static final SurfaceBuilder<SurfaceBuilderConfig> GRAVELLY_MOUNTAIN;
   public static final SurfaceBuilder<SurfaceBuilderConfig> GIANT_TREE_TAIGA;
   public static final SurfaceBuilder<SurfaceBuilderConfig> SWAMP;
   public static final SurfaceBuilder<SurfaceBuilderConfig> BADLANDS;
   public static final SurfaceBuilder<SurfaceBuilderConfig> WOODED_BADLANDS;
   public static final SurfaceBuilder<SurfaceBuilderConfig> ERODED_BADLANDS;
   public static final SurfaceBuilder<SurfaceBuilderConfig> FROZEN_OCEAN;
   public static final SurfaceBuilder<SurfaceBuilderConfig> NETHER;
   public static final SurfaceBuilder<SurfaceBuilderConfig> NOPE;
   private final Function<Dynamic<?>, ? extends C> field_215408_a;

   private static <C extends ISurfaceBuilderConfig, F extends SurfaceBuilder<C>> F register(String p_215389_0_, F p_215389_1_) {
      return (SurfaceBuilder)Registry.register((Registry)Registry.SURFACE_BUILDER, (String)p_215389_0_, (Object)p_215389_1_);
   }

   public SurfaceBuilder(Function<Dynamic<?>, ? extends C> p_i51305_1_) {
      this.field_215408_a = p_i51305_1_;
   }

   public abstract void buildSurface(Random var1, IChunk var2, Biome var3, int var4, int var5, int var6, double var7, BlockState var9, BlockState var10, int var11, long var12, C var14);

   public void setSeed(long p_205548_1_) {
   }

   static {
      AIR = Blocks.AIR.getDefaultState();
      DIRT = Blocks.DIRT.getDefaultState();
      GRASS_BLOCK = Blocks.GRASS_BLOCK.getDefaultState();
      PODZOL = Blocks.PODZOL.getDefaultState();
      GRAVEL = Blocks.GRAVEL.getDefaultState();
      STONE = Blocks.STONE.getDefaultState();
      COARSE_DIRT = Blocks.COARSE_DIRT.getDefaultState();
      SAND = Blocks.SAND.getDefaultState();
      RED_SAND = Blocks.RED_SAND.getDefaultState();
      WHITE_TERRACOTTA = Blocks.WHITE_TERRACOTTA.getDefaultState();
      MYCELIUM = Blocks.MYCELIUM.getDefaultState();
      NETHERRACK = Blocks.NETHERRACK.getDefaultState();
      END_STONE = Blocks.END_STONE.getDefaultState();
      AIR_CONFIG = new SurfaceBuilderConfig(AIR, AIR, AIR);
      PODZOL_DIRT_GRAVEL_CONFIG = new SurfaceBuilderConfig(PODZOL, DIRT, GRAVEL);
      GRAVEL_CONFIG = new SurfaceBuilderConfig(GRAVEL, GRAVEL, GRAVEL);
      GRASS_DIRT_GRAVEL_CONFIG = new SurfaceBuilderConfig(GRASS_BLOCK, DIRT, GRAVEL);
      DIRT_DIRT_GRAVEL_CONFIG = new SurfaceBuilderConfig(DIRT, DIRT, GRAVEL);
      STONE_STONE_GRAVEL_CONFIG = new SurfaceBuilderConfig(STONE, STONE, GRAVEL);
      CORASE_DIRT_DIRT_GRAVEL_CONFIG = new SurfaceBuilderConfig(COARSE_DIRT, DIRT, GRAVEL);
      SAND_SAND_GRAVEL_CONFIG = new SurfaceBuilderConfig(SAND, SAND, GRAVEL);
      GRASS_DIRT_SAND_CONFIG = new SurfaceBuilderConfig(GRASS_BLOCK, DIRT, SAND);
      SAND_CONFIG = new SurfaceBuilderConfig(SAND, SAND, SAND);
      RED_SAND_WHITE_TERRACOTTA_GRAVEL_CONFIG = new SurfaceBuilderConfig(RED_SAND, WHITE_TERRACOTTA, GRAVEL);
      MYCELIUM_DIRT_GRAVEL_CONFIG = new SurfaceBuilderConfig(MYCELIUM, DIRT, GRAVEL);
      NETHERRACK_CONFIG = new SurfaceBuilderConfig(NETHERRACK, NETHERRACK, NETHERRACK);
      END_STONE_CONFIG = new SurfaceBuilderConfig(END_STONE, END_STONE, END_STONE);
      DEFAULT = register("default", new DefaultSurfaceBuilder(SurfaceBuilderConfig::deserialize));
      MOUNTAIN = register("mountain", new MountainSurfaceBuilder(SurfaceBuilderConfig::deserialize));
      SHATTERED_SAVANNA = register("shattered_savanna", new ShatteredSavannaSurfaceBuilder(SurfaceBuilderConfig::deserialize));
      GRAVELLY_MOUNTAIN = register("gravelly_mountain", new GravellyMountainSurfaceBuilder(SurfaceBuilderConfig::deserialize));
      GIANT_TREE_TAIGA = register("giant_tree_taiga", new GiantTreeTaigaSurfaceBuilder(SurfaceBuilderConfig::deserialize));
      SWAMP = register("swamp", new SwampSurfaceBuilder(SurfaceBuilderConfig::deserialize));
      BADLANDS = register("badlands", new BadlandsSurfaceBuilder(SurfaceBuilderConfig::deserialize));
      WOODED_BADLANDS = register("wooded_badlands", new WoodedBadlandsSurfaceBuilder(SurfaceBuilderConfig::deserialize));
      ERODED_BADLANDS = register("eroded_badlands", new ErodedBadlandsSurfaceBuilder(SurfaceBuilderConfig::deserialize));
      FROZEN_OCEAN = register("frozen_ocean", new FrozenOceanSurfaceBuilder(SurfaceBuilderConfig::deserialize));
      NETHER = register("nether", new NetherSurfaceBuilder(SurfaceBuilderConfig::deserialize));
      NOPE = register("nope", new NoopSurfaceBuilder(SurfaceBuilderConfig::deserialize));
   }
}
