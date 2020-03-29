package net.minecraft.world.lighting;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import javax.annotation.Nullable;
import net.minecraft.util.Direction;
import net.minecraft.util.SectionDistanceGraph;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.IChunkLightProvider;
import net.minecraft.world.chunk.NibbleArray;

public abstract class SectionLightStorage<M extends LightDataMap<M>> extends SectionDistanceGraph {
   protected static final NibbleArray EMPTY_ARRAY = new NibbleArray();
   private static final Direction[] DIRECTIONS = Direction.values();
   private final LightType type;
   private final IChunkLightProvider chunkProvider;
   protected final LongSet activeLightSections = new LongOpenHashSet();
   protected final LongSet addedEmptySections = new LongOpenHashSet();
   protected final LongSet addedActiveLightSections = new LongOpenHashSet();
   protected volatile M uncachedLightData;
   protected final M cachedLightData;
   protected final LongSet dirtyCachedSections = new LongOpenHashSet();
   protected final LongSet changedLightPositions = new LongOpenHashSet();
   protected final Long2ObjectMap<NibbleArray> newArrays = Long2ObjectMaps.synchronize(new Long2ObjectOpenHashMap());
   private final LongSet chunksToRetain = new LongOpenHashSet();
   private final LongSet noLightSections = new LongOpenHashSet();
   protected volatile boolean hasSectionsToUpdate;

   protected SectionLightStorage(LightType p_i51291_1_, IChunkLightProvider p_i51291_2_, M p_i51291_3_) {
      super(3, 16, 256);
      this.type = p_i51291_1_;
      this.chunkProvider = p_i51291_2_;
      this.cachedLightData = p_i51291_3_;
      this.uncachedLightData = p_i51291_3_.copy();
      this.uncachedLightData.disableCaching();
   }

   protected boolean hasSection(long p_215518_1_) {
      return this.getArray(p_215518_1_, true) != null;
   }

   @Nullable
   protected NibbleArray getArray(long p_215520_1_, boolean p_215520_3_) {
      return this.getArray(p_215520_3_ ? this.cachedLightData : this.uncachedLightData, p_215520_1_);
   }

   @Nullable
   protected NibbleArray getArray(M p_215531_1_, long p_215531_2_) {
      return p_215531_1_.getArray(p_215531_2_);
   }

   @Nullable
   public NibbleArray getArray(long p_222858_1_) {
      NibbleArray lvt_3_1_ = (NibbleArray)this.newArrays.get(p_222858_1_);
      return lvt_3_1_ != null ? lvt_3_1_ : this.getArray(p_222858_1_, false);
   }

   protected abstract int getLightOrDefault(long var1);

   protected int getLight(long p_215521_1_) {
      long lvt_3_1_ = SectionPos.worldToSection(p_215521_1_);
      NibbleArray lvt_5_1_ = this.getArray(lvt_3_1_, true);
      return lvt_5_1_.get(SectionPos.mask(BlockPos.unpackX(p_215521_1_)), SectionPos.mask(BlockPos.unpackY(p_215521_1_)), SectionPos.mask(BlockPos.unpackZ(p_215521_1_)));
   }

   protected void setLight(long p_215517_1_, int p_215517_3_) {
      long lvt_4_1_ = SectionPos.worldToSection(p_215517_1_);
      if (this.dirtyCachedSections.add(lvt_4_1_)) {
         this.cachedLightData.copyArray(lvt_4_1_);
      }

      NibbleArray lvt_6_1_ = this.getArray(lvt_4_1_, true);
      lvt_6_1_.set(SectionPos.mask(BlockPos.unpackX(p_215517_1_)), SectionPos.mask(BlockPos.unpackY(p_215517_1_)), SectionPos.mask(BlockPos.unpackZ(p_215517_1_)), p_215517_3_);

      for(int lvt_7_1_ = -1; lvt_7_1_ <= 1; ++lvt_7_1_) {
         for(int lvt_8_1_ = -1; lvt_8_1_ <= 1; ++lvt_8_1_) {
            for(int lvt_9_1_ = -1; lvt_9_1_ <= 1; ++lvt_9_1_) {
               this.changedLightPositions.add(SectionPos.worldToSection(BlockPos.offset(p_215517_1_, lvt_8_1_, lvt_9_1_, lvt_7_1_)));
            }
         }
      }

   }

