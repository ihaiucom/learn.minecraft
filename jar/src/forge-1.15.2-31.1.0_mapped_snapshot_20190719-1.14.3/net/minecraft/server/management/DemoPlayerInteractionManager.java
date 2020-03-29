package net.minecraft.server.management;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPlayerDiggingPacket;
import net.minecraft.network.play.server.SChangeGameStatePacket;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class DemoPlayerInteractionManager extends PlayerInteractionManager {
   private boolean displayedIntro;
   private boolean demoTimeExpired;
   private int demoEndedReminder;
   private int gameModeTicks;

   public DemoPlayerInteractionManager(ServerWorld p_i50709_1_) {
      super(p_i50709_1_);
   }

   public void tick() {
      super.tick();
      ++this.gameModeTicks;
      long lvt_1_1_ = this.world.getGameTime();
      long lvt_3_1_ = lvt_1_1_ / 24000L + 1L;
      if (!this.displayedIntro && this.gameModeTicks > 20) {
         this.displayedIntro = true;
         this.player.connection.sendPacket(new SChangeGameStatePacket(5, 0.0F));
      }

      this.demoTimeExpired = lvt_1_1_ > 120500L;
      if (this.demoTimeExpired) {
         ++this.demoEndedReminder;
      }

      if (lvt_1_1_ % 24000L == 500L) {
         if (lvt_3_1_ <= 6L) {
            if (lvt_3_1_ == 6L) {
               this.player.connection.sendPacket(new SChangeGameStatePacket(5, 104.0F));
            } else {
               this.player.sendMessage(new TranslationTextComponent("demo.day." + lvt_3_1_, new Object[0]));
            }
         }
      } else if (lvt_3_1_ == 1L) {
         if (lvt_1_1_ == 100L) {
            this.player.connection.sendPacket(new SChangeGameStatePacket(5, 101.0F));
         } else if (lvt_1_1_ == 175L) {
            this.player.connection.sendPacket(new SChangeGameStatePacket(5, 102.0F));
         } else if (lvt_1_1_ == 250L) {
            this.player.connection.sendPacket(new SChangeGameStatePacket(5, 103.0F));
         }
      } else if (lvt_3_1_ == 5L && lvt_1_1_ % 24000L == 22000L) {
         this.player.sendMessage(new TranslationTextComponent("demo.day.warning", new Object[0]));
      }

   }

   private void sendDemoReminder() {
      if (this.demoEndedReminder > 100) {
         this.player.sendMessage(new TranslationTextComponent("demo.reminder", new Object[0]));
         this.demoEndedReminder = 0;
      }

   }

   public void func_225416_a(BlockPos p_225416_1_, CPlayerDiggingPacket.Action p_225416_2_, Direction p_225416_3_, int p_225416_4_) {
      if (this.demoTimeExpired) {
         this.sendDemoReminder();
      } else {
         super.func_225416_a(p_225416_1_, p_225416_2_, p_225416_3_, p_225416_4_);
      }
   }

   public ActionResultType processRightClick(PlayerEntity p_187250_1_, World p_187250_2_, ItemStack p_187250_3_, Hand p_187250_4_) {
      if (this.demoTimeExpired) {
         this.sendDemoReminder();
         return ActionResultType.PASS;
      } else {
         return super.processRightClick(p_187250_1_, p_187250_2_, p_187250_3_, p_187250_4_);
      }
   }

   public ActionResultType func_219441_a(PlayerEntity p_219441_1_, World p_219441_2_, ItemStack p_219441_3_, Hand p_219441_4_, BlockRayTraceResult p_219441_5_) {
      if (this.demoTimeExpired) {
         this.sendDemoReminder();
         return ActionResultType.PASS;
      } else {
         return super.func_219441_a(p_219441_1_, p_219441_2_, p_219441_3_, p_219441_4_, p_219441_5_);
      }
   }
}
