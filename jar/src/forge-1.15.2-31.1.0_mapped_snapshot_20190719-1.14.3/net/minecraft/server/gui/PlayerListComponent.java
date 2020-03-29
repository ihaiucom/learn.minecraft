package net.minecraft.server.gui;

import java.util.Vector;
import javax.swing.JList;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;

public class PlayerListComponent extends JList<String> {
   private final MinecraftServer server;
   private int ticks;

   public PlayerListComponent(MinecraftServer p_i2366_1_) {
      this.server = p_i2366_1_;
      p_i2366_1_.registerTickable(this::tick);
   }

   public void tick() {
      if (this.ticks++ % 20 == 0) {
         Vector<String> lvt_1_1_ = new Vector();

         for(int lvt_2_1_ = 0; lvt_2_1_ < this.server.getPlayerList().getPlayers().size(); ++lvt_2_1_) {
            lvt_1_1_.add(((ServerPlayerEntity)this.server.getPlayerList().getPlayers().get(lvt_2_1_)).getGameProfile().getName());
         }

         this.setListData(lvt_1_1_);
      }

   }
}
