package net.minecraft.state;

import com.google.common.base.MoreObjects;

public abstract class Property<T extends Comparable<T>> implements IProperty<T> {
   private final Class<T> valueClass;
   private final String name;
   private Integer hashCode;

   protected Property(String p_i45652_1_, Class<T> p_i45652_2_) {
      this.valueClass = p_i45652_2_;
      this.name = p_i45652_1_;
   }

   public String getName() {
      return this.name;
   }

   public Class<T> getValueClass() {
      return this.valueClass;
   }

   public String toString() {
      return MoreObjects.toStringHelper(this).add("name", this.name).add("clazz", this.valueClass).add("values", this.getAllowedValues()).toString();
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (!(p_equals_1_ instanceof Property)) {
         return false;
      } else {
         Property<?> lvt_2_1_ = (Property)p_equals_1_;
         return this.valueClass.equals(lvt_2_1_.valueClass) && this.name.equals(lvt_2_1_.name);
      }
   }

   public final int hashCode() {
      if (this.hashCode == null) {
         this.hashCode = this.computeHashCode();
      }

      return this.hashCode;
   }

   public int computeHashCode() {
      return 31 * this.valueClass.hashCode() + this.name.hashCode();
   }
}
