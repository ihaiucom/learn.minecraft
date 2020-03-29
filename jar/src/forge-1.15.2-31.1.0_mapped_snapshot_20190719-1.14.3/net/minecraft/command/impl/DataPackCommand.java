package net.minecraft.command.impl;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.List;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.resources.ResourcePackList;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.storage.WorldInfo;

public class DataPackCommand {
   private static final DynamicCommandExceptionType UNKNOWN_DATA_PACK_EXCEPTION = new DynamicCommandExceptionType((p_208808_0_) -> {
      return new TranslationTextComponent("commands.datapack.unknown", new Object[]{p_208808_0_});
   });
   private static final DynamicCommandExceptionType ENABLE_FAILED_EXCEPTION = new DynamicCommandExceptionType((p_208818_0_) -> {
      return new TranslationTextComponent("commands.datapack.enable.failed", new Object[]{p_208818_0_});
   });
   private static final DynamicCommandExceptionType DISABLE_FAILED_EXCEPTION = new DynamicCommandExceptionType((p_208815_0_) -> {
      return new TranslationTextComponent("commands.datapack.disable.failed", new Object[]{p_208815_0_});
   });
   private static final SuggestionProvider<CommandSource> SUGGEST_ENABLED_PACK = (p_198305_0_, p_198305_1_) -> {
      return ISuggestionProvider.suggest(((CommandSource)p_198305_0_.getSource()).getServer().getResourcePacks().getEnabledPacks().stream().map(ResourcePackInfo::getName).map(StringArgumentType::escapeIfRequired), p_198305_1_);
   };
   private static final SuggestionProvider<CommandSource> SUGGEST_AVAILABLE_PACK = (p_198296_0_, p_198296_1_) -> {
      return ISuggestionProvider.suggest(((CommandSource)p_198296_0_.getSource()).getServer().getResourcePacks().getAvailablePacks().stream().map(ResourcePackInfo::getName).map(StringArgumentType::escapeIfRequired), p_198296_1_);
   };

