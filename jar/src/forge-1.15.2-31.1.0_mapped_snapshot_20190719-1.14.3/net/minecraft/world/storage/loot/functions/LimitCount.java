package net.minecraft.world.storage.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.IntClamper;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootFunction;
import net.minecraft.world.storage.loot.conditions.ILootCondition;

public class LimitCount extends LootFunction {
   private final IntClamper field_215914_a;

   private LimitCount(ILootCondition[] p_i51232_1_, IntClamper p_i51232_2_) {
      super(p_i51232_1_);
      this.field_215914_a = p_i51232_2_;
   }

   public ItemStack doApply(ItemStack p_215859_1_, LootContext p_215859_2_) {
      int lvt_3_1_ = this.field_215914_a.applyAsInt(p_215859_1_.getCount());
      p_215859_1_.setCount(lvt_3_1_);
      return p_215859_1_;
   }

   public static LootFunction.Builder<?> func_215911_a(IntClamper p_215911_0_) {
      return builder((p_215912_1_) -> {
         return new LimitCount(p_215912_1_, p_215911_0_);
      });
   }

   // $FF: synthetic method
   LimitCount(ILootCondition[] p_i51233_1_, IntClamper p_i51233_2_, Object p_i51233_3_) {
      this(p_i51233_1_, p_i51233_2_);
   }

   public static class Serializer extends LootFunction.Serializer<LimitCount> {
      protected Serializer() {
         super(new ResourceLocation("limit_count"), LimitCount.class);
      }

      public void serialize(JsonObject p_186532_1_, LimitCount p_186532_2_, JsonSerializationContext p_186532_3_) {
         super.serialize(p_186532_1_, (LootFunction)p_186532_2_, p_186532_3_);
         p_186532_1_.add("limit", p_186532_3_.serialize(p_186532_2_.field_215914_a));
      }

      public LimitCount deserialize(JsonObject p_186530_1_, JsonDeserializationContext p_186530_2_, ILootCondition[] p_186530_3_) {
         IntClamper lvt_4_1_ = (IntClamper)JSONUtils.deserializeClass(p_186530_1_, "limit", p_186530_2_, IntClamper.class);
         return new LimitCount(p_186530_3_, lvt_4_1_);
      }

      // $FF: synthetic method
      public LootFunction deserialize(JsonObject p_186530_1_, JsonDeserializationContext p_186530_2_, ILootCondition[] p_186530_3_) {
         return this.deserialize(p_186530_1_, p_186530_2_, p_186530_3_);
      }
   }
}
