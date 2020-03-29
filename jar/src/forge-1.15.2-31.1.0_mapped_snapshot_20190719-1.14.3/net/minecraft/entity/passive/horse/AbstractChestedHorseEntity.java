package net.minecraft.entity.passive.horse;

import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

public abstract class AbstractChestedHorseEntity extends AbstractHorseEntity {
   private static final DataParameter<Boolean> DATA_ID_CHEST;

   protected AbstractChestedHorseEntity(EntityType<? extends AbstractChestedHorseEntity> p_i48564_1_, World p_i48564_2_) {
      super(p_i48564_1_, p_i48564_2_);
      this.canGallop = false;
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(DATA_ID_CHEST, false);
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue((double)this.getModifiedMaxHealth());
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.17499999701976776D);
      this.getAttribute(JUMP_STRENGTH).setBaseValue(0.5D);
   }

   public boolean hasChest() {
      return (Boolean)this.dataManager.get(DATA_ID_CHEST);
   }

   public void setChested(boolean p_110207_1_) {
      this.dataManager.set(DATA_ID_CHEST, p_110207_1_);
   }

   protected int getInventorySize() {
      return this.hasChest() ? 17 : super.getInventorySize();
   }

   public double getMountedYOffset() {
      return super.getMountedYOffset() - 0.25D;
   }

   protected SoundEvent getAngrySound() {
      super.getAngrySound();
      return SoundEvents.ENTITY_DONKEY_ANGRY;
   }

   protected void dropInventory() {
      super.dropInventory();
      if (this.hasChest()) {
         if (!this.world.isRemote) {
            this.entityDropItem(Blocks.CHEST);
         }

         this.setChested(false);
      }

   }

   public void writeAdditional(CompoundNBT p_213281_1_) {
      super.writeAdditional(p_213281_1_);
      p_213281_1_.putBoolean("ChestedHorse", this.hasChest());
      if (this.hasChest()) {
         ListNBT lvt_2_1_ = new ListNBT();

         for(int lvt_3_1_ = 2; lvt_3_1_ < this.horseChest.getSizeInventory(); ++lvt_3_1_) {
            ItemStack lvt_4_1_ = this.horseChest.getStackInSlot(lvt_3_1_);
            if (!lvt_4_1_.isEmpty()) {
               CompoundNBT lvt_5_1_ = new CompoundNBT();
               lvt_5_1_.putByte("Slot", (byte)lvt_3_1_);
               lvt_4_1_.write(lvt_5_1_);
               lvt_2_1_.add(lvt_5_1_);
            }
         }

         p_213281_1_.put("Items", lvt_2_1_);
      }

   }

   public void readAdditional(CompoundNBT p_70037_1_) {
      super.readAdditional(p_70037_1_);
      this.setChested(p_70037_1_.getBoolean("ChestedHorse"));
      if (this.hasChest()) {
         ListNBT lvt_2_1_ = p_70037_1_.getList("Items", 10);
         this.initHorseChest();

         for(int lvt_3_1_ = 0; lvt_3_1_ < lvt_2_1_.size(); ++lvt_3_1_) {
            CompoundNBT lvt_4_1_ = lvt_2_1_.getCompound(lvt_3_1_);
            int lvt_5_1_ = lvt_4_1_.getByte("Slot") & 255;
            if (lvt_5_1_ >= 2 && lvt_5_1_ < this.horseChest.getSizeInventory()) {
               this.horseChest.setInventorySlotContents(lvt_5_1_, ItemStack.read(lvt_4_1_));
            }
         }
      }

      this.updateHorseSlots();
   }

   public boolean replaceItemInInventory(int p_174820_1_, ItemStack p_174820_2_) {
      if (p_174820_1_ == 499) {
         if (this.hasChest() && p_174820_2_.isEmpty()) {
            this.setChested(false);
            this.initHorseChest();
            return true;
         }

         if (!this.hasChest() && p_174820_2_.getItem() == Blocks.CHEST.asItem()) {
            this.setChested(true);
            this.initHorseChest();
            return true;
         }
      }

      return super.replaceItemInInventory(p_174820_1_, p_174820_2_);
   }

   public boolean processInteract(PlayerEntity p_184645_1_, Hand p_184645_2_) {
      ItemStack lvt_3_1_ = p_184645_1_.getHeldItem(p_184645_2_);
      if (lvt_3_1_.getItem() instanceof SpawnEggItem) {
         return super.processInteract(p_184645_1_, p_184645_2_);
      } else {
         if (!this.isChild()) {
            if (this.isTame() && p_184645_1_.func_226563_dT_()) {
               this.openGUI(p_184645_1_);
               return true;
            }

            if (this.isBeingRidden()) {
               return super.processInteract(p_184645_1_, p_184645_2_);
            }
         }

         if (!lvt_3_1_.isEmpty()) {
            boolean lvt_4_1_ = this.handleEating(p_184645_1_, lvt_3_1_);
            if (!lvt_4_1_) {
               if (!this.isTame() || lvt_3_1_.getItem() == Items.NAME_TAG) {
                  if (lvt_3_1_.interactWithEntity(p_184645_1_, this, p_184645_2_)) {
                     return true;
                  } else {
                     this.makeMad();
                     return true;
                  }
               }

               if (!this.hasChest() && lvt_3_1_.getItem() == Blocks.CHEST.asItem()) {
                  this.setChested(true);
                  this.playChestEquipSound();
                  lvt_4_1_ = true;
                  this.initHorseChest();
               }

               if (!this.isChild() && !this.isHorseSaddled() && lvt_3_1_.getItem() == Items.SADDLE) {
                  this.openGUI(p_184645_1_);
                  return true;
               }
            }

            if (lvt_4_1_) {
               if (!p_184645_1_.abilities.isCreativeMode) {
                  lvt_3_1_.shrink(1);
               }

               return true;
            }
         }

         if (this.isChild()) {
            return super.processInteract(p_184645_1_, p_184645_2_);
         } else {
            this.mountTo(p_184645_1_);
            return true;
         }
      }
   }

   protected void playChestEquipSound() {
      this.playSound(SoundEvents.ENTITY_DONKEY_CHEST, 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
   }

   public int getInventoryColumns() {
      return 5;
   }

   static {
      DATA_ID_CHEST = EntityDataManager.createKey(AbstractChestedHorseEntity.class, DataSerializers.BOOLEAN);
   }
}
