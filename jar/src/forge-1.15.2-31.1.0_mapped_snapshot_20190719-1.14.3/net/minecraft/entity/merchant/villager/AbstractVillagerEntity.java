package net.minecraft.entity.merchant.villager;

import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.INPC;
import net.minecraft.entity.Pose;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.merchant.IMerchant;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MerchantOffer;
import net.minecraft.item.MerchantOffers;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.IParticleData;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.ITeleporter;

public abstract class AbstractVillagerEntity extends AgeableEntity implements INPC, IMerchant {
   private static final DataParameter<Integer> SHAKE_HEAD_TICKS;
   @Nullable
   private PlayerEntity customer;
   @Nullable
   protected MerchantOffers offers;
   private final Inventory field_213722_bB = new Inventory(8);

   public AbstractVillagerEntity(EntityType<? extends AbstractVillagerEntity> p_i50185_1_, World p_i50185_2_) {
      super(p_i50185_1_, p_i50185_2_);
   }

   public ILivingEntityData onInitialSpawn(IWorld p_213386_1_, DifficultyInstance p_213386_2_, SpawnReason p_213386_3_, @Nullable ILivingEntityData p_213386_4_, @Nullable CompoundNBT p_213386_5_) {
      if (p_213386_4_ == null) {
         p_213386_4_ = new AgeableEntity.AgeableData();
         ((AgeableEntity.AgeableData)p_213386_4_).func_226259_a_(false);
      }

      return super.onInitialSpawn(p_213386_1_, p_213386_2_, p_213386_3_, (ILivingEntityData)p_213386_4_, p_213386_5_);
   }

   public int getShakeHeadTicks() {
      return (Integer)this.dataManager.get(SHAKE_HEAD_TICKS);
   }

   public void setShakeHeadTicks(int p_213720_1_) {
      this.dataManager.set(SHAKE_HEAD_TICKS, p_213720_1_);
   }

   public int getXp() {
      return 0;
   }

   protected float getStandingEyeHeight(Pose p_213348_1_, EntitySize p_213348_2_) {
      return this.isChild() ? 0.81F : 1.62F;
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(SHAKE_HEAD_TICKS, 0);
   }

   public void setCustomer(@Nullable PlayerEntity p_70932_1_) {
      this.customer = p_70932_1_;
   }

   @Nullable
   public PlayerEntity getCustomer() {
      return this.customer;
   }

   public boolean func_213716_dX() {
      return this.customer != null;
   }

   public MerchantOffers getOffers() {
      if (this.offers == null) {
         this.offers = new MerchantOffers();
         this.populateTradeData();
      }

      return this.offers;
   }

   @OnlyIn(Dist.CLIENT)
   public void func_213703_a(@Nullable MerchantOffers p_213703_1_) {
   }

   public void func_213702_q(int p_213702_1_) {
   }

   public void onTrade(MerchantOffer p_213704_1_) {
      p_213704_1_.func_222219_j();
      this.livingSoundTime = -this.getTalkInterval();
      this.func_213713_b(p_213704_1_);
      if (this.customer instanceof ServerPlayerEntity) {
         CriteriaTriggers.VILLAGER_TRADE.func_215114_a((ServerPlayerEntity)this.customer, this, p_213704_1_.func_222200_d());
      }

   }

   protected abstract void func_213713_b(MerchantOffer var1);

   public boolean func_213705_dZ() {
      return true;
   }

   public void verifySellingItem(ItemStack p_110297_1_) {
      if (!this.world.isRemote && this.livingSoundTime > -this.getTalkInterval() + 20) {
         this.livingSoundTime = -this.getTalkInterval();
         this.playSound(this.func_213721_r(!p_110297_1_.isEmpty()), this.getSoundVolume(), this.getSoundPitch());
      }

   }

   public SoundEvent func_213714_ea() {
      return SoundEvents.ENTITY_VILLAGER_YES;
   }

   protected SoundEvent func_213721_r(boolean p_213721_1_) {
      return p_213721_1_ ? SoundEvents.ENTITY_VILLAGER_YES : SoundEvents.ENTITY_VILLAGER_NO;
   }

   public void func_213711_eb() {
      this.playSound(SoundEvents.ENTITY_VILLAGER_CELEBRATE, this.getSoundVolume(), this.getSoundPitch());
   }

