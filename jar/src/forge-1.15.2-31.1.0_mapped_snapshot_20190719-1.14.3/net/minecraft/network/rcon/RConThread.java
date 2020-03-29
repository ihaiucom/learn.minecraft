package net.minecraft.network.rcon;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.util.DefaultWithNameUncaughtExceptionHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class RConThread implements Runnable {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final AtomicInteger THREAD_ID = new AtomicInteger(0);
   protected boolean running;
   protected final IServer server;
   protected final String threadName;
   protected Thread rconThread;
   protected final int maxStopWait = 5;
   protected final List<DatagramSocket> socketList = Lists.newArrayList();
   protected final List<ServerSocket> serverSocketList = Lists.newArrayList();

   protected RConThread(IServer p_i45300_1_, String p_i45300_2_) {
      this.server = p_i45300_1_;
      this.threadName = p_i45300_2_;
      if (this.server.isDebuggingEnabled()) {
         this.logWarning("Debugging is enabled, performance maybe reduced!");
      }

   }

   public synchronized void startThread() {
      this.rconThread = new Thread(this, this.threadName + " #" + THREAD_ID.incrementAndGet());
      this.rconThread.setUncaughtExceptionHandler(new DefaultWithNameUncaughtExceptionHandler(LOGGER));
      this.rconThread.start();
      this.running = true;
   }

   public synchronized void func_219591_b() {
      this.running = false;
      if (null != this.rconThread) {
         int lvt_1_1_ = 0;

         while(this.rconThread.isAlive()) {
            try {
               this.rconThread.join(1000L);
               ++lvt_1_1_;
               if (5 <= lvt_1_1_) {
                  this.logWarning("Waited " + lvt_1_1_ + " seconds attempting force stop!");
                  this.closeAllSockets_do(true);
               } else if (this.rconThread.isAlive()) {
                  this.logWarning("Thread " + this + " (" + this.rconThread.getState() + ") failed to exit after " + lvt_1_1_ + " second(s)");
                  this.logWarning("Stack:");
                  StackTraceElement[] var2 = this.rconThread.getStackTrace();
                  int var3 = var2.length;

                  for(int var4 = 0; var4 < var3; ++var4) {
                     StackTraceElement lvt_5_1_ = var2[var4];
                     this.logWarning(lvt_5_1_.toString());
                  }

                  this.rconThread.interrupt();
               }
            } catch (InterruptedException var6) {
            }
         }

         this.closeAllSockets_do(true);
         this.rconThread = null;
      }
   }

   public boolean isRunning() {
      return this.running;
   }

   protected void logDebug(String p_72607_1_) {
      this.server.logDebug(p_72607_1_);
   }

   protected void logInfo(String p_72609_1_) {
      this.server.logInfo(p_72609_1_);
   }

   protected void logWarning(String p_72606_1_) {
      this.server.logWarning(p_72606_1_);
   }

   protected void logSevere(String p_72610_1_) {
      this.server.logSevere(p_72610_1_);
   }

   protected int getNumberOfPlayers() {
      return this.server.getCurrentPlayerCount();
   }

   protected void registerSocket(DatagramSocket p_72601_1_) {
      this.logDebug("registerSocket: " + p_72601_1_);
      this.socketList.add(p_72601_1_);
   }

   protected boolean closeSocket(DatagramSocket p_72604_1_, boolean p_72604_2_) {
      this.logDebug("closeSocket: " + p_72604_1_);
      if (null == p_72604_1_) {
         return false;
      } else {
         boolean lvt_3_1_ = false;
         if (!p_72604_1_.isClosed()) {
            p_72604_1_.close();
            lvt_3_1_ = true;
         }

         if (p_72604_2_) {
            this.socketList.remove(p_72604_1_);
         }

         return lvt_3_1_;
      }
   }

   protected boolean closeServerSocket(ServerSocket p_72608_1_) {
      return this.closeServerSocket_do(p_72608_1_, true);
   }

   protected boolean closeServerSocket_do(ServerSocket p_72605_1_, boolean p_72605_2_) {
      this.logDebug("closeSocket: " + p_72605_1_);
      if (null == p_72605_1_) {
         return false;
      } else {
         boolean lvt_3_1_ = false;

         try {
            if (!p_72605_1_.isClosed()) {
               p_72605_1_.close();
               lvt_3_1_ = true;
            }
         } catch (IOException var5) {
            this.logWarning("IO: " + var5.getMessage());
         }

         if (p_72605_2_) {
            this.serverSocketList.remove(p_72605_1_);
         }

         return lvt_3_1_;
      }
   }

   protected void closeAllSockets() {
      this.closeAllSockets_do(false);
   }

   protected void closeAllSockets_do(boolean p_72612_1_) {
      int lvt_2_1_ = 0;
      Iterator var3 = this.socketList.iterator();

      while(var3.hasNext()) {
         DatagramSocket lvt_4_1_ = (DatagramSocket)var3.next();
         if (this.closeSocket(lvt_4_1_, false)) {
            ++lvt_2_1_;
         }
      }

      this.socketList.clear();
      var3 = this.serverSocketList.iterator();

      while(var3.hasNext()) {
         ServerSocket lvt_4_2_ = (ServerSocket)var3.next();
         if (this.closeServerSocket_do(lvt_4_2_, false)) {
            ++lvt_2_1_;
         }
      }

      this.serverSocketList.clear();
      if (p_72612_1_ && 0 < lvt_2_1_) {
         this.logWarning("Force closed " + lvt_2_1_ + " sockets");
      }

   }
}
