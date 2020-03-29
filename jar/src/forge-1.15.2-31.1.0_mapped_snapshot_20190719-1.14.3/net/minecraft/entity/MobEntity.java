package net.minecraft.entity;

import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractSkullBlock;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.ai.EntitySenses;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.controller.BodyController;
import net.minecraft.entity.ai.controller.JumpController;
import net.minecraft.entity.ai.controller.LookController;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.entity.item.HangingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.item.LeashKnotEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.FloatNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.DebugPacketSender;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SMountEntityPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.tags.Tag;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.eventbus.api.Event.Result;

public abstract class MobEntity extends LivingEntity {
   private static final DataParameter<Byte> AI_FLAGS;
   public int livingSoundTime;
   protected int experienceValue;
   protected LookController lookController;
   protected MovementController moveController;
   protected JumpController jumpController;
   private final BodyController bodyController;
   protected PathNavigator navigator;
   public final GoalSelector goalSelector;
   public final GoalSelector targetSelector;
   private LivingEntity attackTarget;
   private final EntitySenses senses;
   private final NonNullList<ItemStack> inventoryHands;
   protected final float[] inventoryHandsDropChances;
   private final NonNullList<ItemStack> inventoryArmor;
   protected final float[] inventoryArmorDropChances;
   private boolean canPickUpLoot;
   private boolean persistenceRequired;
   private final Map<PathNodeType, Float> mapPathPriority;
   private ResourceLocation deathLootTable;
   private long deathLootTableSeed;
   @Nullable
   private Entity leashHolder;
   private int leashHolderID;
   @Nullable
   private CompoundNBT leashNBTTag;
   private BlockPos homePosition;
   private float maximumHomeDistance;

   protected MobEntity(EntityType<? extends MobEntity> p_i48576_1_, World p_i48576_2_) {
      super(p_i48576_1_, p_i48576_2_);
      this.inventoryHands = NonNullList.withSize(2, ItemStack.EMPTY);
      this.inventoryHandsDropChances = new float[2];
      this.inventoryArmor = NonNullList.withSize(4, ItemStack.EMPTY);
      this.inventoryArmorDropChances = new float[4];
      this.mapPathPriority = Maps.newEnumMap(PathNodeType.class);
      this.homePosition = BlockPos.ZERO;
      this.maximumHomeDistance = -1.0F;
      this.goalSelector = new GoalSelector(p_i48576_2_ != null && p_i48576_2_.getProfiler() != null ? p_i48576_2_.getProfiler() : null);
      this.targetSelector = new GoalSelector(p_i48576_2_ != null && p_i48576_2_.getProfiler() != null ? p_i48576_2_.getProfiler() : null);
      this.lookController = new LookController(this);
      this.moveController = new MovementController(this);
      this.jumpController = new JumpController(this);
      this.bodyController = this.createBodyController();
      this.navigator = this.createNavigator(p_i48576_2_);
      this.senses = new EntitySenses(this);
      Arrays.fill(this.inventoryArmorDropChances, 0.085F);
      Arrays.fill(this.inventoryHandsDropChances, 0.085F);
      if (p_i48576_2_ != null && !p_i48576_2_.isRemote) {
         this.registerGoals();
      }

   }

   protected void registerGoals() {
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttributes().registerAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(16.0D);
      this.getAttributes().registerAttribute(SharedMonsterAttributes.ATTACK_KNOCKBACK);
   }

   protected PathNavigator createNavigator(World p_175447_1_) {
      return new GroundPathNavigator(this, p_175447_1_);
   }

   public float getPathPriority(PathNodeType p_184643_1_) {
      Float f = (Float)this.mapPathPriority.get(p_184643_1_);
      return f == null ? p_184643_1_.getPriority() : f;
   }

   public void setPathPriority(PathNodeType p_184644_1_, float p_184644_2_) {
      this.mapPathPriority.put(p_184644_1_, p_184644_2_);
   }

   protected BodyController createBodyController() {
      return new BodyController(this);
   }

   public LookController getLookController() {
      return this.lookController;
   }

   public MovementController getMoveHelper() {
      if (this.isPassenger() && this.getRidingEntity() instanceof MobEntity) {
         MobEntity mobentity = (MobEntity)this.getRidingEntity();
         return mobentity.getMoveHelper();
      } else {
         return this.moveController;
      }
   }

   public JumpController getJumpController() {
      return this.jumpController;
   }

   public PathNavigator getNavigator() {
      if (this.isPassenger() && this.getRidingEntity() instanceof MobEntity) {
         MobEntity mobentity = (MobEntity)this.getRidingEntity();
         return mobentity.getNavigator();
      } else {
         return this.navigator;
      }
   }

   public EntitySenses getEntitySenses() {
      return this.senses;
   }

   @Nullable
   public LivingEntity getAttackTarget() {
      return this.attackTarget;
   }

   public void setAttackTarget(@Nullable LivingEntity p_70624_1_) {
      this.attackTarget = p_70624_1_;
      ForgeHooks.onLivingSetAttackTarget(this, p_70624_1_);
   }

   public boolean canAttack(EntityType<?> p_213358_1_) {
      return p_213358_1_ != EntityType.GHAST;
   }

   public void eatGrassBonus() {
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(AI_FLAGS, (byte)0);
   }

   public int getTalkInterval() {
      return 80;
   }

   public void playAmbientSound() {
      SoundEvent soundevent = this.getAmbientSound();
      if (soundevent != null) {
         this.playSound(soundevent, this.getSoundVolume(), this.getSoundPitch());
      }

   }

   public void baseTick() {
      super.baseTick();
      this.world.getProfiler().startSection("mobBaseTick");
      if (this.isAlive() && this.rand.nextInt(1000) < this.livingSoundTime++) {
         this.applyEntityAI();
         this.playAmbientSound();
      }

      this.world.getProfiler().endSection();
   }

