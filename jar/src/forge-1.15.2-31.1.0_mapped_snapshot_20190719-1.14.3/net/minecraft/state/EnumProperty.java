package net.minecraft.state;

import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.util.IStringSerializable;

public class EnumProperty<T extends Enum<T> & IStringSerializable> extends Property<T> {
   private final ImmutableSet<T> allowedValues;
   private final Map<String, T> nameToValue = Maps.newHashMap();

   protected EnumProperty(String p_i45649_1_, Class<T> p_i45649_2_, Collection<T> p_i45649_3_) {
      super(p_i45649_1_, p_i45649_2_);
      this.allowedValues = ImmutableSet.copyOf(p_i45649_3_);
      Iterator var4 = p_i45649_3_.iterator();

      while(var4.hasNext()) {
         T lvt_5_1_ = (Enum)var4.next();
         String lvt_6_1_ = ((IStringSerializable)lvt_5_1_).getName();
         if (this.nameToValue.containsKey(lvt_6_1_)) {
            throw new IllegalArgumentException("Multiple values have the same name '" + lvt_6_1_ + "'");
         }

         this.nameToValue.put(lvt_6_1_, lvt_5_1_);
      }

   }

   public Collection<T> getAllowedValues() {
      return this.allowedValues;
   }

   public Optional<T> parseValue(String p_185929_1_) {
      return Optional.ofNullable(this.nameToValue.get(p_185929_1_));
   }

   public String getName(T p_177702_1_) {
      return ((IStringSerializable)p_177702_1_).getName();
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (p_equals_1_ instanceof EnumProperty && super.equals(p_equals_1_)) {
         EnumProperty<?> lvt_2_1_ = (EnumProperty)p_equals_1_;
         return this.allowedValues.equals(lvt_2_1_.allowedValues) && this.nameToValue.equals(lvt_2_1_.nameToValue);
      } else {
         return false;
      }
   }

   public int computeHashCode() {
      int lvt_1_1_ = super.computeHashCode();
      lvt_1_1_ = 31 * lvt_1_1_ + this.allowedValues.hashCode();
      lvt_1_1_ = 31 * lvt_1_1_ + this.nameToValue.hashCode();
      return lvt_1_1_;
   }

   public static <T extends Enum<T> & IStringSerializable> EnumProperty<T> create(String p_177709_0_, Class<T> p_177709_1_) {
      return create(p_177709_0_, p_177709_1_, (Predicate)Predicates.alwaysTrue());
   }

   public static <T extends Enum<T> & IStringSerializable> EnumProperty<T> create(String p_177708_0_, Class<T> p_177708_1_, Predicate<T> p_177708_2_) {
      return create(p_177708_0_, p_177708_1_, (Collection)Arrays.stream(p_177708_1_.getEnumConstants()).filter(p_177708_2_).collect(Collectors.toList()));
   }

   public static <T extends Enum<T> & IStringSerializable> EnumProperty<T> create(String p_177706_0_, Class<T> p_177706_1_, T... p_177706_2_) {
      return create(p_177706_0_, p_177706_1_, (Collection)Lists.newArrayList(p_177706_2_));
   }

   public static <T extends Enum<T> & IStringSerializable> EnumProperty<T> create(String p_177707_0_, Class<T> p_177707_1_, Collection<T> p_177707_2_) {
      return new EnumProperty(p_177707_0_, p_177707_1_, p_177707_2_);
   }
}
