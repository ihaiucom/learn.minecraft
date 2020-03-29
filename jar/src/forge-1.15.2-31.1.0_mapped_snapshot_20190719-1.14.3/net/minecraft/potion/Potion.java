package net.minecraft.potion;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.UnmodifiableIterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class Potion extends ForgeRegistryEntry<Potion> {
   private final String baseName;
   private final ImmutableList<EffectInstance> effects;

   public static Potion getPotionTypeForName(String p_185168_0_) {
      return (Potion)Registry.POTION.getOrDefault(ResourceLocation.tryCreate(p_185168_0_));
   }

   public Potion(EffectInstance... p_i46739_1_) {
      this((String)null, p_i46739_1_);
   }

   public Potion(@Nullable String p_i46740_1_, EffectInstance... p_i46740_2_) {
      this.baseName = p_i46740_1_;
      this.effects = ImmutableList.copyOf(p_i46740_2_);
   }

   public String getNamePrefixed(String p_185174_1_) {
      return p_185174_1_ + (this.baseName == null ? Registry.POTION.getKey(this).getPath() : this.baseName);
   }

   public List<EffectInstance> getEffects() {
      return this.effects;
   }

   public boolean hasInstantEffect() {
      if (!this.effects.isEmpty()) {
         UnmodifiableIterator var1 = this.effects.iterator();

         while(var1.hasNext()) {
            EffectInstance effectinstance = (EffectInstance)var1.next();
            if (effectinstance.getPotion().isInstant()) {
               return true;
            }
         }
      }

      return false;
   }
}
