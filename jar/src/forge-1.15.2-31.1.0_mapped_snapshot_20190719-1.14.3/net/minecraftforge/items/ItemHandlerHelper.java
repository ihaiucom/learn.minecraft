package net.minecraftforge.items;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.items.wrapper.PlayerMainInvWrapper;

public class ItemHandlerHelper {
   @Nonnull
   public static ItemStack insertItem(IItemHandler dest, @Nonnull ItemStack stack, boolean simulate) {
      if (dest != null && !stack.isEmpty()) {
         for(int i = 0; i < dest.getSlots(); ++i) {
            stack = dest.insertItem(i, stack, simulate);
            if (stack.isEmpty()) {
               return ItemStack.EMPTY;
            }
         }

         return stack;
      } else {
         return stack;
      }
   }

   public static boolean canItemStacksStack(@Nonnull ItemStack a, @Nonnull ItemStack b) {
      if (!a.isEmpty() && a.isItemEqual(b) && a.hasTag() == b.hasTag()) {
         return (!a.hasTag() || a.getTag().equals(b.getTag())) && a.areCapsCompatible(b);
      } else {
         return false;
      }
   }

   public static boolean canItemStacksStackRelaxed(@Nonnull ItemStack a, @Nonnull ItemStack b) {
      if (!a.isEmpty() && !b.isEmpty() && a.getItem() == b.getItem()) {
         if (!a.isStackable()) {
            return false;
         } else if (a.hasTag() != b.hasTag()) {
            return false;
         } else {
            return (!a.hasTag() || a.getTag().equals(b.getTag())) && a.areCapsCompatible(b);
         }
      } else {
         return false;
      }
   }

   @Nonnull
   public static ItemStack copyStackWithSize(@Nonnull ItemStack itemStack, int size) {
      if (size == 0) {
         return ItemStack.EMPTY;
      } else {
         ItemStack copy = itemStack.copy();
         copy.setCount(size);
         return copy;
      }
   }

   @Nonnull
   public static ItemStack insertItemStacked(IItemHandler inventory, @Nonnull ItemStack stack, boolean simulate) {
      if (inventory != null && !stack.isEmpty()) {
         if (!stack.isStackable()) {
            return insertItem(inventory, stack, simulate);
         } else {
            int sizeInventory = inventory.getSlots();

            int i;
            for(i = 0; i < sizeInventory; ++i) {
               ItemStack slot = inventory.getStackInSlot(i);
               if (canItemStacksStackRelaxed(slot, stack)) {
                  stack = inventory.insertItem(i, stack, simulate);
                  if (stack.isEmpty()) {
                     break;
                  }
               }
            }

            if (!stack.isEmpty()) {
               for(i = 0; i < sizeInventory; ++i) {
                  if (inventory.getStackInSlot(i).isEmpty()) {
                     stack = inventory.insertItem(i, stack, simulate);
                     if (stack.isEmpty()) {
                        break;
                     }
                  }
               }
            }

            return stack;
         }
      } else {
         return stack;
      }
   }

   public static void giveItemToPlayer(PlayerEntity player, @Nonnull ItemStack stack) {
      giveItemToPlayer(player, stack, -1);
   }

   public static void giveItemToPlayer(PlayerEntity player, @Nonnull ItemStack stack, int preferredSlot) {
      if (!stack.isEmpty()) {
         IItemHandler inventory = new PlayerMainInvWrapper(player.inventory);
         World world = player.world;
         ItemStack remainder = stack;
         if (preferredSlot >= 0 && preferredSlot < inventory.getSlots()) {
            remainder = inventory.insertItem(preferredSlot, stack, false);
         }

         if (!remainder.isEmpty()) {
            remainder = insertItemStacked(inventory, remainder, false);
         }

         if (remainder.isEmpty() || remainder.getCount() != stack.getCount()) {
            world.playSound((PlayerEntity)null, player.func_226277_ct_(), player.func_226278_cu_() + 0.5D, player.func_226281_cx_(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, ((world.rand.nextFloat() - world.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
         }

         if (!remainder.isEmpty() && !world.isRemote) {
            ItemEntity entityitem = new ItemEntity(world, player.func_226277_ct_(), player.func_226278_cu_() + 0.5D, player.func_226281_cx_(), remainder);
            entityitem.setPickupDelay(40);
            entityitem.setMotion(entityitem.getMotion().mul(0.0D, 1.0D, 0.0D));
            world.addEntity(entityitem);
         }

      }
   }

   public static int calcRedstoneFromInventory(@Nullable IItemHandler inv) {
      if (inv == null) {
         return 0;
      } else {
         int itemsFound = 0;
         float proportion = 0.0F;

         for(int j = 0; j < inv.getSlots(); ++j) {
            ItemStack itemstack = inv.getStackInSlot(j);
            if (!itemstack.isEmpty()) {
               proportion += (float)itemstack.getCount() / (float)Math.min(inv.getSlotLimit(j), itemstack.getMaxStackSize());
               ++itemsFound;
            }
         }

         proportion /= (float)inv.getSlots();
         return MathHelper.floor(proportion * 14.0F) + (itemsFound > 0 ? 1 : 0);
      }
   }
}
