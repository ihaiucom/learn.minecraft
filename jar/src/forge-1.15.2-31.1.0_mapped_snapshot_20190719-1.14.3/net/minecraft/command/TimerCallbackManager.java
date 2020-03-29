package net.minecraft.command;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.primitives.UnsignedLong;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TimerCallbackManager<T> {
   private static final Logger LOGGER = LogManager.getLogger();
   private final TimerCallbackSerializers<T> field_216334_b;
   private final Queue<TimerCallbackManager.Entry<T>> entries = new PriorityQueue(sorter());
   private UnsignedLong nextUniqueId;
   private final Table<String, Long, TimerCallbackManager.Entry<T>> byName;

   private static <T> Comparator<TimerCallbackManager.Entry<T>> sorter() {
      return Comparator.comparingLong((p_227578_0_) -> {
         return p_227578_0_.triggerTime;
      }).thenComparing((p_227577_0_) -> {
         return p_227577_0_.uniqueId;
      });
   }

   public TimerCallbackManager(TimerCallbackSerializers<T> p_i51188_1_) {
      this.nextUniqueId = UnsignedLong.ZERO;
      this.byName = HashBasedTable.create();
      this.field_216334_b = p_i51188_1_;
   }

   public void run(T p_216331_1_, long p_216331_2_) {
      while(true) {
         TimerCallbackManager.Entry<T> lvt_4_1_ = (TimerCallbackManager.Entry)this.entries.peek();
         if (lvt_4_1_ == null || lvt_4_1_.triggerTime > p_216331_2_) {
            return;
         }

         this.entries.remove();
         this.byName.remove(lvt_4_1_.name, p_216331_2_);
         lvt_4_1_.callback.run(p_216331_1_, this, p_216331_2_);
      }
   }

   public void func_227576_a_(String p_227576_1_, long p_227576_2_, ITimerCallback<T> p_227576_4_) {
      if (!this.byName.contains(p_227576_1_, p_227576_2_)) {
         this.nextUniqueId = this.nextUniqueId.plus(UnsignedLong.ONE);
         TimerCallbackManager.Entry<T> lvt_5_1_ = new TimerCallbackManager.Entry(p_227576_2_, this.nextUniqueId, p_227576_1_, p_227576_4_);
         this.byName.put(p_227576_1_, p_227576_2_, lvt_5_1_);
         this.entries.add(lvt_5_1_);
      }
   }

   public int func_227575_a_(String p_227575_1_) {
      Collection<TimerCallbackManager.Entry<T>> lvt_2_1_ = this.byName.row(p_227575_1_).values();
      Queue var10001 = this.entries;
      lvt_2_1_.forEach(var10001::remove);
      int lvt_3_1_ = lvt_2_1_.size();
      lvt_2_1_.clear();
      return lvt_3_1_;
   }

   public Set<String> func_227574_a_() {
      return Collections.unmodifiableSet(this.byName.rowKeySet());
   }

   private void readEntry(CompoundNBT p_216329_1_) {
      CompoundNBT lvt_2_1_ = p_216329_1_.getCompound("Callback");
      ITimerCallback<T> lvt_3_1_ = this.field_216334_b.func_216341_a(lvt_2_1_);
      if (lvt_3_1_ != null) {
         String lvt_4_1_ = p_216329_1_.getString("Name");
         long lvt_5_1_ = p_216329_1_.getLong("TriggerTime");
         this.func_227576_a_(lvt_4_1_, lvt_5_1_, lvt_3_1_);
      }

   }

   public void read(ListNBT p_216323_1_) {
      this.entries.clear();
      this.byName.clear();
      this.nextUniqueId = UnsignedLong.ZERO;
      if (!p_216323_1_.isEmpty()) {
         if (p_216323_1_.getTagType() != 10) {
            LOGGER.warn("Invalid format of events: " + p_216323_1_);
         } else {
            Iterator var2 = p_216323_1_.iterator();

            while(var2.hasNext()) {
               INBT lvt_3_1_ = (INBT)var2.next();
               this.readEntry((CompoundNBT)lvt_3_1_);
            }

         }
      }
   }

   private CompoundNBT writeEntry(TimerCallbackManager.Entry<T> p_216332_1_) {
      CompoundNBT lvt_2_1_ = new CompoundNBT();
      lvt_2_1_.putString("Name", p_216332_1_.name);
      lvt_2_1_.putLong("TriggerTime", p_216332_1_.triggerTime);
      lvt_2_1_.put("Callback", this.field_216334_b.func_216339_a(p_216332_1_.callback));
      return lvt_2_1_;
   }

   public ListNBT write() {
      ListNBT lvt_1_1_ = new ListNBT();
      this.entries.stream().sorted(sorter()).map(this::writeEntry).forEach(lvt_1_1_::add);
      return lvt_1_1_;
   }

   public static class Entry<T> {
      public final long triggerTime;
      public final UnsignedLong uniqueId;
      public final String name;
      public final ITimerCallback<T> callback;

      private Entry(long p_i50837_1_, UnsignedLong p_i50837_3_, String p_i50837_4_, ITimerCallback<T> p_i50837_5_) {
         this.triggerTime = p_i50837_1_;
         this.uniqueId = p_i50837_3_;
         this.name = p_i50837_4_;
         this.callback = p_i50837_5_;
      }

      // $FF: synthetic method
      Entry(long p_i50838_1_, UnsignedLong p_i50838_3_, String p_i50838_4_, ITimerCallback p_i50838_5_, Object p_i50838_6_) {
         this(p_i50838_1_, p_i50838_3_, p_i50838_4_, p_i50838_5_);
      }
   }
}
