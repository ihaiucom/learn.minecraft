package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CPlaceRecipePacket implements IPacket<IServerPlayNetHandler> {
   private int windowId;
   private ResourceLocation recipeId;
   private boolean placeAll;

   public CPlaceRecipePacket() {
   }

   @OnlyIn(Dist.CLIENT)
   public CPlaceRecipePacket(int p_i47614_1_, IRecipe<?> p_i47614_2_, boolean p_i47614_3_) {
      this.windowId = p_i47614_1_;
      this.recipeId = p_i47614_2_.getId();
      this.placeAll = p_i47614_3_;
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.windowId = p_148837_1_.readByte();
      this.recipeId = p_148837_1_.readResourceLocation();
      this.placeAll = p_148837_1_.readBoolean();
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeByte(this.windowId);
      p_148840_1_.writeResourceLocation(this.recipeId);
      p_148840_1_.writeBoolean(this.placeAll);
   }

   public void processPacket(IServerPlayNetHandler p_148833_1_) {
      p_148833_1_.processPlaceRecipe(this);
   }

   public int getWindowId() {
      return this.windowId;
   }

   public ResourceLocation getRecipeId() {
      return this.recipeId;
   }

   public boolean shouldPlaceAll() {
      return this.placeAll;
   }
}
