package net.minecraftforge.common.extensions;

import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.CachedBlockInfo;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public interface IForgeItemStack extends ICapabilitySerializable<CompoundNBT> {
   default ItemStack getStack() {
      return (ItemStack)this;
   }

   default ItemStack getContainerItem() {
      return this.getStack().getItem().getContainerItem(this.getStack());
   }

   default boolean hasContainerItem() {
      return this.getStack().getItem().hasContainerItem(this.getStack());
   }

   default int getBurnTime() {
      return this.getStack().getItem().getBurnTime(this.getStack());
   }

   default int getHarvestLevel(ToolType tool, @Nullable PlayerEntity player, @Nullable BlockState state) {
      return this.getStack().getItem().getHarvestLevel(this.getStack(), tool, player, state);
   }

   default Set<ToolType> getToolTypes() {
      return this.getStack().getItem().getToolTypes(this.getStack());
   }

   default ActionResultType onItemUseFirst(ItemUseContext context) {
      PlayerEntity entityplayer = context.getPlayer();
      BlockPos blockpos = context.getPos();
      CachedBlockInfo blockworldstate = new CachedBlockInfo(context.getWorld(), blockpos, false);
      if (entityplayer != null && !entityplayer.abilities.allowEdit && !this.getStack().canPlaceOn(context.getWorld().getTags(), blockworldstate)) {
         return ActionResultType.PASS;
      } else {
         Item item = this.getStack().getItem();
         ActionResultType enumactionresult = item.onItemUseFirst(this.getStack(), context);
         if (entityplayer != null && enumactionresult == ActionResultType.SUCCESS) {
            entityplayer.addStat(Stats.ITEM_USED.get(item));
         }

         return enumactionresult;
      }
   }

   default CompoundNBT serializeNBT() {
      CompoundNBT ret = new CompoundNBT();
      this.getStack().write(ret);
      return ret;
   }

   default boolean onBlockStartBreak(BlockPos pos, PlayerEntity player) {
      return !this.getStack().isEmpty() && this.getStack().getItem().onBlockStartBreak(this.getStack(), pos, player);
   }

   default boolean shouldCauseBlockBreakReset(ItemStack newStack) {
      return this.getStack().getItem().shouldCauseBlockBreakReset(this.getStack(), newStack);
   }

   default boolean canApplyAtEnchantingTable(Enchantment enchantment) {
      return this.getStack().getItem().canApplyAtEnchantingTable(this.getStack(), enchantment);
   }

   default int getItemEnchantability() {
      return this.getStack().getItem().getItemEnchantability(this.getStack());
   }

   @Nullable
   default EquipmentSlotType getEquipmentSlot() {
      return this.getStack().getItem().getEquipmentSlot(this.getStack());
   }

   default boolean canDisableShield(ItemStack shield, LivingEntity entity, LivingEntity attacker) {
      return this.getStack().getItem().canDisableShield(this.getStack(), shield, entity, attacker);
   }

   default boolean isShield(@Nullable LivingEntity entity) {
      return this.getStack().getItem().isShield(this.getStack(), entity);
   }

   default boolean onEntitySwing(LivingEntity entity) {
      return this.getStack().getItem().onEntitySwing(this.getStack(), entity);
   }

   default void onUsingTick(LivingEntity player, int count) {
      this.getStack().getItem().onUsingTick(this.getStack(), player, count);
   }

   default int getEntityLifespan(World world) {
      return this.getStack().getItem().getEntityLifespan(this.getStack(), world);
   }

   default boolean onEntityItemUpdate(ItemEntity entity) {
      return this.getStack().getItem().onEntityItemUpdate(this.getStack(), entity);
   }

   default float getXpRepairRatio() {
      return this.getStack().getItem().getXpRepairRatio(this.getStack());
   }

   default void onArmorTick(World world, PlayerEntity player) {
      this.getStack().getItem().onArmorTick(this.getStack(), world, player);
   }

   default void onHorseArmorTick(World world, MobEntity horse) {
      this.getStack().getItem().onHorseArmorTick(this.getStack(), world, horse);
   }

   default boolean isBeaconPayment() {
      return this.getStack().getItem().isBeaconPayment(this.getStack());
   }

   default boolean canEquip(EquipmentSlotType armorType, Entity entity) {
      return this.getStack().getItem().canEquip(this.getStack(), armorType, entity);
   }

   default boolean isBookEnchantable(ItemStack book) {
      return this.getStack().getItem().isBookEnchantable(this.getStack(), book);
   }

   default boolean onDroppedByPlayer(PlayerEntity player) {
      return this.getStack().getItem().onDroppedByPlayer(this.getStack(), player);
   }

   default String getHighlightTip(String displayName) {
      return this.getStack().getItem().getHighlightTip(this.getStack(), displayName);
   }

   @Nullable
   default CompoundNBT getShareTag() {
      return this.getStack().getItem().getShareTag(this.getStack());
   }

   default void readShareTag(@Nullable CompoundNBT nbt) {
      this.getStack().getItem().readShareTag(this.getStack(), nbt);
   }

   default boolean doesSneakBypassUse(IWorldReader world, BlockPos pos, PlayerEntity player) {
      return this.getStack().isEmpty() || this.getStack().getItem().doesSneakBypassUse(this.getStack(), world, pos, player);
   }

   default boolean areShareTagsEqual(ItemStack other) {
      CompoundNBT shareTagA = this.getStack().getShareTag();
      CompoundNBT shareTagB = other.getShareTag();
      if (shareTagA == null) {
         return shareTagB == null;
      } else {
         return shareTagB != null && shareTagA.equals(shareTagB);
      }
   }

   default boolean equals(ItemStack other, boolean limitTags) {
      if (this.getStack().isEmpty()) {
         return other.isEmpty();
      } else {
         boolean var10000;
         label35: {
            if (!other.isEmpty() && this.getStack().getCount() == other.getCount() && this.getStack().getItem() == other.getItem()) {
               if (limitTags) {
                  if (this.getStack().areShareTagsEqual(other)) {
                     break label35;
                  }
               } else if (ItemStack.areItemStackTagsEqual(this.getStack(), other)) {
                  break label35;
               }
            }

            var10000 = false;
            return var10000;
         }

         var10000 = true;
         return var10000;
      }
   }

   default boolean isRepairable() {
      return this.getStack().getItem().isRepairable(this.getStack());
   }
}
