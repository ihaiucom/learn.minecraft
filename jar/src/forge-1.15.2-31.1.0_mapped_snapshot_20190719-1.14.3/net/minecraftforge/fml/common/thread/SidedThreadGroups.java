package net.minecraftforge.fml.common.thread;

import net.minecraftforge.fml.LogicalSide;

public final class SidedThreadGroups {
   public static final SidedThreadGroup CLIENT;
   public static final SidedThreadGroup SERVER;

   private SidedThreadGroups() {
   }

   static {
      CLIENT = new SidedThreadGroup(LogicalSide.CLIENT);
      SERVER = new SidedThreadGroup(LogicalSide.SERVER);
   }
}
