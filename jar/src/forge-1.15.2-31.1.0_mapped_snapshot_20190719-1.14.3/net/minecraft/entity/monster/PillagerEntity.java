package net.minecraft.entity.monster;

import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ICrossbowUser;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.RandomWalkingGoal;
import net.minecraft.entity.ai.goal.RangedCrossbowAttackGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.BannerItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.raid.Raid;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class PillagerEntity extends AbstractIllagerEntity implements ICrossbowUser, IRangedAttackMob {
   private static final DataParameter<Boolean> DATA_CHARGING_STATE;
   private final Inventory inventory = new Inventory(5);

   public PillagerEntity(EntityType<? extends PillagerEntity> p_i50198_1_, World p_i50198_2_) {
      super(p_i50198_1_, p_i50198_2_);
   }

   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(0, new SwimGoal(this));
      this.goalSelector.addGoal(2, new AbstractRaiderEntity.FindTargetGoal(this, 10.0F));
      this.goalSelector.addGoal(3, new RangedCrossbowAttackGoal(this, 1.0D, 8.0F));
      this.goalSelector.addGoal(8, new RandomWalkingGoal(this, 0.6D));
      this.goalSelector.addGoal(9, new LookAtGoal(this, PlayerEntity.class, 15.0F, 1.0F));
      this.goalSelector.addGoal(10, new LookAtGoal(this, MobEntity.class, 15.0F));
      this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, new Class[]{AbstractRaiderEntity.class})).setCallsForHelp());
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, PlayerEntity.class, true));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, AbstractVillagerEntity.class, false));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, IronGolemEntity.class, true));
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3499999940395355D);
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(24.0D);
      this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(5.0D);
      this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(32.0D);
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(DATA_CHARGING_STATE, false);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isCharging() {
      return (Boolean)this.dataManager.get(DATA_CHARGING_STATE);
   }

   public void setCharging(boolean p_213671_1_) {
      this.dataManager.set(DATA_CHARGING_STATE, p_213671_1_);
   }

   public void writeAdditional(CompoundNBT p_213281_1_) {
      super.writeAdditional(p_213281_1_);
      ListNBT lvt_2_1_ = new ListNBT();

      for(int lvt_3_1_ = 0; lvt_3_1_ < this.inventory.getSizeInventory(); ++lvt_3_1_) {
         ItemStack lvt_4_1_ = this.inventory.getStackInSlot(lvt_3_1_);
         if (!lvt_4_1_.isEmpty()) {
            lvt_2_1_.add(lvt_4_1_.write(new CompoundNBT()));
         }
      }

      p_213281_1_.put("Inventory", lvt_2_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public AbstractIllagerEntity.ArmPose getArmPose() {
      if (this.isCharging()) {
         return AbstractIllagerEntity.ArmPose.CROSSBOW_CHARGE;
      } else if (this.isHolding(Items.CROSSBOW)) {
         return AbstractIllagerEntity.ArmPose.CROSSBOW_HOLD;
      } else {
         return this.isAggressive() ? AbstractIllagerEntity.ArmPose.ATTACKING : AbstractIllagerEntity.ArmPose.NEUTRAL;
      }
   }

   public void readAdditional(CompoundNBT p_70037_1_) {
      super.readAdditional(p_70037_1_);
      ListNBT lvt_2_1_ = p_70037_1_.getList("Inventory", 10);

      for(int lvt_3_1_ = 0; lvt_3_1_ < lvt_2_1_.size(); ++lvt_3_1_) {
         ItemStack lvt_4_1_ = ItemStack.read(lvt_2_1_.getCompound(lvt_3_1_));
         if (!lvt_4_1_.isEmpty()) {
            this.inventory.addItem(lvt_4_1_);
         }
      }

      this.setCanPickUpLoot(true);
   }

   public float getBlockPathWeight(BlockPos p_205022_1_, IWorldReader p_205022_2_) {
      Block lvt_3_1_ = p_205022_2_.getBlockState(p_205022_1_.down()).getBlock();
      return lvt_3_1_ != Blocks.GRASS_BLOCK && lvt_3_1_ != Blocks.SAND ? 0.5F - p_205022_2_.getBrightness(p_205022_1_) : 10.0F;
   }

   public int getMaxSpawnedInChunk() {
      return 1;
   }

   @Nullable
   public ILivingEntityData onInitialSpawn(IWorld p_213386_1_, DifficultyInstance p_213386_2_, SpawnReason p_213386_3_, @Nullable ILivingEntityData p_213386_4_, @Nullable CompoundNBT p_213386_5_) {
      this.setEquipmentBasedOnDifficulty(p_213386_2_);
      this.setEnchantmentBasedOnDifficulty(p_213386_2_);
      return super.onInitialSpawn(p_213386_1_, p_213386_2_, p_213386_3_, p_213386_4_, p_213386_5_);
   }

   protected void setEquipmentBasedOnDifficulty(DifficultyInstance p_180481_1_) {
      ItemStack lvt_2_1_ = new ItemStack(Items.CROSSBOW);
      if (this.rand.nextInt(300) == 0) {
         Map<Enchantment, Integer> lvt_3_1_ = Maps.newHashMap();
         lvt_3_1_.put(Enchantments.PIERCING, 1);
         EnchantmentHelper.setEnchantments(lvt_3_1_, lvt_2_1_);
      }

      this.setItemStackToSlot(EquipmentSlotType.MAINHAND, lvt_2_1_);
   }

   public boolean isOnSameTeam(Entity p_184191_1_) {
      if (super.isOnSameTeam(p_184191_1_)) {
         return true;
      } else if (p_184191_1_ instanceof LivingEntity && ((LivingEntity)p_184191_1_).getCreatureAttribute() == CreatureAttribute.ILLAGER) {
         return this.getTeam() == null && p_184191_1_.getTeam() == null;
      } else {
         return false;
      }
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_PILLAGER_AMBIENT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_PILLAGER_DEATH;
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.ENTITY_PILLAGER_HURT;
   }

   public void attackEntityWithRangedAttack(LivingEntity p_82196_1_, float p_82196_2_) {
      Hand lvt_3_1_ = ProjectileHelper.getHandWith(this, Items.CROSSBOW);
      ItemStack lvt_4_1_ = this.getHeldItem(lvt_3_1_);
      if (this.isHolding(Items.CROSSBOW)) {
         CrossbowItem.fireProjectiles(this.world, this, lvt_3_1_, lvt_4_1_, 1.6F, (float)(14 - this.world.getDifficulty().getId() * 4));
      }

      this.idleTime = 0;
   }

   public void shoot(LivingEntity p_213670_1_, ItemStack p_213670_2_, IProjectile p_213670_3_, float p_213670_4_) {
      Entity lvt_5_1_ = (Entity)p_213670_3_;
      double lvt_6_1_ = p_213670_1_.func_226277_ct_() - this.func_226277_ct_();
      double lvt_8_1_ = p_213670_1_.func_226281_cx_() - this.func_226281_cx_();
      double lvt_10_1_ = (double)MathHelper.sqrt(lvt_6_1_ * lvt_6_1_ + lvt_8_1_ * lvt_8_1_);
      double lvt_12_1_ = p_213670_1_.func_226283_e_(0.3333333333333333D) - lvt_5_1_.func_226278_cu_() + lvt_10_1_ * 0.20000000298023224D;
      Vector3f lvt_14_1_ = this.func_213673_a(new Vec3d(lvt_6_1_, lvt_12_1_, lvt_8_1_), p_213670_4_);
      p_213670_3_.shoot((double)lvt_14_1_.getX(), (double)lvt_14_1_.getY(), (double)lvt_14_1_.getZ(), 1.6F, (float)(14 - this.world.getDifficulty().getId() * 4));
      this.playSound(SoundEvents.ITEM_CROSSBOW_SHOOT, 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
   }

   private Vector3f func_213673_a(Vec3d p_213673_1_, float p_213673_2_) {
      Vec3d lvt_3_1_ = p_213673_1_.normalize();
      Vec3d lvt_4_1_ = lvt_3_1_.crossProduct(new Vec3d(0.0D, 1.0D, 0.0D));
      if (lvt_4_1_.lengthSquared() <= 1.0E-7D) {
         lvt_4_1_ = lvt_3_1_.crossProduct(this.func_213286_i(1.0F));
      }

      Quaternion lvt_5_1_ = new Quaternion(new Vector3f(lvt_4_1_), 90.0F, true);
      Vector3f lvt_6_1_ = new Vector3f(lvt_3_1_);
      lvt_6_1_.func_214905_a(lvt_5_1_);
      Quaternion lvt_7_1_ = new Quaternion(lvt_6_1_, p_213673_2_, true);
      Vector3f lvt_8_1_ = new Vector3f(lvt_3_1_);
      lvt_8_1_.func_214905_a(lvt_7_1_);
      return lvt_8_1_;
   }

   protected void updateEquipmentIfNeeded(ItemEntity p_175445_1_) {
      ItemStack lvt_2_1_ = p_175445_1_.getItem();
      if (lvt_2_1_.getItem() instanceof BannerItem) {
         super.updateEquipmentIfNeeded(p_175445_1_);
      } else {
         Item lvt_3_1_ = lvt_2_1_.getItem();
         if (this.func_213672_b(lvt_3_1_)) {
            ItemStack lvt_4_1_ = this.inventory.addItem(lvt_2_1_);
            if (lvt_4_1_.isEmpty()) {
               p_175445_1_.remove();
            } else {
               lvt_2_1_.setCount(lvt_4_1_.getCount());
            }
         }
      }

   }

   private boolean func_213672_b(Item p_213672_1_) {
      return this.isRaidActive() && p_213672_1_ == Items.WHITE_BANNER;
   }

   public boolean replaceItemInInventory(int p_174820_1_, ItemStack p_174820_2_) {
      if (super.replaceItemInInventory(p_174820_1_, p_174820_2_)) {
         return true;
      } else {
         int lvt_3_1_ = p_174820_1_ - 300;
         if (lvt_3_1_ >= 0 && lvt_3_1_ < this.inventory.getSizeInventory()) {
            this.inventory.setInventorySlotContents(lvt_3_1_, p_174820_2_);
            return true;
         } else {
            return false;
         }
      }
   }

   public void func_213660_a(int p_213660_1_, boolean p_213660_2_) {
      Raid lvt_3_1_ = this.getRaid();
      boolean lvt_4_1_ = this.rand.nextFloat() <= lvt_3_1_.func_221308_w();
      if (lvt_4_1_) {
         ItemStack lvt_5_1_ = new ItemStack(Items.CROSSBOW);
         Map<Enchantment, Integer> lvt_6_1_ = Maps.newHashMap();
         if (p_213660_1_ > lvt_3_1_.getWaves(Difficulty.NORMAL)) {
            lvt_6_1_.put(Enchantments.QUICK_CHARGE, 2);
         } else if (p_213660_1_ > lvt_3_1_.getWaves(Difficulty.EASY)) {
            lvt_6_1_.put(Enchantments.QUICK_CHARGE, 1);
         }

         lvt_6_1_.put(Enchantments.MULTISHOT, 1);
         EnchantmentHelper.setEnchantments(lvt_6_1_, lvt_5_1_);
         this.setItemStackToSlot(EquipmentSlotType.MAINHAND, lvt_5_1_);
      }

   }

   public SoundEvent getRaidLossSound() {
      return SoundEvents.ENTITY_PILLAGER_CELEBRATE;
   }

   static {
      DATA_CHARGING_STATE = EntityDataManager.createKey(PillagerEntity.class, DataSerializers.BOOLEAN);
   }
}
