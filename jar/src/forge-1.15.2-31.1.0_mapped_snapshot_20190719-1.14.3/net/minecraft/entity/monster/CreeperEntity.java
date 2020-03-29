package net.minecraft.entity.monster;

import java.util.Collection;
import java.util.Iterator;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IChargeableMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.entity.ai.goal.CreeperSwellGoal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.OcelotEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.ForgeEventFactory;

@OnlyIn(
   value = Dist.CLIENT,
   _interface = IChargeableMob.class
)
public class CreeperEntity extends MonsterEntity implements IChargeableMob {
   private static final DataParameter<Integer> STATE;
   private static final DataParameter<Boolean> POWERED;
   private static final DataParameter<Boolean> IGNITED;
   private int lastActiveTime;
   private int timeSinceIgnited;
   private int fuseTime = 30;
   private int explosionRadius = 3;
   private int droppedSkulls;

   public CreeperEntity(EntityType<? extends CreeperEntity> p_i50213_1_, World p_i50213_2_) {
      super(p_i50213_1_, p_i50213_2_);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(1, new SwimGoal(this));
      this.goalSelector.addGoal(2, new CreeperSwellGoal(this));
      this.goalSelector.addGoal(3, new AvoidEntityGoal(this, OcelotEntity.class, 6.0F, 1.0D, 1.2D));
      this.goalSelector.addGoal(3, new AvoidEntityGoal(this, CatEntity.class, 6.0F, 1.0D, 1.2D));
      this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0D, false));
      this.goalSelector.addGoal(5, new WaterAvoidingRandomWalkingGoal(this, 0.8D));
      this.goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 8.0F));
      this.goalSelector.addGoal(6, new LookRandomlyGoal(this));
      this.targetSelector.addGoal(1, new NearestAttackableTargetGoal(this, PlayerEntity.class, true));
      this.targetSelector.addGoal(2, new HurtByTargetGoal(this, new Class[0]));
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
   }

   public int getMaxFallHeight() {
      return this.getAttackTarget() == null ? 3 : 3 + (int)(this.getHealth() - 1.0F);
   }

   public boolean func_225503_b_(float p_225503_1_, float p_225503_2_) {
      boolean flag = super.func_225503_b_(p_225503_1_, p_225503_2_);
      this.timeSinceIgnited = (int)((float)this.timeSinceIgnited + p_225503_1_ * 1.5F);
      if (this.timeSinceIgnited > this.fuseTime - 5) {
         this.timeSinceIgnited = this.fuseTime - 5;
      }

      return flag;
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(STATE, -1);
      this.dataManager.register(POWERED, false);
      this.dataManager.register(IGNITED, false);
   }

   public void writeAdditional(CompoundNBT p_213281_1_) {
      super.writeAdditional(p_213281_1_);
      if ((Boolean)this.dataManager.get(POWERED)) {
         p_213281_1_.putBoolean("powered", true);
      }

      p_213281_1_.putShort("Fuse", (short)this.fuseTime);
      p_213281_1_.putByte("ExplosionRadius", (byte)this.explosionRadius);
      p_213281_1_.putBoolean("ignited", this.hasIgnited());
   }

   public void readAdditional(CompoundNBT p_70037_1_) {
      super.readAdditional(p_70037_1_);
      this.dataManager.set(POWERED, p_70037_1_.getBoolean("powered"));
      if (p_70037_1_.contains("Fuse", 99)) {
         this.fuseTime = p_70037_1_.getShort("Fuse");
      }

      if (p_70037_1_.contains("ExplosionRadius", 99)) {
         this.explosionRadius = p_70037_1_.getByte("ExplosionRadius");
      }

      if (p_70037_1_.getBoolean("ignited")) {
         this.ignite();
      }

   }

   public void tick() {
      if (this.isAlive()) {
         this.lastActiveTime = this.timeSinceIgnited;
         if (this.hasIgnited()) {
            this.setCreeperState(1);
         }

         int i = this.getCreeperState();
         if (i > 0 && this.timeSinceIgnited == 0) {
            this.playSound(SoundEvents.ENTITY_CREEPER_PRIMED, 1.0F, 0.5F);
         }

         this.timeSinceIgnited += i;
         if (this.timeSinceIgnited < 0) {
            this.timeSinceIgnited = 0;
         }

         if (this.timeSinceIgnited >= this.fuseTime) {
            this.timeSinceIgnited = this.fuseTime;
            this.explode();
         }
      }

      super.tick();
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.ENTITY_CREEPER_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_CREEPER_DEATH;
   }

   protected void dropSpecialItems(DamageSource p_213333_1_, int p_213333_2_, boolean p_213333_3_) {
      super.dropSpecialItems(p_213333_1_, p_213333_2_, p_213333_3_);
      Entity entity = p_213333_1_.getTrueSource();
      if (entity != this && entity instanceof CreeperEntity) {
         CreeperEntity creeperentity = (CreeperEntity)entity;
         if (creeperentity.ableToCauseSkullDrop()) {
            creeperentity.incrementDroppedSkulls();
            this.entityDropItem(Items.CREEPER_HEAD);
         }
      }

   }

   public boolean attackEntityAsMob(Entity p_70652_1_) {
      return true;
   }

   public boolean func_225509_J__() {
      return (Boolean)this.dataManager.get(POWERED);
   }

   @OnlyIn(Dist.CLIENT)
   public float getCreeperFlashIntensity(float p_70831_1_) {
      return MathHelper.lerp(p_70831_1_, (float)this.lastActiveTime, (float)this.timeSinceIgnited) / (float)(this.fuseTime - 2);
   }

   public int getCreeperState() {
      return (Integer)this.dataManager.get(STATE);
   }

   public void setCreeperState(int p_70829_1_) {
      this.dataManager.set(STATE, p_70829_1_);
   }

   public void onStruckByLightning(LightningBoltEntity p_70077_1_) {
      super.onStruckByLightning(p_70077_1_);
      this.dataManager.set(POWERED, true);
   }

   protected boolean processInteract(PlayerEntity p_184645_1_, Hand p_184645_2_) {
      ItemStack itemstack = p_184645_1_.getHeldItem(p_184645_2_);
      if (itemstack.getItem() == Items.FLINT_AND_STEEL) {
         this.world.playSound(p_184645_1_, this.func_226277_ct_(), this.func_226278_cu_(), this.func_226281_cx_(), SoundEvents.ITEM_FLINTANDSTEEL_USE, this.getSoundCategory(), 1.0F, this.rand.nextFloat() * 0.4F + 0.8F);
         if (!this.world.isRemote) {
            this.ignite();
            itemstack.damageItem(1, p_184645_1_, (p_lambda$processInteract$0_1_) -> {
               p_lambda$processInteract$0_1_.sendBreakAnimation(p_184645_2_);
            });
         }

         return true;
      } else {
         return super.processInteract(p_184645_1_, p_184645_2_);
      }
   }

   private void explode() {
      if (!this.world.isRemote) {
         Explosion.Mode explosion$mode = ForgeEventFactory.getMobGriefingEvent(this.world, this) ? Explosion.Mode.DESTROY : Explosion.Mode.NONE;
         float f = this.func_225509_J__() ? 2.0F : 1.0F;
         this.dead = true;
         this.world.createExplosion(this, this.func_226277_ct_(), this.func_226278_cu_(), this.func_226281_cx_(), (float)this.explosionRadius * f, explosion$mode);
         this.remove();
         this.spawnLingeringCloud();
      }

   }

   private void spawnLingeringCloud() {
      Collection<EffectInstance> collection = this.getActivePotionEffects();
      if (!collection.isEmpty()) {
         AreaEffectCloudEntity areaeffectcloudentity = new AreaEffectCloudEntity(this.world, this.func_226277_ct_(), this.func_226278_cu_(), this.func_226281_cx_());
         areaeffectcloudentity.setRadius(2.5F);
         areaeffectcloudentity.setRadiusOnUse(-0.5F);
         areaeffectcloudentity.setWaitTime(10);
         areaeffectcloudentity.setDuration(areaeffectcloudentity.getDuration() / 2);
         areaeffectcloudentity.setRadiusPerTick(-areaeffectcloudentity.getRadius() / (float)areaeffectcloudentity.getDuration());
         Iterator var3 = collection.iterator();

         while(var3.hasNext()) {
            EffectInstance effectinstance = (EffectInstance)var3.next();
            areaeffectcloudentity.addEffect(new EffectInstance(effectinstance));
         }

         this.world.addEntity(areaeffectcloudentity);
      }

   }

   public boolean hasIgnited() {
      return (Boolean)this.dataManager.get(IGNITED);
   }

   public void ignite() {
      this.dataManager.set(IGNITED, true);
   }

   public boolean ableToCauseSkullDrop() {
      return this.func_225509_J__() && this.droppedSkulls < 1;
   }

   public void incrementDroppedSkulls() {
      ++this.droppedSkulls;
   }

   static {
      STATE = EntityDataManager.createKey(CreeperEntity.class, DataSerializers.VARINT);
      POWERED = EntityDataManager.createKey(CreeperEntity.class, DataSerializers.BOOLEAN);
      IGNITED = EntityDataManager.createKey(CreeperEntity.class, DataSerializers.BOOLEAN);
   }
}
