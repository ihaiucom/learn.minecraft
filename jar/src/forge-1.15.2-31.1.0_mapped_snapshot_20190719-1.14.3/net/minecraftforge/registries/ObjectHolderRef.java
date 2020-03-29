package net.minecraftforge.registries;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ResourceLocationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ObjectHolderRef implements Consumer<Predicate<ResourceLocation>> {
   private static final Logger LOGGER = LogManager.getLogger();
   private Field field;
   private ResourceLocation injectedObject;
   private boolean isValid;
   private ForgeRegistry<?> registry;

   public ObjectHolderRef(Field field, ResourceLocation injectedObject) {
      this(field, injectedObject.toString(), false);
   }

   ObjectHolderRef(Field field, String injectedObject, boolean extractFromExistingValues) {
      this.registry = this.getRegistryForType(field);
      this.field = field;
      this.isValid = this.registry != null;
      if (extractFromExistingValues) {
         try {
            Object existing = field.get((Object)null);
            if (!this.isValid || existing == null || existing == this.registry.getDefault()) {
               this.injectedObject = null;
               this.field = null;
               this.isValid = false;
               return;
            }

            this.injectedObject = ((IForgeRegistryEntry)existing).getRegistryName();
         } catch (IllegalAccessException var6) {
            throw new RuntimeException(var6);
         }
      } else {
         try {
            this.injectedObject = new ResourceLocation(injectedObject);
         } catch (ResourceLocationException var5) {
            throw new IllegalArgumentException("Invalid @ObjectHolder annotation on \"" + field.toString() + "\"", var5);
         }
      }

      if (this.injectedObject != null && this.isValid()) {
         field.setAccessible(true);
         if (Modifier.isFinal(field.getModifiers())) {
            throw new RuntimeException("@ObjectHolder on final field, our transformer did not run? " + field.getDeclaringClass().getName() + "/" + field.getName());
         }
      } else {
         throw new IllegalStateException(String.format("The ObjectHolder annotation cannot apply to a field that does not map to a registry. Ensure the registry was created during the RegistryEvent.NewRegistry event. (found : %s at %s.%s)", field.getType().getName(), field.getDeclaringClass().getName(), field.getName()));
      }
   }

   @Nullable
   private ForgeRegistry<?> getRegistryForType(Field field) {
      Queue<Class<?>> typesToExamine = new LinkedList();
      typesToExamine.add(field.getType());
      ForgeRegistry registry = null;

      while(!typesToExamine.isEmpty() && registry == null) {
         Class<?> type = (Class)typesToExamine.remove();
         Collections.addAll(typesToExamine, type.getInterfaces());
         if (IForgeRegistryEntry.class.isAssignableFrom(type)) {
            registry = (ForgeRegistry)RegistryManager.ACTIVE.getRegistry(type);
            Class<?> parentType = type.getSuperclass();
            if (parentType != null) {
               typesToExamine.add(parentType);
            }
         }
      }

      return registry;
   }

   public boolean isValid() {
      return this.isValid;
   }

   public void accept(Predicate<ResourceLocation> filter) {
      if (this.registry != null && filter.test(this.registry.getRegistryName())) {
         IForgeRegistryEntry thing;
         if (this.isValid && this.registry.containsKey(this.injectedObject) && !this.registry.isDummied(this.injectedObject)) {
            thing = this.registry.getValue(this.injectedObject);
         } else {
            thing = null;
         }

         if (thing == null) {
            LOGGER.debug("Unable to lookup {} for {}. This means the object wasn't registered. It's likely just mod options.", this.injectedObject, this.field);
         } else {
            try {
               this.field.set((Object)null, thing);
            } catch (ReflectiveOperationException | IllegalArgumentException var4) {
               LOGGER.warn("Unable to set {} with value {} ({})", this.field, thing, this.injectedObject, var4);
            }

         }
      }
   }

   public int hashCode() {
      return this.field.hashCode();
   }

   public boolean equals(Object other) {
      if (!(other instanceof ObjectHolderRef)) {
         return false;
      } else {
         ObjectHolderRef o = (ObjectHolderRef)other;
         return this.field.equals(o.field);
      }
   }
}
