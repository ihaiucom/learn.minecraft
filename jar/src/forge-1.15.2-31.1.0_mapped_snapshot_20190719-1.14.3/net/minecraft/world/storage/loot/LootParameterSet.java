package net.minecraft.world.storage.loot;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.util.Set;

public class LootParameterSet {
   private final Set<LootParameter<?>> required;
   private final Set<LootParameter<?>> all;

   private LootParameterSet(Set<LootParameter<?>> p_i51211_1_, Set<LootParameter<?>> p_i51211_2_) {
      this.required = ImmutableSet.copyOf(p_i51211_1_);
      this.all = ImmutableSet.copyOf(Sets.union(p_i51211_1_, p_i51211_2_));
   }

   public Set<LootParameter<?>> getRequiredParameters() {
      return this.required;
   }

   public Set<LootParameter<?>> getAllParameters() {
      return this.all;
   }

   public String toString() {
      return "[" + Joiner.on(", ").join(this.all.stream().map((p_216275_1_) -> {
         return (this.required.contains(p_216275_1_) ? "!" : "") + p_216275_1_.getId();
      }).iterator()) + "]";
   }

   public void func_227556_a_(ValidationTracker p_227556_1_, IParameterized p_227556_2_) {
      Set<LootParameter<?>> lvt_3_1_ = p_227556_2_.getRequiredParameters();
      Set<LootParameter<?>> lvt_4_1_ = Sets.difference(lvt_3_1_, this.all);
      if (!lvt_4_1_.isEmpty()) {
         p_227556_1_.func_227530_a_("Parameters " + lvt_4_1_ + " are not provided in this context");
      }

   }

   // $FF: synthetic method
   LootParameterSet(Set p_i51212_1_, Set p_i51212_2_, Object p_i51212_3_) {
      this(p_i51212_1_, p_i51212_2_);
   }

   public static class Builder {
      private final Set<LootParameter<?>> required = Sets.newIdentityHashSet();
      private final Set<LootParameter<?>> optional = Sets.newIdentityHashSet();

      public LootParameterSet.Builder required(LootParameter<?> p_216269_1_) {
         if (this.optional.contains(p_216269_1_)) {
            throw new IllegalArgumentException("Parameter " + p_216269_1_.getId() + " is already optional");
         } else {
            this.required.add(p_216269_1_);
            return this;
         }
      }

      public LootParameterSet.Builder optional(LootParameter<?> p_216271_1_) {
         if (this.required.contains(p_216271_1_)) {
            throw new IllegalArgumentException("Parameter " + p_216271_1_.getId() + " is already required");
         } else {
            this.optional.add(p_216271_1_);
            return this;
         }
      }

      public LootParameterSet build() {
         return new LootParameterSet(this.required, this.optional);
      }
   }
}
