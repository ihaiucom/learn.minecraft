package net.minecraft.command.arguments;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.network.PacketBuffer;

public interface IArgumentSerializer<T extends ArgumentType<?>> {
   void write(T var1, PacketBuffer var2);

   T read(PacketBuffer var1);

   void write(T var1, JsonObject var2);
}
