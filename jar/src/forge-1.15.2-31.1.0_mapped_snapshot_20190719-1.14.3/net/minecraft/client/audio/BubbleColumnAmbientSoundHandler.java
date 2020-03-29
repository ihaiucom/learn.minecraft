package net.minecraft.client.audio;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.BubbleColumnBlock;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BubbleColumnAmbientSoundHandler implements IAmbientSoundHandler {
   private final ClientPlayerEntity player;
   private boolean field_217865_b;
   private boolean field_217866_c = true;

   public BubbleColumnAmbientSoundHandler(ClientPlayerEntity p_i50900_1_) {
      this.player = p_i50900_1_;
   }

   public void tick() {
      World lvt_1_1_ = this.player.world;
      BlockState lvt_2_1_ = lvt_1_1_.findBlockstateInArea(this.player.getBoundingBox().grow(0.0D, -0.4000000059604645D, 0.0D).shrink(0.001D), Blocks.BUBBLE_COLUMN);
      if (lvt_2_1_ != null) {
         if (!this.field_217865_b && !this.field_217866_c && lvt_2_1_.getBlock() == Blocks.BUBBLE_COLUMN && !this.player.isSpectator()) {
            boolean lvt_3_1_ = (Boolean)lvt_2_1_.get(BubbleColumnBlock.DRAG);
            if (lvt_3_1_) {
               this.player.playSound(SoundEvents.BLOCK_BUBBLE_COLUMN_WHIRLPOOL_INSIDE, 1.0F, 1.0F);
            } else {
               this.player.playSound(SoundEvents.BLOCK_BUBBLE_COLUMN_UPWARDS_INSIDE, 1.0F, 1.0F);
            }
         }

         this.field_217865_b = true;
      } else {
         this.field_217865_b = false;
      }

      this.field_217866_c = false;
   }
}
