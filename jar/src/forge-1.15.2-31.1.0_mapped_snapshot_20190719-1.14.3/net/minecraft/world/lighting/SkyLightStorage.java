package net.minecraft.world.lighting;

import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.Arrays;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.IChunkLightProvider;
import net.minecraft.world.chunk.NibbleArray;

public class SkyLightStorage extends SectionLightStorage<SkyLightStorage.StorageMap> {
   private static final Direction[] field_215554_k;
   private final LongSet field_215555_l = new LongOpenHashSet();
   private final LongSet field_215556_m = new LongOpenHashSet();
   private final LongSet field_215557_n = new LongOpenHashSet();
   private final LongSet field_215558_o = new LongOpenHashSet();
   private volatile boolean field_215553_p;

   protected SkyLightStorage(IChunkLightProvider p_i51288_1_) {
      super(LightType.SKY, p_i51288_1_, new SkyLightStorage.StorageMap(new Long2ObjectOpenHashMap(), new Long2IntOpenHashMap(), Integer.MAX_VALUE));
   }

   protected int getLightOrDefault(long p_215525_1_) {
      long lvt_3_1_ = SectionPos.worldToSection(p_215525_1_);
      int lvt_5_1_ = SectionPos.extractY(lvt_3_1_);
      SkyLightStorage.StorageMap lvt_6_1_ = (SkyLightStorage.StorageMap)this.uncachedLightData;
      int lvt_7_1_ = lvt_6_1_.field_215653_c.get(SectionPos.toSectionColumnPos(lvt_3_1_));
      if (lvt_7_1_ != lvt_6_1_.field_215652_b && lvt_5_1_ < lvt_7_1_) {
         NibbleArray lvt_8_1_ = this.getArray(lvt_6_1_, lvt_3_1_);
         if (lvt_8_1_ == null) {
            for(p_215525_1_ = BlockPos.func_218288_f(p_215525_1_); lvt_8_1_ == null; lvt_8_1_ = this.getArray(lvt_6_1_, lvt_3_1_)) {
               lvt_3_1_ = SectionPos.withOffset(lvt_3_1_, Direction.UP);
               ++lvt_5_1_;
               if (lvt_5_1_ >= lvt_7_1_) {
                  return 15;
               }

               p_215525_1_ = BlockPos.offset(p_215525_1_, 0, 16, 0);
            }
         }

         return lvt_8_1_.get(SectionPos.mask(BlockPos.unpackX(p_215525_1_)), SectionPos.mask(BlockPos.unpackY(p_215525_1_)), SectionPos.mask(BlockPos.unpackZ(p_215525_1_)));
      } else {
         return 15;
      }
   }

   protected void func_215524_j(long p_215524_1_) {
      int lvt_3_1_ = SectionPos.extractY(p_215524_1_);
      if (((SkyLightStorage.StorageMap)this.cachedLightData).field_215652_b > lvt_3_1_) {
         ((SkyLightStorage.StorageMap)this.cachedLightData).field_215652_b = lvt_3_1_;
         ((SkyLightStorage.StorageMap)this.cachedLightData).field_215653_c.defaultReturnValue(((SkyLightStorage.StorageMap)this.cachedLightData).field_215652_b);
      }

      long lvt_4_1_ = SectionPos.toSectionColumnPos(p_215524_1_);
      int lvt_6_1_ = ((SkyLightStorage.StorageMap)this.cachedLightData).field_215653_c.get(lvt_4_1_);
      if (lvt_6_1_ < lvt_3_1_ + 1) {
         ((SkyLightStorage.StorageMap)this.cachedLightData).field_215653_c.put(lvt_4_1_, lvt_3_1_ + 1);
         if (this.field_215558_o.contains(lvt_4_1_)) {
            this.func_223404_q(p_215524_1_);
            if (lvt_6_1_ > ((SkyLightStorage.StorageMap)this.cachedLightData).field_215652_b) {
               long lvt_7_1_ = SectionPos.asLong(SectionPos.extractX(p_215524_1_), lvt_6_1_ - 1, SectionPos.extractZ(p_215524_1_));
               this.func_223403_p(lvt_7_1_);
            }

            this.func_215552_e();
         }
      }

   }

   private void func_223403_p(long p_223403_1_) {
      this.field_215557_n.add(p_223403_1_);
      this.field_215556_m.remove(p_223403_1_);
   }

   private void func_223404_q(long p_223404_1_) {
      this.field_215556_m.add(p_223404_1_);
      this.field_215557_n.remove(p_223404_1_);
   }

   private void func_215552_e() {
      this.field_215553_p = !this.field_215556_m.isEmpty() || !this.field_215557_n.isEmpty();
   }

