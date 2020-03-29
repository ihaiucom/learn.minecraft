package net.minecraft.data.advancements;

import java.util.function.Consumer;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.IRequirementsStrategy;
import net.minecraft.advancements.criterion.BeeNestDestroyedTrigger;
import net.minecraft.advancements.criterion.BlockPredicate;
import net.minecraft.advancements.criterion.BredAnimalsTrigger;
import net.minecraft.advancements.criterion.ConsumeItemTrigger;
import net.minecraft.advancements.criterion.EnchantmentPredicate;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.advancements.criterion.FilledBucketTrigger;
import net.minecraft.advancements.criterion.FishingRodHookedTrigger;
import net.minecraft.advancements.criterion.ItemDurabilityTrigger;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.advancements.criterion.PlacedBlockTrigger;
import net.minecraft.advancements.criterion.RightClickBlockWithItemTrigger;
import net.minecraft.advancements.criterion.TameAnimalTrigger;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.TranslationTextComponent;

public class HusbandryAdvancements implements Consumer<Consumer<Advancement>> {
   private static final EntityType<?>[] BREEDABLE_ANIMALS;
   private static final Item[] FISH_ITEMS;
   private static final Item[] FISH_BUCKETS;
   private static final Item[] BALANCED_DIET;

   public void accept(Consumer<Advancement> p_accept_1_) {
      Advancement lvt_2_1_ = Advancement.Builder.builder().withDisplay(Blocks.HAY_BLOCK, new TranslationTextComponent("advancements.husbandry.root.title", new Object[0]), new TranslationTextComponent("advancements.husbandry.root.description", new Object[0]), new ResourceLocation("textures/gui/advancements/backgrounds/husbandry.png"), FrameType.TASK, false, false, false).withCriterion("consumed_item", (ICriterionInstance)ConsumeItemTrigger.Instance.any()).register(p_accept_1_, "husbandry/root");
      Advancement lvt_3_1_ = Advancement.Builder.builder().withParent(lvt_2_1_).withDisplay(Items.WHEAT, new TranslationTextComponent("advancements.husbandry.plant_seed.title", new Object[0]), new TranslationTextComponent("advancements.husbandry.plant_seed.description", new Object[0]), (ResourceLocation)null, FrameType.TASK, true, true, false).withRequirementsStrategy(IRequirementsStrategy.OR).withCriterion("wheat", (ICriterionInstance)PlacedBlockTrigger.Instance.placedBlock(Blocks.WHEAT)).withCriterion("pumpkin_stem", (ICriterionInstance)PlacedBlockTrigger.Instance.placedBlock(Blocks.PUMPKIN_STEM)).withCriterion("melon_stem", (ICriterionInstance)PlacedBlockTrigger.Instance.placedBlock(Blocks.MELON_STEM)).withCriterion("beetroots", (ICriterionInstance)PlacedBlockTrigger.Instance.placedBlock(Blocks.BEETROOTS)).withCriterion("nether_wart", (ICriterionInstance)PlacedBlockTrigger.Instance.placedBlock(Blocks.NETHER_WART)).register(p_accept_1_, "husbandry/plant_seed");
      Advancement lvt_4_1_ = Advancement.Builder.builder().withParent(lvt_2_1_).withDisplay(Items.WHEAT, new TranslationTextComponent("advancements.husbandry.breed_an_animal.title", new Object[0]), new TranslationTextComponent("advancements.husbandry.breed_an_animal.description", new Object[0]), (ResourceLocation)null, FrameType.TASK, true, true, false).withRequirementsStrategy(IRequirementsStrategy.OR).withCriterion("bred", (ICriterionInstance)BredAnimalsTrigger.Instance.any()).register(p_accept_1_, "husbandry/breed_an_animal");
      Advancement lvt_5_1_ = this.makeBalancedDiet(Advancement.Builder.builder()).withParent(lvt_3_1_).withDisplay(Items.APPLE, new TranslationTextComponent("advancements.husbandry.balanced_diet.title", new Object[0]), new TranslationTextComponent("advancements.husbandry.balanced_diet.description", new Object[0]), (ResourceLocation)null, FrameType.CHALLENGE, true, true, false).withRewards(AdvancementRewards.Builder.experience(100)).register(p_accept_1_, "husbandry/balanced_diet");
      Advancement lvt_6_1_ = Advancement.Builder.builder().withParent(lvt_3_1_).withDisplay(Items.DIAMOND_HOE, new TranslationTextComponent("advancements.husbandry.break_diamond_hoe.title", new Object[0]), new TranslationTextComponent("advancements.husbandry.break_diamond_hoe.description", new Object[0]), (ResourceLocation)null, FrameType.CHALLENGE, true, true, false).withRewards(AdvancementRewards.Builder.experience(100)).withCriterion("broke_hoe", (ICriterionInstance)ItemDurabilityTrigger.Instance.forItemDamage(ItemPredicate.Builder.create().item(Items.DIAMOND_HOE).build(), MinMaxBounds.IntBound.exactly(0))).register(p_accept_1_, "husbandry/break_diamond_hoe");
      Advancement lvt_7_1_ = Advancement.Builder.builder().withParent(lvt_2_1_).withDisplay(Items.LEAD, new TranslationTextComponent("advancements.husbandry.tame_an_animal.title", new Object[0]), new TranslationTextComponent("advancements.husbandry.tame_an_animal.description", new Object[0]), (ResourceLocation)null, FrameType.TASK, true, true, false).withCriterion("tamed_animal", (ICriterionInstance)TameAnimalTrigger.Instance.any()).register(p_accept_1_, "husbandry/tame_an_animal");
      Advancement lvt_8_1_ = this.makeBredAllAnimals(Advancement.Builder.builder()).withParent(lvt_4_1_).withDisplay(Items.GOLDEN_CARROT, new TranslationTextComponent("advancements.husbandry.breed_all_animals.title", new Object[0]), new TranslationTextComponent("advancements.husbandry.breed_all_animals.description", new Object[0]), (ResourceLocation)null, FrameType.CHALLENGE, true, true, false).withRewards(AdvancementRewards.Builder.experience(100)).register(p_accept_1_, "husbandry/bred_all_animals");
      Advancement lvt_9_1_ = this.makeFish(Advancement.Builder.builder()).withParent(lvt_2_1_).withRequirementsStrategy(IRequirementsStrategy.OR).withDisplay(Items.FISHING_ROD, new TranslationTextComponent("advancements.husbandry.fishy_business.title", new Object[0]), new TranslationTextComponent("advancements.husbandry.fishy_business.description", new Object[0]), (ResourceLocation)null, FrameType.TASK, true, true, false).register(p_accept_1_, "husbandry/fishy_business");
      Advancement lvt_10_1_ = this.makeFishBucket(Advancement.Builder.builder()).withParent(lvt_9_1_).withRequirementsStrategy(IRequirementsStrategy.OR).withDisplay(Items.PUFFERFISH_BUCKET, new TranslationTextComponent("advancements.husbandry.tactical_fishing.title", new Object[0]), new TranslationTextComponent("advancements.husbandry.tactical_fishing.description", new Object[0]), (ResourceLocation)null, FrameType.TASK, true, true, false).register(p_accept_1_, "husbandry/tactical_fishing");
      Advancement lvt_11_1_ = this.func_218460_e(Advancement.Builder.builder()).withParent(lvt_7_1_).withDisplay(Items.COD, new TranslationTextComponent("advancements.husbandry.complete_catalogue.title", new Object[0]), new TranslationTextComponent("advancements.husbandry.complete_catalogue.description", new Object[0]), (ResourceLocation)null, FrameType.CHALLENGE, true, true, false).withRewards(AdvancementRewards.Builder.experience(50)).register(p_accept_1_, "husbandry/complete_catalogue");
      Advancement lvt_12_1_ = Advancement.Builder.builder().withParent(lvt_2_1_).withCriterion("safely_harvest_honey", (ICriterionInstance)RightClickBlockWithItemTrigger.Instance.func_226699_a_(BlockPredicate.Builder.func_226243_a_().func_226244_a_(BlockTags.field_226151_aa_), ItemPredicate.Builder.create().item(Items.GLASS_BOTTLE))).withDisplay(Items.field_226638_pX_, new TranslationTextComponent("advancements.husbandry.safely_harvest_honey.title", new Object[0]), new TranslationTextComponent("advancements.husbandry.safely_harvest_honey.description", new Object[0]), (ResourceLocation)null, FrameType.TASK, true, true, false).register(p_accept_1_, "husbandry/safely_harvest_honey");
      Advancement lvt_13_1_ = Advancement.Builder.builder().withParent(lvt_2_1_).withCriterion("silk_touch_nest", (ICriterionInstance)BeeNestDestroyedTrigger.Instance.func_226229_a_(Blocks.field_226905_ma_, ItemPredicate.Builder.create().enchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, MinMaxBounds.IntBound.atLeast(1))), MinMaxBounds.IntBound.exactly(3))).withDisplay(Blocks.field_226905_ma_, new TranslationTextComponent("advancements.husbandry.silk_touch_nest.title", new Object[0]), new TranslationTextComponent("advancements.husbandry.silk_touch_nest.description", new Object[0]), (ResourceLocation)null, FrameType.TASK, true, true, false).register(p_accept_1_, "husbandry/silk_touch_nest");
   }

   private Advancement.Builder makeBalancedDiet(Advancement.Builder p_204288_1_) {
      Item[] var2 = BALANCED_DIET;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Item lvt_5_1_ = var2[var4];
         p_204288_1_.withCriterion(Registry.ITEM.getKey(lvt_5_1_).getPath(), (ICriterionInstance)ConsumeItemTrigger.Instance.forItem(lvt_5_1_));
      }

      return p_204288_1_;
   }

   private Advancement.Builder makeBredAllAnimals(Advancement.Builder p_204289_1_) {
      EntityType[] var2 = BREEDABLE_ANIMALS;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         EntityType<?> lvt_5_1_ = var2[var4];
         p_204289_1_.withCriterion(EntityType.getKey(lvt_5_1_).toString(), (ICriterionInstance)BredAnimalsTrigger.Instance.forParent(EntityPredicate.Builder.create().type(lvt_5_1_)));
      }

      return p_204289_1_;
   }

   private Advancement.Builder makeFishBucket(Advancement.Builder p_204865_1_) {
      Item[] var2 = FISH_BUCKETS;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Item lvt_5_1_ = var2[var4];
         p_204865_1_.withCriterion(Registry.ITEM.getKey(lvt_5_1_).getPath(), (ICriterionInstance)FilledBucketTrigger.Instance.forItem(ItemPredicate.Builder.create().item(lvt_5_1_).build()));
      }

      return p_204865_1_;
   }

   private Advancement.Builder makeFish(Advancement.Builder p_204864_1_) {
      Item[] var2 = FISH_ITEMS;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Item lvt_5_1_ = var2[var4];
         p_204864_1_.withCriterion(Registry.ITEM.getKey(lvt_5_1_).getPath(), (ICriterionInstance)FishingRodHookedTrigger.Instance.create(ItemPredicate.ANY, EntityPredicate.ANY, ItemPredicate.Builder.create().item(lvt_5_1_).build()));
      }

      return p_204864_1_;
   }

   private Advancement.Builder func_218460_e(Advancement.Builder p_218460_1_) {
      CatEntity.field_213425_bD.forEach((p_218461_1_, p_218461_2_) -> {
         p_218460_1_.withCriterion(p_218461_2_.getPath(), (ICriterionInstance)TameAnimalTrigger.Instance.func_215124_a(EntityPredicate.Builder.create().func_217986_a(p_218461_2_).build()));
      });
      return p_218460_1_;
   }

   // $FF: synthetic method
   public void accept(Object p_accept_1_) {
      this.accept((Consumer)p_accept_1_);
   }

   static {
      BREEDABLE_ANIMALS = new EntityType[]{EntityType.HORSE, EntityType.SHEEP, EntityType.COW, EntityType.MOOSHROOM, EntityType.PIG, EntityType.CHICKEN, EntityType.WOLF, EntityType.OCELOT, EntityType.RABBIT, EntityType.LLAMA, EntityType.TURTLE, EntityType.CAT, EntityType.PANDA, EntityType.FOX, EntityType.field_226289_e_};
      FISH_ITEMS = new Item[]{Items.COD, Items.TROPICAL_FISH, Items.PUFFERFISH, Items.SALMON};
      FISH_BUCKETS = new Item[]{Items.COD_BUCKET, Items.TROPICAL_FISH_BUCKET, Items.PUFFERFISH_BUCKET, Items.SALMON_BUCKET};
      BALANCED_DIET = new Item[]{Items.APPLE, Items.MUSHROOM_STEW, Items.BREAD, Items.PORKCHOP, Items.COOKED_PORKCHOP, Items.GOLDEN_APPLE, Items.ENCHANTED_GOLDEN_APPLE, Items.COD, Items.SALMON, Items.TROPICAL_FISH, Items.PUFFERFISH, Items.COOKED_COD, Items.COOKED_SALMON, Items.COOKIE, Items.MELON_SLICE, Items.BEEF, Items.COOKED_BEEF, Items.CHICKEN, Items.COOKED_CHICKEN, Items.ROTTEN_FLESH, Items.SPIDER_EYE, Items.CARROT, Items.POTATO, Items.BAKED_POTATO, Items.POISONOUS_POTATO, Items.GOLDEN_CARROT, Items.PUMPKIN_PIE, Items.RABBIT, Items.COOKED_RABBIT, Items.RABBIT_STEW, Items.MUTTON, Items.COOKED_MUTTON, Items.CHORUS_FRUIT, Items.BEETROOT, Items.BEETROOT_SOUP, Items.DRIED_KELP, Items.SUSPICIOUS_STEW, Items.SWEET_BERRIES, Items.field_226638_pX_};
   }
}
