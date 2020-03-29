package net.minecraft.command.impl;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.GameProfileArgument;
import net.minecraft.command.arguments.MessageArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.management.BanList;
import net.minecraft.server.management.ProfileBanEntry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TranslationTextComponent;

public class BanCommand {
   private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslationTextComponent("commands.ban.failed", new Object[0]));

   public static void register(CommandDispatcher<CommandSource> p_198235_0_) {
      p_198235_0_.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("ban").requires((p_198238_0_) -> {
         return p_198238_0_.getServer().getPlayerList().getBannedPlayers().isLanServer() && p_198238_0_.hasPermissionLevel(3);
      })).then(((RequiredArgumentBuilder)Commands.argument("targets", GameProfileArgument.gameProfile()).executes((p_198234_0_) -> {
         return banGameProfiles((CommandSource)p_198234_0_.getSource(), GameProfileArgument.getGameProfiles(p_198234_0_, "targets"), (ITextComponent)null);
      })).then(Commands.argument("reason", MessageArgument.message()).executes((p_198237_0_) -> {
         return banGameProfiles((CommandSource)p_198237_0_.getSource(), GameProfileArgument.getGameProfiles(p_198237_0_, "targets"), MessageArgument.getMessage(p_198237_0_, "reason"));
      }))));
   }

   private static int banGameProfiles(CommandSource p_198236_0_, Collection<GameProfile> p_198236_1_, @Nullable ITextComponent p_198236_2_) throws CommandSyntaxException {
      BanList lvt_3_1_ = p_198236_0_.getServer().getPlayerList().getBannedPlayers();
      int lvt_4_1_ = 0;
      Iterator var5 = p_198236_1_.iterator();

      while(var5.hasNext()) {
         GameProfile lvt_6_1_ = (GameProfile)var5.next();
         if (!lvt_3_1_.isBanned(lvt_6_1_)) {
            ProfileBanEntry lvt_7_1_ = new ProfileBanEntry(lvt_6_1_, (Date)null, p_198236_0_.getName(), (Date)null, p_198236_2_ == null ? null : p_198236_2_.getString());
            lvt_3_1_.addEntry(lvt_7_1_);
            ++lvt_4_1_;
            p_198236_0_.sendFeedback(new TranslationTextComponent("commands.ban.success", new Object[]{TextComponentUtils.getDisplayName(lvt_6_1_), lvt_7_1_.getBanReason()}), true);
            ServerPlayerEntity lvt_8_1_ = p_198236_0_.getServer().getPlayerList().getPlayerByUUID(lvt_6_1_.getId());
            if (lvt_8_1_ != null) {
               lvt_8_1_.connection.disconnect(new TranslationTextComponent("multiplayer.disconnect.banned", new Object[0]));
            }
         }
      }

      if (lvt_4_1_ == 0) {
         throw FAILED_EXCEPTION.create();
      } else {
         return lvt_4_1_;
      }
   }
}
