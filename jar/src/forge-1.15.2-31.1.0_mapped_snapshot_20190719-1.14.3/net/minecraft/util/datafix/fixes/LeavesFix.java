package net.minecraft.util.datafix.fixes;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.List.ListType;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import javax.annotation.Nullable;
import net.minecraft.util.BitArray;
import net.minecraft.util.datafix.TypeReferences;

public class LeavesFix extends DataFix {
   private static final int[][] DIRECTIONS = new int[][]{{-1, 0, 0}, {1, 0, 0}, {0, -1, 0}, {0, 1, 0}, {0, 0, -1}, {0, 0, 1}};
   private static final Object2IntMap<String> LEAVES = (Object2IntMap)DataFixUtils.make(new Object2IntOpenHashMap(), (p_208417_0_) -> {
      p_208417_0_.put("minecraft:acacia_leaves", 0);
      p_208417_0_.put("minecraft:birch_leaves", 1);
      p_208417_0_.put("minecraft:dark_oak_leaves", 2);
      p_208417_0_.put("minecraft:jungle_leaves", 3);
      p_208417_0_.put("minecraft:oak_leaves", 4);
      p_208417_0_.put("minecraft:spruce_leaves", 5);
   });
   private static final Set<String> LOGS = ImmutableSet.of("minecraft:acacia_bark", "minecraft:birch_bark", "minecraft:dark_oak_bark", "minecraft:jungle_bark", "minecraft:oak_bark", "minecraft:spruce_bark", new String[]{"minecraft:acacia_log", "minecraft:birch_log", "minecraft:dark_oak_log", "minecraft:jungle_log", "minecraft:oak_log", "minecraft:spruce_log", "minecraft:stripped_acacia_log", "minecraft:stripped_birch_log", "minecraft:stripped_dark_oak_log", "minecraft:stripped_jungle_log", "minecraft:stripped_oak_log", "minecraft:stripped_spruce_log"});

   public LeavesFix(Schema p_i49629_1_, boolean p_i49629_2_) {
      super(p_i49629_1_, p_i49629_2_);
   }

