package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.dimension.DimensionType;

public class DifficultyCommand {
   private static final DynamicCommandExceptionType FAILED_EXCEPTION = new DynamicCommandExceptionType((p_208823_0_) -> {
      return new TranslationTextComponent("commands.difficulty.failure", new Object[]{p_208823_0_});
   });

   public static void register(CommandDispatcher<CommandSource> p_198344_0_) {
      LiteralArgumentBuilder<CommandSource> lvt_1_1_ = Commands.literal("difficulty");
      Difficulty[] var2 = Difficulty.values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Difficulty lvt_5_1_ = var2[var4];
         lvt_1_1_.then(Commands.literal(lvt_5_1_.getTranslationKey()).executes((p_198347_1_) -> {
            return setDifficulty((CommandSource)p_198347_1_.getSource(), lvt_5_1_);
         }));
      }

      p_198344_0_.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)lvt_1_1_.requires((p_198348_0_) -> {
         return p_198348_0_.hasPermissionLevel(2);
      })).executes((p_198346_0_) -> {
         Difficulty lvt_1_1_ = ((CommandSource)p_198346_0_.getSource()).getWorld().getDifficulty();
         ((CommandSource)p_198346_0_.getSource()).sendFeedback(new TranslationTextComponent("commands.difficulty.query", new Object[]{lvt_1_1_.getDisplayName()}), false);
         return lvt_1_1_.getId();
      }));
   }

   public static int setDifficulty(CommandSource p_198345_0_, Difficulty p_198345_1_) throws CommandSyntaxException {
      MinecraftServer lvt_2_1_ = p_198345_0_.getServer();
      if (lvt_2_1_.getWorld(DimensionType.OVERWORLD).getDifficulty() == p_198345_1_) {
         throw FAILED_EXCEPTION.create(p_198345_1_.getTranslationKey());
      } else {
         lvt_2_1_.setDifficultyForAllWorlds(p_198345_1_, true);
         p_198345_0_.sendFeedback(new TranslationTextComponent("commands.difficulty.success", new Object[]{p_198345_1_.getDisplayName()}), true);
         return 0;
      }
   }
}
