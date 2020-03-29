package net.minecraftforge.server.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import java.util.function.Function;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.command.arguments.DimensionArgument;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.ITeleporter;

public class CommandSetDimension {
   private static final SimpleCommandExceptionType NO_ENTITIES = new SimpleCommandExceptionType(new TranslationTextComponent("commands.forge.setdim.invalid.entity", new Object[0]));
   private static final DynamicCommandExceptionType INVALID_DIMENSION = new DynamicCommandExceptionType((dim) -> {
      return new TranslationTextComponent("commands.forge.setdim.invalid.dim", new Object[]{dim});
   });
   private static final ITeleporter PORTALLESS = new ITeleporter() {
      public Entity placeEntity(Entity entity, ServerWorld currentWorld, ServerWorld destWorld, float yaw, Function<Boolean, Entity> repositionEntity) {
         return (Entity)repositionEntity.apply(false);
      }
   };

   static ArgumentBuilder<CommandSource, ?> register() {
      return ((LiteralArgumentBuilder)Commands.literal("setdimension").requires((cs) -> {
         return cs.hasPermissionLevel(2);
      })).then(Commands.argument("targets", EntityArgument.entities()).then(((RequiredArgumentBuilder)Commands.argument("dim", DimensionArgument.getDimension()).then(Commands.argument("pos", BlockPosArgument.blockPos()).executes((ctx) -> {
         return execute((CommandSource)ctx.getSource(), EntityArgument.getEntitiesAllowingNone(ctx, "targets"), DimensionArgument.func_212592_a(ctx, "dim"), BlockPosArgument.getBlockPos(ctx, "pos"));
      }))).executes((ctx) -> {
         return execute((CommandSource)ctx.getSource(), EntityArgument.getEntitiesAllowingNone(ctx, "targets"), DimensionArgument.func_212592_a(ctx, "dim"), new BlockPos(((CommandSource)ctx.getSource()).getPos()));
      })));
   }

   private static int execute(CommandSource sender, Collection<? extends Entity> entities, DimensionType dim, BlockPos pos) throws CommandSyntaxException {
      entities.removeIf((e) -> {
         return !canEntityTeleport(e);
      });
      if (entities.isEmpty()) {
         throw NO_ENTITIES.create();
      } else {
         entities.stream().filter((e) -> {
            return e.dimension == dim;
         }).forEach((e) -> {
            sender.sendFeedback(new TranslationTextComponent("commands.forge.setdim.invalid.nochange", new Object[]{e.getDisplayName().getFormattedText(), dim}), true);
         });
         entities.stream().filter((e) -> {
            return e.dimension != dim;
         }).forEach((e) -> {
            e.changeDimension(dim, PORTALLESS);
         });
         return 0;
      }
   }

   private static boolean canEntityTeleport(Entity entity) {
      return !entity.isPassenger() && !entity.isBeingRidden() && entity.isNonBoss();
   }
}
