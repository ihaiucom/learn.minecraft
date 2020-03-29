package net.minecraft.tileentity;

import java.util.Arrays;
import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.BrewingStandBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.BrewingStandContainer;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.PotionBrewing;
import net.minecraft.util.Direction;
import net.minecraft.util.IIntArray;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

public class BrewingStandTileEntity extends LockableTileEntity implements ISidedInventory, ITickableTileEntity {
   private static final int[] SLOTS_FOR_UP = new int[]{3};
   private static final int[] SLOTS_FOR_DOWN = new int[]{0, 1, 2, 3};
   private static final int[] OUTPUT_SLOTS = new int[]{0, 1, 2, 4};
   private NonNullList<ItemStack> brewingItemStacks;
   private int brewTime;
   private boolean[] filledSlots;
   private Item ingredientID;
   private int fuel;
   protected final IIntArray field_213954_a;
   LazyOptional<? extends IItemHandler>[] handlers;

   public BrewingStandTileEntity() {
      super(TileEntityType.BREWING_STAND);
      this.brewingItemStacks = NonNullList.withSize(5, ItemStack.EMPTY);
      this.field_213954_a = new IIntArray() {
         public int get(int p_221476_1_) {
            switch(p_221476_1_) {
            case 0:
               return BrewingStandTileEntity.this.brewTime;
            case 1:
               return BrewingStandTileEntity.this.fuel;
            default:
               return 0;
            }
         }

         public void set(int p_221477_1_, int p_221477_2_) {
            switch(p_221477_1_) {
            case 0:
               BrewingStandTileEntity.this.brewTime = p_221477_2_;
               break;
            case 1:
               BrewingStandTileEntity.this.fuel = p_221477_2_;
            }

         }

         public int size() {
            return 2;
         }
      };
      this.handlers = SidedInvWrapper.create(this, Direction.UP, Direction.DOWN, Direction.NORTH);
   }

   protected ITextComponent getDefaultName() {
      return new TranslationTextComponent("container.brewing", new Object[0]);
   }

   public int getSizeInventory() {
      return this.brewingItemStacks.size();
   }

   public boolean isEmpty() {
      Iterator var1 = this.brewingItemStacks.iterator();

      ItemStack itemstack;
      do {
         if (!var1.hasNext()) {
            return true;
         }

         itemstack = (ItemStack)var1.next();
      } while(itemstack.isEmpty());

      return false;
   }

   public void tick() {
      ItemStack itemstack = (ItemStack)this.brewingItemStacks.get(4);
      if (this.fuel <= 0 && itemstack.getItem() == Items.BLAZE_POWDER) {
         this.fuel = 20;
         itemstack.shrink(1);
         this.markDirty();
      }

      boolean flag = this.canBrew();
      boolean flag1 = this.brewTime > 0;
      ItemStack itemstack1 = (ItemStack)this.brewingItemStacks.get(3);
      if (flag1) {
         --this.brewTime;
         boolean flag2 = this.brewTime == 0;
         if (flag2 && flag) {
            this.brewPotions();
            this.markDirty();
         } else if (!flag) {
            this.brewTime = 0;
            this.markDirty();
         } else if (this.ingredientID != itemstack1.getItem()) {
            this.brewTime = 0;
            this.markDirty();
         }
      } else if (flag && this.fuel > 0) {
         --this.fuel;
         this.brewTime = 400;
         this.ingredientID = itemstack1.getItem();
         this.markDirty();
      }

      if (!this.world.isRemote) {
         boolean[] aboolean = this.createFilledSlotsArray();
         if (!Arrays.equals(aboolean, this.filledSlots)) {
            this.filledSlots = aboolean;
            BlockState blockstate = this.world.getBlockState(this.getPos());
            if (!(blockstate.getBlock() instanceof BrewingStandBlock)) {
               return;
            }

            for(int i = 0; i < BrewingStandBlock.HAS_BOTTLE.length; ++i) {
               blockstate = (BlockState)blockstate.with(BrewingStandBlock.HAS_BOTTLE[i], aboolean[i]);
            }

            this.world.setBlockState(this.pos, blockstate, 2);
         }
      }

   }

   public boolean[] createFilledSlotsArray() {
      boolean[] aboolean = new boolean[3];

      for(int i = 0; i < 3; ++i) {
         if (!((ItemStack)this.brewingItemStacks.get(i)).isEmpty()) {
            aboolean[i] = true;
         }
      }

      return aboolean;
   }

   private boolean canBrew() {
      ItemStack itemstack = (ItemStack)this.brewingItemStacks.get(3);
      if (!itemstack.isEmpty()) {
         return BrewingRecipeRegistry.canBrew(this.brewingItemStacks, itemstack, OUTPUT_SLOTS);
      } else if (itemstack.isEmpty()) {
         return false;
      } else if (!PotionBrewing.isReagent(itemstack)) {
         return false;
      } else {
         for(int i = 0; i < 3; ++i) {
            ItemStack itemstack1 = (ItemStack)this.brewingItemStacks.get(i);
            if (!itemstack1.isEmpty() && PotionBrewing.hasConversions(itemstack1, itemstack)) {
               return true;
            }
         }

         return false;
      }
   }

