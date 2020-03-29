package net.minecraft.command.impl;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic4CommandExceptionType;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.Vec2Argument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.server.ServerWorld;

public class SpreadPlayersCommand {
   private static final Dynamic4CommandExceptionType SPREAD_TEAMS_FAILED = new Dynamic4CommandExceptionType((p_208910_0_, p_208910_1_, p_208910_2_, p_208910_3_) -> {
      return new TranslationTextComponent("commands.spreadplayers.failed.teams", new Object[]{p_208910_0_, p_208910_1_, p_208910_2_, p_208910_3_});
   });
   private static final Dynamic4CommandExceptionType SPREAD_ENTITIES_FAILED = new Dynamic4CommandExceptionType((p_208912_0_, p_208912_1_, p_208912_2_, p_208912_3_) -> {
      return new TranslationTextComponent("commands.spreadplayers.failed.entities", new Object[]{p_208912_0_, p_208912_1_, p_208912_2_, p_208912_3_});
   });

   public static void register(CommandDispatcher<CommandSource> p_198716_0_) {
      p_198716_0_.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("spreadplayers").requires((p_198721_0_) -> {
         return p_198721_0_.hasPermissionLevel(2);
      })).then(Commands.argument("center", Vec2Argument.vec2()).then(Commands.argument("spreadDistance", FloatArgumentType.floatArg(0.0F)).then(Commands.argument("maxRange", FloatArgumentType.floatArg(1.0F)).then(Commands.argument("respectTeams", BoolArgumentType.bool()).then(Commands.argument("targets", EntityArgument.entities()).executes((p_198718_0_) -> {
         return spreadPlayers((CommandSource)p_198718_0_.getSource(), Vec2Argument.getVec2f(p_198718_0_, "center"), FloatArgumentType.getFloat(p_198718_0_, "spreadDistance"), FloatArgumentType.getFloat(p_198718_0_, "maxRange"), BoolArgumentType.getBool(p_198718_0_, "respectTeams"), EntityArgument.getEntities(p_198718_0_, "targets"));
      })))))));
   }

   private static int spreadPlayers(CommandSource p_198722_0_, Vec2f p_198722_1_, float p_198722_2_, float p_198722_3_, boolean p_198722_4_, Collection<? extends Entity> p_198722_5_) throws CommandSyntaxException {
      Random lvt_6_1_ = new Random();
      double lvt_7_1_ = (double)(p_198722_1_.x - p_198722_3_);
      double lvt_9_1_ = (double)(p_198722_1_.y - p_198722_3_);
      double lvt_11_1_ = (double)(p_198722_1_.x + p_198722_3_);
      double lvt_13_1_ = (double)(p_198722_1_.y + p_198722_3_);
      SpreadPlayersCommand.Position[] lvt_15_1_ = getPositions(lvt_6_1_, p_198722_4_ ? getNumberOfTeams(p_198722_5_) : p_198722_5_.size(), lvt_7_1_, lvt_9_1_, lvt_11_1_, lvt_13_1_);
      ensureSufficientSeparation(p_198722_1_, (double)p_198722_2_, p_198722_0_.getWorld(), lvt_6_1_, lvt_7_1_, lvt_9_1_, lvt_11_1_, lvt_13_1_, lvt_15_1_, p_198722_4_);
      double lvt_16_1_ = doSpreading(p_198722_5_, p_198722_0_.getWorld(), lvt_15_1_, p_198722_4_);
      p_198722_0_.sendFeedback(new TranslationTextComponent("commands.spreadplayers.success." + (p_198722_4_ ? "teams" : "entities"), new Object[]{lvt_15_1_.length, p_198722_1_.x, p_198722_1_.y, String.format(Locale.ROOT, "%.2f", lvt_16_1_)}), true);
      return lvt_15_1_.length;
   }

   private static int getNumberOfTeams(Collection<? extends Entity> p_198715_0_) {
      Set<Team> lvt_1_1_ = Sets.newHashSet();
      Iterator var2 = p_198715_0_.iterator();

      while(var2.hasNext()) {
         Entity lvt_3_1_ = (Entity)var2.next();
         if (lvt_3_1_ instanceof PlayerEntity) {
            lvt_1_1_.add(lvt_3_1_.getTeam());
         } else {
            lvt_1_1_.add((Object)null);
         }
      }

      return lvt_1_1_.size();
   }

   private static void ensureSufficientSeparation(Vec2f p_198717_0_, double p_198717_1_, ServerWorld p_198717_3_, Random p_198717_4_, double p_198717_5_, double p_198717_7_, double p_198717_9_, double p_198717_11_, SpreadPlayersCommand.Position[] p_198717_13_, boolean p_198717_14_) throws CommandSyntaxException {
      boolean lvt_15_1_ = true;
      double lvt_17_1_ = 3.4028234663852886E38D;

      int lvt_16_1_;
      for(lvt_16_1_ = 0; lvt_16_1_ < 10000 && lvt_15_1_; ++lvt_16_1_) {
         lvt_15_1_ = false;
         lvt_17_1_ = 3.4028234663852886E38D;

         int lvt_21_1_;
         SpreadPlayersCommand.Position lvt_22_2_;
         for(int lvt_19_1_ = 0; lvt_19_1_ < p_198717_13_.length; ++lvt_19_1_) {
            SpreadPlayersCommand.Position lvt_20_1_ = p_198717_13_[lvt_19_1_];
            lvt_21_1_ = 0;
            lvt_22_2_ = new SpreadPlayersCommand.Position();

            for(int lvt_23_1_ = 0; lvt_23_1_ < p_198717_13_.length; ++lvt_23_1_) {
               if (lvt_19_1_ != lvt_23_1_) {
                  SpreadPlayersCommand.Position lvt_24_1_ = p_198717_13_[lvt_23_1_];
                  double lvt_25_1_ = lvt_20_1_.getDistance(lvt_24_1_);
                  lvt_17_1_ = Math.min(lvt_25_1_, lvt_17_1_);
                  if (lvt_25_1_ < p_198717_1_) {
                     ++lvt_21_1_;
                     lvt_22_2_.x = lvt_22_2_.x + (lvt_24_1_.x - lvt_20_1_.x);
                     lvt_22_2_.z = lvt_22_2_.z + (lvt_24_1_.z - lvt_20_1_.z);
                  }
               }
            }

            if (lvt_21_1_ > 0) {
               lvt_22_2_.x = lvt_22_2_.x / (double)lvt_21_1_;
               lvt_22_2_.z = lvt_22_2_.z / (double)lvt_21_1_;
               double lvt_23_2_ = (double)lvt_22_2_.getMagnitude();
               if (lvt_23_2_ > 0.0D) {
                  lvt_22_2_.normalize();
                  lvt_20_1_.subtract(lvt_22_2_);
               } else {
                  lvt_20_1_.computeCoords(p_198717_4_, p_198717_5_, p_198717_7_, p_198717_9_, p_198717_11_);
               }

               lvt_15_1_ = true;
            }

            if (lvt_20_1_.clampWithinRange(p_198717_5_, p_198717_7_, p_198717_9_, p_198717_11_)) {
               lvt_15_1_ = true;
            }
         }

         if (!lvt_15_1_) {
            SpreadPlayersCommand.Position[] var27 = p_198717_13_;
            int var28 = p_198717_13_.length;

            for(lvt_21_1_ = 0; lvt_21_1_ < var28; ++lvt_21_1_) {
               lvt_22_2_ = var27[lvt_21_1_];
               if (!lvt_22_2_.isLocationSafe(p_198717_3_)) {
                  lvt_22_2_.computeCoords(p_198717_4_, p_198717_5_, p_198717_7_, p_198717_9_, p_198717_11_);
                  lvt_15_1_ = true;
               }
            }
         }
      }

      if (lvt_17_1_ == 3.4028234663852886E38D) {
         lvt_17_1_ = 0.0D;
      }

      if (lvt_16_1_ >= 10000) {
         if (p_198717_14_) {
            throw SPREAD_TEAMS_FAILED.create(p_198717_13_.length, p_198717_0_.x, p_198717_0_.y, String.format(Locale.ROOT, "%.2f", lvt_17_1_));
         } else {
            throw SPREAD_ENTITIES_FAILED.create(p_198717_13_.length, p_198717_0_.x, p_198717_0_.y, String.format(Locale.ROOT, "%.2f", lvt_17_1_));
         }
      }
   }

   private static double doSpreading(Collection<? extends Entity> p_198719_0_, ServerWorld p_198719_1_, SpreadPlayersCommand.Position[] p_198719_2_, boolean p_198719_3_) {
      double lvt_4_1_ = 0.0D;
      int lvt_6_1_ = 0;
      Map<Team, SpreadPlayersCommand.Position> lvt_7_1_ = Maps.newHashMap();

      double lvt_11_2_;
      for(Iterator var8 = p_198719_0_.iterator(); var8.hasNext(); lvt_4_1_ += lvt_11_2_) {
         Entity lvt_9_1_ = (Entity)var8.next();
         SpreadPlayersCommand.Position lvt_10_2_;
         if (p_198719_3_) {
            Team lvt_11_1_ = lvt_9_1_ instanceof PlayerEntity ? lvt_9_1_.getTeam() : null;
            if (!lvt_7_1_.containsKey(lvt_11_1_)) {
               lvt_7_1_.put(lvt_11_1_, p_198719_2_[lvt_6_1_++]);
            }

            lvt_10_2_ = (SpreadPlayersCommand.Position)lvt_7_1_.get(lvt_11_1_);
         } else {
            lvt_10_2_ = p_198719_2_[lvt_6_1_++];
         }

         lvt_9_1_.teleportKeepLoaded((double)((float)MathHelper.floor(lvt_10_2_.x) + 0.5F), (double)lvt_10_2_.getHighestNonAirBlock(p_198719_1_), (double)MathHelper.floor(lvt_10_2_.z) + 0.5D);
         lvt_11_2_ = Double.MAX_VALUE;
         SpreadPlayersCommand.Position[] var13 = p_198719_2_;
         int var14 = p_198719_2_.length;

         for(int var15 = 0; var15 < var14; ++var15) {
            SpreadPlayersCommand.Position lvt_16_1_ = var13[var15];
            if (lvt_10_2_ != lvt_16_1_) {
               double lvt_17_1_ = lvt_10_2_.getDistance(lvt_16_1_);
               lvt_11_2_ = Math.min(lvt_17_1_, lvt_11_2_);
            }
         }
      }

      if (p_198719_0_.size() < 2) {
         return 0.0D;
      } else {
         lvt_4_1_ /= (double)p_198719_0_.size();
         return lvt_4_1_;
      }
   }

   private static SpreadPlayersCommand.Position[] getPositions(Random p_198720_0_, int p_198720_1_, double p_198720_2_, double p_198720_4_, double p_198720_6_, double p_198720_8_) {
      SpreadPlayersCommand.Position[] lvt_10_1_ = new SpreadPlayersCommand.Position[p_198720_1_];

      for(int lvt_11_1_ = 0; lvt_11_1_ < lvt_10_1_.length; ++lvt_11_1_) {
         SpreadPlayersCommand.Position lvt_12_1_ = new SpreadPlayersCommand.Position();
         lvt_12_1_.computeCoords(p_198720_0_, p_198720_2_, p_198720_4_, p_198720_6_, p_198720_8_);
         lvt_10_1_[lvt_11_1_] = lvt_12_1_;
      }

      return lvt_10_1_;
   }

   static class Position {
      private double x;
      private double z;

      double getDistance(SpreadPlayersCommand.Position p_198708_1_) {
         double lvt_2_1_ = this.x - p_198708_1_.x;
         double lvt_4_1_ = this.z - p_198708_1_.z;
         return Math.sqrt(lvt_2_1_ * lvt_2_1_ + lvt_4_1_ * lvt_4_1_);
      }

      void normalize() {
         double lvt_1_1_ = (double)this.getMagnitude();
         this.x /= lvt_1_1_;
         this.z /= lvt_1_1_;
      }

      float getMagnitude() {
         return MathHelper.sqrt(this.x * this.x + this.z * this.z);
      }

      public void subtract(SpreadPlayersCommand.Position p_198705_1_) {
         this.x -= p_198705_1_.x;
         this.z -= p_198705_1_.z;
      }

      public boolean clampWithinRange(double p_198709_1_, double p_198709_3_, double p_198709_5_, double p_198709_7_) {
         boolean lvt_9_1_ = false;
         if (this.x < p_198709_1_) {
            this.x = p_198709_1_;
            lvt_9_1_ = true;
         } else if (this.x > p_198709_5_) {
            this.x = p_198709_5_;
            lvt_9_1_ = true;
         }

         if (this.z < p_198709_3_) {
            this.z = p_198709_3_;
            lvt_9_1_ = true;
         } else if (this.z > p_198709_7_) {
            this.z = p_198709_7_;
            lvt_9_1_ = true;
         }

         return lvt_9_1_;
      }

      public int getHighestNonAirBlock(IBlockReader p_198710_1_) {
         BlockPos lvt_2_1_ = new BlockPos(this.x, 256.0D, this.z);

         do {
            if (lvt_2_1_.getY() <= 0) {
               return 257;
            }

            lvt_2_1_ = lvt_2_1_.down();
         } while(p_198710_1_.getBlockState(lvt_2_1_).isAir());

         return lvt_2_1_.getY() + 1;
      }

      public boolean isLocationSafe(IBlockReader p_198706_1_) {
         BlockPos lvt_2_1_ = new BlockPos(this.x, 256.0D, this.z);

         BlockState lvt_3_1_;
         do {
            if (lvt_2_1_.getY() <= 0) {
               return false;
            }

            lvt_2_1_ = lvt_2_1_.down();
            lvt_3_1_ = p_198706_1_.getBlockState(lvt_2_1_);
         } while(lvt_3_1_.isAir());

         Material lvt_4_1_ = lvt_3_1_.getMaterial();
         return !lvt_4_1_.isLiquid() && lvt_4_1_ != Material.FIRE;
      }

      public void computeCoords(Random p_198711_1_, double p_198711_2_, double p_198711_4_, double p_198711_6_, double p_198711_8_) {
         this.x = MathHelper.nextDouble(p_198711_1_, p_198711_2_, p_198711_6_);
         this.z = MathHelper.nextDouble(p_198711_1_, p_198711_4_, p_198711_8_);
      }
   }
}