   protected void playHurtSound(DamageSource p_184581_1_) {
      this.applyEntityAI();
      super.playHurtSound(p_184581_1_);
   }

   private void applyEntityAI() {
      this.livingSoundTime = -this.getTalkInterval();
   }

   protected int getExperiencePoints(PlayerEntity p_70693_1_) {
      if (this.experienceValue > 0) {
         int i = this.experienceValue;

         int k;
         for(k = 0; k < this.inventoryArmor.size(); ++k) {
            if (!((ItemStack)this.inventoryArmor.get(k)).isEmpty() && this.inventoryArmorDropChances[k] <= 1.0F) {
               i += 1 + this.rand.nextInt(3);
            }
         }

         for(k = 0; k < this.inventoryHands.size(); ++k) {
            if (!((ItemStack)this.inventoryHands.get(k)).isEmpty() && this.inventoryHandsDropChances[k] <= 1.0F) {
               i += 1 + this.rand.nextInt(3);
            }
         }

         return i;
      } else {
         return this.experienceValue;
      }
   }

   public void spawnExplosionParticle() {
      if (this.world.isRemote) {
         for(int i = 0; i < 20; ++i) {
            double d0 = this.rand.nextGaussian() * 0.02D;
            double d1 = this.rand.nextGaussian() * 0.02D;
            double d2 = this.rand.nextGaussian() * 0.02D;
            double d3 = 10.0D;
            this.world.addParticle(ParticleTypes.POOF, this.func_226275_c_(1.0D) - d0 * 10.0D, this.func_226279_cv_() - d1 * 10.0D, this.func_226287_g_(1.0D) - d2 * 10.0D, d0, d1, d2);
         }
      } else {
         this.world.setEntityState(this, (byte)20);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public void handleStatusUpdate(byte p_70103_1_) {
      if (p_70103_1_ == 20) {
         this.spawnExplosionParticle();
      } else {
         super.handleStatusUpdate(p_70103_1_);
      }

   }

   public void tick() {
      super.tick();
      if (!this.world.isRemote) {
         this.updateLeashedState();
         if (this.ticksExisted % 5 == 0) {
            this.func_213385_F();
         }
      }

   }

   protected void func_213385_F() {
      boolean flag = !(this.getControllingPassenger() instanceof MobEntity);
      boolean flag1 = !(this.getRidingEntity() instanceof BoatEntity);
      this.goalSelector.setFlag(Goal.Flag.MOVE, flag);
      this.goalSelector.setFlag(Goal.Flag.JUMP, flag && flag1);
      this.goalSelector.setFlag(Goal.Flag.LOOK, flag);
   }

   protected float updateDistance(float p_110146_1_, float p_110146_2_) {
      this.bodyController.updateRenderAngles();
      return p_110146_2_;
   }

   @Nullable
   protected SoundEvent getAmbientSound() {
      return null;
   }

   public void writeAdditional(CompoundNBT p_213281_1_) {
      super.writeAdditional(p_213281_1_);
      p_213281_1_.putBoolean("CanPickUpLoot", this.canPickUpLoot());
      p_213281_1_.putBoolean("PersistenceRequired", this.persistenceRequired);
      ListNBT listnbt = new ListNBT();

      CompoundNBT compoundnbt;
      for(Iterator var3 = this.inventoryArmor.iterator(); var3.hasNext(); listnbt.add(compoundnbt)) {
         ItemStack itemstack = (ItemStack)var3.next();
         compoundnbt = new CompoundNBT();
         if (!itemstack.isEmpty()) {
            itemstack.write(compoundnbt);
         }
      }

      p_213281_1_.put("ArmorItems", listnbt);
      ListNBT listnbt1 = new ListNBT();

      CompoundNBT compoundnbt2;
      for(Iterator var11 = this.inventoryHands.iterator(); var11.hasNext(); listnbt1.add(compoundnbt2)) {
         ItemStack itemstack1 = (ItemStack)var11.next();
         compoundnbt2 = new CompoundNBT();
         if (!itemstack1.isEmpty()) {
            itemstack1.write(compoundnbt2);
         }
      }

      p_213281_1_.put("HandItems", listnbt1);
      ListNBT listnbt2 = new ListNBT();
      float[] var14 = this.inventoryArmorDropChances;
      int var16 = var14.length;

      int var7;
      for(var7 = 0; var7 < var16; ++var7) {
         float f = var14[var7];
         listnbt2.add(FloatNBT.func_229689_a_(f));
      }

      p_213281_1_.put("ArmorDropChances", listnbt2);
      ListNBT listnbt3 = new ListNBT();
      float[] var17 = this.inventoryHandsDropChances;
      var7 = var17.length;

      for(int var19 = 0; var19 < var7; ++var19) {
         float f1 = var17[var19];
         listnbt3.add(FloatNBT.func_229689_a_(f1));
      }

      p_213281_1_.put("HandDropChances", listnbt3);
      if (this.leashHolder != null) {
         compoundnbt2 = new CompoundNBT();
         if (this.leashHolder instanceof LivingEntity) {
            UUID uuid = this.leashHolder.getUniqueID();
            compoundnbt2.putUniqueId("UUID", uuid);
         } else if (this.leashHolder instanceof HangingEntity) {
            BlockPos blockpos = ((HangingEntity)this.leashHolder).getHangingPosition();
            compoundnbt2.putInt("X", blockpos.getX());
            compoundnbt2.putInt("Y", blockpos.getY());
            compoundnbt2.putInt("Z", blockpos.getZ());
         }

         p_213281_1_.put("Leash", compoundnbt2);
      } else if (this.leashNBTTag != null) {
         p_213281_1_.put("Leash", this.leashNBTTag.copy());
      }

      p_213281_1_.putBoolean("LeftHanded", this.isLeftHanded());
      if (this.deathLootTable != null) {
         p_213281_1_.putString("DeathLootTable", this.deathLootTable.toString());
         if (this.deathLootTableSeed != 0L) {
            p_213281_1_.putLong("DeathLootTableSeed", this.deathLootTableSeed);
         }
      }

      if (this.isAIDisabled()) {
         p_213281_1_.putBoolean("NoAI", this.isAIDisabled());
      }

   }

   public void readAdditional(CompoundNBT p_70037_1_) {
      super.readAdditional(p_70037_1_);
      if (p_70037_1_.contains("CanPickUpLoot", 1)) {
         this.setCanPickUpLoot(p_70037_1_.getBoolean("CanPickUpLoot"));
      }

      this.persistenceRequired = p_70037_1_.getBoolean("PersistenceRequired");
      ListNBT listnbt3;
      int l;
      if (p_70037_1_.contains("ArmorItems", 9)) {
         listnbt3 = p_70037_1_.getList("ArmorItems", 10);

         for(l = 0; l < this.inventoryArmor.size(); ++l) {
            this.inventoryArmor.set(l, ItemStack.read(listnbt3.getCompound(l)));
         }
      }

      if (p_70037_1_.contains("HandItems", 9)) {
         listnbt3 = p_70037_1_.getList("HandItems", 10);

         for(l = 0; l < this.inventoryHands.size(); ++l) {
            this.inventoryHands.set(l, ItemStack.read(listnbt3.getCompound(l)));
         }
      }

      if (p_70037_1_.contains("ArmorDropChances", 9)) {
         listnbt3 = p_70037_1_.getList("ArmorDropChances", 5);

         for(l = 0; l < listnbt3.size(); ++l) {
            this.inventoryArmorDropChances[l] = listnbt3.getFloat(l);
         }
      }

      if (p_70037_1_.contains("HandDropChances", 9)) {
         listnbt3 = p_70037_1_.getList("HandDropChances", 5);

         for(l = 0; l < listnbt3.size(); ++l) {
            this.inventoryHandsDropChances[l] = listnbt3.getFloat(l);
         }
      }

      if (p_70037_1_.contains("Leash", 10)) {
         this.leashNBTTag = p_70037_1_.getCompound("Leash");
      }

      this.setLeftHanded(p_70037_1_.getBoolean("LeftHanded"));
      if (p_70037_1_.contains("DeathLootTable", 8)) {
         this.deathLootTable = new ResourceLocation(p_70037_1_.getString("DeathLootTable"));
         this.deathLootTableSeed = p_70037_1_.getLong("DeathLootTableSeed");
      }

      this.setNoAI(p_70037_1_.getBoolean("NoAI"));
   }

   protected void dropLoot(DamageSource p_213354_1_, boolean p_213354_2_) {
      super.dropLoot(p_213354_1_, p_213354_2_);
      this.deathLootTable = null;
   }

   protected LootContext.Builder func_213363_a(boolean p_213363_1_, DamageSource p_213363_2_) {
      return super.func_213363_a(p_213363_1_, p_213363_2_).withSeededRandom(this.deathLootTableSeed, this.rand);
   }

   public final ResourceLocation func_213346_cF() {
      return this.deathLootTable == null ? this.getLootTable() : this.deathLootTable;
   }

   protected ResourceLocation getLootTable() {
      return super.func_213346_cF();
   }

   public void setMoveForward(float p_191989_1_) {
      this.moveForward = p_191989_1_;
   }

   public void setMoveVertical(float p_70657_1_) {
      this.moveVertical = p_70657_1_;
   }

   public void setMoveStrafing(float p_184646_1_) {
      this.moveStrafing = p_184646_1_;
   }

   public void setAIMoveSpeed(float p_70659_1_) {
      super.setAIMoveSpeed(p_70659_1_);
      this.setMoveForward(p_70659_1_);
   }

   public void livingTick() {
      super.livingTick();
      this.world.getProfiler().startSection("looting");
      if (!this.world.isRemote && this.canPickUpLoot() && this.isAlive() && !this.dead && ForgeEventFactory.getMobGriefingEvent(this.world, this)) {
         Iterator var1 = this.world.getEntitiesWithinAABB(ItemEntity.class, this.getBoundingBox().grow(1.0D, 0.0D, 1.0D)).iterator();

         while(var1.hasNext()) {
            ItemEntity itementity = (ItemEntity)var1.next();
            if (!itementity.removed && !itementity.getItem().isEmpty() && !itementity.cannotPickup()) {
               this.updateEquipmentIfNeeded(itementity);
            }
         }
      }

      this.world.getProfiler().endSection();
   }

   protected void updateEquipmentIfNeeded(ItemEntity p_175445_1_) {
      ItemStack itemstack = p_175445_1_.getItem();
      EquipmentSlotType equipmentslottype = getSlotForItemStack(itemstack);
      ItemStack itemstack1 = this.getItemStackFromSlot(equipmentslottype);
      boolean flag = this.shouldExchangeEquipment(itemstack, itemstack1, equipmentslottype);
      if (flag && this.canEquipItem(itemstack)) {
         double d0 = (double)this.getDropChance(equipmentslottype);
         if (!itemstack1.isEmpty() && (double)Math.max(this.rand.nextFloat() - 0.1F, 0.0F) < d0) {
            this.entityDropItem(itemstack1);
         }

         this.setItemStackToSlot(equipmentslottype, itemstack);
         switch(equipmentslottype.getSlotType()) {
         case HAND:
            this.inventoryHandsDropChances[equipmentslottype.getIndex()] = 2.0F;
            break;
         case ARMOR:
            this.inventoryArmorDropChances[equipmentslottype.getIndex()] = 2.0F;
         }

         this.persistenceRequired = true;
         this.onItemPickup(p_175445_1_, itemstack.getCount());
         p_175445_1_.remove();
      }

   }

   protected boolean shouldExchangeEquipment(ItemStack p_208003_1_, ItemStack p_208003_2_, EquipmentSlotType p_208003_3_) {
      boolean flag = true;
      if (!p_208003_2_.isEmpty()) {
         if (p_208003_3_.getSlotType() == EquipmentSlotType.Group.HAND) {
            if (p_208003_1_.getItem() instanceof SwordItem && !(p_208003_2_.getItem() instanceof SwordItem)) {
               flag = true;
            } else if (p_208003_1_.getItem() instanceof SwordItem && p_208003_2_.getItem() instanceof SwordItem) {
               SwordItem sworditem = (SwordItem)p_208003_1_.getItem();
               SwordItem sworditem1 = (SwordItem)p_208003_2_.getItem();
               if (sworditem.getAttackDamage() == sworditem1.getAttackDamage()) {
                  flag = p_208003_1_.getDamage() < p_208003_2_.getDamage() || p_208003_1_.hasTag() && !p_208003_2_.hasTag();
               } else {
                  flag = sworditem.getAttackDamage() > sworditem1.getAttackDamage();
               }
            } else if (p_208003_1_.getItem() instanceof BowItem && p_208003_2_.getItem() instanceof BowItem) {
               flag = p_208003_1_.hasTag() && !p_208003_2_.hasTag();
            } else {
               flag = false;
            }
         } else if (p_208003_1_.getItem() instanceof ArmorItem && !(p_208003_2_.getItem() instanceof ArmorItem)) {
            flag = true;
         } else if (p_208003_1_.getItem() instanceof ArmorItem && p_208003_2_.getItem() instanceof ArmorItem && !EnchantmentHelper.hasBindingCurse(p_208003_2_)) {
            ArmorItem armoritem = (ArmorItem)p_208003_1_.getItem();
            ArmorItem armoritem1 = (ArmorItem)p_208003_2_.getItem();
            if (armoritem.getDamageReduceAmount() == armoritem1.getDamageReduceAmount()) {
               flag = p_208003_1_.getDamage() < p_208003_2_.getDamage() || p_208003_1_.hasTag() && !p_208003_2_.hasTag();
            } else {
               flag = armoritem.getDamageReduceAmount() > armoritem1.getDamageReduceAmount();
            }
         } else {
            flag = false;
         }
      }

      return flag;
   }

   protected boolean canEquipItem(ItemStack p_175448_1_) {
      return true;
   }

   public boolean canDespawn(double p_213397_1_) {
      return true;
   }

   public boolean preventDespawn() {
      return false;
   }

   protected boolean func_225511_J_() {
      return false;
   }

   public void checkDespawn() {
      if (this.world.getDifficulty() == Difficulty.PEACEFUL && this.func_225511_J_()) {
         this.remove();
      } else if (!this.isNoDespawnRequired() && !this.preventDespawn()) {
         Entity entity = this.world.getClosestPlayer(this, -1.0D);
         Result result = ForgeEventFactory.canEntityDespawn(this);
         if (result == Result.DENY) {
            this.idleTime = 0;
            entity = null;
         } else if (result == Result.ALLOW) {
            this.remove();
            entity = null;
         }

         if (entity != null) {
            double d0 = entity.getDistanceSq((Entity)this);
            if (d0 > 16384.0D && this.canDespawn(d0)) {
               this.remove();
            }

            if (this.idleTime > 600 && this.rand.nextInt(800) == 0 && d0 > 1024.0D && this.canDespawn(d0)) {
               this.remove();
            } else if (d0 < 1024.0D) {
               this.idleTime = 0;
            }
         }
      } else {
         this.idleTime = 0;
      }

   }

   protected final void updateEntityActionState() {
      ++this.idleTime;
      this.world.getProfiler().startSection("sensing");
      this.senses.tick();
      this.world.getProfiler().endSection();
      this.world.getProfiler().startSection("targetSelector");
      this.targetSelector.tick();
      this.world.getProfiler().endSection();
      this.world.getProfiler().startSection("goalSelector");
      this.goalSelector.tick();
      this.world.getProfiler().endSection();
      this.world.getProfiler().startSection("navigation");
      this.navigator.tick();
      this.world.getProfiler().endSection();
      this.world.getProfiler().startSection("mob tick");
      this.updateAITasks();
      this.world.getProfiler().endSection();
      this.world.getProfiler().startSection("controls");
      this.world.getProfiler().startSection("move");
      this.moveController.tick();
      this.world.getProfiler().endStartSection("look");
      this.lookController.tick();
      this.world.getProfiler().endStartSection("jump");
      this.jumpController.tick();
      this.world.getProfiler().endSection();
      this.world.getProfiler().endSection();
      this.func_213387_K();
   }

   protected void func_213387_K() {
      DebugPacketSender.func_218800_a(this.world, this, this.goalSelector);
   }

   protected void updateAITasks() {
   }

   public int getVerticalFaceSpeed() {
      return 40;
   }

   public int getHorizontalFaceSpeed() {
      return 75;
   }

   public int func_213396_dB() {
      return 10;
   }

   public void faceEntity(Entity p_70625_1_, float p_70625_2_, float p_70625_3_) {
      double d0 = p_70625_1_.func_226277_ct_() - this.func_226277_ct_();
      double d2 = p_70625_1_.func_226281_cx_() - this.func_226281_cx_();
      double d1;
      if (p_70625_1_ instanceof LivingEntity) {
         LivingEntity livingentity = (LivingEntity)p_70625_1_;
         d1 = livingentity.func_226280_cw_() - this.func_226280_cw_();
      } else {
         d1 = (p_70625_1_.getBoundingBox().minY + p_70625_1_.getBoundingBox().maxY) / 2.0D - this.func_226280_cw_();
      }

      double d3 = (double)MathHelper.sqrt(d0 * d0 + d2 * d2);
      float f = (float)(MathHelper.atan2(d2, d0) * 57.2957763671875D) - 90.0F;
      float f1 = (float)(-(MathHelper.atan2(d1, d3) * 57.2957763671875D));
      this.rotationPitch = this.updateRotation(this.rotationPitch, f1, p_70625_3_);
      this.rotationYaw = this.updateRotation(this.rotationYaw, f, p_70625_2_);
   }

   private float updateRotation(float p_70663_1_, float p_70663_2_, float p_70663_3_) {
      float f = MathHelper.wrapDegrees(p_70663_2_ - p_70663_1_);
      if (f > p_70663_3_) {
         f = p_70663_3_;
      }

      if (f < -p_70663_3_) {
         f = -p_70663_3_;
      }

      return p_70663_1_ + f;
   }

   public static boolean func_223315_a(EntityType<? extends MobEntity> p_223315_0_, IWorld p_223315_1_, SpawnReason p_223315_2_, BlockPos p_223315_3_, Random p_223315_4_) {
      BlockPos blockpos = p_223315_3_.down();
      return p_223315_2_ == SpawnReason.SPAWNER || p_223315_1_.getBlockState(blockpos).canEntitySpawn(p_223315_1_, blockpos, p_223315_0_);
   }

   public boolean canSpawn(IWorld p_213380_1_, SpawnReason p_213380_2_) {
      return true;
   }

   public boolean isNotColliding(IWorldReader p_205019_1_) {
      return !p_205019_1_.containsAnyLiquid(this.getBoundingBox()) && p_205019_1_.func_226668_i_(this);
   }

   public int getMaxSpawnedInChunk() {
      return 4;
   }

   public boolean func_204209_c(int p_204209_1_) {
      return false;
   }

   public int getMaxFallHeight() {
      if (this.getAttackTarget() == null) {
         return 3;
      } else {
         int i = (int)(this.getHealth() - this.getMaxHealth() * 0.33F);
         i -= (3 - this.world.getDifficulty().getId()) * 4;
         if (i < 0) {
            i = 0;
         }

         return i + 3;
      }
   }

   public Iterable<ItemStack> getHeldEquipment() {
      return this.inventoryHands;
   }

   public Iterable<ItemStack> getArmorInventoryList() {
      return this.inventoryArmor;
   }

   public ItemStack getItemStackFromSlot(EquipmentSlotType p_184582_1_) {
      switch(p_184582_1_.getSlotType()) {
      case HAND:
         return (ItemStack)this.inventoryHands.get(p_184582_1_.getIndex());
      case ARMOR:
         return (ItemStack)this.inventoryArmor.get(p_184582_1_.getIndex());
      default:
         return ItemStack.EMPTY;
      }
   }

   public void setItemStackToSlot(EquipmentSlotType p_184201_1_, ItemStack p_184201_2_) {
      switch(p_184201_1_.getSlotType()) {
      case HAND:
         this.inventoryHands.set(p_184201_1_.getIndex(), p_184201_2_);
         break;
      case ARMOR:
         this.inventoryArmor.set(p_184201_1_.getIndex(), p_184201_2_);
      }

   }

   protected void dropSpecialItems(DamageSource p_213333_1_, int p_213333_2_, boolean p_213333_3_) {
      super.dropSpecialItems(p_213333_1_, p_213333_2_, p_213333_3_);
      EquipmentSlotType[] var4 = EquipmentSlotType.values();
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         EquipmentSlotType equipmentslottype = var4[var6];
         ItemStack itemstack = this.getItemStackFromSlot(equipmentslottype);
         float f = this.getDropChance(equipmentslottype);
         boolean flag = f > 1.0F;
         if (!itemstack.isEmpty() && !EnchantmentHelper.hasVanishingCurse(itemstack) && (p_213333_3_ || flag) && Math.max(this.rand.nextFloat() - (float)p_213333_2_ * 0.01F, 0.0F) < f) {
            if (!flag && itemstack.isDamageable()) {
               itemstack.setDamage(itemstack.getMaxDamage() - this.rand.nextInt(1 + this.rand.nextInt(Math.max(itemstack.getMaxDamage() - 3, 1))));
            }

            this.entityDropItem(itemstack);
         }
      }

   }

   protected float getDropChance(EquipmentSlotType p_205712_1_) {
      float f;
      switch(p_205712_1_.getSlotType()) {
      case HAND:
         f = this.inventoryHandsDropChances[p_205712_1_.getIndex()];
         break;
      case ARMOR:
         f = this.inventoryArmorDropChances[p_205712_1_.getIndex()];
         break;
      default:
         f = 0.0F;
      }

      return f;
   }

   protected void setEquipmentBasedOnDifficulty(DifficultyInstance p_180481_1_) {
      if (this.rand.nextFloat() < 0.15F * p_180481_1_.getClampedAdditionalDifficulty()) {
         int i = this.rand.nextInt(2);
         float f = this.world.getDifficulty() == Difficulty.HARD ? 0.1F : 0.25F;
         if (this.rand.nextFloat() < 0.095F) {
            ++i;
         }

         if (this.rand.nextFloat() < 0.095F) {
            ++i;
         }

         if (this.rand.nextFloat() < 0.095F) {
            ++i;
         }

         boolean flag = true;
         EquipmentSlotType[] var5 = EquipmentSlotType.values();
         int var6 = var5.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            EquipmentSlotType equipmentslottype = var5[var7];
            if (equipmentslottype.getSlotType() == EquipmentSlotType.Group.ARMOR) {
               ItemStack itemstack = this.getItemStackFromSlot(equipmentslottype);
               if (!flag && this.rand.nextFloat() < f) {
                  break;
               }

               flag = false;
               if (itemstack.isEmpty()) {
                  Item item = getArmorByChance(equipmentslottype, i);
                  if (item != null) {
                     this.setItemStackToSlot(equipmentslottype, new ItemStack(item));
                  }
               }
            }
         }
      }

   }

