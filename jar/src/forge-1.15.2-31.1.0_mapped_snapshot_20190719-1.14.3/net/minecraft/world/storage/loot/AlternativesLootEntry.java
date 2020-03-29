package net.minecraft.world.storage.loot;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.world.storage.loot.conditions.ILootCondition;
import org.apache.commons.lang3.ArrayUtils;

public class AlternativesLootEntry extends ParentedLootEntry {
   AlternativesLootEntry(LootEntry[] p_i51263_1_, ILootCondition[] p_i51263_2_) {
      super(p_i51263_1_, p_i51263_2_);
   }

   protected ILootEntry combineChildren(ILootEntry[] p_216146_1_) {
      switch(p_216146_1_.length) {
      case 0:
         return field_216139_a;
      case 1:
         return p_216146_1_[0];
      case 2:
         return p_216146_1_[0].alternate(p_216146_1_[1]);
      default:
         return (p_216150_1_, p_216150_2_) -> {
            ILootEntry[] var3 = p_216146_1_;
            int var4 = p_216146_1_.length;

            for(int var5 = 0; var5 < var4; ++var5) {
               ILootEntry lvt_6_1_ = var3[var5];
               if (lvt_6_1_.expand(p_216150_1_, p_216150_2_)) {
                  return true;
               }
            }

            return false;
         };
      }
   }

   public void func_225579_a_(ValidationTracker p_225579_1_) {
      super.func_225579_a_(p_225579_1_);

      for(int lvt_2_1_ = 0; lvt_2_1_ < this.field_216147_c.length - 1; ++lvt_2_1_) {
         if (ArrayUtils.isEmpty(this.field_216147_c[lvt_2_1_].conditions)) {
            p_225579_1_.func_227530_a_("Unreachable entry!");
         }
      }

   }

   public static AlternativesLootEntry.Builder func_216149_a(LootEntry.Builder<?>... p_216149_0_) {
      return new AlternativesLootEntry.Builder(p_216149_0_);
   }

   public static class Builder extends LootEntry.Builder<AlternativesLootEntry.Builder> {
      private final List<LootEntry> field_216083_a = Lists.newArrayList();

      public Builder(LootEntry.Builder<?>... p_i50579_1_) {
         LootEntry.Builder[] var2 = p_i50579_1_;
         int var3 = p_i50579_1_.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            LootEntry.Builder<?> lvt_5_1_ = var2[var4];
            this.field_216083_a.add(lvt_5_1_.func_216081_b());
         }

      }

      protected AlternativesLootEntry.Builder func_212845_d_() {
         return this;
      }

      public AlternativesLootEntry.Builder func_216080_a(LootEntry.Builder<?> p_216080_1_) {
         this.field_216083_a.add(p_216080_1_.func_216081_b());
         return this;
      }

      public LootEntry func_216081_b() {
         return new AlternativesLootEntry((LootEntry[])this.field_216083_a.toArray(new LootEntry[0]), this.func_216079_f());
      }

      // $FF: synthetic method
      protected LootEntry.Builder func_212845_d_() {
         return this.func_212845_d_();
      }
   }
}
