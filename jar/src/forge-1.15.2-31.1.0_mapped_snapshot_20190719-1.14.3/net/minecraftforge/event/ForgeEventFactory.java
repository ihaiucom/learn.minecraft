package net.minecraftforge.event;

import com.mojang.blaze3d.matrix.MatrixStack;
import java.io.File;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.NetherPortalBlock;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.DamagingProjectileEntity;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.Explosion;
import net.minecraft.world.GameRules;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.spawner.AbstractSpawner;
import net.minecraft.world.storage.IPlayerFileData;
import net.minecraft.world.storage.SaveHandler;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTableManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityDispatcher;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.brewing.PlayerBrewedPotionEvent;
import net.minecraftforge.event.brewing.PotionBrewEvent;
import net.minecraftforge.event.enchanting.EnchantmentLevelSetEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityMobGriefingEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
import net.minecraftforge.event.entity.PlaySoundAtEntityEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.item.ItemExpireEvent;
import net.minecraftforge.event.entity.living.AnimalTameEvent;
import net.minecraftforge.event.entity.living.LivingDestroyBlockEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingPackSizeEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.living.ZombieEvent;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.ArrowNockEvent;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerFlyableFallEvent;
import net.minecraftforge.event.entity.player.PlayerSetSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.event.entity.player.SleepingLocationCheckEvent;
import net.minecraftforge.event.entity.player.SleepingTimeCheckEvent;
import net.minecraftforge.event.entity.player.UseHoeEvent;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ChunkWatchEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.event.world.GetCollisionBoxesEvent;
import net.minecraftforge.event.world.PistonEvent;
import net.minecraftforge.event.world.SaplingGrowTreeEvent;
import net.minecraftforge.event.world.SleepFinishedTimeEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.Event.Result;

public class ForgeEventFactory {
   public static boolean onMultiBlockPlace(@Nullable Entity entity, List<BlockSnapshot> blockSnapshots, Direction direction) {
      BlockSnapshot snap = (BlockSnapshot)blockSnapshots.get(0);
      BlockState placedAgainst = snap.getWorld().getBlockState(snap.getPos().offset(direction.getOpposite()));
      BlockEvent.EntityMultiPlaceEvent event = new BlockEvent.EntityMultiPlaceEvent(blockSnapshots, placedAgainst, entity);
      return MinecraftForge.EVENT_BUS.post(event);
   }

   public static boolean onBlockPlace(@Nullable Entity entity, @Nonnull BlockSnapshot blockSnapshot, @Nonnull Direction direction) {
      BlockState placedAgainst = blockSnapshot.getWorld().getBlockState(blockSnapshot.getPos().offset(direction.getOpposite()));
      BlockEvent.EntityPlaceEvent event = new BlockEvent.EntityPlaceEvent(blockSnapshot, placedAgainst, entity);
      return MinecraftForge.EVENT_BUS.post(event);
   }

   public static BlockEvent.NeighborNotifyEvent onNeighborNotify(World world, BlockPos pos, BlockState state, EnumSet<Direction> notifiedSides, boolean forceRedstoneUpdate) {
      BlockEvent.NeighborNotifyEvent event = new BlockEvent.NeighborNotifyEvent(world, pos, state, notifiedSides, forceRedstoneUpdate);
      MinecraftForge.EVENT_BUS.post(event);
      return event;
   }

   public static boolean doPlayerHarvestCheck(PlayerEntity player, BlockState state, boolean success) {
      PlayerEvent.HarvestCheck event = new PlayerEvent.HarvestCheck(player, state, success);
      MinecraftForge.EVENT_BUS.post(event);
      return event.canHarvest();
   }

   public static float getBreakSpeed(PlayerEntity player, BlockState state, float original, BlockPos pos) {
      PlayerEvent.BreakSpeed event = new PlayerEvent.BreakSpeed(player, state, original, pos);
      return MinecraftForge.EVENT_BUS.post(event) ? -1.0F : event.getNewSpeed();
   }

