package net.minecraft.potion;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PotionItem;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.IRegistryDelegate;

public class PotionBrewing {
   private static final List<PotionBrewing.MixPredicate<Potion>> POTION_TYPE_CONVERSIONS = Lists.newArrayList();
   private static final List<PotionBrewing.MixPredicate<Item>> POTION_ITEM_CONVERSIONS = Lists.newArrayList();
   private static final List<Ingredient> POTION_ITEMS = Lists.newArrayList();
   private static final Predicate<ItemStack> IS_POTION_ITEM = (p_lambda$static$0_0_) -> {
      Iterator var1 = POTION_ITEMS.iterator();

      Ingredient ingredient;
      do {
         if (!var1.hasNext()) {
            return false;
         }

         ingredient = (Ingredient)var1.next();
      } while(!ingredient.test(p_lambda$static$0_0_));

      return true;
   };

   public static boolean isReagent(ItemStack p_185205_0_) {
      return isItemConversionReagent(p_185205_0_) || isTypeConversionReagent(p_185205_0_);
   }

   protected static boolean isItemConversionReagent(ItemStack p_185203_0_) {
      int i = 0;

      for(int j = POTION_ITEM_CONVERSIONS.size(); i < j; ++i) {
         if (((PotionBrewing.MixPredicate)POTION_ITEM_CONVERSIONS.get(i)).reagent.test(p_185203_0_)) {
            return true;
         }
      }

      return false;
   }

   protected static boolean isTypeConversionReagent(ItemStack p_185211_0_) {
      int i = 0;

      for(int j = POTION_TYPE_CONVERSIONS.size(); i < j; ++i) {
         if (((PotionBrewing.MixPredicate)POTION_TYPE_CONVERSIONS.get(i)).reagent.test(p_185211_0_)) {
            return true;
         }
      }

      return false;
   }

   public static boolean func_222124_a(Potion p_222124_0_) {
      int i = 0;

      for(int j = POTION_TYPE_CONVERSIONS.size(); i < j; ++i) {
         if (((PotionBrewing.MixPredicate)POTION_TYPE_CONVERSIONS.get(i)).output.get() == p_222124_0_) {
            return true;
         }
      }

      return false;
   }

   public static boolean hasConversions(ItemStack p_185208_0_, ItemStack p_185208_1_) {
      if (!IS_POTION_ITEM.test(p_185208_0_)) {
         return false;
      } else {
         return hasItemConversions(p_185208_0_, p_185208_1_) || hasTypeConversions(p_185208_0_, p_185208_1_);
      }
   }

   protected static boolean hasItemConversions(ItemStack p_185206_0_, ItemStack p_185206_1_) {
      Item item = p_185206_0_.getItem();
      int i = 0;

      for(int j = POTION_ITEM_CONVERSIONS.size(); i < j; ++i) {
         PotionBrewing.MixPredicate<Item> mixpredicate = (PotionBrewing.MixPredicate)POTION_ITEM_CONVERSIONS.get(i);
         if (mixpredicate.input.get() == item && mixpredicate.reagent.test(p_185206_1_)) {
            return true;
         }
      }

      return false;
   }

   protected static boolean hasTypeConversions(ItemStack p_185209_0_, ItemStack p_185209_1_) {
      Potion potion = PotionUtils.getPotionFromItem(p_185209_0_);
      int i = 0;

      for(int j = POTION_TYPE_CONVERSIONS.size(); i < j; ++i) {
         PotionBrewing.MixPredicate<Potion> mixpredicate = (PotionBrewing.MixPredicate)POTION_TYPE_CONVERSIONS.get(i);
         if (mixpredicate.input.get() == potion && mixpredicate.reagent.test(p_185209_1_)) {
            return true;
         }
      }

      return false;
   }

   public static ItemStack doReaction(ItemStack p_185212_0_, ItemStack p_185212_1_) {
      if (!p_185212_1_.isEmpty()) {
         Potion potion = PotionUtils.getPotionFromItem(p_185212_1_);
         Item item = p_185212_1_.getItem();
         int i = 0;

         int k;
         PotionBrewing.MixPredicate mixpredicate1;
         for(k = POTION_ITEM_CONVERSIONS.size(); i < k; ++i) {
            mixpredicate1 = (PotionBrewing.MixPredicate)POTION_ITEM_CONVERSIONS.get(i);
            if (mixpredicate1.input.get() == item && mixpredicate1.reagent.test(p_185212_0_)) {
               return PotionUtils.addPotionToItemStack(new ItemStack((IItemProvider)mixpredicate1.output.get()), potion);
            }
         }

         i = 0;

         for(k = POTION_TYPE_CONVERSIONS.size(); i < k; ++i) {
            mixpredicate1 = (PotionBrewing.MixPredicate)POTION_TYPE_CONVERSIONS.get(i);
            if (mixpredicate1.input.get() == potion && mixpredicate1.reagent.test(p_185212_0_)) {
               return PotionUtils.addPotionToItemStack(new ItemStack(item), (Potion)mixpredicate1.output.get());
            }
         }
      }

      return p_185212_1_;
   }

