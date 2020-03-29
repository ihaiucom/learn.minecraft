package net.minecraftforge.common.util;

import java.util.Comparator;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.ChunkPos;

public class ChunkCoordComparator implements Comparator<ChunkPos> {
   private int x;
   private int z;

   public ChunkCoordComparator(ServerPlayerEntity entityplayer) {
      this.x = (int)entityplayer.func_226277_ct_() >> 4;
      this.z = (int)entityplayer.func_226281_cx_() >> 4;
   }

   public int compare(ChunkPos a, ChunkPos b) {
      if (a.equals(b)) {
         return 0;
      } else {
         int ax = a.x - this.x;
         int az = a.z - this.z;
         int bx = b.x - this.x;
         int bz = b.z - this.z;
         int result = (ax - bx) * (ax + bx) + (az - bz) * (az + bz);
         if (result != 0) {
            return result;
         } else if (ax < 0) {
            return bx < 0 ? bz - az : -1;
         } else {
            return bx < 0 ? 1 : az - bz;
         }
      }
   }
}
