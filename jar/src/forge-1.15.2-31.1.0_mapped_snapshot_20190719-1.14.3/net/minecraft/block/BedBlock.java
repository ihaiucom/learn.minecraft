package net.minecraft.block;

import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BedPart;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.BedTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMerger;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.extensions.IForgeDimension;

public class BedBlock extends HorizontalBlock implements ITileEntityProvider {
   public static final EnumProperty<BedPart> PART;
   public static final BooleanProperty OCCUPIED;
   protected static final VoxelShape field_220176_c;
   protected static final VoxelShape field_220177_d;
   protected static final VoxelShape field_220178_e;
   protected static final VoxelShape field_220179_f;
   protected static final VoxelShape field_220180_g;
   protected static final VoxelShape field_220181_h;
   protected static final VoxelShape field_220182_i;
   protected static final VoxelShape field_220183_j;
   protected static final VoxelShape field_220184_k;
   private final DyeColor color;

   public BedBlock(DyeColor p_i48442_1_, Block.Properties p_i48442_2_) {
      super(p_i48442_2_);
      this.color = p_i48442_1_;
      this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateContainer.getBaseState()).with(PART, BedPart.FOOT)).with(OCCUPIED, false));
   }

   public MaterialColor getMaterialColor(BlockState p_180659_1_, IBlockReader p_180659_2_, BlockPos p_180659_3_) {
      return p_180659_1_.get(PART) == BedPart.FOOT ? this.color.getMapColor() : MaterialColor.WOOL;
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public static Direction func_220174_a(IBlockReader p_220174_0_, BlockPos p_220174_1_) {
      BlockState blockstate = p_220174_0_.getBlockState(p_220174_1_);
      return blockstate.getBlock() instanceof BedBlock ? (Direction)blockstate.get(HORIZONTAL_FACING) : null;
   }

   public ActionResultType func_225533_a_(BlockState p_225533_1_, World p_225533_2_, BlockPos p_225533_3_, PlayerEntity p_225533_4_, Hand p_225533_5_, BlockRayTraceResult p_225533_6_) {
      if (p_225533_2_.isRemote) {
         return ActionResultType.CONSUME;
      } else {
         if (p_225533_1_.get(PART) != BedPart.HEAD) {
            p_225533_3_ = p_225533_3_.offset((Direction)p_225533_1_.get(HORIZONTAL_FACING));
            p_225533_1_ = p_225533_2_.getBlockState(p_225533_3_);
            if (p_225533_1_.getBlock() != this) {
               return ActionResultType.CONSUME;
            }
         }

         IForgeDimension.SleepResult sleepResult = p_225533_2_.dimension.canSleepAt(p_225533_4_, p_225533_3_);
         if (sleepResult != IForgeDimension.SleepResult.BED_EXPLODES) {
            if (sleepResult == IForgeDimension.SleepResult.DENY) {
               return ActionResultType.SUCCESS;
            } else if ((Boolean)p_225533_1_.get(OCCUPIED)) {
               if (!this.func_226861_a_(p_225533_2_, p_225533_3_)) {
                  p_225533_4_.sendStatusMessage(new TranslationTextComponent("block.minecraft.bed.occupied", new Object[0]), true);
               }

               return ActionResultType.SUCCESS;
            } else {
               p_225533_4_.trySleep(p_225533_3_).ifLeft((p_lambda$func_225533_a_$0_1_) -> {
                  if (p_lambda$func_225533_a_$0_1_ != null) {
                     p_225533_4_.sendStatusMessage(p_lambda$func_225533_a_$0_1_.getMessage(), true);
                  }

               });
               return ActionResultType.SUCCESS;
            }
         } else {
            p_225533_2_.removeBlock(p_225533_3_, false);
            BlockPos blockpos = p_225533_3_.offset(((Direction)p_225533_1_.get(HORIZONTAL_FACING)).getOpposite());
            if (p_225533_2_.getBlockState(blockpos).getBlock() == this) {
               p_225533_2_.removeBlock(blockpos, false);
            }

            p_225533_2_.createExplosion((Entity)null, DamageSource.netherBedExplosion(), (double)p_225533_3_.getX() + 0.5D, (double)p_225533_3_.getY() + 0.5D, (double)p_225533_3_.getZ() + 0.5D, 5.0F, true, Explosion.Mode.DESTROY);
            return ActionResultType.SUCCESS;
         }
      }
   }

   private boolean func_226861_a_(World p_226861_1_, BlockPos p_226861_2_) {
      List<VillagerEntity> list = p_226861_1_.getEntitiesWithinAABB(VillagerEntity.class, new AxisAlignedBB(p_226861_2_), LivingEntity::isSleeping);
      if (list.isEmpty()) {
         return false;
      } else {
         ((VillagerEntity)list.get(0)).wakeUp();
         return true;
      }
   }

   public void onFallenUpon(World p_180658_1_, BlockPos p_180658_2_, Entity p_180658_3_, float p_180658_4_) {
      super.onFallenUpon(p_180658_1_, p_180658_2_, p_180658_3_, p_180658_4_ * 0.5F);
   }

   public void onLanded(IBlockReader p_176216_1_, Entity p_176216_2_) {
      if (p_176216_2_.func_226272_bl_()) {
         super.onLanded(p_176216_1_, p_176216_2_);
      } else {
         this.func_226860_a_(p_176216_2_);
      }

   }

   private void func_226860_a_(Entity p_226860_1_) {
      Vec3d vec3d = p_226860_1_.getMotion();
      if (vec3d.y < 0.0D) {
         double d0 = p_226860_1_ instanceof LivingEntity ? 1.0D : 0.8D;
         p_226860_1_.setMotion(vec3d.x, -vec3d.y * 0.6600000262260437D * d0, vec3d.z);
      }

   }

   public BlockState updatePostPlacement(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      if (p_196271_2_ != getDirectionToOther((BedPart)p_196271_1_.get(PART), (Direction)p_196271_1_.get(HORIZONTAL_FACING))) {
         return super.updatePostPlacement(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
      } else {
         return p_196271_3_.getBlock() == this && p_196271_3_.get(PART) != p_196271_1_.get(PART) ? (BlockState)p_196271_1_.with(OCCUPIED, p_196271_3_.get(OCCUPIED)) : Blocks.AIR.getDefaultState();
      }
   }

   private static Direction getDirectionToOther(BedPart p_208070_0_, Direction p_208070_1_) {
      return p_208070_0_ == BedPart.FOOT ? p_208070_1_ : p_208070_1_.getOpposite();
   }

   public void harvestBlock(World p_180657_1_, PlayerEntity p_180657_2_, BlockPos p_180657_3_, BlockState p_180657_4_, @Nullable TileEntity p_180657_5_, ItemStack p_180657_6_) {
      super.harvestBlock(p_180657_1_, p_180657_2_, p_180657_3_, Blocks.AIR.getDefaultState(), p_180657_5_, p_180657_6_);
   }

   public void onBlockHarvested(World p_176208_1_, BlockPos p_176208_2_, BlockState p_176208_3_, PlayerEntity p_176208_4_) {
      BedPart bedpart = (BedPart)p_176208_3_.get(PART);
      BlockPos blockpos = p_176208_2_.offset(getDirectionToOther(bedpart, (Direction)p_176208_3_.get(HORIZONTAL_FACING)));
      BlockState blockstate = p_176208_1_.getBlockState(blockpos);
      if (blockstate.getBlock() == this && blockstate.get(PART) != bedpart) {
         p_176208_1_.setBlockState(blockpos, Blocks.AIR.getDefaultState(), 35);
         p_176208_1_.playEvent(p_176208_4_, 2001, blockpos, Block.getStateId(blockstate));
         if (!p_176208_1_.isRemote && !p_176208_4_.isCreative()) {
            ItemStack itemstack = p_176208_4_.getHeldItemMainhand();
            spawnDrops(p_176208_3_, p_176208_1_, p_176208_2_, (TileEntity)null, p_176208_4_, itemstack);
            spawnDrops(blockstate, p_176208_1_, blockpos, (TileEntity)null, p_176208_4_, itemstack);
         }

         p_176208_4_.addStat(Stats.BLOCK_MINED.get(this));
      }

      super.onBlockHarvested(p_176208_1_, p_176208_2_, p_176208_3_, p_176208_4_);
   }

   @Nullable
   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      Direction direction = p_196258_1_.getPlacementHorizontalFacing();
      BlockPos blockpos = p_196258_1_.getPos();
      BlockPos blockpos1 = blockpos.offset(direction);
      return p_196258_1_.getWorld().getBlockState(blockpos1).isReplaceable(p_196258_1_) ? (BlockState)this.getDefaultState().with(HORIZONTAL_FACING, direction) : null;
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      Direction direction = func_226862_h_(p_220053_1_).getOpposite();
      switch(direction) {
      case NORTH:
         return field_220181_h;
      case SOUTH:
         return field_220182_i;
      case WEST:
         return field_220183_j;
      default:
         return field_220184_k;
      }
   }

   public static Direction func_226862_h_(BlockState p_226862_0_) {
      Direction direction = (Direction)p_226862_0_.get(HORIZONTAL_FACING);
      return p_226862_0_.get(PART) == BedPart.HEAD ? direction.getOpposite() : direction;
   }

   @OnlyIn(Dist.CLIENT)
   public static TileEntityMerger.Type func_226863_i_(BlockState p_226863_0_) {
      BedPart bedpart = (BedPart)p_226863_0_.get(PART);
      return bedpart == BedPart.HEAD ? TileEntityMerger.Type.FIRST : TileEntityMerger.Type.SECOND;
   }

   public static Optional<Vec3d> func_220172_a(EntityType<?> p_220172_0_, IWorldReader p_220172_1_, BlockPos p_220172_2_, int p_220172_3_) {
      Direction direction = (Direction)p_220172_1_.getBlockState(p_220172_2_).get(HORIZONTAL_FACING);
      int i = p_220172_2_.getX();
      int j = p_220172_2_.getY();
      int k = p_220172_2_.getZ();

      for(int l = 0; l <= 1; ++l) {
         int i1 = i - direction.getXOffset() * l - 1;
         int j1 = k - direction.getZOffset() * l - 1;
         int k1 = i1 + 2;
         int l1 = j1 + 2;

         for(int i2 = i1; i2 <= k1; ++i2) {
            for(int j2 = j1; j2 <= l1; ++j2) {
               BlockPos blockpos = new BlockPos(i2, j, j2);
               Optional<Vec3d> optional = func_220175_a(p_220172_0_, p_220172_1_, blockpos);
               if (optional.isPresent()) {
                  if (p_220172_3_ <= 0) {
                     return optional;
                  }

                  --p_220172_3_;
               }
            }
         }
      }

      return Optional.empty();
   }

   protected static Optional<Vec3d> func_220175_a(EntityType<?> p_220175_0_, IWorldReader p_220175_1_, BlockPos p_220175_2_) {
      VoxelShape voxelshape = p_220175_1_.getBlockState(p_220175_2_).getCollisionShape(p_220175_1_, p_220175_2_);
      if (voxelshape.getEnd(Direction.Axis.Y) > 0.4375D) {
         return Optional.empty();
      } else {
         BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable(p_220175_2_);

         while(blockpos$mutable.getY() >= 0 && p_220175_2_.getY() - blockpos$mutable.getY() <= 2 && p_220175_1_.getBlockState(blockpos$mutable).getCollisionShape(p_220175_1_, blockpos$mutable).isEmpty()) {
            blockpos$mutable.move(Direction.DOWN);
         }

         VoxelShape voxelshape1 = p_220175_1_.getBlockState(blockpos$mutable).getCollisionShape(p_220175_1_, blockpos$mutable);
         if (voxelshape1.isEmpty()) {
            return Optional.empty();
         } else {
            double d0 = (double)blockpos$mutable.getY() + voxelshape1.getEnd(Direction.Axis.Y) + 2.0E-7D;
            if ((double)p_220175_2_.getY() - d0 > 2.0D) {
               return Optional.empty();
            } else {
               float f = p_220175_0_.getWidth() / 2.0F;
               Vec3d vec3d = new Vec3d((double)blockpos$mutable.getX() + 0.5D, d0, (double)blockpos$mutable.getZ() + 0.5D);
               return p_220175_1_.func_226664_a_(new AxisAlignedBB(vec3d.x - (double)f, vec3d.y, vec3d.z - (double)f, vec3d.x + (double)f, vec3d.y + (double)p_220175_0_.getHeight(), vec3d.z + (double)f)) ? Optional.of(vec3d) : Optional.empty();
            }
         }
      }
   }

   public PushReaction getPushReaction(BlockState p_149656_1_) {
      return PushReaction.DESTROY;
   }

   public BlockRenderType getRenderType(BlockState p_149645_1_) {
      return BlockRenderType.ENTITYBLOCK_ANIMATED;
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(HORIZONTAL_FACING, PART, OCCUPIED);
   }

   public TileEntity createNewTileEntity(IBlockReader p_196283_1_) {
      return new BedTileEntity(this.color);
   }

   public void onBlockPlacedBy(World p_180633_1_, BlockPos p_180633_2_, BlockState p_180633_3_, @Nullable LivingEntity p_180633_4_, ItemStack p_180633_5_) {
      super.onBlockPlacedBy(p_180633_1_, p_180633_2_, p_180633_3_, p_180633_4_, p_180633_5_);
      if (!p_180633_1_.isRemote) {
         BlockPos blockpos = p_180633_2_.offset((Direction)p_180633_3_.get(HORIZONTAL_FACING));
         p_180633_1_.setBlockState(blockpos, (BlockState)p_180633_3_.with(PART, BedPart.HEAD), 3);
         p_180633_1_.notifyNeighbors(p_180633_2_, Blocks.AIR);
         p_180633_3_.updateNeighbors(p_180633_1_, p_180633_2_, 3);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public DyeColor getColor() {
      return this.color;
   }

   @OnlyIn(Dist.CLIENT)
   public long getPositionRandom(BlockState p_209900_1_, BlockPos p_209900_2_) {
      BlockPos blockpos = p_209900_2_.offset((Direction)p_209900_1_.get(HORIZONTAL_FACING), p_209900_1_.get(PART) == BedPart.HEAD ? 0 : 1);
      return MathHelper.getCoordinateRandom(blockpos.getX(), p_209900_2_.getY(), blockpos.getZ());
   }

   public boolean allowsMovement(BlockState p_196266_1_, IBlockReader p_196266_2_, BlockPos p_196266_3_, PathType p_196266_4_) {
      return false;
   }

   static {
      PART = BlockStateProperties.BED_PART;
      OCCUPIED = BlockStateProperties.OCCUPIED;
      field_220176_c = Block.makeCuboidShape(0.0D, 3.0D, 0.0D, 16.0D, 9.0D, 16.0D);
      field_220177_d = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 3.0D, 3.0D, 3.0D);
      field_220178_e = Block.makeCuboidShape(0.0D, 0.0D, 13.0D, 3.0D, 3.0D, 16.0D);
      field_220179_f = Block.makeCuboidShape(13.0D, 0.0D, 0.0D, 16.0D, 3.0D, 3.0D);
      field_220180_g = Block.makeCuboidShape(13.0D, 0.0D, 13.0D, 16.0D, 3.0D, 16.0D);
      field_220181_h = VoxelShapes.or(field_220176_c, field_220177_d, field_220179_f);
      field_220182_i = VoxelShapes.or(field_220176_c, field_220178_e, field_220180_g);
      field_220183_j = VoxelShapes.or(field_220176_c, field_220177_d, field_220178_e);
      field_220184_k = VoxelShapes.or(field_220176_c, field_220179_f, field_220180_g);
   }
}
