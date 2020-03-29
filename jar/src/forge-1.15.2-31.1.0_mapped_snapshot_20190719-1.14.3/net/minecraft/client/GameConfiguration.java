package net.minecraft.client;

import com.mojang.authlib.properties.PropertyMap;
import java.io.File;
import java.net.Proxy;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.ScreenSize;
import net.minecraft.client.resources.FolderResourceIndex;
import net.minecraft.client.resources.ResourceIndex;
import net.minecraft.util.Session;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GameConfiguration {
   public final GameConfiguration.UserInformation userInfo;
   public final ScreenSize displayInfo;
   public final GameConfiguration.FolderInformation folderInfo;
   public final GameConfiguration.GameInformation gameInfo;
   public final GameConfiguration.ServerInformation serverInfo;

   public GameConfiguration(GameConfiguration.UserInformation p_i51071_1_, ScreenSize p_i51071_2_, GameConfiguration.FolderInformation p_i51071_3_, GameConfiguration.GameInformation p_i51071_4_, GameConfiguration.ServerInformation p_i51071_5_) {
      this.userInfo = p_i51071_1_;
      this.displayInfo = p_i51071_2_;
      this.folderInfo = p_i51071_3_;
      this.gameInfo = p_i51071_4_;
      this.serverInfo = p_i51071_5_;
   }

   @OnlyIn(Dist.CLIENT)
   public static class ServerInformation {
      @Nullable
      public final String serverName;
      public final int serverPort;

      public ServerInformation(@Nullable String p_i45487_1_, int p_i45487_2_) {
         this.serverName = p_i45487_1_;
         this.serverPort = p_i45487_2_;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class FolderInformation {
      public final File gameDir;
      public final File resourcePacksDir;
      public final File assetsDir;
      @Nullable
      public final String assetIndex;

      public FolderInformation(File p_i45489_1_, File p_i45489_2_, File p_i45489_3_, @Nullable String p_i45489_4_) {
         this.gameDir = p_i45489_1_;
         this.resourcePacksDir = p_i45489_2_;
         this.assetsDir = p_i45489_3_;
         this.assetIndex = p_i45489_4_;
      }

      public ResourceIndex getAssetsIndex() {
         return (ResourceIndex)(this.assetIndex == null ? new FolderResourceIndex(this.assetsDir) : new ResourceIndex(this.assetsDir, this.assetIndex));
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class UserInformation {
      public final Session session;
      public final PropertyMap userProperties;
      public final PropertyMap profileProperties;
      public final Proxy proxy;

      public UserInformation(Session p_i46375_1_, PropertyMap p_i46375_2_, PropertyMap p_i46375_3_, Proxy p_i46375_4_) {
         this.session = p_i46375_1_;
         this.userProperties = p_i46375_2_;
         this.profileProperties = p_i46375_3_;
         this.proxy = p_i46375_4_;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class GameInformation {
      public final boolean isDemo;
      public final String version;
      public final String versionType;

      public GameInformation(boolean p_i46801_1_, String p_i46801_2_, String p_i46801_3_) {
         this.isDemo = p_i46801_1_;
         this.version = p_i46801_2_;
         this.versionType = p_i46801_3_;
      }
   }
}
