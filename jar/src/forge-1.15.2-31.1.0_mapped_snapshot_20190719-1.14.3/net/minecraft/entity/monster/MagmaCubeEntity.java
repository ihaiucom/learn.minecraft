package net.minecraft.entity.monster;

import java.util.Random;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.SpawnReason;
import net.minecraft.fluid.Fluid;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTables;
import net.minecraftforge.common.ForgeHooks;

public class MagmaCubeEntity extends SlimeEntity {
   public MagmaCubeEntity(EntityType<? extends MagmaCubeEntity> p_i50202_1_, World p_i50202_2_) {
      super(p_i50202_1_, p_i50202_2_);
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.20000000298023224D);
   }

   public static boolean func_223367_b(EntityType<MagmaCubeEntity> p_223367_0_, IWorld p_223367_1_, SpawnReason p_223367_2_, BlockPos p_223367_3_, Random p_223367_4_) {
      return p_223367_1_.getDifficulty() != Difficulty.PEACEFUL;
   }

   public boolean isNotColliding(IWorldReader p_205019_1_) {
      return p_205019_1_.func_226668_i_(this) && !p_205019_1_.containsAnyLiquid(this.getBoundingBox());
   }

   protected void setSlimeSize(int p_70799_1_, boolean p_70799_2_) {
      super.setSlimeSize(p_70799_1_, p_70799_2_);
      this.getAttribute(SharedMonsterAttributes.ARMOR).setBaseValue((double)(p_70799_1_ * 3));
   }

   public float getBrightness() {
      return 1.0F;
   }

   protected IParticleData getSquishParticle() {
      return ParticleTypes.FLAME;
   }

   protected ResourceLocation getLootTable() {
      return this.isSmallSlime() ? LootTables.EMPTY : this.getType().getLootTable();
   }

   public boolean isBurning() {
      return false;
   }

   protected int getJumpDelay() {
      return super.getJumpDelay() * 4;
   }

   protected void alterSquishAmount() {
      this.squishAmount *= 0.9F;
   }

   protected void jump() {
      Vec3d vec3d = this.getMotion();
      this.setMotion(vec3d.x, (double)(this.getJumpUpwardsMotion() + (float)this.getSlimeSize() * 0.1F), vec3d.z);
      this.isAirBorne = true;
      ForgeHooks.onLivingJump(this);
   }

   protected void handleFluidJump(Tag<Fluid> p_180466_1_) {
      if (p_180466_1_ == FluidTags.LAVA) {
         Vec3d vec3d = this.getMotion();
         this.setMotion(vec3d.x, (double)(0.22F + (float)this.getSlimeSize() * 0.05F), vec3d.z);
         this.isAirBorne = true;
      } else {
         super.handleFluidJump(p_180466_1_);
      }

   }

   public boolean func_225503_b_(float p_225503_1_, float p_225503_2_) {
      return false;
   }

   protected boolean canDamagePlayer() {
      return this.isServerWorld();
   }

   protected float func_225512_er_() {
      return super.func_225512_er_() + 2.0F;
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return this.isSmallSlime() ? SoundEvents.ENTITY_MAGMA_CUBE_HURT_SMALL : SoundEvents.ENTITY_MAGMA_CUBE_HURT;
   }

   protected SoundEvent getDeathSound() {
      return this.isSmallSlime() ? SoundEvents.ENTITY_MAGMA_CUBE_DEATH_SMALL : SoundEvents.ENTITY_MAGMA_CUBE_DEATH;
   }

   protected SoundEvent getSquishSound() {
      return this.isSmallSlime() ? SoundEvents.ENTITY_MAGMA_CUBE_SQUISH_SMALL : SoundEvents.ENTITY_MAGMA_CUBE_SQUISH;
   }

   protected SoundEvent getJumpSound() {
      return SoundEvents.ENTITY_MAGMA_CUBE_JUMP;
   }
}
