package net.minecraft.entity.projectile;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.FishingRodItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameterSets;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTables;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemFishedEvent;

public class FishingBobberEntity extends Entity {
   private static final DataParameter<Integer> DATA_HOOKED_ENTITY;
   private boolean inGround;
   private int ticksInGround;
   private final PlayerEntity angler;
   private int ticksInAir;
   private int ticksCatchable;
   private int ticksCaughtDelay;
   private int ticksCatchableDelay;
   private float fishApproachAngle;
   public Entity caughtEntity;
   private FishingBobberEntity.State currentState;
   private final int luck;
   private final int lureSpeed;

   private FishingBobberEntity(World p_i50219_1_, PlayerEntity p_i50219_2_, int p_i50219_3_, int p_i50219_4_) {
      super(EntityType.FISHING_BOBBER, p_i50219_1_);
      this.currentState = FishingBobberEntity.State.FLYING;
      this.ignoreFrustumCheck = true;
      this.angler = p_i50219_2_;
      this.angler.fishingBobber = this;
      this.luck = Math.max(0, p_i50219_3_);
      this.lureSpeed = Math.max(0, p_i50219_4_);
   }

   @OnlyIn(Dist.CLIENT)
   public FishingBobberEntity(World p_i47290_1_, PlayerEntity p_i47290_2_, double p_i47290_3_, double p_i47290_5_, double p_i47290_7_) {
      this((World)p_i47290_1_, (PlayerEntity)p_i47290_2_, 0, 0);
      this.setPosition(p_i47290_3_, p_i47290_5_, p_i47290_7_);
      this.prevPosX = this.func_226277_ct_();
      this.prevPosY = this.func_226278_cu_();
      this.prevPosZ = this.func_226281_cx_();
   }

   public FishingBobberEntity(PlayerEntity p_i50220_1_, World p_i50220_2_, int p_i50220_3_, int p_i50220_4_) {
      this(p_i50220_2_, p_i50220_1_, p_i50220_3_, p_i50220_4_);
      float f = this.angler.rotationPitch;
      float f1 = this.angler.rotationYaw;
      float f2 = MathHelper.cos(-f1 * 0.017453292F - 3.1415927F);
      float f3 = MathHelper.sin(-f1 * 0.017453292F - 3.1415927F);
      float f4 = -MathHelper.cos(-f * 0.017453292F);
      float f5 = MathHelper.sin(-f * 0.017453292F);
      double d0 = this.angler.func_226277_ct_() - (double)f3 * 0.3D;
      double d1 = this.angler.func_226280_cw_();
      double d2 = this.angler.func_226281_cx_() - (double)f2 * 0.3D;
      this.setLocationAndAngles(d0, d1, d2, f1, f);
      Vec3d vec3d = new Vec3d((double)(-f3), (double)MathHelper.clamp(-(f5 / f4), -5.0F, 5.0F), (double)(-f2));
      double d3 = vec3d.length();
      vec3d = vec3d.mul(0.6D / d3 + 0.5D + this.rand.nextGaussian() * 0.0045D, 0.6D / d3 + 0.5D + this.rand.nextGaussian() * 0.0045D, 0.6D / d3 + 0.5D + this.rand.nextGaussian() * 0.0045D);
      this.setMotion(vec3d);
      this.rotationYaw = (float)(MathHelper.atan2(vec3d.x, vec3d.z) * 57.2957763671875D);
      this.rotationPitch = (float)(MathHelper.atan2(vec3d.y, (double)MathHelper.sqrt(func_213296_b(vec3d))) * 57.2957763671875D);
      this.prevRotationYaw = this.rotationYaw;
      this.prevRotationPitch = this.rotationPitch;
   }

   protected void registerData() {
      this.getDataManager().register(DATA_HOOKED_ENTITY, 0);
   }

