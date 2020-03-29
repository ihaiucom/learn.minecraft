package net.minecraft.network.play.client;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CUseEntityPacket implements IPacket<IServerPlayNetHandler> {
   private int entityId;
   private CUseEntityPacket.Action action;
   private Vec3d hitVec;
   private Hand hand;

   public CUseEntityPacket() {
   }

   public CUseEntityPacket(Entity p_i46877_1_) {
      this.entityId = p_i46877_1_.getEntityId();
      this.action = CUseEntityPacket.Action.ATTACK;
   }

   @OnlyIn(Dist.CLIENT)
   public CUseEntityPacket(Entity p_i46878_1_, Hand p_i46878_2_) {
      this.entityId = p_i46878_1_.getEntityId();
      this.action = CUseEntityPacket.Action.INTERACT;
      this.hand = p_i46878_2_;
   }

   @OnlyIn(Dist.CLIENT)
   public CUseEntityPacket(Entity p_i47098_1_, Hand p_i47098_2_, Vec3d p_i47098_3_) {
      this.entityId = p_i47098_1_.getEntityId();
      this.action = CUseEntityPacket.Action.INTERACT_AT;
      this.hand = p_i47098_2_;
      this.hitVec = p_i47098_3_;
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.entityId = p_148837_1_.readVarInt();
      this.action = (CUseEntityPacket.Action)p_148837_1_.readEnumValue(CUseEntityPacket.Action.class);
      if (this.action == CUseEntityPacket.Action.INTERACT_AT) {
         this.hitVec = new Vec3d((double)p_148837_1_.readFloat(), (double)p_148837_1_.readFloat(), (double)p_148837_1_.readFloat());
      }

      if (this.action == CUseEntityPacket.Action.INTERACT || this.action == CUseEntityPacket.Action.INTERACT_AT) {
         this.hand = (Hand)p_148837_1_.readEnumValue(Hand.class);
      }

   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeVarInt(this.entityId);
      p_148840_1_.writeEnumValue(this.action);
      if (this.action == CUseEntityPacket.Action.INTERACT_AT) {
         p_148840_1_.writeFloat((float)this.hitVec.x);
         p_148840_1_.writeFloat((float)this.hitVec.y);
         p_148840_1_.writeFloat((float)this.hitVec.z);
      }

      if (this.action == CUseEntityPacket.Action.INTERACT || this.action == CUseEntityPacket.Action.INTERACT_AT) {
         p_148840_1_.writeEnumValue(this.hand);
      }

   }

   public void processPacket(IServerPlayNetHandler p_148833_1_) {
      p_148833_1_.processUseEntity(this);
   }

   @Nullable
   public Entity getEntityFromWorld(World p_149564_1_) {
      return p_149564_1_.getEntityByID(this.entityId);
   }

   public CUseEntityPacket.Action getAction() {
      return this.action;
   }

   public Hand getHand() {
      return this.hand;
   }

   public Vec3d getHitVec() {
      return this.hitVec;
   }

   public static enum Action {
      INTERACT,
      ATTACK,
      INTERACT_AT;
   }
}
