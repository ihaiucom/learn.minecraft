package net.minecraft.tileentity;

import net.minecraft.inventory.IClearable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

public class JukeboxTileEntity extends TileEntity implements IClearable {
   private ItemStack record;

   public JukeboxTileEntity() {
      super(TileEntityType.JUKEBOX);
      this.record = ItemStack.EMPTY;
   }

   public void read(CompoundNBT p_145839_1_) {
      super.read(p_145839_1_);
      if (p_145839_1_.contains("RecordItem", 10)) {
         this.setRecord(ItemStack.read(p_145839_1_.getCompound("RecordItem")));
      }

   }

   public CompoundNBT write(CompoundNBT p_189515_1_) {
      super.write(p_189515_1_);
      if (!this.getRecord().isEmpty()) {
         p_189515_1_.put("RecordItem", this.getRecord().write(new CompoundNBT()));
      }

      return p_189515_1_;
   }

   public ItemStack getRecord() {
      return this.record;
   }

   public void setRecord(ItemStack p_195535_1_) {
      this.record = p_195535_1_;
      this.markDirty();
   }

   public void clear() {
      this.setRecord(ItemStack.EMPTY);
   }
}
