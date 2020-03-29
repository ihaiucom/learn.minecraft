package net.minecraft.tileentity;

import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.INameable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.LockCode;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

public abstract class LockableTileEntity extends TileEntity implements IInventory, INamedContainerProvider, INameable {
   private LockCode code;
   private ITextComponent customName;
   private LazyOptional<?> itemHandler;

   protected LockableTileEntity(TileEntityType<?> p_i48285_1_) {
      super(p_i48285_1_);
      this.code = LockCode.EMPTY_CODE;
      this.itemHandler = LazyOptional.of(() -> {
         return this.createUnSidedHandler();
      });
   }

   public void read(CompoundNBT p_145839_1_) {
      super.read(p_145839_1_);
      this.code = LockCode.read(p_145839_1_);
      if (p_145839_1_.contains("CustomName", 8)) {
         this.customName = ITextComponent.Serializer.fromJson(p_145839_1_.getString("CustomName"));
      }

   }

   public CompoundNBT write(CompoundNBT p_189515_1_) {
      super.write(p_189515_1_);
      this.code.write(p_189515_1_);
      if (this.customName != null) {
         p_189515_1_.putString("CustomName", ITextComponent.Serializer.toJson(this.customName));
      }

      return p_189515_1_;
   }

   public void setCustomName(ITextComponent p_213903_1_) {
      this.customName = p_213903_1_;
   }

   public ITextComponent getName() {
      return this.customName != null ? this.customName : this.getDefaultName();
   }

   public ITextComponent getDisplayName() {
      return this.getName();
   }

   @Nullable
   public ITextComponent getCustomName() {
      return this.customName;
   }

   protected abstract ITextComponent getDefaultName();

   public boolean canOpen(PlayerEntity p_213904_1_) {
      return canUnlock(p_213904_1_, this.code, this.getDisplayName());
   }

   public static boolean canUnlock(PlayerEntity p_213905_0_, LockCode p_213905_1_, ITextComponent p_213905_2_) {
      if (!p_213905_0_.isSpectator() && !p_213905_1_.func_219964_a(p_213905_0_.getHeldItemMainhand())) {
         p_213905_0_.sendStatusMessage(new TranslationTextComponent("container.isLocked", new Object[]{p_213905_2_}), true);
         p_213905_0_.func_213823_a(SoundEvents.BLOCK_CHEST_LOCKED, SoundCategory.BLOCKS, 1.0F, 1.0F);
         return false;
      } else {
         return true;
      }
   }

   @Nullable
   public Container createMenu(int p_createMenu_1_, PlayerInventory p_createMenu_2_, PlayerEntity p_createMenu_3_) {
      return this.canOpen(p_createMenu_3_) ? this.createMenu(p_createMenu_1_, p_createMenu_2_) : null;
   }

   protected abstract Container createMenu(int var1, PlayerInventory var2);

   protected IItemHandler createUnSidedHandler() {
      return new InvWrapper(this);
   }

   @Nullable
   public <T> LazyOptional<T> getCapability(Capability<T> p_getCapability_1_, @Nullable Direction p_getCapability_2_) {
      return !this.removed && p_getCapability_1_ == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? this.itemHandler.cast() : super.getCapability(p_getCapability_1_, p_getCapability_2_);
   }

   public void remove() {
      super.remove();
      this.itemHandler.invalidate();
   }
}
