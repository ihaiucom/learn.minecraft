package net.minecraft.world.chunk.listener;

import javax.annotation.Nullable;
import net.minecraft.util.Util;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.chunk.ChunkStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoggingChunkStatusListener implements IChunkStatusListener {
   private static final Logger field_219512_a = LogManager.getLogger();
   private final int totalChunks;
   private int loadedChunks;
   private long startTime;
   private long nextLogTime = Long.MAX_VALUE;

   public LoggingChunkStatusListener(int p_i50697_1_) {
      int lvt_2_1_ = p_i50697_1_ * 2 + 1;
      this.totalChunks = lvt_2_1_ * lvt_2_1_;
   }

   public void start(ChunkPos p_219509_1_) {
      this.nextLogTime = Util.milliTime();
      this.startTime = this.nextLogTime;
   }

   public void statusChanged(ChunkPos p_219508_1_, @Nullable ChunkStatus p_219508_2_) {
      if (p_219508_2_ == ChunkStatus.FULL) {
         ++this.loadedChunks;
      }

      int lvt_3_1_ = this.getPercentDone();
      if (Util.milliTime() > this.nextLogTime) {
         this.nextLogTime += 500L;
         field_219512_a.info((new TranslationTextComponent("menu.preparingSpawn", new Object[]{MathHelper.clamp(lvt_3_1_, 0, 100)})).getString());
      }

   }

   public void stop() {
      field_219512_a.info("Time elapsed: {} ms", Util.milliTime() - this.startTime);
      this.nextLogTime = Long.MAX_VALUE;
   }

   public int getPercentDone() {
      return MathHelper.floor((float)this.loadedChunks * 100.0F / (float)this.totalChunks);
   }
}
