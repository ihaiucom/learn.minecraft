package net.minecraft.client.multiplayer;

import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.function.BooleanSupplier;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.SectionPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.LightType;
import net.minecraft.world.biome.BiomeContainer;
import net.minecraft.world.chunk.AbstractChunkProvider;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.EmptyChunk;
import net.minecraft.world.lighting.WorldLightManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.ChunkEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class ClientChunkProvider extends AbstractChunkProvider {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Chunk empty;
   private final WorldLightManager lightManager;
   private volatile ClientChunkProvider.ChunkArray array;
   private final ClientWorld world;

   public ClientChunkProvider(ClientWorld p_i51057_1_, int p_i51057_2_) {
      this.world = p_i51057_1_;
      this.empty = new EmptyChunk(p_i51057_1_, new ChunkPos(0, 0));
      this.lightManager = new WorldLightManager(this, true, p_i51057_1_.getDimension().hasSkyLight());
      this.array = new ClientChunkProvider.ChunkArray(adjustViewDistance(p_i51057_2_));
   }

   public WorldLightManager getLightManager() {
      return this.lightManager;
   }

   private static boolean isValid(@Nullable Chunk p_217249_0_, int p_217249_1_, int p_217249_2_) {
      if (p_217249_0_ == null) {
         return false;
      } else {
         ChunkPos chunkpos = p_217249_0_.getPos();
         return chunkpos.x == p_217249_1_ && chunkpos.z == p_217249_2_;
      }
   }

   public void unloadChunk(int p_73234_1_, int p_73234_2_) {
      if (this.array.inView(p_73234_1_, p_73234_2_)) {
         int i = this.array.getIndex(p_73234_1_, p_73234_2_);
         Chunk chunk = this.array.get(i);
         if (isValid(chunk, p_73234_1_, p_73234_2_)) {
            MinecraftForge.EVENT_BUS.post(new ChunkEvent.Unload(chunk));
            this.array.unload(i, chunk, (Chunk)null);
         }
      }

   }

   @Nullable
   public Chunk getChunk(int p_212849_1_, int p_212849_2_, ChunkStatus p_212849_3_, boolean p_212849_4_) {
      if (this.array.inView(p_212849_1_, p_212849_2_)) {
         Chunk chunk = this.array.get(this.array.getIndex(p_212849_1_, p_212849_2_));
         if (isValid(chunk, p_212849_1_, p_212849_2_)) {
            return chunk;
         }
      }

      return p_212849_4_ ? this.empty : null;
   }

   public IBlockReader getWorld() {
      return this.world;
   }

   @Nullable
   public Chunk func_228313_a_(int p_228313_1_, int p_228313_2_, @Nullable BiomeContainer p_228313_3_, PacketBuffer p_228313_4_, CompoundNBT p_228313_5_, int p_228313_6_) {
      if (!this.array.inView(p_228313_1_, p_228313_2_)) {
         LOGGER.warn("Ignoring chunk since it's not in the view range: {}, {}", p_228313_1_, p_228313_2_);
         return null;
      } else {
         int i = this.array.getIndex(p_228313_1_, p_228313_2_);
         Chunk chunk = (Chunk)this.array.chunks.get(i);
         if (!isValid(chunk, p_228313_1_, p_228313_2_)) {
            if (p_228313_3_ == null) {
               LOGGER.warn("Ignoring chunk since we don't have complete data: {}, {}", p_228313_1_, p_228313_2_);
               return null;
            }

            chunk = new Chunk(this.world, new ChunkPos(p_228313_1_, p_228313_2_), p_228313_3_);
            chunk.func_227073_a_(p_228313_3_, p_228313_4_, p_228313_5_, p_228313_6_);
            this.array.replace(i, chunk);
         } else {
            chunk.func_227073_a_(p_228313_3_, p_228313_4_, p_228313_5_, p_228313_6_);
         }

         ChunkSection[] achunksection = chunk.getSections();
         WorldLightManager worldlightmanager = this.getLightManager();
         worldlightmanager.func_215571_a(new ChunkPos(p_228313_1_, p_228313_2_), true);

         for(int j = 0; j < achunksection.length; ++j) {
            ChunkSection chunksection = achunksection[j];
            worldlightmanager.updateSectionStatus(SectionPos.of(p_228313_1_, j, p_228313_2_), ChunkSection.isEmpty(chunksection));
         }

         this.world.func_228323_e_(p_228313_1_, p_228313_2_);
         MinecraftForge.EVENT_BUS.post(new ChunkEvent.Load(chunk));
         return chunk;
      }
   }

   public void tick(BooleanSupplier p_217207_1_) {
   }

   public void setCenter(int p_217251_1_, int p_217251_2_) {
      this.array.centerX = p_217251_1_;
      this.array.centerZ = p_217251_2_;
   }

   public void setViewDistance(int p_217248_1_) {
      int i = this.array.viewDistance;
      int j = adjustViewDistance(p_217248_1_);
      if (i != j) {
         ClientChunkProvider.ChunkArray clientchunkprovider$chunkarray = new ClientChunkProvider.ChunkArray(j);
         clientchunkprovider$chunkarray.centerX = this.array.centerX;
         clientchunkprovider$chunkarray.centerZ = this.array.centerZ;

         for(int k = 0; k < this.array.chunks.length(); ++k) {
            Chunk chunk = (Chunk)this.array.chunks.get(k);
            if (chunk != null) {
               ChunkPos chunkpos = chunk.getPos();
               if (clientchunkprovider$chunkarray.inView(chunkpos.x, chunkpos.z)) {
                  clientchunkprovider$chunkarray.replace(clientchunkprovider$chunkarray.getIndex(chunkpos.x, chunkpos.z), chunk);
               }
            }
         }

         this.array = clientchunkprovider$chunkarray;
      }

   }

   private static int adjustViewDistance(int p_217254_0_) {
      return Math.max(2, p_217254_0_) + 3;
   }

   public String makeString() {
      return "Client Chunk Cache: " + this.array.chunks.length() + ", " + this.func_217252_g();
   }

   public int func_217252_g() {
      return this.array.loaded;
   }

   public void markLightChanged(LightType p_217201_1_, SectionPos p_217201_2_) {
      Minecraft.getInstance().worldRenderer.markForRerender(p_217201_2_.getSectionX(), p_217201_2_.getSectionY(), p_217201_2_.getSectionZ());
   }

   public boolean canTick(BlockPos p_222866_1_) {
      return this.chunkExists(p_222866_1_.getX() >> 4, p_222866_1_.getZ() >> 4);
   }

   public boolean isChunkLoaded(ChunkPos p_222865_1_) {
      return this.chunkExists(p_222865_1_.x, p_222865_1_.z);
   }

   public boolean isChunkLoaded(Entity p_217204_1_) {
      return this.chunkExists(MathHelper.floor(p_217204_1_.func_226277_ct_()) >> 4, MathHelper.floor(p_217204_1_.func_226281_cx_()) >> 4);
   }

   @OnlyIn(Dist.CLIENT)
   final class ChunkArray {
      private final AtomicReferenceArray<Chunk> chunks;
      private final int viewDistance;
      private final int sideLength;
      private volatile int centerX;
      private volatile int centerZ;
      private int loaded;

      private ChunkArray(int p_i50568_2_) {
         this.viewDistance = p_i50568_2_;
         this.sideLength = p_i50568_2_ * 2 + 1;
         this.chunks = new AtomicReferenceArray(this.sideLength * this.sideLength);
      }

      private int getIndex(int p_217191_1_, int p_217191_2_) {
         return Math.floorMod(p_217191_2_, this.sideLength) * this.sideLength + Math.floorMod(p_217191_1_, this.sideLength);
      }

      protected void replace(int p_217181_1_, @Nullable Chunk p_217181_2_) {
         Chunk chunk = (Chunk)this.chunks.getAndSet(p_217181_1_, p_217181_2_);
         if (chunk != null) {
            --this.loaded;
            ClientChunkProvider.this.world.onChunkUnloaded(chunk);
         }

         if (p_217181_2_ != null) {
            ++this.loaded;
         }

      }

      protected Chunk unload(int p_217190_1_, Chunk p_217190_2_, @Nullable Chunk p_217190_3_) {
         if (this.chunks.compareAndSet(p_217190_1_, p_217190_2_, p_217190_3_) && p_217190_3_ == null) {
            --this.loaded;
         }

         ClientChunkProvider.this.world.onChunkUnloaded(p_217190_2_);
         return p_217190_2_;
      }

      private boolean inView(int p_217183_1_, int p_217183_2_) {
         return Math.abs(p_217183_1_ - this.centerX) <= this.viewDistance && Math.abs(p_217183_2_ - this.centerZ) <= this.viewDistance;
      }

      @Nullable
      protected Chunk get(int p_217192_1_) {
         return (Chunk)this.chunks.get(p_217192_1_);
      }

      // $FF: synthetic method
      ChunkArray(int p_i50569_2_, Object p_i50569_3_) {
         this(p_i50569_2_);
      }
   }
}
