package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.util.datafix.TypeReferences;

public class SpawnerEntityTypes extends DataFix {
   public SpawnerEntityTypes(Schema p_i49626_1_, boolean p_i49626_2_) {
      super(p_i49626_1_, p_i49626_2_);
   }

   private Dynamic<?> fix(Dynamic<?> p_209659_1_) {
      if (!"MobSpawner".equals(p_209659_1_.get("id").asString(""))) {
         return p_209659_1_;
      } else {
         Optional<String> lvt_2_1_ = p_209659_1_.get("EntityId").asString();
         if (lvt_2_1_.isPresent()) {
            Dynamic<?> lvt_3_1_ = (Dynamic)DataFixUtils.orElse(p_209659_1_.get("SpawnData").get(), p_209659_1_.emptyMap());
            lvt_3_1_ = lvt_3_1_.set("id", lvt_3_1_.createString(((String)lvt_2_1_.get()).isEmpty() ? "Pig" : (String)lvt_2_1_.get()));
            p_209659_1_ = p_209659_1_.set("SpawnData", lvt_3_1_);
            p_209659_1_ = p_209659_1_.remove("EntityId");
         }

         Optional<? extends Stream<? extends Dynamic<?>>> lvt_3_2_ = p_209659_1_.get("SpawnPotentials").asStreamOpt();
         if (lvt_3_2_.isPresent()) {
            p_209659_1_ = p_209659_1_.set("SpawnPotentials", p_209659_1_.createList(((Stream)lvt_3_2_.get()).map((p_209657_0_) -> {
               Optional<String> lvt_1_1_ = p_209657_0_.get("Type").asString();
               if (lvt_1_1_.isPresent()) {
                  Dynamic<?> lvt_2_1_ = ((Dynamic)DataFixUtils.orElse(p_209657_0_.get("Properties").get(), p_209657_0_.emptyMap())).set("id", p_209657_0_.createString((String)lvt_1_1_.get()));
                  return p_209657_0_.set("Entity", lvt_2_1_).remove("Type").remove("Properties");
               } else {
                  return p_209657_0_;
               }
            })));
         }

         return p_209659_1_;
      }
   }

   public TypeRewriteRule makeRule() {
      Type<?> lvt_1_1_ = this.getOutputSchema().getType(TypeReferences.UNTAGGED_SPAWNER);
      return this.fixTypeEverywhereTyped("MobSpawnerEntityIdentifiersFix", this.getInputSchema().getType(TypeReferences.UNTAGGED_SPAWNER), lvt_1_1_, (p_206369_2_) -> {
         Dynamic<?> lvt_3_1_ = (Dynamic)p_206369_2_.get(DSL.remainderFinder());
         lvt_3_1_ = lvt_3_1_.set("id", lvt_3_1_.createString("MobSpawner"));
         Pair<?, ? extends Optional<? extends Typed<?>>> lvt_4_1_ = lvt_1_1_.readTyped(this.fix(lvt_3_1_));
         return !((Optional)lvt_4_1_.getSecond()).isPresent() ? p_206369_2_ : (Typed)((Optional)lvt_4_1_.getSecond()).get();
      });
   }
}
