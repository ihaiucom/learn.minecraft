package net.minecraft.util.datafix.versions;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.TypeReferences;

public class V0100 extends Schema {
   public V0100(int p_i49611_1_, Schema p_i49611_2_) {
      super(p_i49611_1_, p_i49611_2_);
   }

   protected static TypeTemplate equipment(Schema p_206605_0_) {
      return DSL.optionalFields("ArmorItems", DSL.list(TypeReferences.ITEM_STACK.in(p_206605_0_)), "HandItems", DSL.list(TypeReferences.ITEM_STACK.in(p_206605_0_)));
   }

   protected static void registerEntity(Schema p_206611_0_, Map<String, Supplier<TypeTemplate>> p_206611_1_, String p_206611_2_) {
      p_206611_0_.register(p_206611_1_, p_206611_2_, () -> {
         return equipment(p_206611_0_);
      });
   }

   public Map<String, Supplier<TypeTemplate>> registerEntities(Schema p_registerEntities_1_) {
      Map<String, Supplier<TypeTemplate>> lvt_2_1_ = super.registerEntities(p_registerEntities_1_);
      registerEntity(p_registerEntities_1_, lvt_2_1_, "ArmorStand");
      registerEntity(p_registerEntities_1_, lvt_2_1_, "Creeper");
      registerEntity(p_registerEntities_1_, lvt_2_1_, "Skeleton");
      registerEntity(p_registerEntities_1_, lvt_2_1_, "Spider");
      registerEntity(p_registerEntities_1_, lvt_2_1_, "Giant");
      registerEntity(p_registerEntities_1_, lvt_2_1_, "Zombie");
      registerEntity(p_registerEntities_1_, lvt_2_1_, "Slime");
      registerEntity(p_registerEntities_1_, lvt_2_1_, "Ghast");
      registerEntity(p_registerEntities_1_, lvt_2_1_, "PigZombie");
      p_registerEntities_1_.register(lvt_2_1_, "Enderman", (p_206609_1_) -> {
         return DSL.optionalFields("carried", TypeReferences.BLOCK_NAME.in(p_registerEntities_1_), equipment(p_registerEntities_1_));
      });
      registerEntity(p_registerEntities_1_, lvt_2_1_, "CaveSpider");
      registerEntity(p_registerEntities_1_, lvt_2_1_, "Silverfish");
      registerEntity(p_registerEntities_1_, lvt_2_1_, "Blaze");
      registerEntity(p_registerEntities_1_, lvt_2_1_, "LavaSlime");
      registerEntity(p_registerEntities_1_, lvt_2_1_, "EnderDragon");
      registerEntity(p_registerEntities_1_, lvt_2_1_, "WitherBoss");
      registerEntity(p_registerEntities_1_, lvt_2_1_, "Bat");
      registerEntity(p_registerEntities_1_, lvt_2_1_, "Witch");
      registerEntity(p_registerEntities_1_, lvt_2_1_, "Endermite");
      registerEntity(p_registerEntities_1_, lvt_2_1_, "Guardian");
      registerEntity(p_registerEntities_1_, lvt_2_1_, "Pig");
      registerEntity(p_registerEntities_1_, lvt_2_1_, "Sheep");
      registerEntity(p_registerEntities_1_, lvt_2_1_, "Cow");
      registerEntity(p_registerEntities_1_, lvt_2_1_, "Chicken");
      registerEntity(p_registerEntities_1_, lvt_2_1_, "Squid");
      registerEntity(p_registerEntities_1_, lvt_2_1_, "Wolf");
      registerEntity(p_registerEntities_1_, lvt_2_1_, "MushroomCow");
      registerEntity(p_registerEntities_1_, lvt_2_1_, "SnowMan");
      registerEntity(p_registerEntities_1_, lvt_2_1_, "Ozelot");
      registerEntity(p_registerEntities_1_, lvt_2_1_, "VillagerGolem");
      p_registerEntities_1_.register(lvt_2_1_, "EntityHorse", (p_206612_1_) -> {
         return DSL.optionalFields("Items", DSL.list(TypeReferences.ITEM_STACK.in(p_registerEntities_1_)), "ArmorItem", TypeReferences.ITEM_STACK.in(p_registerEntities_1_), "SaddleItem", TypeReferences.ITEM_STACK.in(p_registerEntities_1_), equipment(p_registerEntities_1_));
      });
      registerEntity(p_registerEntities_1_, lvt_2_1_, "Rabbit");
      p_registerEntities_1_.register(lvt_2_1_, "Villager", (p_206608_1_) -> {
         return DSL.optionalFields("Inventory", DSL.list(TypeReferences.ITEM_STACK.in(p_registerEntities_1_)), "Offers", DSL.optionalFields("Recipes", DSL.list(DSL.optionalFields("buy", TypeReferences.ITEM_STACK.in(p_registerEntities_1_), "buyB", TypeReferences.ITEM_STACK.in(p_registerEntities_1_), "sell", TypeReferences.ITEM_STACK.in(p_registerEntities_1_)))), equipment(p_registerEntities_1_));
      });
      registerEntity(p_registerEntities_1_, lvt_2_1_, "Shulker");
      p_registerEntities_1_.registerSimple(lvt_2_1_, "AreaEffectCloud");
      p_registerEntities_1_.registerSimple(lvt_2_1_, "ShulkerBullet");
      return lvt_2_1_;
   }

   public void registerTypes(Schema p_registerTypes_1_, Map<String, Supplier<TypeTemplate>> p_registerTypes_2_, Map<String, Supplier<TypeTemplate>> p_registerTypes_3_) {
      super.registerTypes(p_registerTypes_1_, p_registerTypes_2_, p_registerTypes_3_);
      p_registerTypes_1_.registerType(false, TypeReferences.STRUCTURE, () -> {
         return DSL.optionalFields("entities", DSL.list(DSL.optionalFields("nbt", TypeReferences.ENTITY_TYPE.in(p_registerTypes_1_))), "blocks", DSL.list(DSL.optionalFields("nbt", TypeReferences.BLOCK_ENTITY.in(p_registerTypes_1_))), "palette", DSL.list(TypeReferences.BLOCK_STATE.in(p_registerTypes_1_)));
      });
      p_registerTypes_1_.registerType(false, TypeReferences.BLOCK_STATE, DSL::remainder);
   }
}
