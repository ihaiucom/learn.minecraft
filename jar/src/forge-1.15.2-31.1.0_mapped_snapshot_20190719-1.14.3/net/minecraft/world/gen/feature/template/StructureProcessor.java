package net.minecraft.world.gen.feature.template;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorldReader;

public abstract class StructureProcessor {
   /** @deprecated */
   @Nullable
   @Deprecated
   public Template.BlockInfo process(IWorldReader p_215194_1_, BlockPos p_215194_2_, Template.BlockInfo p_215194_3_, Template.BlockInfo p_215194_4_, PlacementSettings p_215194_5_) {
      return p_215194_4_;
   }

   @Nullable
   public Template.BlockInfo process(IWorldReader p_process_1_, BlockPos p_process_2_, Template.BlockInfo p_process_3_, Template.BlockInfo p_process_4_, PlacementSettings p_process_5_, @Nullable Template p_process_6_) {
      return this.process(p_process_1_, p_process_2_, p_process_3_, p_process_4_, p_process_5_);
   }

   public Template.EntityInfo processEntity(IWorldReader p_processEntity_1_, BlockPos p_processEntity_2_, Template.EntityInfo p_processEntity_3_, Template.EntityInfo p_processEntity_4_, PlacementSettings p_processEntity_5_, Template p_processEntity_6_) {
      return p_processEntity_4_;
   }

   protected abstract IStructureProcessorType getType();

   protected abstract <T> Dynamic<T> serialize0(DynamicOps<T> var1);

   public <T> Dynamic<T> serialize(DynamicOps<T> p_215191_1_) {
      return new Dynamic(p_215191_1_, p_215191_1_.mergeInto(this.serialize0(p_215191_1_).getValue(), p_215191_1_.createString("processor_type"), p_215191_1_.createString(Registry.STRUCTURE_PROCESSOR.getKey(this.getType()).toString())));
   }
}
