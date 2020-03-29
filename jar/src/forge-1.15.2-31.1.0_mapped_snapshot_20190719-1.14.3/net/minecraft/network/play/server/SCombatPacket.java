package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.CombatTracker;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class SCombatPacket implements IPacket<IClientPlayNetHandler> {
   public SCombatPacket.Event eventType;
   public int playerId;
   public int entityId;
   public int duration;
   public ITextComponent deathMessage;

   public SCombatPacket() {
   }

   public SCombatPacket(CombatTracker p_i46931_1_, SCombatPacket.Event p_i46931_2_) {
      this(p_i46931_1_, p_i46931_2_, new StringTextComponent(""));
   }

   public SCombatPacket(CombatTracker p_i49825_1_, SCombatPacket.Event p_i49825_2_, ITextComponent p_i49825_3_) {
      this.eventType = p_i49825_2_;
      LivingEntity lvt_4_1_ = p_i49825_1_.getBestAttacker();
      switch(p_i49825_2_) {
      case END_COMBAT:
         this.duration = p_i49825_1_.getCombatDuration();
         this.entityId = lvt_4_1_ == null ? -1 : lvt_4_1_.getEntityId();
         break;
      case ENTITY_DIED:
         this.playerId = p_i49825_1_.getFighter().getEntityId();
         this.entityId = lvt_4_1_ == null ? -1 : lvt_4_1_.getEntityId();
         this.deathMessage = p_i49825_3_;
      }

   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.eventType = (SCombatPacket.Event)p_148837_1_.readEnumValue(SCombatPacket.Event.class);
      if (this.eventType == SCombatPacket.Event.END_COMBAT) {
         this.duration = p_148837_1_.readVarInt();
         this.entityId = p_148837_1_.readInt();
      } else if (this.eventType == SCombatPacket.Event.ENTITY_DIED) {
         this.playerId = p_148837_1_.readVarInt();
         this.entityId = p_148837_1_.readInt();
         this.deathMessage = p_148837_1_.readTextComponent();
      }

   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeEnumValue(this.eventType);
      if (this.eventType == SCombatPacket.Event.END_COMBAT) {
         p_148840_1_.writeVarInt(this.duration);
         p_148840_1_.writeInt(this.entityId);
      } else if (this.eventType == SCombatPacket.Event.ENTITY_DIED) {
         p_148840_1_.writeVarInt(this.playerId);
         p_148840_1_.writeInt(this.entityId);
         p_148840_1_.writeTextComponent(this.deathMessage);
      }

   }

   public void processPacket(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleCombatEvent(this);
   }

   public boolean shouldSkipErrors() {
      return this.eventType == SCombatPacket.Event.ENTITY_DIED;
   }

   public static enum Event {
      ENTER_COMBAT,
      END_COMBAT,
      ENTITY_DIED;
   }
}
