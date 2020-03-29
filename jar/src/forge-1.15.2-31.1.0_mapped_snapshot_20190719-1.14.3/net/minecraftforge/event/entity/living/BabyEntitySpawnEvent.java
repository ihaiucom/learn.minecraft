package net.minecraftforge.event.entity.living;

import javax.annotation.Nullable;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

@Cancelable
public class BabyEntitySpawnEvent extends Event {
   private final MobEntity parentA;
   private final MobEntity parentB;
   private final PlayerEntity causedByPlayer;
   private AgeableEntity child;

   public BabyEntitySpawnEvent(MobEntity parentA, MobEntity parentB, @Nullable AgeableEntity proposedChild) {
      PlayerEntity causedByPlayer = null;
      if (parentA instanceof AnimalEntity) {
         causedByPlayer = ((AnimalEntity)parentA).getLoveCause();
      }

      if (causedByPlayer == null && parentB instanceof AnimalEntity) {
         causedByPlayer = ((AnimalEntity)parentB).getLoveCause();
      }

      this.parentA = parentA;
      this.parentB = parentB;
      this.causedByPlayer = causedByPlayer;
      this.child = proposedChild;
   }

   public MobEntity getParentA() {
      return this.parentA;
   }

   public MobEntity getParentB() {
      return this.parentB;
   }

   @Nullable
   public PlayerEntity getCausedByPlayer() {
      return this.causedByPlayer;
   }

   @Nullable
   public AgeableEntity getChild() {
      return this.child;
   }

   public void setChild(AgeableEntity proposedChild) {
      this.child = proposedChild;
   }
}
