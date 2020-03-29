package net.minecraft.inventory.container;

import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.BannerItem;
import net.minecraft.item.BannerPatternItem;
import net.minecraft.item.DyeColor;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.BannerPattern;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.IntReferenceHolder;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class LoomContainer extends Container {
   private final IWorldPosCallable worldPos;
   private final IntReferenceHolder field_217034_d;
   private Runnable field_217035_e;
   private final Slot slotBanner;
   private final Slot slotDye;
   private final Slot slotPattern;
   private final Slot output;
   private long field_226622_j_;
   private final IInventory field_217040_j;
   private final IInventory field_217041_k;

   public LoomContainer(int p_i50073_1_, PlayerInventory p_i50073_2_) {
      this(p_i50073_1_, p_i50073_2_, IWorldPosCallable.DUMMY);
   }

   public LoomContainer(int p_i50074_1_, PlayerInventory p_i50074_2_, final IWorldPosCallable p_i50074_3_) {
      super(ContainerType.LOOM, p_i50074_1_);
      this.field_217034_d = IntReferenceHolder.single();
      this.field_217035_e = () -> {
      };
      this.field_217040_j = new Inventory(3) {
         public void markDirty() {
            super.markDirty();
            LoomContainer.this.onCraftMatrixChanged(this);
            LoomContainer.this.field_217035_e.run();
         }
      };
      this.field_217041_k = new Inventory(1) {
         public void markDirty() {
            super.markDirty();
            LoomContainer.this.field_217035_e.run();
         }
      };
      this.worldPos = p_i50074_3_;
      this.slotBanner = this.addSlot(new Slot(this.field_217040_j, 0, 13, 26) {
         public boolean isItemValid(ItemStack p_75214_1_) {
            return p_75214_1_.getItem() instanceof BannerItem;
         }
      });
      this.slotDye = this.addSlot(new Slot(this.field_217040_j, 1, 33, 26) {
         public boolean isItemValid(ItemStack p_75214_1_) {
            return p_75214_1_.getItem() instanceof DyeItem;
         }
      });
      this.slotPattern = this.addSlot(new Slot(this.field_217040_j, 2, 23, 45) {
         public boolean isItemValid(ItemStack p_75214_1_) {
            return p_75214_1_.getItem() instanceof BannerPatternItem;
         }
      });
      this.output = this.addSlot(new Slot(this.field_217041_k, 0, 143, 58) {
         public boolean isItemValid(ItemStack p_75214_1_) {
            return false;
         }

         public ItemStack onTake(PlayerEntity p_190901_1_, ItemStack p_190901_2_) {
            LoomContainer.this.slotBanner.decrStackSize(1);
            LoomContainer.this.slotDye.decrStackSize(1);
            if (!LoomContainer.this.slotBanner.getHasStack() || !LoomContainer.this.slotDye.getHasStack()) {
               LoomContainer.this.field_217034_d.set(0);
            }

            p_i50074_3_.consume((p_216951_1_, p_216951_2_) -> {
               long lvt_3_1_ = p_216951_1_.getGameTime();
               if (LoomContainer.this.field_226622_j_ != lvt_3_1_) {
                  p_216951_1_.playSound((PlayerEntity)null, p_216951_2_, SoundEvents.UI_LOOM_TAKE_RESULT, SoundCategory.BLOCKS, 1.0F, 1.0F);
                  LoomContainer.this.field_226622_j_ = lvt_3_1_;
               }

            });
            return super.onTake(p_190901_1_, p_190901_2_);
         }
      });

      int lvt_4_2_;
      for(lvt_4_2_ = 0; lvt_4_2_ < 3; ++lvt_4_2_) {
         for(int lvt_5_1_ = 0; lvt_5_1_ < 9; ++lvt_5_1_) {
            this.addSlot(new Slot(p_i50074_2_, lvt_5_1_ + lvt_4_2_ * 9 + 9, 8 + lvt_5_1_ * 18, 84 + lvt_4_2_ * 18));
         }
      }

      for(lvt_4_2_ = 0; lvt_4_2_ < 9; ++lvt_4_2_) {
         this.addSlot(new Slot(p_i50074_2_, lvt_4_2_, 8 + lvt_4_2_ * 18, 142));
      }

      this.trackInt(this.field_217034_d);
   }

   @OnlyIn(Dist.CLIENT)
   public int func_217023_e() {
      return this.field_217034_d.get();
   }

   public boolean canInteractWith(PlayerEntity p_75145_1_) {
      return isWithinUsableDistance(this.worldPos, p_75145_1_, Blocks.LOOM);
   }

   public boolean enchantItem(PlayerEntity p_75140_1_, int p_75140_2_) {
      if (p_75140_2_ > 0 && p_75140_2_ <= BannerPattern.field_222481_P) {
         this.field_217034_d.set(p_75140_2_);
         this.func_217031_j();
         return true;
      } else {
         return false;
      }
   }

   public void onCraftMatrixChanged(IInventory p_75130_1_) {
      ItemStack lvt_2_1_ = this.slotBanner.getStack();
      ItemStack lvt_3_1_ = this.slotDye.getStack();
      ItemStack lvt_4_1_ = this.slotPattern.getStack();
      ItemStack lvt_5_1_ = this.output.getStack();
      if (lvt_5_1_.isEmpty() || !lvt_2_1_.isEmpty() && !lvt_3_1_.isEmpty() && this.field_217034_d.get() > 0 && (this.field_217034_d.get() < BannerPattern.field_222480_O - 5 || !lvt_4_1_.isEmpty())) {
         if (!lvt_4_1_.isEmpty() && lvt_4_1_.getItem() instanceof BannerPatternItem) {
            CompoundNBT lvt_6_1_ = lvt_2_1_.getOrCreateChildTag("BlockEntityTag");
            boolean lvt_7_1_ = lvt_6_1_.contains("Patterns", 9) && !lvt_2_1_.isEmpty() && lvt_6_1_.getList("Patterns", 10).size() >= 6;
            if (lvt_7_1_) {
               this.field_217034_d.set(0);
            } else {
               this.field_217034_d.set(((BannerPatternItem)lvt_4_1_.getItem()).func_219980_b().ordinal());
            }
         }
      } else {
         this.output.putStack(ItemStack.EMPTY);
         this.field_217034_d.set(0);
      }

      this.func_217031_j();
      this.detectAndSendChanges();
   }

   @OnlyIn(Dist.CLIENT)
   public void func_217020_a(Runnable p_217020_1_) {
      this.field_217035_e = p_217020_1_;
   }

   public ItemStack transferStackInSlot(PlayerEntity p_82846_1_, int p_82846_2_) {
      ItemStack lvt_3_1_ = ItemStack.EMPTY;
      Slot lvt_4_1_ = (Slot)this.inventorySlots.get(p_82846_2_);
      if (lvt_4_1_ != null && lvt_4_1_.getHasStack()) {
         ItemStack lvt_5_1_ = lvt_4_1_.getStack();
         lvt_3_1_ = lvt_5_1_.copy();
         if (p_82846_2_ == this.output.slotNumber) {
            if (!this.mergeItemStack(lvt_5_1_, 4, 40, true)) {
               return ItemStack.EMPTY;
            }

            lvt_4_1_.onSlotChange(lvt_5_1_, lvt_3_1_);
         } else if (p_82846_2_ != this.slotDye.slotNumber && p_82846_2_ != this.slotBanner.slotNumber && p_82846_2_ != this.slotPattern.slotNumber) {
            if (lvt_5_1_.getItem() instanceof BannerItem) {
               if (!this.mergeItemStack(lvt_5_1_, this.slotBanner.slotNumber, this.slotBanner.slotNumber + 1, false)) {
                  return ItemStack.EMPTY;
               }
            } else if (lvt_5_1_.getItem() instanceof DyeItem) {
               if (!this.mergeItemStack(lvt_5_1_, this.slotDye.slotNumber, this.slotDye.slotNumber + 1, false)) {
                  return ItemStack.EMPTY;
               }
            } else if (lvt_5_1_.getItem() instanceof BannerPatternItem) {
               if (!this.mergeItemStack(lvt_5_1_, this.slotPattern.slotNumber, this.slotPattern.slotNumber + 1, false)) {
                  return ItemStack.EMPTY;
               }
            } else if (p_82846_2_ >= 4 && p_82846_2_ < 31) {
               if (!this.mergeItemStack(lvt_5_1_, 31, 40, false)) {
                  return ItemStack.EMPTY;
               }
            } else if (p_82846_2_ >= 31 && p_82846_2_ < 40 && !this.mergeItemStack(lvt_5_1_, 4, 31, false)) {
               return ItemStack.EMPTY;
            }
         } else if (!this.mergeItemStack(lvt_5_1_, 4, 40, false)) {
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

   public void onContainerClosed(PlayerEntity p_75134_1_) {
      super.onContainerClosed(p_75134_1_);
      this.worldPos.consume((p_217028_2_, p_217028_3_) -> {
         this.clearContainer(p_75134_1_, p_75134_1_.world, this.field_217040_j);
      });
   }

   private void func_217031_j() {
      if (this.field_217034_d.get() > 0) {
         ItemStack lvt_1_1_ = this.slotBanner.getStack();
         ItemStack lvt_2_1_ = this.slotDye.getStack();
         ItemStack lvt_3_1_ = ItemStack.EMPTY;
         if (!lvt_1_1_.isEmpty() && !lvt_2_1_.isEmpty()) {
            lvt_3_1_ = lvt_1_1_.copy();
            lvt_3_1_.setCount(1);
            BannerPattern lvt_4_1_ = BannerPattern.values()[this.field_217034_d.get()];
            DyeColor lvt_5_1_ = ((DyeItem)lvt_2_1_.getItem()).getDyeColor();
            CompoundNBT lvt_6_1_ = lvt_3_1_.getOrCreateChildTag("BlockEntityTag");
            ListNBT lvt_7_2_;
            if (lvt_6_1_.contains("Patterns", 9)) {
               lvt_7_2_ = lvt_6_1_.getList("Patterns", 10);
            } else {
               lvt_7_2_ = new ListNBT();
               lvt_6_1_.put("Patterns", lvt_7_2_);
            }

            CompoundNBT lvt_8_1_ = new CompoundNBT();
            lvt_8_1_.putString("Pattern", lvt_4_1_.getHashname());
            lvt_8_1_.putInt("Color", lvt_5_1_.getId());
            lvt_7_2_.add(lvt_8_1_);
         }

         if (!ItemStack.areItemStacksEqual(lvt_3_1_, this.output.getStack())) {
            this.output.putStack(lvt_3_1_);
         }
      }

   }

   @OnlyIn(Dist.CLIENT)
   public Slot func_217024_f() {
      return this.slotBanner;
   }

   @OnlyIn(Dist.CLIENT)
   public Slot func_217022_g() {
      return this.slotDye;
   }

   @OnlyIn(Dist.CLIENT)
   public Slot func_217025_h() {
      return this.slotPattern;
   }

   @OnlyIn(Dist.CLIENT)
   public Slot func_217026_i() {
      return this.output;
   }
}
