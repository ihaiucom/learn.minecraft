package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CUpdateJigsawBlockPacket implements IPacket<IServerPlayNetHandler> {
   private BlockPos field_218790_a;
   private ResourceLocation field_218791_b;
   private ResourceLocation field_218792_c;
   private String field_218793_d;

   public CUpdateJigsawBlockPacket() {
   }

   @OnlyIn(Dist.CLIENT)
   public CUpdateJigsawBlockPacket(BlockPos p_i50757_1_, ResourceLocation p_i50757_2_, ResourceLocation p_i50757_3_, String p_i50757_4_) {
      this.field_218790_a = p_i50757_1_;
      this.field_218791_b = p_i50757_2_;
      this.field_218792_c = p_i50757_3_;
      this.field_218793_d = p_i50757_4_;
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.field_218790_a = p_148837_1_.readBlockPos();
      this.field_218791_b = p_148837_1_.readResourceLocation();
      this.field_218792_c = p_148837_1_.readResourceLocation();
      this.field_218793_d = p_148837_1_.readString(32767);
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeBlockPos(this.field_218790_a);
      p_148840_1_.writeResourceLocation(this.field_218791_b);
      p_148840_1_.writeResourceLocation(this.field_218792_c);
      p_148840_1_.writeString(this.field_218793_d);
   }

   public void processPacket(IServerPlayNetHandler p_148833_1_) {
      p_148833_1_.func_217262_a(this);
   }

   public BlockPos func_218789_b() {
      return this.field_218790_a;
   }

   public ResourceLocation func_218786_c() {
      return this.field_218792_c;
   }

   public ResourceLocation func_218787_d() {
      return this.field_218791_b;
   }

   public String func_218788_e() {
      return this.field_218793_d;
   }
}
