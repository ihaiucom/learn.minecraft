package net.minecraftforge.server.command;

import com.google.common.collect.Maps;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.DimensionArgument;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

class CommandEntity {
   static ArgumentBuilder<CommandSource, ?> register() {
      return Commands.literal("entity").then(CommandEntity.EntityListCommand.register());
   }

   private static class EntityListCommand {
      private static final SimpleCommandExceptionType INVALID_FILTER = new SimpleCommandExceptionType(new TranslationTextComponent("commands.forge.entity.list.invalid", new Object[0]));
      private static final DynamicCommandExceptionType INVALID_DIMENSION = new DynamicCommandExceptionType((dim) -> {
         return new TranslationTextComponent("commands.forge.entity.list.invalidworld", new Object[]{dim});
      });
      private static final SimpleCommandExceptionType NO_ENTITIES = new SimpleCommandExceptionType(new TranslationTextComponent("commands.forge.entity.list.none", new Object[0]));

      static ArgumentBuilder<CommandSource, ?> register() {
         return ((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("list").requires((cs) -> {
            return cs.hasPermissionLevel(2);
         })).then(((RequiredArgumentBuilder)Commands.argument("filter", StringArgumentType.string()).suggests((ctx, builder) -> {
            return ISuggestionProvider.suggest(ForgeRegistries.ENTITIES.getKeys().stream().map((id) -> {
               return id.toString();
            }), builder);
         }).then(Commands.argument("dim", DimensionArgument.getDimension()).executes((ctx) -> {
            return execute((CommandSource)ctx.getSource(), StringArgumentType.getString(ctx, "filter"), DimensionArgument.func_212592_a(ctx, "dim"));
         }))).executes((ctx) -> {
            return execute((CommandSource)ctx.getSource(), StringArgumentType.getString(ctx, "filter"), ((CommandSource)ctx.getSource()).getWorld().dimension.getType());
         }))).executes((ctx) -> {
            return execute((CommandSource)ctx.getSource(), "*", ((CommandSource)ctx.getSource()).getWorld().dimension.getType());
         });
      }

      private static int execute(CommandSource sender, String filter, DimensionType dim) throws CommandSyntaxException {
         String cleanFilter = filter.replace("?", ".?").replace("*", ".*?");
         Set<ResourceLocation> names = (Set)ForgeRegistries.ENTITIES.getKeys().stream().filter((n) -> {
            return n.toString().matches(cleanFilter);
         }).collect(Collectors.toSet());
         if (names.isEmpty()) {
            throw INVALID_FILTER.create();
         } else {
            ServerWorld world = DimensionManager.getWorld(sender.getServer(), dim, false, false);
            if (world == null) {
               throw INVALID_DIMENSION.create(dim);
            } else {
               Map<ResourceLocation, MutablePair<Integer, Map<ChunkPos, Integer>>> list = Maps.newHashMap();
               world.getEntities().forEach((ex) -> {
                  MutablePair<Integer, Map<ChunkPos, Integer>> info = (MutablePair)list.computeIfAbsent(ex.getType().getRegistryName(), (k) -> {
                     return MutablePair.of(0, Maps.newHashMap());
                  });
                  ChunkPos chunk = new ChunkPos(ex.getPosition());
                  Integer var5 = (Integer)info.left;
                  Object var6 = info.left = (Integer)info.left + 1;
                  ((Map)info.right).put(chunk, (Integer)((Map)info.right).getOrDefault(chunk, 0) + 1);
               });
               if (names.size() != 1) {
                  List<Pair<ResourceLocation, Integer>> info = new ArrayList();
                  list.forEach((key, value) -> {
                     if (names.contains(key)) {
                        Pair<ResourceLocation, Integer> of = Pair.of(key, value.left);
                        info.add(of);
                     }

                  });
                  info.sort((a, b) -> {
                     return Objects.equals(a.getRight(), b.getRight()) ? ((ResourceLocation)a.getKey()).toString().compareTo(((ResourceLocation)b.getKey()).toString()) : (Integer)b.getRight() - (Integer)a.getRight();
                  });
                  if (info.size() == 0) {
                     throw NO_ENTITIES.create();
                  } else {
                     int count = info.stream().mapToInt(Pair::getRight).sum();
                     sender.sendFeedback(new TranslationTextComponent("commands.forge.entity.list.multiple.header", new Object[]{count}), true);
                     info.forEach((ex) -> {
                        sender.sendFeedback(new StringTextComponent("  " + ex.getValue() + ": " + ex.getKey()), true);
                     });
                     return info.size();
                  }
               } else {
                  ResourceLocation name = (ResourceLocation)names.iterator().next();
                  Pair<Integer, Map<ChunkPos, Integer>> info = (Pair)list.get(name);
                  if (info == null) {
                     throw NO_ENTITIES.create();
                  } else {
                     sender.sendFeedback(new TranslationTextComponent("commands.forge.entity.list.single.header", new Object[]{name, info.getLeft()}), true);
                     List<Entry<ChunkPos, Integer>> toSort = new ArrayList();
                     toSort.addAll(((Map)info.getRight()).entrySet());
                     toSort.sort((a, b) -> {
                        return Objects.equals(a.getValue(), b.getValue()) ? ((ChunkPos)a.getKey()).toString().compareTo(((ChunkPos)b.getKey()).toString()) : (Integer)b.getValue() - (Integer)a.getValue();
                     });
                     long limit = 10L;
                     Iterator var12 = toSort.iterator();

                     while(var12.hasNext()) {
                        Entry<ChunkPos, Integer> e = (Entry)var12.next();
                        if (limit-- == 0L) {
                           break;
                        }

                        sender.sendFeedback(new StringTextComponent("  " + e.getValue() + ": " + ((ChunkPos)e.getKey()).x + ", " + ((ChunkPos)e.getKey()).z), true);
                     }

                     return toSort.size();
                  }
               }
            }
         }
      }
   }
}
