package net.minecraftforge.common;

import com.google.common.base.Preconditions;
import com.google.common.collect.MapMaker;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunk;
import net.minecraftforge.common.ticket.AABBTicket;
import net.minecraftforge.common.ticket.ChunkTicketManager;
import net.minecraftforge.common.ticket.ITicketManager;
import net.minecraftforge.common.ticket.SimpleTicket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FarmlandWaterManager {
   private static boolean DEBUG = Boolean.parseBoolean(System.getProperty("forge.debugFarmlandWaterManager", "false"));
   private static final Int2ObjectMap<Map<ChunkPos, ChunkTicketManager<Vec3d>>> customWaterHandler = new Int2ObjectOpenHashMap();
   private static final Logger LOGGER = LogManager.getLogger();

   public static <T extends SimpleTicket<Vec3d>> T addCustomTicket(World world, T ticket, ChunkPos masterChunk, ChunkPos... additionalChunks) {
      Preconditions.checkArgument(!world.isRemote, "Water region is only determined server-side");
      Map<ChunkPos, ChunkTicketManager<Vec3d>> ticketMap = (Map)customWaterHandler.computeIfAbsent(world.getDimension().getType().getId(), (id) -> {
         return (new MapMaker()).weakValues().makeMap();
      });
      ChunkTicketManager<Vec3d>[] additionalTickets = new ChunkTicketManager[additionalChunks.length];

      for(int i = 0; i < additionalChunks.length; ++i) {
         additionalTickets[i] = (ChunkTicketManager)ticketMap.computeIfAbsent(additionalChunks[i], ChunkTicketManager::new);
      }

      ticket.setManager((ITicketManager)ticketMap.computeIfAbsent(masterChunk, ChunkTicketManager::new), additionalTickets);
      ticket.validate();
      return ticket;
   }

   public static AABBTicket addAABBTicket(World world, AxisAlignedBB aabb) {
      if (DEBUG) {
         LOGGER.info("FarmlandWaterManager: New AABBTicket, aabb={}", aabb);
      }

      ChunkPos leftUp = new ChunkPos((int)aabb.minX >> 4, (int)aabb.minZ >> 4);
      ChunkPos rightDown = new ChunkPos((int)aabb.maxX >> 4, (int)aabb.maxZ >> 4);
      Set<ChunkPos> posSet = new HashSet();

      for(int x = leftUp.x; x <= rightDown.x; ++x) {
         for(int z = leftUp.z; z <= rightDown.z; ++z) {
            posSet.add(new ChunkPos(x, z));
         }
      }

      ChunkPos masterPos = null;
      double masterDistance = Double.MAX_VALUE;
      Iterator var8 = posSet.iterator();

      while(var8.hasNext()) {
         ChunkPos pos = (ChunkPos)var8.next();
         double distToCenter = getDistanceSq(pos, aabb.getCenter());
         if (distToCenter < masterDistance) {
            if (DEBUG) {
               LOGGER.info("FarmlandWaterManager: New better pos then {}: {}, prev dist {}, new dist {}", masterPos, pos, masterDistance, distToCenter);
            }

            masterPos = pos;
            masterDistance = distToCenter;
         }
      }

      posSet.remove(masterPos);
      if (DEBUG) {
         LOGGER.info("FarmlandWaterManager: {} center pos, {} dummy posses. Dist to center {}", masterPos, posSet.toArray(new ChunkPos[0]), masterDistance);
      }

      return (AABBTicket)addCustomTicket(world, new AABBTicket(aabb), masterPos, (ChunkPos[])posSet.toArray(new ChunkPos[0]));
   }

   private static double getDistanceSq(ChunkPos pos, Vec3d vec3d) {
      double d0 = (double)(pos.x * 16 + 8);
      double d1 = (double)(pos.z * 16 + 8);
      double d2 = d0 - vec3d.x;
      double d3 = d1 - vec3d.z;
      return d2 * d2 + d3 * d3;
   }

   public static boolean hasBlockWaterTicket(IWorldReader world, BlockPos pos) {
      ChunkTicketManager<Vec3d> ticketManager = getTicketManager(new ChunkPos(pos.getX() >> 4, pos.getZ() >> 4), world);
      if (ticketManager != null) {
         Vec3d posAsVec3d = new Vec3d((double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D);
         Iterator var4 = ticketManager.getTickets().iterator();

         while(var4.hasNext()) {
            SimpleTicket<Vec3d> ticket = (SimpleTicket)var4.next();
            if (ticket.matches(posAsVec3d)) {
               return true;
            }
         }
      }

      return false;
   }

   static void removeTickets(IChunk chunk) {
      ChunkTicketManager<Vec3d> ticketManager = getTicketManager(chunk.getPos(), chunk.getWorldForge());
      if (ticketManager != null) {
         if (DEBUG) {
            LOGGER.info("FarmlandWaterManager: got tickets {} at {} before", ticketManager.getTickets().size(), ticketManager.pos);
         }

         ticketManager.getTickets().removeIf((next) -> {
            return next.unload(ticketManager);
         });
         if (DEBUG) {
            LOGGER.info("FarmlandWaterManager: got tickets {} at {} after", ticketManager.getTickets().size(), ticketManager.pos);
         }
      }

   }

   private static ChunkTicketManager<Vec3d> getTicketManager(ChunkPos pos, IWorldReader world) {
      Preconditions.checkArgument(!world.isRemote(), "Water region is only determined server-side");
      Map<ChunkPos, ChunkTicketManager<Vec3d>> ticketMap = (Map)customWaterHandler.get(world.getDimension().getType().getId());
      return ticketMap == null ? null : (ChunkTicketManager)ticketMap.get(pos);
   }
}
