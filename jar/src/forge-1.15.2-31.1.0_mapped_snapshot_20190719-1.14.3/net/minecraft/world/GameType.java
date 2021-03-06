package net.minecraft.world;

import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public enum GameType {
   NOT_SET(-1, ""),
   SURVIVAL(0, "survival"),
   CREATIVE(1, "creative"),
   ADVENTURE(2, "adventure"),
   SPECTATOR(3, "spectator");

   private final int id;
   private final String name;

   private GameType(int p_i48711_3_, String p_i48711_4_) {
      this.id = p_i48711_3_;
      this.name = p_i48711_4_;
   }

   public int getID() {
      return this.id;
   }

   public String getName() {
      return this.name;
   }

   public ITextComponent getDisplayName() {
      return new TranslationTextComponent("gameMode." + this.name, new Object[0]);
   }

   public void configurePlayerCapabilities(PlayerAbilities p_77147_1_) {
      if (this == CREATIVE) {
         p_77147_1_.allowFlying = true;
         p_77147_1_.isCreativeMode = true;
         p_77147_1_.disableDamage = true;
      } else if (this == SPECTATOR) {
         p_77147_1_.allowFlying = true;
         p_77147_1_.isCreativeMode = false;
         p_77147_1_.disableDamage = true;
         p_77147_1_.isFlying = true;
      } else {
         p_77147_1_.allowFlying = false;
         p_77147_1_.isCreativeMode = false;
         p_77147_1_.disableDamage = false;
         p_77147_1_.isFlying = false;
      }

      p_77147_1_.allowEdit = !this.hasLimitedInteractions();
   }

   public boolean hasLimitedInteractions() {
      return this == ADVENTURE || this == SPECTATOR;
   }

   public boolean isCreative() {
      return this == CREATIVE;
   }

   public boolean isSurvivalOrAdventure() {
      return this == SURVIVAL || this == ADVENTURE;
   }

   public static GameType getByID(int p_77146_0_) {
      return parseGameTypeWithDefault(p_77146_0_, SURVIVAL);
   }

   public static GameType parseGameTypeWithDefault(int p_185329_0_, GameType p_185329_1_) {
      GameType[] var2 = values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         GameType lvt_5_1_ = var2[var4];
         if (lvt_5_1_.id == p_185329_0_) {
            return lvt_5_1_;
         }
      }

      return p_185329_1_;
   }

   public static GameType getByName(String p_77142_0_) {
      return parseGameTypeWithDefault(p_77142_0_, SURVIVAL);
   }

   public static GameType parseGameTypeWithDefault(String p_185328_0_, GameType p_185328_1_) {
      GameType[] var2 = values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         GameType lvt_5_1_ = var2[var4];
         if (lvt_5_1_.name.equals(p_185328_0_)) {
            return lvt_5_1_;
         }
      }

      return p_185328_1_;
   }
}
