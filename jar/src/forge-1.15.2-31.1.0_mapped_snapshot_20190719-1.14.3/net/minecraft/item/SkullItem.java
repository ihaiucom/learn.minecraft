package net.minecraft.item;

import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.SkullTileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.commons.lang3.StringUtils;

public class SkullItem extends WallOrFloorItem {
   public SkullItem(Block p_i48477_1_, Block p_i48477_2_, Item.Properties p_i48477_3_) {
      super(p_i48477_1_, p_i48477_2_, p_i48477_3_);
   }

   public ITextComponent getDisplayName(ItemStack p_200295_1_) {
      if (p_200295_1_.getItem() == Items.PLAYER_HEAD && p_200295_1_.hasTag()) {
         String lvt_2_1_ = null;
         CompoundNBT lvt_3_1_ = p_200295_1_.getTag();
         if (lvt_3_1_.contains("SkullOwner", 8)) {
            lvt_2_1_ = lvt_3_1_.getString("SkullOwner");
         } else if (lvt_3_1_.contains("SkullOwner", 10)) {
            CompoundNBT lvt_4_1_ = lvt_3_1_.getCompound("SkullOwner");
            if (lvt_4_1_.contains("Name", 8)) {
               lvt_2_1_ = lvt_4_1_.getString("Name");
            }
         }

         if (lvt_2_1_ != null) {
            return new TranslationTextComponent(this.getTranslationKey() + ".named", new Object[]{lvt_2_1_});
         }
      }

      return super.getDisplayName(p_200295_1_);
   }

   public boolean updateItemStackNBT(CompoundNBT p_179215_1_) {
      super.updateItemStackNBT(p_179215_1_);
      if (p_179215_1_.contains("SkullOwner", 8) && !StringUtils.isBlank(p_179215_1_.getString("SkullOwner"))) {
         GameProfile lvt_2_1_ = new GameProfile((UUID)null, p_179215_1_.getString("SkullOwner"));
         lvt_2_1_ = SkullTileEntity.updateGameProfile(lvt_2_1_);
         p_179215_1_.put("SkullOwner", NBTUtil.writeGameProfile(new CompoundNBT(), lvt_2_1_));
         return true;
      } else {
         return false;
      }
   }
}
