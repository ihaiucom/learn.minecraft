package net.minecraft.entity.item.minecart;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PoweredRailBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.state.properties.RailShape;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.extensions.IForgeEntityMinecart;

public abstract class AbstractMinecartEntity extends Entity implements IForgeEntityMinecart {
   private static final DataParameter<Integer> ROLLING_AMPLITUDE;
   private static final DataParameter<Integer> ROLLING_DIRECTION;
   private static final DataParameter<Float> DAMAGE;
   private static final DataParameter<Integer> DISPLAY_TILE;
   private static final DataParameter<Integer> DISPLAY_TILE_OFFSET;
   private static final DataParameter<Boolean> SHOW_BLOCK;
   private boolean isInReverse;
   private static final Map<RailShape, Pair<Vec3i, Vec3i>> MATRIX;
   private int turnProgress;
   private double minecartX;
   private double minecartY;
   private double minecartZ;
   private double minecartYaw;
   private double minecartPitch;
   @OnlyIn(Dist.CLIENT)
   private double velocityX;
   @OnlyIn(Dist.CLIENT)
   private double velocityY;
   @OnlyIn(Dist.CLIENT)
   private double velocityZ;
   private boolean canBePushed;
   private boolean canUseRail;
   private float currentSpeedOnRail;
   private float maxSpeedAirLateral;
   private float maxSpeedAirVertical;
   private double dragAir;

   protected AbstractMinecartEntity(EntityType<?> p_i48538_1_, World p_i48538_2_) {
      super(p_i48538_1_, p_i48538_2_);
      this.canBePushed = true;
      this.canUseRail = true;
      this.maxSpeedAirLateral = 0.4F;
      this.maxSpeedAirVertical = -1.0F;
      this.dragAir = 0.949999988079071D;
      this.preventEntitySpawning = true;
   }

   protected AbstractMinecartEntity(EntityType<?> p_i48539_1_, World p_i48539_2_, double p_i48539_3_, double p_i48539_5_, double p_i48539_7_) {
      this(p_i48539_1_, p_i48539_2_);
      this.setPosition(p_i48539_3_, p_i48539_5_, p_i48539_7_);
      this.setMotion(Vec3d.ZERO);
      this.prevPosX = p_i48539_3_;
      this.prevPosY = p_i48539_5_;
      this.prevPosZ = p_i48539_7_;
   }

   public static AbstractMinecartEntity create(World p_184263_0_, double p_184263_1_, double p_184263_3_, double p_184263_5_, AbstractMinecartEntity.Type p_184263_7_) {
      if (p_184263_7_ == AbstractMinecartEntity.Type.CHEST) {
         return new ChestMinecartEntity(p_184263_0_, p_184263_1_, p_184263_3_, p_184263_5_);
      } else if (p_184263_7_ == AbstractMinecartEntity.Type.FURNACE) {
         return new FurnaceMinecartEntity(p_184263_0_, p_184263_1_, p_184263_3_, p_184263_5_);
      } else if (p_184263_7_ == AbstractMinecartEntity.Type.TNT) {
         return new TNTMinecartEntity(p_184263_0_, p_184263_1_, p_184263_3_, p_184263_5_);
      } else if (p_184263_7_ == AbstractMinecartEntity.Type.SPAWNER) {
         return new SpawnerMinecartEntity(p_184263_0_, p_184263_1_, p_184263_3_, p_184263_5_);
      } else if (p_184263_7_ == AbstractMinecartEntity.Type.HOPPER) {
         return new HopperMinecartEntity(p_184263_0_, p_184263_1_, p_184263_3_, p_184263_5_);
      } else {
         return (AbstractMinecartEntity)(p_184263_7_ == AbstractMinecartEntity.Type.COMMAND_BLOCK ? new MinecartCommandBlockEntity(p_184263_0_, p_184263_1_, p_184263_3_, p_184263_5_) : new MinecartEntity(p_184263_0_, p_184263_1_, p_184263_3_, p_184263_5_));
      }
   }

   protected boolean func_225502_at_() {
      return false;
   }

   protected void registerData() {
      this.dataManager.register(ROLLING_AMPLITUDE, 0);
      this.dataManager.register(ROLLING_DIRECTION, 1);
      this.dataManager.register(DAMAGE, 0.0F);
      this.dataManager.register(DISPLAY_TILE, Block.getStateId(Blocks.AIR.getDefaultState()));
      this.dataManager.register(DISPLAY_TILE_OFFSET, 6);
      this.dataManager.register(SHOW_BLOCK, false);
   }

   @Nullable
   public AxisAlignedBB getCollisionBox(Entity p_70114_1_) {
      if (this.getCollisionHandler() != null) {
         return this.getCollisionHandler().getCollisionBox(this, p_70114_1_);
      } else {
         return p_70114_1_.canBePushed() ? p_70114_1_.getBoundingBox() : null;
      }
   }

   public boolean canBePushed() {
      return this.canBePushed;
   }