   private void brewPotions() {
      if (!ForgeEventFactory.onPotionAttemptBrew(this.brewingItemStacks)) {
         ItemStack itemstack = (ItemStack)this.brewingItemStacks.get(3);
         BrewingRecipeRegistry.brewPotions(this.brewingItemStacks, itemstack, OUTPUT_SLOTS);
         itemstack.shrink(1);
         ForgeEventFactory.onPotionBrewed(this.brewingItemStacks);
         BlockPos blockpos = this.getPos();
         if (itemstack.hasContainerItem()) {
            ItemStack itemstack1 = itemstack.getContainerItem();
            if (itemstack.isEmpty()) {
               itemstack = itemstack1;
            } else if (!this.world.isRemote) {
               InventoryHelper.spawnItemStack(this.world, (double)blockpos.getX(), (double)blockpos.getY(), (double)blockpos.getZ(), itemstack1);
            }
         }

         this.brewingItemStacks.set(3, itemstack);
         this.world.playEvent(1035, blockpos, 0);
      }
   }

   public void read(CompoundNBT p_145839_1_) {
      super.read(p_145839_1_);
      this.brewingItemStacks = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
      ItemStackHelper.loadAllItems(p_145839_1_, this.brewingItemStacks);
      this.brewTime = p_145839_1_.getShort("BrewTime");
      this.fuel = p_145839_1_.getByte("Fuel");
   }

   public CompoundNBT write(CompoundNBT p_189515_1_) {
      super.write(p_189515_1_);
      p_189515_1_.putShort("BrewTime", (short)this.brewTime);
      ItemStackHelper.saveAllItems(p_189515_1_, this.brewingItemStacks);
      p_189515_1_.putByte("Fuel", (byte)this.fuel);
      return p_189515_1_;
   }

   public ItemStack getStackInSlot(int p_70301_1_) {
      return p_70301_1_ >= 0 && p_70301_1_ < this.brewingItemStacks.size() ? (ItemStack)this.brewingItemStacks.get(p_70301_1_) : ItemStack.EMPTY;
   }

   public ItemStack decrStackSize(int p_70298_1_, int p_70298_2_) {
      return ItemStackHelper.getAndSplit(this.brewingItemStacks, p_70298_1_, p_70298_2_);
   }

   public ItemStack removeStackFromSlot(int p_70304_1_) {
      return ItemStackHelper.getAndRemove(this.brewingItemStacks, p_70304_1_);
   }

   public void setInventorySlotContents(int p_70299_1_, ItemStack p_70299_2_) {
      if (p_70299_1_ >= 0 && p_70299_1_ < this.brewingItemStacks.size()) {
         this.brewingItemStacks.set(p_70299_1_, p_70299_2_);
      }

   }

   public boolean isUsableByPlayer(PlayerEntity p_70300_1_) {
      if (this.world.getTileEntity(this.pos) != this) {
         return false;
      } else {
         return p_70300_1_.getDistanceSq((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) <= 64.0D;
      }
   }

   public boolean isItemValidForSlot(int p_94041_1_, ItemStack p_94041_2_) {
      if (p_94041_1_ == 3) {
         return BrewingRecipeRegistry.isValidIngredient(p_94041_2_);
      } else {
         Item item = p_94041_2_.getItem();
         if (p_94041_1_ == 4) {
            return item == Items.BLAZE_POWDER;
         } else {
            return BrewingRecipeRegistry.isValidInput(p_94041_2_) && this.getStackInSlot(p_94041_1_).isEmpty();
         }
      }
   }

   public int[] getSlotsForFace(Direction p_180463_1_) {
      if (p_180463_1_ == Direction.UP) {
         return SLOTS_FOR_UP;
      } else {
         return p_180463_1_ == Direction.DOWN ? SLOTS_FOR_DOWN : OUTPUT_SLOTS;
      }
   }

   public boolean canInsertItem(int p_180462_1_, ItemStack p_180462_2_, @Nullable Direction p_180462_3_) {
      return this.isItemValidForSlot(p_180462_1_, p_180462_2_);
   }

   public boolean canExtractItem(int p_180461_1_, ItemStack p_180461_2_, Direction p_180461_3_) {
      if (p_180461_1_ == 3) {
         return p_180461_2_.getItem() == Items.GLASS_BOTTLE;
      } else {
         return true;
      }
   }

   public void clear() {
      this.brewingItemStacks.clear();
   }

   protected Container createMenu(int p_213906_1_, PlayerInventory p_213906_2_) {
      return new BrewingStandContainer(p_213906_1_, p_213906_2_, this, this.field_213954_a);
   }

   public <T> LazyOptional<T> getCapability(Capability<T> p_getCapability_1_, @Nullable Direction p_getCapability_2_) {
      if (!this.removed && p_getCapability_2_ != null && p_getCapability_1_ == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
         if (p_getCapability_2_ == Direction.UP) {
            return this.handlers[0].cast();
         } else {
            return p_getCapability_2_ == Direction.DOWN ? this.handlers[1].cast() : this.handlers[2].cast();
         }
      } else {
         return super.getCapability(p_getCapability_1_, p_getCapability_2_);
      }
   }

   public void remove() {
      super.remove();

      for(int x = 0; x < this.handlers.length; ++x) {
         this.handlers[x].invalidate();
      }

   }
}
