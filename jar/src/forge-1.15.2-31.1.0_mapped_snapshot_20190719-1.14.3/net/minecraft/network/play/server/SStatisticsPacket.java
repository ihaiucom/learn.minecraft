package net.minecraft.network.play.server;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.io.IOException;
import java.util.Map;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatType;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SStatisticsPacket implements IPacket<IClientPlayNetHandler> {
   private Object2IntMap<Stat<?>> statisticMap;

   public SStatisticsPacket() {
   }

   public SStatisticsPacket(Object2IntMap<Stat<?>> p_i47942_1_) {
      this.statisticMap = p_i47942_1_;
   }

   public void processPacket(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleStatistics(this);
   }

   public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
      int lvt_2_1_ = p_148837_1_.readVarInt();
      this.statisticMap = new Object2IntOpenHashMap(lvt_2_1_);

      for(int lvt_3_1_ = 0; lvt_3_1_ < lvt_2_1_; ++lvt_3_1_) {
         this.readValues((StatType)Registry.STATS.getByValue(p_148837_1_.readVarInt()), p_148837_1_);
      }

   }

   private <T> void readValues(StatType<T> p_197684_1_, PacketBuffer p_197684_2_) {
      int lvt_3_1_ = p_197684_2_.readVarInt();
      int lvt_4_1_ = p_197684_2_.readVarInt();
      this.statisticMap.put(p_197684_1_.get(p_197684_1_.getRegistry().getByValue(lvt_3_1_)), lvt_4_1_);
   }

   public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeVarInt(this.statisticMap.size());
      ObjectIterator var2 = this.statisticMap.object2IntEntrySet().iterator();

      while(var2.hasNext()) {
         Entry<Stat<?>> lvt_3_1_ = (Entry)var2.next();
         Stat<?> lvt_4_1_ = (Stat)lvt_3_1_.getKey();
         p_148840_1_.writeVarInt(Registry.STATS.getId(lvt_4_1_.getType()));
         p_148840_1_.writeVarInt(this.func_197683_a(lvt_4_1_));
         p_148840_1_.writeVarInt(lvt_3_1_.getIntValue());
      }

   }

   private <T> int func_197683_a(Stat<T> p_197683_1_) {
      return p_197683_1_.getType().getRegistry().getId(p_197683_1_.getValue());
   }

   @OnlyIn(Dist.CLIENT)
   public Map<Stat<?>, Integer> getStatisticMap() {
      return this.statisticMap;
   }
}
