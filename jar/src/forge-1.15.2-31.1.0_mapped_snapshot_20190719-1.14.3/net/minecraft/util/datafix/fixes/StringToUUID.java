package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.util.datafix.TypeReferences;

public class StringToUUID extends DataFix {
   public StringToUUID(Schema p_i49652_1_, boolean p_i49652_2_) {
      super(p_i49652_1_, p_i49652_2_);
   }

   public TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped("EntityStringUuidFix", this.getInputSchema().getType(TypeReferences.ENTITY), (p_206344_0_) -> {
         return p_206344_0_.update(DSL.remainderFinder(), (p_206345_0_) -> {
            Optional<String> lvt_1_1_ = p_206345_0_.get("UUID").asString();
            if (lvt_1_1_.isPresent()) {
               UUID lvt_2_1_ = UUID.fromString((String)lvt_1_1_.get());
               return p_206345_0_.remove("UUID").set("UUIDMost", p_206345_0_.createLong(lvt_2_1_.getMostSignificantBits())).set("UUIDLeast", p_206345_0_.createLong(lvt_2_1_.getLeastSignificantBits()));
            } else {
               return p_206345_0_;
            }
         });
      });
   }
}
