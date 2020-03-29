package net.minecraft.tileentity;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractBannerBlock;
import net.minecraft.block.BannerBlock;
import net.minecraft.block.BlockState;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.util.INameable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BannerTileEntity extends TileEntity implements INameable {
   @Nullable
   private ITextComponent name;
   @Nullable
   private DyeColor baseColor;
   @Nullable
   private ListNBT patterns;
   private boolean patternDataSet;
   @Nullable
   private List<Pair<BannerPattern, DyeColor>> patternList;

   public BannerTileEntity() {
      super(TileEntityType.BANNER);
      this.baseColor = DyeColor.WHITE;
   }

   public BannerTileEntity(DyeColor p_i47731_1_) {
      this();
      this.baseColor = p_i47731_1_;
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public static ListNBT func_230139_a_(ItemStack p_230139_0_) {
      ListNBT lvt_1_1_ = null;
      CompoundNBT lvt_2_1_ = p_230139_0_.getChildTag("BlockEntityTag");
      if (lvt_2_1_ != null && lvt_2_1_.contains("Patterns", 9)) {
         lvt_1_1_ = lvt_2_1_.getList("Patterns", 10).copy();
      }

      return lvt_1_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public void loadFromItemStack(ItemStack p_195534_1_, DyeColor p_195534_2_) {
      this.patterns = func_230139_a_(p_195534_1_);
      this.baseColor = p_195534_2_;
      this.patternList = null;
      this.patternDataSet = true;
      this.name = p_195534_1_.hasDisplayName() ? p_195534_1_.getDisplayName() : null;
   }

   public ITextComponent getName() {
      return (ITextComponent)(this.name != null ? this.name : new TranslationTextComponent("block.minecraft.banner", new Object[0]));
   }

   @Nullable
   public ITextComponent getCustomName() {
      return this.name;
   }

   public void func_213136_a(ITextComponent p_213136_1_) {
      this.name = p_213136_1_;
   }

   public CompoundNBT write(CompoundNBT p_189515_1_) {
      super.write(p_189515_1_);
      if (this.patterns != null) {
         p_189515_1_.put("Patterns", this.patterns);
      }

      if (this.name != null) {
         p_189515_1_.putString("CustomName", ITextComponent.Serializer.toJson(this.name));
      }

      return p_189515_1_;
   }

   public void read(CompoundNBT p_145839_1_) {
      super.read(p_145839_1_);
      if (p_145839_1_.contains("CustomName", 8)) {
         this.name = ITextComponent.Serializer.fromJson(p_145839_1_.getString("CustomName"));
      }

      if (this.hasWorld()) {
         this.baseColor = ((AbstractBannerBlock)this.getBlockState().getBlock()).getColor();
      } else {
         this.baseColor = null;
      }

      this.patterns = p_145839_1_.getList("Patterns", 10);
      this.patternList = null;
      this.patternDataSet = true;
   }

   @Nullable
   public SUpdateTileEntityPacket getUpdatePacket() {
      return new SUpdateTileEntityPacket(this.pos, 6, this.getUpdateTag());
   }

   public CompoundNBT getUpdateTag() {
      return this.write(new CompoundNBT());
   }

   public static int getPatterns(ItemStack p_175113_0_) {
      CompoundNBT lvt_1_1_ = p_175113_0_.getChildTag("BlockEntityTag");
      return lvt_1_1_ != null && lvt_1_1_.contains("Patterns") ? lvt_1_1_.getList("Patterns", 10).size() : 0;
   }

   @OnlyIn(Dist.CLIENT)
   public List<Pair<BannerPattern, DyeColor>> getPatternList() {
      if (this.patternList == null && this.patternDataSet) {
         this.patternList = func_230138_a_(this.getBaseColor(this::getBlockState), this.patterns);
      }

      return this.patternList;
   }

   @OnlyIn(Dist.CLIENT)
   public static List<Pair<BannerPattern, DyeColor>> func_230138_a_(DyeColor p_230138_0_, @Nullable ListNBT p_230138_1_) {
      List<Pair<BannerPattern, DyeColor>> lvt_2_1_ = Lists.newArrayList();
      lvt_2_1_.add(Pair.of(BannerPattern.BASE, p_230138_0_));
      if (p_230138_1_ != null) {
         for(int lvt_3_1_ = 0; lvt_3_1_ < p_230138_1_.size(); ++lvt_3_1_) {
            CompoundNBT lvt_4_1_ = p_230138_1_.getCompound(lvt_3_1_);
            BannerPattern lvt_5_1_ = BannerPattern.byHash(lvt_4_1_.getString("Pattern"));
            if (lvt_5_1_ != null) {
               int lvt_6_1_ = lvt_4_1_.getInt("Color");
               lvt_2_1_.add(Pair.of(lvt_5_1_, DyeColor.byId(lvt_6_1_)));
            }
         }
      }

      return lvt_2_1_;
   }

   public static void removeBannerData(ItemStack p_175117_0_) {
      CompoundNBT lvt_1_1_ = p_175117_0_.getChildTag("BlockEntityTag");
      if (lvt_1_1_ != null && lvt_1_1_.contains("Patterns", 9)) {
         ListNBT lvt_2_1_ = lvt_1_1_.getList("Patterns", 10);
         if (!lvt_2_1_.isEmpty()) {
            lvt_2_1_.remove(lvt_2_1_.size() - 1);
            if (lvt_2_1_.isEmpty()) {
               p_175117_0_.removeChildTag("BlockEntityTag");
            }

         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public ItemStack getItem(BlockState p_190615_1_) {
      ItemStack lvt_2_1_ = new ItemStack(BannerBlock.forColor(this.getBaseColor(() -> {
         return p_190615_1_;
      })));
      if (this.patterns != null && !this.patterns.isEmpty()) {
         lvt_2_1_.getOrCreateChildTag("BlockEntityTag").put("Patterns", this.patterns.copy());
      }

      if (this.name != null) {
         lvt_2_1_.setDisplayName(this.name);
      }

      return lvt_2_1_;
   }

   public DyeColor getBaseColor(Supplier<BlockState> p_195533_1_) {
      if (this.baseColor == null) {
         this.baseColor = ((AbstractBannerBlock)((BlockState)p_195533_1_.get()).getBlock()).getColor();
      }

      return this.baseColor;
   }
}
