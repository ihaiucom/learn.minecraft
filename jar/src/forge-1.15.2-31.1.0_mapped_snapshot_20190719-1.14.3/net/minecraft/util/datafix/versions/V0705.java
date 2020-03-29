package net.minecraft.util.datafix.versions;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.types.templates.TypeTemplate;
import com.mojang.datafixers.types.templates.Hook.HookFunction;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.NamespacedSchema;
import net.minecraft.util.datafix.TypeReferences;

public class V0705 extends NamespacedSchema {
   protected static final HookFunction field_206597_b = new HookFunction() {
      public <T> T apply(DynamicOps<T> p_apply_1_, T p_apply_2_) {
         return V0099.func_209869_a(new Dynamic(p_apply_1_, p_apply_2_), V0704.field_206647_b, "minecraft:armor_stand");
      }
   };

   public V0705(int p_i49582_1_, Schema p_i49582_2_) {
      super(p_i49582_1_, p_i49582_2_);
   }

   protected static void registerEntity(Schema p_206596_0_, Map<String, Supplier<TypeTemplate>> p_206596_1_, String p_206596_2_) {
      p_206596_0_.register(p_206596_1_, p_206596_2_, () -> {
         return V0100.equipment(p_206596_0_);
      });
   }

   protected static void registerThrowableProjectile(Schema p_206581_0_, Map<String, Supplier<TypeTemplate>> p_206581_1_, String p_206581_2_) {
      p_206581_0_.register(p_206581_1_, p_206581_2_, () -> {
         return DSL.optionalFields("inTile", TypeReferences.BLOCK_NAME.in(p_206581_0_));
      });
   }

