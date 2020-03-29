package net.minecraft.world.storage.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.IRandomRange;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootFunction;
import net.minecraft.world.storage.loot.RandomRanges;
import net.minecraft.world.storage.loot.conditions.ILootCondition;

public class SetCount extends LootFunction {
   private final IRandomRange countRange;

   private SetCount(ILootCondition[] p_i51222_1_, IRandomRange p_i51222_2_) {
      super(p_i51222_1_);
      this.countRange = p_i51222_2_;
   }

   public ItemStack doApply(ItemStack p_215859_1_, LootContext p_215859_2_) {
      p_215859_1_.setCount(this.countRange.generateInt(p_215859_2_.getRandom()));
      return p_215859_1_;
   }

   public static LootFunction.Builder<?> func_215932_a(IRandomRange p_215932_0_) {
      return builder((p_215934_1_) -> {
         return new SetCount(p_215934_1_, p_215932_0_);
      });
   }

   // $FF: synthetic method
   SetCount(ILootCondition[] p_i51223_1_, IRandomRange p_i51223_2_, Object p_i51223_3_) {
      this(p_i51223_1_, p_i51223_2_);
   }

   public static class Serializer extends LootFunction.Serializer<SetCount> {
      protected Serializer() {
         super(new ResourceLocation("set_count"), SetCount.class);
      }

      public void serialize(JsonObject p_186532_1_, SetCount p_186532_2_, JsonSerializationContext p_186532_3_) {
         super.serialize(p_186532_1_, (LootFunction)p_186532_2_, p_186532_3_);
         p_186532_1_.add("count", RandomRanges.serialize(p_186532_2_.countRange, p_186532_3_));
      }

      public SetCount deserialize(JsonObject p_186530_1_, JsonDeserializationContext p_186530_2_, ILootCondition[] p_186530_3_) {
         IRandomRange lvt_4_1_ = RandomRanges.deserialize(p_186530_1_.get("count"), p_186530_2_);
         return new SetCount(p_186530_3_, lvt_4_1_);
      }

      // $FF: synthetic method
      public LootFunction deserialize(JsonObject p_186530_1_, JsonDeserializationContext p_186530_2_, ILootCondition[] p_186530_3_) {
         return this.deserialize(p_186530_1_, p_186530_2_, p_186530_3_);
      }
   }
}
