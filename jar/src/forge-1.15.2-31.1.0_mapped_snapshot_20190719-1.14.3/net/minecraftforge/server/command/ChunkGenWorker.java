package net.minecraftforge.server.command;

import java.util.ArrayDeque;
import java.util.Queue;
import net.minecraft.command.CommandSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.WorldWorkerManager;

public class ChunkGenWorker implements WorldWorkerManager.IWorker {
   private final CommandSource listener;
   protected final BlockPos start;
   protected final int total;
   private final DimensionType dim;
   private final Queue<BlockPos> queue;
   private final int notificationFrequency;
   private int lastNotification = 0;
   private long lastNotifcationTime = 0L;
   private int genned = 0;
   private Boolean keepingLoaded;

   public ChunkGenWorker(CommandSource listener, BlockPos start, int total, DimensionType dim, int interval) {
      this.listener = listener;
      this.start = start;
      this.total = total;
      this.dim = dim;
      this.queue = this.buildQueue();
      this.notificationFrequency = interval != -1 ? interval : Math.max(total / 20, 100);
      this.lastNotifcationTime = System.currentTimeMillis();
   }

   protected Queue<BlockPos> buildQueue() {
      Queue<BlockPos> ret = new ArrayDeque();
      ret.add(this.start);

      for(int radius = 1; ret.size() < this.total; ++radius) {
         int q;
         for(q = -radius + 1; q <= radius && ret.size() < this.total; ++q) {
            ret.add(this.start.add(radius, 0, q));
         }

         for(q = radius - 1; q >= -radius && ret.size() < this.total; --q) {
            ret.add(this.start.add(q, 0, radius));
         }

         for(q = radius - 1; q >= -radius && ret.size() < this.total; --q) {
            ret.add(this.start.add(-radius, 0, q));
         }

         for(q = -radius + 1; q <= radius && ret.size() < this.total; ++q) {
            ret.add(this.start.add(q, 0, -radius));
         }
      }

      return ret;
   }

   public TextComponent getStartMessage(CommandSource sender) {
      return new TranslationTextComponent("commands.forge.gen.start", new Object[]{this.total, this.start.getX(), this.start.getZ(), this.dim});
   }

   public boolean hasWork() {
      return this.queue.size() > 0;
   }

   public boolean doWork() {
      ServerWorld world = DimensionManager.getWorld(this.listener.getServer(), this.dim, false, false);
      if (world == null) {
         world = DimensionManager.initWorld(this.listener.getServer(), this.dim);
         if (world == null) {
            this.listener.sendFeedback(new TranslationTextComponent("commands.forge.gen.dim_fail", new Object[]{this.dim}), true);
            this.queue.clear();
            return false;
         }
      }

      BlockPos next = (BlockPos)this.queue.poll();
      if (next != null) {
         if (this.keepingLoaded == null) {
            this.keepingLoaded = DimensionManager.keepLoaded(this.dim, true);
         }

         if (++this.lastNotification >= this.notificationFrequency || this.lastNotifcationTime < System.currentTimeMillis() - 60000L) {
            this.listener.sendFeedback(new TranslationTextComponent("commands.forge.gen.progress", new Object[]{this.total - this.queue.size(), this.total}), true);
            this.lastNotification = 0;
            this.lastNotifcationTime = System.currentTimeMillis();
         }

         int x = next.getX();
         int z = next.getZ();
         world.getChunk(x, z, ChunkStatus.FULL, false);
      }

      if (this.queue.size() == 0) {
         this.listener.sendFeedback(new TranslationTextComponent("commands.forge.gen.complete", new Object[]{this.genned, this.total, this.dim}), true);
         if (this.keepingLoaded != null && !this.keepingLoaded) {
            DimensionManager.keepLoaded(this.dim, false);
         }

         return false;
      } else {
         return true;
      }
   }
}
