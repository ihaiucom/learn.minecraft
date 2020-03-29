package net.minecraft.state;

import com.google.common.collect.ArrayTable;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import com.google.common.collect.UnmodifiableIterator;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

public abstract class StateHolder<O, S> implements IStateHolder<S> {
   private static final Function<Entry<IProperty<?>, Comparable<?>>, String> MAP_ENTRY_TO_STRING = new Function<Entry<IProperty<?>, Comparable<?>>, String>() {
      public String apply(@Nullable Entry<IProperty<?>, Comparable<?>> p_apply_1_) {
         if (p_apply_1_ == null) {
            return "<NULL>";
         } else {
            IProperty<?> lvt_2_1_ = (IProperty)p_apply_1_.getKey();
            return lvt_2_1_.getName() + "=" + this.getPropertyName(lvt_2_1_, (Comparable)p_apply_1_.getValue());
         }
      }

      private <T extends Comparable<T>> String getPropertyName(IProperty<T> p_185886_1_, Comparable<?> p_185886_2_) {
         return p_185886_1_.getName(p_185886_2_);
      }

      // $FF: synthetic method
      public Object apply(@Nullable Object p_apply_1_) {
         return this.apply((Entry)p_apply_1_);
      }
   };
   protected final O object;
   private final ImmutableMap<IProperty<?>, Comparable<?>> properties;
   private Table<IProperty<?>, Comparable<?>, S> propertyToStateMap;

   protected StateHolder(O p_i49008_1_, ImmutableMap<IProperty<?>, Comparable<?>> p_i49008_2_) {
      this.object = p_i49008_1_;
      this.properties = p_i49008_2_;
   }

   public <T extends Comparable<T>> S cycle(IProperty<T> p_177231_1_) {
      return this.with(p_177231_1_, (Comparable)cyclePropertyValue(p_177231_1_.getAllowedValues(), this.get(p_177231_1_)));
   }

   protected static <T> T cyclePropertyValue(Collection<T> p_177232_0_, T p_177232_1_) {
      Iterator lvt_2_1_ = p_177232_0_.iterator();

      do {
         if (!lvt_2_1_.hasNext()) {
            return lvt_2_1_.next();
         }
      } while(!lvt_2_1_.next().equals(p_177232_1_));

      if (lvt_2_1_.hasNext()) {
         return lvt_2_1_.next();
      } else {
         return p_177232_0_.iterator().next();
      }
   }

   public String toString() {
      StringBuilder lvt_1_1_ = new StringBuilder();
      lvt_1_1_.append(this.object);
      if (!this.getValues().isEmpty()) {
         lvt_1_1_.append('[');
         lvt_1_1_.append((String)this.getValues().entrySet().stream().map(MAP_ENTRY_TO_STRING).collect(Collectors.joining(",")));
         lvt_1_1_.append(']');
      }

      return lvt_1_1_.toString();
   }

   public Collection<IProperty<?>> getProperties() {
      return Collections.unmodifiableCollection(this.properties.keySet());
   }

   public <T extends Comparable<T>> boolean has(IProperty<T> p_196959_1_) {
      return this.properties.containsKey(p_196959_1_);
   }

   public <T extends Comparable<T>> T get(IProperty<T> p_177229_1_) {
      Comparable<?> lvt_2_1_ = (Comparable)this.properties.get(p_177229_1_);
      if (lvt_2_1_ == null) {
         throw new IllegalArgumentException("Cannot get property " + p_177229_1_ + " as it does not exist in " + this.object);
      } else {
         return (Comparable)p_177229_1_.getValueClass().cast(lvt_2_1_);
      }
   }

   public <T extends Comparable<T>, V extends T> S with(IProperty<T> p_206870_1_, V p_206870_2_) {
      Comparable<?> lvt_3_1_ = (Comparable)this.properties.get(p_206870_1_);
      if (lvt_3_1_ == null) {
         throw new IllegalArgumentException("Cannot set property " + p_206870_1_ + " as it does not exist in " + this.object);
      } else if (lvt_3_1_ == p_206870_2_) {
         return this;
      } else {
         S lvt_4_1_ = this.propertyToStateMap.get(p_206870_1_, p_206870_2_);
         if (lvt_4_1_ == null) {
            throw new IllegalArgumentException("Cannot set property " + p_206870_1_ + " to " + p_206870_2_ + " on " + this.object + ", it is not an allowed value");
         } else {
            return lvt_4_1_;
         }
      }
   }

   public void buildPropertyValueTable(Map<Map<IProperty<?>, Comparable<?>>, S> p_206874_1_) {
      if (this.propertyToStateMap != null) {
         throw new IllegalStateException();
      } else {
         Table<IProperty<?>, Comparable<?>, S> lvt_2_1_ = HashBasedTable.create();
         UnmodifiableIterator var3 = this.properties.entrySet().iterator();

         while(var3.hasNext()) {
            Entry<IProperty<?>, Comparable<?>> lvt_4_1_ = (Entry)var3.next();
            IProperty<?> lvt_5_1_ = (IProperty)lvt_4_1_.getKey();
            Iterator var6 = lvt_5_1_.getAllowedValues().iterator();

            while(var6.hasNext()) {
               Comparable<?> lvt_7_1_ = (Comparable)var6.next();
               if (lvt_7_1_ != lvt_4_1_.getValue()) {
                  lvt_2_1_.put(lvt_5_1_, lvt_7_1_, p_206874_1_.get(this.getPropertiesWithValue(lvt_5_1_, lvt_7_1_)));
               }
            }
         }

         this.propertyToStateMap = (Table)(lvt_2_1_.isEmpty() ? lvt_2_1_ : ArrayTable.create(lvt_2_1_));
      }
   }

   private Map<IProperty<?>, Comparable<?>> getPropertiesWithValue(IProperty<?> p_206875_1_, Comparable<?> p_206875_2_) {
      Map<IProperty<?>, Comparable<?>> lvt_3_1_ = Maps.newHashMap(this.properties);
      lvt_3_1_.put(p_206875_1_, p_206875_2_);
      return lvt_3_1_;
   }

   public ImmutableMap<IProperty<?>, Comparable<?>> getValues() {
      return this.properties;
   }
}
