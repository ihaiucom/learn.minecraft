package net.minecraft.util;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ClassInheritanceMultiMap<T> extends AbstractCollection<T> {
   private final Map<Class<?>, List<T>> map = Maps.newHashMap();
   private final Class<T> baseClass;
   private final List<T> values = Lists.newArrayList();

   public ClassInheritanceMultiMap(Class<T> p_i45909_1_) {
      this.baseClass = p_i45909_1_;
      this.map.put(p_i45909_1_, this.values);
   }

   public boolean add(T p_add_1_) {
      boolean lvt_2_1_ = false;
      Iterator var3 = this.map.entrySet().iterator();

      while(var3.hasNext()) {
         Entry<Class<?>, List<T>> lvt_4_1_ = (Entry)var3.next();
         if (((Class)lvt_4_1_.getKey()).isInstance(p_add_1_)) {
            lvt_2_1_ |= ((List)lvt_4_1_.getValue()).add(p_add_1_);
         }
      }

      return lvt_2_1_;
   }

   public boolean remove(Object p_remove_1_) {
      boolean lvt_2_1_ = false;
      Iterator var3 = this.map.entrySet().iterator();

      while(var3.hasNext()) {
         Entry<Class<?>, List<T>> lvt_4_1_ = (Entry)var3.next();
         if (((Class)lvt_4_1_.getKey()).isInstance(p_remove_1_)) {
            List<T> lvt_5_1_ = (List)lvt_4_1_.getValue();
            lvt_2_1_ |= lvt_5_1_.remove(p_remove_1_);
         }
      }

      return lvt_2_1_;
   }

   public boolean contains(Object p_contains_1_) {
      return this.func_219790_a(p_contains_1_.getClass()).contains(p_contains_1_);
   }

   public <S> Collection<S> func_219790_a(Class<S> p_219790_1_) {
      if (!this.baseClass.isAssignableFrom(p_219790_1_)) {
         throw new IllegalArgumentException("Don't know how to search for " + p_219790_1_);
      } else {
         List<T> lvt_2_1_ = (List)this.map.computeIfAbsent(p_219790_1_, (p_219791_1_) -> {
            Stream var10000 = this.values.stream();
            p_219791_1_.getClass();
            return (List)var10000.filter(p_219791_1_::isInstance).collect(Collectors.toList());
         });
         return Collections.unmodifiableCollection(lvt_2_1_);
      }
   }

   public Iterator<T> iterator() {
      return (Iterator)(this.values.isEmpty() ? Collections.emptyIterator() : Iterators.unmodifiableIterator(this.values.iterator()));
   }

   public int size() {
      return this.values.size();
   }
}
