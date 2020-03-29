package net.minecraft.command.arguments.serializers;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType.StringType;
import net.minecraft.command.arguments.IArgumentSerializer;
import net.minecraft.network.PacketBuffer;

public class StringArgumentSerializer implements IArgumentSerializer<StringArgumentType> {
   public void write(StringArgumentType p_197072_1_, PacketBuffer p_197072_2_) {
      p_197072_2_.writeEnumValue(p_197072_1_.getType());
   }

   public StringArgumentType read(PacketBuffer p_197071_1_) {
      StringType lvt_2_1_ = (StringType)p_197071_1_.readEnumValue(StringType.class);
      switch(lvt_2_1_) {
      case SINGLE_WORD:
         return StringArgumentType.word();
      case QUOTABLE_PHRASE:
         return StringArgumentType.string();
      case GREEDY_PHRASE:
      default:
         return StringArgumentType.greedyString();
      }
   }

   public void write(StringArgumentType p_212244_1_, JsonObject p_212244_2_) {
      switch(p_212244_1_.getType()) {
      case SINGLE_WORD:
         p_212244_2_.addProperty("type", "word");
         break;
      case QUOTABLE_PHRASE:
         p_212244_2_.addProperty("type", "phrase");
         break;
      case GREEDY_PHRASE:
      default:
         p_212244_2_.addProperty("type", "greedy");
      }

   }

   // $FF: synthetic method
   public ArgumentType read(PacketBuffer p_197071_1_) {
      return this.read(p_197071_1_);
   }
}
