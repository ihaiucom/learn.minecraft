package net.minecraft.tileentity;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.Tags;

public class ConduitTileEntity extends TileEntity implements ITickableTileEntity {
   private static final Block[] field_205042_e;
   public int ticksExisted;
   private float activeRotation;
   private boolean active;
   private boolean eyeOpen;
   private final List<BlockPos> prismarinePositions;
   @Nullable
   private LivingEntity target;
   @Nullable
   private UUID targetUuid;
   private long nextSoundTime;

   public ConduitTileEntity() {
      this(TileEntityType.CONDUIT);
   }

   public ConduitTileEntity(TileEntityType<?> p_i48929_1_) {
      super(p_i48929_1_);
      this.prismarinePositions = Lists.newArrayList();
   }

   public void read(CompoundNBT p_145839_1_) {
      super.read(p_145839_1_);
      if (p_145839_1_.contains("target_uuid")) {
         this.targetUuid = NBTUtil.readUniqueId(p_145839_1_.getCompound("target_uuid"));
      } else {
         this.targetUuid = null;
      }

   }

   public CompoundNBT write(CompoundNBT p_189515_1_) {
      super.write(p_189515_1_);
      if (this.target != null) {
         p_189515_1_.put("target_uuid", NBTUtil.writeUniqueId(this.target.getUniqueID()));
      }

      return p_189515_1_;
   }

   @Nullable
   public SUpdateTileEntityPacket getUpdatePacket() {
      return new SUpdateTileEntityPacket(this.pos, 5, this.getUpdateTag());
   }

   public CompoundNBT getUpdateTag() {
      return this.write(new CompoundNBT());
   }

   public void tick() {
      ++this.ticksExisted;
      long i = this.world.getGameTime();
      if (i % 40L == 0L) {
         this.setActive(this.shouldBeActive());
         if (!this.world.isRemote && this.isActive()) {
            this.addEffectsToPlayers();
            this.attackMobs();
         }
      }

      if (i % 80L == 0L && this.isActive()) {
         this.playSound(SoundEvents.BLOCK_CONDUIT_AMBIENT);
      }

      if (i > this.nextSoundTime && this.isActive()) {
         this.nextSoundTime = i + 60L + (long)this.world.getRandom().nextInt(40);
         this.playSound(SoundEvents.BLOCK_CONDUIT_AMBIENT_SHORT);
      }

      if (this.world.isRemote) {
         this.updateClientTarget();
         this.spawnParticles();
         if (this.isActive()) {
            ++this.activeRotation;
         }
      }

   }

   private boolean shouldBeActive() {
      this.prismarinePositions.clear();

      int j1;
      int k1;
      int l1;
      for(j1 = -1; j1 <= 1; ++j1) {
         for(k1 = -1; k1 <= 1; ++k1) {
            for(l1 = -1; l1 <= 1; ++l1) {
               BlockPos blockpos = this.pos.add(j1, k1, l1);
               if (!this.world.hasWater(blockpos)) {
                  return false;
               }
            }
         }
      }

      for(j1 = -2; j1 <= 2; ++j1) {
         for(k1 = -2; k1 <= 2; ++k1) {
            for(l1 = -2; l1 <= 2; ++l1) {
               int i2 = Math.abs(j1);
               int l = Math.abs(k1);
               int i1 = Math.abs(l1);
               if ((i2 > 1 || l > 1 || i1 > 1) && (j1 == 0 && (l == 2 || i1 == 2) || k1 == 0 && (i2 == 2 || i1 == 2) || l1 == 0 && (i2 == 2 || l == 2))) {
                  BlockPos blockpos1 = this.pos.add(j1, k1, l1);
                  BlockState blockstate = this.world.getBlockState(blockpos1);
                  Iterator var9 = Tags.Blocks.SUPPORTS_CONDUIT.getAllElements().iterator();

                  while(var9.hasNext()) {
                     Block block = (Block)var9.next();
                     if (blockstate.getBlock() == block) {
                        this.prismarinePositions.add(blockpos1);
                     }
                  }
               }
            }
         }
      }

      this.setEyeOpen(this.prismarinePositions.size() >= 42);
      return this.prismarinePositions.size() >= 16;
   }

   private void addEffectsToPlayers() {
      int i = this.prismarinePositions.size();
      int j = i / 7 * 16;
      int k = this.pos.getX();
      int l = this.pos.getY();
      int i1 = this.pos.getZ();
      AxisAlignedBB axisalignedbb = (new AxisAlignedBB((double)k, (double)l, (double)i1, (double)(k + 1), (double)(l + 1), (double)(i1 + 1))).grow((double)j).expand(0.0D, (double)this.world.getHeight(), 0.0D);
      List<PlayerEntity> list = this.world.getEntitiesWithinAABB(PlayerEntity.class, axisalignedbb);
      if (!list.isEmpty()) {
         Iterator var8 = list.iterator();

         while(var8.hasNext()) {
            PlayerEntity playerentity = (PlayerEntity)var8.next();
            if (this.pos.withinDistance(new BlockPos(playerentity), (double)j) && playerentity.isWet()) {
               playerentity.addPotionEffect(new EffectInstance(Effects.CONDUIT_POWER, 260, 0, true, true));
            }
         }
      }

   }

