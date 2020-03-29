package net.minecraft.block;

import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.material.Material;
import net.minecraft.block.pattern.BlockMaterialMatcher;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.pattern.BlockPatternBuilder;
import net.minecraft.block.pattern.BlockStateMatcher;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tileentity.SkullTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.CachedBlockInfo;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;

public class WitherSkeletonSkullBlock extends SkullBlock {
   @Nullable
   private static BlockPattern witherPatternFull;
   @Nullable
   private static BlockPattern witherPatternBase;

   protected WitherSkeletonSkullBlock(Block.Properties p_i48293_1_) {
      super(SkullBlock.Types.WITHER_SKELETON, p_i48293_1_);
   }

   public void onBlockPlacedBy(World p_180633_1_, BlockPos p_180633_2_, BlockState p_180633_3_, @Nullable LivingEntity p_180633_4_, ItemStack p_180633_5_) {
      super.onBlockPlacedBy(p_180633_1_, p_180633_2_, p_180633_3_, p_180633_4_, p_180633_5_);
      TileEntity lvt_6_1_ = p_180633_1_.getTileEntity(p_180633_2_);
      if (lvt_6_1_ instanceof SkullTileEntity) {
         checkWitherSpawn(p_180633_1_, p_180633_2_, (SkullTileEntity)lvt_6_1_);
      }

   }

   public static void checkWitherSpawn(World p_196298_0_, BlockPos p_196298_1_, SkullTileEntity p_196298_2_) {
      if (!p_196298_0_.isRemote) {
         Block lvt_3_1_ = p_196298_2_.getBlockState().getBlock();
         boolean lvt_4_1_ = lvt_3_1_ == Blocks.WITHER_SKELETON_SKULL || lvt_3_1_ == Blocks.WITHER_SKELETON_WALL_SKULL;
         if (lvt_4_1_ && p_196298_1_.getY() >= 2 && p_196298_0_.getDifficulty() != Difficulty.PEACEFUL) {
            BlockPattern lvt_5_1_ = getOrCreateWitherFull();
            BlockPattern.PatternHelper lvt_6_1_ = lvt_5_1_.match(p_196298_0_, p_196298_1_);
            if (lvt_6_1_ != null) {
               for(int lvt_7_1_ = 0; lvt_7_1_ < lvt_5_1_.getPalmLength(); ++lvt_7_1_) {
                  for(int lvt_8_1_ = 0; lvt_8_1_ < lvt_5_1_.getThumbLength(); ++lvt_8_1_) {
                     CachedBlockInfo lvt_9_1_ = lvt_6_1_.translateOffset(lvt_7_1_, lvt_8_1_, 0);
                     p_196298_0_.setBlockState(lvt_9_1_.getPos(), Blocks.AIR.getDefaultState(), 2);
                     p_196298_0_.playEvent(2001, lvt_9_1_.getPos(), Block.getStateId(lvt_9_1_.getBlockState()));
                  }
               }

               WitherEntity lvt_7_2_ = (WitherEntity)EntityType.WITHER.create(p_196298_0_);
               BlockPos lvt_8_2_ = lvt_6_1_.translateOffset(1, 2, 0).getPos();
               lvt_7_2_.setLocationAndAngles((double)lvt_8_2_.getX() + 0.5D, (double)lvt_8_2_.getY() + 0.55D, (double)lvt_8_2_.getZ() + 0.5D, lvt_6_1_.getForwards().getAxis() == Direction.Axis.X ? 0.0F : 90.0F, 0.0F);
               lvt_7_2_.renderYawOffset = lvt_6_1_.getForwards().getAxis() == Direction.Axis.X ? 0.0F : 90.0F;
               lvt_7_2_.ignite();
               Iterator var13 = p_196298_0_.getEntitiesWithinAABB(ServerPlayerEntity.class, lvt_7_2_.getBoundingBox().grow(50.0D)).iterator();

               while(var13.hasNext()) {
                  ServerPlayerEntity lvt_10_1_ = (ServerPlayerEntity)var13.next();
                  CriteriaTriggers.SUMMONED_ENTITY.trigger(lvt_10_1_, lvt_7_2_);
               }

               p_196298_0_.addEntity(lvt_7_2_);

               for(int lvt_9_2_ = 0; lvt_9_2_ < lvt_5_1_.getPalmLength(); ++lvt_9_2_) {
                  for(int lvt_10_2_ = 0; lvt_10_2_ < lvt_5_1_.getThumbLength(); ++lvt_10_2_) {
                     p_196298_0_.notifyNeighbors(lvt_6_1_.translateOffset(lvt_9_2_, lvt_10_2_, 0).getPos(), Blocks.AIR);
                  }
               }

            }
         }
      }
   }

   public static boolean canSpawnMob(World p_196299_0_, BlockPos p_196299_1_, ItemStack p_196299_2_) {
      if (p_196299_2_.getItem() == Items.WITHER_SKELETON_SKULL && p_196299_1_.getY() >= 2 && p_196299_0_.getDifficulty() != Difficulty.PEACEFUL && !p_196299_0_.isRemote) {
         return getOrCreateWitherBase().match(p_196299_0_, p_196299_1_) != null;
      } else {
         return false;
      }
   }

   private static BlockPattern getOrCreateWitherFull() {
      if (witherPatternFull == null) {
         witherPatternFull = BlockPatternBuilder.start().aisle("^^^", "###", "~#~").where('#', CachedBlockInfo.hasState(BlockStateMatcher.forBlock(Blocks.SOUL_SAND))).where('^', CachedBlockInfo.hasState(BlockStateMatcher.forBlock(Blocks.WITHER_SKELETON_SKULL).or(BlockStateMatcher.forBlock(Blocks.WITHER_SKELETON_WALL_SKULL)))).where('~', CachedBlockInfo.hasState(BlockMaterialMatcher.forMaterial(Material.AIR))).build();
      }

      return witherPatternFull;
   }

   private static BlockPattern getOrCreateWitherBase() {
      if (witherPatternBase == null) {
         witherPatternBase = BlockPatternBuilder.start().aisle("   ", "###", "~#~").where('#', CachedBlockInfo.hasState(BlockStateMatcher.forBlock(Blocks.SOUL_SAND))).where('~', CachedBlockInfo.hasState(BlockMaterialMatcher.forMaterial(Material.AIR))).build();
      }

      return witherPatternBase;
   }
}
