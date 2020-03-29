package com.mojang.realmsclient.client;

import com.google.common.collect.Lists;
import com.mojang.realmsclient.dto.RegionPingResult;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Comparator;
import java.util.List;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Ping {
   public static List<RegionPingResult> func_224867_a(Ping.Region... p_224867_0_) {
      Ping.Region[] var1 = p_224867_0_;
      int var2 = p_224867_0_.length;

      int var3;
      for(var3 = 0; var3 < var2; ++var3) {
         Ping.Region lvt_4_1_ = var1[var3];
         func_224868_a(lvt_4_1_.field_224863_j);
      }

      List<RegionPingResult> lvt_1_1_ = Lists.newArrayList();
      Ping.Region[] var7 = p_224867_0_;
      var3 = p_224867_0_.length;

      for(int var8 = 0; var8 < var3; ++var8) {
         Ping.Region lvt_5_1_ = var7[var8];
         lvt_1_1_.add(new RegionPingResult(lvt_5_1_.field_224862_i, func_224868_a(lvt_5_1_.field_224863_j)));
      }

      lvt_1_1_.sort(Comparator.comparingInt(RegionPingResult::ping));
      return lvt_1_1_;
   }

   private static int func_224868_a(String p_224868_0_) {
      int lvt_1_1_ = true;
      long lvt_2_1_ = 0L;
      Socket lvt_4_1_ = null;

      for(int lvt_5_1_ = 0; lvt_5_1_ < 5; ++lvt_5_1_) {
         try {
            SocketAddress lvt_6_1_ = new InetSocketAddress(p_224868_0_, 80);
            lvt_4_1_ = new Socket();
            long lvt_7_1_ = func_224865_b();
            lvt_4_1_.connect(lvt_6_1_, 700);
            lvt_2_1_ += func_224865_b() - lvt_7_1_;
         } catch (Exception var12) {
            lvt_2_1_ += 700L;
         } finally {
            func_224866_a(lvt_4_1_);
         }
      }

      return (int)((double)lvt_2_1_ / 5.0D);
   }

   private static void func_224866_a(Socket p_224866_0_) {
      try {
         if (p_224866_0_ != null) {
            p_224866_0_.close();
         }
      } catch (Throwable var2) {
      }

   }

   private static long func_224865_b() {
      return System.currentTimeMillis();
   }

   public static List<RegionPingResult> func_224864_a() {
      return func_224867_a(Ping.Region.values());
   }

   @OnlyIn(Dist.CLIENT)
   static enum Region {
      US_EAST_1("us-east-1", "ec2.us-east-1.amazonaws.com"),
      US_WEST_2("us-west-2", "ec2.us-west-2.amazonaws.com"),
      US_WEST_1("us-west-1", "ec2.us-west-1.amazonaws.com"),
      EU_WEST_1("eu-west-1", "ec2.eu-west-1.amazonaws.com"),
      AP_SOUTHEAST_1("ap-southeast-1", "ec2.ap-southeast-1.amazonaws.com"),
      AP_SOUTHEAST_2("ap-southeast-2", "ec2.ap-southeast-2.amazonaws.com"),
      AP_NORTHEAST_1("ap-northeast-1", "ec2.ap-northeast-1.amazonaws.com"),
      SA_EAST_1("sa-east-1", "ec2.sa-east-1.amazonaws.com");

      private final String field_224862_i;
      private final String field_224863_j;

      private Region(String p_i51602_3_, String p_i51602_4_) {
         this.field_224862_i = p_i51602_3_;
         this.field_224863_j = p_i51602_4_;
      }
   }
}
