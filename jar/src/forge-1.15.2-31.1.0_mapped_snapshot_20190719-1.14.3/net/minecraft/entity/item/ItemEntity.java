package net.minecraft.entity.item;

import java.util.Iterator;
import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.ITeleporter;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.hooks.BasicEventHooks;

public class ItemEntity extends Entity {
   private static final DataParameter<ItemStack> ITEM;
   private int age;
   private int pickupDelay;
   private int health;
   private UUID thrower;
   private UUID owner;
   public int lifespan;
   public final float hoverStart;

   public ItemEntity(EntityType<? extends ItemEntity> p_i50217_1_, World p_i50217_2_) {
      super(p_i50217_1_, p_i50217_2_);
      this.health = 5;
      this.lifespan = 6000;
      this.hoverStart = (float)(Math.random() * 3.141592653589793D * 2.0D);
   }

   public ItemEntity(World p_i1709_1_, double p_i1709_2_, double p_i1709_4_, double p_i1709_6_) {
      this(EntityType.ITEM, p_i1709_1_);
      this.setPosition(p_i1709_2_, p_i1709_4_, p_i1709_6_);
      this.rotationYaw = this.rand.nextFloat() * 360.0F;
      this.setMotion(this.rand.nextDouble() * 0.2D - 0.1D, 0.2D, this.rand.nextDouble() * 0.2D - 0.1D);
   }

   public ItemEntity(World p_i1710_1_, double p_i1710_2_, double p_i1710_4_, double p_i1710_6_, ItemStack p_i1710_8_) {
      this(p_i1710_1_, p_i1710_2_, p_i1710_4_, p_i1710_6_);
      this.setItem(p_i1710_8_);
      this.lifespan = p_i1710_8_.getItem() == null ? 6000 : p_i1710_8_.getEntityLifespan(p_i1710_1_);
   }

   protected boolean func_225502_at_() {
      return false;
   }

   protected void registerData() {
      this.getDataManager().register(ITEM, ItemStack.EMPTY);
   }