   protected int getLevel(long p_215471_1_) {
      if (p_215471_1_ == Long.MAX_VALUE) {
         return 2;
      } else if (this.activeLightSections.contains(p_215471_1_)) {
         return 0;
      } else {
         return !this.noLightSections.contains(p_215471_1_) && this.cachedLightData.hasArray(p_215471_1_) ? 1 : 2;
      }
   }

   protected int getSourceLevel(long p_215516_1_) {
      if (this.addedEmptySections.contains(p_215516_1_)) {
         return 2;
      } else {
         return !this.activeLightSections.contains(p_215516_1_) && !this.addedActiveLightSections.contains(p_215516_1_) ? 2 : 0;
      }
   }

   protected void setLevel(long p_215476_1_, int p_215476_3_) {
      int lvt_4_1_ = this.getLevel(p_215476_1_);
      if (lvt_4_1_ != 0 && p_215476_3_ == 0) {
         this.activeLightSections.add(p_215476_1_);
         this.addedActiveLightSections.remove(p_215476_1_);
      }

      if (lvt_4_1_ == 0 && p_215476_3_ != 0) {
         this.activeLightSections.remove(p_215476_1_);
         this.addedEmptySections.remove(p_215476_1_);
      }

      if (lvt_4_1_ >= 2 && p_215476_3_ != 2) {
         if (this.noLightSections.contains(p_215476_1_)) {
            this.noLightSections.remove(p_215476_1_);
         } else {
            this.cachedLightData.setArray(p_215476_1_, this.getOrCreateArray(p_215476_1_));
            this.dirtyCachedSections.add(p_215476_1_);
            this.func_215524_j(p_215476_1_);

            for(int lvt_5_1_ = -1; lvt_5_1_ <= 1; ++lvt_5_1_) {
               for(int lvt_6_1_ = -1; lvt_6_1_ <= 1; ++lvt_6_1_) {
                  for(int lvt_7_1_ = -1; lvt_7_1_ <= 1; ++lvt_7_1_) {
                     this.changedLightPositions.add(SectionPos.worldToSection(BlockPos.offset(p_215476_1_, lvt_6_1_, lvt_7_1_, lvt_5_1_)));
                  }
               }
            }
         }
      }

      if (lvt_4_1_ != 2 && p_215476_3_ >= 2) {
         this.noLightSections.add(p_215476_1_);
      }

      this.hasSectionsToUpdate = !this.noLightSections.isEmpty();
   }

   protected NibbleArray getOrCreateArray(long p_215530_1_) {
      NibbleArray lvt_3_1_ = (NibbleArray)this.newArrays.get(p_215530_1_);
      return lvt_3_1_ != null ? lvt_3_1_ : new NibbleArray();
   }

   protected void cancelSectionUpdates(LightEngine<?, ?> p_215528_1_, long p_215528_2_) {
      if (p_215528_1_.func_227467_c_() < 8192) {
         p_215528_1_.func_227465_a_((p_227469_2_) -> {
            return SectionPos.worldToSection(p_227469_2_) == p_215528_2_;
         });
      } else {
         int lvt_4_1_ = SectionPos.toWorld(SectionPos.extractX(p_215528_2_));
         int lvt_5_1_ = SectionPos.toWorld(SectionPos.extractY(p_215528_2_));
         int lvt_6_1_ = SectionPos.toWorld(SectionPos.extractZ(p_215528_2_));

         for(int lvt_7_1_ = 0; lvt_7_1_ < 16; ++lvt_7_1_) {
            for(int lvt_8_1_ = 0; lvt_8_1_ < 16; ++lvt_8_1_) {
               for(int lvt_9_1_ = 0; lvt_9_1_ < 16; ++lvt_9_1_) {
                  long lvt_10_1_ = BlockPos.pack(lvt_4_1_ + lvt_7_1_, lvt_5_1_ + lvt_8_1_, lvt_6_1_ + lvt_9_1_);
                  p_215528_1_.cancelUpdate(lvt_10_1_);
               }
            }
         }

      }
   }

   protected boolean hasSectionsToUpdate() {
      return this.hasSectionsToUpdate;
   }

