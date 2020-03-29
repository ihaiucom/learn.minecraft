package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import java.util.Optional;
import net.minecraft.util.datafix.TypeReferences;

public class PistonPushedBlock extends NamedEntityFix {
   public PistonPushedBlock(Schema p_i49686_1_, boolean p_i49686_2_) {
      super(p_i49686_1_, p_i49686_2_, "BlockEntityBlockStateFix", TypeReferences.BLOCK_ENTITY, "minecraft:piston");
   }

   protected Typed<?> fix(Typed<?> p_207419_1_) {
      Type<?> lvt_2_1_ = this.getOutputSchema().getChoiceType(TypeReferences.BLOCK_ENTITY, "minecraft:piston");
      Type<?> lvt_3_1_ = lvt_2_1_.findFieldType("blockState");
      OpticFinder<?> lvt_4_1_ = DSL.fieldFinder("blockState", lvt_3_1_);
      Dynamic<?> lvt_5_1_ = (Dynamic)p_207419_1_.get(DSL.remainderFinder());
      int lvt_6_1_ = lvt_5_1_.get("blockId").asInt(0);
      lvt_5_1_ = lvt_5_1_.remove("blockId");
      int lvt_7_1_ = lvt_5_1_.get("blockData").asInt(0) & 15;
      lvt_5_1_ = lvt_5_1_.remove("blockData");
      Dynamic<?> lvt_8_1_ = BlockStateFlatteningMap.getFixedNBTForID(lvt_6_1_ << 4 | lvt_7_1_);
      Typed<?> lvt_9_1_ = (Typed)lvt_2_1_.pointTyped(p_207419_1_.getOps()).orElseThrow(() -> {
         return new IllegalStateException("Could not create new piston block entity.");
      });
      return lvt_9_1_.set(DSL.remainderFinder(), lvt_5_1_).set(lvt_4_1_, (Typed)((Optional)lvt_3_1_.readTyped(lvt_8_1_).getSecond()).orElseThrow(() -> {
         return new IllegalStateException("Could not parse newly created block state tag.");
      }));
   }
}