   public static void init() {
      addContainer(Items.POTION);
      addContainer(Items.SPLASH_POTION);
      addContainer(Items.LINGERING_POTION);
      addContainerRecipe(Items.POTION, Items.GUNPOWDER, Items.SPLASH_POTION);
      addContainerRecipe(Items.SPLASH_POTION, Items.DRAGON_BREATH, Items.LINGERING_POTION);
      addMix(Potions.WATER, Items.GLISTERING_MELON_SLICE, Potions.MUNDANE);
      addMix(Potions.WATER, Items.GHAST_TEAR, Potions.MUNDANE);
      addMix(Potions.WATER, Items.RABBIT_FOOT, Potions.MUNDANE);
      addMix(Potions.WATER, Items.BLAZE_POWDER, Potions.MUNDANE);
      addMix(Potions.WATER, Items.SPIDER_EYE, Potions.MUNDANE);
      addMix(Potions.WATER, Items.SUGAR, Potions.MUNDANE);
      addMix(Potions.WATER, Items.MAGMA_CREAM, Potions.MUNDANE);
      addMix(Potions.WATER, Items.GLOWSTONE_DUST, Potions.THICK);
      addMix(Potions.WATER, Items.REDSTONE, Potions.MUNDANE);
      addMix(Potions.WATER, Items.NETHER_WART, Potions.AWKWARD);
      addMix(Potions.AWKWARD, Items.GOLDEN_CARROT, Potions.NIGHT_VISION);
      addMix(Potions.NIGHT_VISION, Items.REDSTONE, Potions.LONG_NIGHT_VISION);
      addMix(Potions.NIGHT_VISION, Items.FERMENTED_SPIDER_EYE, Potions.INVISIBILITY);
      addMix(Potions.LONG_NIGHT_VISION, Items.FERMENTED_SPIDER_EYE, Potions.LONG_INVISIBILITY);
      addMix(Potions.INVISIBILITY, Items.REDSTONE, Potions.LONG_INVISIBILITY);
      addMix(Potions.AWKWARD, Items.MAGMA_CREAM, Potions.FIRE_RESISTANCE);
      addMix(Potions.FIRE_RESISTANCE, Items.REDSTONE, Potions.LONG_FIRE_RESISTANCE);
      addMix(Potions.AWKWARD, Items.RABBIT_FOOT, Potions.LEAPING);
      addMix(Potions.LEAPING, Items.REDSTONE, Potions.LONG_LEAPING);
      addMix(Potions.LEAPING, Items.GLOWSTONE_DUST, Potions.STRONG_LEAPING);
      addMix(Potions.LEAPING, Items.FERMENTED_SPIDER_EYE, Potions.SLOWNESS);
      addMix(Potions.LONG_LEAPING, Items.FERMENTED_SPIDER_EYE, Potions.LONG_SLOWNESS);
      addMix(Potions.SLOWNESS, Items.REDSTONE, Potions.LONG_SLOWNESS);
      addMix(Potions.SLOWNESS, Items.GLOWSTONE_DUST, Potions.STRONG_SLOWNESS);
      addMix(Potions.AWKWARD, Items.TURTLE_HELMET, Potions.TURTLE_MASTER);
      addMix(Potions.TURTLE_MASTER, Items.REDSTONE, Potions.LONG_TURTLE_MASTER);
      addMix(Potions.TURTLE_MASTER, Items.GLOWSTONE_DUST, Potions.STRONG_TURTLE_MASTER);
      addMix(Potions.SWIFTNESS, Items.FERMENTED_SPIDER_EYE, Potions.SLOWNESS);
      addMix(Potions.LONG_SWIFTNESS, Items.FERMENTED_SPIDER_EYE, Potions.LONG_SLOWNESS);
      addMix(Potions.AWKWARD, Items.SUGAR, Potions.SWIFTNESS);
      addMix(Potions.SWIFTNESS, Items.REDSTONE, Potions.LONG_SWIFTNESS);
      addMix(Potions.SWIFTNESS, Items.GLOWSTONE_DUST, Potions.STRONG_SWIFTNESS);
      addMix(Potions.AWKWARD, Items.PUFFERFISH, Potions.WATER_BREATHING);
      addMix(Potions.WATER_BREATHING, Items.REDSTONE, Potions.LONG_WATER_BREATHING);
      addMix(Potions.AWKWARD, Items.GLISTERING_MELON_SLICE, Potions.HEALING);
      addMix(Potions.HEALING, Items.GLOWSTONE_DUST, Potions.STRONG_HEALING);
      addMix(Potions.HEALING, Items.FERMENTED_SPIDER_EYE, Potions.HARMING);
      addMix(Potions.STRONG_HEALING, Items.FERMENTED_SPIDER_EYE, Potions.STRONG_HARMING);
      addMix(Potions.HARMING, Items.GLOWSTONE_DUST, Potions.STRONG_HARMING);
      addMix(Potions.POISON, Items.FERMENTED_SPIDER_EYE, Potions.HARMING);
      addMix(Potions.LONG_POISON, Items.FERMENTED_SPIDER_EYE, Potions.HARMING);
      addMix(Potions.STRONG_POISON, Items.FERMENTED_SPIDER_EYE, Potions.STRONG_HARMING);
      addMix(Potions.AWKWARD, Items.SPIDER_EYE, Potions.POISON);
      addMix(Potions.POISON, Items.REDSTONE, Potions.LONG_POISON);
      addMix(Potions.POISON, Items.GLOWSTONE_DUST, Potions.STRONG_POISON);
      addMix(Potions.AWKWARD, Items.GHAST_TEAR, Potions.REGENERATION);
      addMix(Potions.REGENERATION, Items.REDSTONE, Potions.LONG_REGENERATION);
      addMix(Potions.REGENERATION, Items.GLOWSTONE_DUST, Potions.STRONG_REGENERATION);
      addMix(Potions.AWKWARD, Items.BLAZE_POWDER, Potions.STRENGTH);
      addMix(Potions.STRENGTH, Items.REDSTONE, Potions.LONG_STRENGTH);
      addMix(Potions.STRENGTH, Items.GLOWSTONE_DUST, Potions.STRONG_STRENGTH);
      addMix(Potions.WATER, Items.FERMENTED_SPIDER_EYE, Potions.WEAKNESS);
      addMix(Potions.WEAKNESS, Items.REDSTONE, Potions.LONG_WEAKNESS);
      addMix(Potions.AWKWARD, Items.PHANTOM_MEMBRANE, Potions.SLOW_FALLING);
      addMix(Potions.SLOW_FALLING, Items.REDSTONE, Potions.LONG_SLOW_FALLING);
   }

