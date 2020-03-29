package net.minecraft.block;

import java.util.Iterator;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.material.Material;
import net.minecraft.block.pattern.BlockMaterialMatcher;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.pattern.BlockPatternBuilder;
import net.minecraft.block.pattern.BlockStateMatcher;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.CachedBlockInfo;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class CarvedPumpkinBlock extends HorizontalBlock {
   public static final DirectionProperty FACING;
   @Nullable
   private BlockPattern field_196361_b;
   @Nullable
   private BlockPattern field_196362_c;
   @Nullable
   private BlockPattern field_196363_y;
   @Nullable
   private BlockPattern field_196364_z;
   private static final Predicate<BlockState> IS_PUMPKIN;

   protected CarvedPumpkinBlock(Block.Properties p_i48432_1_) {
      super(p_i48432_1_);
      this.setDefaultState((BlockState)((BlockState)this.stateContainer.getBaseState()).with(FACING, Direction.NORTH));
   }

   public void onBlockAdded(BlockState p_220082_1_, World p_220082_2_, BlockPos p_220082_3_, BlockState p_220082_4_, boolean p_220082_5_) {
      if (p_220082_4_.getBlock() != p_220082_1_.getBlock()) {
         this.trySpawnGolem(p_220082_2_, p_220082_3_);
      }
   }

   public boolean canDispenserPlace(IWorldReader p_196354_1_, BlockPos p_196354_2_) {
      return this.getSnowmanBasePattern().match(p_196354_1_, p_196354_2_) != null || this.getGolemBasePattern().match(p_196354_1_, p_196354_2_) != null;
   }

   private void trySpawnGolem(World p_196358_1_, BlockPos p_196358_2_) {
      BlockPattern.PatternHelper lvt_3_1_ = this.getSnowmanPattern().match(p_196358_1_, p_196358_2_);
      int lvt_4_3_;
      Iterator var6;
      ServerPlayerEntity lvt_7_3_;
      int lvt_6_3_;
      if (lvt_3_1_ != null) {
         for(lvt_4_3_ = 0; lvt_4_3_ < this.getSnowmanPattern().getThumbLength(); ++lvt_4_3_) {
            CachedBlockInfo lvt_5_1_ = lvt_3_1_.translateOffset(0, lvt_4_3_, 0);
            p_196358_1_.setBlockState(lvt_5_1_.getPos(), Blocks.AIR.getDefaultState(), 2);
            p_196358_1_.playEvent(2001, lvt_5_1_.getPos(), Block.getStateId(lvt_5_1_.getBlockState()));
         }

         SnowGolemEntity lvt_4_2_ = (SnowGolemEntity)EntityType.SNOW_GOLEM.create(p_196358_1_);
         BlockPos lvt_5_2_ = lvt_3_1_.translateOffset(0, 2, 0).getPos();
         lvt_4_2_.setLocationAndAngles((double)lvt_5_2_.getX() + 0.5D, (double)lvt_5_2_.getY() + 0.05D, (double)lvt_5_2_.getZ() + 0.5D, 0.0F, 0.0F);
         p_196358_1_.addEntity(lvt_4_2_);
         var6 = p_196358_1_.getEntitiesWithinAABB(ServerPlayerEntity.class, lvt_4_2_.getBoundingBox().grow(5.0D)).iterator();

         while(var6.hasNext()) {
            lvt_7_3_ = (ServerPlayerEntity)var6.next();
            CriteriaTriggers.SUMMONED_ENTITY.trigger(lvt_7_3_, lvt_4_2_);
         }

         for(lvt_6_3_ = 0; lvt_6_3_ < this.getSnowmanPattern().getThumbLength(); ++lvt_6_3_) {
            CachedBlockInfo lvt_7_2_ = lvt_3_1_.translateOffset(0, lvt_6_3_, 0);
            p_196358_1_.notifyNeighbors(lvt_7_2_.getPos(), Blocks.AIR);
         }
      } else {
         lvt_3_1_ = this.getGolemPattern().match(p_196358_1_, p_196358_2_);
         if (lvt_3_1_ != null) {
            for(lvt_4_3_ = 0; lvt_4_3_ < this.getGolemPattern().getPalmLength(); ++lvt_4_3_) {
               for(int lvt_5_3_ = 0; lvt_5_3_ < this.getGolemPattern().getThumbLength(); ++lvt_5_3_) {
                  CachedBlockInfo lvt_6_2_ = lvt_3_1_.translateOffset(lvt_4_3_, lvt_5_3_, 0);
                  p_196358_1_.setBlockState(lvt_6_2_.getPos(), Blocks.AIR.getDefaultState(), 2);
                  p_196358_1_.playEvent(2001, lvt_6_2_.getPos(), Block.getStateId(lvt_6_2_.getBlockState()));
               }
            }

            BlockPos lvt_4_4_ = lvt_3_1_.translateOffset(1, 2, 0).getPos();
            IronGolemEntity lvt_5_4_ = (IronGolemEntity)EntityType.IRON_GOLEM.create(p_196358_1_);
            lvt_5_4_.setPlayerCreated(true);
            lvt_5_4_.setLocationAndAngles((double)lvt_4_4_.getX() + 0.5D, (double)lvt_4_4_.getY() + 0.05D, (double)lvt_4_4_.getZ() + 0.5D, 0.0F, 0.0F);
            p_196358_1_.addEntity(lvt_5_4_);
            var6 = p_196358_1_.getEntitiesWithinAABB(ServerPlayerEntity.class, lvt_5_4_.getBoundingBox().grow(5.0D)).iterator();

            while(var6.hasNext()) {
               lvt_7_3_ = (ServerPlayerEntity)var6.next();
               CriteriaTriggers.SUMMONED_ENTITY.trigger(lvt_7_3_, lvt_5_4_);
            }

            for(lvt_6_3_ = 0; lvt_6_3_ < this.getGolemPattern().getPalmLength(); ++lvt_6_3_) {
               for(int lvt_7_4_ = 0; lvt_7_4_ < this.getGolemPattern().getThumbLength(); ++lvt_7_4_) {
                  CachedBlockInfo lvt_8_1_ = lvt_3_1_.translateOffset(lvt_6_3_, lvt_7_4_, 0);
                  p_196358_1_.notifyNeighbors(lvt_8_1_.getPos(), Blocks.AIR);
               }
            }
         }
      }

   }

   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      return (BlockState)this.getDefaultState().with(FACING, p_196258_1_.getPlacementHorizontalFacing().getOpposite());
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(FACING);
   }

   private BlockPattern getSnowmanBasePattern() {
      if (this.field_196361_b == null) {
         this.field_196361_b = BlockPatternBuilder.start().aisle(" ", "#", "#").where('#', CachedBlockInfo.hasState(BlockStateMatcher.forBlock(Blocks.SNOW_BLOCK))).build();
      }

      return this.field_196361_b;
   }

   private BlockPattern getSnowmanPattern() {
      if (this.field_196362_c == null) {
         this.field_196362_c = BlockPatternBuilder.start().aisle("^", "#", "#").where('^', CachedBlockInfo.hasState(IS_PUMPKIN)).where('#', CachedBlockInfo.hasState(BlockStateMatcher.forBlock(Blocks.SNOW_BLOCK))).build();
      }

      return this.field_196362_c;
   }

   private BlockPattern getGolemBasePattern() {
      if (this.field_196363_y == null) {
         this.field_196363_y = BlockPatternBuilder.start().aisle("~ ~", "###", "~#~").where('#', CachedBlockInfo.hasState(BlockStateMatcher.forBlock(Blocks.IRON_BLOCK))).where('~', CachedBlockInfo.hasState(BlockMaterialMatcher.forMaterial(Material.AIR))).build();
      }

      return this.field_196363_y;
   }

   private BlockPattern getGolemPattern() {
      if (this.field_196364_z == null) {
         this.field_196364_z = BlockPatternBuilder.start().aisle("~^~", "###", "~#~").where('^', CachedBlockInfo.hasState(IS_PUMPKIN)).where('#', CachedBlockInfo.hasState(BlockStateMatcher.forBlock(Blocks.IRON_BLOCK))).where('~', CachedBlockInfo.hasState(BlockMaterialMatcher.forMaterial(Material.AIR))).build();
      }

      return this.field_196364_z;
   }

   public boolean canEntitySpawn(BlockState p_220067_1_, IBlockReader p_220067_2_, BlockPos p_220067_3_, EntityType<?> p_220067_4_) {
      return true;
   }

   static {
      FACING = HorizontalBlock.HORIZONTAL_FACING;
      IS_PUMPKIN = (p_210301_0_) -> {
         return p_210301_0_ != null && (p_210301_0_.getBlock() == Blocks.CARVED_PUMPKIN || p_210301_0_.getBlock() == Blocks.JACK_O_LANTERN);
      };
   }
}
