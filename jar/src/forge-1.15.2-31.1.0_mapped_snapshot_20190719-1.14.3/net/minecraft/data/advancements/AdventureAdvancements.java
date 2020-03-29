package net.minecraft.data.advancements;

import java.util.function.Consumer;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.IRequirementsStrategy;
import net.minecraft.advancements.criterion.ChanneledLightningTrigger;
import net.minecraft.advancements.criterion.DamagePredicate;
import net.minecraft.advancements.criterion.DamageSourcePredicate;
import net.minecraft.advancements.criterion.DistancePredicate;
import net.minecraft.advancements.criterion.EntityEquipmentPredicate;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.advancements.criterion.KilledByCrossbowTrigger;
import net.minecraft.advancements.criterion.KilledTrigger;
import net.minecraft.advancements.criterion.LocationPredicate;
import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.advancements.criterion.PlayerHurtEntityTrigger;
import net.minecraft.advancements.criterion.PositionTrigger;
import net.minecraft.advancements.criterion.ShotCrossbowTrigger;
import net.minecraft.advancements.criterion.SlideDownBlockTrigger;
import net.minecraft.advancements.criterion.SummonedEntityTrigger;
import net.minecraft.advancements.criterion.UsedTotemTrigger;
import net.minecraft.advancements.criterion.VillagerTradeTrigger;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Items;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.raid.Raid;

public class AdventureAdvancements implements Consumer<Consumer<Advancement>> {
   private static final Biome[] EXPLORATION_BIOMES;
   private static final EntityType<?>[] field_218459_b;

