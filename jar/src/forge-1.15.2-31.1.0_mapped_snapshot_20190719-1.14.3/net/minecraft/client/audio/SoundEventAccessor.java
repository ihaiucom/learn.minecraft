package net.minecraft.client.audio;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SoundEventAccessor implements ISoundEventAccessor<Sound> {
   private final List<ISoundEventAccessor<Sound>> accessorList = Lists.newArrayList();
   private final Random rnd = new Random();
   private final ResourceLocation location;
   private final ITextComponent subtitle;

   public SoundEventAccessor(ResourceLocation p_i46521_1_, @Nullable String p_i46521_2_) {
      this.location = p_i46521_1_;
      this.subtitle = p_i46521_2_ == null ? null : new TranslationTextComponent(p_i46521_2_, new Object[0]);
   }

   public int getWeight() {
      int lvt_1_1_ = 0;

      ISoundEventAccessor lvt_3_1_;
      for(Iterator var2 = this.accessorList.iterator(); var2.hasNext(); lvt_1_1_ += lvt_3_1_.getWeight()) {
         lvt_3_1_ = (ISoundEventAccessor)var2.next();
      }

      return lvt_1_1_;
   }

   public Sound cloneEntry() {
      int lvt_1_1_ = this.getWeight();
      if (!this.accessorList.isEmpty() && lvt_1_1_ != 0) {
         int lvt_2_1_ = this.rnd.nextInt(lvt_1_1_);
         Iterator var3 = this.accessorList.iterator();

         ISoundEventAccessor lvt_4_1_;
         do {
            if (!var3.hasNext()) {
               return SoundHandler.MISSING_SOUND;
            }

            lvt_4_1_ = (ISoundEventAccessor)var3.next();
            lvt_2_1_ -= lvt_4_1_.getWeight();
         } while(lvt_2_1_ >= 0);

         return (Sound)lvt_4_1_.cloneEntry();
      } else {
         return SoundHandler.MISSING_SOUND;
      }
   }

   public void addSound(ISoundEventAccessor<Sound> p_188715_1_) {
      this.accessorList.add(p_188715_1_);
   }

   @Nullable
   public ITextComponent getSubtitle() {
      return this.subtitle;
   }

   public void func_217867_a(SoundEngine p_217867_1_) {
      Iterator var2 = this.accessorList.iterator();

      while(var2.hasNext()) {
         ISoundEventAccessor<Sound> lvt_3_1_ = (ISoundEventAccessor)var2.next();
         lvt_3_1_.func_217867_a(p_217867_1_);
      }

   }

   // $FF: synthetic method
   public Object cloneEntry() {
      return this.cloneEntry();
   }
}
