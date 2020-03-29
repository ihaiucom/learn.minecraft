package net.minecraft.network.play.server;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.lighting.WorldLightManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SUpdateLightPacket implements IPacket<IClientPlayNetHandler> {
   private int chunkX;
   private int chunkZ;
   private int skyLightUpdateMask;
   private int blockLightUpdateMask;
   private int skyLightResetMask;
   private int blockLightResetMask;
   private List<byte[]> skyLightData;
   private List<byte[]> blockLightData;

   public SUpdateLightPacket() {
   }

   public SUpdateLightPacket(ChunkPos p_i50774_1_, WorldLightManager p_i50774_2_) {
      this.chunkX = p_i50774_1_.x;
      this.chunkZ = p_i50774_1_.z;
      this.skyLightData = Lists.newArrayList();
      this.blockLightData = Lists.newArrayList();

      for(int lvt_3_1_ = 0; lvt_3_1_ < 18; ++lvt_3_1_) {
         NibbleArray lvt_4_1_ = p_i50774_2_.getLightEngine(LightType.SKY).getData(SectionPos.from(p_i50774_1_, -1 + lvt_3_1_));
         NibbleArray lvt_5_1_ = p_i50774_2_.getLightEngine(LightType.BLOCK).getData(SectionPos.from(p_i50774_1_, -1 + lvt_3_1_));
         if (lvt_4_1_ != null) {
            if (lvt_4_1_.isEmpty()) {
               this.skyLightResetMask |= 1 << lvt_3_1_;
            } else {
               this.skyLightUpdateMask |= 1 << lvt_3_1_;
               this.skyLightData.add(lvt_4_1_.getData().clone());
            }
         }

         if (lvt_5_1_ != null) {
            if (lvt_5_1_.isEmpty()) {
               this.blockLightResetMask |= 1 << lvt_3_1_;
            } else {
               this.blockLightUpdateMask |= 1 << lvt_3_1_;
               this.blockLightData.add(lvt_5_1_.getData().clone());
            }
         }
      }

   }

   public SUpdateLightPacket(ChunkPos p_i50775_1_, WorldLightManager p_i50775_2_, int p_i50775_3_, int p_i50775_4_) {
      this.chunkX = p_i50775_1_.x;
      this.chunkZ = p_i50775_1_.z;
      this.skyLightUpdateMask = p_i50775_3_;
      this.blockLightUpdateMask = p_i50775_4_;
      this.skyLightData = Lists.newArrayList();
      this.blockLightData = Lists.newArrayList();

      for(int lvt_5_1_ = 0; lvt_5_1_ < 18; ++lvt_5_1_) {
         NibbleArray lvt_6_2_;
         if ((this.skyLightUpdateMask & 1 << lvt_5_1_) != 0) {
            lvt_6_2_ = p_i50775_2_.getLightEngine(LightType.SKY).getData(SectionPos.from(p_i50775_1_, -1 + lvt_5_1_));
            if (lvt_6_2_ != null && !lvt_6_2_.isEmpty()) {
               this.skyLightData.add(lvt_6_2_.getData().clone());
            } else {
               this.skyLightUpdateMask &= ~(1 << lvt_5_1_);
               if (lvt_6_2_ != null) {
                  this.skyLightResetMask |= 1 << lvt_5_1_;
               }
            }
         }

         if ((this.blockLightUpdateMask & 1 << lvt_5_1_) != 0) {
            lvt_6_2_ = p_i50775_2_.getLightEngine(LightType.BLOCK).getData(SectionPos.from(p_i50775_1_, -1 + lvt_5_1_));
            if (lvt_6_2_ != null && !lvt_6_2_.isEmpty()) {
               this.blockLightData.add(lvt_6_2_.getData().clone());
            } else {
               this.blockLightUpdateMask &= ~(1 << lvt_5_1_);
               if (lvt_6_2_ != null) {
                  this.blockLightResetMask |= 1 << lvt_5_1_;
               }
            }
         }
      }

   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.chunkX = p_148837_1_.readVarInt();
      this.chunkZ = p_148837_1_.readVarInt();
      this.skyLightUpdateMask = p_148837_1_.readVarInt();
      this.blockLightUpdateMask = p_148837_1_.readVarInt();
      this.skyLightResetMask = p_148837_1_.readVarInt();
      this.blockLightResetMask = p_148837_1_.readVarInt();
      this.skyLightData = Lists.newArrayList();

      int lvt_2_2_;
      for(lvt_2_2_ = 0; lvt_2_2_ < 18; ++lvt_2_2_) {
         if ((this.skyLightUpdateMask & 1 << lvt_2_2_) != 0) {
            this.skyLightData.add(p_148837_1_.readByteArray(2048));
         }
      }

      this.blockLightData = Lists.newArrayList();

      for(lvt_2_2_ = 0; lvt_2_2_ < 18; ++lvt_2_2_) {
         if ((this.blockLightUpdateMask & 1 << lvt_2_2_) != 0) {
            this.blockLightData.add(p_148837_1_.readByteArray(2048));
         }
      }

   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeVarInt(this.chunkX);
      p_148840_1_.writeVarInt(this.chunkZ);
      p_148840_1_.writeVarInt(this.skyLightUpdateMask);
      p_148840_1_.writeVarInt(this.blockLightUpdateMask);
      p_148840_1_.writeVarInt(this.skyLightResetMask);
      p_148840_1_.writeVarInt(this.blockLightResetMask);
      Iterator var2 = this.skyLightData.iterator();

      byte[] lvt_3_2_;
      while(var2.hasNext()) {
         lvt_3_2_ = (byte[])var2.next();
         p_148840_1_.writeByteArray(lvt_3_2_);
      }

      var2 = this.blockLightData.iterator();

      while(var2.hasNext()) {
         lvt_3_2_ = (byte[])var2.next();
         p_148840_1_.writeByteArray(lvt_3_2_);
      }

   }

   public void processPacket(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleUpdateLight(this);
   }

   @OnlyIn(Dist.CLIENT)
   public int getChunkX() {
      return this.chunkX;
   }

   @OnlyIn(Dist.CLIENT)
   public int getChunkZ() {
      return this.chunkZ;
   }

   @OnlyIn(Dist.CLIENT)
   public int getSkyLightUpdateMask() {
      return this.skyLightUpdateMask;
   }

   @OnlyIn(Dist.CLIENT)
   public int getSkyLightResetMask() {
      return this.skyLightResetMask;
   }

   @OnlyIn(Dist.CLIENT)
   public List<byte[]> getSkyLightData() {
      return this.skyLightData;
   }

   @OnlyIn(Dist.CLIENT)
   public int getBlockLightUpdateMask() {
      return this.blockLightUpdateMask;
   }

   @OnlyIn(Dist.CLIENT)
   public int getBlockLightResetMask() {
      return this.blockLightResetMask;
   }

   @OnlyIn(Dist.CLIENT)
   public List<byte[]> getBlockLightData() {
      return this.blockLightData;
   }
}
