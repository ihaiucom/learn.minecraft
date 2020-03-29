package net.minecraftforge.common.data;

import com.google.gson.JsonObject;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.block.Blocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.data.ShapelessRecipeBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.Tag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;

public class ForgeRecipeProvider extends RecipeProvider {
   private Map<Item, Tag<Item>> replacements = new HashMap();
   private Set<ResourceLocation> excludes = new HashSet();

   public ForgeRecipeProvider(DataGenerator generatorIn) {
      super(generatorIn);
   }

   private void exclude(IItemProvider item) {
      this.excludes.add(item.asItem().getRegistryName());
   }

   private void replace(IItemProvider item, Tag<Item> tag) {
      this.replacements.put(item.asItem(), tag);
   }

   protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {
      this.replace(Items.STICK, Tags.Items.RODS_WOODEN);
      this.replace(Items.GOLD_INGOT, Tags.Items.INGOTS_GOLD);
      this.replace(Items.IRON_INGOT, Tags.Items.INGOTS_IRON);
      this.replace(Items.DIAMOND, Tags.Items.GEMS_DIAMOND);
      this.replace(Items.EMERALD, Tags.Items.GEMS_EMERALD);
      this.replace(Items.CHEST, Tags.Items.CHESTS_WOODEN);
      this.replace(Blocks.COBBLESTONE, Tags.Items.COBBLESTONE);
      this.exclude(Blocks.GOLD_BLOCK);
      this.exclude(Items.GOLD_NUGGET);
      this.exclude(Blocks.IRON_BLOCK);
      this.exclude(Items.IRON_NUGGET);
      this.exclude(Blocks.DIAMOND_BLOCK);
      this.exclude(Blocks.EMERALD_BLOCK);
      this.exclude(Blocks.COBBLESTONE_STAIRS);
      this.exclude(Blocks.COBBLESTONE_SLAB);
      this.exclude(Blocks.COBBLESTONE_WALL);
      super.registerRecipes((vanilla) -> {
         IFinishedRecipe modified = this.enhance(vanilla);
         if (modified != null) {
            consumer.accept(modified);
         }

      });
   }

   protected void saveRecipeAdvancement(DirectoryCache cache, JsonObject advancementJson, Path pathIn) {
   }

   private IFinishedRecipe enhance(IFinishedRecipe vanilla) {
      if (vanilla instanceof ShapelessRecipeBuilder.Result) {
         return this.enhance((ShapelessRecipeBuilder.Result)vanilla);
      } else {
         return vanilla instanceof ShapedRecipeBuilder.Result ? this.enhance((ShapedRecipeBuilder.Result)vanilla) : null;
      }
   }

   private IFinishedRecipe enhance(ShapelessRecipeBuilder.Result vanilla) {
      List<Ingredient> ingredients = (List)this.getField(ShapelessRecipeBuilder.Result.class, vanilla, 4);
      boolean modified = false;

      for(int x = 0; x < ingredients.size(); ++x) {
         Ingredient ing = this.enhance(vanilla.getID(), (Ingredient)ingredients.get(x));
         if (ing != null) {
            ingredients.set(x, ing);
            modified = true;
         }
      }

      return modified ? vanilla : null;
   }

   private IFinishedRecipe enhance(ShapedRecipeBuilder.Result vanilla) {
      Map<Character, Ingredient> ingredients = (Map)this.getField(ShapedRecipeBuilder.Result.class, vanilla, 5);
      boolean modified = false;
      Iterator var4 = ingredients.keySet().iterator();

      while(var4.hasNext()) {
         Character x = (Character)var4.next();
         Ingredient ing = this.enhance(vanilla.getID(), (Ingredient)ingredients.get(x));
         if (ing != null) {
            ingredients.put(x, ing);
            modified = true;
         }
      }

      return modified ? vanilla : null;
   }

   private Ingredient enhance(ResourceLocation name, Ingredient vanilla) {
      if (this.excludes.contains(name)) {
         return null;
      } else {
         boolean modified = false;
         List<Ingredient.IItemList> items = new ArrayList();
         Ingredient.IItemList[] vanillaItems = (Ingredient.IItemList[])this.getField(Ingredient.class, vanilla, 3);
         Ingredient.IItemList[] var6 = vanillaItems;
         int var7 = vanillaItems.length;

         for(int var8 = 0; var8 < var7; ++var8) {
            Ingredient.IItemList entry = var6[var8];
            if (entry instanceof Ingredient.SingleItemList) {
               ItemStack stack = (ItemStack)entry.getStacks().stream().findFirst().orElse(ItemStack.EMPTY);
               Tag<Item> replacement = (Tag)this.replacements.get(stack.getItem());
               if (replacement != null) {
                  items.add(new Ingredient.TagList(replacement));
                  modified = true;
               } else {
                  items.add(entry);
               }
            } else {
               items.add(entry);
            }
         }

         return modified ? Ingredient.fromItemListStream(items.stream()) : null;
      }
   }

   private <T, R> R getField(Class<T> clz, T inst, int index) {
      Field fld = clz.getDeclaredFields()[index];
      fld.setAccessible(true);

      try {
         return fld.get(inst);
      } catch (IllegalAccessException | IllegalArgumentException var6) {
         throw new RuntimeException(var6);
      }
   }
}
