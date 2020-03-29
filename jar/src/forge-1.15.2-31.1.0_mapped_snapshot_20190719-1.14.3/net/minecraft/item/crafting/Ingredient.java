package net.minecraft.item.crafting;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparators;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.ForgeConfig;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import net.minecraftforge.common.crafting.VanillaIngredientSerializer;

public class Ingredient implements Predicate<ItemStack> {
   private static final Set<Ingredient> INSTANCES = Collections.newSetFromMap(new WeakHashMap());
   private static final Predicate<? super Ingredient.IItemList> IS_EMPTY = (p_lambda$static$2_0_) -> {
      return !p_lambda$static$2_0_.getStacks().stream().allMatch(ItemStack::isEmpty);
   };
   public static final Ingredient EMPTY = new Ingredient(Stream.empty());
   private final Ingredient.IItemList[] acceptedItems;
   private ItemStack[] matchingStacks;
   private IntList matchingStacksPacked;
   private final boolean isSimple;
   private final boolean isVanilla = this.getClass() == Ingredient.class;

   public static void invalidateAll() {
      INSTANCES.stream().filter((p_lambda$invalidateAll$0_0_) -> {
         return p_lambda$invalidateAll$0_0_ != null;
      }).forEach((p_lambda$invalidateAll$1_0_) -> {
         p_lambda$invalidateAll$1_0_.invalidate();
      });
   }

   protected Ingredient(Stream<? extends Ingredient.IItemList> p_i49381_1_) {
      this.acceptedItems = (Ingredient.IItemList[])p_i49381_1_.filter(IS_EMPTY).toArray((p_lambda$new$3_0_) -> {
         return new Ingredient.IItemList[p_lambda$new$3_0_];
      });
      this.isSimple = !Arrays.stream(this.acceptedItems).anyMatch((p_lambda$new$5_0_) -> {
         return p_lambda$new$5_0_.getStacks().stream().anyMatch((p_lambda$null$4_0_) -> {
            return p_lambda$null$4_0_.getItem().isDamageable();
         });
      });
      INSTANCES.add(this);
   }

   public ItemStack[] getMatchingStacks() {
      this.determineMatchingStacks();
      return this.matchingStacks;
   }

   private void determineMatchingStacks() {
      if (this.matchingStacks == null) {
         this.matchingStacks = (ItemStack[])Arrays.stream(this.acceptedItems).flatMap((p_lambda$determineMatchingStacks$6_0_) -> {
            return p_lambda$determineMatchingStacks$6_0_.getStacks().stream();
         }).distinct().toArray((p_lambda$determineMatchingStacks$7_0_) -> {
            return new ItemStack[p_lambda$determineMatchingStacks$7_0_];
         });
      }

   }

