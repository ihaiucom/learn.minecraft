package net.minecraft.util.datafix.versions;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.NamespacedSchema;
import net.minecraft.util.datafix.TypeReferences;

public class V1470 extends NamespacedSchema {
   public V1470(int p_i49593_1_, Schema p_i49593_2_) {
      super(p_i49593_1_, p_i49593_2_);
   }

   protected static void registerEntity(Schema p_206563_0_, Map<String, Supplier<TypeTemplate>> p_206563_1_, String p_206563_2_) {
      p_206563_0_.register(p_206563_1_, p_206563_2_, () -> {
         return V0100.equipment(p_206563_0_);
      });
   }

   public Map<String, Supplier<TypeTemplate>> registerEntities(Schema p_registerEntities_1_) {
      Map<String, Supplier<TypeTemplate>> lvt_2_1_ = super.registerEntities(p_registerEntities_1_);
      registerEntity(p_registerEntities_1_, lvt_2_1_, "minecraft:turtle");
      registerEntity(p_registerEntities_1_, lvt_2_1_, "minecraft:cod_mob");
      registerEntity(p_registerEntities_1_, lvt_2_1_, "minecraft:tropical_fish");
      registerEntity(p_registerEntities_1_, lvt_2_1_, "minecraft:salmon_mob");
      registerEntity(p_registerEntities_1_, lvt_2_1_, "minecraft:puffer_fish");
      registerEntity(p_registerEntities_1_, lvt_2_1_, "minecraft:phantom");
      registerEntity(p_registerEntities_1_, lvt_2_1_, "minecraft:dolphin");
      registerEntity(p_registerEntities_1_, lvt_2_1_, "minecraft:drowned");
      p_registerEntities_1_.register(lvt_2_1_, "minecraft:trident", (p_206561_1_) -> {
         return DSL.optionalFields("inBlockState", TypeReferences.BLOCK_STATE.in(p_registerEntities_1_));
      });
      return lvt_2_1_;
   }
}
