package net.minecraft.block;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.PistonType;
import net.minecraft.tileentity.PistonTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameters;

public class MovingPistonBlock extends ContainerBlock {
   public static final DirectionProperty FACING;
   public static final EnumProperty<PistonType> TYPE;

   public MovingPistonBlock(Block.Properties p_i48282_1_) {
      super(p_i48282_1_);
      this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateContainer.getBaseState()).with(FACING, Direction.NORTH)).with(TYPE, PistonType.DEFAULT));
   }

   @Nullable
   public TileEntity createNewTileEntity(IBlockReader p_196283_1_) {
      return null;
   }

   public static TileEntity createTilePiston(BlockState p_196343_0_, Direction p_196343_1_, boolean p_196343_2_, boolean p_196343_3_) {
      return new PistonTileEntity(p_196343_0_, p_196343_1_, p_196343_2_, p_196343_3_);
   }

   public void onReplaced(BlockState p_196243_1_, World p_196243_2_, BlockPos p_196243_3_, BlockState p_196243_4_, boolean p_196243_5_) {
      if (p_196243_1_.getBlock() != p_196243_4_.getBlock()) {
         TileEntity tileentity = p_196243_2_.getTileEntity(p_196243_3_);
         if (tileentity instanceof PistonTileEntity) {
            ((PistonTileEntity)tileentity).clearPistonTileEntity();
         }
      }

   }

   public void onPlayerDestroy(IWorld p_176206_1_, BlockPos p_176206_2_, BlockState p_176206_3_) {
      BlockPos blockpos = p_176206_2_.offset(((Direction)p_176206_3_.get(FACING)).getOpposite());
      BlockState blockstate = p_176206_1_.getBlockState(blockpos);
      if (blockstate.getBlock() instanceof PistonBlock && (Boolean)blockstate.get(PistonBlock.EXTENDED)) {
         p_176206_1_.removeBlock(blockpos, false);
      }

   }

   public boolean isNormalCube(BlockState p_220081_1_, IBlockReader p_220081_2_, BlockPos p_220081_3_) {
      return false;
   }

   public boolean func_229869_c_(BlockState p_229869_1_, IBlockReader p_229869_2_, BlockPos p_229869_3_) {
      return false;
   }

   public ActionResultType func_225533_a_(BlockState p_225533_1_, World p_225533_2_, BlockPos p_225533_3_, PlayerEntity p_225533_4_, Hand p_225533_5_, BlockRayTraceResult p_225533_6_) {
      if (!p_225533_2_.isRemote && p_225533_2_.getTileEntity(p_225533_3_) == null) {
         p_225533_2_.removeBlock(p_225533_3_, false);
         return ActionResultType.CONSUME;
      } else {
         return ActionResultType.PASS;
      }
   }

   public List<ItemStack> getDrops(BlockState p_220076_1_, LootContext.Builder p_220076_2_) {
      PistonTileEntity pistontileentity = this.func_220170_a(p_220076_2_.getWorld(), (BlockPos)p_220076_2_.assertPresent(LootParameters.POSITION));
      return pistontileentity == null ? Collections.emptyList() : pistontileentity.getPistonState().getDrops(p_220076_2_);
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      return VoxelShapes.empty();
   }

   public VoxelShape getCollisionShape(BlockState p_220071_1_, IBlockReader p_220071_2_, BlockPos p_220071_3_, ISelectionContext p_220071_4_) {
      PistonTileEntity pistontileentity = this.func_220170_a(p_220071_2_, p_220071_3_);
      return pistontileentity != null ? pistontileentity.getCollisionShape(p_220071_2_, p_220071_3_) : VoxelShapes.empty();
   }

   @Nullable
   private PistonTileEntity func_220170_a(IBlockReader p_220170_1_, BlockPos p_220170_2_) {
      TileEntity tileentity = p_220170_1_.getTileEntity(p_220170_2_);
      return tileentity instanceof PistonTileEntity ? (PistonTileEntity)tileentity : null;
   }

   public ItemStack getItem(IBlockReader p_185473_1_, BlockPos p_185473_2_, BlockState p_185473_3_) {
      return ItemStack.EMPTY;
   }

   public BlockState rotate(BlockState p_185499_1_, Rotation p_185499_2_) {
      return (BlockState)p_185499_1_.with(FACING, p_185499_2_.rotate((Direction)p_185499_1_.get(FACING)));
   }

   public BlockState mirror(BlockState p_185471_1_, Mirror p_185471_2_) {
      return p_185471_1_.rotate(p_185471_2_.toRotation((Direction)p_185471_1_.get(FACING)));
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(FACING, TYPE);
   }

   public boolean allowsMovement(BlockState p_196266_1_, IBlockReader p_196266_2_, BlockPos p_196266_3_, PathType p_196266_4_) {
      return false;
   }

   static {
      FACING = PistonHeadBlock.FACING;
      TYPE = PistonHeadBlock.TYPE;
   }
}
