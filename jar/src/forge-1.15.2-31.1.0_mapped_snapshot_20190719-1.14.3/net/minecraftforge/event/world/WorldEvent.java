package net.minecraftforge.event.world;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.EntityClassification;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

public class WorldEvent extends Event {
   private final IWorld world;

   public WorldEvent(IWorld world) {
      this.world = world;
   }

   public IWorld getWorld() {
      return this.world;
   }

   @Cancelable
   public static class CreateSpawnPosition extends WorldEvent {
      private final WorldSettings settings;

      public CreateSpawnPosition(IWorld world, WorldSettings settings) {
         super(world);
         this.settings = settings;
      }

      public WorldSettings getSettings() {
         return this.settings;
      }
   }

   @Cancelable
   public static class PotentialSpawns extends WorldEvent {
      private final EntityClassification type;
      private final BlockPos pos;
      private final List<Biome.SpawnListEntry> list;

      public PotentialSpawns(IWorld world, EntityClassification type, BlockPos pos, List<Biome.SpawnListEntry> oldList) {
         super(world);
         this.pos = pos;
         this.type = type;
         if (oldList != null) {
            this.list = new ArrayList(oldList);
         } else {
            this.list = new ArrayList();
         }

      }

      public EntityClassification getType() {
         return this.type;
      }

      public BlockPos getPos() {
         return this.pos;
      }

      public List<Biome.SpawnListEntry> getList() {
         return this.list;
      }
   }

   public static class Save extends WorldEvent {
      public Save(IWorld world) {
         super(world);
      }
   }

   public static class Unload extends WorldEvent {
      public Unload(IWorld world) {
         super(world);
      }
   }

   public static class Load extends WorldEvent {
      public Load(IWorld world) {
         super(world);
      }
   }
}
