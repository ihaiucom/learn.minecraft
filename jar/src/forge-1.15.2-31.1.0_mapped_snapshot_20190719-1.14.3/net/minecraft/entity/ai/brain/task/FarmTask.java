package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropsBlock;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.memory.WalkTarget;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPosWrapper;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;
import net.minecraftforge.event.ForgeEventFactory;

public class FarmTask extends Task<VillagerEntity> {
   @Nullable
   private BlockPos field_220422_a;
   private boolean field_220423_b;
   private boolean field_220424_c;
   private long field_220425_d;
   private int field_220426_e;
   private final List<BlockPos> field_223518_f = Lists.newArrayList();

   public FarmTask() {
      super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryModuleStatus.VALUE_ABSENT, MemoryModuleType.WALK_TARGET, MemoryModuleStatus.VALUE_ABSENT, MemoryModuleType.SECONDARY_JOB_SITE, MemoryModuleStatus.VALUE_PRESENT));
   }

   protected boolean shouldExecute(ServerWorld p_212832_1_, VillagerEntity p_212832_2_) {
      if (!ForgeEventFactory.getMobGriefingEvent(p_212832_1_, p_212832_2_)) {
         return false;
      } else if (p_212832_2_.getVillagerData().getProfession() != VillagerProfession.FARMER) {
         return false;
      } else {
         this.field_220423_b = p_212832_2_.isFarmItemInInventory();
         this.field_220424_c = false;
         Inventory inventory = p_212832_2_.func_213715_ed();
         int i = inventory.getSizeInventory();

         for(int j = 0; j < i; ++j) {
            ItemStack itemstack = inventory.getStackInSlot(j);
            if (itemstack.isEmpty()) {
               this.field_220424_c = true;
               break;
            }

            if (itemstack.getItem() == Items.WHEAT_SEEDS || itemstack.getItem() == Items.BEETROOT_SEEDS) {
               this.field_220424_c = true;
               break;
            }
         }

         BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable(p_212832_2_);
         this.field_223518_f.clear();

         for(int i1 = -1; i1 <= 1; ++i1) {
            for(int k = -1; k <= 1; ++k) {
               for(int l = -1; l <= 1; ++l) {
                  blockpos$mutable.setPos(p_212832_2_.func_226277_ct_() + (double)i1, p_212832_2_.func_226278_cu_() + (double)k, p_212832_2_.func_226281_cx_() + (double)l);
                  if (this.func_223516_a(blockpos$mutable, p_212832_1_)) {
                     this.field_223518_f.add(new BlockPos(blockpos$mutable));
                  }
               }
            }
         }

         this.field_220422_a = this.func_223517_a(p_212832_1_);
         return (this.field_220423_b || this.field_220424_c) && this.field_220422_a != null;
      }
   }

   @Nullable
   private BlockPos func_223517_a(ServerWorld p_223517_1_) {
      return this.field_223518_f.isEmpty() ? null : (BlockPos)this.field_223518_f.get(p_223517_1_.getRandom().nextInt(this.field_223518_f.size()));
   }

   private boolean func_223516_a(BlockPos p_223516_1_, ServerWorld p_223516_2_) {
      BlockState blockstate = p_223516_2_.getBlockState(p_223516_1_);
      Block block = blockstate.getBlock();
      Block block1 = p_223516_2_.getBlockState(p_223516_1_.down()).getBlock();
      return block instanceof CropsBlock && ((CropsBlock)block).isMaxAge(blockstate) && this.field_220424_c || blockstate.isAir() && block1 instanceof FarmlandBlock && this.field_220423_b;
   }

   protected void startExecuting(ServerWorld p_212831_1_, VillagerEntity p_212831_2_, long p_212831_3_) {
      if (p_212831_3_ > this.field_220425_d && this.field_220422_a != null) {
         p_212831_2_.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, (Object)(new BlockPosWrapper(this.field_220422_a)));
         p_212831_2_.getBrain().setMemory(MemoryModuleType.WALK_TARGET, (Object)(new WalkTarget(new BlockPosWrapper(this.field_220422_a), 0.5F, 1)));
      }

   }

   protected void resetTask(ServerWorld p_212835_1_, VillagerEntity p_212835_2_, long p_212835_3_) {
      p_212835_2_.getBrain().removeMemory(MemoryModuleType.LOOK_TARGET);
      p_212835_2_.getBrain().removeMemory(MemoryModuleType.WALK_TARGET);
      this.field_220426_e = 0;
      this.field_220425_d = p_212835_3_ + 40L;
   }

   protected void updateTask(ServerWorld p_212833_1_, VillagerEntity p_212833_2_, long p_212833_3_) {
      if (this.field_220422_a != null && p_212833_3_ > this.field_220425_d) {
         BlockState blockstate = p_212833_1_.getBlockState(this.field_220422_a);
         Block block = blockstate.getBlock();
         Block block1 = p_212833_1_.getBlockState(this.field_220422_a.down()).getBlock();
         if (block instanceof CropsBlock && ((CropsBlock)block).isMaxAge(blockstate) && this.field_220424_c) {
            p_212833_1_.func_225521_a_(this.field_220422_a, true, p_212833_2_);
         }

         if (blockstate.isAir() && block1 instanceof FarmlandBlock && this.field_220423_b) {
            Inventory inventory = p_212833_2_.func_213715_ed();

            for(int i = 0; i < inventory.getSizeInventory(); ++i) {
               ItemStack itemstack = inventory.getStackInSlot(i);
               boolean flag = false;
               if (!itemstack.isEmpty()) {
                  if (itemstack.getItem() == Items.WHEAT_SEEDS) {
                     p_212833_1_.setBlockState(this.field_220422_a, Blocks.WHEAT.getDefaultState(), 3);
                     flag = true;
                  } else if (itemstack.getItem() == Items.POTATO) {
                     p_212833_1_.setBlockState(this.field_220422_a, Blocks.POTATOES.getDefaultState(), 3);
                     flag = true;
                  } else if (itemstack.getItem() == Items.CARROT) {
                     p_212833_1_.setBlockState(this.field_220422_a, Blocks.CARROTS.getDefaultState(), 3);
                     flag = true;
                  } else if (itemstack.getItem() == Items.BEETROOT_SEEDS) {
                     p_212833_1_.setBlockState(this.field_220422_a, Blocks.BEETROOTS.getDefaultState(), 3);
                     flag = true;
                  } else if (itemstack.getItem() instanceof IPlantable && ((IPlantable)itemstack.getItem()).getPlantType(p_212833_1_, this.field_220422_a) == PlantType.Crop) {
                     p_212833_1_.setBlockState(this.field_220422_a, ((IPlantable)itemstack.getItem()).getPlant(p_212833_1_, this.field_220422_a), 3);
                     flag = true;
                  }
               }

               if (flag) {
                  p_212833_1_.playSound((PlayerEntity)null, (double)this.field_220422_a.getX(), (double)this.field_220422_a.getY(), (double)this.field_220422_a.getZ(), SoundEvents.ITEM_CROP_PLANT, SoundCategory.BLOCKS, 1.0F, 1.0F);
                  itemstack.shrink(1);
                  if (itemstack.isEmpty()) {
                     inventory.setInventorySlotContents(i, ItemStack.EMPTY);
                  }
                  break;
               }
            }
         }

         if (block instanceof CropsBlock && !((CropsBlock)block).isMaxAge(blockstate)) {
            this.field_223518_f.remove(this.field_220422_a);
            this.field_220422_a = this.func_223517_a(p_212833_1_);
            if (this.field_220422_a != null) {
               this.field_220425_d = p_212833_3_ + 20L;
               p_212833_2_.getBrain().setMemory(MemoryModuleType.WALK_TARGET, (Object)(new WalkTarget(new BlockPosWrapper(this.field_220422_a), 0.5F, 1)));
               p_212833_2_.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, (Object)(new BlockPosWrapper(this.field_220422_a)));
            }
         }
      }

      ++this.field_220426_e;
   }

   protected boolean shouldContinueExecuting(ServerWorld p_212834_1_, VillagerEntity p_212834_2_, long p_212834_3_) {
      return this.field_220426_e < 200;
   }
}
