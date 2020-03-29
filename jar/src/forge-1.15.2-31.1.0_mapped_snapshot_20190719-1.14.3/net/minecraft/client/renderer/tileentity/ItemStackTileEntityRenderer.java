package net.minecraft.client.renderer.tileentity;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mojang.datafixers.util.Pair;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import net.minecraft.block.AbstractBannerBlock;
import net.minecraft.block.AbstractSkullBlock;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.entity.model.ShieldModel;
import net.minecraft.client.renderer.entity.model.TridentModel;
import net.minecraft.client.renderer.model.Material;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.item.BlockItem;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ShieldItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.BannerPattern;
import net.minecraft.tileentity.BannerTileEntity;
import net.minecraft.tileentity.BedTileEntity;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.ConduitTileEntity;
import net.minecraft.tileentity.EnderChestTileEntity;
import net.minecraft.tileentity.ShulkerBoxTileEntity;
import net.minecraft.tileentity.SkullTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TrappedChestTileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.StringUtils;

@OnlyIn(Dist.CLIENT)
public class ItemStackTileEntityRenderer {
   private static final ShulkerBoxTileEntity[] SHULKER_BOXES = (ShulkerBoxTileEntity[])Arrays.stream(DyeColor.values()).sorted(Comparator.comparingInt(DyeColor::getId)).map(ShulkerBoxTileEntity::new).toArray((p_199929_0_) -> {
      return new ShulkerBoxTileEntity[p_199929_0_];
   });
   private static final ShulkerBoxTileEntity SHULKER_BOX = new ShulkerBoxTileEntity((DyeColor)null);
   public static final ItemStackTileEntityRenderer instance = new ItemStackTileEntityRenderer();
   private final ChestTileEntity chestBasic = new ChestTileEntity();
   private final ChestTileEntity chestTrap = new TrappedChestTileEntity();
   private final EnderChestTileEntity enderChest = new EnderChestTileEntity();
   private final BannerTileEntity banner = new BannerTileEntity();
   private final BedTileEntity bed = new BedTileEntity();
   private final ConduitTileEntity conduit = new ConduitTileEntity();
   private final ShieldModel modelShield = new ShieldModel();
   private final TridentModel trident = new TridentModel();

