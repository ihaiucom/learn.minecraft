package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.command.arguments.BlockStateArgument;
import net.minecraft.command.arguments.BlockStateInput;
import net.minecraft.inventory.IClearable;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.CachedBlockInfo;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;

public class SetBlockCommand {
   private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslationTextComponent("commands.setblock.failed", new Object[0]));

   public static void register(CommandDispatcher<CommandSource> p_198684_0_) {
      p_198684_0_.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("setblock").requires((p_198688_0_) -> {
         return p_198688_0_.hasPermissionLevel(2);
      })).then(Commands.argument("pos", BlockPosArgument.blockPos()).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("block", BlockStateArgument.blockState()).executes((p_198682_0_) -> {
         return setBlock((CommandSource)p_198682_0_.getSource(), BlockPosArgument.getLoadedBlockPos(p_198682_0_, "pos"), BlockStateArgument.getBlockState(p_198682_0_, "block"), SetBlockCommand.Mode.REPLACE, (Predicate)null);
      })).then(Commands.literal("destroy").executes((p_198685_0_) -> {
         return setBlock((CommandSource)p_198685_0_.getSource(), BlockPosArgument.getLoadedBlockPos(p_198685_0_, "pos"), BlockStateArgument.getBlockState(p_198685_0_, "block"), SetBlockCommand.Mode.DESTROY, (Predicate)null);
      }))).then(Commands.literal("keep").executes((p_198681_0_) -> {
         return setBlock((CommandSource)p_198681_0_.getSource(), BlockPosArgument.getLoadedBlockPos(p_198681_0_, "pos"), BlockStateArgument.getBlockState(p_198681_0_, "block"), SetBlockCommand.Mode.REPLACE, (p_198687_0_) -> {
            return p_198687_0_.getWorld().isAirBlock(p_198687_0_.getPos());
         });
      }))).then(Commands.literal("replace").executes((p_198686_0_) -> {
         return setBlock((CommandSource)p_198686_0_.getSource(), BlockPosArgument.getLoadedBlockPos(p_198686_0_, "pos"), BlockStateArgument.getBlockState(p_198686_0_, "block"), SetBlockCommand.Mode.REPLACE, (Predicate)null);
      })))));
   }

   private static int setBlock(CommandSource p_198683_0_, BlockPos p_198683_1_, BlockStateInput p_198683_2_, SetBlockCommand.Mode p_198683_3_, @Nullable Predicate<CachedBlockInfo> p_198683_4_) throws CommandSyntaxException {
      ServerWorld lvt_5_1_ = p_198683_0_.getWorld();
      if (p_198683_4_ != null && !p_198683_4_.test(new CachedBlockInfo(lvt_5_1_, p_198683_1_, true))) {
         throw FAILED_EXCEPTION.create();
      } else {
         boolean lvt_6_2_;
         if (p_198683_3_ == SetBlockCommand.Mode.DESTROY) {
            lvt_5_1_.destroyBlock(p_198683_1_, true);
            lvt_6_2_ = !p_198683_2_.getState().isAir();
         } else {
            TileEntity lvt_7_1_ = lvt_5_1_.getTileEntity(p_198683_1_);
            IClearable.clearObj(lvt_7_1_);
            lvt_6_2_ = true;
         }

         if (lvt_6_2_ && !p_198683_2_.place(lvt_5_1_, p_198683_1_, 2)) {
            throw FAILED_EXCEPTION.create();
         } else {
            lvt_5_1_.notifyNeighbors(p_198683_1_, p_198683_2_.getState().getBlock());
            p_198683_0_.sendFeedback(new TranslationTextComponent("commands.setblock.success", new Object[]{p_198683_1_.getX(), p_198683_1_.getY(), p_198683_1_.getZ()}), true);
            return 1;
         }
      }
   }

   public interface IFilter {
      @Nullable
      BlockStateInput filter(MutableBoundingBox var1, BlockPos var2, BlockStateInput var3, ServerWorld var4);
   }

   public static enum Mode {
      REPLACE,
      DESTROY;
   }
}
