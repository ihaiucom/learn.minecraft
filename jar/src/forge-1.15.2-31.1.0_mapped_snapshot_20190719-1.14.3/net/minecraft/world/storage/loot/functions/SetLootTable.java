package net.minecraft.world.storage.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootFunction;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.ValidationTracker;
import net.minecraft.world.storage.loot.conditions.ILootCondition;

public class SetLootTable extends LootFunction {
   private final ResourceLocation field_215928_a;
   private final long field_215929_c;

   private SetLootTable(ILootCondition[] p_i51224_1_, ResourceLocation p_i51224_2_, long p_i51224_3_) {
      super(p_i51224_1_);
      this.field_215928_a = p_i51224_2_;
      this.field_215929_c = p_i51224_3_;
   }

   public ItemStack doApply(ItemStack p_215859_1_, LootContext p_215859_2_) {
      if (p_215859_1_.isEmpty()) {
         return p_215859_1_;
      } else {
         CompoundNBT lvt_3_1_ = new CompoundNBT();
         lvt_3_1_.putString("LootTable", this.field_215928_a.toString());
         if (this.field_215929_c != 0L) {
            lvt_3_1_.putLong("LootTableSeed", this.field_215929_c);
         }

         p_215859_1_.getOrCreateTag().put("BlockEntityTag", lvt_3_1_);
         return p_215859_1_;
      }
   }

   public void func_225580_a_(ValidationTracker p_225580_1_) {
      if (p_225580_1_.func_227532_a_(this.field_215928_a)) {
         p_225580_1_.func_227530_a_("Table " + this.field_215928_a + " is recursively called");
      } else {
         super.func_225580_a_(p_225580_1_);
         LootTable lvt_2_1_ = p_225580_1_.func_227539_c_(this.field_215928_a);
         if (lvt_2_1_ == null) {
            p_225580_1_.func_227530_a_("Unknown loot table called " + this.field_215928_a);
         } else {
            lvt_2_1_.func_227506_a_(p_225580_1_.func_227531_a_("->{" + this.field_215928_a + "}", this.field_215928_a));
         }

      }
   }

   // $FF: synthetic method
   SetLootTable(ILootCondition[] p_i51225_1_, ResourceLocation p_i51225_2_, long p_i51225_3_, Object p_i51225_5_) {
      this(p_i51225_1_, p_i51225_2_, p_i51225_3_);
   }

   public static class Serializer extends LootFunction.Serializer<SetLootTable> {
      protected Serializer() {
         super(new ResourceLocation("set_loot_table"), SetLootTable.class);
      }

      public void serialize(JsonObject p_186532_1_, SetLootTable p_186532_2_, JsonSerializationContext p_186532_3_) {
         super.serialize(p_186532_1_, (LootFunction)p_186532_2_, p_186532_3_);
         p_186532_1_.addProperty("name", p_186532_2_.field_215928_a.toString());
         if (p_186532_2_.field_215929_c != 0L) {
            p_186532_1_.addProperty("seed", p_186532_2_.field_215929_c);
         }

      }

      public SetLootTable deserialize(JsonObject p_186530_1_, JsonDeserializationContext p_186530_2_, ILootCondition[] p_186530_3_) {
         ResourceLocation lvt_4_1_ = new ResourceLocation(JSONUtils.getString(p_186530_1_, "name"));
         long lvt_5_1_ = JSONUtils.func_219796_a(p_186530_1_, "seed", 0L);
         return new SetLootTable(p_186530_3_, lvt_4_1_, lvt_5_1_);
      }

      // $FF: synthetic method
      public LootFunction deserialize(JsonObject p_186530_1_, JsonDeserializationContext p_186530_2_, ILootCondition[] p_186530_3_) {
         return this.deserialize(p_186530_1_, p_186530_2_, p_186530_3_);
      }
   }
}
