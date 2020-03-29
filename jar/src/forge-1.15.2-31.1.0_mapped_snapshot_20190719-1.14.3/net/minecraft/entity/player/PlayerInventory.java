package net.minecraft.entity.player;

import com.google.common.collect.ImmutableList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.block.BlockState;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipeItemHelper;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.play.server.SSetSlotPacket;
import net.minecraft.tags.Tag;
import net.minecraft.util.INameable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class PlayerInventory implements IInventory, INameable {
   public final NonNullList<ItemStack> mainInventory;
   public final NonNullList<ItemStack> armorInventory;
   public final NonNullList<ItemStack> offHandInventory;
   private final List<NonNullList<ItemStack>> allInventories;
   public int currentItem;
   public final PlayerEntity player;
   private ItemStack itemStack;
   private int timesChanged;

   public PlayerInventory(PlayerEntity p_i1750_1_) {
      this.mainInventory = NonNullList.withSize(36, ItemStack.EMPTY);
      this.armorInventory = NonNullList.withSize(4, ItemStack.EMPTY);
      this.offHandInventory = NonNullList.withSize(1, ItemStack.EMPTY);
      this.allInventories = ImmutableList.of(this.mainInventory, this.armorInventory, this.offHandInventory);
      this.itemStack = ItemStack.EMPTY;
      this.player = p_i1750_1_;
   }

   public ItemStack getCurrentItem() {
      return isHotbar(this.currentItem) ? (ItemStack)this.mainInventory.get(this.currentItem) : ItemStack.EMPTY;
   }

   public static int getHotbarSize() {
      return 9;
   }

   private boolean canMergeStacks(ItemStack p_184436_1_, ItemStack p_184436_2_) {
      return !p_184436_1_.isEmpty() && this.stackEqualExact(p_184436_1_, p_184436_2_) && p_184436_1_.isStackable() && p_184436_1_.getCount() < p_184436_1_.getMaxStackSize() && p_184436_1_.getCount() < this.getInventoryStackLimit();
   }

   private boolean stackEqualExact(ItemStack p_184431_1_, ItemStack p_184431_2_) {
      return p_184431_1_.getItem() == p_184431_2_.getItem() && ItemStack.areItemStackTagsEqual(p_184431_1_, p_184431_2_);
   }

   public int getFirstEmptyStack() {
      for(int i = 0; i < this.mainInventory.size(); ++i) {
         if (((ItemStack)this.mainInventory.get(i)).isEmpty()) {
            return i;
         }
      }

      return -1;
   }

   @OnlyIn(Dist.CLIENT)
   public void setPickedItemStack(ItemStack p_184434_1_) {
      int i = this.getSlotFor(p_184434_1_);
      if (isHotbar(i)) {
         this.currentItem = i;
      } else if (i == -1) {
         this.currentItem = this.getBestHotbarSlot();
         if (!((ItemStack)this.mainInventory.get(this.currentItem)).isEmpty()) {
            int j = this.getFirstEmptyStack();
            if (j != -1) {
               this.mainInventory.set(j, this.mainInventory.get(this.currentItem));
            }
         }

         this.mainInventory.set(this.currentItem, p_184434_1_);
      } else {
         this.pickItem(i);
      }

   }

   public void pickItem(int p_184430_1_) {
      this.currentItem = this.getBestHotbarSlot();
      ItemStack itemstack = (ItemStack)this.mainInventory.get(this.currentItem);
      this.mainInventory.set(this.currentItem, this.mainInventory.get(p_184430_1_));
      this.mainInventory.set(p_184430_1_, itemstack);
   }

   public static boolean isHotbar(int p_184435_0_) {
      return p_184435_0_ >= 0 && p_184435_0_ < 9;
   }

   @OnlyIn(Dist.CLIENT)
   public int getSlotFor(ItemStack p_184429_1_) {
      for(int i = 0; i < this.mainInventory.size(); ++i) {
         if (!((ItemStack)this.mainInventory.get(i)).isEmpty() && this.stackEqualExact(p_184429_1_, (ItemStack)this.mainInventory.get(i))) {
            return i;
         }
      }

      return -1;
   }

   public int findSlotMatchingUnusedItem(ItemStack p_194014_1_) {
      for(int i = 0; i < this.mainInventory.size(); ++i) {
         ItemStack itemstack = (ItemStack)this.mainInventory.get(i);
         if (!((ItemStack)this.mainInventory.get(i)).isEmpty() && this.stackEqualExact(p_194014_1_, (ItemStack)this.mainInventory.get(i)) && !((ItemStack)this.mainInventory.get(i)).isDamaged() && !itemstack.isEnchanted() && !itemstack.hasDisplayName()) {
            return i;
         }
      }

      return -1;
   }

   public int getBestHotbarSlot() {
      int k;
      int l;
      for(k = 0; k < 9; ++k) {
         l = (this.currentItem + k) % 9;
         if (((ItemStack)this.mainInventory.get(l)).isEmpty()) {
            return l;
         }
      }

      for(k = 0; k < 9; ++k) {
         l = (this.currentItem + k) % 9;
         if (!((ItemStack)this.mainInventory.get(l)).isEnchanted()) {
            return l;
         }
      }

      return this.currentItem;
   }

   @OnlyIn(Dist.CLIENT)
   public void changeCurrentItem(double p_195409_1_) {
      if (p_195409_1_ > 0.0D) {
         p_195409_1_ = 1.0D;
      }

      if (p_195409_1_ < 0.0D) {
         p_195409_1_ = -1.0D;
      }

      for(this.currentItem = (int)((double)this.currentItem - p_195409_1_); this.currentItem < 0; this.currentItem += 9) {
      }

      while(this.currentItem >= 9) {
         this.currentItem -= 9;
      }

   }

   public int clearMatchingItems(Predicate<ItemStack> p_195408_1_, int p_195408_2_) {
      int i = 0;

      int j;
      for(j = 0; j < this.getSizeInventory(); ++j) {
         ItemStack itemstack = this.getStackInSlot(j);
         if (!itemstack.isEmpty() && p_195408_1_.test(itemstack)) {
            int k = p_195408_2_ <= 0 ? itemstack.getCount() : Math.min(p_195408_2_ - i, itemstack.getCount());
            i += k;
            if (p_195408_2_ != 0) {
               itemstack.shrink(k);
               if (itemstack.isEmpty()) {
                  this.setInventorySlotContents(j, ItemStack.EMPTY);
               }

               if (p_195408_2_ > 0 && i >= p_195408_2_) {
                  return i;
               }
            }
         }
      }

      if (!this.itemStack.isEmpty() && p_195408_1_.test(this.itemStack)) {
         j = p_195408_2_ <= 0 ? this.itemStack.getCount() : Math.min(p_195408_2_ - i, this.itemStack.getCount());
         i += j;
         if (p_195408_2_ != 0) {
            this.itemStack.shrink(j);
            if (this.itemStack.isEmpty()) {
               this.itemStack = ItemStack.EMPTY;
            }

            if (p_195408_2_ > 0 && i >= p_195408_2_) {
               return i;
            }
         }
      }

      return i;
   }

   private int storePartialItemStack(ItemStack p_70452_1_) {
      int i = this.storeItemStack(p_70452_1_);
      if (i == -1) {
         i = this.getFirstEmptyStack();
      }

      return i == -1 ? p_70452_1_.getCount() : this.addResource(i, p_70452_1_);
   }

   private int addResource(int p_191973_1_, ItemStack p_191973_2_) {
      Item item = p_191973_2_.getItem();
      int i = p_191973_2_.getCount();
      ItemStack itemstack = this.getStackInSlot(p_191973_1_);
      if (itemstack.isEmpty()) {
         itemstack = p_191973_2_.copy();
         itemstack.setCount(0);
         if (p_191973_2_.hasTag()) {
            itemstack.setTag(p_191973_2_.getTag().copy());
         }

         this.setInventorySlotContents(p_191973_1_, itemstack);
      }

      int j = i;
      if (i > itemstack.getMaxStackSize() - itemstack.getCount()) {
         j = itemstack.getMaxStackSize() - itemstack.getCount();
      }

      if (j > this.getInventoryStackLimit() - itemstack.getCount()) {
         j = this.getInventoryStackLimit() - itemstack.getCount();
      }

      if (j == 0) {
         return i;
      } else {
         i -= j;
         itemstack.grow(j);
         itemstack.setAnimationsToGo(5);
         return i;
      }
   }

   public int storeItemStack(ItemStack p_70432_1_) {
      if (this.canMergeStacks(this.getStackInSlot(this.currentItem), p_70432_1_)) {
         return this.currentItem;
      } else if (this.canMergeStacks(this.getStackInSlot(40), p_70432_1_)) {
         return 40;
      } else {
         for(int i = 0; i < this.mainInventory.size(); ++i) {
            if (this.canMergeStacks((ItemStack)this.mainInventory.get(i), p_70432_1_)) {
               return i;
            }
         }

         return -1;
      }
   }

   public void tick() {
      Iterator var1 = this.allInventories.iterator();

      while(var1.hasNext()) {
         NonNullList<ItemStack> nonnulllist = (NonNullList)var1.next();

         for(int i = 0; i < nonnulllist.size(); ++i) {
            if (!((ItemStack)nonnulllist.get(i)).isEmpty()) {
               ((ItemStack)nonnulllist.get(i)).inventoryTick(this.player.world, this.player, i, this.currentItem == i);
            }
         }
      }

      this.armorInventory.forEach((p_lambda$tick$0_1_) -> {
         p_lambda$tick$0_1_.onArmorTick(this.player.world, this.player);
      });
   }

   public boolean addItemStackToInventory(ItemStack p_70441_1_) {
      return this.add(-1, p_70441_1_);
   }

   public boolean add(int p_191971_1_, ItemStack p_191971_2_) {
      if (p_191971_2_.isEmpty()) {
         return false;
      } else {
         try {
            if (p_191971_2_.isDamaged()) {
               if (p_191971_1_ == -1) {
                  p_191971_1_ = this.getFirstEmptyStack();
               }

               if (p_191971_1_ >= 0) {
                  this.mainInventory.set(p_191971_1_, p_191971_2_.copy());
                  ((ItemStack)this.mainInventory.get(p_191971_1_)).setAnimationsToGo(5);
                  p_191971_2_.setCount(0);
                  return true;
               } else if (this.player.abilities.isCreativeMode) {
                  p_191971_2_.setCount(0);
                  return true;
               } else {
                  return false;
               }
            } else {
               int i;
               do {
                  i = p_191971_2_.getCount();
                  if (p_191971_1_ == -1) {
                     p_191971_2_.setCount(this.storePartialItemStack(p_191971_2_));
                  } else {
                     p_191971_2_.setCount(this.addResource(p_191971_1_, p_191971_2_));
                  }
               } while(!p_191971_2_.isEmpty() && p_191971_2_.getCount() < i);

               if (p_191971_2_.getCount() == i && this.player.abilities.isCreativeMode) {
                  p_191971_2_.setCount(0);
                  return true;
               } else {
                  return p_191971_2_.getCount() < i;
               }
            }
         } catch (Throwable var6) {
            CrashReport crashreport = CrashReport.makeCrashReport(var6, "Adding item to inventory");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Item being added");
            crashreportcategory.addDetail("Registry Name", () -> {
               return String.valueOf(p_191971_2_.getItem().getRegistryName());
            });
            crashreportcategory.addDetail("Item Class", () -> {
               return p_191971_2_.getItem().getClass().getName();
            });
            crashreportcategory.addDetail("Item ID", (Object)Item.getIdFromItem(p_191971_2_.getItem()));
            crashreportcategory.addDetail("Item data", (Object)p_191971_2_.getDamage());
            crashreportcategory.addDetail("Item name", () -> {
               return p_191971_2_.getDisplayName().getString();
            });
            throw new ReportedException(crashreport);
         }
      }
   }

   public void placeItemBackInInventory(World p_191975_1_, ItemStack p_191975_2_) {
      if (!p_191975_1_.isRemote) {
         while(!p_191975_2_.isEmpty()) {
            int i = this.storeItemStack(p_191975_2_);
            if (i == -1) {
               i = this.getFirstEmptyStack();
            }

            if (i == -1) {
               this.player.dropItem(p_191975_2_, false);
               break;
            }

            int j = p_191975_2_.getMaxStackSize() - this.getStackInSlot(i).getCount();
            if (this.add(i, p_191975_2_.split(j))) {
               ((ServerPlayerEntity)this.player).connection.sendPacket(new SSetSlotPacket(-2, i, this.getStackInSlot(i)));
            }
         }
      }

   }

   public ItemStack decrStackSize(int p_70298_1_, int p_70298_2_) {
      List<ItemStack> list = null;

      NonNullList nonnulllist;
      for(Iterator var4 = this.allInventories.iterator(); var4.hasNext(); p_70298_1_ -= nonnulllist.size()) {
         nonnulllist = (NonNullList)var4.next();
         if (p_70298_1_ < nonnulllist.size()) {
            list = nonnulllist;
            break;
         }
      }

      return list != null && !((ItemStack)list.get(p_70298_1_)).isEmpty() ? ItemStackHelper.getAndSplit(list, p_70298_1_, p_70298_2_) : ItemStack.EMPTY;
   }

   public void deleteStack(ItemStack p_184437_1_) {
      Iterator var2 = this.allInventories.iterator();

      while(true) {
         while(var2.hasNext()) {
            NonNullList<ItemStack> nonnulllist = (NonNullList)var2.next();

            for(int i = 0; i < nonnulllist.size(); ++i) {
               if (nonnulllist.get(i) == p_184437_1_) {
                  nonnulllist.set(i, ItemStack.EMPTY);
                  break;
               }
            }
         }

         return;
      }
   }

   public ItemStack removeStackFromSlot(int p_70304_1_) {
      NonNullList<ItemStack> nonnulllist = null;

      NonNullList nonnulllist1;
      for(Iterator var3 = this.allInventories.iterator(); var3.hasNext(); p_70304_1_ -= nonnulllist1.size()) {
         nonnulllist1 = (NonNullList)var3.next();
         if (p_70304_1_ < nonnulllist1.size()) {
            nonnulllist = nonnulllist1;
            break;
         }
      }

      if (nonnulllist != null && !((ItemStack)nonnulllist.get(p_70304_1_)).isEmpty()) {
         ItemStack itemstack = (ItemStack)nonnulllist.get(p_70304_1_);
         nonnulllist.set(p_70304_1_, ItemStack.EMPTY);
         return itemstack;
      } else {
         return ItemStack.EMPTY;
      }
   }

   public void setInventorySlotContents(int p_70299_1_, ItemStack p_70299_2_) {
      NonNullList<ItemStack> nonnulllist = null;

      NonNullList nonnulllist1;
      for(Iterator var4 = this.allInventories.iterator(); var4.hasNext(); p_70299_1_ -= nonnulllist1.size()) {
         nonnulllist1 = (NonNullList)var4.next();
         if (p_70299_1_ < nonnulllist1.size()) {
            nonnulllist = nonnulllist1;
            break;
         }
      }

      if (nonnulllist != null) {
         nonnulllist.set(p_70299_1_, p_70299_2_);
      }

   }

   public float getDestroySpeed(BlockState p_184438_1_) {
      return ((ItemStack)this.mainInventory.get(this.currentItem)).getDestroySpeed(p_184438_1_);
   }

   public ListNBT write(ListNBT p_70442_1_) {
      int k;
      CompoundNBT compoundnbt2;
      for(k = 0; k < this.mainInventory.size(); ++k) {
         if (!((ItemStack)this.mainInventory.get(k)).isEmpty()) {
            compoundnbt2 = new CompoundNBT();
            compoundnbt2.putByte("Slot", (byte)k);
            ((ItemStack)this.mainInventory.get(k)).write(compoundnbt2);
            p_70442_1_.add(compoundnbt2);
         }
      }

      for(k = 0; k < this.armorInventory.size(); ++k) {
         if (!((ItemStack)this.armorInventory.get(k)).isEmpty()) {
            compoundnbt2 = new CompoundNBT();
            compoundnbt2.putByte("Slot", (byte)(k + 100));
            ((ItemStack)this.armorInventory.get(k)).write(compoundnbt2);
            p_70442_1_.add(compoundnbt2);
         }
      }

      for(k = 0; k < this.offHandInventory.size(); ++k) {
         if (!((ItemStack)this.offHandInventory.get(k)).isEmpty()) {
            compoundnbt2 = new CompoundNBT();
            compoundnbt2.putByte("Slot", (byte)(k + 150));
            ((ItemStack)this.offHandInventory.get(k)).write(compoundnbt2);
            p_70442_1_.add(compoundnbt2);
         }
      }

      return p_70442_1_;
   }

   public void read(ListNBT p_70443_1_) {
      this.mainInventory.clear();
      this.armorInventory.clear();
      this.offHandInventory.clear();

      for(int i = 0; i < p_70443_1_.size(); ++i) {
         CompoundNBT compoundnbt = p_70443_1_.getCompound(i);
         int j = compoundnbt.getByte("Slot") & 255;
         ItemStack itemstack = ItemStack.read(compoundnbt);
         if (!itemstack.isEmpty()) {
            if (j >= 0 && j < this.mainInventory.size()) {
               this.mainInventory.set(j, itemstack);
            } else if (j >= 100 && j < this.armorInventory.size() + 100) {
               this.armorInventory.set(j - 100, itemstack);
            } else if (j >= 150 && j < this.offHandInventory.size() + 150) {
               this.offHandInventory.set(j - 150, itemstack);
            }
         }
      }

   }

   public int getSizeInventory() {
      return this.mainInventory.size() + this.armorInventory.size() + this.offHandInventory.size();
   }

   public boolean isEmpty() {
      Iterator var1 = this.mainInventory.iterator();

      ItemStack itemstack2;
      do {
         if (!var1.hasNext()) {
            var1 = this.armorInventory.iterator();

            do {
               if (!var1.hasNext()) {
                  var1 = this.offHandInventory.iterator();

                  do {
                     if (!var1.hasNext()) {
                        return true;
                     }

                     itemstack2 = (ItemStack)var1.next();
                  } while(itemstack2.isEmpty());

                  return false;
               }

               itemstack2 = (ItemStack)var1.next();
            } while(itemstack2.isEmpty());

            return false;
         }

         itemstack2 = (ItemStack)var1.next();
      } while(itemstack2.isEmpty());

      return false;
   }

   public ItemStack getStackInSlot(int p_70301_1_) {
      List<ItemStack> list = null;

      NonNullList nonnulllist;
      for(Iterator var3 = this.allInventories.iterator(); var3.hasNext(); p_70301_1_ -= nonnulllist.size()) {
         nonnulllist = (NonNullList)var3.next();
         if (p_70301_1_ < nonnulllist.size()) {
            list = nonnulllist;
            break;
         }
      }

      return list == null ? ItemStack.EMPTY : (ItemStack)list.get(p_70301_1_);
   }

   public ITextComponent getName() {
      return new TranslationTextComponent("container.inventory", new Object[0]);
   }

   public boolean canHarvestBlock(BlockState p_184432_1_) {
      return this.getStackInSlot(this.currentItem).canHarvestBlock(p_184432_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public ItemStack armorItemInSlot(int p_70440_1_) {
      return (ItemStack)this.armorInventory.get(p_70440_1_);
   }

   public void damageArmor(float p_70449_1_) {
      if (p_70449_1_ > 0.0F) {
         p_70449_1_ /= 4.0F;
         if (p_70449_1_ < 1.0F) {
            p_70449_1_ = 1.0F;
         }

         for(int i = 0; i < this.armorInventory.size(); ++i) {
            ItemStack itemstack = (ItemStack)this.armorInventory.get(i);
            if (itemstack.getItem() instanceof ArmorItem) {
               itemstack.damageItem((int)p_70449_1_, this.player, (p_lambda$damageArmor$4_1_) -> {
                  p_lambda$damageArmor$4_1_.sendBreakAnimation(EquipmentSlotType.func_220318_a(EquipmentSlotType.Group.ARMOR, i));
               });
            }
         }
      }

   }

   public void dropAllItems() {
      Iterator var1 = this.allInventories.iterator();

      while(var1.hasNext()) {
         List<ItemStack> list = (List)var1.next();

         for(int i = 0; i < list.size(); ++i) {
            ItemStack itemstack = (ItemStack)list.get(i);
            if (!itemstack.isEmpty()) {
               this.player.dropItem(itemstack, true, false);
               list.set(i, ItemStack.EMPTY);
            }
         }
      }

   }

   public void markDirty() {
      ++this.timesChanged;
   }

   @OnlyIn(Dist.CLIENT)
   public int getTimesChanged() {
      return this.timesChanged;
   }

   public void setItemStack(ItemStack p_70437_1_) {
      this.itemStack = p_70437_1_;
   }

   public ItemStack getItemStack() {
      return this.itemStack;
   }

   public boolean isUsableByPlayer(PlayerEntity p_70300_1_) {
      if (this.player.removed) {
         return false;
      } else {
         return p_70300_1_.getDistanceSq(this.player) <= 64.0D;
      }
   }

   public boolean hasItemStack(ItemStack p_70431_1_) {
      Iterator var2 = this.allInventories.iterator();

      while(var2.hasNext()) {
         List<ItemStack> list = (List)var2.next();
         Iterator iterator = list.iterator();

         while(iterator.hasNext()) {
            ItemStack itemstack = (ItemStack)iterator.next();
            if (!itemstack.isEmpty() && itemstack.isItemEqual(p_70431_1_)) {
               return true;
            }
         }
      }

      return false;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean hasTag(Tag<Item> p_199712_1_) {
      Iterator var2 = this.allInventories.iterator();

      while(var2.hasNext()) {
         List<ItemStack> list = (List)var2.next();
         Iterator iterator = list.iterator();

         while(iterator.hasNext()) {
            ItemStack itemstack = (ItemStack)iterator.next();
            if (!itemstack.isEmpty() && p_199712_1_.contains(itemstack.getItem())) {
               return true;
            }
         }
      }

      return false;
   }

   public void copyInventory(PlayerInventory p_70455_1_) {
      for(int i = 0; i < this.getSizeInventory(); ++i) {
         this.setInventorySlotContents(i, p_70455_1_.getStackInSlot(i));
      }

      this.currentItem = p_70455_1_.currentItem;
   }

   public void clear() {
      Iterator var1 = this.allInventories.iterator();

      while(var1.hasNext()) {
         List<ItemStack> list = (List)var1.next();
         list.clear();
      }

   }

   public void func_201571_a(RecipeItemHelper p_201571_1_) {
      Iterator var2 = this.mainInventory.iterator();

      while(var2.hasNext()) {
         ItemStack itemstack = (ItemStack)var2.next();
         p_201571_1_.accountPlainStack(itemstack);
      }

   }
}
