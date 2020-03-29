package net.minecraft.world.storage.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.function.Consumer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.conditions.ILootCondition;
import net.minecraft.world.storage.loot.functions.ILootFunction;

public class TableLootEntry extends StandaloneLootEntry {
   private final ResourceLocation table;

   private TableLootEntry(ResourceLocation p_i51251_1_, int p_i51251_2_, int p_i51251_3_, ILootCondition[] p_i51251_4_, ILootFunction[] p_i51251_5_) {
      super(p_i51251_2_, p_i51251_3_, p_i51251_4_, p_i51251_5_);
      this.table = p_i51251_1_;
   }

   public void func_216154_a(Consumer<ItemStack> p_216154_1_, LootContext p_216154_2_) {
      LootTable lvt_3_1_ = p_216154_2_.func_227502_a_(this.table);
      lvt_3_1_.recursiveGenerate(p_216154_2_, p_216154_1_);
   }

   public void func_225579_a_(ValidationTracker p_225579_1_) {
      if (p_225579_1_.func_227532_a_(this.table)) {
         p_225579_1_.func_227530_a_("Table " + this.table + " is recursively called");
      } else {
         super.func_225579_a_(p_225579_1_);
         LootTable lvt_2_1_ = p_225579_1_.func_227539_c_(this.table);
         if (lvt_2_1_ == null) {
            p_225579_1_.func_227530_a_("Unknown loot table called " + this.table);
         } else {
            lvt_2_1_.func_227506_a_(p_225579_1_.func_227531_a_("->{" + this.table + "}", this.table));
         }

      }
   }

   public static StandaloneLootEntry.Builder<?> builder(ResourceLocation p_216171_0_) {
      return builder((p_216173_1_, p_216173_2_, p_216173_3_, p_216173_4_) -> {
         return new TableLootEntry(p_216171_0_, p_216173_1_, p_216173_2_, p_216173_3_, p_216173_4_);
      });
   }

   // $FF: synthetic method
   TableLootEntry(ResourceLocation p_i51252_1_, int p_i51252_2_, int p_i51252_3_, ILootCondition[] p_i51252_4_, ILootFunction[] p_i51252_5_, Object p_i51252_6_) {
      this(p_i51252_1_, p_i51252_2_, p_i51252_3_, p_i51252_4_, p_i51252_5_);
   }

   public static class Serializer extends StandaloneLootEntry.Serializer<TableLootEntry> {
      public Serializer() {
         super(new ResourceLocation("loot_table"), TableLootEntry.class);
      }

      public void serialize(JsonObject p_212830_1_, TableLootEntry p_212830_2_, JsonSerializationContext p_212830_3_) {
         super.serialize(p_212830_1_, (StandaloneLootEntry)p_212830_2_, p_212830_3_);
         p_212830_1_.addProperty("name", p_212830_2_.table.toString());
      }

      protected TableLootEntry func_212829_b_(JsonObject p_212829_1_, JsonDeserializationContext p_212829_2_, int p_212829_3_, int p_212829_4_, ILootCondition[] p_212829_5_, ILootFunction[] p_212829_6_) {
         ResourceLocation lvt_7_1_ = new ResourceLocation(JSONUtils.getString(p_212829_1_, "name"));
         return new TableLootEntry(lvt_7_1_, p_212829_3_, p_212829_4_, p_212829_5_, p_212829_6_);
      }

      // $FF: synthetic method
      protected StandaloneLootEntry func_212829_b_(JsonObject p_212829_1_, JsonDeserializationContext p_212829_2_, int p_212829_3_, int p_212829_4_, ILootCondition[] p_212829_5_, ILootFunction[] p_212829_6_) {
         return this.func_212829_b_(p_212829_1_, p_212829_2_, p_212829_3_, p_212829_4_, p_212829_5_, p_212829_6_);
      }
   }
}