   public double getMountedYOffset() {
      return 0.0D;
   }

   public boolean attackEntityFrom(DamageSource p_70097_1_, float p_70097_2_) {
      if (!this.world.isRemote && !this.removed) {
         if (this.isInvulnerableTo(p_70097_1_)) {
            return false;
         } else {
            this.setRollingDirection(-this.getRollingDirection());
            this.setRollingAmplitude(10);
            this.markVelocityChanged();
            this.setDamage(this.getDamage() + p_70097_2_ * 10.0F);
            boolean flag = p_70097_1_.getTrueSource() instanceof PlayerEntity && ((PlayerEntity)p_70097_1_.getTrueSource()).abilities.isCreativeMode;
            if (flag || this.getDamage() > 40.0F) {
               this.removePassengers();
               if (flag && !this.hasCustomName()) {
                  this.remove();
               } else {
                  this.killMinecart(p_70097_1_);
               }
            }

            return true;
         }
      } else {
         return true;
      }
   }

   public void killMinecart(DamageSource p_94095_1_) {
      this.remove();
      if (this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
         ItemStack itemstack = new ItemStack(Items.MINECART);
         if (this.hasCustomName()) {
            itemstack.setDisplayName(this.getCustomName());
         }

         this.entityDropItem(itemstack);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public void performHurtAnimation() {
      this.setRollingDirection(-this.getRollingDirection());
      this.setRollingAmplitude(10);
      this.setDamage(this.getDamage() + this.getDamage() * 10.0F);
   }

   public boolean canBeCollidedWith() {
      return !this.removed;
   }

   private static Pair<Vec3i, Vec3i> func_226573_a_(RailShape p_226573_0_) {
      return (Pair)MATRIX.get(p_226573_0_);
   }

   public Direction getAdjustedHorizontalFacing() {
      return this.isInReverse ? this.getHorizontalFacing().getOpposite().rotateY() : this.getHorizontalFacing().rotateY();
   }

   public void tick() {
      if (this.getRollingAmplitude() > 0) {
         this.setRollingAmplitude(this.getRollingAmplitude() - 1);
      }

      if (this.getDamage() > 0.0F) {
         this.setDamage(this.getDamage() - 1.0F);
      }

      if (this.func_226278_cu_() < -64.0D) {
         this.outOfWorld();
      }

      this.updatePortal();
      if (this.world.isRemote) {
         if (this.turnProgress > 0) {
            double d4 = this.func_226277_ct_() + (this.minecartX - this.func_226277_ct_()) / (double)this.turnProgress;
            double d5 = this.func_226278_cu_() + (this.minecartY - this.func_226278_cu_()) / (double)this.turnProgress;
            double d6 = this.func_226281_cx_() + (this.minecartZ - this.func_226281_cx_()) / (double)this.turnProgress;
            double d1 = MathHelper.wrapDegrees(this.minecartYaw - (double)this.rotationYaw);
            this.rotationYaw = (float)((double)this.rotationYaw + d1 / (double)this.turnProgress);
            this.rotationPitch = (float)((double)this.rotationPitch + (this.minecartPitch - (double)this.rotationPitch) / (double)this.turnProgress);
            --this.turnProgress;
            this.setPosition(d4, d5, d6);
            this.setRotation(this.rotationYaw, this.rotationPitch);
         } else {
            this.func_226264_Z_();
            this.setRotation(this.rotationYaw, this.rotationPitch);
         }
      } else {
         if (!this.hasNoGravity()) {
            this.setMotion(this.getMotion().add(0.0D, -0.04D, 0.0D));
         }

         int i = MathHelper.floor(this.func_226277_ct_());
         int j = MathHelper.floor(this.func_226278_cu_());
         int k = MathHelper.floor(this.func_226281_cx_());
         if (this.world.getBlockState(new BlockPos(i, j - 1, k)).isIn(BlockTags.RAILS)) {
            --j;
         }

         BlockPos blockpos = new BlockPos(i, j, k);
         BlockState blockstate = this.world.getBlockState(blockpos);
         if (this.canUseRail() && blockstate.isIn(BlockTags.RAILS)) {
            this.moveAlongTrack(blockpos, blockstate);
            if (blockstate.getBlock() instanceof PoweredRailBlock && ((PoweredRailBlock)blockstate.getBlock()).isActivatorRail()) {
               this.onActivatorRailPass(i, j, k, (Boolean)blockstate.get(PoweredRailBlock.POWERED));
            }
         } else {
            this.moveDerailedMinecart();
         }

         this.doBlockCollisions();
         this.rotationPitch = 0.0F;
         double d0 = this.prevPosX - this.func_226277_ct_();
         double d2 = this.prevPosZ - this.func_226281_cx_();
         if (d0 * d0 + d2 * d2 > 0.001D) {
            this.rotationYaw = (float)(MathHelper.atan2(d2, d0) * 180.0D / 3.141592653589793D);
            if (this.isInReverse) {
               this.rotationYaw += 180.0F;
            }
         }

         double d3 = (double)MathHelper.wrapDegrees(this.rotationYaw - this.prevRotationYaw);
         if (d3 < -170.0D || d3 >= 170.0D) {
            this.rotationYaw += 180.0F;
            this.isInReverse = !this.isInReverse;
         }

         this.setRotation(this.rotationYaw, this.rotationPitch);
         AxisAlignedBB box;
         if (this.getCollisionHandler() != null) {
            box = this.getCollisionHandler().getMinecartCollisionBox(this);
         } else {
            box = this.getBoundingBox().grow(0.20000000298023224D, 0.0D, 0.20000000298023224D);
         }

         if (this.canBeRidden() && func_213296_b(this.getMotion()) > 0.01D) {
            List<Entity> list = this.world.getEntitiesInAABBexcluding(this, box, EntityPredicates.pushableBy(this));
            if (!list.isEmpty()) {
               for(int l = 0; l < list.size(); ++l) {
                  Entity entity1 = (Entity)list.get(l);
                  if (!(entity1 instanceof PlayerEntity) && !(entity1 instanceof IronGolemEntity) && !(entity1 instanceof AbstractMinecartEntity) && !this.isBeingRidden() && !entity1.isPassenger()) {
                     entity1.startRiding(this);
                  } else {
                     entity1.applyEntityCollision(this);
                  }
               }
            }
         } else {
            Iterator var13 = this.world.getEntitiesWithinAABBExcludingEntity(this, box).iterator();

            while(var13.hasNext()) {
               Entity entity = (Entity)var13.next();
               if (!this.isPassenger(entity) && entity.canBePushed() && entity instanceof AbstractMinecartEntity) {
                  entity.applyEntityCollision(this);
               }
            }
         }

         this.handleWaterMovement();
      }

   }

   protected double getMaximumSpeed() {
      return 0.4D;
   }

   public void onActivatorRailPass(int p_96095_1_, int p_96095_2_, int p_96095_3_, boolean p_96095_4_) {
   }

   protected void moveDerailedMinecart() {
      double d0 = this.onGround ? this.getMaximumSpeed() : (double)this.getMaxSpeedAirLateral();
      Vec3d vec3d = this.getMotion();
      this.setMotion(MathHelper.clamp(vec3d.x, -d0, d0), vec3d.y, MathHelper.clamp(vec3d.z, -d0, d0));
      if (this.getMaxSpeedAirVertical() > 0.0F && this.getMotion().y > (double)this.getMaxSpeedAirVertical()) {
         if (Math.abs(this.getMotion().x) < 0.30000001192092896D && Math.abs(this.getMotion().z) < 0.30000001192092896D) {
            this.setMotion(new Vec3d(this.getMotion().x, 0.15000000596046448D, this.getMotion().z));
         } else {
            this.setMotion(new Vec3d(this.getMotion().x, (double)this.getMaxSpeedAirVertical(), this.getMotion().z));
         }
      }

      if (this.onGround) {
         this.setMotion(this.getMotion().scale(0.5D));
      }

      this.move(MoverType.SELF, this.getMotion());
      if (!this.onGround) {
         this.setMotion(this.getMotion().scale(this.getDragAir()));
      }

   }

   protected void moveAlongTrack(BlockPos p_180460_1_, BlockState p_180460_2_) {
      this.fallDistance = 0.0F;
      double d0 = this.func_226277_ct_();
      double d1 = this.func_226278_cu_();
      double d2 = this.func_226281_cx_();
      Vec3d vec3d = this.getPos(d0, d1, d2);
      d1 = (double)p_180460_1_.getY();
      boolean flag = false;
      boolean flag1 = false;
      AbstractRailBlock abstractrailblock = (AbstractRailBlock)p_180460_2_.getBlock();
      if (abstractrailblock instanceof PoweredRailBlock && !((PoweredRailBlock)abstractrailblock).isActivatorRail()) {
         flag = (Boolean)p_180460_2_.get(PoweredRailBlock.POWERED);
         flag1 = !flag;
      }

      Vec3d vec3d1 = this.getMotion();
      RailShape railshape = ((AbstractRailBlock)p_180460_2_.getBlock()).getRailDirection(p_180460_2_, this.world, p_180460_1_, this);
      switch(railshape) {
      case ASCENDING_EAST:
         this.setMotion(vec3d1.add(-1.0D * this.getSlopeAdjustment(), 0.0D, 0.0D));
         ++d1;
         break;
      case ASCENDING_WEST:
         this.setMotion(vec3d1.add(this.getSlopeAdjustment(), 0.0D, 0.0D));
         ++d1;
         break;
      case ASCENDING_NORTH:
         this.setMotion(vec3d1.add(0.0D, 0.0D, this.getSlopeAdjustment()));
         ++d1;
         break;
      case ASCENDING_SOUTH:
         this.setMotion(vec3d1.add(0.0D, 0.0D, -1.0D * this.getSlopeAdjustment()));
         ++d1;
      }

      vec3d1 = this.getMotion();
      Pair<Vec3i, Vec3i> pair = func_226573_a_(railshape);
      Vec3i vec3i = (Vec3i)pair.getFirst();
      Vec3i vec3i1 = (Vec3i)pair.getSecond();
      double d4 = (double)(vec3i1.getX() - vec3i.getX());
      double d5 = (double)(vec3i1.getZ() - vec3i.getZ());
      double d6 = Math.sqrt(d4 * d4 + d5 * d5);
      double d7 = vec3d1.x * d4 + vec3d1.z * d5;
      if (d7 < 0.0D) {
         d4 = -d4;
         d5 = -d5;
      }

      double d8 = Math.min(2.0D, Math.sqrt(func_213296_b(vec3d1)));
      vec3d1 = new Vec3d(d8 * d4 / d6, vec3d1.y, d8 * d5 / d6);
      this.setMotion(vec3d1);
      Entity entity = this.getPassengers().isEmpty() ? null : (Entity)this.getPassengers().get(0);
      if (entity instanceof PlayerEntity) {
         Vec3d vec3d2 = entity.getMotion();
         double d9 = func_213296_b(vec3d2);
         double d11 = func_213296_b(this.getMotion());
         if (d9 > 1.0E-4D && d11 < 0.01D) {
            this.setMotion(this.getMotion().add(vec3d2.x * 0.1D, 0.0D, vec3d2.z * 0.1D));
            flag1 = false;
         }
      }

      double d23;
      if (flag1 && this.shouldDoRailFunctions()) {
         d23 = Math.sqrt(func_213296_b(this.getMotion()));
         if (d23 < 0.03D) {
            this.setMotion(Vec3d.ZERO);
         } else {
            this.setMotion(this.getMotion().mul(0.5D, 0.0D, 0.5D));
         }
      }

      d23 = (double)p_180460_1_.getX() + 0.5D + (double)vec3i.getX() * 0.5D;
      double d10 = (double)p_180460_1_.getZ() + 0.5D + (double)vec3i.getZ() * 0.5D;
      double d12 = (double)p_180460_1_.getX() + 0.5D + (double)vec3i1.getX() * 0.5D;
      double d13 = (double)p_180460_1_.getZ() + 0.5D + (double)vec3i1.getZ() * 0.5D;
      d4 = d12 - d23;
      d5 = d13 - d10;
      double d14;
      if (d4 == 0.0D) {
         d14 = d2 - (double)p_180460_1_.getZ();
      } else if (d5 == 0.0D) {
         d14 = d0 - (double)p_180460_1_.getX();
      } else {
         double d15 = d0 - d23;
         double d16 = d2 - d10;
         d14 = (d15 * d4 + d16 * d5) * 2.0D;
      }

      d0 = d23 + d4 * d14;
      d2 = d10 + d5 * d14;
      this.setPosition(d0, d1, d2);
      this.moveMinecartOnRail(p_180460_1_);
      if (vec3i.getY() != 0 && MathHelper.floor(this.func_226277_ct_()) - p_180460_1_.getX() == vec3i.getX() && MathHelper.floor(this.func_226281_cx_()) - p_180460_1_.getZ() == vec3i.getZ()) {
         this.setPosition(this.func_226277_ct_(), this.func_226278_cu_() + (double)vec3i.getY(), this.func_226281_cx_());
      } else if (vec3i1.getY() != 0 && MathHelper.floor(this.func_226277_ct_()) - p_180460_1_.getX() == vec3i1.getX() && MathHelper.floor(this.func_226281_cx_()) - p_180460_1_.getZ() == vec3i1.getZ()) {
         this.setPosition(this.func_226277_ct_(), this.func_226278_cu_() + (double)vec3i1.getY(), this.func_226281_cx_());
      }

      this.applyDrag();
      Vec3d vec3d3 = this.getPos(this.func_226277_ct_(), this.func_226278_cu_(), this.func_226281_cx_());
      Vec3d vec3d6;
      double d27;
      if (vec3d3 != null && vec3d != null) {
         double d17 = (vec3d.y - vec3d3.y) * 0.05D;
         vec3d6 = this.getMotion();
         d27 = Math.sqrt(func_213296_b(vec3d6));
         if (d27 > 0.0D) {
            this.setMotion(vec3d6.mul((d27 + d17) / d27, 1.0D, (d27 + d17) / d27));
         }

         this.setPosition(this.func_226277_ct_(), vec3d3.y, this.func_226281_cx_());
      }

      int j = MathHelper.floor(this.func_226277_ct_());
      int i = MathHelper.floor(this.func_226281_cx_());
      if (j != p_180460_1_.getX() || i != p_180460_1_.getZ()) {
         vec3d6 = this.getMotion();
         d27 = Math.sqrt(func_213296_b(vec3d6));
         this.setMotion(d27 * (double)(j - p_180460_1_.getX()), vec3d6.y, d27 * (double)(i - p_180460_1_.getZ()));
      }

      if (this.shouldDoRailFunctions()) {
         ((AbstractRailBlock)p_180460_2_.getBlock()).onMinecartPass(p_180460_2_, this.world, p_180460_1_, this);
      }

      if (flag && this.shouldDoRailFunctions()) {
         vec3d6 = this.getMotion();
         d27 = Math.sqrt(func_213296_b(vec3d6));
         if (d27 > 0.01D) {
            double d19 = 0.06D;
            this.setMotion(vec3d6.add(vec3d6.x / d27 * 0.06D, 0.0D, vec3d6.z / d27 * 0.06D));
         } else {
            Vec3d vec3d7 = this.getMotion();
            double d20 = vec3d7.x;
            double d21 = vec3d7.z;
            if (railshape == RailShape.EAST_WEST) {
               if (this.func_213900_a(p_180460_1_.west())) {
                  d20 = 0.02D;
               } else if (this.func_213900_a(p_180460_1_.east())) {
                  d20 = -0.02D;
               }
            } else {
               if (railshape != RailShape.NORTH_SOUTH) {
                  return;
               }

               if (this.func_213900_a(p_180460_1_.north())) {
                  d21 = 0.02D;
               } else if (this.func_213900_a(p_180460_1_.south())) {
                  d21 = -0.02D;
               }
            }

            this.setMotion(d20, vec3d7.y, d21);
         }
      }

   }

   private boolean func_213900_a(BlockPos p_213900_1_) {
      return this.world.getBlockState(p_213900_1_).isNormalCube(this.world, p_213900_1_);
   }

   protected void applyDrag() {
      double d0 = this.isBeingRidden() ? 0.997D : 0.96D;
      this.setMotion(this.getMotion().mul(d0, 0.0D, d0));
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public Vec3d getPosOffset(double p_70495_1_, double p_70495_3_, double p_70495_5_, double p_70495_7_) {
      int i = MathHelper.floor(p_70495_1_);
      int j = MathHelper.floor(p_70495_3_);
      int k = MathHelper.floor(p_70495_5_);
      if (this.world.getBlockState(new BlockPos(i, j - 1, k)).isIn(BlockTags.RAILS)) {
         --j;
      }

      BlockState blockstate = this.world.getBlockState(new BlockPos(i, j, k));
      if (blockstate.isIn(BlockTags.RAILS)) {
         RailShape railshape = ((AbstractRailBlock)blockstate.getBlock()).getRailDirection(blockstate, this.world, new BlockPos(i, j, k), this);
         p_70495_3_ = (double)j;
         if (railshape.isAscending()) {
            p_70495_3_ = (double)(j + 1);
         }

         Pair<Vec3i, Vec3i> pair = func_226573_a_(railshape);
         Vec3i vec3i = (Vec3i)pair.getFirst();
         Vec3i vec3i1 = (Vec3i)pair.getSecond();
         double d0 = (double)(vec3i1.getX() - vec3i.getX());
         double d1 = (double)(vec3i1.getZ() - vec3i.getZ());
         double d2 = Math.sqrt(d0 * d0 + d1 * d1);
         d0 /= d2;
         d1 /= d2;
         p_70495_1_ += d0 * p_70495_7_;
         p_70495_5_ += d1 * p_70495_7_;
         if (vec3i.getY() != 0 && MathHelper.floor(p_70495_1_) - i == vec3i.getX() && MathHelper.floor(p_70495_5_) - k == vec3i.getZ()) {
            p_70495_3_ += (double)vec3i.getY();
         } else if (vec3i1.getY() != 0 && MathHelper.floor(p_70495_1_) - i == vec3i1.getX() && MathHelper.floor(p_70495_5_) - k == vec3i1.getZ()) {
            p_70495_3_ += (double)vec3i1.getY();
         }

         return this.getPos(p_70495_1_, p_70495_3_, p_70495_5_);
      } else {
         return null;
      }
   }

   @Nullable
   public Vec3d getPos(double p_70489_1_, double p_70489_3_, double p_70489_5_) {
      int i = MathHelper.floor(p_70489_1_);
      int j = MathHelper.floor(p_70489_3_);
      int k = MathHelper.floor(p_70489_5_);
      if (this.world.getBlockState(new BlockPos(i, j - 1, k)).isIn(BlockTags.RAILS)) {
         --j;
      }

      BlockState blockstate = this.world.getBlockState(new BlockPos(i, j, k));
      if (blockstate.isIn(BlockTags.RAILS)) {
         RailShape railshape = ((AbstractRailBlock)blockstate.getBlock()).getRailDirection(blockstate, this.world, new BlockPos(i, j, k), this);
         Pair<Vec3i, Vec3i> pair = func_226573_a_(railshape);
         Vec3i vec3i = (Vec3i)pair.getFirst();
         Vec3i vec3i1 = (Vec3i)pair.getSecond();
         double d0 = (double)i + 0.5D + (double)vec3i.getX() * 0.5D;
         double d1 = (double)j + 0.0625D + (double)vec3i.getY() * 0.5D;
         double d2 = (double)k + 0.5D + (double)vec3i.getZ() * 0.5D;
         double d3 = (double)i + 0.5D + (double)vec3i1.getX() * 0.5D;
         double d4 = (double)j + 0.0625D + (double)vec3i1.getY() * 0.5D;
         double d5 = (double)k + 0.5D + (double)vec3i1.getZ() * 0.5D;
         double d6 = d3 - d0;
         double d7 = (d4 - d1) * 2.0D;
         double d8 = d5 - d2;
         double d9;
         if (d6 == 0.0D) {
            d9 = p_70489_5_ - (double)k;
         } else if (d8 == 0.0D) {
            d9 = p_70489_1_ - (double)i;
         } else {
            double d10 = p_70489_1_ - d0;
            double d11 = p_70489_5_ - d2;
            d9 = (d10 * d6 + d11 * d8) * 2.0D;
         }

         p_70489_1_ = d0 + d6 * d9;
         p_70489_3_ = d1 + d7 * d9;
         p_70489_5_ = d2 + d8 * d9;
         if (d7 < 0.0D) {
            ++p_70489_3_;
         } else if (d7 > 0.0D) {
            p_70489_3_ += 0.5D;
         }

         return new Vec3d(p_70489_1_, p_70489_3_, p_70489_5_);
      } else {
         return null;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public AxisAlignedBB getRenderBoundingBox() {
      AxisAlignedBB axisalignedbb = this.getBoundingBox();
      return this.hasDisplayTile() ? axisalignedbb.grow((double)Math.abs(this.getDisplayTileOffset()) / 16.0D) : axisalignedbb;
   }

   protected void readAdditional(CompoundNBT p_70037_1_) {
      if (p_70037_1_.getBoolean("CustomDisplayTile")) {
         this.setDisplayTile(NBTUtil.readBlockState(p_70037_1_.getCompound("DisplayState")));
         this.setDisplayTileOffset(p_70037_1_.getInt("DisplayOffset"));
      }

   }

   protected void writeAdditional(CompoundNBT p_213281_1_) {
      if (this.hasDisplayTile()) {
         p_213281_1_.putBoolean("CustomDisplayTile", true);
         p_213281_1_.put("DisplayState", NBTUtil.writeBlockState(this.getDisplayTile()));
         p_213281_1_.putInt("DisplayOffset", this.getDisplayTileOffset());
      }

   }

   public void applyEntityCollision(Entity p_70108_1_) {
      if (this.getCollisionHandler() != null) {
         this.getCollisionHandler().onEntityCollision(this, p_70108_1_);
      } else {
         if (!this.world.isRemote && !p_70108_1_.noClip && !this.noClip && !this.isPassenger(p_70108_1_)) {
            double d0 = p_70108_1_.func_226277_ct_() - this.func_226277_ct_();
            double d1 = p_70108_1_.func_226281_cx_() - this.func_226281_cx_();
            double d2 = d0 * d0 + d1 * d1;
            if (d2 >= 9.999999747378752E-5D) {
               d2 = (double)MathHelper.sqrt(d2);
               d0 /= d2;
               d1 /= d2;
               double d3 = 1.0D / d2;
               if (d3 > 1.0D) {
                  d3 = 1.0D;
               }

               d0 *= d3;
               d1 *= d3;
               d0 *= 0.10000000149011612D;
               d1 *= 0.10000000149011612D;
               d0 *= (double)(1.0F - this.entityCollisionReduction);
               d1 *= (double)(1.0F - this.entityCollisionReduction);
               d0 *= 0.5D;
               d1 *= 0.5D;
               if (p_70108_1_ instanceof AbstractMinecartEntity) {
                  double d4 = p_70108_1_.func_226277_ct_() - this.func_226277_ct_();
                  double d5 = p_70108_1_.func_226281_cx_() - this.func_226281_cx_();
                  Vec3d vec3d = (new Vec3d(d4, 0.0D, d5)).normalize();
                  Vec3d vec3d1 = (new Vec3d((double)MathHelper.cos(this.rotationYaw * 0.017453292F), 0.0D, (double)MathHelper.sin(this.rotationYaw * 0.017453292F))).normalize();
                  double d6 = Math.abs(vec3d.dotProduct(vec3d1));
                  if (d6 < 0.800000011920929D) {
                     return;
                  }

                  Vec3d vec3d2 = this.getMotion();
                  Vec3d vec3d3 = p_70108_1_.getMotion();
                  if (((AbstractMinecartEntity)p_70108_1_).isPoweredCart() && !this.isPoweredCart()) {
                     this.setMotion(vec3d2.mul(0.2D, 1.0D, 0.2D));
                     this.addVelocity(vec3d3.x - d0, 0.0D, vec3d3.z - d1);
                     p_70108_1_.setMotion(vec3d3.mul(0.95D, 1.0D, 0.95D));
                  } else if (!((AbstractMinecartEntity)p_70108_1_).isPoweredCart() && this.isPoweredCart()) {
                     p_70108_1_.setMotion(vec3d3.mul(0.2D, 1.0D, 0.2D));
                     p_70108_1_.addVelocity(vec3d2.x + d0, 0.0D, vec3d2.z + d1);
                     this.setMotion(vec3d2.mul(0.95D, 1.0D, 0.95D));
                  } else {
                     double d7 = (vec3d3.x + vec3d2.x) / 2.0D;
                     double d8 = (vec3d3.z + vec3d2.z) / 2.0D;
                     this.setMotion(vec3d2.mul(0.2D, 1.0D, 0.2D));
                     this.addVelocity(d7 - d0, 0.0D, d8 - d1);
                     p_70108_1_.setMotion(vec3d3.mul(0.2D, 1.0D, 0.2D));
                     p_70108_1_.addVelocity(d7 + d0, 0.0D, d8 + d1);
                  }
               } else {
                  this.addVelocity(-d0, 0.0D, -d1);
                  p_70108_1_.addVelocity(d0 / 4.0D, 0.0D, d1 / 4.0D);
               }
            }
         }

      }
   }

   @OnlyIn(Dist.CLIENT)
   public void setPositionAndRotationDirect(double p_180426_1_, double p_180426_3_, double p_180426_5_, float p_180426_7_, float p_180426_8_, int p_180426_9_, boolean p_180426_10_) {
      this.minecartX = p_180426_1_;
      this.minecartY = p_180426_3_;
      this.minecartZ = p_180426_5_;
      this.minecartYaw = (double)p_180426_7_;
      this.minecartPitch = (double)p_180426_8_;
      this.turnProgress = p_180426_9_ + 2;
      this.setMotion(this.velocityX, this.velocityY, this.velocityZ);
   }

   @OnlyIn(Dist.CLIENT)
   public void setVelocity(double p_70016_1_, double p_70016_3_, double p_70016_5_) {
      this.velocityX = p_70016_1_;
      this.velocityY = p_70016_3_;
      this.velocityZ = p_70016_5_;
      this.setMotion(this.velocityX, this.velocityY, this.velocityZ);
   }

   public void setDamage(float p_70492_1_) {
      this.dataManager.set(DAMAGE, p_70492_1_);
   }

   public float getDamage() {
      return (Float)this.dataManager.get(DAMAGE);
   }

   public void setRollingAmplitude(int p_70497_1_) {
      this.dataManager.set(ROLLING_AMPLITUDE, p_70497_1_);
   }

   public int getRollingAmplitude() {
      return (Integer)this.dataManager.get(ROLLING_AMPLITUDE);
   }

   public void setRollingDirection(int p_70494_1_) {
      this.dataManager.set(ROLLING_DIRECTION, p_70494_1_);
   }

   public int getRollingDirection() {
      return (Integer)this.dataManager.get(ROLLING_DIRECTION);
   }

   public abstract AbstractMinecartEntity.Type getMinecartType();

   public BlockState getDisplayTile() {
      return !this.hasDisplayTile() ? this.getDefaultDisplayTile() : Block.getStateById((Integer)this.getDataManager().get(DISPLAY_TILE));
   }

   public BlockState getDefaultDisplayTile() {
      return Blocks.AIR.getDefaultState();
   }

   public int getDisplayTileOffset() {
      return !this.hasDisplayTile() ? this.getDefaultDisplayTileOffset() : (Integer)this.getDataManager().get(DISPLAY_TILE_OFFSET);
   }

   public int getDefaultDisplayTileOffset() {
      return 6;
   }

   public void setDisplayTile(BlockState p_174899_1_) {
      this.getDataManager().set(DISPLAY_TILE, Block.getStateId(p_174899_1_));
      this.setHasDisplayTile(true);
   }

   public void setDisplayTileOffset(int p_94086_1_) {
      this.getDataManager().set(DISPLAY_TILE_OFFSET, p_94086_1_);
      this.setHasDisplayTile(true);
   }

   public boolean hasDisplayTile() {
      return (Boolean)this.getDataManager().get(SHOW_BLOCK);
   }

   public void setHasDisplayTile(boolean p_94096_1_) {
      this.getDataManager().set(SHOW_BLOCK, p_94096_1_);
   }

   public IPacket<?> createSpawnPacket() {
      return new SSpawnObjectPacket(this);
   }

   public boolean canUseRail() {
      return this.canUseRail;
   }

   public void setCanUseRail(boolean p_setCanUseRail_1_) {
      this.canUseRail = p_setCanUseRail_1_;
   }

   public float getCurrentCartSpeedCapOnRail() {
      return this.currentSpeedOnRail;
   }

   public void setCurrentCartSpeedCapOnRail(float p_setCurrentCartSpeedCapOnRail_1_) {
      this.currentSpeedOnRail = Math.min(p_setCurrentCartSpeedCapOnRail_1_, this.getMaxCartSpeedOnRail());
   }

   public float getMaxSpeedAirLateral() {
      return this.maxSpeedAirLateral;
   }

   public void setMaxSpeedAirLateral(float p_setMaxSpeedAirLateral_1_) {
      this.maxSpeedAirLateral = p_setMaxSpeedAirLateral_1_;
   }

   public float getMaxSpeedAirVertical() {
      return this.maxSpeedAirVertical;
   }

   public void setMaxSpeedAirVertical(float p_setMaxSpeedAirVertical_1_) {
      this.maxSpeedAirVertical = p_setMaxSpeedAirVertical_1_;
   }

   public double getDragAir() {
      return this.dragAir;
   }

   public void setDragAir(double p_setDragAir_1_) {
      this.dragAir = p_setDragAir_1_;
   }

   public double getMaxSpeedWithRail() {
      if (!this.canUseRail()) {
         return this.getMaximumSpeed();
      } else {
         BlockPos pos = this.getCurrentRailPosition();
         BlockState state = this.getMinecart().world.getBlockState(pos);
         if (!state.isIn(BlockTags.RAILS)) {
            return this.getMaximumSpeed();
         } else {
            float railMaxSpeed = ((AbstractRailBlock)state.getBlock()).getRailMaxSpeed(state, this.getMinecart().world, pos, this.getMinecart());
            return (double)Math.min(railMaxSpeed, this.getCurrentCartSpeedCapOnRail());
         }
      }
   }

   public void moveMinecartOnRail(BlockPos p_moveMinecartOnRail_1_) {
      AbstractMinecartEntity mc = this.getMinecart();
      double d24 = mc.isBeingRidden() ? 0.75D : 1.0D;
      double d25 = mc.getMaxSpeedWithRail();
      Vec3d vec3d1 = mc.getMotion();
      mc.move(MoverType.SELF, new Vec3d(MathHelper.clamp(d24 * vec3d1.x, -d25, d25), 0.0D, MathHelper.clamp(d24 * vec3d1.z, -d25, d25)));
   }

   static {
      ROLLING_AMPLITUDE = EntityDataManager.createKey(AbstractMinecartEntity.class, DataSerializers.VARINT);
      ROLLING_DIRECTION = EntityDataManager.createKey(AbstractMinecartEntity.class, DataSerializers.VARINT);
      DAMAGE = EntityDataManager.createKey(AbstractMinecartEntity.class, DataSerializers.FLOAT);
      DISPLAY_TILE = EntityDataManager.createKey(AbstractMinecartEntity.class, DataSerializers.VARINT);
      DISPLAY_TILE_OFFSET = EntityDataManager.createKey(AbstractMinecartEntity.class, DataSerializers.VARINT);
      SHOW_BLOCK = EntityDataManager.createKey(AbstractMinecartEntity.class, DataSerializers.BOOLEAN);
      MATRIX = (Map)Util.make(Maps.newEnumMap(RailShape.class), (p_lambda$static$0_0_) -> {
         Vec3i vec3i = Direction.WEST.getDirectionVec();
         Vec3i vec3i1 = Direction.EAST.getDirectionVec();
         Vec3i vec3i2 = Direction.NORTH.getDirectionVec();
         Vec3i vec3i3 = Direction.SOUTH.getDirectionVec();
         Vec3i vec3i4 = vec3i.down();
         Vec3i vec3i5 = vec3i1.down();
         Vec3i vec3i6 = vec3i2.down();
         Vec3i vec3i7 = vec3i3.down();
         p_lambda$static$0_0_.put(RailShape.NORTH_SOUTH, Pair.of(vec3i2, vec3i3));
         p_lambda$static$0_0_.put(RailShape.EAST_WEST, Pair.of(vec3i, vec3i1));
         p_lambda$static$0_0_.put(RailShape.ASCENDING_EAST, Pair.of(vec3i4, vec3i1));
         p_lambda$static$0_0_.put(RailShape.ASCENDING_WEST, Pair.of(vec3i, vec3i5));
         p_lambda$static$0_0_.put(RailShape.ASCENDING_NORTH, Pair.of(vec3i2, vec3i7));
         p_lambda$static$0_0_.put(RailShape.ASCENDING_SOUTH, Pair.of(vec3i6, vec3i3));
         p_lambda$static$0_0_.put(RailShape.SOUTH_EAST, Pair.of(vec3i3, vec3i1));
         p_lambda$static$0_0_.put(RailShape.SOUTH_WEST, Pair.of(vec3i3, vec3i));
         p_lambda$static$0_0_.put(RailShape.NORTH_WEST, Pair.of(vec3i2, vec3i));
         p_lambda$static$0_0_.put(RailShape.NORTH_EAST, Pair.of(vec3i2, vec3i1));
      });
   }

   public static enum Type {
      RIDEABLE,
      CHEST,
      FURNACE,
      TNT,
      SPAWNER,
      HOPPER,
      COMMAND_BLOCK;
   }
}
