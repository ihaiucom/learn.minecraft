package net.minecraft.tileentity;

import java.util.Random;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.DispenserContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class DispenserTileEntity extends LockableLootTileEntity {
   private static final Random RNG = new Random();
   private NonNullList<ItemStack> stacks;

   protected DispenserTileEntity(TileEntityType<?> p_i48286_1_) {
      super(p_i48286_1_);
      this.stacks = NonNullList.withSize(9, ItemStack.EMPTY);
   }

   public DispenserTileEntity() {
      this(TileEntityType.DISPENSER);
   }

   public int getSizeInventory() {
      return 9;
   }

   public int getDispenseSlot() {
      this.fillWithLoot((PlayerEntity)null);
      int lvt_1_1_ = -1;
      int lvt_2_1_ = 1;

      for(int lvt_3_1_ = 0; lvt_3_1_ < this.stacks.size(); ++lvt_3_1_) {
         if (!((ItemStack)this.stacks.get(lvt_3_1_)).isEmpty() && RNG.nextInt(lvt_2_1_++) == 0) {
            lvt_1_1_ = lvt_3_1_;
         }
      }

      return lvt_1_1_;
   }

   public int addItemStack(ItemStack p_146019_1_) {
      for(int lvt_2_1_ = 0; lvt_2_1_ < this.stacks.size(); ++lvt_2_1_) {
         if (((ItemStack)this.stacks.get(lvt_2_1_)).isEmpty()) {
            this.setInventorySlotContents(lvt_2_1_, p_146019_1_);
            return lvt_2_1_;
         }
      }

      return -1;
   }

   protected ITextComponent getDefaultName() {
      return new TranslationTextComponent("container.dispenser", new Object[0]);
   }

   public void read(CompoundNBT p_145839_1_) {
      super.read(p_145839_1_);
      this.stacks = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
      if (!this.checkLootAndRead(p_145839_1_)) {
         ItemStackHelper.loadAllItems(p_145839_1_, this.stacks);
      }

   }

   public CompoundNBT write(CompoundNBT p_189515_1_) {
      super.write(p_189515_1_);
      if (!this.checkLootAndWrite(p_189515_1_)) {
         ItemStackHelper.saveAllItems(p_189515_1_, this.stacks);
      }

      return p_189515_1_;
   }

   protected NonNullList<ItemStack> getItems() {
      return this.stacks;
   }

   protected void setItems(NonNullList<ItemStack> p_199721_1_) {
      this.stacks = p_199721_1_;
   }

   protected Container createMenu(int p_213906_1_, PlayerInventory p_213906_2_) {
      return new DispenserContainer(p_213906_1_, p_213906_2_, this);
   }
}
