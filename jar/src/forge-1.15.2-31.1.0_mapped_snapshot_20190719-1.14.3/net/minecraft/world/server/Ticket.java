package net.minecraft.world.server;

import java.util.Objects;

public final class Ticket<T> implements Comparable<Ticket<?>> {
   private final TicketType<T> type;
   private final int level;
   private final T value;
   private long timestamp;

   protected Ticket(TicketType<T> p_i226095_1_, int p_i226095_2_, T p_i226095_3_) {
      this.type = p_i226095_1_;
      this.level = p_i226095_2_;
      this.value = p_i226095_3_;
   }

   public int compareTo(Ticket<?> p_compareTo_1_) {
      int lvt_2_1_ = Integer.compare(this.level, p_compareTo_1_.level);
      if (lvt_2_1_ != 0) {
         return lvt_2_1_;
      } else {
         int lvt_3_1_ = Integer.compare(System.identityHashCode(this.type), System.identityHashCode(p_compareTo_1_.type));
         return lvt_3_1_ != 0 ? lvt_3_1_ : this.type.getComparator().compare(this.value, p_compareTo_1_.value);
      }
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (!(p_equals_1_ instanceof Ticket)) {
         return false;
      } else {
         Ticket<?> lvt_2_1_ = (Ticket)p_equals_1_;
         return this.level == lvt_2_1_.level && Objects.equals(this.type, lvt_2_1_.type) && Objects.equals(this.value, lvt_2_1_.value);
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.type, this.level, this.value});
   }

   public String toString() {
      return "Ticket[" + this.type + " " + this.level + " (" + this.value + ")] at " + this.timestamp;
   }

   public TicketType<T> getType() {
      return this.type;
   }

   public int getLevel() {
      return this.level;
   }

   protected void func_229861_a_(long p_229861_1_) {
      this.timestamp = p_229861_1_;
   }

   protected boolean isExpired(long p_223182_1_) {
      long lvt_3_1_ = this.type.getLifespan();
      return lvt_3_1_ != 0L && p_223182_1_ - this.timestamp > lvt_3_1_;
   }

   // $FF: synthetic method
   public int compareTo(Object p_compareTo_1_) {
      return this.compareTo((Ticket)p_compareTo_1_);
   }
}
