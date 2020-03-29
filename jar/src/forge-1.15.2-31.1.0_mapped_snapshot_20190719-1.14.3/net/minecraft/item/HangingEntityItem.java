package net.minecraft.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.HangingEntity;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.entity.item.PaintingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class HangingEntityItem extends Item {
   private final EntityType<? extends HangingEntity> field_220001_a;

   public HangingEntityItem(EntityType<? extends HangingEntity> p_i50043_1_, Item.Properties p_i50043_2_) {
      super(p_i50043_2_);
      this.field_220001_a = p_i50043_1_;
   }

   public ActionResultType onItemUse(ItemUseContext p_195939_1_) {
      BlockPos lvt_2_1_ = p_195939_1_.getPos();
      Direction lvt_3_1_ = p_195939_1_.getFace();
      BlockPos lvt_4_1_ = lvt_2_1_.offset(lvt_3_1_);
      PlayerEntity lvt_5_1_ = p_195939_1_.getPlayer();
      ItemStack lvt_6_1_ = p_195939_1_.getItem();
      if (lvt_5_1_ != null && !this.canPlace(lvt_5_1_, lvt_3_1_, lvt_6_1_, lvt_4_1_)) {
         return ActionResultType.FAIL;
      } else {
         World lvt_7_1_ = p_195939_1_.getWorld();
         Object lvt_8_3_;
         if (this.field_220001_a == EntityType.PAINTING) {
            lvt_8_3_ = new PaintingEntity(lvt_7_1_, lvt_4_1_, lvt_3_1_);
         } else {
            if (this.field_220001_a != EntityType.ITEM_FRAME) {
               return ActionResultType.SUCCESS;
            }

            lvt_8_3_ = new ItemFrameEntity(lvt_7_1_, lvt_4_1_, lvt_3_1_);
         }

         CompoundNBT lvt_9_1_ = lvt_6_1_.getTag();
         if (lvt_9_1_ != null) {
            EntityType.applyItemNBT(lvt_7_1_, lvt_5_1_, (Entity)lvt_8_3_, lvt_9_1_);
         }

         if (((HangingEntity)lvt_8_3_).onValidSurface()) {
            if (!lvt_7_1_.isRemote) {
               ((HangingEntity)lvt_8_3_).playPlaceSound();
               lvt_7_1_.addEntity((Entity)lvt_8_3_);
            }

            lvt_6_1_.shrink(1);
            return ActionResultType.SUCCESS;
         } else {
            return ActionResultType.CONSUME;
         }
      }
   }

   protected boolean canPlace(PlayerEntity p_200127_1_, Direction p_200127_2_, ItemStack p_200127_3_, BlockPos p_200127_4_) {
      return !p_200127_2_.getAxis().isVertical() && p_200127_1_.canPlayerEdit(p_200127_4_, p_200127_2_, p_200127_3_);
   }
}