   public static void onPlayerDestroyItem(PlayerEntity player, @Nonnull ItemStack stack, @Nullable Hand hand) {
      MinecraftForge.EVENT_BUS.post(new PlayerDestroyItemEvent(player, stack, hand));
   }

   public static Result canEntitySpawn(MobEntity entity, IWorld world, double x, double y, double z, AbstractSpawner spawner, SpawnReason spawnReason) {
      if (entity == null) {
         return Result.DEFAULT;
      } else {
         LivingSpawnEvent.CheckSpawn event = new LivingSpawnEvent.CheckSpawn(entity, world, x, y, z, spawner, spawnReason);
         MinecraftForge.EVENT_BUS.post(event);
         return event.getResult();
      }
   }

   public static boolean canEntitySpawnSpawner(MobEntity entity, World world, float x, float y, float z, AbstractSpawner spawner) {
      Result result = canEntitySpawn(entity, world, (double)x, (double)y, (double)z, spawner, SpawnReason.SPAWNER);
      if (result != Result.DEFAULT) {
         return result == Result.ALLOW;
      } else {
         return entity.canSpawn(world, SpawnReason.SPAWNER) && entity.isNotColliding(world);
      }
   }

   public static boolean doSpecialSpawn(MobEntity entity, World world, float x, float y, float z, AbstractSpawner spawner, SpawnReason spawnReason) {
      return MinecraftForge.EVENT_BUS.post(new LivingSpawnEvent.SpecialSpawn(entity, world, (double)x, (double)y, (double)z, spawner, spawnReason));
   }

   public static Result canEntityDespawn(MobEntity entity) {
      LivingSpawnEvent.AllowDespawn event = new LivingSpawnEvent.AllowDespawn(entity);
      MinecraftForge.EVENT_BUS.post(event);
      return event.getResult();
   }

   public static int getItemBurnTime(@Nonnull ItemStack itemStack, int burnTime) {
      FurnaceFuelBurnTimeEvent event = new FurnaceFuelBurnTimeEvent(itemStack, burnTime);
      MinecraftForge.EVENT_BUS.post(event);
      return event.getBurnTime();
   }

   public static int getExperienceDrop(LivingEntity entity, PlayerEntity attackingPlayer, int originalExperience) {
      LivingExperienceDropEvent event = new LivingExperienceDropEvent(entity, attackingPlayer, originalExperience);
      return MinecraftForge.EVENT_BUS.post(event) ? 0 : event.getDroppedExperience();
   }

   @Nullable
   public static List<Biome.SpawnListEntry> getPotentialSpawns(IWorld world, EntityClassification type, BlockPos pos, List<Biome.SpawnListEntry> oldList) {
      WorldEvent.PotentialSpawns event = new WorldEvent.PotentialSpawns(world, type, pos, oldList);
      return MinecraftForge.EVENT_BUS.post(event) ? Collections.emptyList() : event.getList();
   }

   public static int getMaxSpawnPackSize(MobEntity entity) {
      LivingPackSizeEvent maxCanSpawnEvent = new LivingPackSizeEvent(entity);
      MinecraftForge.EVENT_BUS.post(maxCanSpawnEvent);
      return maxCanSpawnEvent.getResult() == Result.ALLOW ? maxCanSpawnEvent.getMaxPackSize() : entity.getMaxSpawnedInChunk();
   }

   public static String getPlayerDisplayName(PlayerEntity player, String username) {
      PlayerEvent.NameFormat event = new PlayerEvent.NameFormat(player, username);
      MinecraftForge.EVENT_BUS.post(event);
      return event.getDisplayname();
   }

   public static float fireBlockHarvesting(NonNullList<ItemStack> drops, World world, BlockPos pos, BlockState state, int fortune, float dropChance, boolean silkTouch, PlayerEntity player) {
      BlockEvent.HarvestDropsEvent event = new BlockEvent.HarvestDropsEvent(world, pos, state, fortune, dropChance, drops, player, silkTouch);
      MinecraftForge.EVENT_BUS.post(event);
      return event.getDropChance();
   }

