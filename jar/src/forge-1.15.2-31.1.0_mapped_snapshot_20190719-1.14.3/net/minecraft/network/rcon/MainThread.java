package net.minecraft.network.rcon;

import com.google.common.collect.Maps;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.server.dedicated.ServerProperties;

public class MainThread extends RConThread {
   private final int rconPort;
   private String hostname;
   private ServerSocket serverSocket;
   private final String rconPassword;
   private Map<SocketAddress, ClientThread> clientThreads;

   public MainThread(IServer p_i1538_1_) {
      super(p_i1538_1_, "RCON Listener");
      ServerProperties lvt_2_1_ = p_i1538_1_.getServerProperties();
      this.rconPort = lvt_2_1_.rconPort;
      this.rconPassword = lvt_2_1_.rconPassword;
      this.hostname = p_i1538_1_.getHostname();
      if (this.hostname.isEmpty()) {
         this.hostname = "0.0.0.0";
      }

      this.initClientThreadList();
      this.serverSocket = null;
   }

   private void initClientThreadList() {
      this.clientThreads = Maps.newHashMap();
   }

   private void cleanClientThreadsMap() {
      Iterator lvt_1_1_ = this.clientThreads.entrySet().iterator();

      while(lvt_1_1_.hasNext()) {
         Entry<SocketAddress, ClientThread> lvt_2_1_ = (Entry)lvt_1_1_.next();
         if (!((ClientThread)lvt_2_1_.getValue()).isRunning()) {
            lvt_1_1_.remove();
         }
      }

   }

   public void run() {
      this.logInfo("RCON running on " + this.hostname + ":" + this.rconPort);

      try {
         while(this.running) {
            try {
               Socket lvt_1_1_ = this.serverSocket.accept();
               lvt_1_1_.setSoTimeout(500);
               ClientThread lvt_2_1_ = new ClientThread(this.server, this.rconPassword, lvt_1_1_);
               lvt_2_1_.startThread();
               this.clientThreads.put(lvt_1_1_.getRemoteSocketAddress(), lvt_2_1_);
               this.cleanClientThreadsMap();
            } catch (SocketTimeoutException var7) {
               this.cleanClientThreadsMap();
            } catch (IOException var8) {
               if (this.running) {
                  this.logInfo("IO: " + var8.getMessage());
               }
            }
         }
      } finally {
         this.closeServerSocket(this.serverSocket);
      }

   }

   public void startThread() {
      if (this.rconPassword.isEmpty()) {
         this.logWarning("No rcon password set in server.properties, rcon disabled!");
      } else if (0 < this.rconPort && 65535 >= this.rconPort) {
         if (!this.running) {
            try {
               this.serverSocket = new ServerSocket(this.rconPort, 0, InetAddress.getByName(this.hostname));
               this.serverSocket.setSoTimeout(500);
               super.startThread();
            } catch (IOException var2) {
               this.logWarning("Unable to initialise rcon on " + this.hostname + ":" + this.rconPort + " : " + var2.getMessage());
            }

         }
      } else {
         this.logWarning("Invalid rcon port " + this.rconPort + " found in server.properties, rcon disabled!");
      }
   }

   public void func_219591_b() {
      super.func_219591_b();
      Iterator lvt_1_1_ = this.clientThreads.entrySet().iterator();

      while(lvt_1_1_.hasNext()) {
         Entry<SocketAddress, ClientThread> lvt_2_1_ = (Entry)lvt_1_1_.next();
         ((ClientThread)lvt_2_1_.getValue()).func_219591_b();
      }

      this.closeServerSocket(this.serverSocket);
      this.initClientThreadList();
   }
}
