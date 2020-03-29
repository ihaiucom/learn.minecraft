package net.minecraft.entity.monster;

import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.OpenDoorGoal;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class AbstractIllagerEntity extends AbstractRaiderEntity {
   protected AbstractIllagerEntity(EntityType<? extends AbstractIllagerEntity> p_i48556_1_, World p_i48556_2_) {
      super(p_i48556_1_, p_i48556_2_);
   }

   protected void registerGoals() {
      super.registerGoals();
   }

   public CreatureAttribute getCreatureAttribute() {
      return CreatureAttribute.ILLAGER;
   }

   @OnlyIn(Dist.CLIENT)
   public AbstractIllagerEntity.ArmPose getArmPose() {
      return AbstractIllagerEntity.ArmPose.CROSSED;
   }

   public class RaidOpenDoorGoal extends OpenDoorGoal {
      public RaidOpenDoorGoal(AbstractRaiderEntity p_i51284_2_) {
         super(p_i51284_2_, false);
      }

      public boolean shouldExecute() {
         return super.shouldExecute() && AbstractIllagerEntity.this.isRaidActive();
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static enum ArmPose {
      CROSSED,
      ATTACKING,
      SPELLCASTING,
      BOW_AND_ARROW,
      CROSSBOW_HOLD,
      CROSSBOW_CHARGE,
      CELEBRATING,
      NEUTRAL;
   }
}
