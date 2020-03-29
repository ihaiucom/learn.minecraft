package net.minecraft.network.play.server;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.potion.Effect;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SRemoveEntityEffectPacket implements IPacket<IClientPlayNetHandler> {
   private int entityId;
   private Effect effectId;

   public SRemoveEntityEffectPacket() {
   }

   public SRemoveEntityEffectPacket(int p_i46925_1_, Effect p_i46925_2_) {
      this.entityId = p_i46925_1_;
      this.effectId = p_i46925_2_;
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.entityId = p_148837_1_.readVarInt();
      this.effectId = Effect.get(p_148837_1_.readUnsignedByte());
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeVarInt(this.entityId);
      p_148840_1_.writeByte(Effect.getId(this.effectId));
   }

   public void processPacket(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleRemoveEntityEffect(this);
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public Entity getEntity(World p_186967_1_) {
      return p_186967_1_.getEntityByID(this.entityId);
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public Effect getPotion() {
      return this.effectId;
   }
}
