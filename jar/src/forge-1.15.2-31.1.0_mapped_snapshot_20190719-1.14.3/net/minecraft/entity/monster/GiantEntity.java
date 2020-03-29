package net.minecraft.entity.monster;

import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Pose;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class GiantEntity extends MonsterEntity {
   public GiantEntity(EntityType<? extends GiantEntity> p_i50205_1_, World p_i50205_2_) {
      super(p_i50205_1_, p_i50205_2_);
   }

   protected float getStandingEyeHeight(Pose p_213348_1_, EntitySize p_213348_2_) {
      return 10.440001F;
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(100.0D);
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5D);
      this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(50.0D);
   }

   public float getBlockPathWeight(BlockPos p_205022_1_, IWorldReader p_205022_2_) {
      return p_205022_2_.getBrightness(p_205022_1_) - 0.5F;
   }
}
