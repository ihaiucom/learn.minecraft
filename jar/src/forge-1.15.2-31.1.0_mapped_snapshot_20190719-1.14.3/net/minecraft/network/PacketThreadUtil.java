package net.minecraft.network;

import net.minecraft.util.concurrent.ThreadTaskExecutor;
import net.minecraft.world.server.ServerWorld;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PacketThreadUtil {
   private static final Logger field_225384_a = LogManager.getLogger();

   public static <T extends INetHandler> void checkThreadAndEnqueue(IPacket<T> p_218796_0_, T p_218796_1_, ServerWorld p_218796_2_) throws ThreadQuickExitException {
      checkThreadAndEnqueue(p_218796_0_, p_218796_1_, (ThreadTaskExecutor)p_218796_2_.getServer());
   }

   public static <T extends INetHandler> void checkThreadAndEnqueue(IPacket<T> p_218797_0_, T p_218797_1_, ThreadTaskExecutor<?> p_218797_2_) throws ThreadQuickExitException {
      if (!p_218797_2_.isOnExecutionThread()) {
         p_218797_2_.execute(() -> {
            if (p_218797_1_.getNetworkManager().isChannelOpen()) {
               p_218797_0_.processPacket(p_218797_1_);
            } else {
               field_225384_a.debug("Ignoring packet due to disconnection: " + p_218797_0_);
            }

         });
         throw ThreadQuickExitException.INSTANCE;
      }
   }
}
