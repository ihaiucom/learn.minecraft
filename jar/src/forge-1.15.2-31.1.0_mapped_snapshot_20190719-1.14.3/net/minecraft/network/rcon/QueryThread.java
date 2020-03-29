package net.minecraft.network.rcon;

import com.google.common.collect.Maps;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.PortUnreachableException;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import net.minecraft.util.Util;

public class QueryThread extends RConThread {
   private long lastAuthCheckTime;
   private final int queryPort;
   private final int serverPort;
   private final int maxPlayers;
   private final String serverMotd;
   private final String worldName;
   private DatagramSocket querySocket;
   private final byte[] buffer = new byte[1460];
   private DatagramPacket incomingPacket;
   private final Map<SocketAddress, String> idents;
   private String queryHostname;
   private String serverHostname;
   private final Map<SocketAddress, QueryThread.Auth> queryClients;
   private final long time;
   private final RConOutputStream output;
   private long lastQueryResponseTime;

   public QueryThread(IServer p_i1536_1_) {
      super(p_i1536_1_, "Query Listener");
      this.queryPort = p_i1536_1_.getServerProperties().queryPort;
      this.serverHostname = p_i1536_1_.getHostname();
      this.serverPort = p_i1536_1_.getPort();
      this.serverMotd = p_i1536_1_.getMotd();
      this.maxPlayers = p_i1536_1_.getMaxPlayers();
      this.worldName = p_i1536_1_.getFolderName();
      this.lastQueryResponseTime = 0L;
      this.queryHostname = "0.0.0.0";
      if (!this.serverHostname.isEmpty() && !this.queryHostname.equals(this.serverHostname)) {
         this.queryHostname = this.serverHostname;
      } else {
         this.serverHostname = "0.0.0.0";

         try {
            InetAddress lvt_2_1_ = InetAddress.getLocalHost();
            this.queryHostname = lvt_2_1_.getHostAddress();
         } catch (UnknownHostException var3) {
            this.logWarning("Unable to determine local host IP, please set server-ip in server.properties: " + var3.getMessage());
         }
      }

      this.idents = Maps.newHashMap();
      this.output = new RConOutputStream(1460);
      this.queryClients = Maps.newHashMap();
      this.time = (new Date()).getTime();
   }

   private void sendResponsePacket(byte[] p_72620_1_, DatagramPacket p_72620_2_) throws IOException {
      this.querySocket.send(new DatagramPacket(p_72620_1_, p_72620_1_.length, p_72620_2_.getSocketAddress()));
   }

   private boolean parseIncomingPacket(DatagramPacket p_72621_1_) throws IOException {
      byte[] lvt_2_1_ = p_72621_1_.getData();
      int lvt_3_1_ = p_72621_1_.getLength();
      SocketAddress lvt_4_1_ = p_72621_1_.getSocketAddress();
      this.logDebug("Packet len " + lvt_3_1_ + " [" + lvt_4_1_ + "]");
      if (3 <= lvt_3_1_ && -2 == lvt_2_1_[0] && -3 == lvt_2_1_[1]) {
         this.logDebug("Packet '" + RConUtils.getByteAsHexString(lvt_2_1_[2]) + "' [" + lvt_4_1_ + "]");
         switch(lvt_2_1_[2]) {
         case 0:
            if (!this.verifyClientAuth(p_72621_1_)) {
               this.logDebug("Invalid challenge [" + lvt_4_1_ + "]");
               return false;
            } else if (15 == lvt_3_1_) {
               this.sendResponsePacket(this.createQueryResponse(p_72621_1_), p_72621_1_);
               this.logDebug("Rules [" + lvt_4_1_ + "]");
            } else {
               RConOutputStream lvt_5_1_ = new RConOutputStream(1460);
               lvt_5_1_.writeInt(0);
               lvt_5_1_.writeByteArray(this.getRequestID(p_72621_1_.getSocketAddress()));
               lvt_5_1_.writeString(this.serverMotd);
               lvt_5_1_.writeString("SMP");
               lvt_5_1_.writeString(this.worldName);
               lvt_5_1_.writeString(Integer.toString(this.getNumberOfPlayers()));
               lvt_5_1_.writeString(Integer.toString(this.maxPlayers));
               lvt_5_1_.writeShort((short)this.serverPort);
               lvt_5_1_.writeString(this.queryHostname);
               this.sendResponsePacket(lvt_5_1_.toByteArray(), p_72621_1_);
               this.logDebug("Status [" + lvt_4_1_ + "]");
            }
         default:
            return true;
         case 9:
            this.sendAuthChallenge(p_72621_1_);
            this.logDebug("Challenge [" + lvt_4_1_ + "]");
            return true;
         }
      } else {
         this.logDebug("Invalid packet [" + lvt_4_1_ + "]");
         return false;
      }
   }

