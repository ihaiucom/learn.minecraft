package net.minecraft.util.datafix.fixes;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.util.datafix.TypeReferences;

public class PointOfInterestReorganizationFix extends DataFix {
   public PointOfInterestReorganizationFix(Schema p_i50421_1_, boolean p_i50421_2_) {
      super(p_i50421_1_, p_i50421_2_);
   }

   protected TypeRewriteRule makeRule() {
      Type<Pair<String, Dynamic<?>>> lvt_1_1_ = DSL.named(TypeReferences.POI_CHUNK.typeName(), DSL.remainderType());
      if (!Objects.equals(lvt_1_1_, this.getInputSchema().getType(TypeReferences.POI_CHUNK))) {
         throw new IllegalStateException("Poi type is not what was expected.");
      } else {
         return this.fixTypeEverywhere("POI reorganization", lvt_1_1_, (p_219871_0_) -> {
            return (p_219872_0_) -> {
               return p_219872_0_.mapSecond(PointOfInterestReorganizationFix::func_219870_a);
            };
         });
      }
   }

   private static <T> Dynamic<T> func_219870_a(Dynamic<T> p_219870_0_) {
      Map<Dynamic<T>, Dynamic<T>> lvt_1_1_ = Maps.newHashMap();

      for(int lvt_2_1_ = 0; lvt_2_1_ < 16; ++lvt_2_1_) {
         String lvt_3_1_ = String.valueOf(lvt_2_1_);
         Optional<Dynamic<T>> lvt_4_1_ = p_219870_0_.get(lvt_3_1_).get();
         if (lvt_4_1_.isPresent()) {
            Dynamic<T> lvt_5_1_ = (Dynamic)lvt_4_1_.get();
            Dynamic<T> lvt_6_1_ = p_219870_0_.createMap(ImmutableMap.of(p_219870_0_.createString("Records"), lvt_5_1_));
            lvt_1_1_.put(p_219870_0_.createInt(lvt_2_1_), lvt_6_1_);
            p_219870_0_ = p_219870_0_.remove(lvt_3_1_);
         }
      }

      return p_219870_0_.set("Sections", p_219870_0_.createMap(lvt_1_1_));
   }
}
