package net.minecraft.item.crafting;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.DyeItem;
import net.minecraft.item.FireworkRocketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.world.World;

public class FireworkStarRecipe extends SpecialRecipe {
   private static final Ingredient INGREDIENT_SHAPE;
   private static final Ingredient INGREDIENT_FLICKER;
   private static final Ingredient INGREDIENT_TRAIL;
   private static final Map<Item, FireworkRocketItem.Shape> ITEM_SHAPE_MAP;
   private static final Ingredient INGREDIENT_GUNPOWDER;

   public FireworkStarRecipe(ResourceLocation p_i48166_1_) {
      super(p_i48166_1_);
   }

   public boolean matches(CraftingInventory p_77569_1_, World p_77569_2_) {
      boolean flag = false;
      boolean flag1 = false;
      boolean flag2 = false;
      boolean flag3 = false;
      boolean flag4 = false;

      for(int i = 0; i < p_77569_1_.getSizeInventory(); ++i) {
         ItemStack itemstack = p_77569_1_.getStackInSlot(i);
         if (!itemstack.isEmpty()) {
            if (INGREDIENT_SHAPE.test(itemstack)) {
               if (flag2) {
                  return false;
               }

               flag2 = true;
            } else if (INGREDIENT_TRAIL.test(itemstack)) {
               if (flag4) {
                  return false;
               }

               flag4 = true;
            } else if (INGREDIENT_FLICKER.test(itemstack)) {
               if (flag3) {
                  return false;
               }

               flag3 = true;
            } else if (INGREDIENT_GUNPOWDER.test(itemstack)) {
               if (flag) {
                  return false;
               }

               flag = true;
            } else {
               if (!(itemstack.getItem() instanceof DyeItem)) {
                  return false;
               }

               flag1 = true;
            }
         }
      }

      return flag && flag1;
   }

   public ItemStack getCraftingResult(CraftingInventory p_77572_1_) {
      ItemStack itemstack = new ItemStack(Items.FIREWORK_STAR);
      CompoundNBT compoundnbt = itemstack.getOrCreateChildTag("Explosion");
      FireworkRocketItem.Shape fireworkrocketitem$shape = FireworkRocketItem.Shape.SMALL_BALL;
      List<Integer> list = Lists.newArrayList();

      for(int i = 0; i < p_77572_1_.getSizeInventory(); ++i) {
         ItemStack itemstack1 = p_77572_1_.getStackInSlot(i);
         if (!itemstack1.isEmpty()) {
            if (INGREDIENT_SHAPE.test(itemstack1)) {
               fireworkrocketitem$shape = (FireworkRocketItem.Shape)ITEM_SHAPE_MAP.get(itemstack1.getItem());
            } else if (INGREDIENT_TRAIL.test(itemstack1)) {
               compoundnbt.putBoolean("Flicker", true);
            } else if (INGREDIENT_FLICKER.test(itemstack1)) {
               compoundnbt.putBoolean("Trail", true);
            } else if (itemstack1.getItem() instanceof DyeItem) {
               list.add(((DyeItem)itemstack1.getItem()).getDyeColor().getFireworkColor());
            }
         }
      }

      compoundnbt.putIntArray("Colors", (List)list);
      compoundnbt.putByte("Type", (byte)fireworkrocketitem$shape.func_196071_a());
      return itemstack;
   }

   public boolean canFit(int p_194133_1_, int p_194133_2_) {
      return p_194133_1_ * p_194133_2_ >= 2;
   }

   public ItemStack getRecipeOutput() {
      return new ItemStack(Items.FIREWORK_STAR);
   }

   public IRecipeSerializer<?> getSerializer() {
      return IRecipeSerializer.CRAFTING_SPECIAL_FIREWORK_STAR;
   }

   static {
      INGREDIENT_SHAPE = Ingredient.fromItems(Items.FIRE_CHARGE, Items.FEATHER, Items.GOLD_NUGGET, Items.SKELETON_SKULL, Items.WITHER_SKELETON_SKULL, Items.CREEPER_HEAD, Items.PLAYER_HEAD, Items.DRAGON_HEAD, Items.ZOMBIE_HEAD);
      INGREDIENT_FLICKER = Ingredient.fromItems(Items.DIAMOND);
      INGREDIENT_TRAIL = Ingredient.fromItems(Items.GLOWSTONE_DUST);
      ITEM_SHAPE_MAP = (Map)Util.make(Maps.newHashMap(), (p_lambda$static$0_0_) -> {
         p_lambda$static$0_0_.put(Items.FIRE_CHARGE, FireworkRocketItem.Shape.LARGE_BALL);
         p_lambda$static$0_0_.put(Items.FEATHER, FireworkRocketItem.Shape.BURST);
         p_lambda$static$0_0_.put(Items.GOLD_NUGGET, FireworkRocketItem.Shape.STAR);
         p_lambda$static$0_0_.put(Items.SKELETON_SKULL, FireworkRocketItem.Shape.CREEPER);
         p_lambda$static$0_0_.put(Items.WITHER_SKELETON_SKULL, FireworkRocketItem.Shape.CREEPER);
         p_lambda$static$0_0_.put(Items.CREEPER_HEAD, FireworkRocketItem.Shape.CREEPER);
         p_lambda$static$0_0_.put(Items.PLAYER_HEAD, FireworkRocketItem.Shape.CREEPER);
         p_lambda$static$0_0_.put(Items.DRAGON_HEAD, FireworkRocketItem.Shape.CREEPER);
         p_lambda$static$0_0_.put(Items.ZOMBIE_HEAD, FireworkRocketItem.Shape.CREEPER);
      });
      INGREDIENT_GUNPOWDER = Ingredient.fromItems(Items.GUNPOWDER);
   }
}
