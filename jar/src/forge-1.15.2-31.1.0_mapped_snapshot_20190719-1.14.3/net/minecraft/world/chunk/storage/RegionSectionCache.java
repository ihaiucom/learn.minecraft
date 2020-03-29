package net.minecraft.world.chunk.storage;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OptionalDynamic;
import com.mojang.datafixers.types.DynamicOps;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongLinkedOpenHashSet;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.util.IDynamicSerializable;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.Util;
import net.minecraft.util.datafix.DefaultTypeReferences;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RegionSectionCache<R extends IDynamicSerializable> implements AutoCloseable {
   private static final Logger field_219120_a = LogManager.getLogger();
   private final IOWorker field_227173_b_;
   private final Long2ObjectMap<Optional<R>> data = new Long2ObjectOpenHashMap();
   private final LongLinkedOpenHashSet dirtySections = new LongLinkedOpenHashSet();
   private final BiFunction<Runnable, Dynamic<?>, R> field_219123_e;
   private final Function<Runnable, R> field_219124_f;
   private final DataFixer field_219125_g;
   private final DefaultTypeReferences field_219126_h;

   public RegionSectionCache(File p_i49937_1_, BiFunction<Runnable, Dynamic<?>, R> p_i49937_2_, Function<Runnable, R> p_i49937_3_, DataFixer p_i49937_4_, DefaultTypeReferences p_i49937_5_) {
      this.field_219123_e = p_i49937_2_;
      this.field_219124_f = p_i49937_3_;
      this.field_219125_g = p_i49937_4_;
      this.field_219126_h = p_i49937_5_;
      this.field_227173_b_ = new IOWorker(new RegionFileCache(p_i49937_1_), p_i49937_1_.getName());
   }

   protected void func_219115_a(BooleanSupplier p_219115_1_) {
      while(!this.dirtySections.isEmpty() && p_219115_1_.getAsBoolean()) {
         ChunkPos lvt_2_1_ = SectionPos.from(this.dirtySections.firstLong()).asChunkPos();
         this.save(lvt_2_1_);
      }

   }

   @Nullable
   protected Optional<R> func_219106_c(long p_219106_1_) {
      return (Optional)this.data.get(p_219106_1_);
   }

   protected Optional<R> func_219113_d(long p_219113_1_) {
      SectionPos lvt_3_1_ = SectionPos.from(p_219113_1_);
      if (this.func_219114_b(lvt_3_1_)) {
         return Optional.empty();
      } else {
         Optional<R> lvt_4_1_ = this.func_219106_c(p_219113_1_);
         if (lvt_4_1_ != null) {
            return lvt_4_1_;
         } else {
            this.func_219107_b(lvt_3_1_.asChunkPos());
            lvt_4_1_ = this.func_219106_c(p_219113_1_);
            if (lvt_4_1_ == null) {
               throw (IllegalStateException)Util.func_229757_c_(new IllegalStateException());
            } else {
               return lvt_4_1_;
            }
         }
      }
   }

   protected boolean func_219114_b(SectionPos p_219114_1_) {
      return World.isYOutOfBounds(SectionPos.toWorld(p_219114_1_.getSectionY()));
   }

   protected R func_219110_e(long p_219110_1_) {
      Optional<R> lvt_3_1_ = this.func_219113_d(p_219110_1_);
      if (lvt_3_1_.isPresent()) {
         return (IDynamicSerializable)lvt_3_1_.get();
      } else {
         R lvt_4_1_ = (IDynamicSerializable)this.field_219124_f.apply(() -> {
            this.markDirty(p_219110_1_);
         });
         this.data.put(p_219110_1_, Optional.of(lvt_4_1_));
         return lvt_4_1_;
      }
   }

   private void func_219107_b(ChunkPos p_219107_1_) {
      this.func_219119_a(p_219107_1_, NBTDynamicOps.INSTANCE, this.func_223138_c(p_219107_1_));
   }

   @Nullable
   private CompoundNBT func_223138_c(ChunkPos p_223138_1_) {
      try {
         return this.field_227173_b_.func_227090_a_(p_223138_1_);
      } catch (IOException var3) {
         field_219120_a.error("Error reading chunk {} data from disk", p_223138_1_, var3);
         return null;
      }
   }

   private <T> void func_219119_a(ChunkPos p_219119_1_, DynamicOps<T> p_219119_2_, @Nullable T p_219119_3_) {
      if (p_219119_3_ == null) {
         for(int lvt_4_1_ = 0; lvt_4_1_ < 16; ++lvt_4_1_) {
            this.data.put(SectionPos.from(p_219119_1_, lvt_4_1_).asLong(), Optional.empty());
         }
      } else {
         Dynamic<T> lvt_4_2_ = new Dynamic(p_219119_2_, p_219119_3_);
         int lvt_5_1_ = func_219103_a(lvt_4_2_);
         int lvt_6_1_ = SharedConstants.getVersion().getWorldVersion();
         boolean lvt_7_1_ = lvt_5_1_ != lvt_6_1_;
         Dynamic<T> lvt_8_1_ = this.field_219125_g.update(this.field_219126_h.func_219816_a(), lvt_4_2_, lvt_5_1_, lvt_6_1_);
         OptionalDynamic<T> lvt_9_1_ = lvt_8_1_.get("Sections");

         for(int lvt_10_1_ = 0; lvt_10_1_ < 16; ++lvt_10_1_) {
            long lvt_11_1_ = SectionPos.from(p_219119_1_, lvt_10_1_).asLong();
            Optional<R> lvt_13_1_ = lvt_9_1_.get(Integer.toString(lvt_10_1_)).get().map((p_219105_3_) -> {
               return (IDynamicSerializable)this.field_219123_e.apply(() -> {
                  this.markDirty(lvt_11_1_);
               }, p_219105_3_);
            });
            this.data.put(lvt_11_1_, lvt_13_1_);
            lvt_13_1_.ifPresent((p_219118_4_) -> {
               this.func_219111_b(lvt_11_1_);
               if (lvt_7_1_) {
                  this.markDirty(lvt_11_1_);
               }

            });
         }
      }

   }

   private void save(ChunkPos p_219117_1_) {
      Dynamic<INBT> lvt_2_1_ = this.serialize(p_219117_1_, NBTDynamicOps.INSTANCE);
      INBT lvt_3_1_ = (INBT)lvt_2_1_.getValue();
      if (lvt_3_1_ instanceof CompoundNBT) {
         this.field_227173_b_.func_227093_a_(p_219117_1_, (CompoundNBT)lvt_3_1_);
      } else {
         field_219120_a.error("Expected compound tag, got {}", lvt_3_1_);
      }

   }

   private <T> Dynamic<T> serialize(ChunkPos p_219108_1_, DynamicOps<T> p_219108_2_) {
      Map<T, T> lvt_3_1_ = Maps.newHashMap();

      for(int lvt_4_1_ = 0; lvt_4_1_ < 16; ++lvt_4_1_) {
         long lvt_5_1_ = SectionPos.from(p_219108_1_, lvt_4_1_).asLong();
         this.dirtySections.remove(lvt_5_1_);
         Optional<R> lvt_7_1_ = (Optional)this.data.get(lvt_5_1_);
         if (lvt_7_1_ != null && lvt_7_1_.isPresent()) {
            lvt_3_1_.put(p_219108_2_.createString(Integer.toString(lvt_4_1_)), ((IDynamicSerializable)lvt_7_1_.get()).serialize(p_219108_2_));
         }
      }

      return new Dynamic(p_219108_2_, p_219108_2_.createMap(ImmutableMap.of(p_219108_2_.createString("Sections"), p_219108_2_.createMap(lvt_3_1_), p_219108_2_.createString("DataVersion"), p_219108_2_.createInt(SharedConstants.getVersion().getWorldVersion()))));
   }

   protected void func_219111_b(long p_219111_1_) {
   }

   protected void markDirty(long p_219116_1_) {
      Optional<R> lvt_3_1_ = (Optional)this.data.get(p_219116_1_);
      if (lvt_3_1_ != null && lvt_3_1_.isPresent()) {
         this.dirtySections.add(p_219116_1_);
      } else {
         field_219120_a.warn("No data for position: {}", SectionPos.from(p_219116_1_));
      }
   }

   private static int func_219103_a(Dynamic<?> p_219103_0_) {
      return ((Number)p_219103_0_.get("DataVersion").asNumber().orElse(1945)).intValue();
   }

   public void saveIfDirty(ChunkPos p_219112_1_) {
      if (!this.dirtySections.isEmpty()) {
         for(int lvt_2_1_ = 0; lvt_2_1_ < 16; ++lvt_2_1_) {
            long lvt_3_1_ = SectionPos.from(p_219112_1_, lvt_2_1_).asLong();
            if (this.dirtySections.contains(lvt_3_1_)) {
               this.save(p_219112_1_);
               return;
            }
         }
      }

   }

   public void close() throws IOException {
      this.field_227173_b_.close();
   }
}
