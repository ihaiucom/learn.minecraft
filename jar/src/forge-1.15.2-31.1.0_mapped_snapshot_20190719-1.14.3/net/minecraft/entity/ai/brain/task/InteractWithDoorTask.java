package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.pathfinding.Path;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.server.ServerWorld;

public class InteractWithDoorTask extends Task<LivingEntity> {
   public InteractWithDoorTask() {
      super(ImmutableMap.of(MemoryModuleType.PATH, MemoryModuleStatus.VALUE_PRESENT, MemoryModuleType.INTERACTABLE_DOORS, MemoryModuleStatus.VALUE_PRESENT, MemoryModuleType.field_225462_q, MemoryModuleStatus.REGISTERED));
   }

   protected void startExecuting(ServerWorld p_212831_1_, LivingEntity p_212831_2_, long p_212831_3_) {
      Brain<?> lvt_5_1_ = p_212831_2_.getBrain();
      Path lvt_6_1_ = (Path)lvt_5_1_.getMemory(MemoryModuleType.PATH).get();
      List<GlobalPos> lvt_7_1_ = (List)lvt_5_1_.getMemory(MemoryModuleType.INTERACTABLE_DOORS).get();
      List<BlockPos> lvt_8_1_ = (List)lvt_6_1_.func_215746_d().stream().map((p_220435_0_) -> {
         return new BlockPos(p_220435_0_.x, p_220435_0_.y, p_220435_0_.z);
      }).collect(Collectors.toList());
      Set<BlockPos> lvt_9_1_ = this.func_220436_a(p_212831_1_, lvt_7_1_, lvt_8_1_);
      int lvt_10_1_ = lvt_6_1_.getCurrentPathIndex() - 1;
      this.func_220434_a(p_212831_1_, lvt_8_1_, lvt_9_1_, lvt_10_1_, p_212831_2_, lvt_5_1_);
   }

   private Set<BlockPos> func_220436_a(ServerWorld p_220436_1_, List<GlobalPos> p_220436_2_, List<BlockPos> p_220436_3_) {
      Stream var10000 = p_220436_2_.stream().filter((p_220432_1_) -> {
         return p_220432_1_.getDimension() == p_220436_1_.getDimension().getType();
      }).map(GlobalPos::getPos);
      p_220436_3_.getClass();
      return (Set)var10000.filter(p_220436_3_::contains).collect(Collectors.toSet());
   }

   private void func_220434_a(ServerWorld p_220434_1_, List<BlockPos> p_220434_2_, Set<BlockPos> p_220434_3_, int p_220434_4_, LivingEntity p_220434_5_, Brain<?> p_220434_6_) {
      p_220434_3_.forEach((p_225447_4_) -> {
         int lvt_5_1_ = p_220434_2_.indexOf(p_225447_4_);
         BlockState lvt_6_1_ = p_220434_1_.getBlockState(p_225447_4_);
         Block lvt_7_1_ = lvt_6_1_.getBlock();
         if (BlockTags.WOODEN_DOORS.contains(lvt_7_1_) && lvt_7_1_ instanceof DoorBlock) {
            boolean lvt_8_1_ = lvt_5_1_ >= p_220434_4_;
            ((DoorBlock)lvt_7_1_).toggleDoor(p_220434_1_, p_225447_4_, lvt_8_1_);
            GlobalPos lvt_9_1_ = GlobalPos.of(p_220434_1_.getDimension().getType(), p_225447_4_);
            if (!p_220434_6_.getMemory(MemoryModuleType.field_225462_q).isPresent() && lvt_8_1_) {
               p_220434_6_.setMemory(MemoryModuleType.field_225462_q, (Object)Sets.newHashSet(new GlobalPos[]{lvt_9_1_}));
            } else {
               p_220434_6_.getMemory(MemoryModuleType.field_225462_q).ifPresent((p_225450_2_) -> {
                  if (lvt_8_1_) {
                     p_225450_2_.add(lvt_9_1_);
                  } else {
                     p_225450_2_.remove(lvt_9_1_);
                  }

               });
            }
         }

      });
      func_225449_a(p_220434_1_, p_220434_2_, p_220434_4_, p_220434_5_, p_220434_6_);
   }

   public static void func_225449_a(ServerWorld p_225449_0_, List<BlockPos> p_225449_1_, int p_225449_2_, LivingEntity p_225449_3_, Brain<?> p_225449_4_) {
      p_225449_4_.getMemory(MemoryModuleType.field_225462_q).ifPresent((p_225451_4_) -> {
         Iterator lvt_5_1_ = p_225451_4_.iterator();

         while(lvt_5_1_.hasNext()) {
            GlobalPos lvt_6_1_ = (GlobalPos)lvt_5_1_.next();
            BlockPos lvt_7_1_ = lvt_6_1_.getPos();
            int lvt_8_1_ = p_225449_1_.indexOf(lvt_7_1_);
            if (p_225449_0_.getDimension().getType() != lvt_6_1_.getDimension()) {
               lvt_5_1_.remove();
            } else {
               BlockState lvt_9_1_ = p_225449_0_.getBlockState(lvt_7_1_);
               Block lvt_10_1_ = lvt_9_1_.getBlock();
               if (BlockTags.WOODEN_DOORS.contains(lvt_10_1_) && lvt_10_1_ instanceof DoorBlock && lvt_8_1_ < p_225449_2_ && lvt_7_1_.withinDistance(p_225449_3_.getPositionVec(), 4.0D)) {
                  ((DoorBlock)lvt_10_1_).toggleDoor(p_225449_0_, lvt_7_1_, false);
                  lvt_5_1_.remove();
               }
            }
         }

      });
   }
}
