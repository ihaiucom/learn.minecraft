package net.minecraft.util.datafix.fixes;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.util.datafix.TypeReferences;
import org.apache.commons.lang3.StringUtils;

public class StatsRenaming extends DataFix {
   private static final Set<String> field_209682_a = ImmutableSet.builder().add("stat.craftItem.minecraft.spawn_egg").add("stat.useItem.minecraft.spawn_egg").add("stat.breakItem.minecraft.spawn_egg").add("stat.pickup.minecraft.spawn_egg").add("stat.drop.minecraft.spawn_egg").build();
   private static final Map<String, String> field_209683_b = ImmutableMap.builder().put("stat.leaveGame", "minecraft:leave_game").put("stat.playOneMinute", "minecraft:play_one_minute").put("stat.timeSinceDeath", "minecraft:time_since_death").put("stat.sneakTime", "minecraft:sneak_time").put("stat.walkOneCm", "minecraft:walk_one_cm").put("stat.crouchOneCm", "minecraft:crouch_one_cm").put("stat.sprintOneCm", "minecraft:sprint_one_cm").put("stat.swimOneCm", "minecraft:swim_one_cm").put("stat.fallOneCm", "minecraft:fall_one_cm").put("stat.climbOneCm", "minecraft:climb_one_cm").put("stat.flyOneCm", "minecraft:fly_one_cm").put("stat.diveOneCm", "minecraft:dive_one_cm").put("stat.minecartOneCm", "minecraft:minecart_one_cm").put("stat.boatOneCm", "minecraft:boat_one_cm").put("stat.pigOneCm", "minecraft:pig_one_cm").put("stat.horseOneCm", "minecraft:horse_one_cm").put("stat.aviateOneCm", "minecraft:aviate_one_cm").put("stat.jump", "minecraft:jump").put("stat.drop", "minecraft:drop").put("stat.damageDealt", "minecraft:damage_dealt").put("stat.damageTaken", "minecraft:damage_taken").put("stat.deaths", "minecraft:deaths").put("stat.mobKills", "minecraft:mob_kills").put("stat.animalsBred", "minecraft:animals_bred").put("stat.playerKills", "minecraft:player_kills").put("stat.fishCaught", "minecraft:fish_caught").put("stat.talkedToVillager", "minecraft:talked_to_villager").put("stat.tradedWithVillager", "minecraft:traded_with_villager").put("stat.cakeSlicesEaten", "minecraft:eat_cake_slice").put("stat.cauldronFilled", "minecraft:fill_cauldron").put("stat.cauldronUsed", "minecraft:use_cauldron").put("stat.armorCleaned", "minecraft:clean_armor").put("stat.bannerCleaned", "minecraft:clean_banner").put("stat.brewingstandInteraction", "minecraft:interact_with_brewingstand").put("stat.beaconInteraction", "minecraft:interact_with_beacon").put("stat.dropperInspected", "minecraft:inspect_dropper").put("stat.hopperInspected", "minecraft:inspect_hopper").put("stat.dispenserInspected", "minecraft:inspect_dispenser").put("stat.noteblockPlayed", "minecraft:play_noteblock").put("stat.noteblockTuned", "minecraft:tune_noteblock").put("stat.flowerPotted", "minecraft:pot_flower").put("stat.trappedChestTriggered", "minecraft:trigger_trapped_chest").put("stat.enderchestOpened", "minecraft:open_enderchest").put("stat.itemEnchanted", "minecraft:enchant_item").put("stat.recordPlayed", "minecraft:play_record").put("stat.furnaceInteraction", "minecraft:interact_with_furnace").put("stat.craftingTableInteraction", "minecraft:interact_with_crafting_table").put("stat.chestOpened", "minecraft:open_chest").put("stat.sleepInBed", "minecraft:sleep_in_bed").put("stat.shulkerBoxOpened", "minecraft:open_shulker_box").build();
   private static final Map<String, String> field_199189_b = ImmutableMap.builder().put("stat.craftItem", "minecraft:crafted").put("stat.useItem", "minecraft:used").put("stat.breakItem", "minecraft:broken").put("stat.pickup", "minecraft:picked_up").put("stat.drop", "minecraft:dropped").build();
   private static final Map<String, String> field_209684_d = ImmutableMap.builder().put("stat.entityKilledBy", "minecraft:killed_by").put("stat.killEntity", "minecraft:killed").build();
   private static final Map<String, String> field_209685_e = ImmutableMap.builder().put("Bat", "minecraft:bat").put("Blaze", "minecraft:blaze").put("CaveSpider", "minecraft:cave_spider").put("Chicken", "minecraft:chicken").put("Cow", "minecraft:cow").put("Creeper", "minecraft:creeper").put("Donkey", "minecraft:donkey").put("ElderGuardian", "minecraft:elder_guardian").put("Enderman", "minecraft:enderman").put("Endermite", "minecraft:endermite").put("EvocationIllager", "minecraft:evocation_illager").put("Ghast", "minecraft:ghast").put("Guardian", "minecraft:guardian").put("Horse", "minecraft:horse").put("Husk", "minecraft:husk").put("Llama", "minecraft:llama").put("LavaSlime", "minecraft:magma_cube").put("MushroomCow", "minecraft:mooshroom").put("Mule", "minecraft:mule").put("Ozelot", "minecraft:ocelot").put("Parrot", "minecraft:parrot").put("Pig", "minecraft:pig").put("PolarBear", "minecraft:polar_bear").put("Rabbit", "minecraft:rabbit").put("Sheep", "minecraft:sheep").put("Shulker", "minecraft:shulker").put("Silverfish", "minecraft:silverfish").put("SkeletonHorse", "minecraft:skeleton_horse").put("Skeleton", "minecraft:skeleton").put("Slime", "minecraft:slime").put("Spider", "minecraft:spider").put("Squid", "minecraft:squid").put("Stray", "minecraft:stray").put("Vex", "minecraft:vex").put("Villager", "minecraft:villager").put("VindicationIllager", "minecraft:vindication_illager").put("Witch", "minecraft:witch").put("WitherSkeleton", "minecraft:wither_skeleton").put("Wolf", "minecraft:wolf").put("ZombieHorse", "minecraft:zombie_horse").put("PigZombie", "minecraft:zombie_pigman").put("ZombieVillager", "minecraft:zombie_villager").put("Zombie", "minecraft:zombie").build();

