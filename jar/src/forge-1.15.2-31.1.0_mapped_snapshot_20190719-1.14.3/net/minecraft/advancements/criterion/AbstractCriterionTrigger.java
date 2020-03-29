package net.minecraft.advancements.criterion;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.UnmodifiableIterator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;

public abstract class AbstractCriterionTrigger<T extends ICriterionInstance> implements ICriterionTrigger<T> {
   private final Map<PlayerAdvancements, Set<ICriterionTrigger.Listener<T>>> field_227069_a_ = Maps.newIdentityHashMap();

   public final void addListener(PlayerAdvancements p_192165_1_, ICriterionTrigger.Listener<T> p_192165_2_) {
      ((Set)this.field_227069_a_.computeIfAbsent(p_192165_1_, (p_227072_0_) -> {
         return Sets.newHashSet();
      })).add(p_192165_2_);
   }

   public final void removeListener(PlayerAdvancements p_192164_1_, ICriterionTrigger.Listener<T> p_192164_2_) {
      Set<ICriterionTrigger.Listener<T>> lvt_3_1_ = (Set)this.field_227069_a_.get(p_192164_1_);
      if (lvt_3_1_ != null) {
         lvt_3_1_.remove(p_192164_2_);
         if (lvt_3_1_.isEmpty()) {
            this.field_227069_a_.remove(p_192164_1_);
         }
      }

   }

   public final void removeAllListeners(PlayerAdvancements p_192167_1_) {
      this.field_227069_a_.remove(p_192167_1_);
   }

   protected void func_227070_a_(PlayerAdvancements p_227070_1_, Predicate<T> p_227070_2_) {
      Set<ICriterionTrigger.Listener<T>> lvt_3_1_ = (Set)this.field_227069_a_.get(p_227070_1_);
      if (lvt_3_1_ != null) {
         List<ICriterionTrigger.Listener<T>> lvt_4_1_ = null;
         Iterator var5 = lvt_3_1_.iterator();

         ICriterionTrigger.Listener lvt_6_2_;
         while(var5.hasNext()) {
            lvt_6_2_ = (ICriterionTrigger.Listener)var5.next();
            if (p_227070_2_.test(lvt_6_2_.getCriterionInstance())) {
               if (lvt_4_1_ == null) {
                  lvt_4_1_ = Lists.newArrayList();
               }

               lvt_4_1_.add(lvt_6_2_);
            }
         }

         if (lvt_4_1_ != null) {
            var5 = lvt_4_1_.iterator();

            while(var5.hasNext()) {
               lvt_6_2_ = (ICriterionTrigger.Listener)var5.next();
               lvt_6_2_.grantCriterion(p_227070_1_);
            }
         }

      }
   }

   protected void func_227071_b_(PlayerAdvancements p_227071_1_) {
      Set<ICriterionTrigger.Listener<T>> lvt_2_1_ = (Set)this.field_227069_a_.get(p_227071_1_);
      if (lvt_2_1_ != null && !lvt_2_1_.isEmpty()) {
         UnmodifiableIterator var3 = ImmutableSet.copyOf(lvt_2_1_).iterator();

         while(var3.hasNext()) {
            ICriterionTrigger.Listener<T> lvt_4_1_ = (ICriterionTrigger.Listener)var3.next();
            lvt_4_1_.grantCriterion(p_227071_1_);
         }
      }

   }
}
