package net.minecraft.world.gen.feature.template;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.UnmodifiableIterator;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorldReader;

public class RuleStructureProcessor extends StructureProcessor {
   private final ImmutableList<RuleEntry> rules;

   public RuleStructureProcessor(List<RuleEntry> p_i51320_1_) {
      this.rules = ImmutableList.copyOf(p_i51320_1_);
   }

   public RuleStructureProcessor(Dynamic<?> p_i51321_1_) {
      this(p_i51321_1_.get("rules").asList(RuleEntry::deserialize));
   }

   @Nullable
   public Template.BlockInfo process(IWorldReader p_215194_1_, BlockPos p_215194_2_, Template.BlockInfo p_215194_3_, Template.BlockInfo p_215194_4_, PlacementSettings p_215194_5_) {
      Random lvt_6_1_ = new Random(MathHelper.getPositionRandom(p_215194_4_.pos));
      BlockState lvt_7_1_ = p_215194_1_.getBlockState(p_215194_4_.pos);
      UnmodifiableIterator var8 = this.rules.iterator();

      RuleEntry lvt_9_1_;
      do {
         if (!var8.hasNext()) {
            return p_215194_4_;
         }

         lvt_9_1_ = (RuleEntry)var8.next();
      } while(!lvt_9_1_.test(p_215194_4_.state, lvt_7_1_, lvt_6_1_));

      return new Template.BlockInfo(p_215194_4_.pos, lvt_9_1_.getOutputState(), lvt_9_1_.getOutputNbt());
   }

   protected IStructureProcessorType getType() {
      return IStructureProcessorType.RULE;
   }

   protected <T> Dynamic<T> serialize0(DynamicOps<T> p_215193_1_) {
      return new Dynamic(p_215193_1_, p_215193_1_.createMap(ImmutableMap.of(p_215193_1_.createString("rules"), p_215193_1_.createList(this.rules.stream().map((p_215200_1_) -> {
         return p_215200_1_.serialize(p_215193_1_).getValue();
      })))));
   }
}
