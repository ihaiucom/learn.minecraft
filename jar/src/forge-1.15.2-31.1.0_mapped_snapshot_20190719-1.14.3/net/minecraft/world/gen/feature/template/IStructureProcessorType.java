package net.minecraft.world.gen.feature.template;

import net.minecraft.util.IDynamicDeserializer;
import net.minecraft.util.registry.Registry;

public interface IStructureProcessorType extends IDynamicDeserializer<StructureProcessor> {
   IStructureProcessorType BLOCK_IGNORE = register("block_ignore", BlockIgnoreStructureProcessor::new);
   IStructureProcessorType BLOCK_ROT = register("block_rot", IntegrityProcessor::new);
   IStructureProcessorType GRAVITY = register("gravity", GravityStructureProcessor::new);
   IStructureProcessorType JIGSAW_REPLACEMENT = register("jigsaw_replacement", (p_214919_0_) -> {
      return JigsawReplacementStructureProcessor.INSTANCE;
   });
   IStructureProcessorType RULE = register("rule", RuleStructureProcessor::new);
   IStructureProcessorType NOP = register("nop", (p_214918_0_) -> {
      return NopProcessor.INSTANCE;
   });

   static IStructureProcessorType register(String p_214917_0_, IStructureProcessorType p_214917_1_) {
      return (IStructureProcessorType)Registry.register((Registry)Registry.STRUCTURE_PROCESSOR, (String)p_214917_0_, (Object)p_214917_1_);
   }
}