   private byte[] createQueryResponse(DatagramPacket p_72624_1_) throws IOException {
      long lvt_2_1_ = Util.milliTime();
      if (lvt_2_1_ < this.lastQueryResponseTime + 5000L) {
         byte[] lvt_4_1_ = this.output.toByteArray();
         byte[] lvt_5_1_ = this.getRequestID(p_72624_1_.getSocketAddress());
         lvt_4_1_[1] = lvt_5_1_[0];
         lvt_4_1_[2] = lvt_5_1_[1];
         lvt_4_1_[3] = lvt_5_1_[2];
         lvt_4_1_[4] = lvt_5_1_[3];
         return lvt_4_1_;
      } else {
         this.lastQueryResponseTime = lvt_2_1_;
         this.output.reset();
         this.output.writeInt(0);
         this.output.writeByteArray(this.getRequestID(p_72624_1_.getSocketAddress()));
         this.output.writeString("splitnum");
         this.output.writeInt(128);
         this.output.writeInt(0);
         this.output.writeString("hostname");
         this.output.writeString(this.serverMotd);
         this.output.writeString("gametype");
         this.output.writeString("SMP");
         this.output.writeString("game_id");
         this.output.writeString("MINECRAFT");
         this.output.writeString("version");
         this.output.writeString(this.server.getMinecraftVersion());
         this.output.writeString("plugins");
         this.output.writeString(this.server.getPlugins());
         this.output.writeString("map");
         this.output.writeString(this.worldName);
         this.output.writeString("numplayers");
         this.output.writeString("" + this.getNumberOfPlayers());
         this.output.writeString("maxplayers");
         this.output.writeString("" + this.maxPlayers);
         this.output.writeString("hostport");
         this.output.writeString("" + this.serverPort);
         this.output.writeString("hostip");
         this.output.writeString(this.queryHostname);
         this.output.writeInt(0);
         this.output.writeInt(1);
         this.output.writeString("player_");
         this.output.writeInt(0);
         String[] lvt_4_2_ = this.server.getOnlinePlayerNames();
         String[] var5 = lvt_4_2_;
         int var6 = lvt_4_2_.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            String lvt_8_1_ = var5[var7];
            this.output.writeString(lvt_8_1_);
         }

         this.output.writeInt(0);
         return this.output.toByteArray();
      }
   }

   private byte[] getRequestID(SocketAddress p_72625_1_) {
      return ((QueryThread.Auth)this.queryClients.get(p_72625_1_)).getRequestId();
   }

   private Boolean verifyClientAuth(DatagramPacket p_72627_1_) {
      SocketAddress lvt_2_1_ = p_72627_1_.getSocketAddress();
      if (!this.queryClients.containsKey(lvt_2_1_)) {
         return false;
      } else {
         byte[] lvt_3_1_ = p_72627_1_.getData();
         return ((QueryThread.Auth)this.queryClients.get(lvt_2_1_)).getRandomChallenge() != RConUtils.getBytesAsBEint(lvt_3_1_, 7, p_72627_1_.getLength()) ? false : true;
      }
   }

   private void sendAuthChallenge(DatagramPacket p_72622_1_) throws IOException {
      QueryThread.Auth lvt_2_1_ = new QueryThread.Auth(p_72622_1_);
      this.queryClients.put(p_72622_1_.getSocketAddress(), lvt_2_1_);
      this.sendResponsePacket(lvt_2_1_.getChallengeValue(), p_72622_1_);
   }

   private void cleanQueryClientsMap() {
      if (this.running) {
         long lvt_1_1_ = Util.milliTime();
         if (lvt_1_1_ >= this.lastAuthCheckTime + 30000L) {
            this.lastAuthCheckTime = lvt_1_1_;
            Iterator lvt_3_1_ = this.queryClients.entrySet().iterator();

            while(lvt_3_1_.hasNext()) {
               Entry<SocketAddress, QueryThread.Auth> lvt_4_1_ = (Entry)lvt_3_1_.next();
               if (((QueryThread.Auth)lvt_4_1_.getValue()).hasExpired(lvt_1_1_)) {
                  lvt_3_1_.remove();
               }
            }

         }
      }
   }

   public void run() {
      this.logInfo("Query running on " + this.serverHostname + ":" + this.queryPort);
      this.lastAuthCheckTime = Util.milliTime();
      this.incomingPacket = new DatagramPacket(this.buffer, this.buffer.length);

      try {
         while(this.running) {
            try {
               this.querySocket.receive(this.incomingPacket);
               this.cleanQueryClientsMap();
               this.parseIncomingPacket(this.incomingPacket);
            } catch (SocketTimeoutException var7) {
               this.cleanQueryClientsMap();
            } catch (PortUnreachableException var8) {
            } catch (IOException var9) {
               this.stopWithException(var9);
            }
         }
      } finally {
         this.closeAllSockets();
      }

   }

   public void startThread() {
      if (!this.running) {
         if (0 < this.queryPort && 65535 >= this.queryPort) {
            if (this.initQuerySystem()) {
               super.startThread();
            }

         } else {
            this.logWarning("Invalid query port " + this.queryPort + " found in server.properties (queries disabled)");
         }
      }
   }

   private void stopWithException(Exception p_72623_1_) {
      if (this.running) {
         this.logWarning("Unexpected exception, buggy JRE? (" + p_72623_1_ + ")");
         if (!this.initQuerySystem()) {
            this.logSevere("Failed to recover from buggy JRE, shutting down!");
            this.running = false;
         }

      }
   }

   private boolean initQuerySystem() {
      try {
         this.querySocket = new DatagramSocket(this.queryPort, InetAddress.getByName(this.serverHostname));
         this.registerSocket(this.querySocket);
         this.querySocket.setSoTimeout(500);
         return true;
      } catch (SocketException var2) {
         this.logWarning("Unable to initialise query system on " + this.serverHostname + ":" + this.queryPort + " (Socket): " + var2.getMessage());
      } catch (UnknownHostException var3) {
         this.logWarning("Unable to initialise query system on " + this.serverHostname + ":" + this.queryPort + " (Unknown Host): " + var3.getMessage());
      } catch (Exception var4) {
         this.logWarning("Unable to initialise query system on " + this.serverHostname + ":" + this.queryPort + " (E): " + var4.getMessage());
      }

      return false;
   }

   class Auth {
      private final long timestamp = (new Date()).getTime();
      private final int randomChallenge;
      private final byte[] requestId;
      private final byte[] challengeValue;
      private final String requestIdAsString;

      public Auth(DatagramPacket p_i1535_2_) {
         byte[] lvt_3_1_ = p_i1535_2_.getData();
         this.requestId = new byte[4];
         this.requestId[0] = lvt_3_1_[3];
         this.requestId[1] = lvt_3_1_[4];
         this.requestId[2] = lvt_3_1_[5];
         this.requestId[3] = lvt_3_1_[6];
         this.requestIdAsString = new String(this.requestId, StandardCharsets.UTF_8);
         this.randomChallenge = (new Random()).nextInt(16777216);
         this.challengeValue = String.format("\t%s%d\u0000", this.requestIdAsString, this.randomChallenge).getBytes(StandardCharsets.UTF_8);
      }

      public Boolean hasExpired(long p_72593_1_) {
         return this.timestamp < p_72593_1_;
      }

      public int getRandomChallenge() {
         return this.randomChallenge;
      }

      public byte[] getChallengeValue() {
         return this.challengeValue;
      }

      public byte[] getRequestId() {
         return this.requestId;
      }
   }
}
