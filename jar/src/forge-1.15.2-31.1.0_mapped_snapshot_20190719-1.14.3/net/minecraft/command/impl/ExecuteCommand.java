package net.minecraft.command.impl;

import com.google.common.collect.Lists;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ResultConsumer;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.OptionalInt;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;
import java.util.function.IntFunction;
import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.command.arguments.BlockPredicateArgument;
import net.minecraft.command.arguments.DimensionArgument;
import net.minecraft.command.arguments.EntityAnchorArgument;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.IRangeArgument;
import net.minecraft.command.arguments.NBTPathArgument;
import net.minecraft.command.arguments.ObjectiveArgument;
import net.minecraft.command.arguments.ResourceLocationArgument;
import net.minecraft.command.arguments.RotationArgument;
import net.minecraft.command.arguments.ScoreHolderArgument;
import net.minecraft.command.arguments.SwizzleArgument;
import net.minecraft.command.arguments.Vec3Argument;
import net.minecraft.command.impl.data.DataCommand;
import net.minecraft.command.impl.data.IDataAccessor;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.ByteNBT;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.DoubleNBT;
import net.minecraft.nbt.FloatNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.IntNBT;
import net.minecraft.nbt.LongNBT;
import net.minecraft.nbt.ShortNBT;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.CustomServerBossInfo;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.CachedBlockInfo;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameterSets;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraft.world.storage.loot.LootPredicateManager;
import net.minecraft.world.storage.loot.conditions.ILootCondition;

public class ExecuteCommand {
   private static final Dynamic2CommandExceptionType TOO_MANY_BLOCKS = new Dynamic2CommandExceptionType((p_208885_0_, p_208885_1_) -> {
      return new TranslationTextComponent("commands.execute.blocks.toobig", new Object[]{p_208885_0_, p_208885_1_});
   });
   private static final SimpleCommandExceptionType TEST_FAILED = new SimpleCommandExceptionType(new TranslationTextComponent("commands.execute.conditional.fail", new Object[0]));
   private static final DynamicCommandExceptionType TEST_FAILED_COUNT = new DynamicCommandExceptionType((p_210446_0_) -> {
      return new TranslationTextComponent("commands.execute.conditional.fail_count", new Object[]{p_210446_0_});
   });
   private static final BinaryOperator<ResultConsumer<CommandSource>> COMBINE_ON_RESULT_COMPLETE = (p_209937_0_, p_209937_1_) -> {
      return (p_209939_2_, p_209939_3_, p_209939_4_) -> {
         p_209937_0_.onCommandComplete(p_209939_2_, p_209939_3_, p_209939_4_);
         p_209937_1_.onCommandComplete(p_209939_2_, p_209939_3_, p_209939_4_);
      };
   };
   private static final SuggestionProvider<CommandSource> field_229760_e_ = (p_229763_0_, p_229763_1_) -> {
      LootPredicateManager lvt_2_1_ = ((CommandSource)p_229763_0_.getSource()).getServer().func_229736_aP_();
      return ISuggestionProvider.suggestIterable(lvt_2_1_.func_227513_a_(), p_229763_1_);
   };