   protected void func_215523_k(long p_215523_1_) {
      long lvt_3_1_ = SectionPos.toSectionColumnPos(p_215523_1_);
      boolean lvt_5_1_ = this.field_215558_o.contains(lvt_3_1_);
      if (lvt_5_1_) {
         this.func_223403_p(p_215523_1_);
      }

      int lvt_6_1_ = SectionPos.extractY(p_215523_1_);
      if (((SkyLightStorage.StorageMap)this.cachedLightData).field_215653_c.get(lvt_3_1_) == lvt_6_1_ + 1) {
         long lvt_7_1_;
         for(lvt_7_1_ = p_215523_1_; !this.hasSection(lvt_7_1_) && this.func_215550_a(lvt_6_1_); lvt_7_1_ = SectionPos.withOffset(lvt_7_1_, Direction.DOWN)) {
            --lvt_6_1_;
         }

         if (this.hasSection(lvt_7_1_)) {
            ((SkyLightStorage.StorageMap)this.cachedLightData).field_215653_c.put(lvt_3_1_, lvt_6_1_ + 1);
            if (lvt_5_1_) {
               this.func_223404_q(lvt_7_1_);
            }
         } else {
            ((SkyLightStorage.StorageMap)this.cachedLightData).field_215653_c.remove(lvt_3_1_);
         }
      }

      if (lvt_5_1_) {
         this.func_215552_e();
      }

   }

   protected void func_215526_b(long p_215526_1_, boolean p_215526_3_) {
      this.processAllLevelUpdates();
      if (p_215526_3_ && this.field_215558_o.add(p_215526_1_)) {
         int lvt_4_1_ = ((SkyLightStorage.StorageMap)this.cachedLightData).field_215653_c.get(p_215526_1_);
         if (lvt_4_1_ != ((SkyLightStorage.StorageMap)this.cachedLightData).field_215652_b) {
            long lvt_5_1_ = SectionPos.asLong(SectionPos.extractX(p_215526_1_), lvt_4_1_ - 1, SectionPos.extractZ(p_215526_1_));
            this.func_223404_q(lvt_5_1_);
            this.func_215552_e();
         }
      } else if (!p_215526_3_) {
         this.field_215558_o.remove(p_215526_1_);
      }

   }

   protected boolean hasSectionsToUpdate() {
      return super.hasSectionsToUpdate() || this.field_215553_p;
   }

   protected NibbleArray getOrCreateArray(long p_215530_1_) {
      NibbleArray lvt_3_1_ = (NibbleArray)this.newArrays.get(p_215530_1_);
      if (lvt_3_1_ != null) {
         return lvt_3_1_;
      } else {
         long lvt_4_1_ = SectionPos.withOffset(p_215530_1_, Direction.UP);
         int lvt_6_1_ = ((SkyLightStorage.StorageMap)this.cachedLightData).field_215653_c.get(SectionPos.toSectionColumnPos(p_215530_1_));
         if (lvt_6_1_ != ((SkyLightStorage.StorageMap)this.cachedLightData).field_215652_b && SectionPos.extractY(lvt_4_1_) < lvt_6_1_) {
            NibbleArray lvt_7_1_;
            while((lvt_7_1_ = this.getArray(lvt_4_1_, true)) == null) {
               lvt_4_1_ = SectionPos.withOffset(lvt_4_1_, Direction.UP);
            }

            return new NibbleArray((new NibbleArrayRepeater(lvt_7_1_, 0)).getData());
         } else {
            return new NibbleArray();
         }
      }
   }

