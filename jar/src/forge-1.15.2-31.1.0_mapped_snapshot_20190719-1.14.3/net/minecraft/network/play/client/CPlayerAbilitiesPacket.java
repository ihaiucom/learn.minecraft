package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;

public class CPlayerAbilitiesPacket implements IPacket<IServerPlayNetHandler> {
   private boolean invulnerable;
   private boolean flying;
   private boolean allowFlying;
   private boolean creativeMode;
   private float flySpeed;
   private float walkSpeed;

   public CPlayerAbilitiesPacket() {
   }

   public CPlayerAbilitiesPacket(PlayerAbilities p_i46872_1_) {
      this.setInvulnerable(p_i46872_1_.disableDamage);
      this.setFlying(p_i46872_1_.isFlying);
      this.setAllowFlying(p_i46872_1_.allowFlying);
      this.setCreativeMode(p_i46872_1_.isCreativeMode);
      this.setFlySpeed(p_i46872_1_.getFlySpeed());
      this.setWalkSpeed(p_i46872_1_.getWalkSpeed());
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      byte lvt_2_1_ = p_148837_1_.readByte();
      this.setInvulnerable((lvt_2_1_ & 1) > 0);
      this.setFlying((lvt_2_1_ & 2) > 0);
      this.setAllowFlying((lvt_2_1_ & 4) > 0);
      this.setCreativeMode((lvt_2_1_ & 8) > 0);
      this.setFlySpeed(p_148837_1_.readFloat());
      this.setWalkSpeed(p_148837_1_.readFloat());
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      byte lvt_2_1_ = 0;
      if (this.isInvulnerable()) {
         lvt_2_1_ = (byte)(lvt_2_1_ | 1);
      }

      if (this.isFlying()) {
         lvt_2_1_ = (byte)(lvt_2_1_ | 2);
      }

      if (this.isAllowFlying()) {
         lvt_2_1_ = (byte)(lvt_2_1_ | 4);
      }

      if (this.isCreativeMode()) {
         lvt_2_1_ = (byte)(lvt_2_1_ | 8);
      }

      p_148840_1_.writeByte(lvt_2_1_);
      p_148840_1_.writeFloat(this.flySpeed);
      p_148840_1_.writeFloat(this.walkSpeed);
   }

   public void processPacket(IServerPlayNetHandler p_148833_1_) {
      p_148833_1_.processPlayerAbilities(this);
   }

   public boolean isInvulnerable() {
      return this.invulnerable;
   }

   public void setInvulnerable(boolean p_149490_1_) {
      this.invulnerable = p_149490_1_;
   }

   public boolean isFlying() {
      return this.flying;
   }

   public void setFlying(boolean p_149483_1_) {
      this.flying = p_149483_1_;
   }

   public boolean isAllowFlying() {
      return this.allowFlying;
   }

   public void setAllowFlying(boolean p_149491_1_) {
      this.allowFlying = p_149491_1_;
   }

   public boolean isCreativeMode() {
      return this.creativeMode;
   }

   public void setCreativeMode(boolean p_149493_1_) {
      this.creativeMode = p_149493_1_;
   }

   public void setFlySpeed(float p_149485_1_) {
      this.flySpeed = p_149485_1_;
   }

   public void setWalkSpeed(float p_149492_1_) {
      this.walkSpeed = p_149492_1_;
   }
}
