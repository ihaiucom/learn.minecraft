package net.minecraft.util.registry;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.util.IntIdentityHashBiMap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SimpleRegistry<T> extends MutableRegistry<T> {
   protected static final Logger LOGGER0 = LogManager.getLogger();
   protected final IntIdentityHashBiMap<T> underlyingIntegerMap = new IntIdentityHashBiMap(256);
   protected final BiMap<ResourceLocation, T> registryObjects = HashBiMap.create();
   protected Object[] values;
   private int nextFreeId;

   public <V extends T> V register(int p_218382_1_, ResourceLocation p_218382_2_, V p_218382_3_) {
      this.underlyingIntegerMap.put(p_218382_3_, p_218382_1_);
      Validate.notNull(p_218382_2_);
      Validate.notNull(p_218382_3_);
      this.values = null;
      if (this.registryObjects.containsKey(p_218382_2_)) {
         LOGGER0.debug("Adding duplicate key '{}' to registry", p_218382_2_);
      }

      this.registryObjects.put(p_218382_2_, p_218382_3_);
      if (this.nextFreeId <= p_218382_1_) {
         this.nextFreeId = p_218382_1_ + 1;
      }

      return p_218382_3_;
   }

   public <V extends T> V register(ResourceLocation p_218381_1_, V p_218381_2_) {
      return this.register(this.nextFreeId, p_218381_1_, p_218381_2_);
   }

   @Nullable
   public ResourceLocation getKey(T p_177774_1_) {
      return (ResourceLocation)this.registryObjects.inverse().get(p_177774_1_);
   }

   public int getId(@Nullable T p_148757_1_) {
      return this.underlyingIntegerMap.getId(p_148757_1_);
   }

   @Nullable
   public T getByValue(int p_148745_1_) {
      return this.underlyingIntegerMap.getByValue(p_148745_1_);
   }

   public Iterator<T> iterator() {
      return this.underlyingIntegerMap.iterator();
   }

   @Nullable
   public T getOrDefault(@Nullable ResourceLocation p_82594_1_) {
      return this.registryObjects.get(p_82594_1_);
   }

   public Optional<T> getValue(@Nullable ResourceLocation p_218349_1_) {
      return Optional.ofNullable(this.registryObjects.get(p_218349_1_));
   }

   public Set<ResourceLocation> keySet() {
      return Collections.unmodifiableSet(this.registryObjects.keySet());
   }

   public boolean isEmpty() {
      return this.registryObjects.isEmpty();
   }

   @Nullable
   public T getRandom(Random p_186801_1_) {
      if (this.values == null) {
         Collection<?> lvt_2_1_ = this.registryObjects.values();
         if (lvt_2_1_.isEmpty()) {
            return null;
         }

         this.values = lvt_2_1_.toArray(new Object[lvt_2_1_.size()]);
      }

      return this.values[p_186801_1_.nextInt(this.values.length)];
   }

   @OnlyIn(Dist.CLIENT)
   public boolean containsKey(ResourceLocation p_212607_1_) {
      return this.registryObjects.containsKey(p_212607_1_);
   }
}
