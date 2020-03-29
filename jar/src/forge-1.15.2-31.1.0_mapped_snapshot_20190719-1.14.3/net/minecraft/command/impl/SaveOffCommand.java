package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Iterator;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;

public class SaveOffCommand {
   private static final SimpleCommandExceptionType SAVE_ALREADY_OFF_EXCEPTION = new SimpleCommandExceptionType(new TranslationTextComponent("commands.save.alreadyOff", new Object[0]));

   public static void register(CommandDispatcher<CommandSource> p_198617_0_) {
      p_198617_0_.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("save-off").requires((p_198619_0_) -> {
         return p_198619_0_.hasPermissionLevel(4);
      })).executes((p_198618_0_) -> {
         CommandSource lvt_1_1_ = (CommandSource)p_198618_0_.getSource();
         boolean lvt_2_1_ = false;
         Iterator var3 = lvt_1_1_.getServer().getWorlds().iterator();

         while(var3.hasNext()) {
            ServerWorld lvt_4_1_ = (ServerWorld)var3.next();
            if (lvt_4_1_ != null && !lvt_4_1_.disableLevelSaving) {
               lvt_4_1_.disableLevelSaving = true;
               lvt_2_1_ = true;
            }
         }

         if (!lvt_2_1_) {
            throw SAVE_ALREADY_OFF_EXCEPTION.create();
         } else {
            lvt_1_1_.sendFeedback(new TranslationTextComponent("commands.save.disabled", new Object[0]), true);
            return 1;
         }
      }));
   }
}
