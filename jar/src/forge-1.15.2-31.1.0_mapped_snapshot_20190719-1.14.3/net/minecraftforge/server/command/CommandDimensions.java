package net.minecraftforge.server.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.dimension.DimensionType;

public class CommandDimensions {
   static ArgumentBuilder<CommandSource, ?> register() {
      return ((LiteralArgumentBuilder)Commands.literal("dimensions").requires((cs) -> {
         return cs.hasPermissionLevel(0);
      })).executes((ctx) -> {
         ((CommandSource)ctx.getSource()).sendFeedback(new TranslationTextComponent("commands.forge.dimensions.list", new Object[0]), true);
         Map<String, List<String>> types = new HashMap();
         Iterator var2 = DimensionType.getAll().iterator();

         while(var2.hasNext()) {
            DimensionType dim = (DimensionType)var2.next();
            String key = dim.getModType() == null ? "Vanilla" : dim.getModType().getRegistryName().toString();
            ((List)types.computeIfAbsent(key, (k) -> {
               return new ArrayList();
            })).add(DimensionType.getKey(dim).toString());
         }

         types.keySet().stream().sorted().forEach((keyx) -> {
            ((CommandSource)ctx.getSource()).sendFeedback(new StringTextComponent(keyx + ": " + (String)((List)types.get(keyx)).stream().sorted().collect(Collectors.joining(", "))), true);
         });
         return 0;
      });
   }
}
