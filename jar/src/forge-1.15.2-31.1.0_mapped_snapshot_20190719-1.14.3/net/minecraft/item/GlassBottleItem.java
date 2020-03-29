package net.minecraft.item;

import java.util.List;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class GlassBottleItem extends Item {
   public GlassBottleItem(Item.Properties p_i48523_1_) {
      super(p_i48523_1_);
   }

   public ActionResult<ItemStack> onItemRightClick(World p_77659_1_, PlayerEntity p_77659_2_, Hand p_77659_3_) {
      List<AreaEffectCloudEntity> lvt_4_1_ = p_77659_1_.getEntitiesWithinAABB(AreaEffectCloudEntity.class, p_77659_2_.getBoundingBox().grow(2.0D), (p_210311_0_) -> {
         return p_210311_0_ != null && p_210311_0_.isAlive() && p_210311_0_.getOwner() instanceof EnderDragonEntity;
      });
      ItemStack lvt_5_1_ = p_77659_2_.getHeldItem(p_77659_3_);
      if (!lvt_4_1_.isEmpty()) {
         AreaEffectCloudEntity lvt_6_1_ = (AreaEffectCloudEntity)lvt_4_1_.get(0);
         lvt_6_1_.setRadius(lvt_6_1_.getRadius() - 0.5F);
         p_77659_1_.playSound((PlayerEntity)null, p_77659_2_.func_226277_ct_(), p_77659_2_.func_226278_cu_(), p_77659_2_.func_226281_cx_(), SoundEvents.ITEM_BOTTLE_FILL_DRAGONBREATH, SoundCategory.NEUTRAL, 1.0F, 1.0F);
         return ActionResult.func_226248_a_(this.turnBottleIntoItem(lvt_5_1_, p_77659_2_, new ItemStack(Items.DRAGON_BREATH)));
      } else {
         RayTraceResult lvt_6_2_ = rayTrace(p_77659_1_, p_77659_2_, RayTraceContext.FluidMode.SOURCE_ONLY);
         if (lvt_6_2_.getType() == RayTraceResult.Type.MISS) {
            return ActionResult.func_226250_c_(lvt_5_1_);
         } else {
            if (lvt_6_2_.getType() == RayTraceResult.Type.BLOCK) {
               BlockPos lvt_7_1_ = ((BlockRayTraceResult)lvt_6_2_).getPos();
               if (!p_77659_1_.isBlockModifiable(p_77659_2_, lvt_7_1_)) {
                  return ActionResult.func_226250_c_(lvt_5_1_);
               }

               if (p_77659_1_.getFluidState(lvt_7_1_).isTagged(FluidTags.WATER)) {
                  p_77659_1_.playSound(p_77659_2_, p_77659_2_.func_226277_ct_(), p_77659_2_.func_226278_cu_(), p_77659_2_.func_226281_cx_(), SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.NEUTRAL, 1.0F, 1.0F);
                  return ActionResult.func_226248_a_(this.turnBottleIntoItem(lvt_5_1_, p_77659_2_, PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), Potions.WATER)));
               }
            }

            return ActionResult.func_226250_c_(lvt_5_1_);
         }
      }
   }

   protected ItemStack turnBottleIntoItem(ItemStack p_185061_1_, PlayerEntity p_185061_2_, ItemStack p_185061_3_) {
      p_185061_1_.shrink(1);
      p_185061_2_.addStat(Stats.ITEM_USED.get(this));
      if (p_185061_1_.isEmpty()) {
         return p_185061_3_;
      } else {
         if (!p_185061_2_.inventory.addItemStackToInventory(p_185061_3_)) {
            p_185061_2_.dropItem(p_185061_3_, false);
         }

         return p_185061_1_;
      }
   }
}
