package net.minecraft.util.datafix.versions;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;

public class V0143 extends Schema {
   public V0143(int p_i49604_1_, Schema p_i49604_2_) {
      super(p_i49604_1_, p_i49604_2_);
   }

   public Map<String, Supplier<TypeTemplate>> registerEntities(Schema p_registerEntities_1_) {
      Map<String, Supplier<TypeTemplate>> lvt_2_1_ = super.registerEntities(p_registerEntities_1_);
      lvt_2_1_.remove("TippedArrow");
      return lvt_2_1_;
   }
}
