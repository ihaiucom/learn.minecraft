package net.minecraft.world.storage.loot.functions;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.item.ItemStack;
import net.minecraft.util.INameable;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootFunction;
import net.minecraft.world.storage.loot.LootParameter;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraft.world.storage.loot.conditions.ILootCondition;

public class CopyName extends LootFunction {
   private final CopyName.Source field_215894_a;

   private CopyName(ILootCondition[] p_i51242_1_, CopyName.Source p_i51242_2_) {
      super(p_i51242_1_);
      this.field_215894_a = p_i51242_2_;
   }

   public Set<LootParameter<?>> getRequiredParameters() {
      return ImmutableSet.of(this.field_215894_a.field_216237_f);
   }

   public ItemStack doApply(ItemStack p_215859_1_, LootContext p_215859_2_) {
      Object lvt_3_1_ = p_215859_2_.get(this.field_215894_a.field_216237_f);
      if (lvt_3_1_ instanceof INameable) {
         INameable lvt_4_1_ = (INameable)lvt_3_1_;
         if (lvt_4_1_.hasCustomName()) {
            p_215859_1_.setDisplayName(lvt_4_1_.getDisplayName());
         }
      }

      return p_215859_1_;
   }

   public static LootFunction.Builder<?> func_215893_a(CopyName.Source p_215893_0_) {
      return builder((p_215891_1_) -> {
         return new CopyName(p_215891_1_, p_215893_0_);
      });
   }

   // $FF: synthetic method
   CopyName(ILootCondition[] p_i51243_1_, CopyName.Source p_i51243_2_, Object p_i51243_3_) {
      this(p_i51243_1_, p_i51243_2_);
   }

   public static class Serializer extends LootFunction.Serializer<CopyName> {
      public Serializer() {
         super(new ResourceLocation("copy_name"), CopyName.class);
      }

      public void serialize(JsonObject p_186532_1_, CopyName p_186532_2_, JsonSerializationContext p_186532_3_) {
         super.serialize(p_186532_1_, (LootFunction)p_186532_2_, p_186532_3_);
         p_186532_1_.addProperty("source", p_186532_2_.field_215894_a.field_216236_e);
      }

      public CopyName deserialize(JsonObject p_186530_1_, JsonDeserializationContext p_186530_2_, ILootCondition[] p_186530_3_) {
         CopyName.Source lvt_4_1_ = CopyName.Source.func_216235_a(JSONUtils.getString(p_186530_1_, "source"));
         return new CopyName(p_186530_3_, lvt_4_1_);
      }

      // $FF: synthetic method
      public LootFunction deserialize(JsonObject p_186530_1_, JsonDeserializationContext p_186530_2_, ILootCondition[] p_186530_3_) {
         return this.deserialize(p_186530_1_, p_186530_2_, p_186530_3_);
      }
   }

   public static enum Source {
      THIS("this", LootParameters.THIS_ENTITY),
      KILLER("killer", LootParameters.KILLER_ENTITY),
      KILLER_PLAYER("killer_player", LootParameters.LAST_DAMAGE_PLAYER),
      BLOCK_ENTITY("block_entity", LootParameters.BLOCK_ENTITY);

      public final String field_216236_e;
      public final LootParameter<?> field_216237_f;

      private Source(String p_i50801_3_, LootParameter<?> p_i50801_4_) {
         this.field_216236_e = p_i50801_3_;
         this.field_216237_f = p_i50801_4_;
      }

      public static CopyName.Source func_216235_a(String p_216235_0_) {
         CopyName.Source[] var1 = values();
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            CopyName.Source lvt_4_1_ = var1[var3];
            if (lvt_4_1_.field_216236_e.equals(p_216235_0_)) {
               return lvt_4_1_;
            }
         }

         throw new IllegalArgumentException("Invalid name source " + p_216235_0_);
      }
   }
}
