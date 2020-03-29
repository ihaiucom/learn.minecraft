package net.minecraft.util.datafix.fixes;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import net.minecraft.util.datafix.TypeReferences;

public class VillagerProfessionFix extends NamedEntityFix {
   public VillagerProfessionFix(Schema p_i50420_1_, String p_i50420_2_) {
      super(p_i50420_1_, false, "Villager profession data fix (" + p_i50420_2_ + ")", TypeReferences.ENTITY, p_i50420_2_);
   }

   protected Typed<?> fix(Typed<?> p_207419_1_) {
      Dynamic<?> lvt_2_1_ = (Dynamic)p_207419_1_.get(DSL.remainderFinder());
      return p_207419_1_.set(DSL.remainderFinder(), lvt_2_1_.remove("Profession").remove("Career").remove("CareerLevel").set("VillagerData", lvt_2_1_.createMap(ImmutableMap.of(lvt_2_1_.createString("type"), lvt_2_1_.createString("minecraft:plains"), lvt_2_1_.createString("profession"), lvt_2_1_.createString(func_219811_a(lvt_2_1_.get("Profession").asInt(0), lvt_2_1_.get("Career").asInt(0))), lvt_2_1_.createString("level"), DataFixUtils.orElse(lvt_2_1_.get("CareerLevel").get(), lvt_2_1_.createInt(1))))));
   }

   private static String func_219811_a(int p_219811_0_, int p_219811_1_) {
      if (p_219811_0_ == 0) {
         if (p_219811_1_ == 2) {
            return "minecraft:fisherman";
         } else if (p_219811_1_ == 3) {
            return "minecraft:shepherd";
         } else {
            return p_219811_1_ == 4 ? "minecraft:fletcher" : "minecraft:farmer";
         }
      } else if (p_219811_0_ == 1) {
         return p_219811_1_ == 2 ? "minecraft:cartographer" : "minecraft:librarian";
      } else if (p_219811_0_ == 2) {
         return "minecraft:cleric";
      } else if (p_219811_0_ == 3) {
         if (p_219811_1_ == 2) {
            return "minecraft:weaponsmith";
         } else {
            return p_219811_1_ == 3 ? "minecraft:toolsmith" : "minecraft:armorer";
         }
      } else if (p_219811_0_ == 4) {
         return p_219811_1_ == 2 ? "minecraft:leatherworker" : "minecraft:butcher";
      } else {
         return p_219811_0_ == 5 ? "minecraft:nitwit" : "minecraft:none";
      }
   }
}