   protected void updateSections(LightEngine<M, ?> p_215522_1_, boolean p_215522_2_, boolean p_215522_3_) {
      if (this.hasSectionsToUpdate() || !this.newArrays.isEmpty()) {
         LongIterator var4 = this.noLightSections.iterator();

         long lvt_5_4_;
         NibbleArray lvt_8_2_;
         while(var4.hasNext()) {
            lvt_5_4_ = (Long)var4.next();
            this.cancelSectionUpdates(p_215522_1_, lvt_5_4_);
            NibbleArray lvt_7_1_ = (NibbleArray)this.newArrays.remove(lvt_5_4_);
            lvt_8_2_ = this.cachedLightData.removeArray(lvt_5_4_);
            if (this.chunksToRetain.contains(SectionPos.toSectionColumnPos(lvt_5_4_))) {
               if (lvt_7_1_ != null) {
                  this.newArrays.put(lvt_5_4_, lvt_7_1_);
               } else if (lvt_8_2_ != null) {
                  this.newArrays.put(lvt_5_4_, lvt_8_2_);
               }
            }
         }

         this.cachedLightData.invalidateCaches();
         var4 = this.noLightSections.iterator();

         while(var4.hasNext()) {
            lvt_5_4_ = (Long)var4.next();
            this.func_215523_k(lvt_5_4_);
         }

         this.noLightSections.clear();
         this.hasSectionsToUpdate = false;
         ObjectIterator lvt_4_1_ = this.newArrays.long2ObjectEntrySet().iterator();

         long lvt_6_2_;
         Entry lvt_5_5_;
         while(lvt_4_1_.hasNext()) {
            lvt_5_5_ = (Entry)lvt_4_1_.next();
            lvt_6_2_ = lvt_5_5_.getLongKey();
            if (this.hasSection(lvt_6_2_)) {
               lvt_8_2_ = (NibbleArray)lvt_5_5_.getValue();
               if (this.cachedLightData.getArray(lvt_6_2_) != lvt_8_2_) {
                  this.cancelSectionUpdates(p_215522_1_, lvt_6_2_);
                  this.cachedLightData.setArray(lvt_6_2_, lvt_8_2_);
                  this.dirtyCachedSections.add(lvt_6_2_);
               }
            }
         }

         this.cachedLightData.invalidateCaches();
         if (!p_215522_3_) {
            var4 = this.newArrays.keySet().iterator();

            label99:
            while(true) {
               do {
                  if (!var4.hasNext()) {
                     break label99;
                  }

                  lvt_5_4_ = (Long)var4.next();
               } while(!this.hasSection(lvt_5_4_));

               int lvt_7_2_ = SectionPos.toWorld(SectionPos.extractX(lvt_5_4_));
               int lvt_8_3_ = SectionPos.toWorld(SectionPos.extractY(lvt_5_4_));
               int lvt_9_1_ = SectionPos.toWorld(SectionPos.extractZ(lvt_5_4_));
               Direction[] var10 = DIRECTIONS;
               int var11 = var10.length;

               for(int var12 = 0; var12 < var11; ++var12) {
                  Direction lvt_13_1_ = var10[var12];
                  long lvt_14_1_ = SectionPos.withOffset(lvt_5_4_, lvt_13_1_);
                  if (!this.newArrays.containsKey(lvt_14_1_) && this.hasSection(lvt_14_1_)) {
                     for(int lvt_16_1_ = 0; lvt_16_1_ < 16; ++lvt_16_1_) {
                        for(int lvt_17_1_ = 0; lvt_17_1_ < 16; ++lvt_17_1_) {
                           long lvt_18_6_;
                           long lvt_20_6_;
                           switch(lvt_13_1_) {
                           case DOWN:
                              lvt_18_6_ = BlockPos.pack(lvt_7_2_ + lvt_17_1_, lvt_8_3_, lvt_9_1_ + lvt_16_1_);
                              lvt_20_6_ = BlockPos.pack(lvt_7_2_ + lvt_17_1_, lvt_8_3_ - 1, lvt_9_1_ + lvt_16_1_);
                              break;
                           case UP:
                              lvt_18_6_ = BlockPos.pack(lvt_7_2_ + lvt_17_1_, lvt_8_3_ + 16 - 1, lvt_9_1_ + lvt_16_1_);
                              lvt_20_6_ = BlockPos.pack(lvt_7_2_ + lvt_17_1_, lvt_8_3_ + 16, lvt_9_1_ + lvt_16_1_);
                              break;
                           case NORTH:
                              lvt_18_6_ = BlockPos.pack(lvt_7_2_ + lvt_16_1_, lvt_8_3_ + lvt_17_1_, lvt_9_1_);
                              lvt_20_6_ = BlockPos.pack(lvt_7_2_ + lvt_16_1_, lvt_8_3_ + lvt_17_1_, lvt_9_1_ - 1);
                              break;
                           case SOUTH:
                              lvt_18_6_ = BlockPos.pack(lvt_7_2_ + lvt_16_1_, lvt_8_3_ + lvt_17_1_, lvt_9_1_ + 16 - 1);
                              lvt_20_6_ = BlockPos.pack(lvt_7_2_ + lvt_16_1_, lvt_8_3_ + lvt_17_1_, lvt_9_1_ + 16);
                              break;
                           case WEST:
                              lvt_18_6_ = BlockPos.pack(lvt_7_2_, lvt_8_3_ + lvt_16_1_, lvt_9_1_ + lvt_17_1_);
                              lvt_20_6_ = BlockPos.pack(lvt_7_2_ - 1, lvt_8_3_ + lvt_16_1_, lvt_9_1_ + lvt_17_1_);
                              break;
                           default:
                              lvt_18_6_ = BlockPos.pack(lvt_7_2_ + 16 - 1, lvt_8_3_ + lvt_16_1_, lvt_9_1_ + lvt_17_1_);
                              lvt_20_6_ = BlockPos.pack(lvt_7_2_ + 16, lvt_8_3_ + lvt_16_1_, lvt_9_1_ + lvt_17_1_);
                           }

                           p_215522_1_.scheduleUpdate(lvt_18_6_, lvt_20_6_, p_215522_1_.getEdgeLevel(lvt_18_6_, lvt_20_6_, p_215522_1_.getLevel(lvt_18_6_)), false);
                           p_215522_1_.scheduleUpdate(lvt_20_6_, lvt_18_6_, p_215522_1_.getEdgeLevel(lvt_20_6_, lvt_18_6_, p_215522_1_.getLevel(lvt_20_6_)), false);
                        }
                     }
                  }
               }
            }
         }

         lvt_4_1_ = this.newArrays.long2ObjectEntrySet().iterator();

         while(lvt_4_1_.hasNext()) {
            lvt_5_5_ = (Entry)lvt_4_1_.next();
            lvt_6_2_ = lvt_5_5_.getLongKey();
            if (this.hasSection(lvt_6_2_)) {
               lvt_4_1_.remove();
            }
         }

      }
   }

