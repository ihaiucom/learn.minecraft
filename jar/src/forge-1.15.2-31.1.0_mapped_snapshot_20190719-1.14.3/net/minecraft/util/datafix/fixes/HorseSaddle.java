package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import java.util.Optional;
import net.minecraft.util.datafix.TypeReferences;

public class HorseSaddle extends NamedEntityFix {
   public HorseSaddle(Schema p_i49665_1_, boolean p_i49665_2_) {
      super(p_i49665_1_, p_i49665_2_, "EntityHorseSaddleFix", TypeReferences.ENTITY, "EntityHorse");
   }

   protected Typed<?> fix(Typed<?> p_207419_1_) {
      OpticFinder<Pair<String, String>> lvt_2_1_ = DSL.fieldFinder("id", DSL.named(TypeReferences.ITEM_NAME.typeName(), DSL.namespacedString()));
      Type<?> lvt_3_1_ = this.getInputSchema().getTypeRaw(TypeReferences.ITEM_STACK);
      OpticFinder<?> lvt_4_1_ = DSL.fieldFinder("SaddleItem", lvt_3_1_);
      Optional<? extends Typed<?>> lvt_5_1_ = p_207419_1_.getOptionalTyped(lvt_4_1_);
      Dynamic<?> lvt_6_1_ = (Dynamic)p_207419_1_.get(DSL.remainderFinder());
      if (!lvt_5_1_.isPresent() && lvt_6_1_.get("Saddle").asBoolean(false)) {
         Typed<?> lvt_7_1_ = (Typed)lvt_3_1_.pointTyped(p_207419_1_.getOps()).orElseThrow(IllegalStateException::new);
         lvt_7_1_ = lvt_7_1_.set(lvt_2_1_, Pair.of(TypeReferences.ITEM_NAME.typeName(), "minecraft:saddle"));
         Dynamic<?> lvt_8_1_ = lvt_6_1_.emptyMap();
         lvt_8_1_ = lvt_8_1_.set("Count", lvt_8_1_.createByte((byte)1));
         lvt_8_1_ = lvt_8_1_.set("Damage", lvt_8_1_.createShort((short)0));
         lvt_7_1_ = lvt_7_1_.set(DSL.remainderFinder(), lvt_8_1_);
         lvt_6_1_.remove("Saddle");
         return p_207419_1_.set(lvt_4_1_, lvt_7_1_).set(DSL.remainderFinder(), lvt_6_1_);
      } else {
         return p_207419_1_;
      }
   }
}
