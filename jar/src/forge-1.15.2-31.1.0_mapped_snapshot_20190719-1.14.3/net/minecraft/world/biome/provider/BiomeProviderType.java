package net.minecraft.world.biome.provider;

import java.util.function.Function;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class BiomeProviderType<C extends IBiomeProviderSettings, T extends BiomeProvider> extends ForgeRegistryEntry<BiomeProviderType<?, ?>> {
   public static final BiomeProviderType<CheckerboardBiomeProviderSettings, CheckerboardBiomeProvider> CHECKERBOARD = func_226841_a_("checkerboard", CheckerboardBiomeProvider::new, CheckerboardBiomeProviderSettings::new);
   public static final BiomeProviderType<SingleBiomeProviderSettings, SingleBiomeProvider> FIXED = func_226841_a_("fixed", SingleBiomeProvider::new, SingleBiomeProviderSettings::new);
   public static final BiomeProviderType<OverworldBiomeProviderSettings, OverworldBiomeProvider> VANILLA_LAYERED = func_226841_a_("vanilla_layered", OverworldBiomeProvider::new, OverworldBiomeProviderSettings::new);
   public static final BiomeProviderType<EndBiomeProviderSettings, EndBiomeProvider> THE_END = func_226841_a_("the_end", EndBiomeProvider::new, EndBiomeProviderSettings::new);
   private final Function<C, T> factory;
   private final Function<WorldInfo, C> settingsFactory;

   private static <C extends IBiomeProviderSettings, T extends BiomeProvider> BiomeProviderType<C, T> func_226841_a_(String p_226841_0_, Function<C, T> p_226841_1_, Function<WorldInfo, C> p_226841_2_) {
      return (BiomeProviderType)Registry.register((Registry)Registry.BIOME_SOURCE_TYPE, (String)p_226841_0_, (Object)(new BiomeProviderType(p_226841_1_, p_226841_2_)));
   }

   private BiomeProviderType(Function<C, T> p_i225746_1_, Function<WorldInfo, C> p_i225746_2_) {
      this.factory = p_i225746_1_;
      this.settingsFactory = p_i225746_2_;
   }

   public T create(C p_205457_1_) {
      return (BiomeProvider)this.factory.apply(p_205457_1_);
   }

   public C func_226840_a_(WorldInfo p_226840_1_) {
      return (IBiomeProviderSettings)this.settingsFactory.apply(p_226840_1_);
   }
}
