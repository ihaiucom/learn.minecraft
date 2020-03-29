package net.minecraft.block;

import com.mojang.authlib.GameProfile;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.SkullTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.StringUtils;

public class SkullPlayerBlock extends SkullBlock {
   protected SkullPlayerBlock(Block.Properties p_i48354_1_) {
      super(SkullBlock.Types.PLAYER, p_i48354_1_);
   }

   public void onBlockPlacedBy(World p_180633_1_, BlockPos p_180633_2_, BlockState p_180633_3_, @Nullable LivingEntity p_180633_4_, ItemStack p_180633_5_) {
      super.onBlockPlacedBy(p_180633_1_, p_180633_2_, p_180633_3_, p_180633_4_, p_180633_5_);
      TileEntity lvt_6_1_ = p_180633_1_.getTileEntity(p_180633_2_);
      if (lvt_6_1_ instanceof SkullTileEntity) {
         SkullTileEntity lvt_7_1_ = (SkullTileEntity)lvt_6_1_;
         GameProfile lvt_8_1_ = null;
         if (p_180633_5_.hasTag()) {
            CompoundNBT lvt_9_1_ = p_180633_5_.getTag();
            if (lvt_9_1_.contains("SkullOwner", 10)) {
               lvt_8_1_ = NBTUtil.readGameProfile(lvt_9_1_.getCompound("SkullOwner"));
            } else if (lvt_9_1_.contains("SkullOwner", 8) && !StringUtils.isBlank(lvt_9_1_.getString("SkullOwner"))) {
               lvt_8_1_ = new GameProfile((UUID)null, lvt_9_1_.getString("SkullOwner"));
            }
         }

         lvt_7_1_.setPlayerProfile(lvt_8_1_);
      }

   }
}
