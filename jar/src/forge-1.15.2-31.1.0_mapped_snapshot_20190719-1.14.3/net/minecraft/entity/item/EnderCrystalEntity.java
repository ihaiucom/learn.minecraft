package net.minecraft.entity.item;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.world.dimension.EndDimension;
import net.minecraft.world.end.DragonFightManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EnderCrystalEntity extends Entity {
   private static final DataParameter<Optional<BlockPos>> BEAM_TARGET;
   private static final DataParameter<Boolean> SHOW_BOTTOM;
   public int innerRotation;

   public EnderCrystalEntity(EntityType<? extends EnderCrystalEntity> p_i50231_1_, World p_i50231_2_) {
      super(p_i50231_1_, p_i50231_2_);
      this.preventEntitySpawning = true;
      this.innerRotation = this.rand.nextInt(100000);
   }

   public EnderCrystalEntity(World p_i1699_1_, double p_i1699_2_, double p_i1699_4_, double p_i1699_6_) {
      this(EntityType.END_CRYSTAL, p_i1699_1_);
      this.setPosition(p_i1699_2_, p_i1699_4_, p_i1699_6_);
   }

   protected boolean func_225502_at_() {
      return false;
   }

   protected void registerData() {
      this.getDataManager().register(BEAM_TARGET, Optional.empty());
      this.getDataManager().register(SHOW_BOTTOM, true);
   }

   public void tick() {
      ++this.innerRotation;
      if (!this.world.isRemote) {
         BlockPos lvt_1_1_ = new BlockPos(this);
         if (this.world.dimension instanceof EndDimension && this.world.getBlockState(lvt_1_1_).isAir()) {
            this.world.setBlockState(lvt_1_1_, Blocks.FIRE.getDefaultState());
         }
      }

   }

   protected void writeAdditional(CompoundNBT p_213281_1_) {
      if (this.getBeamTarget() != null) {
         p_213281_1_.put("BeamTarget", NBTUtil.writeBlockPos(this.getBeamTarget()));
      }

      p_213281_1_.putBoolean("ShowBottom", this.shouldShowBottom());
   }

   protected void readAdditional(CompoundNBT p_70037_1_) {
      if (p_70037_1_.contains("BeamTarget", 10)) {
         this.setBeamTarget(NBTUtil.readBlockPos(p_70037_1_.getCompound("BeamTarget")));
      }

      if (p_70037_1_.contains("ShowBottom", 1)) {
         this.setShowBottom(p_70037_1_.getBoolean("ShowBottom"));
      }

   }

   public boolean canBeCollidedWith() {
      return true;
   }

   public boolean attackEntityFrom(DamageSource p_70097_1_, float p_70097_2_) {
      if (this.isInvulnerableTo(p_70097_1_)) {
         return false;
      } else if (p_70097_1_.getTrueSource() instanceof EnderDragonEntity) {
         return false;
      } else {
         if (!this.removed && !this.world.isRemote) {
            this.remove();
            if (!p_70097_1_.isExplosion()) {
               this.world.createExplosion((Entity)null, this.func_226277_ct_(), this.func_226278_cu_(), this.func_226281_cx_(), 6.0F, Explosion.Mode.DESTROY);
            }

            this.onCrystalDestroyed(p_70097_1_);
         }

         return true;
      }
   }

   public void onKillCommand() {
      this.onCrystalDestroyed(DamageSource.GENERIC);
      super.onKillCommand();
   }

   private void onCrystalDestroyed(DamageSource p_184519_1_) {
      if (this.world.dimension instanceof EndDimension) {
         EndDimension lvt_2_1_ = (EndDimension)this.world.dimension;
         DragonFightManager lvt_3_1_ = lvt_2_1_.getDragonFightManager();
         if (lvt_3_1_ != null) {
            lvt_3_1_.onCrystalDestroyed(this, p_184519_1_);
         }
      }

   }

   public void setBeamTarget(@Nullable BlockPos p_184516_1_) {
      this.getDataManager().set(BEAM_TARGET, Optional.ofNullable(p_184516_1_));
   }

   @Nullable
   public BlockPos getBeamTarget() {
      return (BlockPos)((Optional)this.getDataManager().get(BEAM_TARGET)).orElse((Object)null);
   }

   public void setShowBottom(boolean p_184517_1_) {
      this.getDataManager().set(SHOW_BOTTOM, p_184517_1_);
   }

   public boolean shouldShowBottom() {
      return (Boolean)this.getDataManager().get(SHOW_BOTTOM);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isInRangeToRenderDist(double p_70112_1_) {
      return super.isInRangeToRenderDist(p_70112_1_) || this.getBeamTarget() != null;
   }

   public IPacket<?> createSpawnPacket() {
      return new SSpawnObjectPacket(this);
   }

   static {
      BEAM_TARGET = EntityDataManager.createKey(EnderCrystalEntity.class, DataSerializers.OPTIONAL_BLOCK_POS);
      SHOW_BOTTOM = EntityDataManager.createKey(EnderCrystalEntity.class, DataSerializers.BOOLEAN);
   }
}