   public static BlockState fireFluidPlaceBlockEvent(IWorld world, BlockPos pos, BlockPos liquidPos, BlockState state) {
      BlockEvent.FluidPlaceBlockEvent event = new BlockEvent.FluidPlaceBlockEvent(world, pos, liquidPos, state);
      MinecraftForge.EVENT_BUS.post(event);
      return event.getNewState();
   }

   public static ItemTooltipEvent onItemTooltip(ItemStack itemStack, @Nullable PlayerEntity entityPlayer, List<ITextComponent> list, ITooltipFlag flags) {
      ItemTooltipEvent event = new ItemTooltipEvent(itemStack, entityPlayer, list, flags);
      MinecraftForge.EVENT_BUS.post(event);
      return event;
   }

   public static ZombieEvent.SummonAidEvent fireZombieSummonAid(ZombieEntity zombie, World world, int x, int y, int z, LivingEntity attacker, double summonChance) {
      ZombieEvent.SummonAidEvent summonEvent = new ZombieEvent.SummonAidEvent(zombie, world, x, y, z, attacker, summonChance);
      MinecraftForge.EVENT_BUS.post(summonEvent);
      return summonEvent;
   }

   public static boolean onEntityStruckByLightning(Entity entity, LightningBoltEntity bolt) {
      return MinecraftForge.EVENT_BUS.post(new EntityStruckByLightningEvent(entity, bolt));
   }

   public static int onItemUseStart(LivingEntity entity, ItemStack item, int duration) {
      LivingEntityUseItemEvent event = new LivingEntityUseItemEvent.Start(entity, item, duration);
      return MinecraftForge.EVENT_BUS.post(event) ? -1 : event.getDuration();
   }

   public static int onItemUseTick(LivingEntity entity, ItemStack item, int duration) {
      LivingEntityUseItemEvent event = new LivingEntityUseItemEvent.Tick(entity, item, duration);
      return MinecraftForge.EVENT_BUS.post(event) ? -1 : event.getDuration();
   }

   public static boolean onUseItemStop(LivingEntity entity, ItemStack item, int duration) {
      return MinecraftForge.EVENT_BUS.post(new LivingEntityUseItemEvent.Stop(entity, item, duration));
   }

   public static ItemStack onItemUseFinish(LivingEntity entity, ItemStack item, int duration, ItemStack result) {
      LivingEntityUseItemEvent.Finish event = new LivingEntityUseItemEvent.Finish(entity, item, duration, result);
      MinecraftForge.EVENT_BUS.post(event);
      return event.getResultStack();
   }

   public static void onStartEntityTracking(Entity entity, PlayerEntity player) {
      MinecraftForge.EVENT_BUS.post(new PlayerEvent.StartTracking(player, entity));
   }

   public static void onStopEntityTracking(Entity entity, PlayerEntity player) {
      MinecraftForge.EVENT_BUS.post(new PlayerEvent.StopTracking(player, entity));
   }

   public static void firePlayerLoadingEvent(PlayerEntity player, File playerDirectory, String uuidString) {
      MinecraftForge.EVENT_BUS.post(new PlayerEvent.LoadFromFile(player, playerDirectory, uuidString));
   }

   public static void firePlayerSavingEvent(PlayerEntity player, File playerDirectory, String uuidString) {
      MinecraftForge.EVENT_BUS.post(new PlayerEvent.SaveToFile(player, playerDirectory, uuidString));
   }

   public static void firePlayerLoadingEvent(PlayerEntity player, IPlayerFileData playerFileData, String uuidString) {
      MinecraftForge.EVENT_BUS.post(new PlayerEvent.LoadFromFile(player, ((SaveHandler)playerFileData).getPlayerFolder(), uuidString));
   }

   @Nullable
   public static ITextComponent onClientChat(ChatType type, ITextComponent message) {
      ClientChatReceivedEvent event = new ClientChatReceivedEvent(type, message);
      return MinecraftForge.EVENT_BUS.post(event) ? null : event.getMessage();
   }

   @Nonnull
   public static String onClientSendMessage(String message) {
      ClientChatEvent event = new ClientChatEvent(message);
      return MinecraftForge.EVENT_BUS.post(event) ? "" : event.getMessage();
   }

