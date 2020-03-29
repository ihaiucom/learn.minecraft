package net.minecraft.command.impl;

import com.google.common.base.Joiner;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.command.arguments.ColumnPosArgument;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ColumnPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;

public class ForceLoadCommand {
   private static final Dynamic2CommandExceptionType field_212726_a = new Dynamic2CommandExceptionType((p_212724_0_, p_212724_1_) -> {
      return new TranslationTextComponent("commands.forceload.toobig", new Object[]{p_212724_0_, p_212724_1_});
   });
   private static final Dynamic2CommandExceptionType field_212727_b = new Dynamic2CommandExceptionType((p_212717_0_, p_212717_1_) -> {
      return new TranslationTextComponent("commands.forceload.query.failure", new Object[]{p_212717_0_, p_212717_1_});
   });
   private static final SimpleCommandExceptionType field_212728_c = new SimpleCommandExceptionType(new TranslationTextComponent("commands.forceload.added.failure", new Object[0]));
   private static final SimpleCommandExceptionType field_212729_d = new SimpleCommandExceptionType(new TranslationTextComponent("commands.forceload.removed.failure", new Object[0]));

   public static void register(CommandDispatcher<CommandSource> p_212712_0_) {
      p_212712_0_.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("forceload").requires((p_212716_0_) -> {
         return p_212716_0_.hasPermissionLevel(2);
      })).then(Commands.literal("add").then(((RequiredArgumentBuilder)Commands.argument("from", ColumnPosArgument.columnPos()).executes((p_212711_0_) -> {
         return func_212719_a((CommandSource)p_212711_0_.getSource(), ColumnPosArgument.func_218101_a(p_212711_0_, "from"), ColumnPosArgument.func_218101_a(p_212711_0_, "from"), true);
      })).then(Commands.argument("to", ColumnPosArgument.columnPos()).executes((p_212714_0_) -> {
         return func_212719_a((CommandSource)p_212714_0_.getSource(), ColumnPosArgument.func_218101_a(p_212714_0_, "from"), ColumnPosArgument.func_218101_a(p_212714_0_, "to"), true);
      }))))).then(((LiteralArgumentBuilder)Commands.literal("remove").then(((RequiredArgumentBuilder)Commands.argument("from", ColumnPosArgument.columnPos()).executes((p_218850_0_) -> {
         return func_212719_a((CommandSource)p_218850_0_.getSource(), ColumnPosArgument.func_218101_a(p_218850_0_, "from"), ColumnPosArgument.func_218101_a(p_218850_0_, "from"), false);
      })).then(Commands.argument("to", ColumnPosArgument.columnPos()).executes((p_212718_0_) -> {
         return func_212719_a((CommandSource)p_212718_0_.getSource(), ColumnPosArgument.func_218101_a(p_212718_0_, "from"), ColumnPosArgument.func_218101_a(p_212718_0_, "to"), false);
      })))).then(Commands.literal("all").executes((p_212715_0_) -> {
         return func_212722_b((CommandSource)p_212715_0_.getSource());
      })))).then(((LiteralArgumentBuilder)Commands.literal("query").executes((p_212710_0_) -> {
         return func_212721_a((CommandSource)p_212710_0_.getSource());
      })).then(Commands.argument("pos", ColumnPosArgument.columnPos()).executes((p_212723_0_) -> {
         return func_212713_a((CommandSource)p_212723_0_.getSource(), ColumnPosArgument.func_218101_a(p_212723_0_, "pos"));
      }))));
   }

   private static int func_212713_a(CommandSource p_212713_0_, ColumnPos p_212713_1_) throws CommandSyntaxException {
      ChunkPos lvt_2_1_ = new ChunkPos(p_212713_1_.x >> 4, p_212713_1_.z >> 4);
      DimensionType lvt_3_1_ = p_212713_0_.getWorld().getDimension().getType();
      boolean lvt_4_1_ = p_212713_0_.getServer().getWorld(lvt_3_1_).getForcedChunks().contains(lvt_2_1_.asLong());
      if (lvt_4_1_) {
         p_212713_0_.sendFeedback(new TranslationTextComponent("commands.forceload.query.success", new Object[]{lvt_2_1_, lvt_3_1_}), false);
         return 1;
      } else {
         throw field_212727_b.create(lvt_2_1_, lvt_3_1_);
      }
   }

   private static int func_212721_a(CommandSource p_212721_0_) {
      DimensionType lvt_1_1_ = p_212721_0_.getWorld().getDimension().getType();
      LongSet lvt_2_1_ = p_212721_0_.getServer().getWorld(lvt_1_1_).getForcedChunks();
      int lvt_3_1_ = lvt_2_1_.size();
      if (lvt_3_1_ > 0) {
         String lvt_4_1_ = Joiner.on(", ").join(lvt_2_1_.stream().sorted().map(ChunkPos::new).map(ChunkPos::toString).iterator());
         if (lvt_3_1_ == 1) {
            p_212721_0_.sendFeedback(new TranslationTextComponent("commands.forceload.list.single", new Object[]{lvt_1_1_, lvt_4_1_}), false);
         } else {
            p_212721_0_.sendFeedback(new TranslationTextComponent("commands.forceload.list.multiple", new Object[]{lvt_3_1_, lvt_1_1_, lvt_4_1_}), false);
         }
      } else {
         p_212721_0_.sendErrorMessage(new TranslationTextComponent("commands.forceload.added.none", new Object[]{lvt_1_1_}));
      }

      return lvt_3_1_;
   }

   private static int func_212722_b(CommandSource p_212722_0_) {
      DimensionType lvt_1_1_ = p_212722_0_.getWorld().getDimension().getType();
      ServerWorld lvt_2_1_ = p_212722_0_.getServer().getWorld(lvt_1_1_);
      LongSet lvt_3_1_ = lvt_2_1_.getForcedChunks();
      lvt_3_1_.forEach((p_212720_1_) -> {
         lvt_2_1_.forceChunk(ChunkPos.getX(p_212720_1_), ChunkPos.getZ(p_212720_1_), false);
      });
      p_212722_0_.sendFeedback(new TranslationTextComponent("commands.forceload.removed.all", new Object[]{lvt_1_1_}), true);
      return 0;
   }

   private static int func_212719_a(CommandSource p_212719_0_, ColumnPos p_212719_1_, ColumnPos p_212719_2_, boolean p_212719_3_) throws CommandSyntaxException {
      int lvt_4_1_ = Math.min(p_212719_1_.x, p_212719_2_.x);
      int lvt_5_1_ = Math.min(p_212719_1_.z, p_212719_2_.z);
      int lvt_6_1_ = Math.max(p_212719_1_.x, p_212719_2_.x);
      int lvt_7_1_ = Math.max(p_212719_1_.z, p_212719_2_.z);
      if (lvt_4_1_ >= -30000000 && lvt_5_1_ >= -30000000 && lvt_6_1_ < 30000000 && lvt_7_1_ < 30000000) {
         int lvt_8_1_ = lvt_4_1_ >> 4;
         int lvt_9_1_ = lvt_5_1_ >> 4;
         int lvt_10_1_ = lvt_6_1_ >> 4;
         int lvt_11_1_ = lvt_7_1_ >> 4;
         long lvt_12_1_ = ((long)(lvt_10_1_ - lvt_8_1_) + 1L) * ((long)(lvt_11_1_ - lvt_9_1_) + 1L);
         if (lvt_12_1_ > 256L) {
            throw field_212726_a.create(256, lvt_12_1_);
         } else {
            DimensionType lvt_14_1_ = p_212719_0_.getWorld().getDimension().getType();
            ServerWorld lvt_15_1_ = p_212719_0_.getServer().getWorld(lvt_14_1_);
            ChunkPos lvt_16_1_ = null;
            int lvt_17_1_ = 0;

            for(int lvt_18_1_ = lvt_8_1_; lvt_18_1_ <= lvt_10_1_; ++lvt_18_1_) {
               for(int lvt_19_1_ = lvt_9_1_; lvt_19_1_ <= lvt_11_1_; ++lvt_19_1_) {
                  boolean lvt_20_1_ = lvt_15_1_.forceChunk(lvt_18_1_, lvt_19_1_, p_212719_3_);
                  if (lvt_20_1_) {
                     ++lvt_17_1_;
                     if (lvt_16_1_ == null) {
                        lvt_16_1_ = new ChunkPos(lvt_18_1_, lvt_19_1_);
                     }
                  }
               }
            }

            if (lvt_17_1_ == 0) {
               throw (p_212719_3_ ? field_212728_c : field_212729_d).create();
            } else {
               if (lvt_17_1_ == 1) {
                  p_212719_0_.sendFeedback(new TranslationTextComponent("commands.forceload." + (p_212719_3_ ? "added" : "removed") + ".single", new Object[]{lvt_16_1_, lvt_14_1_}), true);
               } else {
                  ChunkPos lvt_18_2_ = new ChunkPos(lvt_8_1_, lvt_9_1_);
                  ChunkPos lvt_19_2_ = new ChunkPos(lvt_10_1_, lvt_11_1_);
                  p_212719_0_.sendFeedback(new TranslationTextComponent("commands.forceload." + (p_212719_3_ ? "added" : "removed") + ".multiple", new Object[]{lvt_17_1_, lvt_14_1_, lvt_18_2_, lvt_19_2_}), true);
               }

               return lvt_17_1_;
            }
         }
      } else {
         throw BlockPosArgument.POS_OUT_OF_WORLD.create();
      }
   }
}
