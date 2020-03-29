package net.minecraftforge.event.world;

import net.minecraft.world.server.ServerWorld;

public class SleepFinishedTimeEvent extends WorldEvent {
   private long newTime;
   private final long minTime;

   public SleepFinishedTimeEvent(ServerWorld worldIn, long newTimeIn, long minTimeIn) {
      super(worldIn);
      this.newTime = newTimeIn;
      this.minTime = minTimeIn;
   }

   public long getNewTime() {
      return this.newTime;
   }

   public boolean setTimeAddition(long newTimeIn) {
      if (this.minTime > newTimeIn) {
         return false;
      } else {
         this.newTime = newTimeIn;
         return true;
      }
   }
}
