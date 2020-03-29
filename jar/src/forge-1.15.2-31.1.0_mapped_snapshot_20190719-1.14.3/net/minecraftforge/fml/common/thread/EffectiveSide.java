package net.minecraftforge.fml.common.thread;

import net.minecraftforge.fml.LogicalSide;

public class EffectiveSide {
   public static LogicalSide get() {
      ThreadGroup group = Thread.currentThread().getThreadGroup();
      return group instanceof SidedThreadGroup ? ((SidedThreadGroup)group).getSide() : LogicalSide.CLIENT;
   }
}
