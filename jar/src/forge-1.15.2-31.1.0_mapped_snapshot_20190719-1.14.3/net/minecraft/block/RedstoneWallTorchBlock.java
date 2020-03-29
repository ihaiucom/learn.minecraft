package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class RedstoneWallTorchBlock extends RedstoneTorchBlock {
   public static final DirectionProperty FACING;
   public static final BooleanProperty REDSTONE_TORCH_LIT;

   protected RedstoneWallTorchBlock(Block.Properties p_i48341_1_) {
      super(p_i48341_1_);
      this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateContainer.getBaseState()).with(FACING, Direction.NORTH)).with(REDSTONE_TORCH_LIT, true));
   }

   public String getTranslationKey() {
      return this.asItem().getTranslationKey();
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      return WallTorchBlock.func_220289_j(p_220053_1_);
   }

   public boolean isValidPosition(BlockState p_196260_1_, IWorldReader p_196260_2_, BlockPos p_196260_3_) {
      return Blocks.WALL_TORCH.isValidPosition(p_196260_1_, p_196260_2_, p_196260_3_);
   }

   public BlockState updatePostPlacement(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      return Blocks.WALL_TORCH.updatePostPlacement(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
   }

   @Nullable
   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      BlockState lvt_2_1_ = Blocks.WALL_TORCH.getStateForPlacement(p_196258_1_);
      return lvt_2_1_ == null ? null : (BlockState)this.getDefaultState().with(FACING, lvt_2_1_.get(FACING));
   }

   @OnlyIn(Dist.CLIENT)
   public void animateTick(BlockState p_180655_1_, World p_180655_2_, BlockPos p_180655_3_, Random p_180655_4_) {
      if ((Boolean)p_180655_1_.get(REDSTONE_TORCH_LIT)) {
         Direction lvt_5_1_ = ((Direction)p_180655_1_.get(FACING)).getOpposite();
         double lvt_6_1_ = 0.27D;
         double lvt_8_1_ = (double)p_180655_3_.getX() + 0.5D + (p_180655_4_.nextDouble() - 0.5D) * 0.2D + 0.27D * (double)lvt_5_1_.getXOffset();
         double lvt_10_1_ = (double)p_180655_3_.getY() + 0.7D + (p_180655_4_.nextDouble() - 0.5D) * 0.2D + 0.22D;
         double lvt_12_1_ = (double)p_180655_3_.getZ() + 0.5D + (p_180655_4_.nextDouble() - 0.5D) * 0.2D + 0.27D * (double)lvt_5_1_.getZOffset();
         p_180655_2_.addParticle(RedstoneParticleData.REDSTONE_DUST, lvt_8_1_, lvt_10_1_, lvt_12_1_, 0.0D, 0.0D, 0.0D);
      }
   }

   protected boolean shouldBeOff(World p_176597_1_, BlockPos p_176597_2_, BlockState p_176597_3_) {
      Direction lvt_4_1_ = ((Direction)p_176597_3_.get(FACING)).getOpposite();
      return p_176597_1_.isSidePowered(p_176597_2_.offset(lvt_4_1_), lvt_4_1_);
   }

   public int getWeakPower(BlockState p_180656_1_, IBlockReader p_180656_2_, BlockPos p_180656_3_, Direction p_180656_4_) {
      return (Boolean)p_180656_1_.get(REDSTONE_TORCH_LIT) && p_180656_1_.get(FACING) != p_180656_4_ ? 15 : 0;
   }

   public BlockState rotate(BlockState p_185499_1_, Rotation p_185499_2_) {
      return Blocks.WALL_TORCH.rotate(p_185499_1_, p_185499_2_);
   }

   public BlockState mirror(BlockState p_185471_1_, Mirror p_185471_2_) {
      return Blocks.WALL_TORCH.mirror(p_185471_1_, p_185471_2_);
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(FACING, REDSTONE_TORCH_LIT);
   }

   static {
      FACING = HorizontalBlock.HORIZONTAL_FACING;
      REDSTONE_TORCH_LIT = RedstoneTorchBlock.LIT;
   }
}
