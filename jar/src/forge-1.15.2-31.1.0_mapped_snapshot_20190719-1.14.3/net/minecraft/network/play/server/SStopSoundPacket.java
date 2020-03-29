package net.minecraft.network.play.server;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SStopSoundPacket implements IPacket<IClientPlayNetHandler> {
   private ResourceLocation name;
   private SoundCategory category;

   public SStopSoundPacket() {
   }

   public SStopSoundPacket(@Nullable ResourceLocation p_i47929_1_, @Nullable SoundCategory p_i47929_2_) {
      this.name = p_i47929_1_;
      this.category = p_i47929_2_;
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      int lvt_2_1_ = p_148837_1_.readByte();
      if ((lvt_2_1_ & 1) > 0) {
         this.category = (SoundCategory)p_148837_1_.readEnumValue(SoundCategory.class);
      }

      if ((lvt_2_1_ & 2) > 0) {
         this.name = p_148837_1_.readResourceLocation();
      }

   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      if (this.category != null) {
         if (this.name != null) {
            p_148840_1_.writeByte(3);
            p_148840_1_.writeEnumValue(this.category);
            p_148840_1_.writeResourceLocation(this.name);
         } else {
            p_148840_1_.writeByte(1);
            p_148840_1_.writeEnumValue(this.category);
         }
      } else if (this.name != null) {
         p_148840_1_.writeByte(2);
         p_148840_1_.writeResourceLocation(this.name);
      } else {
         p_148840_1_.writeByte(0);
      }

   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public ResourceLocation getName() {
      return this.name;
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public SoundCategory getCategory() {
      return this.category;
   }

   public void processPacket(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleStopSound(this);
   }
}
