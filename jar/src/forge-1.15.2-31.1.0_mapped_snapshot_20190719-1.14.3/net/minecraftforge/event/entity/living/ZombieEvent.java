package net.minecraftforge.event.entity.living;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.eventbus.api.Event.HasResult;

public class ZombieEvent extends EntityEvent {
   public ZombieEvent(ZombieEntity entity) {
      super(entity);
   }

   public ZombieEntity getSummoner() {
      return (ZombieEntity)this.getEntity();
   }

   @HasResult
   public static class SummonAidEvent extends ZombieEvent {
      private ZombieEntity customSummonedAid;
      private final World world;
      private final int x;
      private final int y;
      private final int z;
      private final LivingEntity attacker;
      private final double summonChance;

      public SummonAidEvent(ZombieEntity entity, World world, int x, int y, int z, LivingEntity attacker, double summonChance) {
         super(entity);
         this.world = world;
         this.x = x;
         this.y = y;
         this.z = z;
         this.attacker = attacker;
         this.summonChance = summonChance;
      }

      public ZombieEntity getCustomSummonedAid() {
         return this.customSummonedAid;
      }

      public void setCustomSummonedAid(ZombieEntity customSummonedAid) {
         this.customSummonedAid = customSummonedAid;
      }

      public World getWorld() {
         return this.world;
      }

      public int getX() {
         return this.x;
      }

      public int getY() {
         return this.y;
      }

      public int getZ() {
         return this.z;
      }

      public LivingEntity getAttacker() {
         return this.attacker;
      }

      public double getSummonChance() {
         return this.summonChance;
      }
   }
}
