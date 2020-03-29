package net.minecraft.client.renderer;

import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWVidMode.Buffer;

@OnlyIn(Dist.CLIENT)
public final class VideoMode {
   private final int width;
   private final int height;
   private final int redBits;
   private final int greenBits;
   private final int blueBits;
   private final int refreshRate;
   private static final Pattern PATTERN = Pattern.compile("(\\d+)x(\\d+)(?:@(\\d+)(?::(\\d+))?)?");

   public VideoMode(int p_i47669_1_, int p_i47669_2_, int p_i47669_3_, int p_i47669_4_, int p_i47669_5_, int p_i47669_6_) {
      this.width = p_i47669_1_;
      this.height = p_i47669_2_;
      this.redBits = p_i47669_3_;
      this.greenBits = p_i47669_4_;
      this.blueBits = p_i47669_5_;
      this.refreshRate = p_i47669_6_;
   }

   public VideoMode(Buffer p_i47670_1_) {
      this.width = p_i47670_1_.width();
      this.height = p_i47670_1_.height();
      this.redBits = p_i47670_1_.redBits();
      this.greenBits = p_i47670_1_.greenBits();
      this.blueBits = p_i47670_1_.blueBits();
      this.refreshRate = p_i47670_1_.refreshRate();
   }

   public VideoMode(GLFWVidMode p_i47671_1_) {
      this.width = p_i47671_1_.width();
      this.height = p_i47671_1_.height();
      this.redBits = p_i47671_1_.redBits();
      this.greenBits = p_i47671_1_.greenBits();
      this.blueBits = p_i47671_1_.blueBits();
      this.refreshRate = p_i47671_1_.refreshRate();
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   public int getRedBits() {
      return this.redBits;
   }

   public int getGreenBits() {
      return this.greenBits;
   }

   public int getBlueBits() {
      return this.blueBits;
   }

   public int getRefreshRate() {
      return this.refreshRate;
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) {
         VideoMode lvt_2_1_ = (VideoMode)p_equals_1_;
         return this.width == lvt_2_1_.width && this.height == lvt_2_1_.height && this.redBits == lvt_2_1_.redBits && this.greenBits == lvt_2_1_.greenBits && this.blueBits == lvt_2_1_.blueBits && this.refreshRate == lvt_2_1_.refreshRate;
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.width, this.height, this.redBits, this.greenBits, this.blueBits, this.refreshRate});
   }

   public String toString() {
      return String.format("%sx%s@%s (%sbit)", this.width, this.height, this.refreshRate, this.redBits + this.greenBits + this.blueBits);
   }

   public static Optional<VideoMode> parseFromSettings(@Nullable String p_198061_0_) {
      if (p_198061_0_ == null) {
         return Optional.empty();
      } else {
         try {
            Matcher lvt_1_1_ = PATTERN.matcher(p_198061_0_);
            if (lvt_1_1_.matches()) {
               int lvt_2_1_ = Integer.parseInt(lvt_1_1_.group(1));
               int lvt_3_1_ = Integer.parseInt(lvt_1_1_.group(2));
               String lvt_4_1_ = lvt_1_1_.group(3);
               int lvt_5_2_;
               if (lvt_4_1_ == null) {
                  lvt_5_2_ = 60;
               } else {
                  lvt_5_2_ = Integer.parseInt(lvt_4_1_);
               }

               String lvt_6_1_ = lvt_1_1_.group(4);
               int lvt_7_2_;
               if (lvt_6_1_ == null) {
                  lvt_7_2_ = 24;
               } else {
                  lvt_7_2_ = Integer.parseInt(lvt_6_1_);
               }

               int lvt_8_1_ = lvt_7_2_ / 3;
               return Optional.of(new VideoMode(lvt_2_1_, lvt_3_1_, lvt_8_1_, lvt_8_1_, lvt_8_1_, lvt_5_2_));
            }
         } catch (Exception var9) {
         }

         return Optional.empty();
      }
   }

   public String getSettingsString() {
      return String.format("%sx%s@%s:%s", this.width, this.height, this.refreshRate, this.redBits + this.greenBits + this.blueBits);
   }
}
