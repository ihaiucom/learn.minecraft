package net.minecraft.command.arguments;

import com.google.common.collect.Lists;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.command.CommandSource;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class MessageArgument implements ArgumentType<MessageArgument.Message> {
   private static final Collection<String> EXAMPLES = Arrays.asList("Hello world!", "foo", "@e", "Hello @p :)");

   public static MessageArgument message() {
      return new MessageArgument();
   }

   public static ITextComponent getMessage(CommandContext<CommandSource> p_197124_0_, String p_197124_1_) throws CommandSyntaxException {
      return ((MessageArgument.Message)p_197124_0_.getArgument(p_197124_1_, MessageArgument.Message.class)).toComponent((CommandSource)p_197124_0_.getSource(), ((CommandSource)p_197124_0_.getSource()).hasPermissionLevel(2));
   }

   public MessageArgument.Message parse(StringReader p_parse_1_) throws CommandSyntaxException {
      return MessageArgument.Message.parse(p_parse_1_, true);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   // $FF: synthetic method
   public Object parse(StringReader p_parse_1_) throws CommandSyntaxException {
      return this.parse(p_parse_1_);
   }

   public static class Part {
      private final int start;
      private final int end;
      private final EntitySelector selector;

      public Part(int p_i48020_1_, int p_i48020_2_, EntitySelector p_i48020_3_) {
         this.start = p_i48020_1_;
         this.end = p_i48020_2_;
         this.selector = p_i48020_3_;
      }

      public int getStart() {
         return this.start;
      }

      public int getEnd() {
         return this.end;
      }

      @Nullable
      public ITextComponent toComponent(CommandSource p_197116_1_) throws CommandSyntaxException {
         return EntitySelector.joinNames(this.selector.select(p_197116_1_));
      }
   }

   public static class Message {
      private final String text;
      private final MessageArgument.Part[] selectors;

      public Message(String p_i48021_1_, MessageArgument.Part[] p_i48021_2_) {
         this.text = p_i48021_1_;
         this.selectors = p_i48021_2_;
      }

      public ITextComponent toComponent(CommandSource p_201312_1_, boolean p_201312_2_) throws CommandSyntaxException {
         if (this.selectors.length != 0 && p_201312_2_) {
            ITextComponent lvt_3_1_ = new StringTextComponent(this.text.substring(0, this.selectors[0].getStart()));
            int lvt_4_1_ = this.selectors[0].getStart();
            MessageArgument.Part[] var5 = this.selectors;
            int var6 = var5.length;

            for(int var7 = 0; var7 < var6; ++var7) {
               MessageArgument.Part lvt_8_1_ = var5[var7];
               ITextComponent lvt_9_1_ = lvt_8_1_.toComponent(p_201312_1_);
               if (lvt_4_1_ < lvt_8_1_.getStart()) {
                  lvt_3_1_.appendText(this.text.substring(lvt_4_1_, lvt_8_1_.getStart()));
               }

               if (lvt_9_1_ != null) {
                  lvt_3_1_.appendSibling(lvt_9_1_);
               }

               lvt_4_1_ = lvt_8_1_.getEnd();
            }

            if (lvt_4_1_ < this.text.length()) {
               lvt_3_1_.appendText(this.text.substring(lvt_4_1_, this.text.length()));
            }

            return lvt_3_1_;
         } else {
            return new StringTextComponent(this.text);
         }
      }

      public static MessageArgument.Message parse(StringReader p_197113_0_, boolean p_197113_1_) throws CommandSyntaxException {
         String lvt_2_1_ = p_197113_0_.getString().substring(p_197113_0_.getCursor(), p_197113_0_.getTotalLength());
         if (!p_197113_1_) {
            p_197113_0_.setCursor(p_197113_0_.getTotalLength());
            return new MessageArgument.Message(lvt_2_1_, new MessageArgument.Part[0]);
         } else {
            List<MessageArgument.Part> lvt_3_1_ = Lists.newArrayList();
            int lvt_4_1_ = p_197113_0_.getCursor();

            while(true) {
               int lvt_5_1_;
               EntitySelector lvt_6_2_;
               label38:
               while(true) {
                  while(p_197113_0_.canRead()) {
                     if (p_197113_0_.peek() == '@') {
                        lvt_5_1_ = p_197113_0_.getCursor();

                        try {
                           EntitySelectorParser lvt_7_1_ = new EntitySelectorParser(p_197113_0_);
                           lvt_6_2_ = lvt_7_1_.parse();
                           break label38;
                        } catch (CommandSyntaxException var8) {
                           if (var8.getType() != EntitySelectorParser.SELECTOR_TYPE_MISSING && var8.getType() != EntitySelectorParser.UNKNOWN_SELECTOR_TYPE) {
                              throw var8;
                           }

                           p_197113_0_.setCursor(lvt_5_1_ + 1);
                        }
                     } else {
                        p_197113_0_.skip();
                     }
                  }

                  return new MessageArgument.Message(lvt_2_1_, (MessageArgument.Part[])lvt_3_1_.toArray(new MessageArgument.Part[lvt_3_1_.size()]));
               }

               lvt_3_1_.add(new MessageArgument.Part(lvt_5_1_ - lvt_4_1_, p_197113_0_.getCursor() - lvt_4_1_, lvt_6_2_));
            }
         }
      }
   }
}