   public void func_228364_a_(ItemStack p_228364_1_, MatrixStack p_228364_2_, IRenderTypeBuffer p_228364_3_, int p_228364_4_, int p_228364_5_) {
      Item lvt_6_1_ = p_228364_1_.getItem();
      if (lvt_6_1_ instanceof BlockItem) {
         Block lvt_7_1_ = ((BlockItem)lvt_6_1_).getBlock();
         if (lvt_7_1_ instanceof AbstractSkullBlock) {
            GameProfile lvt_8_1_ = null;
            if (p_228364_1_.hasTag()) {
               CompoundNBT lvt_9_1_ = p_228364_1_.getTag();
               if (lvt_9_1_.contains("SkullOwner", 10)) {
                  lvt_8_1_ = NBTUtil.readGameProfile(lvt_9_1_.getCompound("SkullOwner"));
               } else if (lvt_9_1_.contains("SkullOwner", 8) && !StringUtils.isBlank(lvt_9_1_.getString("SkullOwner"))) {
                  lvt_8_1_ = new GameProfile((UUID)null, lvt_9_1_.getString("SkullOwner"));
                  lvt_8_1_ = SkullTileEntity.updateGameProfile(lvt_8_1_);
                  lvt_9_1_.remove("SkullOwner");
                  lvt_9_1_.put("SkullOwner", NBTUtil.writeGameProfile(new CompoundNBT(), lvt_8_1_));
               }
            }

            SkullTileEntityRenderer.func_228879_a_((Direction)null, 180.0F, ((AbstractSkullBlock)lvt_7_1_).getSkullType(), lvt_8_1_, 0.0F, p_228364_2_, p_228364_3_, p_228364_4_);
         } else {
            Object lvt_8_10_;
            if (lvt_7_1_ instanceof AbstractBannerBlock) {
               this.banner.loadFromItemStack(p_228364_1_, ((AbstractBannerBlock)lvt_7_1_).getColor());
               lvt_8_10_ = this.banner;
            } else if (lvt_7_1_ instanceof BedBlock) {
               this.bed.setColor(((BedBlock)lvt_7_1_).getColor());
               lvt_8_10_ = this.bed;
            } else if (lvt_7_1_ == Blocks.CONDUIT) {
               lvt_8_10_ = this.conduit;
            } else if (lvt_7_1_ == Blocks.CHEST) {
               lvt_8_10_ = this.chestBasic;
            } else if (lvt_7_1_ == Blocks.ENDER_CHEST) {
               lvt_8_10_ = this.enderChest;
            } else if (lvt_7_1_ == Blocks.TRAPPED_CHEST) {
               lvt_8_10_ = this.chestTrap;
            } else {
               if (!(lvt_7_1_ instanceof ShulkerBoxBlock)) {
                  return;
               }

               DyeColor lvt_9_2_ = ShulkerBoxBlock.getColorFromItem(lvt_6_1_);
               if (lvt_9_2_ == null) {
                  lvt_8_10_ = SHULKER_BOX;
               } else {
                  lvt_8_10_ = SHULKER_BOXES[lvt_9_2_.getId()];
               }
            }

            TileEntityRendererDispatcher.instance.func_228852_a_((TileEntity)lvt_8_10_, p_228364_2_, p_228364_3_, p_228364_4_, p_228364_5_);
         }
      } else {
         if (lvt_6_1_ == Items.SHIELD) {
            boolean lvt_7_2_ = p_228364_1_.getChildTag("BlockEntityTag") != null;
            p_228364_2_.func_227860_a_();
            p_228364_2_.func_227862_a_(1.0F, -1.0F, -1.0F);
            Material lvt_8_11_ = lvt_7_2_ ? ModelBakery.field_229316_g_ : ModelBakery.field_229317_h_;
            IVertexBuilder lvt_9_3_ = lvt_8_11_.func_229314_c_().func_229230_a_(ItemRenderer.func_229113_a_(p_228364_3_, this.modelShield.func_228282_a_(lvt_8_11_.func_229310_a_()), false, p_228364_1_.hasEffect()));
            this.modelShield.func_228294_b_().func_228309_a_(p_228364_2_, lvt_9_3_, p_228364_4_, p_228364_5_, 1.0F, 1.0F, 1.0F, 1.0F);
            if (lvt_7_2_) {
               List<Pair<BannerPattern, DyeColor>> lvt_10_1_ = BannerTileEntity.func_230138_a_(ShieldItem.getColor(p_228364_1_), BannerTileEntity.func_230139_a_(p_228364_1_));
               BannerTileEntityRenderer.func_230180_a_(p_228364_2_, p_228364_3_, p_228364_4_, p_228364_5_, this.modelShield.func_228293_a_(), lvt_8_11_, false, lvt_10_1_);
            } else {
               this.modelShield.func_228293_a_().func_228309_a_(p_228364_2_, lvt_9_3_, p_228364_4_, p_228364_5_, 1.0F, 1.0F, 1.0F, 1.0F);
            }

            p_228364_2_.func_227865_b_();
         } else if (lvt_6_1_ == Items.TRIDENT) {
            p_228364_2_.func_227860_a_();
            p_228364_2_.func_227862_a_(1.0F, -1.0F, -1.0F);
            IVertexBuilder lvt_7_3_ = ItemRenderer.func_229113_a_(p_228364_3_, this.trident.func_228282_a_(TridentModel.TEXTURE_LOCATION), false, p_228364_1_.hasEffect());
            this.trident.func_225598_a_(p_228364_2_, lvt_7_3_, p_228364_4_, p_228364_5_, 1.0F, 1.0F, 1.0F, 1.0F);
            p_228364_2_.func_227865_b_();
         }

      }
   }
}
