package net.minecraft.tileentity;

import javax.annotation.Nullable;
import net.minecraft.block.LecternBlock;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ICommandSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IClearable;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.LecternContainer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.WrittenBookItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.IIntArray;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;

public class LecternTileEntity extends TileEntity implements IClearable, INamedContainerProvider {
   private final IInventory field_214048_a = new IInventory() {
      public int getSizeInventory() {
         return 1;
      }

      public boolean isEmpty() {
         return LecternTileEntity.this.book.isEmpty();
      }

      public ItemStack getStackInSlot(int p_70301_1_) {
         return p_70301_1_ == 0 ? LecternTileEntity.this.book : ItemStack.EMPTY;
      }

      public ItemStack decrStackSize(int p_70298_1_, int p_70298_2_) {
         if (p_70298_1_ == 0) {
            ItemStack lvt_3_1_ = LecternTileEntity.this.book.split(p_70298_2_);
            if (LecternTileEntity.this.book.isEmpty()) {
               LecternTileEntity.this.bookRemoved();
            }

            return lvt_3_1_;
         } else {
            return ItemStack.EMPTY;
         }
      }

      public ItemStack removeStackFromSlot(int p_70304_1_) {
         if (p_70304_1_ == 0) {
            ItemStack lvt_2_1_ = LecternTileEntity.this.book;
            LecternTileEntity.this.book = ItemStack.EMPTY;
            LecternTileEntity.this.bookRemoved();
            return lvt_2_1_;
         } else {
            return ItemStack.EMPTY;
         }
      }

      public void setInventorySlotContents(int p_70299_1_, ItemStack p_70299_2_) {
      }

      public int getInventoryStackLimit() {
         return 1;
      }

      public void markDirty() {
         LecternTileEntity.this.markDirty();
      }

      public boolean isUsableByPlayer(PlayerEntity p_70300_1_) {
         if (LecternTileEntity.this.world.getTileEntity(LecternTileEntity.this.pos) != LecternTileEntity.this) {
            return false;
         } else {
            return p_70300_1_.getDistanceSq((double)LecternTileEntity.this.pos.getX() + 0.5D, (double)LecternTileEntity.this.pos.getY() + 0.5D, (double)LecternTileEntity.this.pos.getZ() + 0.5D) > 64.0D ? false : LecternTileEntity.this.hasBook();
         }
      }

      public boolean isItemValidForSlot(int p_94041_1_, ItemStack p_94041_2_) {
         return false;
      }

      public void clear() {
      }
   };
   private final IIntArray field_214049_b = new IIntArray() {
      public int get(int p_221476_1_) {
         return p_221476_1_ == 0 ? LecternTileEntity.this.page : 0;
      }

      public void set(int p_221477_1_, int p_221477_2_) {
         if (p_221477_1_ == 0) {
            LecternTileEntity.this.setPage(p_221477_2_);
         }

      }

      public int size() {
         return 1;
      }
   };
   private ItemStack book;
   private int page;
   private int pages;

   public LecternTileEntity() {
      super(TileEntityType.LECTERN);
      this.book = ItemStack.EMPTY;
   }

   public ItemStack getBook() {
      return this.book;
   }

   public boolean hasBook() {
      Item lvt_1_1_ = this.book.getItem();
      return lvt_1_1_ == Items.WRITABLE_BOOK || lvt_1_1_ == Items.WRITTEN_BOOK;
   }

   public void setBook(ItemStack p_214045_1_) {
      this.setBook(p_214045_1_, (PlayerEntity)null);
   }

   private void bookRemoved() {
      this.page = 0;
      this.pages = 0;
      LecternBlock.setHasBook(this.getWorld(), this.getPos(), this.getBlockState(), false);
   }

   public void setBook(ItemStack p_214040_1_, @Nullable PlayerEntity p_214040_2_) {
      this.book = this.ensureResolved(p_214040_1_, p_214040_2_);
      this.page = 0;
      this.pages = WrittenBookItem.func_220049_j(this.book);
      this.markDirty();
   }

   private void setPage(int p_214035_1_) {
      int lvt_2_1_ = MathHelper.clamp(p_214035_1_, 0, this.pages - 1);
      if (lvt_2_1_ != this.page) {
         this.page = lvt_2_1_;
         this.markDirty();
         LecternBlock.pulse(this.getWorld(), this.getPos(), this.getBlockState());
      }

   }

   public int getPage() {
      return this.page;
   }

   public int getComparatorSignalLevel() {
      float lvt_1_1_ = this.pages > 1 ? (float)this.getPage() / ((float)this.pages - 1.0F) : 1.0F;
      return MathHelper.floor(lvt_1_1_ * 14.0F) + (this.hasBook() ? 1 : 0);
   }

   private ItemStack ensureResolved(ItemStack p_214047_1_, @Nullable PlayerEntity p_214047_2_) {
      if (this.world instanceof ServerWorld && p_214047_1_.getItem() == Items.WRITTEN_BOOK) {
         WrittenBookItem.resolveContents(p_214047_1_, this.createCommandSource(p_214047_2_), p_214047_2_);
      }

      return p_214047_1_;
   }

   private CommandSource createCommandSource(@Nullable PlayerEntity p_214039_1_) {
      String lvt_2_2_;
      Object lvt_3_2_;
      if (p_214039_1_ == null) {
         lvt_2_2_ = "Lectern";
         lvt_3_2_ = new StringTextComponent("Lectern");
      } else {
         lvt_2_2_ = p_214039_1_.getName().getString();
         lvt_3_2_ = p_214039_1_.getDisplayName();
      }

      Vec3d lvt_4_1_ = new Vec3d((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D);
      return new CommandSource(ICommandSource.field_213139_a_, lvt_4_1_, Vec2f.ZERO, (ServerWorld)this.world, 2, lvt_2_2_, (ITextComponent)lvt_3_2_, this.world.getServer(), p_214039_1_);
   }

   public boolean onlyOpsCanSetNbt() {
      return true;
   }

   public void read(CompoundNBT p_145839_1_) {
      super.read(p_145839_1_);
      if (p_145839_1_.contains("Book", 10)) {
         this.book = this.ensureResolved(ItemStack.read(p_145839_1_.getCompound("Book")), (PlayerEntity)null);
      } else {
         this.book = ItemStack.EMPTY;
      }

      this.pages = WrittenBookItem.func_220049_j(this.book);
      this.page = MathHelper.clamp(p_145839_1_.getInt("Page"), 0, this.pages - 1);
   }

   public CompoundNBT write(CompoundNBT p_189515_1_) {
      super.write(p_189515_1_);
      if (!this.getBook().isEmpty()) {
         p_189515_1_.put("Book", this.getBook().write(new CompoundNBT()));
         p_189515_1_.putInt("Page", this.page);
      }

      return p_189515_1_;
   }

   public void clear() {
      this.setBook(ItemStack.EMPTY);
   }

   public Container createMenu(int p_createMenu_1_, PlayerInventory p_createMenu_2_, PlayerEntity p_createMenu_3_) {
      return new LecternContainer(p_createMenu_1_, this.field_214048_a, this.field_214049_b);
   }

   public ITextComponent getDisplayName() {
      return new TranslationTextComponent("container.lectern", new Object[0]);
   }
}
