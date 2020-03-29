package net.minecraft.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.extensions.IForgeItem;
import net.minecraftforge.common.util.ReverseTagWrapper;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.GameData;

public class Item extends ForgeRegistryEntry<Item> implements IItemProvider, IForgeItem {
   public static final Map<Block, Item> BLOCK_TO_ITEM = GameData.getBlockItemMap();
   private static final IItemPropertyGetter DAMAGED_GETTER = (p_lambda$static$0_0_, p_lambda$static$0_1_, p_lambda$static$0_2_) -> {
      return p_lambda$static$0_0_.isDamaged() ? 1.0F : 0.0F;
   };
   private static final IItemPropertyGetter DAMAGE_GETTER = (p_lambda$static$1_0_, p_lambda$static$1_1_, p_lambda$static$1_2_) -> {
      return MathHelper.clamp((float)p_lambda$static$1_0_.getDamage() / (float)p_lambda$static$1_0_.getMaxDamage(), 0.0F, 1.0F);
   };
   private static final IItemPropertyGetter LEFTHANDED_GETTER = (p_lambda$static$2_0_, p_lambda$static$2_1_, p_lambda$static$2_2_) -> {
      return p_lambda$static$2_2_ != null && p_lambda$static$2_2_.getPrimaryHand() != HandSide.RIGHT ? 1.0F : 0.0F;
   };
   private static final IItemPropertyGetter COOLDOWN_GETTER = (p_lambda$static$3_0_, p_lambda$static$3_1_, p_lambda$static$3_2_) -> {
      return p_lambda$static$3_2_ instanceof PlayerEntity ? ((PlayerEntity)p_lambda$static$3_2_).getCooldownTracker().getCooldown(p_lambda$static$3_0_.getItem(), 0.0F) : 0.0F;
   };
   private static final IItemPropertyGetter MODELDATA_GETTER = (p_lambda$static$4_0_, p_lambda$static$4_1_, p_lambda$static$4_2_) -> {
      return p_lambda$static$4_0_.hasTag() ? (float)p_lambda$static$4_0_.getTag().getInt("CustomModelData") : 0.0F;
   };
   protected static final UUID ATTACK_DAMAGE_MODIFIER = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
   protected static final UUID ATTACK_SPEED_MODIFIER = UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3");
   protected static final Random random = new Random();
   public final Map<ResourceLocation, IItemPropertyGetter> properties = Maps.newHashMap();
   protected final ItemGroup group;
   private final Rarity rarity;
   private final int maxStackSize;
   private final int maxDamage;
   private final Item containerItem;
   @Nullable
   private String translationKey;
   @Nullable
   private final Food food;
   @Nullable
   private final Supplier<ItemStackTileEntityRenderer> ister;
   private final Map<ToolType, Integer> toolClasses = Maps.newHashMap();
   private final ReverseTagWrapper<Item> reverseTags = new ReverseTagWrapper(this, ItemTags::getGeneration, ItemTags::getCollection);
   protected final boolean canRepair;

   public static int getIdFromItem(Item p_150891_0_) {
      return p_150891_0_ == null ? 0 : Registry.ITEM.getId(p_150891_0_);
   }

   public static Item getItemById(int p_150899_0_) {
      return (Item)Registry.ITEM.getByValue(p_150899_0_);
   }

   /** @deprecated */
   @Deprecated
   public static Item getItemFromBlock(Block p_150898_0_) {
      return (Item)BLOCK_TO_ITEM.getOrDefault(p_150898_0_, Items.AIR);
   }

   public Item(Item.Properties p_i48487_1_) {
      this.addPropertyOverride(new ResourceLocation("lefthanded"), LEFTHANDED_GETTER);
      this.addPropertyOverride(new ResourceLocation("cooldown"), COOLDOWN_GETTER);
      this.addPropertyOverride(new ResourceLocation("custom_model_data"), MODELDATA_GETTER);
      this.group = p_i48487_1_.group;
      this.rarity = p_i48487_1_.rarity;
      this.containerItem = p_i48487_1_.containerItem;
      this.maxDamage = p_i48487_1_.maxDamage;
      this.maxStackSize = p_i48487_1_.maxStackSize;
      this.food = p_i48487_1_.food;
      if (this.maxDamage > 0) {
         this.addPropertyOverride(new ResourceLocation("damaged"), DAMAGED_GETTER);
         this.addPropertyOverride(new ResourceLocation("damage"), DAMAGE_GETTER);
      }

      this.canRepair = p_i48487_1_.canRepair;
      this.toolClasses.putAll(p_i48487_1_.toolClasses);
      Object tmp = p_i48487_1_.ister == null ? null : DistExecutor.callWhenOn(Dist.CLIENT, p_i48487_1_.ister);
      this.ister = tmp == null ? null : () -> {
         return (ItemStackTileEntityRenderer)tmp;
      };
   }

