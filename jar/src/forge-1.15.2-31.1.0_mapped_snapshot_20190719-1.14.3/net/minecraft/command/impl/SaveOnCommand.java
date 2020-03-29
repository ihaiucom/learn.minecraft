package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Iterator;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;

public class SaveOnCommand {
   private static final SimpleCommandExceptionType SAVE_ALREADY_ON_EXCEPTION = new SimpleCommandExceptionType(new TranslationTextComponent("commands.save.alreadyOn", new Object[0]));

   public static void register(CommandDispatcher<CommandSource> p_198621_0_) {
      p_198621_0_.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("save-on").requires((p_198623_0_) -> {
         return p_198623_0_.hasPermissionLevel(4);
      })).executes((p_198622_0_) -> {
         CommandSource lvt_1_1_ = (CommandSource)p_198622_0_.getSource();
         boolean lvt_2_1_ = false;
         Iterator var3 = lvt_1_1_.getServer().getWorlds().iterator();

         while(var3.hasNext()) {
            ServerWorld lvt_4_1_ = (ServerWorld)var3.next();
            if (lvt_4_1_ != null && lvt_4_1_.disableLevelSaving) {
               lvt_4_1_.disableLevelSaving = false;
               lvt_2_1_ = true;
            }
         }

         if (!lvt_2_1_) {
            throw SAVE_ALREADY_ON_EXCEPTION.create();
         } else {
            lvt_1_1_.sendFeedback(new TranslationTextComponent("commands.save.enabled", new Object[0]), true);
            return 1;
         }
      }));
   }
}
