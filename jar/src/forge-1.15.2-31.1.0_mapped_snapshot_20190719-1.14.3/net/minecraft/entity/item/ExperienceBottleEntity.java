package net.minecraft.entity.item;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class ExperienceBottleEntity extends ProjectileItemEntity {
   public ExperienceBottleEntity(EntityType<? extends ExperienceBottleEntity> p_i50152_1_, World p_i50152_2_) {
      super(p_i50152_1_, p_i50152_2_);
   }

   public ExperienceBottleEntity(World p_i1786_1_, LivingEntity p_i1786_2_) {
      super(EntityType.EXPERIENCE_BOTTLE, p_i1786_2_, p_i1786_1_);
   }

   public ExperienceBottleEntity(World p_i1787_1_, double p_i1787_2_, double p_i1787_4_, double p_i1787_6_) {
      super(EntityType.EXPERIENCE_BOTTLE, p_i1787_2_, p_i1787_4_, p_i1787_6_, p_i1787_1_);
   }

   protected Item func_213885_i() {
      return Items.EXPERIENCE_BOTTLE;
   }

   protected float getGravityVelocity() {
      return 0.07F;
   }

   protected void onImpact(RayTraceResult p_70184_1_) {
      if (!this.world.isRemote) {
         this.world.playEvent(2002, new BlockPos(this), PotionUtils.getPotionColor(Potions.WATER));
         int lvt_2_1_ = 3 + this.world.rand.nextInt(5) + this.world.rand.nextInt(5);

         while(lvt_2_1_ > 0) {
            int lvt_3_1_ = ExperienceOrbEntity.getXPSplit(lvt_2_1_);
            lvt_2_1_ -= lvt_3_1_;
            this.world.addEntity(new ExperienceOrbEntity(this.world, this.func_226277_ct_(), this.func_226278_cu_(), this.func_226281_cx_(), lvt_3_1_));
         }

         this.remove();
      }

   }
}