   private static void addContainerRecipe(Item p_196207_0_, Item p_196207_1_, Item p_196207_2_) {
      if (!(p_196207_0_ instanceof PotionItem)) {
         throw new IllegalArgumentException("Expected a potion, got: " + Registry.ITEM.getKey(p_196207_0_));
      } else if (!(p_196207_2_ instanceof PotionItem)) {
         throw new IllegalArgumentException("Expected a potion, got: " + Registry.ITEM.getKey(p_196207_2_));
      } else {
         POTION_ITEM_CONVERSIONS.add(new PotionBrewing.MixPredicate(p_196207_0_, Ingredient.fromItems(p_196207_1_), p_196207_2_));
      }
   }

   private static void addContainer(Item p_196208_0_) {
      if (!(p_196208_0_ instanceof PotionItem)) {
         throw new IllegalArgumentException("Expected a potion, got: " + Registry.ITEM.getKey(p_196208_0_));
      } else {
         POTION_ITEMS.add(Ingredient.fromItems(p_196208_0_));
      }
   }

   private static void addMix(Potion p_193357_0_, Item p_193357_1_, Potion p_193357_2_) {
      POTION_TYPE_CONVERSIONS.add(new PotionBrewing.MixPredicate(p_193357_0_, Ingredient.fromItems(p_193357_1_), p_193357_2_));
   }

   static class MixPredicate<T extends ForgeRegistryEntry<T>> {
      private final IRegistryDelegate<T> input;
      private final Ingredient reagent;
      private final IRegistryDelegate<T> output;

      public MixPredicate(T p_i230079_1_, Ingredient p_i230079_2_, T p_i230079_3_) {
         this.input = p_i230079_1_.delegate;
         this.reagent = p_i230079_2_;
         this.output = p_i230079_3_.delegate;
      }
   }
}
