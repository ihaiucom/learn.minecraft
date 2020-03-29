package net.minecraft.tileentity;

import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(
   value = Dist.CLIENT,
   _interface = IChestLid.class
)
public class EnderChestTileEntity extends TileEntity implements IChestLid, ITickableTileEntity {
   public float lidAngle;
   public float prevLidAngle;
   public int numPlayersUsing;
   private int ticksSinceSync;

   public EnderChestTileEntity() {
      super(TileEntityType.ENDER_CHEST);
   }

   public void tick() {
      if (++this.ticksSinceSync % 20 * 4 == 0) {
         this.world.addBlockEvent(this.pos, Blocks.ENDER_CHEST, 1, this.numPlayersUsing);
      }

      this.prevLidAngle = this.lidAngle;
      int lvt_1_1_ = this.pos.getX();
      int lvt_2_1_ = this.pos.getY();
      int lvt_3_1_ = this.pos.getZ();
      float lvt_4_1_ = 0.1F;
      double lvt_7_2_;
      if (this.numPlayersUsing > 0 && this.lidAngle == 0.0F) {
         double lvt_5_1_ = (double)lvt_1_1_ + 0.5D;
         lvt_7_2_ = (double)lvt_3_1_ + 0.5D;
         this.world.playSound((PlayerEntity)null, lvt_5_1_, (double)lvt_2_1_ + 0.5D, lvt_7_2_, SoundEvents.BLOCK_ENDER_CHEST_OPEN, SoundCategory.BLOCKS, 0.5F, this.world.rand.nextFloat() * 0.1F + 0.9F);
      }

      if (this.numPlayersUsing == 0 && this.lidAngle > 0.0F || this.numPlayersUsing > 0 && this.lidAngle < 1.0F) {
         float lvt_5_2_ = this.lidAngle;
         if (this.numPlayersUsing > 0) {
            this.lidAngle += 0.1F;
         } else {
            this.lidAngle -= 0.1F;
         }

         if (this.lidAngle > 1.0F) {
            this.lidAngle = 1.0F;
         }

         float lvt_6_1_ = 0.5F;
         if (this.lidAngle < 0.5F && lvt_5_2_ >= 0.5F) {
            lvt_7_2_ = (double)lvt_1_1_ + 0.5D;
            double lvt_9_1_ = (double)lvt_3_1_ + 0.5D;
            this.world.playSound((PlayerEntity)null, lvt_7_2_, (double)lvt_2_1_ + 0.5D, lvt_9_1_, SoundEvents.BLOCK_ENDER_CHEST_CLOSE, SoundCategory.BLOCKS, 0.5F, this.world.rand.nextFloat() * 0.1F + 0.9F);
         }

         if (this.lidAngle < 0.0F) {
            this.lidAngle = 0.0F;
         }
      }

   }

   public boolean receiveClientEvent(int p_145842_1_, int p_145842_2_) {
      if (p_145842_1_ == 1) {
         this.numPlayersUsing = p_145842_2_;
         return true;
      } else {
         return super.receiveClientEvent(p_145842_1_, p_145842_2_);
      }
   }

   public void remove() {
      this.updateContainingBlockInfo();
      super.remove();
   }

   public void openChest() {
      ++this.numPlayersUsing;
      this.world.addBlockEvent(this.pos, Blocks.ENDER_CHEST, 1, this.numPlayersUsing);
   }

   public void closeChest() {
      --this.numPlayersUsing;
      this.world.addBlockEvent(this.pos, Blocks.ENDER_CHEST, 1, this.numPlayersUsing);
   }

   public boolean canBeUsed(PlayerEntity p_145971_1_) {
      if (this.world.getTileEntity(this.pos) != this) {
         return false;
      } else {
         return p_145971_1_.getDistanceSq((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) <= 64.0D;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public float getLidAngle(float p_195480_1_) {
      return MathHelper.lerp(p_195480_1_, this.prevLidAngle, this.lidAngle);
   }
}
