package net.minecraft.entity.item.minecart;

import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameterSets;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.ITeleporter;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

public abstract class ContainerMinecartEntity extends AbstractMinecartEntity implements IInventory, INamedContainerProvider {
   private NonNullList<ItemStack> minecartContainerItems;
   private boolean dropContentsWhenDead;
   @Nullable
   private ResourceLocation lootTable;
   private long lootTableSeed;
   private LazyOptional<?> itemHandler;

   protected ContainerMinecartEntity(EntityType<?> p_i48536_1_, World p_i48536_2_) {
      super(p_i48536_1_, p_i48536_2_);
      this.minecartContainerItems = NonNullList.withSize(36, ItemStack.EMPTY);
      this.dropContentsWhenDead = true;
      this.itemHandler = LazyOptional.of(() -> {
         return new InvWrapper(this);
      });
   }

   protected ContainerMinecartEntity(EntityType<?> p_i48537_1_, double p_i48537_2_, double p_i48537_4_, double p_i48537_6_, World p_i48537_8_) {
      super(p_i48537_1_, p_i48537_8_, p_i48537_2_, p_i48537_4_, p_i48537_6_);
      this.minecartContainerItems = NonNullList.withSize(36, ItemStack.EMPTY);
      this.dropContentsWhenDead = true;
      this.itemHandler = LazyOptional.of(() -> {
         return new InvWrapper(this);
      });
   }

   public void killMinecart(DamageSource p_94095_1_) {
      super.killMinecart(p_94095_1_);
      if (this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
         InventoryHelper.dropInventoryItems(this.world, (Entity)this, this);
      }

   }

   public boolean isEmpty() {
      Iterator var1 = this.minecartContainerItems.iterator();

      ItemStack itemstack;
      do {
         if (!var1.hasNext()) {
            return true;
         }

         itemstack = (ItemStack)var1.next();
      } while(itemstack.isEmpty());

      return false;
   }

   public ItemStack getStackInSlot(int p_70301_1_) {
      this.addLoot((PlayerEntity)null);
      return (ItemStack)this.minecartContainerItems.get(p_70301_1_);
   }

   public ItemStack decrStackSize(int p_70298_1_, int p_70298_2_) {
      this.addLoot((PlayerEntity)null);
      return ItemStackHelper.getAndSplit(this.minecartContainerItems, p_70298_1_, p_70298_2_);
   }

   public ItemStack removeStackFromSlot(int p_70304_1_) {
      this.addLoot((PlayerEntity)null);
      ItemStack itemstack = (ItemStack)this.minecartContainerItems.get(p_70304_1_);
      if (itemstack.isEmpty()) {
         return ItemStack.EMPTY;
      } else {
         this.minecartContainerItems.set(p_70304_1_, ItemStack.EMPTY);
         return itemstack;
      }
   }

   public void setInventorySlotContents(int p_70299_1_, ItemStack p_70299_2_) {
      this.addLoot((PlayerEntity)null);
      this.minecartContainerItems.set(p_70299_1_, p_70299_2_);
      if (!p_70299_2_.isEmpty() && p_70299_2_.getCount() > this.getInventoryStackLimit()) {
         p_70299_2_.setCount(this.getInventoryStackLimit());
      }

   }

   public boolean replaceItemInInventory(int p_174820_1_, ItemStack p_174820_2_) {
      if (p_174820_1_ >= 0 && p_174820_1_ < this.getSizeInventory()) {
         this.setInventorySlotContents(p_174820_1_, p_174820_2_);
         return true;
      } else {
         return false;
      }
   }

   public void markDirty() {
   }

   public boolean isUsableByPlayer(PlayerEntity p_70300_1_) {
      if (this.removed) {
         return false;
      } else {
         return p_70300_1_.getDistanceSq(this) <= 64.0D;
      }
   }

   @Nullable
   public Entity changeDimension(DimensionType p_changeDimension_1_, ITeleporter p_changeDimension_2_) {
      this.dropContentsWhenDead = false;
      return super.changeDimension(p_changeDimension_1_, p_changeDimension_2_);
   }