   public void writeAdditional(CompoundNBT p_213281_1_) {
      super.writeAdditional(p_213281_1_);
      MerchantOffers merchantoffers = this.getOffers();
      if (!merchantoffers.isEmpty()) {
         p_213281_1_.put("Offers", merchantoffers.func_222199_a());
      }

      ListNBT listnbt = new ListNBT();

      for(int i = 0; i < this.field_213722_bB.getSizeInventory(); ++i) {
         ItemStack itemstack = this.field_213722_bB.getStackInSlot(i);
         if (!itemstack.isEmpty()) {
            listnbt.add(itemstack.write(new CompoundNBT()));
         }
      }

      p_213281_1_.put("Inventory", listnbt);
   }

   public void readAdditional(CompoundNBT p_70037_1_) {
      super.readAdditional(p_70037_1_);
      if (p_70037_1_.contains("Offers", 10)) {
         this.offers = new MerchantOffers(p_70037_1_.getCompound("Offers"));
      }

      ListNBT listnbt = p_70037_1_.getList("Inventory", 10);

      for(int i = 0; i < listnbt.size(); ++i) {
         ItemStack itemstack = ItemStack.read(listnbt.getCompound(i));
         if (!itemstack.isEmpty()) {
            this.field_213722_bB.addItem(itemstack);
         }
      }

   }

   @Nullable
   public Entity changeDimension(DimensionType p_changeDimension_1_, ITeleporter p_changeDimension_2_) {
      this.func_213750_eg();
      return super.changeDimension(p_changeDimension_1_, p_changeDimension_2_);
   }

   protected void func_213750_eg() {
      this.setCustomer((PlayerEntity)null);
   }

   public void onDeath(DamageSource p_70645_1_) {
      super.onDeath(p_70645_1_);
      this.func_213750_eg();
   }

   @OnlyIn(Dist.CLIENT)
   protected void func_213718_a(IParticleData p_213718_1_) {
      for(int i = 0; i < 5; ++i) {
         double d0 = this.rand.nextGaussian() * 0.02D;
         double d1 = this.rand.nextGaussian() * 0.02D;
         double d2 = this.rand.nextGaussian() * 0.02D;
         this.world.addParticle(p_213718_1_, this.func_226282_d_(1.0D), this.func_226279_cv_() + 1.0D, this.func_226287_g_(1.0D), d0, d1, d2);
      }

   }

   public boolean canBeLeashedTo(PlayerEntity p_184652_1_) {
      return false;
   }

   public Inventory func_213715_ed() {
      return this.field_213722_bB;
   }

   public boolean replaceItemInInventory(int p_174820_1_, ItemStack p_174820_2_) {
      if (super.replaceItemInInventory(p_174820_1_, p_174820_2_)) {
         return true;
      } else {
         int i = p_174820_1_ - 300;
         if (i >= 0 && i < this.field_213722_bB.getSizeInventory()) {
            this.field_213722_bB.setInventorySlotContents(i, p_174820_2_);
            return true;
         } else {
            return false;
         }
      }
   }

   public World getWorld() {
      return this.world;
   }

   protected abstract void populateTradeData();

   protected void addTrades(MerchantOffers p_213717_1_, VillagerTrades.ITrade[] p_213717_2_, int p_213717_3_) {
      Set<Integer> set = Sets.newHashSet();
      if (p_213717_2_.length > p_213717_3_) {
         while(set.size() < p_213717_3_) {
            set.add(this.rand.nextInt(p_213717_2_.length));
         }
      } else {
         for(int i = 0; i < p_213717_2_.length; ++i) {
            set.add(i);
         }
      }

      Iterator var9 = set.iterator();

      while(var9.hasNext()) {
         Integer integer = (Integer)var9.next();
         VillagerTrades.ITrade villagertrades$itrade = p_213717_2_[integer];
         MerchantOffer merchantoffer = villagertrades$itrade.getOffer(this, this.rand);
         if (merchantoffer != null) {
            p_213717_1_.add(merchantoffer);
         }
      }

   }

   static {
      SHAKE_HEAD_TICKS = EntityDataManager.createKey(AbstractVillagerEntity.class, DataSerializers.VARINT);
   }
}
