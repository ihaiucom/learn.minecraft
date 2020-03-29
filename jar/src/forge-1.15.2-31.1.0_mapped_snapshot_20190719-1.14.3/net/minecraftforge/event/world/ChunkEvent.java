package net.minecraftforge.event.world;

import net.minecraft.world.chunk.IChunk;

public class ChunkEvent extends WorldEvent {
   private final IChunk chunk;

   public ChunkEvent(IChunk chunk) {
      super(chunk.getWorldForge());
      this.chunk = chunk;
   }

   public IChunk getChunk() {
      return this.chunk;
   }

   public static class Unload extends ChunkEvent {
      public Unload(IChunk chunk) {
         super(chunk);
      }
   }

   public static class Load extends ChunkEvent {
      public Load(IChunk chunk) {
         super(chunk);
      }
   }
}
