package net.minecraftforge.common.extensions;

import java.util.Collection;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.entity.item.EnderCrystalEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.entity.item.LeashKnotEntity;
import net.minecraft.entity.item.PaintingEntity;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public interface IForgeEntity extends ICapabilitySerializable<CompoundNBT> {
   default Entity getEntity() {
      return (Entity)this;
   }

   default void deserializeNBT(CompoundNBT nbt) {
      this.getEntity().read(nbt);
   }

   default CompoundNBT serializeNBT() {
      CompoundNBT ret = new CompoundNBT();
      String id = this.getEntity().getEntityString();
      if (id != null) {
         ret.putString("id", this.getEntity().getEntityString());
      }

      return this.getEntity().writeWithoutTypeId(ret);
   }

   boolean canUpdate();

   void canUpdate(boolean var1);

   @Nullable
   Collection<ItemEntity> captureDrops();

   Collection<ItemEntity> captureDrops(@Nullable Collection<ItemEntity> var1);

   CompoundNBT getPersistentData();

   default boolean shouldRiderSit() {
      return true;
   }

   default ItemStack getPickedResult(RayTraceResult target) {
      if (this instanceof PaintingEntity) {
         return new ItemStack(Items.PAINTING);
      } else if (this instanceof LeashKnotEntity) {
         return new ItemStack(Items.LEAD);
      } else if (this instanceof ItemFrameEntity) {
         ItemStack held = ((ItemFrameEntity)this).getDisplayedItem();
         return held.isEmpty() ? new ItemStack(Items.ITEM_FRAME) : held.copy();
      } else if (this instanceof AbstractMinecartEntity) {
         return ((AbstractMinecartEntity)this).getCartItem();
      } else if (this instanceof BoatEntity) {
         return new ItemStack(((BoatEntity)this).getItemBoat());
      } else if (this instanceof ArmorStandEntity) {
         return new ItemStack(Items.ARMOR_STAND);
      } else if (this instanceof EnderCrystalEntity) {
         return new ItemStack(Items.END_CRYSTAL);
      } else {
         SpawnEggItem egg = SpawnEggItem.getEgg(this.getEntity().getType());
         return egg != null ? new ItemStack(egg) : ItemStack.EMPTY;
      }
   }

   default boolean canRiderInteract() {
      return false;
   }

   default boolean canBeRiddenInWater(Entity rider) {
      return this instanceof LivingEntity;
   }

   boolean canTrample(BlockState var1, BlockPos var2, float var3);

   default EntityClassification getClassification(boolean forSpawnCount) {
      return this.getEntity().getType().getClassification();
   }

   boolean isAddedToWorld();

   void onAddedToWorld();

   void onRemovedFromWorld();

   void revive();
}
