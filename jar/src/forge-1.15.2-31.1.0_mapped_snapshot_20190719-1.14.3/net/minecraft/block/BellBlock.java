package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BellAttachment;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.BellTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class BellBlock extends ContainerBlock {
   public static final DirectionProperty field_220133_a;
   private static final EnumProperty<BellAttachment> field_220134_b;
   public static final BooleanProperty field_226883_b_;
   private static final VoxelShape field_220135_c;
   private static final VoxelShape field_220136_d;
   private static final VoxelShape field_220137_e;
   private static final VoxelShape field_220138_f;
   private static final VoxelShape field_220139_g;
   private static final VoxelShape field_220140_h;
   private static final VoxelShape field_220141_i;
   private static final VoxelShape field_220142_j;
   private static final VoxelShape field_220143_k;
   private static final VoxelShape field_220144_w;
   private static final VoxelShape field_220145_x;
   private static final VoxelShape field_220146_y;

   public BellBlock(Block.Properties p_i49993_1_) {
      super(p_i49993_1_);
      this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateContainer.getBaseState()).with(field_220133_a, Direction.NORTH)).with(field_220134_b, BellAttachment.FLOOR)).with(field_226883_b_, false));
   }

   public void neighborChanged(BlockState p_220069_1_, World p_220069_2_, BlockPos p_220069_3_, Block p_220069_4_, BlockPos p_220069_5_, boolean p_220069_6_) {
      boolean lvt_7_1_ = p_220069_2_.isBlockPowered(p_220069_3_);
      if (lvt_7_1_ != (Boolean)p_220069_1_.get(field_226883_b_)) {
         if (lvt_7_1_) {
            this.func_226885_a_(p_220069_2_, p_220069_3_, (Direction)null);
         }

         p_220069_2_.setBlockState(p_220069_3_, (BlockState)p_220069_1_.with(field_226883_b_, lvt_7_1_), 3);
      }

   }

   public void onProjectileCollision(World p_220066_1_, BlockState p_220066_2_, BlockRayTraceResult p_220066_3_, Entity p_220066_4_) {
      if (p_220066_4_ instanceof AbstractArrowEntity) {
         Entity lvt_5_1_ = ((AbstractArrowEntity)p_220066_4_).getShooter();
         PlayerEntity lvt_6_1_ = lvt_5_1_ instanceof PlayerEntity ? (PlayerEntity)lvt_5_1_ : null;
         this.func_226884_a_(p_220066_1_, p_220066_2_, p_220066_3_, lvt_6_1_, true);
      }

   }

   public ActionResultType func_225533_a_(BlockState p_225533_1_, World p_225533_2_, BlockPos p_225533_3_, PlayerEntity p_225533_4_, Hand p_225533_5_, BlockRayTraceResult p_225533_6_) {
      return this.func_226884_a_(p_225533_2_, p_225533_1_, p_225533_6_, p_225533_4_, true) ? ActionResultType.SUCCESS : ActionResultType.PASS;
   }

   public boolean func_226884_a_(World p_226884_1_, BlockState p_226884_2_, BlockRayTraceResult p_226884_3_, @Nullable PlayerEntity p_226884_4_, boolean p_226884_5_) {
      Direction lvt_6_1_ = p_226884_3_.getFace();
      BlockPos lvt_7_1_ = p_226884_3_.getPos();
      boolean lvt_8_1_ = !p_226884_5_ || this.func_220129_a(p_226884_2_, lvt_6_1_, p_226884_3_.getHitVec().y - (double)lvt_7_1_.getY());
      if (lvt_8_1_) {
         boolean lvt_9_1_ = this.func_226885_a_(p_226884_1_, lvt_7_1_, lvt_6_1_);
         if (lvt_9_1_ && p_226884_4_ != null) {
            p_226884_4_.addStat(Stats.BELL_RING);
         }

         return true;
      } else {
         return false;
      }
   }

   private boolean func_220129_a(BlockState p_220129_1_, Direction p_220129_2_, double p_220129_3_) {
      if (p_220129_2_.getAxis() != Direction.Axis.Y && p_220129_3_ <= 0.8123999834060669D) {
         Direction lvt_5_1_ = (Direction)p_220129_1_.get(field_220133_a);
         BellAttachment lvt_6_1_ = (BellAttachment)p_220129_1_.get(field_220134_b);
         switch(lvt_6_1_) {
         case FLOOR:
            return lvt_5_1_.getAxis() == p_220129_2_.getAxis();
         case SINGLE_WALL:
         case DOUBLE_WALL:
            return lvt_5_1_.getAxis() != p_220129_2_.getAxis();
         case CEILING:
            return true;
         default:
            return false;
         }
      } else {
         return false;
      }
   }

   public boolean func_226885_a_(World p_226885_1_, BlockPos p_226885_2_, @Nullable Direction p_226885_3_) {
      TileEntity lvt_4_1_ = p_226885_1_.getTileEntity(p_226885_2_);
      if (!p_226885_1_.isRemote && lvt_4_1_ instanceof BellTileEntity) {
         if (p_226885_3_ == null) {
            p_226885_3_ = (Direction)p_226885_1_.getBlockState(p_226885_2_).get(field_220133_a);
         }

         ((BellTileEntity)lvt_4_1_).func_213939_a(p_226885_3_);
         p_226885_1_.playSound((PlayerEntity)null, p_226885_2_, SoundEvents.BLOCK_BELL_USE, SoundCategory.BLOCKS, 2.0F, 1.0F);
         return true;
      } else {
         return false;
      }
   }

   private VoxelShape getShape(BlockState p_220128_1_) {
      Direction lvt_2_1_ = (Direction)p_220128_1_.get(field_220133_a);
      BellAttachment lvt_3_1_ = (BellAttachment)p_220128_1_.get(field_220134_b);
      if (lvt_3_1_ == BellAttachment.FLOOR) {
         return lvt_2_1_ != Direction.NORTH && lvt_2_1_ != Direction.SOUTH ? field_220136_d : field_220135_c;
      } else if (lvt_3_1_ == BellAttachment.CEILING) {
         return field_220146_y;
      } else if (lvt_3_1_ == BellAttachment.DOUBLE_WALL) {
         return lvt_2_1_ != Direction.NORTH && lvt_2_1_ != Direction.SOUTH ? field_220141_i : field_220140_h;
      } else if (lvt_2_1_ == Direction.NORTH) {
         return field_220144_w;
      } else if (lvt_2_1_ == Direction.SOUTH) {
         return field_220145_x;
      } else {
         return lvt_2_1_ == Direction.EAST ? field_220143_k : field_220142_j;
      }
   }

   public VoxelShape getCollisionShape(BlockState p_220071_1_, IBlockReader p_220071_2_, BlockPos p_220071_3_, ISelectionContext p_220071_4_) {
      return this.getShape(p_220071_1_);
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      return this.getShape(p_220053_1_);
   }

   public BlockRenderType getRenderType(BlockState p_149645_1_) {
      return BlockRenderType.MODEL;
   }

   @Nullable
   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      Direction lvt_3_1_ = p_196258_1_.getFace();
      BlockPos lvt_4_1_ = p_196258_1_.getPos();
      World lvt_5_1_ = p_196258_1_.getWorld();
      Direction.Axis lvt_6_1_ = lvt_3_1_.getAxis();
      BlockState lvt_2_2_;
      if (lvt_6_1_ == Direction.Axis.Y) {
         lvt_2_2_ = (BlockState)((BlockState)this.getDefaultState().with(field_220134_b, lvt_3_1_ == Direction.DOWN ? BellAttachment.CEILING : BellAttachment.FLOOR)).with(field_220133_a, p_196258_1_.getPlacementHorizontalFacing());
         if (lvt_2_2_.isValidPosition(p_196258_1_.getWorld(), lvt_4_1_)) {
            return lvt_2_2_;
         }
      } else {
         boolean lvt_7_1_ = lvt_6_1_ == Direction.Axis.X && lvt_5_1_.getBlockState(lvt_4_1_.west()).func_224755_d(lvt_5_1_, lvt_4_1_.west(), Direction.EAST) && lvt_5_1_.getBlockState(lvt_4_1_.east()).func_224755_d(lvt_5_1_, lvt_4_1_.east(), Direction.WEST) || lvt_6_1_ == Direction.Axis.Z && lvt_5_1_.getBlockState(lvt_4_1_.north()).func_224755_d(lvt_5_1_, lvt_4_1_.north(), Direction.SOUTH) && lvt_5_1_.getBlockState(lvt_4_1_.south()).func_224755_d(lvt_5_1_, lvt_4_1_.south(), Direction.NORTH);
         lvt_2_2_ = (BlockState)((BlockState)this.getDefaultState().with(field_220133_a, lvt_3_1_.getOpposite())).with(field_220134_b, lvt_7_1_ ? BellAttachment.DOUBLE_WALL : BellAttachment.SINGLE_WALL);
         if (lvt_2_2_.isValidPosition(p_196258_1_.getWorld(), p_196258_1_.getPos())) {
            return lvt_2_2_;
         }

         boolean lvt_8_1_ = lvt_5_1_.getBlockState(lvt_4_1_.down()).func_224755_d(lvt_5_1_, lvt_4_1_.down(), Direction.UP);
         lvt_2_2_ = (BlockState)lvt_2_2_.with(field_220134_b, lvt_8_1_ ? BellAttachment.FLOOR : BellAttachment.CEILING);
         if (lvt_2_2_.isValidPosition(p_196258_1_.getWorld(), p_196258_1_.getPos())) {
            return lvt_2_2_;
         }
      }

      return null;
   }

   public BlockState updatePostPlacement(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      BellAttachment lvt_7_1_ = (BellAttachment)p_196271_1_.get(field_220134_b);
      Direction lvt_8_1_ = func_220131_q(p_196271_1_).getOpposite();
      if (lvt_8_1_ == p_196271_2_ && !p_196271_1_.isValidPosition(p_196271_4_, p_196271_5_) && lvt_7_1_ != BellAttachment.DOUBLE_WALL) {
         return Blocks.AIR.getDefaultState();
      } else {
         if (p_196271_2_.getAxis() == ((Direction)p_196271_1_.get(field_220133_a)).getAxis()) {
            if (lvt_7_1_ == BellAttachment.DOUBLE_WALL && !p_196271_3_.func_224755_d(p_196271_4_, p_196271_6_, p_196271_2_)) {
               return (BlockState)((BlockState)p_196271_1_.with(field_220134_b, BellAttachment.SINGLE_WALL)).with(field_220133_a, p_196271_2_.getOpposite());
            }

            if (lvt_7_1_ == BellAttachment.SINGLE_WALL && lvt_8_1_.getOpposite() == p_196271_2_ && p_196271_3_.func_224755_d(p_196271_4_, p_196271_6_, (Direction)p_196271_1_.get(field_220133_a))) {
               return (BlockState)p_196271_1_.with(field_220134_b, BellAttachment.DOUBLE_WALL);
            }
         }

         return super.updatePostPlacement(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
      }
   }

   public boolean isValidPosition(BlockState p_196260_1_, IWorldReader p_196260_2_, BlockPos p_196260_3_) {
      return HorizontalFaceBlock.func_220185_b(p_196260_2_, p_196260_3_, func_220131_q(p_196260_1_).getOpposite());
   }

   private static Direction func_220131_q(BlockState p_220131_0_) {
      switch((BellAttachment)p_220131_0_.get(field_220134_b)) {
      case FLOOR:
         return Direction.UP;
      case CEILING:
         return Direction.DOWN;
      default:
         return ((Direction)p_220131_0_.get(field_220133_a)).getOpposite();
      }
   }

   public PushReaction getPushReaction(BlockState p_149656_1_) {
      return PushReaction.DESTROY;
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(field_220133_a, field_220134_b, field_226883_b_);
   }

   @Nullable
   public TileEntity createNewTileEntity(IBlockReader p_196283_1_) {
      return new BellTileEntity();
   }

   public boolean allowsMovement(BlockState p_196266_1_, IBlockReader p_196266_2_, BlockPos p_196266_3_, PathType p_196266_4_) {
      return false;
   }

   static {
      field_220133_a = HorizontalBlock.HORIZONTAL_FACING;
      field_220134_b = BlockStateProperties.BELL_ATTACHMENT;
      field_226883_b_ = BlockStateProperties.POWERED;
      field_220135_c = Block.makeCuboidShape(0.0D, 0.0D, 4.0D, 16.0D, 16.0D, 12.0D);
      field_220136_d = Block.makeCuboidShape(4.0D, 0.0D, 0.0D, 12.0D, 16.0D, 16.0D);
      field_220137_e = Block.makeCuboidShape(5.0D, 6.0D, 5.0D, 11.0D, 13.0D, 11.0D);
      field_220138_f = Block.makeCuboidShape(4.0D, 4.0D, 4.0D, 12.0D, 6.0D, 12.0D);
      field_220139_g = VoxelShapes.or(field_220138_f, field_220137_e);
      field_220140_h = VoxelShapes.or(field_220139_g, Block.makeCuboidShape(7.0D, 13.0D, 0.0D, 9.0D, 15.0D, 16.0D));
      field_220141_i = VoxelShapes.or(field_220139_g, Block.makeCuboidShape(0.0D, 13.0D, 7.0D, 16.0D, 15.0D, 9.0D));
      field_220142_j = VoxelShapes.or(field_220139_g, Block.makeCuboidShape(0.0D, 13.0D, 7.0D, 13.0D, 15.0D, 9.0D));
      field_220143_k = VoxelShapes.or(field_220139_g, Block.makeCuboidShape(3.0D, 13.0D, 7.0D, 16.0D, 15.0D, 9.0D));
      field_220144_w = VoxelShapes.or(field_220139_g, Block.makeCuboidShape(7.0D, 13.0D, 0.0D, 9.0D, 15.0D, 13.0D));
      field_220145_x = VoxelShapes.or(field_220139_g, Block.makeCuboidShape(7.0D, 13.0D, 3.0D, 9.0D, 15.0D, 16.0D));
      field_220146_y = VoxelShapes.or(field_220139_g, Block.makeCuboidShape(7.0D, 13.0D, 7.0D, 9.0D, 16.0D, 9.0D));
   }
}
