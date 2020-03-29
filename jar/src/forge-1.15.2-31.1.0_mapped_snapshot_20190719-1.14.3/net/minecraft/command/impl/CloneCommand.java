package net.minecraft.command.impl;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.command.arguments.BlockPredicateArgument;
import net.minecraft.inventory.IClearable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.CachedBlockInfo;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;

public class CloneCommand {
   private static final SimpleCommandExceptionType OVERLAP_EXCEPTION = new SimpleCommandExceptionType(new TranslationTextComponent("commands.clone.overlap", new Object[0]));
   private static final Dynamic2CommandExceptionType CLONE_TOO_BIG_EXCEPTION = new Dynamic2CommandExceptionType((p_208796_0_, p_208796_1_) -> {
      return new TranslationTextComponent("commands.clone.toobig", new Object[]{p_208796_0_, p_208796_1_});
   });
   private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslationTextComponent("commands.clone.failed", new Object[0]));
   public static final Predicate<CachedBlockInfo> NOT_AIR = (p_198275_0_) -> {
      return !p_198275_0_.getBlockState().isAir();
   };

   public static void register(CommandDispatcher<CommandSource> p_198265_0_) {
      p_198265_0_.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("clone").requires((p_198271_0_) -> {
         return p_198271_0_.hasPermissionLevel(2);
      })).then(Commands.argument("begin", BlockPosArgument.blockPos()).then(Commands.argument("end", BlockPosArgument.blockPos()).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("destination", BlockPosArgument.blockPos()).executes((p_198264_0_) -> {
         return doClone((CommandSource)p_198264_0_.getSource(), BlockPosArgument.getLoadedBlockPos(p_198264_0_, "begin"), BlockPosArgument.getLoadedBlockPos(p_198264_0_, "end"), BlockPosArgument.getLoadedBlockPos(p_198264_0_, "destination"), (p_198269_0_) -> {
            return true;
         }, CloneCommand.Mode.NORMAL);
      })).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("replace").executes((p_198268_0_) -> {
         return doClone((CommandSource)p_198268_0_.getSource(), BlockPosArgument.getLoadedBlockPos(p_198268_0_, "begin"), BlockPosArgument.getLoadedBlockPos(p_198268_0_, "end"), BlockPosArgument.getLoadedBlockPos(p_198268_0_, "destination"), (p_198272_0_) -> {
            return true;
         }, CloneCommand.Mode.NORMAL);
      })).then(Commands.literal("force").executes((p_198277_0_) -> {
         return doClone((CommandSource)p_198277_0_.getSource(), BlockPosArgument.getLoadedBlockPos(p_198277_0_, "begin"), BlockPosArgument.getLoadedBlockPos(p_198277_0_, "end"), BlockPosArgument.getLoadedBlockPos(p_198277_0_, "destination"), (p_198262_0_) -> {
            return true;
         }, CloneCommand.Mode.FORCE);
      }))).then(Commands.literal("move").executes((p_198280_0_) -> {
         return doClone((CommandSource)p_198280_0_.getSource(), BlockPosArgument.getLoadedBlockPos(p_198280_0_, "begin"), BlockPosArgument.getLoadedBlockPos(p_198280_0_, "end"), BlockPosArgument.getLoadedBlockPos(p_198280_0_, "destination"), (p_198281_0_) -> {
            return true;
         }, CloneCommand.Mode.MOVE);
      }))).then(Commands.literal("normal").executes((p_198270_0_) -> {
         return doClone((CommandSource)p_198270_0_.getSource(), BlockPosArgument.getLoadedBlockPos(p_198270_0_, "begin"), BlockPosArgument.getLoadedBlockPos(p_198270_0_, "end"), BlockPosArgument.getLoadedBlockPos(p_198270_0_, "destination"), (p_198279_0_) -> {
            return true;
         }, CloneCommand.Mode.NORMAL);
      })))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("masked").executes((p_198276_0_) -> {
         return doClone((CommandSource)p_198276_0_.getSource(), BlockPosArgument.getLoadedBlockPos(p_198276_0_, "begin"), BlockPosArgument.getLoadedBlockPos(p_198276_0_, "end"), BlockPosArgument.getLoadedBlockPos(p_198276_0_, "destination"), NOT_AIR, CloneCommand.Mode.NORMAL);
      })).then(Commands.literal("force").executes((p_198282_0_) -> {
         return doClone((CommandSource)p_198282_0_.getSource(), BlockPosArgument.getLoadedBlockPos(p_198282_0_, "begin"), BlockPosArgument.getLoadedBlockPos(p_198282_0_, "end"), BlockPosArgument.getLoadedBlockPos(p_198282_0_, "destination"), NOT_AIR, CloneCommand.Mode.FORCE);
      }))).then(Commands.literal("move").executes((p_198263_0_) -> {
         return doClone((CommandSource)p_198263_0_.getSource(), BlockPosArgument.getLoadedBlockPos(p_198263_0_, "begin"), BlockPosArgument.getLoadedBlockPos(p_198263_0_, "end"), BlockPosArgument.getLoadedBlockPos(p_198263_0_, "destination"), NOT_AIR, CloneCommand.Mode.MOVE);
      }))).then(Commands.literal("normal").executes((p_198266_0_) -> {
         return doClone((CommandSource)p_198266_0_.getSource(), BlockPosArgument.getLoadedBlockPos(p_198266_0_, "begin"), BlockPosArgument.getLoadedBlockPos(p_198266_0_, "end"), BlockPosArgument.getLoadedBlockPos(p_198266_0_, "destination"), NOT_AIR, CloneCommand.Mode.NORMAL);
      })))).then(Commands.literal("filtered").then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("filter", BlockPredicateArgument.blockPredicate()).executes((p_198273_0_) -> {
         return doClone((CommandSource)p_198273_0_.getSource(), BlockPosArgument.getLoadedBlockPos(p_198273_0_, "begin"), BlockPosArgument.getLoadedBlockPos(p_198273_0_, "end"), BlockPosArgument.getLoadedBlockPos(p_198273_0_, "destination"), BlockPredicateArgument.getBlockPredicate(p_198273_0_, "filter"), CloneCommand.Mode.NORMAL);
      })).then(Commands.literal("force").executes((p_198267_0_) -> {
         return doClone((CommandSource)p_198267_0_.getSource(), BlockPosArgument.getLoadedBlockPos(p_198267_0_, "begin"), BlockPosArgument.getLoadedBlockPos(p_198267_0_, "end"), BlockPosArgument.getLoadedBlockPos(p_198267_0_, "destination"), BlockPredicateArgument.getBlockPredicate(p_198267_0_, "filter"), CloneCommand.Mode.FORCE);
      }))).then(Commands.literal("move").executes((p_198261_0_) -> {
         return doClone((CommandSource)p_198261_0_.getSource(), BlockPosArgument.getLoadedBlockPos(p_198261_0_, "begin"), BlockPosArgument.getLoadedBlockPos(p_198261_0_, "end"), BlockPosArgument.getLoadedBlockPos(p_198261_0_, "destination"), BlockPredicateArgument.getBlockPredicate(p_198261_0_, "filter"), CloneCommand.Mode.MOVE);
      }))).then(Commands.literal("normal").executes((p_198278_0_) -> {
         return doClone((CommandSource)p_198278_0_.getSource(), BlockPosArgument.getLoadedBlockPos(p_198278_0_, "begin"), BlockPosArgument.getLoadedBlockPos(p_198278_0_, "end"), BlockPosArgument.getLoadedBlockPos(p_198278_0_, "destination"), BlockPredicateArgument.getBlockPredicate(p_198278_0_, "filter"), CloneCommand.Mode.NORMAL);
      }))))))));
   }

   private static int doClone(CommandSource p_198274_0_, BlockPos p_198274_1_, BlockPos p_198274_2_, BlockPos p_198274_3_, Predicate<CachedBlockInfo> p_198274_4_, CloneCommand.Mode p_198274_5_) throws CommandSyntaxException {
      MutableBoundingBox lvt_6_1_ = new MutableBoundingBox(p_198274_1_, p_198274_2_);
      BlockPos lvt_7_1_ = p_198274_3_.add(lvt_6_1_.getLength());
      MutableBoundingBox lvt_8_1_ = new MutableBoundingBox(p_198274_3_, lvt_7_1_);
      if (!p_198274_5_.allowsOverlap() && lvt_8_1_.intersectsWith(lvt_6_1_)) {
         throw OVERLAP_EXCEPTION.create();
      } else {
         int lvt_9_1_ = lvt_6_1_.getXSize() * lvt_6_1_.getYSize() * lvt_6_1_.getZSize();
         if (lvt_9_1_ > 32768) {
            throw CLONE_TOO_BIG_EXCEPTION.create(32768, lvt_9_1_);
         } else {
            ServerWorld lvt_10_1_ = p_198274_0_.getWorld();
            if (lvt_10_1_.isAreaLoaded(p_198274_1_, p_198274_2_) && lvt_10_1_.isAreaLoaded(p_198274_3_, lvt_7_1_)) {
               List<CloneCommand.BlockInfo> lvt_11_1_ = Lists.newArrayList();
               List<CloneCommand.BlockInfo> lvt_12_1_ = Lists.newArrayList();
               List<CloneCommand.BlockInfo> lvt_13_1_ = Lists.newArrayList();
               Deque<BlockPos> lvt_14_1_ = Lists.newLinkedList();
               BlockPos lvt_15_1_ = new BlockPos(lvt_8_1_.minX - lvt_6_1_.minX, lvt_8_1_.minY - lvt_6_1_.minY, lvt_8_1_.minZ - lvt_6_1_.minZ);

               int lvt_18_3_;
               for(int lvt_16_1_ = lvt_6_1_.minZ; lvt_16_1_ <= lvt_6_1_.maxZ; ++lvt_16_1_) {
                  for(int lvt_17_1_ = lvt_6_1_.minY; lvt_17_1_ <= lvt_6_1_.maxY; ++lvt_17_1_) {
                     for(lvt_18_3_ = lvt_6_1_.minX; lvt_18_3_ <= lvt_6_1_.maxX; ++lvt_18_3_) {
                        BlockPos lvt_19_1_ = new BlockPos(lvt_18_3_, lvt_17_1_, lvt_16_1_);
                        BlockPos lvt_20_1_ = lvt_19_1_.add(lvt_15_1_);
                        CachedBlockInfo lvt_21_1_ = new CachedBlockInfo(lvt_10_1_, lvt_19_1_, false);
                        BlockState lvt_22_1_ = lvt_21_1_.getBlockState();
                        if (p_198274_4_.test(lvt_21_1_)) {
                           TileEntity lvt_23_1_ = lvt_10_1_.getTileEntity(lvt_19_1_);
                           if (lvt_23_1_ != null) {
                              CompoundNBT lvt_24_1_ = lvt_23_1_.write(new CompoundNBT());
                              lvt_12_1_.add(new CloneCommand.BlockInfo(lvt_20_1_, lvt_22_1_, lvt_24_1_));
                              lvt_14_1_.addLast(lvt_19_1_);
                           } else if (!lvt_22_1_.isOpaqueCube(lvt_10_1_, lvt_19_1_) && !lvt_22_1_.func_224756_o(lvt_10_1_, lvt_19_1_)) {
                              lvt_13_1_.add(new CloneCommand.BlockInfo(lvt_20_1_, lvt_22_1_, (CompoundNBT)null));
                              lvt_14_1_.addFirst(lvt_19_1_);
                           } else {
                              lvt_11_1_.add(new CloneCommand.BlockInfo(lvt_20_1_, lvt_22_1_, (CompoundNBT)null));
                              lvt_14_1_.addLast(lvt_19_1_);
                           }
                        }
                     }
                  }
               }

               if (p_198274_5_ == CloneCommand.Mode.MOVE) {
                  Iterator var25 = lvt_14_1_.iterator();

                  BlockPos lvt_17_3_;
                  while(var25.hasNext()) {
                     lvt_17_3_ = (BlockPos)var25.next();
                     TileEntity lvt_18_2_ = lvt_10_1_.getTileEntity(lvt_17_3_);
                     IClearable.clearObj(lvt_18_2_);
                     lvt_10_1_.setBlockState(lvt_17_3_, Blocks.BARRIER.getDefaultState(), 2);
                  }

                  var25 = lvt_14_1_.iterator();

                  while(var25.hasNext()) {
                     lvt_17_3_ = (BlockPos)var25.next();
                     lvt_10_1_.setBlockState(lvt_17_3_, Blocks.AIR.getDefaultState(), 3);
                  }
               }

               List<CloneCommand.BlockInfo> lvt_16_2_ = Lists.newArrayList();
               lvt_16_2_.addAll(lvt_11_1_);
               lvt_16_2_.addAll(lvt_12_1_);
               lvt_16_2_.addAll(lvt_13_1_);
               List<CloneCommand.BlockInfo> lvt_17_4_ = Lists.reverse(lvt_16_2_);
               Iterator var30 = lvt_17_4_.iterator();

               while(var30.hasNext()) {
                  CloneCommand.BlockInfo lvt_19_2_ = (CloneCommand.BlockInfo)var30.next();
                  TileEntity lvt_20_2_ = lvt_10_1_.getTileEntity(lvt_19_2_.pos);
                  IClearable.clearObj(lvt_20_2_);
                  lvt_10_1_.setBlockState(lvt_19_2_.pos, Blocks.BARRIER.getDefaultState(), 2);
               }

               lvt_18_3_ = 0;
               Iterator var32 = lvt_16_2_.iterator();

               CloneCommand.BlockInfo lvt_20_5_;
               while(var32.hasNext()) {
                  lvt_20_5_ = (CloneCommand.BlockInfo)var32.next();
                  if (lvt_10_1_.setBlockState(lvt_20_5_.pos, lvt_20_5_.state, 2)) {
                     ++lvt_18_3_;
                  }
               }

               for(var32 = lvt_12_1_.iterator(); var32.hasNext(); lvt_10_1_.setBlockState(lvt_20_5_.pos, lvt_20_5_.state, 2)) {
                  lvt_20_5_ = (CloneCommand.BlockInfo)var32.next();
                  TileEntity lvt_21_2_ = lvt_10_1_.getTileEntity(lvt_20_5_.pos);
                  if (lvt_20_5_.tag != null && lvt_21_2_ != null) {
                     lvt_20_5_.tag.putInt("x", lvt_20_5_.pos.getX());
                     lvt_20_5_.tag.putInt("y", lvt_20_5_.pos.getY());
                     lvt_20_5_.tag.putInt("z", lvt_20_5_.pos.getZ());
                     lvt_21_2_.read(lvt_20_5_.tag);
                     lvt_21_2_.markDirty();
                  }
               }

               var32 = lvt_17_4_.iterator();

               while(var32.hasNext()) {
                  lvt_20_5_ = (CloneCommand.BlockInfo)var32.next();
                  lvt_10_1_.notifyNeighbors(lvt_20_5_.pos, lvt_20_5_.state.getBlock());
               }

               lvt_10_1_.getPendingBlockTicks().copyTicks(lvt_6_1_, lvt_15_1_);
               if (lvt_18_3_ == 0) {
                  throw FAILED_EXCEPTION.create();
               } else {
                  p_198274_0_.sendFeedback(new TranslationTextComponent("commands.clone.success", new Object[]{lvt_18_3_}), true);
                  return lvt_18_3_;
               }
            } else {
               throw BlockPosArgument.POS_UNLOADED.create();
            }
         }
      }
   }

   static class BlockInfo {
      public final BlockPos pos;
      public final BlockState state;
      @Nullable
      public final CompoundNBT tag;

      public BlockInfo(BlockPos p_i47708_1_, BlockState p_i47708_2_, @Nullable CompoundNBT p_i47708_3_) {
         this.pos = p_i47708_1_;
         this.state = p_i47708_2_;
         this.tag = p_i47708_3_;
      }
   }

   static enum Mode {
      FORCE(true),
      MOVE(true),
      NORMAL(false);

      private final boolean allowOverlap;

      private Mode(boolean p_i47707_3_) {
         this.allowOverlap = p_i47707_3_;
      }

      public boolean allowsOverlap() {
         return this.allowOverlap;
      }
   }
}
