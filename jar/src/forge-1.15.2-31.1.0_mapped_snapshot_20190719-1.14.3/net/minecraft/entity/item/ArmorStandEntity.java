package net.minecraft.entity.item;

import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Rotations;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ArmorStandEntity extends LivingEntity {
   private static final Rotations DEFAULT_HEAD_ROTATION = new Rotations(0.0F, 0.0F, 0.0F);
   private static final Rotations DEFAULT_BODY_ROTATION = new Rotations(0.0F, 0.0F, 0.0F);
   private static final Rotations DEFAULT_LEFTARM_ROTATION = new Rotations(-10.0F, 0.0F, -10.0F);
   private static final Rotations DEFAULT_RIGHTARM_ROTATION = new Rotations(-15.0F, 0.0F, 10.0F);
   private static final Rotations DEFAULT_LEFTLEG_ROTATION = new Rotations(-1.0F, 0.0F, -1.0F);
   private static final Rotations DEFAULT_RIGHTLEG_ROTATION = new Rotations(1.0F, 0.0F, 1.0F);
   public static final DataParameter<Byte> STATUS;
   public static final DataParameter<Rotations> HEAD_ROTATION;
   public static final DataParameter<Rotations> BODY_ROTATION;
   public static final DataParameter<Rotations> LEFT_ARM_ROTATION;
   public static final DataParameter<Rotations> RIGHT_ARM_ROTATION;
   public static final DataParameter<Rotations> LEFT_LEG_ROTATION;
   public static final DataParameter<Rotations> RIGHT_LEG_ROTATION;
   private static final Predicate<Entity> IS_RIDEABLE_MINECART;
   private final NonNullList<ItemStack> handItems;
   private final NonNullList<ItemStack> armorItems;
   private boolean canInteract;
   public long punchCooldown;
   private int disabledSlots;
   private Rotations headRotation;
   private Rotations bodyRotation;
   private Rotations leftArmRotation;
   private Rotations rightArmRotation;
   private Rotations leftLegRotation;
   private Rotations rightLegRotation;

   public ArmorStandEntity(EntityType<? extends ArmorStandEntity> p_i50225_1_, World p_i50225_2_) {
      super(p_i50225_1_, p_i50225_2_);
      this.handItems = NonNullList.withSize(2, ItemStack.EMPTY);
      this.armorItems = NonNullList.withSize(4, ItemStack.EMPTY);
      this.headRotation = DEFAULT_HEAD_ROTATION;
      this.bodyRotation = DEFAULT_BODY_ROTATION;
      this.leftArmRotation = DEFAULT_LEFTARM_ROTATION;
      this.rightArmRotation = DEFAULT_RIGHTARM_ROTATION;
      this.leftLegRotation = DEFAULT_LEFTLEG_ROTATION;
      this.rightLegRotation = DEFAULT_RIGHTLEG_ROTATION;
      this.stepHeight = 0.0F;
   }

   public ArmorStandEntity(World p_i45855_1_, double p_i45855_2_, double p_i45855_4_, double p_i45855_6_) {
      this(EntityType.ARMOR_STAND, p_i45855_1_);
      this.setPosition(p_i45855_2_, p_i45855_4_, p_i45855_6_);
   }

   public void recalculateSize() {
      double d0 = this.func_226277_ct_();
      double d1 = this.func_226278_cu_();
      double d2 = this.func_226281_cx_();
      super.recalculateSize();
      this.setPosition(d0, d1, d2);
   }

   private boolean func_213814_A() {
      return !this.hasMarker() && !this.hasNoGravity();
   }

   public boolean isServerWorld() {
      return super.isServerWorld() && this.func_213814_A();
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(STATUS, (byte)0);
      this.dataManager.register(HEAD_ROTATION, DEFAULT_HEAD_ROTATION);
      this.dataManager.register(BODY_ROTATION, DEFAULT_BODY_ROTATION);
      this.dataManager.register(LEFT_ARM_ROTATION, DEFAULT_LEFTARM_ROTATION);
      this.dataManager.register(RIGHT_ARM_ROTATION, DEFAULT_RIGHTARM_ROTATION);
      this.dataManager.register(LEFT_LEG_ROTATION, DEFAULT_LEFTLEG_ROTATION);
      this.dataManager.register(RIGHT_LEG_ROTATION, DEFAULT_RIGHTLEG_ROTATION);
   }

   public Iterable<ItemStack> getHeldEquipment() {
      return this.handItems;
   }

   public Iterable<ItemStack> getArmorInventoryList() {
      return this.armorItems;
   }

   public ItemStack getItemStackFromSlot(EquipmentSlotType p_184582_1_) {
      switch(p_184582_1_.getSlotType()) {
      case HAND:
         return (ItemStack)this.handItems.get(p_184582_1_.getIndex());
      case ARMOR:
         return (ItemStack)this.armorItems.get(p_184582_1_.getIndex());
      default:
         return ItemStack.EMPTY;
      }
   }

   public void setItemStackToSlot(EquipmentSlotType p_184201_1_, ItemStack p_184201_2_) {
      switch(p_184201_1_.getSlotType()) {
      case HAND:
         this.playEquipSound(p_184201_2_);
         this.handItems.set(p_184201_1_.getIndex(), p_184201_2_);
         break;
      case ARMOR:
         this.playEquipSound(p_184201_2_);
         this.armorItems.set(p_184201_1_.getIndex(), p_184201_2_);
      }

   }

   public boolean replaceItemInInventory(int p_174820_1_, ItemStack p_174820_2_) {
      EquipmentSlotType equipmentslottype;
      if (p_174820_1_ == 98) {
         equipmentslottype = EquipmentSlotType.MAINHAND;
      } else if (p_174820_1_ == 99) {
         equipmentslottype = EquipmentSlotType.OFFHAND;
      } else if (p_174820_1_ == 100 + EquipmentSlotType.HEAD.getIndex()) {
         equipmentslottype = EquipmentSlotType.HEAD;
      } else if (p_174820_1_ == 100 + EquipmentSlotType.CHEST.getIndex()) {
         equipmentslottype = EquipmentSlotType.CHEST;
      } else if (p_174820_1_ == 100 + EquipmentSlotType.LEGS.getIndex()) {
         equipmentslottype = EquipmentSlotType.LEGS;
      } else {
         if (p_174820_1_ != 100 + EquipmentSlotType.FEET.getIndex()) {
            return false;
         }

         equipmentslottype = EquipmentSlotType.FEET;
      }

      if (!p_174820_2_.isEmpty() && !MobEntity.isItemStackInSlot(equipmentslottype, p_174820_2_) && equipmentslottype != EquipmentSlotType.HEAD) {
         return false;
      } else {
         this.setItemStackToSlot(equipmentslottype, p_174820_2_);
         return true;
      }
   }

   public boolean func_213365_e(ItemStack p_213365_1_) {
      EquipmentSlotType equipmentslottype = MobEntity.getSlotForItemStack(p_213365_1_);
      return this.getItemStackFromSlot(equipmentslottype).isEmpty() && !this.isDisabled(equipmentslottype);
   }

   public void writeAdditional(CompoundNBT p_213281_1_) {
      super.writeAdditional(p_213281_1_);
      ListNBT listnbt = new ListNBT();

      CompoundNBT compoundnbt;
      for(Iterator var3 = this.armorItems.iterator(); var3.hasNext(); listnbt.add(compoundnbt)) {
         ItemStack itemstack = (ItemStack)var3.next();
         compoundnbt = new CompoundNBT();
         if (!itemstack.isEmpty()) {
            itemstack.write(compoundnbt);
         }
      }

      p_213281_1_.put("ArmorItems", listnbt);
      ListNBT listnbt1 = new ListNBT();

      CompoundNBT compoundnbt1;
      for(Iterator var8 = this.handItems.iterator(); var8.hasNext(); listnbt1.add(compoundnbt1)) {
         ItemStack itemstack1 = (ItemStack)var8.next();
         compoundnbt1 = new CompoundNBT();
         if (!itemstack1.isEmpty()) {
            itemstack1.write(compoundnbt1);
         }
      }

      p_213281_1_.put("HandItems", listnbt1);
      p_213281_1_.putBoolean("Invisible", this.isInvisible());
      p_213281_1_.putBoolean("Small", this.isSmall());
      p_213281_1_.putBoolean("ShowArms", this.getShowArms());
      p_213281_1_.putInt("DisabledSlots", this.disabledSlots);
      p_213281_1_.putBoolean("NoBasePlate", this.hasNoBasePlate());
      if (this.hasMarker()) {
         p_213281_1_.putBoolean("Marker", this.hasMarker());
      }

      p_213281_1_.put("Pose", this.writePose());
   }

   public void readAdditional(CompoundNBT p_70037_1_) {
      super.readAdditional(p_70037_1_);
      ListNBT listnbt1;
      int j;
      if (p_70037_1_.contains("ArmorItems", 9)) {
         listnbt1 = p_70037_1_.getList("ArmorItems", 10);

         for(j = 0; j < this.armorItems.size(); ++j) {
            this.armorItems.set(j, ItemStack.read(listnbt1.getCompound(j)));
         }
      }

      if (p_70037_1_.contains("HandItems", 9)) {
         listnbt1 = p_70037_1_.getList("HandItems", 10);

         for(j = 0; j < this.handItems.size(); ++j) {
            this.handItems.set(j, ItemStack.read(listnbt1.getCompound(j)));
         }
      }

      this.setInvisible(p_70037_1_.getBoolean("Invisible"));
      this.setSmall(p_70037_1_.getBoolean("Small"));
      this.setShowArms(p_70037_1_.getBoolean("ShowArms"));
      this.disabledSlots = p_70037_1_.getInt("DisabledSlots");
      this.setNoBasePlate(p_70037_1_.getBoolean("NoBasePlate"));
      this.setMarker(p_70037_1_.getBoolean("Marker"));
      this.noClip = !this.func_213814_A();
      CompoundNBT compoundnbt = p_70037_1_.getCompound("Pose");
      this.readPose(compoundnbt);
   }

   private void readPose(CompoundNBT p_175416_1_) {
      ListNBT listnbt = p_175416_1_.getList("Head", 5);
      this.setHeadRotation(listnbt.isEmpty() ? DEFAULT_HEAD_ROTATION : new Rotations(listnbt));
      ListNBT listnbt1 = p_175416_1_.getList("Body", 5);
      this.setBodyRotation(listnbt1.isEmpty() ? DEFAULT_BODY_ROTATION : new Rotations(listnbt1));
      ListNBT listnbt2 = p_175416_1_.getList("LeftArm", 5);
      this.setLeftArmRotation(listnbt2.isEmpty() ? DEFAULT_LEFTARM_ROTATION : new Rotations(listnbt2));
      ListNBT listnbt3 = p_175416_1_.getList("RightArm", 5);
      this.setRightArmRotation(listnbt3.isEmpty() ? DEFAULT_RIGHTARM_ROTATION : new Rotations(listnbt3));
      ListNBT listnbt4 = p_175416_1_.getList("LeftLeg", 5);
      this.setLeftLegRotation(listnbt4.isEmpty() ? DEFAULT_LEFTLEG_ROTATION : new Rotations(listnbt4));
      ListNBT listnbt5 = p_175416_1_.getList("RightLeg", 5);
      this.setRightLegRotation(listnbt5.isEmpty() ? DEFAULT_RIGHTLEG_ROTATION : new Rotations(listnbt5));
   }

   private CompoundNBT writePose() {
      CompoundNBT compoundnbt = new CompoundNBT();
      if (!DEFAULT_HEAD_ROTATION.equals(this.headRotation)) {
         compoundnbt.put("Head", this.headRotation.writeToNBT());
      }

      if (!DEFAULT_BODY_ROTATION.equals(this.bodyRotation)) {
         compoundnbt.put("Body", this.bodyRotation.writeToNBT());
      }

      if (!DEFAULT_LEFTARM_ROTATION.equals(this.leftArmRotation)) {
         compoundnbt.put("LeftArm", this.leftArmRotation.writeToNBT());
      }

      if (!DEFAULT_RIGHTARM_ROTATION.equals(this.rightArmRotation)) {
         compoundnbt.put("RightArm", this.rightArmRotation.writeToNBT());
      }

      if (!DEFAULT_LEFTLEG_ROTATION.equals(this.leftLegRotation)) {
         compoundnbt.put("LeftLeg", this.leftLegRotation.writeToNBT());
      }

      if (!DEFAULT_RIGHTLEG_ROTATION.equals(this.rightLegRotation)) {
         compoundnbt.put("RightLeg", this.rightLegRotation.writeToNBT());
      }

      return compoundnbt;
   }

   public boolean canBePushed() {
      return false;
   }

   protected void collideWithEntity(Entity p_82167_1_) {
   }

   protected void collideWithNearbyEntities() {
      List<Entity> list = this.world.getEntitiesInAABBexcluding(this, this.getBoundingBox(), IS_RIDEABLE_MINECART);

      for(int i = 0; i < list.size(); ++i) {
         Entity entity = (Entity)list.get(i);
         if (this.getDistanceSq(entity) <= 0.2D) {
            entity.applyEntityCollision(this);
         }
      }

   }

   public ActionResultType applyPlayerInteraction(PlayerEntity p_184199_1_, Vec3d p_184199_2_, Hand p_184199_3_) {
      ItemStack itemstack = p_184199_1_.getHeldItem(p_184199_3_);
      if (!this.hasMarker() && itemstack.getItem() != Items.NAME_TAG) {
         if (p_184199_1_.isSpectator()) {
            return ActionResultType.SUCCESS;
         } else if (p_184199_1_.world.isRemote) {
            return ActionResultType.CONSUME;
         } else {
            EquipmentSlotType equipmentslottype = MobEntity.getSlotForItemStack(itemstack);
            if (itemstack.isEmpty()) {
               EquipmentSlotType equipmentslottype1 = this.getClickedSlot(p_184199_2_);
               EquipmentSlotType equipmentslottype2 = this.isDisabled(equipmentslottype1) ? equipmentslottype : equipmentslottype1;
               if (this.hasItemInSlot(equipmentslottype2) && this.func_226529_a_(p_184199_1_, equipmentslottype2, itemstack, p_184199_3_)) {
                  return ActionResultType.SUCCESS;
               }
            } else {
               if (this.isDisabled(equipmentslottype)) {
                  return ActionResultType.FAIL;
               }

               if (equipmentslottype.getSlotType() == EquipmentSlotType.Group.HAND && !this.getShowArms()) {
                  return ActionResultType.FAIL;
               }

               if (this.func_226529_a_(p_184199_1_, equipmentslottype, itemstack, p_184199_3_)) {
                  return ActionResultType.SUCCESS;
               }
            }

            return ActionResultType.PASS;
         }
      } else {
         return ActionResultType.PASS;
      }
   }

   private EquipmentSlotType getClickedSlot(Vec3d p_190772_1_) {
      EquipmentSlotType equipmentslottype = EquipmentSlotType.MAINHAND;
      boolean flag = this.isSmall();
      double d0 = flag ? p_190772_1_.y * 2.0D : p_190772_1_.y;
      EquipmentSlotType equipmentslottype1 = EquipmentSlotType.FEET;
      if (d0 >= 0.1D && d0 < 0.1D + (flag ? 0.8D : 0.45D) && this.hasItemInSlot(equipmentslottype1)) {
         equipmentslottype = EquipmentSlotType.FEET;
      } else if (d0 >= 0.9D + (flag ? 0.3D : 0.0D) && d0 < 0.9D + (flag ? 1.0D : 0.7D) && this.hasItemInSlot(EquipmentSlotType.CHEST)) {
         equipmentslottype = EquipmentSlotType.CHEST;
      } else if (d0 >= 0.4D && d0 < 0.4D + (flag ? 1.0D : 0.8D) && this.hasItemInSlot(EquipmentSlotType.LEGS)) {
         equipmentslottype = EquipmentSlotType.LEGS;
      } else if (d0 >= 1.6D && this.hasItemInSlot(EquipmentSlotType.HEAD)) {
         equipmentslottype = EquipmentSlotType.HEAD;
      } else if (!this.hasItemInSlot(EquipmentSlotType.MAINHAND) && this.hasItemInSlot(EquipmentSlotType.OFFHAND)) {
         equipmentslottype = EquipmentSlotType.OFFHAND;
      }

      return equipmentslottype;
   }

   private boolean isDisabled(EquipmentSlotType p_184796_1_) {
      return (this.disabledSlots & 1 << p_184796_1_.getSlotIndex()) != 0 || p_184796_1_.getSlotType() == EquipmentSlotType.Group.HAND && !this.getShowArms();
   }

   private boolean func_226529_a_(PlayerEntity p_226529_1_, EquipmentSlotType p_226529_2_, ItemStack p_226529_3_, Hand p_226529_4_) {
      ItemStack itemstack = this.getItemStackFromSlot(p_226529_2_);
      if (!itemstack.isEmpty() && (this.disabledSlots & 1 << p_226529_2_.getSlotIndex() + 8) != 0) {
         return false;
      } else if (itemstack.isEmpty() && (this.disabledSlots & 1 << p_226529_2_.getSlotIndex() + 16) != 0) {
         return false;
      } else {
         ItemStack itemstack1;
         if (p_226529_1_.abilities.isCreativeMode && itemstack.isEmpty() && !p_226529_3_.isEmpty()) {
            itemstack1 = p_226529_3_.copy();
            itemstack1.setCount(1);
            this.setItemStackToSlot(p_226529_2_, itemstack1);
            return true;
         } else if (!p_226529_3_.isEmpty() && p_226529_3_.getCount() > 1) {
            if (!itemstack.isEmpty()) {
               return false;
            } else {
               itemstack1 = p_226529_3_.copy();
               itemstack1.setCount(1);
               this.setItemStackToSlot(p_226529_2_, itemstack1);
               p_226529_3_.shrink(1);
               return true;
            }
         } else {
            this.setItemStackToSlot(p_226529_2_, p_226529_3_);
            p_226529_1_.setHeldItem(p_226529_4_, itemstack);
            return true;
         }
      }
   }

   public boolean attackEntityFrom(DamageSource p_70097_1_, float p_70097_2_) {
      if (!this.world.isRemote && !this.removed) {
         if (DamageSource.OUT_OF_WORLD.equals(p_70097_1_)) {
            this.remove();
            return false;
         } else if (!this.isInvulnerableTo(p_70097_1_) && !this.canInteract && !this.hasMarker()) {
            if (p_70097_1_.isExplosion()) {
               this.func_213816_g(p_70097_1_);
               this.remove();
               return false;
            } else if (DamageSource.IN_FIRE.equals(p_70097_1_)) {
               if (this.isBurning()) {
                  this.func_213817_e(p_70097_1_, 0.15F);
               } else {
                  this.setFire(5);
               }

               return false;
            } else if (DamageSource.ON_FIRE.equals(p_70097_1_) && this.getHealth() > 0.5F) {
               this.func_213817_e(p_70097_1_, 4.0F);
               return false;
            } else {
               boolean flag = p_70097_1_.getImmediateSource() instanceof AbstractArrowEntity;
               boolean flag1 = flag && ((AbstractArrowEntity)p_70097_1_.getImmediateSource()).func_213874_s() > 0;
               boolean flag2 = "player".equals(p_70097_1_.getDamageType());
               if (!flag2 && !flag) {
                  return false;
               } else if (p_70097_1_.getTrueSource() instanceof PlayerEntity && !((PlayerEntity)p_70097_1_.getTrueSource()).abilities.allowEdit) {
                  return false;
               } else if (p_70097_1_.isCreativePlayer()) {
                  this.playBrokenSound();
                  this.playParticles();
                  this.remove();
                  return flag1;
               } else {
                  long i = this.world.getGameTime();
                  if (i - this.punchCooldown > 5L && !flag) {
                     this.world.setEntityState(this, (byte)32);
                     this.punchCooldown = i;
                  } else {
                     this.func_213815_f(p_70097_1_);
                     this.playParticles();
                     this.remove();
                  }

                  return true;
               }
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void handleStatusUpdate(byte p_70103_1_) {
      if (p_70103_1_ == 32) {
         if (this.world.isRemote) {
            this.world.playSound(this.func_226277_ct_(), this.func_226278_cu_(), this.func_226281_cx_(), SoundEvents.ENTITY_ARMOR_STAND_HIT, this.getSoundCategory(), 0.3F, 1.0F, false);
            this.punchCooldown = this.world.getGameTime();
         }
      } else {
         super.handleStatusUpdate(p_70103_1_);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public boolean isInRangeToRenderDist(double p_70112_1_) {
      double d0 = this.getBoundingBox().getAverageEdgeLength() * 4.0D;
      if (Double.isNaN(d0) || d0 == 0.0D) {
         d0 = 4.0D;
      }

      d0 *= 64.0D;
      return p_70112_1_ < d0 * d0;
   }

   private void playParticles() {
      if (this.world instanceof ServerWorld) {
         ((ServerWorld)this.world).spawnParticle(new BlockParticleData(ParticleTypes.BLOCK, Blocks.OAK_PLANKS.getDefaultState()), this.func_226277_ct_(), this.func_226283_e_(0.6666666666666666D), this.func_226281_cx_(), 10, (double)(this.getWidth() / 4.0F), (double)(this.getHeight() / 4.0F), (double)(this.getWidth() / 4.0F), 0.05D);
      }

   }

   private void func_213817_e(DamageSource p_213817_1_, float p_213817_2_) {
      float f = this.getHealth();
      f -= p_213817_2_;
      if (f <= 0.5F) {
         this.func_213816_g(p_213817_1_);
         this.remove();
      } else {
         this.setHealth(f);
      }

   }

   private void func_213815_f(DamageSource p_213815_1_) {
      Block.spawnAsEntity(this.world, new BlockPos(this), new ItemStack(Items.ARMOR_STAND));
      this.func_213816_g(p_213815_1_);
   }

   private void func_213816_g(DamageSource p_213816_1_) {
      this.playBrokenSound();
      this.spawnDrops(p_213816_1_);

      int j;
      ItemStack itemstack1;
      for(j = 0; j < this.handItems.size(); ++j) {
         itemstack1 = (ItemStack)this.handItems.get(j);
         if (!itemstack1.isEmpty()) {
            Block.spawnAsEntity(this.world, (new BlockPos(this)).up(), itemstack1);
            this.handItems.set(j, ItemStack.EMPTY);
         }
      }

      for(j = 0; j < this.armorItems.size(); ++j) {
         itemstack1 = (ItemStack)this.armorItems.get(j);
         if (!itemstack1.isEmpty()) {
            Block.spawnAsEntity(this.world, (new BlockPos(this)).up(), itemstack1);
            this.armorItems.set(j, ItemStack.EMPTY);
         }
      }

   }

   private void playBrokenSound() {
      this.world.playSound((PlayerEntity)null, this.func_226277_ct_(), this.func_226278_cu_(), this.func_226281_cx_(), SoundEvents.ENTITY_ARMOR_STAND_BREAK, this.getSoundCategory(), 1.0F, 1.0F);
   }

   protected float updateDistance(float p_110146_1_, float p_110146_2_) {
      this.prevRenderYawOffset = this.prevRotationYaw;
      this.renderYawOffset = this.rotationYaw;
      return 0.0F;
   }

   protected float getStandingEyeHeight(Pose p_213348_1_, EntitySize p_213348_2_) {
      return p_213348_2_.height * (this.isChild() ? 0.5F : 0.9F);
   }

   public double getYOffset() {
      return this.hasMarker() ? 0.0D : 0.10000000149011612D;
   }

   public void travel(Vec3d p_213352_1_) {
      if (this.func_213814_A()) {
         super.travel(p_213352_1_);
      }

   }

   public void setRenderYawOffset(float p_181013_1_) {
      this.prevRenderYawOffset = this.prevRotationYaw = p_181013_1_;
      this.prevRotationYawHead = this.rotationYawHead = p_181013_1_;
   }

   public void setRotationYawHead(float p_70034_1_) {
      this.prevRenderYawOffset = this.prevRotationYaw = p_70034_1_;
      this.prevRotationYawHead = this.rotationYawHead = p_70034_1_;
   }

   public void tick() {
      super.tick();
      Rotations rotations = (Rotations)this.dataManager.get(HEAD_ROTATION);
      if (!this.headRotation.equals(rotations)) {
         this.setHeadRotation(rotations);
      }

      Rotations rotations1 = (Rotations)this.dataManager.get(BODY_ROTATION);
      if (!this.bodyRotation.equals(rotations1)) {
         this.setBodyRotation(rotations1);
      }

      Rotations rotations2 = (Rotations)this.dataManager.get(LEFT_ARM_ROTATION);
      if (!this.leftArmRotation.equals(rotations2)) {
         this.setLeftArmRotation(rotations2);
      }

      Rotations rotations3 = (Rotations)this.dataManager.get(RIGHT_ARM_ROTATION);
      if (!this.rightArmRotation.equals(rotations3)) {
         this.setRightArmRotation(rotations3);
      }

      Rotations rotations4 = (Rotations)this.dataManager.get(LEFT_LEG_ROTATION);
      if (!this.leftLegRotation.equals(rotations4)) {
         this.setLeftLegRotation(rotations4);
      }

      Rotations rotations5 = (Rotations)this.dataManager.get(RIGHT_LEG_ROTATION);
      if (!this.rightLegRotation.equals(rotations5)) {
         this.setRightLegRotation(rotations5);
      }

   }

   protected void updatePotionMetadata() {
      this.setInvisible(this.canInteract);
   }

   public void setInvisible(boolean p_82142_1_) {
      this.canInteract = p_82142_1_;
      super.setInvisible(p_82142_1_);
   }

   public boolean isChild() {
      return this.isSmall();
   }

   public void onKillCommand() {
      this.remove();
   }

   public boolean isImmuneToExplosions() {
      return this.isInvisible();
   }

   public PushReaction getPushReaction() {
      return this.hasMarker() ? PushReaction.IGNORE : super.getPushReaction();
   }

   private void setSmall(boolean p_175420_1_) {
      this.dataManager.set(STATUS, this.setBit((Byte)this.dataManager.get(STATUS), 1, p_175420_1_));
   }

   public boolean isSmall() {
      return ((Byte)this.dataManager.get(STATUS) & 1) != 0;
   }

   private void setShowArms(boolean p_175413_1_) {
      this.dataManager.set(STATUS, this.setBit((Byte)this.dataManager.get(STATUS), 4, p_175413_1_));
   }

   public boolean getShowArms() {
      return ((Byte)this.dataManager.get(STATUS) & 4) != 0;
   }

   private void setNoBasePlate(boolean p_175426_1_) {
      this.dataManager.set(STATUS, this.setBit((Byte)this.dataManager.get(STATUS), 8, p_175426_1_));
   }

   public boolean hasNoBasePlate() {
      return ((Byte)this.dataManager.get(STATUS) & 8) != 0;
   }

   private void setMarker(boolean p_181027_1_) {
      this.dataManager.set(STATUS, this.setBit((Byte)this.dataManager.get(STATUS), 16, p_181027_1_));
   }

   public boolean hasMarker() {
      return ((Byte)this.dataManager.get(STATUS) & 16) != 0;
   }

   private byte setBit(byte p_184797_1_, int p_184797_2_, boolean p_184797_3_) {
      if (p_184797_3_) {
         p_184797_1_ = (byte)(p_184797_1_ | p_184797_2_);
      } else {
         p_184797_1_ = (byte)(p_184797_1_ & ~p_184797_2_);
      }

      return p_184797_1_;
   }

   public void setHeadRotation(Rotations p_175415_1_) {
      this.headRotation = p_175415_1_;
      this.dataManager.set(HEAD_ROTATION, p_175415_1_);
   }

   public void setBodyRotation(Rotations p_175424_1_) {
      this.bodyRotation = p_175424_1_;
      this.dataManager.set(BODY_ROTATION, p_175424_1_);
   }

   public void setLeftArmRotation(Rotations p_175405_1_) {
      this.leftArmRotation = p_175405_1_;
      this.dataManager.set(LEFT_ARM_ROTATION, p_175405_1_);
   }

   public void setRightArmRotation(Rotations p_175428_1_) {
      this.rightArmRotation = p_175428_1_;
      this.dataManager.set(RIGHT_ARM_ROTATION, p_175428_1_);
   }

   public void setLeftLegRotation(Rotations p_175417_1_) {
      this.leftLegRotation = p_175417_1_;
      this.dataManager.set(LEFT_LEG_ROTATION, p_175417_1_);
   }

   public void setRightLegRotation(Rotations p_175427_1_) {
      this.rightLegRotation = p_175427_1_;
      this.dataManager.set(RIGHT_LEG_ROTATION, p_175427_1_);
   }

   public Rotations getHeadRotation() {
      return this.headRotation;
   }

   public Rotations getBodyRotation() {
      return this.bodyRotation;
   }

   @OnlyIn(Dist.CLIENT)
   public Rotations getLeftArmRotation() {
      return this.leftArmRotation;
   }

   @OnlyIn(Dist.CLIENT)
   public Rotations getRightArmRotation() {
      return this.rightArmRotation;
   }

   @OnlyIn(Dist.CLIENT)
   public Rotations getLeftLegRotation() {
      return this.leftLegRotation;
   }

   @OnlyIn(Dist.CLIENT)
   public Rotations getRightLegRotation() {
      return this.rightLegRotation;
   }

   public boolean canBeCollidedWith() {
      return super.canBeCollidedWith() && !this.hasMarker();
   }

   public boolean hitByEntity(Entity p_85031_1_) {
      return p_85031_1_ instanceof PlayerEntity && !this.world.isBlockModifiable((PlayerEntity)p_85031_1_, new BlockPos(this));
   }

   public HandSide getPrimaryHand() {
      return HandSide.RIGHT;
   }

   protected SoundEvent getFallSound(int p_184588_1_) {
      return SoundEvents.ENTITY_ARMOR_STAND_FALL;
   }

   @Nullable
   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.ENTITY_ARMOR_STAND_HIT;
   }

   @Nullable
   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_ARMOR_STAND_BREAK;
   }

   public void onStruckByLightning(LightningBoltEntity p_70077_1_) {
   }

   public boolean canBeHitWithPotion() {
      return false;
   }

   public void notifyDataManagerChange(DataParameter<?> p_184206_1_) {
      if (STATUS.equals(p_184206_1_)) {
         this.recalculateSize();
         this.preventEntitySpawning = !this.hasMarker();
      }

      super.notifyDataManagerChange(p_184206_1_);
   }

   public boolean attackable() {
      return false;
   }

   public EntitySize getSize(Pose p_213305_1_) {
      float f = this.hasMarker() ? 0.0F : (this.isChild() ? 0.5F : 1.0F);
      return this.getType().getSize().scale(f);
   }

   static {
      STATUS = EntityDataManager.createKey(ArmorStandEntity.class, DataSerializers.BYTE);
      HEAD_ROTATION = EntityDataManager.createKey(ArmorStandEntity.class, DataSerializers.ROTATIONS);
      BODY_ROTATION = EntityDataManager.createKey(ArmorStandEntity.class, DataSerializers.ROTATIONS);
      LEFT_ARM_ROTATION = EntityDataManager.createKey(ArmorStandEntity.class, DataSerializers.ROTATIONS);
      RIGHT_ARM_ROTATION = EntityDataManager.createKey(ArmorStandEntity.class, DataSerializers.ROTATIONS);
      LEFT_LEG_ROTATION = EntityDataManager.createKey(ArmorStandEntity.class, DataSerializers.ROTATIONS);
      RIGHT_LEG_ROTATION = EntityDataManager.createKey(ArmorStandEntity.class, DataSerializers.ROTATIONS);
      IS_RIDEABLE_MINECART = (p_lambda$static$0_0_) -> {
         return p_lambda$static$0_0_ instanceof AbstractMinecartEntity && ((AbstractMinecartEntity)p_lambda$static$0_0_).canBeRidden();
      };
   }
}
