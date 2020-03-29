package net.minecraft.network.login.server;

import com.mojang.authlib.GameProfile;
import java.io.IOException;
import java.util.UUID;
import net.minecraft.client.network.login.IClientLoginNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SLoginSuccessPacket implements IPacket<IClientLoginNetHandler> {
   private GameProfile profile;

   public SLoginSuccessPacket() {
   }

   public SLoginSuccessPacket(GameProfile p_i46856_1_) {
      this.profile = p_i46856_1_;
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      String s = p_148837_1_.readString(36);
      String s1 = p_148837_1_.readString(16);
      UUID uuid = s.length() > 0 ? UUID.fromString(s) : null;
      this.profile = new GameProfile(uuid, s1);
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      UUID uuid = this.profile.getId();
      p_148840_1_.writeString(uuid == null ? "" : uuid.toString());
      p_148840_1_.writeString(this.profile.getName());
   }

   public void processPacket(IClientLoginNetHandler p_148833_1_) {
      p_148833_1_.handleLoginSuccess(this);
   }

   @OnlyIn(Dist.CLIENT)
   public GameProfile getProfile() {
      return this.profile;
   }
}
