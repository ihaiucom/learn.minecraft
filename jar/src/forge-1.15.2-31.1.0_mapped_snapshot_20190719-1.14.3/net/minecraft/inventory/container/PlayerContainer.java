package net.minecraft.inventory.container;

import com.mojang.datafixers.util.Pair;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipeItemHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class PlayerContainer extends RecipeBookContainer<CraftingInventory> {
   public static final ResourceLocation field_226615_c_ = new ResourceLocation("textures/atlas/blocks.png");
   public static final ResourceLocation field_226616_d_ = new ResourceLocation("item/empty_armor_slot_helmet");
   public static final ResourceLocation field_226617_e_ = new ResourceLocation("item/empty_armor_slot_chestplate");
   public static final ResourceLocation field_226618_f_ = new ResourceLocation("item/empty_armor_slot_leggings");
   public static final ResourceLocation field_226619_g_ = new ResourceLocation("item/empty_armor_slot_boots");
   public static final ResourceLocation field_226620_h_ = new ResourceLocation("item/empty_armor_slot_shield");
   private static final ResourceLocation[] ARMOR_SLOT_TEXTURES;
   private static final EquipmentSlotType[] VALID_EQUIPMENT_SLOTS;
   private final CraftingInventory field_75181_e = new CraftingInventory(this, 2, 2);
   private final CraftResultInventory field_75179_f = new CraftResultInventory();
   public final boolean isLocalWorld;
   private final PlayerEntity player;

   public PlayerContainer(PlayerInventory p_i1819_1_, boolean p_i1819_2_, PlayerEntity p_i1819_3_) {
      super((ContainerType)null, 0);
      this.isLocalWorld = p_i1819_2_;
      this.player = p_i1819_3_;
      this.addSlot(new CraftingResultSlot(p_i1819_1_.player, this.field_75181_e, this.field_75179_f, 0, 154, 28));

      int i1;
      int j1;
      for(i1 = 0; i1 < 2; ++i1) {
         for(j1 = 0; j1 < 2; ++j1) {
            this.addSlot(new Slot(this.field_75181_e, j1 + i1 * 2, 98 + j1 * 18, 18 + i1 * 18));
         }
      }

      for(i1 = 0; i1 < 4; ++i1) {
         final EquipmentSlotType equipmentslottype = VALID_EQUIPMENT_SLOTS[i1];
         this.addSlot(new Slot(p_i1819_1_, 39 - i1, 8, 8 + i1 * 18) {
            public int getSlotStackLimit() {
               return 1;
            }

            public boolean isItemValid(ItemStack p_75214_1_) {
               return p_75214_1_.canEquip(equipmentslottype, PlayerContainer.this.player);
            }

            public boolean canTakeStack(PlayerEntity p_82869_1_) {
               ItemStack itemstack = this.getStack();
               return !itemstack.isEmpty() && !p_82869_1_.isCreative() && EnchantmentHelper.hasBindingCurse(itemstack) ? false : super.canTakeStack(p_82869_1_);
            }

            @OnlyIn(Dist.CLIENT)
            public Pair<ResourceLocation, ResourceLocation> func_225517_c_() {
               return Pair.of(PlayerContainer.field_226615_c_, PlayerContainer.ARMOR_SLOT_TEXTURES[equipmentslottype.getIndex()]);
            }
         });
      }

      for(i1 = 0; i1 < 3; ++i1) {
         for(j1 = 0; j1 < 9; ++j1) {
            this.addSlot(new Slot(p_i1819_1_, j1 + (i1 + 1) * 9, 8 + j1 * 18, 84 + i1 * 18));
         }
      }

      for(i1 = 0; i1 < 9; ++i1) {
         this.addSlot(new Slot(p_i1819_1_, i1, 8 + i1 * 18, 142));
      }

      this.addSlot(new Slot(p_i1819_1_, 40, 77, 62) {
         @OnlyIn(Dist.CLIENT)
         public Pair<ResourceLocation, ResourceLocation> func_225517_c_() {
            return Pair.of(PlayerContainer.field_226615_c_, PlayerContainer.field_226620_h_);
         }
      });
   }

   public void func_201771_a(RecipeItemHelper p_201771_1_) {
      this.field_75181_e.fillStackedContents(p_201771_1_);
   }

   public void clear() {
      this.field_75179_f.clear();
      this.field_75181_e.clear();
   }

   public boolean matches(IRecipe<? super CraftingInventory> p_201769_1_) {
      return p_201769_1_.matches(this.field_75181_e, this.player.world);
   }

   public void onCraftMatrixChanged(IInventory p_75130_1_) {
      WorkbenchContainer.func_217066_a(this.windowId, this.player.world, this.player, this.field_75181_e, this.field_75179_f);
   }

   public void onContainerClosed(PlayerEntity p_75134_1_) {
      super.onContainerClosed(p_75134_1_);
      this.field_75179_f.clear();
      if (!p_75134_1_.world.isRemote) {
         this.clearContainer(p_75134_1_, p_75134_1_.world, this.field_75181_e);
      }

   }

   public boolean canInteractWith(PlayerEntity p_75145_1_) {
      return true;
   }

   public ItemStack transferStackInSlot(PlayerEntity p_82846_1_, int p_82846_2_) {
      ItemStack itemstack = ItemStack.EMPTY;
      Slot slot = (Slot)this.inventorySlots.get(p_82846_2_);
      if (slot != null && slot.getHasStack()) {
         ItemStack itemstack1 = slot.getStack();
         itemstack = itemstack1.copy();
         EquipmentSlotType equipmentslottype = MobEntity.getSlotForItemStack(itemstack);
         if (p_82846_2_ == 0) {
            if (!this.mergeItemStack(itemstack1, 9, 45, true)) {
               return ItemStack.EMPTY;
            }

            slot.onSlotChange(itemstack1, itemstack);
         } else if (p_82846_2_ >= 1 && p_82846_2_ < 5) {
            if (!this.mergeItemStack(itemstack1, 9, 45, false)) {
               return ItemStack.EMPTY;
            }
         } else if (p_82846_2_ >= 5 && p_82846_2_ < 9) {
            if (!this.mergeItemStack(itemstack1, 9, 45, false)) {
               return ItemStack.EMPTY;
            }
         } else if (equipmentslottype.getSlotType() == EquipmentSlotType.Group.ARMOR && !((Slot)this.inventorySlots.get(8 - equipmentslottype.getIndex())).getHasStack()) {
            int i = 8 - equipmentslottype.getIndex();
            if (!this.mergeItemStack(itemstack1, i, i + 1, false)) {
               return ItemStack.EMPTY;
            }
         } else if (equipmentslottype == EquipmentSlotType.OFFHAND && !((Slot)this.inventorySlots.get(45)).getHasStack()) {
            if (!this.mergeItemStack(itemstack1, 45, 46, false)) {
               return ItemStack.EMPTY;
            }
         } else if (p_82846_2_ >= 9 && p_82846_2_ < 36) {
            if (!this.mergeItemStack(itemstack1, 36, 45, false)) {
               return ItemStack.EMPTY;
            }
         } else if (p_82846_2_ >= 36 && p_82846_2_ < 45) {
            if (!this.mergeItemStack(itemstack1, 9, 36, false)) {
               return ItemStack.EMPTY;
            }
         } else if (!this.mergeItemStack(itemstack1, 9, 45, false)) {
            return ItemStack.EMPTY;
         }

         if (itemstack1.isEmpty()) {
            slot.putStack(ItemStack.EMPTY);
         } else {
            slot.onSlotChanged();
         }

         if (itemstack1.getCount() == itemstack.getCount()) {
            return ItemStack.EMPTY;
         }

         ItemStack itemstack2 = slot.onTake(p_82846_1_, itemstack1);
         if (p_82846_2_ == 0) {
            p_82846_1_.dropItem(itemstack2, false);
         }
      }

      return itemstack;
   }

   public boolean canMergeSlot(ItemStack p_94530_1_, Slot p_94530_2_) {
      return p_94530_2_.inventory != this.field_75179_f && super.canMergeSlot(p_94530_1_, p_94530_2_);
   }

   public int getOutputSlot() {
      return 0;
   }

   public int getWidth() {
      return this.field_75181_e.getWidth();
   }

   public int getHeight() {
      return this.field_75181_e.getHeight();
   }

   @OnlyIn(Dist.CLIENT)
   public int getSize() {
      return 5;
   }

   static {
      ARMOR_SLOT_TEXTURES = new ResourceLocation[]{field_226619_g_, field_226618_f_, field_226617_e_, field_226616_d_};
      VALID_EQUIPMENT_SLOTS = new EquipmentSlotType[]{EquipmentSlotType.HEAD, EquipmentSlotType.CHEST, EquipmentSlotType.LEGS, EquipmentSlotType.FEET};
   }
}
