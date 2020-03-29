package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.util.datafix.TypeReferences;

public class HorseSplit extends EntityRename {
   public HorseSplit(Schema p_i49664_1_, boolean p_i49664_2_) {
      super("EntityHorseSplitFix", p_i49664_1_, p_i49664_2_);
   }

   protected Pair<String, Typed<?>> fix(String p_209149_1_, Typed<?> p_209149_2_) {
      Dynamic<?> lvt_3_1_ = (Dynamic)p_209149_2_.get(DSL.remainderFinder());
      if (Objects.equals("EntityHorse", p_209149_1_)) {
         int lvt_5_1_ = lvt_3_1_.get("Type").asInt(0);
         String lvt_4_5_;
         switch(lvt_5_1_) {
         case 0:
         default:
            lvt_4_5_ = "Horse";
            break;
         case 1:
            lvt_4_5_ = "Donkey";
            break;
         case 2:
            lvt_4_5_ = "Mule";
            break;
         case 3:
            lvt_4_5_ = "ZombieHorse";
            break;
         case 4:
            lvt_4_5_ = "SkeletonHorse";
         }

         lvt_3_1_.remove("Type");
         Type<?> lvt_6_1_ = (Type)this.getOutputSchema().findChoiceType(TypeReferences.ENTITY).types().get(lvt_4_5_);
         return Pair.of(lvt_4_5_, ((Optional)lvt_6_1_.readTyped(p_209149_2_.write()).getSecond()).orElseThrow(() -> {
            return new IllegalStateException("Could not parse the new horse");
         }));
      } else {
         return Pair.of(p_209149_1_, p_209149_2_);
      }
   }
}