   public static EquipmentSlotType getSlotForItemStack(ItemStack p_184640_0_) {
      EquipmentSlotType slot = p_184640_0_.getEquipmentSlot();
      if (slot != null) {
         return slot;
      } else {
         Item item = p_184640_0_.getItem();
         if (item == Blocks.CARVED_PUMPKIN.asItem() || item instanceof BlockItem && ((BlockItem)item).getBlock() instanceof AbstractSkullBlock) {
            return EquipmentSlotType.HEAD;
         } else if (item instanceof ArmorItem) {
            return ((ArmorItem)item).getEquipmentSlot();
         } else if (item == Items.ELYTRA) {
            return EquipmentSlotType.CHEST;
         } else {
            return p_184640_0_.isShield((LivingEntity)null) ? EquipmentSlotType.OFFHAND : EquipmentSlotType.MAINHAND;
         }
      }
   }

   @Nullable
   public static Item getArmorByChance(EquipmentSlotType p_184636_0_, int p_184636_1_) {
      switch(p_184636_0_) {
      case HEAD:
         if (p_184636_1_ == 0) {
            return Items.LEATHER_HELMET;
         } else if (p_184636_1_ == 1) {
            return Items.GOLDEN_HELMET;
         } else if (p_184636_1_ == 2) {
            return Items.CHAINMAIL_HELMET;
         } else if (p_184636_1_ == 3) {
            return Items.IRON_HELMET;
         } else if (p_184636_1_ == 4) {
            return Items.DIAMOND_HELMET;
         }
      case CHEST:
         if (p_184636_1_ == 0) {
            return Items.LEATHER_CHESTPLATE;
         } else if (p_184636_1_ == 1) {
            return Items.GOLDEN_CHESTPLATE;
         } else if (p_184636_1_ == 2) {
            return Items.CHAINMAIL_CHESTPLATE;
         } else if (p_184636_1_ == 3) {
            return Items.IRON_CHESTPLATE;
         } else if (p_184636_1_ == 4) {
            return Items.DIAMOND_CHESTPLATE;
         }
      case LEGS:
         if (p_184636_1_ == 0) {
            return Items.LEATHER_LEGGINGS;
         } else if (p_184636_1_ == 1) {
            return Items.GOLDEN_LEGGINGS;
         } else if (p_184636_1_ == 2) {
            return Items.CHAINMAIL_LEGGINGS;
         } else if (p_184636_1_ == 3) {
            return Items.IRON_LEGGINGS;
         } else if (p_184636_1_ == 4) {
            return Items.DIAMOND_LEGGINGS;
         }
      case FEET:
         if (p_184636_1_ == 0) {
            return Items.LEATHER_BOOTS;
         } else if (p_184636_1_ == 1) {
            return Items.GOLDEN_BOOTS;
         } else if (p_184636_1_ == 2) {
            return Items.CHAINMAIL_BOOTS;
         } else if (p_184636_1_ == 3) {
            return Items.IRON_BOOTS;
         } else if (p_184636_1_ == 4) {
            return Items.DIAMOND_BOOTS;
         }
      default:
         return null;
      }
   }

