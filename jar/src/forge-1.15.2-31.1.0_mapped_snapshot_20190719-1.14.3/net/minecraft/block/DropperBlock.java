package net.minecraft.block;

import net.minecraft.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.dispenser.IDispenseItemBehavior;
import net.minecraft.dispenser.ProxyBlockSource;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.DispenserTileEntity;
import net.minecraft.tileentity.DropperTileEntity;
import net.minecraft.tileentity.HopperTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.items.VanillaInventoryCodeHooks;

public class DropperBlock extends DispenserBlock {
   private static final IDispenseItemBehavior DISPENSE_BEHAVIOR = new DefaultDispenseItemBehavior();

   public DropperBlock(Block.Properties p_i48410_1_) {
      super(p_i48410_1_);
   }

   protected IDispenseItemBehavior getBehavior(ItemStack p_149940_1_) {
      return DISPENSE_BEHAVIOR;
   }

   public TileEntity createNewTileEntity(IBlockReader p_196283_1_) {
      return new DropperTileEntity();
   }

   protected void dispense(World p_176439_1_, BlockPos p_176439_2_) {
      ProxyBlockSource proxyblocksource = new ProxyBlockSource(p_176439_1_, p_176439_2_);
      DispenserTileEntity dispensertileentity = (DispenserTileEntity)proxyblocksource.getBlockTileEntity();
      int i = dispensertileentity.getDispenseSlot();
      if (i < 0) {
         p_176439_1_.playEvent(1001, p_176439_2_, 0);
      } else {
         ItemStack itemstack = dispensertileentity.getStackInSlot(i);
         if (!itemstack.isEmpty() && VanillaInventoryCodeHooks.dropperInsertHook(p_176439_1_, p_176439_2_, dispensertileentity, i, itemstack)) {
            Direction direction = (Direction)p_176439_1_.getBlockState(p_176439_2_).get(FACING);
            IInventory iinventory = HopperTileEntity.getInventoryAtPosition(p_176439_1_, p_176439_2_.offset(direction));
            ItemStack itemstack1;
            if (iinventory == null) {
               itemstack1 = DISPENSE_BEHAVIOR.dispense(proxyblocksource, itemstack);
            } else {
               itemstack1 = HopperTileEntity.putStackInInventoryAllSlots(dispensertileentity, iinventory, itemstack.copy().split(1), direction.getOpposite());
               if (itemstack1.isEmpty()) {
                  itemstack1 = itemstack.copy();
                  itemstack1.shrink(1);
               } else {
                  itemstack1 = itemstack.copy();
               }
            }

            dispensertileentity.setInventorySlotContents(i, itemstack1);
         }
      }

   }
}
