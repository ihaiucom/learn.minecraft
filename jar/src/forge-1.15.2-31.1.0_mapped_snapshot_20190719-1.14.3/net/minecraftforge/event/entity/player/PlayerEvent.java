package net.minecraftforge.event.entity.player;

import java.io.File;
import javax.annotation.Nonnull;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.Cancelable;

public class PlayerEvent extends LivingEvent {
   private final PlayerEntity entityPlayer;

   public PlayerEvent(PlayerEntity player) {
      super(player);
      this.entityPlayer = player;
   }

   public PlayerEntity getPlayer() {
      return this.entityPlayer;
   }

   public static class PlayerChangedDimensionEvent extends PlayerEvent {
      private final DimensionType fromDim;
      private final DimensionType toDim;

      public PlayerChangedDimensionEvent(PlayerEntity player, DimensionType fromDim, DimensionType toDim) {
         super(player);
         this.fromDim = fromDim;
         this.toDim = toDim;
      }

      public DimensionType getFrom() {
         return this.fromDim;
      }

      public DimensionType getTo() {
         return this.toDim;
      }
   }

   public static class PlayerRespawnEvent extends PlayerEvent {
      private final boolean endConquered;

      public PlayerRespawnEvent(PlayerEntity player, boolean endConquered) {
         super(player);
         this.endConquered = endConquered;
      }

      public boolean isEndConquered() {
         return this.endConquered;
      }
   }

   public static class PlayerLoggedOutEvent extends PlayerEvent {
      public PlayerLoggedOutEvent(PlayerEntity player) {
         super(player);
      }
   }

   public static class PlayerLoggedInEvent extends PlayerEvent {
      public PlayerLoggedInEvent(PlayerEntity player) {
         super(player);
      }
   }

   public static class ItemSmeltedEvent extends PlayerEvent {
      @Nonnull
      private final ItemStack smelting;

      public ItemSmeltedEvent(PlayerEntity player, @Nonnull ItemStack crafting) {
         super(player);
         this.smelting = crafting;
      }

      @Nonnull
      public ItemStack getSmelting() {
         return this.smelting;
      }
   }

   public static class ItemCraftedEvent extends PlayerEvent {
      @Nonnull
      private final ItemStack crafting;
      private final IInventory craftMatrix;

      public ItemCraftedEvent(PlayerEntity player, @Nonnull ItemStack crafting, IInventory craftMatrix) {
         super(player);
         this.crafting = crafting;
         this.craftMatrix = craftMatrix;
      }

      @Nonnull
      public ItemStack getCrafting() {
         return this.crafting;
      }

      public IInventory getInventory() {
         return this.craftMatrix;
      }
   }

   public static class ItemPickupEvent extends PlayerEvent {
      private final ItemEntity originalEntity;
      private final ItemStack stack;

      public ItemPickupEvent(PlayerEntity player, ItemEntity entPickedUp, ItemStack stack) {
         super(player);
         this.originalEntity = entPickedUp;
         this.stack = stack;
      }

      public ItemStack getStack() {
         return this.stack;
      }

      public ItemEntity getOriginalEntity() {
         return this.originalEntity;
      }
   }

   public static class Visibility extends PlayerEvent {
      private double visibilityModifier = 1.0D;

      public Visibility(PlayerEntity player) {
         super(player);
      }

      public void modifyVisibility(double mod) {
         this.visibilityModifier *= mod;
      }

      public double getVisibilityModifier() {
         return this.visibilityModifier;
      }
   }

   public static class SaveToFile extends PlayerEvent {
      private final File playerDirectory;
      private final String playerUUID;

      public SaveToFile(PlayerEntity player, File originDirectory, String playerUUID) {
         super(player);
         this.playerDirectory = originDirectory;
         this.playerUUID = playerUUID;
      }

      public File getPlayerFile(String suffix) {
         if ("dat".equals(suffix)) {
            throw new IllegalArgumentException("The suffix 'dat' is reserved");
         } else {
            return new File(this.getPlayerDirectory(), this.getPlayerUUID() + "." + suffix);
         }
      }

      public File getPlayerDirectory() {
         return this.playerDirectory;
      }

      public String getPlayerUUID() {
         return this.playerUUID;
      }
   }

   public static class LoadFromFile extends PlayerEvent {
      private final File playerDirectory;
      private final String playerUUID;

      public LoadFromFile(PlayerEntity player, File originDirectory, String playerUUID) {
         super(player);
         this.playerDirectory = originDirectory;
         this.playerUUID = playerUUID;
      }

      public File getPlayerFile(String suffix) {
         if ("dat".equals(suffix)) {
            throw new IllegalArgumentException("The suffix 'dat' is reserved");
         } else {
            return new File(this.getPlayerDirectory(), this.getPlayerUUID() + "." + suffix);
         }
      }

      public File getPlayerDirectory() {
         return this.playerDirectory;
      }

      public String getPlayerUUID() {
         return this.playerUUID;
      }
   }

   public static class StopTracking extends PlayerEvent {
      private final Entity target;

      public StopTracking(PlayerEntity player, Entity target) {
         super(player);
         this.target = target;
      }

      public Entity getTarget() {
         return this.target;
      }
   }

   public static class StartTracking extends PlayerEvent {
      private final Entity target;

      public StartTracking(PlayerEntity player, Entity target) {
         super(player);
         this.target = target;
      }

      public Entity getTarget() {
         return this.target;
      }
   }

   public static class Clone extends PlayerEvent {
      private final PlayerEntity original;
      private final boolean wasDeath;

      public Clone(PlayerEntity _new, PlayerEntity oldPlayer, boolean wasDeath) {
         super(_new);
         this.original = oldPlayer;
         this.wasDeath = wasDeath;
      }

      public PlayerEntity getOriginal() {
         return this.original;
      }

      public boolean isWasDeath() {
         return this.wasDeath;
      }
   }

   public static class NameFormat extends PlayerEvent {
      private final String username;
      private String displayname;

      public NameFormat(PlayerEntity player, String username) {
         super(player);
         this.username = username;
         this.setDisplayname(username);
      }

      public String getUsername() {
         return this.username;
      }

      public String getDisplayname() {
         return this.displayname;
      }

      public void setDisplayname(String displayname) {
         this.displayname = displayname;
      }
   }

   @Cancelable
   public static class BreakSpeed extends PlayerEvent {
      private final BlockState state;
      private final float originalSpeed;
      private float newSpeed = 0.0F;
      private final BlockPos pos;

      public BreakSpeed(PlayerEntity player, BlockState state, float original, BlockPos pos) {
         super(player);
         this.state = state;
         this.originalSpeed = original;
         this.setNewSpeed(original);
         this.pos = pos;
      }

      public BlockState getState() {
         return this.state;
      }

      public float getOriginalSpeed() {
         return this.originalSpeed;
      }

      public float getNewSpeed() {
         return this.newSpeed;
      }

      public void setNewSpeed(float newSpeed) {
         this.newSpeed = newSpeed;
      }

      public BlockPos getPos() {
         return this.pos;
      }
   }

   public static class HarvestCheck extends PlayerEvent {
      private final BlockState state;
      private boolean success;

      public HarvestCheck(PlayerEntity player, BlockState state, boolean success) {
         super(player);
         this.state = state;
         this.success = success;
      }

      public BlockState getTargetBlock() {
         return this.state;
      }

      public boolean canHarvest() {
         return this.success;
      }

      public void setCanHarvest(boolean success) {
         this.success = success;
      }
   }
}
