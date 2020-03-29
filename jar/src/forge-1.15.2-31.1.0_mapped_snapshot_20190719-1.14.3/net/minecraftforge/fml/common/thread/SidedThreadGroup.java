package net.minecraftforge.fml.common.thread;

import java.util.concurrent.ThreadFactory;
import javax.annotation.Nonnull;
import net.minecraftforge.fml.LogicalSide;

public final class SidedThreadGroup extends ThreadGroup implements ThreadFactory {
   private final LogicalSide side;

   SidedThreadGroup(LogicalSide side) {
      super(side.name());
      this.side = side;
   }

   public LogicalSide getSide() {
      return this.side;
   }

   public Thread newThread(@Nonnull Runnable runnable) {
      return new Thread(this, runnable);
   }
}
