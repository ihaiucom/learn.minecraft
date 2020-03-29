package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.TaggedChoice.TaggedChoiceType;
import com.mojang.datafixers.util.Pair;
import net.minecraft.util.datafix.TypeReferences;

public abstract class EntityRename extends DataFix {
   protected final String name;

   public EntityRename(String p_i49715_1_, Schema p_i49715_2_, boolean p_i49715_3_) {
      super(p_i49715_2_, p_i49715_3_);
      this.name = p_i49715_1_;
   }

   public TypeRewriteRule makeRule() {
      TaggedChoiceType<String> lvt_1_1_ = this.getInputSchema().findChoiceType(TypeReferences.ENTITY);
      TaggedChoiceType<String> lvt_2_1_ = this.getOutputSchema().findChoiceType(TypeReferences.ENTITY);
      return this.fixTypeEverywhere(this.name, lvt_1_1_, lvt_2_1_, (p_209755_3_) -> {
         return (p_209150_4_) -> {
            String lvt_5_1_ = (String)p_209150_4_.getFirst();
            Type<?> lvt_6_1_ = (Type)lvt_1_1_.types().get(lvt_5_1_);
            Pair<String, Typed<?>> lvt_7_1_ = this.fix(lvt_5_1_, this.getEntity(p_209150_4_.getSecond(), p_209755_3_, lvt_6_1_));
            Type<?> lvt_8_1_ = (Type)lvt_2_1_.types().get(lvt_7_1_.getFirst());
            if (!lvt_8_1_.equals(((Typed)lvt_7_1_.getSecond()).getType(), true, true)) {
               throw new IllegalStateException(String.format("Dynamic type check failed: %s not equal to %s", lvt_8_1_, ((Typed)lvt_7_1_.getSecond()).getType()));
            } else {
               return Pair.of(lvt_7_1_.getFirst(), ((Typed)lvt_7_1_.getSecond()).getValue());
            }
         };
      });
   }

   private <A> Typed<A> getEntity(Object p_209757_1_, DynamicOps<?> p_209757_2_, Type<A> p_209757_3_) {
      return new Typed(p_209757_3_, p_209757_2_, p_209757_1_);
   }

   protected abstract Pair<String, Typed<?>> fix(String var1, Typed<?> var2);
}
