package net.minecraft.entity;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HoneyBlock;
import net.minecraft.block.NetherPortalBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.PushReaction;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ICommandSource;
import net.minecraft.command.arguments.EntityAnchorArgument;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.ProtectionEnchantment;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.DoubleNBT;
import net.minecraft.nbt.FloatNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SEntityPacket;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.INameable;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ReuseableStream;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.world.Explosion;
import net.minecraft.world.GameRules;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.server.TicketType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityProvider;
import net.minecraftforge.common.extensions.IForgeEntity;
import net.minecraftforge.common.util.ITeleporter;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.EntityEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Entity extends CapabilityProvider<Entity> implements INameable, ICommandSource, IForgeEntity {
   protected static final Logger LOGGER = LogManager.getLogger();
   private static final AtomicInteger NEXT_ENTITY_ID = new AtomicInteger();
   private static final List<ItemStack> EMPTY_EQUIPMENT = Collections.emptyList();
   private static final AxisAlignedBB ZERO_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
   private static double renderDistanceWeight = 1.0D;
   /** @deprecated */
   @Deprecated
   private final EntityType<?> type;
   private int entityId;
   public boolean preventEntitySpawning;
   private final List<Entity> passengers;
   protected int rideCooldown;
   @Nullable
   private Entity ridingEntity;
   public boolean forceSpawn;
   public World world;
   public double prevPosX;
   public double prevPosY;
   public double prevPosZ;
   private double posX;
   private double posY;
   private double posZ;
   private Vec3d motion;
   public float rotationYaw;
   public float rotationPitch;
   public float prevRotationYaw;
   public float prevRotationPitch;
   private AxisAlignedBB boundingBox;
   public boolean onGround;
   public boolean collidedHorizontally;
   public boolean collidedVertically;
   public boolean collided;
   public boolean velocityChanged;
   protected Vec3d motionMultiplier;
   /** @deprecated */
   @Deprecated
   public boolean removed;
   public float prevDistanceWalkedModified;
   public float distanceWalkedModified;
   public float distanceWalkedOnStepModified;
   public float fallDistance;
   private float nextStepDistance;
   private float nextFlap;
   public double lastTickPosX;
   public double lastTickPosY;
   public double lastTickPosZ;
   public float stepHeight;
   public boolean noClip;
   public float entityCollisionReduction;
   protected final Random rand;
   public int ticksExisted;
   private int fire;
   protected boolean inWater;
   protected double submergedHeight;
   protected boolean eyesInWater;
   protected boolean inLava;
   public int hurtResistantTime;
   protected boolean firstUpdate;
   protected final EntityDataManager dataManager;
   protected static final DataParameter<Byte> FLAGS;
   private static final DataParameter<Integer> AIR;
   private static final DataParameter<Optional<ITextComponent>> CUSTOM_NAME;
   private static final DataParameter<Boolean> CUSTOM_NAME_VISIBLE;
   private static final DataParameter<Boolean> SILENT;
   private static final DataParameter<Boolean> NO_GRAVITY;
   protected static final DataParameter<Pose> POSE;
   public boolean addedToChunk;
   public int chunkCoordX;
   public int chunkCoordY;
   public int chunkCoordZ;
   public long serverPosX;
   public long serverPosY;
   public long serverPosZ;
   public boolean ignoreFrustumCheck;
   public boolean isAirBorne;
   public int timeUntilPortal;
   protected boolean inPortal;
   protected int portalCounter;
   public DimensionType dimension;
   protected BlockPos lastPortalPos;
   protected Vec3d lastPortalVec;
   protected Direction teleportDirection;
   private boolean invulnerable;
   protected UUID entityUniqueID;
   protected String cachedUniqueIdString;
   protected boolean glowing;
   private final Set<String> tags;
   private boolean isPositionDirty;
   private final double[] pistonDeltas;
   private long pistonDeltasGameTime;
   private EntitySize size;
   private float eyeHeight;
   private boolean canUpdate;
   private Collection<ItemEntity> captureDrops;
   private CompoundNBT persistentData;
   private boolean isAddedToWorld;

   public Entity(EntityType<?> p_i48580_1_, World p_i48580_2_) {
      super(Entity.class);
      this.entityId = NEXT_ENTITY_ID.incrementAndGet();
      this.passengers = Lists.newArrayList();
      this.motion = Vec3d.ZERO;
      this.boundingBox = ZERO_AABB;
      this.motionMultiplier = Vec3d.ZERO;
      this.nextStepDistance = 1.0F;
      this.nextFlap = 1.0F;
      this.rand = new Random();
      this.fire = -this.getFireImmuneTicks();
      this.firstUpdate = true;
      this.entityUniqueID = MathHelper.getRandomUUID(this.rand);
      this.cachedUniqueIdString = this.entityUniqueID.toString();
      this.tags = Sets.newHashSet();
      this.pistonDeltas = new double[]{0.0D, 0.0D, 0.0D};
      this.canUpdate = true;
      this.captureDrops = null;
      this.type = p_i48580_1_;
      this.world = p_i48580_2_;
      this.size = p_i48580_1_.getSize();
      this.setPosition(0.0D, 0.0D, 0.0D);
      if (p_i48580_2_ != null) {
         this.dimension = p_i48580_2_.dimension.getType();
      }

      this.dataManager = new EntityDataManager(this);
      this.dataManager.register(FLAGS, (byte)0);
      this.dataManager.register(AIR, this.getMaxAir());
      this.dataManager.register(CUSTOM_NAME_VISIBLE, false);
      this.dataManager.register(CUSTOM_NAME, Optional.empty());
      this.dataManager.register(SILENT, false);
      this.dataManager.register(NO_GRAVITY, false);
      this.dataManager.register(POSE, Pose.STANDING);
      this.registerData();
      this.eyeHeight = this.getEyeHeightForge(Pose.STANDING, this.size);
      MinecraftForge.EVENT_BUS.post(new EntityEvent.EntityConstructing(this));
      this.gatherCapabilities();
   }

   @OnlyIn(Dist.CLIENT)
   public int func_226263_P_() {
      Team team = this.getTeam();
      return team != null && team.getColor().getColor() != null ? team.getColor().getColor() : 16777215;
   }

   public boolean isSpectator() {
      return false;
   }

   public final void detach() {
      if (this.isBeingRidden()) {
         this.removePassengers();
      }

      if (this.isPassenger()) {
         this.stopRiding();
      }

   }

   public void func_213312_b(double p_213312_1_, double p_213312_3_, double p_213312_5_) {
      this.serverPosX = SEntityPacket.func_218743_a(p_213312_1_);
      this.serverPosY = SEntityPacket.func_218743_a(p_213312_3_);
      this.serverPosZ = SEntityPacket.func_218743_a(p_213312_5_);
   }

   public EntityType<?> getType() {
      return this.type;
   }

   public int getEntityId() {
      return this.entityId;
   }

   public void setEntityId(int p_145769_1_) {
      this.entityId = p_145769_1_;
   }

   public Set<String> getTags() {
      return this.tags;
   }

   public boolean addTag(String p_184211_1_) {
      return this.tags.size() >= 1024 ? false : this.tags.add(p_184211_1_);
   }

   public boolean removeTag(String p_184197_1_) {
      return this.tags.remove(p_184197_1_);
   }

   public void onKillCommand() {
      this.remove();
   }

   protected abstract void registerData();

   public EntityDataManager getDataManager() {
      return this.dataManager;
   }

   public boolean equals(Object p_equals_1_) {
      if (p_equals_1_ instanceof Entity) {
         return ((Entity)p_equals_1_).entityId == this.entityId;
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.entityId;
   }

   @OnlyIn(Dist.CLIENT)
   protected void preparePlayerToSpawn() {
      if (this.world != null) {
         for(double d0 = this.func_226278_cu_(); d0 > 0.0D && d0 < (double)this.world.getDimension().getHeight(); ++d0) {
            this.setPosition(this.func_226277_ct_(), d0, this.func_226281_cx_());
            if (this.world.func_226669_j_(this)) {
               break;
            }
         }

         this.setMotion(Vec3d.ZERO);
         this.rotationPitch = 0.0F;
      }

   }

   public void remove() {
      this.remove(false);
   }

   public void remove(boolean p_remove_1_) {
      this.removed = true;
      if (!p_remove_1_) {
         this.invalidateCaps();
      }

   }

   protected void setPose(Pose p_213301_1_) {
      this.dataManager.set(POSE, p_213301_1_);
   }

   public Pose getPose() {
      return (Pose)this.dataManager.get(POSE);
   }

   protected void setRotation(float p_70101_1_, float p_70101_2_) {
      this.rotationYaw = p_70101_1_ % 360.0F;
      this.rotationPitch = p_70101_2_ % 360.0F;
   }

   public void setPosition(double p_70107_1_, double p_70107_3_, double p_70107_5_) {
      this.func_226288_n_(p_70107_1_, p_70107_3_, p_70107_5_);
      if (this.isAddedToWorld() && !this.world.isRemote && this.world instanceof ServerWorld) {
         ((ServerWorld)this.world).chunkCheck(this);
      }

      float f = this.size.width / 2.0F;
      float f1 = this.size.height;
      this.setBoundingBox(new AxisAlignedBB(p_70107_1_ - (double)f, p_70107_3_, p_70107_5_ - (double)f, p_70107_1_ + (double)f, p_70107_3_ + (double)f1, p_70107_5_ + (double)f));
   }

   protected void func_226264_Z_() {
      this.setPosition(this.posX, this.posY, this.posZ);
   }

   @OnlyIn(Dist.CLIENT)
   public void rotateTowards(double p_195049_1_, double p_195049_3_) {
      double d0 = p_195049_3_ * 0.15D;
      double d1 = p_195049_1_ * 0.15D;
      this.rotationPitch = (float)((double)this.rotationPitch + d0);
      this.rotationYaw = (float)((double)this.rotationYaw + d1);
      this.rotationPitch = MathHelper.clamp(this.rotationPitch, -90.0F, 90.0F);
      this.prevRotationPitch = (float)((double)this.prevRotationPitch + d0);
      this.prevRotationYaw = (float)((double)this.prevRotationYaw + d1);
      this.prevRotationPitch = MathHelper.clamp(this.prevRotationPitch, -90.0F, 90.0F);
      if (this.ridingEntity != null) {
         this.ridingEntity.applyOrientationToEntity(this);
      }

   }

   public void tick() {
      if (!this.world.isRemote) {
         this.setFlag(6, this.func_225510_bt_());
      }

      this.baseTick();
   }

   public void baseTick() {
      this.world.getProfiler().startSection("entityBaseTick");
      if (this.isPassenger() && this.getRidingEntity().removed) {
         this.stopRiding();
      }

      if (this.rideCooldown > 0) {
         --this.rideCooldown;
      }

      this.prevDistanceWalkedModified = this.distanceWalkedModified;
      this.prevRotationPitch = this.rotationPitch;
      this.prevRotationYaw = this.rotationYaw;
      this.updatePortal();
      this.spawnRunningParticles();
      this.updateAquatics();
      if (this.world.isRemote) {
         this.extinguish();
      } else if (this.fire > 0) {
         if (this.isImmuneToFire()) {
            this.fire -= 4;
            if (this.fire < 0) {
               this.extinguish();
            }
         } else {
            if (this.fire % 20 == 0) {
               this.attackEntityFrom(DamageSource.ON_FIRE, 1.0F);
            }

            --this.fire;
         }
      }

      if (this.isInLava()) {
         this.setOnFireFromLava();
         this.fallDistance *= 0.5F;
      }

      if (this.func_226278_cu_() < -64.0D) {
         this.outOfWorld();
      }

      if (!this.world.isRemote) {
         this.setFlag(0, this.fire > 0);
      }

      this.firstUpdate = false;
      this.world.getProfiler().endSection();
   }

   protected void decrementTimeUntilPortal() {
      if (this.timeUntilPortal > 0) {
         --this.timeUntilPortal;
      }

   }

   public int getMaxInPortalTime() {
      return 1;
   }

   protected void setOnFireFromLava() {
      if (!this.isImmuneToFire()) {
         this.setFire(15);
         this.attackEntityFrom(DamageSource.LAVA, 4.0F);
      }

   }

   public void setFire(int p_70015_1_) {
      int i = p_70015_1_ * 20;
      if (this instanceof LivingEntity) {
         i = ProtectionEnchantment.getFireTimeForEntity((LivingEntity)this, i);
      }

      if (this.fire < i) {
         this.fire = i;
      }

   }

   public void func_223308_g(int p_223308_1_) {
      this.fire = p_223308_1_;
   }

   public int func_223314_ad() {
      return this.fire;
   }

   public void extinguish() {
      this.fire = 0;
   }

   protected void outOfWorld() {
      this.remove();
   }

   public boolean isOffsetPositionInLiquid(double p_70038_1_, double p_70038_3_, double p_70038_5_) {
      return this.isLiquidPresentInAABB(this.getBoundingBox().offset(p_70038_1_, p_70038_3_, p_70038_5_));
   }

   private boolean isLiquidPresentInAABB(AxisAlignedBB p_174809_1_) {
      return this.world.func_226665_a__(this, p_174809_1_) && !this.world.containsAnyLiquid(p_174809_1_);
   }

   public void move(MoverType p_213315_1_, Vec3d p_213315_2_) {
      if (this.noClip) {
         this.setBoundingBox(this.getBoundingBox().offset(p_213315_2_));
         this.resetPositionToBB();
      } else {
         if (p_213315_1_ == MoverType.PISTON) {
            p_213315_2_ = this.handlePistonMovement(p_213315_2_);
            if (p_213315_2_.equals(Vec3d.ZERO)) {
               return;
            }
         }

         this.world.getProfiler().startSection("move");
         if (this.motionMultiplier.lengthSquared() > 1.0E-7D) {
            p_213315_2_ = p_213315_2_.mul(this.motionMultiplier);
            this.motionMultiplier = Vec3d.ZERO;
            this.setMotion(Vec3d.ZERO);
         }

         p_213315_2_ = this.func_225514_a_(p_213315_2_, p_213315_1_);
         Vec3d vec3d = this.getAllowedMovement(p_213315_2_);
         if (vec3d.lengthSquared() > 1.0E-7D) {
            this.setBoundingBox(this.getBoundingBox().offset(vec3d));
            this.resetPositionToBB();
         }

         this.world.getProfiler().endSection();
         this.world.getProfiler().startSection("rest");
         this.collidedHorizontally = !MathHelper.epsilonEquals(p_213315_2_.x, vec3d.x) || !MathHelper.epsilonEquals(p_213315_2_.z, vec3d.z);
         this.collidedVertically = p_213315_2_.y != vec3d.y;
         this.onGround = this.collidedVertically && p_213315_2_.y < 0.0D;
         this.collided = this.collidedHorizontally || this.collidedVertically;
         BlockPos blockpos = this.func_226268_ag_();
         BlockState blockstate = this.world.getBlockState(blockpos);
         this.updateFallState(vec3d.y, this.onGround, blockstate, blockpos);
         Vec3d vec3d1 = this.getMotion();
         if (p_213315_2_.x != vec3d.x) {
            this.setMotion(0.0D, vec3d1.y, vec3d1.z);
         }

         if (p_213315_2_.z != vec3d.z) {
            this.setMotion(vec3d1.x, vec3d1.y, 0.0D);
         }

         Block block = blockstate.getBlock();
         if (p_213315_2_.y != vec3d.y) {
            block.onLanded(this.world, this);
         }

         if (this.onGround && !this.func_226271_bk_()) {
            block.onEntityWalk(this.world, blockpos, this);
         }

         if (this.func_225502_at_() && !this.isPassenger()) {
            double d0 = vec3d.x;
            double d1 = vec3d.y;
            double d2 = vec3d.z;
            if (block != Blocks.LADDER && block != Blocks.SCAFFOLDING) {
               d1 = 0.0D;
            }

            this.distanceWalkedModified = (float)((double)this.distanceWalkedModified + (double)MathHelper.sqrt(func_213296_b(vec3d)) * 0.6D);
            this.distanceWalkedOnStepModified = (float)((double)this.distanceWalkedOnStepModified + (double)MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2) * 0.6D);
            if (this.distanceWalkedOnStepModified > this.nextStepDistance && !blockstate.isAir(this.world, blockpos)) {
               this.nextStepDistance = this.determineNextStepDistance();
               if (!this.isInWater()) {
                  this.playStepSound(blockpos, blockstate);
               } else {
                  Entity entity = this.isBeingRidden() && this.getControllingPassenger() != null ? this.getControllingPassenger() : this;
                  float f = entity == this ? 0.35F : 0.4F;
                  Vec3d vec3d2 = entity.getMotion();
                  float f1 = MathHelper.sqrt(vec3d2.x * vec3d2.x * 0.20000000298023224D + vec3d2.y * vec3d2.y + vec3d2.z * vec3d2.z * 0.20000000298023224D) * f;
                  if (f1 > 1.0F) {
                     f1 = 1.0F;
                  }

                  this.playSwimSound(f1);
               }
            } else if (this.distanceWalkedOnStepModified > this.nextFlap && this.makeFlySound() && blockstate.isAir(this.world, blockpos)) {
               this.nextFlap = this.playFlySound(this.distanceWalkedOnStepModified);
            }
         }

         try {
            this.inLava = false;
            this.doBlockCollisions();
         } catch (Throwable var18) {
            CrashReport crashreport = CrashReport.makeCrashReport(var18, "Checking entity block collision");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Entity being checked for collision");
            this.fillCrashReport(crashreportcategory);
            throw new ReportedException(crashreport);
         }

         this.setMotion(this.getMotion().mul((double)this.func_225515_ai_(), 1.0D, (double)this.func_225515_ai_()));
         boolean flag = this.isInWaterRainOrBubbleColumn();
         if (this.world.isFlammableWithin(this.getBoundingBox().shrink(0.001D))) {
            if (!flag) {
               ++this.fire;
               if (this.fire == 0) {
                  this.setFire(8);
               }
            }

            this.dealFireDamage(1);
         } else if (this.fire <= 0) {
            this.fire = -this.getFireImmuneTicks();
         }

         if (flag && this.isBurning()) {
            this.playSound(SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, 0.7F, 1.6F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F);
            this.fire = -this.getFireImmuneTicks();
         }

         this.world.getProfiler().endSection();
      }

   }

   protected BlockPos func_226268_ag_() {
      int i = MathHelper.floor(this.posX);
      int j = MathHelper.floor(this.posY - 0.20000000298023224D);
      int k = MathHelper.floor(this.posZ);
      BlockPos blockpos = new BlockPos(i, j, k);
      if (this.world.isAirBlock(blockpos)) {
         BlockPos blockpos1 = blockpos.down();
         BlockState blockstate = this.world.getBlockState(blockpos1);
         if (blockstate.collisionExtendsVertically(this.world, blockpos1, this)) {
            return blockpos1;
         }
      }

      return blockpos;
   }

   protected float func_226269_ah_() {
      float f = this.world.getBlockState(new BlockPos(this)).getBlock().func_226892_n_();
      float f1 = this.world.getBlockState(this.func_226270_aj_()).getBlock().func_226892_n_();
      return (double)f == 1.0D ? f1 : f;
   }

   protected float func_225515_ai_() {
      Block block = this.world.getBlockState(new BlockPos(this)).getBlock();
      float f = block.func_226891_m_();
      if (block != Blocks.WATER && block != Blocks.BUBBLE_COLUMN) {
         return (double)f == 1.0D ? this.world.getBlockState(this.func_226270_aj_()).getBlock().func_226891_m_() : f;
      } else {
         return f;
      }
   }

   protected BlockPos func_226270_aj_() {
      return new BlockPos(this.posX, this.getBoundingBox().minY - 0.5000001D, this.posZ);
   }

   protected Vec3d func_225514_a_(Vec3d p_225514_1_, MoverType p_225514_2_) {
      return p_225514_1_;
   }

   protected Vec3d handlePistonMovement(Vec3d p_213308_1_) {
      if (p_213308_1_.lengthSquared() <= 1.0E-7D) {
         return p_213308_1_;
      } else {
         long i = this.world.getGameTime();
         if (i != this.pistonDeltasGameTime) {
            Arrays.fill(this.pistonDeltas, 0.0D);
            this.pistonDeltasGameTime = i;
         }

         double d0;
         if (p_213308_1_.x != 0.0D) {
            d0 = this.calculatePistonDeltas(Direction.Axis.X, p_213308_1_.x);
            return Math.abs(d0) <= 9.999999747378752E-6D ? Vec3d.ZERO : new Vec3d(d0, 0.0D, 0.0D);
         } else if (p_213308_1_.y != 0.0D) {
            d0 = this.calculatePistonDeltas(Direction.Axis.Y, p_213308_1_.y);
            return Math.abs(d0) <= 9.999999747378752E-6D ? Vec3d.ZERO : new Vec3d(0.0D, d0, 0.0D);
         } else if (p_213308_1_.z != 0.0D) {
            d0 = this.calculatePistonDeltas(Direction.Axis.Z, p_213308_1_.z);
            return Math.abs(d0) <= 9.999999747378752E-6D ? Vec3d.ZERO : new Vec3d(0.0D, 0.0D, d0);
         } else {
            return Vec3d.ZERO;
         }
      }
   }

   private double calculatePistonDeltas(Direction.Axis p_213304_1_, double p_213304_2_) {
      int i = p_213304_1_.ordinal();
      double d0 = MathHelper.clamp(p_213304_2_ + this.pistonDeltas[i], -0.51D, 0.51D);
      p_213304_2_ = d0 - this.pistonDeltas[i];
      this.pistonDeltas[i] = d0;
      return p_213304_2_;
   }

   private Vec3d getAllowedMovement(Vec3d p_213306_1_) {
      AxisAlignedBB axisalignedbb = this.getBoundingBox();
      ISelectionContext iselectioncontext = ISelectionContext.forEntity(this);
      VoxelShape voxelshape = this.world.getWorldBorder().getShape();
      Stream<VoxelShape> stream = VoxelShapes.compare(voxelshape, VoxelShapes.create(axisalignedbb.shrink(1.0E-7D)), IBooleanFunction.AND) ? Stream.empty() : Stream.of(voxelshape);
      Stream<VoxelShape> stream1 = this.world.getEmptyCollisionShapes(this, axisalignedbb.expand(p_213306_1_), ImmutableSet.of());
      ReuseableStream<VoxelShape> reuseablestream = new ReuseableStream(Stream.concat(stream1, stream));
      Vec3d vec3d = p_213306_1_.lengthSquared() == 0.0D ? p_213306_1_ : func_223307_a(this, p_213306_1_, axisalignedbb, this.world, iselectioncontext, reuseablestream);
      boolean flag = p_213306_1_.x != vec3d.x;
      boolean flag1 = p_213306_1_.y != vec3d.y;
      boolean flag2 = p_213306_1_.z != vec3d.z;
      boolean flag3 = this.onGround || flag1 && p_213306_1_.y < 0.0D;
      if (this.stepHeight > 0.0F && flag3 && (flag || flag2)) {
         Vec3d vec3d1 = func_223307_a(this, new Vec3d(p_213306_1_.x, (double)this.stepHeight, p_213306_1_.z), axisalignedbb, this.world, iselectioncontext, reuseablestream);
         Vec3d vec3d2 = func_223307_a(this, new Vec3d(0.0D, (double)this.stepHeight, 0.0D), axisalignedbb.expand(p_213306_1_.x, 0.0D, p_213306_1_.z), this.world, iselectioncontext, reuseablestream);
         if (vec3d2.y < (double)this.stepHeight) {
            Vec3d vec3d3 = func_223307_a(this, new Vec3d(p_213306_1_.x, 0.0D, p_213306_1_.z), axisalignedbb.offset(vec3d2), this.world, iselectioncontext, reuseablestream).add(vec3d2);
            if (func_213296_b(vec3d3) > func_213296_b(vec3d1)) {
               vec3d1 = vec3d3;
            }
         }

         if (func_213296_b(vec3d1) > func_213296_b(vec3d)) {
            return vec3d1.add(func_223307_a(this, new Vec3d(0.0D, -vec3d1.y + p_213306_1_.y, 0.0D), axisalignedbb.offset(vec3d1), this.world, iselectioncontext, reuseablestream));
         }
      }

      return vec3d;
   }

   public static double func_213296_b(Vec3d p_213296_0_) {
      return p_213296_0_.x * p_213296_0_.x + p_213296_0_.z * p_213296_0_.z;
   }

   public static Vec3d func_223307_a(@Nullable Entity p_223307_0_, Vec3d p_223307_1_, AxisAlignedBB p_223307_2_, World p_223307_3_, ISelectionContext p_223307_4_, ReuseableStream<VoxelShape> p_223307_5_) {
      boolean flag = p_223307_1_.x == 0.0D;
      boolean flag1 = p_223307_1_.y == 0.0D;
      boolean flag2 = p_223307_1_.z == 0.0D;
      if (flag && flag1 || flag && flag2 || flag1 && flag2) {
         return getAllowedMovement(p_223307_1_, p_223307_2_, p_223307_3_, p_223307_4_, p_223307_5_);
      } else {
         ReuseableStream<VoxelShape> reuseablestream = new ReuseableStream(Stream.concat(p_223307_5_.createStream(), p_223307_3_.func_226666_b_(p_223307_0_, p_223307_2_.expand(p_223307_1_))));
         return func_223310_a(p_223307_1_, p_223307_2_, reuseablestream);
      }
   }

   public static Vec3d func_223310_a(Vec3d p_223310_0_, AxisAlignedBB p_223310_1_, ReuseableStream<VoxelShape> p_223310_2_) {
      double d0 = p_223310_0_.x;
      double d1 = p_223310_0_.y;
      double d2 = p_223310_0_.z;
      if (d1 != 0.0D) {
         d1 = VoxelShapes.getAllowedOffset(Direction.Axis.Y, p_223310_1_, p_223310_2_.createStream(), d1);
         if (d1 != 0.0D) {
            p_223310_1_ = p_223310_1_.offset(0.0D, d1, 0.0D);
         }
      }

      boolean flag = Math.abs(d0) < Math.abs(d2);
      if (flag && d2 != 0.0D) {
         d2 = VoxelShapes.getAllowedOffset(Direction.Axis.Z, p_223310_1_, p_223310_2_.createStream(), d2);
         if (d2 != 0.0D) {
            p_223310_1_ = p_223310_1_.offset(0.0D, 0.0D, d2);
         }
      }

      if (d0 != 0.0D) {
         d0 = VoxelShapes.getAllowedOffset(Direction.Axis.X, p_223310_1_, p_223310_2_.createStream(), d0);
         if (!flag && d0 != 0.0D) {
            p_223310_1_ = p_223310_1_.offset(d0, 0.0D, 0.0D);
         }
      }

      if (!flag && d2 != 0.0D) {
         d2 = VoxelShapes.getAllowedOffset(Direction.Axis.Z, p_223310_1_, p_223310_2_.createStream(), d2);
      }

      return new Vec3d(d0, d1, d2);
   }

   public static Vec3d getAllowedMovement(Vec3d p_213313_0_, AxisAlignedBB p_213313_1_, IWorldReader p_213313_2_, ISelectionContext p_213313_3_, ReuseableStream<VoxelShape> p_213313_4_) {
      double d0 = p_213313_0_.x;
      double d1 = p_213313_0_.y;
      double d2 = p_213313_0_.z;
      if (d1 != 0.0D) {
         d1 = VoxelShapes.getAllowedOffset(Direction.Axis.Y, p_213313_1_, p_213313_2_, d1, p_213313_3_, p_213313_4_.createStream());
         if (d1 != 0.0D) {
            p_213313_1_ = p_213313_1_.offset(0.0D, d1, 0.0D);
         }
      }

      boolean flag = Math.abs(d0) < Math.abs(d2);
      if (flag && d2 != 0.0D) {
         d2 = VoxelShapes.getAllowedOffset(Direction.Axis.Z, p_213313_1_, p_213313_2_, d2, p_213313_3_, p_213313_4_.createStream());
         if (d2 != 0.0D) {
            p_213313_1_ = p_213313_1_.offset(0.0D, 0.0D, d2);
         }
      }

      if (d0 != 0.0D) {
         d0 = VoxelShapes.getAllowedOffset(Direction.Axis.X, p_213313_1_, p_213313_2_, d0, p_213313_3_, p_213313_4_.createStream());
         if (!flag && d0 != 0.0D) {
            p_213313_1_ = p_213313_1_.offset(d0, 0.0D, 0.0D);
         }
      }

      if (!flag && d2 != 0.0D) {
         d2 = VoxelShapes.getAllowedOffset(Direction.Axis.Z, p_213313_1_, p_213313_2_, d2, p_213313_3_, p_213313_4_.createStream());
      }

      return new Vec3d(d0, d1, d2);
   }

   protected float determineNextStepDistance() {
      return (float)((int)this.distanceWalkedOnStepModified + 1);
   }

   public void resetPositionToBB() {
      AxisAlignedBB axisalignedbb = this.getBoundingBox();
      this.func_226288_n_((axisalignedbb.minX + axisalignedbb.maxX) / 2.0D, axisalignedbb.minY, (axisalignedbb.minZ + axisalignedbb.maxZ) / 2.0D);
      if (this.isAddedToWorld() && !this.world.isRemote && this.world instanceof ServerWorld) {
         ((ServerWorld)this.world).chunkCheck(this);
      }

   }

   protected SoundEvent getSwimSound() {
      return SoundEvents.ENTITY_GENERIC_SWIM;
   }

   protected SoundEvent getSplashSound() {
      return SoundEvents.ENTITY_GENERIC_SPLASH;
   }

   protected SoundEvent getHighspeedSplashSound() {
      return SoundEvents.ENTITY_GENERIC_SPLASH;
   }

   protected void doBlockCollisions() {
      AxisAlignedBB axisalignedbb = this.getBoundingBox();
      BlockPos.PooledMutable blockpos$pooledmutable = BlockPos.PooledMutable.retain(axisalignedbb.minX + 0.001D, axisalignedbb.minY + 0.001D, axisalignedbb.minZ + 0.001D);
      Throwable var3 = null;

      try {
         BlockPos.PooledMutable blockpos$pooledmutable1 = BlockPos.PooledMutable.retain(axisalignedbb.maxX - 0.001D, axisalignedbb.maxY - 0.001D, axisalignedbb.maxZ - 0.001D);
         Throwable var5 = null;

         try {
            BlockPos.PooledMutable blockpos$pooledmutable2 = BlockPos.PooledMutable.retain();
            Throwable var7 = null;

            try {
               if (this.world.isAreaLoaded(blockpos$pooledmutable, blockpos$pooledmutable1)) {
                  for(int i = blockpos$pooledmutable.getX(); i <= blockpos$pooledmutable1.getX(); ++i) {
                     for(int j = blockpos$pooledmutable.getY(); j <= blockpos$pooledmutable1.getY(); ++j) {
                        for(int k = blockpos$pooledmutable.getZ(); k <= blockpos$pooledmutable1.getZ(); ++k) {
                           blockpos$pooledmutable2.setPos(i, j, k);
                           BlockState blockstate = this.world.getBlockState(blockpos$pooledmutable2);

                           try {
                              blockstate.onEntityCollision(this.world, blockpos$pooledmutable2, this);
                              this.onInsideBlock(blockstate);
                           } catch (Throwable var60) {
                              CrashReport crashreport = CrashReport.makeCrashReport(var60, "Colliding entity with block");
                              CrashReportCategory crashreportcategory = crashreport.makeCategory("Block being collided with");
                              CrashReportCategory.addBlockInfo(crashreportcategory, blockpos$pooledmutable2, blockstate);
                              throw new ReportedException(crashreport);
                           }
                        }
                     }
                  }
               }
            } catch (Throwable var61) {
               var7 = var61;
               throw var61;
            } finally {
               if (blockpos$pooledmutable2 != null) {
                  if (var7 != null) {
                     try {
                        blockpos$pooledmutable2.close();
                     } catch (Throwable var59) {
                        var7.addSuppressed(var59);
                     }
                  } else {
                     blockpos$pooledmutable2.close();
                  }
               }

            }
         } catch (Throwable var63) {
            var5 = var63;
            throw var63;
         } finally {
            if (blockpos$pooledmutable1 != null) {
               if (var5 != null) {
                  try {
                     blockpos$pooledmutable1.close();
                  } catch (Throwable var58) {
                     var5.addSuppressed(var58);
                  }
               } else {
                  blockpos$pooledmutable1.close();
               }
            }

         }
      } catch (Throwable var65) {
         var3 = var65;
         throw var65;
      } finally {
         if (blockpos$pooledmutable != null) {
            if (var3 != null) {
               try {
                  blockpos$pooledmutable.close();
               } catch (Throwable var57) {
                  var3.addSuppressed(var57);
               }
            } else {
               blockpos$pooledmutable.close();
            }
         }

      }

   }

   protected void onInsideBlock(BlockState p_191955_1_) {
   }

   protected void playStepSound(BlockPos p_180429_1_, BlockState p_180429_2_) {
      if (!p_180429_2_.getMaterial().isLiquid()) {
         BlockState blockstate = this.world.getBlockState(p_180429_1_.up());
         SoundType soundtype = blockstate.getBlock() == Blocks.SNOW ? blockstate.getSoundType(this.world, p_180429_1_, this) : p_180429_2_.getSoundType(this.world, p_180429_1_, this);
         this.playSound(soundtype.getStepSound(), soundtype.getVolume() * 0.15F, soundtype.getPitch());
      }

   }

   protected void playSwimSound(float p_203006_1_) {
      this.playSound(this.getSwimSound(), p_203006_1_, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F);
   }

   protected float playFlySound(float p_191954_1_) {
      return 0.0F;
   }

   protected boolean makeFlySound() {
      return false;
   }

   public void playSound(SoundEvent p_184185_1_, float p_184185_2_, float p_184185_3_) {
      if (!this.isSilent()) {
         this.world.playSound((PlayerEntity)null, this.func_226277_ct_(), this.func_226278_cu_(), this.func_226281_cx_(), p_184185_1_, this.getSoundCategory(), p_184185_2_, p_184185_3_);
      }

   }

   public boolean isSilent() {
      return (Boolean)this.dataManager.get(SILENT);
   }

   public void setSilent(boolean p_174810_1_) {
      this.dataManager.set(SILENT, p_174810_1_);
   }

   public boolean hasNoGravity() {
      return (Boolean)this.dataManager.get(NO_GRAVITY);
   }

   public void setNoGravity(boolean p_189654_1_) {
      this.dataManager.set(NO_GRAVITY, p_189654_1_);
   }

   protected boolean func_225502_at_() {
      return true;
   }

   protected void updateFallState(double p_184231_1_, boolean p_184231_3_, BlockState p_184231_4_, BlockPos p_184231_5_) {
      if (p_184231_3_) {
         if (this.fallDistance > 0.0F) {
            p_184231_4_.getBlock().onFallenUpon(this.world, p_184231_5_, this, this.fallDistance);
         }

         this.fallDistance = 0.0F;
      } else if (p_184231_1_ < 0.0D) {
         this.fallDistance = (float)((double)this.fallDistance - p_184231_1_);
      }

   }

   @Nullable
   public AxisAlignedBB getCollisionBoundingBox() {
      return null;
   }

   protected void dealFireDamage(int p_70081_1_) {
      if (!this.isImmuneToFire()) {
         this.attackEntityFrom(DamageSource.IN_FIRE, (float)p_70081_1_);
      }

   }

   public final boolean isImmuneToFire() {
      return this.getType().isImmuneToFire();
   }

   public boolean func_225503_b_(float p_225503_1_, float p_225503_2_) {
      if (this.isBeingRidden()) {
         Iterator var3 = this.getPassengers().iterator();

         while(var3.hasNext()) {
            Entity entity = (Entity)var3.next();
            entity.func_225503_b_(p_225503_1_, p_225503_2_);
         }
      }

      return false;
   }

   public boolean isInWater() {
      return this.inWater;
   }

   private boolean isInRain() {
      BlockPos.PooledMutable blockpos$pooledmutable = BlockPos.PooledMutable.retain(this);
      Throwable var3 = null;

      boolean flag;
      try {
         flag = this.world.isRainingAt(blockpos$pooledmutable) || this.world.isRainingAt(blockpos$pooledmutable.setPos(this.func_226277_ct_(), this.func_226278_cu_() + (double)this.size.height, this.func_226281_cx_()));
      } catch (Throwable var12) {
         var3 = var12;
         throw var12;
      } finally {
         if (blockpos$pooledmutable != null) {
            if (var3 != null) {
               try {
                  blockpos$pooledmutable.close();
               } catch (Throwable var11) {
                  var3.addSuppressed(var11);
               }
            } else {
               blockpos$pooledmutable.close();
            }
         }

      }

      return flag;
   }

   private boolean isInBubbleColumn() {
      return this.world.getBlockState(new BlockPos(this)).getBlock() == Blocks.BUBBLE_COLUMN;
   }

   public boolean isWet() {
      return this.isInWater() || this.isInRain();
   }

   public boolean isInWaterRainOrBubbleColumn() {
      return this.isInWater() || this.isInRain() || this.isInBubbleColumn();
   }

   public boolean isInWaterOrBubbleColumn() {
      return this.isInWater() || this.isInBubbleColumn();
   }

   public boolean canSwim() {
      return this.eyesInWater && this.isInWater();
   }

   private void updateAquatics() {
      this.handleWaterMovement();
      this.updateEyesInWater();
      this.updateSwimming();
   }

   public void updateSwimming() {
      if (this.isSwimming()) {
         this.setSwimming(this.isSprinting() && this.isInWater() && !this.isPassenger());
      } else {
         this.setSwimming(this.isSprinting() && this.canSwim() && !this.isPassenger());
      }

   }

   public boolean handleWaterMovement() {
      if (this.getRidingEntity() instanceof BoatEntity) {
         this.inWater = false;
      } else if (this.handleFluidAcceleration(FluidTags.WATER)) {
         if (!this.inWater && !this.firstUpdate) {
            this.doWaterSplashEffect();
         }

         this.fallDistance = 0.0F;
         this.inWater = true;
         this.extinguish();
      } else {
         this.inWater = false;
      }

      return this.inWater;
   }

   private void updateEyesInWater() {
      this.eyesInWater = this.areEyesInFluid(FluidTags.WATER, true);
   }

   protected void doWaterSplashEffect() {
      Entity entity = this.isBeingRidden() && this.getControllingPassenger() != null ? this.getControllingPassenger() : this;
      float f = entity == this ? 0.2F : 0.9F;
      Vec3d vec3d = entity.getMotion();
      float f1 = MathHelper.sqrt(vec3d.x * vec3d.x * 0.20000000298023224D + vec3d.y * vec3d.y + vec3d.z * vec3d.z * 0.20000000298023224D) * f;
      if (f1 > 1.0F) {
         f1 = 1.0F;
      }

      if ((double)f1 < 0.25D) {
         this.playSound(this.getSplashSound(), f1, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F);
      } else {
         this.playSound(this.getHighspeedSplashSound(), f1, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F);
      }

      float f2 = (float)MathHelper.floor(this.func_226278_cu_());

      int j;
      float f5;
      float f6;
      for(j = 0; (float)j < 1.0F + this.size.width * 20.0F; ++j) {
         f5 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.size.width;
         f6 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.size.width;
         this.world.addParticle(ParticleTypes.BUBBLE, this.func_226277_ct_() + (double)f5, (double)(f2 + 1.0F), this.func_226281_cx_() + (double)f6, vec3d.x, vec3d.y - (double)(this.rand.nextFloat() * 0.2F), vec3d.z);
      }

      for(j = 0; (float)j < 1.0F + this.size.width * 20.0F; ++j) {
         f5 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.size.width;
         f6 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.size.width;
         this.world.addParticle(ParticleTypes.SPLASH, this.func_226277_ct_() + (double)f5, (double)(f2 + 1.0F), this.func_226281_cx_() + (double)f6, vec3d.x, vec3d.y, vec3d.z);
      }

   }

   public void spawnRunningParticles() {
      if (this.isSprinting() && !this.isInWater()) {
         this.createRunningParticles();
      }

   }

   protected void createRunningParticles() {
      int i = MathHelper.floor(this.func_226277_ct_());
      int j = MathHelper.floor(this.func_226278_cu_() - 0.20000000298023224D);
      int k = MathHelper.floor(this.func_226281_cx_());
      BlockPos blockpos = new BlockPos(i, j, k);
      BlockState blockstate = this.world.getBlockState(blockpos);
      if (!blockstate.addRunningEffects(this.world, blockpos, this) && blockstate.getRenderType() != BlockRenderType.INVISIBLE) {
         Vec3d vec3d = this.getMotion();
         this.world.addParticle((new BlockParticleData(ParticleTypes.BLOCK, blockstate)).setPos(blockpos), this.func_226277_ct_() + ((double)this.rand.nextFloat() - 0.5D) * (double)this.size.width, this.func_226278_cu_() + 0.1D, this.func_226281_cx_() + ((double)this.rand.nextFloat() - 0.5D) * (double)this.size.width, vec3d.x * -4.0D, 1.5D, vec3d.z * -4.0D);
      }

   }

   public boolean areEyesInFluid(Tag<Fluid> p_208600_1_) {
      return this.areEyesInFluid(p_208600_1_, false);
   }

   public boolean areEyesInFluid(Tag<Fluid> p_213290_1_, boolean p_213290_2_) {
      if (this.getRidingEntity() instanceof BoatEntity) {
         return false;
      } else {
         double d0 = this.func_226280_cw_();
         BlockPos blockpos = new BlockPos(this.func_226277_ct_(), d0, this.func_226281_cx_());
         if (p_213290_2_ && !this.world.chunkExists(blockpos.getX() >> 4, blockpos.getZ() >> 4)) {
            return false;
         } else {
            IFluidState ifluidstate = this.world.getFluidState(blockpos);
            return ifluidstate.isEntityInside(this.world, blockpos, this, d0, p_213290_1_, true);
         }
      }
   }

   public void setInLava() {
      this.inLava = true;
   }

   public boolean isInLava() {
      return this.inLava;
   }

   public void moveRelative(float p_213309_1_, Vec3d p_213309_2_) {
      Vec3d vec3d = getAbsoluteMotion(p_213309_2_, p_213309_1_, this.rotationYaw);
      this.setMotion(this.getMotion().add(vec3d));
   }

   private static Vec3d getAbsoluteMotion(Vec3d p_213299_0_, float p_213299_1_, float p_213299_2_) {
      double d0 = p_213299_0_.lengthSquared();
      if (d0 < 1.0E-7D) {
         return Vec3d.ZERO;
      } else {
         Vec3d vec3d = (d0 > 1.0D ? p_213299_0_.normalize() : p_213299_0_).scale((double)p_213299_1_);
         float f = MathHelper.sin(p_213299_2_ * 0.017453292F);
         float f1 = MathHelper.cos(p_213299_2_ * 0.017453292F);
         return new Vec3d(vec3d.x * (double)f1 - vec3d.z * (double)f, vec3d.y, vec3d.z * (double)f1 + vec3d.x * (double)f);
      }
   }

   public float getBrightness() {
      BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable(this.func_226277_ct_(), 0.0D, this.func_226281_cx_());
      if (this.world.isBlockLoaded(blockpos$mutable)) {
         blockpos$mutable.setY(MathHelper.floor(this.func_226280_cw_()));
         return this.world.getBrightness(blockpos$mutable);
      } else {
         return 0.0F;
      }
   }

   public void setWorld(World p_70029_1_) {
      this.world = p_70029_1_;
   }

   public void setPositionAndRotation(double p_70080_1_, double p_70080_3_, double p_70080_5_, float p_70080_7_, float p_70080_8_) {
      double d0 = MathHelper.clamp(p_70080_1_, -3.0E7D, 3.0E7D);
      double d1 = MathHelper.clamp(p_70080_5_, -3.0E7D, 3.0E7D);
      this.prevPosX = d0;
      this.prevPosY = p_70080_3_;
      this.prevPosZ = d1;
      this.setPosition(d0, p_70080_3_, d1);
      this.rotationYaw = p_70080_7_ % 360.0F;
      this.rotationPitch = MathHelper.clamp(p_70080_8_, -90.0F, 90.0F) % 360.0F;
      this.prevRotationYaw = this.rotationYaw;
      this.prevRotationPitch = this.rotationPitch;
   }

   public void moveToBlockPosAndAngles(BlockPos p_174828_1_, float p_174828_2_, float p_174828_3_) {
      this.setLocationAndAngles((double)p_174828_1_.getX() + 0.5D, (double)p_174828_1_.getY(), (double)p_174828_1_.getZ() + 0.5D, p_174828_2_, p_174828_3_);
   }

   public void setLocationAndAngles(double p_70012_1_, double p_70012_3_, double p_70012_5_, float p_70012_7_, float p_70012_8_) {
      this.func_226286_f_(p_70012_1_, p_70012_3_, p_70012_5_);
      this.rotationYaw = p_70012_7_;
      this.rotationPitch = p_70012_8_;
      this.func_226264_Z_();
   }

   public void func_226286_f_(double p_226286_1_, double p_226286_3_, double p_226286_5_) {
      this.func_226288_n_(p_226286_1_, p_226286_3_, p_226286_5_);
      this.prevPosX = p_226286_1_;
      this.prevPosY = p_226286_3_;
      this.prevPosZ = p_226286_5_;
      this.lastTickPosX = p_226286_1_;
      this.lastTickPosY = p_226286_3_;
      this.lastTickPosZ = p_226286_5_;
   }

   public float getDistance(Entity p_70032_1_) {
      float f = (float)(this.func_226277_ct_() - p_70032_1_.func_226277_ct_());
      float f1 = (float)(this.func_226278_cu_() - p_70032_1_.func_226278_cu_());
      float f2 = (float)(this.func_226281_cx_() - p_70032_1_.func_226281_cx_());
      return MathHelper.sqrt(f * f + f1 * f1 + f2 * f2);
   }

   public double getDistanceSq(double p_70092_1_, double p_70092_3_, double p_70092_5_) {
      double d0 = this.func_226277_ct_() - p_70092_1_;
      double d1 = this.func_226278_cu_() - p_70092_3_;
      double d2 = this.func_226281_cx_() - p_70092_5_;
      return d0 * d0 + d1 * d1 + d2 * d2;
   }

   public double getDistanceSq(Entity p_70068_1_) {
      return this.getDistanceSq(p_70068_1_.getPositionVec());
   }

   public double getDistanceSq(Vec3d p_195048_1_) {
      double d0 = this.func_226277_ct_() - p_195048_1_.x;
      double d1 = this.func_226278_cu_() - p_195048_1_.y;
      double d2 = this.func_226281_cx_() - p_195048_1_.z;
      return d0 * d0 + d1 * d1 + d2 * d2;
   }

   public void onCollideWithPlayer(PlayerEntity p_70100_1_) {
   }

   public void applyEntityCollision(Entity p_70108_1_) {
      if (!this.isRidingSameEntity(p_70108_1_) && !p_70108_1_.noClip && !this.noClip) {
         double d0 = p_70108_1_.func_226277_ct_() - this.func_226277_ct_();
         double d1 = p_70108_1_.func_226281_cx_() - this.func_226281_cx_();
         double d2 = MathHelper.absMax(d0, d1);
         if (d2 >= 0.009999999776482582D) {
            d2 = (double)MathHelper.sqrt(d2);
            d0 /= d2;
            d1 /= d2;
            double d3 = 1.0D / d2;
            if (d3 > 1.0D) {
               d3 = 1.0D;
            }

            d0 *= d3;
            d1 *= d3;
            d0 *= 0.05000000074505806D;
            d1 *= 0.05000000074505806D;
            d0 *= (double)(1.0F - this.entityCollisionReduction);
            d1 *= (double)(1.0F - this.entityCollisionReduction);
            if (!this.isBeingRidden()) {
               this.addVelocity(-d0, 0.0D, -d1);
            }

            if (!p_70108_1_.isBeingRidden()) {
               p_70108_1_.addVelocity(d0, 0.0D, d1);
            }
         }
      }

   }

   public void addVelocity(double p_70024_1_, double p_70024_3_, double p_70024_5_) {
      this.setMotion(this.getMotion().add(p_70024_1_, p_70024_3_, p_70024_5_));
      this.isAirBorne = true;
   }

   protected void markVelocityChanged() {
      this.velocityChanged = true;
   }

   public boolean attackEntityFrom(DamageSource p_70097_1_, float p_70097_2_) {
      if (this.isInvulnerableTo(p_70097_1_)) {
         return false;
      } else {
         this.markVelocityChanged();
         return false;
      }
   }

   public final Vec3d getLook(float p_70676_1_) {
      return this.getVectorForRotation(this.getPitch(p_70676_1_), this.getYaw(p_70676_1_));
   }

   public float getPitch(float p_195050_1_) {
      return p_195050_1_ == 1.0F ? this.rotationPitch : MathHelper.lerp(p_195050_1_, this.prevRotationPitch, this.rotationPitch);
   }

   public float getYaw(float p_195046_1_) {
      return p_195046_1_ == 1.0F ? this.rotationYaw : MathHelper.lerp(p_195046_1_, this.prevRotationYaw, this.rotationYaw);
   }

   protected final Vec3d getVectorForRotation(float p_174806_1_, float p_174806_2_) {
      float f = p_174806_1_ * 0.017453292F;
      float f1 = -p_174806_2_ * 0.017453292F;
      float f2 = MathHelper.cos(f1);
      float f3 = MathHelper.sin(f1);
      float f4 = MathHelper.cos(f);
      float f5 = MathHelper.sin(f);
      return new Vec3d((double)(f3 * f4), (double)(-f5), (double)(f2 * f4));
   }

   public final Vec3d func_213286_i(float p_213286_1_) {
      return this.func_213320_d(this.getPitch(p_213286_1_), this.getYaw(p_213286_1_));
   }

   protected final Vec3d func_213320_d(float p_213320_1_, float p_213320_2_) {
      return this.getVectorForRotation(p_213320_1_ - 90.0F, p_213320_2_);
   }

   public final Vec3d getEyePosition(float p_174824_1_) {
      if (p_174824_1_ == 1.0F) {
         return new Vec3d(this.func_226277_ct_(), this.func_226280_cw_(), this.func_226281_cx_());
      } else {
         double d0 = MathHelper.lerp((double)p_174824_1_, this.prevPosX, this.func_226277_ct_());
         double d1 = MathHelper.lerp((double)p_174824_1_, this.prevPosY, this.func_226278_cu_()) + (double)this.getEyeHeight();
         double d2 = MathHelper.lerp((double)p_174824_1_, this.prevPosZ, this.func_226281_cx_());
         return new Vec3d(d0, d1, d2);
      }
   }

   public RayTraceResult func_213324_a(double p_213324_1_, float p_213324_3_, boolean p_213324_4_) {
      Vec3d vec3d = this.getEyePosition(p_213324_3_);
      Vec3d vec3d1 = this.getLook(p_213324_3_);
      Vec3d vec3d2 = vec3d.add(vec3d1.x * p_213324_1_, vec3d1.y * p_213324_1_, vec3d1.z * p_213324_1_);
      return this.world.rayTraceBlocks(new RayTraceContext(vec3d, vec3d2, RayTraceContext.BlockMode.OUTLINE, p_213324_4_ ? RayTraceContext.FluidMode.ANY : RayTraceContext.FluidMode.NONE, this));
   }

   public boolean canBeCollidedWith() {
      return false;
   }

   public boolean canBePushed() {
      return false;
   }

   public void awardKillScore(Entity p_191956_1_, int p_191956_2_, DamageSource p_191956_3_) {
      if (p_191956_1_ instanceof ServerPlayerEntity) {
         CriteriaTriggers.ENTITY_KILLED_PLAYER.trigger((ServerPlayerEntity)p_191956_1_, this, p_191956_3_);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public boolean isInRangeToRender3d(double p_145770_1_, double p_145770_3_, double p_145770_5_) {
      double d0 = this.func_226277_ct_() - p_145770_1_;
      double d1 = this.func_226278_cu_() - p_145770_3_;
      double d2 = this.func_226281_cx_() - p_145770_5_;
      double d3 = d0 * d0 + d1 * d1 + d2 * d2;
      return this.isInRangeToRenderDist(d3);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isInRangeToRenderDist(double p_70112_1_) {
      double d0 = this.getBoundingBox().getAverageEdgeLength();
      if (Double.isNaN(d0)) {
         d0 = 1.0D;
      }

      d0 = d0 * 64.0D * renderDistanceWeight;
      return p_70112_1_ < d0 * d0;
   }

   public boolean writeUnlessRemoved(CompoundNBT p_184198_1_) {
      String s = this.getEntityString();
      if (!this.removed && s != null) {
         p_184198_1_.putString("id", s);
         this.writeWithoutTypeId(p_184198_1_);
         return true;
      } else {
         return false;
      }
   }

   public boolean writeUnlessPassenger(CompoundNBT p_70039_1_) {
      return this.isPassenger() ? false : this.writeUnlessRemoved(p_70039_1_);
   }

   public CompoundNBT writeWithoutTypeId(CompoundNBT p_189511_1_) {
      try {
         p_189511_1_.put("Pos", this.newDoubleNBTList(this.func_226277_ct_(), this.func_226278_cu_(), this.func_226281_cx_()));
         Vec3d vec3d = this.getMotion();
         p_189511_1_.put("Motion", this.newDoubleNBTList(vec3d.x, vec3d.y, vec3d.z));
         p_189511_1_.put("Rotation", this.newFloatNBTList(this.rotationYaw, this.rotationPitch));
         p_189511_1_.putFloat("FallDistance", this.fallDistance);
         p_189511_1_.putShort("Fire", (short)this.fire);
         p_189511_1_.putShort("Air", (short)this.getAir());
         p_189511_1_.putBoolean("OnGround", this.onGround);
         p_189511_1_.putInt("Dimension", this.dimension.getId());
         p_189511_1_.putBoolean("Invulnerable", this.invulnerable);
         p_189511_1_.putInt("PortalCooldown", this.timeUntilPortal);
         p_189511_1_.putUniqueId("UUID", this.getUniqueID());
         ITextComponent itextcomponent = this.getCustomName();
         if (itextcomponent != null) {
            p_189511_1_.putString("CustomName", ITextComponent.Serializer.toJson(itextcomponent));
         }

         if (this.isCustomNameVisible()) {
            p_189511_1_.putBoolean("CustomNameVisible", this.isCustomNameVisible());
         }

         if (this.isSilent()) {
            p_189511_1_.putBoolean("Silent", this.isSilent());
         }

         if (this.hasNoGravity()) {
            p_189511_1_.putBoolean("NoGravity", this.hasNoGravity());
         }

         if (this.glowing) {
            p_189511_1_.putBoolean("Glowing", this.glowing);
         }

         p_189511_1_.putBoolean("CanUpdate", this.canUpdate);
         if (!this.tags.isEmpty()) {
            ListNBT listnbt = new ListNBT();
            Iterator var5 = this.tags.iterator();

            while(var5.hasNext()) {
               String s = (String)var5.next();
               listnbt.add(StringNBT.func_229705_a_(s));
            }

            p_189511_1_.put("Tags", listnbt);
         }

         CompoundNBT caps = this.serializeCaps();
         if (caps != null) {
            p_189511_1_.put("ForgeCaps", caps);
         }

         if (this.persistentData != null) {
            p_189511_1_.put("ForgeData", this.persistentData);
         }

         this.writeAdditional(p_189511_1_);
         if (this.isBeingRidden()) {
            ListNBT listnbt1 = new ListNBT();
            Iterator var14 = this.getPassengers().iterator();

            while(var14.hasNext()) {
               Entity entity = (Entity)var14.next();
               CompoundNBT compoundnbt = new CompoundNBT();
               if (entity.writeUnlessRemoved(compoundnbt)) {
                  listnbt1.add(compoundnbt);
               }
            }

            if (!listnbt1.isEmpty()) {
               p_189511_1_.put("Passengers", listnbt1);
            }
         }

         return p_189511_1_;
      } catch (Throwable var9) {
         CrashReport crashreport = CrashReport.makeCrashReport(var9, "Saving entity NBT");
         CrashReportCategory crashreportcategory = crashreport.makeCategory("Entity being saved");
         this.fillCrashReport(crashreportcategory);
         throw new ReportedException(crashreport);
      }
   }

   public void read(CompoundNBT p_70020_1_) {
      try {
         ListNBT listnbt = p_70020_1_.getList("Pos", 6);
         ListNBT listnbt2 = p_70020_1_.getList("Motion", 6);
         ListNBT listnbt3 = p_70020_1_.getList("Rotation", 5);
         double d0 = listnbt2.getDouble(0);
         double d1 = listnbt2.getDouble(1);
         double d2 = listnbt2.getDouble(2);
         this.setMotion(Math.abs(d0) > 10.0D ? 0.0D : d0, Math.abs(d1) > 10.0D ? 0.0D : d1, Math.abs(d2) > 10.0D ? 0.0D : d2);
         this.func_226286_f_(listnbt.getDouble(0), listnbt.getDouble(1), listnbt.getDouble(2));
         this.rotationYaw = listnbt3.getFloat(0);
         this.rotationPitch = listnbt3.getFloat(1);
         this.prevRotationYaw = this.rotationYaw;
         this.prevRotationPitch = this.rotationPitch;
         this.setRotationYawHead(this.rotationYaw);
         this.setRenderYawOffset(this.rotationYaw);
         this.fallDistance = p_70020_1_.getFloat("FallDistance");
         this.fire = p_70020_1_.getShort("Fire");
         this.setAir(p_70020_1_.getShort("Air"));
         this.onGround = p_70020_1_.getBoolean("OnGround");
         if (p_70020_1_.contains("Dimension")) {
            this.dimension = DimensionType.getById(p_70020_1_.getInt("Dimension"));
         }

         this.invulnerable = p_70020_1_.getBoolean("Invulnerable");
         this.timeUntilPortal = p_70020_1_.getInt("PortalCooldown");
         if (p_70020_1_.hasUniqueId("UUID")) {
            this.entityUniqueID = p_70020_1_.getUniqueId("UUID");
            this.cachedUniqueIdString = this.entityUniqueID.toString();
         }

         if (Double.isFinite(this.func_226277_ct_()) && Double.isFinite(this.func_226278_cu_()) && Double.isFinite(this.func_226281_cx_())) {
            if (Double.isFinite((double)this.rotationYaw) && Double.isFinite((double)this.rotationPitch)) {
               this.func_226264_Z_();
               this.setRotation(this.rotationYaw, this.rotationPitch);
               if (p_70020_1_.contains("CustomName", 8)) {
                  this.setCustomName(ITextComponent.Serializer.fromJson(p_70020_1_.getString("CustomName")));
               }

               this.setCustomNameVisible(p_70020_1_.getBoolean("CustomNameVisible"));
               this.setSilent(p_70020_1_.getBoolean("Silent"));
               this.setNoGravity(p_70020_1_.getBoolean("NoGravity"));
               this.setGlowing(p_70020_1_.getBoolean("Glowing"));
               if (p_70020_1_.contains("ForgeData", 10)) {
                  this.persistentData = p_70020_1_.getCompound("ForgeData");
               }

               if (p_70020_1_.contains("CanUpdate", 99)) {
                  this.canUpdate(p_70020_1_.getBoolean("CanUpdate"));
               }

               if (p_70020_1_.contains("ForgeCaps", 10)) {
                  this.deserializeCaps(p_70020_1_.getCompound("ForgeCaps"));
               }

               if (p_70020_1_.contains("Tags", 9)) {
                  this.tags.clear();
                  ListNBT listnbt1 = p_70020_1_.getList("Tags", 8);
                  int i = Math.min(listnbt1.size(), 1024);

                  for(int j = 0; j < i; ++j) {
                     this.tags.add(listnbt1.getString(j));
                  }
               }

               this.readAdditional(p_70020_1_);
               if (this.shouldSetPosAfterLoading()) {
                  this.func_226264_Z_();
               }

            } else {
               throw new IllegalStateException("Entity has invalid rotation");
            }
         } else {
            throw new IllegalStateException("Entity has invalid position");
         }
      } catch (Throwable var14) {
         CrashReport crashreport = CrashReport.makeCrashReport(var14, "Loading entity NBT");
         CrashReportCategory crashreportcategory = crashreport.makeCategory("Entity being loaded");
         this.fillCrashReport(crashreportcategory);
         throw new ReportedException(crashreport);
      }
   }

   protected boolean shouldSetPosAfterLoading() {
      return true;
   }

   @Nullable
   public final String getEntityString() {
      EntityType<?> entitytype = this.getType();
      ResourceLocation resourcelocation = EntityType.getKey(entitytype);
      return entitytype.isSerializable() && resourcelocation != null ? resourcelocation.toString() : null;
   }

   protected abstract void readAdditional(CompoundNBT var1);

   protected abstract void writeAdditional(CompoundNBT var1);

   protected ListNBT newDoubleNBTList(double... p_70087_1_) {
      ListNBT listnbt = new ListNBT();
      double[] var3 = p_70087_1_;
      int var4 = p_70087_1_.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         double d0 = var3[var5];
         listnbt.add(DoubleNBT.func_229684_a_(d0));
      }

      return listnbt;
   }

   protected ListNBT newFloatNBTList(float... p_70049_1_) {
      ListNBT listnbt = new ListNBT();
      float[] var3 = p_70049_1_;
      int var4 = p_70049_1_.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         float f = var3[var5];
         listnbt.add(FloatNBT.func_229689_a_(f));
      }

      return listnbt;
   }

   @Nullable
   public ItemEntity entityDropItem(IItemProvider p_199703_1_) {
      return this.entityDropItem(p_199703_1_, 0);
   }

   @Nullable
   public ItemEntity entityDropItem(IItemProvider p_199702_1_, int p_199702_2_) {
      return this.entityDropItem(new ItemStack(p_199702_1_), (float)p_199702_2_);
   }

   @Nullable
   public ItemEntity entityDropItem(ItemStack p_199701_1_) {
      return this.entityDropItem(p_199701_1_, 0.0F);
   }

   @Nullable
   public ItemEntity entityDropItem(ItemStack p_70099_1_, float p_70099_2_) {
      if (p_70099_1_.isEmpty()) {
         return null;
      } else if (this.world.isRemote) {
         return null;
      } else {
         ItemEntity itementity = new ItemEntity(this.world, this.func_226277_ct_(), this.func_226278_cu_() + (double)p_70099_2_, this.func_226281_cx_(), p_70099_1_);
         itementity.setDefaultPickupDelay();
         if (this.captureDrops() != null) {
            this.captureDrops().add(itementity);
         } else {
            this.world.addEntity(itementity);
         }

         return itementity;
      }
   }

   public boolean isAlive() {
      return !this.removed;
   }

   public boolean isEntityInsideOpaqueBlock() {
      if (this.noClip) {
         return false;
      } else {
         BlockPos.PooledMutable blockpos$pooledmutable = BlockPos.PooledMutable.retain();
         Throwable var2 = null;

         boolean var20;
         try {
            for(int i = 0; i < 8; ++i) {
               int j = MathHelper.floor(this.func_226278_cu_() + (double)(((float)((i >> 0) % 2) - 0.5F) * 0.1F) + (double)this.eyeHeight);
               int k = MathHelper.floor(this.func_226277_ct_() + (double)(((float)((i >> 1) % 2) - 0.5F) * this.size.width * 0.8F));
               int l = MathHelper.floor(this.func_226281_cx_() + (double)(((float)((i >> 2) % 2) - 0.5F) * this.size.width * 0.8F));
               if (blockpos$pooledmutable.getX() != k || blockpos$pooledmutable.getY() != j || blockpos$pooledmutable.getZ() != l) {
                  blockpos$pooledmutable.setPos(k, j, l);
                  if (this.world.getBlockState(blockpos$pooledmutable).func_229980_m_(this.world, blockpos$pooledmutable)) {
                     boolean flag = true;
                     boolean var8 = flag;
                     return var8;
                  }
               }
            }

            var20 = false;
         } catch (Throwable var18) {
            var2 = var18;
            throw var18;
         } finally {
            if (blockpos$pooledmutable != null) {
               if (var2 != null) {
                  try {
                     blockpos$pooledmutable.close();
                  } catch (Throwable var17) {
                     var2.addSuppressed(var17);
                  }
               } else {
                  blockpos$pooledmutable.close();
               }
            }

         }

         return var20;
      }
   }

   public boolean processInitialInteract(PlayerEntity p_184230_1_, Hand p_184230_2_) {
      return false;
   }

   @Nullable
   public AxisAlignedBB getCollisionBox(Entity p_70114_1_) {
      return null;
   }

   public void updateRidden() {
      this.setMotion(Vec3d.ZERO);
      if (this.canUpdate()) {
         this.tick();
      }

      if (this.isPassenger()) {
         this.getRidingEntity().updatePassenger(this);
      }

   }

   public void updatePassenger(Entity p_184232_1_) {
      this.func_226266_a_(p_184232_1_, Entity::setPosition);
   }

   public void func_226266_a_(Entity p_226266_1_, Entity.IMoveCallback p_226266_2_) {
      if (this.isPassenger(p_226266_1_)) {
         p_226266_2_.accept(p_226266_1_, this.func_226277_ct_(), this.func_226278_cu_() + this.getMountedYOffset() + p_226266_1_.getYOffset(), this.func_226281_cx_());
      }

   }

   @OnlyIn(Dist.CLIENT)
   public void applyOrientationToEntity(Entity p_184190_1_) {
   }

   public double getYOffset() {
      return 0.0D;
   }

   public double getMountedYOffset() {
      return (double)this.size.height * 0.75D;
   }

   public boolean startRiding(Entity p_184220_1_) {
      return this.startRiding(p_184220_1_, false);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isLiving() {
      return this instanceof LivingEntity;
   }

   public boolean startRiding(Entity p_184205_1_, boolean p_184205_2_) {
      for(Entity entity = p_184205_1_; entity.ridingEntity != null; entity = entity.ridingEntity) {
         if (entity.ridingEntity == this) {
            return false;
         }
      }

      if (!ForgeEventFactory.canMountEntity(this, p_184205_1_, true)) {
         return false;
      } else if (!p_184205_2_ && (!this.canBeRidden(p_184205_1_) || !p_184205_1_.canFitPassenger(this))) {
         return false;
      } else {
         if (this.isPassenger()) {
            this.stopRiding();
         }

         this.ridingEntity = p_184205_1_;
         this.ridingEntity.addPassenger(this);
         return true;
      }
   }

   protected boolean canBeRidden(Entity p_184228_1_) {
      return this.rideCooldown <= 0;
   }

   protected boolean isPoseClear(Pose p_213298_1_) {
      return this.world.func_226665_a__(this, this.getBoundingBox(p_213298_1_));
   }

   public void removePassengers() {
      for(int i = this.passengers.size() - 1; i >= 0; --i) {
         ((Entity)this.passengers.get(i)).stopRiding();
      }

   }

   public void stopRiding() {
      if (this.ridingEntity != null) {
         Entity entity = this.ridingEntity;
         if (!ForgeEventFactory.canMountEntity(this, entity, false)) {
            return;
         }

         this.ridingEntity = null;
         entity.removePassenger(this);
      }

   }

   protected void addPassenger(Entity p_184200_1_) {
      if (p_184200_1_.getRidingEntity() != this) {
         throw new IllegalStateException("Use x.startRiding(y), not y.addPassenger(x)");
      } else {
         if (!this.world.isRemote && p_184200_1_ instanceof PlayerEntity && !(this.getControllingPassenger() instanceof PlayerEntity)) {
            this.passengers.add(0, p_184200_1_);
         } else {
            this.passengers.add(p_184200_1_);
         }

      }
   }

   protected void removePassenger(Entity p_184225_1_) {
      if (p_184225_1_.getRidingEntity() == this) {
         throw new IllegalStateException("Use x.stopRiding(y), not y.removePassenger(x)");
      } else {
         this.passengers.remove(p_184225_1_);
         p_184225_1_.rideCooldown = 60;
      }
   }

   protected boolean canFitPassenger(Entity p_184219_1_) {
      return this.getPassengers().size() < 1;
   }

   @OnlyIn(Dist.CLIENT)
   public void setPositionAndRotationDirect(double p_180426_1_, double p_180426_3_, double p_180426_5_, float p_180426_7_, float p_180426_8_, int p_180426_9_, boolean p_180426_10_) {
      this.setPosition(p_180426_1_, p_180426_3_, p_180426_5_);
      this.setRotation(p_180426_7_, p_180426_8_);
   }

   @OnlyIn(Dist.CLIENT)
   public void setHeadRotation(float p_208000_1_, int p_208000_2_) {
      this.setRotationYawHead(p_208000_1_);
   }

   public float getCollisionBorderSize() {
      return 0.0F;
   }

   public Vec3d getLookVec() {
      return this.getVectorForRotation(this.rotationPitch, this.rotationYaw);
   }

   public Vec2f getPitchYaw() {
      return new Vec2f(this.rotationPitch, this.rotationYaw);
   }

   @OnlyIn(Dist.CLIENT)
   public Vec3d getForward() {
      return Vec3d.fromPitchYaw(this.getPitchYaw());
   }

   public void setPortal(BlockPos p_181015_1_) {
      if (this.timeUntilPortal > 0) {
         this.timeUntilPortal = this.getPortalCooldown();
      } else {
         if (!this.world.isRemote && !p_181015_1_.equals(this.lastPortalPos)) {
            this.lastPortalPos = new BlockPos(p_181015_1_);
            NetherPortalBlock netherportalblock = (NetherPortalBlock)Blocks.NETHER_PORTAL;
            BlockPattern.PatternHelper blockpattern$patternhelper = NetherPortalBlock.createPatternHelper(this.world, this.lastPortalPos);
            double d0 = blockpattern$patternhelper.getForwards().getAxis() == Direction.Axis.X ? (double)blockpattern$patternhelper.getFrontTopLeft().getZ() : (double)blockpattern$patternhelper.getFrontTopLeft().getX();
            double d1 = Math.abs(MathHelper.pct((blockpattern$patternhelper.getForwards().getAxis() == Direction.Axis.X ? this.func_226281_cx_() : this.func_226277_ct_()) - (double)(blockpattern$patternhelper.getForwards().rotateY().getAxisDirection() == Direction.AxisDirection.NEGATIVE ? 1 : 0), d0, d0 - (double)blockpattern$patternhelper.getWidth()));
            double d2 = MathHelper.pct(this.func_226278_cu_() - 1.0D, (double)blockpattern$patternhelper.getFrontTopLeft().getY(), (double)(blockpattern$patternhelper.getFrontTopLeft().getY() - blockpattern$patternhelper.getHeight()));
            this.lastPortalVec = new Vec3d(d1, d2, 0.0D);
            this.teleportDirection = blockpattern$patternhelper.getForwards();
         }

         this.inPortal = true;
      }

   }

   protected void updatePortal() {
      if (this.world instanceof ServerWorld) {
         int i = this.getMaxInPortalTime();
         if (this.inPortal) {
            if (this.world.getServer().getAllowNether() && !this.isPassenger() && this.portalCounter++ >= i) {
               this.world.getProfiler().startSection("portal");
               this.portalCounter = i;
               this.timeUntilPortal = this.getPortalCooldown();
               this.changeDimension(this.world.dimension.getType() == DimensionType.THE_NETHER ? DimensionType.OVERWORLD : DimensionType.THE_NETHER);
               this.world.getProfiler().endSection();
            }

            this.inPortal = false;
         } else {
            if (this.portalCounter > 0) {
               this.portalCounter -= 4;
            }

            if (this.portalCounter < 0) {
               this.portalCounter = 0;
            }
         }

         this.decrementTimeUntilPortal();
      }

   }

   public int getPortalCooldown() {
      return 300;
   }

   @OnlyIn(Dist.CLIENT)
   public void setVelocity(double p_70016_1_, double p_70016_3_, double p_70016_5_) {
      this.setMotion(p_70016_1_, p_70016_3_, p_70016_5_);
   }

   @OnlyIn(Dist.CLIENT)
   public void handleStatusUpdate(byte p_70103_1_) {
      switch(p_70103_1_) {
      case 53:
         HoneyBlock.func_226931_a_(this);
      default:
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void performHurtAnimation() {
   }

   public Iterable<ItemStack> getHeldEquipment() {
      return EMPTY_EQUIPMENT;
   }

   public Iterable<ItemStack> getArmorInventoryList() {
      return EMPTY_EQUIPMENT;
   }

   public Iterable<ItemStack> getEquipmentAndArmor() {
      return Iterables.concat(this.getHeldEquipment(), this.getArmorInventoryList());
   }

   public void setItemStackToSlot(EquipmentSlotType p_184201_1_, ItemStack p_184201_2_) {
   }

   public boolean isBurning() {
      boolean flag = this.world != null && this.world.isRemote;
      return !this.isImmuneToFire() && (this.fire > 0 || flag && this.getFlag(0));
   }

   public boolean isPassenger() {
      return this.getRidingEntity() != null;
   }

   public boolean isBeingRidden() {
      return !this.getPassengers().isEmpty();
   }

   /** @deprecated */
   @Deprecated
   public boolean canBeRiddenInWater() {
      return true;
   }

   public void func_226284_e_(boolean p_226284_1_) {
      this.setFlag(1, p_226284_1_);
   }

   public boolean func_225608_bj_() {
      return this.getFlag(1);
   }

   public boolean func_226271_bk_() {
      return this.func_225608_bj_();
   }

   public boolean func_226272_bl_() {
      return this.func_225608_bj_();
   }

   public boolean func_226273_bm_() {
      return this.func_225608_bj_();
   }

   public boolean func_226274_bn_() {
      return this.func_225608_bj_();
   }

   public boolean isCrouching() {
      return this.getPose() == Pose.CROUCHING;
   }

   public boolean isSprinting() {
      return this.getFlag(3);
   }

   public void setSprinting(boolean p_70031_1_) {
      this.setFlag(3, p_70031_1_);
   }

   public boolean isSwimming() {
      return this.getFlag(4);
   }

   public boolean func_213314_bj() {
      return this.getPose() == Pose.SWIMMING;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean func_213300_bk() {
      return this.func_213314_bj() && !this.isInWater();
   }

   public void setSwimming(boolean p_204711_1_) {
      this.setFlag(4, p_204711_1_);
   }

   public boolean func_225510_bt_() {
      return this.glowing || this.world.isRemote && this.getFlag(6);
   }

   public void setGlowing(boolean p_184195_1_) {
      this.glowing = p_184195_1_;
      if (!this.world.isRemote) {
         this.setFlag(6, this.glowing);
      }

   }

   public boolean isInvisible() {
      return this.getFlag(5);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isInvisibleToPlayer(PlayerEntity p_98034_1_) {
      if (p_98034_1_.isSpectator()) {
         return false;
      } else {
         Team team = this.getTeam();
         return team != null && p_98034_1_ != null && p_98034_1_.getTeam() == team && team.getSeeFriendlyInvisiblesEnabled() ? false : this.isInvisible();
      }
   }

   @Nullable
   public Team getTeam() {
      return this.world.getScoreboard().getPlayersTeam(this.getScoreboardName());
   }

   public boolean isOnSameTeam(Entity p_184191_1_) {
      return this.isOnScoreboardTeam(p_184191_1_.getTeam());
   }

   public boolean isOnScoreboardTeam(Team p_184194_1_) {
      return this.getTeam() != null ? this.getTeam().isSameTeam(p_184194_1_) : false;
   }

   public void setInvisible(boolean p_82142_1_) {
      this.setFlag(5, p_82142_1_);
   }

   protected boolean getFlag(int p_70083_1_) {
      return ((Byte)this.dataManager.get(FLAGS) & 1 << p_70083_1_) != 0;
   }

   protected void setFlag(int p_70052_1_, boolean p_70052_2_) {
      byte b0 = (Byte)this.dataManager.get(FLAGS);
      if (p_70052_2_) {
         this.dataManager.set(FLAGS, (byte)(b0 | 1 << p_70052_1_));
      } else {
         this.dataManager.set(FLAGS, (byte)(b0 & ~(1 << p_70052_1_)));
      }

   }

   public int getMaxAir() {
      return 300;
   }

   public int getAir() {
      return (Integer)this.dataManager.get(AIR);
   }

   public void setAir(int p_70050_1_) {
      this.dataManager.set(AIR, p_70050_1_);
   }

   public void onStruckByLightning(LightningBoltEntity p_70077_1_) {
      ++this.fire;
      if (this.fire == 0) {
         this.setFire(8);
      }

      this.attackEntityFrom(DamageSource.LIGHTNING_BOLT, 5.0F);
   }

   public void onEnterBubbleColumnWithAirAbove(boolean p_203002_1_) {
      Vec3d vec3d = this.getMotion();
      double d0;
      if (p_203002_1_) {
         d0 = Math.max(-0.9D, vec3d.y - 0.03D);
      } else {
         d0 = Math.min(1.8D, vec3d.y + 0.1D);
      }

      this.setMotion(vec3d.x, d0, vec3d.z);
   }

   public void onEnterBubbleColumn(boolean p_203004_1_) {
      Vec3d vec3d = this.getMotion();
      double d0;
      if (p_203004_1_) {
         d0 = Math.max(-0.3D, vec3d.y - 0.03D);
      } else {
         d0 = Math.min(0.7D, vec3d.y + 0.06D);
      }

      this.setMotion(vec3d.x, d0, vec3d.z);
      this.fallDistance = 0.0F;
   }

   public void onKillEntity(LivingEntity p_70074_1_) {
   }

   protected void pushOutOfBlocks(double p_213282_1_, double p_213282_3_, double p_213282_5_) {
      BlockPos blockpos = new BlockPos(p_213282_1_, p_213282_3_, p_213282_5_);
      Vec3d vec3d = new Vec3d(p_213282_1_ - (double)blockpos.getX(), p_213282_3_ - (double)blockpos.getY(), p_213282_5_ - (double)blockpos.getZ());
      BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
      Direction direction = Direction.UP;
      double d0 = Double.MAX_VALUE;
      Direction[] var13 = new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST, Direction.UP};
      int var14 = var13.length;

      for(int var15 = 0; var15 < var14; ++var15) {
         Direction direction1 = var13[var15];
         blockpos$mutable.setPos((Vec3i)blockpos).move(direction1);
         if (!this.world.getBlockState(blockpos$mutable).func_224756_o(this.world, blockpos$mutable)) {
            double d1 = vec3d.getCoordinate(direction1.getAxis());
            double d2 = direction1.getAxisDirection() == Direction.AxisDirection.POSITIVE ? 1.0D - d1 : d1;
            if (d2 < d0) {
               d0 = d2;
               direction = direction1;
            }
         }
      }

      float f = this.rand.nextFloat() * 0.2F + 0.1F;
      float f1 = (float)direction.getAxisDirection().getOffset();
      Vec3d vec3d1 = this.getMotion().scale(0.75D);
      if (direction.getAxis() == Direction.Axis.X) {
         this.setMotion((double)(f1 * f), vec3d1.y, vec3d1.z);
      } else if (direction.getAxis() == Direction.Axis.Y) {
         this.setMotion(vec3d1.x, (double)(f1 * f), vec3d1.z);
      } else if (direction.getAxis() == Direction.Axis.Z) {
         this.setMotion(vec3d1.x, vec3d1.y, (double)(f1 * f));
      }

   }

   public void setMotionMultiplier(BlockState p_213295_1_, Vec3d p_213295_2_) {
      this.fallDistance = 0.0F;
      this.motionMultiplier = p_213295_2_;
   }

   private static void removeClickEvents(ITextComponent p_207712_0_) {
      p_207712_0_.applyTextStyle((p_lambda$removeClickEvents$0_0_) -> {
         p_lambda$removeClickEvents$0_0_.setClickEvent((ClickEvent)null);
      }).getSiblings().forEach(Entity::removeClickEvents);
   }

   public ITextComponent getName() {
      ITextComponent itextcomponent = this.getCustomName();
      if (itextcomponent != null) {
         ITextComponent itextcomponent1 = itextcomponent.deepCopy();
         removeClickEvents(itextcomponent1);
         return itextcomponent1;
      } else {
         return this.func_225513_by_();
      }
   }

   protected ITextComponent func_225513_by_() {
      return this.getType().getName();
   }

   public boolean isEntityEqual(Entity p_70028_1_) {
      return this == p_70028_1_;
   }

   public float getRotationYawHead() {
      return 0.0F;
   }

   public void setRotationYawHead(float p_70034_1_) {
   }

   public void setRenderYawOffset(float p_181013_1_) {
   }

   public boolean canBeAttackedWithItem() {
      return true;
   }

   public boolean hitByEntity(Entity p_85031_1_) {
      return false;
   }

   public String toString() {
      return String.format(Locale.ROOT, "%s['%s'/%d, l='%s', x=%.2f, y=%.2f, z=%.2f]", this.getClass().getSimpleName(), this.getName().getUnformattedComponentText(), this.entityId, this.world == null ? "~NULL~" : this.world.getWorldInfo().getWorldName(), this.func_226277_ct_(), this.func_226278_cu_(), this.func_226281_cx_());
   }

   public boolean isInvulnerableTo(DamageSource p_180431_1_) {
      return this.invulnerable && p_180431_1_ != DamageSource.OUT_OF_WORLD && !p_180431_1_.isCreativePlayer();
   }

   public boolean isInvulnerable() {
      return this.invulnerable;
   }

   public void setInvulnerable(boolean p_184224_1_) {
      this.invulnerable = p_184224_1_;
   }

   public void copyLocationAndAnglesFrom(Entity p_82149_1_) {
      this.setLocationAndAngles(p_82149_1_.func_226277_ct_(), p_82149_1_.func_226278_cu_(), p_82149_1_.func_226281_cx_(), p_82149_1_.rotationYaw, p_82149_1_.rotationPitch);
   }

   public void copyDataFromOld(Entity p_180432_1_) {
      CompoundNBT compoundnbt = p_180432_1_.writeWithoutTypeId(new CompoundNBT());
      compoundnbt.remove("Dimension");
      this.read(compoundnbt);
      this.timeUntilPortal = p_180432_1_.timeUntilPortal;
      this.lastPortalPos = p_180432_1_.lastPortalPos;
      this.lastPortalVec = p_180432_1_.lastPortalVec;
      this.teleportDirection = p_180432_1_.teleportDirection;
   }

   @Nullable
   public Entity changeDimension(DimensionType p_212321_1_) {
      return this.changeDimension(p_212321_1_, this.getServer().getWorld(p_212321_1_).getDefaultTeleporter());
   }

   @Nullable
   public Entity changeDimension(DimensionType p_changeDimension_1_, ITeleporter p_changeDimension_2_) {
      if (!ForgeHooks.onTravelToDimension(this, p_changeDimension_1_)) {
         return null;
      } else if (!this.world.isRemote && !this.removed) {
         this.world.getProfiler().startSection("changeDimension");
         MinecraftServer minecraftserver = this.getServer();
         DimensionType dimensiontype = this.dimension;
         ServerWorld serverworld = minecraftserver.getWorld(dimensiontype);
         ServerWorld serverworld1 = minecraftserver.getWorld(p_changeDimension_1_);
         this.dimension = p_changeDimension_1_;
         this.detach();
         this.world.getProfiler().startSection("reposition");
         Entity transportedEntity = p_changeDimension_2_.placeEntity(this, serverworld, serverworld1, this.rotationYaw, (p_lambda$changeDimension$1_5_) -> {
            Vec3d vec3d = this.getMotion();
            float f = 0.0F;
            BlockPos blockpos;
            if (dimensiontype == DimensionType.THE_END && p_changeDimension_1_ == DimensionType.OVERWORLD) {
               blockpos = serverworld1.getHeight(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, serverworld1.getSpawnPoint());
            } else if (p_changeDimension_1_ == DimensionType.THE_END) {
               blockpos = serverworld1.getSpawnCoordinate();
            } else {
               double movementFactor = serverworld.getDimension().getMovementFactor() / serverworld1.getDimension().getMovementFactor();
               double d0 = this.func_226277_ct_() * movementFactor;
               double d1 = this.func_226281_cx_() * movementFactor;
               double d3 = Math.min(-2.9999872E7D, serverworld1.getWorldBorder().minX() + 16.0D);
               double d4 = Math.min(-2.9999872E7D, serverworld1.getWorldBorder().minZ() + 16.0D);
               double d5 = Math.min(2.9999872E7D, serverworld1.getWorldBorder().maxX() - 16.0D);
               double d6 = Math.min(2.9999872E7D, serverworld1.getWorldBorder().maxZ() - 16.0D);
               d0 = MathHelper.clamp(d0, d3, d5);
               d1 = MathHelper.clamp(d1, d4, d6);
               Vec3d vec3d1 = this.getLastPortalVec();
               blockpos = new BlockPos(d0, this.func_226278_cu_(), d1);
               if (p_lambda$changeDimension$1_5_) {
                  BlockPattern.PortalInfo blockpattern$portalinfo = serverworld1.getDefaultTeleporter().func_222272_a(blockpos, vec3d, this.getTeleportDirection(), vec3d1.x, vec3d1.y, this instanceof PlayerEntity);
                  if (blockpattern$portalinfo == null) {
                     return null;
                  }

                  blockpos = new BlockPos(blockpattern$portalinfo.field_222505_a);
                  vec3d = blockpattern$portalinfo.field_222506_b;
                  f = (float)blockpattern$portalinfo.field_222507_c;
               }
            }

            this.world.getProfiler().endStartSection("reloading");
            Entity entity = this.getType().create(serverworld1);
            if (entity != null) {
               entity.copyDataFromOld(this);
               entity.moveToBlockPosAndAngles(blockpos, entity.rotationYaw + f, entity.rotationPitch);
               entity.setMotion(vec3d);
               serverworld1.func_217460_e(entity);
            }

            return entity;
         });
         this.remove(false);
         this.world.getProfiler().endSection();
         serverworld.resetUpdateEntityTick();
         serverworld1.resetUpdateEntityTick();
         this.world.getProfiler().endSection();
         return transportedEntity;
      } else {
         return null;
      }
   }

   public boolean isNonBoss() {
      return true;
   }

   public float getExplosionResistance(Explosion p_180428_1_, IBlockReader p_180428_2_, BlockPos p_180428_3_, BlockState p_180428_4_, IFluidState p_180428_5_, float p_180428_6_) {
      return p_180428_6_;
   }

   public boolean canExplosionDestroyBlock(Explosion p_174816_1_, IBlockReader p_174816_2_, BlockPos p_174816_3_, BlockState p_174816_4_, float p_174816_5_) {
      return true;
   }

   public int getMaxFallHeight() {
      return 3;
   }

   public Vec3d getLastPortalVec() {
      return this.lastPortalVec == null ? Vec3d.ZERO : this.lastPortalVec;
   }

   public Direction getTeleportDirection() {
      return this.teleportDirection == null ? Direction.NORTH : this.teleportDirection;
   }

   public boolean doesEntityNotTriggerPressurePlate() {
      return false;
   }

   public void fillCrashReport(CrashReportCategory p_85029_1_) {
      p_85029_1_.addDetail("Entity Type", () -> {
         return EntityType.getKey(this.getType()) + " (" + this.getClass().getCanonicalName() + ")";
      });
      p_85029_1_.addDetail("Entity ID", (Object)this.entityId);
      p_85029_1_.addDetail("Entity Name", () -> {
         return this.getName().getString();
      });
      p_85029_1_.addDetail("Entity's Exact location", (Object)String.format(Locale.ROOT, "%.2f, %.2f, %.2f", this.func_226277_ct_(), this.func_226278_cu_(), this.func_226281_cx_()));
      p_85029_1_.addDetail("Entity's Block location", (Object)CrashReportCategory.getCoordinateInfo(MathHelper.floor(this.func_226277_ct_()), MathHelper.floor(this.func_226278_cu_()), MathHelper.floor(this.func_226281_cx_())));
      Vec3d vec3d = this.getMotion();
      p_85029_1_.addDetail("Entity's Momentum", (Object)String.format(Locale.ROOT, "%.2f, %.2f, %.2f", vec3d.x, vec3d.y, vec3d.z));
      p_85029_1_.addDetail("Entity's Passengers", () -> {
         return this.getPassengers().toString();
      });
      p_85029_1_.addDetail("Entity's Vehicle", () -> {
         return this.getRidingEntity().toString();
      });
   }

   @OnlyIn(Dist.CLIENT)
   public boolean canRenderOnFire() {
      return this.isBurning() && !this.isSpectator();
   }

   public void setUniqueId(UUID p_184221_1_) {
      this.entityUniqueID = p_184221_1_;
      this.cachedUniqueIdString = this.entityUniqueID.toString();
   }

   public UUID getUniqueID() {
      return this.entityUniqueID;
   }

   public String getCachedUniqueIdString() {
      return this.cachedUniqueIdString;
   }

   public String getScoreboardName() {
      return this.cachedUniqueIdString;
   }

   public boolean isPushedByWater() {
      return true;
   }

   @OnlyIn(Dist.CLIENT)
   public static double getRenderDistanceWeight() {
      return renderDistanceWeight;
   }

   @OnlyIn(Dist.CLIENT)
   public static void setRenderDistanceWeight(double p_184227_0_) {
      renderDistanceWeight = p_184227_0_;
   }

   public ITextComponent getDisplayName() {
      return ScorePlayerTeam.formatMemberName(this.getTeam(), this.getName()).applyTextStyle((p_lambda$getDisplayName$6_1_) -> {
         p_lambda$getDisplayName$6_1_.setHoverEvent(this.getHoverEvent()).setInsertion(this.getCachedUniqueIdString());
      });
   }

   public void setCustomName(@Nullable ITextComponent p_200203_1_) {
      this.dataManager.set(CUSTOM_NAME, Optional.ofNullable(p_200203_1_));
   }

   @Nullable
   public ITextComponent getCustomName() {
      return (ITextComponent)((Optional)this.dataManager.get(CUSTOM_NAME)).orElse((ITextComponent)null);
   }

   public boolean hasCustomName() {
      return ((Optional)this.dataManager.get(CUSTOM_NAME)).isPresent();
   }

   public void setCustomNameVisible(boolean p_174805_1_) {
      this.dataManager.set(CUSTOM_NAME_VISIBLE, p_174805_1_);
   }

   public boolean isCustomNameVisible() {
      return (Boolean)this.dataManager.get(CUSTOM_NAME_VISIBLE);
   }

   public final void teleportKeepLoaded(double p_223102_1_, double p_223102_3_, double p_223102_5_) {
      if (this.world instanceof ServerWorld) {
         ChunkPos chunkpos = new ChunkPos(new BlockPos(p_223102_1_, p_223102_3_, p_223102_5_));
         ((ServerWorld)this.world).getChunkProvider().func_217228_a(TicketType.POST_TELEPORT, chunkpos, 0, this.getEntityId());
         this.world.getChunk(chunkpos.x, chunkpos.z);
         this.setPositionAndUpdate(p_223102_1_, p_223102_3_, p_223102_5_);
      }

   }

   public void setPositionAndUpdate(double p_70634_1_, double p_70634_3_, double p_70634_5_) {
      if (this.world instanceof ServerWorld) {
         ServerWorld serverworld = (ServerWorld)this.world;
         this.setLocationAndAngles(p_70634_1_, p_70634_3_, p_70634_5_, this.rotationYaw, this.rotationPitch);
         this.func_226276_cg_().forEach((p_lambda$setPositionAndUpdate$7_1_) -> {
            serverworld.chunkCheck(p_lambda$setPositionAndUpdate$7_1_);
            p_lambda$setPositionAndUpdate$7_1_.isPositionDirty = true;
            p_lambda$setPositionAndUpdate$7_1_.func_226265_a_(Entity::func_225653_b_);
         });
      }

   }

   @OnlyIn(Dist.CLIENT)
   public boolean getAlwaysRenderNameTagForRender() {
      return this.isCustomNameVisible();
   }

   public void notifyDataManagerChange(DataParameter<?> p_184206_1_) {
      if (POSE.equals(p_184206_1_)) {
         this.recalculateSize();
      }

   }

   public void recalculateSize() {
      EntitySize entitysize = this.size;
      Pose pose = this.getPose();
      EntitySize entitysize1 = this.getSize(pose);
      this.size = entitysize1;
      this.eyeHeight = this.getEyeHeightForge(pose, entitysize1);
      if (entitysize1.width < entitysize.width) {
         double d0 = (double)entitysize1.width / 2.0D;
         this.setBoundingBox(new AxisAlignedBB(this.func_226277_ct_() - d0, this.func_226278_cu_(), this.func_226281_cx_() - d0, this.func_226277_ct_() + d0, this.func_226278_cu_() + (double)entitysize1.height, this.func_226281_cx_() + d0));
      } else {
         AxisAlignedBB axisalignedbb = this.getBoundingBox();
         this.setBoundingBox(new AxisAlignedBB(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ, axisalignedbb.minX + (double)entitysize1.width, axisalignedbb.minY + (double)entitysize1.height, axisalignedbb.minZ + (double)entitysize1.width));
         if (entitysize1.width > entitysize.width && !this.firstUpdate && !this.world.isRemote) {
            float f = entitysize.width - entitysize1.width;
            this.move(MoverType.SELF, new Vec3d((double)f, 0.0D, (double)f));
         }
      }

   }

   public Direction getHorizontalFacing() {
      return Direction.fromAngle((double)this.rotationYaw);
   }

   public Direction getAdjustedHorizontalFacing() {
      return this.getHorizontalFacing();
   }

   protected HoverEvent getHoverEvent() {
      CompoundNBT compoundnbt = new CompoundNBT();
      ResourceLocation resourcelocation = EntityType.getKey(this.getType());
      compoundnbt.putString("id", this.getCachedUniqueIdString());
      if (resourcelocation != null) {
         compoundnbt.putString("type", resourcelocation.toString());
      }

      compoundnbt.putString("name", ITextComponent.Serializer.toJson(this.getName()));
      return new HoverEvent(HoverEvent.Action.SHOW_ENTITY, new StringTextComponent(compoundnbt.toString()));
   }

   public boolean isSpectatedByPlayer(ServerPlayerEntity p_174827_1_) {
      return true;
   }

   public AxisAlignedBB getBoundingBox() {
      return this.boundingBox;
   }

   @OnlyIn(Dist.CLIENT)
   public AxisAlignedBB getRenderBoundingBox() {
      return this.getBoundingBox();
   }

   protected AxisAlignedBB getBoundingBox(Pose p_213321_1_) {
      EntitySize entitysize = this.getSize(p_213321_1_);
      float f = entitysize.width / 2.0F;
      Vec3d vec3d = new Vec3d(this.func_226277_ct_() - (double)f, this.func_226278_cu_(), this.func_226281_cx_() - (double)f);
      Vec3d vec3d1 = new Vec3d(this.func_226277_ct_() + (double)f, this.func_226278_cu_() + (double)entitysize.height, this.func_226281_cx_() + (double)f);
      return new AxisAlignedBB(vec3d, vec3d1);
   }

   public void setBoundingBox(AxisAlignedBB p_174826_1_) {
      this.boundingBox = p_174826_1_;
   }

   protected float getEyeHeight(Pose p_213316_1_, EntitySize p_213316_2_) {
      return p_213316_2_.height * 0.85F;
   }

   @OnlyIn(Dist.CLIENT)
   public float getEyeHeight(Pose p_213307_1_) {
      return this.getEyeHeight(p_213307_1_, this.getSize(p_213307_1_));
   }

   public final float getEyeHeight() {
      return this.eyeHeight;
   }

   public boolean replaceItemInInventory(int p_174820_1_, ItemStack p_174820_2_) {
      return false;
   }

   public void sendMessage(ITextComponent p_145747_1_) {
   }

   public BlockPos getPosition() {
      return new BlockPos(this);
   }

   public Vec3d getPositionVector() {
      return this.getPositionVec();
   }

   public World getEntityWorld() {
      return this.world;
   }

   @Nullable
   public MinecraftServer getServer() {
      return this.world.getServer();
   }

   public ActionResultType applyPlayerInteraction(PlayerEntity p_184199_1_, Vec3d p_184199_2_, Hand p_184199_3_) {
      return ActionResultType.PASS;
   }

   public boolean isImmuneToExplosions() {
      return false;
   }

   protected void applyEnchantments(LivingEntity p_174815_1_, Entity p_174815_2_) {
      if (p_174815_2_ instanceof LivingEntity) {
         EnchantmentHelper.applyThornEnchantments((LivingEntity)p_174815_2_, p_174815_1_);
      }

      EnchantmentHelper.applyArthropodEnchantments(p_174815_1_, p_174815_2_);
   }

   public void addTrackingPlayer(ServerPlayerEntity p_184178_1_) {
   }

   public void removeTrackingPlayer(ServerPlayerEntity p_184203_1_) {
   }

   public float getRotatedYaw(Rotation p_184229_1_) {
      float f = MathHelper.wrapDegrees(this.rotationYaw);
      switch(p_184229_1_) {
      case CLOCKWISE_180:
         return f + 180.0F;
      case COUNTERCLOCKWISE_90:
         return f + 270.0F;
      case CLOCKWISE_90:
         return f + 90.0F;
      default:
         return f;
      }
   }

   public float getMirroredYaw(Mirror p_184217_1_) {
      float f = MathHelper.wrapDegrees(this.rotationYaw);
      switch(p_184217_1_) {
      case LEFT_RIGHT:
         return -f;
      case FRONT_BACK:
         return 180.0F - f;
      default:
         return f;
      }
   }

   public boolean ignoreItemEntityData() {
      return false;
   }

   public boolean setPositionNonDirty() {
      boolean flag = this.isPositionDirty;
      this.isPositionDirty = false;
      return flag;
   }

   @Nullable
   public Entity getControllingPassenger() {
      return null;
   }

   public List<Entity> getPassengers() {
      return (List)(this.passengers.isEmpty() ? Collections.emptyList() : Lists.newArrayList(this.passengers));
   }

   public boolean isPassenger(Entity p_184196_1_) {
      Iterator var2 = this.getPassengers().iterator();

      Entity entity;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         entity = (Entity)var2.next();
      } while(!entity.equals(p_184196_1_));

      return true;
   }

   public boolean isPassenger(Class<? extends Entity> p_205708_1_) {
      Iterator var2 = this.getPassengers().iterator();

      Entity entity;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         entity = (Entity)var2.next();
      } while(!p_205708_1_.isAssignableFrom(entity.getClass()));

      return true;
   }

   public Collection<Entity> getRecursivePassengers() {
      Set<Entity> set = Sets.newHashSet();
      Iterator var2 = this.getPassengers().iterator();

      while(var2.hasNext()) {
         Entity entity = (Entity)var2.next();
         set.add(entity);
         entity.getRecursivePassengers(false, set);
      }

      return set;
   }

   public Stream<Entity> func_226276_cg_() {
      return Stream.concat(Stream.of(this), this.passengers.stream().flatMap(Entity::func_226276_cg_));
   }

   public boolean isOnePlayerRiding() {
      Set<Entity> set = Sets.newHashSet();
      this.getRecursivePassengers(true, set);
      return set.size() == 1;
   }

   private void getRecursivePassengers(boolean p_200604_1_, Set<Entity> p_200604_2_) {
      Entity entity;
      for(Iterator var3 = this.getPassengers().iterator(); var3.hasNext(); entity.getRecursivePassengers(p_200604_1_, p_200604_2_)) {
         entity = (Entity)var3.next();
         if (!p_200604_1_ || ServerPlayerEntity.class.isAssignableFrom(entity.getClass())) {
            p_200604_2_.add(entity);
         }
      }

   }

   public Entity getLowestRidingEntity() {
      Entity entity;
      for(entity = this; entity.isPassenger(); entity = entity.getRidingEntity()) {
      }

      return entity;
   }

   public boolean isRidingSameEntity(Entity p_184223_1_) {
      return this.getLowestRidingEntity() == p_184223_1_.getLowestRidingEntity();
   }

   public boolean isRidingOrBeingRiddenBy(Entity p_184215_1_) {
      Iterator var2 = this.getPassengers().iterator();

      Entity entity;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         entity = (Entity)var2.next();
         if (entity.equals(p_184215_1_)) {
            return true;
         }
      } while(!entity.isRidingOrBeingRiddenBy(p_184215_1_));

      return true;
   }

   public void func_226265_a_(Entity.IMoveCallback p_226265_1_) {
      Iterator var2 = this.passengers.iterator();

      while(var2.hasNext()) {
         Entity entity = (Entity)var2.next();
         this.func_226266_a_(entity, p_226265_1_);
      }

   }

   public boolean canPassengerSteer() {
      Entity entity = this.getControllingPassenger();
      if (entity instanceof PlayerEntity) {
         return ((PlayerEntity)entity).isUser();
      } else {
         return !this.world.isRemote;
      }
   }

   @Nullable
   public Entity getRidingEntity() {
      return this.ridingEntity;
   }

   public PushReaction getPushReaction() {
      return PushReaction.NORMAL;
   }

   public SoundCategory getSoundCategory() {
      return SoundCategory.NEUTRAL;
   }

   protected int getFireImmuneTicks() {
      return 1;
   }

   public CommandSource getCommandSource() {
      return new CommandSource(this, this.getPositionVec(), this.getPitchYaw(), this.world instanceof ServerWorld ? (ServerWorld)this.world : null, this.getPermissionLevel(), this.getName().getString(), this.getDisplayName(), this.world.getServer(), this);
   }

   protected int getPermissionLevel() {
      return 0;
   }

   public boolean hasPermissionLevel(int p_211513_1_) {
      return this.getPermissionLevel() >= p_211513_1_;
   }

   public boolean shouldReceiveFeedback() {
      return this.world.getGameRules().getBoolean(GameRules.SEND_COMMAND_FEEDBACK);
   }

   public boolean shouldReceiveErrors() {
      return true;
   }

   public boolean allowLogging() {
      return true;
   }

   public void lookAt(EntityAnchorArgument.Type p_200602_1_, Vec3d p_200602_2_) {
      Vec3d vec3d = p_200602_1_.apply(this);
      double d0 = p_200602_2_.x - vec3d.x;
      double d1 = p_200602_2_.y - vec3d.y;
      double d2 = p_200602_2_.z - vec3d.z;
      double d3 = (double)MathHelper.sqrt(d0 * d0 + d2 * d2);
      this.rotationPitch = MathHelper.wrapDegrees((float)(-(MathHelper.atan2(d1, d3) * 57.2957763671875D)));
      this.rotationYaw = MathHelper.wrapDegrees((float)(MathHelper.atan2(d2, d0) * 57.2957763671875D) - 90.0F);
      this.setRotationYawHead(this.rotationYaw);
      this.prevRotationPitch = this.rotationPitch;
      this.prevRotationYaw = this.rotationYaw;
   }

   public boolean handleFluidAcceleration(Tag<Fluid> p_210500_1_) {
      AxisAlignedBB axisalignedbb = this.getBoundingBox().shrink(0.001D);
      int i = MathHelper.floor(axisalignedbb.minX);
      int j = MathHelper.ceil(axisalignedbb.maxX);
      int k = MathHelper.floor(axisalignedbb.minY);
      int l = MathHelper.ceil(axisalignedbb.maxY);
      int i1 = MathHelper.floor(axisalignedbb.minZ);
      int j1 = MathHelper.ceil(axisalignedbb.maxZ);
      if (!this.world.isAreaLoaded(i, k, i1, j, l, j1)) {
         return false;
      } else {
         double d0 = 0.0D;
         boolean flag = this.isPushedByWater();
         boolean flag1 = false;
         Vec3d vec3d = Vec3d.ZERO;
         int k1 = 0;
         BlockPos.PooledMutable blockpos$pooledmutable = BlockPos.PooledMutable.retain();
         Throwable var16 = null;

         try {
            for(int l1 = i; l1 < j; ++l1) {
               for(int i2 = k; i2 < l; ++i2) {
                  for(int j2 = i1; j2 < j1; ++j2) {
                     blockpos$pooledmutable.setPos(l1, i2, j2);
                     IFluidState ifluidstate = this.world.getFluidState(blockpos$pooledmutable);
                     if (ifluidstate.isTagged(p_210500_1_)) {
                        double d1 = (double)((float)i2 + ifluidstate.func_215679_a(this.world, blockpos$pooledmutable));
                        if (d1 >= axisalignedbb.minY) {
                           flag1 = true;
                           d0 = Math.max(d1 - axisalignedbb.minY, d0);
                           if (flag) {
                              Vec3d vec3d1 = ifluidstate.getFlow(this.world, blockpos$pooledmutable);
                              if (d0 < 0.4D) {
                                 vec3d1 = vec3d1.scale(d0);
                              }

                              vec3d = vec3d.add(vec3d1);
                              ++k1;
                           }
                        }
                     }
                  }
               }
            }
         } catch (Throwable var31) {
            var16 = var31;
            throw var31;
         } finally {
            if (blockpos$pooledmutable != null) {
               if (var16 != null) {
                  try {
                     blockpos$pooledmutable.close();
                  } catch (Throwable var30) {
                     var16.addSuppressed(var30);
                  }
               } else {
                  blockpos$pooledmutable.close();
               }
            }

         }

         if (vec3d.length() > 0.0D) {
            if (k1 > 0) {
               vec3d = vec3d.scale(1.0D / (double)k1);
            }

            if (!(this instanceof PlayerEntity)) {
               vec3d = vec3d.normalize();
            }

            this.setMotion(this.getMotion().add(vec3d.scale(0.014D)));
         }

         this.submergedHeight = d0;
         return flag1;
      }
   }

   public double getSubmergedHeight() {
      return this.submergedHeight;
   }

   public final float getWidth() {
      return this.size.width;
   }

   public final float getHeight() {
      return this.size.height;
   }

   public abstract IPacket<?> createSpawnPacket();

   public EntitySize getSize(Pose p_213305_1_) {
      return this.type.getSize();
   }

   public Vec3d getPositionVec() {
      return new Vec3d(this.posX, this.posY, this.posZ);
   }

   public Vec3d getMotion() {
      return this.motion;
   }

   public void setMotion(Vec3d p_213317_1_) {
      this.motion = p_213317_1_;
   }

   public void setMotion(double p_213293_1_, double p_213293_3_, double p_213293_5_) {
      this.setMotion(new Vec3d(p_213293_1_, p_213293_3_, p_213293_5_));
   }

   public final double func_226277_ct_() {
      return this.posX;
   }

   public double func_226275_c_(double p_226275_1_) {
      return this.posX + (double)this.getWidth() * p_226275_1_;
   }

   public double func_226282_d_(double p_226282_1_) {
      return this.func_226275_c_((2.0D * this.rand.nextDouble() - 1.0D) * p_226282_1_);
   }

   public final double func_226278_cu_() {
      return this.posY;
   }

   public double func_226283_e_(double p_226283_1_) {
      return this.posY + (double)this.getHeight() * p_226283_1_;
   }

   public double func_226279_cv_() {
      return this.func_226283_e_(this.rand.nextDouble());
   }

   public double func_226280_cw_() {
      return this.posY + (double)this.eyeHeight;
   }

   public final double func_226281_cx_() {
      return this.posZ;
   }

   public double func_226285_f_(double p_226285_1_) {
      return this.posZ + (double)this.getWidth() * p_226285_1_;
   }

   public double func_226287_g_(double p_226287_1_) {
      return this.func_226285_f_((2.0D * this.rand.nextDouble() - 1.0D) * p_226287_1_);
   }

   public void func_226288_n_(double p_226288_1_, double p_226288_3_, double p_226288_5_) {
      this.posX = p_226288_1_;
      this.posY = p_226288_3_;
      this.posZ = p_226288_5_;
      if (this.isAddedToWorld() && !this.world.isRemote && !this.removed) {
         this.world.getChunk((int)Math.floor(this.posX) >> 4, (int)Math.floor(this.posZ) >> 4);
      }

   }

   public void checkDespawn() {
   }

   public void func_225653_b_(double p_225653_1_, double p_225653_3_, double p_225653_5_) {
      this.setLocationAndAngles(p_225653_1_, p_225653_3_, p_225653_5_, this.rotationYaw, this.rotationPitch);
   }

   public void canUpdate(boolean p_canUpdate_1_) {
      this.canUpdate = p_canUpdate_1_;
   }

   public boolean canUpdate() {
      return this.canUpdate;
   }

   public Collection<ItemEntity> captureDrops() {
      return this.captureDrops;
   }

   public Collection<ItemEntity> captureDrops(Collection<ItemEntity> p_captureDrops_1_) {
      Collection<ItemEntity> ret = this.captureDrops;
      this.captureDrops = p_captureDrops_1_;
      return ret;
   }

   public CompoundNBT getPersistentData() {
      if (this.persistentData == null) {
         this.persistentData = new CompoundNBT();
      }

      return this.persistentData;
   }

   public boolean canTrample(BlockState p_canTrample_1_, BlockPos p_canTrample_2_, float p_canTrample_3_) {
      return this.world.rand.nextFloat() < p_canTrample_3_ - 0.5F && this instanceof LivingEntity && (this instanceof PlayerEntity || ForgeEventFactory.getMobGriefingEvent(this.world, this)) && this.getWidth() * this.getWidth() * this.getHeight() > 0.512F;
   }

   public final boolean isAddedToWorld() {
      return this.isAddedToWorld;
   }

   public void onAddedToWorld() {
      this.isAddedToWorld = true;
   }

   public void onRemovedFromWorld() {
      this.isAddedToWorld = false;
   }

   public void revive() {
      this.removed = false;
      this.reviveCaps();
   }

   private float getEyeHeightForge(Pose p_getEyeHeightForge_1_, EntitySize p_getEyeHeightForge_2_) {
      EntityEvent.EyeHeight evt = new EntityEvent.EyeHeight(this, p_getEyeHeightForge_1_, p_getEyeHeightForge_2_, this.getEyeHeight(p_getEyeHeightForge_1_, p_getEyeHeightForge_2_));
      MinecraftForge.EVENT_BUS.post(evt);
      return evt.getNewHeight();
   }

   static {
      FLAGS = EntityDataManager.createKey(Entity.class, DataSerializers.BYTE);
      AIR = EntityDataManager.createKey(Entity.class, DataSerializers.VARINT);
      CUSTOM_NAME = EntityDataManager.createKey(Entity.class, DataSerializers.OPTIONAL_TEXT_COMPONENT);
      CUSTOM_NAME_VISIBLE = EntityDataManager.createKey(Entity.class, DataSerializers.BOOLEAN);
      SILENT = EntityDataManager.createKey(Entity.class, DataSerializers.BOOLEAN);
      NO_GRAVITY = EntityDataManager.createKey(Entity.class, DataSerializers.BOOLEAN);
      POSE = EntityDataManager.createKey(Entity.class, DataSerializers.POSE);
   }

   @FunctionalInterface
   public interface IMoveCallback {
      void accept(Entity var1, double var2, double var4, double var6);
   }
}