   public static void register(CommandDispatcher<CommandSource> p_198299_0_) {
      p_198299_0_.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("datapack").requires((p_198301_0_) -> {
         return p_198301_0_.hasPermissionLevel(2);
      })).then(Commands.literal("enable").then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("name", StringArgumentType.string()).suggests(SUGGEST_AVAILABLE_PACK).executes((p_198292_0_) -> {
         return enablePack((CommandSource)p_198292_0_.getSource(), parsePackInfo(p_198292_0_, "name", true), (p_198289_0_, p_198289_1_) -> {
            p_198289_1_.getPriority().func_198993_a(p_198289_0_, p_198289_1_, (p_198304_0_) -> {
               return p_198304_0_;
            }, false);
         });
      })).then(Commands.literal("after").then(Commands.argument("existing", StringArgumentType.string()).suggests(SUGGEST_ENABLED_PACK).executes((p_198307_0_) -> {
         return enablePack((CommandSource)p_198307_0_.getSource(), parsePackInfo(p_198307_0_, "name", true), (p_198308_1_, p_198308_2_) -> {
            p_198308_1_.add(p_198308_1_.indexOf(parsePackInfo(p_198307_0_, "existing", false)) + 1, p_198308_2_);
         });
      })))).then(Commands.literal("before").then(Commands.argument("existing", StringArgumentType.string()).suggests(SUGGEST_ENABLED_PACK).executes((p_198311_0_) -> {
         return enablePack((CommandSource)p_198311_0_.getSource(), parsePackInfo(p_198311_0_, "name", true), (p_198302_1_, p_198302_2_) -> {
            p_198302_1_.add(p_198302_1_.indexOf(parsePackInfo(p_198311_0_, "existing", false)), p_198302_2_);
         });
      })))).then(Commands.literal("last").executes((p_198298_0_) -> {
         return enablePack((CommandSource)p_198298_0_.getSource(), parsePackInfo(p_198298_0_, "name", true), List::add);
      }))).then(Commands.literal("first").executes((p_198300_0_) -> {
         return enablePack((CommandSource)p_198300_0_.getSource(), parsePackInfo(p_198300_0_, "name", true), (p_198310_0_, p_198310_1_) -> {
            p_198310_0_.add(0, p_198310_1_);
         });
      }))))).then(Commands.literal("disable").then(Commands.argument("name", StringArgumentType.string()).suggests(SUGGEST_ENABLED_PACK).executes((p_198295_0_) -> {
         return disablePack((CommandSource)p_198295_0_.getSource(), parsePackInfo(p_198295_0_, "name", false));
      })))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("list").executes((p_198290_0_) -> {
         return listAllPacks((CommandSource)p_198290_0_.getSource());
      })).then(Commands.literal("available").executes((p_198288_0_) -> {
         return listAvailablePacks((CommandSource)p_198288_0_.getSource());
      }))).then(Commands.literal("enabled").executes((p_198309_0_) -> {
         return listEnabledPacks((CommandSource)p_198309_0_.getSource());
      }))));
   }

   private static int enablePack(CommandSource p_198297_0_, ResourcePackInfo p_198297_1_, DataPackCommand.IHandler p_198297_2_) throws CommandSyntaxException {
      ResourcePackList<ResourcePackInfo> lvt_3_1_ = p_198297_0_.getServer().getResourcePacks();
      List<ResourcePackInfo> lvt_4_1_ = Lists.newArrayList(lvt_3_1_.getEnabledPacks());
      p_198297_2_.apply(lvt_4_1_, p_198297_1_);
      lvt_3_1_.setEnabledPacks(lvt_4_1_);
      WorldInfo lvt_5_1_ = p_198297_0_.getServer().getWorld(DimensionType.OVERWORLD).getWorldInfo();
      lvt_5_1_.getEnabledDataPacks().clear();
      lvt_3_1_.getEnabledPacks().forEach((p_198294_1_) -> {
         lvt_5_1_.getEnabledDataPacks().add(p_198294_1_.getName());
      });
      lvt_5_1_.getDisabledDataPacks().remove(p_198297_1_.getName());
      p_198297_0_.sendFeedback(new TranslationTextComponent("commands.datapack.enable.success", new Object[]{p_198297_1_.func_195794_a(true)}), true);
      p_198297_0_.getServer().reload();
      return lvt_3_1_.getEnabledPacks().size();
   }

   private static int disablePack(CommandSource p_198312_0_, ResourcePackInfo p_198312_1_) {
      ResourcePackList<ResourcePackInfo> lvt_2_1_ = p_198312_0_.getServer().getResourcePacks();
      List<ResourcePackInfo> lvt_3_1_ = Lists.newArrayList(lvt_2_1_.getEnabledPacks());
      lvt_3_1_.remove(p_198312_1_);
      lvt_2_1_.setEnabledPacks(lvt_3_1_);
      WorldInfo lvt_4_1_ = p_198312_0_.getServer().getWorld(DimensionType.OVERWORLD).getWorldInfo();
      lvt_4_1_.getEnabledDataPacks().clear();
      lvt_2_1_.getEnabledPacks().forEach((p_198291_1_) -> {
         lvt_4_1_.getEnabledDataPacks().add(p_198291_1_.getName());
      });
      lvt_4_1_.getDisabledDataPacks().add(p_198312_1_.getName());
      p_198312_0_.sendFeedback(new TranslationTextComponent("commands.datapack.disable.success", new Object[]{p_198312_1_.func_195794_a(true)}), true);
      p_198312_0_.getServer().reload();
      return lvt_2_1_.getEnabledPacks().size();
   }

   private static int listAllPacks(CommandSource p_198313_0_) {
      return listEnabledPacks(p_198313_0_) + listAvailablePacks(p_198313_0_);
   }

   private static int listAvailablePacks(CommandSource p_198314_0_) {
      ResourcePackList<ResourcePackInfo> lvt_1_1_ = p_198314_0_.getServer().getResourcePacks();
      if (lvt_1_1_.getAvailablePacks().isEmpty()) {
         p_198314_0_.sendFeedback(new TranslationTextComponent("commands.datapack.list.available.none", new Object[0]), false);
      } else {
         p_198314_0_.sendFeedback(new TranslationTextComponent("commands.datapack.list.available.success", new Object[]{lvt_1_1_.getAvailablePacks().size(), TextComponentUtils.makeList(lvt_1_1_.getAvailablePacks(), (p_198293_0_) -> {
            return p_198293_0_.func_195794_a(false);
         })}), false);
      }

      return lvt_1_1_.getAvailablePacks().size();
   }

   private static int listEnabledPacks(CommandSource p_198315_0_) {
      ResourcePackList<ResourcePackInfo> lvt_1_1_ = p_198315_0_.getServer().getResourcePacks();
      if (lvt_1_1_.getEnabledPacks().isEmpty()) {
         p_198315_0_.sendFeedback(new TranslationTextComponent("commands.datapack.list.enabled.none", new Object[0]), false);
      } else {
         p_198315_0_.sendFeedback(new TranslationTextComponent("commands.datapack.list.enabled.success", new Object[]{lvt_1_1_.getEnabledPacks().size(), TextComponentUtils.makeList(lvt_1_1_.getEnabledPacks(), (p_198306_0_) -> {
            return p_198306_0_.func_195794_a(true);
         })}), false);
      }

      return lvt_1_1_.getEnabledPacks().size();
   }

   private static ResourcePackInfo parsePackInfo(CommandContext<CommandSource> p_198303_0_, String p_198303_1_, boolean p_198303_2_) throws CommandSyntaxException {
      String lvt_3_1_ = StringArgumentType.getString(p_198303_0_, p_198303_1_);
      ResourcePackList<ResourcePackInfo> lvt_4_1_ = ((CommandSource)p_198303_0_.getSource()).getServer().getResourcePacks();
      ResourcePackInfo lvt_5_1_ = lvt_4_1_.getPackInfo(lvt_3_1_);
      if (lvt_5_1_ == null) {
         throw UNKNOWN_DATA_PACK_EXCEPTION.create(lvt_3_1_);
      } else {
         boolean lvt_6_1_ = lvt_4_1_.getEnabledPacks().contains(lvt_5_1_);
         if (p_198303_2_ && lvt_6_1_) {
            throw ENABLE_FAILED_EXCEPTION.create(lvt_3_1_);
         } else if (!p_198303_2_ && !lvt_6_1_) {
            throw DISABLE_FAILED_EXCEPTION.create(lvt_3_1_);
         } else {
            return lvt_5_1_;
         }
      }
   }

   interface IHandler {
      void apply(List<ResourcePackInfo> var1, ResourcePackInfo var2) throws CommandSyntaxException;
   }
}