   protected void updateSections(LightEngine<SkyLightStorage.StorageMap, ?> p_215522_1_, boolean p_215522_2_, boolean p_215522_3_) {
      super.updateSections(p_215522_1_, p_215522_2_, p_215522_3_);
      if (p_215522_2_) {
         LongIterator var4;
         long lvt_5_1_;
         int lvt_7_2_;
         int lvt_8_1_;
         if (!this.field_215556_m.isEmpty()) {
            var4 = this.field_215556_m.iterator();

            label160:
            while(true) {
               while(true) {
                  do {
                     do {
                        do {
                           if (!var4.hasNext()) {
                              break label160;
                           }

                           lvt_5_1_ = (Long)var4.next();
                           lvt_7_2_ = this.getLevel(lvt_5_1_);
                        } while(lvt_7_2_ == 2);
                     } while(this.field_215557_n.contains(lvt_5_1_));
                  } while(!this.field_215555_l.add(lvt_5_1_));

                  int lvt_9_1_;
                  if (lvt_7_2_ == 1) {
                     this.cancelSectionUpdates(p_215522_1_, lvt_5_1_);
                     if (this.dirtyCachedSections.add(lvt_5_1_)) {
                        ((SkyLightStorage.StorageMap)this.cachedLightData).copyArray(lvt_5_1_);
                     }

                     Arrays.fill(this.getArray(lvt_5_1_, true).getData(), (byte)-1);
                     lvt_8_1_ = SectionPos.toWorld(SectionPos.extractX(lvt_5_1_));
                     lvt_9_1_ = SectionPos.toWorld(SectionPos.extractY(lvt_5_1_));
                     int lvt_10_1_ = SectionPos.toWorld(SectionPos.extractZ(lvt_5_1_));
                     Direction[] var11 = field_215554_k;
                     int lvt_12_1_ = var11.length;

                     long lvt_15_2_;
                     for(int var13 = 0; var13 < lvt_12_1_; ++var13) {
                        Direction lvt_14_1_ = var11[var13];
                        lvt_15_2_ = SectionPos.withOffset(lvt_5_1_, lvt_14_1_);
                        if ((this.field_215557_n.contains(lvt_15_2_) || !this.field_215555_l.contains(lvt_15_2_) && !this.field_215556_m.contains(lvt_15_2_)) && this.hasSection(lvt_15_2_)) {
                           for(int lvt_17_1_ = 0; lvt_17_1_ < 16; ++lvt_17_1_) {
                              for(int lvt_18_1_ = 0; lvt_18_1_ < 16; ++lvt_18_1_) {
                                 long lvt_19_4_;
                                 long lvt_21_4_;
                                 switch(lvt_14_1_) {
                                 case NORTH:
                                    lvt_19_4_ = BlockPos.pack(lvt_8_1_ + lvt_17_1_, lvt_9_1_ + lvt_18_1_, lvt_10_1_);
                                    lvt_21_4_ = BlockPos.pack(lvt_8_1_ + lvt_17_1_, lvt_9_1_ + lvt_18_1_, lvt_10_1_ - 1);
                                    break;
                                 case SOUTH:
                                    lvt_19_4_ = BlockPos.pack(lvt_8_1_ + lvt_17_1_, lvt_9_1_ + lvt_18_1_, lvt_10_1_ + 16 - 1);
                                    lvt_21_4_ = BlockPos.pack(lvt_8_1_ + lvt_17_1_, lvt_9_1_ + lvt_18_1_, lvt_10_1_ + 16);
                                    break;
                                 case WEST:
                                    lvt_19_4_ = BlockPos.pack(lvt_8_1_, lvt_9_1_ + lvt_17_1_, lvt_10_1_ + lvt_18_1_);
                                    lvt_21_4_ = BlockPos.pack(lvt_8_1_ - 1, lvt_9_1_ + lvt_17_1_, lvt_10_1_ + lvt_18_1_);
                                    break;
                                 default:
                                    lvt_19_4_ = BlockPos.pack(lvt_8_1_ + 16 - 1, lvt_9_1_ + lvt_17_1_, lvt_10_1_ + lvt_18_1_);
                                    lvt_21_4_ = BlockPos.pack(lvt_8_1_ + 16, lvt_9_1_ + lvt_17_1_, lvt_10_1_ + lvt_18_1_);
                                 }

                                 p_215522_1_.scheduleUpdate(lvt_19_4_, lvt_21_4_, p_215522_1_.getEdgeLevel(lvt_19_4_, lvt_21_4_, 0), true);
                              }
                           }
                        }
                     }

                     for(int lvt_11_1_ = 0; lvt_11_1_ < 16; ++lvt_11_1_) {
                        for(lvt_12_1_ = 0; lvt_12_1_ < 16; ++lvt_12_1_) {
                           long lvt_13_1_ = BlockPos.pack(SectionPos.toWorld(SectionPos.extractX(lvt_5_1_)) + lvt_11_1_, SectionPos.toWorld(SectionPos.extractY(lvt_5_1_)), SectionPos.toWorld(SectionPos.extractZ(lvt_5_1_)) + lvt_12_1_);
                           lvt_15_2_ = BlockPos.pack(SectionPos.toWorld(SectionPos.extractX(lvt_5_1_)) + lvt_11_1_, SectionPos.toWorld(SectionPos.extractY(lvt_5_1_)) - 1, SectionPos.toWorld(SectionPos.extractZ(lvt_5_1_)) + lvt_12_1_);
                           p_215522_1_.scheduleUpdate(lvt_13_1_, lvt_15_2_, p_215522_1_.getEdgeLevel(lvt_13_1_, lvt_15_2_, 0), true);
                        }
                     }
                  } else {
                     for(lvt_8_1_ = 0; lvt_8_1_ < 16; ++lvt_8_1_) {
                        for(lvt_9_1_ = 0; lvt_9_1_ < 16; ++lvt_9_1_) {
                           long lvt_10_2_ = BlockPos.pack(SectionPos.toWorld(SectionPos.extractX(lvt_5_1_)) + lvt_8_1_, SectionPos.toWorld(SectionPos.extractY(lvt_5_1_)) + 16 - 1, SectionPos.toWorld(SectionPos.extractZ(lvt_5_1_)) + lvt_9_1_);
                           p_215522_1_.scheduleUpdate(Long.MAX_VALUE, lvt_10_2_, 0, true);
                        }
                     }
                  }
               }
            }
         }

         this.field_215556_m.clear();
         if (!this.field_215557_n.isEmpty()) {
            var4 = this.field_215557_n.iterator();

            label90:
            while(true) {
               do {
                  do {
                     if (!var4.hasNext()) {
                        break label90;
                     }

                     lvt_5_1_ = (Long)var4.next();
                  } while(!this.field_215555_l.remove(lvt_5_1_));
               } while(!this.hasSection(lvt_5_1_));

               for(lvt_7_2_ = 0; lvt_7_2_ < 16; ++lvt_7_2_) {
                  for(lvt_8_1_ = 0; lvt_8_1_ < 16; ++lvt_8_1_) {
                     long lvt_9_3_ = BlockPos.pack(SectionPos.toWorld(SectionPos.extractX(lvt_5_1_)) + lvt_7_2_, SectionPos.toWorld(SectionPos.extractY(lvt_5_1_)) + 16 - 1, SectionPos.toWorld(SectionPos.extractZ(lvt_5_1_)) + lvt_8_1_);
                     p_215522_1_.scheduleUpdate(Long.MAX_VALUE, lvt_9_3_, 15, false);
                  }
               }
            }
         }

         this.field_215557_n.clear();
         this.field_215553_p = false;
      }
   }