   public void accept(Consumer<Advancement> p_accept_1_) {
      Advancement lvt_2_1_ = Advancement.Builder.builder().withDisplay(Items.MAP, new TranslationTextComponent("advancements.adventure.root.title", new Object[0]), new TranslationTextComponent("advancements.adventure.root.description", new Object[0]), new ResourceLocation("textures/gui/advancements/backgrounds/adventure.png"), FrameType.TASK, false, false, false).withRequirementsStrategy(IRequirementsStrategy.OR).withCriterion("killed_something", (ICriterionInstance)KilledTrigger.Instance.playerKilledEntity()).withCriterion("killed_by_something", (ICriterionInstance)KilledTrigger.Instance.entityKilledPlayer()).register(p_accept_1_, "adventure/root");
      Advancement lvt_3_1_ = Advancement.Builder.builder().withParent(lvt_2_1_).withDisplay(Blocks.RED_BED, new TranslationTextComponent("advancements.adventure.sleep_in_bed.title", new Object[0]), new TranslationTextComponent("advancements.adventure.sleep_in_bed.description", new Object[0]), (ResourceLocation)null, FrameType.TASK, true, true, false).withCriterion("slept_in_bed", (ICriterionInstance)PositionTrigger.Instance.sleptInBed()).register(p_accept_1_, "adventure/sleep_in_bed");
      Advancement lvt_4_1_ = this.makeBiomeAdvancement(Advancement.Builder.builder()).withParent(lvt_3_1_).withDisplay(Items.DIAMOND_BOOTS, new TranslationTextComponent("advancements.adventure.adventuring_time.title", new Object[0]), new TranslationTextComponent("advancements.adventure.adventuring_time.description", new Object[0]), (ResourceLocation)null, FrameType.CHALLENGE, true, true, false).withRewards(AdvancementRewards.Builder.experience(500)).register(p_accept_1_, "adventure/adventuring_time");
      Advancement lvt_5_1_ = Advancement.Builder.builder().withParent(lvt_2_1_).withDisplay(Items.EMERALD, new TranslationTextComponent("advancements.adventure.trade.title", new Object[0]), new TranslationTextComponent("advancements.adventure.trade.description", new Object[0]), (ResourceLocation)null, FrameType.TASK, true, true, false).withCriterion("traded", (ICriterionInstance)VillagerTradeTrigger.Instance.any()).register(p_accept_1_, "adventure/trade");
      Advancement lvt_6_1_ = this.makeMobAdvancement(Advancement.Builder.builder()).withParent(lvt_2_1_).withDisplay(Items.IRON_SWORD, new TranslationTextComponent("advancements.adventure.kill_a_mob.title", new Object[0]), new TranslationTextComponent("advancements.adventure.kill_a_mob.description", new Object[0]), (ResourceLocation)null, FrameType.TASK, true, true, false).withRequirementsStrategy(IRequirementsStrategy.OR).register(p_accept_1_, "adventure/kill_a_mob");
      Advancement lvt_7_1_ = this.makeMobAdvancement(Advancement.Builder.builder()).withParent(lvt_6_1_).withDisplay(Items.DIAMOND_SWORD, new TranslationTextComponent("advancements.adventure.kill_all_mobs.title", new Object[0]), new TranslationTextComponent("advancements.adventure.kill_all_mobs.description", new Object[0]), (ResourceLocation)null, FrameType.CHALLENGE, true, true, false).withRewards(AdvancementRewards.Builder.experience(100)).register(p_accept_1_, "adventure/kill_all_mobs");
      Advancement lvt_8_1_ = Advancement.Builder.builder().withParent(lvt_6_1_).withDisplay(Items.BOW, new TranslationTextComponent("advancements.adventure.shoot_arrow.title", new Object[0]), new TranslationTextComponent("advancements.adventure.shoot_arrow.description", new Object[0]), (ResourceLocation)null, FrameType.TASK, true, true, false).withCriterion("shot_arrow", (ICriterionInstance)PlayerHurtEntityTrigger.Instance.forDamage(DamagePredicate.Builder.create().type(DamageSourcePredicate.Builder.damageType().isProjectile(true).direct(EntityPredicate.Builder.create().func_217989_a(EntityTypeTags.field_226156_d_))))).register(p_accept_1_, "adventure/shoot_arrow");
      Advancement lvt_9_1_ = Advancement.Builder.builder().withParent(lvt_6_1_).withDisplay(Items.TRIDENT, new TranslationTextComponent("advancements.adventure.throw_trident.title", new Object[0]), new TranslationTextComponent("advancements.adventure.throw_trident.description", new Object[0]), (ResourceLocation)null, FrameType.TASK, true, true, false).withCriterion("shot_trident", (ICriterionInstance)PlayerHurtEntityTrigger.Instance.forDamage(DamagePredicate.Builder.create().type(DamageSourcePredicate.Builder.damageType().isProjectile(true).direct(EntityPredicate.Builder.create().type(EntityType.TRIDENT))))).register(p_accept_1_, "adventure/throw_trident");
      Advancement lvt_10_1_ = Advancement.Builder.builder().withParent(lvt_9_1_).withDisplay(Items.TRIDENT, new TranslationTextComponent("advancements.adventure.very_very_frightening.title", new Object[0]), new TranslationTextComponent("advancements.adventure.very_very_frightening.description", new Object[0]), (ResourceLocation)null, FrameType.TASK, true, true, false).withCriterion("struck_villager", (ICriterionInstance)ChanneledLightningTrigger.Instance.channeledLightning(EntityPredicate.Builder.create().type(EntityType.VILLAGER).build())).register(p_accept_1_, "adventure/very_very_frightening");
      Advancement lvt_11_1_ = Advancement.Builder.builder().withParent(lvt_5_1_).withDisplay(Blocks.CARVED_PUMPKIN, new TranslationTextComponent("advancements.adventure.summon_iron_golem.title", new Object[0]), new TranslationTextComponent("advancements.adventure.summon_iron_golem.description", new Object[0]), (ResourceLocation)null, FrameType.GOAL, true, true, false).withCriterion("summoned_golem", (ICriterionInstance)SummonedEntityTrigger.Instance.summonedEntity(EntityPredicate.Builder.create().type(EntityType.IRON_GOLEM))).register(p_accept_1_, "adventure/summon_iron_golem");
      Advancement lvt_12_1_ = Advancement.Builder.builder().withParent(lvt_8_1_).withDisplay(Items.ARROW, new TranslationTextComponent("advancements.adventure.sniper_duel.title", new Object[0]), new TranslationTextComponent("advancements.adventure.sniper_duel.description", new Object[0]), (ResourceLocation)null, FrameType.CHALLENGE, true, true, false).withRewards(AdvancementRewards.Builder.experience(50)).withCriterion("killed_skeleton", (ICriterionInstance)KilledTrigger.Instance.playerKilledEntity(EntityPredicate.Builder.create().type(EntityType.SKELETON).distance(DistancePredicate.forHorizontal(MinMaxBounds.FloatBound.atLeast(50.0F))), DamageSourcePredicate.Builder.damageType().isProjectile(true))).register(p_accept_1_, "adventure/sniper_duel");
      Advancement lvt_13_1_ = Advancement.Builder.builder().withParent(lvt_6_1_).withDisplay(Items.TOTEM_OF_UNDYING, new TranslationTextComponent("advancements.adventure.totem_of_undying.title", new Object[0]), new TranslationTextComponent("advancements.adventure.totem_of_undying.description", new Object[0]), (ResourceLocation)null, FrameType.GOAL, true, true, false).withCriterion("used_totem", (ICriterionInstance)UsedTotemTrigger.Instance.usedTotem(Items.TOTEM_OF_UNDYING)).register(p_accept_1_, "adventure/totem_of_undying");
      Advancement lvt_14_1_ = Advancement.Builder.builder().withParent(lvt_2_1_).withDisplay(Items.CROSSBOW, new TranslationTextComponent("advancements.adventure.ol_betsy.title", new Object[0]), new TranslationTextComponent("advancements.adventure.ol_betsy.description", new Object[0]), (ResourceLocation)null, FrameType.TASK, true, true, false).withCriterion("shot_crossbow", (ICriterionInstance)ShotCrossbowTrigger.Instance.func_215122_a(Items.CROSSBOW)).register(p_accept_1_, "adventure/ol_betsy");
      Advancement lvt_15_1_ = Advancement.Builder.builder().withParent(lvt_14_1_).withDisplay(Items.CROSSBOW, new TranslationTextComponent("advancements.adventure.whos_the_pillager_now.title", new Object[0]), new TranslationTextComponent("advancements.adventure.whos_the_pillager_now.description", new Object[0]), (ResourceLocation)null, FrameType.TASK, true, true, false).withCriterion("kill_pillager", (ICriterionInstance)KilledByCrossbowTrigger.Instance.func_215116_a(EntityPredicate.Builder.create().type(EntityType.PILLAGER))).register(p_accept_1_, "adventure/whos_the_pillager_now");
      Advancement lvt_16_1_ = Advancement.Builder.builder().withParent(lvt_14_1_).withDisplay(Items.CROSSBOW, new TranslationTextComponent("advancements.adventure.two_birds_one_arrow.title", new Object[0]), new TranslationTextComponent("advancements.adventure.two_birds_one_arrow.description", new Object[0]), (ResourceLocation)null, FrameType.CHALLENGE, true, true, false).withRewards(AdvancementRewards.Builder.experience(65)).withCriterion("two_birds", (ICriterionInstance)KilledByCrossbowTrigger.Instance.func_215116_a(EntityPredicate.Builder.create().type(EntityType.PHANTOM), EntityPredicate.Builder.create().type(EntityType.PHANTOM))).register(p_accept_1_, "adventure/two_birds_one_arrow");
      Advancement lvt_17_1_ = Advancement.Builder.builder().withParent(lvt_14_1_).withDisplay(Items.CROSSBOW, new TranslationTextComponent("advancements.adventure.arbalistic.title", new Object[0]), new TranslationTextComponent("advancements.adventure.arbalistic.description", new Object[0]), (ResourceLocation)null, FrameType.CHALLENGE, true, true, true).withRewards(AdvancementRewards.Builder.experience(85)).withCriterion("arbalistic", (ICriterionInstance)KilledByCrossbowTrigger.Instance.func_215117_a(MinMaxBounds.IntBound.exactly(5))).register(p_accept_1_, "adventure/arbalistic");
      Advancement lvt_18_1_ = Advancement.Builder.builder().withParent(lvt_2_1_).func_215092_a(Raid.createIllagerBanner(), new TranslationTextComponent("advancements.adventure.voluntary_exile.title", new Object[0]), new TranslationTextComponent("advancements.adventure.voluntary_exile.description", new Object[0]), (ResourceLocation)null, FrameType.TASK, true, true, true).withCriterion("voluntary_exile", (ICriterionInstance)KilledTrigger.Instance.playerKilledEntity(EntityPredicate.Builder.create().func_217989_a(EntityTypeTags.RAIDERS).func_217985_a(EntityEquipmentPredicate.WEARING_ILLAGER_BANNER))).register(p_accept_1_, "adventure/voluntary_exile");
      Advancement lvt_19_1_ = Advancement.Builder.builder().withParent(lvt_18_1_).func_215092_a(Raid.createIllagerBanner(), new TranslationTextComponent("advancements.adventure.hero_of_the_village.title", new Object[0]), new TranslationTextComponent("advancements.adventure.hero_of_the_village.description", new Object[0]), (ResourceLocation)null, FrameType.CHALLENGE, true, true, true).withRewards(AdvancementRewards.Builder.experience(100)).withCriterion("hero_of_the_village", (ICriterionInstance)PositionTrigger.Instance.func_215120_d()).register(p_accept_1_, "adventure/hero_of_the_village");
      Advancement lvt_20_1_ = Advancement.Builder.builder().withParent(lvt_2_1_).withDisplay(Blocks.field_226907_mc_.asItem(), new TranslationTextComponent("advancements.adventure.honey_block_slide.title", new Object[0]), new TranslationTextComponent("advancements.adventure.honey_block_slide.description", new Object[0]), (ResourceLocation)null, FrameType.TASK, true, true, false).withCriterion("honey_block_slide", (ICriterionInstance)SlideDownBlockTrigger.Instance.func_227156_a_(Blocks.field_226907_mc_)).register(p_accept_1_, "adventure/honey_block_slide");
   }

