package net.minecraft.client.multiplayer;

import com.google.common.collect.Lists;
import java.io.File;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class ServerList {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Minecraft mc;
   private final List<ServerData> servers = Lists.newArrayList();

   public ServerList(Minecraft p_i1194_1_) {
      this.mc = p_i1194_1_;
      this.loadServerList();
   }

   public void loadServerList() {
      try {
         this.servers.clear();
         CompoundNBT lvt_1_1_ = CompressedStreamTools.read(new File(this.mc.gameDir, "servers.dat"));
         if (lvt_1_1_ == null) {
            return;
         }

         ListNBT lvt_2_1_ = lvt_1_1_.getList("servers", 10);

         for(int lvt_3_1_ = 0; lvt_3_1_ < lvt_2_1_.size(); ++lvt_3_1_) {
            this.servers.add(ServerData.getServerDataFromNBTCompound(lvt_2_1_.getCompound(lvt_3_1_)));
         }
      } catch (Exception var4) {
         LOGGER.error("Couldn't load server list", var4);
      }

   }

   public void saveServerList() {
      try {
         ListNBT lvt_1_1_ = new ListNBT();
         Iterator var2 = this.servers.iterator();

         while(var2.hasNext()) {
            ServerData lvt_3_1_ = (ServerData)var2.next();
            lvt_1_1_.add(lvt_3_1_.getNBTCompound());
         }

         CompoundNBT lvt_2_1_ = new CompoundNBT();
         lvt_2_1_.put("servers", lvt_1_1_);
         CompressedStreamTools.safeWrite(lvt_2_1_, new File(this.mc.gameDir, "servers.dat"));
      } catch (Exception var4) {
         LOGGER.error("Couldn't save server list", var4);
      }

   }

   public ServerData getServerData(int p_78850_1_) {
      return (ServerData)this.servers.get(p_78850_1_);
   }

   public void func_217506_a(ServerData p_217506_1_) {
      this.servers.remove(p_217506_1_);
   }

   public void addServerData(ServerData p_78849_1_) {
      this.servers.add(p_78849_1_);
   }

   public int countServers() {
      return this.servers.size();
   }

   public void swapServers(int p_78857_1_, int p_78857_2_) {
      ServerData lvt_3_1_ = this.getServerData(p_78857_1_);
      this.servers.set(p_78857_1_, this.getServerData(p_78857_2_));
      this.servers.set(p_78857_2_, lvt_3_1_);
      this.saveServerList();
   }

   public void set(int p_147413_1_, ServerData p_147413_2_) {
      this.servers.set(p_147413_1_, p_147413_2_);
   }

   public static void saveSingleServer(ServerData p_147414_0_) {
      ServerList lvt_1_1_ = new ServerList(Minecraft.getInstance());
      lvt_1_1_.loadServerList();

      for(int lvt_2_1_ = 0; lvt_2_1_ < lvt_1_1_.countServers(); ++lvt_2_1_) {
         ServerData lvt_3_1_ = lvt_1_1_.getServerData(lvt_2_1_);
         if (lvt_3_1_.serverName.equals(p_147414_0_.serverName) && lvt_3_1_.serverIP.equals(p_147414_0_.serverIP)) {
            lvt_1_1_.set(lvt_2_1_, p_147414_0_);
            break;
         }
      }

      lvt_1_1_.saveServerList();
   }
}
