package net.minecraft.village;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.util.IDynamicSerializable;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.SectionPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Supplier;

public class PointOfInterestData implements IDynamicSerializable {
   private static final Logger field_218255_a = LogManager.getLogger();
   private final Short2ObjectMap<PointOfInterest> field_218256_b = new Short2ObjectOpenHashMap();
   private final Map<PointOfInterestType, Set<PointOfInterest>> field_218257_c = Maps.newHashMap();
   private final Runnable onChange;
   private boolean valid;

   public PointOfInterestData(Runnable p_i50293_1_) {
      this.onChange = p_i50293_1_;
      this.valid = true;
   }

   public <T> PointOfInterestData(Runnable p_i50294_1_, Dynamic<T> p_i50294_2_) {
      this.onChange = p_i50294_1_;

      try {
         this.valid = p_i50294_2_.get("Valid").asBoolean(false);
         p_i50294_2_.get("Records").asStream().forEach((p_218249_2_) -> {
            this.func_218254_a(new PointOfInterest(p_218249_2_, p_i50294_1_));
         });
      } catch (Exception var4) {
         field_218255_a.error("Failed to load POI chunk", var4);
         this.clear();
         this.valid = false;
      }

   }

   public Stream<PointOfInterest> func_218247_a(Predicate<PointOfInterestType> p_218247_1_, PointOfInterestManager.Status p_218247_2_) {
      return this.field_218257_c.entrySet().stream().filter((p_218239_1_) -> {
         return p_218247_1_.test(p_218239_1_.getKey());
      }).flatMap((p_218246_0_) -> {
         return ((Set)p_218246_0_.getValue()).stream();
      }).filter(p_218247_2_.func_221035_a());
   }

   public void func_218243_a(BlockPos p_218243_1_, PointOfInterestType p_218243_2_) {
      if (this.func_218254_a(new PointOfInterest(p_218243_1_, p_218243_2_, this.onChange))) {
         field_218255_a.debug("Added POI of type {} @ {}", new Supplier[]{() -> {
            return p_218243_2_;
         }, () -> {
            return p_218243_1_;
         }});
         this.onChange.run();
      }

   }

   private boolean func_218254_a(PointOfInterest p_218254_1_) {
      BlockPos lvt_2_1_ = p_218254_1_.getPos();
      PointOfInterestType lvt_3_1_ = p_218254_1_.getType();
      short lvt_4_1_ = SectionPos.toRelativeOffset(lvt_2_1_);
      PointOfInterest lvt_5_1_ = (PointOfInterest)this.field_218256_b.get(lvt_4_1_);
      if (lvt_5_1_ != null) {
         if (lvt_3_1_.equals(lvt_5_1_.getType())) {
            return false;
         } else {
            throw (IllegalStateException)Util.func_229757_c_(new IllegalStateException("POI data mismatch: already registered at " + lvt_2_1_));
         }
      } else {
         this.field_218256_b.put(lvt_4_1_, p_218254_1_);
         ((Set)this.field_218257_c.computeIfAbsent(lvt_3_1_, (p_218252_0_) -> {
            return Sets.newHashSet();
         })).add(p_218254_1_);
         return true;
      }
   }

   public void remove(BlockPos p_218248_1_) {
      PointOfInterest lvt_2_1_ = (PointOfInterest)this.field_218256_b.remove(SectionPos.toRelativeOffset(p_218248_1_));
      if (lvt_2_1_ == null) {
         field_218255_a.error("POI data mismatch: never registered at " + p_218248_1_);
      } else {
         ((Set)this.field_218257_c.get(lvt_2_1_.getType())).remove(lvt_2_1_);
         field_218255_a.debug("Removed POI of type {} @ {}", new Supplier[]{lvt_2_1_::getType, lvt_2_1_::getPos});
         this.onChange.run();
      }
   }

   public boolean func_218251_c(BlockPos p_218251_1_) {
      PointOfInterest lvt_2_1_ = (PointOfInterest)this.field_218256_b.get(SectionPos.toRelativeOffset(p_218251_1_));
      if (lvt_2_1_ == null) {
         throw (IllegalStateException)Util.func_229757_c_(new IllegalStateException("POI never registered at " + p_218251_1_));
      } else {
         boolean lvt_3_1_ = lvt_2_1_.release();
         this.onChange.run();
         return lvt_3_1_;
      }
   }

   public boolean func_218245_a(BlockPos p_218245_1_, Predicate<PointOfInterestType> p_218245_2_) {
      short lvt_3_1_ = SectionPos.toRelativeOffset(p_218245_1_);
      PointOfInterest lvt_4_1_ = (PointOfInterest)this.field_218256_b.get(lvt_3_1_);
      return lvt_4_1_ != null && p_218245_2_.test(lvt_4_1_.getType());
   }

   public Optional<PointOfInterestType> func_218244_d(BlockPos p_218244_1_) {
      short lvt_2_1_ = SectionPos.toRelativeOffset(p_218244_1_);
      PointOfInterest lvt_3_1_ = (PointOfInterest)this.field_218256_b.get(lvt_2_1_);
      return lvt_3_1_ != null ? Optional.of(lvt_3_1_.getType()) : Optional.empty();
   }

   public <T> T serialize(DynamicOps<T> p_218175_1_) {
      T lvt_2_1_ = p_218175_1_.createList(this.field_218256_b.values().stream().map((p_218242_1_) -> {
         return p_218242_1_.serialize(p_218175_1_);
      }));
      return p_218175_1_.createMap(ImmutableMap.of(p_218175_1_.createString("Records"), lvt_2_1_, p_218175_1_.createString("Valid"), p_218175_1_.createBoolean(this.valid)));
   }

   public void func_218240_a(Consumer<BiConsumer<BlockPos, PointOfInterestType>> p_218240_1_) {
      if (!this.valid) {
         Short2ObjectMap<PointOfInterest> lvt_2_1_ = new Short2ObjectOpenHashMap(this.field_218256_b);
         this.clear();
         p_218240_1_.accept((p_218250_2_, p_218250_3_) -> {
            short lvt_4_1_ = SectionPos.toRelativeOffset(p_218250_2_);
            PointOfInterest lvt_5_1_ = (PointOfInterest)lvt_2_1_.computeIfAbsent(lvt_4_1_, (p_218241_3_) -> {
               return new PointOfInterest(p_218250_2_, p_218250_3_, this.onChange);
            });
            this.func_218254_a(lvt_5_1_);
         });
         this.valid = true;
         this.onChange.run();
      }

   }

   private void clear() {
      this.field_218256_b.clear();
      this.field_218257_c.clear();
   }

   boolean func_226355_a_() {
      return this.valid;
   }
}
