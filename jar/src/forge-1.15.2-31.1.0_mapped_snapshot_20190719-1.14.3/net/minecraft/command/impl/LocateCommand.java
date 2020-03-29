package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.ResourceLocationArgument;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.registries.GameData;

public class LocateCommand {
   private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslationTextComponent("commands.locate.failed", new Object[0]));

   public static void register(CommandDispatcher<CommandSource> p_198528_0_) {
      p_198528_0_.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("locate").requires((p_lambda$register$0_0_) -> {
         return p_lambda$register$0_0_.hasPermissionLevel(2);
      })).then(Commands.literal("Pillager_Outpost").executes((p_lambda$register$1_0_) -> {
         return locateStructure((CommandSource)p_lambda$register$1_0_.getSource(), "Pillager_Outpost");
      }))).then(Commands.literal("Mineshaft").executes((p_lambda$register$2_0_) -> {
         return locateStructure((CommandSource)p_lambda$register$2_0_.getSource(), "Mineshaft");
      }))).then(Commands.literal("Mansion").executes((p_lambda$register$3_0_) -> {
         return locateStructure((CommandSource)p_lambda$register$3_0_.getSource(), "Mansion");
      }))).then(Commands.literal("Igloo").executes((p_lambda$register$4_0_) -> {
         return locateStructure((CommandSource)p_lambda$register$4_0_.getSource(), "Igloo");
      }))).then(Commands.literal("Desert_Pyramid").executes((p_lambda$register$5_0_) -> {
         return locateStructure((CommandSource)p_lambda$register$5_0_.getSource(), "Desert_Pyramid");
      }))).then(Commands.literal("Jungle_Pyramid").executes((p_lambda$register$6_0_) -> {
         return locateStructure((CommandSource)p_lambda$register$6_0_.getSource(), "Jungle_Pyramid");
      }))).then(Commands.literal("Swamp_Hut").executes((p_lambda$register$7_0_) -> {
         return locateStructure((CommandSource)p_lambda$register$7_0_.getSource(), "Swamp_Hut");
      }))).then(Commands.literal("Stronghold").executes((p_lambda$register$8_0_) -> {
         return locateStructure((CommandSource)p_lambda$register$8_0_.getSource(), "Stronghold");
      }))).then(Commands.literal("Monument").executes((p_lambda$register$9_0_) -> {
         return locateStructure((CommandSource)p_lambda$register$9_0_.getSource(), "Monument");
      }))).then(Commands.literal("Fortress").executes((p_lambda$register$10_0_) -> {
         return locateStructure((CommandSource)p_lambda$register$10_0_.getSource(), "Fortress");
      }))).then(Commands.literal("EndCity").executes((p_lambda$register$11_0_) -> {
         return locateStructure((CommandSource)p_lambda$register$11_0_.getSource(), "EndCity");
      }))).then(Commands.literal("Ocean_Ruin").executes((p_lambda$register$12_0_) -> {
         return locateStructure((CommandSource)p_lambda$register$12_0_.getSource(), "Ocean_Ruin");
      }))).then(Commands.literal("Buried_Treasure").executes((p_lambda$register$13_0_) -> {
         return locateStructure((CommandSource)p_lambda$register$13_0_.getSource(), "Buried_Treasure");
      }))).then(Commands.literal("Shipwreck").executes((p_lambda$register$14_0_) -> {
         return locateStructure((CommandSource)p_lambda$register$14_0_.getSource(), "Shipwreck");
      }))).then(Commands.literal("Village").executes((p_lambda$register$15_0_) -> {
         return locateStructure((CommandSource)p_lambda$register$15_0_.getSource(), "Village");
      }))).then(Commands.argument("structure_type", ResourceLocationArgument.resourceLocation()).suggests((p_lambda$register$16_0_, p_lambda$register$16_1_) -> {
         return ISuggestionProvider.suggest(GameData.getStructureFeatures().keySet().stream().map(ResourceLocation::toString), p_lambda$register$16_1_);
      }).executes((p_lambda$register$17_0_) -> {
         return locateStructure((CommandSource)p_lambda$register$17_0_.getSource(), ((ResourceLocation)p_lambda$register$17_0_.getArgument("structure_type", ResourceLocation.class)).toString().replace("minecraft:", ""));
      })));
   }

   private static int locateStructure(CommandSource p_198534_0_, String p_198534_1_) throws CommandSyntaxException {
      BlockPos blockpos = new BlockPos(p_198534_0_.getPos());
      BlockPos blockpos1 = p_198534_0_.getWorld().findNearestStructure(p_198534_1_, blockpos, 100, false);
      if (blockpos1 == null) {
         throw FAILED_EXCEPTION.create();
      } else {
         int i = MathHelper.floor(getDistance(blockpos.getX(), blockpos.getZ(), blockpos1.getX(), blockpos1.getZ()));
         ITextComponent itextcomponent = TextComponentUtils.wrapInSquareBrackets(new TranslationTextComponent("chat.coordinates", new Object[]{blockpos1.getX(), "~", blockpos1.getZ()})).applyTextStyle((p_lambda$locateStructure$18_1_) -> {
            p_lambda$locateStructure$18_1_.setColor(TextFormatting.GREEN).setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tp @s " + blockpos1.getX() + " ~ " + blockpos1.getZ())).setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslationTextComponent("chat.coordinates.tooltip", new Object[0])));
         });
         p_198534_0_.sendFeedback(new TranslationTextComponent("commands.locate.success", new Object[]{p_198534_1_, itextcomponent, i}), false);
         return i;
      }
   }

   private static float getDistance(int p_211907_0_, int p_211907_1_, int p_211907_2_, int p_211907_3_) {
      int i = p_211907_2_ - p_211907_0_;
      int j = p_211907_3_ - p_211907_1_;
      return MathHelper.sqrt((float)(i * i + j * j));
   }
}
