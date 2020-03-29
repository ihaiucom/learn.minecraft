package net.minecraftforge.fml;

import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.dedicated.PendingCommand;
import net.minecraftforge.fml.client.gui.screen.ConfirmationScreen;
import net.minecraftforge.fml.client.gui.screen.NotificationScreen;
import net.minecraftforge.fml.common.thread.EffectiveSide;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class StartupQuery {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Marker SQ = MarkerManager.getMarker("STARTUPQUERY");
   private InterruptedException exception;
   private static volatile StartupQuery pending;
   private static volatile boolean aborted = false;
   private String text;
   @Nullable
   private AtomicBoolean result;
   private CountDownLatch signal = new CountDownLatch(1);
   private volatile boolean synchronous;

   public static boolean confirm(String text) {
      StartupQuery query = new StartupQuery(text, new AtomicBoolean());
      query.execute();
      return query.getResult();
   }

   public static void notify(String text) {
      StartupQuery query = new StartupQuery(text, (AtomicBoolean)null);
      query.execute();
   }

   public static void abort() {
      MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
      if (server != null) {
         server.initiateShutdown(false);
      }

      aborted = true;
      throw new StartupQuery.AbortedException();
   }

   public static boolean pendingQuery() {
      return pending != null;
   }

   public static void reset() {
      pending = null;
      aborted = false;
   }

   public static boolean check() {
      if (pending != null) {
         try {
            try {
               ((Consumer)SidedProvider.STARTUPQUERY.get()).accept(pending);
            } catch (RuntimeException var1) {
               LOGGER.error(SQ, "An exception occurred during startup query handling", var1);
            }

            pending.throwException();
         } catch (InterruptedException var2) {
            LOGGER.warn(SQ, "query interrupted");
            abort();
         }

         pending = null;
      }

      return !aborted;
   }

   private void throwException() throws InterruptedException {
      if (this.exception != null) {
         throw this.exception;
      }
   }

   private StartupQuery(String text, @Nullable AtomicBoolean result) {
      this.text = text;
      this.result = result;
   }

   @Nullable
   public Boolean getResult() {
      return this.result == null ? null : this.result.get();
   }

   public void setResult(boolean result) {
      this.result.set(result);
   }

   public String getText() {
      return this.text;
   }

   public boolean isSynchronous() {
      return this.synchronous;
   }

   public void finish() {
      this.signal.countDown();
   }

   private void execute() {
      String prop = System.getProperty("fml.queryResult");
      if (this.result != null && prop != null) {
         LOGGER.info(SQ, "Using fml.queryResult {} to answer the following query:\n{}", prop, this.text);
         if (prop.equalsIgnoreCase("confirm")) {
            this.setResult(true);
            return;
         }

         if (prop.equalsIgnoreCase("cancel")) {
            this.setResult(false);
            return;
         }

         LOGGER.warn(SQ, "Invalid value for fml.queryResult: {}, expected confirm or cancel", prop);
      }

      this.synchronous = false;
      pending = this;
      if (FMLEnvironment.dist.isDedicatedServer() || EffectiveSide.get() == LogicalSide.CLIENT) {
         this.synchronous = true;
         check();
      }

      try {
         this.signal.await();
         reset();
      } catch (InterruptedException var3) {
         LOGGER.warn(SQ, "query interrupted");
         abort();
      }

   }

   public static class QueryWrapperServer {
      public static Consumer<StartupQuery> dedicatedServerQuery(Supplier<DedicatedServer> serverSupplier) {
         return (query) -> {
            DedicatedServer server = (DedicatedServer)serverSupplier.get();
            if (query.getResult() == null) {
               StartupQuery.LOGGER.warn(StartupQuery.SQ, query.getText());
               query.finish();
            } else {
               String text = query.getText() + "\n\nRun the command /fml confirm or or /fml cancel to proceed.\nAlternatively start the server with -Dfml.queryResult=confirm or -Dfml.queryResult=cancel to preselect the answer.";
               StartupQuery.LOGGER.warn(StartupQuery.SQ, text);
               if (!query.isSynchronous()) {
                  return;
               }

               boolean done = false;

               while(true) {
                  if (done || !server.isServerRunning()) {
                     query.finish();
                     break;
                  }

                  if (Thread.interrupted()) {
                     query.exception = new InterruptedException();
                     throw new RuntimeException();
                  }

                  DedicatedServer dedServer = server;
                  synchronized(server.pendingCommandList) {
                     Iterator it = dedServer.pendingCommandList.iterator();

                     while(it.hasNext()) {
                        String cmd = ((PendingCommand)it.next()).command.trim().toLowerCase();
                        cmd = cmd.charAt(0) == '/' ? cmd.substring(1) : cmd;
                        if (cmd.equals("fml confirm")) {
                           StartupQuery.LOGGER.info(StartupQuery.SQ, "confirmed");
                           query.setResult(true);
                           done = true;
                           it.remove();
                        } else if (cmd.equals("fml cancel")) {
                           StartupQuery.LOGGER.info(StartupQuery.SQ, "cancelled");
                           query.setResult(false);
                           done = true;
                           it.remove();
                        } else if (cmd.equals("stop")) {
                           StartupQuery.abort();
                        }
                     }
                  }

                  try {
                     Thread.sleep(10L);
                  } catch (InterruptedException var10) {
                     query.exception = var10;
                  }
               }
            }

         };
      }
   }

   public static class QueryWrapperClient {
      public static Consumer<StartupQuery> clientQuery(Supplier<Minecraft> clientSupplier) {
         return (query) -> {
            Minecraft client = (Minecraft)clientSupplier.get();
            if (query.getResult() == null) {
               client.displayGuiScreen(new NotificationScreen(query));
            } else {
               client.displayGuiScreen(new ConfirmationScreen(query));
            }

            if (query.isSynchronous()) {
               while(client.currentScreen instanceof NotificationScreen) {
                  if (Thread.interrupted()) {
                     query.exception = new InterruptedException();
                     throw new RuntimeException();
                  }

                  try {
                     Thread.sleep(50L);
                  } catch (InterruptedException var4) {
                     query.exception = var4;
                  }
               }
            }

         };
      }
   }

   public static class AbortedException extends RuntimeException {
      private static final long serialVersionUID = -5933665223696833921L;

      private AbortedException() {
      }

      // $FF: synthetic method
      AbortedException(Object x0) {
         this();
      }
   }
}
