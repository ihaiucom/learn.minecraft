package net.minecraftforge.server.permission;

import com.google.common.base.Preconditions;
import com.mojang.authlib.GameProfile;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.server.permission.context.IContext;
import net.minecraftforge.server.permission.context.PlayerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PermissionAPI {
   private static final Logger LOGGER = LogManager.getLogger();
   private static IPermissionHandler permissionHandler;

   public static void setPermissionHandler(IPermissionHandler handler) {
      Preconditions.checkNotNull(handler, "Permission handler can't be null!");
      LOGGER.warn("Replacing {} with {}", permissionHandler.getClass().getName(), handler.getClass().getName());
      permissionHandler = handler;
   }

   public static IPermissionHandler getPermissionHandler() {
      return permissionHandler;
   }

   public static String registerNode(String node, DefaultPermissionLevel level, String desc) {
      Preconditions.checkNotNull(node, "Permission node can't be null!");
      Preconditions.checkNotNull(level, "Permission level can't be null!");
      Preconditions.checkNotNull(desc, "Permission description can't be null!");
      Preconditions.checkArgument(!node.isEmpty(), "Permission node can't be empty!");
      permissionHandler.registerNode(node, level, desc);
      return node;
   }

   public static boolean hasPermission(GameProfile profile, String node, @Nullable IContext context) {
      Preconditions.checkNotNull(profile, "GameProfile can't be null!");
      Preconditions.checkNotNull(node, "Permission node can't be null!");
      Preconditions.checkArgument(!node.isEmpty(), "Permission node can't be empty!");
      return permissionHandler.hasPermission(profile, node, context);
   }

   public static boolean hasPermission(PlayerEntity player, String node) {
      Preconditions.checkNotNull(player, "Player can't be null!");
      return hasPermission(player.getGameProfile(), node, new PlayerContext(player));
   }

   static {
      permissionHandler = DefaultPermissionHandler.INSTANCE;
   }
}
