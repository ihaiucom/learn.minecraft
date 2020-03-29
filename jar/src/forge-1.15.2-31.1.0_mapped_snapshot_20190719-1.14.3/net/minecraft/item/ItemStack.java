package net.minecraft.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.gson.JsonParseException;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.command.arguments.BlockPredicateArgument;
import net.minecraft.command.arguments.BlockStateParser;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.enchantment.UnbreakingEnchantment;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.NetworkTagManager;
import net.minecraft.tags.Tag;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.CachedBlockInfo;
import net.minecraft.util.Hand;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.CapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.extensions.IForgeItemStack;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.registries.IRegistryDelegate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class ItemStack extends CapabilityProvider<ItemStack> implements IForgeItemStack {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final ItemStack EMPTY = new ItemStack((Item)null);
   public static final DecimalFormat DECIMALFORMAT = createAttributeModifierDecimalFormat();
   private int count;
   private int animationsToGo;
   /** @deprecated */
   @Deprecated
   private final Item item;
   private CompoundNBT tag;
   private boolean isEmpty;
   private ItemFrameEntity itemFrame;
   private CachedBlockInfo canDestroyCacheBlock;
   private boolean canDestroyCacheResult;
   private CachedBlockInfo canPlaceOnCacheBlock;
   private boolean canPlaceOnCacheResult;
   private IRegistryDelegate<Item> delegate;
   private CompoundNBT capNBT;

   private static DecimalFormat createAttributeModifierDecimalFormat() {
      DecimalFormat decimalformat = new DecimalFormat("#.##");
      decimalformat.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ROOT));
      return decimalformat;
   }

   public ItemStack(IItemProvider p_i48203_1_) {
      this(p_i48203_1_, 1);
   }

   public ItemStack(IItemProvider p_i48204_1_, int p_i48204_2_) {
      this(p_i48204_1_, p_i48204_2_, (CompoundNBT)null);
   }

   public ItemStack(IItemProvider p_i230070_1_, int p_i230070_2_, @Nullable CompoundNBT p_i230070_3_) {
      super(ItemStack.class);
      this.capNBT = p_i230070_3_;
      this.item = p_i230070_1_ == null ? null : p_i230070_1_.asItem();
      this.count = p_i230070_2_;
      if (this.item != null && this.item.isDamageable()) {
         this.setDamage(this.getDamage());
      }

      this.updateEmptyState();
      this.forgeInit();
   }

   private void updateEmptyState() {
      this.isEmpty = false;
      this.isEmpty = this.isEmpty();
   }

   private ItemStack(CompoundNBT p_i47263_1_) {
      super(ItemStack.class);
      this.capNBT = p_i47263_1_.contains("ForgeCaps") ? p_i47263_1_.getCompound("ForgeCaps") : null;
      this.item = (Item)Registry.ITEM.getOrDefault(new ResourceLocation(p_i47263_1_.getString("id")));
      this.count = p_i47263_1_.getByte("Count");
      if (p_i47263_1_.contains("tag", 10)) {
         this.tag = p_i47263_1_.getCompound("tag");
         this.getItem().updateItemStackNBT(p_i47263_1_);
      }

      if (this.getItem().isDamageable()) {
         this.setDamage(this.getDamage());
      }

      this.updateEmptyState();
      this.forgeInit();
   }

   public static ItemStack read(CompoundNBT p_199557_0_) {
      try {
         return new ItemStack(p_199557_0_);
      } catch (RuntimeException var2) {
         LOGGER.debug("Tried to load invalid item: {}", p_199557_0_, var2);
         return EMPTY;
      }
   }

   public boolean isEmpty() {
      if (this == EMPTY) {
         return true;
      } else if (this.getItemRaw() != null && this.getItemRaw() != Items.AIR) {
         return this.count <= 0;
      } else {
         return true;
      }
   }

   public ItemStack split(int p_77979_1_) {
      int i = Math.min(p_77979_1_, this.count);
      ItemStack itemstack = this.copy();
      itemstack.setCount(i);
      this.shrink(i);
      return itemstack;
   }

   public Item getItem() {
      return !this.isEmpty && this.delegate != null ? (Item)this.delegate.get() : Items.AIR;
   }

   public ActionResultType onItemUse(ItemUseContext p_196084_1_) {
      return !p_196084_1_.world.isRemote ? ForgeHooks.onPlaceItemIntoWorld(p_196084_1_) : this.onItemUse(p_196084_1_, (p_lambda$onItemUse$0_2_) -> {
         return this.getItem().onItemUse(p_196084_1_);
      });
   }

   public ActionResultType onItemUseFirst(ItemUseContext p_onItemUseFirst_1_) {
      return this.onItemUse(p_onItemUseFirst_1_, (p_lambda$onItemUseFirst$1_2_) -> {
         return this.getItem().onItemUseFirst(this, p_onItemUseFirst_1_);
      });
   }

   private ActionResultType onItemUse(ItemUseContext p_onItemUse_1_, Function<ItemUseContext, ActionResultType> p_onItemUse_2_) {
      PlayerEntity playerentity = p_onItemUse_1_.getPlayer();
      BlockPos blockpos = p_onItemUse_1_.getPos();
      CachedBlockInfo cachedblockinfo = new CachedBlockInfo(p_onItemUse_1_.getWorld(), blockpos, false);
      if (playerentity != null && !playerentity.abilities.allowEdit && !this.canPlaceOn(p_onItemUse_1_.getWorld().getTags(), cachedblockinfo)) {
         return ActionResultType.PASS;
      } else {
         Item item = this.getItem();
         ActionResultType actionresulttype = (ActionResultType)p_onItemUse_2_.apply(p_onItemUse_1_);
         if (playerentity != null && actionresulttype == ActionResultType.SUCCESS) {
            playerentity.addStat(Stats.ITEM_USED.get(item));
         }

         return actionresulttype;
      }
   }

   public float getDestroySpeed(BlockState p_150997_1_) {
      return this.getItem().getDestroySpeed(this, p_150997_1_);
   }

   public ActionResult<ItemStack> useItemRightClick(World p_77957_1_, PlayerEntity p_77957_2_, Hand p_77957_3_) {
      return this.getItem().onItemRightClick(p_77957_1_, p_77957_2_, p_77957_3_);
   }

   public ItemStack onItemUseFinish(World p_77950_1_, LivingEntity p_77950_2_) {
      return this.getItem().onItemUseFinish(this, p_77950_1_, p_77950_2_);
   }

   public CompoundNBT write(CompoundNBT p_77955_1_) {
      ResourceLocation resourcelocation = Registry.ITEM.getKey(this.getItem());
      p_77955_1_.putString("id", resourcelocation == null ? "minecraft:air" : resourcelocation.toString());
      p_77955_1_.putByte("Count", (byte)this.count);
      if (this.tag != null) {
         p_77955_1_.put("tag", this.tag.copy());
      }

      CompoundNBT cnbt = this.serializeCaps();
      if (cnbt != null && !cnbt.isEmpty()) {
         p_77955_1_.put("ForgeCaps", cnbt);
      }

      return p_77955_1_;
   }

   public int getMaxStackSize() {
      return this.getItem().getItemStackLimit(this);
   }

   public boolean isStackable() {
      return this.getMaxStackSize() > 1 && (!this.isDamageable() || !this.isDamaged());
   }

   public boolean isDamageable() {
      if (!this.isEmpty && this.getItem().getMaxDamage(this) > 0) {
         CompoundNBT compoundnbt = this.getTag();
         return compoundnbt == null || !compoundnbt.getBoolean("Unbreakable");
      } else {
         return false;
      }
   }

   public boolean isDamaged() {
      return this.isDamageable() && this.getItem().isDamaged(this);
   }

   public int getDamage() {
      return this.getItem().getDamage(this);
   }

   public void setDamage(int p_196085_1_) {
      this.getItem().setDamage(this, p_196085_1_);
   }

   public int getMaxDamage() {
      return this.getItem().getMaxDamage(this);
   }

   public boolean attemptDamageItem(int p_96631_1_, Random p_96631_2_, @Nullable ServerPlayerEntity p_96631_3_) {
      if (!this.isDamageable()) {
         return false;
      } else {
         int i;
         if (p_96631_1_ > 0) {
            i = EnchantmentHelper.getEnchantmentLevel(Enchantments.UNBREAKING, this);
            int j = 0;

            for(int k = 0; i > 0 && k < p_96631_1_; ++k) {
               if (UnbreakingEnchantment.negateDamage(this, i, p_96631_2_)) {
                  ++j;
               }
            }

            p_96631_1_ -= j;
            if (p_96631_1_ <= 0) {
               return false;
            }
         }

         if (p_96631_3_ != null && p_96631_1_ != 0) {
            CriteriaTriggers.ITEM_DURABILITY_CHANGED.trigger(p_96631_3_, this, this.getDamage() + p_96631_1_);
         }

         i = this.getDamage() + p_96631_1_;
         this.setDamage(i);
         return i >= this.getMaxDamage();
      }
   }

   public <T extends LivingEntity> void damageItem(int p_222118_1_, T p_222118_2_, Consumer<T> p_222118_3_) {
      if (!p_222118_2_.world.isRemote && (!(p_222118_2_ instanceof PlayerEntity) || !((PlayerEntity)p_222118_2_).abilities.isCreativeMode) && this.isDamageable()) {
         p_222118_1_ = this.getItem().damageItem(this, p_222118_1_, p_222118_2_, p_222118_3_);
         if (this.attemptDamageItem(p_222118_1_, p_222118_2_.getRNG(), p_222118_2_ instanceof ServerPlayerEntity ? (ServerPlayerEntity)p_222118_2_ : null)) {
            p_222118_3_.accept(p_222118_2_);
            Item item = this.getItem();
            this.shrink(1);
            if (p_222118_2_ instanceof PlayerEntity) {
               ((PlayerEntity)p_222118_2_).addStat(Stats.ITEM_BROKEN.get(item));
            }

            this.setDamage(0);
         }
      }

   }

   public void hitEntity(LivingEntity p_77961_1_, PlayerEntity p_77961_2_) {
      Item item = this.getItem();
      if (item.hitEntity(this, p_77961_1_, p_77961_2_)) {
         p_77961_2_.addStat(Stats.ITEM_USED.get(item));
      }

   }

   public void onBlockDestroyed(World p_179548_1_, BlockState p_179548_2_, BlockPos p_179548_3_, PlayerEntity p_179548_4_) {
      Item item = this.getItem();
      if (item.onBlockDestroyed(this, p_179548_1_, p_179548_2_, p_179548_3_, p_179548_4_)) {
         p_179548_4_.addStat(Stats.ITEM_USED.get(item));
      }

   }

   public boolean canHarvestBlock(BlockState p_150998_1_) {
      return this.getItem().canHarvestBlock(this, p_150998_1_);
   }

   public boolean interactWithEntity(PlayerEntity p_111282_1_, LivingEntity p_111282_2_, Hand p_111282_3_) {
      return this.getItem().itemInteractionForEntity(this, p_111282_1_, p_111282_2_, p_111282_3_);
   }

   public ItemStack copy() {
      if (this.isEmpty()) {
         return EMPTY;
      } else {
         ItemStack itemstack = new ItemStack(this.getItem(), this.count, this.serializeCaps());
         itemstack.setAnimationsToGo(this.getAnimationsToGo());
         if (this.tag != null) {
            itemstack.tag = this.tag.copy();
         }

         return itemstack;
      }
   }

   public static boolean areItemStackTagsEqual(ItemStack p_77970_0_, ItemStack p_77970_1_) {
      if (p_77970_0_.isEmpty() && p_77970_1_.isEmpty()) {
         return true;
      } else if (!p_77970_0_.isEmpty() && !p_77970_1_.isEmpty()) {
         if (p_77970_0_.tag == null && p_77970_1_.tag != null) {
            return false;
         } else {
            return p_77970_0_.tag == null || p_77970_0_.tag.equals(p_77970_1_.tag) && p_77970_0_.areCapsCompatible(p_77970_1_);
         }
      } else {
         return false;
      }
   }

   public static boolean areItemStacksEqual(ItemStack p_77989_0_, ItemStack p_77989_1_) {
      if (p_77989_0_.isEmpty() && p_77989_1_.isEmpty()) {
         return true;
      } else {
         return !p_77989_0_.isEmpty() && !p_77989_1_.isEmpty() ? p_77989_0_.isItemStackEqual(p_77989_1_) : false;
      }
   }

   private boolean isItemStackEqual(ItemStack p_77959_1_) {
      if (this.count != p_77959_1_.count) {
         return false;
      } else if (this.getItem() != p_77959_1_.getItem()) {
         return false;
      } else if (this.tag == null && p_77959_1_.tag != null) {
         return false;
      } else {
         return this.tag == null || this.tag.equals(p_77959_1_.tag) && this.areCapsCompatible(p_77959_1_);
      }
   }

   public static boolean areItemsEqual(ItemStack p_179545_0_, ItemStack p_179545_1_) {
      if (p_179545_0_ == p_179545_1_) {
         return true;
      } else {
         return !p_179545_0_.isEmpty() && !p_179545_1_.isEmpty() ? p_179545_0_.isItemEqual(p_179545_1_) : false;
      }
   }

   public static boolean areItemsEqualIgnoreDurability(ItemStack p_185132_0_, ItemStack p_185132_1_) {
      if (p_185132_0_ == p_185132_1_) {
         return true;
      } else {
         return !p_185132_0_.isEmpty() && !p_185132_1_.isEmpty() ? p_185132_0_.isItemEqualIgnoreDurability(p_185132_1_) : false;
      }
   }

   public boolean isItemEqual(ItemStack p_77969_1_) {
      return !p_77969_1_.isEmpty() && this.getItem() == p_77969_1_.getItem();
   }

   public boolean isItemEqualIgnoreDurability(ItemStack p_185136_1_) {
      if (!this.isDamageable()) {
         return this.isItemEqual(p_185136_1_);
      } else {
         return !p_185136_1_.isEmpty() && this.getItem() == p_185136_1_.getItem();
      }
   }

   public String getTranslationKey() {
      return this.getItem().getTranslationKey(this);
   }

   public String toString() {
      return this.count + " " + this.getItem();
   }

   public void inventoryTick(World p_77945_1_, Entity p_77945_2_, int p_77945_3_, boolean p_77945_4_) {
      if (this.animationsToGo > 0) {
         --this.animationsToGo;
      }

      if (this.getItem() != null) {
         this.getItem().inventoryTick(this, p_77945_1_, p_77945_2_, p_77945_3_, p_77945_4_);
      }

   }

   public void onCrafting(World p_77980_1_, PlayerEntity p_77980_2_, int p_77980_3_) {
      p_77980_2_.addStat(Stats.ITEM_CRAFTED.get(this.getItem()), p_77980_3_);
      this.getItem().onCreated(this, p_77980_1_, p_77980_2_);
   }

   public int getUseDuration() {
      return this.getItem().getUseDuration(this);
   }

   public UseAction getUseAction() {
      return this.getItem().getUseAction(this);
   }

   public void onPlayerStoppedUsing(World p_77974_1_, LivingEntity p_77974_2_, int p_77974_3_) {
      this.getItem().onPlayerStoppedUsing(this, p_77974_1_, p_77974_2_, p_77974_3_);
   }

   public boolean isCrossbowStack() {
      return this.getItem().isCrossbow(this);
   }

   public boolean hasTag() {
      return !this.isEmpty && this.tag != null && !this.tag.isEmpty();
   }

   @Nullable
   public CompoundNBT getTag() {
      return this.tag;
   }

   public CompoundNBT getOrCreateTag() {
      if (this.tag == null) {
         this.setTag(new CompoundNBT());
      }

      return this.tag;
   }

   public CompoundNBT getOrCreateChildTag(String p_190925_1_) {
      if (this.tag != null && this.tag.contains(p_190925_1_, 10)) {
         return this.tag.getCompound(p_190925_1_);
      } else {
         CompoundNBT compoundnbt = new CompoundNBT();
         this.setTagInfo(p_190925_1_, compoundnbt);
         return compoundnbt;
      }
   }

   @Nullable
   public CompoundNBT getChildTag(String p_179543_1_) {
      return this.tag != null && this.tag.contains(p_179543_1_, 10) ? this.tag.getCompound(p_179543_1_) : null;
   }

   public void removeChildTag(String p_196083_1_) {
      if (this.tag != null && this.tag.contains(p_196083_1_)) {
         this.tag.remove(p_196083_1_);
         if (this.tag.isEmpty()) {
            this.tag = null;
         }
      }

   }

   public ListNBT getEnchantmentTagList() {
      return this.tag != null ? this.tag.getList("Enchantments", 10) : new ListNBT();
   }

   public void setTag(@Nullable CompoundNBT p_77982_1_) {
      this.tag = p_77982_1_;
      if (this.getItem().isDamageable()) {
         this.setDamage(this.getDamage());
      }

   }

   public ITextComponent getDisplayName() {
      CompoundNBT compoundnbt = this.getChildTag("display");
      if (compoundnbt != null && compoundnbt.contains("Name", 8)) {
         try {
            ITextComponent itextcomponent = ITextComponent.Serializer.fromJson(compoundnbt.getString("Name"));
            if (itextcomponent != null) {
               return itextcomponent;
            }

            compoundnbt.remove("Name");
         } catch (JsonParseException var3) {
            compoundnbt.remove("Name");
         }
      }

      return this.getItem().getDisplayName(this);
   }

   public ItemStack setDisplayName(@Nullable ITextComponent p_200302_1_) {
      CompoundNBT compoundnbt = this.getOrCreateChildTag("display");
      if (p_200302_1_ != null) {
         compoundnbt.putString("Name", ITextComponent.Serializer.toJson(p_200302_1_));
      } else {
         compoundnbt.remove("Name");
      }

      return this;
   }

   public void clearCustomName() {
      CompoundNBT compoundnbt = this.getChildTag("display");
      if (compoundnbt != null) {
         compoundnbt.remove("Name");
         if (compoundnbt.isEmpty()) {
            this.removeChildTag("display");
         }
      }

      if (this.tag != null && this.tag.isEmpty()) {
         this.tag = null;
      }

   }

   public boolean hasDisplayName() {
      CompoundNBT compoundnbt = this.getChildTag("display");
      return compoundnbt != null && compoundnbt.contains("Name", 8);
   }

   @OnlyIn(Dist.CLIENT)
   public List<ITextComponent> getTooltip(@Nullable PlayerEntity p_82840_1_, ITooltipFlag p_82840_2_) {
      List<ITextComponent> list = Lists.newArrayList();
      ITextComponent itextcomponent = (new StringTextComponent("")).appendSibling(this.getDisplayName()).applyTextStyle(this.getRarity().color);
      if (this.hasDisplayName()) {
         itextcomponent.applyTextStyle(TextFormatting.ITALIC);
      }

      list.add(itextcomponent);
      if (!p_82840_2_.isAdvanced() && !this.hasDisplayName() && this.getItem() == Items.FILLED_MAP) {
         list.add((new StringTextComponent("#" + FilledMapItem.getMapId(this))).applyTextStyle(TextFormatting.GRAY));
      }

      int i = 0;
      if (this.hasTag() && this.tag.contains("HideFlags", 99)) {
         i = this.tag.getInt("HideFlags");
      }

      if ((i & 32) == 0) {
         this.getItem().addInformation(this, p_82840_1_ == null ? null : p_82840_1_.world, list, p_82840_2_);
      }

      int j;
      if (this.hasTag()) {
         if ((i & 1) == 0) {
            addEnchantmentTooltips(list, this.getEnchantmentTagList());
         }

         if (this.tag.contains("display", 10)) {
            CompoundNBT compoundnbt = this.tag.getCompound("display");
            if (compoundnbt.contains("color", 3)) {
               if (p_82840_2_.isAdvanced()) {
                  list.add((new TranslationTextComponent("item.color", new Object[]{String.format("#%06X", compoundnbt.getInt("color"))})).applyTextStyle(TextFormatting.GRAY));
               } else {
                  list.add((new TranslationTextComponent("item.dyed", new Object[0])).applyTextStyles(new TextFormatting[]{TextFormatting.GRAY, TextFormatting.ITALIC}));
               }
            }

            if (compoundnbt.getTagId("Lore") == 9) {
               ListNBT listnbt = compoundnbt.getList("Lore", 8);

               for(j = 0; j < listnbt.size(); ++j) {
                  String s = listnbt.getString(j);

                  try {
                     ITextComponent itextcomponent1 = ITextComponent.Serializer.fromJson(s);
                     if (itextcomponent1 != null) {
                        list.add(TextComponentUtils.mergeStyles(itextcomponent1, (new Style()).setColor(TextFormatting.DARK_PURPLE).setItalic(true)));
                     }
                  } catch (JsonParseException var19) {
                     compoundnbt.remove("Lore");
                  }
               }
            }
         }
      }

      EquipmentSlotType[] var20 = EquipmentSlotType.values();
      int l = var20.length;

      for(j = 0; j < l; ++j) {
         EquipmentSlotType equipmentslottype = var20[j];
         Multimap<String, AttributeModifier> multimap = this.getAttributeModifiers(equipmentslottype);
         if (!multimap.isEmpty() && (i & 2) == 0) {
            list.add(new StringTextComponent(""));
            list.add((new TranslationTextComponent("item.modifiers." + equipmentslottype.getName(), new Object[0])).applyTextStyle(TextFormatting.GRAY));
            Iterator var11 = multimap.entries().iterator();

            while(var11.hasNext()) {
               Entry<String, AttributeModifier> entry = (Entry)var11.next();
               AttributeModifier attributemodifier = (AttributeModifier)entry.getValue();
               double d0 = attributemodifier.getAmount();
               boolean flag = false;
               if (p_82840_1_ != null) {
                  if (attributemodifier.getID() == Item.ATTACK_DAMAGE_MODIFIER) {
                     d0 += p_82840_1_.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getBaseValue();
                     d0 += (double)EnchantmentHelper.getModifierForCreature(this, CreatureAttribute.UNDEFINED);
                     flag = true;
                  } else if (attributemodifier.getID() == Item.ATTACK_SPEED_MODIFIER) {
                     d0 += p_82840_1_.getAttribute(SharedMonsterAttributes.ATTACK_SPEED).getBaseValue();
                     flag = true;
                  }
               }

               double d1;
               if (attributemodifier.getOperation() != AttributeModifier.Operation.MULTIPLY_BASE && attributemodifier.getOperation() != AttributeModifier.Operation.MULTIPLY_TOTAL) {
                  d1 = d0;
               } else {
                  d1 = d0 * 100.0D;
               }

               if (flag) {
                  list.add((new StringTextComponent(" ")).appendSibling(new TranslationTextComponent("attribute.modifier.equals." + attributemodifier.getOperation().getId(), new Object[]{DECIMALFORMAT.format(d1), new TranslationTextComponent("attribute.name." + (String)entry.getKey(), new Object[0])})).applyTextStyle(TextFormatting.DARK_GREEN));
               } else if (d0 > 0.0D) {
                  list.add((new TranslationTextComponent("attribute.modifier.plus." + attributemodifier.getOperation().getId(), new Object[]{DECIMALFORMAT.format(d1), new TranslationTextComponent("attribute.name." + (String)entry.getKey(), new Object[0])})).applyTextStyle(TextFormatting.BLUE));
               } else if (d0 < 0.0D) {
                  d1 *= -1.0D;
                  list.add((new TranslationTextComponent("attribute.modifier.take." + attributemodifier.getOperation().getId(), new Object[]{DECIMALFORMAT.format(d1), new TranslationTextComponent("attribute.name." + (String)entry.getKey(), new Object[0])})).applyTextStyle(TextFormatting.RED));
               }
            }
         }
      }

      if (this.hasTag() && this.getTag().getBoolean("Unbreakable") && (i & 4) == 0) {
         list.add((new TranslationTextComponent("item.unbreakable", new Object[0])).applyTextStyle(TextFormatting.BLUE));
      }

      ListNBT listnbt2;
      if (this.hasTag() && this.tag.contains("CanDestroy", 9) && (i & 8) == 0) {
         listnbt2 = this.tag.getList("CanDestroy", 8);
         if (!listnbt2.isEmpty()) {
            list.add(new StringTextComponent(""));
            list.add((new TranslationTextComponent("item.canBreak", new Object[0])).applyTextStyle(TextFormatting.GRAY));

            for(l = 0; l < listnbt2.size(); ++l) {
               list.addAll(getPlacementTooltip(listnbt2.getString(l)));
            }
         }
      }

      if (this.hasTag() && this.tag.contains("CanPlaceOn", 9) && (i & 16) == 0) {
         listnbt2 = this.tag.getList("CanPlaceOn", 8);
         if (!listnbt2.isEmpty()) {
            list.add(new StringTextComponent(""));
            list.add((new TranslationTextComponent("item.canPlace", new Object[0])).applyTextStyle(TextFormatting.GRAY));

            for(l = 0; l < listnbt2.size(); ++l) {
               list.addAll(getPlacementTooltip(listnbt2.getString(l)));
            }
         }
      }

      if (p_82840_2_.isAdvanced()) {
         if (this.isDamaged()) {
            list.add(new TranslationTextComponent("item.durability", new Object[]{this.getMaxDamage() - this.getDamage(), this.getMaxDamage()}));
         }

         list.add((new StringTextComponent(Registry.ITEM.getKey(this.getItem()).toString())).applyTextStyle(TextFormatting.DARK_GRAY));
         if (this.hasTag()) {
            list.add((new TranslationTextComponent("item.nbt_tags", new Object[]{this.getTag().keySet().size()})).applyTextStyle(TextFormatting.DARK_GRAY));
         }
      }

      ForgeEventFactory.onItemTooltip(this, p_82840_1_, list, p_82840_2_);
      return list;
   }

   @OnlyIn(Dist.CLIENT)
   public static void addEnchantmentTooltips(List<ITextComponent> p_222120_0_, ListNBT p_222120_1_) {
      for(int i = 0; i < p_222120_1_.size(); ++i) {
         CompoundNBT compoundnbt = p_222120_1_.getCompound(i);
         Registry.ENCHANTMENT.getValue(ResourceLocation.tryCreate(compoundnbt.getString("id"))).ifPresent((p_lambda$addEnchantmentTooltips$2_2_) -> {
            p_222120_0_.add(p_lambda$addEnchantmentTooltips$2_2_.getDisplayName(compoundnbt.getInt("lvl")));
         });
      }

   }

   @OnlyIn(Dist.CLIENT)
   private static Collection<ITextComponent> getPlacementTooltip(String p_206845_0_) {
      try {
         BlockStateParser blockstateparser = (new BlockStateParser(new StringReader(p_206845_0_), true)).parse(true);
         BlockState blockstate = blockstateparser.getState();
         ResourceLocation resourcelocation = blockstateparser.getTag();
         boolean flag = blockstate != null;
         boolean flag1 = resourcelocation != null;
         if (flag || flag1) {
            if (flag) {
               return Lists.newArrayList(blockstate.getBlock().getNameTextComponent().applyTextStyle(TextFormatting.DARK_GRAY));
            }

            Tag<Block> tag = BlockTags.getCollection().get(resourcelocation);
            if (tag != null) {
               Collection<Block> collection = tag.getAllElements();
               if (!collection.isEmpty()) {
                  return (Collection)collection.stream().map(Block::getNameTextComponent).map((p_lambda$getPlacementTooltip$3_0_) -> {
                     return p_lambda$getPlacementTooltip$3_0_.applyTextStyle(TextFormatting.DARK_GRAY);
                  }).collect(Collectors.toList());
               }
            }
         }
      } catch (CommandSyntaxException var8) {
      }

      return Lists.newArrayList((new StringTextComponent("missingno")).applyTextStyle(TextFormatting.DARK_GRAY));
   }

   public boolean hasEffect() {
      return this.getItem().hasEffect(this);
   }

   public Rarity getRarity() {
      return this.getItem().getRarity(this);
   }

   public boolean isEnchantable() {
      if (!this.getItem().isEnchantable(this)) {
         return false;
      } else {
         return !this.isEnchanted();
      }
   }

   public void addEnchantment(Enchantment p_77966_1_, int p_77966_2_) {
      this.getOrCreateTag();
      if (!this.tag.contains("Enchantments", 9)) {
         this.tag.put("Enchantments", new ListNBT());
      }

      ListNBT listnbt = this.tag.getList("Enchantments", 10);
      CompoundNBT compoundnbt = new CompoundNBT();
      compoundnbt.putString("id", String.valueOf(Registry.ENCHANTMENT.getKey(p_77966_1_)));
      compoundnbt.putShort("lvl", (short)((byte)p_77966_2_));
      listnbt.add(compoundnbt);
   }

   public boolean isEnchanted() {
      if (this.tag != null && this.tag.contains("Enchantments", 9)) {
         return !this.tag.getList("Enchantments", 10).isEmpty();
      } else {
         return false;
      }
   }

   public void setTagInfo(String p_77983_1_, INBT p_77983_2_) {
      this.getOrCreateTag().put(p_77983_1_, p_77983_2_);
   }

   public boolean isOnItemFrame() {
      return this.itemFrame != null;
   }

   public void setItemFrame(@Nullable ItemFrameEntity p_82842_1_) {
      this.itemFrame = p_82842_1_;
   }

   @Nullable
   public ItemFrameEntity getItemFrame() {
      return this.isEmpty ? null : this.itemFrame;
   }

   public int getRepairCost() {
      return this.hasTag() && this.tag.contains("RepairCost", 3) ? this.tag.getInt("RepairCost") : 0;
   }

   public void setRepairCost(int p_82841_1_) {
      this.getOrCreateTag().putInt("RepairCost", p_82841_1_);
   }

   public Multimap<String, AttributeModifier> getAttributeModifiers(EquipmentSlotType p_111283_1_) {
      Object multimap;
      if (this.hasTag() && this.tag.contains("AttributeModifiers", 9)) {
         multimap = HashMultimap.create();
         ListNBT listnbt = this.tag.getList("AttributeModifiers", 10);

         for(int i = 0; i < listnbt.size(); ++i) {
            CompoundNBT compoundnbt = listnbt.getCompound(i);
            AttributeModifier attributemodifier = SharedMonsterAttributes.readAttributeModifier(compoundnbt);
            if (attributemodifier != null && (!compoundnbt.contains("Slot", 8) || compoundnbt.getString("Slot").equals(p_111283_1_.getName())) && attributemodifier.getID().getLeastSignificantBits() != 0L && attributemodifier.getID().getMostSignificantBits() != 0L) {
               ((Multimap)multimap).put(compoundnbt.getString("AttributeName"), attributemodifier);
            }
         }
      } else {
         multimap = this.getItem().getAttributeModifiers(p_111283_1_, this);
      }

      ((Multimap)multimap).values().forEach((p_lambda$getAttributeModifiers$4_0_) -> {
         p_lambda$getAttributeModifiers$4_0_.setSaved(false);
      });
      return (Multimap)multimap;
   }

   public void addAttributeModifier(String p_185129_1_, AttributeModifier p_185129_2_, @Nullable EquipmentSlotType p_185129_3_) {
      this.getOrCreateTag();
      if (!this.tag.contains("AttributeModifiers", 9)) {
         this.tag.put("AttributeModifiers", new ListNBT());
      }

      ListNBT listnbt = this.tag.getList("AttributeModifiers", 10);
      CompoundNBT compoundnbt = SharedMonsterAttributes.writeAttributeModifier(p_185129_2_);
      compoundnbt.putString("AttributeName", p_185129_1_);
      if (p_185129_3_ != null) {
         compoundnbt.putString("Slot", p_185129_3_.getName());
      }

      listnbt.add(compoundnbt);
   }

   public ITextComponent getTextComponent() {
      ITextComponent itextcomponent = (new StringTextComponent("")).appendSibling(this.getDisplayName());
      if (this.hasDisplayName()) {
         itextcomponent.applyTextStyle(TextFormatting.ITALIC);
      }

      ITextComponent itextcomponent1 = TextComponentUtils.wrapInSquareBrackets(itextcomponent);
      if (!this.isEmpty) {
         CompoundNBT compoundnbt = this.write(new CompoundNBT());
         itextcomponent1.applyTextStyle(this.getRarity().color).applyTextStyle((p_lambda$getTextComponent$5_1_) -> {
            p_lambda$getTextComponent$5_1_.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new StringTextComponent(compoundnbt.toString())));
         });
      }

      return itextcomponent1;
   }

   private static boolean isStateAndTileEntityEqual(CachedBlockInfo p_206846_0_, @Nullable CachedBlockInfo p_206846_1_) {
      if (p_206846_1_ != null && p_206846_0_.getBlockState() == p_206846_1_.getBlockState()) {
         if (p_206846_0_.getTileEntity() == null && p_206846_1_.getTileEntity() == null) {
            return true;
         } else {
            return p_206846_0_.getTileEntity() != null && p_206846_1_.getTileEntity() != null ? Objects.equals(p_206846_0_.getTileEntity().write(new CompoundNBT()), p_206846_1_.getTileEntity().write(new CompoundNBT())) : false;
         }
      } else {
         return false;
      }
   }

   public boolean canDestroy(NetworkTagManager p_206848_1_, CachedBlockInfo p_206848_2_) {
      if (isStateAndTileEntityEqual(p_206848_2_, this.canDestroyCacheBlock)) {
         return this.canDestroyCacheResult;
      } else {
         this.canDestroyCacheBlock = p_206848_2_;
         if (this.hasTag() && this.tag.contains("CanDestroy", 9)) {
            ListNBT listnbt = this.tag.getList("CanDestroy", 8);

            for(int i = 0; i < listnbt.size(); ++i) {
               String s = listnbt.getString(i);

               try {
                  Predicate<CachedBlockInfo> predicate = BlockPredicateArgument.blockPredicate().parse(new StringReader(s)).create(p_206848_1_);
                  if (predicate.test(p_206848_2_)) {
                     this.canDestroyCacheResult = true;
                     return true;
                  }
               } catch (CommandSyntaxException var7) {
               }
            }
         }

         this.canDestroyCacheResult = false;
         return false;
      }
   }

   public boolean canPlaceOn(NetworkTagManager p_206847_1_, CachedBlockInfo p_206847_2_) {
      if (isStateAndTileEntityEqual(p_206847_2_, this.canPlaceOnCacheBlock)) {
         return this.canPlaceOnCacheResult;
      } else {
         this.canPlaceOnCacheBlock = p_206847_2_;
         if (this.hasTag() && this.tag.contains("CanPlaceOn", 9)) {
            ListNBT listnbt = this.tag.getList("CanPlaceOn", 8);

            for(int i = 0; i < listnbt.size(); ++i) {
               String s = listnbt.getString(i);

               try {
                  Predicate<CachedBlockInfo> predicate = BlockPredicateArgument.blockPredicate().parse(new StringReader(s)).create(p_206847_1_);
                  if (predicate.test(p_206847_2_)) {
                     this.canPlaceOnCacheResult = true;
                     return true;
                  }
               } catch (CommandSyntaxException var7) {
               }
            }
         }

         this.canPlaceOnCacheResult = false;
         return false;
      }
   }

   public int getAnimationsToGo() {
      return this.animationsToGo;
   }

   public void setAnimationsToGo(int p_190915_1_) {
      this.animationsToGo = p_190915_1_;
   }

   public int getCount() {
      return this.isEmpty ? 0 : this.count;
   }

   public void setCount(int p_190920_1_) {
      this.count = p_190920_1_;
      this.updateEmptyState();
   }

   public void grow(int p_190917_1_) {
      this.setCount(this.count + p_190917_1_);
   }

   public void shrink(int p_190918_1_) {
      this.grow(-p_190918_1_);
   }

   public void func_222121_b(World p_222121_1_, LivingEntity p_222121_2_, int p_222121_3_) {
      this.getItem().func_219972_a(p_222121_1_, p_222121_2_, this, p_222121_3_);
   }

   public boolean isFood() {
      return this.getItem().isFood();
   }

   public void deserializeNBT(CompoundNBT p_deserializeNBT_1_) {
      ItemStack itemStack = read(p_deserializeNBT_1_);
      this.getStack().setTag(itemStack.getTag());
      if (itemStack.capNBT != null) {
         this.deserializeCaps(itemStack.capNBT);
      }

   }

   private void forgeInit() {
      Item item = this.getItemRaw();
      if (item != null) {
         this.delegate = item.delegate;
         ICapabilityProvider provider = item.initCapabilities(this, this.capNBT);
         this.gatherCapabilities(provider);
         if (this.capNBT != null) {
            this.deserializeCaps(this.capNBT);
         }
      }

   }

   @Nullable
   private Item getItemRaw() {
      return this.item;
   }

   public SoundEvent func_226629_F_() {
      return this.getItem().func_225520_U__();
   }

   public SoundEvent func_226630_G_() {
      return this.getItem().func_225519_S__();
   }
}
