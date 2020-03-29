package net.minecraft.util.datafix.versions;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.NamespacedSchema;

public class V1483 extends NamespacedSchema {
   public V1483(int p_i49591_1_, Schema p_i49591_2_) {
      super(p_i49591_1_, p_i49591_2_);
   }

   public Map<String, Supplier<TypeTemplate>> registerEntities(Schema p_registerEntities_1_) {
      Map<String, Supplier<TypeTemplate>> lvt_2_1_ = super.registerEntities(p_registerEntities_1_);
      lvt_2_1_.put("minecraft:pufferfish", lvt_2_1_.remove("minecraft:puffer_fish"));
      return lvt_2_1_;
   }
}