   protected boolean func_215550_a(int p_215550_1_) {
      return p_215550_1_ >= ((SkyLightStorage.StorageMap)this.cachedLightData).field_215652_b;
   }

   protected boolean func_215551_l(long p_215551_1_) {
      int lvt_3_1_ = BlockPos.unpackY(p_215551_1_);
      if ((lvt_3_1_ & 15) != 15) {
         return false;
      } else {
         long lvt_4_1_ = SectionPos.worldToSection(p_215551_1_);
         long lvt_6_1_ = SectionPos.toSectionColumnPos(lvt_4_1_);
         if (!this.field_215558_o.contains(lvt_6_1_)) {
            return false;
         } else {
            int lvt_8_1_ = ((SkyLightStorage.StorageMap)this.cachedLightData).field_215653_c.get(lvt_6_1_);
            return SectionPos.toWorld(lvt_8_1_) == lvt_3_1_ + 16;
         }
      }
   }

   protected boolean func_215549_m(long p_215549_1_) {
      long lvt_3_1_ = SectionPos.toSectionColumnPos(p_215549_1_);
      int lvt_5_1_ = ((SkyLightStorage.StorageMap)this.cachedLightData).field_215653_c.get(lvt_3_1_);
      return lvt_5_1_ == ((SkyLightStorage.StorageMap)this.cachedLightData).field_215652_b || SectionPos.extractY(p_215549_1_) >= lvt_5_1_;
   }

   protected boolean func_215548_n(long p_215548_1_) {
      long lvt_3_1_ = SectionPos.toSectionColumnPos(p_215548_1_);
      return this.field_215558_o.contains(lvt_3_1_);
   }

   static {
      field_215554_k = new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};
   }

   public static final class StorageMap extends LightDataMap<SkyLightStorage.StorageMap> {
      private int field_215652_b;
      private final Long2IntOpenHashMap field_215653_c;

      public StorageMap(Long2ObjectOpenHashMap<NibbleArray> p_i50496_1_, Long2IntOpenHashMap p_i50496_2_, int p_i50496_3_) {
         super(p_i50496_1_);
         this.field_215653_c = p_i50496_2_;
         p_i50496_2_.defaultReturnValue(p_i50496_3_);
         this.field_215652_b = p_i50496_3_;
      }

      public SkyLightStorage.StorageMap copy() {
         return new SkyLightStorage.StorageMap(this.arrays.clone(), this.field_215653_c.clone(), this.field_215652_b);
      }

      // $FF: synthetic method
      public LightDataMap copy() {
         return this.copy();
      }
   }
}
