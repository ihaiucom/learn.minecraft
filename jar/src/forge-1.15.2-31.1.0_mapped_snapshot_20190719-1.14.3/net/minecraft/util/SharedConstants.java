package net.minecraft.util;

import com.mojang.bridge.game.GameVersion;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.types.constant.NamespacedStringType;
import io.netty.util.ResourceLeakDetector;
import io.netty.util.ResourceLeakDetector.Level;
import net.minecraft.command.TranslatableExceptionProvider;
import net.minecraft.util.datafix.NamespacedSchema;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SharedConstants {
   public static final Level NETTY_LEAK_DETECTION;
   public static boolean developmentMode;
   public static final char[] ILLEGAL_FILE_CHARACTERS;
   private static GameVersion version;

   public static boolean isAllowedCharacter(char p_71566_0_) {
      return p_71566_0_ != 167 && p_71566_0_ >= ' ' && p_71566_0_ != 127;
   }

   public static String filterAllowedCharacters(String p_71565_0_) {
      StringBuilder stringbuilder = new StringBuilder();
      char[] var2 = p_71565_0_.toCharArray();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         char c0 = var2[var4];
         if (isAllowedCharacter(c0)) {
            stringbuilder.append(c0);
         }
      }

      return stringbuilder.toString();
   }

   @OnlyIn(Dist.CLIENT)
   public static String func_215070_b(String p_215070_0_) {
      StringBuilder stringbuilder = new StringBuilder();

      for(int i = 0; i < p_215070_0_.length(); i = p_215070_0_.offsetByCodePoints(i, 1)) {
         int j = p_215070_0_.codePointAt(i);
         if (!Character.isSupplementaryCodePoint(j)) {
            stringbuilder.appendCodePoint(j);
         } else {
            stringbuilder.append('�');
         }
      }

      return stringbuilder.toString();
   }

   public static GameVersion getVersion() {
      if (version == null) {
         version = MinecraftVersion.load();
      }

      return version;
   }

   static {
      NETTY_LEAK_DETECTION = Level.DISABLED;
      ILLEGAL_FILE_CHARACTERS = new char[]{'/', '\n', '\r', '\t', '\u0000', '\f', '`', '?', '*', '\\', '<', '>', '|', '"', ':'};
      if (System.getProperty("io.netty.leakDetection.level") == null) {
         ResourceLeakDetector.setLevel(NETTY_LEAK_DETECTION);
      }

      CommandSyntaxException.ENABLE_COMMAND_STACK_TRACES = false;
      CommandSyntaxException.BUILT_IN_EXCEPTIONS = new TranslatableExceptionProvider();
      NamespacedStringType.ENSURE_NAMESPACE = NamespacedSchema::ensureNamespaced;
   }
}
