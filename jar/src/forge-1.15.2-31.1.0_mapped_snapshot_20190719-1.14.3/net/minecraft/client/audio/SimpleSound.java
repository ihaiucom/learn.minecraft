package net.minecraft.client.audio;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SimpleSound extends LocatableSound {
   public SimpleSound(SoundEvent p_i46527_1_, SoundCategory p_i46527_2_, float p_i46527_3_, float p_i46527_4_, BlockPos p_i46527_5_) {
      this(p_i46527_1_, p_i46527_2_, p_i46527_3_, p_i46527_4_, (float)p_i46527_5_.getX() + 0.5F, (float)p_i46527_5_.getY() + 0.5F, (float)p_i46527_5_.getZ() + 0.5F);
   }

   public static SimpleSound master(SoundEvent p_184371_0_, float p_184371_1_) {
      return master(p_184371_0_, p_184371_1_, 0.25F);
   }

   public static SimpleSound master(SoundEvent p_194007_0_, float p_194007_1_, float p_194007_2_) {
      return new SimpleSound(p_194007_0_.getName(), SoundCategory.MASTER, p_194007_2_, p_194007_1_, false, 0, ISound.AttenuationType.NONE, 0.0F, 0.0F, 0.0F, true);
   }

   public static SimpleSound music(SoundEvent p_184370_0_) {
      return new SimpleSound(p_184370_0_.getName(), SoundCategory.MUSIC, 1.0F, 1.0F, false, 0, ISound.AttenuationType.NONE, 0.0F, 0.0F, 0.0F, true);
   }

   public static SimpleSound record(SoundEvent p_184372_0_, float p_184372_1_, float p_184372_2_, float p_184372_3_) {
      return new SimpleSound(p_184372_0_, SoundCategory.RECORDS, 4.0F, 1.0F, false, 0, ISound.AttenuationType.LINEAR, p_184372_1_, p_184372_2_, p_184372_3_);
   }

   public SimpleSound(SoundEvent p_i46528_1_, SoundCategory p_i46528_2_, float p_i46528_3_, float p_i46528_4_, float p_i46528_5_, float p_i46528_6_, float p_i46528_7_) {
      this(p_i46528_1_, p_i46528_2_, p_i46528_3_, p_i46528_4_, false, 0, ISound.AttenuationType.LINEAR, p_i46528_5_, p_i46528_6_, p_i46528_7_);
   }

   private SimpleSound(SoundEvent p_i46529_1_, SoundCategory p_i46529_2_, float p_i46529_3_, float p_i46529_4_, boolean p_i46529_5_, int p_i46529_6_, ISound.AttenuationType p_i46529_7_, float p_i46529_8_, float p_i46529_9_, float p_i46529_10_) {
      this(p_i46529_1_.getName(), p_i46529_2_, p_i46529_3_, p_i46529_4_, p_i46529_5_, p_i46529_6_, p_i46529_7_, p_i46529_8_, p_i46529_9_, p_i46529_10_, false);
   }

   public SimpleSound(ResourceLocation p_i50897_1_, SoundCategory p_i50897_2_, float p_i50897_3_, float p_i50897_4_, boolean p_i50897_5_, int p_i50897_6_, ISound.AttenuationType p_i50897_7_, float p_i50897_8_, float p_i50897_9_, float p_i50897_10_, boolean p_i50897_11_) {
      super(p_i50897_1_, p_i50897_2_);
      this.volume = p_i50897_3_;
      this.pitch = p_i50897_4_;
      this.x = p_i50897_8_;
      this.y = p_i50897_9_;
      this.z = p_i50897_10_;
      this.repeat = p_i50897_5_;
      this.repeatDelay = p_i50897_6_;
      this.attenuationType = p_i50897_7_;
      this.global = p_i50897_11_;
   }
}
