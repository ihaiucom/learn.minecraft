package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.List.ListType;
import java.util.Optional;
import net.minecraft.util.datafix.TypeReferences;
import net.minecraft.util.math.MathHelper;

public class VillagerLevelAndXpFix extends DataFix {
   private static final int[] field_223004_a = new int[]{0, 10, 50, 100, 150};

   public static int func_223001_a(int p_223001_0_) {
      return field_223004_a[MathHelper.clamp(p_223001_0_ - 1, 0, field_223004_a.length - 1)];
   }

   public VillagerLevelAndXpFix(Schema p_i51508_1_, boolean p_i51508_2_) {
      super(p_i51508_1_, p_i51508_2_);
   }

   public TypeRewriteRule makeRule() {
      Type<?> lvt_1_1_ = this.getInputSchema().getChoiceType(TypeReferences.ENTITY, "minecraft:villager");
      OpticFinder<?> lvt_2_1_ = DSL.namedChoice("minecraft:villager", lvt_1_1_);
      OpticFinder<?> lvt_3_1_ = lvt_1_1_.findField("Offers");
      Type<?> lvt_4_1_ = lvt_3_1_.type();
      OpticFinder<?> lvt_5_1_ = lvt_4_1_.findField("Recipes");
      ListType<?> lvt_6_1_ = (ListType)lvt_5_1_.type();
      OpticFinder<?> lvt_7_1_ = lvt_6_1_.getElement().finder();
      return this.fixTypeEverywhereTyped("Villager level and xp rebuild", this.getInputSchema().getType(TypeReferences.ENTITY), (p_222996_5_) -> {
         return p_222996_5_.updateTyped(lvt_2_1_, lvt_1_1_, (p_222995_3_) -> {
            Dynamic<?> lvt_4_1_ = (Dynamic)p_222995_3_.get(DSL.remainderFinder());
            int lvt_5_1_x = ((Number)lvt_4_1_.get("VillagerData").get("level").asNumber().orElse(0)).intValue();
            Typed<?> lvt_6_1_ = p_222995_3_;
            if (lvt_5_1_x == 0 || lvt_5_1_x == 1) {
               int lvt_7_1_x = (Integer)p_222995_3_.getOptionalTyped(lvt_3_1_).flatMap((p_223002_1_) -> {
                  return p_223002_1_.getOptionalTyped(lvt_5_1_);
               }).map((p_222997_1_) -> {
                  return p_222997_1_.getAllTyped(lvt_7_1_).size();
               }).orElse(0);
               lvt_5_1_x = MathHelper.clamp(lvt_7_1_x / 2, 1, 5);
               if (lvt_5_1_x > 1) {
                  lvt_6_1_ = func_223003_a(p_222995_3_, lvt_5_1_x);
               }
            }

            Optional<Number> lvt_7_2_ = lvt_4_1_.get("Xp").asNumber();
            if (!lvt_7_2_.isPresent()) {
               lvt_6_1_ = func_222994_b(lvt_6_1_, lvt_5_1_x);
            }

            return lvt_6_1_;
         });
      });
   }

   private static Typed<?> func_223003_a(Typed<?> p_223003_0_, int p_223003_1_) {
      return p_223003_0_.update(DSL.remainderFinder(), (p_222998_1_) -> {
         return p_222998_1_.update("VillagerData", (p_222999_1_) -> {
            return p_222999_1_.set("level", p_222999_1_.createInt(p_223003_1_));
         });
      });
   }

   private static Typed<?> func_222994_b(Typed<?> p_222994_0_, int p_222994_1_) {
      int lvt_2_1_ = func_223001_a(p_222994_1_);
      return p_222994_0_.update(DSL.remainderFinder(), (p_223000_1_) -> {
         return p_223000_1_.set("Xp", p_223000_1_.createInt(lvt_2_1_));
      });
   }
}
