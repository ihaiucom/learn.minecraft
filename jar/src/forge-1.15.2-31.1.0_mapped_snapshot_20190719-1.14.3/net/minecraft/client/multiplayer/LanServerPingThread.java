package net.minecraft.client.multiplayer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.util.DefaultUncaughtExceptionHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class LanServerPingThread extends Thread {
   private static final AtomicInteger UNIQUE_THREAD_ID = new AtomicInteger(0);
   private static final Logger LOGGER = LogManager.getLogger();
   private final String motd;
   private final DatagramSocket socket;
   private boolean isStopping = true;
   private final String address;

   public LanServerPingThread(String p_i1321_1_, String p_i1321_2_) throws IOException {
      super("LanServerPinger #" + UNIQUE_THREAD_ID.incrementAndGet());
      this.motd = p_i1321_1_;
      this.address = p_i1321_2_;
      this.setDaemon(true);
      this.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER));
      this.socket = new DatagramSocket();
   }

   public void run() {
      String lvt_1_1_ = getPingResponse(this.motd, this.address);
      byte[] lvt_2_1_ = lvt_1_1_.getBytes(StandardCharsets.UTF_8);

      while(!this.isInterrupted() && this.isStopping) {
         try {
            InetAddress lvt_3_1_ = InetAddress.getByName("224.0.2.60");
            DatagramPacket lvt_4_1_ = new DatagramPacket(lvt_2_1_, lvt_2_1_.length, lvt_3_1_, 4445);
            this.socket.send(lvt_4_1_);
         } catch (IOException var6) {
            LOGGER.warn("LanServerPinger: {}", var6.getMessage());
            break;
         }

         try {
            sleep(1500L);
         } catch (InterruptedException var5) {
         }
      }

   }

   public void interrupt() {
      super.interrupt();
      this.isStopping = false;
   }

   public static String getPingResponse(String p_77525_0_, String p_77525_1_) {
      return "[MOTD]" + p_77525_0_ + "[/MOTD][AD]" + p_77525_1_ + "[/AD]";
   }

   public static String getMotdFromPingResponse(String p_77524_0_) {
      int lvt_1_1_ = p_77524_0_.indexOf("[MOTD]");
      if (lvt_1_1_ < 0) {
         return "missing no";
      } else {
         int lvt_2_1_ = p_77524_0_.indexOf("[/MOTD]", lvt_1_1_ + "[MOTD]".length());
         return lvt_2_1_ < lvt_1_1_ ? "missing no" : p_77524_0_.substring(lvt_1_1_ + "[MOTD]".length(), lvt_2_1_);
      }
   }

   public static String getAdFromPingResponse(String p_77523_0_) {
      int lvt_1_1_ = p_77523_0_.indexOf("[/MOTD]");
      if (lvt_1_1_ < 0) {
         return null;
      } else {
         int lvt_2_1_ = p_77523_0_.indexOf("[/MOTD]", lvt_1_1_ + "[/MOTD]".length());
         if (lvt_2_1_ >= 0) {
            return null;
         } else {
            int lvt_3_1_ = p_77523_0_.indexOf("[AD]", lvt_1_1_ + "[/MOTD]".length());
            if (lvt_3_1_ < 0) {
               return null;
            } else {
               int lvt_4_1_ = p_77523_0_.indexOf("[/AD]", lvt_3_1_ + "[AD]".length());
               return lvt_4_1_ < lvt_3_1_ ? null : p_77523_0_.substring(lvt_3_1_ + "[AD]".length(), lvt_4_1_);
            }
         }
      }
   }
}
