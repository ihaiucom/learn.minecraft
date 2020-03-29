package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.Validate;

public class SSpawnMovingSoundEffectPacket implements IPacket<IClientPlayNetHandler> {
   private SoundEvent field_218765_a;
   private SoundCategory field_218766_b;
   private int field_218767_c;
   private float field_218768_d;
   private float field_218769_e;

   public SSpawnMovingSoundEffectPacket() {
   }

   public SSpawnMovingSoundEffectPacket(SoundEvent p_i50763_1_, SoundCategory p_i50763_2_, Entity p_i50763_3_, float p_i50763_4_, float p_i50763_5_) {
      Validate.notNull(p_i50763_1_, "sound", new Object[0]);
      this.field_218765_a = p_i50763_1_;
      this.field_218766_b = p_i50763_2_;
      this.field_218767_c = p_i50763_3_.getEntityId();
      this.field_218768_d = p_i50763_4_;
      this.field_218769_e = p_i50763_5_;
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.field_218765_a = (SoundEvent)Registry.SOUND_EVENT.getByValue(p_148837_1_.readVarInt());
      this.field_218766_b = (SoundCategory)p_148837_1_.readEnumValue(SoundCategory.class);
      this.field_218767_c = p_148837_1_.readVarInt();
      this.field_218768_d = p_148837_1_.readFloat();
      this.field_218769_e = p_148837_1_.readFloat();
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeVarInt(Registry.SOUND_EVENT.getId(this.field_218765_a));
      p_148840_1_.writeEnumValue(this.field_218766_b);
      p_148840_1_.writeVarInt(this.field_218767_c);
      p_148840_1_.writeFloat(this.field_218768_d);
      p_148840_1_.writeFloat(this.field_218769_e);
   }

   @OnlyIn(Dist.CLIENT)
   public SoundEvent func_218763_b() {
      return this.field_218765_a;
   }

   @OnlyIn(Dist.CLIENT)
   public SoundCategory func_218760_c() {
      return this.field_218766_b;
   }

   @OnlyIn(Dist.CLIENT)
   public int func_218762_d() {
      return this.field_218767_c;
   }

   @OnlyIn(Dist.CLIENT)
   public float func_218764_e() {
      return this.field_218768_d;
   }

   @OnlyIn(Dist.CLIENT)
   public float func_218761_f() {
      return this.field_218769_e;
   }

   public void processPacket(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.func_217266_a(this);
   }
}
