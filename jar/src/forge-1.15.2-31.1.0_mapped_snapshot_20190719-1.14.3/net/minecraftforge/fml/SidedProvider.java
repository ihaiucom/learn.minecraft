package net.minecraftforge.fml;

import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.client.ClientHooks;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;

public enum SidedProvider {
   DATAFIXER((c) -> {
      return ((Minecraft)c.get()).getDataFixer();
   }, (s) -> {
      return ((DedicatedServer)s.get()).getDataFixer();
   }, () -> {
      throw new UnsupportedOperationException();
   }),
   SIDED_SETUP_EVENT((c) -> {
      return (mc) -> {
         return new FMLClientSetupEvent(c, mc);
      };
   }, (s) -> {
      return (mc) -> {
         return new FMLDedicatedServerSetupEvent(s, mc);
      };
   }, () -> {
      throw new UnsupportedOperationException();
   }),
   STRIPCHARS((c) -> {
      return ClientHooks::stripSpecialChars;
   }, (s) -> {
      return (str) -> {
         return str;
      };
   }, () -> {
      return (str) -> {
         return str;
      };
   }),
   STARTUPQUERY((c) -> {
      return StartupQuery.QueryWrapperClient.clientQuery(c);
   }, (s) -> {
      return StartupQuery.QueryWrapperServer.dedicatedServerQuery(s);
   }, () -> {
      throw new UnsupportedOperationException();
   });

   private static Supplier<Minecraft> client;
   private static Supplier<DedicatedServer> server;
   private final Function<Supplier<Minecraft>, ?> clientSide;
   private final Function<Supplier<DedicatedServer>, ?> serverSide;
   private final Supplier<?> testSide;

   public static void setClient(Supplier<Minecraft> client) {
      SidedProvider.client = client;
   }

   public static void setServer(Supplier<DedicatedServer> server) {
      SidedProvider.server = server;
   }

   private <T> SidedProvider(Function<Supplier<Minecraft>, T> clientSide, Function<Supplier<DedicatedServer>, T> serverSide, Supplier<T> testSide) {
      this.clientSide = clientSide;
      this.serverSide = serverSide;
      this.testSide = testSide;
   }

   public <T> T get() {
      if (FMLEnvironment.dist == Dist.CLIENT) {
         return this.clientSide.apply(client);
      } else {
         return FMLEnvironment.dist == Dist.DEDICATED_SERVER ? this.serverSide.apply(server) : this.testSide.get();
      }
   }
}
