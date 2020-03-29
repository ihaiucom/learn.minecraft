package net.minecraftforge.registries;

import com.google.common.base.Objects;
import net.minecraft.util.ResourceLocation;

final class RegistryDelegate<T> implements IRegistryDelegate<T> {
   private T referent;
   private ResourceLocation name;
   private final Class<T> type;

   public RegistryDelegate(T referent, Class<T> type) {
      this.referent = referent;
      this.type = type;
   }

   public T get() {
      return this.referent;
   }

   public ResourceLocation name() {
      return this.name;
   }

   public Class<T> type() {
      return this.type;
   }

   void changeReference(T newTarget) {
      this.referent = newTarget;
   }

   public void setName(ResourceLocation name) {
      this.name = name;
   }

   public boolean equals(Object obj) {
      if (obj instanceof RegistryDelegate) {
         RegistryDelegate<?> other = (RegistryDelegate)obj;
         return Objects.equal(other.name, this.name);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hashCode(new Object[]{this.name});
   }
}
