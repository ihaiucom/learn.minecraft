package net.minecraft.entity.ai.brain;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.util.Pair;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.schedule.Activity;
import net.minecraft.entity.ai.brain.schedule.Schedule;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.util.IDynamicSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.server.ServerWorld;

public class Brain<E extends LivingEntity> implements IDynamicSerializable {
   private final Map<MemoryModuleType<?>, Optional<?>> memories = Maps.newHashMap();
   private final Map<SensorType<? extends Sensor<? super E>>, Sensor<? super E>> sensors = Maps.newLinkedHashMap();
   private final Map<Integer, Map<Activity, Set<Task<? super E>>>> field_218232_c = Maps.newTreeMap();
   private Schedule schedule;
   private final Map<Activity, Set<Pair<MemoryModuleType<?>, MemoryModuleStatus>>> requiredMemoryStates;
   private Set<Activity> defaultActivities;
   private final Set<Activity> activities;
   private Activity fallbackActivity;
   private long lastGameTime;

   public <T> Brain(Collection<MemoryModuleType<?>> p_i50378_1_, Collection<SensorType<? extends Sensor<? super E>>> p_i50378_2_, Dynamic<T> p_i50378_3_) {
      this.schedule = Schedule.EMPTY;
      this.requiredMemoryStates = Maps.newHashMap();
      this.defaultActivities = Sets.newHashSet();
      this.activities = Sets.newHashSet();
      this.fallbackActivity = Activity.IDLE;
      this.lastGameTime = -9999L;
      p_i50378_1_.forEach((p_218228_1_) -> {
         Optional var10000 = (Optional)this.memories.put(p_218228_1_, Optional.empty());
      });
      p_i50378_2_.forEach((p_218204_1_) -> {
         Sensor var10000 = (Sensor)this.sensors.put(p_218204_1_, p_218204_1_.func_220995_a());
      });
      this.sensors.values().forEach((p_218225_1_) -> {
         Iterator var2 = p_218225_1_.getUsedMemories().iterator();

         while(var2.hasNext()) {
            MemoryModuleType<?> lvt_3_1_ = (MemoryModuleType)var2.next();
            this.memories.put(lvt_3_1_, Optional.empty());
         }

      });
      Iterator var4 = p_i50378_3_.get("memories").asMap(Function.identity(), Function.identity()).entrySet().iterator();

      while(var4.hasNext()) {
         Entry<Dynamic<T>, Dynamic<T>> lvt_5_1_ = (Entry)var4.next();
         this.setMemory((MemoryModuleType)Registry.MEMORY_MODULE_TYPE.getOrDefault(new ResourceLocation(((Dynamic)lvt_5_1_.getKey()).asString(""))), (Dynamic)lvt_5_1_.getValue());
      }

   }

   public boolean hasMemory(MemoryModuleType<?> p_218191_1_) {
      return this.hasMemory(p_218191_1_, MemoryModuleStatus.VALUE_PRESENT);
   }

   private <T, U> void setMemory(MemoryModuleType<U> p_218216_1_, Dynamic<T> p_218216_2_) {
      this.setMemory(p_218216_1_, ((Function)p_218216_1_.getDeserializer().orElseThrow(RuntimeException::new)).apply(p_218216_2_));
   }

   public <U> void removeMemory(MemoryModuleType<U> p_218189_1_) {
      this.setMemory(p_218189_1_, Optional.empty());
   }

   public <U> void setMemory(MemoryModuleType<U> p_218205_1_, @Nullable U p_218205_2_) {
      this.setMemory(p_218205_1_, Optional.ofNullable(p_218205_2_));
   }

   public <U> void setMemory(MemoryModuleType<U> p_218226_1_, Optional<U> p_218226_2_) {
      if (this.memories.containsKey(p_218226_1_)) {
         if (p_218226_2_.isPresent() && this.isEmptyCollection(p_218226_2_.get())) {
            this.removeMemory(p_218226_1_);
         } else {
            this.memories.put(p_218226_1_, p_218226_2_);
         }
      }

   }

   public <U> Optional<U> getMemory(MemoryModuleType<U> p_218207_1_) {
      return (Optional)this.memories.get(p_218207_1_);
   }

   public boolean hasMemory(MemoryModuleType<?> p_218196_1_, MemoryModuleStatus p_218196_2_) {
      Optional<?> lvt_3_1_ = (Optional)this.memories.get(p_218196_1_);
      if (lvt_3_1_ == null) {
         return false;
      } else {
         return p_218196_2_ == MemoryModuleStatus.REGISTERED || p_218196_2_ == MemoryModuleStatus.VALUE_PRESENT && lvt_3_1_.isPresent() || p_218196_2_ == MemoryModuleStatus.VALUE_ABSENT && !lvt_3_1_.isPresent();
      }
   }

   public Schedule getSchedule() {
      return this.schedule;
   }

   public void setSchedule(Schedule p_218203_1_) {
      this.schedule = p_218203_1_;
   }

   public void setDefaultActivities(Set<Activity> p_218199_1_) {
      this.defaultActivities = p_218199_1_;
   }

   @Deprecated
   public Stream<Task<? super E>> getRunningTasks() {
      return this.field_218232_c.values().stream().flatMap((p_218221_0_) -> {
         return p_218221_0_.values().stream();
      }).flatMap(Collection::stream).filter((p_218187_0_) -> {
         return p_218187_0_.getStatus() == Task.Status.RUNNING;
      });
   }

   public void switchTo(Activity p_218202_1_) {
      this.activities.clear();
      this.activities.addAll(this.defaultActivities);
      boolean lvt_2_1_ = this.requiredMemoryStates.keySet().contains(p_218202_1_) && this.hasRequiredMemories(p_218202_1_);
      this.activities.add(lvt_2_1_ ? p_218202_1_ : this.fallbackActivity);
   }

