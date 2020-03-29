package net.minecraftforge.common.extensions;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import com.google.common.collect.ImmutableMap.Builder;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.animation.ITimeValue;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public interface IForgeItem {
   default Item getItem() {
      return (Item)this;
   }

   default Multimap<String, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot, ItemStack stack) {
      return this.getItem().getAttributeModifiers(slot);
   }

   default boolean onDroppedByPlayer(ItemStack item, PlayerEntity player) {
      return true;
   }

   default String getHighlightTip(ItemStack item, String displayName) {
      return displayName;
   }

   default ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext context) {
      return ActionResultType.PASS;
   }

   boolean isRepairable(ItemStack var1);

   default float getXpRepairRatio(ItemStack stack) {
      return 2.0F;
   }

   @Nullable
   default CompoundNBT getShareTag(ItemStack stack) {
      return stack.getTag();
   }

   default void readShareTag(ItemStack stack, @Nullable CompoundNBT nbt) {
      stack.setTag(nbt);
   }

   default boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, PlayerEntity player) {
      return false;
   }

   default void onUsingTick(ItemStack stack, LivingEntity player, int count) {
   }

   default boolean onLeftClickEntity(ItemStack stack, PlayerEntity player, Entity entity) {
      return false;
   }

   default ItemStack getContainerItem(ItemStack itemStack) {
      return !this.hasContainerItem(itemStack) ? ItemStack.EMPTY : new ItemStack(this.getItem().getContainerItem());
   }

   default boolean hasContainerItem(ItemStack stack) {
      return this.getItem().hasContainerItem();
   }

   default int getEntityLifespan(ItemStack itemStack, World world) {
      return 6000;
   }

   default boolean hasCustomEntity(ItemStack stack) {
      return false;
   }

   @Nullable
   default Entity createEntity(World world, Entity location, ItemStack itemstack) {
      return null;
   }

   default boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
      return false;
   }

   default Collection<ItemGroup> getCreativeTabs() {
      return Collections.singletonList(this.getItem().getGroup());
   }

   default float getSmeltingExperience(ItemStack item) {
      return -1.0F;
   }

   default boolean doesSneakBypassUse(ItemStack stack, IWorldReader world, BlockPos pos, PlayerEntity player) {
      return false;
   }

   default void onArmorTick(ItemStack stack, World world, PlayerEntity player) {
   }

   default boolean canEquip(ItemStack stack, EquipmentSlotType armorType, Entity entity) {
      return MobEntity.getSlotForItemStack(stack) == armorType;
   }

   @Nullable
   default EquipmentSlotType getEquipmentSlot(ItemStack stack) {
      return null;
   }

   default boolean isBookEnchantable(ItemStack stack, ItemStack book) {
      return true;
   }

   @Nullable
   default String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlotType slot, String type) {
      return null;
   }

   @OnlyIn(Dist.CLIENT)
   @Nullable
   default FontRenderer getFontRenderer(ItemStack stack) {
      return null;
   }

   @OnlyIn(Dist.CLIENT)
   @Nullable
   default <A extends BipedModel<?>> A getArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlotType armorSlot, A _default) {
      return null;
   }

   default boolean onEntitySwing(ItemStack stack, LivingEntity entity) {
      return false;
   }

   @OnlyIn(Dist.CLIENT)
   default void renderHelmetOverlay(ItemStack stack, PlayerEntity player, int width, int height, float partialTicks) {
   }

   default int getDamage(ItemStack stack) {
      return !stack.hasTag() ? 0 : stack.getTag().getInt("Damage");
   }

   default boolean showDurabilityBar(ItemStack stack) {
      return stack.isDamaged();
   }

   default double getDurabilityForDisplay(ItemStack stack) {
      return (double)stack.getDamage() / (double)stack.getMaxDamage();
   }

   default int getRGBDurabilityForDisplay(ItemStack stack) {
      return MathHelper.hsvToRGB(Math.max(0.0F, (float)(1.0D - this.getDurabilityForDisplay(stack))) / 3.0F, 1.0F, 1.0F);
   }

   default int getMaxDamage(ItemStack stack) {
      return this.getItem().getMaxDamage();
   }

   default boolean isDamaged(ItemStack stack) {
      return stack.getDamage() > 0;
   }

   default void setDamage(ItemStack stack, int damage) {
      stack.getOrCreateTag().putInt("Damage", Math.max(0, damage));
   }

   default boolean canHarvestBlock(ItemStack stack, BlockState state) {
      return this.getItem().canHarvestBlock(state);
   }

   default int getItemStackLimit(ItemStack stack) {
      return this.getItem().getMaxStackSize();
   }

   Set<ToolType> getToolTypes(ItemStack var1);

   int getHarvestLevel(ItemStack var1, ToolType var2, @Nullable PlayerEntity var3, @Nullable BlockState var4);

   default int getItemEnchantability(ItemStack stack) {
      return this.getItem().getItemEnchantability();
   }

   default boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
      return enchantment.type.canEnchantItem(stack.getItem());
   }

   default boolean isBeaconPayment(ItemStack stack) {
      return Tags.Items.BEACON_PAYMENT.contains(stack.getItem());
   }

   default boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
      return !oldStack.equals(newStack);
   }

   default boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack) {
      return newStack.getItem() != oldStack.getItem() || !ItemStack.areItemStackTagsEqual(newStack, oldStack) || !newStack.isDamageable() && newStack.getDamage() != oldStack.getDamage();
   }

   default boolean canContinueUsing(ItemStack oldStack, ItemStack newStack) {
      return ItemStack.areItemsEqualIgnoreDurability(oldStack, newStack);
   }

   @Nullable
   default String getCreatorModId(ItemStack itemStack) {
      return ForgeHooks.getDefaultCreatorModId(itemStack);
   }

   @Nullable
   default ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
      return null;
   }

   default ImmutableMap<String, ITimeValue> getAnimationParameters(ItemStack stack, World world, LivingEntity entity) {
      Builder<String, ITimeValue> builder = ImmutableMap.builder();
      this.getItem().properties.forEach((k, v) -> {
         builder.put(k.toString(), (input) -> {
            return v.call(stack, world, entity);
         });
      });
      return builder.build();
   }

   default boolean canDisableShield(ItemStack stack, ItemStack shield, LivingEntity entity, LivingEntity attacker) {
      return this instanceof AxeItem;
   }

   default boolean isShield(ItemStack stack, @Nullable LivingEntity entity) {
      return stack.getItem() == Items.SHIELD;
   }

   default int getBurnTime(ItemStack itemStack) {
      return -1;
   }

   default void onHorseArmorTick(ItemStack stack, World world, MobEntity horse) {
   }

   @OnlyIn(Dist.CLIENT)
   ItemStackTileEntityRenderer getItemStackTileEntityRenderer();

   Set<ResourceLocation> getTags();

   default <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
      return amount;
   }
}
