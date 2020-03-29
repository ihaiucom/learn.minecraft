package net.minecraft.command.impl;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.command.arguments.BlockPredicateArgument;
import net.minecraft.command.arguments.BlockStateArgument;
import net.minecraft.command.arguments.BlockStateInput;
import net.minecraft.inventory.IClearable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.CachedBlockInfo;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;

public class FillCommand {
   private static final Dynamic2CommandExceptionType TOO_BIG_EXCEPTION = new Dynamic2CommandExceptionType((p_208897_0_, p_208897_1_) -> {
      return new TranslationTextComponent("commands.fill.toobig", new Object[]{p_208897_0_, p_208897_1_});
   });
   private static final BlockStateInput AIR;
   private static final SimpleCommandExceptionType FAILED_EXCEPTION;

   public static void register(CommandDispatcher<CommandSource> p_198465_0_) {
      p_198465_0_.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("fill").requires((p_198471_0_) -> {
         return p_198471_0_.hasPermissionLevel(2);
      })).then(Commands.argument("from", BlockPosArgument.blockPos()).then(Commands.argument("to", BlockPosArgument.blockPos()).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("block", BlockStateArgument.blockState()).executes((p_198472_0_) -> {
         return doFill((CommandSource)p_198472_0_.getSource(), new MutableBoundingBox(BlockPosArgument.getLoadedBlockPos(p_198472_0_, "from"), BlockPosArgument.getLoadedBlockPos(p_198472_0_, "to")), BlockStateArgument.getBlockState(p_198472_0_, "block"), FillCommand.Mode.REPLACE, (Predicate)null);
      })).then(((LiteralArgumentBuilder)Commands.literal("replace").executes((p_198464_0_) -> {
         return doFill((CommandSource)p_198464_0_.getSource(), new MutableBoundingBox(BlockPosArgument.getLoadedBlockPos(p_198464_0_, "from"), BlockPosArgument.getLoadedBlockPos(p_198464_0_, "to")), BlockStateArgument.getBlockState(p_198464_0_, "block"), FillCommand.Mode.REPLACE, (Predicate)null);
      })).then(Commands.argument("filter", BlockPredicateArgument.blockPredicate()).executes((p_198466_0_) -> {
         return doFill((CommandSource)p_198466_0_.getSource(), new MutableBoundingBox(BlockPosArgument.getLoadedBlockPos(p_198466_0_, "from"), BlockPosArgument.getLoadedBlockPos(p_198466_0_, "to")), BlockStateArgument.getBlockState(p_198466_0_, "block"), FillCommand.Mode.REPLACE, BlockPredicateArgument.getBlockPredicate(p_198466_0_, "filter"));
      })))).then(Commands.literal("keep").executes((p_198462_0_) -> {
         return doFill((CommandSource)p_198462_0_.getSource(), new MutableBoundingBox(BlockPosArgument.getLoadedBlockPos(p_198462_0_, "from"), BlockPosArgument.getLoadedBlockPos(p_198462_0_, "to")), BlockStateArgument.getBlockState(p_198462_0_, "block"), FillCommand.Mode.REPLACE, (p_198469_0_) -> {
            return p_198469_0_.getWorld().isAirBlock(p_198469_0_.getPos());
         });
      }))).then(Commands.literal("outline").executes((p_198467_0_) -> {
         return doFill((CommandSource)p_198467_0_.getSource(), new MutableBoundingBox(BlockPosArgument.getLoadedBlockPos(p_198467_0_, "from"), BlockPosArgument.getLoadedBlockPos(p_198467_0_, "to")), BlockStateArgument.getBlockState(p_198467_0_, "block"), FillCommand.Mode.OUTLINE, (Predicate)null);
      }))).then(Commands.literal("hollow").executes((p_198461_0_) -> {
         return doFill((CommandSource)p_198461_0_.getSource(), new MutableBoundingBox(BlockPosArgument.getLoadedBlockPos(p_198461_0_, "from"), BlockPosArgument.getLoadedBlockPos(p_198461_0_, "to")), BlockStateArgument.getBlockState(p_198461_0_, "block"), FillCommand.Mode.HOLLOW, (Predicate)null);
      }))).then(Commands.literal("destroy").executes((p_198468_0_) -> {
         return doFill((CommandSource)p_198468_0_.getSource(), new MutableBoundingBox(BlockPosArgument.getLoadedBlockPos(p_198468_0_, "from"), BlockPosArgument.getLoadedBlockPos(p_198468_0_, "to")), BlockStateArgument.getBlockState(p_198468_0_, "block"), FillCommand.Mode.DESTROY, (Predicate)null);
      }))))));
   }

   private static int doFill(CommandSource p_198463_0_, MutableBoundingBox p_198463_1_, BlockStateInput p_198463_2_, FillCommand.Mode p_198463_3_, @Nullable Predicate<CachedBlockInfo> p_198463_4_) throws CommandSyntaxException {
      int lvt_5_1_ = p_198463_1_.getXSize() * p_198463_1_.getYSize() * p_198463_1_.getZSize();
      if (lvt_5_1_ > 32768) {
         throw TOO_BIG_EXCEPTION.create(32768, lvt_5_1_);
      } else {
         List<BlockPos> lvt_6_1_ = Lists.newArrayList();
         ServerWorld lvt_7_1_ = p_198463_0_.getWorld();
         int lvt_8_1_ = 0;
         Iterator var9 = BlockPos.getAllInBoxMutable(p_198463_1_.minX, p_198463_1_.minY, p_198463_1_.minZ, p_198463_1_.maxX, p_198463_1_.maxY, p_198463_1_.maxZ).iterator();

         while(true) {
            BlockPos lvt_10_1_;
            do {
               if (!var9.hasNext()) {
                  var9 = lvt_6_1_.iterator();

                  while(var9.hasNext()) {
                     lvt_10_1_ = (BlockPos)var9.next();
                     Block lvt_11_2_ = lvt_7_1_.getBlockState(lvt_10_1_).getBlock();
                     lvt_7_1_.notifyNeighbors(lvt_10_1_, lvt_11_2_);
                  }

                  if (lvt_8_1_ == 0) {
                     throw FAILED_EXCEPTION.create();
                  }

                  p_198463_0_.sendFeedback(new TranslationTextComponent("commands.fill.success", new Object[]{lvt_8_1_}), true);
                  return lvt_8_1_;
               }

               lvt_10_1_ = (BlockPos)var9.next();
            } while(p_198463_4_ != null && !p_198463_4_.test(new CachedBlockInfo(lvt_7_1_, lvt_10_1_, true)));

            BlockStateInput lvt_11_1_ = p_198463_3_.filter.filter(p_198463_1_, lvt_10_1_, p_198463_2_, lvt_7_1_);
            if (lvt_11_1_ != null) {
               TileEntity lvt_12_1_ = lvt_7_1_.getTileEntity(lvt_10_1_);
               IClearable.clearObj(lvt_12_1_);
               if (lvt_11_1_.place(lvt_7_1_, lvt_10_1_, 2)) {
                  lvt_6_1_.add(lvt_10_1_.toImmutable());
                  ++lvt_8_1_;
               }
            }
         }
      }
   }

   static {
      AIR = new BlockStateInput(Blocks.AIR.getDefaultState(), Collections.emptySet(), (CompoundNBT)null);
      FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslationTextComponent("commands.fill.failed", new Object[0]));
   }

   static enum Mode {
      REPLACE((p_198450_0_, p_198450_1_, p_198450_2_, p_198450_3_) -> {
         return p_198450_2_;
      }),
      OUTLINE((p_198454_0_, p_198454_1_, p_198454_2_, p_198454_3_) -> {
         return p_198454_1_.getX() != p_198454_0_.minX && p_198454_1_.getX() != p_198454_0_.maxX && p_198454_1_.getY() != p_198454_0_.minY && p_198454_1_.getY() != p_198454_0_.maxY && p_198454_1_.getZ() != p_198454_0_.minZ && p_198454_1_.getZ() != p_198454_0_.maxZ ? null : p_198454_2_;
      }),
      HOLLOW((p_198453_0_, p_198453_1_, p_198453_2_, p_198453_3_) -> {
         return p_198453_1_.getX() != p_198453_0_.minX && p_198453_1_.getX() != p_198453_0_.maxX && p_198453_1_.getY() != p_198453_0_.minY && p_198453_1_.getY() != p_198453_0_.maxY && p_198453_1_.getZ() != p_198453_0_.minZ && p_198453_1_.getZ() != p_198453_0_.maxZ ? FillCommand.AIR : p_198453_2_;
      }),
      DESTROY((p_198452_0_, p_198452_1_, p_198452_2_, p_198452_3_) -> {
         p_198452_3_.destroyBlock(p_198452_1_, true);
         return p_198452_2_;
      });

      public final SetBlockCommand.IFilter filter;

      private Mode(SetBlockCommand.IFilter p_i47985_3_) {
         this.filter = p_i47985_3_;
      }
   }
}
