package net.minecraftforge.common.crafting;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparators;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;

public class CompoundIngredient extends Ingredient {
   private List<Ingredient> children;
   private ItemStack[] stacks;
   private IntList itemIds;
   private final boolean isSimple;

   protected CompoundIngredient(List<Ingredient> children) {
      super(Stream.of());
      this.children = Collections.unmodifiableList(children);
      this.isSimple = children.stream().allMatch(Ingredient::isSimple);
   }

   @Nonnull
   public ItemStack[] getMatchingStacks() {
      if (this.stacks == null) {
         List<ItemStack> tmp = Lists.newArrayList();
         Iterator var2 = this.children.iterator();

         while(var2.hasNext()) {
            Ingredient child = (Ingredient)var2.next();
            Collections.addAll(tmp, child.getMatchingStacks());
         }

         this.stacks = (ItemStack[])tmp.toArray(new ItemStack[tmp.size()]);
      }

      return this.stacks;
   }

   @Nonnull
   public IntList getValidItemStacksPacked() {
      if (this.itemIds == null) {
         this.itemIds = new IntArrayList();
         Iterator var1 = this.children.iterator();

         while(var1.hasNext()) {
            Ingredient child = (Ingredient)var1.next();
            this.itemIds.addAll(child.getValidItemStacksPacked());
         }

         this.itemIds.sort(IntComparators.NATURAL_COMPARATOR);
      }

      return this.itemIds;
   }

   public boolean test(@Nullable ItemStack target) {
      return target == null ? false : this.children.stream().anyMatch((c) -> {
         return c.test(target);
      });
   }

   protected void invalidate() {
      this.itemIds = null;
      this.stacks = null;
   }

   public boolean isSimple() {
      return this.isSimple;
   }

   public IIngredientSerializer<? extends Ingredient> getSerializer() {
      return CompoundIngredient.Serializer.INSTANCE;
   }

   @Nonnull
   public Collection<Ingredient> getChildren() {
      return this.children;
   }

   public JsonElement serialize() {
      if (this.children.size() == 1) {
         return ((Ingredient)this.children.get(0)).serialize();
      } else {
         JsonArray json = new JsonArray();
         this.children.stream().forEach((e) -> {
            json.add(e.serialize());
         });
         return json;
      }
   }

   public static class Serializer implements IIngredientSerializer<CompoundIngredient> {
      public static final CompoundIngredient.Serializer INSTANCE = new CompoundIngredient.Serializer();

      public CompoundIngredient parse(PacketBuffer buffer) {
         return new CompoundIngredient((List)Stream.generate(() -> {
            return Ingredient.read(buffer);
         }).limit((long)buffer.readVarInt()).collect(Collectors.toList()));
      }

      public CompoundIngredient parse(JsonObject json) {
         throw new JsonSyntaxException("CompoundIngredient should not be directly referenced in json, just use an array of ingredients.");
      }

      public void write(PacketBuffer buffer, CompoundIngredient ingredient) {
         buffer.writeVarInt(ingredient.children.size());
         ingredient.children.forEach((c) -> {
            c.write(buffer);
         });
      }
   }
}