   private void attackMobs() {
      LivingEntity livingentity = this.target;
      int i = this.prismarinePositions.size();
      if (i < 42) {
         this.target = null;
      } else if (this.target == null && this.targetUuid != null) {
         this.target = this.findExistingTarget();
         this.targetUuid = null;
      } else if (this.target == null) {
         List<LivingEntity> list = this.world.getEntitiesWithinAABB(LivingEntity.class, this.getAreaOfEffect(), (p_lambda$attackMobs$0_0_) -> {
            return p_lambda$attackMobs$0_0_ instanceof IMob && p_lambda$attackMobs$0_0_.isWet();
         });
         if (!list.isEmpty()) {
            this.target = (LivingEntity)list.get(this.world.rand.nextInt(list.size()));
         }
      } else if (!this.target.isAlive() || !this.pos.withinDistance(new BlockPos(this.target), 8.0D)) {
         this.target = null;
      }

      if (this.target != null) {
         this.world.playSound((PlayerEntity)null, this.target.func_226277_ct_(), this.target.func_226278_cu_(), this.target.func_226281_cx_(), SoundEvents.BLOCK_CONDUIT_ATTACK_TARGET, SoundCategory.BLOCKS, 1.0F, 1.0F);
         this.target.attackEntityFrom(DamageSource.MAGIC, 4.0F);
      }

      if (livingentity != this.target) {
         BlockState blockstate = this.getBlockState();
         this.world.notifyBlockUpdate(this.pos, blockstate, blockstate, 2);
      }

   }

   private void updateClientTarget() {
      if (this.targetUuid == null) {
         this.target = null;
      } else if (this.target == null || !this.target.getUniqueID().equals(this.targetUuid)) {
         this.target = this.findExistingTarget();
         if (this.target == null) {
            this.targetUuid = null;
         }
      }

   }

   private AxisAlignedBB getAreaOfEffect() {
      int i = this.pos.getX();
      int j = this.pos.getY();
      int k = this.pos.getZ();
      return (new AxisAlignedBB((double)i, (double)j, (double)k, (double)(i + 1), (double)(j + 1), (double)(k + 1))).grow(8.0D);
   }

   @Nullable
   private LivingEntity findExistingTarget() {
      List<LivingEntity> list = this.world.getEntitiesWithinAABB(LivingEntity.class, this.getAreaOfEffect(), (p_lambda$findExistingTarget$1_1_) -> {
         return p_lambda$findExistingTarget$1_1_.getUniqueID().equals(this.targetUuid);
      });
      return list.size() == 1 ? (LivingEntity)list.get(0) : null;
   }

   private void spawnParticles() {
      Random random = this.world.rand;
      double d0 = (double)(MathHelper.sin((float)(this.ticksExisted + 35) * 0.1F) / 2.0F + 0.5F);
      d0 = (d0 * d0 + d0) * 0.30000001192092896D;
      Vec3d vec3d = new Vec3d((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 1.5D + d0, (double)this.pos.getZ() + 0.5D);
      Iterator var5 = this.prismarinePositions.iterator();

      float f4;
      float f5;
      while(var5.hasNext()) {
         BlockPos blockpos = (BlockPos)var5.next();
         if (random.nextInt(50) == 0) {
            f4 = -0.5F + random.nextFloat();
            f5 = -2.0F + random.nextFloat();
            float f2 = -0.5F + random.nextFloat();
            BlockPos blockpos1 = blockpos.subtract(this.pos);
            Vec3d vec3d1 = (new Vec3d((double)f4, (double)f5, (double)f2)).add((double)blockpos1.getX(), (double)blockpos1.getY(), (double)blockpos1.getZ());
            this.world.addParticle(ParticleTypes.NAUTILUS, vec3d.x, vec3d.y, vec3d.z, vec3d1.x, vec3d1.y, vec3d1.z);
         }
      }

      if (this.target != null) {
         Vec3d vec3d2 = new Vec3d(this.target.func_226277_ct_(), this.target.func_226280_cw_(), this.target.func_226281_cx_());
         float f3 = (-0.5F + random.nextFloat()) * (3.0F + this.target.getWidth());
         f4 = -1.0F + random.nextFloat() * this.target.getHeight();
         f5 = (-0.5F + random.nextFloat()) * (3.0F + this.target.getWidth());
         Vec3d vec3d3 = new Vec3d((double)f3, (double)f4, (double)f5);
         this.world.addParticle(ParticleTypes.NAUTILUS, vec3d2.x, vec3d2.y, vec3d2.z, vec3d3.x, vec3d3.y, vec3d3.z);
      }

   }

   public boolean isActive() {
      return this.active;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isEyeOpen() {
      return this.eyeOpen;
   }

   private void setActive(boolean p_205739_1_) {
      if (p_205739_1_ != this.active) {
         this.playSound(p_205739_1_ ? SoundEvents.BLOCK_CONDUIT_ACTIVATE : SoundEvents.BLOCK_CONDUIT_DEACTIVATE);
      }

      this.active = p_205739_1_;
   }

   private void setEyeOpen(boolean p_207736_1_) {
      this.eyeOpen = p_207736_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public float getActiveRotation(float p_205036_1_) {
      return (this.activeRotation + p_205036_1_) * -0.0375F;
   }

   public void playSound(SoundEvent p_205738_1_) {
      this.world.playSound((PlayerEntity)null, this.pos, p_205738_1_, SoundCategory.BLOCKS, 1.0F, 1.0F);
   }

   static {
      field_205042_e = new Block[]{Blocks.PRISMARINE, Blocks.PRISMARINE_BRICKS, Blocks.SEA_LANTERN, Blocks.DARK_PRISMARINE};
   }
}
