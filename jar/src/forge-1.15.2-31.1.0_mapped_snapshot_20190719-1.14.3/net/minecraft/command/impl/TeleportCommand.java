package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityAnchorArgument;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.ILocationArgument;
import net.minecraft.command.arguments.LocationInput;
import net.minecraft.command.arguments.RotationArgument;
import net.minecraft.command.arguments.Vec3Argument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.server.SPlayerPositionLookPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.server.TicketType;

public class TeleportCommand {
   public static void register(CommandDispatcher<CommandSource> p_198809_0_) {
      LiteralCommandNode<CommandSource> literalcommandnode = p_198809_0_.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("teleport").requires((p_lambda$register$0_0_) -> {
         return p_lambda$register$0_0_.hasPermissionLevel(2);
      })).then(((RequiredArgumentBuilder)Commands.argument("targets", EntityArgument.entities()).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("location", Vec3Argument.vec3()).executes((p_lambda$register$1_0_) -> {
         return teleportToPos((CommandSource)p_lambda$register$1_0_.getSource(), EntityArgument.getEntities(p_lambda$register$1_0_, "targets"), ((CommandSource)p_lambda$register$1_0_.getSource()).getWorld(), Vec3Argument.getLocation(p_lambda$register$1_0_, "location"), (ILocationArgument)null, (TeleportCommand.Facing)null);
      })).then(Commands.argument("rotation", RotationArgument.rotation()).executes((p_lambda$register$2_0_) -> {
         return teleportToPos((CommandSource)p_lambda$register$2_0_.getSource(), EntityArgument.getEntities(p_lambda$register$2_0_, "targets"), ((CommandSource)p_lambda$register$2_0_.getSource()).getWorld(), Vec3Argument.getLocation(p_lambda$register$2_0_, "location"), RotationArgument.getRotation(p_lambda$register$2_0_, "rotation"), (TeleportCommand.Facing)null);
      }))).then(((LiteralArgumentBuilder)Commands.literal("facing").then(Commands.literal("entity").then(((RequiredArgumentBuilder)Commands.argument("facingEntity", EntityArgument.entity()).executes((p_lambda$register$3_0_) -> {
         return teleportToPos((CommandSource)p_lambda$register$3_0_.getSource(), EntityArgument.getEntities(p_lambda$register$3_0_, "targets"), ((CommandSource)p_lambda$register$3_0_.getSource()).getWorld(), Vec3Argument.getLocation(p_lambda$register$3_0_, "location"), (ILocationArgument)null, new TeleportCommand.Facing(EntityArgument.getEntity(p_lambda$register$3_0_, "facingEntity"), EntityAnchorArgument.Type.FEET));
      })).then(Commands.argument("facingAnchor", EntityAnchorArgument.entityAnchor()).executes((p_lambda$register$4_0_) -> {
         return teleportToPos((CommandSource)p_lambda$register$4_0_.getSource(), EntityArgument.getEntities(p_lambda$register$4_0_, "targets"), ((CommandSource)p_lambda$register$4_0_.getSource()).getWorld(), Vec3Argument.getLocation(p_lambda$register$4_0_, "location"), (ILocationArgument)null, new TeleportCommand.Facing(EntityArgument.getEntity(p_lambda$register$4_0_, "facingEntity"), EntityAnchorArgument.getEntityAnchor(p_lambda$register$4_0_, "facingAnchor")));
      }))))).then(Commands.argument("facingLocation", Vec3Argument.vec3()).executes((p_lambda$register$5_0_) -> {
         return teleportToPos((CommandSource)p_lambda$register$5_0_.getSource(), EntityArgument.getEntities(p_lambda$register$5_0_, "targets"), ((CommandSource)p_lambda$register$5_0_.getSource()).getWorld(), Vec3Argument.getLocation(p_lambda$register$5_0_, "location"), (ILocationArgument)null, new TeleportCommand.Facing(Vec3Argument.getVec3(p_lambda$register$5_0_, "facingLocation")));
      }))))).then(Commands.argument("destination", EntityArgument.entity()).executes((p_lambda$register$6_0_) -> {
         return teleportToEntity((CommandSource)p_lambda$register$6_0_.getSource(), EntityArgument.getEntities(p_lambda$register$6_0_, "targets"), EntityArgument.getEntity(p_lambda$register$6_0_, "destination"));
      })))).then(Commands.argument("location", Vec3Argument.vec3()).executes((p_lambda$register$7_0_) -> {
         return teleportToPos((CommandSource)p_lambda$register$7_0_.getSource(), Collections.singleton(((CommandSource)p_lambda$register$7_0_.getSource()).assertIsEntity()), ((CommandSource)p_lambda$register$7_0_.getSource()).getWorld(), Vec3Argument.getLocation(p_lambda$register$7_0_, "location"), LocationInput.current(), (TeleportCommand.Facing)null);
      }))).then(Commands.argument("destination", EntityArgument.entity()).executes((p_lambda$register$8_0_) -> {
         return teleportToEntity((CommandSource)p_lambda$register$8_0_.getSource(), Collections.singleton(((CommandSource)p_lambda$register$8_0_.getSource()).assertIsEntity()), EntityArgument.getEntity(p_lambda$register$8_0_, "destination"));
      })));
      p_198809_0_.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("tp").requires((p_lambda$register$9_0_) -> {
         return p_lambda$register$9_0_.hasPermissionLevel(2);
      })).redirect(literalcommandnode));
   }

   private static int teleportToEntity(CommandSource p_201126_0_, Collection<? extends Entity> p_201126_1_, Entity p_201126_2_) {
      Iterator var3 = p_201126_1_.iterator();

      while(var3.hasNext()) {
         Entity entity = (Entity)var3.next();
         teleport(p_201126_0_, entity, (ServerWorld)p_201126_2_.world, p_201126_2_.func_226277_ct_(), p_201126_2_.func_226278_cu_(), p_201126_2_.func_226281_cx_(), EnumSet.noneOf(SPlayerPositionLookPacket.Flags.class), p_201126_2_.rotationYaw, p_201126_2_.rotationPitch, (TeleportCommand.Facing)null);
      }

      if (p_201126_1_.size() == 1) {
         p_201126_0_.sendFeedback(new TranslationTextComponent("commands.teleport.success.entity.single", new Object[]{((Entity)p_201126_1_.iterator().next()).getDisplayName(), p_201126_2_.getDisplayName()}), true);
      } else {
         p_201126_0_.sendFeedback(new TranslationTextComponent("commands.teleport.success.entity.multiple", new Object[]{p_201126_1_.size(), p_201126_2_.getDisplayName()}), true);
      }

      return p_201126_1_.size();
   }

   private static int teleportToPos(CommandSource p_200559_0_, Collection<? extends Entity> p_200559_1_, ServerWorld p_200559_2_, ILocationArgument p_200559_3_, @Nullable ILocationArgument p_200559_4_, @Nullable TeleportCommand.Facing p_200559_5_) throws CommandSyntaxException {
      Vec3d vec3d = p_200559_3_.getPosition(p_200559_0_);
      Vec2f vec2f = p_200559_4_ == null ? null : p_200559_4_.getRotation(p_200559_0_);
      Set<SPlayerPositionLookPacket.Flags> set = EnumSet.noneOf(SPlayerPositionLookPacket.Flags.class);
      if (p_200559_3_.isXRelative()) {
         set.add(SPlayerPositionLookPacket.Flags.X);
      }

      if (p_200559_3_.isYRelative()) {
         set.add(SPlayerPositionLookPacket.Flags.Y);
      }

      if (p_200559_3_.isZRelative()) {
         set.add(SPlayerPositionLookPacket.Flags.Z);
      }

      if (p_200559_4_ == null) {
         set.add(SPlayerPositionLookPacket.Flags.X_ROT);
         set.add(SPlayerPositionLookPacket.Flags.Y_ROT);
      } else {
         if (p_200559_4_.isXRelative()) {
            set.add(SPlayerPositionLookPacket.Flags.X_ROT);
         }

         if (p_200559_4_.isYRelative()) {
            set.add(SPlayerPositionLookPacket.Flags.Y_ROT);
         }
      }

      Iterator var9 = p_200559_1_.iterator();

      while(var9.hasNext()) {
         Entity entity = (Entity)var9.next();
         if (p_200559_4_ == null) {
            teleport(p_200559_0_, entity, p_200559_2_, vec3d.x, vec3d.y, vec3d.z, set, entity.rotationYaw, entity.rotationPitch, p_200559_5_);
         } else {
            teleport(p_200559_0_, entity, p_200559_2_, vec3d.x, vec3d.y, vec3d.z, set, vec2f.y, vec2f.x, p_200559_5_);
         }
      }

      if (p_200559_1_.size() == 1) {
         p_200559_0_.sendFeedback(new TranslationTextComponent("commands.teleport.success.location.single", new Object[]{((Entity)p_200559_1_.iterator().next()).getDisplayName(), vec3d.x, vec3d.y, vec3d.z}), true);
      } else {
         p_200559_0_.sendFeedback(new TranslationTextComponent("commands.teleport.success.location.multiple", new Object[]{p_200559_1_.size(), vec3d.x, vec3d.y, vec3d.z}), true);
      }

      return p_200559_1_.size();
   }

   private static void teleport(CommandSource p_201127_0_, Entity p_201127_1_, ServerWorld p_201127_2_, double p_201127_3_, double p_201127_5_, double p_201127_7_, Set<SPlayerPositionLookPacket.Flags> p_201127_9_, float p_201127_10_, float p_201127_11_, @Nullable TeleportCommand.Facing p_201127_12_) {
      if (p_201127_1_ instanceof ServerPlayerEntity) {
         ChunkPos chunkpos = new ChunkPos(new BlockPos(p_201127_3_, p_201127_5_, p_201127_7_));
         p_201127_2_.getChunkProvider().func_217228_a(TicketType.POST_TELEPORT, chunkpos, 1, p_201127_1_.getEntityId());
         p_201127_1_.stopRiding();
         if (((ServerPlayerEntity)p_201127_1_).isSleeping()) {
            ((ServerPlayerEntity)p_201127_1_).func_225652_a_(true, true);
         }

         if (p_201127_2_ == p_201127_1_.world) {
            ((ServerPlayerEntity)p_201127_1_).connection.setPlayerLocation(p_201127_3_, p_201127_5_, p_201127_7_, p_201127_10_, p_201127_11_, p_201127_9_);
         } else {
            ((ServerPlayerEntity)p_201127_1_).teleport(p_201127_2_, p_201127_3_, p_201127_5_, p_201127_7_, p_201127_10_, p_201127_11_);
         }

         p_201127_1_.setRotationYawHead(p_201127_10_);
      } else {
         float f1 = MathHelper.wrapDegrees(p_201127_10_);
         float f = MathHelper.wrapDegrees(p_201127_11_);
         f = MathHelper.clamp(f, -90.0F, 90.0F);
         if (p_201127_2_ == p_201127_1_.world) {
            p_201127_1_.setLocationAndAngles(p_201127_3_, p_201127_5_, p_201127_7_, f1, f);
            p_201127_1_.setRotationYawHead(f1);
         } else {
            p_201127_1_.detach();
            p_201127_1_.dimension = p_201127_2_.dimension.getType();
            Entity entity = p_201127_1_;
            p_201127_1_ = p_201127_1_.getType().create(p_201127_2_);
            if (p_201127_1_ == null) {
               return;
            }

            p_201127_1_.copyDataFromOld(entity);
            p_201127_1_.setLocationAndAngles(p_201127_3_, p_201127_5_, p_201127_7_, f1, f);
            p_201127_1_.setRotationYawHead(f1);
            p_201127_2_.func_217460_e(p_201127_1_);
         }
      }

      if (p_201127_12_ != null) {
         p_201127_12_.updateLook(p_201127_0_, p_201127_1_);
      }

      if (!(p_201127_1_ instanceof LivingEntity) || !((LivingEntity)p_201127_1_).isElytraFlying()) {
         p_201127_1_.setMotion(p_201127_1_.getMotion().mul(1.0D, 0.0D, 1.0D));
         p_201127_1_.onGround = true;
      }

   }

   static class Facing {
      private final Vec3d position;
      private final Entity entity;
      private final EntityAnchorArgument.Type anchor;

      public Facing(Entity p_i48274_1_, EntityAnchorArgument.Type p_i48274_2_) {
         this.entity = p_i48274_1_;
         this.anchor = p_i48274_2_;
         this.position = p_i48274_2_.apply(p_i48274_1_);
      }

      public Facing(Vec3d p_i48246_1_) {
         this.entity = null;
         this.position = p_i48246_1_;
         this.anchor = null;
      }

      public void updateLook(CommandSource p_201124_1_, Entity p_201124_2_) {
         if (this.entity != null) {
            if (p_201124_2_ instanceof ServerPlayerEntity) {
               ((ServerPlayerEntity)p_201124_2_).lookAt(p_201124_1_.getEntityAnchorType(), this.entity, this.anchor);
            } else {
               p_201124_2_.lookAt(p_201124_1_.getEntityAnchorType(), this.position);
            }
         } else {
            p_201124_2_.lookAt(p_201124_1_.getEntityAnchorType(), this.position);
         }

      }
   }
}
