package net.minecraft.entity.item.minecart;

import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.HopperContainer;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.HopperTileEntity;
import net.minecraft.tileentity.IHopper;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public class HopperMinecartEntity extends ContainerMinecartEntity implements IHopper {
   private boolean isBlocked = true;
   private int transferTicker = -1;
   private final BlockPos lastPosition;

   public HopperMinecartEntity(EntityType<? extends HopperMinecartEntity> p_i50116_1_, World p_i50116_2_) {
      super(p_i50116_1_, p_i50116_2_);
      this.lastPosition = BlockPos.ZERO;
   }

   public HopperMinecartEntity(World p_i1721_1_, double p_i1721_2_, double p_i1721_4_, double p_i1721_6_) {
      super(EntityType.HOPPER_MINECART, p_i1721_2_, p_i1721_4_, p_i1721_6_, p_i1721_1_);
      this.lastPosition = BlockPos.ZERO;
   }

   public AbstractMinecartEntity.Type getMinecartType() {
      return AbstractMinecartEntity.Type.HOPPER;
   }

   public BlockState getDefaultDisplayTile() {
      return Blocks.HOPPER.getDefaultState();
   }

   public int getDefaultDisplayTileOffset() {
      return 1;
   }

   public int getSizeInventory() {
      return 5;
   }

   public void onActivatorRailPass(int p_96095_1_, int p_96095_2_, int p_96095_3_, boolean p_96095_4_) {
      boolean lvt_5_1_ = !p_96095_4_;
      if (lvt_5_1_ != this.getBlocked()) {
         this.setBlocked(lvt_5_1_);
      }

   }

   public boolean getBlocked() {
      return this.isBlocked;
   }

   public void setBlocked(boolean p_96110_1_) {
      this.isBlocked = p_96110_1_;
   }

   public World getWorld() {
      return this.world;
   }

   public double getXPos() {
      return this.func_226277_ct_();
   }

   public double getYPos() {
      return this.func_226278_cu_() + 0.5D;
   }

   public double getZPos() {
      return this.func_226281_cx_();
   }

   public void tick() {
      super.tick();
      if (!this.world.isRemote && this.isAlive() && this.getBlocked()) {
         BlockPos lvt_1_1_ = new BlockPos(this);
         if (lvt_1_1_.equals(this.lastPosition)) {
            --this.transferTicker;
         } else {
            this.setTransferTicker(0);
         }

         if (!this.canTransfer()) {
            this.setTransferTicker(0);
            if (this.captureDroppedItems()) {
               this.setTransferTicker(4);
               this.markDirty();
            }
         }
      }

   }

   public boolean captureDroppedItems() {
      if (HopperTileEntity.pullItems(this)) {
         return true;
      } else {
         List<ItemEntity> lvt_1_1_ = this.world.getEntitiesWithinAABB(ItemEntity.class, this.getBoundingBox().grow(0.25D, 0.0D, 0.25D), EntityPredicates.IS_ALIVE);
         if (!lvt_1_1_.isEmpty()) {
            HopperTileEntity.captureItem(this, (ItemEntity)lvt_1_1_.get(0));
         }

         return false;
      }
   }

   public void killMinecart(DamageSource p_94095_1_) {
      super.killMinecart(p_94095_1_);
      if (this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
         this.entityDropItem(Blocks.HOPPER);
      }

   }

   protected void writeAdditional(CompoundNBT p_213281_1_) {
      super.writeAdditional(p_213281_1_);
      p_213281_1_.putInt("TransferCooldown", this.transferTicker);
      p_213281_1_.putBoolean("Enabled", this.isBlocked);
   }

   protected void readAdditional(CompoundNBT p_70037_1_) {
      super.readAdditional(p_70037_1_);
      this.transferTicker = p_70037_1_.getInt("TransferCooldown");
      this.isBlocked = p_70037_1_.contains("Enabled") ? p_70037_1_.getBoolean("Enabled") : true;
   }

   public void setTransferTicker(int p_98042_1_) {
      this.transferTicker = p_98042_1_;
   }

   public boolean canTransfer() {
      return this.transferTicker > 0;
   }

   public Container func_213968_a(int p_213968_1_, PlayerInventory p_213968_2_) {
      return new HopperContainer(p_213968_1_, p_213968_2_, this);
   }
}
