package net.minecraftforge.client.model;

import com.google.common.base.Preconditions;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(
   modid = "forge",
   bus = Mod.EventBusSubscriber.Bus.FORGE,
   value = {Dist.CLIENT}
)
public class ModelDataManager {
   private static WeakReference<World> currentWorld = new WeakReference((Object)null);
   private static final Map<ChunkPos, Set<BlockPos>> needModelDataRefresh = new ConcurrentHashMap();
   private static final Map<ChunkPos, Map<BlockPos, IModelData>> modelDataCache = new ConcurrentHashMap();

   private static void cleanCaches(World world) {
      Preconditions.checkNotNull(world, "World must not be null");
      Preconditions.checkArgument(world == Minecraft.getInstance().world, "Cannot use model data for a world other than the current client world");
      if (world != currentWorld.get()) {
         currentWorld = new WeakReference(world);
         needModelDataRefresh.clear();
         modelDataCache.clear();
      }

   }

   public static void requestModelDataRefresh(TileEntity te) {
      Preconditions.checkNotNull(te, "Tile entity must not be null");
      World world = te.getWorld();
      cleanCaches(world);
      ((Set)needModelDataRefresh.computeIfAbsent(new ChunkPos(te.getPos()), ($) -> {
         return Collections.synchronizedSet(new HashSet());
      })).add(te.getPos());
   }

   private static void refreshModelData(World world, ChunkPos chunk) {
      cleanCaches(world);
      Set<BlockPos> needUpdate = (Set)needModelDataRefresh.remove(chunk);
      if (needUpdate != null) {
         Map<BlockPos, IModelData> data = (Map)modelDataCache.computeIfAbsent(chunk, ($) -> {
            return new ConcurrentHashMap();
         });
         Iterator var4 = needUpdate.iterator();

         while(true) {
            while(var4.hasNext()) {
               BlockPos pos = (BlockPos)var4.next();
               TileEntity toUpdate = world.getTileEntity(pos);
               if (toUpdate != null && !toUpdate.isRemoved()) {
                  data.put(pos, toUpdate.getModelData());
               } else {
                  data.remove(pos);
               }
            }

            return;
         }
      }
   }

   @SubscribeEvent
   public static void onChunkUnload(ChunkEvent.Unload event) {
      if (event.getChunk().getWorldForge().isRemote()) {
         ChunkPos chunk = event.getChunk().getPos();
         needModelDataRefresh.remove(chunk);
         modelDataCache.remove(chunk);
      }
   }

   @Nullable
   public static IModelData getModelData(World world, BlockPos pos) {
      return (IModelData)getModelData(world, new ChunkPos(pos)).get(pos);
   }

   public static Map<BlockPos, IModelData> getModelData(World world, ChunkPos pos) {
      Preconditions.checkArgument(world.isRemote, "Cannot request model data for server world");
      refreshModelData(world, pos);
      return (Map)modelDataCache.getOrDefault(pos, Collections.emptyMap());
   }
}
