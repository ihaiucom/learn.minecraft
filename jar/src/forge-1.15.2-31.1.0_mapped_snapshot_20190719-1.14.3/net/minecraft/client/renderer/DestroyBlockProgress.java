package net.minecraft.client.renderer;

import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DestroyBlockProgress implements Comparable<DestroyBlockProgress> {
   private final int miningPlayerEntId;
   private final BlockPos position;
   private int partialBlockProgress;
   private int createdAtCloudUpdateTick;

   public DestroyBlockProgress(int p_i45925_1_, BlockPos p_i45925_2_) {
      this.miningPlayerEntId = p_i45925_1_;
      this.position = p_i45925_2_;
   }

   public BlockPos getPosition() {
      return this.position;
   }

   public void setPartialBlockDamage(int p_73107_1_) {
      if (p_73107_1_ > 10) {
         p_73107_1_ = 10;
      }

      this.partialBlockProgress = p_73107_1_;
   }

   public int getPartialBlockDamage() {
      return this.partialBlockProgress;
   }

   public void setCloudUpdateTick(int p_82744_1_) {
      this.createdAtCloudUpdateTick = p_82744_1_;
   }

   public int getCreationCloudUpdateTick() {
      return this.createdAtCloudUpdateTick;
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) {
         DestroyBlockProgress lvt_2_1_ = (DestroyBlockProgress)p_equals_1_;
         return this.miningPlayerEntId == lvt_2_1_.miningPlayerEntId;
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Integer.hashCode(this.miningPlayerEntId);
   }

   public int compareTo(DestroyBlockProgress p_compareTo_1_) {
      return this.partialBlockProgress != p_compareTo_1_.partialBlockProgress ? Integer.compare(this.partialBlockProgress, p_compareTo_1_.partialBlockProgress) : Integer.compare(this.miningPlayerEntId, p_compareTo_1_.miningPlayerEntId);
   }

   // $FF: synthetic method
   public int compareTo(Object p_compareTo_1_) {
      return this.compareTo((DestroyBlockProgress)p_compareTo_1_);
   }
}
