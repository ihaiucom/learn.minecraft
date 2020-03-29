package net.minecraft.world.gen;

import java.util.function.Supplier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class ChunkGeneratorType<C extends GenerationSettings, T extends ChunkGenerator<C>> extends ForgeRegistryEntry<ChunkGeneratorType<?, ?>> implements IChunkGeneratorFactory<C, T> {
   public static final ChunkGeneratorType<OverworldGenSettings, OverworldChunkGenerator> SURFACE = register("surface", OverworldChunkGenerator::new, OverworldGenSettings::new, true);
   public static final ChunkGeneratorType<NetherGenSettings, NetherChunkGenerator> CAVES = register("caves", NetherChunkGenerator::new, NetherGenSettings::new, true);
   public static final ChunkGeneratorType<EndGenerationSettings, EndChunkGenerator> FLOATING_ISLANDS = register("floating_islands", EndChunkGenerator::new, EndGenerationSettings::new, true);
   public static final ChunkGeneratorType<DebugGenerationSettings, DebugChunkGenerator> DEBUG = register("debug", DebugChunkGenerator::new, DebugGenerationSettings::new, false);
   public static final ChunkGeneratorType<FlatGenerationSettings, FlatChunkGenerator> FLAT = register("flat", FlatChunkGenerator::new, FlatGenerationSettings::new, false);
   private final IChunkGeneratorFactory<C, T> factory;
   private final boolean isOptionForBuffetWorld;
   private final Supplier<C> settings;

   private static <C extends GenerationSettings, T extends ChunkGenerator<C>> ChunkGeneratorType<C, T> register(String p_212676_0_, IChunkGeneratorFactory<C, T> p_212676_1_, Supplier<C> p_212676_2_, boolean p_212676_3_) {
      return (ChunkGeneratorType)Registry.register((Registry)Registry.CHUNK_GENERATOR_TYPE, (String)p_212676_0_, (Object)(new ChunkGeneratorType(p_212676_1_, p_212676_3_, p_212676_2_)));
   }

   public ChunkGeneratorType(IChunkGeneratorFactory<C, T> p_i49953_1_, boolean p_i49953_2_, Supplier<C> p_i49953_3_) {
      this.factory = p_i49953_1_;
      this.isOptionForBuffetWorld = p_i49953_2_;
      this.settings = p_i49953_3_;
   }

   public T create(World p_create_1_, BiomeProvider p_create_2_, C p_create_3_) {
      return this.factory.create(p_create_1_, p_create_2_, p_create_3_);
   }

   public C createSettings() {
      return (GenerationSettings)this.settings.get();
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isOptionForBuffetWorld() {
      return this.isOptionForBuffetWorld;
   }
}
