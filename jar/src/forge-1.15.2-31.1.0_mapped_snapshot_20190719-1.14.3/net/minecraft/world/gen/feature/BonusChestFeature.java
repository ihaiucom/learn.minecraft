package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.storage.loot.LootTables;

public class BonusChestFeature extends Feature<NoFeatureConfig> {
   public BonusChestFeature(Function<Dynamic<?>, ? extends NoFeatureConfig> p_i49911_1_) {
      super(p_i49911_1_);
   }

   public boolean place(IWorld p_212245_1_, ChunkGenerator<? extends GenerationSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, NoFeatureConfig p_212245_5_) {
      ChunkPos lvt_6_1_ = new ChunkPos(p_212245_4_);
      List<Integer> lvt_7_1_ = (List)IntStream.rangeClosed(lvt_6_1_.getXStart(), lvt_6_1_.getXEnd()).boxed().collect(Collectors.toList());
      Collections.shuffle(lvt_7_1_, p_212245_3_);
      List<Integer> lvt_8_1_ = (List)IntStream.rangeClosed(lvt_6_1_.getZStart(), lvt_6_1_.getZEnd()).boxed().collect(Collectors.toList());
      Collections.shuffle(lvt_8_1_, p_212245_3_);
      BlockPos.Mutable lvt_9_1_ = new BlockPos.Mutable();
      Iterator var10 = lvt_7_1_.iterator();

      while(var10.hasNext()) {
         Integer lvt_11_1_ = (Integer)var10.next();
         Iterator var12 = lvt_8_1_.iterator();

         while(var12.hasNext()) {
            Integer lvt_13_1_ = (Integer)var12.next();
            lvt_9_1_.setPos(lvt_11_1_, 0, lvt_13_1_);
            BlockPos lvt_14_1_ = p_212245_1_.getHeight(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, lvt_9_1_);
            if (p_212245_1_.isAirBlock(lvt_14_1_) || p_212245_1_.getBlockState(lvt_14_1_).getCollisionShape(p_212245_1_, lvt_14_1_).isEmpty()) {
               p_212245_1_.setBlockState(lvt_14_1_, Blocks.CHEST.getDefaultState(), 2);
               LockableLootTileEntity.setLootTable(p_212245_1_, p_212245_3_, lvt_14_1_, LootTables.CHESTS_SPAWN_BONUS_CHEST);
               BlockState lvt_15_1_ = Blocks.TORCH.getDefaultState();
               Iterator var16 = Direction.Plane.HORIZONTAL.iterator();

               while(var16.hasNext()) {
                  Direction lvt_17_1_ = (Direction)var16.next();
                  BlockPos lvt_18_1_ = lvt_14_1_.offset(lvt_17_1_);
                  if (lvt_15_1_.isValidPosition(p_212245_1_, lvt_18_1_)) {
                     p_212245_1_.setBlockState(lvt_18_1_, lvt_15_1_, 2);
                  }
               }

               return true;
            }
         }
      }

      return false;
   }
}
