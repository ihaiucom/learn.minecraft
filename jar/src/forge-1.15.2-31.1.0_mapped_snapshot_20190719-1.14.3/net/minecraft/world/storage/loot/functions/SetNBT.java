package net.minecraft.world.storage.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootFunction;
import net.minecraft.world.storage.loot.conditions.ILootCondition;

public class SetNBT extends LootFunction {
   private final CompoundNBT tag;

   private SetNBT(ILootCondition[] p_i46620_1_, CompoundNBT p_i46620_2_) {
      super(p_i46620_1_);
      this.tag = p_i46620_2_;
   }

   public ItemStack doApply(ItemStack p_215859_1_, LootContext p_215859_2_) {
      p_215859_1_.getOrCreateTag().merge(this.tag);
      return p_215859_1_;
   }

   public static LootFunction.Builder<?> func_215952_a(CompoundNBT p_215952_0_) {
      return builder((p_215951_1_) -> {
         return new SetNBT(p_215951_1_, p_215952_0_);
      });
   }

   // $FF: synthetic method
   SetNBT(ILootCondition[] p_i51217_1_, CompoundNBT p_i51217_2_, Object p_i51217_3_) {
      this(p_i51217_1_, p_i51217_2_);
   }

   public static class Serializer extends LootFunction.Serializer<SetNBT> {
      public Serializer() {
         super(new ResourceLocation("set_nbt"), SetNBT.class);
      }

      public void serialize(JsonObject p_186532_1_, SetNBT p_186532_2_, JsonSerializationContext p_186532_3_) {
         super.serialize(p_186532_1_, (LootFunction)p_186532_2_, p_186532_3_);
         p_186532_1_.addProperty("tag", p_186532_2_.tag.toString());
      }

      public SetNBT deserialize(JsonObject p_186530_1_, JsonDeserializationContext p_186530_2_, ILootCondition[] p_186530_3_) {
         try {
            CompoundNBT lvt_4_1_ = JsonToNBT.getTagFromJson(JSONUtils.getString(p_186530_1_, "tag"));
            return new SetNBT(p_186530_3_, lvt_4_1_);
         } catch (CommandSyntaxException var5) {
            throw new JsonSyntaxException(var5.getMessage());
         }
      }

      // $FF: synthetic method
      public LootFunction deserialize(JsonObject p_186530_1_, JsonDeserializationContext p_186530_2_, ILootCondition[] p_186530_3_) {
         return this.deserialize(p_186530_1_, p_186530_2_, p_186530_3_);
      }
   }
}
