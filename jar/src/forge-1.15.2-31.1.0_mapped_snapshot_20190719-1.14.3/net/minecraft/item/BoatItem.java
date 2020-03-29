package net.minecraft.item;

import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class BoatItem extends Item {
   private static final Predicate<Entity> field_219989_a;
   private final BoatEntity.Type type;

   public BoatItem(BoatEntity.Type p_i48526_1_, Item.Properties p_i48526_2_) {
      super(p_i48526_2_);
      this.type = p_i48526_1_;
   }

   public ActionResult<ItemStack> onItemRightClick(World p_77659_1_, PlayerEntity p_77659_2_, Hand p_77659_3_) {
      ItemStack lvt_4_1_ = p_77659_2_.getHeldItem(p_77659_3_);
      RayTraceResult lvt_5_1_ = rayTrace(p_77659_1_, p_77659_2_, RayTraceContext.FluidMode.ANY);
      if (lvt_5_1_.getType() == RayTraceResult.Type.MISS) {
         return ActionResult.func_226250_c_(lvt_4_1_);
      } else {
         Vec3d lvt_6_1_ = p_77659_2_.getLook(1.0F);
         double lvt_7_1_ = 5.0D;
         List<Entity> lvt_9_1_ = p_77659_1_.getEntitiesInAABBexcluding(p_77659_2_, p_77659_2_.getBoundingBox().expand(lvt_6_1_.scale(5.0D)).grow(1.0D), field_219989_a);
         if (!lvt_9_1_.isEmpty()) {
            Vec3d lvt_10_1_ = p_77659_2_.getEyePosition(1.0F);
            Iterator var11 = lvt_9_1_.iterator();

            while(var11.hasNext()) {
               Entity lvt_12_1_ = (Entity)var11.next();
               AxisAlignedBB lvt_13_1_ = lvt_12_1_.getBoundingBox().grow((double)lvt_12_1_.getCollisionBorderSize());
               if (lvt_13_1_.contains(lvt_10_1_)) {
                  return ActionResult.func_226250_c_(lvt_4_1_);
               }
            }
         }

         if (lvt_5_1_.getType() == RayTraceResult.Type.BLOCK) {
            BoatEntity lvt_10_2_ = new BoatEntity(p_77659_1_, lvt_5_1_.getHitVec().x, lvt_5_1_.getHitVec().y, lvt_5_1_.getHitVec().z);
            lvt_10_2_.setBoatType(this.type);
            lvt_10_2_.rotationYaw = p_77659_2_.rotationYaw;
            if (!p_77659_1_.func_226665_a__(lvt_10_2_, lvt_10_2_.getBoundingBox().grow(-0.1D))) {
               return ActionResult.func_226251_d_(lvt_4_1_);
            } else {
               if (!p_77659_1_.isRemote) {
                  p_77659_1_.addEntity(lvt_10_2_);
                  if (!p_77659_2_.abilities.isCreativeMode) {
                     lvt_4_1_.shrink(1);
                  }
               }

               p_77659_2_.addStat(Stats.ITEM_USED.get(this));
               return ActionResult.func_226248_a_(lvt_4_1_);
            }
         } else {
            return ActionResult.func_226250_c_(lvt_4_1_);
         }
      }
   }

   static {
      field_219989_a = EntityPredicates.NOT_SPECTATING.and(Entity::canBeCollidedWith);
   }
}
