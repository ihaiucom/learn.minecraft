package net.minecraft.tileentity;

import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class JigsawTileEntity extends TileEntity {
   private ResourceLocation attachmentType;
   private ResourceLocation targetPool;
   private String finalState;

   public JigsawTileEntity(TileEntityType<?> p_i49960_1_) {
      super(p_i49960_1_);
      this.attachmentType = new ResourceLocation("empty");
      this.targetPool = new ResourceLocation("empty");
      this.finalState = "minecraft:air";
   }

   public JigsawTileEntity() {
      this(TileEntityType.JIGSAW);
   }

   @OnlyIn(Dist.CLIENT)
   public ResourceLocation getAttachmentType() {
      return this.attachmentType;
   }

   @OnlyIn(Dist.CLIENT)
   public ResourceLocation getTargetPool() {
      return this.targetPool;
   }

   @OnlyIn(Dist.CLIENT)
   public String getFinalState() {
      return this.finalState;
   }

   public void setAttachmentType(ResourceLocation p_214057_1_) {
      this.attachmentType = p_214057_1_;
   }

   public void setTargetPool(ResourceLocation p_214058_1_) {
      this.targetPool = p_214058_1_;
   }

   public void setFinalState(String p_214055_1_) {
      this.finalState = p_214055_1_;
   }

   public CompoundNBT write(CompoundNBT p_189515_1_) {
      super.write(p_189515_1_);
      p_189515_1_.putString("attachement_type", this.attachmentType.toString());
      p_189515_1_.putString("target_pool", this.targetPool.toString());
      p_189515_1_.putString("final_state", this.finalState);
      return p_189515_1_;
   }

   public void read(CompoundNBT p_145839_1_) {
      super.read(p_145839_1_);
      this.attachmentType = new ResourceLocation(p_145839_1_.getString("attachement_type"));
      this.targetPool = new ResourceLocation(p_145839_1_.getString("target_pool"));
      this.finalState = p_145839_1_.getString("final_state");
   }

   @Nullable
   public SUpdateTileEntityPacket getUpdatePacket() {
      return new SUpdateTileEntityPacket(this.pos, 12, this.getUpdateTag());
   }

   public CompoundNBT getUpdateTag() {
      return this.write(new CompoundNBT());
   }
}