   public static int onHoeUse(ItemUseContext context) {
      UseHoeEvent event = new UseHoeEvent(context);
      if (MinecraftForge.EVENT_BUS.post(event)) {
         return -1;
      } else if (event.getResult() == Result.ALLOW) {
         context.getItem().damageItem(1, context.getPlayer(), (player) -> {
            player.sendBreakAnimation(context.getHand());
         });
         return 1;
      } else {
         return 0;
      }
   }

   public static int onApplyBonemeal(@Nonnull PlayerEntity player, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull ItemStack stack) {
      BonemealEvent event = new BonemealEvent(player, world, pos, state, stack);
      if (MinecraftForge.EVENT_BUS.post(event)) {
         return -1;
      } else if (event.getResult() == Result.ALLOW) {
         if (!world.isRemote) {
            stack.shrink(1);
         }

         return 1;
      } else {
         return 0;
      }
   }

   @Nullable
   public static ActionResult<ItemStack> onBucketUse(@Nonnull PlayerEntity player, @Nonnull World world, @Nonnull ItemStack stack, @Nullable RayTraceResult target) {
      FillBucketEvent event = new FillBucketEvent(player, stack, world, target);
      if (MinecraftForge.EVENT_BUS.post(event)) {
         return new ActionResult(ActionResultType.FAIL, stack);
      } else if (event.getResult() == Result.ALLOW) {
         if (player.abilities.isCreativeMode) {
            return new ActionResult(ActionResultType.SUCCESS, stack);
         } else {
            stack.shrink(1);
            if (stack.isEmpty()) {
               return new ActionResult(ActionResultType.SUCCESS, event.getFilledBucket());
            } else {
               if (!player.inventory.addItemStackToInventory(event.getFilledBucket())) {
                  player.dropItem(event.getFilledBucket(), false);
               }

               return new ActionResult(ActionResultType.SUCCESS, stack);
            }
         }
      } else {
         return null;
      }
   }

   public static boolean canEntityUpdate(Entity entity) {
      EntityEvent.CanUpdate event = new EntityEvent.CanUpdate(entity);
      MinecraftForge.EVENT_BUS.post(event);
      return event.getCanUpdate();
   }

   public static PlaySoundAtEntityEvent onPlaySoundAtEntity(Entity entity, SoundEvent name, SoundCategory category, float volume, float pitch) {
      PlaySoundAtEntityEvent event = new PlaySoundAtEntityEvent(entity, name, category, volume, pitch);
      MinecraftForge.EVENT_BUS.post(event);
      return event;
   }

   public static int onItemExpire(ItemEntity entity, @Nonnull ItemStack item) {
      if (item.isEmpty()) {
         return -1;
      } else {
         ItemExpireEvent event = new ItemExpireEvent(entity, item.isEmpty() ? 6000 : item.getItem().getEntityLifespan(item, entity.world));
         return !MinecraftForge.EVENT_BUS.post(event) ? -1 : event.getExtraLife();
      }
   }

   public static int onItemPickup(ItemEntity entityItem, PlayerEntity player) {
      Event event = new EntityItemPickupEvent(player, entityItem);
      if (MinecraftForge.EVENT_BUS.post(event)) {
         return -1;
      } else {
         return event.getResult() == Result.ALLOW ? 1 : 0;
      }
   }

   public static boolean canMountEntity(Entity entityMounting, Entity entityBeingMounted, boolean isMounting) {
      boolean isCanceled = MinecraftForge.EVENT_BUS.post(new EntityMountEvent(entityMounting, entityBeingMounted, entityMounting.world, isMounting));
      if (isCanceled) {
         entityMounting.setPositionAndRotation(entityMounting.func_226277_ct_(), entityMounting.func_226278_cu_(), entityMounting.func_226281_cx_(), entityMounting.prevRotationYaw, entityMounting.prevRotationPitch);
         return false;
      } else {
         return true;
      }
   }

