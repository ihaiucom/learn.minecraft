package net.minecraftforge.common.util;

import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.UUID;
import net.minecraft.world.server.ServerWorld;

public class FakePlayerFactory {
   private static GameProfile MINECRAFT = new GameProfile(UUID.fromString("41C82C87-7AfB-4024-BA57-13D2C99CAE77"), "[Minecraft]");
   private static Map<GameProfile, FakePlayer> fakePlayers = Maps.newHashMap();
   private static WeakReference<FakePlayer> MINECRAFT_PLAYER = null;

   public static FakePlayer getMinecraft(ServerWorld world) {
      FakePlayer ret = MINECRAFT_PLAYER != null ? (FakePlayer)MINECRAFT_PLAYER.get() : null;
      if (ret == null) {
         ret = get(world, MINECRAFT);
         MINECRAFT_PLAYER = new WeakReference(ret);
      }

      return ret;
   }

   public static FakePlayer get(ServerWorld world, GameProfile username) {
      if (!fakePlayers.containsKey(username)) {
         FakePlayer fakePlayer = new FakePlayer(world, username);
         fakePlayers.put(username, fakePlayer);
      }

      return (FakePlayer)fakePlayers.get(username);
   }

   public static void unloadWorld(ServerWorld world) {
      fakePlayers.entrySet().removeIf((entry) -> {
         return ((FakePlayer)entry.getValue()).world == world;
      });
      if (MINECRAFT_PLAYER != null && MINECRAFT_PLAYER.get() != null && ((FakePlayer)MINECRAFT_PLAYER.get()).world == world) {
         FakePlayer mc = (FakePlayer)MINECRAFT_PLAYER.get();
         if (mc != null && mc.world == world) {
            MINECRAFT_PLAYER = null;
         }
      }

   }
}
