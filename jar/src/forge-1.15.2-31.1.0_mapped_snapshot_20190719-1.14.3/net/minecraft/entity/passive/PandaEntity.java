package net.minecraft.entity.passive;

import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.entity.ai.goal.BreedGoal;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.GameRules;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class PandaEntity extends AnimalEntity {
   private static final DataParameter<Integer> field_213609_bA;
   private static final DataParameter<Integer> field_213593_bB;
   private static final DataParameter<Integer> field_213594_bD;
   private static final DataParameter<Byte> MAIN_GENE;
   private static final DataParameter<Byte> HIDDEN_GENE;
   private static final DataParameter<Byte> PANDA_FLAGS;
   private static final EntityPredicate field_229963_bD_;
   private boolean field_213598_bH;
   private boolean field_213599_bI;
   public int field_213608_bz;
   private Vec3d field_213600_bJ;
   private float field_213601_bK;
   private float field_213602_bL;
   private float field_213603_bM;
   private float field_213604_bN;
   private float field_213605_bO;
   private float field_213606_bP;
   private PandaEntity.WatchGoal field_229964_bN_;
   private static final Predicate<ItemEntity> field_213607_bQ;

   public PandaEntity(EntityType<? extends PandaEntity> p_i50252_1_, World p_i50252_2_) {
      super(p_i50252_1_, p_i50252_2_);
      this.moveController = new PandaEntity.MoveHelperController(this);
      if (!this.isChild()) {
         this.setCanPickUpLoot(true);
      }

   }

   public boolean func_213365_e(ItemStack p_213365_1_) {
      EquipmentSlotType lvt_2_1_ = MobEntity.getSlotForItemStack(p_213365_1_);
      if (!this.getItemStackFromSlot(lvt_2_1_).isEmpty()) {
         return false;
      } else {
         return lvt_2_1_ == EquipmentSlotType.MAINHAND && super.func_213365_e(p_213365_1_);
      }
   }

   public int func_213544_dV() {
      return (Integer)this.dataManager.get(field_213609_bA);
   }

   public void func_213588_r(int p_213588_1_) {
      this.dataManager.set(field_213609_bA, p_213588_1_);
   }

   public boolean func_213539_dW() {
      return this.getPandaFlag(2);
   }

   public boolean func_213556_dX() {
      return this.getPandaFlag(8);
   }

   public void func_213553_r(boolean p_213553_1_) {
      this.setPandaFlag(8, p_213553_1_);
   }

   public boolean func_213567_dY() {
      return this.getPandaFlag(16);
   }

   public void func_213542_s(boolean p_213542_1_) {
      this.setPandaFlag(16, p_213542_1_);
   }

   public boolean func_213578_dZ() {
      return (Integer)this.dataManager.get(field_213594_bD) > 0;
   }

   public void func_213534_t(boolean p_213534_1_) {
      this.dataManager.set(field_213594_bD, p_213534_1_ ? 1 : 0);
   }

   private int func_213559_es() {
      return (Integer)this.dataManager.get(field_213594_bD);
   }

   private void func_213571_t(int p_213571_1_) {
      this.dataManager.set(field_213594_bD, p_213571_1_);
   }

   public void func_213581_u(boolean p_213581_1_) {
      this.setPandaFlag(2, p_213581_1_);
      if (!p_213581_1_) {
         this.func_213562_s(0);
      }

   }

   public int func_213585_ee() {
      return (Integer)this.dataManager.get(field_213593_bB);
   }

   public void func_213562_s(int p_213562_1_) {
      this.dataManager.set(field_213593_bB, p_213562_1_);
   }

   public PandaEntity.Type getMainGene() {
      return PandaEntity.Type.byIndex((Byte)this.dataManager.get(MAIN_GENE));
   }

   public void setMainGene(PandaEntity.Type p_213589_1_) {
      if (p_213589_1_.getIndex() > 6) {
         p_213589_1_ = PandaEntity.Type.getRandomType(this.rand);
      }

      this.dataManager.set(MAIN_GENE, (byte)p_213589_1_.getIndex());
   }

   public PandaEntity.Type getHiddenGene() {
      return PandaEntity.Type.byIndex((Byte)this.dataManager.get(HIDDEN_GENE));
   }

   public void setHiddenGene(PandaEntity.Type p_213541_1_) {
      if (p_213541_1_.getIndex() > 6) {
         p_213541_1_ = PandaEntity.Type.getRandomType(this.rand);
      }

      this.dataManager.set(HIDDEN_GENE, (byte)p_213541_1_.getIndex());
   }

   public boolean func_213564_eh() {
      return this.getPandaFlag(4);
   }

   public void func_213576_v(boolean p_213576_1_) {
      this.setPandaFlag(4, p_213576_1_);
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(field_213609_bA, 0);
      this.dataManager.register(field_213593_bB, 0);
      this.dataManager.register(MAIN_GENE, (byte)0);
      this.dataManager.register(HIDDEN_GENE, (byte)0);
      this.dataManager.register(PANDA_FLAGS, (byte)0);
      this.dataManager.register(field_213594_bD, 0);
   }

   private boolean getPandaFlag(int p_213547_1_) {
      return ((Byte)this.dataManager.get(PANDA_FLAGS) & p_213547_1_) != 0;
   }

   private void setPandaFlag(int p_213587_1_, boolean p_213587_2_) {
      byte lvt_3_1_ = (Byte)this.dataManager.get(PANDA_FLAGS);
      if (p_213587_2_) {
         this.dataManager.set(PANDA_FLAGS, (byte)(lvt_3_1_ | p_213587_1_));
      } else {
         this.dataManager.set(PANDA_FLAGS, (byte)(lvt_3_1_ & ~p_213587_1_));
      }

   }

   public void writeAdditional(CompoundNBT p_213281_1_) {
      super.writeAdditional(p_213281_1_);
      p_213281_1_.putString("MainGene", this.getMainGene().getName());
      p_213281_1_.putString("HiddenGene", this.getHiddenGene().getName());
   }

   public void readAdditional(CompoundNBT p_70037_1_) {
      super.readAdditional(p_70037_1_);
      this.setMainGene(PandaEntity.Type.byName(p_70037_1_.getString("MainGene")));
      this.setHiddenGene(PandaEntity.Type.byName(p_70037_1_.getString("HiddenGene")));
   }

   @Nullable
   public AgeableEntity createChild(AgeableEntity p_90011_1_) {
      PandaEntity lvt_2_1_ = (PandaEntity)EntityType.PANDA.create(this.world);
      if (p_90011_1_ instanceof PandaEntity) {
         lvt_2_1_.func_213545_a(this, (PandaEntity)p_90011_1_);
      }

      lvt_2_1_.func_213554_ep();
      return lvt_2_1_;
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(0, new SwimGoal(this));
      this.goalSelector.addGoal(2, new PandaEntity.PanicGoal(this, 2.0D));
      this.goalSelector.addGoal(2, new PandaEntity.MateGoal(this, 1.0D));
      this.goalSelector.addGoal(3, new PandaEntity.AttackGoal(this, 1.2000000476837158D, true));
      this.goalSelector.addGoal(4, new TemptGoal(this, 1.0D, Ingredient.fromItems(Blocks.BAMBOO.asItem()), false));
      this.goalSelector.addGoal(6, new PandaEntity.AvoidGoal(this, PlayerEntity.class, 8.0F, 2.0D, 2.0D));
      this.goalSelector.addGoal(6, new PandaEntity.AvoidGoal(this, MonsterEntity.class, 4.0F, 2.0D, 2.0D));
      this.goalSelector.addGoal(7, new PandaEntity.SitGoal());
      this.goalSelector.addGoal(8, new PandaEntity.LieBackGoal(this));
      this.goalSelector.addGoal(8, new PandaEntity.ChildPlayGoal(this));
      this.field_229964_bN_ = new PandaEntity.WatchGoal(this, PlayerEntity.class, 6.0F);
      this.goalSelector.addGoal(9, this.field_229964_bN_);
      this.goalSelector.addGoal(10, new LookRandomlyGoal(this));
      this.goalSelector.addGoal(12, new PandaEntity.RollGoal(this));
      this.goalSelector.addGoal(13, new FollowParentGoal(this, 1.25D));
      this.goalSelector.addGoal(14, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
      this.targetSelector.addGoal(1, (new PandaEntity.RevengeGoal(this, new Class[0])).setCallsForHelp(new Class[0]));
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.15000000596046448D);
      this.getAttributes().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(6.0D);
   }

   public PandaEntity.Type func_213590_ei() {
      return PandaEntity.Type.func_221101_b(this.getMainGene(), this.getHiddenGene());
   }

   public boolean isLazy() {
      return this.func_213590_ei() == PandaEntity.Type.LAZY;
   }

   public boolean isWorried() {
      return this.func_213590_ei() == PandaEntity.Type.WORRIED;
   }

   public boolean func_213557_el() {
      return this.func_213590_ei() == PandaEntity.Type.PLAYFUL;
   }

   public boolean isWeak() {
      return this.func_213590_ei() == PandaEntity.Type.WEAK;
   }

   public boolean isAggressive() {
      return this.func_213590_ei() == PandaEntity.Type.AGGRESSIVE;
   }

   public boolean canBeLeashedTo(PlayerEntity p_184652_1_) {
      return false;
   }

   public boolean attackEntityAsMob(Entity p_70652_1_) {
      this.playSound(SoundEvents.ENTITY_PANDA_BITE, 1.0F, 1.0F);
      if (!this.isAggressive()) {
         this.field_213599_bI = true;
      }

      return super.attackEntityAsMob(p_70652_1_);
   }

   public void tick() {
      super.tick();
      if (this.isWorried()) {
         if (this.world.isThundering() && !this.isInWater()) {
            this.func_213553_r(true);
            this.func_213534_t(false);
         } else if (!this.func_213578_dZ()) {
            this.func_213553_r(false);
         }
      }

      if (this.getAttackTarget() == null) {
         this.field_213598_bH = false;
         this.field_213599_bI = false;
      }

      if (this.func_213544_dV() > 0) {
         if (this.getAttackTarget() != null) {
            this.faceEntity(this.getAttackTarget(), 90.0F, 90.0F);
         }

         if (this.func_213544_dV() == 29 || this.func_213544_dV() == 14) {
            this.playSound(SoundEvents.ENTITY_PANDA_CANT_BREED, 1.0F, 1.0F);
         }

         this.func_213588_r(this.func_213544_dV() - 1);
      }

      if (this.func_213539_dW()) {
         this.func_213562_s(this.func_213585_ee() + 1);
         if (this.func_213585_ee() > 20) {
            this.func_213581_u(false);
            this.func_213577_ez();
         } else if (this.func_213585_ee() == 1) {
            this.playSound(SoundEvents.ENTITY_PANDA_PRE_SNEEZE, 1.0F, 1.0F);
         }
      }

      if (this.func_213564_eh()) {
         this.func_213535_ey();
      } else {
         this.field_213608_bz = 0;
      }

      if (this.func_213556_dX()) {
         this.rotationPitch = 0.0F;
      }

      this.func_213574_ev();
      this.func_213546_et();
      this.func_213563_ew();
      this.func_213550_ex();
   }

   public boolean func_213566_eo() {
      return this.isWorried() && this.world.isThundering();
   }

   private void func_213546_et() {
      if (!this.func_213578_dZ() && this.func_213556_dX() && !this.func_213566_eo() && !this.getItemStackFromSlot(EquipmentSlotType.MAINHAND).isEmpty() && this.rand.nextInt(80) == 1) {
         this.func_213534_t(true);
      } else if (this.getItemStackFromSlot(EquipmentSlotType.MAINHAND).isEmpty() || !this.func_213556_dX()) {
         this.func_213534_t(false);
      }

      if (this.func_213578_dZ()) {
         this.func_213533_eu();
         if (!this.world.isRemote && this.func_213559_es() > 80 && this.rand.nextInt(20) == 1) {
            if (this.func_213559_es() > 100 && this.func_213548_j(this.getItemStackFromSlot(EquipmentSlotType.MAINHAND))) {
               if (!this.world.isRemote) {
                  this.setItemStackToSlot(EquipmentSlotType.MAINHAND, ItemStack.EMPTY);
               }

               this.func_213553_r(false);
            }

            this.func_213534_t(false);
            return;
         }

         this.func_213571_t(this.func_213559_es() + 1);
      }

   }

   private void func_213533_eu() {
      if (this.func_213559_es() % 5 == 0) {
         this.playSound(SoundEvents.ENTITY_PANDA_EAT, 0.5F + 0.5F * (float)this.rand.nextInt(2), (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);

         for(int lvt_1_1_ = 0; lvt_1_1_ < 6; ++lvt_1_1_) {
            Vec3d lvt_2_1_ = new Vec3d(((double)this.rand.nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, ((double)this.rand.nextFloat() - 0.5D) * 0.1D);
            lvt_2_1_ = lvt_2_1_.rotatePitch(-this.rotationPitch * 0.017453292F);
            lvt_2_1_ = lvt_2_1_.rotateYaw(-this.rotationYaw * 0.017453292F);
            double lvt_3_1_ = (double)(-this.rand.nextFloat()) * 0.6D - 0.3D;
            Vec3d lvt_5_1_ = new Vec3d(((double)this.rand.nextFloat() - 0.5D) * 0.8D, lvt_3_1_, 1.0D + ((double)this.rand.nextFloat() - 0.5D) * 0.4D);
            lvt_5_1_ = lvt_5_1_.rotateYaw(-this.renderYawOffset * 0.017453292F);
            lvt_5_1_ = lvt_5_1_.add(this.func_226277_ct_(), this.func_226280_cw_() + 1.0D, this.func_226281_cx_());
            this.world.addParticle(new ItemParticleData(ParticleTypes.ITEM, this.getItemStackFromSlot(EquipmentSlotType.MAINHAND)), lvt_5_1_.x, lvt_5_1_.y, lvt_5_1_.z, lvt_2_1_.x, lvt_2_1_.y + 0.05D, lvt_2_1_.z);
         }
      }

   }

   private void func_213574_ev() {
      this.field_213602_bL = this.field_213601_bK;
      if (this.func_213556_dX()) {
         this.field_213601_bK = Math.min(1.0F, this.field_213601_bK + 0.15F);
      } else {
         this.field_213601_bK = Math.max(0.0F, this.field_213601_bK - 0.19F);
      }

   }

   private void func_213563_ew() {
      this.field_213604_bN = this.field_213603_bM;
      if (this.func_213567_dY()) {
         this.field_213603_bM = Math.min(1.0F, this.field_213603_bM + 0.15F);
      } else {
         this.field_213603_bM = Math.max(0.0F, this.field_213603_bM - 0.19F);
      }

   }

   private void func_213550_ex() {
      this.field_213606_bP = this.field_213605_bO;
      if (this.func_213564_eh()) {
         this.field_213605_bO = Math.min(1.0F, this.field_213605_bO + 0.15F);
      } else {
         this.field_213605_bO = Math.max(0.0F, this.field_213605_bO - 0.19F);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public float func_213561_v(float p_213561_1_) {
      return MathHelper.lerp(p_213561_1_, this.field_213602_bL, this.field_213601_bK);
   }

   @OnlyIn(Dist.CLIENT)
   public float func_213583_w(float p_213583_1_) {
      return MathHelper.lerp(p_213583_1_, this.field_213604_bN, this.field_213603_bM);
   }

   @OnlyIn(Dist.CLIENT)
   public float func_213591_x(float p_213591_1_) {
      return MathHelper.lerp(p_213591_1_, this.field_213606_bP, this.field_213605_bO);
   }

   private void func_213535_ey() {
      ++this.field_213608_bz;
      if (this.field_213608_bz > 32) {
         this.func_213576_v(false);
      } else {
         if (!this.world.isRemote) {
            Vec3d lvt_1_1_ = this.getMotion();
            if (this.field_213608_bz == 1) {
               float lvt_2_1_ = this.rotationYaw * 0.017453292F;
               float lvt_3_1_ = this.isChild() ? 0.1F : 0.2F;
               this.field_213600_bJ = new Vec3d(lvt_1_1_.x + (double)(-MathHelper.sin(lvt_2_1_) * lvt_3_1_), 0.0D, lvt_1_1_.z + (double)(MathHelper.cos(lvt_2_1_) * lvt_3_1_));
               this.setMotion(this.field_213600_bJ.add(0.0D, 0.27D, 0.0D));
            } else if ((float)this.field_213608_bz != 7.0F && (float)this.field_213608_bz != 15.0F && (float)this.field_213608_bz != 23.0F) {
               this.setMotion(this.field_213600_bJ.x, lvt_1_1_.y, this.field_213600_bJ.z);
            } else {
               this.setMotion(0.0D, this.onGround ? 0.27D : lvt_1_1_.y, 0.0D);
            }
         }

      }
   }

   private void func_213577_ez() {
      Vec3d lvt_1_1_ = this.getMotion();
      this.world.addParticle(ParticleTypes.SNEEZE, this.func_226277_ct_() - (double)(this.getWidth() + 1.0F) * 0.5D * (double)MathHelper.sin(this.renderYawOffset * 0.017453292F), this.func_226280_cw_() - 0.10000000149011612D, this.func_226281_cx_() + (double)(this.getWidth() + 1.0F) * 0.5D * (double)MathHelper.cos(this.renderYawOffset * 0.017453292F), lvt_1_1_.x, 0.0D, lvt_1_1_.z);
      this.playSound(SoundEvents.ENTITY_PANDA_SNEEZE, 1.0F, 1.0F);
      List<PandaEntity> lvt_2_1_ = this.world.getEntitiesWithinAABB(PandaEntity.class, this.getBoundingBox().grow(10.0D));
      Iterator var3 = lvt_2_1_.iterator();

      while(var3.hasNext()) {
         PandaEntity lvt_4_1_ = (PandaEntity)var3.next();
         if (!lvt_4_1_.isChild() && lvt_4_1_.onGround && !lvt_4_1_.isInWater() && lvt_4_1_.func_213537_eq()) {
            lvt_4_1_.jump();
         }
      }

      if (!this.world.isRemote() && this.rand.nextInt(700) == 0 && this.world.getGameRules().getBoolean(GameRules.DO_MOB_LOOT)) {
         this.entityDropItem(Items.SLIME_BALL);
      }

   }

   protected void updateEquipmentIfNeeded(ItemEntity p_175445_1_) {
      if (this.getItemStackFromSlot(EquipmentSlotType.MAINHAND).isEmpty() && field_213607_bQ.test(p_175445_1_)) {
         ItemStack lvt_2_1_ = p_175445_1_.getItem();
         this.setItemStackToSlot(EquipmentSlotType.MAINHAND, lvt_2_1_);
         this.inventoryHandsDropChances[EquipmentSlotType.MAINHAND.getIndex()] = 2.0F;
         this.onItemPickup(p_175445_1_, lvt_2_1_.getCount());
         p_175445_1_.remove();
      }

   }

   public boolean attackEntityFrom(DamageSource p_70097_1_, float p_70097_2_) {
      this.func_213553_r(false);
      return super.attackEntityFrom(p_70097_1_, p_70097_2_);
   }

   @Nullable
   public ILivingEntityData onInitialSpawn(IWorld p_213386_1_, DifficultyInstance p_213386_2_, SpawnReason p_213386_3_, @Nullable ILivingEntityData p_213386_4_, @Nullable CompoundNBT p_213386_5_) {
      this.setMainGene(PandaEntity.Type.getRandomType(this.rand));
      this.setHiddenGene(PandaEntity.Type.getRandomType(this.rand));
      this.func_213554_ep();
      if (p_213386_4_ == null) {
         p_213386_4_ = new AgeableEntity.AgeableData();
         ((AgeableEntity.AgeableData)p_213386_4_).func_226258_a_(0.2F);
      }

      return super.onInitialSpawn(p_213386_1_, p_213386_2_, p_213386_3_, (ILivingEntityData)p_213386_4_, p_213386_5_);
   }

   public void func_213545_a(PandaEntity p_213545_1_, @Nullable PandaEntity p_213545_2_) {
      if (p_213545_2_ == null) {
         if (this.rand.nextBoolean()) {
            this.setMainGene(p_213545_1_.func_213568_eA());
            this.setHiddenGene(PandaEntity.Type.getRandomType(this.rand));
         } else {
            this.setMainGene(PandaEntity.Type.getRandomType(this.rand));
            this.setHiddenGene(p_213545_1_.func_213568_eA());
         }
      } else if (this.rand.nextBoolean()) {
         this.setMainGene(p_213545_1_.func_213568_eA());
         this.setHiddenGene(p_213545_2_.func_213568_eA());
      } else {
         this.setMainGene(p_213545_2_.func_213568_eA());
         this.setHiddenGene(p_213545_1_.func_213568_eA());
      }

      if (this.rand.nextInt(32) == 0) {
         this.setMainGene(PandaEntity.Type.getRandomType(this.rand));
      }

      if (this.rand.nextInt(32) == 0) {
         this.setHiddenGene(PandaEntity.Type.getRandomType(this.rand));
      }

   }

   private PandaEntity.Type func_213568_eA() {
      return this.rand.nextBoolean() ? this.getMainGene() : this.getHiddenGene();
   }

   public void func_213554_ep() {
      if (this.isWeak()) {
         this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0D);
      }

      if (this.isLazy()) {
         this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.07000000029802322D);
      }

   }

   private void func_213586_eB() {
      if (!this.isInWater()) {
         this.setMoveForward(0.0F);
         this.getNavigator().clearPath();
         this.func_213553_r(true);
      }

   }

   public boolean processInteract(PlayerEntity p_184645_1_, Hand p_184645_2_) {
      ItemStack lvt_3_1_ = p_184645_1_.getHeldItem(p_184645_2_);
      if (lvt_3_1_.getItem() instanceof SpawnEggItem) {
         return super.processInteract(p_184645_1_, p_184645_2_);
      } else if (this.func_213566_eo()) {
         return false;
      } else if (this.func_213567_dY()) {
         this.func_213542_s(false);
         return true;
      } else if (this.isBreedingItem(lvt_3_1_)) {
         if (this.getAttackTarget() != null) {
            this.field_213598_bH = true;
         }

         if (this.isChild()) {
            this.consumeItemFromStack(p_184645_1_, lvt_3_1_);
            this.ageUp((int)((float)(-this.getGrowingAge() / 20) * 0.1F), true);
         } else if (!this.world.isRemote && this.getGrowingAge() == 0 && this.canBreed()) {
            this.consumeItemFromStack(p_184645_1_, lvt_3_1_);
            this.setInLove(p_184645_1_);
         } else {
            if (this.world.isRemote || this.func_213556_dX() || this.isInWater()) {
               return false;
            }

            this.func_213586_eB();
            this.func_213534_t(true);
            ItemStack lvt_4_1_ = this.getItemStackFromSlot(EquipmentSlotType.MAINHAND);
            if (!lvt_4_1_.isEmpty() && !p_184645_1_.abilities.isCreativeMode) {
               this.entityDropItem(lvt_4_1_);
            }

            this.setItemStackToSlot(EquipmentSlotType.MAINHAND, new ItemStack(lvt_3_1_.getItem(), 1));
            this.consumeItemFromStack(p_184645_1_, lvt_3_1_);
         }

         p_184645_1_.func_226292_a_(p_184645_2_, true);
         return true;
      } else {
         return false;
      }
   }

   @Nullable
   protected SoundEvent getAmbientSound() {
      if (this.isAggressive()) {
         return SoundEvents.ENTITY_PANDA_AGGRESSIVE_AMBIENT;
      } else {
         return this.isWorried() ? SoundEvents.ENTITY_PANDA_WORRIED_AMBIENT : SoundEvents.ENTITY_PANDA_AMBIENT;
      }
   }

   protected void playStepSound(BlockPos p_180429_1_, BlockState p_180429_2_) {
      this.playSound(SoundEvents.ENTITY_PANDA_STEP, 0.15F, 1.0F);
   }

   public boolean isBreedingItem(ItemStack p_70877_1_) {
      return p_70877_1_.getItem() == Blocks.BAMBOO.asItem();
   }

   private boolean func_213548_j(ItemStack p_213548_1_) {
      return this.isBreedingItem(p_213548_1_) || p_213548_1_.getItem() == Blocks.CAKE.asItem();
   }

   @Nullable
   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_PANDA_DEATH;
   }

   @Nullable
   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.ENTITY_PANDA_HURT;
   }

   public boolean func_213537_eq() {
      return !this.func_213567_dY() && !this.func_213566_eo() && !this.func_213578_dZ() && !this.func_213564_eh() && !this.func_213556_dX();
   }

   static {
      field_213609_bA = EntityDataManager.createKey(PandaEntity.class, DataSerializers.VARINT);
      field_213593_bB = EntityDataManager.createKey(PandaEntity.class, DataSerializers.VARINT);
      field_213594_bD = EntityDataManager.createKey(PandaEntity.class, DataSerializers.VARINT);
      MAIN_GENE = EntityDataManager.createKey(PandaEntity.class, DataSerializers.BYTE);
      HIDDEN_GENE = EntityDataManager.createKey(PandaEntity.class, DataSerializers.BYTE);
      PANDA_FLAGS = EntityDataManager.createKey(PandaEntity.class, DataSerializers.BYTE);
      field_229963_bD_ = (new EntityPredicate()).setDistance(8.0D).allowFriendlyFire().allowInvulnerable();
      field_213607_bQ = (p_213575_0_) -> {
         Item lvt_1_1_ = p_213575_0_.getItem().getItem();
         return (lvt_1_1_ == Blocks.BAMBOO.asItem() || lvt_1_1_ == Blocks.CAKE.asItem()) && p_213575_0_.isAlive() && !p_213575_0_.cannotPickup();
      };
   }

   static class PanicGoal extends net.minecraft.entity.ai.goal.PanicGoal {
      private final PandaEntity panda;

      public PanicGoal(PandaEntity p_i51454_1_, double p_i51454_2_) {
         super(p_i51454_1_, p_i51454_2_);
         this.panda = p_i51454_1_;
      }

      public boolean shouldExecute() {
         if (!this.panda.isBurning()) {
            return false;
         } else {
            BlockPos lvt_1_1_ = this.getRandPos(this.creature.world, this.creature, 5, 4);
            if (lvt_1_1_ != null) {
               this.randPosX = (double)lvt_1_1_.getX();
               this.randPosY = (double)lvt_1_1_.getY();
               this.randPosZ = (double)lvt_1_1_.getZ();
               return true;
            } else {
               return this.findRandomPosition();
            }
         }
      }

      public boolean shouldContinueExecuting() {
         if (this.panda.func_213556_dX()) {
            this.panda.getNavigator().clearPath();
            return false;
         } else {
            return super.shouldContinueExecuting();
         }
      }
   }

   static class RevengeGoal extends HurtByTargetGoal {
      private final PandaEntity panda;

      public RevengeGoal(PandaEntity p_i51462_1_, Class<?>... p_i51462_2_) {
         super(p_i51462_1_, p_i51462_2_);
         this.panda = p_i51462_1_;
      }

      public boolean shouldContinueExecuting() {
         if (!this.panda.field_213598_bH && !this.panda.field_213599_bI) {
            return super.shouldContinueExecuting();
         } else {
            this.panda.setAttackTarget((LivingEntity)null);
            return false;
         }
      }

      protected void setAttackTarget(MobEntity p_220793_1_, LivingEntity p_220793_2_) {
         if (p_220793_1_ instanceof PandaEntity && ((PandaEntity)p_220793_1_).isAggressive()) {
            p_220793_1_.setAttackTarget(p_220793_2_);
         }

      }
   }

   static class LieBackGoal extends Goal {
      private final PandaEntity panda;
      private int field_220829_b;

      public LieBackGoal(PandaEntity p_i51460_1_) {
         this.panda = p_i51460_1_;
      }

      public boolean shouldExecute() {
         return this.field_220829_b < this.panda.ticksExisted && this.panda.isLazy() && this.panda.func_213537_eq() && this.panda.rand.nextInt(400) == 1;
      }

      public boolean shouldContinueExecuting() {
         if (!this.panda.isInWater() && (this.panda.isLazy() || this.panda.rand.nextInt(600) != 1)) {
            return this.panda.rand.nextInt(2000) != 1;
         } else {
            return false;
         }
      }

      public void startExecuting() {
         this.panda.func_213542_s(true);
         this.field_220829_b = 0;
      }

      public void resetTask() {
         this.panda.func_213542_s(false);
         this.field_220829_b = this.panda.ticksExisted + 200;
      }
   }

   class SitGoal extends Goal {
      private int field_220832_b;

      public SitGoal() {
         this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
      }

      public boolean shouldExecute() {
         if (this.field_220832_b <= PandaEntity.this.ticksExisted && !PandaEntity.this.isChild() && !PandaEntity.this.isInWater() && PandaEntity.this.func_213537_eq() && PandaEntity.this.func_213544_dV() <= 0) {
            List<ItemEntity> lvt_1_1_ = PandaEntity.this.world.getEntitiesWithinAABB(ItemEntity.class, PandaEntity.this.getBoundingBox().grow(6.0D, 6.0D, 6.0D), PandaEntity.field_213607_bQ);
            return !lvt_1_1_.isEmpty() || !PandaEntity.this.getItemStackFromSlot(EquipmentSlotType.MAINHAND).isEmpty();
         } else {
            return false;
         }
      }

      public boolean shouldContinueExecuting() {
         if (!PandaEntity.this.isInWater() && (PandaEntity.this.isLazy() || PandaEntity.this.rand.nextInt(600) != 1)) {
            return PandaEntity.this.rand.nextInt(2000) != 1;
         } else {
            return false;
         }
      }

      public void tick() {
         if (!PandaEntity.this.func_213556_dX() && !PandaEntity.this.getItemStackFromSlot(EquipmentSlotType.MAINHAND).isEmpty()) {
            PandaEntity.this.func_213586_eB();
         }

      }

      public void startExecuting() {
         List<ItemEntity> lvt_1_1_ = PandaEntity.this.world.getEntitiesWithinAABB(ItemEntity.class, PandaEntity.this.getBoundingBox().grow(8.0D, 8.0D, 8.0D), PandaEntity.field_213607_bQ);
         if (!lvt_1_1_.isEmpty() && PandaEntity.this.getItemStackFromSlot(EquipmentSlotType.MAINHAND).isEmpty()) {
            PandaEntity.this.getNavigator().tryMoveToEntityLiving((Entity)lvt_1_1_.get(0), 1.2000000476837158D);
         } else if (!PandaEntity.this.getItemStackFromSlot(EquipmentSlotType.MAINHAND).isEmpty()) {
            PandaEntity.this.func_213586_eB();
         }

         this.field_220832_b = 0;
      }

      public void resetTask() {
         ItemStack lvt_1_1_ = PandaEntity.this.getItemStackFromSlot(EquipmentSlotType.MAINHAND);
         if (!lvt_1_1_.isEmpty()) {
            PandaEntity.this.entityDropItem(lvt_1_1_);
            PandaEntity.this.setItemStackToSlot(EquipmentSlotType.MAINHAND, ItemStack.EMPTY);
            int lvt_2_1_ = PandaEntity.this.isLazy() ? PandaEntity.this.rand.nextInt(50) + 10 : PandaEntity.this.rand.nextInt(150) + 10;
            this.field_220832_b = PandaEntity.this.ticksExisted + lvt_2_1_ * 20;
         }

         PandaEntity.this.func_213553_r(false);
      }
   }

   static class AvoidGoal<T extends LivingEntity> extends AvoidEntityGoal<T> {
      private final PandaEntity field_220875_i;

      public AvoidGoal(PandaEntity p_i51466_1_, Class<T> p_i51466_2_, float p_i51466_3_, double p_i51466_4_, double p_i51466_6_) {
         Predicate var10006 = EntityPredicates.NOT_SPECTATING;
         super(p_i51466_1_, p_i51466_2_, p_i51466_3_, p_i51466_4_, p_i51466_6_, var10006::test);
         this.field_220875_i = p_i51466_1_;
      }

      public boolean shouldExecute() {
         return this.field_220875_i.isWorried() && this.field_220875_i.func_213537_eq() && super.shouldExecute();
      }
   }

   class MateGoal extends BreedGoal {
      private final PandaEntity panda;
      private int field_220694_f;

      public MateGoal(PandaEntity p_i229957_2_, double p_i229957_3_) {
         super(p_i229957_2_, p_i229957_3_);
         this.panda = p_i229957_2_;
      }

      public boolean shouldExecute() {
         if (super.shouldExecute() && this.panda.func_213544_dV() == 0) {
            if (!this.func_220691_h()) {
               if (this.field_220694_f <= this.panda.ticksExisted) {
                  this.panda.func_213588_r(32);
                  this.field_220694_f = this.panda.ticksExisted + 600;
                  if (this.panda.isServerWorld()) {
                     PlayerEntity lvt_1_1_ = this.world.getClosestPlayer(PandaEntity.field_229963_bD_, this.panda);
                     this.panda.field_229964_bN_.func_229975_a_(lvt_1_1_);
                  }
               }

               return false;
            } else {
               return true;
            }
         } else {
            return false;
         }
      }

      private boolean func_220691_h() {
         BlockPos lvt_1_1_ = new BlockPos(this.panda);
         BlockPos.Mutable lvt_2_1_ = new BlockPos.Mutable();

         for(int lvt_3_1_ = 0; lvt_3_1_ < 3; ++lvt_3_1_) {
            for(int lvt_4_1_ = 0; lvt_4_1_ < 8; ++lvt_4_1_) {
               for(int lvt_5_1_ = 0; lvt_5_1_ <= lvt_4_1_; lvt_5_1_ = lvt_5_1_ > 0 ? -lvt_5_1_ : 1 - lvt_5_1_) {
                  for(int lvt_6_1_ = lvt_5_1_ < lvt_4_1_ && lvt_5_1_ > -lvt_4_1_ ? lvt_4_1_ : 0; lvt_6_1_ <= lvt_4_1_; lvt_6_1_ = lvt_6_1_ > 0 ? -lvt_6_1_ : 1 - lvt_6_1_) {
                     lvt_2_1_.setPos((Vec3i)lvt_1_1_).move(lvt_5_1_, lvt_3_1_, lvt_6_1_);
                     if (this.world.getBlockState(lvt_2_1_).getBlock() == Blocks.BAMBOO) {
                        return true;
                     }
                  }
               }
            }
         }

         return false;
      }
   }

   static class ChildPlayGoal extends Goal {
      private final PandaEntity panda;

      public ChildPlayGoal(PandaEntity p_i51448_1_) {
         this.panda = p_i51448_1_;
      }

      public boolean shouldExecute() {
         if (this.panda.isChild() && this.panda.func_213537_eq()) {
            if (this.panda.isWeak() && this.panda.rand.nextInt(500) == 1) {
               return true;
            } else {
               return this.panda.rand.nextInt(6000) == 1;
            }
         } else {
            return false;
         }
      }

      public boolean shouldContinueExecuting() {
         return false;
      }

      public void startExecuting() {
         this.panda.func_213581_u(true);
      }
   }

   static class RollGoal extends Goal {
      private final PandaEntity panda;

      public RollGoal(PandaEntity p_i51452_1_) {
         this.panda = p_i51452_1_;
         this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK, Goal.Flag.JUMP));
      }

      public boolean shouldExecute() {
         if ((this.panda.isChild() || this.panda.func_213557_el()) && this.panda.onGround) {
            if (!this.panda.func_213537_eq()) {
               return false;
            } else {
               float lvt_1_1_ = this.panda.rotationYaw * 0.017453292F;
               int lvt_2_1_ = 0;
               int lvt_3_1_ = 0;
               float lvt_4_1_ = -MathHelper.sin(lvt_1_1_);
               float lvt_5_1_ = MathHelper.cos(lvt_1_1_);
               if ((double)Math.abs(lvt_4_1_) > 0.5D) {
                  lvt_2_1_ = (int)((float)lvt_2_1_ + lvt_4_1_ / Math.abs(lvt_4_1_));
               }

               if ((double)Math.abs(lvt_5_1_) > 0.5D) {
                  lvt_3_1_ = (int)((float)lvt_3_1_ + lvt_5_1_ / Math.abs(lvt_5_1_));
               }

               if (this.panda.world.getBlockState((new BlockPos(this.panda)).add(lvt_2_1_, -1, lvt_3_1_)).isAir()) {
                  return true;
               } else if (this.panda.func_213557_el() && this.panda.rand.nextInt(60) == 1) {
                  return true;
               } else {
                  return this.panda.rand.nextInt(500) == 1;
               }
            }
         } else {
            return false;
         }
      }

      public boolean shouldContinueExecuting() {
         return false;
      }

      public void startExecuting() {
         this.panda.func_213576_v(true);
      }

      public boolean isPreemptible() {
         return false;
      }
   }

   static class WatchGoal extends LookAtGoal {
      private final PandaEntity field_220718_f;

      public WatchGoal(PandaEntity p_i51458_1_, Class<? extends LivingEntity> p_i51458_2_, float p_i51458_3_) {
         super(p_i51458_1_, p_i51458_2_, p_i51458_3_);
         this.field_220718_f = p_i51458_1_;
      }

      public void func_229975_a_(LivingEntity p_229975_1_) {
         this.closestEntity = p_229975_1_;
      }

      public boolean shouldContinueExecuting() {
         return this.closestEntity != null && super.shouldContinueExecuting();
      }

      public boolean shouldExecute() {
         if (this.entity.getRNG().nextFloat() >= this.chance) {
            return false;
         } else {
            if (this.closestEntity == null) {
               if (this.watchedClass == PlayerEntity.class) {
                  this.closestEntity = this.entity.world.getClosestPlayer(this.field_220716_e, this.entity, this.entity.func_226277_ct_(), this.entity.func_226280_cw_(), this.entity.func_226281_cx_());
               } else {
                  this.closestEntity = this.entity.world.func_225318_b(this.watchedClass, this.field_220716_e, this.entity, this.entity.func_226277_ct_(), this.entity.func_226280_cw_(), this.entity.func_226281_cx_(), this.entity.getBoundingBox().grow((double)this.maxDistance, 3.0D, (double)this.maxDistance));
               }
            }

            return this.field_220718_f.func_213537_eq() && this.closestEntity != null;
         }
      }

      public void tick() {
         if (this.closestEntity != null) {
            super.tick();
         }

      }
   }

   static class AttackGoal extends MeleeAttackGoal {
      private final PandaEntity field_220722_d;

      public AttackGoal(PandaEntity p_i51467_1_, double p_i51467_2_, boolean p_i51467_4_) {
         super(p_i51467_1_, p_i51467_2_, p_i51467_4_);
         this.field_220722_d = p_i51467_1_;
      }

      public boolean shouldExecute() {
         return this.field_220722_d.func_213537_eq() && super.shouldExecute();
      }
   }

   static class MoveHelperController extends MovementController {
      private final PandaEntity panda;

      public MoveHelperController(PandaEntity p_i51456_1_) {
         super(p_i51456_1_);
         this.panda = p_i51456_1_;
      }

      public void tick() {
         if (this.panda.func_213537_eq()) {
            super.tick();
         }
      }
   }

   public static enum Type {
      NORMAL(0, "normal", false),
      LAZY(1, "lazy", false),
      WORRIED(2, "worried", false),
      PLAYFUL(3, "playful", false),
      BROWN(4, "brown", true),
      WEAK(5, "weak", true),
      AGGRESSIVE(6, "aggressive", false);

      private static final PandaEntity.Type[] field_221109_h = (PandaEntity.Type[])Arrays.stream(values()).sorted(Comparator.comparingInt(PandaEntity.Type::getIndex)).toArray((p_221102_0_) -> {
         return new PandaEntity.Type[p_221102_0_];
      });
      private final int index;
      private final String name;
      private final boolean field_221112_k;

      private Type(int p_i51468_3_, String p_i51468_4_, boolean p_i51468_5_) {
         this.index = p_i51468_3_;
         this.name = p_i51468_4_;
         this.field_221112_k = p_i51468_5_;
      }

      public int getIndex() {
         return this.index;
      }

      public String getName() {
         return this.name;
      }

      public boolean func_221107_c() {
         return this.field_221112_k;
      }

      private static PandaEntity.Type func_221101_b(PandaEntity.Type p_221101_0_, PandaEntity.Type p_221101_1_) {
         if (p_221101_0_.func_221107_c()) {
            return p_221101_0_ == p_221101_1_ ? p_221101_0_ : NORMAL;
         } else {
            return p_221101_0_;
         }
      }

      public static PandaEntity.Type byIndex(int p_221105_0_) {
         if (p_221105_0_ < 0 || p_221105_0_ >= field_221109_h.length) {
            p_221105_0_ = 0;
         }

         return field_221109_h[p_221105_0_];
      }

      public static PandaEntity.Type byName(String p_221108_0_) {
         PandaEntity.Type[] var1 = values();
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            PandaEntity.Type lvt_4_1_ = var1[var3];
            if (lvt_4_1_.name.equals(p_221108_0_)) {
               return lvt_4_1_;
            }
         }

         return NORMAL;
      }

      public static PandaEntity.Type getRandomType(Random p_221104_0_) {
         int lvt_1_1_ = p_221104_0_.nextInt(16);
         if (lvt_1_1_ == 0) {
            return LAZY;
         } else if (lvt_1_1_ == 1) {
            return WORRIED;
         } else if (lvt_1_1_ == 2) {
            return PLAYFUL;
         } else if (lvt_1_1_ == 4) {
            return AGGRESSIVE;
         } else if (lvt_1_1_ < 9) {
            return WEAK;
         } else {
            return lvt_1_1_ < 11 ? BROWN : NORMAL;
         }
      }
   }
}
