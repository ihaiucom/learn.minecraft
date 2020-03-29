package net.minecraft.inventory.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.IRecipeHelperPopulator;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.AbstractCookingRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeItemHelper;
import net.minecraft.item.crafting.ServerRecipePlacerFurnace;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class AbstractFurnaceContainer extends RecipeBookContainer<IInventory> {
   private final IInventory furnaceInventory;
   private final IIntArray field_217064_e;
   protected final World world;
   private final IRecipeType<? extends AbstractCookingRecipe> recipeType;

   protected AbstractFurnaceContainer(ContainerType<?> p_i50103_1_, IRecipeType<? extends AbstractCookingRecipe> p_i50103_2_, int p_i50103_3_, PlayerInventory p_i50103_4_) {
      this(p_i50103_1_, p_i50103_2_, p_i50103_3_, p_i50103_4_, new Inventory(3), new IntArray(4));
   }

   protected AbstractFurnaceContainer(ContainerType<?> p_i50104_1_, IRecipeType<? extends AbstractCookingRecipe> p_i50104_2_, int p_i50104_3_, PlayerInventory p_i50104_4_, IInventory p_i50104_5_, IIntArray p_i50104_6_) {
      super(p_i50104_1_, p_i50104_3_);
      this.recipeType = p_i50104_2_;
      assertInventorySize(p_i50104_5_, 3);
      assertIntArraySize(p_i50104_6_, 4);
      this.furnaceInventory = p_i50104_5_;
      this.field_217064_e = p_i50104_6_;
      this.world = p_i50104_4_.player.world;
      this.addSlot(new Slot(p_i50104_5_, 0, 56, 17));
      this.addSlot(new FurnaceFuelSlot(this, p_i50104_5_, 1, 56, 53));
      this.addSlot(new FurnaceResultSlot(p_i50104_4_.player, p_i50104_5_, 2, 116, 35));

      int lvt_7_2_;
      for(lvt_7_2_ = 0; lvt_7_2_ < 3; ++lvt_7_2_) {
         for(int lvt_8_1_ = 0; lvt_8_1_ < 9; ++lvt_8_1_) {
            this.addSlot(new Slot(p_i50104_4_, lvt_8_1_ + lvt_7_2_ * 9 + 9, 8 + lvt_8_1_ * 18, 84 + lvt_7_2_ * 18));
         }
      }

      for(lvt_7_2_ = 0; lvt_7_2_ < 9; ++lvt_7_2_) {
         this.addSlot(new Slot(p_i50104_4_, lvt_7_2_, 8 + lvt_7_2_ * 18, 142));
      }

      this.trackIntArray(p_i50104_6_);
   }

   public void func_201771_a(RecipeItemHelper p_201771_1_) {
      if (this.furnaceInventory instanceof IRecipeHelperPopulator) {
         ((IRecipeHelperPopulator)this.furnaceInventory).fillStackedContents(p_201771_1_);
      }

   }

   public void clear() {
      this.furnaceInventory.clear();
   }

   public void func_217056_a(boolean p_217056_1_, IRecipe<?> p_217056_2_, ServerPlayerEntity p_217056_3_) {
      (new ServerRecipePlacerFurnace(this)).place(p_217056_3_, p_217056_2_, p_217056_1_);
   }

   public boolean matches(IRecipe<? super IInventory> p_201769_1_) {
      return p_201769_1_.matches(this.furnaceInventory, this.world);
   }

   public int getOutputSlot() {
      return 2;
   }

   public int getWidth() {
      return 1;
   }

   public int getHeight() {
      return 1;
   }

   @OnlyIn(Dist.CLIENT)
   public int getSize() {
      return 3;
   }

   public boolean canInteractWith(PlayerEntity p_75145_1_) {
      return this.furnaceInventory.isUsableByPlayer(p_75145_1_);
   }

   public ItemStack transferStackInSlot(PlayerEntity p_82846_1_, int p_82846_2_) {
      ItemStack lvt_3_1_ = ItemStack.EMPTY;
      Slot lvt_4_1_ = (Slot)this.inventorySlots.get(p_82846_2_);
      if (lvt_4_1_ != null && lvt_4_1_.getHasStack()) {
         ItemStack lvt_5_1_ = lvt_4_1_.getStack();
         lvt_3_1_ = lvt_5_1_.copy();
         if (p_82846_2_ == 2) {
            if (!this.mergeItemStack(lvt_5_1_, 3, 39, true)) {
               return ItemStack.EMPTY;
            }

            lvt_4_1_.onSlotChange(lvt_5_1_, lvt_3_1_);
         } else if (p_82846_2_ != 1 && p_82846_2_ != 0) {
            if (this.func_217057_a(lvt_5_1_)) {
               if (!this.mergeItemStack(lvt_5_1_, 0, 1, false)) {
                  return ItemStack.EMPTY;
               }
            } else if (this.isFuel(lvt_5_1_)) {
               if (!this.mergeItemStack(lvt_5_1_, 1, 2, false)) {
                  return ItemStack.EMPTY;
               }
            } else if (p_82846_2_ >= 3 && p_82846_2_ < 30) {
               if (!this.mergeItemStack(lvt_5_1_, 30, 39, false)) {
                  return ItemStack.EMPTY;
               }
            } else if (p_82846_2_ >= 30 && p_82846_2_ < 39 && !this.mergeItemStack(lvt_5_1_, 3, 30, false)) {
               return ItemStack.EMPTY;
            }
         } else if (!this.mergeItemStack(lvt_5_1_, 3, 39, false)) {
            return ItemStack.EMPTY;
         }

         if (lvt_5_1_.isEmpty()) {
            lvt_4_1_.putStack(ItemStack.EMPTY);
         } else {
            lvt_4_1_.onSlotChanged();
         }

         if (lvt_5_1_.getCount() == lvt_3_1_.getCount()) {
            return ItemStack.EMPTY;
         }

         lvt_4_1_.onTake(p_82846_1_, lvt_5_1_);
      }

      return lvt_3_1_;
   }

   protected boolean func_217057_a(ItemStack p_217057_1_) {
      return this.world.getRecipeManager().getRecipe(this.recipeType, new Inventory(new ItemStack[]{p_217057_1_}), this.world).isPresent();
   }

   protected boolean isFuel(ItemStack p_217058_1_) {
      return AbstractFurnaceTileEntity.isFuel(p_217058_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public int getCookProgressionScaled() {
      int lvt_1_1_ = this.field_217064_e.get(2);
      int lvt_2_1_ = this.field_217064_e.get(3);
      return lvt_2_1_ != 0 && lvt_1_1_ != 0 ? lvt_1_1_ * 24 / lvt_2_1_ : 0;
   }

   @OnlyIn(Dist.CLIENT)
   public int getBurnLeftScaled() {
      int lvt_1_1_ = this.field_217064_e.get(1);
      if (lvt_1_1_ == 0) {
         lvt_1_1_ = 200;
      }

      return this.field_217064_e.get(0) * 13 / lvt_1_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean func_217061_l() {
      return this.field_217064_e.get(0) > 0;
   }
}
