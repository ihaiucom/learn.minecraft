package net.minecraft.state;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.UnmodifiableIterator;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.util.MapPopulator;

public class StateContainer<O, S extends IStateHolder<S>> {
   private static final Pattern NAME_PATTERN = Pattern.compile("^[a-z0-9_]+$");
   private final O owner;
   private final ImmutableSortedMap<String, IProperty<?>> properties;
   private final ImmutableList<S> validStates;

   protected <A extends StateHolder<O, S>> StateContainer(O p_i49005_1_, StateContainer.IFactory<O, S, A> p_i49005_2_, Map<String, IProperty<?>> p_i49005_3_) {
      this.owner = p_i49005_1_;
      this.properties = ImmutableSortedMap.copyOf(p_i49005_3_);
      Map<Map<IProperty<?>, Comparable<?>>, A> lvt_4_1_ = Maps.newLinkedHashMap();
      List<A> lvt_5_1_ = Lists.newArrayList();
      Stream<List<Comparable<?>>> lvt_6_1_ = Stream.of(Collections.emptyList());

      IProperty lvt_8_1_;
      for(UnmodifiableIterator var7 = this.properties.values().iterator(); var7.hasNext(); lvt_6_1_ = lvt_6_1_.flatMap((p_200999_1_) -> {
         return lvt_8_1_.getAllowedValues().stream().map((p_200998_1_) -> {
            List<Comparable<?>> lvt_2_1_ = Lists.newArrayList(p_200999_1_);
            lvt_2_1_.add(p_200998_1_);
            return lvt_2_1_;
         });
      })) {
         lvt_8_1_ = (IProperty)var7.next();
      }

      lvt_6_1_.forEach((p_201000_5_) -> {
         Map<IProperty<?>, Comparable<?>> lvt_6_1_ = MapPopulator.createMap(this.properties.values(), p_201000_5_);
         A lvt_7_1_ = p_i49005_2_.create(p_i49005_1_, ImmutableMap.copyOf(lvt_6_1_));
         lvt_4_1_.put(lvt_6_1_, lvt_7_1_);
         lvt_5_1_.add(lvt_7_1_);
      });
      Iterator var9 = lvt_5_1_.iterator();

      while(var9.hasNext()) {
         A lvt_8_2_ = (StateHolder)var9.next();
         lvt_8_2_.buildPropertyValueTable(lvt_4_1_);
      }

      this.validStates = ImmutableList.copyOf(lvt_5_1_);
   }

   public ImmutableList<S> getValidStates() {
      return this.validStates;
   }

   public S getBaseState() {
      return (IStateHolder)this.validStates.get(0);
   }

   public O getOwner() {
      return this.owner;
   }

   public Collection<IProperty<?>> getProperties() {
      return this.properties.values();
   }

   public String toString() {
      return MoreObjects.toStringHelper(this).add("block", this.owner).add("properties", this.properties.values().stream().map(IProperty::getName).collect(Collectors.toList())).toString();
   }

   @Nullable
   public IProperty<?> getProperty(String p_185920_1_) {
      return (IProperty)this.properties.get(p_185920_1_);
   }

   public static class Builder<O, S extends IStateHolder<S>> {
      private final O owner;
      private final Map<String, IProperty<?>> properties = Maps.newHashMap();

      public Builder(O p_i49165_1_) {
         this.owner = p_i49165_1_;
      }

      public StateContainer.Builder<O, S> add(IProperty<?>... p_206894_1_) {
         IProperty[] var2 = p_206894_1_;
         int var3 = p_206894_1_.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            IProperty<?> lvt_5_1_ = var2[var4];
            this.validateProperty(lvt_5_1_);
            this.properties.put(lvt_5_1_.getName(), lvt_5_1_);
         }

         return this;
      }

      private <T extends Comparable<T>> void validateProperty(IProperty<T> p_206892_1_) {
         String lvt_2_1_ = p_206892_1_.getName();
         if (!StateContainer.NAME_PATTERN.matcher(lvt_2_1_).matches()) {
            throw new IllegalArgumentException(this.owner + " has invalidly named property: " + lvt_2_1_);
         } else {
            Collection<T> lvt_3_1_ = p_206892_1_.getAllowedValues();
            if (lvt_3_1_.size() <= 1) {
               throw new IllegalArgumentException(this.owner + " attempted use property " + lvt_2_1_ + " with <= 1 possible values");
            } else {
               Iterator var4 = lvt_3_1_.iterator();

               String lvt_6_1_;
               do {
                  if (!var4.hasNext()) {
                     if (this.properties.containsKey(lvt_2_1_)) {
                        throw new IllegalArgumentException(this.owner + " has duplicate property: " + lvt_2_1_);
                     }

                     return;
                  }

                  T lvt_5_1_ = (Comparable)var4.next();
                  lvt_6_1_ = p_206892_1_.getName(lvt_5_1_);
               } while(StateContainer.NAME_PATTERN.matcher(lvt_6_1_).matches());

               throw new IllegalArgumentException(this.owner + " has property: " + lvt_2_1_ + " with invalidly named value: " + lvt_6_1_);
            }
         }
      }

      public <A extends StateHolder<O, S>> StateContainer<O, S> create(StateContainer.IFactory<O, S, A> p_206893_1_) {
         return new StateContainer(this.owner, p_206893_1_, this.properties);
      }
   }

   public interface IFactory<O, S extends IStateHolder<S>, A extends StateHolder<O, S>> {
      A create(O var1, ImmutableMap<IProperty<?>, Comparable<?>> var2);
   }
}
