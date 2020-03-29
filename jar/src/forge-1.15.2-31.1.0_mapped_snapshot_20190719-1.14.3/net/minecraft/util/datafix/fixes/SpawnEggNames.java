package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.util.datafix.TypeReferences;

public class SpawnEggNames extends DataFix {
   private static final String[] ENTITY_IDS = (String[])DataFixUtils.make(new String[256], (p_209278_0_) -> {
      p_209278_0_[1] = "Item";
      p_209278_0_[2] = "XPOrb";
      p_209278_0_[7] = "ThrownEgg";
      p_209278_0_[8] = "LeashKnot";
      p_209278_0_[9] = "Painting";
      p_209278_0_[10] = "Arrow";
      p_209278_0_[11] = "Snowball";
      p_209278_0_[12] = "Fireball";
      p_209278_0_[13] = "SmallFireball";
      p_209278_0_[14] = "ThrownEnderpearl";
      p_209278_0_[15] = "EyeOfEnderSignal";
      p_209278_0_[16] = "ThrownPotion";
      p_209278_0_[17] = "ThrownExpBottle";
      p_209278_0_[18] = "ItemFrame";
      p_209278_0_[19] = "WitherSkull";
      p_209278_0_[20] = "PrimedTnt";
      p_209278_0_[21] = "FallingSand";
      p_209278_0_[22] = "FireworksRocketEntity";
      p_209278_0_[23] = "TippedArrow";
      p_209278_0_[24] = "SpectralArrow";
      p_209278_0_[25] = "ShulkerBullet";
      p_209278_0_[26] = "DragonFireball";
      p_209278_0_[30] = "ArmorStand";
      p_209278_0_[41] = "Boat";
      p_209278_0_[42] = "MinecartRideable";
      p_209278_0_[43] = "MinecartChest";
      p_209278_0_[44] = "MinecartFurnace";
      p_209278_0_[45] = "MinecartTNT";
      p_209278_0_[46] = "MinecartHopper";
      p_209278_0_[47] = "MinecartSpawner";
      p_209278_0_[40] = "MinecartCommandBlock";
      p_209278_0_[48] = "Mob";
      p_209278_0_[49] = "Monster";
      p_209278_0_[50] = "Creeper";
      p_209278_0_[51] = "Skeleton";
      p_209278_0_[52] = "Spider";
      p_209278_0_[53] = "Giant";
      p_209278_0_[54] = "Zombie";
      p_209278_0_[55] = "Slime";
      p_209278_0_[56] = "Ghast";
      p_209278_0_[57] = "PigZombie";
      p_209278_0_[58] = "Enderman";
      p_209278_0_[59] = "CaveSpider";
      p_209278_0_[60] = "Silverfish";
      p_209278_0_[61] = "Blaze";
      p_209278_0_[62] = "LavaSlime";
      p_209278_0_[63] = "EnderDragon";
      p_209278_0_[64] = "WitherBoss";
      p_209278_0_[65] = "Bat";
      p_209278_0_[66] = "Witch";
      p_209278_0_[67] = "Endermite";
      p_209278_0_[68] = "Guardian";
      p_209278_0_[69] = "Shulker";
      p_209278_0_[90] = "Pig";
      p_209278_0_[91] = "Sheep";
      p_209278_0_[92] = "Cow";
      p_209278_0_[93] = "Chicken";
      p_209278_0_[94] = "Squid";
      p_209278_0_[95] = "Wolf";
      p_209278_0_[96] = "MushroomCow";
      p_209278_0_[97] = "SnowMan";
      p_209278_0_[98] = "Ozelot";
      p_209278_0_[99] = "VillagerGolem";
      p_209278_0_[100] = "EntityHorse";
      p_209278_0_[101] = "Rabbit";
      p_209278_0_[120] = "Villager";
      p_209278_0_[200] = "EnderCrystal";
   });

   public SpawnEggNames(Schema p_i49639_1_, boolean p_i49639_2_) {
      super(p_i49639_1_, p_i49639_2_);
   }

