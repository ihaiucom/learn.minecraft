package net.minecraft.client.network;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.client.multiplayer.LanServerPingThread;
import net.minecraft.util.DefaultUncaughtExceptionHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class LanServerDetector {
   private static final AtomicInteger ATOMIC_COUNTER = new AtomicInteger(0);
   private static final Logger LOGGER = LogManager.getLogger();

   @OnlyIn(Dist.CLIENT)
   public static class LanServerFindThread extends Thread {
      private final LanServerDetector.LanServerList localServerList;
      private final InetAddress broadcastAddress;
      private final MulticastSocket socket;

      public LanServerFindThread(LanServerDetector.LanServerList p_i1320_1_) throws IOException {
         super("LanServerDetector #" + LanServerDetector.ATOMIC_COUNTER.incrementAndGet());
         this.localServerList = p_i1320_1_;
         this.setDaemon(true);
         this.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LanServerDetector.LOGGER));
         this.socket = new MulticastSocket(4445);
         this.broadcastAddress = InetAddress.getByName("224.0.2.60");
         this.socket.setSoTimeout(5000);
         this.socket.joinGroup(this.broadcastAddress);
      }

      public void run() {
         byte[] lvt_2_1_ = new byte[1024];

         while(!this.isInterrupted()) {
            DatagramPacket lvt_1_1_ = new DatagramPacket(lvt_2_1_, lvt_2_1_.length);

            try {
               this.socket.receive(lvt_1_1_);
            } catch (SocketTimeoutException var5) {
               continue;
            } catch (IOException var6) {
               LanServerDetector.LOGGER.error("Couldn't ping server", var6);
               break;
            }

            String lvt_3_3_ = new String(lvt_1_1_.getData(), lvt_1_1_.getOffset(), lvt_1_1_.getLength(), StandardCharsets.UTF_8);
            LanServerDetector.LOGGER.debug("{}: {}", lvt_1_1_.getAddress(), lvt_3_3_);
            this.localServerList.addServer(lvt_3_3_, lvt_1_1_.getAddress());
         }

         try {
            this.socket.leaveGroup(this.broadcastAddress);
         } catch (IOException var4) {
         }

         this.socket.close();
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class LanServerList {
      private final List<LanServerInfo> listOfLanServers = Lists.newArrayList();
      private boolean wasUpdated;

      public synchronized boolean getWasUpdated() {
         return this.wasUpdated;
      }

      public synchronized void setWasNotUpdated() {
         this.wasUpdated = false;
      }

      public synchronized List<LanServerInfo> getLanServers() {
         return Collections.unmodifiableList(this.listOfLanServers);
      }

      public synchronized void addServer(String p_77551_1_, InetAddress p_77551_2_) {
         String lvt_3_1_ = LanServerPingThread.getMotdFromPingResponse(p_77551_1_);
         String lvt_4_1_ = LanServerPingThread.getAdFromPingResponse(p_77551_1_);
         if (lvt_4_1_ != null) {
            lvt_4_1_ = p_77551_2_.getHostAddress() + ":" + lvt_4_1_;
            boolean lvt_5_1_ = false;
            Iterator var6 = this.listOfLanServers.iterator();

            while(var6.hasNext()) {
               LanServerInfo lvt_7_1_ = (LanServerInfo)var6.next();
               if (lvt_7_1_.getServerIpPort().equals(lvt_4_1_)) {
                  lvt_7_1_.updateLastSeen();
                  lvt_5_1_ = true;
                  break;
               }
            }

            if (!lvt_5_1_) {
               this.listOfLanServers.add(new LanServerInfo(lvt_3_1_, lvt_4_1_));
               this.wasUpdated = true;
            }

         }
      }
   }
}
