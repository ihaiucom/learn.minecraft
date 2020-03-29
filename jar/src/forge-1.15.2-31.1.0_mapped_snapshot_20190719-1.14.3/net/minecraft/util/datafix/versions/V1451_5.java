package net.minecraft.util.datafix.versions;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.NamespacedSchema;

public class V1451_5 extends NamespacedSchema {
   public V1451_5(int p_i49598_1_, Schema p_i49598_2_) {
      super(p_i49598_1_, p_i49598_2_);
   }

   public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema p_registerBlockEntities_1_) {
      Map<String, Supplier<TypeTemplate>> lvt_2_1_ = super.registerBlockEntities(p_registerBlockEntities_1_);
      lvt_2_1_.remove("minecraft:flower_pot");
      lvt_2_1_.remove("minecraft:noteblock");
      return lvt_2_1_;
   }
}
