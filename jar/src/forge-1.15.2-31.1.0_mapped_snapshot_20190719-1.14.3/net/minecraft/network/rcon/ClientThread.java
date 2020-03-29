package net.minecraft.network.rcon;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClientThread extends RConThread {
   private static final Logger LOGGER = LogManager.getLogger();
   private boolean loggedIn;
   private Socket clientSocket;
   private final byte[] buffer = new byte[1460];
   private final String rconPassword;

   ClientThread(IServer p_i50687_1_, String p_i50687_2_, Socket p_i50687_3_) {
      super(p_i50687_1_, "RCON Client");
      this.clientSocket = p_i50687_3_;

      try {
         this.clientSocket.setSoTimeout(0);
      } catch (Exception var5) {
         this.running = false;
      }

      this.rconPassword = p_i50687_2_;
      this.logInfo("Rcon connection from: " + p_i50687_3_.getInetAddress());
   }

   public void run() {
      while(true) {
         try {
            if (!this.running) {
               return;
            }

            BufferedInputStream lvt_1_1_ = new BufferedInputStream(this.clientSocket.getInputStream());
            int lvt_2_1_ = lvt_1_1_.read(this.buffer, 0, 1460);
            if (10 <= lvt_2_1_) {
               int lvt_3_1_ = 0;
               int lvt_4_1_ = RConUtils.getBytesAsLEInt(this.buffer, 0, lvt_2_1_);
               if (lvt_4_1_ != lvt_2_1_ - 4) {
                  return;
               }

               int lvt_3_1_ = lvt_3_1_ + 4;
               int lvt_5_1_ = RConUtils.getBytesAsLEInt(this.buffer, lvt_3_1_, lvt_2_1_);
               lvt_3_1_ += 4;
               int lvt_6_1_ = RConUtils.getRemainingBytesAsLEInt(this.buffer, lvt_3_1_);
               lvt_3_1_ += 4;
               switch(lvt_6_1_) {
               case 2:
                  if (this.loggedIn) {
                     String lvt_8_1_ = RConUtils.getBytesAsString(this.buffer, lvt_3_1_, lvt_2_1_);

                     try {
                        this.sendMultipacketResponse(lvt_5_1_, this.server.handleRConCommand(lvt_8_1_));
                     } catch (Exception var16) {
                        this.sendMultipacketResponse(lvt_5_1_, "Error executing: " + lvt_8_1_ + " (" + var16.getMessage() + ")");
                     }
                     continue;
                  }

                  this.sendLoginFailedResponse();
                  continue;
               case 3:
                  String lvt_7_1_ = RConUtils.getBytesAsString(this.buffer, lvt_3_1_, lvt_2_1_);
                  int var10000 = lvt_3_1_ + lvt_7_1_.length();
                  if (!lvt_7_1_.isEmpty() && lvt_7_1_.equals(this.rconPassword)) {
                     this.loggedIn = true;
                     this.sendResponse(lvt_5_1_, 2, "");
                     continue;
                  }

                  this.loggedIn = false;
                  this.sendLoginFailedResponse();
                  continue;
               default:
                  this.sendMultipacketResponse(lvt_5_1_, String.format("Unknown request %s", Integer.toHexString(lvt_6_1_)));
                  continue;
               }
            }
         } catch (SocketTimeoutException var17) {
            return;
         } catch (IOException var18) {
            return;
         } catch (Exception var19) {
            LOGGER.error("Exception whilst parsing RCON input", var19);
            return;
         } finally {
            this.closeSocket();
         }

         return;
      }
   }

   private void sendResponse(int p_72654_1_, int p_72654_2_, String p_72654_3_) throws IOException {
      ByteArrayOutputStream lvt_4_1_ = new ByteArrayOutputStream(1248);
      DataOutputStream lvt_5_1_ = new DataOutputStream(lvt_4_1_);
      byte[] lvt_6_1_ = p_72654_3_.getBytes("UTF-8");
      lvt_5_1_.writeInt(Integer.reverseBytes(lvt_6_1_.length + 10));
      lvt_5_1_.writeInt(Integer.reverseBytes(p_72654_1_));
      lvt_5_1_.writeInt(Integer.reverseBytes(p_72654_2_));
      lvt_5_1_.write(lvt_6_1_);
      lvt_5_1_.write(0);
      lvt_5_1_.write(0);
      this.clientSocket.getOutputStream().write(lvt_4_1_.toByteArray());
   }

   private void sendLoginFailedResponse() throws IOException {
      this.sendResponse(-1, 2, "");
   }

   private void sendMultipacketResponse(int p_72655_1_, String p_72655_2_) throws IOException {
      int lvt_3_1_ = p_72655_2_.length();

      do {
         int lvt_4_1_ = 4096 <= lvt_3_1_ ? 4096 : lvt_3_1_;
         this.sendResponse(p_72655_1_, 0, p_72655_2_.substring(0, lvt_4_1_));
         p_72655_2_ = p_72655_2_.substring(lvt_4_1_);
         lvt_3_1_ = p_72655_2_.length();
      } while(0 != lvt_3_1_);

   }

   public void func_219591_b() {
      super.func_219591_b();
      this.closeSocket();
   }

   private void closeSocket() {
      if (null != this.clientSocket) {
         try {
            this.clientSocket.close();
         } catch (IOException var2) {
            this.logWarning("IO: " + var2.getMessage());
         }

         this.clientSocket = null;
      }
   }
}
