package net.minecraft.entity;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.block.material.PushReaction;
import net.minecraft.command.arguments.ParticleArgument;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AreaEffectCloudEntity extends Entity {
   private static final Logger PRIVATE_LOGGER = LogManager.getLogger();
   private static final DataParameter<Float> RADIUS;
   private static final DataParameter<Integer> COLOR;
   private static final DataParameter<Boolean> IGNORE_RADIUS;
   private static final DataParameter<IParticleData> PARTICLE;
   private Potion potion;
   private final List<EffectInstance> effects;
   private final Map<Entity, Integer> reapplicationDelayMap;
   private int duration;
   private int waitTime;
   private int reapplicationDelay;
   private boolean colorSet;
   private int durationOnUse;
   private float radiusOnUse;
   private float radiusPerTick;
   private LivingEntity owner;
   private UUID ownerUniqueId;

   public AreaEffectCloudEntity(EntityType<? extends AreaEffectCloudEntity> p_i50389_1_, World p_i50389_2_) {
      super(p_i50389_1_, p_i50389_2_);
      this.potion = Potions.EMPTY;
      this.effects = Lists.newArrayList();
      this.reapplicationDelayMap = Maps.newHashMap();
      this.duration = 600;
      this.waitTime = 20;
      this.reapplicationDelay = 20;
      this.noClip = true;
      this.setRadius(3.0F);
   }

   public AreaEffectCloudEntity(World p_i46810_1_, double p_i46810_2_, double p_i46810_4_, double p_i46810_6_) {
      this(EntityType.AREA_EFFECT_CLOUD, p_i46810_1_);
      this.setPosition(p_i46810_2_, p_i46810_4_, p_i46810_6_);
   }

   protected void registerData() {
      this.getDataManager().register(COLOR, 0);
      this.getDataManager().register(RADIUS, 0.5F);
      this.getDataManager().register(IGNORE_RADIUS, false);
      this.getDataManager().register(PARTICLE, ParticleTypes.ENTITY_EFFECT);
   }

   public void setRadius(float p_184483_1_) {
      if (!this.world.isRemote) {
         this.getDataManager().set(RADIUS, p_184483_1_);
      }

   }

   public void recalculateSize() {
      double lvt_1_1_ = this.func_226277_ct_();
      double lvt_3_1_ = this.func_226278_cu_();
      double lvt_5_1_ = this.func_226281_cx_();
      super.recalculateSize();
      this.setPosition(lvt_1_1_, lvt_3_1_, lvt_5_1_);
   }

   public float getRadius() {
      return (Float)this.getDataManager().get(RADIUS);
   }

   public void setPotion(Potion p_184484_1_) {
      this.potion = p_184484_1_;
      if (!this.colorSet) {
         this.updateFixedColor();
      }

   }

   private void updateFixedColor() {
      if (this.potion == Potions.EMPTY && this.effects.isEmpty()) {
         this.getDataManager().set(COLOR, 0);
      } else {
         this.getDataManager().set(COLOR, PotionUtils.getPotionColorFromEffectList(PotionUtils.mergeEffects(this.potion, this.effects)));
      }

   }

   public void addEffect(EffectInstance p_184496_1_) {
      this.effects.add(p_184496_1_);
      if (!this.colorSet) {
         this.updateFixedColor();
      }

   }

   public int getColor() {
      return (Integer)this.getDataManager().get(COLOR);
   }

   public void setColor(int p_184482_1_) {
      this.colorSet = true;
      this.getDataManager().set(COLOR, p_184482_1_);
   }

   public IParticleData getParticleData() {
      return (IParticleData)this.getDataManager().get(PARTICLE);
   }

   public void setParticleData(IParticleData p_195059_1_) {
      this.getDataManager().set(PARTICLE, p_195059_1_);
   }

   protected void setIgnoreRadius(boolean p_184488_1_) {
      this.getDataManager().set(IGNORE_RADIUS, p_184488_1_);
   }

   public boolean shouldIgnoreRadius() {
      return (Boolean)this.getDataManager().get(IGNORE_RADIUS);
   }

   public int getDuration() {
      return this.duration;
   }

   public void setDuration(int p_184486_1_) {
      this.duration = p_184486_1_;
   }

   public void tick() {
      super.tick();
      boolean lvt_1_1_ = this.shouldIgnoreRadius();
      float lvt_2_1_ = this.getRadius();
      if (this.world.isRemote) {
         IParticleData lvt_3_1_ = this.getParticleData();
         float lvt_6_1_;
         float lvt_7_1_;
         float lvt_8_1_;
         int lvt_10_1_;
         int lvt_11_1_;
         int lvt_12_1_;
         if (lvt_1_1_) {
            if (this.rand.nextBoolean()) {
               for(int lvt_4_1_ = 0; lvt_4_1_ < 2; ++lvt_4_1_) {
                  float lvt_5_1_ = this.rand.nextFloat() * 6.2831855F;
                  lvt_6_1_ = MathHelper.sqrt(this.rand.nextFloat()) * 0.2F;
                  lvt_7_1_ = MathHelper.cos(lvt_5_1_) * lvt_6_1_;
                  lvt_8_1_ = MathHelper.sin(lvt_5_1_) * lvt_6_1_;
                  if (lvt_3_1_.getType() == ParticleTypes.ENTITY_EFFECT) {
                     int lvt_9_1_ = this.rand.nextBoolean() ? 16777215 : this.getColor();
                     lvt_10_1_ = lvt_9_1_ >> 16 & 255;
                     lvt_11_1_ = lvt_9_1_ >> 8 & 255;
                     lvt_12_1_ = lvt_9_1_ & 255;
                     this.world.addOptionalParticle(lvt_3_1_, this.func_226277_ct_() + (double)lvt_7_1_, this.func_226278_cu_(), this.func_226281_cx_() + (double)lvt_8_1_, (double)((float)lvt_10_1_ / 255.0F), (double)((float)lvt_11_1_ / 255.0F), (double)((float)lvt_12_1_ / 255.0F));
                  } else {
                     this.world.addOptionalParticle(lvt_3_1_, this.func_226277_ct_() + (double)lvt_7_1_, this.func_226278_cu_(), this.func_226281_cx_() + (double)lvt_8_1_, 0.0D, 0.0D, 0.0D);
                  }
               }
            }
         } else {
            float lvt_4_2_ = 3.1415927F * lvt_2_1_ * lvt_2_1_;

            for(int lvt_5_2_ = 0; (float)lvt_5_2_ < lvt_4_2_; ++lvt_5_2_) {
               lvt_6_1_ = this.rand.nextFloat() * 6.2831855F;
               lvt_7_1_ = MathHelper.sqrt(this.rand.nextFloat()) * lvt_2_1_;
               lvt_8_1_ = MathHelper.cos(lvt_6_1_) * lvt_7_1_;
               float lvt_9_2_ = MathHelper.sin(lvt_6_1_) * lvt_7_1_;
               if (lvt_3_1_.getType() == ParticleTypes.ENTITY_EFFECT) {
                  lvt_10_1_ = this.getColor();
                  lvt_11_1_ = lvt_10_1_ >> 16 & 255;
                  lvt_12_1_ = lvt_10_1_ >> 8 & 255;
                  int lvt_13_1_ = lvt_10_1_ & 255;
                  this.world.addOptionalParticle(lvt_3_1_, this.func_226277_ct_() + (double)lvt_8_1_, this.func_226278_cu_(), this.func_226281_cx_() + (double)lvt_9_2_, (double)((float)lvt_11_1_ / 255.0F), (double)((float)lvt_12_1_ / 255.0F), (double)((float)lvt_13_1_ / 255.0F));
               } else {
                  this.world.addOptionalParticle(lvt_3_1_, this.func_226277_ct_() + (double)lvt_8_1_, this.func_226278_cu_(), this.func_226281_cx_() + (double)lvt_9_2_, (0.5D - this.rand.nextDouble()) * 0.15D, 0.009999999776482582D, (0.5D - this.rand.nextDouble()) * 0.15D);
               }
            }
         }
      } else {
         if (this.ticksExisted >= this.waitTime + this.duration) {
            this.remove();
            return;
         }

         boolean lvt_3_2_ = this.ticksExisted < this.waitTime;
         if (lvt_1_1_ != lvt_3_2_) {
            this.setIgnoreRadius(lvt_3_2_);
         }

         if (lvt_3_2_) {
            return;
         }

         if (this.radiusPerTick != 0.0F) {
            lvt_2_1_ += this.radiusPerTick;
            if (lvt_2_1_ < 0.5F) {
               this.remove();
               return;
            }

            this.setRadius(lvt_2_1_);
         }

         if (this.ticksExisted % 5 == 0) {
            Iterator lvt_4_3_ = this.reapplicationDelayMap.entrySet().iterator();

            while(lvt_4_3_.hasNext()) {
               Entry<Entity, Integer> lvt_5_3_ = (Entry)lvt_4_3_.next();
               if (this.ticksExisted >= (Integer)lvt_5_3_.getValue()) {
                  lvt_4_3_.remove();
               }
            }

            List<EffectInstance> lvt_4_4_ = Lists.newArrayList();
            Iterator var22 = this.potion.getEffects().iterator();

            while(var22.hasNext()) {
               EffectInstance lvt_6_3_ = (EffectInstance)var22.next();
               lvt_4_4_.add(new EffectInstance(lvt_6_3_.getPotion(), lvt_6_3_.getDuration() / 4, lvt_6_3_.getAmplifier(), lvt_6_3_.isAmbient(), lvt_6_3_.doesShowParticles()));
            }

            lvt_4_4_.addAll(this.effects);
            if (lvt_4_4_.isEmpty()) {
               this.reapplicationDelayMap.clear();
            } else {
               List<LivingEntity> lvt_5_4_ = this.world.getEntitiesWithinAABB(LivingEntity.class, this.getBoundingBox());
               if (!lvt_5_4_.isEmpty()) {
                  Iterator var25 = lvt_5_4_.iterator();

                  while(true) {
                     LivingEntity lvt_7_3_;
                     double lvt_12_3_;
                     do {
                        do {
                           do {
                              if (!var25.hasNext()) {
                                 return;
                              }

                              lvt_7_3_ = (LivingEntity)var25.next();
                           } while(this.reapplicationDelayMap.containsKey(lvt_7_3_));
                        } while(!lvt_7_3_.canBeHitWithPotion());

                        double lvt_8_3_ = lvt_7_3_.func_226277_ct_() - this.func_226277_ct_();
                        double lvt_10_3_ = lvt_7_3_.func_226281_cx_() - this.func_226281_cx_();
                        lvt_12_3_ = lvt_8_3_ * lvt_8_3_ + lvt_10_3_ * lvt_10_3_;
                     } while(lvt_12_3_ > (double)(lvt_2_1_ * lvt_2_1_));

                     this.reapplicationDelayMap.put(lvt_7_3_, this.ticksExisted + this.reapplicationDelay);
                     Iterator var14 = lvt_4_4_.iterator();

                     while(var14.hasNext()) {
                        EffectInstance lvt_15_1_ = (EffectInstance)var14.next();
                        if (lvt_15_1_.getPotion().isInstant()) {
                           lvt_15_1_.getPotion().affectEntity(this, this.getOwner(), lvt_7_3_, lvt_15_1_.getAmplifier(), 0.5D);
                        } else {
                           lvt_7_3_.addPotionEffect(new EffectInstance(lvt_15_1_));
                        }
                     }

                     if (this.radiusOnUse != 0.0F) {
                        lvt_2_1_ += this.radiusOnUse;
                        if (lvt_2_1_ < 0.5F) {
                           this.remove();
                           return;
                        }

                        this.setRadius(lvt_2_1_);
                     }

                     if (this.durationOnUse != 0) {
                        this.duration += this.durationOnUse;
                        if (this.duration <= 0) {
                           this.remove();
                           return;
                        }
                     }
                  }
               }
            }
         }
      }

   }

   public void setRadiusOnUse(float p_184495_1_) {
      this.radiusOnUse = p_184495_1_;
   }

   public void setRadiusPerTick(float p_184487_1_) {
      this.radiusPerTick = p_184487_1_;
   }

   public void setWaitTime(int p_184485_1_) {
      this.waitTime = p_184485_1_;
   }

   public void setOwner(@Nullable LivingEntity p_184481_1_) {
      this.owner = p_184481_1_;
      this.ownerUniqueId = p_184481_1_ == null ? null : p_184481_1_.getUniqueID();
   }

   @Nullable
   public LivingEntity getOwner() {
      if (this.owner == null && this.ownerUniqueId != null && this.world instanceof ServerWorld) {
         Entity lvt_1_1_ = ((ServerWorld)this.world).getEntityByUuid(this.ownerUniqueId);
         if (lvt_1_1_ instanceof LivingEntity) {
            this.owner = (LivingEntity)lvt_1_1_;
         }
      }

      return this.owner;
   }

   protected void readAdditional(CompoundNBT p_70037_1_) {
      this.ticksExisted = p_70037_1_.getInt("Age");
      this.duration = p_70037_1_.getInt("Duration");
      this.waitTime = p_70037_1_.getInt("WaitTime");
      this.reapplicationDelay = p_70037_1_.getInt("ReapplicationDelay");
      this.durationOnUse = p_70037_1_.getInt("DurationOnUse");
      this.radiusOnUse = p_70037_1_.getFloat("RadiusOnUse");
      this.radiusPerTick = p_70037_1_.getFloat("RadiusPerTick");
      this.setRadius(p_70037_1_.getFloat("Radius"));
      this.ownerUniqueId = p_70037_1_.getUniqueId("OwnerUUID");
      if (p_70037_1_.contains("Particle", 8)) {
         try {
            this.setParticleData(ParticleArgument.parseParticle(new StringReader(p_70037_1_.getString("Particle"))));
         } catch (CommandSyntaxException var5) {
            PRIVATE_LOGGER.warn("Couldn't load custom particle {}", p_70037_1_.getString("Particle"), var5);
         }
      }

      if (p_70037_1_.contains("Color", 99)) {
         this.setColor(p_70037_1_.getInt("Color"));
      }

      if (p_70037_1_.contains("Potion", 8)) {
         this.setPotion(PotionUtils.getPotionTypeFromNBT(p_70037_1_));
      }

      if (p_70037_1_.contains("Effects", 9)) {
         ListNBT lvt_2_2_ = p_70037_1_.getList("Effects", 10);
         this.effects.clear();

         for(int lvt_3_1_ = 0; lvt_3_1_ < lvt_2_2_.size(); ++lvt_3_1_) {
            EffectInstance lvt_4_1_ = EffectInstance.read(lvt_2_2_.getCompound(lvt_3_1_));
            if (lvt_4_1_ != null) {
               this.addEffect(lvt_4_1_);
            }
         }
      }

   }

   protected void writeAdditional(CompoundNBT p_213281_1_) {
      p_213281_1_.putInt("Age", this.ticksExisted);
      p_213281_1_.putInt("Duration", this.duration);
      p_213281_1_.putInt("WaitTime", this.waitTime);
      p_213281_1_.putInt("ReapplicationDelay", this.reapplicationDelay);
      p_213281_1_.putInt("DurationOnUse", this.durationOnUse);
      p_213281_1_.putFloat("RadiusOnUse", this.radiusOnUse);
      p_213281_1_.putFloat("RadiusPerTick", this.radiusPerTick);
      p_213281_1_.putFloat("Radius", this.getRadius());
      p_213281_1_.putString("Particle", this.getParticleData().getParameters());
      if (this.ownerUniqueId != null) {
         p_213281_1_.putUniqueId("OwnerUUID", this.ownerUniqueId);
      }

      if (this.colorSet) {
         p_213281_1_.putInt("Color", this.getColor());
      }

      if (this.potion != Potions.EMPTY && this.potion != null) {
         p_213281_1_.putString("Potion", Registry.POTION.getKey(this.potion).toString());
      }

      if (!this.effects.isEmpty()) {
         ListNBT lvt_2_1_ = new ListNBT();
         Iterator var3 = this.effects.iterator();

         while(var3.hasNext()) {
            EffectInstance lvt_4_1_ = (EffectInstance)var3.next();
            lvt_2_1_.add(lvt_4_1_.write(new CompoundNBT()));
         }

         p_213281_1_.put("Effects", lvt_2_1_);
      }

   }

   public void notifyDataManagerChange(DataParameter<?> p_184206_1_) {
      if (RADIUS.equals(p_184206_1_)) {
         this.recalculateSize();
      }

      super.notifyDataManagerChange(p_184206_1_);
   }

   public PushReaction getPushReaction() {
      return PushReaction.IGNORE;
   }

   public IPacket<?> createSpawnPacket() {
      return new SSpawnObjectPacket(this);
   }

   public EntitySize getSize(Pose p_213305_1_) {
      return EntitySize.flexible(this.getRadius() * 2.0F, 0.5F);
   }

   static {
      RADIUS = EntityDataManager.createKey(AreaEffectCloudEntity.class, DataSerializers.FLOAT);
      COLOR = EntityDataManager.createKey(AreaEffectCloudEntity.class, DataSerializers.VARINT);
      IGNORE_RADIUS = EntityDataManager.createKey(AreaEffectCloudEntity.class, DataSerializers.BOOLEAN);
      PARTICLE = EntityDataManager.createKey(AreaEffectCloudEntity.class, DataSerializers.PARTICLE_DATA);
   }
}