   public static void register(CommandDispatcher<CommandSource> p_198378_0_) {
      LiteralCommandNode<CommandSource> lvt_1_1_ = p_198378_0_.register((LiteralArgumentBuilder)Commands.literal("execute").requires((p_198387_0_) -> {
         return p_198387_0_.hasPermissionLevel(2);
      }));
      p_198378_0_.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("execute").requires((p_229766_0_) -> {
         return p_229766_0_.hasPermissionLevel(2);
      })).then(Commands.literal("run").redirect(p_198378_0_.getRoot()))).then(makeIfCommand(lvt_1_1_, Commands.literal("if"), true))).then(makeIfCommand(lvt_1_1_, Commands.literal("unless"), false))).then(Commands.literal("as").then(Commands.argument("targets", EntityArgument.entities()).fork(lvt_1_1_, (p_198384_0_) -> {
         List<CommandSource> lvt_1_1_ = Lists.newArrayList();
         Iterator var2 = EntityArgument.getEntitiesAllowingNone(p_198384_0_, "targets").iterator();

         while(var2.hasNext()) {
            Entity lvt_3_1_ = (Entity)var2.next();
            lvt_1_1_.add(((CommandSource)p_198384_0_.getSource()).withEntity(lvt_3_1_));
         }

         return lvt_1_1_;
      })))).then(Commands.literal("at").then(Commands.argument("targets", EntityArgument.entities()).fork(lvt_1_1_, (p_229809_0_) -> {
         List<CommandSource> lvt_1_1_ = Lists.newArrayList();
         Iterator var2 = EntityArgument.getEntitiesAllowingNone(p_229809_0_, "targets").iterator();

         while(var2.hasNext()) {
            Entity lvt_3_1_ = (Entity)var2.next();
            lvt_1_1_.add(((CommandSource)p_229809_0_.getSource()).withWorld((ServerWorld)lvt_3_1_.world).withPos(lvt_3_1_.getPositionVector()).withRotation(lvt_3_1_.getPitchYaw()));
         }

         return lvt_1_1_;
      })))).then(((LiteralArgumentBuilder)Commands.literal("store").then(makeStoreSubcommand(lvt_1_1_, Commands.literal("result"), true))).then(makeStoreSubcommand(lvt_1_1_, Commands.literal("success"), false)))).then(((LiteralArgumentBuilder)Commands.literal("positioned").then(Commands.argument("pos", Vec3Argument.vec3()).redirect(lvt_1_1_, (p_229808_0_) -> {
         return ((CommandSource)p_229808_0_.getSource()).withPos(Vec3Argument.getVec3(p_229808_0_, "pos")).withEntityAnchorType(EntityAnchorArgument.Type.FEET);
      }))).then(Commands.literal("as").then(Commands.argument("targets", EntityArgument.entities()).fork(lvt_1_1_, (p_229807_0_) -> {
         List<CommandSource> lvt_1_1_ = Lists.newArrayList();
         Iterator var2 = EntityArgument.getEntitiesAllowingNone(p_229807_0_, "targets").iterator();

         while(var2.hasNext()) {
            Entity lvt_3_1_ = (Entity)var2.next();
            lvt_1_1_.add(((CommandSource)p_229807_0_.getSource()).withPos(lvt_3_1_.getPositionVector()));
         }

         return lvt_1_1_;
      }))))).then(((LiteralArgumentBuilder)Commands.literal("rotated").then(Commands.argument("rot", RotationArgument.rotation()).redirect(lvt_1_1_, (p_229806_0_) -> {
         return ((CommandSource)p_229806_0_.getSource()).withRotation(RotationArgument.getRotation(p_229806_0_, "rot").getRotation((CommandSource)p_229806_0_.getSource()));
      }))).then(Commands.literal("as").then(Commands.argument("targets", EntityArgument.entities()).fork(lvt_1_1_, (p_201083_0_) -> {
         List<CommandSource> lvt_1_1_ = Lists.newArrayList();
         Iterator var2 = EntityArgument.getEntitiesAllowingNone(p_201083_0_, "targets").iterator();

         while(var2.hasNext()) {
            Entity lvt_3_1_ = (Entity)var2.next();
            lvt_1_1_.add(((CommandSource)p_201083_0_.getSource()).withRotation(lvt_3_1_.getPitchYaw()));
         }

         return lvt_1_1_;
      }))))).then(((LiteralArgumentBuilder)Commands.literal("facing").then(Commands.literal("entity").then(Commands.argument("targets", EntityArgument.entities()).then(Commands.argument("anchor", EntityAnchorArgument.entityAnchor()).fork(lvt_1_1_, (p_229805_0_) -> {
         List<CommandSource> lvt_1_1_ = Lists.newArrayList();
         EntityAnchorArgument.Type lvt_2_1_ = EntityAnchorArgument.getEntityAnchor(p_229805_0_, "anchor");
         Iterator var3 = EntityArgument.getEntitiesAllowingNone(p_229805_0_, "targets").iterator();

         while(var3.hasNext()) {
            Entity lvt_4_1_ = (Entity)var3.next();
            lvt_1_1_.add(((CommandSource)p_229805_0_.getSource()).withRotation(lvt_4_1_, lvt_2_1_));
         }

         return lvt_1_1_;
      }))))).then(Commands.argument("pos", Vec3Argument.vec3()).redirect(lvt_1_1_, (p_198381_0_) -> {
         return ((CommandSource)p_198381_0_.getSource()).withRotation(Vec3Argument.getVec3(p_198381_0_, "pos"));
      })))).then(Commands.literal("align").then(Commands.argument("axes", SwizzleArgument.swizzle()).redirect(lvt_1_1_, (p_201091_0_) -> {
         return ((CommandSource)p_201091_0_.getSource()).withPos(((CommandSource)p_201091_0_.getSource()).getPos().align(SwizzleArgument.getSwizzle(p_201091_0_, "axes")));
      })))).then(Commands.literal("anchored").then(Commands.argument("anchor", EntityAnchorArgument.entityAnchor()).redirect(lvt_1_1_, (p_201089_0_) -> {
         return ((CommandSource)p_201089_0_.getSource()).withEntityAnchorType(EntityAnchorArgument.getEntityAnchor(p_201089_0_, "anchor"));
      })))).then(Commands.literal("in").then(Commands.argument("dimension", DimensionArgument.getDimension()).redirect(lvt_1_1_, (p_229804_0_) -> {
         return ((CommandSource)p_229804_0_.getSource()).withWorld(((CommandSource)p_229804_0_.getSource()).getServer().getWorld(DimensionArgument.func_212592_a(p_229804_0_, "dimension")));
      }))));
   }

   private static ArgumentBuilder<CommandSource, ?> makeStoreSubcommand(LiteralCommandNode<CommandSource> p_198392_0_, LiteralArgumentBuilder<CommandSource> p_198392_1_, boolean p_198392_2_) {
      p_198392_1_.then(Commands.literal("score").then(Commands.argument("targets", ScoreHolderArgument.scoreHolders()).suggests(ScoreHolderArgument.SUGGEST_ENTITY_SELECTOR).then(Commands.argument("objective", ObjectiveArgument.objective()).redirect(p_198392_0_, (p_201468_1_) -> {
         return storeIntoScore((CommandSource)p_201468_1_.getSource(), ScoreHolderArgument.getScoreHolder(p_201468_1_, "targets"), ObjectiveArgument.getObjective(p_201468_1_, "objective"), p_198392_2_);
      }))));
      p_198392_1_.then(Commands.literal("bossbar").then(((RequiredArgumentBuilder)Commands.argument("id", ResourceLocationArgument.resourceLocation()).suggests(BossBarCommand.SUGGESTIONS_PROVIDER).then(Commands.literal("value").redirect(p_198392_0_, (p_201457_1_) -> {
         return storeIntoBossbar((CommandSource)p_201457_1_.getSource(), BossBarCommand.getBossbar(p_201457_1_), true, p_198392_2_);
      }))).then(Commands.literal("max").redirect(p_198392_0_, (p_229795_1_) -> {
         return storeIntoBossbar((CommandSource)p_229795_1_.getSource(), BossBarCommand.getBossbar(p_229795_1_), false, p_198392_2_);
      }))));
      Iterator var3 = DataCommand.field_218955_b.iterator();

      while(var3.hasNext()) {
         DataCommand.IDataProvider lvt_4_1_ = (DataCommand.IDataProvider)var3.next();
         lvt_4_1_.createArgument(p_198392_1_, (p_229765_3_) -> {
            return p_229765_3_.then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("path", NBTPathArgument.nbtPath()).then(Commands.literal("int").then(Commands.argument("scale", DoubleArgumentType.doubleArg()).redirect(p_198392_0_, (p_229801_2_) -> {
               return storeIntoNBT((CommandSource)p_229801_2_.getSource(), lvt_4_1_.createAccessor(p_229801_2_), NBTPathArgument.getNBTPath(p_229801_2_, "path"), (p_229800_1_) -> {
                  return IntNBT.func_229692_a_((int)((double)p_229800_1_ * DoubleArgumentType.getDouble(p_229801_2_, "scale")));
               }, p_198392_2_);
            })))).then(Commands.literal("float").then(Commands.argument("scale", DoubleArgumentType.doubleArg()).redirect(p_198392_0_, (p_229798_2_) -> {
               return storeIntoNBT((CommandSource)p_229798_2_.getSource(), lvt_4_1_.createAccessor(p_229798_2_), NBTPathArgument.getNBTPath(p_229798_2_, "path"), (p_229797_1_) -> {
                  return FloatNBT.func_229689_a_((float)((double)p_229797_1_ * DoubleArgumentType.getDouble(p_229798_2_, "scale")));
               }, p_198392_2_);
            })))).then(Commands.literal("short").then(Commands.argument("scale", DoubleArgumentType.doubleArg()).redirect(p_198392_0_, (p_229794_2_) -> {
               return storeIntoNBT((CommandSource)p_229794_2_.getSource(), lvt_4_1_.createAccessor(p_229794_2_), NBTPathArgument.getNBTPath(p_229794_2_, "path"), (p_229792_1_) -> {
                  return ShortNBT.func_229701_a_((short)((int)((double)p_229792_1_ * DoubleArgumentType.getDouble(p_229794_2_, "scale"))));
               }, p_198392_2_);
            })))).then(Commands.literal("long").then(Commands.argument("scale", DoubleArgumentType.doubleArg()).redirect(p_198392_0_, (p_229790_2_) -> {
               return storeIntoNBT((CommandSource)p_229790_2_.getSource(), lvt_4_1_.createAccessor(p_229790_2_), NBTPathArgument.getNBTPath(p_229790_2_, "path"), (p_229788_1_) -> {
                  return LongNBT.func_229698_a_((long)((double)p_229788_1_ * DoubleArgumentType.getDouble(p_229790_2_, "scale")));
               }, p_198392_2_);
            })))).then(Commands.literal("double").then(Commands.argument("scale", DoubleArgumentType.doubleArg()).redirect(p_198392_0_, (p_229784_2_) -> {
               return storeIntoNBT((CommandSource)p_229784_2_.getSource(), lvt_4_1_.createAccessor(p_229784_2_), NBTPathArgument.getNBTPath(p_229784_2_, "path"), (p_229781_1_) -> {
                  return DoubleNBT.func_229684_a_((double)p_229781_1_ * DoubleArgumentType.getDouble(p_229784_2_, "scale"));
               }, p_198392_2_);
            })))).then(Commands.literal("byte").then(Commands.argument("scale", DoubleArgumentType.doubleArg()).redirect(p_198392_0_, (p_229774_2_) -> {
               return storeIntoNBT((CommandSource)p_229774_2_.getSource(), lvt_4_1_.createAccessor(p_229774_2_), NBTPathArgument.getNBTPath(p_229774_2_, "path"), (p_229762_1_) -> {
                  return ByteNBT.func_229671_a_((byte)((int)((double)p_229762_1_ * DoubleArgumentType.getDouble(p_229774_2_, "scale"))));
               }, p_198392_2_);
            }))));
         });
      }

      return p_198392_1_;
   }

   private static CommandSource storeIntoScore(CommandSource p_209930_0_, Collection<String> p_209930_1_, ScoreObjective p_209930_2_, boolean p_209930_3_) {
      Scoreboard lvt_4_1_ = p_209930_0_.getServer().getScoreboard();
      return p_209930_0_.withResultConsumer((p_229769_4_, p_229769_5_, p_229769_6_) -> {
         Iterator var7 = p_209930_1_.iterator();

         while(var7.hasNext()) {
            String lvt_8_1_ = (String)var7.next();
            Score lvt_9_1_ = lvt_4_1_.getOrCreateScore(lvt_8_1_, p_209930_2_);
            int lvt_10_1_ = p_209930_3_ ? p_229769_6_ : (p_229769_5_ ? 1 : 0);
            lvt_9_1_.setScorePoints(lvt_10_1_);
         }

      }, COMBINE_ON_RESULT_COMPLETE);
   }

   private static CommandSource storeIntoBossbar(CommandSource p_209952_0_, CustomServerBossInfo p_209952_1_, boolean p_209952_2_, boolean p_209952_3_) {
      return p_209952_0_.withResultConsumer((p_229779_3_, p_229779_4_, p_229779_5_) -> {
         int lvt_6_1_ = p_209952_3_ ? p_229779_5_ : (p_229779_4_ ? 1 : 0);
         if (p_209952_2_) {
            p_209952_1_.setValue(lvt_6_1_);
         } else {
            p_209952_1_.setMax(lvt_6_1_);
         }

      }, COMBINE_ON_RESULT_COMPLETE);
   }

   private static CommandSource storeIntoNBT(CommandSource p_198397_0_, IDataAccessor p_198397_1_, NBTPathArgument.NBTPath p_198397_2_, IntFunction<INBT> p_198397_3_, boolean p_198397_4_) {
      return p_198397_0_.withResultConsumer((p_229772_4_, p_229772_5_, p_229772_6_) -> {
         try {
            CompoundNBT lvt_7_1_ = p_198397_1_.getData();
            int lvt_8_1_ = p_198397_4_ ? p_229772_6_ : (p_229772_5_ ? 1 : 0);
            p_198397_2_.func_218076_b(lvt_7_1_, () -> {
               return (INBT)p_198397_3_.apply(lvt_8_1_);
            });
            p_198397_1_.mergeData(lvt_7_1_);
         } catch (CommandSyntaxException var9) {
         }

      }, COMBINE_ON_RESULT_COMPLETE);
   }

   private static ArgumentBuilder<CommandSource, ?> makeIfCommand(CommandNode<CommandSource> p_198394_0_, LiteralArgumentBuilder<CommandSource> p_198394_1_, boolean p_198394_2_) {
      ((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)p_198394_1_.then(Commands.literal("block").then(Commands.argument("pos", BlockPosArgument.blockPos()).then(buildIfResult(p_198394_0_, Commands.argument("block", BlockPredicateArgument.blockPredicate()), p_198394_2_, (p_210438_0_) -> {
         return BlockPredicateArgument.getBlockPredicate(p_210438_0_, "block").test(new CachedBlockInfo(((CommandSource)p_210438_0_.getSource()).getWorld(), BlockPosArgument.getLoadedBlockPos(p_210438_0_, "pos"), true));
      }))))).then(Commands.literal("score").then(Commands.argument("target", ScoreHolderArgument.scoreHolder()).suggests(ScoreHolderArgument.SUGGEST_ENTITY_SELECTOR).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("targetObjective", ObjectiveArgument.objective()).then(Commands.literal("=").then(Commands.argument("source", ScoreHolderArgument.scoreHolder()).suggests(ScoreHolderArgument.SUGGEST_ENTITY_SELECTOR).then(buildIfResult(p_198394_0_, Commands.argument("sourceObjective", ObjectiveArgument.objective()), p_198394_2_, (p_229803_0_) -> {
         return compareScores(p_229803_0_, Integer::equals);
      }))))).then(Commands.literal("<").then(Commands.argument("source", ScoreHolderArgument.scoreHolder()).suggests(ScoreHolderArgument.SUGGEST_ENTITY_SELECTOR).then(buildIfResult(p_198394_0_, Commands.argument("sourceObjective", ObjectiveArgument.objective()), p_198394_2_, (p_229802_0_) -> {
         return compareScores(p_229802_0_, (p_229793_0_, p_229793_1_) -> {
            return p_229793_0_ < p_229793_1_;
         });
      }))))).then(Commands.literal("<=").then(Commands.argument("source", ScoreHolderArgument.scoreHolder()).suggests(ScoreHolderArgument.SUGGEST_ENTITY_SELECTOR).then(buildIfResult(p_198394_0_, Commands.argument("sourceObjective", ObjectiveArgument.objective()), p_198394_2_, (p_229799_0_) -> {
         return compareScores(p_229799_0_, (p_229789_0_, p_229789_1_) -> {
            return p_229789_0_ <= p_229789_1_;
         });
      }))))).then(Commands.literal(">").then(Commands.argument("source", ScoreHolderArgument.scoreHolder()).suggests(ScoreHolderArgument.SUGGEST_ENTITY_SELECTOR).then(buildIfResult(p_198394_0_, Commands.argument("sourceObjective", ObjectiveArgument.objective()), p_198394_2_, (p_229796_0_) -> {
         return compareScores(p_229796_0_, (p_229782_0_, p_229782_1_) -> {
            return p_229782_0_ > p_229782_1_;
         });
      }))))).then(Commands.literal(">=").then(Commands.argument("source", ScoreHolderArgument.scoreHolder()).suggests(ScoreHolderArgument.SUGGEST_ENTITY_SELECTOR).then(buildIfResult(p_198394_0_, Commands.argument("sourceObjective", ObjectiveArgument.objective()), p_198394_2_, (p_201088_0_) -> {
         return compareScores(p_201088_0_, (p_229768_0_, p_229768_1_) -> {
            return p_229768_0_ >= p_229768_1_;
         });
      }))))).then(Commands.literal("matches").then(buildIfResult(p_198394_0_, Commands.argument("range", IRangeArgument.intRange()), p_198394_2_, (p_229787_0_) -> {
         return checkScore(p_229787_0_, IRangeArgument.IntRange.getIntRange(p_229787_0_, "range"));
      }))))))).then(Commands.literal("blocks").then(Commands.argument("start", BlockPosArgument.blockPos()).then(Commands.argument("end", BlockPosArgument.blockPos()).then(((RequiredArgumentBuilder)Commands.argument("destination", BlockPosArgument.blockPos()).then(buildIfBlocks(p_198394_0_, Commands.literal("all"), p_198394_2_, false))).then(buildIfBlocks(p_198394_0_, Commands.literal("masked"), p_198394_2_, true))))))).then(Commands.literal("entity").then(((RequiredArgumentBuilder)Commands.argument("entities", EntityArgument.entities()).fork(p_198394_0_, (p_229791_1_) -> {
         return checkIfMatches(p_229791_1_, p_198394_2_, !EntityArgument.getEntitiesAllowingNone(p_229791_1_, "entities").isEmpty());
      })).executes(func_218834_a(p_198394_2_, (p_229780_0_) -> {
         return EntityArgument.getEntitiesAllowingNone(p_229780_0_, "entities").size();
      }))))).then(Commands.literal("predicate").then(buildIfResult(p_198394_0_, Commands.argument("predicate", ResourceLocationArgument.resourceLocation()).suggests(field_229760_e_), p_198394_2_, (p_229761_0_) -> {
         return func_229767_a_((CommandSource)p_229761_0_.getSource(), ResourceLocationArgument.func_228259_c_(p_229761_0_, "predicate"));
      })));
      Iterator var3 = DataCommand.field_218956_c.iterator();

      while(var3.hasNext()) {
         DataCommand.IDataProvider lvt_4_1_ = (DataCommand.IDataProvider)var3.next();
         p_198394_1_.then(lvt_4_1_.createArgument(Commands.literal("data"), (p_229764_3_) -> {
            return p_229764_3_.then(((RequiredArgumentBuilder)Commands.argument("path", NBTPathArgument.nbtPath()).fork(p_198394_0_, (p_229777_2_) -> {
               return checkIfMatches(p_229777_2_, p_198394_2_, func_218831_a(lvt_4_1_.createAccessor(p_229777_2_), NBTPathArgument.getNBTPath(p_229777_2_, "path")) > 0);
            })).executes(func_218834_a(p_198394_2_, (p_229773_1_) -> {
               return func_218831_a(lvt_4_1_.createAccessor(p_229773_1_), NBTPathArgument.getNBTPath(p_229773_1_, "path"));
            })));
         }));
      }

      return p_198394_1_;
   }

   private static Command<CommandSource> func_218834_a(boolean p_218834_0_, ExecuteCommand.INumericTest p_218834_1_) {
      return p_218834_0_ ? (p_229783_1_) -> {
         int lvt_2_1_ = p_218834_1_.test(p_229783_1_);
         if (lvt_2_1_ > 0) {
            ((CommandSource)p_229783_1_.getSource()).sendFeedback(new TranslationTextComponent("commands.execute.conditional.pass_count", new Object[]{lvt_2_1_}), false);
            return lvt_2_1_;
         } else {
            throw TEST_FAILED.create();
         }
      } : (p_229771_1_) -> {
         int lvt_2_1_ = p_218834_1_.test(p_229771_1_);
         if (lvt_2_1_ == 0) {
            ((CommandSource)p_229771_1_.getSource()).sendFeedback(new TranslationTextComponent("commands.execute.conditional.pass", new Object[0]), false);
            return 1;
         } else {
            throw TEST_FAILED_COUNT.create(lvt_2_1_);
         }
      };
   }

   private static int func_218831_a(IDataAccessor p_218831_0_, NBTPathArgument.NBTPath p_218831_1_) throws CommandSyntaxException {
      return p_218831_1_.func_218069_b(p_218831_0_.getData());
   }

   private static boolean compareScores(CommandContext<CommandSource> p_198371_0_, BiPredicate<Integer, Integer> p_198371_1_) throws CommandSyntaxException {
      String lvt_2_1_ = ScoreHolderArgument.getSingleScoreHolderNoObjectives(p_198371_0_, "target");
      ScoreObjective lvt_3_1_ = ObjectiveArgument.getObjective(p_198371_0_, "targetObjective");
      String lvt_4_1_ = ScoreHolderArgument.getSingleScoreHolderNoObjectives(p_198371_0_, "source");
      ScoreObjective lvt_5_1_ = ObjectiveArgument.getObjective(p_198371_0_, "sourceObjective");
      Scoreboard lvt_6_1_ = ((CommandSource)p_198371_0_.getSource()).getServer().getScoreboard();
      if (lvt_6_1_.entityHasObjective(lvt_2_1_, lvt_3_1_) && lvt_6_1_.entityHasObjective(lvt_4_1_, lvt_5_1_)) {
         Score lvt_7_1_ = lvt_6_1_.getOrCreateScore(lvt_2_1_, lvt_3_1_);
         Score lvt_8_1_ = lvt_6_1_.getOrCreateScore(lvt_4_1_, lvt_5_1_);
         return p_198371_1_.test(lvt_7_1_.getScorePoints(), lvt_8_1_.getScorePoints());
      } else {
         return false;
      }
   }

   private static boolean checkScore(CommandContext<CommandSource> p_201115_0_, MinMaxBounds.IntBound p_201115_1_) throws CommandSyntaxException {
      String lvt_2_1_ = ScoreHolderArgument.getSingleScoreHolderNoObjectives(p_201115_0_, "target");
      ScoreObjective lvt_3_1_ = ObjectiveArgument.getObjective(p_201115_0_, "targetObjective");
      Scoreboard lvt_4_1_ = ((CommandSource)p_201115_0_.getSource()).getServer().getScoreboard();
      return !lvt_4_1_.entityHasObjective(lvt_2_1_, lvt_3_1_) ? false : p_201115_1_.test(lvt_4_1_.getOrCreateScore(lvt_2_1_, lvt_3_1_).getScorePoints());
   }

   private static boolean func_229767_a_(CommandSource p_229767_0_, ILootCondition p_229767_1_) {
      ServerWorld lvt_2_1_ = p_229767_0_.getWorld();
      LootContext.Builder lvt_3_1_ = (new LootContext.Builder(lvt_2_1_)).withParameter(LootParameters.POSITION, new BlockPos(p_229767_0_.getPos())).withNullableParameter(LootParameters.THIS_ENTITY, p_229767_0_.getEntity());
      return p_229767_1_.test(lvt_3_1_.build(LootParameterSets.field_227557_c_));
   }

   private static Collection<CommandSource> checkIfMatches(CommandContext<CommandSource> p_198411_0_, boolean p_198411_1_, boolean p_198411_2_) {
      return (Collection)(p_198411_2_ == p_198411_1_ ? Collections.singleton(p_198411_0_.getSource()) : Collections.emptyList());
   }

   private static ArgumentBuilder<CommandSource, ?> buildIfResult(CommandNode<CommandSource> p_210415_0_, ArgumentBuilder<CommandSource, ?> p_210415_1_, boolean p_210415_2_, ExecuteCommand.IBooleanTest p_210415_3_) {
      return p_210415_1_.fork(p_210415_0_, (p_229786_2_) -> {
         return checkIfMatches(p_229786_2_, p_210415_2_, p_210415_3_.test(p_229786_2_));
      }).executes((p_229776_2_) -> {
         if (p_210415_2_ == p_210415_3_.test(p_229776_2_)) {
            ((CommandSource)p_229776_2_.getSource()).sendFeedback(new TranslationTextComponent("commands.execute.conditional.pass", new Object[0]), false);
            return 1;
         } else {
            throw TEST_FAILED.create();
         }
      });
   }

   private static ArgumentBuilder<CommandSource, ?> buildIfBlocks(CommandNode<CommandSource> p_212178_0_, ArgumentBuilder<CommandSource, ?> p_212178_1_, boolean p_212178_2_, boolean p_212178_3_) {
      return p_212178_1_.fork(p_212178_0_, (p_229778_2_) -> {
         return checkIfMatches(p_229778_2_, p_212178_2_, countMatchingBlocks(p_229778_2_, p_212178_3_).isPresent());
      }).executes(p_212178_2_ ? (p_229785_1_) -> {
         return checkBlockCountIf(p_229785_1_, p_212178_3_);
      } : (p_229775_1_) -> {
         return checkBlockCountUnless(p_229775_1_, p_212178_3_);
      });
   }

   private static int checkBlockCountIf(CommandContext<CommandSource> p_212175_0_, boolean p_212175_1_) throws CommandSyntaxException {
      OptionalInt lvt_2_1_ = countMatchingBlocks(p_212175_0_, p_212175_1_);
      if (lvt_2_1_.isPresent()) {
         ((CommandSource)p_212175_0_.getSource()).sendFeedback(new TranslationTextComponent("commands.execute.conditional.pass_count", new Object[]{lvt_2_1_.getAsInt()}), false);
         return lvt_2_1_.getAsInt();
      } else {
         throw TEST_FAILED.create();
      }
   }

   private static int checkBlockCountUnless(CommandContext<CommandSource> p_212173_0_, boolean p_212173_1_) throws CommandSyntaxException {
      OptionalInt lvt_2_1_ = countMatchingBlocks(p_212173_0_, p_212173_1_);
      if (lvt_2_1_.isPresent()) {
         throw TEST_FAILED_COUNT.create(lvt_2_1_.getAsInt());
      } else {
         ((CommandSource)p_212173_0_.getSource()).sendFeedback(new TranslationTextComponent("commands.execute.conditional.pass", new Object[0]), false);
         return 1;
      }
   }

   private static OptionalInt countMatchingBlocks(CommandContext<CommandSource> p_212169_0_, boolean p_212169_1_) throws CommandSyntaxException {
      return countMatchingBlocks(((CommandSource)p_212169_0_.getSource()).getWorld(), BlockPosArgument.getLoadedBlockPos(p_212169_0_, "start"), BlockPosArgument.getLoadedBlockPos(p_212169_0_, "end"), BlockPosArgument.getLoadedBlockPos(p_212169_0_, "destination"), p_212169_1_);
   }

   private static OptionalInt countMatchingBlocks(ServerWorld p_198395_0_, BlockPos p_198395_1_, BlockPos p_198395_2_, BlockPos p_198395_3_, boolean p_198395_4_) throws CommandSyntaxException {
      MutableBoundingBox lvt_5_1_ = new MutableBoundingBox(p_198395_1_, p_198395_2_);
      MutableBoundingBox lvt_6_1_ = new MutableBoundingBox(p_198395_3_, p_198395_3_.add(lvt_5_1_.getLength()));
      BlockPos lvt_7_1_ = new BlockPos(lvt_6_1_.minX - lvt_5_1_.minX, lvt_6_1_.minY - lvt_5_1_.minY, lvt_6_1_.minZ - lvt_5_1_.minZ);
      int lvt_8_1_ = lvt_5_1_.getXSize() * lvt_5_1_.getYSize() * lvt_5_1_.getZSize();
      if (lvt_8_1_ > 32768) {
         throw TOO_MANY_BLOCKS.create(32768, lvt_8_1_);
      } else {
         int lvt_9_1_ = 0;

         for(int lvt_10_1_ = lvt_5_1_.minZ; lvt_10_1_ <= lvt_5_1_.maxZ; ++lvt_10_1_) {
            for(int lvt_11_1_ = lvt_5_1_.minY; lvt_11_1_ <= lvt_5_1_.maxY; ++lvt_11_1_) {
               for(int lvt_12_1_ = lvt_5_1_.minX; lvt_12_1_ <= lvt_5_1_.maxX; ++lvt_12_1_) {
                  BlockPos lvt_13_1_ = new BlockPos(lvt_12_1_, lvt_11_1_, lvt_10_1_);
                  BlockPos lvt_14_1_ = lvt_13_1_.add(lvt_7_1_);
                  BlockState lvt_15_1_ = p_198395_0_.getBlockState(lvt_13_1_);
                  if (!p_198395_4_ || lvt_15_1_.getBlock() != Blocks.AIR) {
                     if (lvt_15_1_ != p_198395_0_.getBlockState(lvt_14_1_)) {
                        return OptionalInt.empty();
                     }

                     TileEntity lvt_16_1_ = p_198395_0_.getTileEntity(lvt_13_1_);
                     TileEntity lvt_17_1_ = p_198395_0_.getTileEntity(lvt_14_1_);
                     if (lvt_16_1_ != null) {
                        if (lvt_17_1_ == null) {
                           return OptionalInt.empty();
                        }

                        CompoundNBT lvt_18_1_ = lvt_16_1_.write(new CompoundNBT());
                        lvt_18_1_.remove("x");
                        lvt_18_1_.remove("y");
                        lvt_18_1_.remove("z");
                        CompoundNBT lvt_19_1_ = lvt_17_1_.write(new CompoundNBT());
                        lvt_19_1_.remove("x");
                        lvt_19_1_.remove("y");
                        lvt_19_1_.remove("z");
                        if (!lvt_18_1_.equals(lvt_19_1_)) {
                           return OptionalInt.empty();
                        }
                     }

                     ++lvt_9_1_;
                  }
               }
            }
         }

         return OptionalInt.of(lvt_9_1_);
      }
   }

   @FunctionalInterface
   interface INumericTest {
      int test(CommandContext<CommandSource> var1) throws CommandSyntaxException;
   }

   @FunctionalInterface
   interface IBooleanTest {
      boolean test(CommandContext<CommandSource> var1) throws CommandSyntaxException;
   }
}
