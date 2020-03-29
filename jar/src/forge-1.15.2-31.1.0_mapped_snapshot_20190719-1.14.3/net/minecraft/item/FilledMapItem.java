package net.minecraft.item;

import com.google.common.collect.Iterables;
import com.google.common.collect.LinkedHashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.IFluidState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class FilledMapItem extends AbstractMapItem {
   public FilledMapItem(Item.Properties p_i48482_1_) {
      super(p_i48482_1_);
   }

   public static ItemStack setupNewMap(World p_195952_0_, int p_195952_1_, int p_195952_2_, byte p_195952_3_, boolean p_195952_4_, boolean p_195952_5_) {
      ItemStack itemstack = new ItemStack(Items.FILLED_MAP);
      createMapData(itemstack, p_195952_0_, p_195952_1_, p_195952_2_, p_195952_3_, p_195952_4_, p_195952_5_, p_195952_0_.dimension.getType());
      return itemstack;
   }

   @Nullable
   public static MapData func_219994_a(ItemStack p_219994_0_, World p_219994_1_) {
      return p_219994_1_.func_217406_a(func_219993_a(getMapId(p_219994_0_)));
   }

   @Nullable
   public static MapData getMapData(ItemStack p_195950_0_, World p_195950_1_) {
      Item map = p_195950_0_.getItem();
      return map instanceof FilledMapItem ? ((FilledMapItem)map).getCustomMapData(p_195950_0_, p_195950_1_) : null;
   }

   @Nullable
   protected MapData getCustomMapData(ItemStack p_getCustomMapData_1_, World p_getCustomMapData_2_) {
      MapData mapdata = func_219994_a(p_getCustomMapData_1_, p_getCustomMapData_2_);
      if (mapdata == null && !p_getCustomMapData_2_.isRemote) {
         mapdata = createMapData(p_getCustomMapData_1_, p_getCustomMapData_2_, p_getCustomMapData_2_.getWorldInfo().getSpawnX(), p_getCustomMapData_2_.getWorldInfo().getSpawnZ(), 3, false, false, p_getCustomMapData_2_.dimension.getType());
      }

      return mapdata;
   }

   public static int getMapId(ItemStack p_195949_0_) {
      CompoundNBT compoundnbt = p_195949_0_.getTag();
      return compoundnbt != null && compoundnbt.contains("map", 99) ? compoundnbt.getInt("map") : 0;
   }

   private static MapData createMapData(ItemStack p_195951_0_, World p_195951_1_, int p_195951_2_, int p_195951_3_, int p_195951_4_, boolean p_195951_5_, boolean p_195951_6_, DimensionType p_195951_7_) {
      int i = p_195951_1_.getNextMapId();
      MapData mapdata = new MapData(func_219993_a(i));
      mapdata.func_212440_a(p_195951_2_, p_195951_3_, p_195951_4_, p_195951_5_, p_195951_6_, p_195951_7_);
      p_195951_1_.func_217399_a(mapdata);
      p_195951_0_.getOrCreateTag().putInt("map", i);
      return mapdata;
   }

   public static String func_219993_a(int p_219993_0_) {
      return "map_" + p_219993_0_;
   }

   public void updateMapData(World p_77872_1_, Entity p_77872_2_, MapData p_77872_3_) {
      if (p_77872_1_.dimension.getType() == p_77872_3_.dimension && p_77872_2_ instanceof PlayerEntity) {
         int i = 1 << p_77872_3_.scale;
         int j = p_77872_3_.xCenter;
         int k = p_77872_3_.zCenter;
         int l = MathHelper.floor(p_77872_2_.func_226277_ct_() - (double)j) / i + 64;
         int i1 = MathHelper.floor(p_77872_2_.func_226281_cx_() - (double)k) / i + 64;
         int j1 = 128 / i;
         if (p_77872_1_.dimension.isNether()) {
            j1 /= 2;
         }

         MapData.MapInfo mapdata$mapinfo = p_77872_3_.getMapInfo((PlayerEntity)p_77872_2_);
         ++mapdata$mapinfo.step;
         boolean flag = false;

         for(int k1 = l - j1 + 1; k1 < l + j1; ++k1) {
            if ((k1 & 15) == (mapdata$mapinfo.step & 15) || flag) {
               flag = false;
               double d0 = 0.0D;

               for(int l1 = i1 - j1 - 1; l1 < i1 + j1; ++l1) {
                  if (k1 >= 0 && l1 >= -1 && k1 < 128 && l1 < 128) {
                     int i2 = k1 - l;
                     int j2 = l1 - i1;
                     boolean flag1 = i2 * i2 + j2 * j2 > (j1 - 2) * (j1 - 2);
                     int k2 = (j / i + k1 - 64) * i;
                     int l2 = (k / i + l1 - 64) * i;
                     Multiset<MaterialColor> multiset = LinkedHashMultiset.create();
                     Chunk chunk = p_77872_1_.getChunkAt(new BlockPos(k2, 0, l2));
                     if (!chunk.isEmpty()) {
                        ChunkPos chunkpos = chunk.getPos();
                        int i3 = k2 & 15;
                        int j3 = l2 & 15;
                        int k3 = 0;
                        double d1 = 0.0D;
                        if (p_77872_1_.dimension.isNether()) {
                           int l3 = k2 + l2 * 231871;
                           l3 = l3 * l3 * 31287121 + l3 * 11;
                           if ((l3 >> 20 & 1) == 0) {
                              multiset.add(Blocks.DIRT.getDefaultState().getMaterialColor(p_77872_1_, BlockPos.ZERO), 10);
                           } else {
                              multiset.add(Blocks.STONE.getDefaultState().getMaterialColor(p_77872_1_, BlockPos.ZERO), 100);
                           }

                           d1 = 100.0D;
                        } else {
                           BlockPos.Mutable blockpos$mutable1 = new BlockPos.Mutable();
                           BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

                           for(int i4 = 0; i4 < i; ++i4) {
                              for(int j4 = 0; j4 < i; ++j4) {
                                 int k4 = chunk.getTopBlockY(Heightmap.Type.WORLD_SURFACE, i4 + i3, j4 + j3) + 1;
                                 BlockState blockstate;
                                 if (k4 <= 1) {
                                    blockstate = Blocks.BEDROCK.getDefaultState();
                                 } else {
                                    do {
                                       --k4;
                                       blockpos$mutable1.setPos(chunkpos.getXStart() + i4 + i3, k4, chunkpos.getZStart() + j4 + j3);
                                       blockstate = chunk.getBlockState(blockpos$mutable1);
                                    } while(blockstate.getMaterialColor(p_77872_1_, blockpos$mutable1) == MaterialColor.AIR && k4 > 0);

                                    if (k4 > 0 && !blockstate.getFluidState().isEmpty()) {
                                       int l4 = k4 - 1;
                                       blockpos$mutable.setPos((Vec3i)blockpos$mutable1);

                                       BlockState blockstate1;
                                       do {
                                          blockpos$mutable.setY(l4--);
                                          blockstate1 = chunk.getBlockState(blockpos$mutable);
                                          ++k3;
                                       } while(l4 > 0 && !blockstate1.getFluidState().isEmpty());

                                       blockstate = this.func_211698_a(p_77872_1_, blockstate, blockpos$mutable1);
                                    }
                                 }

                                 p_77872_3_.removeStaleBanners(p_77872_1_, chunkpos.getXStart() + i4 + i3, chunkpos.getZStart() + j4 + j3);
                                 d1 += (double)k4 / (double)(i * i);
                                 multiset.add(blockstate.getMaterialColor(p_77872_1_, blockpos$mutable1));
                              }
                           }
                        }

                        k3 /= i * i;
                        double d2 = (d1 - d0) * 4.0D / (double)(i + 4) + ((double)(k1 + l1 & 1) - 0.5D) * 0.4D;
                        int i5 = 1;
                        if (d2 > 0.6D) {
                           i5 = 2;
                        }

                        if (d2 < -0.6D) {
                           i5 = 0;
                        }

                        MaterialColor materialcolor = (MaterialColor)Iterables.getFirst(Multisets.copyHighestCountFirst(multiset), MaterialColor.AIR);
                        if (materialcolor == MaterialColor.WATER) {
                           d2 = (double)k3 * 0.1D + (double)(k1 + l1 & 1) * 0.2D;
                           i5 = 1;
                           if (d2 < 0.5D) {
                              i5 = 2;
                           }

                           if (d2 > 0.9D) {
                              i5 = 0;
                           }
                        }

                        d0 = d1;
                        if (l1 >= 0 && i2 * i2 + j2 * j2 < j1 * j1 && (!flag1 || (k1 + l1 & 1) != 0)) {
                           byte b0 = p_77872_3_.colors[k1 + l1 * 128];
                           byte b1 = (byte)(materialcolor.colorIndex * 4 + i5);
                           if (b0 != b1) {
                              p_77872_3_.colors[k1 + l1 * 128] = b1;
                              p_77872_3_.updateMapData(k1, l1);
                              flag = true;
                           }
                        }
                     }
                  }
               }
            }
         }
      }

   }

   private BlockState func_211698_a(World p_211698_1_, BlockState p_211698_2_, BlockPos p_211698_3_) {
      IFluidState ifluidstate = p_211698_2_.getFluidState();
      return !ifluidstate.isEmpty() && !p_211698_2_.func_224755_d(p_211698_1_, p_211698_3_, Direction.UP) ? ifluidstate.getBlockState() : p_211698_2_;
   }

   private static boolean func_195954_a(Biome[] p_195954_0_, int p_195954_1_, int p_195954_2_, int p_195954_3_) {
      return p_195954_0_[p_195954_2_ * p_195954_1_ + p_195954_3_ * p_195954_1_ * 128 * p_195954_1_].getDepth() >= 0.0F;
   }

   public static void func_226642_a_(ServerWorld p_226642_0_, ItemStack p_226642_1_) {
      MapData mapdata = getMapData(p_226642_1_, p_226642_0_);
      if (mapdata != null && p_226642_0_.dimension.getType() == mapdata.dimension) {
         int i = 1 << mapdata.scale;
         int j = mapdata.xCenter;
         int k = mapdata.zCenter;
         Biome[] abiome = new Biome[128 * i * 128 * i];

         int l1;
         int i2;
         for(l1 = 0; l1 < 128 * i; ++l1) {
            for(i2 = 0; i2 < 128 * i; ++i2) {
               abiome[l1 * 128 * i + i2] = p_226642_0_.func_226691_t_(new BlockPos((j / i - 64) * i + i2, 0, (k / i - 64) * i + l1));
            }
         }

         for(l1 = 0; l1 < 128; ++l1) {
            for(i2 = 0; i2 < 128; ++i2) {
               if (l1 > 0 && i2 > 0 && l1 < 127 && i2 < 127) {
                  Biome biome = abiome[l1 * i + i2 * i * 128 * i];
                  int j1 = 8;
                  if (func_195954_a(abiome, i, l1 - 1, i2 - 1)) {
                     --j1;
                  }

                  if (func_195954_a(abiome, i, l1 - 1, i2 + 1)) {
                     --j1;
                  }

                  if (func_195954_a(abiome, i, l1 - 1, i2)) {
                     --j1;
                  }

                  if (func_195954_a(abiome, i, l1 + 1, i2 - 1)) {
                     --j1;
                  }

                  if (func_195954_a(abiome, i, l1 + 1, i2 + 1)) {
                     --j1;
                  }

                  if (func_195954_a(abiome, i, l1 + 1, i2)) {
                     --j1;
                  }

                  if (func_195954_a(abiome, i, l1, i2 - 1)) {
                     --j1;
                  }

                  if (func_195954_a(abiome, i, l1, i2 + 1)) {
                     --j1;
                  }

                  int k1 = 3;
                  MaterialColor materialcolor = MaterialColor.AIR;
                  if (biome.getDepth() < 0.0F) {
                     materialcolor = MaterialColor.ADOBE;
                     if (j1 > 7 && i2 % 2 == 0) {
                        k1 = (l1 + (int)(MathHelper.sin((float)i2 + 0.0F) * 7.0F)) / 8 % 5;
                        if (k1 == 3) {
                           k1 = 1;
                        } else if (k1 == 4) {
                           k1 = 0;
                        }
                     } else if (j1 > 7) {
                        materialcolor = MaterialColor.AIR;
                     } else if (j1 > 5) {
                        k1 = 1;
                     } else if (j1 > 3) {
                        k1 = 0;
                     } else if (j1 > 1) {
                        k1 = 0;
                     }
                  } else if (j1 > 0) {
                     materialcolor = MaterialColor.BROWN;
                     if (j1 > 3) {
                        k1 = 1;
                     } else {
                        k1 = 3;
                     }
                  }

                  if (materialcolor != MaterialColor.AIR) {
                     mapdata.colors[l1 + i2 * 128] = (byte)(materialcolor.colorIndex * 4 + k1);
                     mapdata.updateMapData(l1, i2);
                  }
               }
            }
         }
      }

   }

   public void inventoryTick(ItemStack p_77663_1_, World p_77663_2_, Entity p_77663_3_, int p_77663_4_, boolean p_77663_5_) {
      if (!p_77663_2_.isRemote) {
         MapData mapdata = getMapData(p_77663_1_, p_77663_2_);
         if (mapdata != null) {
            if (p_77663_3_ instanceof PlayerEntity) {
               PlayerEntity playerentity = (PlayerEntity)p_77663_3_;
               mapdata.updateVisiblePlayers(playerentity, p_77663_1_);
            }

            if (!mapdata.locked && (p_77663_5_ || p_77663_3_ instanceof PlayerEntity && ((PlayerEntity)p_77663_3_).getHeldItemOffhand() == p_77663_1_)) {
               this.updateMapData(p_77663_2_, p_77663_3_, mapdata);
            }
         }
      }

   }

   @Nullable
   public IPacket<?> getUpdatePacket(ItemStack p_150911_1_, World p_150911_2_, PlayerEntity p_150911_3_) {
      return getMapData(p_150911_1_, p_150911_2_).getMapPacket(p_150911_1_, p_150911_2_, p_150911_3_);
   }

   public void onCreated(ItemStack p_77622_1_, World p_77622_2_, PlayerEntity p_77622_3_) {
      CompoundNBT compoundnbt = p_77622_1_.getTag();
      if (compoundnbt != null && compoundnbt.contains("map_scale_direction", 99)) {
         scaleMap(p_77622_1_, p_77622_2_, compoundnbt.getInt("map_scale_direction"));
         compoundnbt.remove("map_scale_direction");
      }

   }

   protected static void scaleMap(ItemStack p_185063_0_, World p_185063_1_, int p_185063_2_) {
      MapData mapdata = getMapData(p_185063_0_, p_185063_1_);
      if (mapdata != null) {
         createMapData(p_185063_0_, p_185063_1_, mapdata.xCenter, mapdata.zCenter, MathHelper.clamp(mapdata.scale + p_185063_2_, 0, 4), mapdata.trackingPosition, mapdata.unlimitedTracking, mapdata.dimension);
      }

   }

   @Nullable
   public static ItemStack func_219992_b(World p_219992_0_, ItemStack p_219992_1_) {
      MapData mapdata = getMapData(p_219992_1_, p_219992_0_);
      if (mapdata != null) {
         ItemStack itemstack = p_219992_1_.copy();
         MapData mapdata1 = createMapData(itemstack, p_219992_0_, 0, 0, mapdata.scale, mapdata.trackingPosition, mapdata.unlimitedTracking, mapdata.dimension);
         mapdata1.func_215160_a(mapdata);
         return itemstack;
      } else {
         return null;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void addInformation(ItemStack p_77624_1_, @Nullable World p_77624_2_, List<ITextComponent> p_77624_3_, ITooltipFlag p_77624_4_) {
      MapData mapdata = p_77624_2_ == null ? null : getMapData(p_77624_1_, p_77624_2_);
      if (mapdata != null && mapdata.locked) {
         p_77624_3_.add((new TranslationTextComponent("filled_map.locked", new Object[]{getMapId(p_77624_1_)})).applyTextStyle(TextFormatting.GRAY));
      }

      if (p_77624_4_.isAdvanced()) {
         if (mapdata != null) {
            p_77624_3_.add((new TranslationTextComponent("filled_map.id", new Object[]{getMapId(p_77624_1_)})).applyTextStyle(TextFormatting.GRAY));
            p_77624_3_.add((new TranslationTextComponent("filled_map.scale", new Object[]{1 << mapdata.scale})).applyTextStyle(TextFormatting.GRAY));
            p_77624_3_.add((new TranslationTextComponent("filled_map.level", new Object[]{mapdata.scale, 4})).applyTextStyle(TextFormatting.GRAY));
         } else {
            p_77624_3_.add((new TranslationTextComponent("filled_map.unknown", new Object[0])).applyTextStyle(TextFormatting.GRAY));
         }
      }

   }

   @OnlyIn(Dist.CLIENT)
   public static int getColor(ItemStack p_190907_0_) {
      CompoundNBT compoundnbt = p_190907_0_.getChildTag("display");
      if (compoundnbt != null && compoundnbt.contains("MapColor", 99)) {
         int i = compoundnbt.getInt("MapColor");
         return -16777216 | i & 16777215;
      } else {
         return -12173266;
      }
   }

   public ActionResultType onItemUse(ItemUseContext p_195939_1_) {
      BlockState blockstate = p_195939_1_.getWorld().getBlockState(p_195939_1_.getPos());
      if (blockstate.isIn(BlockTags.BANNERS)) {
         if (!p_195939_1_.world.isRemote) {
            MapData mapdata = getMapData(p_195939_1_.getItem(), p_195939_1_.getWorld());
            mapdata.tryAddBanner(p_195939_1_.getWorld(), p_195939_1_.getPos());
         }

         return ActionResultType.SUCCESS;
      } else {
         return super.onItemUse(p_195939_1_);
      }
   }
}
