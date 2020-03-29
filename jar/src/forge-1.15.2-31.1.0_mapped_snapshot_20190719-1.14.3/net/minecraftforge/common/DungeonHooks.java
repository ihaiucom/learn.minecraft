package net.minecraftforge.common;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import net.minecraft.entity.EntityType;
import net.minecraft.util.WeightedRandom;

public class DungeonHooks {
   private static ArrayList<DungeonHooks.DungeonMob> dungeonMobs = new ArrayList();

   public static float addDungeonMob(EntityType<?> type, int rarity) {
      if (rarity <= 0) {
         throw new IllegalArgumentException("Rarity must be greater then zero");
      } else {
         Iterator itr = dungeonMobs.iterator();

         while(itr.hasNext()) {
            DungeonHooks.DungeonMob mob = (DungeonHooks.DungeonMob)itr.next();
            if (type == mob.type) {
               itr.remove();
               rarity += mob.itemWeight;
               break;
            }
         }

         dungeonMobs.add(new DungeonHooks.DungeonMob(rarity, type));
         return (float)rarity;
      }
   }

   public static int removeDungeonMob(EntityType<?> name) {
      Iterator var1 = dungeonMobs.iterator();

      DungeonHooks.DungeonMob mob;
      do {
         if (!var1.hasNext()) {
            return 0;
         }

         mob = (DungeonHooks.DungeonMob)var1.next();
      } while(name != mob.type);

      dungeonMobs.remove(mob);
      return mob.itemWeight;
   }

   public static EntityType<?> getRandomDungeonMob(Random rand) {
      DungeonHooks.DungeonMob mob = (DungeonHooks.DungeonMob)WeightedRandom.getRandomItem(rand, dungeonMobs);
      return mob.type;
   }

   static {
      addDungeonMob(EntityType.SKELETON, 100);
      addDungeonMob(EntityType.ZOMBIE, 200);
      addDungeonMob(EntityType.SPIDER, 100);
   }

   public static class DungeonMob extends WeightedRandom.Item {
      public final EntityType<?> type;

      public DungeonMob(int weight, EntityType<?> type) {
         super(weight);
         this.type = type;
      }

      public boolean equals(Object target) {
         return target instanceof DungeonHooks.DungeonMob && this.type.equals(((DungeonHooks.DungeonMob)target).type);
      }
   }
}
