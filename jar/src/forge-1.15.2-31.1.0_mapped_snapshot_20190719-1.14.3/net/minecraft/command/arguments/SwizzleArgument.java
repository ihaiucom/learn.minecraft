package net.minecraft.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import net.minecraft.command.CommandSource;
import net.minecraft.util.Direction;
import net.minecraft.util.text.TranslationTextComponent;

public class SwizzleArgument implements ArgumentType<EnumSet<Direction.Axis>> {
   private static final Collection<String> EXAMPLES = Arrays.asList("xyz", "x");
   private static final SimpleCommandExceptionType SWIZZLE_INVALID = new SimpleCommandExceptionType(new TranslationTextComponent("arguments.swizzle.invalid", new Object[0]));

   public static SwizzleArgument swizzle() {
      return new SwizzleArgument();
   }

   public static EnumSet<Direction.Axis> getSwizzle(CommandContext<CommandSource> p_197291_0_, String p_197291_1_) {
      return (EnumSet)p_197291_0_.getArgument(p_197291_1_, EnumSet.class);
   }

   public EnumSet<Direction.Axis> parse(StringReader p_parse_1_) throws CommandSyntaxException {
      EnumSet lvt_2_1_ = EnumSet.noneOf(Direction.Axis.class);

      while(p_parse_1_.canRead() && p_parse_1_.peek() != ' ') {
         char lvt_3_1_ = p_parse_1_.read();
         Direction.Axis lvt_4_4_;
         switch(lvt_3_1_) {
         case 'x':
            lvt_4_4_ = Direction.Axis.X;
            break;
         case 'y':
            lvt_4_4_ = Direction.Axis.Y;
            break;
         case 'z':
            lvt_4_4_ = Direction.Axis.Z;
            break;
         default:
            throw SWIZZLE_INVALID.create();
         }

         if (lvt_2_1_.contains(lvt_4_4_)) {
            throw SWIZZLE_INVALID.create();
         }

         lvt_2_1_.add(lvt_4_4_);
      }

      return lvt_2_1_;
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   // $FF: synthetic method
   public Object parse(StringReader p_parse_1_) throws CommandSyntaxException {
      return this.parse(p_parse_1_);
   }
}
