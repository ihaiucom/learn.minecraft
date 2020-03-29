package net.minecraft.tileentity;

import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.HopperBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ISidedInventoryProvider;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.HopperContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.VanillaHopperItemHandler;
import net.minecraftforge.items.VanillaInventoryCodeHooks;

public class HopperTileEntity extends LockableLootTileEntity implements IHopper, ITickableTileEntity {
   private NonNullList<ItemStack> inventory;
   private int transferCooldown;
   private long tickedGameTime;

   public HopperTileEntity() {
      super(TileEntityType.HOPPER);
      this.inventory = NonNullList.withSize(5, ItemStack.EMPTY);
      this.transferCooldown = -1;
   }

   public void read(CompoundNBT p_145839_1_) {
      super.read(p_145839_1_);
      this.inventory = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
      if (!this.checkLootAndRead(p_145839_1_)) {
         ItemStackHelper.loadAllItems(p_145839_1_, this.inventory);
      }

      this.transferCooldown = p_145839_1_.getInt("TransferCooldown");
   }

   public CompoundNBT write(CompoundNBT p_189515_1_) {
      super.write(p_189515_1_);
      if (!this.checkLootAndWrite(p_189515_1_)) {
         ItemStackHelper.saveAllItems(p_189515_1_, this.inventory);
      }

      p_189515_1_.putInt("TransferCooldown", this.transferCooldown);
      return p_189515_1_;
   }

   public int getSizeInventory() {
      return this.inventory.size();
   }

   public ItemStack decrStackSize(int p_70298_1_, int p_70298_2_) {
      this.fillWithLoot((PlayerEntity)null);
      return ItemStackHelper.getAndSplit(this.getItems(), p_70298_1_, p_70298_2_);
   }

   public void setInventorySlotContents(int p_70299_1_, ItemStack p_70299_2_) {
      this.fillWithLoot((PlayerEntity)null);
      this.getItems().set(p_70299_1_, p_70299_2_);
      if (p_70299_2_.getCount() > this.getInventoryStackLimit()) {
         p_70299_2_.setCount(this.getInventoryStackLimit());
      }

   }

   protected ITextComponent getDefaultName() {
      return new TranslationTextComponent("container.hopper", new Object[0]);
   }

   public void tick() {
      if (this.world != null && !this.world.isRemote) {
         --this.transferCooldown;
         this.tickedGameTime = this.world.getGameTime();
         if (!this.isOnTransferCooldown()) {
            this.setTransferCooldown(0);
            this.updateHopper(() -> {
               return pullItems(this);
            });
         }
      }

   }

