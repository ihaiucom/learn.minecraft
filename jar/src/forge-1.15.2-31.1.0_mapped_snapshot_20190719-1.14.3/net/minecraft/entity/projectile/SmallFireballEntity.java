package net.minecraft.entity.projectile;

import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;

public class SmallFireballEntity extends AbstractFireballEntity {
   public SmallFireballEntity(EntityType<? extends SmallFireballEntity> p_i50160_1_, World p_i50160_2_) {
      super(p_i50160_1_, p_i50160_2_);
   }

   public SmallFireballEntity(World p_i1771_1_, LivingEntity p_i1771_2_, double p_i1771_3_, double p_i1771_5_, double p_i1771_7_) {
      super(EntityType.SMALL_FIREBALL, p_i1771_2_, p_i1771_3_, p_i1771_5_, p_i1771_7_, p_i1771_1_);
   }

   public SmallFireballEntity(World p_i1772_1_, double p_i1772_2_, double p_i1772_4_, double p_i1772_6_, double p_i1772_8_, double p_i1772_10_, double p_i1772_12_) {
      super(EntityType.SMALL_FIREBALL, p_i1772_2_, p_i1772_4_, p_i1772_6_, p_i1772_8_, p_i1772_10_, p_i1772_12_, p_i1772_1_);
   }

   protected void onImpact(RayTraceResult p_70227_1_) {
      super.onImpact(p_70227_1_);
      if (!this.world.isRemote) {
         if (p_70227_1_.getType() == RayTraceResult.Type.ENTITY) {
            Entity entity = ((EntityRayTraceResult)p_70227_1_).getEntity();
            if (!entity.isImmuneToFire()) {
               int i = entity.func_223314_ad();
               entity.setFire(5);
               boolean flag = entity.attackEntityFrom(DamageSource.causeFireballDamage(this, this.shootingEntity), 5.0F);
               if (flag) {
                  this.applyEnchantments(this.shootingEntity, entity);
               } else {
                  entity.func_223308_g(i);
               }
            }
         } else if (this.shootingEntity == null || !(this.shootingEntity instanceof MobEntity) || ForgeEventFactory.getMobGriefingEvent(this.world, this.shootingEntity)) {
            BlockRayTraceResult blockraytraceresult = (BlockRayTraceResult)p_70227_1_;
            BlockPos blockpos = blockraytraceresult.getPos().offset(blockraytraceresult.getFace());
            if (this.world.isAirBlock(blockpos)) {
               this.world.setBlockState(blockpos, Blocks.FIRE.getDefaultState());
            }
         }

         this.remove();
      }

   }

   public boolean canBeCollidedWith() {
      return false;
   }

   public boolean attackEntityFrom(DamageSource p_70097_1_, float p_70097_2_) {
      return false;
   }
}
