package com.mojang.realmsclient.gui;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsNews;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsServerPlayerLists;
import com.mojang.realmsclient.util.RealmsPersistence;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import net.minecraft.realms.Realms;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class RealmsDataFetcher {
   private static final Logger field_225088_a = LogManager.getLogger();
   private final ScheduledExecutorService field_225089_b = Executors.newScheduledThreadPool(3);
   private volatile boolean field_225090_c = true;
   private final RealmsDataFetcher.ServerListUpdateTask field_225091_d = new RealmsDataFetcher.ServerListUpdateTask();
   private final RealmsDataFetcher.PendingInviteUpdateTask field_225092_e = new RealmsDataFetcher.PendingInviteUpdateTask();
   private final RealmsDataFetcher.TrialAvailabilityTask field_225093_f = new RealmsDataFetcher.TrialAvailabilityTask();
   private final RealmsDataFetcher.LiveStatsTask field_225094_g = new RealmsDataFetcher.LiveStatsTask();
   private final RealmsDataFetcher.UnreadNewsTask field_225095_h = new RealmsDataFetcher.UnreadNewsTask();
   private final Set<RealmsServer> field_225096_i = Sets.newHashSet();
   private List<RealmsServer> field_225097_j = Lists.newArrayList();
   private RealmsServerPlayerLists field_225098_k;
   private int field_225099_l;
   private boolean field_225100_m;
   private boolean field_225101_n;
   private String field_225102_o;
   private ScheduledFuture<?> field_225103_p;
   private ScheduledFuture<?> field_225104_q;
   private ScheduledFuture<?> field_225105_r;
   private ScheduledFuture<?> field_225106_s;
   private ScheduledFuture<?> field_225107_t;
   private final Map<RealmsDataFetcher.Task, Boolean> field_225108_u = new ConcurrentHashMap(RealmsDataFetcher.Task.values().length);

   public boolean func_225065_a() {
      return this.field_225090_c;
   }

   public synchronized void func_225086_b() {
      if (this.field_225090_c) {
         this.field_225090_c = false;
         this.func_225084_n();
         this.func_225069_m();
      }

   }

   public synchronized void func_225077_a(List<RealmsDataFetcher.Task> p_225077_1_) {
      if (this.field_225090_c) {
         this.field_225090_c = false;
         this.func_225084_n();
         Iterator var2 = p_225077_1_.iterator();

         while(var2.hasNext()) {
            RealmsDataFetcher.Task lvt_3_1_ = (RealmsDataFetcher.Task)var2.next();
            this.field_225108_u.put(lvt_3_1_, false);
            switch(lvt_3_1_) {
            case SERVER_LIST:
               this.field_225103_p = this.field_225089_b.scheduleAtFixedRate(this.field_225091_d, 0L, 60L, TimeUnit.SECONDS);
               break;
            case PENDING_INVITE:
               this.field_225104_q = this.field_225089_b.scheduleAtFixedRate(this.field_225092_e, 0L, 10L, TimeUnit.SECONDS);
               break;
            case TRIAL_AVAILABLE:
               this.field_225105_r = this.field_225089_b.scheduleAtFixedRate(this.field_225093_f, 0L, 60L, TimeUnit.SECONDS);
               break;
            case LIVE_STATS:
               this.field_225106_s = this.field_225089_b.scheduleAtFixedRate(this.field_225094_g, 0L, 10L, TimeUnit.SECONDS);
               break;
            case UNREAD_NEWS:
               this.field_225107_t = this.field_225089_b.scheduleAtFixedRate(this.field_225095_h, 0L, 300L, TimeUnit.SECONDS);
            }
         }
      }

   }

   public boolean func_225083_a(RealmsDataFetcher.Task p_225083_1_) {
      Boolean lvt_2_1_ = (Boolean)this.field_225108_u.get(p_225083_1_);
      return lvt_2_1_ == null ? false : lvt_2_1_;
   }

   public void func_225072_c() {
      Iterator var1 = this.field_225108_u.keySet().iterator();

      while(var1.hasNext()) {
         RealmsDataFetcher.Task lvt_2_1_ = (RealmsDataFetcher.Task)var1.next();
         this.field_225108_u.put(lvt_2_1_, false);
      }

   }

   public synchronized void func_225087_d() {
      this.func_225070_k();
      this.func_225086_b();
   }

   public synchronized List<RealmsServer> func_225078_e() {
      return Lists.newArrayList(this.field_225097_j);
   }

   public synchronized int func_225081_f() {
      return this.field_225099_l;
   }

   public synchronized boolean func_225071_g() {
      return this.field_225100_m;
   }

   public synchronized RealmsServerPlayerLists func_225079_h() {
      return this.field_225098_k;
   }

   public synchronized boolean func_225059_i() {
      return this.field_225101_n;
   }

   public synchronized String func_225063_j() {
      return this.field_225102_o;
   }

   public synchronized void func_225070_k() {
      this.field_225090_c = true;
      this.func_225084_n();
   }

   private void func_225069_m() {
      RealmsDataFetcher.Task[] var1 = RealmsDataFetcher.Task.values();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         RealmsDataFetcher.Task lvt_4_1_ = var1[var3];
         this.field_225108_u.put(lvt_4_1_, false);
      }

      this.field_225103_p = this.field_225089_b.scheduleAtFixedRate(this.field_225091_d, 0L, 60L, TimeUnit.SECONDS);
      this.field_225104_q = this.field_225089_b.scheduleAtFixedRate(this.field_225092_e, 0L, 10L, TimeUnit.SECONDS);
      this.field_225105_r = this.field_225089_b.scheduleAtFixedRate(this.field_225093_f, 0L, 60L, TimeUnit.SECONDS);
      this.field_225106_s = this.field_225089_b.scheduleAtFixedRate(this.field_225094_g, 0L, 10L, TimeUnit.SECONDS);
      this.field_225107_t = this.field_225089_b.scheduleAtFixedRate(this.field_225095_h, 0L, 300L, TimeUnit.SECONDS);
   }

   private void func_225084_n() {
      try {
         if (this.field_225103_p != null) {
            this.field_225103_p.cancel(false);
         }

         if (this.field_225104_q != null) {
            this.field_225104_q.cancel(false);
         }

         if (this.field_225105_r != null) {
            this.field_225105_r.cancel(false);
         }

         if (this.field_225106_s != null) {
            this.field_225106_s.cancel(false);
         }

         if (this.field_225107_t != null) {
            this.field_225107_t.cancel(false);
         }
      } catch (Exception var2) {
         field_225088_a.error("Failed to cancel Realms tasks", var2);
      }

   }

   private synchronized void func_225080_b(List<RealmsServer> p_225080_1_) {
      int lvt_2_1_ = 0;
      Iterator var3 = this.field_225096_i.iterator();

      while(var3.hasNext()) {
         RealmsServer lvt_4_1_ = (RealmsServer)var3.next();
         if (p_225080_1_.remove(lvt_4_1_)) {
            ++lvt_2_1_;
         }
      }

      if (lvt_2_1_ == 0) {
         this.field_225096_i.clear();
      }

      this.field_225097_j = p_225080_1_;
   }

   public synchronized void func_225085_a(RealmsServer p_225085_1_) {
      this.field_225097_j.remove(p_225085_1_);
      this.field_225096_i.add(p_225085_1_);
   }

   private void func_225082_c(List<RealmsServer> p_225082_1_) {
      p_225082_1_.sort(new RealmsServer.ServerComparator(Realms.getName()));
   }

   private boolean func_225068_o() {
      return !this.field_225090_c;
   }

   @OnlyIn(Dist.CLIENT)
   public static enum Task {
      SERVER_LIST,
      PENDING_INVITE,
      TRIAL_AVAILABLE,
      LIVE_STATS,
      UNREAD_NEWS;
   }

   @OnlyIn(Dist.CLIENT)
   class UnreadNewsTask implements Runnable {
      private UnreadNewsTask() {
      }

      public void run() {
         if (RealmsDataFetcher.this.func_225068_o()) {
            this.func_225057_a();
         }

      }

      private void func_225057_a() {
         try {
            RealmsClient lvt_1_1_ = RealmsClient.func_224911_a();
            if (lvt_1_1_ != null) {
               RealmsNews lvt_2_1_ = null;

               try {
                  lvt_2_1_ = lvt_1_1_.func_224920_m();
               } catch (Exception var5) {
               }

               RealmsPersistence.RealmsPersistenceData lvt_3_1_ = RealmsPersistence.func_225188_a();
               if (lvt_2_1_ != null) {
                  String lvt_4_1_ = lvt_2_1_.newsLink;
                  if (lvt_4_1_ != null && !lvt_4_1_.equals(lvt_3_1_.field_225185_a)) {
                     lvt_3_1_.field_225186_b = true;
                     lvt_3_1_.field_225185_a = lvt_4_1_;
                     RealmsPersistence.func_225187_a(lvt_3_1_);
                  }
               }

               RealmsDataFetcher.this.field_225101_n = lvt_3_1_.field_225186_b;
               RealmsDataFetcher.this.field_225102_o = lvt_3_1_.field_225185_a;
               RealmsDataFetcher.this.field_225108_u.put(RealmsDataFetcher.Task.UNREAD_NEWS, true);
            }
         } catch (Exception var6) {
            RealmsDataFetcher.field_225088_a.error("Couldn't get unread news", var6);
         }

      }

      // $FF: synthetic method
      UnreadNewsTask(Object p_i51574_2_) {
         this();
      }
   }

   @OnlyIn(Dist.CLIENT)
   class LiveStatsTask implements Runnable {
      private LiveStatsTask() {
      }

      public void run() {
         if (RealmsDataFetcher.this.func_225068_o()) {
            this.func_225048_a();
         }

      }

      private void func_225048_a() {
         try {
            RealmsClient lvt_1_1_ = RealmsClient.func_224911_a();
            if (lvt_1_1_ != null) {
               RealmsDataFetcher.this.field_225098_k = lvt_1_1_.func_224915_f();
               RealmsDataFetcher.this.field_225108_u.put(RealmsDataFetcher.Task.LIVE_STATS, true);
            }
         } catch (Exception var2) {
            RealmsDataFetcher.field_225088_a.error("Couldn't get live stats", var2);
         }

      }

      // $FF: synthetic method
      LiveStatsTask(Object p_i51583_2_) {
         this();
      }
   }

   @OnlyIn(Dist.CLIENT)
   class TrialAvailabilityTask implements Runnable {
      private TrialAvailabilityTask() {
      }

      public void run() {
         if (RealmsDataFetcher.this.func_225068_o()) {
            this.func_225055_a();
         }

      }

      private void func_225055_a() {
         try {
            RealmsClient lvt_1_1_ = RealmsClient.func_224911_a();
            if (lvt_1_1_ != null) {
               RealmsDataFetcher.this.field_225100_m = lvt_1_1_.func_224914_n();
               RealmsDataFetcher.this.field_225108_u.put(RealmsDataFetcher.Task.TRIAL_AVAILABLE, true);
            }
         } catch (Exception var2) {
            RealmsDataFetcher.field_225088_a.error("Couldn't get trial availability", var2);
         }

      }

      // $FF: synthetic method
      TrialAvailabilityTask(Object p_i51576_2_) {
         this();
      }
   }

   @OnlyIn(Dist.CLIENT)
   class PendingInviteUpdateTask implements Runnable {
      private PendingInviteUpdateTask() {
      }

      public void run() {
         if (RealmsDataFetcher.this.func_225068_o()) {
            this.func_225051_a();
         }

      }

      private void func_225051_a() {
         try {
            RealmsClient lvt_1_1_ = RealmsClient.func_224911_a();
            if (lvt_1_1_ != null) {
               RealmsDataFetcher.this.field_225099_l = lvt_1_1_.func_224909_j();
               RealmsDataFetcher.this.field_225108_u.put(RealmsDataFetcher.Task.PENDING_INVITE, true);
            }
         } catch (Exception var2) {
            RealmsDataFetcher.field_225088_a.error("Couldn't get pending invite count", var2);
         }

      }

      // $FF: synthetic method
      PendingInviteUpdateTask(Object p_i51581_2_) {
         this();
      }
   }

   @OnlyIn(Dist.CLIENT)
   class ServerListUpdateTask implements Runnable {
      private ServerListUpdateTask() {
      }

      public void run() {
         if (RealmsDataFetcher.this.func_225068_o()) {
            this.func_225053_a();
         }

      }

      private void func_225053_a() {
         try {
            RealmsClient lvt_1_1_ = RealmsClient.func_224911_a();
            if (lvt_1_1_ != null) {
               List<RealmsServer> lvt_2_1_ = lvt_1_1_.func_224902_e().servers;
               if (lvt_2_1_ != null) {
                  RealmsDataFetcher.this.func_225082_c(lvt_2_1_);
                  RealmsDataFetcher.this.func_225080_b(lvt_2_1_);
                  RealmsDataFetcher.this.field_225108_u.put(RealmsDataFetcher.Task.SERVER_LIST, true);
               } else {
                  RealmsDataFetcher.field_225088_a.warn("Realms server list was null or empty");
               }
            }
         } catch (Exception var3) {
            RealmsDataFetcher.this.field_225108_u.put(RealmsDataFetcher.Task.SERVER_LIST, true);
            RealmsDataFetcher.field_225088_a.error("Couldn't get server list", var3);
         }

      }

      // $FF: synthetic method
      ServerListUpdateTask(Object p_i51579_2_) {
         this();
      }
   }
}