   private boolean updateHopper(Supplier<Boolean> p_200109_1_) {
      if (this.world != null && !this.world.isRemote) {
         if (!this.isOnTransferCooldown() && (Boolean)this.getBlockState().get(HopperBlock.ENABLED)) {
            boolean flag = false;
            if (!this.isEmpty()) {
               flag = this.transferItemsOut();
            }

            if (!this.isFull()) {
               flag |= (Boolean)p_200109_1_.get();
            }

            if (flag) {
               this.setTransferCooldown(8);
               this.markDirty();
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private boolean isFull() {
      Iterator var1 = this.inventory.iterator();

      ItemStack itemstack;
      do {
         if (!var1.hasNext()) {
            return true;
         }

         itemstack = (ItemStack)var1.next();
      } while(!itemstack.isEmpty() && itemstack.getCount() == itemstack.getMaxStackSize());

      return false;
   }

   private boolean transferItemsOut() {
      if (VanillaInventoryCodeHooks.insertHook(this)) {
         return true;
      } else {
         IInventory iinventory = this.getInventoryForHopperTransfer();
         if (iinventory == null) {
            return false;
         } else {
            Direction direction = ((Direction)this.getBlockState().get(HopperBlock.FACING)).getOpposite();
            if (this.isInventoryFull(iinventory, direction)) {
               return false;
            } else {
               for(int i = 0; i < this.getSizeInventory(); ++i) {
                  if (!this.getStackInSlot(i).isEmpty()) {
                     ItemStack itemstack = this.getStackInSlot(i).copy();
                     ItemStack itemstack1 = putStackInInventoryAllSlots(this, iinventory, this.decrStackSize(i, 1), direction);
                     if (itemstack1.isEmpty()) {
                        iinventory.markDirty();
                        return true;
                     }

                     this.setInventorySlotContents(i, itemstack);
                  }
               }

               return false;
            }
         }
      }
   }

   private static IntStream func_213972_a(IInventory p_213972_0_, Direction p_213972_1_) {
      return p_213972_0_ instanceof ISidedInventory ? IntStream.of(((ISidedInventory)p_213972_0_).getSlotsForFace(p_213972_1_)) : IntStream.range(0, p_213972_0_.getSizeInventory());
   }

   private boolean isInventoryFull(IInventory p_174919_1_, Direction p_174919_2_) {
      return func_213972_a(p_174919_1_, p_174919_2_).allMatch((p_lambda$isInventoryFull$1_1_) -> {
         ItemStack itemstack = p_174919_1_.getStackInSlot(p_lambda$isInventoryFull$1_1_);
         return itemstack.getCount() >= itemstack.getMaxStackSize();
      });
   }

   private static boolean isInventoryEmpty(IInventory p_174917_0_, Direction p_174917_1_) {
      return func_213972_a(p_174917_0_, p_174917_1_).allMatch((p_lambda$isInventoryEmpty$2_1_) -> {
         return p_174917_0_.getStackInSlot(p_lambda$isInventoryEmpty$2_1_).isEmpty();
      });
   }

   public static boolean pullItems(IHopper p_145891_0_) {
      Boolean ret = VanillaInventoryCodeHooks.extractHook(p_145891_0_);
      if (ret != null) {
         return ret;
      } else {
         IInventory iinventory = getSourceInventory(p_145891_0_);
         if (iinventory != null) {
            Direction direction = Direction.DOWN;
            return isInventoryEmpty(iinventory, direction) ? false : func_213972_a(iinventory, direction).anyMatch((p_lambda$pullItems$3_3_) -> {
               return pullItemFromSlot(p_145891_0_, iinventory, p_lambda$pullItems$3_3_, direction);
            });
         } else {
            Iterator var3 = getCaptureItems(p_145891_0_).iterator();

            ItemEntity itementity;
            do {
               if (!var3.hasNext()) {
                  return false;
               }

               itementity = (ItemEntity)var3.next();
            } while(!captureItem(p_145891_0_, itementity));

            return true;
         }
      }
   }

   private static boolean pullItemFromSlot(IHopper p_174915_0_, IInventory p_174915_1_, int p_174915_2_, Direction p_174915_3_) {
      ItemStack itemstack = p_174915_1_.getStackInSlot(p_174915_2_);
      if (!itemstack.isEmpty() && canExtractItemFromSlot(p_174915_1_, itemstack, p_174915_2_, p_174915_3_)) {
         ItemStack itemstack1 = itemstack.copy();
         ItemStack itemstack2 = putStackInInventoryAllSlots(p_174915_1_, p_174915_0_, p_174915_1_.decrStackSize(p_174915_2_, 1), (Direction)null);
         if (itemstack2.isEmpty()) {
            p_174915_1_.markDirty();
            return true;
         }

         p_174915_1_.setInventorySlotContents(p_174915_2_, itemstack1);
      }

      return false;
   }

   public static boolean captureItem(IInventory p_200114_0_, ItemEntity p_200114_1_) {
      boolean flag = false;
      ItemStack itemstack = p_200114_1_.getItem().copy();
      ItemStack itemstack1 = putStackInInventoryAllSlots((IInventory)null, p_200114_0_, itemstack, (Direction)null);
      if (itemstack1.isEmpty()) {
         flag = true;
         p_200114_1_.remove();
      } else {
         p_200114_1_.setItem(itemstack1);
      }

      return flag;
   }

   public static ItemStack putStackInInventoryAllSlots(@Nullable IInventory p_174918_0_, IInventory p_174918_1_, ItemStack p_174918_2_, @Nullable Direction p_174918_3_) {
      if (p_174918_1_ instanceof ISidedInventory && p_174918_3_ != null) {
         ISidedInventory isidedinventory = (ISidedInventory)p_174918_1_;
         int[] aint = isidedinventory.getSlotsForFace(p_174918_3_);

         for(int k = 0; k < aint.length && !p_174918_2_.isEmpty(); ++k) {
            p_174918_2_ = insertStack(p_174918_0_, p_174918_1_, p_174918_2_, aint[k], p_174918_3_);
         }
      } else {
         int i = p_174918_1_.getSizeInventory();

         for(int j = 0; j < i && !p_174918_2_.isEmpty(); ++j) {
            p_174918_2_ = insertStack(p_174918_0_, p_174918_1_, p_174918_2_, j, p_174918_3_);
         }
      }

      return p_174918_2_;
   }

   private static boolean canInsertItemInSlot(IInventory p_174920_0_, ItemStack p_174920_1_, int p_174920_2_, @Nullable Direction p_174920_3_) {
      if (!p_174920_0_.isItemValidForSlot(p_174920_2_, p_174920_1_)) {
         return false;
      } else {
         return !(p_174920_0_ instanceof ISidedInventory) || ((ISidedInventory)p_174920_0_).canInsertItem(p_174920_2_, p_174920_1_, p_174920_3_);
      }
   }

   private static boolean canExtractItemFromSlot(IInventory p_174921_0_, ItemStack p_174921_1_, int p_174921_2_, Direction p_174921_3_) {
      return !(p_174921_0_ instanceof ISidedInventory) || ((ISidedInventory)p_174921_0_).canExtractItem(p_174921_2_, p_174921_1_, p_174921_3_);
   }

   private static ItemStack insertStack(@Nullable IInventory p_174916_0_, IInventory p_174916_1_, ItemStack p_174916_2_, int p_174916_3_, @Nullable Direction p_174916_4_) {
      ItemStack itemstack = p_174916_1_.getStackInSlot(p_174916_3_);
      if (canInsertItemInSlot(p_174916_1_, p_174916_2_, p_174916_3_, p_174916_4_)) {
         boolean flag = false;
         boolean flag1 = p_174916_1_.isEmpty();
         if (itemstack.isEmpty()) {
            p_174916_1_.setInventorySlotContents(p_174916_3_, p_174916_2_);
            p_174916_2_ = ItemStack.EMPTY;
            flag = true;
         } else if (canCombine(itemstack, p_174916_2_)) {
            int i = p_174916_2_.getMaxStackSize() - itemstack.getCount();
            int j = Math.min(p_174916_2_.getCount(), i);
            p_174916_2_.shrink(j);
            itemstack.grow(j);
            flag = j > 0;
         }

         if (flag) {
            if (flag1 && p_174916_1_ instanceof HopperTileEntity) {
               HopperTileEntity hoppertileentity1 = (HopperTileEntity)p_174916_1_;
               if (!hoppertileentity1.mayTransfer()) {
                  int k = 0;
                  if (p_174916_0_ instanceof HopperTileEntity) {
                     HopperTileEntity hoppertileentity = (HopperTileEntity)p_174916_0_;
                     if (hoppertileentity1.tickedGameTime >= hoppertileentity.tickedGameTime) {
                        k = 1;
                     }
                  }

                  hoppertileentity1.setTransferCooldown(8 - k);
               }
            }

            p_174916_1_.markDirty();
         }
      }

      return p_174916_2_;
   }

   @Nullable
   private IInventory getInventoryForHopperTransfer() {
      Direction direction = (Direction)this.getBlockState().get(HopperBlock.FACING);
      return getInventoryAtPosition(this.getWorld(), this.pos.offset(direction));
   }

   @Nullable
   public static IInventory getSourceInventory(IHopper p_145884_0_) {
      return getInventoryAtPosition(p_145884_0_.getWorld(), p_145884_0_.getXPos(), p_145884_0_.getYPos() + 1.0D, p_145884_0_.getZPos());
   }

   public static List<ItemEntity> getCaptureItems(IHopper p_200115_0_) {
      return (List)p_200115_0_.getCollectionArea().toBoundingBoxList().stream().flatMap((p_lambda$getCaptureItems$4_1_) -> {
         return p_200115_0_.getWorld().getEntitiesWithinAABB(ItemEntity.class, p_lambda$getCaptureItems$4_1_.offset(p_200115_0_.getXPos() - 0.5D, p_200115_0_.getYPos() - 0.5D, p_200115_0_.getZPos() - 0.5D), EntityPredicates.IS_ALIVE).stream();
      }).collect(Collectors.toList());
   }

   @Nullable
   public static IInventory getInventoryAtPosition(World p_195484_0_, BlockPos p_195484_1_) {
      return getInventoryAtPosition(p_195484_0_, (double)p_195484_1_.getX() + 0.5D, (double)p_195484_1_.getY() + 0.5D, (double)p_195484_1_.getZ() + 0.5D);
   }

   @Nullable
   public static IInventory getInventoryAtPosition(World p_145893_0_, double p_145893_1_, double p_145893_3_, double p_145893_5_) {
      IInventory iinventory = null;
      BlockPos blockpos = new BlockPos(p_145893_1_, p_145893_3_, p_145893_5_);
      BlockState blockstate = p_145893_0_.getBlockState(blockpos);
      Block block = blockstate.getBlock();
      if (block instanceof ISidedInventoryProvider) {
         iinventory = ((ISidedInventoryProvider)block).createInventory(blockstate, p_145893_0_, blockpos);
      } else if (blockstate.hasTileEntity()) {
         TileEntity tileentity = p_145893_0_.getTileEntity(blockpos);
         if (tileentity instanceof IInventory) {
            iinventory = (IInventory)tileentity;
            if (iinventory instanceof ChestTileEntity && block instanceof ChestBlock) {
               iinventory = ChestBlock.func_226916_a_((ChestBlock)block, blockstate, p_145893_0_, blockpos, true);
            }
         }
      }

      if (iinventory == null) {
         List<Entity> list = p_145893_0_.getEntitiesInAABBexcluding((Entity)null, new AxisAlignedBB(p_145893_1_ - 0.5D, p_145893_3_ - 0.5D, p_145893_5_ - 0.5D, p_145893_1_ + 0.5D, p_145893_3_ + 0.5D, p_145893_5_ + 0.5D), EntityPredicates.HAS_INVENTORY);
         if (!list.isEmpty()) {
            iinventory = (IInventory)list.get(p_145893_0_.rand.nextInt(list.size()));
         }
      }

      return (IInventory)iinventory;
   }

   private static boolean canCombine(ItemStack p_145894_0_, ItemStack p_145894_1_) {
      if (p_145894_0_.getItem() != p_145894_1_.getItem()) {
         return false;
      } else if (p_145894_0_.getDamage() != p_145894_1_.getDamage()) {
         return false;
      } else {
         return p_145894_0_.getCount() > p_145894_0_.getMaxStackSize() ? false : ItemStack.areItemStackTagsEqual(p_145894_0_, p_145894_1_);
      }
   }

   public double getXPos() {
      return (double)this.pos.getX() + 0.5D;
   }

   public double getYPos() {
      return (double)this.pos.getY() + 0.5D;
   }

   public double getZPos() {
      return (double)this.pos.getZ() + 0.5D;
   }

   public void setTransferCooldown(int p_145896_1_) {
      this.transferCooldown = p_145896_1_;
   }

   private boolean isOnTransferCooldown() {
      return this.transferCooldown > 0;
   }

   public boolean mayTransfer() {
      return this.transferCooldown > 8;
   }

   protected NonNullList<ItemStack> getItems() {
      return this.inventory;
   }

   protected void setItems(NonNullList<ItemStack> p_199721_1_) {
      this.inventory = p_199721_1_;
   }

   public void onEntityCollision(Entity p_200113_1_) {
      if (p_200113_1_ instanceof ItemEntity) {
         BlockPos blockpos = this.getPos();
         if (VoxelShapes.compare(VoxelShapes.create(p_200113_1_.getBoundingBox().offset((double)(-blockpos.getX()), (double)(-blockpos.getY()), (double)(-blockpos.getZ()))), this.getCollectionArea(), IBooleanFunction.AND)) {
            this.updateHopper(() -> {
               return captureItem(this, (ItemEntity)p_200113_1_);
            });
         }
      }

   }

   protected Container createMenu(int p_213906_1_, PlayerInventory p_213906_2_) {
      return new HopperContainer(p_213906_1_, p_213906_2_, this);
   }

   protected IItemHandler createUnSidedHandler() {
      return new VanillaHopperItemHandler(this);
   }

   public long getLastUpdateTime() {
      return this.tickedGameTime;
   }
}
