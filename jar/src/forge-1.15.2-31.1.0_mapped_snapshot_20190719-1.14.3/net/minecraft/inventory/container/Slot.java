package net.minecraft.inventory.container;

import com.mojang.datafixers.util.Pair;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class Slot {
   private final int slotIndex;
   public final IInventory inventory;
   public int slotNumber;
   public final int xPos;
   public final int yPos;
   private Pair<ResourceLocation, ResourceLocation> backgroundPair;

   public Slot(IInventory p_i1824_1_, int p_i1824_2_, int p_i1824_3_, int p_i1824_4_) {
      this.inventory = p_i1824_1_;
      this.slotIndex = p_i1824_2_;
      this.xPos = p_i1824_3_;
      this.yPos = p_i1824_4_;
   }

   public void onSlotChange(ItemStack p_75220_1_, ItemStack p_75220_2_) {
      int i = p_75220_2_.getCount() - p_75220_1_.getCount();
      if (i > 0) {
         this.onCrafting(p_75220_2_, i);
      }

   }

   protected void onCrafting(ItemStack p_75210_1_, int p_75210_2_) {
   }

   protected void onSwapCraft(int p_190900_1_) {
   }

   protected void onCrafting(ItemStack p_75208_1_) {
   }

   public ItemStack onTake(PlayerEntity p_190901_1_, ItemStack p_190901_2_) {
      this.onSlotChanged();
      return p_190901_2_;
   }

   public boolean isItemValid(ItemStack p_75214_1_) {
      return true;
   }

   public ItemStack getStack() {
      return this.inventory.getStackInSlot(this.slotIndex);
   }

   public boolean getHasStack() {
      return !this.getStack().isEmpty();
   }

   public void putStack(ItemStack p_75215_1_) {
      this.inventory.setInventorySlotContents(this.slotIndex, p_75215_1_);
      this.onSlotChanged();
   }

   public void onSlotChanged() {
      this.inventory.markDirty();
   }

   public int getSlotStackLimit() {
      return this.inventory.getInventoryStackLimit();
   }

   public int getItemStackLimit(ItemStack p_178170_1_) {
      return this.getSlotStackLimit();
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public Pair<ResourceLocation, ResourceLocation> func_225517_c_() {
      return this.backgroundPair;
   }

   public ItemStack decrStackSize(int p_75209_1_) {
      return this.inventory.decrStackSize(this.slotIndex, p_75209_1_);
   }

   public boolean canTakeStack(PlayerEntity p_82869_1_) {
      return true;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isEnabled() {
      return true;
   }

   public int getSlotIndex() {
      return this.slotIndex;
   }

   public boolean isSameInventory(Slot p_isSameInventory_1_) {
      return this.inventory == p_isSameInventory_1_.inventory;
   }

   public Slot setBackground(ResourceLocation p_setBackground_1_, ResourceLocation p_setBackground_2_) {
      this.backgroundPair = Pair.of(p_setBackground_1_, p_setBackground_2_);
      return this;
   }
}
