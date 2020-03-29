package net.minecraft.data.advancements;

import java.util.function.Consumer;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.criterion.BrewedPotionTrigger;
import net.minecraft.advancements.criterion.ChangeDimensionTrigger;
import net.minecraft.advancements.criterion.ConstructBeaconTrigger;
import net.minecraft.advancements.criterion.DamageSourcePredicate;
import net.minecraft.advancements.criterion.DistancePredicate;
import net.minecraft.advancements.criterion.EffectsChangedTrigger;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.advancements.criterion.KilledTrigger;
import net.minecraft.advancements.criterion.LocationPredicate;
import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.advancements.criterion.MobEffectsPredicate;
import net.minecraft.advancements.criterion.NetherTravelTrigger;
import net.minecraft.advancements.criterion.PositionTrigger;
import net.minecraft.advancements.criterion.SummonedEntityTrigger;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Items;
import net.minecraft.potion.Effects;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.feature.Feature;

public class NetherAdvancements implements Consumer<Consumer<Advancement>> {
   public void accept(Consumer<Advancement> p_accept_1_) {
      Advancement lvt_2_1_ = Advancement.Builder.builder().withDisplay(Blocks.RED_NETHER_BRICKS, new TranslationTextComponent("advancements.nether.root.title", new Object[0]), new TranslationTextComponent("advancements.nether.root.description", new Object[0]), new ResourceLocation("textures/gui/advancements/backgrounds/nether.png"), FrameType.TASK, false, false, false).withCriterion("entered_nether", (ICriterionInstance)ChangeDimensionTrigger.Instance.changedDimensionTo(DimensionType.THE_NETHER)).register(p_accept_1_, "nether/root");
      Advancement lvt_3_1_ = Advancement.Builder.builder().withParent(lvt_2_1_).withDisplay(Items.FIRE_CHARGE, new TranslationTextComponent("advancements.nether.return_to_sender.title", new Object[0]), new TranslationTextComponent("advancements.nether.return_to_sender.description", new Object[0]), (ResourceLocation)null, FrameType.CHALLENGE, true, true, false).withRewards(AdvancementRewards.Builder.experience(50)).withCriterion("killed_ghast", (ICriterionInstance)KilledTrigger.Instance.playerKilledEntity(EntityPredicate.Builder.create().type(EntityType.GHAST), DamageSourcePredicate.Builder.damageType().isProjectile(true).direct(EntityPredicate.Builder.create().type(EntityType.FIREBALL)))).register(p_accept_1_, "nether/return_to_sender");
      Advancement lvt_4_1_ = Advancement.Builder.builder().withParent(lvt_2_1_).withDisplay(Blocks.NETHER_BRICKS, new TranslationTextComponent("advancements.nether.find_fortress.title", new Object[0]), new TranslationTextComponent("advancements.nether.find_fortress.description", new Object[0]), (ResourceLocation)null, FrameType.TASK, true, true, false).withCriterion("fortress", (ICriterionInstance)PositionTrigger.Instance.forLocation(LocationPredicate.forFeature(Feature.NETHER_BRIDGE))).register(p_accept_1_, "nether/find_fortress");
      Advancement lvt_5_1_ = Advancement.Builder.builder().withParent(lvt_2_1_).withDisplay(Items.MAP, new TranslationTextComponent("advancements.nether.fast_travel.title", new Object[0]), new TranslationTextComponent("advancements.nether.fast_travel.description", new Object[0]), (ResourceLocation)null, FrameType.CHALLENGE, true, true, false).withRewards(AdvancementRewards.Builder.experience(100)).withCriterion("travelled", (ICriterionInstance)NetherTravelTrigger.Instance.forDistance(DistancePredicate.forHorizontal(MinMaxBounds.FloatBound.atLeast(7000.0F)))).register(p_accept_1_, "nether/fast_travel");
      Advancement lvt_6_1_ = Advancement.Builder.builder().withParent(lvt_3_1_).withDisplay(Items.GHAST_TEAR, new TranslationTextComponent("advancements.nether.uneasy_alliance.title", new Object[0]), new TranslationTextComponent("advancements.nether.uneasy_alliance.description", new Object[0]), (ResourceLocation)null, FrameType.CHALLENGE, true, true, false).withRewards(AdvancementRewards.Builder.experience(100)).withCriterion("killed_ghast", (ICriterionInstance)KilledTrigger.Instance.playerKilledEntity(EntityPredicate.Builder.create().type(EntityType.GHAST).location(LocationPredicate.forDimension(DimensionType.OVERWORLD)))).register(p_accept_1_, "nether/uneasy_alliance");
      Advancement lvt_7_1_ = Advancement.Builder.builder().withParent(lvt_4_1_).withDisplay(Blocks.WITHER_SKELETON_SKULL, new TranslationTextComponent("advancements.nether.get_wither_skull.title", new Object[0]), new TranslationTextComponent("advancements.nether.get_wither_skull.description", new Object[0]), (ResourceLocation)null, FrameType.TASK, true, true, false).withCriterion("wither_skull", (ICriterionInstance)InventoryChangeTrigger.Instance.forItems(Blocks.WITHER_SKELETON_SKULL)).register(p_accept_1_, "nether/get_wither_skull");
      Advancement lvt_8_1_ = Advancement.Builder.builder().withParent(lvt_7_1_).withDisplay(Items.NETHER_STAR, new TranslationTextComponent("advancements.nether.summon_wither.title", new Object[0]), new TranslationTextComponent("advancements.nether.summon_wither.description", new Object[0]), (ResourceLocation)null, FrameType.TASK, true, true, false).withCriterion("summoned", (ICriterionInstance)SummonedEntityTrigger.Instance.summonedEntity(EntityPredicate.Builder.create().type(EntityType.WITHER))).register(p_accept_1_, "nether/summon_wither");
      Advancement lvt_9_1_ = Advancement.Builder.builder().withParent(lvt_4_1_).withDisplay(Items.BLAZE_ROD, new TranslationTextComponent("advancements.nether.obtain_blaze_rod.title", new Object[0]), new TranslationTextComponent("advancements.nether.obtain_blaze_rod.description", new Object[0]), (ResourceLocation)null, FrameType.TASK, true, true, false).withCriterion("blaze_rod", (ICriterionInstance)InventoryChangeTrigger.Instance.forItems(Items.BLAZE_ROD)).register(p_accept_1_, "nether/obtain_blaze_rod");
      Advancement lvt_10_1_ = Advancement.Builder.builder().withParent(lvt_8_1_).withDisplay(Blocks.BEACON, new TranslationTextComponent("advancements.nether.create_beacon.title", new Object[0]), new TranslationTextComponent("advancements.nether.create_beacon.description", new Object[0]), (ResourceLocation)null, FrameType.TASK, true, true, false).withCriterion("beacon", (ICriterionInstance)ConstructBeaconTrigger.Instance.forLevel(MinMaxBounds.IntBound.atLeast(1))).register(p_accept_1_, "nether/create_beacon");
      Advancement lvt_11_1_ = Advancement.Builder.builder().withParent(lvt_10_1_).withDisplay(Blocks.BEACON, new TranslationTextComponent("advancements.nether.create_full_beacon.title", new Object[0]), new TranslationTextComponent("advancements.nether.create_full_beacon.description", new Object[0]), (ResourceLocation)null, FrameType.GOAL, true, true, false).withCriterion("beacon", (ICriterionInstance)ConstructBeaconTrigger.Instance.forLevel(MinMaxBounds.IntBound.exactly(4))).register(p_accept_1_, "nether/create_full_beacon");
      Advancement lvt_12_1_ = Advancement.Builder.builder().withParent(lvt_9_1_).withDisplay(Items.POTION, new TranslationTextComponent("advancements.nether.brew_potion.title", new Object[0]), new TranslationTextComponent("advancements.nether.brew_potion.description", new Object[0]), (ResourceLocation)null, FrameType.TASK, true, true, false).withCriterion("potion", (ICriterionInstance)BrewedPotionTrigger.Instance.brewedPotion()).register(p_accept_1_, "nether/brew_potion");
      Advancement lvt_13_1_ = Advancement.Builder.builder().withParent(lvt_12_1_).withDisplay(Items.MILK_BUCKET, new TranslationTextComponent("advancements.nether.all_potions.title", new Object[0]), new TranslationTextComponent("advancements.nether.all_potions.description", new Object[0]), (ResourceLocation)null, FrameType.CHALLENGE, true, true, false).withRewards(AdvancementRewards.Builder.experience(100)).withCriterion("all_effects", (ICriterionInstance)EffectsChangedTrigger.Instance.forEffect(MobEffectsPredicate.any().addEffect(Effects.SPEED).addEffect(Effects.SLOWNESS).addEffect(Effects.STRENGTH).addEffect(Effects.JUMP_BOOST).addEffect(Effects.REGENERATION).addEffect(Effects.FIRE_RESISTANCE).addEffect(Effects.WATER_BREATHING).addEffect(Effects.INVISIBILITY).addEffect(Effects.NIGHT_VISION).addEffect(Effects.WEAKNESS).addEffect(Effects.POISON).addEffect(Effects.SLOW_FALLING).addEffect(Effects.RESISTANCE))).register(p_accept_1_, "nether/all_potions");
      Advancement lvt_14_1_ = Advancement.Builder.builder().withParent(lvt_13_1_).withDisplay(Items.BUCKET, new TranslationTextComponent("advancements.nether.all_effects.title", new Object[0]), new TranslationTextComponent("advancements.nether.all_effects.description", new Object[0]), (ResourceLocation)null, FrameType.CHALLENGE, true, true, true).withRewards(AdvancementRewards.Builder.experience(1000)).withCriterion("all_effects", (ICriterionInstance)EffectsChangedTrigger.Instance.forEffect(MobEffectsPredicate.any().addEffect(Effects.SPEED).addEffect(Effects.SLOWNESS).addEffect(Effects.STRENGTH).addEffect(Effects.JUMP_BOOST).addEffect(Effects.REGENERATION).addEffect(Effects.FIRE_RESISTANCE).addEffect(Effects.WATER_BREATHING).addEffect(Effects.INVISIBILITY).addEffect(Effects.NIGHT_VISION).addEffect(Effects.WEAKNESS).addEffect(Effects.POISON).addEffect(Effects.WITHER).addEffect(Effects.HASTE).addEffect(Effects.MINING_FATIGUE).addEffect(Effects.LEVITATION).addEffect(Effects.GLOWING).addEffect(Effects.ABSORPTION).addEffect(Effects.HUNGER).addEffect(Effects.NAUSEA).addEffect(Effects.RESISTANCE).addEffect(Effects.SLOW_FALLING).addEffect(Effects.CONDUIT_POWER).addEffect(Effects.DOLPHINS_GRACE).addEffect(Effects.BLINDNESS).addEffect(Effects.BAD_OMEN).addEffect(Effects.HERO_OF_THE_VILLAGE))).register(p_accept_1_, "nether/all_effects");
   }

   // $FF: synthetic method
   public void accept(Object p_accept_1_) {
      this.accept((Consumer)p_accept_1_);
   }
}
