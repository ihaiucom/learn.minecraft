package net.minecraft.world.gen.feature;

import java.util.Iterator;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.WallTorchBlock;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;

public class EndPodiumFeature extends Feature<NoFeatureConfig> {
   public static final BlockPos END_PODIUM_LOCATION;
   private final boolean activePortal;

   public EndPodiumFeature(boolean p_i46666_1_) {
      super(NoFeatureConfig::deserialize);
      this.activePortal = p_i46666_1_;
   }

   public boolean place(IWorld p_212245_1_, ChunkGenerator<? extends GenerationSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, NoFeatureConfig p_212245_5_) {
      Iterator var6 = BlockPos.getAllInBoxMutable(new BlockPos(p_212245_4_.getX() - 4, p_212245_4_.getY() - 1, p_212245_4_.getZ() - 4), new BlockPos(p_212245_4_.getX() + 4, p_212245_4_.getY() + 32, p_212245_4_.getZ() + 4)).iterator();

      while(true) {
         BlockPos lvt_7_1_;
         boolean lvt_8_1_;
         do {
            if (!var6.hasNext()) {
               for(int lvt_6_1_ = 0; lvt_6_1_ < 4; ++lvt_6_1_) {
                  this.setBlockState(p_212245_1_, p_212245_4_.up(lvt_6_1_), Blocks.BEDROCK.getDefaultState());
               }

               BlockPos lvt_6_2_ = p_212245_4_.up(2);
               Iterator var11 = Direction.Plane.HORIZONTAL.iterator();

               while(var11.hasNext()) {
                  Direction lvt_8_2_ = (Direction)var11.next();
                  this.setBlockState(p_212245_1_, lvt_6_2_.offset(lvt_8_2_), (BlockState)Blocks.WALL_TORCH.getDefaultState().with(WallTorchBlock.HORIZONTAL_FACING, lvt_8_2_));
               }

               return true;
            }

            lvt_7_1_ = (BlockPos)var6.next();
            lvt_8_1_ = lvt_7_1_.withinDistance(p_212245_4_, 2.5D);
         } while(!lvt_8_1_ && !lvt_7_1_.withinDistance(p_212245_4_, 3.5D));

         if (lvt_7_1_.getY() < p_212245_4_.getY()) {
            if (lvt_8_1_) {
               this.setBlockState(p_212245_1_, lvt_7_1_, Blocks.BEDROCK.getDefaultState());
            } else if (lvt_7_1_.getY() < p_212245_4_.getY()) {
               this.setBlockState(p_212245_1_, lvt_7_1_, Blocks.END_STONE.getDefaultState());
            }
         } else if (lvt_7_1_.getY() > p_212245_4_.getY()) {
            this.setBlockState(p_212245_1_, lvt_7_1_, Blocks.AIR.getDefaultState());
         } else if (!lvt_8_1_) {
            this.setBlockState(p_212245_1_, lvt_7_1_, Blocks.BEDROCK.getDefaultState());
         } else if (this.activePortal) {
            this.setBlockState(p_212245_1_, new BlockPos(lvt_7_1_), Blocks.END_PORTAL.getDefaultState());
         } else {
            this.setBlockState(p_212245_1_, new BlockPos(lvt_7_1_), Blocks.AIR.getDefaultState());
         }
      }
   }

   static {
      END_PODIUM_LOCATION = BlockPos.ZERO;
   }
}