   public void tick() {
      if (!this.getItem().onEntityItemUpdate(this)) {
         if (this.getItem().isEmpty()) {
            this.remove();
         } else {
            super.tick();
            if (this.pickupDelay > 0 && this.pickupDelay != 32767) {
               --this.pickupDelay;
            }

            this.prevPosX = this.func_226277_ct_();
            this.prevPosY = this.func_226278_cu_();
            this.prevPosZ = this.func_226281_cx_();
            Vec3d vec3d = this.getMotion();
            if (this.areEyesInFluid(FluidTags.WATER)) {
               this.applyFloatMotion();
            } else if (!this.hasNoGravity()) {
               this.setMotion(this.getMotion().add(0.0D, -0.04D, 0.0D));
            }

            if (this.world.isRemote) {
               this.noClip = false;
            } else {
               this.noClip = !this.world.func_226669_j_(this);
               if (this.noClip) {
                  this.pushOutOfBlocks(this.func_226277_ct_(), (this.getBoundingBox().minY + this.getBoundingBox().maxY) / 2.0D, this.func_226281_cx_());
               }
            }

            if (!this.onGround || func_213296_b(this.getMotion()) > 9.999999747378752E-6D || (this.ticksExisted + this.getEntityId()) % 4 == 0) {
               this.move(MoverType.SELF, this.getMotion());
               float f = 0.98F;
               if (this.onGround) {
                  BlockPos pos = new BlockPos(this.func_226277_ct_(), this.func_226278_cu_() - 1.0D, this.func_226281_cx_());
                  f = this.world.getBlockState(pos).getSlipperiness(this.world, pos, this) * 0.98F;
               }

               this.setMotion(this.getMotion().mul((double)f, 0.98D, (double)f));
               if (this.onGround) {
                  this.setMotion(this.getMotion().mul(1.0D, -0.5D, 1.0D));
               }
            }

            boolean flag = MathHelper.floor(this.prevPosX) != MathHelper.floor(this.func_226277_ct_()) || MathHelper.floor(this.prevPosY) != MathHelper.floor(this.func_226278_cu_()) || MathHelper.floor(this.prevPosZ) != MathHelper.floor(this.func_226281_cx_());
            int i = flag ? 2 : 40;
            if (this.ticksExisted % i == 0) {
               if (this.world.getFluidState(new BlockPos(this)).isTagged(FluidTags.LAVA)) {
                  this.setMotion((double)((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F), 0.20000000298023224D, (double)((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F));
                  this.playSound(SoundEvents.ENTITY_GENERIC_BURN, 0.4F, 2.0F + this.rand.nextFloat() * 0.4F);
               }

               if (!this.world.isRemote && this.func_213857_z()) {
                  this.searchForOtherItemsNearby();
               }
            }

            if (this.age != -32768) {
               ++this.age;
            }

            this.isAirBorne |= this.handleWaterMovement();
            if (!this.world.isRemote) {
               double d0 = this.getMotion().subtract(vec3d).lengthSquared();
               if (d0 > 0.01D) {
                  this.isAirBorne = true;
               }
            }

            ItemStack item = this.getItem();
            if (!this.world.isRemote && this.age >= this.lifespan) {
               int hook = ForgeEventFactory.onItemExpire(this, item);
               if (hook < 0) {
                  this.remove();
               } else {
                  this.lifespan += hook;
               }
            }

            if (item.isEmpty()) {
               this.remove();
            }
         }

      }
   }

   private void applyFloatMotion() {
      Vec3d vec3d = this.getMotion();
      this.setMotion(vec3d.x * 0.9900000095367432D, vec3d.y + (double)(vec3d.y < 0.05999999865889549D ? 5.0E-4F : 0.0F), vec3d.z * 0.9900000095367432D);
   }

   private void searchForOtherItemsNearby() {
      if (this.func_213857_z()) {
         Iterator var1 = this.world.getEntitiesWithinAABB(ItemEntity.class, this.getBoundingBox().grow(0.5D, 0.0D, 0.5D), (p_lambda$searchForOtherItemsNearby$0_1_) -> {
            return p_lambda$searchForOtherItemsNearby$0_1_ != this && p_lambda$searchForOtherItemsNearby$0_1_.func_213857_z();
         }).iterator();

         while(var1.hasNext()) {
            ItemEntity itementity = (ItemEntity)var1.next();
            if (itementity.func_213857_z()) {
               this.func_226530_a_(itementity);
               if (this.removed) {
                  break;
               }
            }
         }
      }

   }

   private boolean func_213857_z() {
      ItemStack itemstack = this.getItem();
      return this.isAlive() && this.pickupDelay != 32767 && this.age != -32768 && this.age < 6000 && itemstack.getCount() < itemstack.getMaxStackSize();
   }

   private void func_226530_a_(ItemEntity p_226530_1_) {
      ItemStack itemstack = this.getItem();
      ItemStack itemstack1 = p_226530_1_.getItem();
      if (Objects.equals(this.getOwnerId(), p_226530_1_.getOwnerId()) && func_226532_a_(itemstack, itemstack1)) {
         if (itemstack1.getCount() < itemstack.getCount()) {
            func_213858_a(this, itemstack, p_226530_1_, itemstack1);
         } else {
            func_213858_a(p_226530_1_, itemstack1, this, itemstack);
         }
      }

   }

   public static boolean func_226532_a_(ItemStack p_226532_0_, ItemStack p_226532_1_) {
      if (p_226532_1_.getItem() != p_226532_0_.getItem()) {
         return false;
      } else if (p_226532_1_.getCount() + p_226532_0_.getCount() > p_226532_1_.getMaxStackSize()) {
         return false;
      } else if (p_226532_1_.hasTag() ^ p_226532_0_.hasTag()) {
         return false;
      } else if (!p_226532_0_.areCapsCompatible(p_226532_1_)) {
         return false;
      } else {
         return !p_226532_1_.hasTag() || p_226532_1_.getTag().equals(p_226532_0_.getTag());
      }
   }

   public static ItemStack func_226533_a_(ItemStack p_226533_0_, ItemStack p_226533_1_, int p_226533_2_) {
      int i = Math.min(Math.min(p_226533_0_.getMaxStackSize(), p_226533_2_) - p_226533_0_.getCount(), p_226533_1_.getCount());
      ItemStack itemstack = p_226533_0_.copy();
      itemstack.grow(i);
      p_226533_1_.shrink(i);
      return itemstack;
   }

   private static void func_226531_a_(ItemEntity p_226531_0_, ItemStack p_226531_1_, ItemStack p_226531_2_) {
      ItemStack itemstack = func_226533_a_(p_226531_1_, p_226531_2_, 64);
      p_226531_0_.setItem(itemstack);
   }

   private static void func_213858_a(ItemEntity p_213858_0_, ItemStack p_213858_1_, ItemEntity p_213858_2_, ItemStack p_213858_3_) {
      func_226531_a_(p_213858_0_, p_213858_1_, p_213858_3_);
      p_213858_0_.pickupDelay = Math.max(p_213858_0_.pickupDelay, p_213858_2_.pickupDelay);
      p_213858_0_.age = Math.min(p_213858_0_.age, p_213858_2_.age);
      if (p_213858_3_.isEmpty()) {
         p_213858_2_.remove();
      }

   }

   protected void dealFireDamage(int p_70081_1_) {
      this.attackEntityFrom(DamageSource.IN_FIRE, (float)p_70081_1_);
   }

   public boolean attackEntityFrom(DamageSource p_70097_1_, float p_70097_2_) {
      if (!this.world.isRemote && !this.removed) {
         if (this.isInvulnerableTo(p_70097_1_)) {
            return false;
         } else if (!this.getItem().isEmpty() && this.getItem().getItem() == Items.NETHER_STAR && p_70097_1_.isExplosion()) {
            return false;
         } else {
            this.markVelocityChanged();
            this.health = (int)((float)this.health - p_70097_2_);
            if (this.health <= 0) {
               this.remove();
            }

            return false;
         }
      } else {
         return false;
      }
   }

   public void writeAdditional(CompoundNBT p_213281_1_) {
      p_213281_1_.putShort("Health", (short)this.health);
      p_213281_1_.putShort("Age", (short)this.age);
      p_213281_1_.putShort("PickupDelay", (short)this.pickupDelay);
      p_213281_1_.putInt("Lifespan", this.lifespan);
      if (this.getThrowerId() != null) {
         p_213281_1_.put("Thrower", NBTUtil.writeUniqueId(this.getThrowerId()));
      }

      if (this.getOwnerId() != null) {
         p_213281_1_.put("Owner", NBTUtil.writeUniqueId(this.getOwnerId()));
      }

      if (!this.getItem().isEmpty()) {
         p_213281_1_.put("Item", this.getItem().write(new CompoundNBT()));
      }

   }

   public void readAdditional(CompoundNBT p_70037_1_) {
      this.health = p_70037_1_.getShort("Health");
      this.age = p_70037_1_.getShort("Age");
      if (p_70037_1_.contains("PickupDelay")) {
         this.pickupDelay = p_70037_1_.getShort("PickupDelay");
      }

      if (p_70037_1_.contains("Lifespan")) {
         this.lifespan = p_70037_1_.getInt("Lifespan");
      }

      if (p_70037_1_.contains("Owner", 10)) {
         this.owner = NBTUtil.readUniqueId(p_70037_1_.getCompound("Owner"));
      }

      if (p_70037_1_.contains("Thrower", 10)) {
         this.thrower = NBTUtil.readUniqueId(p_70037_1_.getCompound("Thrower"));
      }

      CompoundNBT compoundnbt = p_70037_1_.getCompound("Item");
      this.setItem(ItemStack.read(compoundnbt));
      if (this.getItem().isEmpty()) {
         this.remove();
      }

   }

   public void onCollideWithPlayer(PlayerEntity p_70100_1_) {
      if (!this.world.isRemote) {
         if (this.pickupDelay > 0) {
            return;
         }

         ItemStack itemstack = this.getItem();
         Item item = itemstack.getItem();
         int i = itemstack.getCount();
         int hook = ForgeEventFactory.onItemPickup(this, p_70100_1_);
         if (hook < 0) {
            return;
         }

         ItemStack copy = itemstack.copy();
         if (this.pickupDelay == 0 && (this.owner == null || this.lifespan - this.age <= 200 || this.owner.equals(p_70100_1_.getUniqueID())) && (hook == 1 || i <= 0 || p_70100_1_.inventory.addItemStackToInventory(itemstack))) {
            copy.setCount(copy.getCount() - this.getItem().getCount());
            BasicEventHooks.firePlayerItemPickupEvent(p_70100_1_, this, copy);
            p_70100_1_.onItemPickup(this, i);
            if (itemstack.isEmpty()) {
               p_70100_1_.onItemPickup(this, i);
               this.remove();
               itemstack.setCount(i);
            }

            p_70100_1_.addStat(Stats.ITEM_PICKED_UP.get(item), i);
         }
      }

   }

   public ITextComponent getName() {
      ITextComponent itextcomponent = this.getCustomName();
      return (ITextComponent)(itextcomponent != null ? itextcomponent : new TranslationTextComponent(this.getItem().getTranslationKey(), new Object[0]));
   }

   public boolean canBeAttackedWithItem() {
      return false;
   }

   @Nullable
   public Entity changeDimension(DimensionType p_changeDimension_1_, ITeleporter p_changeDimension_2_) {
      Entity entity = super.changeDimension(p_changeDimension_1_, p_changeDimension_2_);
      if (!this.world.isRemote && entity instanceof ItemEntity) {
         ((ItemEntity)entity).searchForOtherItemsNearby();
      }

      return entity;
   }

   public ItemStack getItem() {
      return (ItemStack)this.getDataManager().get(ITEM);
   }

   public void setItem(ItemStack p_92058_1_) {
      this.getDataManager().set(ITEM, p_92058_1_);
   }

   @Nullable
   public UUID getOwnerId() {
      return this.owner;
   }

   public void setOwnerId(@Nullable UUID p_200217_1_) {
      this.owner = p_200217_1_;
   }

   @Nullable
   public UUID getThrowerId() {
      return this.thrower;
   }

   public void setThrowerId(@Nullable UUID p_200216_1_) {
      this.thrower = p_200216_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public int getAge() {
      return this.age;
   }

   public void setDefaultPickupDelay() {
      this.pickupDelay = 10;
   }

   public void setNoPickupDelay() {
      this.pickupDelay = 0;
   }

   public void setInfinitePickupDelay() {
      this.pickupDelay = 32767;
   }

   public void setPickupDelay(int p_174867_1_) {
      this.pickupDelay = p_174867_1_;
   }

   public boolean cannotPickup() {
      return this.pickupDelay > 0;
   }

   public void setNoDespawn() {
      this.age = -6000;
   }

   public void makeFakeItem() {
      this.setInfinitePickupDelay();
      this.age = this.getItem().getEntityLifespan(this.world) - 1;
   }

   public IPacket<?> createSpawnPacket() {
      return new SSpawnObjectPacket(this);
   }

   static {
      ITEM = EntityDataManager.createKey(ItemEntity.class, DataSerializers.ITEMSTACK);
   }
}
