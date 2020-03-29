package net.minecraft.command.arguments;

import com.google.gson.JsonParseException;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.command.CommandSource;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class ComponentArgument implements ArgumentType<ITextComponent> {
   private static final Collection<String> EXAMPLES = Arrays.asList("\"hello world\"", "\"\"", "\"{\"text\":\"hello world\"}", "[\"\"]");
   public static final DynamicCommandExceptionType COMPONENT_INVALID = new DynamicCommandExceptionType((p_208660_0_) -> {
      return new TranslationTextComponent("argument.component.invalid", new Object[]{p_208660_0_});
   });

   private ComponentArgument() {
   }

   public static ITextComponent getComponent(CommandContext<CommandSource> p_197068_0_, String p_197068_1_) {
      return (ITextComponent)p_197068_0_.getArgument(p_197068_1_, ITextComponent.class);
   }

   public static ComponentArgument component() {
      return new ComponentArgument();
   }

   public ITextComponent parse(StringReader p_parse_1_) throws CommandSyntaxException {
      try {
         ITextComponent lvt_2_1_ = ITextComponent.Serializer.fromJson(p_parse_1_);
         if (lvt_2_1_ == null) {
            throw COMPONENT_INVALID.createWithContext(p_parse_1_, "empty");
         } else {
            return lvt_2_1_;
         }
      } catch (JsonParseException var4) {
         String lvt_3_1_ = var4.getCause() != null ? var4.getCause().getMessage() : var4.getMessage();
         throw COMPONENT_INVALID.createWithContext(p_parse_1_, lvt_3_1_);
      }
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   // $FF: synthetic method
   public Object parse(StringReader p_parse_1_) throws CommandSyntaxException {
      return this.parse(p_parse_1_);
   }
}