   public void updateActivity(long p_218211_1_, long p_218211_3_) {
      if (p_218211_3_ - this.lastGameTime > 20L) {
         this.lastGameTime = p_218211_3_;
         Activity lvt_5_1_ = this.getSchedule().getScheduledActivity((int)(p_218211_1_ % 24000L));
         if (!this.activities.contains(lvt_5_1_)) {
            this.switchTo(lvt_5_1_);
         }
      }

   }

   public void setFallbackActivity(Activity p_218200_1_) {
      this.fallbackActivity = p_218200_1_;
   }

   public void registerActivity(Activity p_218208_1_, ImmutableList<Pair<Integer, ? extends Task<? super E>>> p_218208_2_) {
      this.registerActivity(p_218208_1_, p_218208_2_, ImmutableSet.of());
   }

   public void registerActivity(Activity p_218224_1_, ImmutableList<Pair<Integer, ? extends Task<? super E>>> p_218224_2_, Set<Pair<MemoryModuleType<?>, MemoryModuleStatus>> p_218224_3_) {
      this.requiredMemoryStates.put(p_218224_1_, p_218224_3_);
      p_218224_2_.forEach((p_218223_2_) -> {
         ((Set)((Map)this.field_218232_c.computeIfAbsent(p_218223_2_.getFirst(), (p_218212_0_) -> {
            return Maps.newHashMap();
         })).computeIfAbsent(p_218224_1_, (p_218195_0_) -> {
            return Sets.newLinkedHashSet();
         })).add(p_218223_2_.getSecond());
      });
   }

   public boolean hasActivity(Activity p_218214_1_) {
      return this.activities.contains(p_218214_1_);
   }

   public Brain<E> copy() {
      Brain<E> lvt_1_1_ = new Brain(this.memories.keySet(), this.sensors.keySet(), new Dynamic(NBTDynamicOps.INSTANCE, new CompoundNBT()));
      this.memories.forEach((p_218188_1_, p_218188_2_) -> {
         p_218188_2_.ifPresent((p_218209_2_) -> {
            Optional var10000 = (Optional)lvt_1_1_.memories.put(p_218188_1_, Optional.of(p_218209_2_));
         });
      });
      return lvt_1_1_;
   }

   public void tick(ServerWorld p_218210_1_, E p_218210_2_) {
      this.updateSensors(p_218210_1_, p_218210_2_);
      this.startTasks(p_218210_1_, p_218210_2_);
      this.tickTasks(p_218210_1_, p_218210_2_);
   }

   public void stopAllTasks(ServerWorld p_218227_1_, E p_218227_2_) {
      long lvt_3_1_ = p_218227_2_.world.getGameTime();
      this.getRunningTasks().forEach((p_218206_4_) -> {
         p_218206_4_.stop(p_218227_1_, p_218227_2_, lvt_3_1_);
      });
   }

   public <T> T serialize(DynamicOps<T> p_218175_1_) {
      T lvt_2_1_ = p_218175_1_.createMap((Map)this.memories.entrySet().stream().filter((p_218197_0_) -> {
         return ((MemoryModuleType)p_218197_0_.getKey()).getDeserializer().isPresent() && ((Optional)p_218197_0_.getValue()).isPresent();
      }).map((p_218186_1_) -> {
         return Pair.of(p_218175_1_.createString(Registry.MEMORY_MODULE_TYPE.getKey(p_218186_1_.getKey()).toString()), ((IDynamicSerializable)((Optional)p_218186_1_.getValue()).get()).serialize(p_218175_1_));
      }).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)));
      return p_218175_1_.createMap(ImmutableMap.of(p_218175_1_.createString("memories"), lvt_2_1_));
   }

   private void updateSensors(ServerWorld p_218229_1_, E p_218229_2_) {
      this.sensors.values().forEach((p_218201_2_) -> {
         p_218201_2_.tick(p_218229_1_, p_218229_2_);
      });
   }

   private void startTasks(ServerWorld p_218218_1_, E p_218218_2_) {
      long lvt_3_1_ = p_218218_1_.getGameTime();
      this.field_218232_c.values().stream().flatMap((p_218219_0_) -> {
         return p_218219_0_.entrySet().stream();
      }).filter((p_218215_1_) -> {
         return this.activities.contains(p_218215_1_.getKey());
      }).map(Entry::getValue).flatMap(Collection::stream).filter((p_218194_0_) -> {
         return p_218194_0_.getStatus() == Task.Status.STOPPED;
      }).forEach((p_218192_4_) -> {
         p_218192_4_.start(p_218218_1_, p_218218_2_, lvt_3_1_);
      });
   }

   private void tickTasks(ServerWorld p_218222_1_, E p_218222_2_) {
      long lvt_3_1_ = p_218222_1_.getGameTime();
      this.getRunningTasks().forEach((p_218220_4_) -> {
         p_218220_4_.tick(p_218222_1_, p_218222_2_, lvt_3_1_);
      });
   }

   private boolean hasRequiredMemories(Activity p_218217_1_) {
      return ((Set)this.requiredMemoryStates.get(p_218217_1_)).stream().allMatch((p_218190_1_) -> {
         MemoryModuleType<?> lvt_2_1_ = (MemoryModuleType)p_218190_1_.getFirst();
         MemoryModuleStatus lvt_3_1_ = (MemoryModuleStatus)p_218190_1_.getSecond();
         return this.hasMemory(lvt_2_1_, lvt_3_1_);
      });
   }

   private boolean isEmptyCollection(Object p_218213_1_) {
      return p_218213_1_ instanceof Collection && ((Collection)p_218213_1_).isEmpty();
   }
}
