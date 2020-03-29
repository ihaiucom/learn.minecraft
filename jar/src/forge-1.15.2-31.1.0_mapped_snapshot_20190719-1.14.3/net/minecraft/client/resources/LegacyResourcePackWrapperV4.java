package net.minecraft.client.resources;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.resources.IResourcePack;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.resources.data.IMetadataSectionSerializer;
import net.minecraft.state.properties.ChestType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LegacyResourcePackWrapperV4 implements IResourcePack {
   private static final Map<String, Pair<ChestType, ResourceLocation>> field_229279_d_ = (Map)Util.make(Maps.newHashMap(), (p_229288_0_) -> {
      p_229288_0_.put("textures/entity/chest/normal_left.png", new Pair(ChestType.LEFT, new ResourceLocation("textures/entity/chest/normal_double.png")));
      p_229288_0_.put("textures/entity/chest/normal_right.png", new Pair(ChestType.RIGHT, new ResourceLocation("textures/entity/chest/normal_double.png")));
      p_229288_0_.put("textures/entity/chest/normal.png", new Pair(ChestType.SINGLE, new ResourceLocation("textures/entity/chest/normal.png")));
      p_229288_0_.put("textures/entity/chest/trapped_left.png", new Pair(ChestType.LEFT, new ResourceLocation("textures/entity/chest/trapped_double.png")));
      p_229288_0_.put("textures/entity/chest/trapped_right.png", new Pair(ChestType.RIGHT, new ResourceLocation("textures/entity/chest/trapped_double.png")));
      p_229288_0_.put("textures/entity/chest/trapped.png", new Pair(ChestType.SINGLE, new ResourceLocation("textures/entity/chest/trapped.png")));
      p_229288_0_.put("textures/entity/chest/christmas_left.png", new Pair(ChestType.LEFT, new ResourceLocation("textures/entity/chest/christmas_double.png")));
      p_229288_0_.put("textures/entity/chest/christmas_right.png", new Pair(ChestType.RIGHT, new ResourceLocation("textures/entity/chest/christmas_double.png")));
      p_229288_0_.put("textures/entity/chest/christmas.png", new Pair(ChestType.SINGLE, new ResourceLocation("textures/entity/chest/christmas.png")));
      p_229288_0_.put("textures/entity/chest/ender.png", new Pair(ChestType.SINGLE, new ResourceLocation("textures/entity/chest/ender.png")));
   });
   private static final List<String> field_229280_e_ = Lists.newArrayList(new String[]{"base", "border", "bricks", "circle", "creeper", "cross", "curly_border", "diagonal_left", "diagonal_right", "diagonal_up_left", "diagonal_up_right", "flower", "globe", "gradient", "gradient_up", "half_horizontal", "half_horizontal_bottom", "half_vertical", "half_vertical_right", "mojang", "rhombus", "skull", "small_stripes", "square_bottom_left", "square_bottom_right", "square_top_left", "square_top_right", "straight_cross", "stripe_bottom", "stripe_center", "stripe_downleft", "stripe_downright", "stripe_left", "stripe_middle", "stripe_right", "stripe_top", "triangle_bottom", "triangle_top", "triangles_bottom", "triangles_top"});
   private static final Set<String> field_229281_f_;
   private static final Set<String> field_229282_g_;
   public static final ResourceLocation field_229276_a_;
   public static final ResourceLocation field_229277_b_;
   public static final ResourceLocation field_229278_c_;
   private final IResourcePack field_229283_h_;

   public LegacyResourcePackWrapperV4(IResourcePack p_i226053_1_) {
      this.field_229283_h_ = p_i226053_1_;
   }

   public InputStream getRootResourceStream(String p_195763_1_) throws IOException {
      return this.field_229283_h_.getRootResourceStream(p_195763_1_);
   }

   public boolean resourceExists(ResourcePackType p_195764_1_, ResourceLocation p_195764_2_) {
      if (!"minecraft".equals(p_195764_2_.getNamespace())) {
         return this.field_229283_h_.resourceExists(p_195764_1_, p_195764_2_);
      } else {
         String lvt_3_1_ = p_195764_2_.getPath();
         if ("textures/misc/enchanted_item_glint.png".equals(lvt_3_1_)) {
            return false;
         } else if ("textures/entity/iron_golem/iron_golem.png".equals(lvt_3_1_)) {
            return this.field_229283_h_.resourceExists(p_195764_1_, field_229278_c_);
         } else if (!"textures/entity/conduit/wind.png".equals(lvt_3_1_) && !"textures/entity/conduit/wind_vertical.png".equals(lvt_3_1_)) {
            if (field_229281_f_.contains(lvt_3_1_)) {
               return this.field_229283_h_.resourceExists(p_195764_1_, field_229276_a_) && this.field_229283_h_.resourceExists(p_195764_1_, p_195764_2_);
            } else if (!field_229282_g_.contains(lvt_3_1_)) {
               Pair<ChestType, ResourceLocation> lvt_4_1_ = (Pair)field_229279_d_.get(lvt_3_1_);
               return lvt_4_1_ != null && this.field_229283_h_.resourceExists(p_195764_1_, (ResourceLocation)lvt_4_1_.getSecond()) ? true : this.field_229283_h_.resourceExists(p_195764_1_, p_195764_2_);
            } else {
               return this.field_229283_h_.resourceExists(p_195764_1_, field_229277_b_) && this.field_229283_h_.resourceExists(p_195764_1_, p_195764_2_);
            }
         } else {
            return false;
         }
      }
   }

   public InputStream getResourceStream(ResourcePackType p_195761_1_, ResourceLocation p_195761_2_) throws IOException {
      if (!"minecraft".equals(p_195761_2_.getNamespace())) {
         return this.field_229283_h_.getResourceStream(p_195761_1_, p_195761_2_);
      } else {
         String lvt_3_1_ = p_195761_2_.getPath();
         if ("textures/entity/iron_golem/iron_golem.png".equals(lvt_3_1_)) {
            return this.field_229283_h_.getResourceStream(p_195761_1_, field_229278_c_);
         } else {
            InputStream lvt_4_2_;
            if (field_229281_f_.contains(lvt_3_1_)) {
               lvt_4_2_ = func_229286_a_(this.field_229283_h_.getResourceStream(p_195761_1_, field_229276_a_), this.field_229283_h_.getResourceStream(p_195761_1_, p_195761_2_), 64, 2, 2, 12, 22);
               if (lvt_4_2_ != null) {
                  return lvt_4_2_;
               }
            } else if (field_229282_g_.contains(lvt_3_1_)) {
               lvt_4_2_ = func_229286_a_(this.field_229283_h_.getResourceStream(p_195761_1_, field_229277_b_), this.field_229283_h_.getResourceStream(p_195761_1_, p_195761_2_), 64, 0, 0, 42, 41);
               if (lvt_4_2_ != null) {
                  return lvt_4_2_;
               }
            } else {
               if (!"textures/entity/enderdragon/dragon.png".equals(lvt_3_1_) && !"textures/entity/enderdragon/dragon_exploding.png".equals(lvt_3_1_)) {
                  if (!"textures/entity/conduit/closed_eye.png".equals(lvt_3_1_) && !"textures/entity/conduit/open_eye.png".equals(lvt_3_1_)) {
                     Pair<ChestType, ResourceLocation> lvt_4_4_ = (Pair)field_229279_d_.get(lvt_3_1_);
                     if (lvt_4_4_ != null) {
                        ChestType lvt_5_1_ = (ChestType)lvt_4_4_.getFirst();
                        InputStream lvt_6_2_ = this.field_229283_h_.getResourceStream(p_195761_1_, (ResourceLocation)lvt_4_4_.getSecond());
                        if (lvt_5_1_ == ChestType.SINGLE) {
                           return func_229292_d_(lvt_6_2_);
                        }

                        if (lvt_5_1_ == ChestType.LEFT) {
                           return func_229289_b_(lvt_6_2_);
                        }

                        if (lvt_5_1_ == ChestType.RIGHT) {
                           return func_229290_c_(lvt_6_2_);
                        }
                     }

                     return this.field_229283_h_.getResourceStream(p_195761_1_, p_195761_2_);
                  }

                  return func_229285_a_(this.field_229283_h_.getResourceStream(p_195761_1_, p_195761_2_));
               }

               NativeImage lvt_4_3_ = NativeImage.read(this.field_229283_h_.getResourceStream(p_195761_1_, p_195761_2_));
               Throwable var5 = null;

               try {
                  int lvt_6_1_ = lvt_4_3_.getWidth() / 256;

                  for(int lvt_7_1_ = 88 * lvt_6_1_; lvt_7_1_ < 200 * lvt_6_1_; ++lvt_7_1_) {
                     for(int lvt_8_1_ = 56 * lvt_6_1_; lvt_8_1_ < 112 * lvt_6_1_; ++lvt_8_1_) {
                        lvt_4_3_.setPixelRGBA(lvt_8_1_, lvt_7_1_, 0);
                     }
                  }

                  ByteArrayInputStream var22 = new ByteArrayInputStream(lvt_4_3_.func_227796_e_());
                  return var22;
               } catch (Throwable var16) {
                  var5 = var16;
                  throw var16;
               } finally {
                  if (lvt_4_3_ != null) {
                     if (var5 != null) {
                        try {
                           lvt_4_3_.close();
                        } catch (Throwable var15) {
                           var5.addSuppressed(var15);
                        }
                     } else {
                        lvt_4_3_.close();
                     }
                  }

               }
            }

            return this.field_229283_h_.getResourceStream(p_195761_1_, p_195761_2_);
         }
      }
   }

   @Nullable
   public static InputStream func_229286_a_(InputStream p_229286_0_, InputStream p_229286_1_, int p_229286_2_, int p_229286_3_, int p_229286_4_, int p_229286_5_, int p_229286_6_) throws IOException {
      NativeImage lvt_7_1_ = NativeImage.read(p_229286_0_);
      Throwable var8 = null;

      try {
         NativeImage lvt_9_1_ = NativeImage.read(p_229286_1_);
         Throwable var10 = null;

         try {
            int lvt_11_1_ = lvt_7_1_.getWidth();
            int lvt_12_1_ = lvt_7_1_.getHeight();
            if (lvt_11_1_ != lvt_9_1_.getWidth() || lvt_12_1_ != lvt_9_1_.getHeight()) {
               return null;
            } else {
               NativeImage lvt_13_1_ = new NativeImage(lvt_11_1_, lvt_12_1_, true);
               Throwable var14 = null;

               try {
                  int lvt_15_1_ = lvt_11_1_ / p_229286_2_;

                  for(int lvt_16_1_ = p_229286_4_ * lvt_15_1_; lvt_16_1_ < p_229286_6_ * lvt_15_1_; ++lvt_16_1_) {
                     for(int lvt_17_1_ = p_229286_3_ * lvt_15_1_; lvt_17_1_ < p_229286_5_ * lvt_15_1_; ++lvt_17_1_) {
                        int lvt_18_1_ = NativeImage.func_227791_b_(lvt_9_1_.getPixelRGBA(lvt_17_1_, lvt_16_1_));
                        int lvt_19_1_ = lvt_7_1_.getPixelRGBA(lvt_17_1_, lvt_16_1_);
                        lvt_13_1_.setPixelRGBA(lvt_17_1_, lvt_16_1_, NativeImage.func_227787_a_(lvt_18_1_, NativeImage.func_227795_d_(lvt_19_1_), NativeImage.func_227793_c_(lvt_19_1_), NativeImage.func_227791_b_(lvt_19_1_)));
                     }
                  }

                  ByteArrayInputStream var71 = new ByteArrayInputStream(lvt_13_1_.func_227796_e_());
                  return var71;
               } catch (Throwable var65) {
                  var14 = var65;
                  throw var65;
               } finally {
                  if (lvt_13_1_ != null) {
                     if (var14 != null) {
                        try {
                           lvt_13_1_.close();
                        } catch (Throwable var64) {
                           var14.addSuppressed(var64);
                        }
                     } else {
                        lvt_13_1_.close();
                     }
                  }

               }
            }
         } catch (Throwable var67) {
            var10 = var67;
            throw var67;
         } finally {
            if (lvt_9_1_ != null) {
               if (var10 != null) {
                  try {
                     lvt_9_1_.close();
                  } catch (Throwable var63) {
                     var10.addSuppressed(var63);
                  }
               } else {
                  lvt_9_1_.close();
               }
            }

         }
      } catch (Throwable var69) {
         var8 = var69;
         throw var69;
      } finally {
         if (lvt_7_1_ != null) {
            if (var8 != null) {
               try {
                  lvt_7_1_.close();
               } catch (Throwable var62) {
                  var8.addSuppressed(var62);
               }
            } else {
               lvt_7_1_.close();
            }
         }

      }
   }

   public static InputStream func_229285_a_(InputStream p_229285_0_) throws IOException {
      NativeImage lvt_1_1_ = NativeImage.read(p_229285_0_);
      Throwable var2 = null;

      Object var7;
      try {
         int lvt_3_1_ = lvt_1_1_.getWidth();
         int lvt_4_1_ = lvt_1_1_.getHeight();
         NativeImage lvt_5_1_ = new NativeImage(2 * lvt_3_1_, 2 * lvt_4_1_, true);
         Throwable var6 = null;

         try {
            func_229284_a_(lvt_1_1_, lvt_5_1_, 0, 0, 0, 0, lvt_3_1_, lvt_4_1_, 1, false, false);
            var7 = new ByteArrayInputStream(lvt_5_1_.func_227796_e_());
         } catch (Throwable var30) {
            var7 = var30;
            var6 = var30;
            throw var30;
         } finally {
            if (lvt_5_1_ != null) {
               if (var6 != null) {
                  try {
                     lvt_5_1_.close();
                  } catch (Throwable var29) {
                     var6.addSuppressed(var29);
                  }
               } else {
                  lvt_5_1_.close();
               }
            }

         }
      } catch (Throwable var32) {
         var2 = var32;
         throw var32;
      } finally {
         if (lvt_1_1_ != null) {
            if (var2 != null) {
               try {
                  lvt_1_1_.close();
               } catch (Throwable var28) {
                  var2.addSuppressed(var28);
               }
            } else {
               lvt_1_1_.close();
            }
         }

      }

      return (InputStream)var7;
   }

   public static InputStream func_229289_b_(InputStream p_229289_0_) throws IOException {
      NativeImage lvt_1_1_ = NativeImage.read(p_229289_0_);
      Throwable var2 = null;

      ByteArrayInputStream var8;
      try {
         int lvt_3_1_ = lvt_1_1_.getWidth();
         int lvt_4_1_ = lvt_1_1_.getHeight();
         NativeImage lvt_5_1_ = new NativeImage(lvt_3_1_ / 2, lvt_4_1_, true);
         Throwable var6 = null;

         try {
            int lvt_7_1_ = lvt_4_1_ / 64;
            func_229284_a_(lvt_1_1_, lvt_5_1_, 29, 0, 29, 0, 15, 14, lvt_7_1_, false, true);
            func_229284_a_(lvt_1_1_, lvt_5_1_, 59, 0, 14, 0, 15, 14, lvt_7_1_, false, true);
            func_229284_a_(lvt_1_1_, lvt_5_1_, 29, 14, 43, 14, 15, 5, lvt_7_1_, true, true);
            func_229284_a_(lvt_1_1_, lvt_5_1_, 44, 14, 29, 14, 14, 5, lvt_7_1_, true, true);
            func_229284_a_(lvt_1_1_, lvt_5_1_, 58, 14, 14, 14, 15, 5, lvt_7_1_, true, true);
            func_229284_a_(lvt_1_1_, lvt_5_1_, 29, 19, 29, 19, 15, 14, lvt_7_1_, false, true);
            func_229284_a_(lvt_1_1_, lvt_5_1_, 59, 19, 14, 19, 15, 14, lvt_7_1_, false, true);
            func_229284_a_(lvt_1_1_, lvt_5_1_, 29, 33, 43, 33, 15, 10, lvt_7_1_, true, true);
            func_229284_a_(lvt_1_1_, lvt_5_1_, 44, 33, 29, 33, 14, 10, lvt_7_1_, true, true);
            func_229284_a_(lvt_1_1_, lvt_5_1_, 58, 33, 14, 33, 15, 10, lvt_7_1_, true, true);
            func_229284_a_(lvt_1_1_, lvt_5_1_, 2, 0, 2, 0, 1, 1, lvt_7_1_, false, true);
            func_229284_a_(lvt_1_1_, lvt_5_1_, 4, 0, 1, 0, 1, 1, lvt_7_1_, false, true);
            func_229284_a_(lvt_1_1_, lvt_5_1_, 2, 1, 3, 1, 1, 4, lvt_7_1_, true, true);
            func_229284_a_(lvt_1_1_, lvt_5_1_, 3, 1, 2, 1, 1, 4, lvt_7_1_, true, true);
            func_229284_a_(lvt_1_1_, lvt_5_1_, 4, 1, 1, 1, 1, 4, lvt_7_1_, true, true);
            var8 = new ByteArrayInputStream(lvt_5_1_.func_227796_e_());
         } catch (Throwable var31) {
            var6 = var31;
            throw var31;
         } finally {
            if (lvt_5_1_ != null) {
               if (var6 != null) {
                  try {
                     lvt_5_1_.close();
                  } catch (Throwable var30) {
                     var6.addSuppressed(var30);
                  }
               } else {
                  lvt_5_1_.close();
               }
            }

         }
      } catch (Throwable var33) {
         var2 = var33;
         throw var33;
      } finally {
         if (lvt_1_1_ != null) {
            if (var2 != null) {
               try {
                  lvt_1_1_.close();
               } catch (Throwable var29) {
                  var2.addSuppressed(var29);
               }
            } else {
               lvt_1_1_.close();
            }
         }

      }

      return var8;
   }

   public static InputStream func_229290_c_(InputStream p_229290_0_) throws IOException {
      NativeImage lvt_1_1_ = NativeImage.read(p_229290_0_);
      Throwable var2 = null;

      ByteArrayInputStream var8;
      try {
         int lvt_3_1_ = lvt_1_1_.getWidth();
         int lvt_4_1_ = lvt_1_1_.getHeight();
         NativeImage lvt_5_1_ = new NativeImage(lvt_3_1_ / 2, lvt_4_1_, true);
         Throwable var6 = null;

         try {
            int lvt_7_1_ = lvt_4_1_ / 64;
            func_229284_a_(lvt_1_1_, lvt_5_1_, 14, 0, 29, 0, 15, 14, lvt_7_1_, false, true);
            func_229284_a_(lvt_1_1_, lvt_5_1_, 44, 0, 14, 0, 15, 14, lvt_7_1_, false, true);
            func_229284_a_(lvt_1_1_, lvt_5_1_, 0, 14, 0, 14, 14, 5, lvt_7_1_, true, true);
            func_229284_a_(lvt_1_1_, lvt_5_1_, 14, 14, 43, 14, 15, 5, lvt_7_1_, true, true);
            func_229284_a_(lvt_1_1_, lvt_5_1_, 73, 14, 14, 14, 15, 5, lvt_7_1_, true, true);
            func_229284_a_(lvt_1_1_, lvt_5_1_, 14, 19, 29, 19, 15, 14, lvt_7_1_, false, true);
            func_229284_a_(lvt_1_1_, lvt_5_1_, 44, 19, 14, 19, 15, 14, lvt_7_1_, false, true);
            func_229284_a_(lvt_1_1_, lvt_5_1_, 0, 33, 0, 33, 14, 10, lvt_7_1_, true, true);
            func_229284_a_(lvt_1_1_, lvt_5_1_, 14, 33, 43, 33, 15, 10, lvt_7_1_, true, true);
            func_229284_a_(lvt_1_1_, lvt_5_1_, 73, 33, 14, 33, 15, 10, lvt_7_1_, true, true);
            func_229284_a_(lvt_1_1_, lvt_5_1_, 1, 0, 2, 0, 1, 1, lvt_7_1_, false, true);
            func_229284_a_(lvt_1_1_, lvt_5_1_, 3, 0, 1, 0, 1, 1, lvt_7_1_, false, true);
            func_229284_a_(lvt_1_1_, lvt_5_1_, 0, 1, 0, 1, 1, 4, lvt_7_1_, true, true);
            func_229284_a_(lvt_1_1_, lvt_5_1_, 1, 1, 3, 1, 1, 4, lvt_7_1_, true, true);
            func_229284_a_(lvt_1_1_, lvt_5_1_, 5, 1, 1, 1, 1, 4, lvt_7_1_, true, true);
            var8 = new ByteArrayInputStream(lvt_5_1_.func_227796_e_());
         } catch (Throwable var31) {
            var6 = var31;
            throw var31;
         } finally {
            if (lvt_5_1_ != null) {
               if (var6 != null) {
                  try {
                     lvt_5_1_.close();
                  } catch (Throwable var30) {
                     var6.addSuppressed(var30);
                  }
               } else {
                  lvt_5_1_.close();
               }
            }

         }
      } catch (Throwable var33) {
         var2 = var33;
         throw var33;
      } finally {
         if (lvt_1_1_ != null) {
            if (var2 != null) {
               try {
                  lvt_1_1_.close();
               } catch (Throwable var29) {
                  var2.addSuppressed(var29);
               }
            } else {
               lvt_1_1_.close();
            }
         }

      }

      return var8;
   }

   public static InputStream func_229292_d_(InputStream p_229292_0_) throws IOException {
      NativeImage lvt_1_1_ = NativeImage.read(p_229292_0_);
      Throwable var2 = null;

      ByteArrayInputStream var8;
      try {
         int lvt_3_1_ = lvt_1_1_.getWidth();
         int lvt_4_1_ = lvt_1_1_.getHeight();
         NativeImage lvt_5_1_ = new NativeImage(lvt_3_1_, lvt_4_1_, true);
         Throwable var6 = null;

         try {
            int lvt_7_1_ = lvt_4_1_ / 64;
            func_229284_a_(lvt_1_1_, lvt_5_1_, 14, 0, 28, 0, 14, 14, lvt_7_1_, false, true);
            func_229284_a_(lvt_1_1_, lvt_5_1_, 28, 0, 14, 0, 14, 14, lvt_7_1_, false, true);
            func_229284_a_(lvt_1_1_, lvt_5_1_, 0, 14, 0, 14, 14, 5, lvt_7_1_, true, true);
            func_229284_a_(lvt_1_1_, lvt_5_1_, 14, 14, 42, 14, 14, 5, lvt_7_1_, true, true);
            func_229284_a_(lvt_1_1_, lvt_5_1_, 28, 14, 28, 14, 14, 5, lvt_7_1_, true, true);
            func_229284_a_(lvt_1_1_, lvt_5_1_, 42, 14, 14, 14, 14, 5, lvt_7_1_, true, true);
            func_229284_a_(lvt_1_1_, lvt_5_1_, 14, 19, 28, 19, 14, 14, lvt_7_1_, false, true);
            func_229284_a_(lvt_1_1_, lvt_5_1_, 28, 19, 14, 19, 14, 14, lvt_7_1_, false, true);
            func_229284_a_(lvt_1_1_, lvt_5_1_, 0, 33, 0, 33, 14, 10, lvt_7_1_, true, true);
            func_229284_a_(lvt_1_1_, lvt_5_1_, 14, 33, 42, 33, 14, 10, lvt_7_1_, true, true);
            func_229284_a_(lvt_1_1_, lvt_5_1_, 28, 33, 28, 33, 14, 10, lvt_7_1_, true, true);
            func_229284_a_(lvt_1_1_, lvt_5_1_, 42, 33, 14, 33, 14, 10, lvt_7_1_, true, true);
            func_229284_a_(lvt_1_1_, lvt_5_1_, 1, 0, 3, 0, 2, 1, lvt_7_1_, false, true);
            func_229284_a_(lvt_1_1_, lvt_5_1_, 3, 0, 1, 0, 2, 1, lvt_7_1_, false, true);
            func_229284_a_(lvt_1_1_, lvt_5_1_, 0, 1, 0, 1, 1, 4, lvt_7_1_, true, true);
            func_229284_a_(lvt_1_1_, lvt_5_1_, 1, 1, 4, 1, 2, 4, lvt_7_1_, true, true);
            func_229284_a_(lvt_1_1_, lvt_5_1_, 3, 1, 3, 1, 1, 4, lvt_7_1_, true, true);
            func_229284_a_(lvt_1_1_, lvt_5_1_, 4, 1, 1, 1, 2, 4, lvt_7_1_, true, true);
            var8 = new ByteArrayInputStream(lvt_5_1_.func_227796_e_());
         } catch (Throwable var31) {
            var6 = var31;
            throw var31;
         } finally {
            if (lvt_5_1_ != null) {
               if (var6 != null) {
                  try {
                     lvt_5_1_.close();
                  } catch (Throwable var30) {
                     var6.addSuppressed(var30);
                  }
               } else {
                  lvt_5_1_.close();
               }
            }

         }
      } catch (Throwable var33) {
         var2 = var33;
         throw var33;
      } finally {
         if (lvt_1_1_ != null) {
            if (var2 != null) {
               try {
                  lvt_1_1_.close();
               } catch (Throwable var29) {
                  var2.addSuppressed(var29);
               }
            } else {
               lvt_1_1_.close();
            }
         }

      }

      return var8;
   }

   public Collection<ResourceLocation> func_225637_a_(ResourcePackType p_225637_1_, String p_225637_2_, String p_225637_3_, int p_225637_4_, Predicate<String> p_225637_5_) {
      return this.field_229283_h_.func_225637_a_(p_225637_1_, p_225637_2_, p_225637_3_, p_225637_4_, p_225637_5_);
   }

   public Set<String> getResourceNamespaces(ResourcePackType p_195759_1_) {
      return this.field_229283_h_.getResourceNamespaces(p_195759_1_);
   }

   @Nullable
   public <T> T getMetadata(IMetadataSectionSerializer<T> p_195760_1_) throws IOException {
      return this.field_229283_h_.getMetadata(p_195760_1_);
   }

   public String getName() {
      return this.field_229283_h_.getName();
   }

   public void close() throws IOException {
      this.field_229283_h_.close();
   }

   private static void func_229284_a_(NativeImage p_229284_0_, NativeImage p_229284_1_, int p_229284_2_, int p_229284_3_, int p_229284_4_, int p_229284_5_, int p_229284_6_, int p_229284_7_, int p_229284_8_, boolean p_229284_9_, boolean p_229284_10_) {
      p_229284_7_ *= p_229284_8_;
      p_229284_6_ *= p_229284_8_;
      p_229284_4_ *= p_229284_8_;
      p_229284_5_ *= p_229284_8_;
      p_229284_2_ *= p_229284_8_;
      p_229284_3_ *= p_229284_8_;

      for(int lvt_11_1_ = 0; lvt_11_1_ < p_229284_7_; ++lvt_11_1_) {
         for(int lvt_12_1_ = 0; lvt_12_1_ < p_229284_6_; ++lvt_12_1_) {
            p_229284_1_.setPixelRGBA(p_229284_4_ + lvt_12_1_, p_229284_5_ + lvt_11_1_, p_229284_0_.getPixelRGBA(p_229284_2_ + (p_229284_9_ ? p_229284_6_ - 1 - lvt_12_1_ : lvt_12_1_), p_229284_3_ + (p_229284_10_ ? p_229284_7_ - 1 - lvt_11_1_ : lvt_11_1_)));
         }
      }

   }

   static {
      field_229281_f_ = (Set)field_229280_e_.stream().map((p_229291_0_) -> {
         return "textures/entity/shield/" + p_229291_0_ + ".png";
      }).collect(Collectors.toSet());
      field_229282_g_ = (Set)field_229280_e_.stream().map((p_229287_0_) -> {
         return "textures/entity/banner/" + p_229287_0_ + ".png";
      }).collect(Collectors.toSet());
      field_229276_a_ = new ResourceLocation("textures/entity/shield_base.png");
      field_229277_b_ = new ResourceLocation("textures/entity/banner_base.png");
      field_229278_c_ = new ResourceLocation("textures/entity/iron_golem.png");
   }
}
