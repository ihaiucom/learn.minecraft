package net.minecraft.world.biome;

import javax.annotation.Nullable;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.provider.BiomeProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BiomeContainer implements BiomeManager.IBiomeReader {
   private static final Logger field_230029_d_ = LogManager.getLogger();
   private static final int field_227052_d_ = (int)Math.round(Math.log(16.0D) / Math.log(2.0D)) - 2;
   private static final int field_227053_e_ = (int)Math.round(Math.log(256.0D) / Math.log(2.0D)) - 2;
   public static final int field_227049_a_;
   public static final int field_227050_b_;
   public static final int field_227051_c_;
   private final Biome[] field_227054_f_;

   public BiomeContainer(Biome[] p_i225779_1_) {
      this.field_227054_f_ = p_i225779_1_;
   }

   private BiomeContainer() {
      this(new Biome[field_227049_a_]);
   }

   public BiomeContainer(PacketBuffer p_i225778_1_) {
      this();

      for(int lvt_2_1_ = 0; lvt_2_1_ < this.field_227054_f_.length; ++lvt_2_1_) {
         int lvt_3_1_ = p_i225778_1_.readInt();
         Biome lvt_4_1_ = (Biome)Registry.BIOME.getByValue(lvt_3_1_);
         if (lvt_4_1_ == null) {
            field_230029_d_.warn("Received invalid biome id: " + lvt_3_1_);
            this.field_227054_f_[lvt_2_1_] = Biomes.PLAINS;
         } else {
            this.field_227054_f_[lvt_2_1_] = lvt_4_1_;
         }
      }

   }

   public BiomeContainer(ChunkPos p_i225776_1_, BiomeProvider p_i225776_2_) {
      this();
      int lvt_3_1_ = p_i225776_1_.getXStart() >> 2;
      int lvt_4_1_ = p_i225776_1_.getZStart() >> 2;

      for(int lvt_5_1_ = 0; lvt_5_1_ < this.field_227054_f_.length; ++lvt_5_1_) {
         int lvt_6_1_ = lvt_5_1_ & field_227050_b_;
         int lvt_7_1_ = lvt_5_1_ >> field_227052_d_ + field_227052_d_ & field_227051_c_;
         int lvt_8_1_ = lvt_5_1_ >> field_227052_d_ & field_227050_b_;
         this.field_227054_f_[lvt_5_1_] = p_i225776_2_.func_225526_b_(lvt_3_1_ + lvt_6_1_, lvt_7_1_, lvt_4_1_ + lvt_8_1_);
      }

   }

   public BiomeContainer(ChunkPos p_i225777_1_, BiomeProvider p_i225777_2_, @Nullable int[] p_i225777_3_) {
      this();
      int lvt_4_1_ = p_i225777_1_.getXStart() >> 2;
      int lvt_5_1_ = p_i225777_1_.getZStart() >> 2;
      int lvt_6_1_;
      int lvt_7_1_;
      int lvt_8_1_;
      int lvt_9_1_;
      if (p_i225777_3_ != null) {
         for(lvt_6_1_ = 0; lvt_6_1_ < p_i225777_3_.length; ++lvt_6_1_) {
            this.field_227054_f_[lvt_6_1_] = (Biome)Registry.BIOME.getByValue(p_i225777_3_[lvt_6_1_]);
            if (this.field_227054_f_[lvt_6_1_] == null) {
               lvt_7_1_ = lvt_6_1_ & field_227050_b_;
               lvt_8_1_ = lvt_6_1_ >> field_227052_d_ + field_227052_d_ & field_227051_c_;
               lvt_9_1_ = lvt_6_1_ >> field_227052_d_ & field_227050_b_;
               this.field_227054_f_[lvt_6_1_] = p_i225777_2_.func_225526_b_(lvt_4_1_ + lvt_7_1_, lvt_8_1_, lvt_5_1_ + lvt_9_1_);
            }
         }
      } else {
         for(lvt_6_1_ = 0; lvt_6_1_ < this.field_227054_f_.length; ++lvt_6_1_) {
            lvt_7_1_ = lvt_6_1_ & field_227050_b_;
            lvt_8_1_ = lvt_6_1_ >> field_227052_d_ + field_227052_d_ & field_227051_c_;
            lvt_9_1_ = lvt_6_1_ >> field_227052_d_ & field_227050_b_;
            this.field_227054_f_[lvt_6_1_] = p_i225777_2_.func_225526_b_(lvt_4_1_ + lvt_7_1_, lvt_8_1_, lvt_5_1_ + lvt_9_1_);
         }
      }

   }

   public int[] func_227055_a_() {
      int[] lvt_1_1_ = new int[this.field_227054_f_.length];

      for(int lvt_2_1_ = 0; lvt_2_1_ < this.field_227054_f_.length; ++lvt_2_1_) {
         lvt_1_1_[lvt_2_1_] = Registry.BIOME.getId(this.field_227054_f_[lvt_2_1_]);
      }

      return lvt_1_1_;
   }

   public void func_227056_a_(PacketBuffer p_227056_1_) {
      Biome[] var2 = this.field_227054_f_;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Biome lvt_5_1_ = var2[var4];
         p_227056_1_.writeInt(Registry.BIOME.getId(lvt_5_1_));
      }

   }

   public BiomeContainer func_227057_b_() {
      return new BiomeContainer((Biome[])this.field_227054_f_.clone());
   }

   public Biome func_225526_b_(int p_225526_1_, int p_225526_2_, int p_225526_3_) {
      int lvt_4_1_ = p_225526_1_ & field_227050_b_;
      int lvt_5_1_ = MathHelper.clamp(p_225526_2_, 0, field_227051_c_);
      int lvt_6_1_ = p_225526_3_ & field_227050_b_;
      return this.field_227054_f_[lvt_5_1_ << field_227052_d_ + field_227052_d_ | lvt_6_1_ << field_227052_d_ | lvt_4_1_];
   }

   static {
      field_227049_a_ = 1 << field_227052_d_ + field_227052_d_ + field_227053_e_;
      field_227050_b_ = (1 << field_227052_d_) - 1;
      field_227051_c_ = (1 << field_227053_e_) - 1;
   }
}
