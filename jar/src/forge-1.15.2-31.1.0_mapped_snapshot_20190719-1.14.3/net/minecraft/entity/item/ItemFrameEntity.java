package net.minecraft.entity.item;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.RedstoneDiodeBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Pose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ItemFrameEntity extends HangingEntity {
   private static final Logger PRIVATE_LOGGER = LogManager.getLogger();
   private static final DataParameter<ItemStack> ITEM;
   private static final DataParameter<Integer> ROTATION;
   private float itemDropChance = 1.0F;

   public ItemFrameEntity(EntityType<? extends ItemFrameEntity> p_i50224_1_, World p_i50224_2_) {
      super(p_i50224_1_, p_i50224_2_);
   }

   public ItemFrameEntity(World p_i45852_1_, BlockPos p_i45852_2_, Direction p_i45852_3_) {
      super(EntityType.ITEM_FRAME, p_i45852_1_, p_i45852_2_);
      this.updateFacingWithBoundingBox(p_i45852_3_);
   }

   protected float getEyeHeight(Pose p_213316_1_, EntitySize p_213316_2_) {
      return 0.0F;
   }

   protected void registerData() {
      this.getDataManager().register(ITEM, ItemStack.EMPTY);
      this.getDataManager().register(ROTATION, 0);
   }

   protected void updateFacingWithBoundingBox(Direction p_174859_1_) {
      Validate.notNull(p_174859_1_);
      this.facingDirection = p_174859_1_;
      if (p_174859_1_.getAxis().isHorizontal()) {
         this.rotationPitch = 0.0F;
         this.rotationYaw = (float)(this.facingDirection.getHorizontalIndex() * 90);
      } else {
         this.rotationPitch = (float)(-90 * p_174859_1_.getAxisDirection().getOffset());
         this.rotationYaw = 0.0F;
      }

      this.prevRotationPitch = this.rotationPitch;
      this.prevRotationYaw = this.rotationYaw;
      this.updateBoundingBox();
   }

   protected void updateBoundingBox() {
      if (this.facingDirection != null) {
         double d0 = 0.46875D;
         double d1 = (double)this.hangingPosition.getX() + 0.5D - (double)this.facingDirection.getXOffset() * 0.46875D;
         double d2 = (double)this.hangingPosition.getY() + 0.5D - (double)this.facingDirection.getYOffset() * 0.46875D;
         double d3 = (double)this.hangingPosition.getZ() + 0.5D - (double)this.facingDirection.getZOffset() * 0.46875D;
         this.func_226288_n_(d1, d2, d3);
         double d4 = (double)this.getWidthPixels();
         double d5 = (double)this.getHeightPixels();
         double d6 = (double)this.getWidthPixels();
         Direction.Axis direction$axis = this.facingDirection.getAxis();
         switch(direction$axis) {
         case X:
            d4 = 1.0D;
            break;
         case Y:
            d5 = 1.0D;
            break;
         case Z:
            d6 = 1.0D;
         }

         d4 /= 32.0D;
         d5 /= 32.0D;
         d6 /= 32.0D;
         this.setBoundingBox(new AxisAlignedBB(d1 - d4, d2 - d5, d3 - d6, d1 + d4, d2 + d5, d3 + d6));
      }

   }

   public boolean onValidSurface() {
      if (!this.world.func_226669_j_(this)) {
         return false;
      } else {
         BlockState blockstate = this.world.getBlockState(this.hangingPosition.offset(this.facingDirection.getOpposite()));
         return !blockstate.getMaterial().isSolid() && (!this.facingDirection.getAxis().isHorizontal() || !RedstoneDiodeBlock.isDiode(blockstate)) ? false : this.world.getEntitiesInAABBexcluding(this, this.getBoundingBox(), IS_HANGING_ENTITY).isEmpty();
      }
   }

   public float getCollisionBorderSize() {
      return 0.0F;
   }

   public void onKillCommand() {
      this.removeItem(this.getDisplayedItem());
      super.onKillCommand();
   }

   public boolean attackEntityFrom(DamageSource p_70097_1_, float p_70097_2_) {
      if (this.isInvulnerableTo(p_70097_1_)) {
         return false;
      } else if (!p_70097_1_.isExplosion() && !this.getDisplayedItem().isEmpty()) {
         if (!this.world.isRemote) {
            this.dropItemOrSelf(p_70097_1_.getTrueSource(), false);
            this.playSound(SoundEvents.ENTITY_ITEM_FRAME_REMOVE_ITEM, 1.0F, 1.0F);
         }

         return true;
      } else {
         return super.attackEntityFrom(p_70097_1_, p_70097_2_);
      }
   }

   public int getWidthPixels() {
      return 12;
   }

   public int getHeightPixels() {
      return 12;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isInRangeToRenderDist(double p_70112_1_) {
      double d0 = 16.0D;
      d0 = d0 * 64.0D * getRenderDistanceWeight();
      return p_70112_1_ < d0 * d0;
   }

   public void onBroken(@Nullable Entity p_110128_1_) {
      this.playSound(SoundEvents.ENTITY_ITEM_FRAME_BREAK, 1.0F, 1.0F);
      this.dropItemOrSelf(p_110128_1_, true);
   }

   public void playPlaceSound() {
      this.playSound(SoundEvents.ENTITY_ITEM_FRAME_PLACE, 1.0F, 1.0F);
   }

   private void dropItemOrSelf(@Nullable Entity p_146065_1_, boolean p_146065_2_) {
      if (!this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
         if (p_146065_1_ == null) {
            this.removeItem(this.getDisplayedItem());
         }
      } else {
         ItemStack itemstack = this.getDisplayedItem();
         this.setDisplayedItem(ItemStack.EMPTY);
         if (p_146065_1_ instanceof PlayerEntity) {
            PlayerEntity playerentity = (PlayerEntity)p_146065_1_;
            if (playerentity.abilities.isCreativeMode) {
               this.removeItem(itemstack);
               return;
            }
         }

         if (p_146065_2_) {
            this.entityDropItem(Items.ITEM_FRAME);
         }

         if (!itemstack.isEmpty()) {
            itemstack = itemstack.copy();
            this.removeItem(itemstack);
            if (this.rand.nextFloat() < this.itemDropChance) {
               this.entityDropItem(itemstack);
            }
         }
      }

   }

   private void removeItem(ItemStack p_110131_1_) {
      if (p_110131_1_.getItem() instanceof FilledMapItem) {
         MapData mapdata = FilledMapItem.getMapData(p_110131_1_, this.world);
         mapdata.removeItemFrame(this.hangingPosition, this.getEntityId());
         mapdata.setDirty(true);
      }

      p_110131_1_.setItemFrame((ItemFrameEntity)null);
   }

   public ItemStack getDisplayedItem() {
      return (ItemStack)this.getDataManager().get(ITEM);
   }

   public void setDisplayedItem(ItemStack p_82334_1_) {
      this.setDisplayedItemWithUpdate(p_82334_1_, true);
   }

   public void setDisplayedItemWithUpdate(ItemStack p_174864_1_, boolean p_174864_2_) {
      if (!p_174864_1_.isEmpty()) {
         p_174864_1_ = p_174864_1_.copy();
         p_174864_1_.setCount(1);
         p_174864_1_.setItemFrame(this);
      }

      this.getDataManager().set(ITEM, p_174864_1_);
      if (!p_174864_1_.isEmpty()) {
         this.playSound(SoundEvents.ENTITY_ITEM_FRAME_ADD_ITEM, 1.0F, 1.0F);
      }

      if (p_174864_2_ && this.hangingPosition != null) {
         this.world.updateComparatorOutputLevel(this.hangingPosition, Blocks.AIR);
      }

   }

   public boolean replaceItemInInventory(int p_174820_1_, ItemStack p_174820_2_) {
      if (p_174820_1_ == 0) {
         this.setDisplayedItem(p_174820_2_);
         return true;
      } else {
         return false;
      }
   }

   public void notifyDataManagerChange(DataParameter<?> p_184206_1_) {
      if (p_184206_1_.equals(ITEM)) {
         ItemStack itemstack = this.getDisplayedItem();
         if (!itemstack.isEmpty() && itemstack.getItemFrame() != this) {
            itemstack.setItemFrame(this);
         }
      }

   }

   public int getRotation() {
      return (Integer)this.getDataManager().get(ROTATION);
   }

   public void setItemRotation(int p_82336_1_) {
      this.setRotation(p_82336_1_, true);
   }

   private void setRotation(int p_174865_1_, boolean p_174865_2_) {
      this.getDataManager().set(ROTATION, p_174865_1_ % 8);
      if (p_174865_2_ && this.hangingPosition != null) {
         this.world.updateComparatorOutputLevel(this.hangingPosition, Blocks.AIR);
      }

   }

   public void writeAdditional(CompoundNBT p_213281_1_) {
      super.writeAdditional(p_213281_1_);
      if (!this.getDisplayedItem().isEmpty()) {
         p_213281_1_.put("Item", this.getDisplayedItem().write(new CompoundNBT()));
         p_213281_1_.putByte("ItemRotation", (byte)this.getRotation());
         p_213281_1_.putFloat("ItemDropChance", this.itemDropChance);
      }

      p_213281_1_.putByte("Facing", (byte)this.facingDirection.getIndex());
   }

   public void readAdditional(CompoundNBT p_70037_1_) {
      super.readAdditional(p_70037_1_);
      CompoundNBT compoundnbt = p_70037_1_.getCompound("Item");
      if (compoundnbt != null && !compoundnbt.isEmpty()) {
         ItemStack itemstack = ItemStack.read(compoundnbt);
         if (itemstack.isEmpty()) {
            PRIVATE_LOGGER.warn("Unable to load item from: {}", compoundnbt);
         }

         ItemStack itemstack1 = this.getDisplayedItem();
         if (!itemstack1.isEmpty() && !ItemStack.areItemStacksEqual(itemstack, itemstack1)) {
            this.removeItem(itemstack1);
         }

         this.setDisplayedItemWithUpdate(itemstack, false);
         this.setRotation(p_70037_1_.getByte("ItemRotation"), false);
         if (p_70037_1_.contains("ItemDropChance", 99)) {
            this.itemDropChance = p_70037_1_.getFloat("ItemDropChance");
         }
      }

      this.updateFacingWithBoundingBox(Direction.byIndex(p_70037_1_.getByte("Facing")));
   }

   public boolean processInitialInteract(PlayerEntity p_184230_1_, Hand p_184230_2_) {
      ItemStack itemstack = p_184230_1_.getHeldItem(p_184230_2_);
      boolean flag = !this.getDisplayedItem().isEmpty();
      boolean flag1 = !itemstack.isEmpty();
      if (!this.world.isRemote) {
         if (!flag) {
            if (flag1) {
               this.setDisplayedItem(itemstack);
               if (!p_184230_1_.abilities.isCreativeMode) {
                  itemstack.shrink(1);
               }
            }
         } else {
            this.playSound(SoundEvents.ENTITY_ITEM_FRAME_ROTATE_ITEM, 1.0F, 1.0F);
            this.setItemRotation(this.getRotation() + 1);
         }

         return true;
      } else {
         return flag || flag1;
      }
   }

   public int getAnalogOutput() {
      return this.getDisplayedItem().isEmpty() ? 0 : this.getRotation() % 8 + 1;
   }

   public IPacket<?> createSpawnPacket() {
      return new SSpawnObjectPacket(this, this.getType(), this.facingDirection.getIndex(), this.getHangingPosition());
   }

   static {
      ITEM = EntityDataManager.createKey(ItemFrameEntity.class, DataSerializers.ITEMSTACK);
      ROTATION = EntityDataManager.createKey(ItemFrameEntity.class, DataSerializers.VARINT);
   }
}
