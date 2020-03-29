package net.minecraft.world.chunk;

import java.io.IOException;
import java.util.function.BooleanSupplier;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.lighting.WorldLightManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class AbstractChunkProvider implements IChunkLightProvider, AutoCloseable {
   @Nullable
   public Chunk getChunk(int p_217205_1_, int p_217205_2_, boolean p_217205_3_) {
      return (Chunk)this.getChunk(p_217205_1_, p_217205_2_, ChunkStatus.FULL, p_217205_3_);
   }

   @Nullable
   public Chunk func_225313_a(int p_225313_1_, int p_225313_2_) {
      return this.getChunk(p_225313_1_, p_225313_2_, false);
   }

   @Nullable
   public IBlockReader getChunkForLight(int p_217202_1_, int p_217202_2_) {
      return this.getChunk(p_217202_1_, p_217202_2_, ChunkStatus.EMPTY, false);
   }

   public boolean chunkExists(int p_73149_1_, int p_73149_2_) {
      return this.getChunk(p_73149_1_, p_73149_2_, ChunkStatus.FULL, false) != null;
   }

   @Nullable
   public abstract IChunk getChunk(int var1, int var2, ChunkStatus var3, boolean var4);

   @OnlyIn(Dist.CLIENT)
   public abstract void tick(BooleanSupplier var1);

   public abstract String makeString();

   public void close() throws IOException {
   }

   public abstract WorldLightManager getLightManager();

   public void setAllowedSpawnTypes(boolean p_217203_1_, boolean p_217203_2_) {
   }

   public void forceChunk(ChunkPos p_217206_1_, boolean p_217206_2_) {
   }

   public boolean isChunkLoaded(Entity p_217204_1_) {
      return true;
   }

   public boolean isChunkLoaded(ChunkPos p_222865_1_) {
      return true;
   }

   public boolean canTick(BlockPos p_222866_1_) {
      return true;
   }
}