   public void remove(boolean p_remove_1_) {
      if (!this.world.isRemote && this.dropContentsWhenDead) {
         InventoryHelper.dropInventoryItems(this.world, (Entity)this, this);
      }

      super.remove(p_remove_1_);
      if (!p_remove_1_) {
         this.itemHandler.invalidate();
      }

   }

   protected void writeAdditional(CompoundNBT p_213281_1_) {
      super.writeAdditional(p_213281_1_);
      if (this.lootTable != null) {
         p_213281_1_.putString("LootTable", this.lootTable.toString());
         if (this.lootTableSeed != 0L) {
            p_213281_1_.putLong("LootTableSeed", this.lootTableSeed);
         }
      } else {
         ItemStackHelper.saveAllItems(p_213281_1_, this.minecartContainerItems);
      }

   }

   protected void readAdditional(CompoundNBT p_70037_1_) {
      super.readAdditional(p_70037_1_);
      this.minecartContainerItems = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
      if (p_70037_1_.contains("LootTable", 8)) {
         this.lootTable = new ResourceLocation(p_70037_1_.getString("LootTable"));
         this.lootTableSeed = p_70037_1_.getLong("LootTableSeed");
      } else {
         ItemStackHelper.loadAllItems(p_70037_1_, this.minecartContainerItems);
      }

   }

   public boolean processInitialInteract(PlayerEntity p_184230_1_, Hand p_184230_2_) {
      if (super.processInitialInteract(p_184230_1_, p_184230_2_)) {
         return true;
      } else {
         p_184230_1_.openContainer(this);
         return true;
      }
   }

   protected void applyDrag() {
      float f = 0.98F;
      if (this.lootTable == null) {
         int i = 15 - Container.calcRedstoneFromInventory(this);
         f += (float)i * 0.001F;
      }

      this.setMotion(this.getMotion().mul((double)f, 0.0D, (double)f));
   }

   public void addLoot(@Nullable PlayerEntity p_184288_1_) {
      if (this.lootTable != null && this.world.getServer() != null) {
         LootTable loottable = this.world.getServer().getLootTableManager().getLootTableFromLocation(this.lootTable);
         this.lootTable = null;
         LootContext.Builder lootcontext$builder = (new LootContext.Builder((ServerWorld)this.world)).withParameter(LootParameters.POSITION, new BlockPos(this)).withSeed(this.lootTableSeed);
         lootcontext$builder.withParameter(LootParameters.KILLER_ENTITY, this);
         if (p_184288_1_ != null) {
            lootcontext$builder.withLuck(p_184288_1_.getLuck()).withParameter(LootParameters.THIS_ENTITY, p_184288_1_);
         }

         loottable.fillInventory(this, lootcontext$builder.build(LootParameterSets.CHEST));
      }

   }

   public void clear() {
      this.addLoot((PlayerEntity)null);
      this.minecartContainerItems.clear();
   }

   public void setLootTable(ResourceLocation p_184289_1_, long p_184289_2_) {
      this.lootTable = p_184289_1_;
      this.lootTableSeed = p_184289_2_;
   }

   @Nullable
   public Container createMenu(int p_createMenu_1_, PlayerInventory p_createMenu_2_, PlayerEntity p_createMenu_3_) {
      if (this.lootTable != null && p_createMenu_3_.isSpectator()) {
         return null;
      } else {
         this.addLoot(p_createMenu_2_.player);
         return this.func_213968_a(p_createMenu_1_, p_createMenu_2_);
      }
   }

   protected abstract Container func_213968_a(int var1, PlayerInventory var2);

   public <T> LazyOptional<T> getCapability(Capability<T> p_getCapability_1_, @Nullable Direction p_getCapability_2_) {
      return this.isAlive() && p_getCapability_1_ == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? this.itemHandler.cast() : super.getCapability(p_getCapability_1_, p_getCapability_2_);
   }

   public void dropContentsWhenDead(boolean p_dropContentsWhenDead_1_) {
      this.dropContentsWhenDead = p_dropContentsWhenDead_1_;
   }
}
