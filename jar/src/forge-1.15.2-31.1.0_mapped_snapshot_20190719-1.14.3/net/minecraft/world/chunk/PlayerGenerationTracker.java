package net.minecraft.world.chunk;

import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import java.util.stream.Stream;
import net.minecraft.entity.player.ServerPlayerEntity;

public final class PlayerGenerationTracker {
   private final Object2BooleanMap<ServerPlayerEntity> generatingPlayers = new Object2BooleanOpenHashMap();

   public Stream<ServerPlayerEntity> getGeneratingPlayers(long p_219444_1_) {
      return this.generatingPlayers.keySet().stream();
   }

   public void addPlayer(long p_219442_1_, ServerPlayerEntity p_219442_3_, boolean p_219442_4_) {
      this.generatingPlayers.put(p_219442_3_, p_219442_4_);
   }

   public void removePlayer(long p_219443_1_, ServerPlayerEntity p_219443_3_) {
      this.generatingPlayers.removeBoolean(p_219443_3_);
   }

   public void disableGeneration(ServerPlayerEntity p_219446_1_) {
      this.generatingPlayers.replace(p_219446_1_, true);
   }

   public void enableGeneration(ServerPlayerEntity p_219447_1_) {
      this.generatingPlayers.replace(p_219447_1_, false);
   }

   public boolean cannotGenerateChunks(ServerPlayerEntity p_219448_1_) {
      return this.generatingPlayers.getOrDefault(p_219448_1_, true);
   }

   public boolean func_225419_d(ServerPlayerEntity p_225419_1_) {
      return this.generatingPlayers.getBoolean(p_225419_1_);
   }

   public void updatePlayerPosition(long p_219445_1_, long p_219445_3_, ServerPlayerEntity p_219445_5_) {
   }
}