   public static boolean onAnimalTame(AnimalEntity animal, PlayerEntity tamer) {
      return MinecraftForge.EVENT_BUS.post(new AnimalTameEvent(animal, tamer));
   }

   public static PlayerEntity.SleepResult onPlayerSleepInBed(PlayerEntity player, Optional<BlockPos> pos) {
      PlayerSleepInBedEvent event = new PlayerSleepInBedEvent(player, pos);
      MinecraftForge.EVENT_BUS.post(event);
      return event.getResultStatus();
   }

   public static void onPlayerWakeup(PlayerEntity player, boolean wakeImmediately, boolean updateWorldFlag) {
      MinecraftForge.EVENT_BUS.post(new PlayerWakeUpEvent(player, wakeImmediately, updateWorldFlag));
   }

   public static void onPlayerFall(PlayerEntity player, float distance, float multiplier) {
      MinecraftForge.EVENT_BUS.post(new PlayerFlyableFallEvent(player, distance, multiplier));
   }

   public static boolean onPlayerSpawnSet(PlayerEntity player, BlockPos pos, boolean forced) {
      return MinecraftForge.EVENT_BUS.post(new PlayerSetSpawnEvent(player, pos, forced));
   }

   public static void onPlayerClone(PlayerEntity player, PlayerEntity oldPlayer, boolean wasDeath) {
      MinecraftForge.EVENT_BUS.post(new PlayerEvent.Clone(player, oldPlayer, wasDeath));
   }

   public static boolean onExplosionStart(World world, Explosion explosion) {
      return MinecraftForge.EVENT_BUS.post(new ExplosionEvent.Start(world, explosion));
   }

   public static void onExplosionDetonate(World world, Explosion explosion, List<Entity> list, double diameter) {
      MinecraftForge.EVENT_BUS.post(new ExplosionEvent.Detonate(world, explosion, list));
   }

   public static boolean onCreateWorldSpawn(World world, WorldSettings settings) {
      return MinecraftForge.EVENT_BUS.post(new WorldEvent.CreateSpawnPosition(world, settings));
   }

   public static float onLivingHeal(LivingEntity entity, float amount) {
      LivingHealEvent event = new LivingHealEvent(entity, amount);
      return MinecraftForge.EVENT_BUS.post(event) ? 0.0F : event.getAmount();
   }

   public static boolean onPotionAttemptBrew(NonNullList<ItemStack> stacks) {
      NonNullList<ItemStack> tmp = NonNullList.withSize(stacks.size(), ItemStack.EMPTY);

      for(int x = 0; x < tmp.size(); ++x) {
         tmp.set(x, ((ItemStack)stacks.get(x)).copy());
      }

      PotionBrewEvent.Pre event = new PotionBrewEvent.Pre(tmp);
      if (!MinecraftForge.EVENT_BUS.post(event)) {
         return false;
      } else {
         boolean changed = false;

         for(int x = 0; x < stacks.size(); ++x) {
            changed |= ItemStack.areItemStacksEqual((ItemStack)tmp.get(x), (ItemStack)stacks.get(x));
            stacks.set(x, event.getItem(x));
         }

         if (changed) {
            onPotionBrewed(stacks);
         }

         return true;
      }
   }

   public static void onPotionBrewed(NonNullList<ItemStack> brewingItemStacks) {
      MinecraftForge.EVENT_BUS.post(new PotionBrewEvent.Post(brewingItemStacks));
   }

   public static void onPlayerBrewedPotion(PlayerEntity player, ItemStack stack) {
      MinecraftForge.EVENT_BUS.post(new PlayerBrewedPotionEvent(player, stack));
   }

   @OnlyIn(Dist.CLIENT)
   public static boolean renderFireOverlay(PlayerEntity player, MatrixStack mat) {
      return renderBlockOverlay(player, mat, RenderBlockOverlayEvent.OverlayType.FIRE, Blocks.FIRE.getDefaultState(), new BlockPos(player));
   }

   @OnlyIn(Dist.CLIENT)
   public static boolean renderWaterOverlay(PlayerEntity player, MatrixStack mat) {
      return renderBlockOverlay(player, mat, RenderBlockOverlayEvent.OverlayType.WATER, Blocks.WATER.getDefaultState(), new BlockPos(player));
   }