   public boolean test(@Nullable ItemStack p_test_1_) {
      if (p_test_1_ == null) {
         return false;
      } else if (this.acceptedItems.length == 0) {
         return p_test_1_.isEmpty();
      } else {
         this.determineMatchingStacks();
         ItemStack[] var2 = this.matchingStacks;
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            ItemStack itemstack = var2[var4];
            if (itemstack.getItem() == p_test_1_.getItem()) {
               return true;
            }
         }

         return false;
      }
   }

   public IntList getValidItemStacksPacked() {
      if (this.matchingStacksPacked == null) {
         this.determineMatchingStacks();
         this.matchingStacksPacked = new IntArrayList(this.matchingStacks.length);
         ItemStack[] var1 = this.matchingStacks;
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            ItemStack itemstack = var1[var3];
            this.matchingStacksPacked.add(RecipeItemHelper.pack(itemstack));
         }

         this.matchingStacksPacked.sort(IntComparators.NATURAL_COMPARATOR);
      }

      return this.matchingStacksPacked;
   }

   public final void write(PacketBuffer p_199564_1_) {
      this.determineMatchingStacks();
      if (!this.isVanilla()) {
         CraftingHelper.write(p_199564_1_, this);
      } else {
         p_199564_1_.writeVarInt(this.matchingStacks.length);

         for(int i = 0; i < this.matchingStacks.length; ++i) {
            p_199564_1_.writeItemStack(this.matchingStacks[i]);
         }

      }
   }

   public JsonElement serialize() {
      if (this.acceptedItems.length == 1) {
         return this.acceptedItems[0].serialize();
      } else {
         JsonArray jsonarray = new JsonArray();
         Ingredient.IItemList[] var2 = this.acceptedItems;
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Ingredient.IItemList ingredient$iitemlist = var2[var4];
            jsonarray.add(ingredient$iitemlist.serialize());
         }

         return jsonarray;
      }
   }

   public boolean hasNoMatchingItems() {
      return this.acceptedItems.length == 0 && (this.matchingStacks == null || this.matchingStacks.length == 0) && (this.matchingStacksPacked == null || this.matchingStacksPacked.isEmpty());
   }

   protected void invalidate() {
      this.matchingStacks = null;
      this.matchingStacksPacked = null;
   }

   public boolean isSimple() {
      return this.isSimple || this == EMPTY;
   }

   public final boolean isVanilla() {
      return this.isVanilla;
   }

   public IIngredientSerializer<? extends Ingredient> getSerializer() {
      if (!this.isVanilla()) {
         throw new IllegalStateException("Modderrs must implement Ingredient.getSerializer in their custom Ingredients: " + this);
      } else {
         return VanillaIngredientSerializer.INSTANCE;
      }
   }

   public static Ingredient fromItemListStream(Stream<? extends Ingredient.IItemList> p_209357_0_) {
      Ingredient ingredient = new Ingredient(p_209357_0_);
      return ingredient.acceptedItems.length == 0 ? EMPTY : ingredient;
   }

   public static Ingredient fromItems(IItemProvider... p_199804_0_) {
      return fromItemListStream(Arrays.stream(p_199804_0_).map((p_lambda$fromItems$8_0_) -> {
         return new Ingredient.SingleItemList(new ItemStack(p_lambda$fromItems$8_0_));
      }));
   }

   public static Ingredient fromStacks(ItemStack... p_193369_0_) {
      return fromItemListStream(Arrays.stream(p_193369_0_).map((p_lambda$fromStacks$9_0_) -> {
         return new Ingredient.SingleItemList(p_lambda$fromStacks$9_0_);
      }));
   }

   public static Ingredient fromTag(Tag<Item> p_199805_0_) {
      return fromItemListStream(Stream.of(new Ingredient.TagList(p_199805_0_)));
   }

   public static Ingredient read(PacketBuffer p_199566_0_) {
      int i = p_199566_0_.readVarInt();
      return i == -1 ? CraftingHelper.getIngredient(p_199566_0_.readResourceLocation(), p_199566_0_) : fromItemListStream(Stream.generate(() -> {
         return new Ingredient.SingleItemList(p_199566_0_.readItemStack());
      }).limit((long)i));
   }

   public static Ingredient deserialize(@Nullable JsonElement p_199802_0_) {
      if (p_199802_0_ != null && !p_199802_0_.isJsonNull()) {
         Ingredient ret = CraftingHelper.getIngredient(p_199802_0_);
         if (ret != null) {
            return ret;
         } else if (p_199802_0_.isJsonObject()) {
            return fromItemListStream(Stream.of(deserializeItemList(p_199802_0_.getAsJsonObject())));
         } else if (p_199802_0_.isJsonArray()) {
            JsonArray jsonarray = p_199802_0_.getAsJsonArray();
            if (jsonarray.size() == 0) {
               throw new JsonSyntaxException("Item array cannot be empty, at least one item must be defined");
            } else {
               return fromItemListStream(StreamSupport.stream(jsonarray.spliterator(), false).map((p_lambda$deserialize$11_0_) -> {
                  return deserializeItemList(JSONUtils.getJsonObject(p_lambda$deserialize$11_0_, "item"));
               }));
            }
         } else {
            throw new JsonSyntaxException("Expected item to be object or array of objects");
         }
      } else {
         throw new JsonSyntaxException("Item cannot be null");
      }
   }

   public static Ingredient.IItemList deserializeItemList(JsonObject p_199803_0_) {
      if (p_199803_0_.has("item") && p_199803_0_.has("tag")) {
         throw new JsonParseException("An ingredient entry is either a tag or an item, not both");
      } else {
         ResourceLocation resourcelocation;
         if (p_199803_0_.has("item")) {
            resourcelocation = new ResourceLocation(JSONUtils.getString(p_199803_0_, "item"));
            Item item = (Item)Registry.ITEM.getValue(resourcelocation).orElseThrow(() -> {
               return new JsonSyntaxException("Unknown item '" + resourcelocation + "'");
            });
            return new Ingredient.SingleItemList(new ItemStack(item));
         } else if (p_199803_0_.has("tag")) {
            resourcelocation = new ResourceLocation(JSONUtils.getString(p_199803_0_, "tag"));
            Tag<Item> tag = ItemTags.getCollection().get(resourcelocation);
            if (tag == null) {
               throw new JsonSyntaxException("Unknown item tag '" + resourcelocation + "'");
            } else {
               return new Ingredient.TagList(tag);
            }
         } else {
            throw new JsonParseException("An ingredient entry needs either a tag or an item");
         }
      }
   }

   public static Ingredient merge(Collection<Ingredient> p_merge_0_) {
      return fromItemListStream(p_merge_0_.stream().flatMap((p_lambda$merge$13_0_) -> {
         return Arrays.stream(p_lambda$merge$13_0_.acceptedItems);
      }));
   }

   public static class TagList implements Ingredient.IItemList {
      private final Tag<Item> tag;

      public TagList(Tag<Item> p_i48193_1_) {
         this.tag = p_i48193_1_;
      }

      public Collection<ItemStack> getStacks() {
         List<ItemStack> list = Lists.newArrayList();
         Iterator var2 = this.tag.getAllElements().iterator();

         while(var2.hasNext()) {
            Item item = (Item)var2.next();
            list.add(new ItemStack(item));
         }

         if (list.size() == 0 && !(Boolean)ForgeConfig.SERVER.treatEmptyTagsAsAir.get()) {
            list.add((new ItemStack(Blocks.BARRIER)).setDisplayName(new StringTextComponent("Empty Tag: " + this.tag.getId().toString())));
         }

         return list;
      }

      public JsonObject serialize() {
         JsonObject jsonobject = new JsonObject();
         jsonobject.addProperty("tag", this.tag.getId().toString());
         return jsonobject;
      }
   }

   public static class SingleItemList implements Ingredient.IItemList {
      private final ItemStack stack;

      public SingleItemList(ItemStack p_i48195_1_) {
         this.stack = p_i48195_1_;
      }

      public Collection<ItemStack> getStacks() {
         return Collections.singleton(this.stack);
      }

      public JsonObject serialize() {
         JsonObject jsonobject = new JsonObject();
         jsonobject.addProperty("item", Registry.ITEM.getKey(this.stack.getItem()).toString());
         return jsonobject;
      }
   }

   public interface IItemList {
      Collection<ItemStack> getStacks();

      JsonObject serialize();
   }
}
