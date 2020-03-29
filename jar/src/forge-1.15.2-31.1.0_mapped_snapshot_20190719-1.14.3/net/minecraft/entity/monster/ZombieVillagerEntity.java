package net.minecraft.entity.monster;

import com.mojang.datafixers.Dynamic;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.merchant.IReputationType;
import net.minecraft.entity.merchant.villager.VillagerData;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.villager.IVillagerDataHolder;
import net.minecraft.entity.villager.IVillagerType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MerchantOffers;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ZombieVillagerEntity extends ZombieEntity implements IVillagerDataHolder {
   private static final DataParameter<Boolean> CONVERTING;
   private static final DataParameter<VillagerData> field_213795_c;
   private int conversionTime;
   private UUID converstionStarter;
   private INBT field_223728_bB;
   private CompoundNBT field_213793_bB;
   private int field_213794_bC;

   public ZombieVillagerEntity(EntityType<? extends ZombieVillagerEntity> p_i50186_1_, World p_i50186_2_) {
      super(p_i50186_1_, p_i50186_2_);
      this.func_213792_a(this.getVillagerData().withProfession((VillagerProfession)Registry.VILLAGER_PROFESSION.getRandom(this.rand)));
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(CONVERTING, false);
      this.dataManager.register(field_213795_c, new VillagerData(IVillagerType.PLAINS, VillagerProfession.NONE, 1));
   }

   public void writeAdditional(CompoundNBT p_213281_1_) {
      super.writeAdditional(p_213281_1_);
      p_213281_1_.put("VillagerData", (INBT)this.getVillagerData().serialize(NBTDynamicOps.INSTANCE));
      if (this.field_213793_bB != null) {
         p_213281_1_.put("Offers", this.field_213793_bB);
      }

      if (this.field_223728_bB != null) {
         p_213281_1_.put("Gossips", this.field_223728_bB);
      }

      p_213281_1_.putInt("ConversionTime", this.isConverting() ? this.conversionTime : -1);
      if (this.converstionStarter != null) {
         p_213281_1_.putUniqueId("ConversionPlayer", this.converstionStarter);
      }

      p_213281_1_.putInt("Xp", this.field_213794_bC);
   }

   public void readAdditional(CompoundNBT p_70037_1_) {
      super.readAdditional(p_70037_1_);
      if (p_70037_1_.contains("VillagerData", 10)) {
         this.func_213792_a(new VillagerData(new Dynamic(NBTDynamicOps.INSTANCE, p_70037_1_.get("VillagerData"))));
      }

      if (p_70037_1_.contains("Offers", 10)) {
         this.field_213793_bB = p_70037_1_.getCompound("Offers");
      }

      if (p_70037_1_.contains("Gossips", 10)) {
         this.field_223728_bB = p_70037_1_.getList("Gossips", 10);
      }

      if (p_70037_1_.contains("ConversionTime", 99) && p_70037_1_.getInt("ConversionTime") > -1) {
         this.startConverting(p_70037_1_.hasUniqueId("ConversionPlayer") ? p_70037_1_.getUniqueId("ConversionPlayer") : null, p_70037_1_.getInt("ConversionTime"));
      }

      if (p_70037_1_.contains("Xp", 3)) {
         this.field_213794_bC = p_70037_1_.getInt("Xp");
      }

   }

   public void tick() {
      if (!this.world.isRemote && this.isAlive() && this.isConverting()) {
         int lvt_1_1_ = this.getConversionProgress();
         this.conversionTime -= lvt_1_1_;
         if (this.conversionTime <= 0) {
            this.func_213791_a((ServerWorld)this.world);
         }
      }

      super.tick();
   }

   public boolean processInteract(PlayerEntity p_184645_1_, Hand p_184645_2_) {
      ItemStack lvt_3_1_ = p_184645_1_.getHeldItem(p_184645_2_);
      if (lvt_3_1_.getItem() == Items.GOLDEN_APPLE && this.isPotionActive(Effects.WEAKNESS)) {
         if (!p_184645_1_.abilities.isCreativeMode) {
            lvt_3_1_.shrink(1);
         }

         if (!this.world.isRemote) {
            this.startConverting(p_184645_1_.getUniqueID(), this.rand.nextInt(2401) + 3600);
            p_184645_1_.func_226292_a_(p_184645_2_, true);
         }

         return true;
      } else {
         return super.processInteract(p_184645_1_, p_184645_2_);
      }
   }

   protected boolean shouldDrown() {
      return false;
   }

   public boolean canDespawn(double p_213397_1_) {
      return !this.isConverting();
   }

   public boolean isConverting() {
      return (Boolean)this.getDataManager().get(CONVERTING);
   }

   private void startConverting(@Nullable UUID p_191991_1_, int p_191991_2_) {
      this.converstionStarter = p_191991_1_;
      this.conversionTime = p_191991_2_;
      this.getDataManager().set(CONVERTING, true);
      this.removePotionEffect(Effects.WEAKNESS);
      this.addPotionEffect(new EffectInstance(Effects.STRENGTH, p_191991_2_, Math.min(this.world.getDifficulty().getId() - 1, 0)));
      this.world.setEntityState(this, (byte)16);
   }

   @OnlyIn(Dist.CLIENT)
   public void handleStatusUpdate(byte p_70103_1_) {
      if (p_70103_1_ == 16) {
         if (!this.isSilent()) {
            this.world.playSound(this.func_226277_ct_(), this.func_226280_cw_(), this.func_226281_cx_(), SoundEvents.ENTITY_ZOMBIE_VILLAGER_CURE, this.getSoundCategory(), 1.0F + this.rand.nextFloat(), this.rand.nextFloat() * 0.7F + 0.3F, false);
         }

      } else {
         super.handleStatusUpdate(p_70103_1_);
      }
   }

   private void func_213791_a(ServerWorld p_213791_1_) {
      VillagerEntity lvt_2_1_ = (VillagerEntity)EntityType.VILLAGER.create(p_213791_1_);
      EquipmentSlotType[] var3 = EquipmentSlotType.values();
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         EquipmentSlotType lvt_6_1_ = var3[var5];
         ItemStack lvt_7_1_ = this.getItemStackFromSlot(lvt_6_1_);
         if (!lvt_7_1_.isEmpty()) {
            if (EnchantmentHelper.hasBindingCurse(lvt_7_1_)) {
               lvt_2_1_.replaceItemInInventory(lvt_6_1_.getIndex() + 300, lvt_7_1_);
            } else {
               double lvt_8_1_ = (double)this.getDropChance(lvt_6_1_);
               if (lvt_8_1_ > 1.0D) {
                  this.entityDropItem(lvt_7_1_);
               }
            }
         }
      }

      lvt_2_1_.copyLocationAndAnglesFrom(this);
      lvt_2_1_.setVillagerData(this.getVillagerData());
      if (this.field_223728_bB != null) {
         lvt_2_1_.func_223716_a(this.field_223728_bB);
      }

      if (this.field_213793_bB != null) {
         lvt_2_1_.func_213768_b(new MerchantOffers(this.field_213793_bB));
      }

      lvt_2_1_.setXp(this.field_213794_bC);
      lvt_2_1_.onInitialSpawn(p_213791_1_, p_213791_1_.getDifficultyForLocation(new BlockPos(lvt_2_1_)), SpawnReason.CONVERSION, (ILivingEntityData)null, (CompoundNBT)null);
      if (this.isChild()) {
         lvt_2_1_.setGrowingAge(-24000);
      }

      this.remove();
      lvt_2_1_.setNoAI(this.isAIDisabled());
      if (this.hasCustomName()) {
         lvt_2_1_.setCustomName(this.getCustomName());
         lvt_2_1_.setCustomNameVisible(this.isCustomNameVisible());
      }

      if (this.isNoDespawnRequired()) {
         lvt_2_1_.enablePersistence();
      }

      lvt_2_1_.setInvulnerable(this.isInvulnerable());
      p_213791_1_.addEntity(lvt_2_1_);
      if (this.converstionStarter != null) {
         PlayerEntity lvt_3_1_ = p_213791_1_.getPlayerByUuid(this.converstionStarter);
         if (lvt_3_1_ instanceof ServerPlayerEntity) {
            CriteriaTriggers.CURED_ZOMBIE_VILLAGER.trigger((ServerPlayerEntity)lvt_3_1_, this, lvt_2_1_);
            p_213791_1_.func_217489_a(IReputationType.ZOMBIE_VILLAGER_CURED, lvt_3_1_, lvt_2_1_);
         }
      }

      lvt_2_1_.addPotionEffect(new EffectInstance(Effects.NAUSEA, 200, 0));
      p_213791_1_.playEvent((PlayerEntity)null, 1027, new BlockPos(this), 0);
   }

   private int getConversionProgress() {
      int lvt_1_1_ = 1;
      if (this.rand.nextFloat() < 0.01F) {
         int lvt_2_1_ = 0;
         BlockPos.Mutable lvt_3_1_ = new BlockPos.Mutable();

         for(int lvt_4_1_ = (int)this.func_226277_ct_() - 4; lvt_4_1_ < (int)this.func_226277_ct_() + 4 && lvt_2_1_ < 14; ++lvt_4_1_) {
            for(int lvt_5_1_ = (int)this.func_226278_cu_() - 4; lvt_5_1_ < (int)this.func_226278_cu_() + 4 && lvt_2_1_ < 14; ++lvt_5_1_) {
               for(int lvt_6_1_ = (int)this.func_226281_cx_() - 4; lvt_6_1_ < (int)this.func_226281_cx_() + 4 && lvt_2_1_ < 14; ++lvt_6_1_) {
                  Block lvt_7_1_ = this.world.getBlockState(lvt_3_1_.setPos(lvt_4_1_, lvt_5_1_, lvt_6_1_)).getBlock();
                  if (lvt_7_1_ == Blocks.IRON_BARS || lvt_7_1_ instanceof BedBlock) {
                     if (this.rand.nextFloat() < 0.3F) {
                        ++lvt_1_1_;
                     }

                     ++lvt_2_1_;
                  }
               }
            }
         }
      }

      return lvt_1_1_;
   }

   protected float getSoundPitch() {
      return this.isChild() ? (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 2.0F : (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F;
   }

   public SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_ZOMBIE_VILLAGER_AMBIENT;
   }

   public SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.ENTITY_ZOMBIE_VILLAGER_HURT;
   }

   public SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_ZOMBIE_VILLAGER_DEATH;
   }

   public SoundEvent getStepSound() {
      return SoundEvents.ENTITY_ZOMBIE_VILLAGER_STEP;
   }

   protected ItemStack getSkullDrop() {
      return ItemStack.EMPTY;
   }

   public void func_213790_g(CompoundNBT p_213790_1_) {
      this.field_213793_bB = p_213790_1_;
   }

   public void func_223727_a(INBT p_223727_1_) {
      this.field_223728_bB = p_223727_1_;
   }

   @Nullable
   public ILivingEntityData onInitialSpawn(IWorld p_213386_1_, DifficultyInstance p_213386_2_, SpawnReason p_213386_3_, @Nullable ILivingEntityData p_213386_4_, @Nullable CompoundNBT p_213386_5_) {
      this.func_213792_a(this.getVillagerData().withType(IVillagerType.byBiome(p_213386_1_.func_226691_t_(new BlockPos(this)))));
      return super.onInitialSpawn(p_213386_1_, p_213386_2_, p_213386_3_, p_213386_4_, p_213386_5_);
   }

   public void func_213792_a(VillagerData p_213792_1_) {
      VillagerData lvt_2_1_ = this.getVillagerData();
      if (lvt_2_1_.getProfession() != p_213792_1_.getProfession()) {
         this.field_213793_bB = null;
      }

      this.dataManager.set(field_213795_c, p_213792_1_);
   }

   public VillagerData getVillagerData() {
      return (VillagerData)this.dataManager.get(field_213795_c);
   }

   public void func_213789_a(int p_213789_1_) {
      this.field_213794_bC = p_213789_1_;
   }

   static {
      CONVERTING = EntityDataManager.createKey(ZombieVillagerEntity.class, DataSerializers.BOOLEAN);
      field_213795_c = EntityDataManager.createKey(ZombieVillagerEntity.class, DataSerializers.VILLAGER_DATA);
   }
}
