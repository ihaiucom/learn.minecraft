package net.minecraft.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.CommandNode;
import java.io.File;
import java.io.IOException;
import java.net.Proxy;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import net.minecraft.command.CommandSource;
import net.minecraft.command.arguments.ArgumentTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerPropertiesProvider;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.util.datafix.DataFixesManager;
import net.minecraft.world.chunk.listener.LoggingChunkStatusListener;

public class CommandsReport implements IDataProvider {
   private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
   private final DataGenerator generator;

   public CommandsReport(DataGenerator p_i48264_1_) {
      this.generator = p_i48264_1_;
   }

   public void act(DirectoryCache p_200398_1_) throws IOException {
      YggdrasilAuthenticationService lvt_2_1_ = new YggdrasilAuthenticationService(Proxy.NO_PROXY, UUID.randomUUID().toString());
      MinecraftSessionService lvt_3_1_ = lvt_2_1_.createMinecraftSessionService();
      GameProfileRepository lvt_4_1_ = lvt_2_1_.createProfileRepository();
      File lvt_5_1_ = new File(this.generator.getOutputFolder().toFile(), "tmp");
      PlayerProfileCache lvt_6_1_ = new PlayerProfileCache(lvt_4_1_, new File(lvt_5_1_, MinecraftServer.USER_CACHE_FILE.getName()));
      ServerPropertiesProvider lvt_7_1_ = new ServerPropertiesProvider(Paths.get("server.properties"));
      MinecraftServer lvt_8_1_ = new DedicatedServer(lvt_5_1_, lvt_7_1_, DataFixesManager.getDataFixer(), lvt_2_1_, lvt_3_1_, lvt_4_1_, lvt_6_1_, LoggingChunkStatusListener::new, lvt_7_1_.getProperties().worldName);
      Path lvt_9_1_ = this.generator.getOutputFolder().resolve("reports/commands.json");
      CommandDispatcher<CommandSource> lvt_10_1_ = lvt_8_1_.getCommandManager().getDispatcher();
      IDataProvider.save(GSON, p_200398_1_, ArgumentTypes.serialize((CommandDispatcher)lvt_10_1_, (CommandNode)lvt_10_1_.getRoot()), lvt_9_1_);
   }

   public String getName() {
      return "Command Syntax";
   }
}
