package net.minecraft.server.integrated;

import com.mojang.authlib.GameProfile;
import java.net.SocketAddress;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class IntegratedPlayerList extends PlayerList {
   private CompoundNBT hostPlayerData;

   public IntegratedPlayerList(IntegratedServer p_i1314_1_) {
      super(p_i1314_1_, 8);
      this.setViewDistance(10);
   }

   protected void writePlayerData(ServerPlayerEntity p_72391_1_) {
      if (p_72391_1_.getName().getString().equals(this.getServer().getServerOwner())) {
         this.hostPlayerData = p_72391_1_.writeWithoutTypeId(new CompoundNBT());
      }

      super.writePlayerData(p_72391_1_);
   }

   public ITextComponent canPlayerLogin(SocketAddress p_206258_1_, GameProfile p_206258_2_) {
      return (ITextComponent)(p_206258_2_.getName().equalsIgnoreCase(this.getServer().getServerOwner()) && this.getPlayerByUsername(p_206258_2_.getName()) != null ? new TranslationTextComponent("multiplayer.disconnect.name_taken", new Object[0]) : super.canPlayerLogin(p_206258_1_, p_206258_2_));
   }

   public IntegratedServer getServer() {
      return (IntegratedServer)super.getServer();
   }

   public CompoundNBT getHostPlayerData() {
      return this.hostPlayerData;
   }

   // $FF: synthetic method
   public MinecraftServer getServer() {
      return this.getServer();
   }
}
