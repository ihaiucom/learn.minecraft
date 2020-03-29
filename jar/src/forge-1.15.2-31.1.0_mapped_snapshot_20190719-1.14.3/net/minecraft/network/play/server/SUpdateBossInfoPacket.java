package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.UUID;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.BossInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SUpdateBossInfoPacket implements IPacket<IClientPlayNetHandler> {
   private UUID uniqueId;
   private SUpdateBossInfoPacket.Operation operation;
   private ITextComponent name;
   private float percent;
   private BossInfo.Color color;
   private BossInfo.Overlay overlay;
   private boolean darkenSky;
   private boolean playEndBossMusic;
   private boolean createFog;

   public SUpdateBossInfoPacket() {
   }

   public SUpdateBossInfoPacket(SUpdateBossInfoPacket.Operation p_i46964_1_, BossInfo p_i46964_2_) {
      this.operation = p_i46964_1_;
      this.uniqueId = p_i46964_2_.getUniqueId();
      this.name = p_i46964_2_.getName();
      this.percent = p_i46964_2_.getPercent();
      this.color = p_i46964_2_.getColor();
      this.overlay = p_i46964_2_.getOverlay();
      this.darkenSky = p_i46964_2_.shouldDarkenSky();
      this.playEndBossMusic = p_i46964_2_.shouldPlayEndBossMusic();
      this.createFog = p_i46964_2_.shouldCreateFog();
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      this.uniqueId = p_148837_1_.readUniqueId();
      this.operation = (SUpdateBossInfoPacket.Operation)p_148837_1_.readEnumValue(SUpdateBossInfoPacket.Operation.class);
      switch(this.operation) {
      case ADD:
         this.name = p_148837_1_.readTextComponent();
         this.percent = p_148837_1_.readFloat();
         this.color = (BossInfo.Color)p_148837_1_.readEnumValue(BossInfo.Color.class);
         this.overlay = (BossInfo.Overlay)p_148837_1_.readEnumValue(BossInfo.Overlay.class);
         this.setFlags(p_148837_1_.readUnsignedByte());
      case REMOVE:
      default:
         break;
      case UPDATE_PCT:
         this.percent = p_148837_1_.readFloat();
         break;
      case UPDATE_NAME:
         this.name = p_148837_1_.readTextComponent();
         break;
      case UPDATE_STYLE:
         this.color = (BossInfo.Color)p_148837_1_.readEnumValue(BossInfo.Color.class);
         this.overlay = (BossInfo.Overlay)p_148837_1_.readEnumValue(BossInfo.Overlay.class);
         break;
      case UPDATE_PROPERTIES:
         this.setFlags(p_148837_1_.readUnsignedByte());
      }

   }

   private void setFlags(int p_186903_1_) {
      this.darkenSky = (p_186903_1_ & 1) > 0;
      this.playEndBossMusic = (p_186903_1_ & 2) > 0;
      this.createFog = (p_186903_1_ & 4) > 0;
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeUniqueId(this.uniqueId);
      p_148840_1_.writeEnumValue(this.operation);
      switch(this.operation) {
      case ADD:
         p_148840_1_.writeTextComponent(this.name);
         p_148840_1_.writeFloat(this.percent);
         p_148840_1_.writeEnumValue(this.color);
         p_148840_1_.writeEnumValue(this.overlay);
         p_148840_1_.writeByte(this.getFlags());
      case REMOVE:
      default:
         break;
      case UPDATE_PCT:
         p_148840_1_.writeFloat(this.percent);
         break;
      case UPDATE_NAME:
         p_148840_1_.writeTextComponent(this.name);
         break;
      case UPDATE_STYLE:
         p_148840_1_.writeEnumValue(this.color);
         p_148840_1_.writeEnumValue(this.overlay);
         break;
      case UPDATE_PROPERTIES:
         p_148840_1_.writeByte(this.getFlags());
      }

   }

   private int getFlags() {
      int lvt_1_1_ = 0;
      if (this.darkenSky) {
         lvt_1_1_ |= 1;
      }

      if (this.playEndBossMusic) {
         lvt_1_1_ |= 2;
      }

      if (this.createFog) {
         lvt_1_1_ |= 4;
      }

      return lvt_1_1_;
   }

   public void processPacket(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleUpdateBossInfo(this);
   }

   @OnlyIn(Dist.CLIENT)
   public UUID getUniqueId() {
      return this.uniqueId;
   }

   @OnlyIn(Dist.CLIENT)
   public SUpdateBossInfoPacket.Operation getOperation() {
      return this.operation;
   }

   @OnlyIn(Dist.CLIENT)
   public ITextComponent getName() {
      return this.name;
   }

   @OnlyIn(Dist.CLIENT)
   public float getPercent() {
      return this.percent;
   }

   @OnlyIn(Dist.CLIENT)
   public BossInfo.Color getColor() {
      return this.color;
   }

   @OnlyIn(Dist.CLIENT)
   public BossInfo.Overlay getOverlay() {
      return this.overlay;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean shouldDarkenSky() {
      return this.darkenSky;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean shouldPlayEndBossMusic() {
      return this.playEndBossMusic;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean shouldCreateFog() {
      return this.createFog;
   }

   public static enum Operation {
      ADD,
      REMOVE,
      UPDATE_PCT,
      UPDATE_NAME,
      UPDATE_STYLE,
      UPDATE_PROPERTIES;
   }
}
