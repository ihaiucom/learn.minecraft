package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import java.util.Optional;
import net.minecraft.util.datafix.TypeReferences;

public class JukeboxRecordItem extends NamedEntityFix {
   public JukeboxRecordItem(Schema p_i49683_1_, boolean p_i49683_2_) {
      super(p_i49683_1_, p_i49683_2_, "BlockEntityJukeboxFix", TypeReferences.BLOCK_ENTITY, "minecraft:jukebox");
   }

   protected Typed<?> fix(Typed<?> p_207419_1_) {
      Type<?> lvt_2_1_ = this.getInputSchema().getChoiceType(TypeReferences.BLOCK_ENTITY, "minecraft:jukebox");
      Type<?> lvt_3_1_ = lvt_2_1_.findFieldType("RecordItem");
      OpticFinder<?> lvt_4_1_ = DSL.fieldFinder("RecordItem", lvt_3_1_);
      Dynamic<?> lvt_5_1_ = (Dynamic)p_207419_1_.get(DSL.remainderFinder());
      int lvt_6_1_ = lvt_5_1_.get("Record").asInt(0);
      if (lvt_6_1_ > 0) {
         lvt_5_1_.remove("Record");
         String lvt_7_1_ = ItemStackDataFlattening.updateItem(ItemIntIDToString.getItem(lvt_6_1_), 0);
         if (lvt_7_1_ != null) {
            Dynamic<?> lvt_8_1_ = lvt_5_1_.emptyMap();
            lvt_8_1_ = lvt_8_1_.set("id", lvt_8_1_.createString(lvt_7_1_));
            lvt_8_1_ = lvt_8_1_.set("Count", lvt_8_1_.createByte((byte)1));
            return p_207419_1_.set(lvt_4_1_, (Typed)((Optional)lvt_3_1_.readTyped(lvt_8_1_).getSecond()).orElseThrow(() -> {
               return new IllegalStateException("Could not create record item stack.");
            })).set(DSL.remainderFinder(), lvt_5_1_);
         }
      }

      return p_207419_1_;
   }
}