   protected void setEnchantmentBasedOnDifficulty(DifficultyInstance p_180483_1_) {
      float f = p_180483_1_.getClampedAdditionalDifficulty();
      if (!this.getHeldItemMainhand().isEmpty() && this.rand.nextFloat() < 0.25F * f) {
         this.setItemStackToSlot(EquipmentSlotType.MAINHAND, EnchantmentHelper.addRandomEnchantment(this.rand, this.getHeldItemMainhand(), (int)(5.0F + f * (float)this.rand.nextInt(18)), false));
      }

      EquipmentSlotType[] var3 = EquipmentSlotType.values();
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         EquipmentSlotType equipmentslottype = var3[var5];
         if (equipmentslottype.getSlotType() == EquipmentSlotType.Group.ARMOR) {
            ItemStack itemstack = this.getItemStackFromSlot(equipmentslottype);
            if (!itemstack.isEmpty() && this.rand.nextFloat() < 0.5F * f) {
               this.setItemStackToSlot(equipmentslottype, EnchantmentHelper.addRandomEnchantment(this.rand, itemstack, (int)(5.0F + f * (float)this.rand.nextInt(18)), false));
            }
         }
      }

   }

   @Nullable
   public ILivingEntityData onInitialSpawn(IWorld p_213386_1_, DifficultyInstance p_213386_2_, SpawnReason p_213386_3_, @Nullable ILivingEntityData p_213386_4_, @Nullable CompoundNBT p_213386_5_) {
      this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).applyModifier(new AttributeModifier("Random spawn bonus", this.rand.nextGaussian() * 0.05D, AttributeModifier.Operation.MULTIPLY_BASE));
      if (this.rand.nextFloat() < 0.05F) {
         this.setLeftHanded(true);
      } else {
         this.setLeftHanded(false);
      }

      return p_213386_4_;
   }

   public boolean canBeSteered() {
      return false;
   }

   public void enablePersistence() {
      this.persistenceRequired = true;
   }

   public void setDropChance(EquipmentSlotType p_184642_1_, float p_184642_2_) {
      switch(p_184642_1_.getSlotType()) {
      case HAND:
         this.inventoryHandsDropChances[p_184642_1_.getIndex()] = p_184642_2_;
         break;
      case ARMOR:
         this.inventoryArmorDropChances[p_184642_1_.getIndex()] = p_184642_2_;
      }

   }

   public boolean canPickUpLoot() {
      return this.canPickUpLoot;
   }

   public void setCanPickUpLoot(boolean p_98053_1_) {
      this.canPickUpLoot = p_98053_1_;
   }

   public boolean func_213365_e(ItemStack p_213365_1_) {
      EquipmentSlotType equipmentslottype = getSlotForItemStack(p_213365_1_);
      return this.getItemStackFromSlot(equipmentslottype).isEmpty() && this.canPickUpLoot();
   }

   public boolean isNoDespawnRequired() {
      return this.persistenceRequired;
   }

   public final boolean processInitialInteract(PlayerEntity p_184230_1_, Hand p_184230_2_) {
      if (!this.isAlive()) {
         return false;
      } else if (this.getLeashHolder() == p_184230_1_) {
         this.clearLeashed(true, !p_184230_1_.abilities.isCreativeMode);
         return true;
      } else {
         ItemStack itemstack = p_184230_1_.getHeldItem(p_184230_2_);
         if (itemstack.getItem() == Items.LEAD && this.canBeLeashedTo(p_184230_1_)) {
            this.setLeashHolder(p_184230_1_, true);
            itemstack.shrink(1);
            return true;
         } else {
            return this.processInteract(p_184230_1_, p_184230_2_) ? true : super.processInitialInteract(p_184230_1_, p_184230_2_);
         }
      }
   }

   protected boolean processInteract(PlayerEntity p_184645_1_, Hand p_184645_2_) {
      return false;
   }

   public boolean isWithinHomeDistanceCurrentPosition() {
      return this.isWithinHomeDistanceFromPosition(new BlockPos(this));
   }

   public boolean isWithinHomeDistanceFromPosition(BlockPos p_213389_1_) {
      if (this.maximumHomeDistance == -1.0F) {
         return true;
      } else {
         return this.homePosition.distanceSq(p_213389_1_) < (double)(this.maximumHomeDistance * this.maximumHomeDistance);
      }
   }

   public void setHomePosAndDistance(BlockPos p_213390_1_, int p_213390_2_) {
      this.homePosition = p_213390_1_;
      this.maximumHomeDistance = (float)p_213390_2_;
   }

   public BlockPos getHomePosition() {
      return this.homePosition;
   }

   public float getMaximumHomeDistance() {
      return this.maximumHomeDistance;
   }

   public boolean detachHome() {
      return this.maximumHomeDistance != -1.0F;
   }

   protected void updateLeashedState() {
      if (this.leashNBTTag != null) {
         this.recreateLeash();
      }

      if (this.leashHolder != null && (!this.isAlive() || !this.leashHolder.isAlive())) {
         this.clearLeashed(true, true);
      }

   }

   public void clearLeashed(boolean p_110160_1_, boolean p_110160_2_) {
      if (this.leashHolder != null) {
         this.forceSpawn = false;
         if (!(this.leashHolder instanceof PlayerEntity)) {
            this.leashHolder.forceSpawn = false;
         }

         this.leashHolder = null;
         if (!this.world.isRemote && p_110160_2_) {
            this.entityDropItem(Items.LEAD);
         }

         if (!this.world.isRemote && p_110160_1_ && this.world instanceof ServerWorld) {
            ((ServerWorld)this.world).getChunkProvider().sendToAllTracking(this, new SMountEntityPacket(this, (Entity)null));
         }
      }

   }

   public boolean canBeLeashedTo(PlayerEntity p_184652_1_) {
      return !this.getLeashed() && !(this instanceof IMob);
   }

   public boolean getLeashed() {
      return this.leashHolder != null;
   }

   @Nullable
   public Entity getLeashHolder() {
      if (this.leashHolder == null && this.leashHolderID != 0 && this.world.isRemote) {
         this.leashHolder = this.world.getEntityByID(this.leashHolderID);
      }

      return this.leashHolder;
   }

   public void setLeashHolder(Entity p_110162_1_, boolean p_110162_2_) {
      this.leashHolder = p_110162_1_;
      this.forceSpawn = true;
      if (!(this.leashHolder instanceof PlayerEntity)) {
         this.leashHolder.forceSpawn = true;
      }

      if (!this.world.isRemote && p_110162_2_ && this.world instanceof ServerWorld) {
         ((ServerWorld)this.world).getChunkProvider().sendToAllTracking(this, new SMountEntityPacket(this, this.leashHolder));
      }

      if (this.isPassenger()) {
         this.stopRiding();
      }

   }

   @OnlyIn(Dist.CLIENT)
   public void func_213381_d(int p_213381_1_) {
      this.leashHolderID = p_213381_1_;
      this.clearLeashed(false, false);
   }

   public boolean startRiding(Entity p_184205_1_, boolean p_184205_2_) {
      boolean flag = super.startRiding(p_184205_1_, p_184205_2_);
      if (flag && this.getLeashed()) {
         this.clearLeashed(true, true);
      }

      return flag;
   }

   private void recreateLeash() {
      if (this.leashNBTTag != null && this.world instanceof ServerWorld) {
         if (this.leashNBTTag.hasUniqueId("UUID")) {
            UUID uuid = this.leashNBTTag.getUniqueId("UUID");
            Entity entity = ((ServerWorld)this.world).getEntityByUuid(uuid);
            if (entity != null) {
               this.setLeashHolder(entity, true);
            }
         } else if (this.leashNBTTag.contains("X", 99) && this.leashNBTTag.contains("Y", 99) && this.leashNBTTag.contains("Z", 99)) {
            BlockPos blockpos = new BlockPos(this.leashNBTTag.getInt("X"), this.leashNBTTag.getInt("Y"), this.leashNBTTag.getInt("Z"));
            this.setLeashHolder(LeashKnotEntity.create(this.world, blockpos), true);
         } else {
            this.clearLeashed(false, true);
         }

         this.leashNBTTag = null;
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

      if (!p_174820_2_.isEmpty() && !isItemStackInSlot(equipmentslottype, p_174820_2_) && equipmentslottype != EquipmentSlotType.HEAD) {
         return false;
      } else {
         this.setItemStackToSlot(equipmentslottype, p_174820_2_);
         return true;
      }
   }

   public boolean canPassengerSteer() {
      return this.canBeSteered() && super.canPassengerSteer();
   }

   public static boolean isItemStackInSlot(EquipmentSlotType p_184648_0_, ItemStack p_184648_1_) {
      EquipmentSlotType equipmentslottype = getSlotForItemStack(p_184648_1_);
      return equipmentslottype == p_184648_0_ || equipmentslottype == EquipmentSlotType.MAINHAND && p_184648_0_ == EquipmentSlotType.OFFHAND || equipmentslottype == EquipmentSlotType.OFFHAND && p_184648_0_ == EquipmentSlotType.MAINHAND;
   }

   public boolean isServerWorld() {
      return super.isServerWorld() && !this.isAIDisabled();
   }

   public void setNoAI(boolean p_94061_1_) {
      byte b0 = (Byte)this.dataManager.get(AI_FLAGS);
      this.dataManager.set(AI_FLAGS, p_94061_1_ ? (byte)(b0 | 1) : (byte)(b0 & -2));
   }

   public void setLeftHanded(boolean p_184641_1_) {
      byte b0 = (Byte)this.dataManager.get(AI_FLAGS);
      this.dataManager.set(AI_FLAGS, p_184641_1_ ? (byte)(b0 | 2) : (byte)(b0 & -3));
   }

   public void setAggroed(boolean p_213395_1_) {
      byte b0 = (Byte)this.dataManager.get(AI_FLAGS);
      this.dataManager.set(AI_FLAGS, p_213395_1_ ? (byte)(b0 | 4) : (byte)(b0 & -5));
   }

   public boolean isAIDisabled() {
      return ((Byte)this.dataManager.get(AI_FLAGS) & 1) != 0;
   }

   public boolean isLeftHanded() {
      return ((Byte)this.dataManager.get(AI_FLAGS) & 2) != 0;
   }

   public boolean isAggressive() {
      return ((Byte)this.dataManager.get(AI_FLAGS) & 4) != 0;
   }

   public HandSide getPrimaryHand() {
      return this.isLeftHanded() ? HandSide.LEFT : HandSide.RIGHT;
   }

   public boolean canAttack(LivingEntity p_213336_1_) {
      return p_213336_1_.getType() == EntityType.PLAYER && ((PlayerEntity)p_213336_1_).abilities.disableDamage ? false : super.canAttack(p_213336_1_);
   }

   public boolean attackEntityAsMob(Entity p_70652_1_) {
      float f = (float)this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getValue();
      float f1 = (float)this.getAttribute(SharedMonsterAttributes.ATTACK_KNOCKBACK).getValue();
      if (p_70652_1_ instanceof LivingEntity) {
         f += EnchantmentHelper.getModifierForCreature(this.getHeldItemMainhand(), ((LivingEntity)p_70652_1_).getCreatureAttribute());
         f1 += (float)EnchantmentHelper.getKnockbackModifier(this);
      }

      int i = EnchantmentHelper.getFireAspectModifier(this);
      if (i > 0) {
         p_70652_1_.setFire(i * 4);
      }

      boolean flag = p_70652_1_.attackEntityFrom(DamageSource.causeMobDamage(this), f);
      if (flag) {
         if (f1 > 0.0F && p_70652_1_ instanceof LivingEntity) {
            ((LivingEntity)p_70652_1_).knockBack(this, f1 * 0.5F, (double)MathHelper.sin(this.rotationYaw * 0.017453292F), (double)(-MathHelper.cos(this.rotationYaw * 0.017453292F)));
            this.setMotion(this.getMotion().mul(0.6D, 1.0D, 0.6D));
         }

         if (p_70652_1_ instanceof PlayerEntity) {
            PlayerEntity playerentity = (PlayerEntity)p_70652_1_;
            ItemStack itemstack = this.getHeldItemMainhand();
            ItemStack itemstack1 = playerentity.isHandActive() ? playerentity.getActiveItemStack() : ItemStack.EMPTY;
            if (!itemstack.isEmpty() && !itemstack1.isEmpty() && itemstack.canDisableShield(itemstack1, playerentity, this) && itemstack1.isShield(playerentity)) {
               float f2 = 0.25F + (float)EnchantmentHelper.getEfficiencyModifier(this) * 0.05F;
               if (this.rand.nextFloat() < f2) {
                  playerentity.getCooldownTracker().setCooldown(itemstack.getItem(), 100);
                  this.world.setEntityState(playerentity, (byte)30);
               }
            }
         }

         this.applyEnchantments(this, p_70652_1_);
         this.setLastAttackedEntity(p_70652_1_);
      }

      return flag;
   }

   protected boolean isInDaylight() {
      if (this.world.isDaytime() && !this.world.isRemote) {
         float f = this.getBrightness();
         BlockPos blockpos = this.getRidingEntity() instanceof BoatEntity ? (new BlockPos(this.func_226277_ct_(), (double)Math.round(this.func_226278_cu_()), this.func_226281_cx_())).up() : new BlockPos(this.func_226277_ct_(), (double)Math.round(this.func_226278_cu_()), this.func_226281_cx_());
         if (f > 0.5F && this.rand.nextFloat() * 30.0F < (f - 0.4F) * 2.0F && this.world.func_226660_f_(blockpos)) {
            return true;
         }
      }

      return false;
   }

   protected void handleFluidJump(Tag<Fluid> p_180466_1_) {
      if (this.getNavigator().getCanSwim()) {
         super.handleFluidJump(p_180466_1_);
      } else {
         this.setMotion(this.getMotion().add(0.0D, 0.3D, 0.0D));
      }

   }

   public boolean isHolding(Item p_213382_1_) {
      return this.getHeldItemMainhand().getItem() == p_213382_1_ || this.getHeldItemOffhand().getItem() == p_213382_1_;
   }

   static {
      AI_FLAGS = EntityDataManager.createKey(MobEntity.class, DataSerializers.BYTE);
   }
}
