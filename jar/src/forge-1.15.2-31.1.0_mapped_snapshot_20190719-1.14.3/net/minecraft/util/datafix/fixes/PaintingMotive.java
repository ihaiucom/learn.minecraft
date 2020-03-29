package net.minecraft.util.datafix.fixes;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.datafix.TypeReferences;

public class PaintingMotive extends NamedEntityFix {
   private static final Map<String, String> MAP = (Map)DataFixUtils.make(Maps.newHashMap(), (p_201153_0_) -> {
      p_201153_0_.put("donkeykong", "donkey_kong");
      p_201153_0_.put("burningskull", "burning_skull");
      p_201153_0_.put("skullandroses", "skull_and_roses");
   });

   public PaintingMotive(Schema p_i49659_1_, boolean p_i49659_2_) {
      super(p_i49659_1_, p_i49659_2_, "EntityPaintingMotiveFix", TypeReferences.ENTITY, "minecraft:painting");
   }

   public Dynamic<?> fixTag(Dynamic<?> p_209652_1_) {
      Optional<String> lvt_2_1_ = p_209652_1_.get("Motive").asString();
      if (lvt_2_1_.isPresent()) {
         String lvt_3_1_ = ((String)lvt_2_1_.get()).toLowerCase(Locale.ROOT);
         return p_209652_1_.set("Motive", p_209652_1_.createString((new ResourceLocation((String)MAP.getOrDefault(lvt_3_1_, lvt_3_1_))).toString()));
      } else {
         return p_209652_1_;
      }
   }

   protected Typed<?> fix(Typed<?> p_207419_1_) {
      return p_207419_1_.update(DSL.remainderFinder(), this::fixTag);
   }
}
