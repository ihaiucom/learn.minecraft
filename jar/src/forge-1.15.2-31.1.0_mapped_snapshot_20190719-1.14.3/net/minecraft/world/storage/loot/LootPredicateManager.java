package net.minecraft.world.storage.loot;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.conditions.ILootCondition;
import net.minecraft.world.storage.loot.conditions.LootConditionManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LootPredicateManager extends JsonReloadListener {
   private static final Logger field_227510_a_ = LogManager.getLogger();
   private static final Gson field_227511_b_ = (new GsonBuilder()).registerTypeAdapter(RandomValueRange.class, new RandomValueRange.Serializer()).registerTypeAdapter(BinomialRange.class, new BinomialRange.Serializer()).registerTypeAdapter(ConstantRange.class, new ConstantRange.Serializer()).registerTypeHierarchyAdapter(ILootCondition.class, new LootConditionManager.Serializer()).registerTypeHierarchyAdapter(LootContext.EntityTarget.class, new LootContext.EntityTarget.Serializer()).create();
   private Map<ResourceLocation, ILootCondition> field_227512_c_ = ImmutableMap.of();

   public LootPredicateManager() {
      super(field_227511_b_, "predicates");
   }

   @Nullable
   public ILootCondition func_227517_a_(ResourceLocation p_227517_1_) {
      return (ILootCondition)this.field_227512_c_.get(p_227517_1_);
   }

   protected void apply(Map<ResourceLocation, JsonObject> p_212853_1_, IResourceManager p_212853_2_, IProfiler p_212853_3_) {
      Builder<ResourceLocation, ILootCondition> lvt_4_1_ = ImmutableMap.builder();
      p_212853_1_.forEach((p_227514_1_, p_227514_2_) -> {
         try {
            ILootCondition lvt_3_1_ = (ILootCondition)field_227511_b_.fromJson(p_227514_2_, ILootCondition.class);
            lvt_4_1_.put(p_227514_1_, lvt_3_1_);
         } catch (Exception var4) {
            field_227510_a_.error("Couldn't parse loot table {}", p_227514_1_, var4);
         }

      });
      Map<ResourceLocation, ILootCondition> lvt_5_1_ = lvt_4_1_.build();
      ValidationTracker lvt_6_1_ = new ValidationTracker(LootParameterSets.GENERIC, lvt_5_1_::get, (p_227518_0_) -> {
         return null;
      });
      lvt_5_1_.forEach((p_227515_1_, p_227515_2_) -> {
         p_227515_2_.func_225580_a_(lvt_6_1_.func_227535_b_("{" + p_227515_1_ + "}", p_227515_1_));
      });
      lvt_6_1_.func_227527_a_().forEach((p_227516_0_, p_227516_1_) -> {
         field_227510_a_.warn("Found validation problem in " + p_227516_0_ + ": " + p_227516_1_);
      });
      this.field_227512_c_ = lvt_5_1_;
   }

   public Set<ResourceLocation> func_227513_a_() {
      return Collections.unmodifiableSet(this.field_227512_c_.keySet());
   }
}
