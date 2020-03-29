package net.minecraft.block;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.SignTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class AbstractSignBlock extends ContainerBlock implements IWaterLoggable {
   public static final BooleanProperty WATERLOGGED;
   protected static final VoxelShape SHAPE;
   private final WoodType field_226943_c_;

   protected AbstractSignBlock(Block.Properties p_i225763_1_, WoodType p_i225763_2_) {
      super(p_i225763_1_);
      this.field_226943_c_ = p_i225763_2_;
   }

   public BlockState updatePostPlacement(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
      if ((Boolean)p_196271_1_.get(WATERLOGGED)) {
         p_196271_4_.getPendingFluidTicks().scheduleTick(p_196271_5_, Fluids.WATER, Fluids.WATER.getTickRate(p_196271_4_));
      }

      return super.updatePostPlacement(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      return SHAPE;
   }

   public boolean canSpawnInBlock() {
      return true;
   }

   public TileEntity createNewTileEntity(IBlockReader p_196283_1_) {
      return new SignTileEntity();
   }

   public ActionResultType func_225533_a_(BlockState p_225533_1_, World p_225533_2_, BlockPos p_225533_3_, PlayerEntity p_225533_4_, Hand p_225533_5_, BlockRayTraceResult p_225533_6_) {
      ItemStack lvt_7_1_ = p_225533_4_.getHeldItem(p_225533_5_);
      boolean lvt_8_1_ = lvt_7_1_.getItem() instanceof DyeItem && p_225533_4_.abilities.allowEdit;
      if (p_225533_2_.isRemote) {
         return lvt_8_1_ ? ActionResultType.SUCCESS : ActionResultType.CONSUME;
      } else {
         TileEntity lvt_9_1_ = p_225533_2_.getTileEntity(p_225533_3_);
         if (lvt_9_1_ instanceof SignTileEntity) {
            SignTileEntity lvt_10_1_ = (SignTileEntity)lvt_9_1_;
            if (lvt_8_1_) {
               boolean lvt_11_1_ = lvt_10_1_.setTextColor(((DyeItem)lvt_7_1_.getItem()).getDyeColor());
               if (lvt_11_1_ && !p_225533_4_.isCreative()) {
                  lvt_7_1_.shrink(1);
               }
            }

            return lvt_10_1_.executeCommand(p_225533_4_) ? ActionResultType.SUCCESS : ActionResultType.PASS;
         } else {
            return ActionResultType.PASS;
         }
      }
   }

   public IFluidState getFluidState(BlockState p_204507_1_) {
      return (Boolean)p_204507_1_.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(p_204507_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public WoodType func_226944_c_() {
      return this.field_226943_c_;
   }

   static {
      WATERLOGGED = BlockStateProperties.WATERLOGGED;
      SHAPE = Block.makeCuboidShape(4.0D, 0.0D, 4.0D, 12.0D, 16.0D, 12.0D);
   }
}
