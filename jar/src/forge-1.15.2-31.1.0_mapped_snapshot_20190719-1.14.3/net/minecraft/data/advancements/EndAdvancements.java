package net.minecraft.data.advancements;

import java.util.function.Consumer;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.criterion.ChangeDimensionTrigger;
import net.minecraft.advancements.criterion.DistancePredicate;
import net.minecraft.advancements.criterion.EnterBlockTrigger;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.advancements.criterion.KilledTrigger;
import net.minecraft.advancements.criterion.LevitationTrigger;
import net.minecraft.advancements.criterion.LocationPredicate;
import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.advancements.criterion.PositionTrigger;
import net.minecraft.advancements.criterion.SummonedEntityTrigger;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.feature.Feature;

public class EndAdvancements implements Consumer<Consumer<Advancement>> {
   public void accept(Consumer<Advancement> p_accept_1_) {
      Advancement lvt_2_1_ = Advancement.Builder.builder().withDisplay(Blocks.END_STONE, new TranslationTextComponent("advancements.end.root.title", new Object[0]), new TranslationTextComponent("advancements.end.root.description", new Object[0]), new ResourceLocation("textures/gui/advancements/backgrounds/end.png"), FrameType.TASK, false, false, false).withCriterion("entered_end", (ICriterionInstance)ChangeDimensionTrigger.Instance.changedDimensionTo(DimensionType.THE_END)).register(p_accept_1_, "end/root");
      Advancement lvt_3_1_ = Advancement.Builder.builder().withParent(lvt_2_1_).withDisplay(Blocks.DRAGON_HEAD, new TranslationTextComponent("advancements.end.kill_dragon.title", new Object[0]), new TranslationTextComponent("advancements.end.kill_dragon.description", new Object[0]), (ResourceLocation)null, FrameType.TASK, true, true, false).withCriterion("killed_dragon", (ICriterionInstance)KilledTrigger.Instance.playerKilledEntity(EntityPredicate.Builder.create().type(EntityType.ENDER_DRAGON))).register(p_accept_1_, "end/kill_dragon");
      Advancement lvt_4_1_ = Advancement.Builder.builder().withParent(lvt_3_1_).withDisplay(Items.ENDER_PEARL, new TranslationTextComponent("advancements.end.enter_end_gateway.title", new Object[0]), new TranslationTextComponent("advancements.end.enter_end_gateway.description", new Object[0]), (ResourceLocation)null, FrameType.TASK, true, true, false).withCriterion("entered_end_gateway", (ICriterionInstance)EnterBlockTrigger.Instance.forBlock(Blocks.END_GATEWAY)).register(p_accept_1_, "end/enter_end_gateway");
      Advancement lvt_5_1_ = Advancement.Builder.builder().withParent(lvt_3_1_).withDisplay(Items.END_CRYSTAL, new TranslationTextComponent("advancements.end.respawn_dragon.title", new Object[0]), new TranslationTextComponent("advancements.end.respawn_dragon.description", new Object[0]), (ResourceLocation)null, FrameType.GOAL, true, true, false).withCriterion("summoned_dragon", (ICriterionInstance)SummonedEntityTrigger.Instance.summonedEntity(EntityPredicate.Builder.create().type(EntityType.ENDER_DRAGON))).register(p_accept_1_, "end/respawn_dragon");
      Advancement lvt_6_1_ = Advancement.Builder.builder().withParent(lvt_4_1_).withDisplay(Blocks.PURPUR_BLOCK, new TranslationTextComponent("advancements.end.find_end_city.title", new Object[0]), new TranslationTextComponent("advancements.end.find_end_city.description", new Object[0]), (ResourceLocation)null, FrameType.TASK, true, true, false).withCriterion("in_city", (ICriterionInstance)PositionTrigger.Instance.forLocation(LocationPredicate.forFeature(Feature.END_CITY))).register(p_accept_1_, "end/find_end_city");
      Advancement lvt_7_1_ = Advancement.Builder.builder().withParent(lvt_3_1_).withDisplay(Items.DRAGON_BREATH, new TranslationTextComponent("advancements.end.dragon_breath.title", new Object[0]), new TranslationTextComponent("advancements.end.dragon_breath.description", new Object[0]), (ResourceLocation)null, FrameType.GOAL, true, true, false).withCriterion("dragon_breath", (ICriterionInstance)InventoryChangeTrigger.Instance.forItems(Items.DRAGON_BREATH)).register(p_accept_1_, "end/dragon_breath");
      Advancement lvt_8_1_ = Advancement.Builder.builder().withParent(lvt_6_1_).withDisplay(Items.SHULKER_SHELL, new TranslationTextComponent("advancements.end.levitate.title", new Object[0]), new TranslationTextComponent("advancements.end.levitate.description", new Object[0]), (ResourceLocation)null, FrameType.CHALLENGE, true, true, false).withRewards(AdvancementRewards.Builder.experience(50)).withCriterion("levitated", (ICriterionInstance)LevitationTrigger.Instance.forDistance(DistancePredicate.forVertical(MinMaxBounds.FloatBound.atLeast(50.0F)))).register(p_accept_1_, "end/levitate");
      Advancement lvt_9_1_ = Advancement.Builder.builder().withParent(lvt_6_1_).withDisplay(Items.ELYTRA, new TranslationTextComponent("advancements.end.elytra.title", new Object[0]), new TranslationTextComponent("advancements.end.elytra.description", new Object[0]), (ResourceLocation)null, FrameType.GOAL, true, true, false).withCriterion("elytra", (ICriterionInstance)InventoryChangeTrigger.Instance.forItems(Items.ELYTRA)).register(p_accept_1_, "end/elytra");
      Advancement lvt_10_1_ = Advancement.Builder.builder().withParent(lvt_3_1_).withDisplay(Blocks.DRAGON_EGG, new TranslationTextComponent("advancements.end.dragon_egg.title", new Object[0]), new TranslationTextComponent("advancements.end.dragon_egg.description", new Object[0]), (ResourceLocation)null, FrameType.GOAL, true, true, false).withCriterion("dragon_egg", (ICriterionInstance)InventoryChangeTrigger.Instance.forItems(Blocks.DRAGON_EGG)).register(p_accept_1_, "end/dragon_egg");
   }

   // $FF: synthetic method
   public void accept(Object p_accept_1_) {
      this.accept((Consumer)p_accept_1_);
   }
}
