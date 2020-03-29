package net.minecraft.util.datafix.versions;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.NamespacedSchema;

public class V1510 extends NamespacedSchema {
   public V1510(int p_i49589_1_, Schema p_i49589_2_) {
      super(p_i49589_1_, p_i49589_2_);
   }

   public Map<String, Supplier<TypeTemplate>> registerEntities(Schema p_registerEntities_1_) {
      Map<String, Supplier<TypeTemplate>> lvt_2_1_ = super.registerEntities(p_registerEntities_1_);
      lvt_2_1_.put("minecraft:command_block_minecart", lvt_2_1_.remove("minecraft:commandblock_minecart"));
      lvt_2_1_.put("minecraft:end_crystal", lvt_2_1_.remove("minecraft:ender_crystal"));
      lvt_2_1_.put("minecraft:snow_golem", lvt_2_1_.remove("minecraft:snowman"));
      lvt_2_1_.put("minecraft:evoker", lvt_2_1_.remove("minecraft:evocation_illager"));
      lvt_2_1_.put("minecraft:evoker_fangs", lvt_2_1_.remove("minecraft:evocation_fangs"));
      lvt_2_1_.put("minecraft:illusioner", lvt_2_1_.remove("minecraft:illusion_illager"));
      lvt_2_1_.put("minecraft:vindicator", lvt_2_1_.remove("minecraft:vindication_illager"));
      lvt_2_1_.put("minecraft:iron_golem", lvt_2_1_.remove("minecraft:villager_golem"));
      lvt_2_1_.put("minecraft:experience_orb", lvt_2_1_.remove("minecraft:xp_orb"));
      lvt_2_1_.put("minecraft:experience_bottle", lvt_2_1_.remove("minecraft:xp_bottle"));
      lvt_2_1_.put("minecraft:eye_of_ender", lvt_2_1_.remove("minecraft:eye_of_ender_signal"));
      lvt_2_1_.put("minecraft:firework_rocket", lvt_2_1_.remove("minecraft:fireworks_rocket"));
      return lvt_2_1_;
   }
}
