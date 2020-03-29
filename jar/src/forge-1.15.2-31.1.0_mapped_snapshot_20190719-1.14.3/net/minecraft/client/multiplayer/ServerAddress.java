package net.minecraft.client.multiplayer;

import java.net.IDN;
import java.util.Hashtable;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ServerAddress {
   private final String ipAddress;
   private final int serverPort;

   private ServerAddress(String p_i1192_1_, int p_i1192_2_) {
      this.ipAddress = p_i1192_1_;
      this.serverPort = p_i1192_2_;
   }

   public String getIP() {
      try {
         return IDN.toASCII(this.ipAddress);
      } catch (IllegalArgumentException var2) {
         return "";
      }
   }

   public int getPort() {
      return this.serverPort;
   }

   public static ServerAddress fromString(String p_78860_0_) {
      if (p_78860_0_ == null) {
         return null;
      } else {
         String[] lvt_1_1_ = p_78860_0_.split(":");
         if (p_78860_0_.startsWith("[")) {
            int lvt_2_1_ = p_78860_0_.indexOf("]");
            if (lvt_2_1_ > 0) {
               String lvt_3_1_ = p_78860_0_.substring(1, lvt_2_1_);
               String lvt_4_1_ = p_78860_0_.substring(lvt_2_1_ + 1).trim();
               if (lvt_4_1_.startsWith(":") && !lvt_4_1_.isEmpty()) {
                  lvt_4_1_ = lvt_4_1_.substring(1);
                  lvt_1_1_ = new String[]{lvt_3_1_, lvt_4_1_};
               } else {
                  lvt_1_1_ = new String[]{lvt_3_1_};
               }
            }
         }

         if (lvt_1_1_.length > 2) {
            lvt_1_1_ = new String[]{p_78860_0_};
         }

         String lvt_2_2_ = lvt_1_1_[0];
         int lvt_3_2_ = lvt_1_1_.length > 1 ? getInt(lvt_1_1_[1], 25565) : 25565;
         if (lvt_3_2_ == 25565) {
            String[] lvt_4_2_ = getServerAddress(lvt_2_2_);
            lvt_2_2_ = lvt_4_2_[0];
            lvt_3_2_ = getInt(lvt_4_2_[1], 25565);
         }

         return new ServerAddress(lvt_2_2_, lvt_3_2_);
      }
   }

   private static String[] getServerAddress(String p_78863_0_) {
      try {
         String lvt_1_1_ = "com.sun.jndi.dns.DnsContextFactory";
         Class.forName("com.sun.jndi.dns.DnsContextFactory");
         Hashtable<String, String> lvt_2_1_ = new Hashtable();
         lvt_2_1_.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
         lvt_2_1_.put("java.naming.provider.url", "dns:");
         lvt_2_1_.put("com.sun.jndi.dns.timeout.retries", "1");
         DirContext lvt_3_1_ = new InitialDirContext(lvt_2_1_);
         Attributes lvt_4_1_ = lvt_3_1_.getAttributes("_minecraft._tcp." + p_78863_0_, new String[]{"SRV"});
         String[] lvt_5_1_ = lvt_4_1_.get("srv").get().toString().split(" ", 4);
         return new String[]{lvt_5_1_[3], lvt_5_1_[2]};
      } catch (Throwable var6) {
         return new String[]{p_78863_0_, Integer.toString(25565)};
      }
   }

   private static int getInt(String p_78862_0_, int p_78862_1_) {
      try {
         return Integer.parseInt(p_78862_0_.trim());
      } catch (Exception var3) {
         return p_78862_1_;
      }
   }
}
