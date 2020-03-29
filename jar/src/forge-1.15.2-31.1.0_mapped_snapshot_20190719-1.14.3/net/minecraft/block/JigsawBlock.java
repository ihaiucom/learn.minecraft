package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.JigsawTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.template.Template;

public class JigsawBlock extends DirectionalBlock implements ITileEntityProvider {
   protected JigsawBlock(Block.Properties p_i49981_1_) {
      super(p_i49981_1_);
      this.setDefaultState((BlockState)((BlockState)this.stateContainer.getBaseState()).with(FACING, Direction.UP));
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(FACING);
   }

   public BlockState rotate(BlockState p_185499_1_, Rotation p_185499_2_) {
      return (BlockState)p_185499_1_.with(FACING, p_185499_2_.rotate((Direction)p_185499_1_.get(FACING)));
   }

   /** @deprecated */
   @Deprecated
   public BlockState mirror(BlockState p_185471_1_, Mirror p_185471_2_) {
      return (BlockState)p_185471_1_.with(FACING, p_185471_2_.mirror((Direction)p_185471_1_.get(FACING)));
   }

   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      return (BlockState)this.getDefaultState().with(FACING, p_196258_1_.getFace());
   }

   @Nullable
   public TileEntity createNewTileEntity(IBlockReader p_196283_1_) {
      return new JigsawTileEntity();
   }

   public ActionResultType func_225533_a_(BlockState p_225533_1_, World p_225533_2_, BlockPos p_225533_3_, PlayerEntity p_225533_4_, Hand p_225533_5_, BlockRayTraceResult p_225533_6_) {
      TileEntity tileentity = p_225533_2_.getTileEntity(p_225533_3_);
      if (tileentity instanceof JigsawTileEntity && p_225533_4_.canUseCommandBlock()) {
         p_225533_4_.func_213826_a((JigsawTileEntity)tileentity);
         return ActionResultType.SUCCESS;
      } else {
         return ActionResultType.PASS;
      }
   }

   public static boolean func_220171_a(Template.BlockInfo p_220171_0_, Template.BlockInfo p_220171_1_) {
      return p_220171_0_.state.get(FACING) == ((Direction)p_220171_1_.state.get(FACING)).getOpposite() && p_220171_0_.nbt.getString("attachement_type").equals(p_220171_1_.nbt.getString("attachement_type"));
   }
}
