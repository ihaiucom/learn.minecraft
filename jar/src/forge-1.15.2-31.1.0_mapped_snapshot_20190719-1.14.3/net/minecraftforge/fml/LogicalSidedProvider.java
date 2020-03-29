package net.minecraftforge.fml;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;

public enum LogicalSidedProvider {
   WORKQUEUE((c) -> {
      return (Minecraft)c.get();
   }, (s) -> {
      return (MinecraftServer)s.get();
   }),
   INSTANCE((c) -> {
      return (Minecraft)c.get();
   }, (s) -> {
      return (MinecraftServer)s.get();
   }),
   CLIENTWORLD((c) -> {
      return Optional.of(((Minecraft)c.get()).world);
   }, (s) -> {
      return Optional.empty();
   });

   private static Supplier<Minecraft> client;
   private static Supplier<MinecraftServer> server;
   private final Function<Supplier<Minecraft>, ?> clientSide;
   private final Function<Supplier<MinecraftServer>, ?> serverSide;

   private LogicalSidedProvider(Function<Supplier<Minecraft>, ?> clientSide, Function<Supplier<MinecraftServer>, ?> serverSide) {
      this.clientSide = clientSide;
      this.serverSide = serverSide;
   }

   public static void setClient(Supplier<Minecraft> client) {
      LogicalSidedProvider.client = client;
   }

   public static void setServer(Supplier<MinecraftServer> server) {
      LogicalSidedProvider.server = server;
   }

   public <T> T get(LogicalSide side) {
      return side == LogicalSide.CLIENT ? this.clientSide.apply(client) : this.serverSide.apply(server);
   }
}
