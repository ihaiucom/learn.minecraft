package net.minecraft.world.storage.loot;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.storage.loot.conditions.ILootCondition;
import net.minecraft.world.storage.loot.functions.ILootFunction;
import net.minecraft.world.storage.loot.functions.LootFunctionManager;
import org.apache.commons.lang3.ArrayUtils;

public abstract class StandaloneLootEntry extends LootEntry {
   protected final int weight;
   protected final int quality;
   protected final ILootFunction[] functions;
   private final BiFunction<ItemStack, LootContext, ItemStack> field_216157_c;
   private final ILootGenerator field_216161_h = new StandaloneLootEntry.Generator() {
      public void func_216188_a(Consumer<ItemStack> p_216188_1_, LootContext p_216188_2_) {
         StandaloneLootEntry.this.func_216154_a(ILootFunction.func_215858_a(StandaloneLootEntry.this.field_216157_c, p_216188_1_, p_216188_2_), p_216188_2_);
      }
   };

   protected StandaloneLootEntry(int p_i51253_1_, int p_i51253_2_, ILootCondition[] p_i51253_3_, ILootFunction[] p_i51253_4_) {
      super(p_i51253_3_);
      this.weight = p_i51253_1_;
      this.quality = p_i51253_2_;
      this.functions = p_i51253_4_;
      this.field_216157_c = LootFunctionManager.combine(p_i51253_4_);
   }

   public void func_225579_a_(ValidationTracker p_225579_1_) {
      super.func_225579_a_(p_225579_1_);

      for(int lvt_2_1_ = 0; lvt_2_1_ < this.functions.length; ++lvt_2_1_) {
         this.functions[lvt_2_1_].func_225580_a_(p_225579_1_.func_227534_b_(".functions[" + lvt_2_1_ + "]"));
      }

   }

   protected abstract void func_216154_a(Consumer<ItemStack> var1, LootContext var2);

   public boolean expand(LootContext p_expand_1_, Consumer<ILootGenerator> p_expand_2_) {
      if (this.func_216141_a(p_expand_1_)) {
         p_expand_2_.accept(this.field_216161_h);
         return true;
      } else {
         return false;
      }
   }

   public static StandaloneLootEntry.Builder<?> builder(StandaloneLootEntry.ILootEntryBuilder p_216156_0_) {
      return new StandaloneLootEntry.BuilderImpl(p_216156_0_);
   }

   public abstract static class Serializer<T extends StandaloneLootEntry> extends LootEntry.Serializer<T> {
      public Serializer(ResourceLocation p_i50483_1_, Class<T> p_i50483_2_) {
         super(p_i50483_1_, p_i50483_2_);
      }

      public void serialize(JsonObject p_212830_1_, T p_212830_2_, JsonSerializationContext p_212830_3_) {
         if (p_212830_2_.weight != 1) {
            p_212830_1_.addProperty("weight", p_212830_2_.weight);
         }

         if (p_212830_2_.quality != 0) {
            p_212830_1_.addProperty("quality", p_212830_2_.quality);
         }

         if (!ArrayUtils.isEmpty(p_212830_2_.functions)) {
            p_212830_1_.add("functions", p_212830_3_.serialize(p_212830_2_.functions));
         }

      }

      public final T deserialize(JsonObject p_212865_1_, JsonDeserializationContext p_212865_2_, ILootCondition[] p_212865_3_) {
         int lvt_4_1_ = JSONUtils.getInt(p_212865_1_, "weight", 1);
         int lvt_5_1_ = JSONUtils.getInt(p_212865_1_, "quality", 0);
         ILootFunction[] lvt_6_1_ = (ILootFunction[])JSONUtils.deserializeClass(p_212865_1_, "functions", new ILootFunction[0], p_212865_2_, ILootFunction[].class);
         return this.func_212829_b_(p_212865_1_, p_212865_2_, lvt_4_1_, lvt_5_1_, p_212865_3_, lvt_6_1_);
      }

      protected abstract T func_212829_b_(JsonObject var1, JsonDeserializationContext var2, int var3, int var4, ILootCondition[] var5, ILootFunction[] var6);

      // $FF: synthetic method
      public LootEntry deserialize(JsonObject p_212865_1_, JsonDeserializationContext p_212865_2_, ILootCondition[] p_212865_3_) {
         return this.deserialize(p_212865_1_, p_212865_2_, p_212865_3_);
      }
   }

   static class BuilderImpl extends StandaloneLootEntry.Builder<StandaloneLootEntry.BuilderImpl> {
      private final StandaloneLootEntry.ILootEntryBuilder field_216090_c;

      public BuilderImpl(StandaloneLootEntry.ILootEntryBuilder p_i50485_1_) {
         this.field_216090_c = p_i50485_1_;
      }

      protected StandaloneLootEntry.BuilderImpl func_212845_d_() {
         return this;
      }

      public LootEntry func_216081_b() {
         return this.field_216090_c.build(this.weight, this.quality, this.func_216079_f(), this.getFunctions());
      }

      // $FF: synthetic method
      protected LootEntry.Builder func_212845_d_() {
         return this.func_212845_d_();
      }
   }

   @FunctionalInterface
   public interface ILootEntryBuilder {
      StandaloneLootEntry build(int var1, int var2, ILootCondition[] var3, ILootFunction[] var4);
   }

   public abstract static class Builder<T extends StandaloneLootEntry.Builder<T>> extends LootEntry.Builder<T> implements ILootFunctionConsumer<T> {
      protected int weight = 1;
      protected int quality = 0;
      private final List<ILootFunction> functions = Lists.newArrayList();

      public T acceptFunction(ILootFunction.IBuilder p_212841_1_) {
         this.functions.add(p_212841_1_.build());
         return (StandaloneLootEntry.Builder)this.func_212845_d_();
      }

      protected ILootFunction[] getFunctions() {
         return (ILootFunction[])this.functions.toArray(new ILootFunction[0]);
      }

      public T weight(int p_216086_1_) {
         this.weight = p_216086_1_;
         return (StandaloneLootEntry.Builder)this.func_212845_d_();
      }

      public T quality(int p_216085_1_) {
         this.quality = p_216085_1_;
         return (StandaloneLootEntry.Builder)this.func_212845_d_();
      }

      // $FF: synthetic method
      public Object acceptFunction(ILootFunction.IBuilder p_212841_1_) {
         return this.acceptFunction(p_212841_1_);
      }
   }

   public abstract class Generator implements ILootGenerator {
      protected Generator() {
      }

      public int getEffectiveWeight(float p_186361_1_) {
         return Math.max(MathHelper.floor((float)StandaloneLootEntry.this.weight + (float)StandaloneLootEntry.this.quality * p_186361_1_), 0);
      }
   }
}
