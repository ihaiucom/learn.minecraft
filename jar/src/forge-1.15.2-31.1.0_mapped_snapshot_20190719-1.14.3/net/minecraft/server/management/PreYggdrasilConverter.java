package net.minecraft.server.management;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.mojang.authlib.Agent;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.ProfileLookupCallback;
import com.mojang.authlib.yggdrasil.ProfileNotFoundException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.util.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PreYggdrasilConverter {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final File OLD_IPBAN_FILE = new File("banned-ips.txt");
   public static final File OLD_PLAYERBAN_FILE = new File("banned-players.txt");
   public static final File OLD_OPS_FILE = new File("ops.txt");
   public static final File OLD_WHITELIST_FILE = new File("white-list.txt");

   static List<String> readFile(File p_152721_0_, Map<String, String[]> p_152721_1_) throws IOException {
      List<String> lvt_2_1_ = Files.readLines(p_152721_0_, StandardCharsets.UTF_8);
      Iterator var3 = lvt_2_1_.iterator();

      while(var3.hasNext()) {
         String lvt_4_1_ = (String)var3.next();
         lvt_4_1_ = lvt_4_1_.trim();
         if (!lvt_4_1_.startsWith("#") && lvt_4_1_.length() >= 1) {
            String[] lvt_5_1_ = lvt_4_1_.split("\\|");
            p_152721_1_.put(lvt_5_1_[0].toLowerCase(Locale.ROOT), lvt_5_1_);
         }
      }

      return lvt_2_1_;
   }

   private static void lookupNames(MinecraftServer p_152717_0_, Collection<String> p_152717_1_, ProfileLookupCallback p_152717_2_) {
      String[] lvt_3_1_ = (String[])p_152717_1_.stream().filter((p_201150_0_) -> {
         return !StringUtils.isNullOrEmpty(p_201150_0_);
      }).toArray((p_201149_0_) -> {
         return new String[p_201149_0_];
      });
      if (p_152717_0_.isServerInOnlineMode()) {
         p_152717_0_.getGameProfileRepository().findProfilesByNames(lvt_3_1_, Agent.MINECRAFT, p_152717_2_);
      } else {
         String[] var4 = lvt_3_1_;
         int var5 = lvt_3_1_.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            String lvt_7_1_ = var4[var6];
            UUID lvt_8_1_ = PlayerEntity.getUUID(new GameProfile((UUID)null, lvt_7_1_));
            GameProfile lvt_9_1_ = new GameProfile(lvt_8_1_, lvt_7_1_);
            p_152717_2_.onProfileLookupSucceeded(lvt_9_1_);
         }
      }

   }

   public static boolean convertUserBanlist(final MinecraftServer p_152724_0_) {
      final BanList lvt_1_1_ = new BanList(PlayerList.FILE_PLAYERBANS);
      if (OLD_PLAYERBAN_FILE.exists() && OLD_PLAYERBAN_FILE.isFile()) {
         if (lvt_1_1_.getSaveFile().exists()) {
            try {
               lvt_1_1_.readSavedFile();
            } catch (FileNotFoundException var6) {
               LOGGER.warn("Could not load existing file {}", lvt_1_1_.getSaveFile().getName(), var6);
            }
         }

         try {
            final Map<String, String[]> lvt_2_2_ = Maps.newHashMap();
            readFile(OLD_PLAYERBAN_FILE, lvt_2_2_);
            ProfileLookupCallback lvt_3_1_ = new ProfileLookupCallback() {
               public void onProfileLookupSucceeded(GameProfile p_onProfileLookupSucceeded_1_) {
                  p_152724_0_.getPlayerProfileCache().addEntry(p_onProfileLookupSucceeded_1_);
                  String[] lvt_2_1_ = (String[])lvt_2_2_.get(p_onProfileLookupSucceeded_1_.getName().toLowerCase(Locale.ROOT));
                  if (lvt_2_1_ == null) {
                     PreYggdrasilConverter.LOGGER.warn("Could not convert user banlist entry for {}", p_onProfileLookupSucceeded_1_.getName());
                     throw new PreYggdrasilConverter.ConversionError("Profile not in the conversionlist");
                  } else {
                     Date lvt_3_1_ = lvt_2_1_.length > 1 ? PreYggdrasilConverter.parseDate(lvt_2_1_[1], (Date)null) : null;
                     String lvt_4_1_ = lvt_2_1_.length > 2 ? lvt_2_1_[2] : null;
                     Date lvt_5_1_ = lvt_2_1_.length > 3 ? PreYggdrasilConverter.parseDate(lvt_2_1_[3], (Date)null) : null;
                     String lvt_6_1_ = lvt_2_1_.length > 4 ? lvt_2_1_[4] : null;
                     lvt_1_1_.addEntry(new ProfileBanEntry(p_onProfileLookupSucceeded_1_, lvt_3_1_, lvt_4_1_, lvt_5_1_, lvt_6_1_));
                  }
               }

               public void onProfileLookupFailed(GameProfile p_onProfileLookupFailed_1_, Exception p_onProfileLookupFailed_2_) {
                  PreYggdrasilConverter.LOGGER.warn("Could not lookup user banlist entry for {}", p_onProfileLookupFailed_1_.getName(), p_onProfileLookupFailed_2_);
                  if (!(p_onProfileLookupFailed_2_ instanceof ProfileNotFoundException)) {
                     throw new PreYggdrasilConverter.ConversionError("Could not request user " + p_onProfileLookupFailed_1_.getName() + " from backend systems", p_onProfileLookupFailed_2_);
                  }
               }
            };
            lookupNames(p_152724_0_, lvt_2_2_.keySet(), lvt_3_1_);
            lvt_1_1_.writeChanges();
            backupConverted(OLD_PLAYERBAN_FILE);
            return true;
         } catch (IOException var4) {
            LOGGER.warn("Could not read old user banlist to convert it!", var4);
            return false;
         } catch (PreYggdrasilConverter.ConversionError var5) {
            LOGGER.error("Conversion failed, please try again later", var5);
            return false;
         }
      } else {
         return true;
      }
   }

   public static boolean convertIpBanlist(MinecraftServer p_152722_0_) {
      IPBanList lvt_1_1_ = new IPBanList(PlayerList.FILE_IPBANS);
      if (OLD_IPBAN_FILE.exists() && OLD_IPBAN_FILE.isFile()) {
         if (lvt_1_1_.getSaveFile().exists()) {
            try {
               lvt_1_1_.readSavedFile();
            } catch (FileNotFoundException var11) {
               LOGGER.warn("Could not load existing file {}", lvt_1_1_.getSaveFile().getName(), var11);
            }
         }

         try {
            Map<String, String[]> lvt_2_2_ = Maps.newHashMap();
            readFile(OLD_IPBAN_FILE, lvt_2_2_);
            Iterator var3 = lvt_2_2_.keySet().iterator();

            while(var3.hasNext()) {
               String lvt_4_1_ = (String)var3.next();
               String[] lvt_5_1_ = (String[])lvt_2_2_.get(lvt_4_1_);
               Date lvt_6_1_ = lvt_5_1_.length > 1 ? parseDate(lvt_5_1_[1], (Date)null) : null;
               String lvt_7_1_ = lvt_5_1_.length > 2 ? lvt_5_1_[2] : null;
               Date lvt_8_1_ = lvt_5_1_.length > 3 ? parseDate(lvt_5_1_[3], (Date)null) : null;
               String lvt_9_1_ = lvt_5_1_.length > 4 ? lvt_5_1_[4] : null;
               lvt_1_1_.addEntry(new IPBanEntry(lvt_4_1_, lvt_6_1_, lvt_7_1_, lvt_8_1_, lvt_9_1_));
            }

            lvt_1_1_.writeChanges();
            backupConverted(OLD_IPBAN_FILE);
            return true;
         } catch (IOException var10) {
            LOGGER.warn("Could not parse old ip banlist to convert it!", var10);
            return false;
         }
      } else {
         return true;
      }
   }

   public static boolean convertOplist(final MinecraftServer p_152718_0_) {
      final OpList lvt_1_1_ = new OpList(PlayerList.FILE_OPS);
      if (OLD_OPS_FILE.exists() && OLD_OPS_FILE.isFile()) {
         if (lvt_1_1_.getSaveFile().exists()) {
            try {
               lvt_1_1_.readSavedFile();
            } catch (FileNotFoundException var6) {
               LOGGER.warn("Could not load existing file {}", lvt_1_1_.getSaveFile().getName(), var6);
            }
         }

         try {
            List<String> lvt_2_2_ = Files.readLines(OLD_OPS_FILE, StandardCharsets.UTF_8);
            ProfileLookupCallback lvt_3_1_ = new ProfileLookupCallback() {
               public void onProfileLookupSucceeded(GameProfile p_onProfileLookupSucceeded_1_) {
                  p_152718_0_.getPlayerProfileCache().addEntry(p_onProfileLookupSucceeded_1_);
                  lvt_1_1_.addEntry(new OpEntry(p_onProfileLookupSucceeded_1_, p_152718_0_.getOpPermissionLevel(), false));
               }

               public void onProfileLookupFailed(GameProfile p_onProfileLookupFailed_1_, Exception p_onProfileLookupFailed_2_) {
                  PreYggdrasilConverter.LOGGER.warn("Could not lookup oplist entry for {}", p_onProfileLookupFailed_1_.getName(), p_onProfileLookupFailed_2_);
                  if (!(p_onProfileLookupFailed_2_ instanceof ProfileNotFoundException)) {
                     throw new PreYggdrasilConverter.ConversionError("Could not request user " + p_onProfileLookupFailed_1_.getName() + " from backend systems", p_onProfileLookupFailed_2_);
                  }
               }
            };
            lookupNames(p_152718_0_, lvt_2_2_, lvt_3_1_);
            lvt_1_1_.writeChanges();
            backupConverted(OLD_OPS_FILE);
            return true;
         } catch (IOException var4) {
            LOGGER.warn("Could not read old oplist to convert it!", var4);
            return false;
         } catch (PreYggdrasilConverter.ConversionError var5) {
            LOGGER.error("Conversion failed, please try again later", var5);
            return false;
         }
      } else {
         return true;
      }
   }

   public static boolean convertWhitelist(final MinecraftServer p_152710_0_) {
      final WhiteList lvt_1_1_ = new WhiteList(PlayerList.FILE_WHITELIST);
      if (OLD_WHITELIST_FILE.exists() && OLD_WHITELIST_FILE.isFile()) {
         if (lvt_1_1_.getSaveFile().exists()) {
            try {
               lvt_1_1_.readSavedFile();
            } catch (FileNotFoundException var6) {
               LOGGER.warn("Could not load existing file {}", lvt_1_1_.getSaveFile().getName(), var6);
            }
         }

         try {
            List<String> lvt_2_2_ = Files.readLines(OLD_WHITELIST_FILE, StandardCharsets.UTF_8);
            ProfileLookupCallback lvt_3_1_ = new ProfileLookupCallback() {
               public void onProfileLookupSucceeded(GameProfile p_onProfileLookupSucceeded_1_) {
                  p_152710_0_.getPlayerProfileCache().addEntry(p_onProfileLookupSucceeded_1_);
                  lvt_1_1_.addEntry(new WhitelistEntry(p_onProfileLookupSucceeded_1_));
               }

               public void onProfileLookupFailed(GameProfile p_onProfileLookupFailed_1_, Exception p_onProfileLookupFailed_2_) {
                  PreYggdrasilConverter.LOGGER.warn("Could not lookup user whitelist entry for {}", p_onProfileLookupFailed_1_.getName(), p_onProfileLookupFailed_2_);
                  if (!(p_onProfileLookupFailed_2_ instanceof ProfileNotFoundException)) {
                     throw new PreYggdrasilConverter.ConversionError("Could not request user " + p_onProfileLookupFailed_1_.getName() + " from backend systems", p_onProfileLookupFailed_2_);
                  }
               }
            };
            lookupNames(p_152710_0_, lvt_2_2_, lvt_3_1_);
            lvt_1_1_.writeChanges();
            backupConverted(OLD_WHITELIST_FILE);
            return true;
         } catch (IOException var4) {
            LOGGER.warn("Could not read old whitelist to convert it!", var4);
            return false;
         } catch (PreYggdrasilConverter.ConversionError var5) {
            LOGGER.error("Conversion failed, please try again later", var5);
            return false;
         }
      } else {
         return true;
      }
   }

   public static String convertMobOwnerIfNeeded(final MinecraftServer p_187473_0_, String p_187473_1_) {
      if (!StringUtils.isNullOrEmpty(p_187473_1_) && p_187473_1_.length() <= 16) {
         GameProfile lvt_2_1_ = p_187473_0_.getPlayerProfileCache().getGameProfileForUsername(p_187473_1_);
         if (lvt_2_1_ != null && lvt_2_1_.getId() != null) {
            return lvt_2_1_.getId().toString();
         } else if (!p_187473_0_.isSinglePlayer() && p_187473_0_.isServerInOnlineMode()) {
            final List<GameProfile> lvt_3_1_ = Lists.newArrayList();
            ProfileLookupCallback lvt_4_1_ = new ProfileLookupCallback() {
               public void onProfileLookupSucceeded(GameProfile p_onProfileLookupSucceeded_1_) {
                  p_187473_0_.getPlayerProfileCache().addEntry(p_onProfileLookupSucceeded_1_);
                  lvt_3_1_.add(p_onProfileLookupSucceeded_1_);
               }

               public void onProfileLookupFailed(GameProfile p_onProfileLookupFailed_1_, Exception p_onProfileLookupFailed_2_) {
                  PreYggdrasilConverter.LOGGER.warn("Could not lookup user whitelist entry for {}", p_onProfileLookupFailed_1_.getName(), p_onProfileLookupFailed_2_);
               }
            };
            lookupNames(p_187473_0_, Lists.newArrayList(new String[]{p_187473_1_}), lvt_4_1_);
            return !lvt_3_1_.isEmpty() && ((GameProfile)lvt_3_1_.get(0)).getId() != null ? ((GameProfile)lvt_3_1_.get(0)).getId().toString() : "";
         } else {
            return PlayerEntity.getUUID(new GameProfile((UUID)null, p_187473_1_)).toString();
         }
      } else {
         return p_187473_1_;
      }
   }

   public static boolean convertSaveFiles(final DedicatedServer p_152723_0_) {
      final File lvt_1_1_ = func_219585_g(p_152723_0_);
      final File lvt_2_1_ = new File(lvt_1_1_.getParentFile(), "playerdata");
      final File lvt_3_1_ = new File(lvt_1_1_.getParentFile(), "unknownplayers");
      if (lvt_1_1_.exists() && lvt_1_1_.isDirectory()) {
         File[] lvt_4_1_ = lvt_1_1_.listFiles();
         List<String> lvt_5_1_ = Lists.newArrayList();
         File[] var6 = lvt_4_1_;
         int var7 = lvt_4_1_.length;

         for(int var8 = 0; var8 < var7; ++var8) {
            File lvt_9_1_ = var6[var8];
            String lvt_10_1_ = lvt_9_1_.getName();
            if (lvt_10_1_.toLowerCase(Locale.ROOT).endsWith(".dat")) {
               String lvt_11_1_ = lvt_10_1_.substring(0, lvt_10_1_.length() - ".dat".length());
               if (!lvt_11_1_.isEmpty()) {
                  lvt_5_1_.add(lvt_11_1_);
               }
            }
         }

         try {
            final String[] lvt_6_1_ = (String[])lvt_5_1_.toArray(new String[lvt_5_1_.size()]);
            ProfileLookupCallback lvt_7_1_ = new ProfileLookupCallback() {
               public void onProfileLookupSucceeded(GameProfile p_onProfileLookupSucceeded_1_) {
                  p_152723_0_.getPlayerProfileCache().addEntry(p_onProfileLookupSucceeded_1_);
                  UUID lvt_2_1_x = p_onProfileLookupSucceeded_1_.getId();
                  if (lvt_2_1_x == null) {
                     throw new PreYggdrasilConverter.ConversionError("Missing UUID for user profile " + p_onProfileLookupSucceeded_1_.getName());
                  } else {
                     this.renamePlayerFile(lvt_2_1_, this.getFileNameForProfile(p_onProfileLookupSucceeded_1_), lvt_2_1_x.toString());
                  }
               }

               public void onProfileLookupFailed(GameProfile p_onProfileLookupFailed_1_, Exception p_onProfileLookupFailed_2_) {
                  PreYggdrasilConverter.LOGGER.warn("Could not lookup user uuid for {}", p_onProfileLookupFailed_1_.getName(), p_onProfileLookupFailed_2_);
                  if (p_onProfileLookupFailed_2_ instanceof ProfileNotFoundException) {
                     String lvt_3_1_x = this.getFileNameForProfile(p_onProfileLookupFailed_1_);
                     this.renamePlayerFile(lvt_3_1_, lvt_3_1_x, lvt_3_1_x);
                  } else {
                     throw new PreYggdrasilConverter.ConversionError("Could not request user " + p_onProfileLookupFailed_1_.getName() + " from backend systems", p_onProfileLookupFailed_2_);
                  }
               }

               private void renamePlayerFile(File p_152743_1_, String p_152743_2_, String p_152743_3_) {
                  File lvt_4_1_ = new File(lvt_1_1_, p_152743_2_ + ".dat");
                  File lvt_5_1_ = new File(p_152743_1_, p_152743_3_ + ".dat");
                  PreYggdrasilConverter.mkdir(p_152743_1_);
                  if (!lvt_4_1_.renameTo(lvt_5_1_)) {
                     throw new PreYggdrasilConverter.ConversionError("Could not convert file for " + p_152743_2_);
                  }
               }

               private String getFileNameForProfile(GameProfile p_152744_1_) {
                  String lvt_2_1_x = null;
                  String[] var3 = lvt_6_1_;
                  int var4 = var3.length;

                  for(int var5 = 0; var5 < var4; ++var5) {
                     String lvt_6_1_x = var3[var5];
                     if (lvt_6_1_x != null && lvt_6_1_x.equalsIgnoreCase(p_152744_1_.getName())) {
                        lvt_2_1_x = lvt_6_1_x;
                        break;
                     }
                  }

                  if (lvt_2_1_x == null) {
                     throw new PreYggdrasilConverter.ConversionError("Could not find the filename for " + p_152744_1_.getName() + " anymore");
                  } else {
                     return lvt_2_1_x;
                  }
               }
            };
            lookupNames(p_152723_0_, Lists.newArrayList(lvt_6_1_), lvt_7_1_);
            return true;
         } catch (PreYggdrasilConverter.ConversionError var12) {
            LOGGER.error("Conversion failed, please try again later", var12);
            return false;
         }
      } else {
         return true;
      }
   }

   private static void mkdir(File p_152711_0_) {
      if (p_152711_0_.exists()) {
         if (!p_152711_0_.isDirectory()) {
            throw new PreYggdrasilConverter.ConversionError("Can't create directory " + p_152711_0_.getName() + " in world save directory.");
         }
      } else if (!p_152711_0_.mkdirs()) {
         throw new PreYggdrasilConverter.ConversionError("Can't create directory " + p_152711_0_.getName() + " in world save directory.");
      }
   }

   public static boolean func_219587_e(MinecraftServer p_219587_0_) {
      boolean lvt_1_1_ = hasUnconvertableFiles();
      lvt_1_1_ = lvt_1_1_ && func_219589_f(p_219587_0_);
      return lvt_1_1_;
   }

   private static boolean hasUnconvertableFiles() {
      boolean lvt_0_1_ = false;
      if (OLD_PLAYERBAN_FILE.exists() && OLD_PLAYERBAN_FILE.isFile()) {
         lvt_0_1_ = true;
      }

      boolean lvt_1_1_ = false;
      if (OLD_IPBAN_FILE.exists() && OLD_IPBAN_FILE.isFile()) {
         lvt_1_1_ = true;
      }

      boolean lvt_2_1_ = false;
      if (OLD_OPS_FILE.exists() && OLD_OPS_FILE.isFile()) {
         lvt_2_1_ = true;
      }

      boolean lvt_3_1_ = false;
      if (OLD_WHITELIST_FILE.exists() && OLD_WHITELIST_FILE.isFile()) {
         lvt_3_1_ = true;
      }

      if (!lvt_0_1_ && !lvt_1_1_ && !lvt_2_1_ && !lvt_3_1_) {
         return true;
      } else {
         LOGGER.warn("**** FAILED TO START THE SERVER AFTER ACCOUNT CONVERSION!");
         LOGGER.warn("** please remove the following files and restart the server:");
         if (lvt_0_1_) {
            LOGGER.warn("* {}", OLD_PLAYERBAN_FILE.getName());
         }

         if (lvt_1_1_) {
            LOGGER.warn("* {}", OLD_IPBAN_FILE.getName());
         }

         if (lvt_2_1_) {
            LOGGER.warn("* {}", OLD_OPS_FILE.getName());
         }

         if (lvt_3_1_) {
            LOGGER.warn("* {}", OLD_WHITELIST_FILE.getName());
         }

         return false;
      }
   }

   private static boolean func_219589_f(MinecraftServer p_219589_0_) {
      File lvt_1_1_ = func_219585_g(p_219589_0_);
      if (!lvt_1_1_.exists() || !lvt_1_1_.isDirectory() || lvt_1_1_.list().length <= 0 && lvt_1_1_.delete()) {
         return true;
      } else {
         LOGGER.warn("**** DETECTED OLD PLAYER DIRECTORY IN THE WORLD SAVE");
         LOGGER.warn("**** THIS USUALLY HAPPENS WHEN THE AUTOMATIC CONVERSION FAILED IN SOME WAY");
         LOGGER.warn("** please restart the server and if the problem persists, remove the directory '{}'", lvt_1_1_.getPath());
         return false;
      }
   }

   private static File func_219585_g(MinecraftServer p_219585_0_) {
      String lvt_1_1_ = p_219585_0_.getFolderName();
      File lvt_2_1_ = new File(lvt_1_1_);
      return new File(lvt_2_1_, "players");
   }

   private static void backupConverted(File p_152727_0_) {
      File lvt_1_1_ = new File(p_152727_0_.getName() + ".converted");
      p_152727_0_.renameTo(lvt_1_1_);
   }

   private static Date parseDate(String p_152713_0_, Date p_152713_1_) {
      Date lvt_2_2_;
      try {
         lvt_2_2_ = BanEntry.DATE_FORMAT.parse(p_152713_0_);
      } catch (ParseException var4) {
         lvt_2_2_ = p_152713_1_;
      }

      return lvt_2_2_;
   }

   static class ConversionError extends RuntimeException {
      private ConversionError(String p_i1206_1_, Throwable p_i1206_2_) {
         super(p_i1206_1_, p_i1206_2_);
      }

      private ConversionError(String p_i1207_1_) {
         super(p_i1207_1_);
      }

      // $FF: synthetic method
      ConversionError(String p_i1208_1_, Object p_i1208_2_) {
         this(p_i1208_1_);
      }

      // $FF: synthetic method
      ConversionError(String p_i46367_1_, Throwable p_i46367_2_, Object p_i46367_3_) {
         this(p_i46367_1_, p_i46367_2_);
      }
   }
}
