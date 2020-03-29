package net.minecraft.entity.passive;

import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.DamageSource;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public abstract class WaterMobEntity extends CreatureEntity {
   protected WaterMobEntity(EntityType<? extends WaterMobEntity> p_i48565_1_, World p_i48565_2_) {
      super(p_i48565_1_, p_i48565_2_);
      this.setPathPriority(PathNodeType.WATER, 0.0F);
   }

   public boolean canBreatheUnderwater() {
      return true;
   }

   public CreatureAttribute getCreatureAttribute() {
      return CreatureAttribute.WATER;
   }

   public boolean isNotColliding(IWorldReader p_205019_1_) {
      return p_205019_1_.func_226668_i_(this);
   }

   public int getTalkInterval() {
      return 120;
   }

   protected int getExperiencePoints(PlayerEntity p_70693_1_) {
      return 1 + this.world.rand.nextInt(3);
   }

   protected void updateAir(int p_209207_1_) {
      if (this.isAlive() && !this.isInWaterOrBubbleColumn()) {
         this.setAir(p_209207_1_ - 1);
         if (this.getAir() == -20) {
            this.setAir(0);
            this.attackEntityFrom(DamageSource.DROWN, 2.0F);
         }
      } else {
         this.setAir(300);
      }

   }

   public void baseTick() {
      int lvt_1_1_ = this.getAir();
      super.baseTick();
      this.updateAir(lvt_1_1_);
   }

   public boolean isPushedByWater() {
      return false;
   }

   public boolean canBeLeashedTo(PlayerEntity p_184652_1_) {
      return false;
   }
}