   public void func_219972_a(World p_219972_1_, LivingEntity p_219972_2_, ItemStack p_219972_3_, int p_219972_4_) {
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public IItemPropertyGetter getPropertyGetter(ResourceLocation p_185045_1_) {
      return (IItemPropertyGetter)this.properties.get(p_185045_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean hasCustomProperties() {
      return !this.properties.isEmpty();
   }

   public boolean updateItemStackNBT(CompoundNBT p_179215_1_) {
      return false;
   }

   public boolean canPlayerBreakBlockWhileHolding(BlockState p_195938_1_, World p_195938_2_, BlockPos p_195938_3_, PlayerEntity p_195938_4_) {
      return true;
   }

   public Item asItem() {
      return this;
   }

   public final void addPropertyOverride(ResourceLocation p_185043_1_, IItemPropertyGetter p_185043_2_) {
      this.properties.put(p_185043_1_, p_185043_2_);
   }

   public ActionResultType onItemUse(ItemUseContext p_195939_1_) {
      return ActionResultType.PASS;
   }

   public float getDestroySpeed(ItemStack p_150893_1_, BlockState p_150893_2_) {
      return 1.0F;
   }

   public ActionResult<ItemStack> onItemRightClick(World p_77659_1_, PlayerEntity p_77659_2_, Hand p_77659_3_) {
      if (this.isFood()) {
         ItemStack itemstack = p_77659_2_.getHeldItem(p_77659_3_);
         if (p_77659_2_.canEat(this.getFood().canEatWhenFull())) {
            p_77659_2_.setActiveHand(p_77659_3_);
            return ActionResult.func_226249_b_(itemstack);
         } else {
            return ActionResult.func_226251_d_(itemstack);
         }
      } else {
         return ActionResult.func_226250_c_(p_77659_2_.getHeldItem(p_77659_3_));
      }
   }

   public ItemStack onItemUseFinish(ItemStack p_77654_1_, World p_77654_2_, LivingEntity p_77654_3_) {
      return this.isFood() ? p_77654_3_.onFoodEaten(p_77654_2_, p_77654_1_) : p_77654_1_;
   }

   /** @deprecated */
   @Deprecated
   public final int getMaxStackSize() {
      return this.maxStackSize;
   }

   /** @deprecated */
   @Deprecated
   public final int getMaxDamage() {
      return this.maxDamage;
   }

   public boolean isDamageable() {
      return this.maxDamage > 0;
   }

   public boolean hitEntity(ItemStack p_77644_1_, LivingEntity p_77644_2_, LivingEntity p_77644_3_) {
      return false;
   }

   public boolean onBlockDestroyed(ItemStack p_179218_1_, World p_179218_2_, BlockState p_179218_3_, BlockPos p_179218_4_, LivingEntity p_179218_5_) {
      return false;
   }

   public boolean canHarvestBlock(BlockState p_150897_1_) {
      return false;
   }

   public boolean itemInteractionForEntity(ItemStack p_111207_1_, PlayerEntity p_111207_2_, LivingEntity p_111207_3_, Hand p_111207_4_) {
      return false;
   }

   @OnlyIn(Dist.CLIENT)
   public ITextComponent getName() {
      return new TranslationTextComponent(this.getTranslationKey(), new Object[0]);
   }

   public String toString() {
      return Registry.ITEM.getKey(this).getPath();
   }

   protected String getDefaultTranslationKey() {
      if (this.translationKey == null) {
         this.translationKey = Util.makeTranslationKey("item", Registry.ITEM.getKey(this));
      }

      return this.translationKey;
   }

   public String getTranslationKey() {
      return this.getDefaultTranslationKey();
   }

   public String getTranslationKey(ItemStack p_77667_1_) {
      return this.getTranslationKey();
   }

   public boolean shouldSyncTag() {
      return true;
   }

   /** @deprecated */
   @Nullable
   @Deprecated
   public final Item getContainerItem() {
      return this.containerItem;
   }

   /** @deprecated */
   @Deprecated
   public boolean hasContainerItem() {
      return this.containerItem != null;
   }

   public void inventoryTick(ItemStack p_77663_1_, World p_77663_2_, Entity p_77663_3_, int p_77663_4_, boolean p_77663_5_) {
   }

   public void onCreated(ItemStack p_77622_1_, World p_77622_2_, PlayerEntity p_77622_3_) {
   }

   public boolean isComplex() {
      return false;
   }

   public UseAction getUseAction(ItemStack p_77661_1_) {
      return p_77661_1_.getItem().isFood() ? UseAction.EAT : UseAction.NONE;
   }

   public int getUseDuration(ItemStack p_77626_1_) {
      if (p_77626_1_.getItem().isFood()) {
         return this.getFood().isFastEating() ? 16 : 32;
      } else {
         return 0;
      }
   }

   public void onPlayerStoppedUsing(ItemStack p_77615_1_, World p_77615_2_, LivingEntity p_77615_3_, int p_77615_4_) {
   }

   @OnlyIn(Dist.CLIENT)
   public void addInformation(ItemStack p_77624_1_, @Nullable World p_77624_2_, List<ITextComponent> p_77624_3_, ITooltipFlag p_77624_4_) {
   }

   public ITextComponent getDisplayName(ItemStack p_200295_1_) {
      return new TranslationTextComponent(this.getTranslationKey(p_200295_1_), new Object[0]);
   }

   public boolean hasEffect(ItemStack p_77636_1_) {
      return p_77636_1_.isEnchanted();
   }

   public Rarity getRarity(ItemStack p_77613_1_) {
      if (!p_77613_1_.isEnchanted()) {
         return this.rarity;
      } else {
         switch(this.rarity) {
         case COMMON:
         case UNCOMMON:
            return Rarity.RARE;
         case RARE:
            return Rarity.EPIC;
         case EPIC:
         default:
            return this.rarity;
         }
      }
   }

   public boolean isEnchantable(ItemStack p_77616_1_) {
      return this.getItemStackLimit(p_77616_1_) == 1 && this.isDamageable();
   }

   protected static RayTraceResult rayTrace(World p_219968_0_, PlayerEntity p_219968_1_, RayTraceContext.FluidMode p_219968_2_) {
      float f = p_219968_1_.rotationPitch;
      float f1 = p_219968_1_.rotationYaw;
      Vec3d vec3d = p_219968_1_.getEyePosition(1.0F);
      float f2 = MathHelper.cos(-f1 * 0.017453292F - 3.1415927F);
      float f3 = MathHelper.sin(-f1 * 0.017453292F - 3.1415927F);
      float f4 = -MathHelper.cos(-f * 0.017453292F);
      float f5 = MathHelper.sin(-f * 0.017453292F);
      float f6 = f3 * f4;
      float f7 = f2 * f4;
      double d0 = p_219968_1_.getAttribute(PlayerEntity.REACH_DISTANCE).getValue();
      Vec3d vec3d1 = vec3d.add((double)f6 * d0, (double)f5 * d0, (double)f7 * d0);
      return p_219968_0_.rayTraceBlocks(new RayTraceContext(vec3d, vec3d1, RayTraceContext.BlockMode.OUTLINE, p_219968_2_, p_219968_1_));
   }

   public int getItemEnchantability() {
      return 0;
   }

   public void fillItemGroup(ItemGroup p_150895_1_, NonNullList<ItemStack> p_150895_2_) {
      if (this.isInGroup(p_150895_1_)) {
         p_150895_2_.add(new ItemStack(this));
      }

   }

   protected boolean isInGroup(ItemGroup p_194125_1_) {
      if (this.getCreativeTabs().stream().anyMatch((p_lambda$isInGroup$6_1_) -> {
         return p_lambda$isInGroup$6_1_ == p_194125_1_;
      })) {
         return true;
      } else {
         ItemGroup itemgroup = this.getGroup();
         return itemgroup != null && (p_194125_1_ == ItemGroup.SEARCH || p_194125_1_ == itemgroup);
      }
   }

   @Nullable
   public final ItemGroup getGroup() {
      return this.group;
   }

   public boolean getIsRepairable(ItemStack p_82789_1_, ItemStack p_82789_2_) {
      return false;
   }

   /** @deprecated */
   @Deprecated
   public Multimap<String, AttributeModifier> getAttributeModifiers(EquipmentSlotType p_111205_1_) {
      return HashMultimap.create();
   }

   public boolean isRepairable(ItemStack p_isRepairable_1_) {
      return this.canRepair && this.isDamageable();
   }

   public Set<ToolType> getToolTypes(ItemStack p_getToolTypes_1_) {
      return this.toolClasses.keySet();
   }

   public int getHarvestLevel(ItemStack p_getHarvestLevel_1_, ToolType p_getHarvestLevel_2_, @Nullable PlayerEntity p_getHarvestLevel_3_, @Nullable BlockState p_getHarvestLevel_4_) {
      return (Integer)this.toolClasses.getOrDefault(p_getHarvestLevel_2_, -1);
   }

   @OnlyIn(Dist.CLIENT)
   public final ItemStackTileEntityRenderer getItemStackTileEntityRenderer() {
      ItemStackTileEntityRenderer renderer = this.ister != null ? (ItemStackTileEntityRenderer)this.ister.get() : null;
      return renderer != null ? renderer : ItemStackTileEntityRenderer.instance;
   }

   public Set<ResourceLocation> getTags() {
      return this.reverseTags.getTagNames();
   }

   public boolean isCrossbow(ItemStack p_219970_1_) {
      return p_219970_1_.getItem() == Items.CROSSBOW;
   }

   @OnlyIn(Dist.CLIENT)
   public ItemStack getDefaultInstance() {
      return new ItemStack(this);
   }

   public boolean isIn(Tag<Item> p_206844_1_) {
      return p_206844_1_.contains(this);
   }

   public boolean isFood() {
      return this.food != null;
   }

   @Nullable
   public Food getFood() {
      return this.food;
   }

   public SoundEvent func_225520_U__() {
      return SoundEvents.ENTITY_GENERIC_DRINK;
   }

   public SoundEvent func_225519_S__() {
      return SoundEvents.ENTITY_GENERIC_EAT;
   }

   public static class Properties {
      private int maxStackSize = 64;
      private int maxDamage;
      private Item containerItem;
      private ItemGroup group;
      private Rarity rarity;
      private Food food;
      private boolean canRepair;
      private Map<ToolType, Integer> toolClasses;
      private Supplier<Callable<ItemStackTileEntityRenderer>> ister;

      public Properties() {
         this.rarity = Rarity.COMMON;
         this.canRepair = true;
         this.toolClasses = Maps.newHashMap();
      }

      public Item.Properties food(Food p_221540_1_) {
         this.food = p_221540_1_;
         return this;
      }

      public Item.Properties maxStackSize(int p_200917_1_) {
         if (this.maxDamage > 0) {
            throw new RuntimeException("Unable to have damage AND stack.");
         } else {
            this.maxStackSize = p_200917_1_;
            return this;
         }
      }

      public Item.Properties defaultMaxDamage(int p_200915_1_) {
         return this.maxDamage == 0 ? this.maxDamage(p_200915_1_) : this;
      }

      public Item.Properties maxDamage(int p_200918_1_) {
         this.maxDamage = p_200918_1_;
         this.maxStackSize = 1;
         return this;
      }

      public Item.Properties containerItem(Item p_200919_1_) {
         this.containerItem = p_200919_1_;
         return this;
      }

      public Item.Properties group(ItemGroup p_200916_1_) {
         this.group = p_200916_1_;
         return this;
      }

      public Item.Properties rarity(Rarity p_208103_1_) {
         this.rarity = p_208103_1_;
         return this;
      }

      public Item.Properties setNoRepair() {
         this.canRepair = false;
         return this;
      }

      public Item.Properties addToolType(ToolType p_addToolType_1_, int p_addToolType_2_) {
         this.toolClasses.put(p_addToolType_1_, p_addToolType_2_);
         return this;
      }

      public Item.Properties setISTER(Supplier<Callable<ItemStackTileEntityRenderer>> p_setISTER_1_) {
         this.ister = p_setISTER_1_;
         return this;
      }
   }
}
