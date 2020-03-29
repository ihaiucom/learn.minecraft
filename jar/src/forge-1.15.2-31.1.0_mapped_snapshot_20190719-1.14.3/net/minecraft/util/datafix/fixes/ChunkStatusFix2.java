package net.minecraft.util.datafix.fixes;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import java.util.Map;
import java.util.Objects;
import net.minecraft.util.datafix.TypeReferences;

public class ChunkStatusFix2 extends DataFix {
   private static final Map<String, String> field_219825_a = ImmutableMap.builder().put("structure_references", "empty").put("biomes", "empty").put("base", "surface").put("carved", "carvers").put("liquid_carved", "liquid_carvers").put("decorated", "features").put("lighted", "light").put("mobs_spawned", "spawn").put("finalized", "heightmaps").put("fullchunk", "full").build();

   public ChunkStatusFix2(Schema p_i50429_1_, boolean p_i50429_2_) {
      super(p_i50429_1_, p_i50429_2_);
   }

   protected TypeRewriteRule makeRule() {
      Type<?> lvt_1_1_ = this.getInputSchema().getType(TypeReferences.CHUNK);
      Type<?> lvt_2_1_ = lvt_1_1_.findFieldType("Level");
      OpticFinder<?> lvt_3_1_ = DSL.fieldFinder("Level", lvt_2_1_);
      return this.fixTypeEverywhereTyped("ChunkStatusFix2", lvt_1_1_, this.getOutputSchema().getType(TypeReferences.CHUNK), (p_219823_1_) -> {
         return p_219823_1_.updateTyped(lvt_3_1_, (p_219824_0_) -> {
            Dynamic<?> lvt_1_1_ = (Dynamic)p_219824_0_.get(DSL.remainderFinder());
            String lvt_2_1_ = lvt_1_1_.get("Status").asString("empty");
            String lvt_3_1_ = (String)field_219825_a.getOrDefault(lvt_2_1_, "empty");
            return Objects.equals(lvt_2_1_, lvt_3_1_) ? p_219824_0_ : p_219824_0_.set(DSL.remainderFinder(), lvt_1_1_.set("Status", lvt_1_1_.createString(lvt_3_1_)));
         });
      });
   }
}