   protected TypeRewriteRule makeRule() {
      Type<?> lvt_1_1_ = this.getInputSchema().getType(TypeReferences.CHUNK);
      OpticFinder<?> lvt_2_1_ = lvt_1_1_.findField("Level");
      OpticFinder<?> lvt_3_1_ = lvt_2_1_.type().findField("Sections");
      Type<?> lvt_4_1_ = lvt_3_1_.type();
      if (!(lvt_4_1_ instanceof ListType)) {
         throw new IllegalStateException("Expecting sections to be a list.");
      } else {
         Type<?> lvt_5_1_ = ((ListType)lvt_4_1_).getElement();
         OpticFinder<?> lvt_6_1_ = DSL.typeFinder(lvt_5_1_);
         return this.fixTypeEverywhereTyped("Leaves fix", lvt_1_1_, (p_208422_4_) -> {
            return p_208422_4_.updateTyped(lvt_2_1_, (p_208420_3_) -> {
               int[] lvt_4_1_ = new int[]{0};
               Typed<?> lvt_5_1_ = p_208420_3_.updateTyped(lvt_3_1_, (p_208415_3_) -> {
                  Int2ObjectMap<LeavesFix.LeavesSection> lvt_4_1_x = new Int2ObjectOpenHashMap((Map)p_208415_3_.getAllTyped(lvt_6_1_).stream().map((p_212527_1_) -> {
                     return new LeavesFix.LeavesSection(p_212527_1_, this.getInputSchema());
                  }).collect(Collectors.toMap(LeavesFix.Section::getIndex, (p_208410_0_) -> {
                     return p_208410_0_;
                  })));
                  if (lvt_4_1_x.values().stream().allMatch(LeavesFix.Section::isSkippable)) {
                     return p_208415_3_;
                  } else {
                     List<IntSet> lvt_5_1_ = Lists.newArrayList();

                     int lvt_6_2_;
                     for(lvt_6_2_ = 0; lvt_6_2_ < 7; ++lvt_6_2_) {
                        lvt_5_1_.add(new IntOpenHashSet());
                     }

                     ObjectIterator var25 = lvt_4_1_x.values().iterator();

                     while(true) {
                        LeavesFix.LeavesSection lvt_7_1_;
                        int lvt_10_1_;
                        int lvt_11_1_;
                        do {
                           if (!var25.hasNext()) {
                              for(lvt_6_2_ = 1; lvt_6_2_ < 7; ++lvt_6_2_) {
                                 IntSet lvt_7_2_ = (IntSet)lvt_5_1_.get(lvt_6_2_ - 1);
                                 IntSet lvt_8_2_ = (IntSet)lvt_5_1_.get(lvt_6_2_);
                                 IntIterator lvt_9_2_ = lvt_7_2_.iterator();

                                 while(lvt_9_2_.hasNext()) {
                                    lvt_10_1_ = lvt_9_2_.nextInt();
                                    lvt_11_1_ = this.getX(lvt_10_1_);
                                    int lvt_12_1_ = this.getY(lvt_10_1_);
                                    int lvt_13_1_ = this.getZ(lvt_10_1_);
                                    int[][] var14 = DIRECTIONS;
                                    int var15 = var14.length;

                                    for(int var16 = 0; var16 < var15; ++var16) {
                                       int[] lvt_17_1_ = var14[var16];
                                       int lvt_18_1_ = lvt_11_1_ + lvt_17_1_[0];
                                       int lvt_19_1_ = lvt_12_1_ + lvt_17_1_[1];
                                       int lvt_20_1_ = lvt_13_1_ + lvt_17_1_[2];
                                       if (lvt_18_1_ >= 0 && lvt_18_1_ <= 15 && lvt_20_1_ >= 0 && lvt_20_1_ <= 15 && lvt_19_1_ >= 0 && lvt_19_1_ <= 255) {
                                          LeavesFix.LeavesSection lvt_21_1_ = (LeavesFix.LeavesSection)lvt_4_1_x.get(lvt_19_1_ >> 4);
                                          if (lvt_21_1_ != null && !lvt_21_1_.isSkippable()) {
                                             int lvt_22_1_ = getIndex(lvt_18_1_, lvt_19_1_ & 15, lvt_20_1_);
                                             int lvt_23_1_ = lvt_21_1_.getBlock(lvt_22_1_);
                                             if (lvt_21_1_.isLeaf(lvt_23_1_)) {
                                                int lvt_24_1_ = lvt_21_1_.getDistance(lvt_23_1_);
                                                if (lvt_24_1_ > lvt_6_2_) {
                                                   lvt_21_1_.setDistance(lvt_22_1_, lvt_23_1_, lvt_6_2_);
                                                   lvt_8_2_.add(getIndex(lvt_18_1_, lvt_19_1_, lvt_20_1_));
                                                }
                                             }
                                          }
                                       }
                                    }
                                 }
                              }

                              return p_208415_3_.updateTyped(lvt_6_1_, (p_208413_1_) -> {
                                 return ((LeavesFix.LeavesSection)lvt_4_1_x.get(((Dynamic)p_208413_1_.get(DSL.remainderFinder())).get("Y").asInt(0))).write(p_208413_1_);
                              });
                           }

                           lvt_7_1_ = (LeavesFix.LeavesSection)var25.next();
                        } while(lvt_7_1_.isSkippable());

                        for(int lvt_8_1_ = 0; lvt_8_1_ < 4096; ++lvt_8_1_) {
                           int lvt_9_1_ = lvt_7_1_.getBlock(lvt_8_1_);
                           if (lvt_7_1_.isLog(lvt_9_1_)) {
                              ((IntSet)lvt_5_1_.get(0)).add(lvt_7_1_.getIndex() << 12 | lvt_8_1_);
                           } else if (lvt_7_1_.isLeaf(lvt_9_1_)) {
                              lvt_10_1_ = this.getX(lvt_8_1_);
                              lvt_11_1_ = this.getZ(lvt_8_1_);
                              lvt_4_1_[0] |= getSideMask(lvt_10_1_ == 0, lvt_10_1_ == 15, lvt_11_1_ == 0, lvt_11_1_ == 15);
                           }
                        }
                     }
                  }
               });
               if (lvt_4_1_[0] != 0) {
                  lvt_5_1_ = lvt_5_1_.update(DSL.remainderFinder(), (p_208419_1_) -> {
                     Dynamic<?> lvt_2_1_ = (Dynamic)DataFixUtils.orElse(p_208419_1_.get("UpgradeData").get(), p_208419_1_.emptyMap());
                     return p_208419_1_.set("UpgradeData", lvt_2_1_.set("Sides", p_208419_1_.createByte((byte)(lvt_2_1_.get("Sides").asByte((byte)0) | lvt_4_1_[0]))));
                  });
               }

               return lvt_5_1_;
            });
         });
      }
   }