   public StatsRenaming(Schema p_i49615_1_, boolean p_i49615_2_) {
      super(p_i49615_1_, p_i49615_2_);
   }

   public TypeRewriteRule makeRule() {
      Type<?> lvt_1_1_ = this.getOutputSchema().getType(TypeReferences.STATS);
      return this.fixTypeEverywhereTyped("StatsCounterFix", this.getInputSchema().getType(TypeReferences.STATS), lvt_1_1_, (p_209680_2_) -> {
         Dynamic<?> lvt_3_1_ = (Dynamic)p_209680_2_.get(DSL.remainderFinder());
         Map<Dynamic<?>, Dynamic<?>> lvt_4_1_ = Maps.newHashMap();
         Optional<? extends Map<? extends Dynamic<?>, ? extends Dynamic<?>>> lvt_5_1_ = lvt_3_1_.getMapValues();
         if (lvt_5_1_.isPresent()) {
            Iterator var6 = ((Map)lvt_5_1_.get()).entrySet().iterator();

            while(true) {
               Entry lvt_7_1_;
               String lvt_9_4_;
               String lvt_10_4_;
               while(true) {
                  String lvt_8_1_;
                  do {
                     do {
                        if (!var6.hasNext()) {
                           return (Typed)((Optional)lvt_1_1_.readTyped(lvt_3_1_.emptyMap().set("stats", lvt_3_1_.createMap(lvt_4_1_))).getSecond()).orElseThrow(() -> {
                              return new IllegalStateException("Could not parse new stats object.");
                           });
                        }

                        lvt_7_1_ = (Entry)var6.next();
                     } while(!((Dynamic)lvt_7_1_.getValue()).asNumber().isPresent());

                     lvt_8_1_ = ((Dynamic)lvt_7_1_.getKey()).asString("");
                  } while(field_209682_a.contains(lvt_8_1_));

                  if (field_209683_b.containsKey(lvt_8_1_)) {
                     lvt_9_4_ = "minecraft:custom";
                     lvt_10_4_ = (String)field_209683_b.get(lvt_8_1_);
                     break;
                  }

                  int lvt_11_1_ = StringUtils.ordinalIndexOf(lvt_8_1_, ".", 2);
                  if (lvt_11_1_ >= 0) {
                     String lvt_12_1_ = lvt_8_1_.substring(0, lvt_11_1_);
                     if ("stat.mineBlock".equals(lvt_12_1_)) {
                        lvt_9_4_ = "minecraft:mined";
                        lvt_10_4_ = this.upgradeBlock(lvt_8_1_.substring(lvt_11_1_ + 1).replace('.', ':'));
                        break;
                     }

                     String lvt_13_2_;
                     if (field_199189_b.containsKey(lvt_12_1_)) {
                        lvt_9_4_ = (String)field_199189_b.get(lvt_12_1_);
                        lvt_13_2_ = lvt_8_1_.substring(lvt_11_1_ + 1).replace('.', ':');
                        String lvt_14_1_ = this.upgradeItem(lvt_13_2_);
                        lvt_10_4_ = lvt_14_1_ == null ? lvt_13_2_ : lvt_14_1_;
                        break;
                     }

                     if (field_209684_d.containsKey(lvt_12_1_)) {
                        lvt_9_4_ = (String)field_209684_d.get(lvt_12_1_);
                        lvt_13_2_ = lvt_8_1_.substring(lvt_11_1_ + 1).replace('.', ':');
                        lvt_10_4_ = (String)field_209685_e.getOrDefault(lvt_13_2_, lvt_13_2_);
                        break;
                     }
                  }
               }

               Dynamic<?> lvt_11_2_ = lvt_3_1_.createString(lvt_9_4_);
               Dynamic<?> lvt_12_2_ = (Dynamic)lvt_4_1_.computeIfAbsent(lvt_11_2_, (p_209679_1_) -> {
                  return lvt_3_1_.emptyMap();
               });
               lvt_4_1_.put(lvt_11_2_, lvt_12_2_.set(lvt_10_4_, (Dynamic)lvt_7_1_.getValue()));
            }
         } else {
            return (Typed)((Optional)lvt_1_1_.readTyped(lvt_3_1_.emptyMap().set("stats", lvt_3_1_.createMap(lvt_4_1_))).getSecond()).orElseThrow(() -> {
               return new IllegalStateException("Could not parse new stats object.");
            });
         }
      });
   }

   @Nullable
   protected String upgradeItem(String p_209681_1_) {
      return ItemStackDataFlattening.updateItem(p_209681_1_, 0);
   }

   protected String upgradeBlock(String p_206287_1_) {
      return BlockStateFlatteningMap.updateName(p_206287_1_);
   }
}
