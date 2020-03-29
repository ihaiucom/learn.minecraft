package net.minecraft.tileentity;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.INameable;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class EnchantingTableTileEntity extends TileEntity implements INameable, ITickableTileEntity {
   public int field_195522_a;
   public float field_195523_f;
   public float field_195524_g;
   public float field_195525_h;
   public float field_195526_i;
   public float field_195527_j;
   public float field_195528_k;
   public float field_195529_l;
   public float field_195530_m;
   public float field_195531_n;
   private static final Random field_195532_o = new Random();
   private ITextComponent customname;

   public EnchantingTableTileEntity() {
      super(TileEntityType.ENCHANTING_TABLE);
   }

   public CompoundNBT write(CompoundNBT p_189515_1_) {
      super.write(p_189515_1_);
      if (this.hasCustomName()) {
         p_189515_1_.putString("CustomName", ITextComponent.Serializer.toJson(this.customname));
      }

      return p_189515_1_;
   }

   public void read(CompoundNBT p_145839_1_) {
      super.read(p_145839_1_);
      if (p_145839_1_.contains("CustomName", 8)) {
         this.customname = ITextComponent.Serializer.fromJson(p_145839_1_.getString("CustomName"));
      }

   }

   public void tick() {
      this.field_195528_k = this.field_195527_j;
      this.field_195530_m = this.field_195529_l;
      PlayerEntity lvt_1_1_ = this.world.getClosestPlayer((double)((float)this.pos.getX() + 0.5F), (double)((float)this.pos.getY() + 0.5F), (double)((float)this.pos.getZ() + 0.5F), 3.0D, false);
      if (lvt_1_1_ != null) {
         double lvt_2_1_ = lvt_1_1_.func_226277_ct_() - ((double)this.pos.getX() + 0.5D);
         double lvt_4_1_ = lvt_1_1_.func_226281_cx_() - ((double)this.pos.getZ() + 0.5D);
         this.field_195531_n = (float)MathHelper.atan2(lvt_4_1_, lvt_2_1_);
         this.field_195527_j += 0.1F;
         if (this.field_195527_j < 0.5F || field_195532_o.nextInt(40) == 0) {
            float lvt_6_1_ = this.field_195525_h;

            do {
               this.field_195525_h += (float)(field_195532_o.nextInt(4) - field_195532_o.nextInt(4));
            } while(lvt_6_1_ == this.field_195525_h);
         }
      } else {
         this.field_195531_n += 0.02F;
         this.field_195527_j -= 0.1F;
      }

      while(this.field_195529_l >= 3.1415927F) {
         this.field_195529_l -= 6.2831855F;
      }

      while(this.field_195529_l < -3.1415927F) {
         this.field_195529_l += 6.2831855F;
      }

      while(this.field_195531_n >= 3.1415927F) {
         this.field_195531_n -= 6.2831855F;
      }

      while(this.field_195531_n < -3.1415927F) {
         this.field_195531_n += 6.2831855F;
      }

      float lvt_2_2_;
      for(lvt_2_2_ = this.field_195531_n - this.field_195529_l; lvt_2_2_ >= 3.1415927F; lvt_2_2_ -= 6.2831855F) {
      }

      while(lvt_2_2_ < -3.1415927F) {
         lvt_2_2_ += 6.2831855F;
      }

      this.field_195529_l += lvt_2_2_ * 0.4F;
      this.field_195527_j = MathHelper.clamp(this.field_195527_j, 0.0F, 1.0F);
      ++this.field_195522_a;
      this.field_195524_g = this.field_195523_f;
      float lvt_3_1_ = (this.field_195525_h - this.field_195523_f) * 0.4F;
      float lvt_4_2_ = 0.2F;
      lvt_3_1_ = MathHelper.clamp(lvt_3_1_, -0.2F, 0.2F);
      this.field_195526_i += (lvt_3_1_ - this.field_195526_i) * 0.9F;
      this.field_195523_f += this.field_195526_i;
   }

   public ITextComponent getName() {
      return (ITextComponent)(this.customname != null ? this.customname : new TranslationTextComponent("container.enchant", new Object[0]));
   }

   public void setCustomName(@Nullable ITextComponent p_200229_1_) {
      this.customname = p_200229_1_;
   }

   @Nullable
   public ITextComponent getCustomName() {
      return this.customname;
   }
}
