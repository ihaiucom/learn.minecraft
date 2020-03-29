package net.minecraft.network.play.server;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.command.arguments.EntityAnchorArgument;
import net.minecraft.entity.Entity;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SPlayerLookPacket implements IPacket<IClientPlayNetHandler> {
   private double x;
   private double y;
   private double z;
   private int entityId;
   private EntityAnchorArgument.Type sourceAnchor;
   private EntityAnchorArgument.Type targetAnchor;
   private boolean isEntity;

   public SPlayerLookPacket() {
   }

   public SPlayerLookPacket(EntityAnchorArgument.Type p_i48589_1_, double p_i48589_2_, double p_i48589_4_, double p_i48589_6_) {
      this.sourceAnchor = p_i48589_1_;
      this.x = p_i48589_2_;
      this.y = p_i48589_4_;
      this.z = p_i48589_6_;
   }

   public SPlayerLookPacket(EntityAnchorArgument.Type p_i48590_1_, Entity p_i48590_2_, EntityAnchorArgument.Type p_i48590_3_) {
      this.sourceAnchor = p_i48590_1_;
      this.entityId = p_i48590_2_.getEntityId();
      this.targetAnchor = p_i48590_3_;
      Vec3d lvt_4_1_ = p_i48590_3_.apply(p_i48590_2_);
      this.x = lvt_4_1_.x;
      this.y = lvt_4_1_.y;
      this.z = lvt_4_1_.z;
      this.isEntity = true;
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.sourceAnchor = (EntityAnchorArgument.Type)p_148837_1_.readEnumValue(EntityAnchorArgument.Type.class);
      this.x = p_148837_1_.readDouble();
      this.y = p_148837_1_.readDouble();
      this.z = p_148837_1_.readDouble();
      if (p_148837_1_.readBoolean()) {
         this.isEntity = true;
         this.entityId = p_148837_1_.readVarInt();
         this.targetAnchor = (EntityAnchorArgument.Type)p_148837_1_.readEnumValue(EntityAnchorArgument.Type.class);
      }

   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeEnumValue(this.sourceAnchor);
      p_148840_1_.writeDouble(this.x);
      p_148840_1_.writeDouble(this.y);
      p_148840_1_.writeDouble(this.z);
      p_148840_1_.writeBoolean(this.isEntity);
      if (this.isEntity) {
         p_148840_1_.writeVarInt(this.entityId);
         p_148840_1_.writeEnumValue(this.targetAnchor);
      }

   }

   public void processPacket(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handlePlayerLook(this);
   }

   @OnlyIn(Dist.CLIENT)
   public EntityAnchorArgument.Type getSourceAnchor() {
      return this.sourceAnchor;
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public Vec3d getTargetPosition(World p_200531_1_) {
      if (this.isEntity) {
         Entity lvt_2_1_ = p_200531_1_.getEntityByID(this.entityId);
         return lvt_2_1_ == null ? new Vec3d(this.x, this.y, this.z) : this.targetAnchor.apply(lvt_2_1_);
      } else {
         return new Vec3d(this.x, this.y, this.z);
      }
   }
}
