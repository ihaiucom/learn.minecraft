package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ScaffoldingBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.server.SChatPacket;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class ScaffoldingItem extends BlockItem {
   public ScaffoldingItem(Block p_i50039_1_, Item.Properties p_i50039_2_) {
      super(p_i50039_1_, p_i50039_2_);
   }

   @Nullable
   public BlockItemUseContext getBlockItemUseContext(BlockItemUseContext p_219984_1_) {
      BlockPos lvt_2_1_ = p_219984_1_.getPos();
      World lvt_3_1_ = p_219984_1_.getWorld();
      BlockState lvt_4_1_ = lvt_3_1_.getBlockState(lvt_2_1_);
      Block lvt_5_1_ = this.getBlock();
      if (lvt_4_1_.getBlock() != lvt_5_1_) {
         return ScaffoldingBlock.func_220117_a(lvt_3_1_, lvt_2_1_) == 7 ? null : p_219984_1_;
      } else {
         Direction lvt_6_2_;
         if (p_219984_1_.func_225518_g_()) {
            lvt_6_2_ = p_219984_1_.func_221533_k() ? p_219984_1_.getFace().getOpposite() : p_219984_1_.getFace();
         } else {
            lvt_6_2_ = p_219984_1_.getFace() == Direction.UP ? p_219984_1_.getPlacementHorizontalFacing() : Direction.UP;
         }

         int lvt_7_1_ = 0;
         BlockPos.Mutable lvt_8_1_ = (new BlockPos.Mutable(lvt_2_1_)).move(lvt_6_2_);

         while(lvt_7_1_ < 7) {
            if (!lvt_3_1_.isRemote && !World.isValid(lvt_8_1_)) {
               PlayerEntity lvt_9_1_ = p_219984_1_.getPlayer();
               int lvt_10_1_ = lvt_3_1_.getHeight();
               if (lvt_9_1_ instanceof ServerPlayerEntity && lvt_8_1_.getY() >= lvt_10_1_) {
                  SChatPacket lvt_11_1_ = new SChatPacket((new TranslationTextComponent("build.tooHigh", new Object[]{lvt_10_1_})).applyTextStyle(TextFormatting.RED), ChatType.GAME_INFO);
                  ((ServerPlayerEntity)lvt_9_1_).connection.sendPacket(lvt_11_1_);
               }
               break;
            }

            lvt_4_1_ = lvt_3_1_.getBlockState(lvt_8_1_);
            if (lvt_4_1_.getBlock() != this.getBlock()) {
               if (lvt_4_1_.isReplaceable(p_219984_1_)) {
                  return BlockItemUseContext.func_221536_a(p_219984_1_, lvt_8_1_, lvt_6_2_);
               }
               break;
            }

            lvt_8_1_.move(lvt_6_2_);
            if (lvt_6_2_.getAxis().isHorizontal()) {
               ++lvt_7_1_;
            }
         }

         return null;
      }
   }

   protected boolean func_219987_d() {
      return false;
   }
}
