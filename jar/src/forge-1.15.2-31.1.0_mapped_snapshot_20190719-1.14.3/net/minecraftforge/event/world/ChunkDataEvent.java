package net.minecraftforge.event.world;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.IChunk;

public class ChunkDataEvent extends ChunkEvent {
   private final CompoundNBT data;

   public ChunkDataEvent(IChunk chunk, CompoundNBT data) {
      super(chunk);
      this.data = data;
   }

   public CompoundNBT getData() {
      return this.data;
   }

   public static class Save extends ChunkDataEvent {
      public Save(IChunk chunk, CompoundNBT data) {
         super(chunk, data);
      }
   }

   public static class Load extends ChunkDataEvent {
      private ChunkStatus.Type status;

      public Load(IChunk chunk, CompoundNBT data, ChunkStatus.Type status) {
         super(chunk, data);
         this.status = status;
      }

      public ChunkStatus.Type getStatus() {
         return this.status;
      }
   }
}
