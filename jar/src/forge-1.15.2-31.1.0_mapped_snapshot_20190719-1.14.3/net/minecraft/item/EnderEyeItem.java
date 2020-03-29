package net.minecraft.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.EndPortalFrameBlock;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.entity.item.EyeOfEnderEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class EnderEyeItem extends Item {
   public EnderEyeItem(Item.Properties p_i48502_1_) {
      super(p_i48502_1_);
   }

   public ActionResultType onItemUse(ItemUseContext p_195939_1_) {
      World lvt_2_1_ = p_195939_1_.getWorld();
      BlockPos lvt_3_1_ = p_195939_1_.getPos();
      BlockState lvt_4_1_ = lvt_2_1_.getBlockState(lvt_3_1_);
      if (lvt_4_1_.getBlock() == Blocks.END_PORTAL_FRAME && !(Boolean)lvt_4_1_.get(EndPortalFrameBlock.EYE)) {
         if (lvt_2_1_.isRemote) {
            return ActionResultType.SUCCESS;
         } else {
            BlockState lvt_5_1_ = (BlockState)lvt_4_1_.with(EndPortalFrameBlock.EYE, true);
            Block.nudgeEntitiesWithNewState(lvt_4_1_, lvt_5_1_, lvt_2_1_, lvt_3_1_);
            lvt_2_1_.setBlockState(lvt_3_1_, lvt_5_1_, 2);
            lvt_2_1_.updateComparatorOutputLevel(lvt_3_1_, Blocks.END_PORTAL_FRAME);
            p_195939_1_.getItem().shrink(1);
            lvt_2_1_.playEvent(1503, lvt_3_1_, 0);
            BlockPattern.PatternHelper lvt_6_1_ = EndPortalFrameBlock.getOrCreatePortalShape().match(lvt_2_1_, lvt_3_1_);
            if (lvt_6_1_ != null) {
               BlockPos lvt_7_1_ = lvt_6_1_.getFrontTopLeft().add(-3, 0, -3);

               for(int lvt_8_1_ = 0; lvt_8_1_ < 3; ++lvt_8_1_) {
                  for(int lvt_9_1_ = 0; lvt_9_1_ < 3; ++lvt_9_1_) {
                     lvt_2_1_.setBlockState(lvt_7_1_.add(lvt_8_1_, 0, lvt_9_1_), Blocks.END_PORTAL.getDefaultState(), 2);
                  }
               }

               lvt_2_1_.playBroadcastSound(1038, lvt_7_1_.add(1, 0, 1), 0);
            }

            return ActionResultType.SUCCESS;
         }
      } else {
         return ActionResultType.PASS;
      }
   }

   public ActionResult<ItemStack> onItemRightClick(World p_77659_1_, PlayerEntity p_77659_2_, Hand p_77659_3_) {
      ItemStack lvt_4_1_ = p_77659_2_.getHeldItem(p_77659_3_);
      RayTraceResult lvt_5_1_ = rayTrace(p_77659_1_, p_77659_2_, RayTraceContext.FluidMode.NONE);
      if (lvt_5_1_.getType() == RayTraceResult.Type.BLOCK && p_77659_1_.getBlockState(((BlockRayTraceResult)lvt_5_1_).getPos()).getBlock() == Blocks.END_PORTAL_FRAME) {
         return ActionResult.func_226250_c_(lvt_4_1_);
      } else {
         p_77659_2_.setActiveHand(p_77659_3_);
         if (p_77659_1_ instanceof ServerWorld) {
            BlockPos lvt_6_1_ = ((ServerWorld)p_77659_1_).getChunkProvider().getChunkGenerator().findNearestStructure(p_77659_1_, "Stronghold", new BlockPos(p_77659_2_), 100, false);
            if (lvt_6_1_ != null) {
               EyeOfEnderEntity lvt_7_1_ = new EyeOfEnderEntity(p_77659_1_, p_77659_2_.func_226277_ct_(), p_77659_2_.func_226283_e_(0.5D), p_77659_2_.func_226281_cx_());
               lvt_7_1_.func_213863_b(lvt_4_1_);
               lvt_7_1_.moveTowards(lvt_6_1_);
               p_77659_1_.addEntity(lvt_7_1_);
               if (p_77659_2_ instanceof ServerPlayerEntity) {
                  CriteriaTriggers.USED_ENDER_EYE.trigger((ServerPlayerEntity)p_77659_2_, lvt_6_1_);
               }

               p_77659_1_.playSound((PlayerEntity)null, p_77659_2_.func_226277_ct_(), p_77659_2_.func_226278_cu_(), p_77659_2_.func_226281_cx_(), SoundEvents.ENTITY_ENDER_EYE_LAUNCH, SoundCategory.NEUTRAL, 0.5F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));
               p_77659_1_.playEvent((PlayerEntity)null, 1003, new BlockPos(p_77659_2_), 0);
               if (!p_77659_2_.abilities.isCreativeMode) {
                  lvt_4_1_.shrink(1);
               }

               p_77659_2_.addStat(Stats.ITEM_USED.get(this));
               p_77659_2_.func_226292_a_(p_77659_3_, true);
               return ActionResult.func_226248_a_(lvt_4_1_);
            }
         }

         return ActionResult.func_226249_b_(lvt_4_1_);
      }
   }
}
