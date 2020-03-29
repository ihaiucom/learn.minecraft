package net.minecraft.world.storage.loot;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.conditions.ILootCondition;
import net.minecraft.world.storage.loot.conditions.LootConditionManager;
import net.minecraft.world.storage.loot.functions.ILootFunction;
import net.minecraft.world.storage.loot.functions.LootFunctionManager;
import net.minecraftforge.common.ForgeHooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LootTableManager extends JsonReloadListener {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Gson GSON_INSTANCE = (new GsonBuilder()).registerTypeAdapter(RandomValueRange.class, new RandomValueRange.Serializer()).registerTypeAdapter(BinomialRange.class, new BinomialRange.Serializer()).registerTypeAdapter(ConstantRange.class, new ConstantRange.Serializer()).registerTypeAdapter(IntClamper.class, new IntClamper.Serializer()).registerTypeAdapter(LootPool.class, new LootPool.Serializer()).registerTypeAdapter(LootTable.class, new LootTable.Serializer()).registerTypeHierarchyAdapter(LootEntry.class, new LootEntryManager.Serializer()).registerTypeHierarchyAdapter(ILootFunction.class, new LootFunctionManager.Serializer()).registerTypeHierarchyAdapter(ILootCondition.class, new LootConditionManager.Serializer()).registerTypeHierarchyAdapter(LootContext.EntityTarget.class, new LootContext.EntityTarget.Serializer()).create();
   private Map<ResourceLocation, LootTable> registeredLootTables = ImmutableMap.of();
   private final LootPredicateManager field_227507_d_;

   public LootTableManager(LootPredicateManager p_i225887_1_) {
      super(GSON_INSTANCE, "loot_tables");
      this.field_227507_d_ = p_i225887_1_;
   }

   public LootTable getLootTableFromLocation(ResourceLocation p_186521_1_) {
      return (LootTable)this.registeredLootTables.getOrDefault(p_186521_1_, LootTable.EMPTY_LOOT_TABLE);
   }

   protected void apply(Map<ResourceLocation, JsonObject> p_212853_1_, IResourceManager p_212853_2_, IProfiler p_212853_3_) {
      Builder<ResourceLocation, LootTable> builder = ImmutableMap.builder();
      JsonObject jsonobject = (JsonObject)p_212853_1_.remove(LootTables.EMPTY);
      if (jsonobject != null) {
         LOGGER.warn("Datapack tried to redefine {} loot table, ignoring", LootTables.EMPTY);
      }

      p_212853_1_.forEach((p_lambda$apply$0_3_, p_lambda$apply$0_4_) -> {
         try {
            IResource res = p_212853_2_.getResource(this.getPreparedPath(p_lambda$apply$0_3_));
            LootTable loottable = ForgeHooks.loadLootTable(GSON_INSTANCE, p_lambda$apply$0_3_, p_lambda$apply$0_4_, res == null || !res.getPackName().equals("Default"), this);
            builder.put(p_lambda$apply$0_3_, loottable);
         } catch (Exception var7) {
            LOGGER.error("Couldn't parse loot table {}", p_lambda$apply$0_3_, var7);
         }

      });
      builder.put(LootTables.EMPTY, LootTable.EMPTY_LOOT_TABLE);
      ImmutableMap<ResourceLocation, LootTable> immutablemap = builder.build();
      LootParameterSet var10002 = LootParameterSets.GENERIC;
      Function var10003 = this.field_227507_d_::func_227517_a_;
      immutablemap.getClass();
      ValidationTracker validationtracker = new ValidationTracker(var10002, var10003, immutablemap::get);
      immutablemap.forEach((p_lambda$apply$1_1_, p_lambda$apply$1_2_) -> {
         func_227508_a_(validationtracker, p_lambda$apply$1_1_, p_lambda$apply$1_2_);
      });
      validationtracker.func_227527_a_().forEach((p_lambda$apply$2_0_, p_lambda$apply$2_1_) -> {
         LOGGER.warn("Found validation problem in " + p_lambda$apply$2_0_ + ": " + p_lambda$apply$2_1_);
      });
      this.registeredLootTables = immutablemap;
   }

   public static void func_227508_a_(ValidationTracker p_227508_0_, ResourceLocation p_227508_1_, LootTable p_227508_2_) {
      p_227508_2_.func_227506_a_(p_227508_0_.func_227529_a_(p_227508_2_.getParameterSet()).func_227531_a_("{" + p_227508_1_ + "}", p_227508_1_));
   }

   public static JsonElement toJson(LootTable p_215301_0_) {
      return GSON_INSTANCE.toJsonTree(p_215301_0_);
   }

   public Set<ResourceLocation> getLootTableKeys() {
      return this.registeredLootTables.keySet();
   }
}