   @OnlyIn(Dist.CLIENT)
   public static boolean renderBlockOverlay(PlayerEntity player, MatrixStack mat, RenderBlockOverlayEvent.OverlayType type, BlockState block, BlockPos pos) {
      return MinecraftForge.EVENT_BUS.post(new RenderBlockOverlayEvent(player, mat, type, block, pos));
   }

   @Nullable
   public static <T extends ICapabilityProvider> CapabilityDispatcher gatherCapabilities(Class<? extends T> type, T provider) {
      return gatherCapabilities(type, provider, (ICapabilityProvider)null);
   }

   @Nullable
   public static <T extends ICapabilityProvider> CapabilityDispatcher gatherCapabilities(Class<? extends T> type, T provider, @Nullable ICapabilityProvider parent) {
      return gatherCapabilities(new AttachCapabilitiesEvent(type, provider), parent);
   }

   @Nullable
   private static CapabilityDispatcher gatherCapabilities(AttachCapabilitiesEvent<?> event, @Nullable ICapabilityProvider parent) {
      MinecraftForge.EVENT_BUS.post(event);
      return event.getCapabilities().size() <= 0 && parent == null ? null : new CapabilityDispatcher(event.getCapabilities(), event.getListeners(), parent);
   }

   public static boolean fireSleepingLocationCheck(LivingEntity player, BlockPos sleepingLocation) {
      SleepingLocationCheckEvent evt = new SleepingLocationCheckEvent(player, sleepingLocation);
      MinecraftForge.EVENT_BUS.post(evt);
      Result canContinueSleep = evt.getResult();
      if (canContinueSleep == Result.DEFAULT) {
         return (Boolean)player.getBedPosition().map((pos) -> {
            BlockState state = player.world.getBlockState(pos);
            return state.getBlock().isBed(state, player.world, pos, player);
         }).orElse(false);
      } else {
         return canContinueSleep == Result.ALLOW;
      }
   }

   public static boolean fireSleepingTimeCheck(PlayerEntity player, Optional<BlockPos> sleepingLocation) {
      SleepingTimeCheckEvent evt = new SleepingTimeCheckEvent(player, sleepingLocation);
      MinecraftForge.EVENT_BUS.post(evt);
      Result canContinueSleep = evt.getResult();
      if (canContinueSleep == Result.DEFAULT) {
         return !player.world.isDaytime();
      } else {
         return canContinueSleep == Result.ALLOW;
      }
   }

   public static ActionResult<ItemStack> onArrowNock(ItemStack item, World world, PlayerEntity player, Hand hand, boolean hasAmmo) {
      ArrowNockEvent event = new ArrowNockEvent(player, item, hand, world, hasAmmo);
      return MinecraftForge.EVENT_BUS.post(event) ? new ActionResult(ActionResultType.FAIL, item) : event.getAction();
   }

   public static int onArrowLoose(ItemStack stack, World world, PlayerEntity player, int charge, boolean hasAmmo) {
      ArrowLooseEvent event = new ArrowLooseEvent(player, stack, world, charge, hasAmmo);
      return MinecraftForge.EVENT_BUS.post(event) ? -1 : event.getCharge();
   }

   public static boolean onProjectileImpact(Entity entity, RayTraceResult ray) {
      return MinecraftForge.EVENT_BUS.post(new ProjectileImpactEvent(entity, ray));
   }

   public static boolean onProjectileImpact(AbstractArrowEntity arrow, RayTraceResult ray) {
      return MinecraftForge.EVENT_BUS.post(new ProjectileImpactEvent.Arrow(arrow, ray));
   }

   public static boolean onProjectileImpact(DamagingProjectileEntity fireball, RayTraceResult ray) {
      return MinecraftForge.EVENT_BUS.post(new ProjectileImpactEvent.Fireball(fireball, ray));
   }

   public static boolean onProjectileImpact(ThrowableEntity throwable, RayTraceResult ray) {
      return MinecraftForge.EVENT_BUS.post(new ProjectileImpactEvent.Throwable(throwable, ray));
   }