   public TypeRewriteRule makeRule() {
      Schema lvt_1_1_ = this.getInputSchema();
      Type<?> lvt_2_1_ = lvt_1_1_.getType(TypeReferences.ITEM_STACK);
      OpticFinder<Pair<String, String>> lvt_3_1_ = DSL.fieldFinder("id", DSL.named(TypeReferences.ITEM_NAME.typeName(), DSL.namespacedString()));
      OpticFinder<String> lvt_4_1_ = DSL.fieldFinder("id", DSL.string());
      OpticFinder<?> lvt_5_1_ = lvt_2_1_.findField("tag");
      OpticFinder<?> lvt_6_1_ = lvt_5_1_.type().findField("EntityTag");
      OpticFinder<?> lvt_7_1_ = DSL.typeFinder(lvt_1_1_.getTypeRaw(TypeReferences.ENTITY));
      return this.fixTypeEverywhereTyped("ItemSpawnEggFix", lvt_2_1_, (p_206359_6_) -> {
         Optional<Pair<String, String>> lvt_7_1_x = p_206359_6_.getOptional(lvt_3_1_);
         if (lvt_7_1_x.isPresent() && Objects.equals(((Pair)lvt_7_1_x.get()).getSecond(), "minecraft:spawn_egg")) {
            Dynamic<?> lvt_8_1_ = (Dynamic)p_206359_6_.get(DSL.remainderFinder());
            short lvt_9_1_ = lvt_8_1_.get("Damage").asShort((short)0);
            Optional<? extends Typed<?>> lvt_10_1_ = p_206359_6_.getOptionalTyped(lvt_5_1_);
            Optional<? extends Typed<?>> lvt_11_1_ = lvt_10_1_.flatMap((p_207479_1_) -> {
               return p_207479_1_.getOptionalTyped(lvt_6_1_);
            });
            Optional<? extends Typed<?>> lvt_12_1_ = lvt_11_1_.flatMap((p_207482_1_) -> {
               return p_207482_1_.getOptionalTyped(lvt_7_1_);
            });
            Optional<String> lvt_13_1_ = lvt_12_1_.flatMap((p_207481_1_) -> {
               return p_207481_1_.getOptional(lvt_4_1_);
            });
            Typed<?> lvt_14_1_ = p_206359_6_;
            String lvt_15_1_ = ENTITY_IDS[lvt_9_1_ & 255];
            if (lvt_15_1_ != null && (!lvt_13_1_.isPresent() || !Objects.equals(lvt_13_1_.get(), lvt_15_1_))) {
               Typed<?> lvt_16_1_ = p_206359_6_.getOrCreateTyped(lvt_5_1_);
               Typed<?> lvt_17_1_ = lvt_16_1_.getOrCreateTyped(lvt_6_1_);
               Typed<?> lvt_18_1_ = lvt_17_1_.getOrCreateTyped(lvt_7_1_);
               Dynamic<?> lvt_19_1_ = lvt_18_1_.write().set("id", lvt_8_1_.createString(lvt_15_1_));
               Typed<?> lvt_20_1_ = (Typed)((Optional)this.getOutputSchema().getTypeRaw(TypeReferences.ENTITY).readTyped(lvt_19_1_).getSecond()).orElseThrow(() -> {
                  return new IllegalStateException("Could not parse new entity");
               });
               lvt_14_1_ = p_206359_6_.set(lvt_5_1_, lvt_16_1_.set(lvt_6_1_, lvt_17_1_.set(lvt_7_1_, lvt_20_1_)));
            }

            if (lvt_9_1_ != 0) {
               lvt_8_1_ = lvt_8_1_.set("Damage", lvt_8_1_.createShort((short)0));
               lvt_14_1_ = lvt_14_1_.set(DSL.remainderFinder(), lvt_8_1_);
            }

            return lvt_14_1_;
         } else {
            return p_206359_6_;
         }
      });
   }
}
