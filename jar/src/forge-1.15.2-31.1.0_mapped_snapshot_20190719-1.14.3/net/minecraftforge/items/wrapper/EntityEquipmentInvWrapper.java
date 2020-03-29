package net.minecraftforge.items.wrapper;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;

public abstract class EntityEquipmentInvWrapper implements IItemHandlerModifiable {
   protected final LivingEntity entity;
   protected final List<EquipmentSlotType> slots;

   public EntityEquipmentInvWrapper(LivingEntity entity, EquipmentSlotType.Group slotType) {
      this.entity = entity;
      List<EquipmentSlotType> slots = new ArrayList();
      EquipmentSlotType[] var4 = EquipmentSlotType.values();
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         EquipmentSlotType slot = var4[var6];
         if (slot.getSlotType() == slotType) {
            slots.add(slot);
         }
      }

      this.slots = ImmutableList.copyOf(slots);
   }

   public int getSlots() {
      return this.slots.size();
   }

   @Nonnull
   public ItemStack getStackInSlot(int slot) {
      return this.entity.getItemStackFromSlot(this.validateSlotIndex(slot));
   }

   @Nonnull
   public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
      if (stack.isEmpty()) {
         return ItemStack.EMPTY;
      } else {
         EquipmentSlotType equipmentSlot = this.validateSlotIndex(slot);
         ItemStack existing = this.entity.getItemStackFromSlot(equipmentSlot);
         int limit = this.getStackLimit(slot, stack);
         if (!existing.isEmpty()) {
            if (!ItemHandlerHelper.canItemStacksStack(stack, existing)) {
               return stack;
            }

            limit -= existing.getCount();
         }

         if (limit <= 0) {
            return stack;
         } else {
            boolean reachedLimit = stack.getCount() > limit;
            if (!simulate) {
               if (existing.isEmpty()) {
                  this.entity.setItemStackToSlot(equipmentSlot, reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack);
               } else {
                  existing.grow(reachedLimit ? limit : stack.getCount());
               }
            }

            return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - limit) : ItemStack.EMPTY;
         }
      }
   }

   @Nonnull
   public ItemStack extractItem(int slot, int amount, boolean simulate) {
      if (amount == 0) {
         return ItemStack.EMPTY;
      } else {
         EquipmentSlotType equipmentSlot = this.validateSlotIndex(slot);
         ItemStack existing = this.entity.getItemStackFromSlot(equipmentSlot);
         if (existing.isEmpty()) {
            return ItemStack.EMPTY;
         } else {
            int toExtract = Math.min(amount, existing.getMaxStackSize());
            if (existing.getCount() <= toExtract) {
               if (!simulate) {
                  this.entity.setItemStackToSlot(equipmentSlot, ItemStack.EMPTY);
               }

               return existing;
            } else {
               if (!simulate) {
                  this.entity.setItemStackToSlot(equipmentSlot, ItemHandlerHelper.copyStackWithSize(existing, existing.getCount() - toExtract));
               }

               return ItemHandlerHelper.copyStackWithSize(existing, toExtract);
            }
         }
      }
   }

   public int getSlotLimit(int slot) {
      EquipmentSlotType equipmentSlot = this.validateSlotIndex(slot);
      return equipmentSlot.getSlotType() == EquipmentSlotType.Group.ARMOR ? 1 : 64;
   }

   protected int getStackLimit(int slot, @Nonnull ItemStack stack) {
      return Math.min(this.getSlotLimit(slot), stack.getMaxStackSize());
   }

   public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
      EquipmentSlotType equipmentSlot = this.validateSlotIndex(slot);
      if (!ItemStack.areItemStacksEqual(this.entity.getItemStackFromSlot(equipmentSlot), stack)) {
         this.entity.setItemStackToSlot(equipmentSlot, stack);
      }
   }

   public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
      return true;
   }

   protected EquipmentSlotType validateSlotIndex(int slot) {
      if (slot >= 0 && slot < this.slots.size()) {
         return (EquipmentSlotType)this.slots.get(slot);
      } else {
         throw new IllegalArgumentException("Slot " + slot + " not in valid range - [0," + this.slots.size() + ")");
      }
   }

   public static LazyOptional<IItemHandlerModifiable>[] create(LivingEntity entity) {
      LazyOptional<IItemHandlerModifiable>[] ret = new LazyOptional[]{LazyOptional.of(() -> {
         return new EntityHandsInvWrapper(entity);
      }), LazyOptional.of(() -> {
         return new EntityArmorInvWrapper(entity);
      }), null};
      ret[2] = LazyOptional.of(() -> {
         return new CombinedInvWrapper(new IItemHandlerModifiable[]{(IItemHandlerModifiable)ret[0].orElse((Object)null), (IItemHandlerModifiable)ret[1].orElse((Object)null)});
      });
      return ret;
   }
}
