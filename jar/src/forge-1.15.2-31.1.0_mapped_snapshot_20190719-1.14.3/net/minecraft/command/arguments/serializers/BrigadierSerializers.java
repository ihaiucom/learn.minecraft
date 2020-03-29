package net.minecraft.command.arguments.serializers;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.arguments.ArgumentSerializer;
import net.minecraft.command.arguments.ArgumentTypes;

public class BrigadierSerializers {
   public static void registerArgumentTypes() {
      ArgumentTypes.register("brigadier:bool", BoolArgumentType.class, new ArgumentSerializer(BoolArgumentType::bool));
      ArgumentTypes.register("brigadier:float", FloatArgumentType.class, new FloatArgumentSerializer());
      ArgumentTypes.register("brigadier:double", DoubleArgumentType.class, new DoubleArgumentSerializer());
      ArgumentTypes.register("brigadier:integer", IntegerArgumentType.class, new IntArgumentSerializer());
      ArgumentTypes.register("brigadier:long", LongArgumentType.class, new LongArgumentSerializer());
      ArgumentTypes.register("brigadier:string", StringArgumentType.class, new StringArgumentSerializer());
   }

   public static byte minMaxFlags(boolean p_197508_0_, boolean p_197508_1_) {
      byte lvt_2_1_ = 0;
      if (p_197508_0_) {
         lvt_2_1_ = (byte)(lvt_2_1_ | 1);
      }

      if (p_197508_1_) {
         lvt_2_1_ = (byte)(lvt_2_1_ | 2);
      }

      return lvt_2_1_;
   }

   public static boolean hasMin(byte p_197510_0_) {
      return (p_197510_0_ & 1) != 0;
   }

   public static boolean hasMax(byte p_197509_0_) {
      return (p_197509_0_ & 2) != 0;
   }
}