   public static int getIndex(int p_208411_0_, int p_208411_1_, int p_208411_2_) {
      return p_208411_1_ << 8 | p_208411_2_ << 4 | p_208411_0_;
   }

   private int getX(int p_208412_1_) {
      return p_208412_1_ & 15;
   }

   private int getY(int p_208421_1_) {
      return p_208421_1_ >> 8 & 255;
   }

   private int getZ(int p_208409_1_) {
      return p_208409_1_ >> 4 & 15;
   }

   public static int getSideMask(boolean p_210537_0_, boolean p_210537_1_, boolean p_210537_2_, boolean p_210537_3_) {
      int lvt_4_1_ = 0;
      if (p_210537_2_) {
         if (p_210537_1_) {
            lvt_4_1_ |= 2;
         } else if (p_210537_0_) {
            lvt_4_1_ |= 128;
         } else {
            lvt_4_1_ |= 1;
         }
      } else if (p_210537_3_) {
         if (p_210537_0_) {
            lvt_4_1_ |= 32;
         } else if (p_210537_1_) {
            lvt_4_1_ |= 8;
         } else {
            lvt_4_1_ |= 16;
         }
      } else if (p_210537_1_) {
         lvt_4_1_ |= 4;
      } else if (p_210537_0_) {
         lvt_4_1_ |= 64;
      }

      return lvt_4_1_;
   }

   public static final class LeavesSection extends LeavesFix.Section {
      @Nullable
      private IntSet field_212523_f;
      @Nullable
      private IntSet field_212524_g;
      @Nullable
      private Int2IntMap field_212525_h;

      public LeavesSection(Typed<?> p_i49851_1_, Schema p_i49851_2_) {
         super(p_i49851_1_, p_i49851_2_);
      }

      protected boolean func_212508_a() {
         this.field_212523_f = new IntOpenHashSet();
         this.field_212524_g = new IntOpenHashSet();
         this.field_212525_h = new Int2IntOpenHashMap();

         for(int lvt_1_1_ = 0; lvt_1_1_ < this.palette.size(); ++lvt_1_1_) {
            Dynamic<?> lvt_2_1_ = (Dynamic)this.palette.get(lvt_1_1_);
            String lvt_3_1_ = lvt_2_1_.get("Name").asString("");
            if (LeavesFix.LEAVES.containsKey(lvt_3_1_)) {
               boolean lvt_4_1_ = Objects.equals(lvt_2_1_.get("Properties").get("decayable").asString(""), "false");
               this.field_212523_f.add(lvt_1_1_);
               this.field_212525_h.put(this.getStateId(lvt_3_1_, lvt_4_1_, 7), lvt_1_1_);
               this.palette.set(lvt_1_1_, this.makeLeafTag(lvt_2_1_, lvt_3_1_, lvt_4_1_, 7));
            }

            if (LeavesFix.LOGS.contains(lvt_3_1_)) {
               this.field_212524_g.add(lvt_1_1_);
            }
         }

         return this.field_212523_f.isEmpty() && this.field_212524_g.isEmpty();
      }

      private Dynamic<?> makeLeafTag(Dynamic<?> p_209770_1_, String p_209770_2_, boolean p_209770_3_, int p_209770_4_) {
         Dynamic<?> lvt_5_1_ = p_209770_1_.emptyMap();
         lvt_5_1_ = lvt_5_1_.set("persistent", lvt_5_1_.createString(p_209770_3_ ? "true" : "false"));
         lvt_5_1_ = lvt_5_1_.set("distance", lvt_5_1_.createString(Integer.toString(p_209770_4_)));
         Dynamic<?> lvt_6_1_ = p_209770_1_.emptyMap();
         lvt_6_1_ = lvt_6_1_.set("Properties", lvt_5_1_);
         lvt_6_1_ = lvt_6_1_.set("Name", lvt_6_1_.createString(p_209770_2_));
         return lvt_6_1_;
      }

      public boolean isLog(int p_208457_1_) {
         return this.field_212524_g.contains(p_208457_1_);
      }

      public boolean isLeaf(int p_208460_1_) {
         return this.field_212523_f.contains(p_208460_1_);
      }

      private int getDistance(int p_208459_1_) {
         return this.isLog(p_208459_1_) ? 0 : Integer.parseInt(((Dynamic)this.palette.get(p_208459_1_)).get("Properties").get("distance").asString(""));
      }

