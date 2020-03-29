package net.minecraftforge.common.command;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.arguments.EntitySelector;
import net.minecraft.command.arguments.EntitySelectorParser;
import net.minecraft.util.text.ITextComponent;

public interface IEntitySelectorType {
   EntitySelector build(EntitySelectorParser var1) throws CommandSyntaxException;

   ITextComponent getSuggestionTooltip();
}
