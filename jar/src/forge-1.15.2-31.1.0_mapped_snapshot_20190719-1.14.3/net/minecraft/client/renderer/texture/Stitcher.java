package net.minecraft.client.renderer.texture;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.client.renderer.StitcherException;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.loading.AdvancedLogMessageAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class Stitcher {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Comparator<Stitcher.Holder> field_217797_a = Comparator.comparing((p_lambda$static$0_0_) -> {
      return -p_lambda$static$0_0_.height;
   }).thenComparing((p_lambda$static$1_0_) -> {
      return -p_lambda$static$1_0_.width;
   }).thenComparing((p_lambda$static$2_0_) -> {
      return p_lambda$static$2_0_.field_229213_a_.func_229248_a_();
   });
   private final int mipmapLevelStitcher;
   private final Set<Stitcher.Holder> setStitchHolders = Sets.newHashSetWithExpectedSize(256);
   private final List<Stitcher.Slot> stitchSlots = Lists.newArrayListWithCapacity(256);
   private int currentWidth;
   private int currentHeight;
   private final int maxWidth;
   private final int maxHeight;

   public Stitcher(int p_i50910_1_, int p_i50910_2_, int p_i50910_3_) {
      this.mipmapLevelStitcher = p_i50910_3_;
      this.maxWidth = p_i50910_1_;
      this.maxHeight = p_i50910_2_;
   }

   public int getCurrentWidth() {
      return this.currentWidth;
   }

   public int getCurrentHeight() {
      return this.currentHeight;
   }

   public void func_229211_a_(TextureAtlasSprite.Info p_229211_1_) {
      Stitcher.Holder stitcher$holder = new Stitcher.Holder(p_229211_1_, this.mipmapLevelStitcher);
      this.setStitchHolders.add(stitcher$holder);
   }

   public void doStitch() {
      List<Stitcher.Holder> list = Lists.newArrayList(this.setStitchHolders);
      list.sort(field_217797_a);
      Iterator var2 = list.iterator();

      Stitcher.Holder stitcher$holder;
      do {
         if (!var2.hasNext()) {
            this.currentWidth = MathHelper.smallestEncompassingPowerOfTwo(this.currentWidth);
            this.currentHeight = MathHelper.smallestEncompassingPowerOfTwo(this.currentHeight);
            return;
         }

         stitcher$holder = (Stitcher.Holder)var2.next();
      } while(this.allocateSlot(stitcher$holder));

      LOGGER.info(new AdvancedLogMessageAdapter((p_lambda$doStitch$4_2_) -> {
         p_lambda$doStitch$4_2_.append("Unable to fit: ").append(stitcher$holder.field_229213_a_.func_229248_a_());
         p_lambda$doStitch$4_2_.append(" - size: ").append(stitcher$holder.field_229213_a_.func_229250_b_()).append("x").append(stitcher$holder.field_229213_a_.func_229252_c_());
         p_lambda$doStitch$4_2_.append(" - Maybe try a lower resolution resourcepack?\n");
         list.forEach((p_lambda$null$3_1_) -> {
            p_lambda$doStitch$4_2_.append("\t").append(p_lambda$null$3_1_).append("\n");
         });
      }));
      throw new StitcherException(stitcher$holder.field_229213_a_, (Collection)list.stream().map((p_lambda$doStitch$5_0_) -> {
         return p_lambda$doStitch$5_0_.field_229213_a_;
      }).collect(ImmutableList.toImmutableList()));
   }

   public void func_229209_a_(Stitcher.ISpriteLoader p_229209_1_) {
      Iterator var2 = this.stitchSlots.iterator();

      while(var2.hasNext()) {
         Stitcher.Slot stitcher$slot = (Stitcher.Slot)var2.next();
         stitcher$slot.func_217792_a((p_lambda$func_229209_a_$6_2_) -> {
            Stitcher.Holder stitcher$holder = p_lambda$func_229209_a_$6_2_.getStitchHolder();
            TextureAtlasSprite.Info textureatlassprite$info = stitcher$holder.field_229213_a_;
            p_229209_1_.load(textureatlassprite$info, this.currentWidth, this.currentHeight, p_lambda$func_229209_a_$6_2_.getOriginX(), p_lambda$func_229209_a_$6_2_.getOriginY());
         });
      }

   }

   private static int getMipmapDimension(int p_147969_0_, int p_147969_1_) {
      return (p_147969_0_ >> p_147969_1_) + ((p_147969_0_ & (1 << p_147969_1_) - 1) == 0 ? 0 : 1) << p_147969_1_;
   }

   private boolean allocateSlot(Stitcher.Holder p_94310_1_) {
      Iterator var2 = this.stitchSlots.iterator();

      Stitcher.Slot stitcher$slot;
      do {
         if (!var2.hasNext()) {
            return this.expandAndAllocateSlot(p_94310_1_);
         }

         stitcher$slot = (Stitcher.Slot)var2.next();
      } while(!stitcher$slot.addSlot(p_94310_1_));

      return true;
   }

   private boolean expandAndAllocateSlot(Stitcher.Holder p_94311_1_) {
      int i = MathHelper.smallestEncompassingPowerOfTwo(this.currentWidth);
      int j = MathHelper.smallestEncompassingPowerOfTwo(this.currentHeight);
      int k = MathHelper.smallestEncompassingPowerOfTwo(this.currentWidth + p_94311_1_.width);
      int l = MathHelper.smallestEncompassingPowerOfTwo(this.currentHeight + p_94311_1_.height);
      boolean flag1 = k <= this.maxWidth;
      boolean flag2 = l <= this.maxHeight;
      if (!flag1 && !flag2) {
         return false;
      } else {
         boolean flag3 = flag1 && i != k;
         boolean flag4 = flag2 && j != l;
         boolean flag;
         if (flag3 ^ flag4) {
            flag = !flag3 && flag1;
         } else {
            flag = flag1 && i <= j;
         }

         Stitcher.Slot stitcher$slot;
         if (flag) {
            if (this.currentHeight == 0) {
               this.currentHeight = p_94311_1_.height;
            }

            stitcher$slot = new Stitcher.Slot(this.currentWidth, 0, p_94311_1_.width, this.currentHeight);
            this.currentWidth += p_94311_1_.width;
         } else {
            stitcher$slot = new Stitcher.Slot(0, this.currentHeight, this.currentWidth, p_94311_1_.height);
            this.currentHeight += p_94311_1_.height;
         }

         stitcher$slot.addSlot(p_94311_1_);
         this.stitchSlots.add(stitcher$slot);
         return true;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Slot {
      private final int originX;
      private final int originY;
      private final int width;
      private final int height;
      private List<Stitcher.Slot> subSlots;
      private Stitcher.Holder holder;

      public Slot(int p_i1277_1_, int p_i1277_2_, int p_i1277_3_, int p_i1277_4_) {
         this.originX = p_i1277_1_;
         this.originY = p_i1277_2_;
         this.width = p_i1277_3_;
         this.height = p_i1277_4_;
      }

      public Stitcher.Holder getStitchHolder() {
         return this.holder;
      }

      public int getOriginX() {
         return this.originX;
      }

      public int getOriginY() {
         return this.originY;
      }

      public boolean addSlot(Stitcher.Holder p_94182_1_) {
         if (this.holder != null) {
            return false;
         } else {
            int i = p_94182_1_.width;
            int j = p_94182_1_.height;
            if (i <= this.width && j <= this.height) {
               if (i == this.width && j == this.height) {
                  this.holder = p_94182_1_;
                  return true;
               } else {
                  if (this.subSlots == null) {
                     this.subSlots = Lists.newArrayListWithCapacity(1);
                     this.subSlots.add(new Stitcher.Slot(this.originX, this.originY, i, j));
                     int k = this.width - i;
                     int l = this.height - j;
                     if (l > 0 && k > 0) {
                        int i1 = Math.max(this.height, k);
                        int j1 = Math.max(this.width, l);
                        if (i1 >= j1) {
                           this.subSlots.add(new Stitcher.Slot(this.originX, this.originY + j, i, l));
                           this.subSlots.add(new Stitcher.Slot(this.originX + i, this.originY, k, this.height));
                        } else {
                           this.subSlots.add(new Stitcher.Slot(this.originX + i, this.originY, k, j));
                           this.subSlots.add(new Stitcher.Slot(this.originX, this.originY + j, this.width, l));
                        }
                     } else if (k == 0) {
                        this.subSlots.add(new Stitcher.Slot(this.originX, this.originY + j, i, l));
                     } else if (l == 0) {
                        this.subSlots.add(new Stitcher.Slot(this.originX + i, this.originY, k, j));
                     }
                  }

                  Iterator var8 = this.subSlots.iterator();

                  Stitcher.Slot stitcher$slot;
                  do {
                     if (!var8.hasNext()) {
                        return false;
                     }

                     stitcher$slot = (Stitcher.Slot)var8.next();
                  } while(!stitcher$slot.addSlot(p_94182_1_));

                  return true;
               }
            } else {
               return false;
            }
         }
      }

      public void func_217792_a(Consumer<Stitcher.Slot> p_217792_1_) {
         if (this.holder != null) {
            p_217792_1_.accept(this);
         } else if (this.subSlots != null) {
            Iterator var2 = this.subSlots.iterator();

            while(var2.hasNext()) {
               Stitcher.Slot stitcher$slot = (Stitcher.Slot)var2.next();
               stitcher$slot.func_217792_a(p_217792_1_);
            }
         }

      }

      public String toString() {
         return "Slot{originX=" + this.originX + ", originY=" + this.originY + ", width=" + this.width + ", height=" + this.height + ", texture=" + this.holder + ", subSlots=" + this.subSlots + '}';
      }
   }

   @OnlyIn(Dist.CLIENT)
   public interface ISpriteLoader {
      void load(TextureAtlasSprite.Info var1, int var2, int var3, int var4, int var5);
   }

   @OnlyIn(Dist.CLIENT)
   static class Holder {
      public final TextureAtlasSprite.Info field_229213_a_;
      public final int width;
      public final int height;

      public Holder(TextureAtlasSprite.Info p_i226045_1_, int p_i226045_2_) {
         this.field_229213_a_ = p_i226045_1_;
         this.width = Stitcher.getMipmapDimension(p_i226045_1_.func_229250_b_(), p_i226045_2_);
         this.height = Stitcher.getMipmapDimension(p_i226045_1_.func_229252_c_(), p_i226045_2_);
      }

      public String toString() {
         return "Holder{width=" + this.width + ", height=" + this.height + ", name=" + this.field_229213_a_.func_229248_a_() + '}';
      }
   }
}
