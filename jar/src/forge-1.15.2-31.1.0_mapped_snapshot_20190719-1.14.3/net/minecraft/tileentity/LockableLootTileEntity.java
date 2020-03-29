package net.minecraft.tileentity;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameterSets;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraft.world.storage.loot.LootTable;

public abstract class LockableLootTileEntity extends LockableTileEntity {
   @Nullable
   protected ResourceLocation lootTable;
   protected long lootTableSeed;

   protected LockableLootTileEntity(TileEntityType<?> p_i48284_1_) {
      super(p_i48284_1_);
   }

   public static void setLootTable(IBlockReader p_195479_0_, Random p_195479_1_, BlockPos p_195479_2_, ResourceLocation p_195479_3_) {
      TileEntity lvt_4_1_ = p_195479_0_.getTileEntity(p_195479_2_);
      if (lvt_4_1_ instanceof LockableLootTileEntity) {
         ((LockableLootTileEntity)lvt_4_1_).setLootTable(p_195479_3_, p_195479_1_.nextLong());
      }

   }

   protected boolean checkLootAndRead(CompoundNBT p_184283_1_) {
      if (p_184283_1_.contains("LootTable", 8)) {
         this.lootTable = new ResourceLocation(p_184283_1_.getString("LootTable"));
         this.lootTableSeed = p_184283_1_.getLong("LootTableSeed");
         return true;
      } else {
         return false;
      }
   }

   protected boolean checkLootAndWrite(CompoundNBT p_184282_1_) {
      if (this.lootTable == null) {
         return false;
      } else {
         p_184282_1_.putString("LootTable", this.lootTable.toString());
         if (this.lootTableSeed != 0L) {
            p_184282_1_.putLong("LootTableSeed", this.lootTableSeed);
         }

         return true;
      }
   }

   public void fillWithLoot(@Nullable PlayerEntity p_184281_1_) {
      if (this.lootTable != null && this.world.getServer() != null) {
         LootTable lvt_2_1_ = this.world.getServer().getLootTableManager().getLootTableFromLocation(this.lootTable);
         this.lootTable = null;
         LootContext.Builder lvt_3_1_ = (new LootContext.Builder((ServerWorld)this.world)).withParameter(LootParameters.POSITION, new BlockPos(this.pos)).withSeed(this.lootTableSeed);
         if (p_184281_1_ != null) {
            lvt_3_1_.withLuck(p_184281_1_.getLuck()).withParameter(LootParameters.THIS_ENTITY, p_184281_1_);
         }

         lvt_2_1_.fillInventory(this, lvt_3_1_.build(LootParameterSets.CHEST));
      }

   }

   public void setLootTable(ResourceLocation p_189404_1_, long p_189404_2_) {
      this.lootTable = p_189404_1_;
      this.lootTableSeed = p_189404_2_;
   }

   public boolean isEmpty() {
      this.fillWithLoot((PlayerEntity)null);
      return this.getItems().stream().allMatch(ItemStack::isEmpty);
   }

   public ItemStack getStackInSlot(int p_70301_1_) {
      this.fillWithLoot((PlayerEntity)null);
      return (ItemStack)this.getItems().get(p_70301_1_);
   }

   public ItemStack decrStackSize(int p_70298_1_, int p_70298_2_) {
      this.fillWithLoot((PlayerEntity)null);
      ItemStack lvt_3_1_ = ItemStackHelper.getAndSplit(this.getItems(), p_70298_1_, p_70298_2_);
      if (!lvt_3_1_.isEmpty()) {
         this.markDirty();
      }

      return lvt_3_1_;
   }

   public ItemStack removeStackFromSlot(int p_70304_1_) {
      this.fillWithLoot((PlayerEntity)null);
      return ItemStackHelper.getAndRemove(this.getItems(), p_70304_1_);
   }

   public void setInventorySlotContents(int p_70299_1_, ItemStack p_70299_2_) {
      this.fillWithLoot((PlayerEntity)null);
      this.getItems().set(p_70299_1_, p_70299_2_);
      if (p_70299_2_.getCount() > this.getInventoryStackLimit()) {
         p_70299_2_.setCount(this.getInventoryStackLimit());
      }

      this.markDirty();
   }

   public boolean isUsableByPlayer(PlayerEntity p_70300_1_) {
      if (this.world.getTileEntity(this.pos) != this) {
         return false;
      } else {
         return p_70300_1_.getDistanceSq((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) <= 64.0D;
      }
   }

   public void clear() {
      this.getItems().clear();
   }

   protected abstract NonNullList<ItemStack> getItems();

   protected abstract void setItems(NonNullList<ItemStack> var1);

   public boolean canOpen(PlayerEntity p_213904_1_) {
      return super.canOpen(p_213904_1_) && (this.lootTable == null || !p_213904_1_.isSpectator());
   }

   @Nullable
   public Container createMenu(int p_createMenu_1_, PlayerInventory p_createMenu_2_, PlayerEntity p_createMenu_3_) {
      if (this.canOpen(p_createMenu_3_)) {
         this.fillWithLoot(p_createMenu_2_.player);
         return this.createMenu(p_createMenu_1_, p_createMenu_2_);
      } else {
         return null;
      }
   }
}
