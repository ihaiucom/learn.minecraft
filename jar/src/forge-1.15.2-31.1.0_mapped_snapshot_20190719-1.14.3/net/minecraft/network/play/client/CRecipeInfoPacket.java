package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CRecipeInfoPacket implements IPacket<IServerPlayNetHandler> {
   private CRecipeInfoPacket.Purpose purpose;
   private ResourceLocation recipe;
   private boolean isGuiOpen;
   private boolean filteringCraftable;
   private boolean isFurnaceGuiOpen;
   private boolean furnaceFilteringCraftable;
   private boolean field_218782_g;
   private boolean field_218783_h;
   private boolean field_218784_i;
   private boolean field_218785_j;

   public CRecipeInfoPacket() {
   }

   public CRecipeInfoPacket(IRecipe<?> p_i47518_1_) {
      this.purpose = CRecipeInfoPacket.Purpose.SHOWN;
      this.recipe = p_i47518_1_.getId();
   }

   @OnlyIn(Dist.CLIENT)
   public CRecipeInfoPacket(boolean p_i50758_1_, boolean p_i50758_2_, boolean p_i50758_3_, boolean p_i50758_4_, boolean p_i50758_5_, boolean p_i50758_6_) {
      this.purpose = CRecipeInfoPacket.Purpose.SETTINGS;
      this.isGuiOpen = p_i50758_1_;
      this.filteringCraftable = p_i50758_2_;
      this.isFurnaceGuiOpen = p_i50758_3_;
      this.furnaceFilteringCraftable = p_i50758_4_;
      this.field_218782_g = p_i50758_5_;
      this.field_218783_h = p_i50758_6_;
      this.field_218784_i = p_i50758_5_;
      this.field_218785_j = p_i50758_6_;
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.purpose = (CRecipeInfoPacket.Purpose)p_148837_1_.readEnumValue(CRecipeInfoPacket.Purpose.class);
      if (this.purpose == CRecipeInfoPacket.Purpose.SHOWN) {
         this.recipe = p_148837_1_.readResourceLocation();
      } else if (this.purpose == CRecipeInfoPacket.Purpose.SETTINGS) {
         this.isGuiOpen = p_148837_1_.readBoolean();
         this.filteringCraftable = p_148837_1_.readBoolean();
         this.isFurnaceGuiOpen = p_148837_1_.readBoolean();
         this.furnaceFilteringCraftable = p_148837_1_.readBoolean();
         this.field_218782_g = p_148837_1_.readBoolean();
         this.field_218783_h = p_148837_1_.readBoolean();
         this.field_218784_i = p_148837_1_.readBoolean();
         this.field_218785_j = p_148837_1_.readBoolean();
      }

   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeEnumValue(this.purpose);
      if (this.purpose == CRecipeInfoPacket.Purpose.SHOWN) {
         p_148840_1_.writeResourceLocation(this.recipe);
      } else if (this.purpose == CRecipeInfoPacket.Purpose.SETTINGS) {
         p_148840_1_.writeBoolean(this.isGuiOpen);
         p_148840_1_.writeBoolean(this.filteringCraftable);
         p_148840_1_.writeBoolean(this.isFurnaceGuiOpen);
         p_148840_1_.writeBoolean(this.furnaceFilteringCraftable);
         p_148840_1_.writeBoolean(this.field_218782_g);
         p_148840_1_.writeBoolean(this.field_218783_h);
         p_148840_1_.writeBoolean(this.field_218784_i);
         p_148840_1_.writeBoolean(this.field_218785_j);
      }

   }

   public void processPacket(IServerPlayNetHandler p_148833_1_) {
      p_148833_1_.handleRecipeBookUpdate(this);
   }

   public CRecipeInfoPacket.Purpose getPurpose() {
      return this.purpose;
   }

   public ResourceLocation getRecipeId() {
      return this.recipe;
   }

   public boolean isGuiOpen() {
      return this.isGuiOpen;
   }

   public boolean isFilteringCraftable() {
      return this.filteringCraftable;
   }

   public boolean isFurnaceGuiOpen() {
      return this.isFurnaceGuiOpen;
   }

   public boolean isFurnaceFilteringCraftable() {
      return this.furnaceFilteringCraftable;
   }

   public boolean func_218779_h() {
      return this.field_218782_g;
   }

   public boolean func_218778_i() {
      return this.field_218783_h;
   }

   public boolean func_218780_j() {
      return this.field_218784_i;
   }

   public boolean func_218781_k() {
      return this.field_218785_j;
   }

   public static enum Purpose {
      SHOWN,
      SETTINGS;
   }
}
