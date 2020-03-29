package net.minecraft.item;

import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IDispenseItemBehavior;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.state.properties.RailShape;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MinecartItem extends Item {
   private static final IDispenseItemBehavior MINECART_DISPENSER_BEHAVIOR = new DefaultDispenseItemBehavior() {
      private final DefaultDispenseItemBehavior behaviourDefaultDispenseItem = new DefaultDispenseItemBehavior();

      public ItemStack dispenseStack(IBlockSource p_82487_1_, ItemStack p_82487_2_) {
         Direction direction = (Direction)p_82487_1_.getBlockState().get(DispenserBlock.FACING);
         World world = p_82487_1_.getWorld();
         double d0 = p_82487_1_.getX() + (double)direction.getXOffset() * 1.125D;
         double d1 = Math.floor(p_82487_1_.getY()) + (double)direction.getYOffset();
         double d2 = p_82487_1_.getZ() + (double)direction.getZOffset() * 1.125D;
         BlockPos blockpos = p_82487_1_.getBlockPos().offset(direction);
         BlockState blockstate = world.getBlockState(blockpos);
         RailShape railshape = blockstate.getBlock() instanceof AbstractRailBlock ? ((AbstractRailBlock)blockstate.getBlock()).getRailDirection(blockstate, world, blockpos, (AbstractMinecartEntity)null) : RailShape.NORTH_SOUTH;
         double d3;
         if (blockstate.isIn(BlockTags.RAILS)) {
            if (railshape.isAscending()) {
               d3 = 0.6D;
            } else {
               d3 = 0.1D;
            }
         } else {
            if (!blockstate.isAir(world, blockpos) || !world.getBlockState(blockpos.down()).isIn(BlockTags.RAILS)) {
               return this.behaviourDefaultDispenseItem.dispense(p_82487_1_, p_82487_2_);
            }

            BlockState blockstate1 = world.getBlockState(blockpos.down());
            RailShape railshape1 = blockstate1.getBlock() instanceof AbstractRailBlock ? ((AbstractRailBlock)blockstate1.getBlock()).getRailDirection(blockstate1, world, blockpos.down(), (AbstractMinecartEntity)null) : RailShape.NORTH_SOUTH;
            if (direction != Direction.DOWN && railshape1.isAscending()) {
               d3 = -0.4D;
            } else {
               d3 = -0.9D;
            }
         }

         AbstractMinecartEntity abstractminecartentity = AbstractMinecartEntity.create(world, d0, d1 + d3, d2, ((MinecartItem)p_82487_2_.getItem()).minecartType);
         if (p_82487_2_.hasDisplayName()) {
            abstractminecartentity.setCustomName(p_82487_2_.getDisplayName());
         }

         world.addEntity(abstractminecartentity);
         p_82487_2_.shrink(1);
         return p_82487_2_;
      }

      protected void playDispenseSound(IBlockSource p_82485_1_) {
         p_82485_1_.getWorld().playEvent(1000, p_82485_1_.getBlockPos(), 0);
      }
   };
   private final AbstractMinecartEntity.Type minecartType;

   public MinecartItem(AbstractMinecartEntity.Type p_i48480_1_, Item.Properties p_i48480_2_) {
      super(p_i48480_2_);
      this.minecartType = p_i48480_1_;
      DispenserBlock.registerDispenseBehavior(this, MINECART_DISPENSER_BEHAVIOR);
   }

   public ActionResultType onItemUse(ItemUseContext p_195939_1_) {
      World world = p_195939_1_.getWorld();
      BlockPos blockpos = p_195939_1_.getPos();
      BlockState blockstate = world.getBlockState(blockpos);
      if (!blockstate.isIn(BlockTags.RAILS)) {
         return ActionResultType.FAIL;
      } else {
         ItemStack itemstack = p_195939_1_.getItem();
         if (!world.isRemote) {
            RailShape railshape = blockstate.getBlock() instanceof AbstractRailBlock ? ((AbstractRailBlock)blockstate.getBlock()).getRailDirection(blockstate, world, blockpos, (AbstractMinecartEntity)null) : RailShape.NORTH_SOUTH;
            double d0 = 0.0D;
            if (railshape.isAscending()) {
               d0 = 0.5D;
            }

            AbstractMinecartEntity abstractminecartentity = AbstractMinecartEntity.create(world, (double)blockpos.getX() + 0.5D, (double)blockpos.getY() + 0.0625D + d0, (double)blockpos.getZ() + 0.5D, this.minecartType);
            if (itemstack.hasDisplayName()) {
               abstractminecartentity.setCustomName(itemstack.getDisplayName());
            }

            world.addEntity(abstractminecartentity);
         }

         itemstack.shrink(1);
         return ActionResultType.SUCCESS;
      }
   }
}
