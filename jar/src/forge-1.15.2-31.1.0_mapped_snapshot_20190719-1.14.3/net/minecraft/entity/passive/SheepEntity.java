package net.minecraft.entity.passive;

import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.Pose;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.BreedGoal;
import net.minecraft.entity.ai.goal.EatGrassGoal;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.PanicGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.DyeColor;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTables;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.IShearable;

public class SheepEntity extends AnimalEntity implements IShearable {
   private static final DataParameter<Byte> DYE_COLOR;
   private static final Map<DyeColor, IItemProvider> WOOL_BY_COLOR;
   private static final Map<DyeColor, float[]> DYE_TO_RGB;
   private int sheepTimer;
   private EatGrassGoal eatGrassGoal;

   private static float[] createSheepColor(DyeColor p_192020_0_) {
      if (p_192020_0_ == DyeColor.WHITE) {
         return new float[]{0.9019608F, 0.9019608F, 0.9019608F};
      } else {
         float[] afloat = p_192020_0_.getColorComponentValues();
         float f = 0.75F;
         return new float[]{afloat[0] * 0.75F, afloat[1] * 0.75F, afloat[2] * 0.75F};
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static float[] getDyeRgb(DyeColor p_175513_0_) {
      return (float[])DYE_TO_RGB.get(p_175513_0_);
   }

   public SheepEntity(EntityType<? extends SheepEntity> p_i50245_1_, World p_i50245_2_) {
      super(p_i50245_1_, p_i50245_2_);
   }

   protected void registerGoals() {
      this.eatGrassGoal = new EatGrassGoal(this);
      this.goalSelector.addGoal(0, new SwimGoal(this));
      this.goalSelector.addGoal(1, new PanicGoal(this, 1.25D));
      this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D));
      this.goalSelector.addGoal(3, new TemptGoal(this, 1.1D, Ingredient.fromItems(Items.WHEAT), false));
      this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.1D));
      this.goalSelector.addGoal(5, this.eatGrassGoal);
      this.goalSelector.addGoal(6, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
      this.goalSelector.addGoal(7, new LookAtGoal(this, PlayerEntity.class, 6.0F));
      this.goalSelector.addGoal(8, new LookRandomlyGoal(this));
   }

   protected void updateAITasks() {
      this.sheepTimer = this.eatGrassGoal.getEatingGrassTimer();
      super.updateAITasks();
   }

   public void livingTick() {
      if (this.world.isRemote) {
         this.sheepTimer = Math.max(0, this.sheepTimer - 1);
      }

      super.livingTick();
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(8.0D);
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.23000000417232513D);
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(DYE_COLOR, (byte)0);
   }

   public ResourceLocation getLootTable() {
      if (this.getSheared()) {
         return this.getType().getLootTable();
      } else {
         switch(this.getFleeceColor()) {
         case WHITE:
         default:
            return LootTables.ENTITIES_SHEEP_WHITE;
         case ORANGE:
            return LootTables.ENTITIES_SHEEP_ORANGE;
         case MAGENTA:
            return LootTables.ENTITIES_SHEEP_MAGENTA;
         case LIGHT_BLUE:
            return LootTables.ENTITIES_SHEEP_LIGHT_BLUE;
         case YELLOW:
            return LootTables.ENTITIES_SHEEP_YELLOW;
         case LIME:
            return LootTables.ENTITIES_SHEEP_LIME;
         case PINK:
            return LootTables.ENTITIES_SHEEP_PINK;
         case GRAY:
            return LootTables.ENTITIES_SHEEP_GRAY;
         case LIGHT_GRAY:
            return LootTables.ENTITIES_SHEEP_LIGHT_GRAY;
         case CYAN:
            return LootTables.ENTITIES_SHEEP_CYAN;
         case PURPLE:
            return LootTables.ENTITIES_SHEEP_PURPLE;
         case BLUE:
            return LootTables.ENTITIES_SHEEP_BLUE;
         case BROWN:
            return LootTables.ENTITIES_SHEEP_BROWN;
         case GREEN:
            return LootTables.ENTITIES_SHEEP_GREEN;
         case RED:
            return LootTables.ENTITIES_SHEEP_RED;
         case BLACK:
            return LootTables.ENTITIES_SHEEP_BLACK;
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void handleStatusUpdate(byte p_70103_1_) {
      if (p_70103_1_ == 10) {
         this.sheepTimer = 40;
      } else {
         super.handleStatusUpdate(p_70103_1_);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public float getHeadRotationPointY(float p_70894_1_) {
      if (this.sheepTimer <= 0) {
         return 0.0F;
      } else if (this.sheepTimer >= 4 && this.sheepTimer <= 36) {
         return 1.0F;
      } else {
         return this.sheepTimer < 4 ? ((float)this.sheepTimer - p_70894_1_) / 4.0F : -((float)(this.sheepTimer - 40) - p_70894_1_) / 4.0F;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public float getHeadRotationAngleX(float p_70890_1_) {
      if (this.sheepTimer > 4 && this.sheepTimer <= 36) {
         float f = ((float)(this.sheepTimer - 4) - p_70890_1_) / 32.0F;
         return 0.62831855F + 0.21991149F * MathHelper.sin(f * 28.7F);
      } else {
         return this.sheepTimer > 0 ? 0.62831855F : this.rotationPitch * 0.017453292F;
      }
   }

   public boolean processInteract(PlayerEntity p_184645_1_, Hand p_184645_2_) {
      p_184645_1_.getHeldItem(p_184645_2_);
      return super.processInteract(p_184645_1_, p_184645_2_);
   }

   /** @deprecated */
   @Deprecated
   public void func_213612_dV() {
      if (!this.world.isRemote) {
         this.setSheared(true);
         int i = 1 + this.rand.nextInt(3);

         for(int j = 0; j < i; ++j) {
            ItemEntity itementity = this.entityDropItem((IItemProvider)WOOL_BY_COLOR.get(this.getFleeceColor()), 1);
            if (itementity != null) {
               itementity.setMotion(itementity.getMotion().add((double)((this.rand.nextFloat() - this.rand.nextFloat()) * 0.1F), (double)(this.rand.nextFloat() * 0.05F), (double)((this.rand.nextFloat() - this.rand.nextFloat()) * 0.1F)));
            }
         }
      }

      this.playSound(SoundEvents.ENTITY_SHEEP_SHEAR, 1.0F, 1.0F);
   }

   public void writeAdditional(CompoundNBT p_213281_1_) {
      super.writeAdditional(p_213281_1_);
      p_213281_1_.putBoolean("Sheared", this.getSheared());
      p_213281_1_.putByte("Color", (byte)this.getFleeceColor().getId());
   }

   public void readAdditional(CompoundNBT p_70037_1_) {
      super.readAdditional(p_70037_1_);
      this.setSheared(p_70037_1_.getBoolean("Sheared"));
      this.setFleeceColor(DyeColor.byId(p_70037_1_.getByte("Color")));
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_SHEEP_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.ENTITY_SHEEP_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_SHEEP_DEATH;
   }

   protected void playStepSound(BlockPos p_180429_1_, BlockState p_180429_2_) {
      this.playSound(SoundEvents.ENTITY_SHEEP_STEP, 0.15F, 1.0F);
   }

   public DyeColor getFleeceColor() {
      return DyeColor.byId((Byte)this.dataManager.get(DYE_COLOR) & 15);
   }

   public void setFleeceColor(DyeColor p_175512_1_) {
      byte b0 = (Byte)this.dataManager.get(DYE_COLOR);
      this.dataManager.set(DYE_COLOR, (byte)(b0 & 240 | p_175512_1_.getId() & 15));
   }

   public boolean getSheared() {
      return ((Byte)this.dataManager.get(DYE_COLOR) & 16) != 0;
   }

   public void setSheared(boolean p_70893_1_) {
      byte b0 = (Byte)this.dataManager.get(DYE_COLOR);
      if (p_70893_1_) {
         this.dataManager.set(DYE_COLOR, (byte)(b0 | 16));
      } else {
         this.dataManager.set(DYE_COLOR, (byte)(b0 & -17));
      }

   }

   public static DyeColor getRandomSheepColor(Random p_175510_0_) {
      int i = p_175510_0_.nextInt(100);
      if (i < 5) {
         return DyeColor.BLACK;
      } else if (i < 10) {
         return DyeColor.GRAY;
      } else if (i < 15) {
         return DyeColor.LIGHT_GRAY;
      } else if (i < 18) {
         return DyeColor.BROWN;
      } else {
         return p_175510_0_.nextInt(500) == 0 ? DyeColor.PINK : DyeColor.WHITE;
      }
   }

   public SheepEntity createChild(AgeableEntity p_90011_1_) {
      SheepEntity sheepentity = (SheepEntity)p_90011_1_;
      SheepEntity sheepentity1 = (SheepEntity)EntityType.SHEEP.create(this.world);
      sheepentity1.setFleeceColor(this.getDyeColorMixFromParents(this, sheepentity));
      return sheepentity1;
   }

   public void eatGrassBonus() {
      this.setSheared(false);
      if (this.isChild()) {
         this.addGrowth(60);
      }

   }

   @Nullable
   public ILivingEntityData onInitialSpawn(IWorld p_213386_1_, DifficultyInstance p_213386_2_, SpawnReason p_213386_3_, @Nullable ILivingEntityData p_213386_4_, @Nullable CompoundNBT p_213386_5_) {
      this.setFleeceColor(getRandomSheepColor(p_213386_1_.getRandom()));
      return super.onInitialSpawn(p_213386_1_, p_213386_2_, p_213386_3_, p_213386_4_, p_213386_5_);
   }

   private DyeColor getDyeColorMixFromParents(AnimalEntity p_175511_1_, AnimalEntity p_175511_2_) {
      DyeColor dyecolor = ((SheepEntity)p_175511_1_).getFleeceColor();
      DyeColor dyecolor1 = ((SheepEntity)p_175511_2_).getFleeceColor();
      CraftingInventory craftinginventory = func_213611_a(dyecolor, dyecolor1);
      Optional var10000 = this.world.getRecipeManager().getRecipe(IRecipeType.CRAFTING, craftinginventory, this.world).map((p_lambda$getDyeColorMixFromParents$3_1_) -> {
         return p_lambda$getDyeColorMixFromParents$3_1_.getCraftingResult(craftinginventory);
      }).map(ItemStack::getItem);
      DyeItem.class.getClass();
      var10000 = var10000.filter(DyeItem.class::isInstance);
      DyeItem.class.getClass();
      return (DyeColor)var10000.map(DyeItem.class::cast).map(DyeItem::getDyeColor).orElseGet(() -> {
         return this.world.rand.nextBoolean() ? dyecolor : dyecolor1;
      });
   }

   private static CraftingInventory func_213611_a(DyeColor p_213611_0_, DyeColor p_213611_1_) {
      CraftingInventory craftinginventory = new CraftingInventory(new Container((ContainerType)null, -1) {
         public boolean canInteractWith(PlayerEntity p_75145_1_) {
            return false;
         }
      }, 2, 1);
      craftinginventory.setInventorySlotContents(0, new ItemStack(DyeItem.getItem(p_213611_0_)));
      craftinginventory.setInventorySlotContents(1, new ItemStack(DyeItem.getItem(p_213611_1_)));
      return craftinginventory;
   }

   protected float getStandingEyeHeight(Pose p_213348_1_, EntitySize p_213348_2_) {
      return 0.95F * p_213348_2_.height;
   }

   public boolean isShearable(ItemStack p_isShearable_1_, IWorldReader p_isShearable_2_, BlockPos p_isShearable_3_) {
      return !this.getSheared() && !this.isChild();
   }

   public List<ItemStack> onSheared(ItemStack p_onSheared_1_, IWorld p_onSheared_2_, BlockPos p_onSheared_3_, int p_onSheared_4_) {
      List<ItemStack> ret = new ArrayList();
      if (!this.world.isRemote) {
         this.setSheared(true);
         int i = 1 + this.rand.nextInt(3);

         for(int j = 0; j < i; ++j) {
            ret.add(new ItemStack((IItemProvider)WOOL_BY_COLOR.get(this.getFleeceColor())));
         }
      }

      this.playSound(SoundEvents.ENTITY_SHEEP_SHEAR, 1.0F, 1.0F);
      return ret;
   }

   // $FF: synthetic method
   private static void lambda$processInteract$2(Hand p_lambda$processInteract$2_0_, PlayerEntity p_lambda$processInteract$2_1_) {
      p_lambda$processInteract$2_1_.sendBreakAnimation(p_lambda$processInteract$2_0_);
   }

   static {
      DYE_COLOR = EntityDataManager.createKey(SheepEntity.class, DataSerializers.BYTE);
      WOOL_BY_COLOR = (Map)Util.make(Maps.newEnumMap(DyeColor.class), (p_lambda$static$0_0_) -> {
         p_lambda$static$0_0_.put(DyeColor.WHITE, Blocks.WHITE_WOOL);
         p_lambda$static$0_0_.put(DyeColor.ORANGE, Blocks.ORANGE_WOOL);
         p_lambda$static$0_0_.put(DyeColor.MAGENTA, Blocks.MAGENTA_WOOL);
         p_lambda$static$0_0_.put(DyeColor.LIGHT_BLUE, Blocks.LIGHT_BLUE_WOOL);
         p_lambda$static$0_0_.put(DyeColor.YELLOW, Blocks.YELLOW_WOOL);
         p_lambda$static$0_0_.put(DyeColor.LIME, Blocks.LIME_WOOL);
         p_lambda$static$0_0_.put(DyeColor.PINK, Blocks.PINK_WOOL);
         p_lambda$static$0_0_.put(DyeColor.GRAY, Blocks.GRAY_WOOL);
         p_lambda$static$0_0_.put(DyeColor.LIGHT_GRAY, Blocks.LIGHT_GRAY_WOOL);
         p_lambda$static$0_0_.put(DyeColor.CYAN, Blocks.CYAN_WOOL);
         p_lambda$static$0_0_.put(DyeColor.PURPLE, Blocks.PURPLE_WOOL);
         p_lambda$static$0_0_.put(DyeColor.BLUE, Blocks.BLUE_WOOL);
         p_lambda$static$0_0_.put(DyeColor.BROWN, Blocks.BROWN_WOOL);
         p_lambda$static$0_0_.put(DyeColor.GREEN, Blocks.GREEN_WOOL);
         p_lambda$static$0_0_.put(DyeColor.RED, Blocks.RED_WOOL);
         p_lambda$static$0_0_.put(DyeColor.BLACK, Blocks.BLACK_WOOL);
      });
      DYE_TO_RGB = Maps.newEnumMap((Map)Arrays.stream(DyeColor.values()).collect(Collectors.toMap((p_lambda$static$1_0_) -> {
         return p_lambda$static$1_0_;
      }, SheepEntity::createSheepColor)));
   }
}
