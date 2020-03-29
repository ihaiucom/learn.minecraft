package net.minecraft.world.storage.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import java.util.Random;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootFunction;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraft.world.storage.loot.conditions.ILootCondition;

public class ExplosionDecay extends LootFunction {
   private ExplosionDecay(ILootCondition[] p_i51244_1_) {
      super(p_i51244_1_);
   }

   public ItemStack doApply(ItemStack p_215859_1_, LootContext p_215859_2_) {
      Float lvt_3_1_ = (Float)p_215859_2_.get(LootParameters.EXPLOSION_RADIUS);
      if (lvt_3_1_ != null) {
         Random lvt_4_1_ = p_215859_2_.getRandom();
         float lvt_5_1_ = 1.0F / lvt_3_1_;
         int lvt_6_1_ = p_215859_1_.getCount();
         int lvt_7_1_ = 0;

         for(int lvt_8_1_ = 0; lvt_8_1_ < lvt_6_1_; ++lvt_8_1_) {
            if (lvt_4_1_.nextFloat() <= lvt_5_1_) {
               ++lvt_7_1_;
            }
         }

         p_215859_1_.setCount(lvt_7_1_);
      }

      return p_215859_1_;
   }

   public static LootFunction.Builder<?> func_215863_b() {
      return builder(ExplosionDecay::new);
   }

   // $FF: synthetic method
   ExplosionDecay(ILootCondition[] p_i51245_1_, Object p_i51245_2_) {
      this(p_i51245_1_);
   }

   public static class Serializer extends LootFunction.Serializer<ExplosionDecay> {
      protected Serializer() {
         super(new ResourceLocation("explosion_decay"), ExplosionDecay.class);
      }

      public ExplosionDecay deserialize(JsonObject p_186530_1_, JsonDeserializationContext p_186530_2_, ILootCondition[] p_186530_3_) {
         return new ExplosionDecay(p_186530_3_);
      }

      // $FF: synthetic method
      public LootFunction deserialize(JsonObject p_186530_1_, JsonDeserializationContext p_186530_2_, ILootCondition[] p_186530_3_) {
         return this.deserialize(p_186530_1_, p_186530_2_, p_186530_3_);
      }
   }
}
