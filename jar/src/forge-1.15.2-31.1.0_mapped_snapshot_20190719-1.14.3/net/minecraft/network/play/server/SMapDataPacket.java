package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.Collection;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.storage.MapData;
import net.minecraft.world.storage.MapDecoration;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SMapDataPacket implements IPacket<IClientPlayNetHandler> {
   private int mapId;
   private byte mapScale;
   private boolean trackingPosition;
   private boolean field_218730_d;
   private MapDecoration[] icons;
   private int minX;
   private int minZ;
   private int columns;
   private int rows;
   private byte[] mapDataBytes;

   public SMapDataPacket() {
   }

   public SMapDataPacket(int p_i50772_1_, byte p_i50772_2_, boolean p_i50772_3_, boolean p_i50772_4_, Collection<MapDecoration> p_i50772_5_, byte[] p_i50772_6_, int p_i50772_7_, int p_i50772_8_, int p_i50772_9_, int p_i50772_10_) {
      this.mapId = p_i50772_1_;
      this.mapScale = p_i50772_2_;
      this.trackingPosition = p_i50772_3_;
      this.field_218730_d = p_i50772_4_;
      this.icons = (MapDecoration[])p_i50772_5_.toArray(new MapDecoration[p_i50772_5_.size()]);
      this.minX = p_i50772_7_;
      this.minZ = p_i50772_8_;
      this.columns = p_i50772_9_;
      this.rows = p_i50772_10_;
      this.mapDataBytes = new byte[p_i50772_9_ * p_i50772_10_];

      for(int lvt_11_1_ = 0; lvt_11_1_ < p_i50772_9_; ++lvt_11_1_) {
         for(int lvt_12_1_ = 0; lvt_12_1_ < p_i50772_10_; ++lvt_12_1_) {
            this.mapDataBytes[lvt_11_1_ + lvt_12_1_ * p_i50772_9_] = p_i50772_6_[p_i50772_7_ + lvt_11_1_ + (p_i50772_8_ + lvt_12_1_) * 128];
         }
      }

   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.mapId = p_148837_1_.readVarInt();
      this.mapScale = p_148837_1_.readByte();
      this.trackingPosition = p_148837_1_.readBoolean();
      this.field_218730_d = p_148837_1_.readBoolean();
      this.icons = new MapDecoration[p_148837_1_.readVarInt()];

      for(int lvt_2_1_ = 0; lvt_2_1_ < this.icons.length; ++lvt_2_1_) {
         MapDecoration.Type lvt_3_1_ = (MapDecoration.Type)p_148837_1_.readEnumValue(MapDecoration.Type.class);
         this.icons[lvt_2_1_] = new MapDecoration(lvt_3_1_, p_148837_1_.readByte(), p_148837_1_.readByte(), (byte)(p_148837_1_.readByte() & 15), p_148837_1_.readBoolean() ? p_148837_1_.readTextComponent() : null);
      }

      this.columns = p_148837_1_.readUnsignedByte();
      if (this.columns > 0) {
         this.rows = p_148837_1_.readUnsignedByte();
         this.minX = p_148837_1_.readUnsignedByte();
         this.minZ = p_148837_1_.readUnsignedByte();
         this.mapDataBytes = p_148837_1_.readByteArray();
      }

   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeVarInt(this.mapId);
      p_148840_1_.writeByte(this.mapScale);
      p_148840_1_.writeBoolean(this.trackingPosition);
      p_148840_1_.writeBoolean(this.field_218730_d);
      p_148840_1_.writeVarInt(this.icons.length);
      MapDecoration[] var2 = this.icons;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         MapDecoration lvt_5_1_ = var2[var4];
         p_148840_1_.writeEnumValue(lvt_5_1_.getType());
         p_148840_1_.writeByte(lvt_5_1_.getX());
         p_148840_1_.writeByte(lvt_5_1_.getY());
         p_148840_1_.writeByte(lvt_5_1_.getRotation() & 15);
         if (lvt_5_1_.getCustomName() != null) {
            p_148840_1_.writeBoolean(true);
            p_148840_1_.writeTextComponent(lvt_5_1_.getCustomName());
         } else {
            p_148840_1_.writeBoolean(false);
         }
      }

      p_148840_1_.writeByte(this.columns);
      if (this.columns > 0) {
         p_148840_1_.writeByte(this.rows);
         p_148840_1_.writeByte(this.minX);
         p_148840_1_.writeByte(this.minZ);
         p_148840_1_.writeByteArray(this.mapDataBytes);
      }

   }

   public void processPacket(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleMaps(this);
   }

   @OnlyIn(Dist.CLIENT)
   public int getMapId() {
      return this.mapId;
   }

   @OnlyIn(Dist.CLIENT)
   public void setMapdataTo(MapData p_179734_1_) {
      p_179734_1_.scale = this.mapScale;
      p_179734_1_.trackingPosition = this.trackingPosition;
      p_179734_1_.locked = this.field_218730_d;
      p_179734_1_.mapDecorations.clear();

      int lvt_2_2_;
      for(lvt_2_2_ = 0; lvt_2_2_ < this.icons.length; ++lvt_2_2_) {
         MapDecoration lvt_3_1_ = this.icons[lvt_2_2_];
         p_179734_1_.mapDecorations.put("icon-" + lvt_2_2_, lvt_3_1_);
      }

      for(lvt_2_2_ = 0; lvt_2_2_ < this.columns; ++lvt_2_2_) {
         for(int lvt_3_2_ = 0; lvt_3_2_ < this.rows; ++lvt_3_2_) {
            p_179734_1_.colors[this.minX + lvt_2_2_ + (this.minZ + lvt_3_2_) * 128] = this.mapDataBytes[lvt_2_2_ + lvt_3_2_ * this.columns];
         }
      }

   }
}
