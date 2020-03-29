package net.minecraft.command.impl;

import com.google.common.collect.ImmutableMap;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.spi.FileSystemProvider;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.profiler.DebugProfiler;
import net.minecraft.profiler.IProfileResult;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DebugCommand {
   private static final Logger field_225390_a = LogManager.getLogger();
   private static final SimpleCommandExceptionType NOT_RUNNING_EXCEPTION = new SimpleCommandExceptionType(new TranslationTextComponent("commands.debug.notRunning", new Object[0]));
   private static final SimpleCommandExceptionType ALREADY_RUNNING_EXCEPTION = new SimpleCommandExceptionType(new TranslationTextComponent("commands.debug.alreadyRunning", new Object[0]));
   @Nullable
   private static final FileSystemProvider field_225391_d = (FileSystemProvider)FileSystemProvider.installedProviders().stream().filter((p_225386_0_) -> {
      return p_225386_0_.getScheme().equalsIgnoreCase("jar");
   }).findFirst().orElse((Object)null);

   public static void register(CommandDispatcher<CommandSource> p_198330_0_) {
      p_198330_0_.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("debug").requires((p_198332_0_) -> {
         return p_198332_0_.hasPermissionLevel(3);
      })).then(Commands.literal("start").executes((p_198329_0_) -> {
         return startDebug((CommandSource)p_198329_0_.getSource());
      }))).then(Commands.literal("stop").executes((p_198333_0_) -> {
         return stopDebug((CommandSource)p_198333_0_.getSource());
      }))).then(Commands.literal("report").executes((p_225388_0_) -> {
         return func_225389_c((CommandSource)p_225388_0_.getSource());
      })));
   }

   private static int startDebug(CommandSource p_198335_0_) throws CommandSyntaxException {
      MinecraftServer lvt_1_1_ = p_198335_0_.getServer();
      DebugProfiler lvt_2_1_ = lvt_1_1_.getProfiler();
      if (lvt_2_1_.func_219899_d().isEnabled()) {
         throw ALREADY_RUNNING_EXCEPTION.create();
      } else {
         lvt_1_1_.enableProfiling();
         p_198335_0_.sendFeedback(new TranslationTextComponent("commands.debug.started", new Object[]{"Started the debug profiler. Type '/debug stop' to stop it."}), true);
         return 0;
      }
   }

   private static int stopDebug(CommandSource p_198336_0_) throws CommandSyntaxException {
      MinecraftServer lvt_1_1_ = p_198336_0_.getServer();
      DebugProfiler lvt_2_1_ = lvt_1_1_.getProfiler();
      if (!lvt_2_1_.func_219899_d().isEnabled()) {
         throw NOT_RUNNING_EXCEPTION.create();
      } else {
         IProfileResult lvt_3_1_ = lvt_2_1_.func_219899_d().func_219938_b();
         File lvt_4_1_ = new File(lvt_1_1_.getFile("debug"), "profile-results-" + (new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss")).format(new Date()) + ".txt");
         lvt_3_1_.writeToFile(lvt_4_1_);
         float lvt_5_1_ = (float)lvt_3_1_.nanoTime() / 1.0E9F;
         float lvt_6_1_ = (float)lvt_3_1_.ticksSpend() / lvt_5_1_;
         p_198336_0_.sendFeedback(new TranslationTextComponent("commands.debug.stopped", new Object[]{String.format(Locale.ROOT, "%.2f", lvt_5_1_), lvt_3_1_.ticksSpend(), String.format("%.2f", lvt_6_1_)}), true);
         return MathHelper.floor(lvt_6_1_);
      }
   }

   private static int func_225389_c(CommandSource p_225389_0_) {
      MinecraftServer lvt_1_1_ = p_225389_0_.getServer();
      String lvt_2_1_ = "debug-report-" + (new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss")).format(new Date());

      try {
         Path lvt_4_1_ = lvt_1_1_.getFile("debug").toPath();
         Files.createDirectories(lvt_4_1_);
         Path lvt_3_1_;
         if (!SharedConstants.developmentMode && field_225391_d != null) {
            lvt_3_1_ = lvt_4_1_.resolve(lvt_2_1_ + ".zip");
            FileSystem lvt_5_1_ = field_225391_d.newFileSystem(lvt_3_1_, ImmutableMap.of("create", "true"));
            Throwable var6 = null;

            try {
               lvt_1_1_.func_223711_a(lvt_5_1_.getPath("/"));
            } catch (Throwable var16) {
               var6 = var16;
               throw var16;
            } finally {
               if (lvt_5_1_ != null) {
                  if (var6 != null) {
                     try {
                        lvt_5_1_.close();
                     } catch (Throwable var15) {
                        var6.addSuppressed(var15);
                     }
                  } else {
                     lvt_5_1_.close();
                  }
               }

            }
         } else {
            lvt_3_1_ = lvt_4_1_.resolve(lvt_2_1_);
            lvt_1_1_.func_223711_a(lvt_3_1_);
         }

         p_225389_0_.sendFeedback(new TranslationTextComponent("commands.debug.reportSaved", new Object[]{lvt_2_1_}), false);
         return 1;
      } catch (IOException var18) {
         field_225390_a.error("Failed to save debug dump", var18);
         p_225389_0_.sendErrorMessage(new TranslationTextComponent("commands.debug.reportFailed", new Object[0]));
         return 0;
      }
   }
}
