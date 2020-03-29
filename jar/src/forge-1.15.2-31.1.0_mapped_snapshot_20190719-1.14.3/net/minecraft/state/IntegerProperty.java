package net.minecraft.state;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public class IntegerProperty extends Property<Integer> {
   private final ImmutableSet<Integer> allowedValues;

   protected IntegerProperty(String p_i45648_1_, int p_i45648_2_, int p_i45648_3_) {
      super(p_i45648_1_, Integer.class);
      if (p_i45648_2_ < 0) {
         throw new IllegalArgumentException("Min value of " + p_i45648_1_ + " must be 0 or greater");
      } else if (p_i45648_3_ <= p_i45648_2_) {
         throw new IllegalArgumentException("Max value of " + p_i45648_1_ + " must be greater than min (" + p_i45648_2_ + ")");
      } else {
         Set<Integer> lvt_4_1_ = Sets.newHashSet();

         for(int lvt_5_1_ = p_i45648_2_; lvt_5_1_ <= p_i45648_3_; ++lvt_5_1_) {
            lvt_4_1_.add(lvt_5_1_);
         }

         this.allowedValues = ImmutableSet.copyOf(lvt_4_1_);
      }
   }

   public Collection<Integer> getAllowedValues() {
      return this.allowedValues;
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (p_equals_1_ instanceof IntegerProperty && super.equals(p_equals_1_)) {
         IntegerProperty lvt_2_1_ = (IntegerProperty)p_equals_1_;
         return this.allowedValues.equals(lvt_2_1_.allowedValues);
      } else {
         return false;
      }
   }

   public int computeHashCode() {
      return 31 * super.computeHashCode() + this.allowedValues.hashCode();
   }

   public static IntegerProperty create(String p_177719_0_, int p_177719_1_, int p_177719_2_) {
      return new IntegerProperty(p_177719_0_, p_177719_1_, p_177719_2_);
   }

   public Optional<Integer> parseValue(String p_185929_1_) {
      try {
         Integer lvt_2_1_ = Integer.valueOf(p_185929_1_);
         return this.allowedValues.contains(lvt_2_1_) ? Optional.of(lvt_2_1_) : Optional.empty();
      } catch (NumberFormatException var3) {
         return Optional.empty();
      }
   }

   public String getName(Integer p_177702_1_) {
      return p_177702_1_.toString();
   }
}
