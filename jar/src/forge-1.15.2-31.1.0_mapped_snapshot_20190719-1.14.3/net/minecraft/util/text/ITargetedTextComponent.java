package net.minecraft.util.text;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;

public interface ITargetedTextComponent {
   ITextComponent createNames(@Nullable CommandSource var1, @Nullable Entity var2, int var3) throws CommandSyntaxException;
}
