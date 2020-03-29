package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import java.util.Iterator;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.ResourceLocationArgument;
import net.minecraft.command.arguments.SuggestionProviders;
import net.minecraft.command.arguments.Vec3Argument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.server.SPlaySoundPacket;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TranslationTextComponent;

public class PlaySoundCommand {
   private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslationTextComponent("commands.playsound.failed", new Object[0]));

   public static void register(CommandDispatcher<CommandSource> p_198572_0_) {
      RequiredArgumentBuilder<CommandSource, ResourceLocation> lvt_1_1_ = Commands.argument("sound", ResourceLocationArgument.resourceLocation()).suggests(SuggestionProviders.AVAILABLE_SOUNDS);
      SoundCategory[] var2 = SoundCategory.values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         SoundCategory lvt_5_1_ = var2[var4];
         lvt_1_1_.then(buildCategorySubcommand(lvt_5_1_));
      }

      p_198572_0_.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("playsound").requires((p_198576_0_) -> {
         return p_198576_0_.hasPermissionLevel(2);
      })).then(lvt_1_1_));
   }

   private static LiteralArgumentBuilder<CommandSource> buildCategorySubcommand(SoundCategory p_198577_0_) {
      return (LiteralArgumentBuilder)Commands.literal(p_198577_0_.getName()).then(((RequiredArgumentBuilder)Commands.argument("targets", EntityArgument.players()).executes((p_198575_1_) -> {
         return playSound((CommandSource)p_198575_1_.getSource(), EntityArgument.getPlayers(p_198575_1_, "targets"), ResourceLocationArgument.getResourceLocation(p_198575_1_, "sound"), p_198577_0_, ((CommandSource)p_198575_1_.getSource()).getPos(), 1.0F, 1.0F, 0.0F);
      })).then(((RequiredArgumentBuilder)Commands.argument("pos", Vec3Argument.vec3()).executes((p_198578_1_) -> {
         return playSound((CommandSource)p_198578_1_.getSource(), EntityArgument.getPlayers(p_198578_1_, "targets"), ResourceLocationArgument.getResourceLocation(p_198578_1_, "sound"), p_198577_0_, Vec3Argument.getVec3(p_198578_1_, "pos"), 1.0F, 1.0F, 0.0F);
      })).then(((RequiredArgumentBuilder)Commands.argument("volume", FloatArgumentType.floatArg(0.0F)).executes((p_198571_1_) -> {
         return playSound((CommandSource)p_198571_1_.getSource(), EntityArgument.getPlayers(p_198571_1_, "targets"), ResourceLocationArgument.getResourceLocation(p_198571_1_, "sound"), p_198577_0_, Vec3Argument.getVec3(p_198571_1_, "pos"), (Float)p_198571_1_.getArgument("volume", Float.class), 1.0F, 0.0F);
      })).then(((RequiredArgumentBuilder)Commands.argument("pitch", FloatArgumentType.floatArg(0.0F, 2.0F)).executes((p_198574_1_) -> {
         return playSound((CommandSource)p_198574_1_.getSource(), EntityArgument.getPlayers(p_198574_1_, "targets"), ResourceLocationArgument.getResourceLocation(p_198574_1_, "sound"), p_198577_0_, Vec3Argument.getVec3(p_198574_1_, "pos"), (Float)p_198574_1_.getArgument("volume", Float.class), (Float)p_198574_1_.getArgument("pitch", Float.class), 0.0F);
      })).then(Commands.argument("minVolume", FloatArgumentType.floatArg(0.0F, 1.0F)).executes((p_198570_1_) -> {
         return playSound((CommandSource)p_198570_1_.getSource(), EntityArgument.getPlayers(p_198570_1_, "targets"), ResourceLocationArgument.getResourceLocation(p_198570_1_, "sound"), p_198577_0_, Vec3Argument.getVec3(p_198570_1_, "pos"), (Float)p_198570_1_.getArgument("volume", Float.class), (Float)p_198570_1_.getArgument("pitch", Float.class), (Float)p_198570_1_.getArgument("minVolume", Float.class));
      }))))));
   }

   private static int playSound(CommandSource p_198573_0_, Collection<ServerPlayerEntity> p_198573_1_, ResourceLocation p_198573_2_, SoundCategory p_198573_3_, Vec3d p_198573_4_, float p_198573_5_, float p_198573_6_, float p_198573_7_) throws CommandSyntaxException {
      double lvt_8_1_ = Math.pow(p_198573_5_ > 1.0F ? (double)(p_198573_5_ * 16.0F) : 16.0D, 2.0D);
      int lvt_10_1_ = 0;
      Iterator var11 = p_198573_1_.iterator();

      while(true) {
         ServerPlayerEntity lvt_12_1_;
         Vec3d lvt_21_1_;
         float lvt_22_1_;
         while(true) {
            if (!var11.hasNext()) {
               if (lvt_10_1_ == 0) {
                  throw FAILED_EXCEPTION.create();
               }

               if (p_198573_1_.size() == 1) {
                  p_198573_0_.sendFeedback(new TranslationTextComponent("commands.playsound.success.single", new Object[]{p_198573_2_, ((ServerPlayerEntity)p_198573_1_.iterator().next()).getDisplayName()}), true);
               } else {
                  p_198573_0_.sendFeedback(new TranslationTextComponent("commands.playsound.success.multiple", new Object[]{p_198573_2_, p_198573_1_.size()}), true);
               }

               return lvt_10_1_;
            }

            lvt_12_1_ = (ServerPlayerEntity)var11.next();
            double lvt_13_1_ = p_198573_4_.x - lvt_12_1_.func_226277_ct_();
            double lvt_15_1_ = p_198573_4_.y - lvt_12_1_.func_226278_cu_();
            double lvt_17_1_ = p_198573_4_.z - lvt_12_1_.func_226281_cx_();
            double lvt_19_1_ = lvt_13_1_ * lvt_13_1_ + lvt_15_1_ * lvt_15_1_ + lvt_17_1_ * lvt_17_1_;
            lvt_21_1_ = p_198573_4_;
            lvt_22_1_ = p_198573_5_;
            if (lvt_19_1_ <= lvt_8_1_) {
               break;
            }

            if (p_198573_7_ > 0.0F) {
               double lvt_23_1_ = (double)MathHelper.sqrt(lvt_19_1_);
               lvt_21_1_ = new Vec3d(lvt_12_1_.func_226277_ct_() + lvt_13_1_ / lvt_23_1_ * 2.0D, lvt_12_1_.func_226278_cu_() + lvt_15_1_ / lvt_23_1_ * 2.0D, lvt_12_1_.func_226281_cx_() + lvt_17_1_ / lvt_23_1_ * 2.0D);
               lvt_22_1_ = p_198573_7_;
               break;
            }
         }

         lvt_12_1_.connection.sendPacket(new SPlaySoundPacket(p_198573_2_, p_198573_3_, lvt_21_1_, lvt_22_1_, p_198573_6_));
         ++lvt_10_1_;
      }
   }
}