      private void setDistance(int p_208454_1_, int p_208454_2_, int p_208454_3_) {
         Dynamic<?> lvt_4_1_ = (Dynamic)this.palette.get(p_208454_2_);
         String lvt_5_1_ = lvt_4_1_.get("Name").asString("");
         boolean lvt_6_1_ = Objects.equals(lvt_4_1_.get("Properties").get("persistent").asString(""), "true");
         int lvt_7_1_ = this.getStateId(lvt_5_1_, lvt_6_1_, p_208454_3_);
         int lvt_8_2_;
         if (!this.field_212525_h.containsKey(lvt_7_1_)) {
            lvt_8_2_ = this.palette.size();
            this.field_212523_f.add(lvt_8_2_);
            this.field_212525_h.put(lvt_7_1_, lvt_8_2_);
            this.palette.add(this.makeLeafTag(lvt_4_1_, lvt_5_1_, lvt_6_1_, p_208454_3_));
         }

         lvt_8_2_ = this.field_212525_h.get(lvt_7_1_);
         if (1 << this.storage.bitsPerEntry() <= lvt_8_2_) {
            BitArray lvt_9_1_ = new BitArray(this.storage.bitsPerEntry() + 1, 4096);

            for(int lvt_10_1_ = 0; lvt_10_1_ < 4096; ++lvt_10_1_) {
               lvt_9_1_.setAt(lvt_10_1_, this.storage.getAt(lvt_10_1_));
            }

            this.storage = lvt_9_1_;
         }

         this.storage.setAt(p_208454_1_, lvt_8_2_);
      }
   }

   public abstract static class Section {
      private final Type<Pair<String, Dynamic<?>>> blockStateType;
      protected final OpticFinder<List<Pair<String, Dynamic<?>>>> paletteFinder;
      protected final List<Dynamic<?>> palette;
      protected final int index;
      @Nullable
      protected BitArray storage;

      public Section(Typed<?> p_i49850_1_, Schema p_i49850_2_) {
         this.blockStateType = DSL.named(TypeReferences.BLOCK_STATE.typeName(), DSL.remainderType());
         this.paletteFinder = DSL.fieldFinder("Palette", DSL.list(this.blockStateType));
         if (!Objects.equals(p_i49850_2_.getType(TypeReferences.BLOCK_STATE), this.blockStateType)) {
            throw new IllegalStateException("Block state type is not what was expected.");
         } else {
            Optional<List<Pair<String, Dynamic<?>>>> lvt_3_1_ = p_i49850_1_.getOptional(this.paletteFinder);
            this.palette = (List)lvt_3_1_.map((p_208463_0_) -> {
               return (List)p_208463_0_.stream().map(Pair::getSecond).collect(Collectors.toList());
            }).orElse(ImmutableList.of());
            Dynamic<?> lvt_4_1_ = (Dynamic)p_i49850_1_.get(DSL.remainderFinder());
            this.index = lvt_4_1_.get("Y").asInt(0);
            this.func_212507_a(lvt_4_1_);
         }
      }

      protected void func_212507_a(Dynamic<?> p_212507_1_) {
         if (this.func_212508_a()) {
            this.storage = null;
         } else {
            long[] lvt_2_1_ = ((LongStream)p_212507_1_.get("BlockStates").asLongStreamOpt().get()).toArray();
            int lvt_3_1_ = Math.max(4, DataFixUtils.ceillog2(this.palette.size()));
            this.storage = new BitArray(lvt_3_1_, 4096, lvt_2_1_);
         }

      }

      public Typed<?> write(Typed<?> p_208465_1_) {
         return this.isSkippable() ? p_208465_1_ : p_208465_1_.update(DSL.remainderFinder(), (p_212510_1_) -> {
            return p_212510_1_.set("BlockStates", p_212510_1_.createLongList(Arrays.stream(this.storage.getBackingLongArray())));
         }).set(this.paletteFinder, this.palette.stream().map((p_212509_0_) -> {
            return Pair.of(TypeReferences.BLOCK_STATE.typeName(), p_212509_0_);
         }).collect(Collectors.toList()));
      }

      public boolean isSkippable() {
         return this.storage == null;
      }

      public int getBlock(int p_208453_1_) {
         return this.storage.getAt(p_208453_1_);
      }

      protected int getStateId(String p_208464_1_, boolean p_208464_2_, int p_208464_3_) {
         return LeavesFix.LEAVES.get(p_208464_1_) << 5 | (p_208464_2_ ? 16 : 0) | p_208464_3_;
      }

      int getIndex() {
         return this.index;
      }

      protected abstract boolean func_212508_a();
   }
}
