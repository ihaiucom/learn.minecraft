package net.minecraftforge.server.permission;

import com.mojang.authlib.GameProfile;
import java.util.Collection;
import javax.annotation.Nullable;
import net.minecraftforge.server.permission.context.IContext;

public interface IPermissionHandler {
   void registerNode(String var1, DefaultPermissionLevel var2, String var3);

   Collection<String> getRegisteredNodes();

   boolean hasPermission(GameProfile var1, String var2, @Nullable IContext var3);

   String getNodeDescription(String var1);
}
