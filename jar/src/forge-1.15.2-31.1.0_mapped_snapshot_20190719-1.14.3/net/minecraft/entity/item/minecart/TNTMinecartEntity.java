package net.minecraft.entity.item.minecart;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.fluid.IFluidState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.GameRules;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class TNTMinecartEntity extends AbstractMinecartEntity {
   private int minecartTNTFuse = -1;

   public TNTMinecartEntity(EntityType<? extends TNTMinecartEntity> p_i50112_1_, World p_i50112_2_) {
      super(p_i50112_1_, p_i50112_2_);
   }

   public TNTMinecartEntity(World p_i1728_1_, double p_i1728_2_, double p_i1728_4_, double p_i1728_6_) {
      super(EntityType.TNT_MINECART, p_i1728_1_, p_i1728_2_, p_i1728_4_, p_i1728_6_);
   }

   public AbstractMinecartEntity.Type getMinecartType() {
      return AbstractMinecartEntity.Type.TNT;
   }

   public BlockState getDefaultDisplayTile() {
      return Blocks.TNT.getDefaultState();
   }

   public void tick() {
      super.tick();
      if (this.minecartTNTFuse > 0) {
         --this.minecartTNTFuse;
         this.world.addParticle(ParticleTypes.SMOKE, this.func_226277_ct_(), this.func_226278_cu_() + 0.5D, this.func_226281_cx_(), 0.0D, 0.0D, 0.0D);
      } else if (this.minecartTNTFuse == 0) {
         this.explodeCart(func_213296_b(this.getMotion()));
      }

      if (this.collidedHorizontally) {
         double lvt_1_1_ = func_213296_b(this.getMotion());
         if (lvt_1_1_ >= 0.009999999776482582D) {
            this.explodeCart(lvt_1_1_);
         }
      }

   }

   public boolean attackEntityFrom(DamageSource p_70097_1_, float p_70097_2_) {
      Entity lvt_3_1_ = p_70097_1_.getImmediateSource();
      if (lvt_3_1_ instanceof AbstractArrowEntity) {
         AbstractArrowEntity lvt_4_1_ = (AbstractArrowEntity)lvt_3_1_;
         if (lvt_4_1_.isBurning()) {
            this.explodeCart(lvt_4_1_.getMotion().lengthSquared());
         }
      }

      return super.attackEntityFrom(p_70097_1_, p_70097_2_);
   }

   public void killMinecart(DamageSource p_94095_1_) {
      double lvt_2_1_ = func_213296_b(this.getMotion());
      if (!p_94095_1_.isFireDamage() && !p_94095_1_.isExplosion() && lvt_2_1_ < 0.009999999776482582D) {
         super.killMinecart(p_94095_1_);
         if (!p_94095_1_.isExplosion() && this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
            this.entityDropItem(Blocks.TNT);
         }

      } else {
         if (this.minecartTNTFuse < 0) {
            this.ignite();
            this.minecartTNTFuse = this.rand.nextInt(20) + this.rand.nextInt(20);
         }

      }
   }

   protected void explodeCart(double p_94103_1_) {
      if (!this.world.isRemote) {
         double lvt_3_1_ = Math.sqrt(p_94103_1_);
         if (lvt_3_1_ > 5.0D) {
            lvt_3_1_ = 5.0D;
         }

         this.world.createExplosion(this, this.func_226277_ct_(), this.func_226278_cu_(), this.func_226281_cx_(), (float)(4.0D + this.rand.nextDouble() * 1.5D * lvt_3_1_), Explosion.Mode.BREAK);
         this.remove();
      }

   }

   public boolean func_225503_b_(float p_225503_1_, float p_225503_2_) {
      if (p_225503_1_ >= 3.0F) {
         float lvt_3_1_ = p_225503_1_ / 10.0F;
         this.explodeCart((double)(lvt_3_1_ * lvt_3_1_));
      }

      return super.func_225503_b_(p_225503_1_, p_225503_2_);
   }

   public void onActivatorRailPass(int p_96095_1_, int p_96095_2_, int p_96095_3_, boolean p_96095_4_) {
      if (p_96095_4_ && this.minecartTNTFuse < 0) {
         this.ignite();
      }

   }

   @OnlyIn(Dist.CLIENT)
   public void handleStatusUpdate(byte p_70103_1_) {
      if (p_70103_1_ == 10) {
         this.ignite();
      } else {
         super.handleStatusUpdate(p_70103_1_);
      }

   }

   public void ignite() {
      this.minecartTNTFuse = 80;
      if (!this.world.isRemote) {
         this.world.setEntityState(this, (byte)10);
         if (!this.isSilent()) {
            this.world.playSound((PlayerEntity)null, this.func_226277_ct_(), this.func_226278_cu_(), this.func_226281_cx_(), SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, 1.0F);
         }
      }

   }

   @OnlyIn(Dist.CLIENT)
   public int getFuseTicks() {
      return this.minecartTNTFuse;
   }

   public boolean isIgnited() {
      return this.minecartTNTFuse > -1;
   }

   public float getExplosionResistance(Explosion p_180428_1_, IBlockReader p_180428_2_, BlockPos p_180428_3_, BlockState p_180428_4_, IFluidState p_180428_5_, float p_180428_6_) {
      return !this.isIgnited() || !p_180428_4_.isIn(BlockTags.RAILS) && !p_180428_2_.getBlockState(p_180428_3_.up()).isIn(BlockTags.RAILS) ? super.getExplosionResistance(p_180428_1_, p_180428_2_, p_180428_3_, p_180428_4_, p_180428_5_, p_180428_6_) : 0.0F;
   }

   public boolean canExplosionDestroyBlock(Explosion p_174816_1_, IBlockReader p_174816_2_, BlockPos p_174816_3_, BlockState p_174816_4_, float p_174816_5_) {
      return !this.isIgnited() || !p_174816_4_.isIn(BlockTags.RAILS) && !p_174816_2_.getBlockState(p_174816_3_.up()).isIn(BlockTags.RAILS) ? super.canExplosionDestroyBlock(p_174816_1_, p_174816_2_, p_174816_3_, p_174816_4_, p_174816_5_) : false;
   }

   protected void readAdditional(CompoundNBT p_70037_1_) {
      super.readAdditional(p_70037_1_);
      if (p_70037_1_.contains("TNTFuse", 99)) {
         this.minecartTNTFuse = p_70037_1_.getInt("TNTFuse");
      }

   }

   protected void writeAdditional(CompoundNBT p_213281_1_) {
      super.writeAdditional(p_213281_1_);
      p_213281_1_.putInt("TNTFuse", this.minecartTNTFuse);
   }
}