   public void notifyDataManagerChange(DataParameter<?> p_184206_1_) {
      if (DATA_HOOKED_ENTITY.equals(p_184206_1_)) {
         int i = (Integer)this.getDataManager().get(DATA_HOOKED_ENTITY);
         this.caughtEntity = i > 0 ? this.world.getEntityByID(i - 1) : null;
      }

      super.notifyDataManagerChange(p_184206_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isInRangeToRenderDist(double p_70112_1_) {
      double d0 = 64.0D;
      return p_70112_1_ < 4096.0D;
   }

   @OnlyIn(Dist.CLIENT)
   public void setPositionAndRotationDirect(double p_180426_1_, double p_180426_3_, double p_180426_5_, float p_180426_7_, float p_180426_8_, int p_180426_9_, boolean p_180426_10_) {
   }

   public void tick() {
      super.tick();
      if (this.angler == null) {
         this.remove();
      } else if (this.world.isRemote || !this.shouldStopFishing()) {
         if (this.inGround) {
            ++this.ticksInGround;
            if (this.ticksInGround >= 1200) {
               this.remove();
               return;
            }
         }

         float f = 0.0F;
         BlockPos blockpos = new BlockPos(this);
         IFluidState ifluidstate = this.world.getFluidState(blockpos);
         if (ifluidstate.isTagged(FluidTags.WATER)) {
            f = ifluidstate.func_215679_a(this.world, blockpos);
         }

         if (this.currentState == FishingBobberEntity.State.FLYING) {
            if (this.caughtEntity != null) {
               this.setMotion(Vec3d.ZERO);
               this.currentState = FishingBobberEntity.State.HOOKED_IN_ENTITY;
               return;
            }

            if (f > 0.0F) {
               this.setMotion(this.getMotion().mul(0.3D, 0.2D, 0.3D));
               this.currentState = FishingBobberEntity.State.BOBBING;
               return;
            }

            if (!this.world.isRemote) {
               this.checkCollision();
            }

            if (!this.inGround && !this.onGround && !this.collidedHorizontally) {
               ++this.ticksInAir;
            } else {
               this.ticksInAir = 0;
               this.setMotion(Vec3d.ZERO);
            }
         } else {
            if (this.currentState == FishingBobberEntity.State.HOOKED_IN_ENTITY) {
               if (this.caughtEntity != null) {
                  if (this.caughtEntity.removed) {
                     this.caughtEntity = null;
                     this.currentState = FishingBobberEntity.State.FLYING;
                  } else {
                     this.setPosition(this.caughtEntity.func_226277_ct_(), this.caughtEntity.func_226283_e_(0.8D), this.caughtEntity.func_226281_cx_());
                  }
               }

               return;
            }

            if (this.currentState == FishingBobberEntity.State.BOBBING) {
               Vec3d vec3d = this.getMotion();
               double d0 = this.func_226278_cu_() + vec3d.y - (double)blockpos.getY() - (double)f;
               if (Math.abs(d0) < 0.01D) {
                  d0 += Math.signum(d0) * 0.1D;
               }

               this.setMotion(vec3d.x * 0.9D, vec3d.y - d0 * (double)this.rand.nextFloat() * 0.2D, vec3d.z * 0.9D);
               if (!this.world.isRemote && f > 0.0F) {
                  this.catchingFish(blockpos);
               }
            }
         }

         if (!ifluidstate.isTagged(FluidTags.WATER)) {
            this.setMotion(this.getMotion().add(0.0D, -0.03D, 0.0D));
         }

         this.move(MoverType.SELF, this.getMotion());
         this.updateRotation();
         double d1 = 0.92D;
         this.setMotion(this.getMotion().scale(0.92D));
         this.func_226264_Z_();
      }

   }

   private boolean shouldStopFishing() {
      ItemStack itemstack = this.angler.getHeldItemMainhand();
      ItemStack itemstack1 = this.angler.getHeldItemOffhand();
      boolean flag = itemstack.getItem() instanceof FishingRodItem;
      boolean flag1 = itemstack1.getItem() instanceof FishingRodItem;
      if (!this.angler.removed && this.angler.isAlive() && (flag || flag1) && this.getDistanceSq(this.angler) <= 1024.0D) {
         return false;
      } else {
         this.remove();
         return true;
      }
   }

   private void updateRotation() {
      Vec3d vec3d = this.getMotion();
      float f = MathHelper.sqrt(func_213296_b(vec3d));
      this.rotationYaw = (float)(MathHelper.atan2(vec3d.x, vec3d.z) * 57.2957763671875D);

      for(this.rotationPitch = (float)(MathHelper.atan2(vec3d.y, (double)f) * 57.2957763671875D); this.rotationPitch - this.prevRotationPitch < -180.0F; this.prevRotationPitch -= 360.0F) {
      }

      while(this.rotationPitch - this.prevRotationPitch >= 180.0F) {
         this.prevRotationPitch += 360.0F;
      }

      while(this.rotationYaw - this.prevRotationYaw < -180.0F) {
         this.prevRotationYaw -= 360.0F;
      }

      while(this.rotationYaw - this.prevRotationYaw >= 180.0F) {
         this.prevRotationYaw += 360.0F;
      }

      this.rotationPitch = MathHelper.lerp(0.2F, this.prevRotationPitch, this.rotationPitch);
      this.rotationYaw = MathHelper.lerp(0.2F, this.prevRotationYaw, this.rotationYaw);
   }

   private void checkCollision() {
      RayTraceResult raytraceresult = ProjectileHelper.func_221267_a(this, this.getBoundingBox().expand(this.getMotion()).grow(1.0D), (p_lambda$checkCollision$0_1_) -> {
         return !p_lambda$checkCollision$0_1_.isSpectator() && (p_lambda$checkCollision$0_1_.canBeCollidedWith() || p_lambda$checkCollision$0_1_ instanceof ItemEntity) && (p_lambda$checkCollision$0_1_ != this.angler || this.ticksInAir >= 5);
      }, RayTraceContext.BlockMode.COLLIDER, true);
      if (raytraceresult.getType() != RayTraceResult.Type.MISS) {
         if (raytraceresult.getType() == RayTraceResult.Type.ENTITY) {
            this.caughtEntity = ((EntityRayTraceResult)raytraceresult).getEntity();
            this.setHookedEntity();
         } else {
            this.inGround = true;
         }
      }

   }

   private void setHookedEntity() {
      this.getDataManager().set(DATA_HOOKED_ENTITY, this.caughtEntity.getEntityId() + 1);
   }

   private void catchingFish(BlockPos p_190621_1_) {
      ServerWorld serverworld = (ServerWorld)this.world;
      int i = 1;
      BlockPos blockpos = p_190621_1_.up();
      if (this.rand.nextFloat() < 0.25F && this.world.isRainingAt(blockpos)) {
         ++i;
      }

      if (this.rand.nextFloat() < 0.5F && !this.world.func_226660_f_(blockpos)) {
         --i;
      }

      if (this.ticksCatchable > 0) {
         --this.ticksCatchable;
         if (this.ticksCatchable <= 0) {
            this.ticksCaughtDelay = 0;
            this.ticksCatchableDelay = 0;
         } else {
            this.setMotion(this.getMotion().add(0.0D, -0.2D * (double)this.rand.nextFloat() * (double)this.rand.nextFloat(), 0.0D));
         }
      } else {
         float f5;
         float f6;
         float f7;
         double d4;
         double d5;
         double d6;
         Block block1;
         if (this.ticksCatchableDelay > 0) {
            this.ticksCatchableDelay -= i;
            if (this.ticksCatchableDelay > 0) {
               this.fishApproachAngle = (float)((double)this.fishApproachAngle + this.rand.nextGaussian() * 4.0D);
               f5 = this.fishApproachAngle * 0.017453292F;
               f6 = MathHelper.sin(f5);
               f7 = MathHelper.cos(f5);
               d4 = this.func_226277_ct_() + (double)(f6 * (float)this.ticksCatchableDelay * 0.1F);
               d5 = (double)((float)MathHelper.floor(this.func_226278_cu_()) + 1.0F);
               d6 = this.func_226281_cx_() + (double)(f7 * (float)this.ticksCatchableDelay * 0.1F);
               block1 = serverworld.getBlockState(new BlockPos(d4, d5 - 1.0D, d6)).getBlock();
               if (serverworld.getBlockState(new BlockPos((int)d4, (int)d5 - 1, (int)d6)).getMaterial() == Material.WATER) {
                  if (this.rand.nextFloat() < 0.15F) {
                     serverworld.spawnParticle(ParticleTypes.BUBBLE, d4, d5 - 0.10000000149011612D, d6, 1, (double)f6, 0.1D, (double)f7, 0.0D);
                  }

                  float f3 = f6 * 0.04F;
                  float f4 = f7 * 0.04F;
                  serverworld.spawnParticle(ParticleTypes.FISHING, d4, d5, d6, 0, (double)f4, 0.01D, (double)(-f3), 1.0D);
                  serverworld.spawnParticle(ParticleTypes.FISHING, d4, d5, d6, 0, (double)(-f4), 0.01D, (double)f3, 1.0D);
               }
            } else {
               Vec3d vec3d = this.getMotion();
               this.setMotion(vec3d.x, (double)(-0.4F * MathHelper.nextFloat(this.rand, 0.6F, 1.0F)), vec3d.z);
               this.playSound(SoundEvents.ENTITY_FISHING_BOBBER_SPLASH, 0.25F, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F);
               double d3 = this.func_226278_cu_() + 0.5D;
               serverworld.spawnParticle(ParticleTypes.BUBBLE, this.func_226277_ct_(), d3, this.func_226281_cx_(), (int)(1.0F + this.getWidth() * 20.0F), (double)this.getWidth(), 0.0D, (double)this.getWidth(), 0.20000000298023224D);
               serverworld.spawnParticle(ParticleTypes.FISHING, this.func_226277_ct_(), d3, this.func_226281_cx_(), (int)(1.0F + this.getWidth() * 20.0F), (double)this.getWidth(), 0.0D, (double)this.getWidth(), 0.20000000298023224D);
               this.ticksCatchable = MathHelper.nextInt(this.rand, 20, 40);
            }
         } else if (this.ticksCaughtDelay > 0) {
            this.ticksCaughtDelay -= i;
            f5 = 0.15F;
            if (this.ticksCaughtDelay < 20) {
               f5 = (float)((double)f5 + (double)(20 - this.ticksCaughtDelay) * 0.05D);
            } else if (this.ticksCaughtDelay < 40) {
               f5 = (float)((double)f5 + (double)(40 - this.ticksCaughtDelay) * 0.02D);
            } else if (this.ticksCaughtDelay < 60) {
               f5 = (float)((double)f5 + (double)(60 - this.ticksCaughtDelay) * 0.01D);
            }

            if (this.rand.nextFloat() < f5) {
               f6 = MathHelper.nextFloat(this.rand, 0.0F, 360.0F) * 0.017453292F;
               f7 = MathHelper.nextFloat(this.rand, 25.0F, 60.0F);
               d4 = this.func_226277_ct_() + (double)(MathHelper.sin(f6) * f7 * 0.1F);
               d5 = (double)((float)MathHelper.floor(this.func_226278_cu_()) + 1.0F);
               d6 = this.func_226281_cx_() + (double)(MathHelper.cos(f6) * f7 * 0.1F);
               block1 = serverworld.getBlockState(new BlockPos(d4, d5 - 1.0D, d6)).getBlock();
               if (serverworld.getBlockState(new BlockPos(d4, d5 - 1.0D, d6)).getMaterial() == Material.WATER) {
                  serverworld.spawnParticle(ParticleTypes.SPLASH, d4, d5, d6, 2 + this.rand.nextInt(2), 0.10000000149011612D, 0.0D, 0.10000000149011612D, 0.0D);
               }
            }

            if (this.ticksCaughtDelay <= 0) {
               this.fishApproachAngle = MathHelper.nextFloat(this.rand, 0.0F, 360.0F);
               this.ticksCatchableDelay = MathHelper.nextInt(this.rand, 20, 80);
            }
         } else {
            this.ticksCaughtDelay = MathHelper.nextInt(this.rand, 100, 600);
            this.ticksCaughtDelay -= this.lureSpeed * 20 * 5;
         }
      }

   }

   public void writeAdditional(CompoundNBT p_213281_1_) {
   }

   public void readAdditional(CompoundNBT p_70037_1_) {
   }

   public int handleHookRetraction(ItemStack p_146034_1_) {
      if (!this.world.isRemote && this.angler != null) {
         int i = 0;
         ItemFishedEvent event = null;
         if (this.caughtEntity != null) {
            this.bringInHookedEntity();
            CriteriaTriggers.FISHING_ROD_HOOKED.trigger((ServerPlayerEntity)this.angler, p_146034_1_, this, Collections.emptyList());
            this.world.setEntityState(this, (byte)31);
            i = this.caughtEntity instanceof ItemEntity ? 3 : 5;
         } else if (this.ticksCatchable > 0) {
            LootContext.Builder lootcontext$builder = (new LootContext.Builder((ServerWorld)this.world)).withParameter(LootParameters.POSITION, new BlockPos(this)).withParameter(LootParameters.TOOL, p_146034_1_).withRandom(this.rand).withLuck((float)this.luck + this.angler.getLuck());
            lootcontext$builder.withParameter(LootParameters.KILLER_ENTITY, this.angler).withParameter(LootParameters.THIS_ENTITY, this);
            LootTable loottable = this.world.getServer().getLootTableManager().getLootTableFromLocation(LootTables.GAMEPLAY_FISHING);
            List<ItemStack> list = loottable.generate(lootcontext$builder.build(LootParameterSets.FISHING));
            event = new ItemFishedEvent(list, this.inGround ? 2 : 1, this);
            MinecraftForge.EVENT_BUS.post(event);
            if (event.isCanceled()) {
               this.remove();
               return event.getRodDamage();
            }

            CriteriaTriggers.FISHING_ROD_HOOKED.trigger((ServerPlayerEntity)this.angler, p_146034_1_, this, list);
            Iterator var7 = list.iterator();

            while(var7.hasNext()) {
               ItemStack itemstack = (ItemStack)var7.next();
               ItemEntity itementity = new ItemEntity(this.world, this.func_226277_ct_(), this.func_226278_cu_(), this.func_226281_cx_(), itemstack);
               double d0 = this.angler.func_226277_ct_() - this.func_226277_ct_();
               double d1 = this.angler.func_226278_cu_() - this.func_226278_cu_();
               double d2 = this.angler.func_226281_cx_() - this.func_226281_cx_();
               double d3 = 0.1D;
               itementity.setMotion(d0 * 0.1D, d1 * 0.1D + Math.sqrt(Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2)) * 0.08D, d2 * 0.1D);
               this.world.addEntity(itementity);
               this.angler.world.addEntity(new ExperienceOrbEntity(this.angler.world, this.angler.func_226277_ct_(), this.angler.func_226278_cu_() + 0.5D, this.angler.func_226281_cx_() + 0.5D, this.rand.nextInt(6) + 1));
               if (itemstack.getItem().isIn(ItemTags.FISHES)) {
                  this.angler.addStat((ResourceLocation)Stats.FISH_CAUGHT, 1);
               }
            }

            i = 1;
         }

         if (this.inGround) {
            i = 2;
         }

         this.remove();
         return event == null ? i : event.getRodDamage();
      } else {
         return 0;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void handleStatusUpdate(byte p_70103_1_) {
      if (p_70103_1_ == 31 && this.world.isRemote && this.caughtEntity instanceof PlayerEntity && ((PlayerEntity)this.caughtEntity).isUser()) {
         this.bringInHookedEntity();
      }

      super.handleStatusUpdate(p_70103_1_);
   }

   protected void bringInHookedEntity() {
      if (this.angler != null) {
         Vec3d vec3d = (new Vec3d(this.angler.func_226277_ct_() - this.func_226277_ct_(), this.angler.func_226278_cu_() - this.func_226278_cu_(), this.angler.func_226281_cx_() - this.func_226281_cx_())).scale(0.1D);
         this.caughtEntity.setMotion(this.caughtEntity.getMotion().add(vec3d));
      }

   }

   protected boolean func_225502_at_() {
      return false;
   }

   public void remove(boolean p_remove_1_) {
      super.remove(p_remove_1_);
      if (this.angler != null) {
         this.angler.fishingBobber = null;
      }

   }

   @Nullable
   public PlayerEntity getAngler() {
      return this.angler;
   }

   public boolean isNonBoss() {
      return false;
   }

   public IPacket<?> createSpawnPacket() {
      Entity entity = this.getAngler();
      return new SSpawnObjectPacket(this, entity == null ? this.getEntityId() : entity.getEntityId());
   }

   static {
      DATA_HOOKED_ENTITY = EntityDataManager.createKey(FishingBobberEntity.class, DataSerializers.VARINT);
   }

   static enum State {
      FLYING,
      HOOKED_IN_ENTITY,
      BOBBING;
   }
}