   public Map<String, Supplier<TypeTemplate>> registerEntities(Schema p_registerEntities_1_) {
      Map<String, Supplier<TypeTemplate>> lvt_2_1_ = Maps.newHashMap();
      p_registerEntities_1_.registerSimple(lvt_2_1_, "minecraft:area_effect_cloud");
      registerEntity(p_registerEntities_1_, lvt_2_1_, "minecraft:armor_stand");
      p_registerEntities_1_.register(lvt_2_1_, "minecraft:arrow", (p_206582_1_) -> {
         return DSL.optionalFields("inTile", TypeReferences.BLOCK_NAME.in(p_registerEntities_1_));
      });
      registerEntity(p_registerEntities_1_, lvt_2_1_, "minecraft:bat");
      registerEntity(p_registerEntities_1_, lvt_2_1_, "minecraft:blaze");
      p_registerEntities_1_.registerSimple(lvt_2_1_, "minecraft:boat");
      registerEntity(p_registerEntities_1_, lvt_2_1_, "minecraft:cave_spider");
      p_registerEntities_1_.register(lvt_2_1_, "minecraft:chest_minecart", (p_206574_1_) -> {
         return DSL.optionalFields("DisplayTile", TypeReferences.BLOCK_NAME.in(p_registerEntities_1_), "Items", DSL.list(TypeReferences.ITEM_STACK.in(p_registerEntities_1_)));
      });
      registerEntity(p_registerEntities_1_, lvt_2_1_, "minecraft:chicken");
      p_registerEntities_1_.register(lvt_2_1_, "minecraft:commandblock_minecart", (p_206575_1_) -> {
         return DSL.optionalFields("DisplayTile", TypeReferences.BLOCK_NAME.in(p_registerEntities_1_));
      });
      registerEntity(p_registerEntities_1_, lvt_2_1_, "minecraft:cow");
      registerEntity(p_registerEntities_1_, lvt_2_1_, "minecraft:creeper");
      p_registerEntities_1_.register(lvt_2_1_, "minecraft:donkey", (p_206594_1_) -> {
         return DSL.optionalFields("Items", DSL.list(TypeReferences.ITEM_STACK.in(p_registerEntities_1_)), "SaddleItem", TypeReferences.ITEM_STACK.in(p_registerEntities_1_), V0100.equipment(p_registerEntities_1_));
      });
      p_registerEntities_1_.registerSimple(lvt_2_1_, "minecraft:dragon_fireball");
      registerThrowableProjectile(p_registerEntities_1_, lvt_2_1_, "minecraft:egg");
      registerEntity(p_registerEntities_1_, lvt_2_1_, "minecraft:elder_guardian");
      p_registerEntities_1_.registerSimple(lvt_2_1_, "minecraft:ender_crystal");
      registerEntity(p_registerEntities_1_, lvt_2_1_, "minecraft:ender_dragon");
      p_registerEntities_1_.register(lvt_2_1_, "minecraft:enderman", (p_206567_1_) -> {
         return DSL.optionalFields("carried", TypeReferences.BLOCK_NAME.in(p_registerEntities_1_), V0100.equipment(p_registerEntities_1_));
      });
      registerEntity(p_registerEntities_1_, lvt_2_1_, "minecraft:endermite");
      registerThrowableProjectile(p_registerEntities_1_, lvt_2_1_, "minecraft:ender_pearl");
      p_registerEntities_1_.registerSimple(lvt_2_1_, "minecraft:eye_of_ender_signal");
      p_registerEntities_1_.register(lvt_2_1_, "minecraft:falling_block", (p_206586_1_) -> {
         return DSL.optionalFields("Block", TypeReferences.BLOCK_NAME.in(p_registerEntities_1_), "TileEntityData", TypeReferences.BLOCK_ENTITY.in(p_registerEntities_1_));
      });
      registerThrowableProjectile(p_registerEntities_1_, lvt_2_1_, "minecraft:fireball");
      p_registerEntities_1_.register(lvt_2_1_, "minecraft:fireworks_rocket", (p_206588_1_) -> {
         return DSL.optionalFields("FireworksItem", TypeReferences.ITEM_STACK.in(p_registerEntities_1_));
      });
      p_registerEntities_1_.register(lvt_2_1_, "minecraft:furnace_minecart", (p_206570_1_) -> {
         return DSL.optionalFields("DisplayTile", TypeReferences.BLOCK_NAME.in(p_registerEntities_1_));
      });
      registerEntity(p_registerEntities_1_, lvt_2_1_, "minecraft:ghast");
      registerEntity(p_registerEntities_1_, lvt_2_1_, "minecraft:giant");
      registerEntity(p_registerEntities_1_, lvt_2_1_, "minecraft:guardian");
      p_registerEntities_1_.register(lvt_2_1_, "minecraft:hopper_minecart", (p_206584_1_) -> {
         return DSL.optionalFields("DisplayTile", TypeReferences.BLOCK_NAME.in(p_registerEntities_1_), "Items", DSL.list(TypeReferences.ITEM_STACK.in(p_registerEntities_1_)));
      });
      p_registerEntities_1_.register(lvt_2_1_, "minecraft:horse", (p_206595_1_) -> {
         return DSL.optionalFields("ArmorItem", TypeReferences.ITEM_STACK.in(p_registerEntities_1_), "SaddleItem", TypeReferences.ITEM_STACK.in(p_registerEntities_1_), V0100.equipment(p_registerEntities_1_));
      });
      registerEntity(p_registerEntities_1_, lvt_2_1_, "minecraft:husk");
      p_registerEntities_1_.register(lvt_2_1_, "minecraft:item", (p_206578_1_) -> {
         return DSL.optionalFields("Item", TypeReferences.ITEM_STACK.in(p_registerEntities_1_));
      });
      p_registerEntities_1_.register(lvt_2_1_, "minecraft:item_frame", (p_206587_1_) -> {
         return DSL.optionalFields("Item", TypeReferences.ITEM_STACK.in(p_registerEntities_1_));
      });
      p_registerEntities_1_.registerSimple(lvt_2_1_, "minecraft:leash_knot");
      registerEntity(p_registerEntities_1_, lvt_2_1_, "minecraft:magma_cube");
      p_registerEntities_1_.register(lvt_2_1_, "minecraft:minecart", (p_206568_1_) -> {
         return DSL.optionalFields("DisplayTile", TypeReferences.BLOCK_NAME.in(p_registerEntities_1_));
      });
      registerEntity(p_registerEntities_1_, lvt_2_1_, "minecraft:mooshroom");
      p_registerEntities_1_.register(lvt_2_1_, "minecraft:mule", (p_206579_1_) -> {
         return DSL.optionalFields("Items", DSL.list(TypeReferences.ITEM_STACK.in(p_registerEntities_1_)), "SaddleItem", TypeReferences.ITEM_STACK.in(p_registerEntities_1_), V0100.equipment(p_registerEntities_1_));
      });
      registerEntity(p_registerEntities_1_, lvt_2_1_, "minecraft:ocelot");
      p_registerEntities_1_.registerSimple(lvt_2_1_, "minecraft:painting");
      p_registerEntities_1_.registerSimple(lvt_2_1_, "minecraft:parrot");
      registerEntity(p_registerEntities_1_, lvt_2_1_, "minecraft:pig");
      registerEntity(p_registerEntities_1_, lvt_2_1_, "minecraft:polar_bear");
      p_registerEntities_1_.register(lvt_2_1_, "minecraft:potion", (p_206573_1_) -> {
         return DSL.optionalFields("Potion", TypeReferences.ITEM_STACK.in(p_registerEntities_1_), "inTile", TypeReferences.BLOCK_NAME.in(p_registerEntities_1_));
      });
      registerEntity(p_registerEntities_1_, lvt_2_1_, "minecraft:rabbit");
      registerEntity(p_registerEntities_1_, lvt_2_1_, "minecraft:sheep");
      registerEntity(p_registerEntities_1_, lvt_2_1_, "minecraft:shulker");
      p_registerEntities_1_.registerSimple(lvt_2_1_, "minecraft:shulker_bullet");
      registerEntity(p_registerEntities_1_, lvt_2_1_, "minecraft:silverfish");
      registerEntity(p_registerEntities_1_, lvt_2_1_, "minecraft:skeleton");
      p_registerEntities_1_.register(lvt_2_1_, "minecraft:skeleton_horse", (p_206592_1_) -> {
         return DSL.optionalFields("SaddleItem", TypeReferences.ITEM_STACK.in(p_registerEntities_1_), V0100.equipment(p_registerEntities_1_));
      });
      registerEntity(p_registerEntities_1_, lvt_2_1_, "minecraft:slime");
      registerThrowableProjectile(p_registerEntities_1_, lvt_2_1_, "minecraft:small_fireball");
      registerThrowableProjectile(p_registerEntities_1_, lvt_2_1_, "minecraft:snowball");
      registerEntity(p_registerEntities_1_, lvt_2_1_, "minecraft:snowman");
      p_registerEntities_1_.register(lvt_2_1_, "minecraft:spawner_minecart", (p_206583_1_) -> {
         return DSL.optionalFields("DisplayTile", TypeReferences.BLOCK_NAME.in(p_registerEntities_1_), TypeReferences.UNTAGGED_SPAWNER.in(p_registerEntities_1_));
      });
      p_registerEntities_1_.register(lvt_2_1_, "minecraft:spectral_arrow", (p_206571_1_) -> {
         return DSL.optionalFields("inTile", TypeReferences.BLOCK_NAME.in(p_registerEntities_1_));
      });
      registerEntity(p_registerEntities_1_, lvt_2_1_, "minecraft:spider");
      registerEntity(p_registerEntities_1_, lvt_2_1_, "minecraft:squid");
      registerEntity(p_registerEntities_1_, lvt_2_1_, "minecraft:stray");
      p_registerEntities_1_.registerSimple(lvt_2_1_, "minecraft:tnt");
      p_registerEntities_1_.register(lvt_2_1_, "minecraft:tnt_minecart", (p_206591_1_) -> {
         return DSL.optionalFields("DisplayTile", TypeReferences.BLOCK_NAME.in(p_registerEntities_1_));
      });
      p_registerEntities_1_.register(lvt_2_1_, "minecraft:villager", (p_206580_1_) -> {
         return DSL.optionalFields("Inventory", DSL.list(TypeReferences.ITEM_STACK.in(p_registerEntities_1_)), "Offers", DSL.optionalFields("Recipes", DSL.list(DSL.optionalFields("buy", TypeReferences.ITEM_STACK.in(p_registerEntities_1_), "buyB", TypeReferences.ITEM_STACK.in(p_registerEntities_1_), "sell", TypeReferences.ITEM_STACK.in(p_registerEntities_1_)))), V0100.equipment(p_registerEntities_1_));
      });
      registerEntity(p_registerEntities_1_, lvt_2_1_, "minecraft:villager_golem");
      registerEntity(p_registerEntities_1_, lvt_2_1_, "minecraft:witch");
      registerEntity(p_registerEntities_1_, lvt_2_1_, "minecraft:wither");
      registerEntity(p_registerEntities_1_, lvt_2_1_, "minecraft:wither_skeleton");
      registerThrowableProjectile(p_registerEntities_1_, lvt_2_1_, "minecraft:wither_skull");
      registerEntity(p_registerEntities_1_, lvt_2_1_, "minecraft:wolf");
      registerThrowableProjectile(p_registerEntities_1_, lvt_2_1_, "minecraft:xp_bottle");
      p_registerEntities_1_.registerSimple(lvt_2_1_, "minecraft:xp_orb");
      registerEntity(p_registerEntities_1_, lvt_2_1_, "minecraft:zombie");
      p_registerEntities_1_.register(lvt_2_1_, "minecraft:zombie_horse", (p_206569_1_) -> {
         return DSL.optionalFields("SaddleItem", TypeReferences.ITEM_STACK.in(p_registerEntities_1_), V0100.equipment(p_registerEntities_1_));
      });
      registerEntity(p_registerEntities_1_, lvt_2_1_, "minecraft:zombie_pigman");
      registerEntity(p_registerEntities_1_, lvt_2_1_, "minecraft:zombie_villager");
      p_registerEntities_1_.registerSimple(lvt_2_1_, "minecraft:evocation_fangs");
      registerEntity(p_registerEntities_1_, lvt_2_1_, "minecraft:evocation_illager");
      p_registerEntities_1_.registerSimple(lvt_2_1_, "minecraft:illusion_illager");
      p_registerEntities_1_.register(lvt_2_1_, "minecraft:llama", (p_209329_1_) -> {
         return DSL.optionalFields("Items", DSL.list(TypeReferences.ITEM_STACK.in(p_registerEntities_1_)), "SaddleItem", TypeReferences.ITEM_STACK.in(p_registerEntities_1_), "DecorItem", TypeReferences.ITEM_STACK.in(p_registerEntities_1_), V0100.equipment(p_registerEntities_1_));
      });
      p_registerEntities_1_.registerSimple(lvt_2_1_, "minecraft:llama_spit");
      registerEntity(p_registerEntities_1_, lvt_2_1_, "minecraft:vex");
      registerEntity(p_registerEntities_1_, lvt_2_1_, "minecraft:vindication_illager");
      return lvt_2_1_;
   }

