package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.CompoundList.CompoundListType;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.util.datafix.NamespacedSchema;
import net.minecraft.util.datafix.TypeReferences;

public class NewVillageFix extends DataFix {
   public NewVillageFix(Schema p_i50423_1_, boolean p_i50423_2_) {
      super(p_i50423_1_, p_i50423_2_);
   }

   protected TypeRewriteRule makeRule() {
      CompoundListType<String, ?> lvt_1_1_ = DSL.compoundList(DSL.string(), this.getInputSchema().getType(TypeReferences.STRUCTURE_FEATURE));
      OpticFinder<? extends List<? extends Pair<String, ?>>> lvt_2_1_ = lvt_1_1_.finder();
      return this.func_219848_a(lvt_1_1_);
   }

   private <SF> TypeRewriteRule func_219848_a(CompoundListType<String, SF> p_219848_1_) {
      Type<?> lvt_2_1_ = this.getInputSchema().getType(TypeReferences.CHUNK);
      Type<?> lvt_3_1_ = this.getInputSchema().getType(TypeReferences.STRUCTURE_FEATURE);
      OpticFinder<?> lvt_4_1_ = lvt_2_1_.findField("Level");
      OpticFinder<?> lvt_5_1_ = lvt_4_1_.type().findField("Structures");
      OpticFinder<?> lvt_6_1_ = lvt_5_1_.type().findField("Starts");
      OpticFinder<List<Pair<String, SF>>> lvt_7_1_ = p_219848_1_.finder();
      return TypeRewriteRule.seq(this.fixTypeEverywhereTyped("NewVillageFix", lvt_2_1_, (p_219841_4_) -> {
         return p_219841_4_.updateTyped(lvt_4_1_, (p_219849_3_) -> {
            return p_219849_3_.updateTyped(lvt_5_1_, (p_219842_2_) -> {
               return p_219842_2_.updateTyped(lvt_6_1_, (p_219850_1_) -> {
                  return p_219850_1_.update(lvt_7_1_, (p_219851_0_) -> {
                     return (List)p_219851_0_.stream().filter((p_219854_0_) -> {
                        return !Objects.equals(p_219854_0_.getFirst(), "Village");
                     }).map((p_219852_0_) -> {
                        return p_219852_0_.mapFirst((p_219847_0_) -> {
                           return p_219847_0_.equals("New_Village") ? "Village" : p_219847_0_;
                        });
                     }).collect(Collectors.toList());
                  });
               }).update(DSL.remainderFinder(), (p_219843_0_) -> {
                  return p_219843_0_.update("References", (p_219844_0_) -> {
                     Optional<? extends Dynamic<?>> lvt_1_1_ = p_219844_0_.get("New_Village").get();
                     return ((Dynamic)DataFixUtils.orElse(lvt_1_1_.map((p_219846_1_) -> {
                        return p_219844_0_.remove("New_Village").merge(p_219844_0_.createString("Village"), p_219846_1_);
                     }), p_219844_0_)).remove("Village");
                  });
               });
            });
         });
      }), this.fixTypeEverywhereTyped("NewVillageStartFix", lvt_3_1_, (p_219853_0_) -> {
         return p_219853_0_.update(DSL.remainderFinder(), (p_219840_0_) -> {
            return p_219840_0_.update("id", (p_219845_0_) -> {
               return Objects.equals(NamespacedSchema.ensureNamespaced(p_219845_0_.asString("")), "minecraft:new_village") ? p_219845_0_.createString("minecraft:village") : p_219845_0_;
            });
         });
      }));
   }
}
