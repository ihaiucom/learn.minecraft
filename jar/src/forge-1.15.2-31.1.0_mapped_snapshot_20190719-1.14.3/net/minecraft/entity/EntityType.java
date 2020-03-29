package net.minecraft.entity;

import com.mojang.datafixers.DataFixUtils;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.entity.item.EnderCrystalEntity;
import net.minecraft.entity.item.EnderPearlEntity;
import net.minecraft.entity.item.ExperienceBottleEntity;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.item.EyeOfEnderEntity;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.entity.item.FireworkRocketEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.entity.item.LeashKnotEntity;
import net.minecraft.entity.item.PaintingEntity;
import net.minecraft.entity.item.TNTEntity;
import net.minecraft.entity.item.minecart.ChestMinecartEntity;
import net.minecraft.entity.item.minecart.FurnaceMinecartEntity;
import net.minecraft.entity.item.minecart.HopperMinecartEntity;
import net.minecraft.entity.item.minecart.MinecartCommandBlockEntity;
import net.minecraft.entity.item.minecart.MinecartEntity;
import net.minecraft.entity.item.minecart.SpawnerMinecartEntity;
import net.minecraft.entity.item.minecart.TNTMinecartEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.merchant.villager.WanderingTraderEntity;
import net.minecraft.entity.monster.BlazeEntity;
import net.minecraft.entity.monster.CaveSpiderEntity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.monster.DrownedEntity;
import net.minecraft.entity.monster.ElderGuardianEntity;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.entity.monster.EndermiteEntity;
import net.minecraft.entity.monster.EvokerEntity;
import net.minecraft.entity.monster.GhastEntity;
import net.minecraft.entity.monster.GiantEntity;
import net.minecraft.entity.monster.GuardianEntity;
import net.minecraft.entity.monster.HuskEntity;
import net.minecraft.entity.monster.IllusionerEntity;
import net.minecraft.entity.monster.MagmaCubeEntity;
import net.minecraft.entity.monster.PhantomEntity;
import net.minecraft.entity.monster.PillagerEntity;
import net.minecraft.entity.monster.RavagerEntity;
import net.minecraft.entity.monster.ShulkerEntity;
import net.minecraft.entity.monster.SilverfishEntity;
import net.minecraft.entity.monster.SkeletonEntity;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.entity.monster.SpiderEntity;
import net.minecraft.entity.monster.StrayEntity;
import net.minecraft.entity.monster.VexEntity;
import net.minecraft.entity.monster.VindicatorEntity;
import net.minecraft.entity.monster.WitchEntity;
import net.minecraft.entity.monster.WitherSkeletonEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.monster.ZombiePigmanEntity;
import net.minecraft.entity.monster.ZombieVillagerEntity;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.DolphinEntity;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.MooshroomEntity;
import net.minecraft.entity.passive.OcelotEntity;
import net.minecraft.entity.passive.PandaEntity;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.passive.PolarBearEntity;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.entity.passive.SquidEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.passive.fish.CodEntity;
import net.minecraft.entity.passive.fish.PufferfishEntity;
import net.minecraft.entity.passive.fish.SalmonEntity;
import net.minecraft.entity.passive.fish.TropicalFishEntity;
import net.minecraft.entity.passive.horse.DonkeyEntity;
import net.minecraft.entity.passive.horse.HorseEntity;
import net.minecraft.entity.passive.horse.LlamaEntity;
import net.minecraft.entity.passive.horse.MuleEntity;
import net.minecraft.entity.passive.horse.SkeletonHorseEntity;
import net.minecraft.entity.passive.horse.TraderLlamaEntity;
import net.minecraft.entity.passive.horse.ZombieHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.DragonFireballEntity;
import net.minecraft.entity.projectile.EggEntity;
import net.minecraft.entity.projectile.EvokerFangsEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.entity.projectile.LlamaSpitEntity;
import net.minecraft.entity.projectile.PotionEntity;
import net.minecraft.entity.projectile.ShulkerBulletEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.entity.projectile.SnowballEntity;
import net.minecraft.entity.projectile.SpectralArrowEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.Util;
import net.minecraft.util.datafix.DataFixesManager;
import net.minecraft.util.datafix.TypeReferences;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.spawner.AbstractSpawner;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.ReverseTagWrapper;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.network.FMLPlayMessages;
import net.minecraftforge.registries.ForgeRegistryEntry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityType<T extends Entity> extends ForgeRegistryEntry<EntityType<?>> {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final EntityType<AreaEffectCloudEntity> AREA_EFFECT_CLOUD;
   public static final EntityType<ArmorStandEntity> ARMOR_STAND;
   public static final EntityType<ArrowEntity> ARROW;
   public static final EntityType<BatEntity> BAT;
   public static final EntityType<BeeEntity> field_226289_e_;
   public static final EntityType<BlazeEntity> BLAZE;
   public static final EntityType<BoatEntity> BOAT;
   public static final EntityType<CatEntity> CAT;
   public static final EntityType<CaveSpiderEntity> CAVE_SPIDER;
   public static final EntityType<ChickenEntity> CHICKEN;
   public static final EntityType<CodEntity> COD;
   public static final EntityType<CowEntity> COW;
   public static final EntityType<CreeperEntity> CREEPER;
   public static final EntityType<DonkeyEntity> DONKEY;
   public static final EntityType<DolphinEntity> DOLPHIN;
   public static final EntityType<DragonFireballEntity> DRAGON_FIREBALL;
   public static final EntityType<DrownedEntity> DROWNED;
   public static final EntityType<ElderGuardianEntity> ELDER_GUARDIAN;
   public static final EntityType<EnderCrystalEntity> END_CRYSTAL;
   public static final EntityType<EnderDragonEntity> ENDER_DRAGON;
   public static final EntityType<EndermanEntity> ENDERMAN;
   public static final EntityType<EndermiteEntity> ENDERMITE;
   public static final EntityType<EvokerFangsEntity> EVOKER_FANGS;
   public static final EntityType<EvokerEntity> EVOKER;
   public static final EntityType<ExperienceOrbEntity> EXPERIENCE_ORB;
   public static final EntityType<EyeOfEnderEntity> EYE_OF_ENDER;
   public static final EntityType<FallingBlockEntity> FALLING_BLOCK;
   public static final EntityType<FireworkRocketEntity> FIREWORK_ROCKET;
   public static final EntityType<FoxEntity> FOX;
   public static final EntityType<GhastEntity> GHAST;
   public static final EntityType<GiantEntity> GIANT;
   public static final EntityType<GuardianEntity> GUARDIAN;
   public static final EntityType<HorseEntity> HORSE;
   public static final EntityType<HuskEntity> HUSK;
   public static final EntityType<IllusionerEntity> ILLUSIONER;
   public static final EntityType<ItemEntity> ITEM;
   public static final EntityType<ItemFrameEntity> ITEM_FRAME;
   public static final EntityType<FireballEntity> FIREBALL;
   public static final EntityType<LeashKnotEntity> LEASH_KNOT;
   public static final EntityType<LlamaEntity> LLAMA;
   public static final EntityType<LlamaSpitEntity> LLAMA_SPIT;
   public static final EntityType<MagmaCubeEntity> MAGMA_CUBE;
   public static final EntityType<MinecartEntity> MINECART;
   public static final EntityType<ChestMinecartEntity> CHEST_MINECART;
   public static final EntityType<MinecartCommandBlockEntity> COMMAND_BLOCK_MINECART;
   public static final EntityType<FurnaceMinecartEntity> FURNACE_MINECART;
   public static final EntityType<HopperMinecartEntity> HOPPER_MINECART;
   public static final EntityType<SpawnerMinecartEntity> SPAWNER_MINECART;
   public static final EntityType<TNTMinecartEntity> TNT_MINECART;
   public static final EntityType<MuleEntity> MULE;
   public static final EntityType<MooshroomEntity> MOOSHROOM;
   public static final EntityType<OcelotEntity> OCELOT;
   public static final EntityType<PaintingEntity> PAINTING;
   public static final EntityType<PandaEntity> PANDA;
   public static final EntityType<ParrotEntity> PARROT;
   public static final EntityType<PigEntity> PIG;
   public static final EntityType<PufferfishEntity> PUFFERFISH;
   public static final EntityType<ZombiePigmanEntity> ZOMBIE_PIGMAN;
   public static final EntityType<PolarBearEntity> POLAR_BEAR;
   public static final EntityType<TNTEntity> TNT;
   public static final EntityType<RabbitEntity> RABBIT;
   public static final EntityType<SalmonEntity> SALMON;
   public static final EntityType<SheepEntity> SHEEP;
   public static final EntityType<ShulkerEntity> SHULKER;
   public static final EntityType<ShulkerBulletEntity> SHULKER_BULLET;
   public static final EntityType<SilverfishEntity> SILVERFISH;
   public static final EntityType<SkeletonEntity> SKELETON;
   public static final EntityType<SkeletonHorseEntity> SKELETON_HORSE;
   public static final EntityType<SlimeEntity> SLIME;
   public static final EntityType<SmallFireballEntity> SMALL_FIREBALL;
   public static final EntityType<SnowGolemEntity> SNOW_GOLEM;
   public static final EntityType<SnowballEntity> SNOWBALL;
   public static final EntityType<SpectralArrowEntity> SPECTRAL_ARROW;
   public static final EntityType<SpiderEntity> SPIDER;
   public static final EntityType<SquidEntity> SQUID;
   public static final EntityType<StrayEntity> STRAY;
   public static final EntityType<TraderLlamaEntity> TRADER_LLAMA;
   public static final EntityType<TropicalFishEntity> TROPICAL_FISH;
   public static final EntityType<TurtleEntity> TURTLE;
   public static final EntityType<EggEntity> EGG;
   public static final EntityType<EnderPearlEntity> ENDER_PEARL;
   public static final EntityType<ExperienceBottleEntity> EXPERIENCE_BOTTLE;
   public static final EntityType<PotionEntity> POTION;
   public static final EntityType<TridentEntity> TRIDENT;
   public static final EntityType<VexEntity> VEX;
   public static final EntityType<VillagerEntity> VILLAGER;
   public static final EntityType<IronGolemEntity> IRON_GOLEM;
   public static final EntityType<VindicatorEntity> VINDICATOR;
   public static final EntityType<PillagerEntity> PILLAGER;
   public static final EntityType<WanderingTraderEntity> WANDERING_TRADER;
   public static final EntityType<WitchEntity> WITCH;
   public static final EntityType<WitherEntity> WITHER;
   public static final EntityType<WitherSkeletonEntity> WITHER_SKELETON;
   public static final EntityType<WitherSkullEntity> WITHER_SKULL;
   public static final EntityType<WolfEntity> WOLF;
   public static final EntityType<ZombieEntity> ZOMBIE;
   public static final EntityType<ZombieHorseEntity> ZOMBIE_HORSE;
   public static final EntityType<ZombieVillagerEntity> ZOMBIE_VILLAGER;
   public static final EntityType<PhantomEntity> PHANTOM;
   public static final EntityType<RavagerEntity> RAVAGER;
   public static final EntityType<LightningBoltEntity> LIGHTNING_BOLT;
   public static final EntityType<PlayerEntity> PLAYER;
   public static final EntityType<FishingBobberEntity> FISHING_BOBBER;
   private final EntityType.IFactory<T> factory;
   private final EntityClassification classification;
   private final boolean serializable;
   private final boolean summonable;
   private final boolean immuneToFire;
   private final boolean field_225438_be;
   @Nullable
   private String translationKey;
   @Nullable
   private ITextComponent name;
   @Nullable
   private ResourceLocation lootTable;
   private final EntitySize size;
   private final Predicate<EntityType<?>> velocityUpdateSupplier;
   private final ToIntFunction<EntityType<?>> trackingRangeSupplier;
   private final ToIntFunction<EntityType<?>> updateIntervalSupplier;
   private final BiFunction<FMLPlayMessages.SpawnEntity, World, T> customClientFactory;
   private final ReverseTagWrapper<EntityType<?>> reverseTags;

   private static <T extends Entity> EntityType<T> register(String p_200712_0_, EntityType.Builder<T> p_200712_1_) {
      return (EntityType)Registry.register((Registry)Registry.ENTITY_TYPE, (String)p_200712_0_, (Object)p_200712_1_.build(p_200712_0_));
   }

   public static ResourceLocation getKey(EntityType<?> p_200718_0_) {
      return Registry.ENTITY_TYPE.getKey(p_200718_0_);
   }

   public static Optional<EntityType<?>> byKey(String p_220327_0_) {
      return Registry.ENTITY_TYPE.getValue(ResourceLocation.tryCreate(p_220327_0_));
   }

   public EntityType(EntityType.IFactory<T> p_i51559_1_, EntityClassification p_i51559_2_, boolean p_i51559_3_, boolean p_i51559_4_, boolean p_i51559_5_, boolean p_i51559_6_, EntitySize p_i51559_7_) {
      this(p_i51559_1_, p_i51559_2_, p_i51559_3_, p_i51559_4_, p_i51559_5_, p_i51559_6_, p_i51559_7_, EntityType::defaultVelocitySupplier, EntityType::defaultTrackingRangeSupplier, EntityType::defaultUpdateIntervalSupplier, (BiFunction)null);
   }

   public EntityType(EntityType.IFactory<T> p_i230076_1_, EntityClassification p_i230076_2_, boolean p_i230076_3_, boolean p_i230076_4_, boolean p_i230076_5_, boolean p_i230076_6_, EntitySize p_i230076_7_, Predicate<EntityType<?>> p_i230076_8_, ToIntFunction<EntityType<?>> p_i230076_9_, ToIntFunction<EntityType<?>> p_i230076_10_, BiFunction<FMLPlayMessages.SpawnEntity, World, T> p_i230076_11_) {
      this.reverseTags = new ReverseTagWrapper(this, EntityTypeTags::getGeneration, EntityTypeTags::getCollection);
      this.factory = p_i230076_1_;
      this.classification = p_i230076_2_;
      this.field_225438_be = p_i230076_6_;
      this.serializable = p_i230076_3_;
      this.summonable = p_i230076_4_;
      this.immuneToFire = p_i230076_5_;
      this.size = p_i230076_7_;
      this.velocityUpdateSupplier = p_i230076_8_;
      this.trackingRangeSupplier = p_i230076_9_;
      this.updateIntervalSupplier = p_i230076_10_;
      this.customClientFactory = p_i230076_11_;
   }

   @Nullable
   public Entity spawn(World p_220331_1_, @Nullable ItemStack p_220331_2_, @Nullable PlayerEntity p_220331_3_, BlockPos p_220331_4_, SpawnReason p_220331_5_, boolean p_220331_6_, boolean p_220331_7_) {
      return this.spawn(p_220331_1_, p_220331_2_ == null ? null : p_220331_2_.getTag(), p_220331_2_ != null && p_220331_2_.hasDisplayName() ? p_220331_2_.getDisplayName() : null, p_220331_3_, p_220331_4_, p_220331_5_, p_220331_6_, p_220331_7_);
   }

   @Nullable
   public T spawn(World p_220342_1_, @Nullable CompoundNBT p_220342_2_, @Nullable ITextComponent p_220342_3_, @Nullable PlayerEntity p_220342_4_, BlockPos p_220342_5_, SpawnReason p_220342_6_, boolean p_220342_7_, boolean p_220342_8_) {
      T t = this.create(p_220342_1_, p_220342_2_, p_220342_3_, p_220342_4_, p_220342_5_, p_220342_6_, p_220342_7_, p_220342_8_);
      if (t instanceof MobEntity && ForgeEventFactory.doSpecialSpawn((MobEntity)t, p_220342_1_, (float)p_220342_5_.getX(), (float)p_220342_5_.getY(), (float)p_220342_5_.getZ(), (AbstractSpawner)null, p_220342_6_)) {
         return null;
      } else {
         p_220342_1_.addEntity(t);
         return t;
      }
   }

   @Nullable
   public T create(World p_220349_1_, @Nullable CompoundNBT p_220349_2_, @Nullable ITextComponent p_220349_3_, @Nullable PlayerEntity p_220349_4_, BlockPos p_220349_5_, SpawnReason p_220349_6_, boolean p_220349_7_, boolean p_220349_8_) {
      T t = this.create(p_220349_1_);
      if (t == null) {
         return (Entity)null;
      } else {
         double d0;
         if (p_220349_7_) {
            t.setPosition((double)p_220349_5_.getX() + 0.5D, (double)(p_220349_5_.getY() + 1), (double)p_220349_5_.getZ() + 0.5D);
            d0 = func_208051_a(p_220349_1_, p_220349_5_, p_220349_8_, t.getBoundingBox());
         } else {
            d0 = 0.0D;
         }

         t.setLocationAndAngles((double)p_220349_5_.getX() + 0.5D, (double)p_220349_5_.getY() + d0, (double)p_220349_5_.getZ() + 0.5D, MathHelper.wrapDegrees(p_220349_1_.rand.nextFloat() * 360.0F), 0.0F);
         if (t instanceof MobEntity) {
            MobEntity mobentity = (MobEntity)t;
            mobentity.rotationYawHead = mobentity.rotationYaw;
            mobentity.renderYawOffset = mobentity.rotationYaw;
            mobentity.onInitialSpawn(p_220349_1_, p_220349_1_.getDifficultyForLocation(new BlockPos(mobentity)), p_220349_6_, (ILivingEntityData)null, p_220349_2_);
            mobentity.playAmbientSound();
         }

         if (p_220349_3_ != null && t instanceof LivingEntity) {
            t.setCustomName(p_220349_3_);
         }

         applyItemNBT(p_220349_1_, p_220349_4_, t, p_220349_2_);
         return t;
      }
   }

   protected static double func_208051_a(IWorldReader p_208051_0_, BlockPos p_208051_1_, boolean p_208051_2_, AxisAlignedBB p_208051_3_) {
      AxisAlignedBB axisalignedbb = new AxisAlignedBB(p_208051_1_);
      if (p_208051_2_) {
         axisalignedbb = axisalignedbb.expand(0.0D, -1.0D, 0.0D);
      }

      Stream<VoxelShape> stream = p_208051_0_.func_226667_c_((Entity)null, axisalignedbb, Collections.emptySet());
      return 1.0D + VoxelShapes.getAllowedOffset(Direction.Axis.Y, p_208051_3_, stream, p_208051_2_ ? -2.0D : -1.0D);
   }

   public static void applyItemNBT(World p_208048_0_, @Nullable PlayerEntity p_208048_1_, @Nullable Entity p_208048_2_, @Nullable CompoundNBT p_208048_3_) {
      if (p_208048_3_ != null && p_208048_3_.contains("EntityTag", 10)) {
         MinecraftServer minecraftserver = p_208048_0_.getServer();
         if (minecraftserver != null && p_208048_2_ != null && (p_208048_0_.isRemote || !p_208048_2_.ignoreItemEntityData() || p_208048_1_ != null && minecraftserver.getPlayerList().canSendCommands(p_208048_1_.getGameProfile()))) {
            CompoundNBT compoundnbt = p_208048_2_.writeWithoutTypeId(new CompoundNBT());
            UUID uuid = p_208048_2_.getUniqueID();
            compoundnbt.merge(p_208048_3_.getCompound("EntityTag"));
            p_208048_2_.setUniqueId(uuid);
            p_208048_2_.read(compoundnbt);
         }
      }

   }

   public boolean isSerializable() {
      return this.serializable;
   }

   public boolean isSummonable() {
      return this.summonable;
   }

   public boolean isImmuneToFire() {
      return this.immuneToFire;
   }

   public boolean func_225437_d() {
      return this.field_225438_be;
   }

   public EntityClassification getClassification() {
      return this.classification;
   }

   public String getTranslationKey() {
      if (this.translationKey == null) {
         this.translationKey = Util.makeTranslationKey("entity", Registry.ENTITY_TYPE.getKey(this));
      }

      return this.translationKey;
   }

   public ITextComponent getName() {
      if (this.name == null) {
         this.name = new TranslationTextComponent(this.getTranslationKey(), new Object[0]);
      }

      return this.name;
   }

   public ResourceLocation getLootTable() {
      if (this.lootTable == null) {
         ResourceLocation resourcelocation = Registry.ENTITY_TYPE.getKey(this);
         this.lootTable = new ResourceLocation(resourcelocation.getNamespace(), "entities/" + resourcelocation.getPath());
      }

      return this.lootTable;
   }

   public float getWidth() {
      return this.size.width;
   }

   public float getHeight() {
      return this.size.height;
   }

   @Nullable
   public T create(World p_200721_1_) {
      return this.factory.create(this, p_200721_1_);
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public static Entity create(int p_200717_0_, World p_200717_1_) {
      return create(p_200717_1_, (EntityType)Registry.ENTITY_TYPE.getByValue(p_200717_0_));
   }

   public static Optional<Entity> loadEntityUnchecked(CompoundNBT p_220330_0_, World p_220330_1_) {
      return Util.acceptOrElse(readEntityType(p_220330_0_).map((p_lambda$loadEntityUnchecked$0_1_) -> {
         return p_lambda$loadEntityUnchecked$0_1_.create(p_220330_1_);
      }), (p_lambda$loadEntityUnchecked$1_1_) -> {
         p_lambda$loadEntityUnchecked$1_1_.read(p_220330_0_);
      }, () -> {
         LOGGER.warn("Skipping Entity with id {}", p_220330_0_.getString("id"));
      });
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   private static Entity create(World p_200719_0_, @Nullable EntityType<?> p_200719_1_) {
      return p_200719_1_ == null ? null : p_200719_1_.create(p_200719_0_);
   }

   public AxisAlignedBB func_220328_a(double p_220328_1_, double p_220328_3_, double p_220328_5_) {
      float f = this.getWidth() / 2.0F;
      return new AxisAlignedBB(p_220328_1_ - (double)f, p_220328_3_, p_220328_5_ - (double)f, p_220328_1_ + (double)f, p_220328_3_ + (double)this.getHeight(), p_220328_5_ + (double)f);
   }

   public EntitySize getSize() {
      return this.size;
   }

   public static Optional<EntityType<?>> readEntityType(CompoundNBT p_220347_0_) {
      return Registry.ENTITY_TYPE.getValue(new ResourceLocation(p_220347_0_.getString("id")));
   }

   @Nullable
   public static Entity func_220335_a(CompoundNBT p_220335_0_, World p_220335_1_, Function<Entity, Entity> p_220335_2_) {
      return (Entity)loadEntity(p_220335_0_, p_220335_1_).map(p_220335_2_).map((p_lambda$func_220335_a$3_3_) -> {
         if (p_220335_0_.contains("Passengers", 9)) {
            ListNBT listnbt = p_220335_0_.getList("Passengers", 10);

            for(int i = 0; i < listnbt.size(); ++i) {
               Entity entity = func_220335_a(listnbt.getCompound(i), p_220335_1_, p_220335_2_);
               if (entity != null) {
                  entity.startRiding(p_lambda$func_220335_a$3_3_, true);
               }
            }
         }

         return p_lambda$func_220335_a$3_3_;
      }).orElse((Entity)null);
   }

   private static Optional<Entity> loadEntity(CompoundNBT p_220343_0_, World p_220343_1_) {
      try {
         return loadEntityUnchecked(p_220343_0_, p_220343_1_);
      } catch (RuntimeException var3) {
         LOGGER.warn("Exception loading entity: ", var3);
         return Optional.empty();
      }
   }

   public int getTrackingRange() {
      return this.trackingRangeSupplier.applyAsInt(this);
   }

   private int defaultTrackingRangeSupplier() {
      if (this == PLAYER) {
         return 32;
      } else if (this == END_CRYSTAL) {
         return 16;
      } else if (this != ENDER_DRAGON && this != TNT && this != FALLING_BLOCK && this != ITEM_FRAME && this != LEASH_KNOT && this != PAINTING && this != ARMOR_STAND && this != EXPERIENCE_ORB && this != AREA_EFFECT_CLOUD && this != EVOKER_FANGS) {
         return this != FISHING_BOBBER && this != ARROW && this != SPECTRAL_ARROW && this != TRIDENT && this != SMALL_FIREBALL && this != DRAGON_FIREBALL && this != FIREBALL && this != WITHER_SKULL && this != SNOWBALL && this != LLAMA_SPIT && this != ENDER_PEARL && this != EYE_OF_ENDER && this != EGG && this != POTION && this != EXPERIENCE_BOTTLE && this != FIREWORK_ROCKET && this != ITEM ? 5 : 4;
      } else {
         return 10;
      }
   }

   public int getUpdateFrequency() {
      return this.updateIntervalSupplier.applyAsInt(this);
   }

   private int defaultUpdateIntervalSupplier() {
      if (this != PLAYER && this != EVOKER_FANGS) {
         if (this == EYE_OF_ENDER) {
            return 4;
         } else if (this == FISHING_BOBBER) {
            return 5;
         } else if (this != SMALL_FIREBALL && this != DRAGON_FIREBALL && this != FIREBALL && this != WITHER_SKULL && this != SNOWBALL && this != LLAMA_SPIT && this != ENDER_PEARL && this != EGG && this != POTION && this != EXPERIENCE_BOTTLE && this != FIREWORK_ROCKET && this != TNT) {
            if (this != ARROW && this != SPECTRAL_ARROW && this != TRIDENT && this != ITEM && this != FALLING_BLOCK && this != EXPERIENCE_ORB) {
               return this != ITEM_FRAME && this != LEASH_KNOT && this != PAINTING && this != AREA_EFFECT_CLOUD && this != END_CRYSTAL ? 3 : Integer.MAX_VALUE;
            } else {
               return 20;
            }
         } else {
            return 10;
         }
      } else {
         return 2;
      }
   }

   public boolean shouldSendVelocityUpdates() {
      return this.velocityUpdateSupplier.test(this);
   }

   private boolean defaultVelocitySupplier() {
      return this != PLAYER && this != LLAMA_SPIT && this != WITHER && this != BAT && this != ITEM_FRAME && this != LEASH_KNOT && this != PAINTING && this != END_CRYSTAL && this != EVOKER_FANGS;
   }

   public boolean isContained(Tag<EntityType<?>> p_220341_1_) {
      return p_220341_1_.contains(this);
   }

   public T customClientSpawn(FMLPlayMessages.SpawnEntity p_customClientSpawn_1_, World p_customClientSpawn_2_) {
      return this.customClientFactory == null ? this.create(p_customClientSpawn_2_) : (Entity)this.customClientFactory.apply(p_customClientSpawn_1_, p_customClientSpawn_2_);
   }

   public Set<ResourceLocation> getTags() {
      return this.reverseTags.getTagNames();
   }

   static {
      AREA_EFFECT_CLOUD = register("area_effect_cloud", EntityType.Builder.create(AreaEffectCloudEntity::new, EntityClassification.MISC).immuneToFire().size(6.0F, 0.5F));
      ARMOR_STAND = register("armor_stand", EntityType.Builder.create(ArmorStandEntity::new, EntityClassification.MISC).size(0.5F, 1.975F));
      ARROW = register("arrow", EntityType.Builder.create(ArrowEntity::new, EntityClassification.MISC).size(0.5F, 0.5F));
      BAT = register("bat", EntityType.Builder.create(BatEntity::new, EntityClassification.AMBIENT).size(0.5F, 0.9F));
      field_226289_e_ = register("bee", EntityType.Builder.create(BeeEntity::new, EntityClassification.CREATURE).size(0.7F, 0.6F));
      BLAZE = register("blaze", EntityType.Builder.create(BlazeEntity::new, EntityClassification.MONSTER).immuneToFire().size(0.6F, 1.8F));
      BOAT = register("boat", EntityType.Builder.create(BoatEntity::new, EntityClassification.MISC).size(1.375F, 0.5625F));
      CAT = register("cat", EntityType.Builder.create(CatEntity::new, EntityClassification.CREATURE).size(0.6F, 0.7F));
      CAVE_SPIDER = register("cave_spider", EntityType.Builder.create(CaveSpiderEntity::new, EntityClassification.MONSTER).size(0.7F, 0.5F));
      CHICKEN = register("chicken", EntityType.Builder.create(ChickenEntity::new, EntityClassification.CREATURE).size(0.4F, 0.7F));
      COD = register("cod", EntityType.Builder.create(CodEntity::new, EntityClassification.WATER_CREATURE).size(0.5F, 0.3F));
      COW = register("cow", EntityType.Builder.create(CowEntity::new, EntityClassification.CREATURE).size(0.9F, 1.4F));
      CREEPER = register("creeper", EntityType.Builder.create(CreeperEntity::new, EntityClassification.MONSTER).size(0.6F, 1.7F));
      DONKEY = register("donkey", EntityType.Builder.create(DonkeyEntity::new, EntityClassification.CREATURE).size(1.3964844F, 1.5F));
      DOLPHIN = register("dolphin", EntityType.Builder.create(DolphinEntity::new, EntityClassification.WATER_CREATURE).size(0.9F, 0.6F));
      DRAGON_FIREBALL = register("dragon_fireball", EntityType.Builder.create(DragonFireballEntity::new, EntityClassification.MISC).size(1.0F, 1.0F));
      DROWNED = register("drowned", EntityType.Builder.create(DrownedEntity::new, EntityClassification.MONSTER).size(0.6F, 1.95F));
      ELDER_GUARDIAN = register("elder_guardian", EntityType.Builder.create(ElderGuardianEntity::new, EntityClassification.MONSTER).size(1.9975F, 1.9975F));
      END_CRYSTAL = register("end_crystal", EntityType.Builder.create(EnderCrystalEntity::new, EntityClassification.MISC).size(2.0F, 2.0F));
      ENDER_DRAGON = register("ender_dragon", EntityType.Builder.create(EnderDragonEntity::new, EntityClassification.MONSTER).immuneToFire().size(16.0F, 8.0F));
      ENDERMAN = register("enderman", EntityType.Builder.create(EndermanEntity::new, EntityClassification.MONSTER).size(0.6F, 2.9F));
      ENDERMITE = register("endermite", EntityType.Builder.create(EndermiteEntity::new, EntityClassification.MONSTER).size(0.4F, 0.3F));
      EVOKER_FANGS = register("evoker_fangs", EntityType.Builder.create(EvokerFangsEntity::new, EntityClassification.MISC).size(0.5F, 0.8F));
      EVOKER = register("evoker", EntityType.Builder.create(EvokerEntity::new, EntityClassification.MONSTER).size(0.6F, 1.95F));
      EXPERIENCE_ORB = register("experience_orb", EntityType.Builder.create(ExperienceOrbEntity::new, EntityClassification.MISC).size(0.5F, 0.5F));
      EYE_OF_ENDER = register("eye_of_ender", EntityType.Builder.create(EyeOfEnderEntity::new, EntityClassification.MISC).size(0.25F, 0.25F));
      FALLING_BLOCK = register("falling_block", EntityType.Builder.create(FallingBlockEntity::new, EntityClassification.MISC).size(0.98F, 0.98F));
      FIREWORK_ROCKET = register("firework_rocket", EntityType.Builder.create(FireworkRocketEntity::new, EntityClassification.MISC).size(0.25F, 0.25F));
      FOX = register("fox", EntityType.Builder.create(FoxEntity::new, EntityClassification.CREATURE).size(0.6F, 0.7F));
      GHAST = register("ghast", EntityType.Builder.create(GhastEntity::new, EntityClassification.MONSTER).immuneToFire().size(4.0F, 4.0F));
      GIANT = register("giant", EntityType.Builder.create(GiantEntity::new, EntityClassification.MONSTER).size(3.6F, 12.0F));
      GUARDIAN = register("guardian", EntityType.Builder.create(GuardianEntity::new, EntityClassification.MONSTER).size(0.85F, 0.85F));
      HORSE = register("horse", EntityType.Builder.create(HorseEntity::new, EntityClassification.CREATURE).size(1.3964844F, 1.6F));
      HUSK = register("husk", EntityType.Builder.create(HuskEntity::new, EntityClassification.MONSTER).size(0.6F, 1.95F));
      ILLUSIONER = register("illusioner", EntityType.Builder.create(IllusionerEntity::new, EntityClassification.MONSTER).size(0.6F, 1.95F));
      ITEM = register("item", EntityType.Builder.create(ItemEntity::new, EntityClassification.MISC).size(0.25F, 0.25F));
      ITEM_FRAME = register("item_frame", EntityType.Builder.create(ItemFrameEntity::new, EntityClassification.MISC).size(0.5F, 0.5F));
      FIREBALL = register("fireball", EntityType.Builder.create(FireballEntity::new, EntityClassification.MISC).size(1.0F, 1.0F));
      LEASH_KNOT = register("leash_knot", EntityType.Builder.create(LeashKnotEntity::new, EntityClassification.MISC).disableSerialization().size(0.5F, 0.5F));
      LLAMA = register("llama", EntityType.Builder.create(LlamaEntity::new, EntityClassification.CREATURE).size(0.9F, 1.87F));
      LLAMA_SPIT = register("llama_spit", EntityType.Builder.create(LlamaSpitEntity::new, EntityClassification.MISC).size(0.25F, 0.25F));
      MAGMA_CUBE = register("magma_cube", EntityType.Builder.create(MagmaCubeEntity::new, EntityClassification.MONSTER).immuneToFire().size(2.04F, 2.04F));
      MINECART = register("minecart", EntityType.Builder.create(MinecartEntity::new, EntityClassification.MISC).size(0.98F, 0.7F));
      CHEST_MINECART = register("chest_minecart", EntityType.Builder.create(ChestMinecartEntity::new, EntityClassification.MISC).size(0.98F, 0.7F));
      COMMAND_BLOCK_MINECART = register("command_block_minecart", EntityType.Builder.create(MinecartCommandBlockEntity::new, EntityClassification.MISC).size(0.98F, 0.7F));
      FURNACE_MINECART = register("furnace_minecart", EntityType.Builder.create(FurnaceMinecartEntity::new, EntityClassification.MISC).size(0.98F, 0.7F));
      HOPPER_MINECART = register("hopper_minecart", EntityType.Builder.create(HopperMinecartEntity::new, EntityClassification.MISC).size(0.98F, 0.7F));
      SPAWNER_MINECART = register("spawner_minecart", EntityType.Builder.create(SpawnerMinecartEntity::new, EntityClassification.MISC).size(0.98F, 0.7F));
      TNT_MINECART = register("tnt_minecart", EntityType.Builder.create(TNTMinecartEntity::new, EntityClassification.MISC).size(0.98F, 0.7F));
      MULE = register("mule", EntityType.Builder.create(MuleEntity::new, EntityClassification.CREATURE).size(1.3964844F, 1.6F));
      MOOSHROOM = register("mooshroom", EntityType.Builder.create(MooshroomEntity::new, EntityClassification.CREATURE).size(0.9F, 1.4F));
      OCELOT = register("ocelot", EntityType.Builder.create(OcelotEntity::new, EntityClassification.CREATURE).size(0.6F, 0.7F));
      PAINTING = register("painting", EntityType.Builder.create(PaintingEntity::new, EntityClassification.MISC).size(0.5F, 0.5F));
      PANDA = register("panda", EntityType.Builder.create(PandaEntity::new, EntityClassification.CREATURE).size(1.3F, 1.25F));
      PARROT = register("parrot", EntityType.Builder.create(ParrotEntity::new, EntityClassification.CREATURE).size(0.5F, 0.9F));
      PIG = register("pig", EntityType.Builder.create(PigEntity::new, EntityClassification.CREATURE).size(0.9F, 0.9F));
      PUFFERFISH = register("pufferfish", EntityType.Builder.create(PufferfishEntity::new, EntityClassification.WATER_CREATURE).size(0.7F, 0.7F));
      ZOMBIE_PIGMAN = register("zombie_pigman", EntityType.Builder.create(ZombiePigmanEntity::new, EntityClassification.MONSTER).immuneToFire().size(0.6F, 1.95F));
      POLAR_BEAR = register("polar_bear", EntityType.Builder.create(PolarBearEntity::new, EntityClassification.CREATURE).size(1.4F, 1.4F));
      TNT = register("tnt", EntityType.Builder.create(TNTEntity::new, EntityClassification.MISC).immuneToFire().size(0.98F, 0.98F));
      RABBIT = register("rabbit", EntityType.Builder.create(RabbitEntity::new, EntityClassification.CREATURE).size(0.4F, 0.5F));
      SALMON = register("salmon", EntityType.Builder.create(SalmonEntity::new, EntityClassification.WATER_CREATURE).size(0.7F, 0.4F));
      SHEEP = register("sheep", EntityType.Builder.create(SheepEntity::new, EntityClassification.CREATURE).size(0.9F, 1.3F));
      SHULKER = register("shulker", EntityType.Builder.create(ShulkerEntity::new, EntityClassification.MONSTER).immuneToFire().func_225435_d().size(1.0F, 1.0F));
      SHULKER_BULLET = register("shulker_bullet", EntityType.Builder.create(ShulkerBulletEntity::new, EntityClassification.MISC).size(0.3125F, 0.3125F));
      SILVERFISH = register("silverfish", EntityType.Builder.create(SilverfishEntity::new, EntityClassification.MONSTER).size(0.4F, 0.3F));
      SKELETON = register("skeleton", EntityType.Builder.create(SkeletonEntity::new, EntityClassification.MONSTER).size(0.6F, 1.99F));
      SKELETON_HORSE = register("skeleton_horse", EntityType.Builder.create(SkeletonHorseEntity::new, EntityClassification.CREATURE).size(1.3964844F, 1.6F));
      SLIME = register("slime", EntityType.Builder.create(SlimeEntity::new, EntityClassification.MONSTER).size(2.04F, 2.04F));
      SMALL_FIREBALL = register("small_fireball", EntityType.Builder.create(SmallFireballEntity::new, EntityClassification.MISC).size(0.3125F, 0.3125F));
      SNOW_GOLEM = register("snow_golem", EntityType.Builder.create(SnowGolemEntity::new, EntityClassification.MISC).size(0.7F, 1.9F));
      SNOWBALL = register("snowball", EntityType.Builder.create(SnowballEntity::new, EntityClassification.MISC).size(0.25F, 0.25F));
      SPECTRAL_ARROW = register("spectral_arrow", EntityType.Builder.create(SpectralArrowEntity::new, EntityClassification.MISC).size(0.5F, 0.5F));
      SPIDER = register("spider", EntityType.Builder.create(SpiderEntity::new, EntityClassification.MONSTER).size(1.4F, 0.9F));
      SQUID = register("squid", EntityType.Builder.create(SquidEntity::new, EntityClassification.WATER_CREATURE).size(0.8F, 0.8F));
      STRAY = register("stray", EntityType.Builder.create(StrayEntity::new, EntityClassification.MONSTER).size(0.6F, 1.99F));
      TRADER_LLAMA = register("trader_llama", EntityType.Builder.create(TraderLlamaEntity::new, EntityClassification.CREATURE).size(0.9F, 1.87F));
      TROPICAL_FISH = register("tropical_fish", EntityType.Builder.create(TropicalFishEntity::new, EntityClassification.WATER_CREATURE).size(0.5F, 0.4F));
      TURTLE = register("turtle", EntityType.Builder.create(TurtleEntity::new, EntityClassification.CREATURE).size(1.2F, 0.4F));
      EGG = register("egg", EntityType.Builder.create(EggEntity::new, EntityClassification.MISC).size(0.25F, 0.25F));
      ENDER_PEARL = register("ender_pearl", EntityType.Builder.create(EnderPearlEntity::new, EntityClassification.MISC).size(0.25F, 0.25F));
      EXPERIENCE_BOTTLE = register("experience_bottle", EntityType.Builder.create(ExperienceBottleEntity::new, EntityClassification.MISC).size(0.25F, 0.25F));
      POTION = register("potion", EntityType.Builder.create(PotionEntity::new, EntityClassification.MISC).size(0.25F, 0.25F));
      TRIDENT = register("trident", EntityType.Builder.create(TridentEntity::new, EntityClassification.MISC).size(0.5F, 0.5F));
      VEX = register("vex", EntityType.Builder.create(VexEntity::new, EntityClassification.MONSTER).immuneToFire().size(0.4F, 0.8F));
      VILLAGER = register("villager", EntityType.Builder.create(VillagerEntity::new, EntityClassification.MISC).size(0.6F, 1.95F));
      IRON_GOLEM = register("iron_golem", EntityType.Builder.create(IronGolemEntity::new, EntityClassification.MISC).size(1.4F, 2.7F));
      VINDICATOR = register("vindicator", EntityType.Builder.create(VindicatorEntity::new, EntityClassification.MONSTER).size(0.6F, 1.95F));
      PILLAGER = register("pillager", EntityType.Builder.create(PillagerEntity::new, EntityClassification.MONSTER).func_225435_d().size(0.6F, 1.95F));
      WANDERING_TRADER = register("wandering_trader", EntityType.Builder.create(WanderingTraderEntity::new, EntityClassification.CREATURE).size(0.6F, 1.95F));
      WITCH = register("witch", EntityType.Builder.create(WitchEntity::new, EntityClassification.MONSTER).size(0.6F, 1.95F));
      WITHER = register("wither", EntityType.Builder.create(WitherEntity::new, EntityClassification.MONSTER).immuneToFire().size(0.9F, 3.5F));
      WITHER_SKELETON = register("wither_skeleton", EntityType.Builder.create(WitherSkeletonEntity::new, EntityClassification.MONSTER).immuneToFire().size(0.7F, 2.4F));
      WITHER_SKULL = register("wither_skull", EntityType.Builder.create(WitherSkullEntity::new, EntityClassification.MISC).size(0.3125F, 0.3125F));
      WOLF = register("wolf", EntityType.Builder.create(WolfEntity::new, EntityClassification.CREATURE).size(0.6F, 0.85F));
      ZOMBIE = register("zombie", EntityType.Builder.create(ZombieEntity::new, EntityClassification.MONSTER).size(0.6F, 1.95F));
      ZOMBIE_HORSE = register("zombie_horse", EntityType.Builder.create(ZombieHorseEntity::new, EntityClassification.CREATURE).size(1.3964844F, 1.6F));
      ZOMBIE_VILLAGER = register("zombie_villager", EntityType.Builder.create(ZombieVillagerEntity::new, EntityClassification.MONSTER).size(0.6F, 1.95F));
      PHANTOM = register("phantom", EntityType.Builder.create(PhantomEntity::new, EntityClassification.MONSTER).size(0.9F, 0.5F));
      RAVAGER = register("ravager", EntityType.Builder.create(RavagerEntity::new, EntityClassification.MONSTER).size(1.95F, 2.2F));
      LIGHTNING_BOLT = register("lightning_bolt", EntityType.Builder.create(EntityClassification.MISC).disableSerialization().size(0.0F, 0.0F));
      PLAYER = register("player", EntityType.Builder.create(EntityClassification.MISC).disableSerialization().disableSummoning().size(0.6F, 1.8F));
      FISHING_BOBBER = register("fishing_bobber", EntityType.Builder.create(EntityClassification.MISC).disableSerialization().disableSummoning().size(0.25F, 0.25F));
   }

   public interface IFactory<T extends Entity> {
      T create(EntityType<T> var1, World var2);
   }

   public static class Builder<T extends Entity> {
      private final EntityType.IFactory<T> factory;
      private final EntityClassification classification;
      private boolean serializable = true;
      private boolean summonable = true;
      private boolean immuneToFire;
      private Predicate<EntityType<?>> velocityUpdateSupplier = (p_lambda$new$0_0_) -> {
         return ((EntityType)p_lambda$new$0_0_).defaultVelocitySupplier();
      };
      private ToIntFunction<EntityType<?>> trackingRangeSupplier = (p_lambda$new$1_0_) -> {
         return ((EntityType)p_lambda$new$1_0_).defaultTrackingRangeSupplier();
      };
      private ToIntFunction<EntityType<?>> updateIntervalSupplier = (p_lambda$new$2_0_) -> {
         return ((EntityType)p_lambda$new$2_0_).defaultUpdateIntervalSupplier();
      };
      private BiFunction<FMLPlayMessages.SpawnEntity, World, T> customClientFactory;
      private boolean field_225436_f;
      private EntitySize size = EntitySize.flexible(0.6F, 1.8F);

      private Builder(EntityType.IFactory<T> p_i50479_1_, EntityClassification p_i50479_2_) {
         this.factory = p_i50479_1_;
         this.classification = p_i50479_2_;
         this.field_225436_f = p_i50479_2_ == EntityClassification.CREATURE || p_i50479_2_ == EntityClassification.MISC;
      }

      public static <T extends Entity> EntityType.Builder<T> create(EntityType.IFactory<T> p_220322_0_, EntityClassification p_220322_1_) {
         return new EntityType.Builder(p_220322_0_, p_220322_1_);
      }

      public static <T extends Entity> EntityType.Builder<T> create(EntityClassification p_220319_0_) {
         return new EntityType.Builder((p_lambda$create$3_0_, p_lambda$create$3_1_) -> {
            return (Entity)null;
         }, p_220319_0_);
      }

      public EntityType.Builder<T> size(float p_220321_1_, float p_220321_2_) {
         this.size = EntitySize.flexible(p_220321_1_, p_220321_2_);
         return this;
      }

      public EntityType.Builder<T> disableSummoning() {
         this.summonable = false;
         return this;
      }

      public EntityType.Builder<T> disableSerialization() {
         this.serializable = false;
         return this;
      }

      public EntityType.Builder<T> immuneToFire() {
         this.immuneToFire = true;
         return this;
      }

      public EntityType.Builder<T> func_225435_d() {
         this.field_225436_f = true;
         return this;
      }

      public EntityType.Builder<T> setUpdateInterval(int p_setUpdateInterval_1_) {
         this.updateIntervalSupplier = (p_lambda$setUpdateInterval$4_1_) -> {
            return p_setUpdateInterval_1_;
         };
         return this;
      }

      public EntityType.Builder<T> setTrackingRange(int p_setTrackingRange_1_) {
         this.trackingRangeSupplier = (p_lambda$setTrackingRange$5_1_) -> {
            return p_setTrackingRange_1_;
         };
         return this;
      }

      public EntityType.Builder<T> setShouldReceiveVelocityUpdates(boolean p_setShouldReceiveVelocityUpdates_1_) {
         this.velocityUpdateSupplier = (p_lambda$setShouldReceiveVelocityUpdates$6_1_) -> {
            return p_setShouldReceiveVelocityUpdates_1_;
         };
         return this;
      }

      public EntityType.Builder<T> setCustomClientFactory(BiFunction<FMLPlayMessages.SpawnEntity, World, T> p_setCustomClientFactory_1_) {
         this.customClientFactory = p_setCustomClientFactory_1_;
         return this;
      }

      public EntityType<T> build(String p_206830_1_) {
         if (this.serializable) {
            try {
               DataFixesManager.getDataFixer().getSchema(DataFixUtils.makeKey(SharedConstants.getVersion().getWorldVersion())).getChoiceType(TypeReferences.ENTITY_TYPE, p_206830_1_);
            } catch (IllegalArgumentException var3) {
               if (SharedConstants.developmentMode) {
                  throw var3;
               }

               EntityType.LOGGER.warn("No data fixer registered for entity {}", p_206830_1_);
            }
         }

         return new EntityType(this.factory, this.classification, this.serializable, this.summonable, this.immuneToFire, this.field_225436_f, this.size, this.velocityUpdateSupplier, this.trackingRangeSupplier, this.updateIntervalSupplier, this.customClientFactory);
      }
   }
}
