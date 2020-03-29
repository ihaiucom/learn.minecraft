package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class BlockItemUseContext extends ItemUseContext {
   private final BlockPos offsetPos;
   protected boolean replaceClicked;

   public BlockItemUseContext(ItemUseContext p_i47813_1_) {
      this(p_i47813_1_.getWorld(), p_i47813_1_.getPlayer(), p_i47813_1_.getHand(), p_i47813_1_.getItem(), p_i47813_1_.rayTraceResult);
   }

   protected BlockItemUseContext(World p_i50056_1_, @Nullable PlayerEntity p_i50056_2_, Hand p_i50056_3_, ItemStack p_i50056_4_, BlockRayTraceResult p_i50056_5_) {
      super(p_i50056_1_, p_i50056_2_, p_i50056_3_, p_i50056_4_, p_i50056_5_);
      this.replaceClicked = true;
      this.offsetPos = p_i50056_5_.getPos().offset(p_i50056_5_.getFace());
      this.replaceClicked = p_i50056_1_.getBlockState(p_i50056_5_.getPos()).isReplaceable(this);
   }

   public static BlockItemUseContext func_221536_a(BlockItemUseContext p_221536_0_, BlockPos p_221536_1_, Direction p_221536_2_) {
      return new BlockItemUseContext(p_221536_0_.getWorld(), p_221536_0_.getPlayer(), p_221536_0_.getHand(), p_221536_0_.getItem(), new BlockRayTraceResult(new Vec3d((double)p_221536_1_.getX() + 0.5D + (double)p_221536_2_.getXOffset() * 0.5D, (double)p_221536_1_.getY() + 0.5D + (double)p_221536_2_.getYOffset() * 0.5D, (double)p_221536_1_.getZ() + 0.5D + (double)p_221536_2_.getZOffset() * 0.5D), p_221536_2_, p_221536_1_, false));
   }

   public BlockPos getPos() {
      return this.replaceClicked ? super.getPos() : this.offsetPos;
   }

   public boolean canPlace() {
      return this.replaceClicked || this.getWorld().getBlockState(this.getPos()).isReplaceable(this);
   }

   public boolean replacingClickedOnBlock() {
      return this.replaceClicked;
   }

   public Direction getNearestLookingDirection() {
      return Direction.getFacingDirections(this.player)[0];
   }

   public Direction[] getNearestLookingDirections() {
      Direction[] lvt_1_1_ = Direction.getFacingDirections(this.player);
      if (this.replaceClicked) {
         return lvt_1_1_;
      } else {
         Direction lvt_2_1_ = this.getFace();

         int lvt_3_1_;
         for(lvt_3_1_ = 0; lvt_3_1_ < lvt_1_1_.length && lvt_1_1_[lvt_3_1_] != lvt_2_1_.getOpposite(); ++lvt_3_1_) {
         }

         if (lvt_3_1_ > 0) {
            System.arraycopy(lvt_1_1_, 0, lvt_1_1_, 1, lvt_3_1_);
            lvt_1_1_[0] = lvt_2_1_.getOpposite();
         }

         return lvt_1_1_;
      }
   }
}
