package net.minecraftforge.event.enchanting;

import javax.annotation.Nonnull;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.eventbus.api.Event;

public class EnchantmentLevelSetEvent extends Event {
   private final World world;
   private final BlockPos pos;
   private final int enchantRow;
   private final int power;
   @Nonnull
   private final ItemStack itemStack;
   private final int originalLevel;
   private int level;

   public EnchantmentLevelSetEvent(World world, BlockPos pos, int enchantRow, int power, @Nonnull ItemStack itemStack, int level) {
      this.world = world;
      this.pos = pos;
      this.enchantRow = enchantRow;
      this.power = power;
      this.itemStack = itemStack;
      this.originalLevel = level;
      this.level = level;
   }

   public World getWorld() {
      return this.world;
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public int getEnchantRow() {
      return this.enchantRow;
   }

   public int getPower() {
      return this.power;
   }

   @Nonnull
   public ItemStack getItem() {
      return this.itemStack;
   }

   public int getOriginalLevel() {
      return this.originalLevel;
   }

   public int getLevel() {
      return this.level;
   }

   public void setLevel(int level) {
      this.level = level;
   }
}
