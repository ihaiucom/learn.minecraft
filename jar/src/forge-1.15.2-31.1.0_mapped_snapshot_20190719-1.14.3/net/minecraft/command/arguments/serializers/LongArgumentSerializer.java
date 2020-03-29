package net.minecraft.command.arguments.serializers;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import net.minecraft.command.arguments.IArgumentSerializer;
import net.minecraft.network.PacketBuffer;

public class LongArgumentSerializer implements IArgumentSerializer<LongArgumentType> {
   public void write(LongArgumentType p_197072_1_, PacketBuffer p_197072_2_) {
      boolean lvt_3_1_ = p_197072_1_.getMinimum() != Long.MIN_VALUE;
      boolean lvt_4_1_ = p_197072_1_.getMaximum() != Long.MAX_VALUE;
      p_197072_2_.writeByte(BrigadierSerializers.minMaxFlags(lvt_3_1_, lvt_4_1_));
      if (lvt_3_1_) {
         p_197072_2_.writeLong(p_197072_1_.getMinimum());
      }

      if (lvt_4_1_) {
         p_197072_2_.writeLong(p_197072_1_.getMaximum());
      }

   }

   public LongArgumentType read(PacketBuffer p_197071_1_) {
      byte lvt_2_1_ = p_197071_1_.readByte();
      long lvt_3_1_ = BrigadierSerializers.hasMin(lvt_2_1_) ? p_197071_1_.readLong() : Long.MIN_VALUE;
      long lvt_5_1_ = BrigadierSerializers.hasMax(lvt_2_1_) ? p_197071_1_.readLong() : Long.MAX_VALUE;
      return LongArgumentType.longArg(lvt_3_1_, lvt_5_1_);
   }

   public void write(LongArgumentType p_212244_1_, JsonObject p_212244_2_) {
      if (p_212244_1_.getMinimum() != Long.MIN_VALUE) {
         p_212244_2_.addProperty("min", p_212244_1_.getMinimum());
      }

      if (p_212244_1_.getMaximum() != Long.MAX_VALUE) {
         p_212244_2_.addProperty("max", p_212244_1_.getMaximum());
      }

   }

   // $FF: synthetic method
   public ArgumentType read(PacketBuffer p_197071_1_) {
      return this.read(p_197071_1_);
   }
}
