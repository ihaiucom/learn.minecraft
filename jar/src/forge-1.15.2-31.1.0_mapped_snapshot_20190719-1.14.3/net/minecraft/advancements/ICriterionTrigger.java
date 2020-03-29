package net.minecraft.advancements;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.util.ResourceLocation;

public interface ICriterionTrigger<T extends ICriterionInstance> {
   ResourceLocation getId();

   void addListener(PlayerAdvancements var1, ICriterionTrigger.Listener<T> var2);

   void removeListener(PlayerAdvancements var1, ICriterionTrigger.Listener<T> var2);

   void removeAllListeners(PlayerAdvancements var1);

   T deserializeInstance(JsonObject var1, JsonDeserializationContext var2);

   public static class Listener<T extends ICriterionInstance> {
      private final T criterionInstance;
      private final Advancement advancement;
      private final String criterionName;

      public Listener(T p_i47405_1_, Advancement p_i47405_2_, String p_i47405_3_) {
         this.criterionInstance = p_i47405_1_;
         this.advancement = p_i47405_2_;
         this.criterionName = p_i47405_3_;
      }

      public T getCriterionInstance() {
         return this.criterionInstance;
      }

      public void grantCriterion(PlayerAdvancements p_192159_1_) {
         p_192159_1_.grantCriterion(this.advancement, this.criterionName);
      }

      public boolean equals(Object p_equals_1_) {
         if (this == p_equals_1_) {
            return true;
         } else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) {
            ICriterionTrigger.Listener<?> lvt_2_1_ = (ICriterionTrigger.Listener)p_equals_1_;
            if (!this.criterionInstance.equals(lvt_2_1_.criterionInstance)) {
               return false;
            } else {
               return !this.advancement.equals(lvt_2_1_.advancement) ? false : this.criterionName.equals(lvt_2_1_.criterionName);
            }
         } else {
            return false;
         }
      }

      public int hashCode() {
         int lvt_1_1_ = this.criterionInstance.hashCode();
         lvt_1_1_ = 31 * lvt_1_1_ + this.advancement.hashCode();
         lvt_1_1_ = 31 * lvt_1_1_ + this.criterionName.hashCode();
         return lvt_1_1_;
      }
   }
}
