package net.minecraft.entity.ai.goal;

import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FurnaceBlock;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.state.properties.BedPart;
import net.minecraft.tags.BlockTags;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

public class CatSitOnBlockGoal extends MoveToBlockGoal {
   private final CatEntity field_220728_g;

   public CatSitOnBlockGoal(CatEntity p_i50330_1_, double p_i50330_2_) {
      super(p_i50330_1_, p_i50330_2_, 8);
      this.field_220728_g = p_i50330_1_;
   }

   public boolean shouldExecute() {
      return this.field_220728_g.isTamed() && !this.field_220728_g.isSitting() && super.shouldExecute();
   }

   public void startExecuting() {
      super.startExecuting();
      this.field_220728_g.getAISit().setSitting(false);
   }

   public void resetTask() {
      super.resetTask();
      this.field_220728_g.setSitting(false);
   }

   public void tick() {
      super.tick();
      this.field_220728_g.getAISit().setSitting(false);
      if (!this.getIsAboveDestination()) {
         this.field_220728_g.setSitting(false);
      } else if (!this.field_220728_g.isSitting()) {
         this.field_220728_g.setSitting(true);
      }

   }

   protected boolean shouldMoveTo(IWorldReader p_179488_1_, BlockPos p_179488_2_) {
      if (!p_179488_1_.isAirBlock(p_179488_2_.up())) {
         return false;
      } else {
         BlockState lvt_3_1_ = p_179488_1_.getBlockState(p_179488_2_);
         Block lvt_4_1_ = lvt_3_1_.getBlock();
         if (lvt_4_1_ == Blocks.CHEST) {
            return ChestTileEntity.getPlayersUsing(p_179488_1_, p_179488_2_) < 1;
         } else if (lvt_4_1_ == Blocks.FURNACE && (Boolean)lvt_3_1_.get(FurnaceBlock.LIT)) {
            return true;
         } else {
            return lvt_4_1_.isIn(BlockTags.BEDS) && lvt_3_1_.get(BedBlock.PART) != BedPart.HEAD;
         }
      }
   }
}
