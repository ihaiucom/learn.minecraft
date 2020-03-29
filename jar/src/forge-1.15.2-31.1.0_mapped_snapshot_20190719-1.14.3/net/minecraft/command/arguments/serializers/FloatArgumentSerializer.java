package net.minecraft.command.arguments.serializers;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import net.minecraft.command.arguments.IArgumentSerializer;
import net.minecraft.network.PacketBuffer;

public class FloatArgumentSerializer implements IArgumentSerializer<FloatArgumentType> {
   public void write(FloatArgumentType p_197072_1_, PacketBuffer p_197072_2_) {
      boolean lvt_3_1_ = p_197072_1_.getMinimum() != -3.4028235E38F;
      boolean lvt_4_1_ = p_197072_1_.getMaximum() != Float.MAX_VALUE;
      p_197072_2_.writeByte(BrigadierSerializers.minMaxFlags(lvt_3_1_, lvt_4_1_));
      if (lvt_3_1_) {
         p_197072_2_.writeFloat(p_197072_1_.getMinimum());
      }

      if (lvt_4_1_) {
         p_197072_2_.writeFloat(p_197072_1_.getMaximum());
      }

   }

   public FloatArgumentType read(PacketBuffer p_197071_1_) {
      byte lvt_2_1_ = p_197071_1_.readByte();
      float lvt_3_1_ = BrigadierSerializers.hasMin(lvt_2_1_) ? p_197071_1_.readFloat() : -3.4028235E38F;
      float lvt_4_1_ = BrigadierSerializers.hasMax(lvt_2_1_) ? p_197071_1_.readFloat() : Float.MAX_VALUE;
      return FloatArgumentType.floatArg(lvt_3_1_, lvt_4_1_);
   }

   public void write(FloatArgumentType p_212244_1_, JsonObject p_212244_2_) {
      if (p_212244_1_.getMinimum() != -3.4028235E38F) {
         p_212244_2_.addProperty("min", p_212244_1_.getMinimum());
      }

      if (p_212244_1_.getMaximum() != Float.MAX_VALUE) {
         p_212244_2_.addProperty("max", p_212244_1_.getMaximum());
      }

   }

   // $FF: synthetic method
   public ArgumentType read(PacketBuffer p_197071_1_) {
      return this.read(p_197071_1_);
   }
}