   public static LootTable loadLootTable(ResourceLocation name, LootTable table, LootTableManager lootTableManager) {
      LootTableLoadEvent event = new LootTableLoadEvent(name, table, lootTableManager);
      return MinecraftForge.EVENT_BUS.post(event) ? LootTable.EMPTY_LOOT_TABLE : event.getTable();
   }

   public static boolean canCreateFluidSource(World world, BlockPos pos, BlockState state, boolean def) {
      BlockEvent.CreateFluidSourceEvent evt = new BlockEvent.CreateFluidSourceEvent(world, pos, state);
      MinecraftForge.EVENT_BUS.post(evt);
      Result result = evt.getResult();
      return result == Result.DEFAULT ? def : result == Result.ALLOW;
   }

   public static boolean onTrySpawnPortal(IWorld world, BlockPos pos, NetherPortalBlock.Size size) {
      return MinecraftForge.EVENT_BUS.post(new BlockEvent.PortalSpawnEvent(world, pos, world.getBlockState(pos), size));
   }

   public static int onEnchantmentLevelSet(World world, BlockPos pos, int enchantRow, int power, ItemStack itemStack, int level) {
      EnchantmentLevelSetEvent e = new EnchantmentLevelSetEvent(world, pos, enchantRow, power, itemStack, level);
      MinecraftForge.EVENT_BUS.post(e);
      return e.getLevel();
   }

   public static boolean onEntityDestroyBlock(LivingEntity entity, BlockPos pos, BlockState state) {
      return !MinecraftForge.EVENT_BUS.post(new LivingDestroyBlockEvent(entity, pos, state));
   }

   public static boolean gatherCollisionBoxes(World world, Entity entity, AxisAlignedBB aabb, List<AxisAlignedBB> outList) {
      MinecraftForge.EVENT_BUS.post(new GetCollisionBoxesEvent(world, entity, aabb, outList));
      return outList.isEmpty();
   }

   public static boolean getMobGriefingEvent(World world, Entity entity) {
      EntityMobGriefingEvent event = new EntityMobGriefingEvent(entity);
      MinecraftForge.EVENT_BUS.post(event);
      Result result = event.getResult();
      return result == Result.DEFAULT ? world.getGameRules().getBoolean(GameRules.MOB_GRIEFING) : result == Result.ALLOW;
   }

   public static boolean saplingGrowTree(IWorld world, Random rand, BlockPos pos) {
      SaplingGrowTreeEvent event = new SaplingGrowTreeEvent(world, rand, pos);
      MinecraftForge.EVENT_BUS.post(event);
      return event.getResult() != Result.DENY;
   }

   public static void fireChunkWatch(boolean watch, ServerPlayerEntity entity, ChunkPos chunkpos, ServerWorld world) {
      if (watch) {
         MinecraftForge.EVENT_BUS.post(new ChunkWatchEvent.Watch(entity, chunkpos, world));
      } else {
         MinecraftForge.EVENT_BUS.post(new ChunkWatchEvent.UnWatch(entity, chunkpos, world));
      }

   }

   public static boolean onPistonMovePre(World world, BlockPos pos, Direction direction, boolean extending) {
      return MinecraftForge.EVENT_BUS.post(new PistonEvent.Pre(world, pos, direction, extending ? PistonEvent.PistonMoveType.EXTEND : PistonEvent.PistonMoveType.RETRACT));
   }

   public static boolean onPistonMovePost(World world, BlockPos pos, Direction direction, boolean extending) {
      return MinecraftForge.EVENT_BUS.post(new PistonEvent.Post(world, pos, direction, extending ? PistonEvent.PistonMoveType.EXTEND : PistonEvent.PistonMoveType.RETRACT));
   }

   public static long onSleepFinished(ServerWorld world, long newTime, long minTime) {
      SleepFinishedTimeEvent event = new SleepFinishedTimeEvent(world, newTime, minTime);
      MinecraftForge.EVENT_BUS.post(event);
      return event.getNewTime();
   }
}
