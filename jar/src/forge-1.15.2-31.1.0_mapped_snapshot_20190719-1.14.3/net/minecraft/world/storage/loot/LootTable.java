package net.minecraft.world.storage.loot;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.storage.loot.functions.ILootFunction;
import net.minecraft.world.storage.loot.functions.LootFunctionManager;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LootTable {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final LootTable EMPTY_LOOT_TABLE;
   public static final LootParameterSet DEFAULT_PARAMETER_SET;
   private final LootParameterSet parameterSet;
   private final List<LootPool> pools;
   private final ILootFunction[] functions;
   private final BiFunction<ItemStack, LootContext, ItemStack> combinedFunctions;
   private boolean isFrozen;

   private LootTable(LootParameterSet p_i51265_1_, LootPool[] p_i51265_2_, ILootFunction[] p_i51265_3_) {
      this.isFrozen = false;
      this.parameterSet = p_i51265_1_;
      this.pools = Lists.newArrayList(p_i51265_2_);
      this.functions = p_i51265_3_;
      this.combinedFunctions = LootFunctionManager.combine(p_i51265_3_);
   }

   public static Consumer<ItemStack> capStackSizes(Consumer<ItemStack> p_216124_0_) {
      return (p_lambda$capStackSizes$0_1_) -> {
         if (p_lambda$capStackSizes$0_1_.getCount() < p_lambda$capStackSizes$0_1_.getMaxStackSize()) {
            p_216124_0_.accept(p_lambda$capStackSizes$0_1_);
         } else {
            int i = p_lambda$capStackSizes$0_1_.getCount();

            while(i > 0) {
               ItemStack itemstack = p_lambda$capStackSizes$0_1_.copy();
               itemstack.setCount(Math.min(p_lambda$capStackSizes$0_1_.getMaxStackSize(), i));
               i -= itemstack.getCount();
               p_216124_0_.accept(itemstack);
            }
         }

      };
   }

   public void recursiveGenerate(LootContext p_216114_1_, Consumer<ItemStack> p_216114_2_) {
      if (p_216114_1_.addLootTable(this)) {
         Consumer<ItemStack> consumer = ILootFunction.func_215858_a(this.combinedFunctions, p_216114_2_, p_216114_1_);
         Iterator var4 = this.pools.iterator();

         while(var4.hasNext()) {
            LootPool lootpool = (LootPool)var4.next();
            lootpool.generate(consumer, p_216114_1_);
         }

         p_216114_1_.removeLootTable(this);
      } else {
         LOGGER.warn("Detected infinite loop in loot tables");
      }

   }

   public void generate(LootContext p_216120_1_, Consumer<ItemStack> p_216120_2_) {
      this.recursiveGenerate(p_216120_1_, capStackSizes(p_216120_2_));
   }

   public List<ItemStack> generate(LootContext p_216113_1_) {
      List<ItemStack> list = Lists.newArrayList();
      this.generate(p_216113_1_, list::add);
      return list;
   }

   public LootParameterSet getParameterSet() {
      return this.parameterSet;
   }

   public void func_227506_a_(ValidationTracker p_227506_1_) {
      int j;
      for(j = 0; j < this.pools.size(); ++j) {
         ((LootPool)this.pools.get(j)).func_227505_a_(p_227506_1_.func_227534_b_(".pools[" + j + "]"));
      }

      for(j = 0; j < this.functions.length; ++j) {
         this.functions[j].func_225580_a_(p_227506_1_.func_227534_b_(".functions[" + j + "]"));
      }

   }

   public void fillInventory(IInventory p_216118_1_, LootContext p_216118_2_) {
      List<ItemStack> list = this.generate(p_216118_2_);
      Random random = p_216118_2_.getRandom();
      List<Integer> list1 = this.getEmptySlotsRandomized(p_216118_1_, random);
      this.shuffleItems(list, list1.size(), random);
      Iterator var6 = list.iterator();

      while(var6.hasNext()) {
         ItemStack itemstack = (ItemStack)var6.next();
         if (list1.isEmpty()) {
            LOGGER.warn("Tried to over-fill a container");
            return;
         }

         if (itemstack.isEmpty()) {
            p_216118_1_.setInventorySlotContents((Integer)list1.remove(list1.size() - 1), ItemStack.EMPTY);
         } else {
            p_216118_1_.setInventorySlotContents((Integer)list1.remove(list1.size() - 1), itemstack);
         }
      }

   }

   private void shuffleItems(List<ItemStack> p_186463_1_, int p_186463_2_, Random p_186463_3_) {
      List<ItemStack> list = Lists.newArrayList();
      Iterator iterator = p_186463_1_.iterator();

      ItemStack itemstack;
      while(iterator.hasNext()) {
         itemstack = (ItemStack)iterator.next();
         if (itemstack.isEmpty()) {
            iterator.remove();
         } else if (itemstack.getCount() > 1) {
            list.add(itemstack);
            iterator.remove();
         }
      }

      while(p_186463_2_ - p_186463_1_.size() - list.size() > 0 && !list.isEmpty()) {
         itemstack = (ItemStack)list.remove(MathHelper.nextInt(p_186463_3_, 0, list.size() - 1));
         int i = MathHelper.nextInt(p_186463_3_, 1, itemstack.getCount() / 2);
         ItemStack itemstack1 = itemstack.split(i);
         if (itemstack.getCount() > 1 && p_186463_3_.nextBoolean()) {
            list.add(itemstack);
         } else {
            p_186463_1_.add(itemstack);
         }

         if (itemstack1.getCount() > 1 && p_186463_3_.nextBoolean()) {
            list.add(itemstack1);
         } else {
            p_186463_1_.add(itemstack1);
         }
      }

      p_186463_1_.addAll(list);
      Collections.shuffle(p_186463_1_, p_186463_3_);
   }

   private List<Integer> getEmptySlotsRandomized(IInventory p_186459_1_, Random p_186459_2_) {
      List<Integer> list = Lists.newArrayList();

      for(int i = 0; i < p_186459_1_.getSizeInventory(); ++i) {
         if (p_186459_1_.getStackInSlot(i).isEmpty()) {
            list.add(i);
         }
      }

      Collections.shuffle(list, p_186459_2_);
      return list;
   }

   public static LootTable.Builder builder() {
      return new LootTable.Builder();
   }

   public void freeze() {
      this.isFrozen = true;
      this.pools.forEach(LootPool::freeze);
   }

   public boolean isFrozen() {
      return this.isFrozen;
   }

   private void checkFrozen() {
      if (this.isFrozen()) {
         throw new RuntimeException("Attempted to modify LootTable after being finalized!");
      }
   }

   public LootPool getPool(String p_getPool_1_) {
      return (LootPool)this.pools.stream().filter((p_lambda$getPool$1_1_) -> {
         return p_getPool_1_.equals(p_lambda$getPool$1_1_.getName());
      }).findFirst().orElse((Object)null);
   }

   public LootPool removePool(String p_removePool_1_) {
      this.checkFrozen();
      Iterator var2 = this.pools.iterator();

      LootPool pool;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         pool = (LootPool)var2.next();
      } while(!p_removePool_1_.equals(pool.getName()));

      this.pools.remove(pool);
      return pool;
   }

   public void addPool(LootPool p_addPool_1_) {
      this.checkFrozen();
      if (this.pools.stream().anyMatch((p_lambda$addPool$2_1_) -> {
         return p_lambda$addPool$2_1_ == p_addPool_1_ || p_lambda$addPool$2_1_.getName().equals(p_addPool_1_.getName());
      })) {
         throw new RuntimeException("Attempted to add a duplicate pool to loot table: " + p_addPool_1_.getName());
      } else {
         this.pools.add(p_addPool_1_);
      }
   }

   // $FF: synthetic method
   LootTable(LootParameterSet p_i51266_1_, LootPool[] p_i51266_2_, ILootFunction[] p_i51266_3_, Object p_i51266_4_) {
      this(p_i51266_1_, p_i51266_2_, p_i51266_3_);
   }

   static {
      EMPTY_LOOT_TABLE = new LootTable(LootParameterSets.EMPTY, new LootPool[0], new ILootFunction[0]);
      DEFAULT_PARAMETER_SET = LootParameterSets.GENERIC;
   }

   public static class Serializer implements JsonDeserializer<LootTable>, JsonSerializer<LootTable> {
      public LootTable deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
         JsonObject jsonobject = JSONUtils.getJsonObject(p_deserialize_1_, "loot table");
         LootPool[] alootpool = (LootPool[])JSONUtils.deserializeClass(jsonobject, "pools", new LootPool[0], p_deserialize_3_, LootPool[].class);
         LootParameterSet lootparameterset = null;
         if (jsonobject.has("type")) {
            String s = JSONUtils.getString(jsonobject, "type");
            lootparameterset = LootParameterSets.getValue(new ResourceLocation(s));
         }

         ILootFunction[] ailootfunction = (ILootFunction[])JSONUtils.deserializeClass(jsonobject, "functions", new ILootFunction[0], p_deserialize_3_, ILootFunction[].class);
         return new LootTable(lootparameterset != null ? lootparameterset : LootParameterSets.GENERIC, alootpool, ailootfunction);
      }

      public JsonElement serialize(LootTable p_serialize_1_, Type p_serialize_2_, JsonSerializationContext p_serialize_3_) {
         JsonObject jsonobject = new JsonObject();
         if (p_serialize_1_.parameterSet != LootTable.DEFAULT_PARAMETER_SET) {
            ResourceLocation resourcelocation = LootParameterSets.getKey(p_serialize_1_.parameterSet);
            if (resourcelocation != null) {
               jsonobject.addProperty("type", resourcelocation.toString());
            } else {
               LootTable.LOGGER.warn("Failed to find id for param set " + p_serialize_1_.parameterSet);
            }
         }

         if (!p_serialize_1_.pools.isEmpty()) {
            jsonobject.add("pools", p_serialize_3_.serialize(p_serialize_1_.pools));
         }

         if (!ArrayUtils.isEmpty((Object[])p_serialize_1_.functions)) {
            jsonobject.add("functions", p_serialize_3_.serialize(p_serialize_1_.functions));
         }

         return jsonobject;
      }
   }

   public static class Builder implements ILootFunctionConsumer<LootTable.Builder> {
      private final List<LootPool> lootPools = Lists.newArrayList();
      private final List<ILootFunction> lootFunctions = Lists.newArrayList();
      private LootParameterSet parameterSet;

      public Builder() {
         this.parameterSet = LootTable.DEFAULT_PARAMETER_SET;
      }

      public LootTable.Builder addLootPool(LootPool.Builder p_216040_1_) {
         this.lootPools.add(p_216040_1_.build());
         return this;
      }

      public LootTable.Builder setParameterSet(LootParameterSet p_216039_1_) {
         this.parameterSet = p_216039_1_;
         return this;
      }

      public LootTable.Builder acceptFunction(ILootFunction.IBuilder p_212841_1_) {
         this.lootFunctions.add(p_212841_1_.build());
         return this;
      }

      public LootTable.Builder cast() {
         return this;
      }

      public LootTable build() {
         return new LootTable(this.parameterSet, (LootPool[])this.lootPools.toArray(new LootPool[0]), (ILootFunction[])this.lootFunctions.toArray(new ILootFunction[0]));
      }
   }
}
