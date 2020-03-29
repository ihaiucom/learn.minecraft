package net.minecraft.block;

import net.minecraft.util.math.BlockPos;

public class BlockEventData {
   private final BlockPos position;
   private final Block blockType;
   private final int eventID;
   private final int eventParameter;

   public BlockEventData(BlockPos p_i45756_1_, Block p_i45756_2_, int p_i45756_3_, int p_i45756_4_) {
      this.position = p_i45756_1_;
      this.blockType = p_i45756_2_;
      this.eventID = p_i45756_3_;
      this.eventParameter = p_i45756_4_;
   }

   public BlockPos getPosition() {
      return this.position;
   }

   public Block getBlock() {
      return this.blockType;
   }

   public int getEventID() {
      return this.eventID;
   }

   public int getEventParameter() {
      return this.eventParameter;
   }

   public boolean equals(Object p_equals_1_) {
      if (!(p_equals_1_ instanceof BlockEventData)) {
         return false;
      } else {
         BlockEventData lvt_2_1_ = (BlockEventData)p_equals_1_;
         return this.position.equals(lvt_2_1_.position) && this.eventID == lvt_2_1_.eventID && this.eventParameter == lvt_2_1_.eventParameter && this.blockType == lvt_2_1_.blockType;
      }
   }

   public int hashCode() {
      int lvt_1_1_ = this.position.hashCode();
      lvt_1_1_ = 31 * lvt_1_1_ + this.blockType.hashCode();
      lvt_1_1_ = 31 * lvt_1_1_ + this.eventID;
      lvt_1_1_ = 31 * lvt_1_1_ + this.eventParameter;
      return lvt_1_1_;
   }

   public String toString() {
      return "TE(" + this.position + ")," + this.eventID + "," + this.eventParameter + "," + this.blockType;
   }
}