   protected void func_215524_j(long p_215524_1_) {
   }

   protected void func_215523_k(long p_215523_1_) {
   }

   protected void func_215526_b(long p_215526_1_, boolean p_215526_3_) {
   }

   public void retainChunkData(long p_223113_1_, boolean p_223113_3_) {
      if (p_223113_3_) {
         this.chunksToRetain.add(p_223113_1_);
      } else {
         this.chunksToRetain.remove(p_223113_1_);
      }

   }

   protected void setData(long p_215529_1_, @Nullable NibbleArray p_215529_3_) {
      if (p_215529_3_ != null) {
         this.newArrays.put(p_215529_1_, p_215529_3_);
      } else {
         this.newArrays.remove(p_215529_1_);
      }

   }

   protected void updateSectionStatus(long p_215519_1_, boolean p_215519_3_) {
      boolean lvt_4_1_ = this.activeLightSections.contains(p_215519_1_);
      if (!lvt_4_1_ && !p_215519_3_) {
         this.addedActiveLightSections.add(p_215519_1_);
         this.scheduleUpdate(Long.MAX_VALUE, p_215519_1_, 0, true);
      }

      if (lvt_4_1_ && p_215519_3_) {
         this.addedEmptySections.add(p_215519_1_);
         this.scheduleUpdate(Long.MAX_VALUE, p_215519_1_, 2, false);
      }

   }

   protected void processAllLevelUpdates() {
      if (this.needsUpdate()) {
         this.processUpdates(Integer.MAX_VALUE);
      }

   }

   protected void updateAndNotify() {
      if (!this.dirtyCachedSections.isEmpty()) {
         M lvt_1_1_ = this.cachedLightData.copy();
         lvt_1_1_.disableCaching();
         this.uncachedLightData = lvt_1_1_;
         this.dirtyCachedSections.clear();
      }

      if (!this.changedLightPositions.isEmpty()) {
         LongIterator lvt_1_2_ = this.changedLightPositions.iterator();

         while(lvt_1_2_.hasNext()) {
            long lvt_2_1_ = lvt_1_2_.nextLong();
            this.chunkProvider.markLightChanged(this.type, SectionPos.from(lvt_2_1_));
         }

         this.changedLightPositions.clear();
      }

   }
}
