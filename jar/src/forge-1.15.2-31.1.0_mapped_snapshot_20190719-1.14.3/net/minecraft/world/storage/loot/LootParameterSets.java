package net.minecraft.world.storage.loot;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;

public class LootParameterSets {
   private static final BiMap<ResourceLocation, LootParameterSet> REGISTRY = HashBiMap.create();
   public static final LootParameterSet EMPTY = register("empty", (p_lambda$static$0_0_) -> {
   });
   public static final LootParameterSet CHEST = register("chest", (p_lambda$static$1_0_) -> {
      p_lambda$static$1_0_.required(LootParameters.POSITION).optional(LootParameters.THIS_ENTITY);
      p_lambda$static$1_0_.optional(LootParameters.KILLER_ENTITY);
   });
   public static final LootParameterSet field_227557_c_ = register("command", (p_lambda$static$2_0_) -> {
      p_lambda$static$2_0_.required(LootParameters.POSITION).optional(LootParameters.THIS_ENTITY);
   });
   public static final LootParameterSet field_227558_d_ = register("selector", (p_lambda$static$3_0_) -> {
      p_lambda$static$3_0_.required(LootParameters.POSITION).required(LootParameters.THIS_ENTITY);
   });
   public static final LootParameterSet FISHING = register("fishing", (p_lambda$static$4_0_) -> {
      p_lambda$static$4_0_.required(LootParameters.POSITION).required(LootParameters.TOOL);
      p_lambda$static$4_0_.optional(LootParameters.KILLER_ENTITY).optional(LootParameters.THIS_ENTITY);
   });
   public static final LootParameterSet ENTITY = register("entity", (p_lambda$static$5_0_) -> {
      p_lambda$static$5_0_.required(LootParameters.THIS_ENTITY).required(LootParameters.POSITION).required(LootParameters.DAMAGE_SOURCE).optional(LootParameters.KILLER_ENTITY).optional(LootParameters.DIRECT_KILLER_ENTITY).optional(LootParameters.LAST_DAMAGE_PLAYER);
   });
   public static final LootParameterSet GIFT = register("gift", (p_lambda$static$6_0_) -> {
      p_lambda$static$6_0_.required(LootParameters.POSITION).required(LootParameters.THIS_ENTITY);
   });
   public static final LootParameterSet ADVANCEMENT = register("advancement_reward", (p_lambda$static$7_0_) -> {
      p_lambda$static$7_0_.required(LootParameters.THIS_ENTITY).required(LootParameters.POSITION);
   });
   public static final LootParameterSet GENERIC = register("generic", (p_lambda$static$8_0_) -> {
      p_lambda$static$8_0_.required(LootParameters.THIS_ENTITY).required(LootParameters.LAST_DAMAGE_PLAYER).required(LootParameters.DAMAGE_SOURCE).required(LootParameters.KILLER_ENTITY).required(LootParameters.DIRECT_KILLER_ENTITY).required(LootParameters.POSITION).required(LootParameters.BLOCK_STATE).required(LootParameters.BLOCK_ENTITY).required(LootParameters.TOOL).required(LootParameters.EXPLOSION_RADIUS);
   });
   public static final LootParameterSet BLOCK = register("block", (p_lambda$static$9_0_) -> {
      p_lambda$static$9_0_.required(LootParameters.BLOCK_STATE).required(LootParameters.POSITION).required(LootParameters.TOOL).optional(LootParameters.THIS_ENTITY).optional(LootParameters.BLOCK_ENTITY).optional(LootParameters.EXPLOSION_RADIUS);
   });

   private static LootParameterSet register(String p_216253_0_, Consumer<LootParameterSet.Builder> p_216253_1_) {
      LootParameterSet.Builder lootparameterset$builder = new LootParameterSet.Builder();
      p_216253_1_.accept(lootparameterset$builder);
      LootParameterSet lootparameterset = lootparameterset$builder.build();
      ResourceLocation resourcelocation = new ResourceLocation(p_216253_0_);
      LootParameterSet lootparameterset1 = (LootParameterSet)REGISTRY.put(resourcelocation, lootparameterset);
      if (lootparameterset1 != null) {
         throw new IllegalStateException("Loot table parameter set " + resourcelocation + " is already registered");
      } else {
         return lootparameterset;
      }
   }

   @Nullable
   public static LootParameterSet getValue(ResourceLocation p_216256_0_) {
      return (LootParameterSet)REGISTRY.get(p_216256_0_);
   }

   @Nullable
   public static ResourceLocation getKey(LootParameterSet p_216257_0_) {
      return (ResourceLocation)REGISTRY.inverse().get(p_216257_0_);
   }
}
