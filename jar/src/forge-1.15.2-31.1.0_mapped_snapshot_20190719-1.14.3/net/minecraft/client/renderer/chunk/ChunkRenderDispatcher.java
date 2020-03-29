package net.minecraft.client.renderer.chunk;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.google.common.primitives.Doubles;
import com.mojang.blaze3d.matrix.MatrixStack;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BlockModelRenderer;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RegionRenderCacheBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.crash.CrashReport;
import net.minecraft.fluid.IFluidState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Util;
import net.minecraft.util.concurrent.DelegatedTaskExecutor;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.extensions.IForgeRenderChunk;
import net.minecraftforge.client.model.ModelDataManager;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class ChunkRenderDispatcher {
   private static final Logger LOGGER = LogManager.getLogger();
   private final PriorityQueue<ChunkRenderDispatcher.ChunkRender.ChunkRenderTask> field_228885_b_;
   private final Queue<RegionRenderCacheBuilder> field_228886_c_;
   private final Queue<Runnable> field_228887_d_;
   private volatile int field_228888_e_;
   private volatile int field_228889_f_;
   private final RegionRenderCacheBuilder field_228890_g_;
   private final DelegatedTaskExecutor<Runnable> field_228891_h_;
   private final Executor field_228892_i_;
   private World field_228893_j_;
   private final WorldRenderer field_228894_k_;
   private Vec3d field_217672_l;

   public ChunkRenderDispatcher(World p_i226020_1_, WorldRenderer p_i226020_2_, Executor p_i226020_3_, boolean p_i226020_4_, RegionRenderCacheBuilder p_i226020_5_) {
      this(p_i226020_1_, p_i226020_2_, p_i226020_3_, p_i226020_4_, p_i226020_5_, -1);
   }

   public ChunkRenderDispatcher(World p_i230088_1_, WorldRenderer p_i230088_2_, Executor p_i230088_3_, boolean p_i230088_4_, RegionRenderCacheBuilder p_i230088_5_, int p_i230088_6_) {
      this.field_228885_b_ = Queues.newPriorityQueue();
      this.field_228887_d_ = Queues.newConcurrentLinkedQueue();
      this.field_217672_l = Vec3d.ZERO;
      this.field_228893_j_ = p_i230088_1_;
      this.field_228894_k_ = p_i230088_2_;
      int i = Math.max(1, (int)((double)Runtime.getRuntime().maxMemory() * 0.3D) / (RenderType.func_228661_n_().stream().mapToInt(RenderType::func_228662_o_).sum() * 4) - 1);
      int j = Runtime.getRuntime().availableProcessors();
      int k = p_i230088_4_ ? j : Math.min(j, 4);
      int l = p_i230088_6_ < 0 ? Math.max(1, Math.min(k, i)) : p_i230088_6_;
      this.field_228890_g_ = p_i230088_5_;
      ArrayList list = Lists.newArrayListWithExpectedSize(l);

      try {
         for(int i1 = 0; i1 < l; ++i1) {
            list.add(new RegionRenderCacheBuilder());
         }
      } catch (OutOfMemoryError var15) {
         LOGGER.warn("Allocated only {}/{} buffers", list.size(), l);
         int j1 = Math.min(list.size() * 2 / 3, list.size() - 1);

         for(int k1 = 0; k1 < j1; ++k1) {
            list.remove(list.size() - 1);
         }

         System.gc();
      }

      this.field_228886_c_ = Queues.newArrayDeque(list);
      this.field_228889_f_ = this.field_228886_c_.size();
      this.field_228892_i_ = p_i230088_3_;
      this.field_228891_h_ = DelegatedTaskExecutor.create(p_i230088_3_, "Chunk Renderer");
      this.field_228891_h_.enqueue(this::func_228909_h_);
   }

   public void func_228895_a_(World p_228895_1_) {
      this.field_228893_j_ = p_228895_1_;
   }

   private void func_228909_h_() {
      if (!this.field_228886_c_.isEmpty()) {
         ChunkRenderDispatcher.ChunkRender.ChunkRenderTask chunkrenderdispatcher$chunkrender$chunkrendertask = (ChunkRenderDispatcher.ChunkRender.ChunkRenderTask)this.field_228885_b_.poll();
         if (chunkrenderdispatcher$chunkrender$chunkrendertask != null) {
            RegionRenderCacheBuilder regionrendercachebuilder = (RegionRenderCacheBuilder)this.field_228886_c_.poll();
            this.field_228888_e_ = this.field_228885_b_.size();
            this.field_228889_f_ = this.field_228886_c_.size();
            CompletableFuture.runAsync(() -> {
            }, this.field_228892_i_).thenCompose((p_lambda$func_228909_h_$1_2_) -> {
               return chunkrenderdispatcher$chunkrender$chunkrendertask.func_225618_a_(regionrendercachebuilder);
            }).whenComplete((p_lambda$func_228909_h_$3_2_, p_lambda$func_228909_h_$3_3_) -> {
               if (p_lambda$func_228909_h_$3_3_ != null) {
                  CrashReport crashreport = CrashReport.makeCrashReport(p_lambda$func_228909_h_$3_3_, "Batching chunks");
                  Minecraft.getInstance().crashed(Minecraft.getInstance().addGraphicsAndWorldToCrashReport(crashreport));
               } else {
                  this.field_228891_h_.enqueue(() -> {
                     if (p_lambda$func_228909_h_$3_2_ == ChunkRenderDispatcher.ChunkTaskResult.SUCCESSFUL) {
                        regionrendercachebuilder.func_228365_a_();
                     } else {
                        regionrendercachebuilder.func_228367_b_();
                     }

                     this.field_228886_c_.add(regionrendercachebuilder);
                     this.field_228889_f_ = this.field_228886_c_.size();
                     this.func_228909_h_();
                  });
               }

            });
         }
      }

   }

   public String getDebugInfo() {
      return String.format("pC: %03d, pU: %02d, aB: %02d", this.field_228888_e_, this.field_228887_d_.size(), this.field_228889_f_);
   }

   public void func_217669_a(Vec3d p_217669_1_) {
      this.field_217672_l = p_217669_1_;
   }

   public Vec3d func_217671_b() {
      return this.field_217672_l;
   }

   public boolean func_228908_d_() {
      boolean flag;
      Runnable runnable;
      for(flag = false; (runnable = (Runnable)this.field_228887_d_.poll()) != null; flag = true) {
         runnable.run();
      }

      return flag;
   }

   public void func_228902_a_(ChunkRenderDispatcher.ChunkRender p_228902_1_) {
      p_228902_1_.func_228936_k_();
   }

   public void stopChunkUpdates() {
      this.clearChunkUpdates();
   }

   public void func_228900_a_(ChunkRenderDispatcher.ChunkRender.ChunkRenderTask p_228900_1_) {
      this.field_228891_h_.enqueue(() -> {
         this.field_228885_b_.offer(p_228900_1_);
         this.field_228888_e_ = this.field_228885_b_.size();
         this.func_228909_h_();
      });
   }

   public CompletableFuture<Void> func_228896_a_(BufferBuilder p_228896_1_, VertexBuffer p_228896_2_) {
      Runnable var10000 = () -> {
      };
      Queue var10001 = this.field_228887_d_;
      var10001.getClass();
      return CompletableFuture.runAsync(var10000, var10001::add).thenCompose((p_lambda$func_228896_a_$6_3_) -> {
         return this.func_228904_b_(p_228896_1_, p_228896_2_);
      });
   }

   private CompletableFuture<Void> func_228904_b_(BufferBuilder p_228904_1_, VertexBuffer p_228904_2_) {
      return p_228904_2_.func_227878_b_(p_228904_1_);
   }

   private void clearChunkUpdates() {
      while(!this.field_228885_b_.isEmpty()) {
         ChunkRenderDispatcher.ChunkRender.ChunkRenderTask chunkrenderdispatcher$chunkrender$chunkrendertask = (ChunkRenderDispatcher.ChunkRender.ChunkRenderTask)this.field_228885_b_.poll();
         if (chunkrenderdispatcher$chunkrender$chunkrendertask != null) {
            chunkrenderdispatcher$chunkrender$chunkrendertask.func_225617_a_();
         }
      }

      this.field_228888_e_ = 0;
   }

   public boolean hasNoChunkUpdates() {
      return this.field_228888_e_ == 0 && this.field_228887_d_.isEmpty();
   }

   public void stopWorkerThreads() {
      this.clearChunkUpdates();
      this.field_228891_h_.close();
      this.field_228886_c_.clear();
   }

   @OnlyIn(Dist.CLIENT)
   public static class CompiledChunk {
      public static final ChunkRenderDispatcher.CompiledChunk DUMMY = new ChunkRenderDispatcher.CompiledChunk() {
         public boolean isVisible(Direction p_178495_1_, Direction p_178495_2_) {
            return false;
         }
      };
      private final Set<RenderType> layersUsed = new ObjectArraySet();
      private final Set<RenderType> layersStarted = new ObjectArraySet();
      private boolean empty = true;
      private final List<TileEntity> tileEntities = Lists.newArrayList();
      private SetVisibility setVisibility = new SetVisibility();
      @Nullable
      private BufferBuilder.State state;

      public boolean isEmpty() {
         return this.empty;
      }

      public boolean func_228912_a_(RenderType p_228912_1_) {
         return !this.layersUsed.contains(p_228912_1_);
      }

      public List<TileEntity> getTileEntities() {
         return this.tileEntities;
      }

      public boolean isVisible(Direction p_178495_1_, Direction p_178495_2_) {
         return this.setVisibility.isVisible(p_178495_1_, p_178495_2_);
      }
   }

   @OnlyIn(Dist.CLIENT)
   static enum ChunkTaskResult {
      SUCCESSFUL,
      CANCELLED;
   }

   @OnlyIn(Dist.CLIENT)
   public class ChunkRender implements IForgeRenderChunk {
      public final AtomicReference<ChunkRenderDispatcher.CompiledChunk> compiledChunk;
      @Nullable
      private ChunkRenderDispatcher.ChunkRender.RebuildTask field_228921_d_;
      @Nullable
      private ChunkRenderDispatcher.ChunkRender.SortTransparencyTask field_228922_e_;
      private final Set<TileEntity> setTileEntities;
      private final Map<RenderType, VertexBuffer> vertexBuffers;
      public AxisAlignedBB boundingBox;
      private int frameIndex;
      private boolean needsUpdate;
      private final BlockPos.Mutable position;
      private final BlockPos.Mutable[] mapEnumFacing;
      private boolean needsImmediateUpdate;

      public ChunkRender() {
         this.compiledChunk = new AtomicReference(ChunkRenderDispatcher.CompiledChunk.DUMMY);
         this.setTileEntities = Sets.newHashSet();
         this.vertexBuffers = (Map)RenderType.func_228661_n_().stream().collect(Collectors.toMap((p_lambda$new$0_0_) -> {
            return p_lambda$new$0_0_;
         }, (p_lambda$new$1_0_) -> {
            return new VertexBuffer(DefaultVertexFormats.BLOCK);
         }));
         this.frameIndex = -1;
         this.needsUpdate = true;
         this.position = new BlockPos.Mutable(-1, -1, -1);
         this.mapEnumFacing = (BlockPos.Mutable[])Util.make(new BlockPos.Mutable[6], (p_lambda$new$2_0_) -> {
            for(int i = 0; i < p_lambda$new$2_0_.length; ++i) {
               p_lambda$new$2_0_[i] = new BlockPos.Mutable();
            }

         });
      }

      private boolean func_228930_a_(BlockPos p_228930_1_) {
         return ChunkRenderDispatcher.this.field_228893_j_.getChunk(p_228930_1_.getX() >> 4, p_228930_1_.getZ() >> 4, ChunkStatus.FULL, false) != null;
      }

      public boolean shouldStayLoaded() {
         int i = true;
         if (this.getDistanceSq() <= 576.0D) {
            return true;
         } else {
            return this.func_228930_a_(this.mapEnumFacing[Direction.WEST.ordinal()]) && this.func_228930_a_(this.mapEnumFacing[Direction.NORTH.ordinal()]) && this.func_228930_a_(this.mapEnumFacing[Direction.EAST.ordinal()]) && this.func_228930_a_(this.mapEnumFacing[Direction.SOUTH.ordinal()]);
         }
      }

      public boolean setFrameIndex(int p_178577_1_) {
         if (this.frameIndex == p_178577_1_) {
            return false;
         } else {
            this.frameIndex = p_178577_1_;
            return true;
         }
      }

      public VertexBuffer func_228924_a_(RenderType p_228924_1_) {
         return (VertexBuffer)this.vertexBuffers.get(p_228924_1_);
      }

      public void setPosition(int p_189562_1_, int p_189562_2_, int p_189562_3_) {
         if (p_189562_1_ != this.position.getX() || p_189562_2_ != this.position.getY() || p_189562_3_ != this.position.getZ()) {
            this.stopCompileTask();
            this.position.setPos(p_189562_1_, p_189562_2_, p_189562_3_);
            this.boundingBox = new AxisAlignedBB((double)p_189562_1_, (double)p_189562_2_, (double)p_189562_3_, (double)(p_189562_1_ + 16), (double)(p_189562_2_ + 16), (double)(p_189562_3_ + 16));
            Direction[] var4 = Direction.values();
            int var5 = var4.length;

            for(int var6 = 0; var6 < var5; ++var6) {
               Direction direction = var4[var6];
               this.mapEnumFacing[direction.ordinal()].setPos((Vec3i)this.position).move(direction, 16);
            }
         }

      }

      protected double getDistanceSq() {
         ActiveRenderInfo activerenderinfo = Minecraft.getInstance().gameRenderer.getActiveRenderInfo();
         double d0 = this.boundingBox.minX + 8.0D - activerenderinfo.getProjectedView().x;
         double d1 = this.boundingBox.minY + 8.0D - activerenderinfo.getProjectedView().y;
         double d2 = this.boundingBox.minZ + 8.0D - activerenderinfo.getProjectedView().z;
         return d0 * d0 + d1 * d1 + d2 * d2;
      }

      private void func_228923_a_(BufferBuilder p_228923_1_) {
         p_228923_1_.begin(7, DefaultVertexFormats.BLOCK);
      }

      public ChunkRenderDispatcher.CompiledChunk getCompiledChunk() {
         return (ChunkRenderDispatcher.CompiledChunk)this.compiledChunk.get();
      }

      private void stopCompileTask() {
         this.func_228935_i_();
         this.compiledChunk.set(ChunkRenderDispatcher.CompiledChunk.DUMMY);
         this.needsUpdate = true;
      }

      public void deleteGlResources() {
         this.stopCompileTask();
         this.vertexBuffers.values().forEach(VertexBuffer::close);
      }

      public BlockPos getPosition() {
         return this.position;
      }

      public void setNeedsUpdate(boolean p_178575_1_) {
         boolean flag = this.needsUpdate;
         this.needsUpdate = true;
         this.needsImmediateUpdate = p_178575_1_ | (flag && this.needsImmediateUpdate);
      }

      public void clearNeedsUpdate() {
         this.needsUpdate = false;
         this.needsImmediateUpdate = false;
      }

      public boolean needsUpdate() {
         return this.needsUpdate;
      }

      public boolean needsImmediateUpdate() {
         return this.needsUpdate && this.needsImmediateUpdate;
      }

      public BlockPos getBlockPosOffset16(Direction p_181701_1_) {
         return this.mapEnumFacing[p_181701_1_.ordinal()];
      }

      public boolean func_228925_a_(RenderType p_228925_1_, ChunkRenderDispatcher p_228925_2_) {
         ChunkRenderDispatcher.CompiledChunk chunkrenderdispatcher$compiledchunk = this.getCompiledChunk();
         if (this.field_228922_e_ != null) {
            this.field_228922_e_.func_225617_a_();
         }

         if (!chunkrenderdispatcher$compiledchunk.layersStarted.contains(p_228925_1_)) {
            return false;
         } else {
            this.field_228922_e_ = new ChunkRenderDispatcher.ChunkRender.SortTransparencyTask(new ChunkPos(this.getPosition()), this.getDistanceSq(), chunkrenderdispatcher$compiledchunk);
            p_228925_2_.func_228900_a_(this.field_228922_e_);
            return true;
         }
      }

      protected void func_228935_i_() {
         if (this.field_228921_d_ != null) {
            this.field_228921_d_.func_225617_a_();
            this.field_228921_d_ = null;
         }

         if (this.field_228922_e_ != null) {
            this.field_228922_e_.func_225617_a_();
            this.field_228922_e_ = null;
         }

      }

      public ChunkRenderDispatcher.ChunkRender.ChunkRenderTask makeCompileTaskChunk() {
         this.func_228935_i_();
         BlockPos blockpos = this.position.toImmutable();
         int i = true;
         ChunkRenderCache chunkrendercache = this.createRegionRenderCache(ChunkRenderDispatcher.this.field_228893_j_, blockpos.add(-1, -1, -1), blockpos.add(16, 16, 16), 1);
         this.field_228921_d_ = new ChunkRenderDispatcher.ChunkRender.RebuildTask(new ChunkPos(this.getPosition()), this.getDistanceSq(), chunkrendercache);
         return this.field_228921_d_;
      }

      public void func_228929_a_(ChunkRenderDispatcher p_228929_1_) {
         ChunkRenderDispatcher.ChunkRender.ChunkRenderTask chunkrenderdispatcher$chunkrender$chunkrendertask = this.makeCompileTaskChunk();
         p_228929_1_.func_228900_a_(chunkrenderdispatcher$chunkrender$chunkrendertask);
      }

      private void func_228931_a_(Set<TileEntity> p_228931_1_) {
         Set<TileEntity> set = Sets.newHashSet(p_228931_1_);
         Set<TileEntity> set1 = Sets.newHashSet(this.setTileEntities);
         set.removeAll(this.setTileEntities);
         set1.removeAll(p_228931_1_);
         this.setTileEntities.clear();
         this.setTileEntities.addAll(p_228931_1_);
         ChunkRenderDispatcher.this.field_228894_k_.updateTileEntities(set1, set);
      }

      public void func_228936_k_() {
         ChunkRenderDispatcher.ChunkRender.ChunkRenderTask chunkrenderdispatcher$chunkrender$chunkrendertask = this.makeCompileTaskChunk();
         chunkrenderdispatcher$chunkrender$chunkrendertask.func_225618_a_(ChunkRenderDispatcher.this.field_228890_g_);
      }

      @OnlyIn(Dist.CLIENT)
      class SortTransparencyTask extends ChunkRenderDispatcher.ChunkRender.ChunkRenderTask {
         private final ChunkRenderDispatcher.CompiledChunk field_228945_e_;

         /** @deprecated */
         @Deprecated
         public SortTransparencyTask(double p_i226025_2_, ChunkRenderDispatcher.CompiledChunk p_i226025_4_) {
            this((ChunkPos)null, p_i226025_2_, p_i226025_4_);
         }

         public SortTransparencyTask(ChunkPos p_i230096_2_, double p_i230096_3_, ChunkRenderDispatcher.CompiledChunk p_i230096_5_) {
            super(p_i230096_2_, p_i230096_3_);
            this.field_228945_e_ = p_i230096_5_;
         }

         public CompletableFuture<ChunkRenderDispatcher.ChunkTaskResult> func_225618_a_(RegionRenderCacheBuilder p_225618_1_) {
            if (this.finished.get()) {
               return CompletableFuture.completedFuture(ChunkRenderDispatcher.ChunkTaskResult.CANCELLED);
            } else if (!ChunkRender.this.shouldStayLoaded()) {
               this.finished.set(true);
               return CompletableFuture.completedFuture(ChunkRenderDispatcher.ChunkTaskResult.CANCELLED);
            } else if (this.finished.get()) {
               return CompletableFuture.completedFuture(ChunkRenderDispatcher.ChunkTaskResult.CANCELLED);
            } else {
               Vec3d vec3d = ChunkRenderDispatcher.this.func_217671_b();
               float f = (float)vec3d.x;
               float f1 = (float)vec3d.y;
               float f2 = (float)vec3d.z;
               BufferBuilder.State bufferbuilder$state = this.field_228945_e_.state;
               if (bufferbuilder$state != null && this.field_228945_e_.layersUsed.contains(RenderType.func_228645_f_())) {
                  BufferBuilder bufferbuilder = p_225618_1_.func_228366_a_(RenderType.func_228645_f_());
                  ChunkRender.this.func_228923_a_(bufferbuilder);
                  bufferbuilder.setVertexState(bufferbuilder$state);
                  bufferbuilder.sortVertexData(f - (float)ChunkRender.this.position.getX(), f1 - (float)ChunkRender.this.position.getY(), f2 - (float)ChunkRender.this.position.getZ());
                  this.field_228945_e_.state = bufferbuilder.getVertexState();
                  bufferbuilder.finishDrawing();
                  if (this.finished.get()) {
                     return CompletableFuture.completedFuture(ChunkRenderDispatcher.ChunkTaskResult.CANCELLED);
                  } else {
                     CompletableFuture<ChunkRenderDispatcher.ChunkTaskResult> completablefuture = ChunkRenderDispatcher.this.func_228896_a_(p_225618_1_.func_228366_a_(RenderType.func_228645_f_()), ChunkRender.this.func_228924_a_(RenderType.func_228645_f_())).thenApply((p_lambda$func_225618_a_$0_0_) -> {
                        return ChunkRenderDispatcher.ChunkTaskResult.CANCELLED;
                     });
                     return completablefuture.handle((p_lambda$func_225618_a_$1_1_, p_lambda$func_225618_a_$1_2_) -> {
                        if (p_lambda$func_225618_a_$1_2_ != null && !(p_lambda$func_225618_a_$1_2_ instanceof CancellationException) && !(p_lambda$func_225618_a_$1_2_ instanceof InterruptedException)) {
                           Minecraft.getInstance().crashed(CrashReport.makeCrashReport(p_lambda$func_225618_a_$1_2_, "Rendering chunk"));
                        }

                        return this.finished.get() ? ChunkRenderDispatcher.ChunkTaskResult.CANCELLED : ChunkRenderDispatcher.ChunkTaskResult.SUCCESSFUL;
                     });
                  }
               } else {
                  return CompletableFuture.completedFuture(ChunkRenderDispatcher.ChunkTaskResult.CANCELLED);
               }
            }
         }

         public void func_225617_a_() {
            this.finished.set(true);
         }
      }

      @OnlyIn(Dist.CLIENT)
      class RebuildTask extends ChunkRenderDispatcher.ChunkRender.ChunkRenderTask {
         @Nullable
         protected ChunkRenderCache field_228938_d_;

         /** @deprecated */
         @Deprecated
         public RebuildTask(@Nullable double p_i226024_2_, ChunkRenderCache p_i226024_4_) {
            this((ChunkPos)null, p_i226024_2_, p_i226024_4_);
         }

         public RebuildTask(ChunkPos p_i230097_2_, @Nullable double p_i230097_3_, ChunkRenderCache p_i230097_5_) {
            super(p_i230097_2_, p_i230097_3_);
            this.field_228938_d_ = p_i230097_5_;
         }

         public CompletableFuture<ChunkRenderDispatcher.ChunkTaskResult> func_225618_a_(RegionRenderCacheBuilder p_225618_1_) {
            if (this.finished.get()) {
               return CompletableFuture.completedFuture(ChunkRenderDispatcher.ChunkTaskResult.CANCELLED);
            } else if (!ChunkRender.this.shouldStayLoaded()) {
               this.field_228938_d_ = null;
               ChunkRender.this.setNeedsUpdate(false);
               this.finished.set(true);
               return CompletableFuture.completedFuture(ChunkRenderDispatcher.ChunkTaskResult.CANCELLED);
            } else if (this.finished.get()) {
               return CompletableFuture.completedFuture(ChunkRenderDispatcher.ChunkTaskResult.CANCELLED);
            } else {
               Vec3d vec3d = ChunkRenderDispatcher.this.func_217671_b();
               float f = (float)vec3d.x;
               float f1 = (float)vec3d.y;
               float f2 = (float)vec3d.z;
               ChunkRenderDispatcher.CompiledChunk chunkrenderdispatcher$compiledchunk = new ChunkRenderDispatcher.CompiledChunk();
               Set<TileEntity> set = this.func_228940_a_(f, f1, f2, chunkrenderdispatcher$compiledchunk, p_225618_1_);
               ChunkRender.this.func_228931_a_(set);
               if (this.finished.get()) {
                  return CompletableFuture.completedFuture(ChunkRenderDispatcher.ChunkTaskResult.CANCELLED);
               } else {
                  List<CompletableFuture<Void>> list = Lists.newArrayList();
                  chunkrenderdispatcher$compiledchunk.layersStarted.forEach((p_lambda$func_225618_a_$0_3_) -> {
                     list.add(ChunkRenderDispatcher.this.func_228896_a_(p_225618_1_.func_228366_a_(p_lambda$func_225618_a_$0_3_), ChunkRender.this.func_228924_a_(p_lambda$func_225618_a_$0_3_)));
                  });
                  return Util.gather(list).handle((p_lambda$func_225618_a_$1_2_, p_lambda$func_225618_a_$1_3_) -> {
                     if (p_lambda$func_225618_a_$1_3_ != null && !(p_lambda$func_225618_a_$1_3_ instanceof CancellationException) && !(p_lambda$func_225618_a_$1_3_ instanceof InterruptedException)) {
                        Minecraft.getInstance().crashed(CrashReport.makeCrashReport(p_lambda$func_225618_a_$1_3_, "Rendering chunk"));
                     }

                     if (this.finished.get()) {
                        return ChunkRenderDispatcher.ChunkTaskResult.CANCELLED;
                     } else {
                        ChunkRender.this.compiledChunk.set(chunkrenderdispatcher$compiledchunk);
                        return ChunkRenderDispatcher.ChunkTaskResult.SUCCESSFUL;
                     }
                  });
               }
            }
         }

         private Set<TileEntity> func_228940_a_(float p_228940_1_, float p_228940_2_, float p_228940_3_, ChunkRenderDispatcher.CompiledChunk p_228940_4_, RegionRenderCacheBuilder p_228940_5_) {
            int i = true;
            BlockPos blockpos = ChunkRender.this.position.toImmutable();
            BlockPos blockpos1 = blockpos.add(15, 15, 15);
            VisGraph visgraph = new VisGraph();
            Set<TileEntity> set = Sets.newHashSet();
            ChunkRenderCache chunkrendercache = this.field_228938_d_;
            this.field_228938_d_ = null;
            MatrixStack matrixstack = new MatrixStack();
            if (chunkrendercache != null) {
               BlockModelRenderer.enableCache();
               Random random = new Random();
               BlockRendererDispatcher blockrendererdispatcher = Minecraft.getInstance().getBlockRendererDispatcher();
               Iterator var15 = BlockPos.getAllInBoxMutable(blockpos, blockpos1).iterator();

               while(var15.hasNext()) {
                  BlockPos blockpos2 = (BlockPos)var15.next();
                  BlockState blockstate = chunkrendercache.getBlockState(blockpos2);
                  Block block = blockstate.getBlock();
                  if (blockstate.isOpaqueCube(chunkrendercache, blockpos2)) {
                     visgraph.setOpaqueCube(blockpos2);
                  }

                  if (blockstate.hasTileEntity()) {
                     TileEntity tileentity = chunkrendercache.getTileEntity(blockpos2, Chunk.CreateEntityType.CHECK);
                     if (tileentity != null) {
                        this.func_228942_a_(p_228940_4_, set, tileentity);
                     }
                  }

                  IFluidState ifluidstate = chunkrendercache.getFluidState(blockpos2);
                  IModelData modelData = this.getModelData(blockpos2);
                  Iterator var21 = RenderType.func_228661_n_().iterator();

                  while(var21.hasNext()) {
                     RenderType rendertype = (RenderType)var21.next();
                     ForgeHooksClient.setRenderLayer(rendertype);
                     if (!ifluidstate.isEmpty() && RenderTypeLookup.canRenderInLayer(ifluidstate, rendertype)) {
                        BufferBuilder bufferbuilder = p_228940_5_.func_228366_a_(rendertype);
                        if (p_228940_4_.layersStarted.add(rendertype)) {
                           ChunkRender.this.func_228923_a_(bufferbuilder);
                        }

                        if (blockrendererdispatcher.func_228794_a_(blockpos2, chunkrendercache, bufferbuilder, ifluidstate)) {
                           p_228940_4_.empty = false;
                           p_228940_4_.layersUsed.add(rendertype);
                        }
                     }

                     if (blockstate.getRenderType() != BlockRenderType.INVISIBLE && RenderTypeLookup.canRenderInLayer(blockstate, rendertype)) {
                        BufferBuilder bufferbuilder2 = p_228940_5_.func_228366_a_(rendertype);
                        if (p_228940_4_.layersStarted.add(rendertype)) {
                           ChunkRender.this.func_228923_a_(bufferbuilder2);
                        }

                        matrixstack.func_227860_a_();
                        matrixstack.func_227861_a_((double)(blockpos2.getX() & 15), (double)(blockpos2.getY() & 15), (double)(blockpos2.getZ() & 15));
                        if (blockrendererdispatcher.renderModel(blockstate, blockpos2, chunkrendercache, matrixstack, bufferbuilder2, true, random, modelData)) {
                           p_228940_4_.empty = false;
                           p_228940_4_.layersUsed.add(rendertype);
                        }

                        matrixstack.func_227865_b_();
                     }
                  }
               }

               ForgeHooksClient.setRenderLayer((RenderType)null);
               if (p_228940_4_.layersUsed.contains(RenderType.func_228645_f_())) {
                  BufferBuilder bufferbuilder1 = p_228940_5_.func_228366_a_(RenderType.func_228645_f_());
                  bufferbuilder1.sortVertexData(p_228940_1_ - (float)blockpos.getX(), p_228940_2_ - (float)blockpos.getY(), p_228940_3_ - (float)blockpos.getZ());
                  p_228940_4_.state = bufferbuilder1.getVertexState();
               }

               Stream var10000 = p_228940_4_.layersStarted.stream();
               p_228940_5_.getClass();
               var10000.map(p_228940_5_::func_228366_a_).forEach(BufferBuilder::finishDrawing);
               BlockModelRenderer.disableCache();
            }

            p_228940_4_.setVisibility = visgraph.computeVisibility();
            return set;
         }

         private <E extends TileEntity> void func_228942_a_(ChunkRenderDispatcher.CompiledChunk p_228942_1_, Set<TileEntity> p_228942_2_, E p_228942_3_) {
            TileEntityRenderer<E> tileentityrenderer = TileEntityRendererDispatcher.instance.getRenderer(p_228942_3_);
            if (tileentityrenderer != null) {
               if (tileentityrenderer.isGlobalRenderer(p_228942_3_)) {
                  p_228942_2_.add(p_228942_3_);
               } else {
                  p_228942_1_.tileEntities.add(p_228942_3_);
               }
            }

         }

         public void func_225617_a_() {
            this.field_228938_d_ = null;
            if (this.finished.compareAndSet(false, true)) {
               ChunkRender.this.setNeedsUpdate(false);
            }

         }
      }

      @OnlyIn(Dist.CLIENT)
      abstract class ChunkRenderTask implements Comparable<ChunkRenderDispatcher.ChunkRender.ChunkRenderTask> {
         protected final double distanceSq;
         protected final AtomicBoolean finished;
         protected Map<BlockPos, IModelData> modelData;

         public ChunkRenderTask(double p_i226023_2_) {
            this((ChunkPos)null, p_i226023_2_);
         }

         public ChunkRenderTask(ChunkPos p_i230098_2_, double p_i230098_3_) {
            this.finished = new AtomicBoolean(false);
            this.distanceSq = p_i230098_3_;
            if (p_i230098_2_ == null) {
               this.modelData = Collections.emptyMap();
            } else {
               this.modelData = ModelDataManager.getModelData(Minecraft.getInstance().world, (ChunkPos)p_i230098_2_);
            }

         }

         public abstract CompletableFuture<ChunkRenderDispatcher.ChunkTaskResult> func_225618_a_(RegionRenderCacheBuilder var1);

         public abstract void func_225617_a_();

         public int compareTo(ChunkRenderDispatcher.ChunkRender.ChunkRenderTask p_compareTo_1_) {
            return Doubles.compare(this.distanceSq, p_compareTo_1_.distanceSq);
         }

         public IModelData getModelData(BlockPos p_getModelData_1_) {
            return (IModelData)this.modelData.getOrDefault(p_getModelData_1_, EmptyModelData.INSTANCE);
         }
      }
   }
}
