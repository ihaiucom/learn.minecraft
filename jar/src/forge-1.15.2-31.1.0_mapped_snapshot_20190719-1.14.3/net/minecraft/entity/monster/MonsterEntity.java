package net.minecraft.entity.monster;

import java.util.Random;
import java.util.function.Predicate;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ShootableItem;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

public abstract class MonsterEntity extends CreatureEntity implements IMob {
   protected MonsterEntity(EntityType<? extends MonsterEntity> p_i48553_1_, World p_i48553_2_) {
      super(p_i48553_1_, p_i48553_2_);
      this.experienceValue = 5;
   }

   public SoundCategory getSoundCategory() {
      return SoundCategory.HOSTILE;
   }

   public void livingTick() {
      this.updateArmSwingProgress();
      this.func_213623_ec();
      super.livingTick();
   }

   protected void func_213623_ec() {
      float lvt_1_1_ = this.getBrightness();
      if (lvt_1_1_ > 0.5F) {
         this.idleTime += 2;
      }

   }

   protected boolean func_225511_J_() {
      return true;
   }

   protected SoundEvent getSwimSound() {
      return SoundEvents.ENTITY_HOSTILE_SWIM;
   }

   protected SoundEvent getSplashSound() {
      return SoundEvents.ENTITY_HOSTILE_SPLASH;
   }

   public boolean attackEntityFrom(DamageSource p_70097_1_, float p_70097_2_) {
      return this.isInvulnerableTo(p_70097_1_) ? false : super.attackEntityFrom(p_70097_1_, p_70097_2_);
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.ENTITY_HOSTILE_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_HOSTILE_DEATH;
   }

   protected SoundEvent getFallSound(int p_184588_1_) {
      return p_184588_1_ > 4 ? SoundEvents.ENTITY_HOSTILE_BIG_FALL : SoundEvents.ENTITY_HOSTILE_SMALL_FALL;
   }

   public float getBlockPathWeight(BlockPos p_205022_1_, IWorldReader p_205022_2_) {
      return 0.5F - p_205022_2_.getBrightness(p_205022_1_);
   }

   public static boolean func_223323_a(IWorld p_223323_0_, BlockPos p_223323_1_, Random p_223323_2_) {
      if (p_223323_0_.func_226658_a_(LightType.SKY, p_223323_1_) > p_223323_2_.nextInt(32)) {
         return false;
      } else {
         int lvt_3_1_ = p_223323_0_.getWorld().isThundering() ? p_223323_0_.getNeighborAwareLightSubtracted(p_223323_1_, 10) : p_223323_0_.getLight(p_223323_1_);
         return lvt_3_1_ <= p_223323_2_.nextInt(8);
      }
   }

   public static boolean func_223325_c(EntityType<? extends MonsterEntity> p_223325_0_, IWorld p_223325_1_, SpawnReason p_223325_2_, BlockPos p_223325_3_, Random p_223325_4_) {
      return p_223325_1_.getDifficulty() != Difficulty.PEACEFUL && func_223323_a(p_223325_1_, p_223325_3_, p_223325_4_) && func_223315_a(p_223325_0_, p_223325_1_, p_223325_2_, p_223325_3_, p_223325_4_);
   }

   public static boolean func_223324_d(EntityType<? extends MonsterEntity> p_223324_0_, IWorld p_223324_1_, SpawnReason p_223324_2_, BlockPos p_223324_3_, Random p_223324_4_) {
      return p_223324_1_.getDifficulty() != Difficulty.PEACEFUL && func_223315_a(p_223324_0_, p_223324_1_, p_223324_2_, p_223324_3_, p_223324_4_);
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttributes().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
   }

   protected boolean canDropLoot() {
      return true;
   }

   public boolean isPreventingPlayerRest(PlayerEntity p_191990_1_) {
      return true;
   }

   public ItemStack findAmmo(ItemStack p_213356_1_) {
      if (p_213356_1_.getItem() instanceof ShootableItem) {
         Predicate<ItemStack> lvt_2_1_ = ((ShootableItem)p_213356_1_.getItem()).getAmmoPredicate();
         ItemStack lvt_3_1_ = ShootableItem.getHeldAmmo(this, lvt_2_1_);
         return lvt_3_1_.isEmpty() ? new ItemStack(Items.ARROW) : lvt_3_1_;
      } else {
         return ItemStack.EMPTY;
      }
   }
}
