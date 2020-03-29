package net.minecraft.world.gen.feature.template;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.command.arguments.BlockStateParser;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

public class JigsawReplacementStructureProcessor extends StructureProcessor {
   public static final JigsawReplacementStructureProcessor INSTANCE = new JigsawReplacementStructureProcessor();

   private JigsawReplacementStructureProcessor() {
   }

   @Nullable
   public Template.BlockInfo process(IWorldReader p_215194_1_, BlockPos p_215194_2_, Template.BlockInfo p_215194_3_, Template.BlockInfo p_215194_4_, PlacementSettings p_215194_5_) {
      Block lvt_6_1_ = p_215194_4_.state.getBlock();
      if (lvt_6_1_ != Blocks.field_226904_lY_) {
         return p_215194_4_;
      } else {
         String lvt_7_1_ = p_215194_4_.nbt.getString("final_state");
         BlockStateParser lvt_8_1_ = new BlockStateParser(new StringReader(lvt_7_1_), false);

         try {
            lvt_8_1_.parse(true);
         } catch (CommandSyntaxException var10) {
            throw new RuntimeException(var10);
         }

         return lvt_8_1_.getState().getBlock() == Blocks.STRUCTURE_VOID ? null : new Template.BlockInfo(p_215194_4_.pos, lvt_8_1_.getState(), (CompoundNBT)null);
      }
   }

   protected IStructureProcessorType getType() {
      return IStructureProcessorType.JIGSAW_REPLACEMENT;
   }

   protected <T> Dynamic<T> serialize0(DynamicOps<T> p_215193_1_) {
      return new Dynamic(p_215193_1_, p_215193_1_.emptyMap());
   }
}
