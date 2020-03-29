package net.minecraftforge.server.permission;

import com.mojang.authlib.GameProfile;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import javax.annotation.Nullable;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import net.minecraftforge.server.permission.context.IContext;

public enum DefaultPermissionHandler implements IPermissionHandler {
   INSTANCE;

   private static final HashMap<String, DefaultPermissionLevel> PERMISSION_LEVEL_MAP = new HashMap();
   private static final HashMap<String, String> DESCRIPTION_MAP = new HashMap();

   public void registerNode(String node, DefaultPermissionLevel level, String desc) {
      PERMISSION_LEVEL_MAP.put(node, level);
      if (!desc.isEmpty()) {
         DESCRIPTION_MAP.put(node, desc);
      }

   }

   public Collection<String> getRegisteredNodes() {
      return Collections.unmodifiableSet(PERMISSION_LEVEL_MAP.keySet());
   }

   public boolean hasPermission(GameProfile profile, String node, @Nullable IContext context) {
      DefaultPermissionLevel level = this.getDefaultPermissionLevel(node);
      if (level == DefaultPermissionLevel.NONE) {
         return false;
      } else if (level == DefaultPermissionLevel.ALL) {
         return true;
      } else {
         MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
         return server != null && server.getPlayerList().canSendCommands(profile);
      }
   }

   public String getNodeDescription(String node) {
      String desc = (String)DESCRIPTION_MAP.get(node);
      return desc == null ? "" : desc;
   }

   public DefaultPermissionLevel getDefaultPermissionLevel(String node) {
      DefaultPermissionLevel level = (DefaultPermissionLevel)PERMISSION_LEVEL_MAP.get(node);
      return level == null ? DefaultPermissionLevel.NONE : level;
   }
}
