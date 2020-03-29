package net.minecraft.entity.ai.brain;

import java.util.Comparator;
import java.util.stream.Stream;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.memory.WalkTarget;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.EntityPosWrapper;
import net.minecraft.util.math.SectionPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.server.ServerWorld;

public class BrainUtil {
   public static void func_220618_a(LivingEntity p_220618_0_, LivingEntity p_220618_1_) {
      func_220616_b(p_220618_0_, p_220618_1_);
      func_220626_d(p_220618_0_, p_220618_1_);
   }

   public static boolean canSee(Brain<?> p_220619_0_, LivingEntity p_220619_1_) {
      return p_220619_0_.getMemory(MemoryModuleType.VISIBLE_MOBS).filter((p_220614_1_) -> {
         return p_220614_1_.contains(p_220619_1_);
      }).isPresent();
   }

   public static boolean isCorrectVisibleType(Brain<?> p_220623_0_, MemoryModuleType<? extends LivingEntity> p_220623_1_, EntityType<?> p_220623_2_) {
      return p_220623_0_.getMemory(p_220623_1_).filter((p_220622_1_) -> {
         return p_220622_1_.getType() == p_220623_2_;
      }).filter(LivingEntity::isAlive).filter((p_220615_1_) -> {
         return canSee(p_220623_0_, p_220615_1_);
      }).isPresent();
   }

   public static void func_220616_b(LivingEntity p_220616_0_, LivingEntity p_220616_1_) {
      lookAt(p_220616_0_, p_220616_1_);
      lookAt(p_220616_1_, p_220616_0_);
   }

   public static void lookAt(LivingEntity p_220625_0_, LivingEntity p_220625_1_) {
      p_220625_0_.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, (Object)(new EntityPosWrapper(p_220625_1_)));
   }

   public static void func_220626_d(LivingEntity p_220626_0_, LivingEntity p_220626_1_) {
      int lvt_2_1_ = true;
      approach(p_220626_0_, p_220626_1_, 2);
      approach(p_220626_1_, p_220626_0_, 2);
   }

   public static void approach(LivingEntity p_220621_0_, LivingEntity p_220621_1_, int p_220621_2_) {
      float lvt_3_1_ = (float)p_220621_0_.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getValue();
      EntityPosWrapper lvt_4_1_ = new EntityPosWrapper(p_220621_1_);
      WalkTarget lvt_5_1_ = new WalkTarget(lvt_4_1_, lvt_3_1_, p_220621_2_);
      p_220621_0_.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, (Object)lvt_4_1_);
      p_220621_0_.getBrain().setMemory(MemoryModuleType.WALK_TARGET, (Object)lvt_5_1_);
   }

   public static void throwItemAt(LivingEntity p_220624_0_, ItemStack p_220624_1_, LivingEntity p_220624_2_) {
      double lvt_3_1_ = p_220624_0_.func_226280_cw_() - 0.30000001192092896D;
      ItemEntity lvt_5_1_ = new ItemEntity(p_220624_0_.world, p_220624_0_.func_226277_ct_(), lvt_3_1_, p_220624_0_.func_226281_cx_(), p_220624_1_);
      BlockPos lvt_6_1_ = new BlockPos(p_220624_2_);
      BlockPos lvt_7_1_ = new BlockPos(p_220624_0_);
      float lvt_8_1_ = 0.3F;
      Vec3d lvt_9_1_ = new Vec3d(lvt_6_1_.subtract(lvt_7_1_));
      lvt_9_1_ = lvt_9_1_.normalize().scale(0.30000001192092896D);
      lvt_5_1_.setMotion(lvt_9_1_);
      lvt_5_1_.setDefaultPickupDelay();
      p_220624_0_.world.addEntity(lvt_5_1_);
   }

   public static SectionPos func_220617_a(ServerWorld p_220617_0_, SectionPos p_220617_1_, int p_220617_2_) {
      int lvt_3_1_ = p_220617_0_.func_217486_a(p_220617_1_);
      Stream var10000 = SectionPos.getAllInBox(p_220617_1_, p_220617_2_).filter((p_220620_2_) -> {
         return p_220617_0_.func_217486_a(p_220620_2_) < lvt_3_1_;
      });
      p_220617_0_.getClass();
      return (SectionPos)var10000.min(Comparator.comparingInt(p_220617_0_::func_217486_a)).orElse(p_220617_1_);
   }
}
