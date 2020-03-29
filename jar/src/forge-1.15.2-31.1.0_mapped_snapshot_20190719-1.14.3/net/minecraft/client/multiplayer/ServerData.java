package net.minecraft.client.multiplayer;

import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.ExtendedServerListData;

@OnlyIn(Dist.CLIENT)
public class ServerData {
   public String serverName;
   public String serverIP;
   public String populationInfo;
   public String serverMOTD;
   public long pingToServer;
   public int version = SharedConstants.getVersion().getProtocolVersion();
   public String gameVersion = SharedConstants.getVersion().getName();
   public boolean pinged;
   public String playerList;
   private ServerData.ServerResourceMode resourceMode;
   private String serverIcon;
   private boolean lanServer;
   public ExtendedServerListData forgeData;

   public ServerData(String p_i46420_1_, String p_i46420_2_, boolean p_i46420_3_) {
      this.resourceMode = ServerData.ServerResourceMode.PROMPT;
      this.forgeData = null;
      this.serverName = p_i46420_1_;
      this.serverIP = p_i46420_2_;
      this.lanServer = p_i46420_3_;
   }

   public CompoundNBT getNBTCompound() {
      CompoundNBT compoundnbt = new CompoundNBT();
      compoundnbt.putString("name", this.serverName);
      compoundnbt.putString("ip", this.serverIP);
      if (this.serverIcon != null) {
         compoundnbt.putString("icon", this.serverIcon);
      }

      if (this.resourceMode == ServerData.ServerResourceMode.ENABLED) {
         compoundnbt.putBoolean("acceptTextures", true);
      } else if (this.resourceMode == ServerData.ServerResourceMode.DISABLED) {
         compoundnbt.putBoolean("acceptTextures", false);
      }

      return compoundnbt;
   }

   public ServerData.ServerResourceMode getResourceMode() {
      return this.resourceMode;
   }

   public void setResourceMode(ServerData.ServerResourceMode p_152584_1_) {
      this.resourceMode = p_152584_1_;
   }

   public static ServerData getServerDataFromNBTCompound(CompoundNBT p_78837_0_) {
      ServerData serverdata = new ServerData(p_78837_0_.getString("name"), p_78837_0_.getString("ip"), false);
      if (p_78837_0_.contains("icon", 8)) {
         serverdata.setBase64EncodedIconData(p_78837_0_.getString("icon"));
      }

      if (p_78837_0_.contains("acceptTextures", 1)) {
         if (p_78837_0_.getBoolean("acceptTextures")) {
            serverdata.setResourceMode(ServerData.ServerResourceMode.ENABLED);
         } else {
            serverdata.setResourceMode(ServerData.ServerResourceMode.DISABLED);
         }
      } else {
         serverdata.setResourceMode(ServerData.ServerResourceMode.PROMPT);
      }

      return serverdata;
   }

   @Nullable
   public String getBase64EncodedIconData() {
      return this.serverIcon;
   }

   public void setBase64EncodedIconData(@Nullable String p_147407_1_) {
      this.serverIcon = p_147407_1_;
   }

   public boolean isOnLAN() {
      return this.lanServer;
   }

   public void copyFrom(ServerData p_152583_1_) {
      this.serverIP = p_152583_1_.serverIP;
      this.serverName = p_152583_1_.serverName;
      this.setResourceMode(p_152583_1_.getResourceMode());
      this.serverIcon = p_152583_1_.serverIcon;
      this.lanServer = p_152583_1_.lanServer;
   }

   @OnlyIn(Dist.CLIENT)
   public static enum ServerResourceMode {
      ENABLED("enabled"),
      DISABLED("disabled"),
      PROMPT("prompt");

      private final ITextComponent motd;

      private ServerResourceMode(String p_i1053_3_) {
         this.motd = new TranslationTextComponent("addServer.resourcePack." + p_i1053_3_, new Object[0]);
      }

      public ITextComponent getMotd() {
         return this.motd;
      }
   }
}
