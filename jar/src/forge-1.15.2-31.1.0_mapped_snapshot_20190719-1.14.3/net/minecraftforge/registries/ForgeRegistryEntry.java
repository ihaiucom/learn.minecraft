package net.minecraftforge.registries;

import com.google.common.reflect.TypeToken;
import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;

public abstract class ForgeRegistryEntry<V extends IForgeRegistryEntry<V>> implements IForgeRegistryEntry<V> {
   private final TypeToken<V> token = new TypeToken<V>(this.getClass()) {
   };
   public final IRegistryDelegate<V> delegate;
   private ResourceLocation registryName;

   public ForgeRegistryEntry() {
      this.delegate = new RegistryDelegate(this, this.token.getRawType());
      this.registryName = null;
   }

   public final V setRegistryName(String name) {
      if (this.getRegistryName() != null) {
         throw new IllegalStateException("Attempted to set registry name with existing registry name! New: " + name + " Old: " + this.getRegistryName());
      } else {
         this.registryName = GameData.checkPrefix(name, true);
         return this;
      }
   }

   public final V setRegistryName(ResourceLocation name) {
      return this.setRegistryName(name.toString());
   }

   public final V setRegistryName(String modID, String name) {
      return this.setRegistryName(modID + ":" + name);
   }

   @Nullable
   public final ResourceLocation getRegistryName() {
      if (this.delegate.name() != null) {
         return this.delegate.name();
      } else {
         return this.registryName != null ? this.registryName : null;
      }
   }

   public final Class<V> getRegistryType() {
      return this.token.getRawType();
   }
}
