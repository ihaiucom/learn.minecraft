package net.minecraft.network.play.server;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SEntityPropertiesPacket implements IPacket<IClientPlayNetHandler> {
   private int entityId;
   private final List<SEntityPropertiesPacket.Snapshot> snapshots = Lists.newArrayList();

   public SEntityPropertiesPacket() {
   }

   public SEntityPropertiesPacket(int p_i46892_1_, Collection<IAttributeInstance> p_i46892_2_) {
      this.entityId = p_i46892_1_;
      Iterator var3 = p_i46892_2_.iterator();

      while(var3.hasNext()) {
         IAttributeInstance lvt_4_1_ = (IAttributeInstance)var3.next();
         this.snapshots.add(new SEntityPropertiesPacket.Snapshot(lvt_4_1_.getAttribute().getName(), lvt_4_1_.getBaseValue(), lvt_4_1_.func_225505_c_()));
      }

   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.entityId = p_148837_1_.readVarInt();
      int lvt_2_1_ = p_148837_1_.readInt();

      for(int lvt_3_1_ = 0; lvt_3_1_ < lvt_2_1_; ++lvt_3_1_) {
         String lvt_4_1_ = p_148837_1_.readString(64);
         double lvt_5_1_ = p_148837_1_.readDouble();
         List<AttributeModifier> lvt_7_1_ = Lists.newArrayList();
         int lvt_8_1_ = p_148837_1_.readVarInt();

         for(int lvt_9_1_ = 0; lvt_9_1_ < lvt_8_1_; ++lvt_9_1_) {
            UUID lvt_10_1_ = p_148837_1_.readUniqueId();
            lvt_7_1_.add(new AttributeModifier(lvt_10_1_, "Unknown synced attribute modifier", p_148837_1_.readDouble(), AttributeModifier.Operation.byId(p_148837_1_.readByte())));
         }

         this.snapshots.add(new SEntityPropertiesPacket.Snapshot(lvt_4_1_, lvt_5_1_, lvt_7_1_));
      }

   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeVarInt(this.entityId);
      p_148840_1_.writeInt(this.snapshots.size());
      Iterator var2 = this.snapshots.iterator();

      while(var2.hasNext()) {
         SEntityPropertiesPacket.Snapshot lvt_3_1_ = (SEntityPropertiesPacket.Snapshot)var2.next();
         p_148840_1_.writeString(lvt_3_1_.getName());
         p_148840_1_.writeDouble(lvt_3_1_.getBaseValue());
         p_148840_1_.writeVarInt(lvt_3_1_.getModifiers().size());
         Iterator var4 = lvt_3_1_.getModifiers().iterator();

         while(var4.hasNext()) {
            AttributeModifier lvt_5_1_ = (AttributeModifier)var4.next();
            p_148840_1_.writeUniqueId(lvt_5_1_.getID());
            p_148840_1_.writeDouble(lvt_5_1_.getAmount());
            p_148840_1_.writeByte(lvt_5_1_.getOperation().getId());
         }
      }

   }

   public void processPacket(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleEntityProperties(this);
   }

   @OnlyIn(Dist.CLIENT)
   public int getEntityId() {
      return this.entityId;
   }

   @OnlyIn(Dist.CLIENT)
   public List<SEntityPropertiesPacket.Snapshot> getSnapshots() {
      return this.snapshots;
   }

   public class Snapshot {
      private final String name;
      private final double baseValue;
      private final Collection<AttributeModifier> modifiers;

      public Snapshot(String p_i47075_2_, double p_i47075_3_, Collection<AttributeModifier> p_i47075_5_) {
         this.name = p_i47075_2_;
         this.baseValue = p_i47075_3_;
         this.modifiers = p_i47075_5_;
      }

      public String getName() {
         return this.name;
      }

      public double getBaseValue() {
         return this.baseValue;
      }

      public Collection<AttributeModifier> getModifiers() {
         return this.modifiers;
      }
   }
}
