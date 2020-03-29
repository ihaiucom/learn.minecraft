package net.minecraftforge.client;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.io.IOException;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.chunk.ChunkRenderCache;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;

public class MinecraftForgeClient {
   private static BitSet stencilBits = new BitSet(8);
   private static final LoadingCache<Pair<World, BlockPos>, ChunkRenderCache> regionCache;
   private static HashMap<ResourceLocation, Supplier<NativeImage>> bufferedImageSuppliers;

   public static RenderType getRenderLayer() {
      return (RenderType)ForgeHooksClient.renderLayer.get();
   }

   public static Locale getLocale() {
      return Minecraft.getInstance().getLanguageManager().getCurrentLanguage().getJavaLocale();
   }

   public static int reserveStencilBit() {
      int bit = stencilBits.nextSetBit(0);
      if (bit >= 0) {
         stencilBits.clear(bit);
      }

      return bit;
   }

   public static void releaseStencilBit(int bit) {
      if (bit >= 0 && bit < stencilBits.length()) {
         stencilBits.set(bit);
      }

   }

   public static void onRebuildChunk(World world, BlockPos position, ChunkRenderCache cache) {
      if (cache == null) {
         regionCache.invalidate(Pair.of(world, position));
      } else {
         regionCache.put(Pair.of(world, position), cache);
      }

   }

   public static ChunkRenderCache getRegionRenderCache(World world, BlockPos pos) {
      int x = pos.getX() & -16;
      int y = pos.getY() & -16;
      int z = pos.getZ() & -16;
      return (ChunkRenderCache)regionCache.getUnchecked(Pair.of(world, new BlockPos(x, y, z)));
   }

   public static void clearRenderCache() {
      regionCache.invalidateAll();
      regionCache.cleanUp();
   }

   public static void registerImageLayerSupplier(ResourceLocation resourceLocation, Supplier<NativeImage> supplier) {
      bufferedImageSuppliers.put(resourceLocation, supplier);
   }

   @Nonnull
   public static NativeImage getImageLayer(ResourceLocation resourceLocation, IResourceManager resourceManager) throws IOException {
      Supplier<NativeImage> supplier = (Supplier)bufferedImageSuppliers.get(resourceLocation);
      if (supplier != null) {
         return (NativeImage)supplier.get();
      } else {
         IResource iresource1 = resourceManager.getResource(resourceLocation);
         return NativeImage.read(iresource1.getInputStream());
      }
   }

   static {
      stencilBits.set(0, 8);
      regionCache = CacheBuilder.newBuilder().maximumSize(500L).concurrencyLevel(5).expireAfterAccess(1L, TimeUnit.SECONDS).build(new CacheLoader<Pair<World, BlockPos>, ChunkRenderCache>() {
         public ChunkRenderCache load(Pair<World, BlockPos> key) {
            return ChunkRenderCache.generateCache((World)key.getLeft(), ((BlockPos)key.getRight()).add(-1, -1, -1), ((BlockPos)key.getRight()).add(16, 16, 16), 1);
         }
      });
      bufferedImageSuppliers = new HashMap();
   }
}
