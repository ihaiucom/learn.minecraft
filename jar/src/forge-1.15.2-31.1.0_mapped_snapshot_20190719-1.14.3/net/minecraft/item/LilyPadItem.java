package net.minecraft.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.ForgeEventFactory;

public class LilyPadItem extends BlockItem {
   public LilyPadItem(Block p_i48456_1_, Item.Properties p_i48456_2_) {
      super(p_i48456_1_, p_i48456_2_);
   }

   public ActionResultType onItemUse(ItemUseContext p_195939_1_) {
      return ActionResultType.PASS;
   }

   public ActionResult<ItemStack> onItemRightClick(World p_77659_1_, PlayerEntity p_77659_2_, Hand p_77659_3_) {
      ItemStack itemstack = p_77659_2_.getHeldItem(p_77659_3_);
      RayTraceResult raytraceresult = rayTrace(p_77659_1_, p_77659_2_, RayTraceContext.FluidMode.SOURCE_ONLY);
      if (raytraceresult.getType() == RayTraceResult.Type.MISS) {
         return ActionResult.func_226250_c_(itemstack);
      } else {
         if (raytraceresult.getType() == RayTraceResult.Type.BLOCK) {
            BlockRayTraceResult blockraytraceresult = (BlockRayTraceResult)raytraceresult;
            BlockPos blockpos = blockraytraceresult.getPos();
            Direction direction = blockraytraceresult.getFace();
            if (!p_77659_1_.isBlockModifiable(p_77659_2_, blockpos) || !p_77659_2_.canPlayerEdit(blockpos.offset(direction), direction, itemstack)) {
               return ActionResult.func_226251_d_(itemstack);
            }

            BlockPos blockpos1 = blockpos.up();
            BlockState blockstate = p_77659_1_.getBlockState(blockpos);
            Material material = blockstate.getMaterial();
            IFluidState ifluidstate = p_77659_1_.getFluidState(blockpos);
            if ((ifluidstate.getFluid() == Fluids.WATER || material == Material.ICE) && p_77659_1_.isAirBlock(blockpos1)) {
               BlockSnapshot blocksnapshot = BlockSnapshot.getBlockSnapshot(p_77659_1_, blockpos1);
               p_77659_1_.setBlockState(blockpos1, Blocks.LILY_PAD.getDefaultState(), 11);
               if (ForgeEventFactory.onBlockPlace(p_77659_2_, blocksnapshot, Direction.UP)) {
                  blocksnapshot.restore(true, false);
                  return ActionResult.func_226251_d_(itemstack);
               }

               if (p_77659_2_ instanceof ServerPlayerEntity) {
                  CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayerEntity)p_77659_2_, blockpos1, itemstack);
               }

               if (!p_77659_2_.abilities.isCreativeMode) {
                  itemstack.shrink(1);
               }

               p_77659_2_.addStat(Stats.ITEM_USED.get(this));
               p_77659_1_.playSound(p_77659_2_, blockpos, SoundEvents.BLOCK_LILY_PAD_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F);
               return ActionResult.func_226248_a_(itemstack);
            }
         }

         return ActionResult.func_226251_d_(itemstack);
      }
   }
}
