package net.minecraft.util;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class SoundEvent extends ForgeRegistryEntry<SoundEvent> {
   private final ResourceLocation name;

   public SoundEvent(ResourceLocation p_i46834_1_) {
      this.name = p_i46834_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public ResourceLocation getName() {
      return this.name;
   }
}