   public void registerTypes(Schema p_registerTypes_1_, Map<String, Supplier<TypeTemplate>> p_registerTypes_2_, Map<String, Supplier<TypeTemplate>> p_registerTypes_3_) {
      super.registerTypes(p_registerTypes_1_, p_registerTypes_2_, p_registerTypes_3_);
      p_registerTypes_1_.registerType(true, TypeReferences.ENTITY, () -> {
         return DSL.taggedChoiceLazy("id", DSL.namespacedString(), p_registerTypes_2_);
      });
      p_registerTypes_1_.registerType(true, TypeReferences.ITEM_STACK, () -> {
         return DSL.hook(DSL.optionalFields("id", TypeReferences.ITEM_NAME.in(p_registerTypes_1_), "tag", DSL.optionalFields("EntityTag", TypeReferences.ENTITY_TYPE.in(p_registerTypes_1_), "BlockEntityTag", TypeReferences.BLOCK_ENTITY.in(p_registerTypes_1_), "CanDestroy", DSL.list(TypeReferences.BLOCK_NAME.in(p_registerTypes_1_)), "CanPlaceOn", DSL.list(TypeReferences.BLOCK_NAME.in(p_registerTypes_1_)))), field_206597_b, HookFunction.IDENTITY);
      });
   }
}
