package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class BegGoal extends Goal {
   private final WolfEntity wolf;
   private PlayerEntity player;
   private final World world;
   private final float minPlayerDistance;
   private int timeoutCounter;
   private final EntityPredicate field_220688_f;

   public BegGoal(WolfEntity p_i1617_1_, float p_i1617_2_) {
      this.wolf = p_i1617_1_;
      this.world = p_i1617_1_.world;
      this.minPlayerDistance = p_i1617_2_;
      this.field_220688_f = (new EntityPredicate()).setDistance((double)p_i1617_2_).allowInvulnerable().allowFriendlyFire().setSkipAttackChecks();
      this.setMutexFlags(EnumSet.of(Goal.Flag.LOOK));
   }

   public boolean shouldExecute() {
      this.player = this.world.getClosestPlayer(this.field_220688_f, this.wolf);
      return this.player == null ? false : this.hasTemptationItemInHand(this.player);
   }

   public boolean shouldContinueExecuting() {
      if (!this.player.isAlive()) {
         return false;
      } else if (this.wolf.getDistanceSq(this.player) > (double)(this.minPlayerDistance * this.minPlayerDistance)) {
         return false;
      } else {
         return this.timeoutCounter > 0 && this.hasTemptationItemInHand(this.player);
      }
   }

   public void startExecuting() {
      this.wolf.setBegging(true);
      this.timeoutCounter = 40 + this.wolf.getRNG().nextInt(40);
   }

   public void resetTask() {
      this.wolf.setBegging(false);
      this.player = null;
   }

   public void tick() {
      this.wolf.getLookController().setLookPosition(this.player.func_226277_ct_(), this.player.func_226280_cw_(), this.player.func_226281_cx_(), 10.0F, (float)this.wolf.getVerticalFaceSpeed());
      --this.timeoutCounter;
   }

   private boolean hasTemptationItemInHand(PlayerEntity p_75382_1_) {
      Hand[] var2 = Hand.values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Hand lvt_5_1_ = var2[var4];
         ItemStack lvt_6_1_ = p_75382_1_.getHeldItem(lvt_5_1_);
         if (this.wolf.isTamed() && lvt_6_1_.getItem() == Items.BONE) {
            return true;
         }

         if (this.wolf.isBreedingItem(lvt_6_1_)) {
            return true;
         }
      }

      return false;
   }
}