   private Advancement.Builder makeMobAdvancement(Advancement.Builder p_204284_1_) {
      EntityType[] var2 = field_218459_b;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         EntityType<?> lvt_5_1_ = var2[var4];
         p_204284_1_.withCriterion(Registry.ENTITY_TYPE.getKey(lvt_5_1_).toString(), (ICriterionInstance)KilledTrigger.Instance.playerKilledEntity(EntityPredicate.Builder.create().type(lvt_5_1_)));
      }

      return p_204284_1_;
   }

   private Advancement.Builder makeBiomeAdvancement(Advancement.Builder p_204285_1_) {
      Biome[] var2 = EXPLORATION_BIOMES;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Biome lvt_5_1_ = var2[var4];
         p_204285_1_.withCriterion(Registry.BIOME.getKey(lvt_5_1_).toString(), (ICriterionInstance)PositionTrigger.Instance.forLocation(LocationPredicate.forBiome(lvt_5_1_)));
      }

      return p_204285_1_;
   }

   // $FF: synthetic method
   public void accept(Object p_accept_1_) {
      this.accept((Consumer)p_accept_1_);
   }

   static {
      EXPLORATION_BIOMES = new Biome[]{Biomes.BIRCH_FOREST_HILLS, Biomes.RIVER, Biomes.SWAMP, Biomes.DESERT, Biomes.WOODED_HILLS, Biomes.GIANT_TREE_TAIGA_HILLS, Biomes.SNOWY_TAIGA, Biomes.BADLANDS, Biomes.FOREST, Biomes.STONE_SHORE, Biomes.SNOWY_TUNDRA, Biomes.TAIGA_HILLS, Biomes.SNOWY_MOUNTAINS, Biomes.WOODED_BADLANDS_PLATEAU, Biomes.SAVANNA, Biomes.PLAINS, Biomes.FROZEN_RIVER, Biomes.GIANT_TREE_TAIGA, Biomes.SNOWY_BEACH, Biomes.JUNGLE_HILLS, Biomes.JUNGLE_EDGE, Biomes.MUSHROOM_FIELD_SHORE, Biomes.MOUNTAINS, Biomes.DESERT_HILLS, Biomes.JUNGLE, Biomes.BEACH, Biomes.SAVANNA_PLATEAU, Biomes.SNOWY_TAIGA_HILLS, Biomes.BADLANDS_PLATEAU, Biomes.DARK_FOREST, Biomes.TAIGA, Biomes.BIRCH_FOREST, Biomes.MUSHROOM_FIELDS, Biomes.WOODED_MOUNTAINS, Biomes.WARM_OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.COLD_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN, Biomes.DEEP_COLD_OCEAN, Biomes.DEEP_FROZEN_OCEAN, Biomes.BAMBOO_JUNGLE, Biomes.BAMBOO_JUNGLE_HILLS};
      field_218459_b = new EntityType[]{EntityType.CAVE_SPIDER, EntityType.SPIDER, EntityType.ZOMBIE_PIGMAN, EntityType.ENDERMAN, EntityType.BLAZE, EntityType.CREEPER, EntityType.EVOKER, EntityType.GHAST, EntityType.GUARDIAN, EntityType.HUSK, EntityType.MAGMA_CUBE, EntityType.SHULKER, EntityType.SILVERFISH, EntityType.SKELETON, EntityType.SLIME, EntityType.STRAY, EntityType.VINDICATOR, EntityType.WITCH, EntityType.WITHER_SKELETON, EntityType.ZOMBIE, EntityType.ZOMBIE_VILLAGER, EntityType.PHANTOM, EntityType.DROWNED, EntityType.PILLAGER, EntityType.RAVAGER};
   }
}
