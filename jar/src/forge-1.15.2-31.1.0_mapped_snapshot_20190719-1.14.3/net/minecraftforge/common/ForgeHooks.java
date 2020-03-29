package net.minecraftforge.common;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.item.minecart.ContainerMinecartEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.EmptyFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.fluid.LavaFluid;
import net.minecraft.fluid.WaterFluid;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.RepairContainer;
import net.minecraft.item.AxeItem;
import net.minecraft.item.BucketItem;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.PotionItem;
import net.minecraft.item.ShovelItem;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.item.TippedArrowItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.IDataSerializer;
import net.minecraft.network.play.ServerPlayNetHandler;
import net.minecraft.network.play.server.SChangeBlockPacket;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.stats.Stats;
import net.minecraft.tags.Tag;
import net.minecraft.tileentity.FurnaceTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.CachedBlockInfo;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.IntIdentityHashBiMap;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameType;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.spawner.AbstractSpawner;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTableManager;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.DifficultyChangeEvent;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.living.LootingLevelEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.event.entity.player.AnvilRepairEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.NoteBlockEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.registries.DataSerializerEntry;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.GameData;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.IRegistryDelegate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.apache.logging.log4j.util.TriConsumer;

public class ForgeHooks {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Marker FORGEHOOKS = MarkerManager.getMarker("FORGEHOOKS");
   private static boolean toolInit = false;
   static final Pattern URL_PATTERN = Pattern.compile("((?:[a-z0-9]{2,}:\\/\\/)?(?:(?:[0-9]{1,3}\\.){3}[0-9]{1,3}|(?:[-\\w_]{1,}\\.[a-z]{2,}?))(?::[0-9]{1,5})?.*?(?=[!\"§ \n]|$))", 2);
   private static ThreadLocal<PlayerEntity> craftingPlayer = new ThreadLocal();
   private static ThreadLocal<Deque<ForgeHooks.LootTableContext>> lootContext = new ThreadLocal();
   private static TriConsumer<Block, ToolType, Integer> blockToolSetter;
   private static final ForgeHooks.DummyBlockReader DUMMY_WORLD = new ForgeHooks.DummyBlockReader();
   private static final Map<IDataSerializer<?>, DataSerializerEntry> serializerEntries = GameData.getSerializerMap();
   private static final Map<IRegistryDelegate<Item>, Integer> VANILLA_BURNS = new HashMap();

   public static boolean canContinueUsing(@Nonnull ItemStack from, @Nonnull ItemStack to) {
      return !from.isEmpty() && !to.isEmpty() ? from.getItem().canContinueUsing(from, to) : false;
   }

   public static boolean canHarvestBlock(@Nonnull BlockState state, @Nonnull PlayerEntity player, @Nonnull IBlockReader world, @Nonnull BlockPos pos) {
      if (state.getMaterial().isToolNotRequired()) {
         return true;
      } else {
         ItemStack stack = player.getHeldItemMainhand();
         ToolType tool = state.getHarvestTool();
         if (!stack.isEmpty() && tool != null) {
            int toolLevel = stack.getItem().getHarvestLevel(stack, tool, player, state);
            if (toolLevel < 0) {
               return player.canHarvestBlock(state);
            } else {
               return toolLevel >= state.getHarvestLevel();
            }
         } else {
            return player.canHarvestBlock(state);
         }
      }
   }

   public static boolean canToolHarvestBlock(IWorldReader world, BlockPos pos, @Nonnull ItemStack stack) {
      BlockState state = world.getBlockState(pos);
      ToolType tool = state.getHarvestTool();
      if (!stack.isEmpty() && tool != null) {
         return stack.getHarvestLevel(tool, (PlayerEntity)null, (BlockState)null) >= state.getHarvestLevel();
      } else {
         return false;
      }
   }

   public static boolean isToolEffective(IWorldReader world, BlockPos pos, @Nonnull ItemStack stack) {
      BlockState state = world.getBlockState(pos);
      Iterator var4 = stack.getToolTypes().iterator();

      ToolType type;
      do {
         if (!var4.hasNext()) {
            return false;
         }

         type = (ToolType)var4.next();
      } while(!state.isToolEffective(type));

      return true;
   }

   static void initTools() {
      if (!toolInit) {
         toolInit = true;
         Set<Block> blocks = (Set)getPrivateValue(PickaxeItem.class, (Object)null, 0);
         blocks.forEach((blockx) -> {
            blockToolSetter.accept(blockx, ToolType.PICKAXE, 0);
         });
         blocks = (Set)getPrivateValue(ShovelItem.class, (Object)null, 0);
         blocks.forEach((blockx) -> {
            blockToolSetter.accept(blockx, ToolType.SHOVEL, 0);
         });
         blocks = (Set)getPrivateValue(AxeItem.class, (Object)null, 0);
         blocks.forEach((blockx) -> {
            blockToolSetter.accept(blockx, ToolType.AXE, 0);
         });
         blockToolSetter.accept(Blocks.OBSIDIAN, ToolType.PICKAXE, 3);
         Block[] var1 = new Block[]{Blocks.DIAMOND_BLOCK, Blocks.DIAMOND_ORE, Blocks.EMERALD_ORE, Blocks.EMERALD_BLOCK, Blocks.GOLD_BLOCK, Blocks.GOLD_ORE, Blocks.REDSTONE_ORE};
         int var2 = var1.length;

         int var3;
         Block block;
         for(var3 = 0; var3 < var2; ++var3) {
            block = var1[var3];
            blockToolSetter.accept(block, ToolType.PICKAXE, 2);
         }

         var1 = new Block[]{Blocks.IRON_BLOCK, Blocks.IRON_ORE, Blocks.LAPIS_BLOCK, Blocks.LAPIS_ORE};
         var2 = var1.length;

         for(var3 = 0; var3 < var2; ++var3) {
            block = var1[var3];
            blockToolSetter.accept(block, ToolType.PICKAXE, 1);
         }

      }
   }

   public static boolean onPickBlock(RayTraceResult target, PlayerEntity player, World world) {
      ItemStack result = ItemStack.EMPTY;
      boolean isCreative = player.abilities.isCreativeMode;
      TileEntity te = null;
      if (target.getType() == RayTraceResult.Type.BLOCK) {
         BlockPos pos = ((BlockRayTraceResult)target).getPos();
         BlockState state = world.getBlockState(pos);
         if (state.isAir(world, pos)) {
            return false;
         }

         if (isCreative && Screen.hasControlDown() && state.hasTileEntity()) {
            te = world.getTileEntity(pos);
         }

         result = state.getBlock().getPickBlock(state, target, world, pos, player);
         if (result.isEmpty()) {
            LOGGER.warn("Picking on: [{}] {} gave null item", target.getType(), state.getBlock().getRegistryName());
         }
      } else if (target.getType() == RayTraceResult.Type.ENTITY) {
         Entity entity = ((EntityRayTraceResult)target).getEntity();
         result = entity.getPickedResult(target);
         if (result.isEmpty()) {
            LOGGER.warn("Picking on: [{}] {} gave null item", target.getType(), entity.getType().getRegistryName());
         }
      }

      if (result.isEmpty()) {
         return false;
      } else {
         if (te != null) {
            Minecraft.getInstance().storeTEInStack(result, te);
         }

         if (isCreative) {
            player.inventory.setPickedItemStack(result);
            Minecraft.getInstance().playerController.sendSlotPacket(player.getHeldItem(Hand.MAIN_HAND), 36 + player.inventory.currentItem);
            return true;
         } else {
            int slot = player.inventory.getSlotFor(result);
            if (slot != -1) {
               if (PlayerInventory.isHotbar(slot)) {
                  player.inventory.currentItem = slot;
               } else {
                  Minecraft.getInstance().playerController.pickItem(slot);
               }

               return true;
            } else {
               return false;
            }
         }
      }
   }

   public static void onDifficultyChange(Difficulty difficulty, Difficulty oldDifficulty) {
      MinecraftForge.EVENT_BUS.post(new DifficultyChangeEvent(difficulty, oldDifficulty));
   }

   public static void onLivingSetAttackTarget(LivingEntity entity, LivingEntity target) {
      MinecraftForge.EVENT_BUS.post(new LivingSetAttackTargetEvent(entity, target));
   }

   public static boolean onLivingUpdate(LivingEntity entity) {
      return MinecraftForge.EVENT_BUS.post(new LivingEvent.LivingUpdateEvent(entity));
   }

   public static boolean onLivingAttack(LivingEntity entity, DamageSource src, float amount) {
      return entity instanceof PlayerEntity || !MinecraftForge.EVENT_BUS.post(new LivingAttackEvent(entity, src, amount));
   }

   public static boolean onPlayerAttack(LivingEntity entity, DamageSource src, float amount) {
      return !MinecraftForge.EVENT_BUS.post(new LivingAttackEvent(entity, src, amount));
   }

   public static LivingKnockBackEvent onLivingKnockBack(LivingEntity target, Entity attacker, float strength, double ratioX, double ratioZ) {
      LivingKnockBackEvent event = new LivingKnockBackEvent(target, attacker, strength, ratioX, ratioZ);
      MinecraftForge.EVENT_BUS.post(event);
      return event;
   }

   public static float onLivingHurt(LivingEntity entity, DamageSource src, float amount) {
      LivingHurtEvent event = new LivingHurtEvent(entity, src, amount);
      return MinecraftForge.EVENT_BUS.post(event) ? 0.0F : event.getAmount();
   }

   public static float onLivingDamage(LivingEntity entity, DamageSource src, float amount) {
      LivingDamageEvent event = new LivingDamageEvent(entity, src, amount);
      return MinecraftForge.EVENT_BUS.post(event) ? 0.0F : event.getAmount();
   }

   public static boolean onLivingDeath(LivingEntity entity, DamageSource src) {
      return MinecraftForge.EVENT_BUS.post(new LivingDeathEvent(entity, src));
   }

   public static boolean onLivingDrops(LivingEntity entity, DamageSource source, Collection<ItemEntity> drops, int lootingLevel, boolean recentlyHit) {
      return MinecraftForge.EVENT_BUS.post(new LivingDropsEvent(entity, source, drops, lootingLevel, recentlyHit));
   }

   @Nullable
   public static float[] onLivingFall(LivingEntity entity, float distance, float damageMultiplier) {
      LivingFallEvent event = new LivingFallEvent(entity, distance, damageMultiplier);
      return MinecraftForge.EVENT_BUS.post(event) ? null : new float[]{event.getDistance(), event.getDamageMultiplier()};
   }

   public static int getLootingLevel(Entity target, @Nullable Entity killer, DamageSource cause) {
      int looting = 0;
      if (killer instanceof LivingEntity) {
         looting = EnchantmentHelper.getLootingModifier((LivingEntity)killer);
      }

      if (target instanceof LivingEntity) {
         looting = getLootingLevel((LivingEntity)target, cause, looting);
      }

      return looting;
   }

   public static int getLootingLevel(LivingEntity target, DamageSource cause, int level) {
      LootingLevelEvent event = new LootingLevelEvent(target, cause, level);
      MinecraftForge.EVENT_BUS.post(event);
      return event.getLootingLevel();
   }

   public static double getPlayerVisibilityDistance(PlayerEntity player, double xzDistance, double maxXZDistance) {
      PlayerEvent.Visibility event = new PlayerEvent.Visibility(player);
      MinecraftForge.EVENT_BUS.post(event);
      double value = event.getVisibilityModifier() * xzDistance;
      return value >= maxXZDistance ? maxXZDistance : value;
   }

   public static boolean isLivingOnLadder(@Nonnull BlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull LivingEntity entity) {
      boolean isSpectator = entity instanceof PlayerEntity && ((PlayerEntity)entity).isSpectator();
      if (isSpectator) {
         return false;
      } else if (!(Boolean)ForgeConfig.SERVER.fullBoundingBoxLadders.get()) {
         return state.isLadder(world, pos, entity);
      } else {
         AxisAlignedBB bb = entity.getBoundingBox();
         int mX = MathHelper.floor(bb.minX);
         int mY = MathHelper.floor(bb.minY);
         int mZ = MathHelper.floor(bb.minZ);

         for(int y2 = mY; (double)y2 < bb.maxY; ++y2) {
            for(int x2 = mX; (double)x2 < bb.maxX; ++x2) {
               for(int z2 = mZ; (double)z2 < bb.maxZ; ++z2) {
                  BlockPos tmp = new BlockPos(x2, y2, z2);
                  state = world.getBlockState(tmp);
                  if (state.isLadder(world, tmp, entity)) {
                     return true;
                  }
               }
            }
         }

         return false;
      }
   }

   public static void onLivingJump(LivingEntity entity) {
      MinecraftForge.EVENT_BUS.post(new LivingEvent.LivingJumpEvent(entity));
   }

   @Nullable
   public static ItemEntity onPlayerTossEvent(@Nonnull PlayerEntity player, @Nonnull ItemStack item, boolean includeName) {
      player.captureDrops(Lists.newArrayList());
      ItemEntity ret = player.dropItem(item, false, includeName);
      player.captureDrops((Collection)null);
      if (ret == null) {
         return null;
      } else {
         ItemTossEvent event = new ItemTossEvent(ret, player);
         if (MinecraftForge.EVENT_BUS.post(event)) {
            return null;
         } else {
            if (!player.world.isRemote) {
               player.getEntityWorld().addEntity(event.getEntityItem());
            }

            return event.getEntityItem();
         }
      }
   }

   @Nullable
   public static ITextComponent onServerChatEvent(ServerPlayNetHandler net, String raw, ITextComponent comp) {
      ServerChatEvent event = new ServerChatEvent(net.player, raw, comp);
      return MinecraftForge.EVENT_BUS.post(event) ? null : event.getComponent();
   }

   public static ITextComponent newChatWithLinks(String string) {
      return newChatWithLinks(string, true);
   }

   public static ITextComponent newChatWithLinks(String string, boolean allowMissingHeader) {
      ITextComponent ichat = null;
      Matcher matcher = URL_PATTERN.matcher(string);
      int lastEnd = 0;

      while(true) {
         String url;
         StringTextComponent link;
         while(true) {
            if (!matcher.find()) {
               String end = string.substring(lastEnd);
               if (ichat == null) {
                  ichat = new StringTextComponent(end);
               } else if (end.length() > 0) {
                  ichat.appendText(string.substring(lastEnd));
               }

               return ichat;
            }

            int start = matcher.start();
            int end = matcher.end();
            String part = string.substring(lastEnd, start);
            if (part.length() > 0) {
               if (ichat == null) {
                  ichat = new StringTextComponent(part);
               } else {
                  ichat.appendText(part);
               }
            }

            lastEnd = end;
            url = string.substring(start, end);
            link = new StringTextComponent(url);

            try {
               if ((new URI(url)).getScheme() != null) {
                  break;
               }

               if (allowMissingHeader) {
                  url = "http://" + url;
                  break;
               }

               if (ichat == null) {
                  ichat = new StringTextComponent(url);
               } else {
                  ichat.appendText(url);
               }
            } catch (URISyntaxException var11) {
               if (ichat == null) {
                  ichat = new StringTextComponent(url);
               } else {
                  ichat.appendText(url);
               }
            }
         }

         ClickEvent click = new ClickEvent(ClickEvent.Action.OPEN_URL, url);
         link.getStyle().setClickEvent(click);
         link.getStyle().setUnderlined(true);
         link.getStyle().setColor(TextFormatting.BLUE);
         if (ichat == null) {
            ichat = new StringTextComponent("");
         }

         ichat.appendSibling(link);
      }
   }

   public static int onBlockBreakEvent(World world, GameType gameType, ServerPlayerEntity entityPlayer, BlockPos pos) {
      boolean preCancelEvent = false;
      ItemStack itemstack = entityPlayer.getHeldItemMainhand();
      if (!itemstack.isEmpty() && !itemstack.getItem().canPlayerBreakBlockWhileHolding(world.getBlockState(pos), world, pos, entityPlayer)) {
         preCancelEvent = true;
      }

      if (gameType.hasLimitedInteractions()) {
         if (gameType == GameType.SPECTATOR) {
            preCancelEvent = true;
         }

         if (!entityPlayer.isAllowEdit() && (itemstack.isEmpty() || !itemstack.canDestroy(world.getTags(), new CachedBlockInfo(world, pos, false)))) {
            preCancelEvent = true;
         }
      }

      if (world.getTileEntity(pos) == null) {
         entityPlayer.connection.sendPacket(new SChangeBlockPacket(DUMMY_WORLD, pos));
      }

      BlockState state = world.getBlockState(pos);
      BlockEvent.BreakEvent event = new BlockEvent.BreakEvent(world, pos, state, entityPlayer);
      event.setCanceled(preCancelEvent);
      MinecraftForge.EVENT_BUS.post(event);
      if (event.isCanceled()) {
         entityPlayer.connection.sendPacket(new SChangeBlockPacket(world, pos));
         TileEntity tileentity = world.getTileEntity(pos);
         if (tileentity != null) {
            IPacket<?> pkt = tileentity.getUpdatePacket();
            if (pkt != null) {
               entityPlayer.connection.sendPacket(pkt);
            }
         }
      }

      return event.isCanceled() ? -1 : event.getExpToDrop();
   }

   public static ActionResultType onPlaceItemIntoWorld(@Nonnull ItemUseContext context) {
      ItemStack itemstack = context.getItem();
      World world = context.getWorld();
      PlayerEntity player = context.getPlayer();
      if (player != null && !player.abilities.allowEdit && !itemstack.canPlaceOn(world.getTags(), new CachedBlockInfo(world, context.getPos(), false))) {
         return ActionResultType.PASS;
      } else {
         Item item = itemstack.getItem();
         int size = itemstack.getCount();
         CompoundNBT nbt = null;
         if (itemstack.getTag() != null) {
            nbt = itemstack.getTag().copy();
         }

         if (!(itemstack.getItem() instanceof BucketItem)) {
            world.captureBlockSnapshots = true;
         }

         ItemStack copy = itemstack.isDamageable() ? itemstack.copy() : null;
         ActionResultType ret = itemstack.getItem().onItemUse(context);
         if (itemstack.isEmpty()) {
            ForgeEventFactory.onPlayerDestroyItem(player, copy, context.getHand());
         }

         world.captureBlockSnapshots = false;
         if (ret == ActionResultType.SUCCESS) {
            int newSize = itemstack.getCount();
            CompoundNBT newNBT = null;
            if (itemstack.getTag() != null) {
               newNBT = itemstack.getTag().copy();
            }

            List<BlockSnapshot> blockSnapshots = (List)world.capturedBlockSnapshots.clone();
            world.capturedBlockSnapshots.clear();
            itemstack.setCount(size);
            itemstack.setTag(nbt);
            Direction side = context.getFace();
            boolean eventResult = false;
            if (blockSnapshots.size() > 1) {
               eventResult = ForgeEventFactory.onMultiBlockPlace(player, blockSnapshots, side);
            } else if (blockSnapshots.size() == 1) {
               eventResult = ForgeEventFactory.onBlockPlace(player, (BlockSnapshot)blockSnapshots.get(0), side);
            }

            Iterator var14;
            BlockSnapshot snap;
            if (eventResult) {
               ret = ActionResultType.FAIL;

               for(var14 = Lists.reverse(blockSnapshots).iterator(); var14.hasNext(); world.restoringBlockSnapshots = false) {
                  snap = (BlockSnapshot)var14.next();
                  world.restoringBlockSnapshots = true;
                  snap.restore(true, false);
               }
            } else {
               itemstack.setCount(newSize);
               itemstack.setTag(newNBT);

               int updateFlag;
               BlockState oldBlock;
               BlockState newBlock;
               for(var14 = blockSnapshots.iterator(); var14.hasNext(); world.markAndNotifyBlock(snap.getPos(), (Chunk)null, oldBlock, newBlock, updateFlag)) {
                  snap = (BlockSnapshot)var14.next();
                  updateFlag = snap.getFlag();
                  oldBlock = snap.getReplacedBlock();
                  newBlock = world.getBlockState(snap.getPos());
                  if (!newBlock.getBlock().hasTileEntity(newBlock)) {
                     newBlock.onBlockAdded(world, snap.getPos(), oldBlock, false);
                  }
               }

               player.addStat(Stats.ITEM_USED.get(item));
            }
         }

         world.capturedBlockSnapshots.clear();
         return ret;
      }
   }

   public static boolean onAnvilChange(RepairContainer container, @Nonnull ItemStack left, @Nonnull ItemStack right, IInventory outputSlot, String name, int baseCost) {
      AnvilUpdateEvent e = new AnvilUpdateEvent(left, right, name, baseCost);
      if (MinecraftForge.EVENT_BUS.post(e)) {
         return false;
      } else if (e.getOutput().isEmpty()) {
         return true;
      } else {
         outputSlot.setInventorySlotContents(0, e.getOutput());
         container.setMaximumCost(e.getCost());
         container.materialCost = e.getMaterialCost();
         return false;
      }
   }

   public static float onAnvilRepair(PlayerEntity player, @Nonnull ItemStack output, @Nonnull ItemStack left, @Nonnull ItemStack right) {
      AnvilRepairEvent e = new AnvilRepairEvent(player, left, right, output);
      MinecraftForge.EVENT_BUS.post(e);
      return e.getBreakChance();
   }

   public static void setCraftingPlayer(PlayerEntity player) {
      craftingPlayer.set(player);
   }

   public static PlayerEntity getCraftingPlayer() {
      return (PlayerEntity)craftingPlayer.get();
   }

   @Nonnull
   public static ItemStack getContainerItem(@Nonnull ItemStack stack) {
      if (stack.getItem().hasContainerItem(stack)) {
         stack = stack.getItem().getContainerItem(stack);
         if (!stack.isEmpty() && stack.isDamageable() && stack.getDamage() > stack.getMaxDamage()) {
            ForgeEventFactory.onPlayerDestroyItem((PlayerEntity)craftingPlayer.get(), stack, (Hand)null);
            return ItemStack.EMPTY;
         } else {
            return stack;
         }
      } else {
         return ItemStack.EMPTY;
      }
   }

   public static boolean onPlayerAttackTarget(PlayerEntity player, Entity target) {
      if (MinecraftForge.EVENT_BUS.post(new AttackEntityEvent(player, target))) {
         return false;
      } else {
         ItemStack stack = player.getHeldItemMainhand();
         return stack.isEmpty() || !stack.getItem().onLeftClickEntity(stack, player, target);
      }
   }

   public static boolean onTravelToDimension(Entity entity, DimensionType dimension) {
      EntityTravelToDimensionEvent event = new EntityTravelToDimensionEvent(entity, dimension);
      MinecraftForge.EVENT_BUS.post(event);
      if (event.isCanceled() && entity instanceof ContainerMinecartEntity) {
         ((ContainerMinecartEntity)entity).dropContentsWhenDead(true);
      }

      return !event.isCanceled();
   }

   public static ActionResultType onInteractEntityAt(PlayerEntity player, Entity entity, RayTraceResult ray, Hand hand) {
      Vec3d vec3d = ray.getHitVec().subtract(entity.getPositionVec());
      return onInteractEntityAt(player, entity, vec3d, hand);
   }

   public static ActionResultType onInteractEntityAt(PlayerEntity player, Entity entity, Vec3d vec3d, Hand hand) {
      PlayerInteractEvent.EntityInteractSpecific evt = new PlayerInteractEvent.EntityInteractSpecific(player, hand, entity, vec3d);
      MinecraftForge.EVENT_BUS.post(evt);
      return evt.isCanceled() ? evt.getCancellationResult() : null;
   }

   public static ActionResultType onInteractEntity(PlayerEntity player, Entity entity, Hand hand) {
      PlayerInteractEvent.EntityInteract evt = new PlayerInteractEvent.EntityInteract(player, hand, entity);
      MinecraftForge.EVENT_BUS.post(evt);
      return evt.isCanceled() ? evt.getCancellationResult() : null;
   }

   public static ActionResultType onItemRightClick(PlayerEntity player, Hand hand) {
      PlayerInteractEvent.RightClickItem evt = new PlayerInteractEvent.RightClickItem(player, hand);
      MinecraftForge.EVENT_BUS.post(evt);
      return evt.isCanceled() ? evt.getCancellationResult() : null;
   }

   public static PlayerInteractEvent.LeftClickBlock onLeftClickBlock(PlayerEntity player, BlockPos pos, Direction face) {
      PlayerInteractEvent.LeftClickBlock evt = new PlayerInteractEvent.LeftClickBlock(player, pos, face);
      MinecraftForge.EVENT_BUS.post(evt);
      return evt;
   }

   public static PlayerInteractEvent.RightClickBlock onRightClickBlock(PlayerEntity player, Hand hand, BlockPos pos, Direction face) {
      PlayerInteractEvent.RightClickBlock evt = new PlayerInteractEvent.RightClickBlock(player, hand, pos, face);
      MinecraftForge.EVENT_BUS.post(evt);
      return evt;
   }

   public static void onEmptyClick(PlayerEntity player, Hand hand) {
      MinecraftForge.EVENT_BUS.post(new PlayerInteractEvent.RightClickEmpty(player, hand));
   }

   public static void onEmptyLeftClick(PlayerEntity player) {
      MinecraftForge.EVENT_BUS.post(new PlayerInteractEvent.LeftClickEmpty(player));
   }

   private static ForgeHooks.LootTableContext getLootTableContext() {
      ForgeHooks.LootTableContext ctx = (ForgeHooks.LootTableContext)((Deque)lootContext.get()).peek();
      if (ctx == null) {
         throw new JsonParseException("Invalid call stack, could not grab json context!");
      } else {
         return ctx;
      }
   }

   @Nullable
   public static LootTable loadLootTable(Gson gson, ResourceLocation name, JsonObject data, boolean custom, LootTableManager lootTableManager) {
      Deque<ForgeHooks.LootTableContext> que = (Deque)lootContext.get();
      if (que == null) {
         que = Queues.newArrayDeque();
         lootContext.set(que);
      }

      LootTable ret = null;

      try {
         ((Deque)que).push(new ForgeHooks.LootTableContext(name, custom));
         ret = (LootTable)gson.fromJson(data, LootTable.class);
         ((Deque)que).pop();
      } catch (JsonParseException var8) {
         ((Deque)que).pop();
         throw var8;
      }

      if (!custom) {
         ret = ForgeEventFactory.loadLootTable(name, ret, lootTableManager);
      }

      if (ret != null) {
         ret.freeze();
      }

      return ret;
   }

   public static FluidAttributes createVanillaFluidAttributes(Fluid fluid) {
      if (fluid instanceof EmptyFluid) {
         return FluidAttributes.builder((ResourceLocation)null, (ResourceLocation)null).translationKey("block.minecraft.air").color(0).density(0).temperature(0).luminosity(0).viscosity(0).build(fluid);
      } else if (fluid instanceof WaterFluid) {
         return FluidAttributes.Water.builder(new ResourceLocation("block/water_still"), new ResourceLocation("block/water_flow")).overlay(new ResourceLocation("block/water_overlay")).translationKey("block.minecraft.water").color(-12618012).build(fluid);
      } else if (fluid instanceof LavaFluid) {
         return FluidAttributes.builder(new ResourceLocation("block/lava_still"), new ResourceLocation("block/lava_flow")).translationKey("block.minecraft.lava").luminosity(15).density(3000).viscosity(6000).temperature(1300).build(fluid);
      } else {
         throw new RuntimeException("Mod fluids must override createAttributes.");
      }
   }

   public static String readPoolName(JsonObject json) {
      ForgeHooks.LootTableContext ctx = getLootTableContext();
      ctx.resetPoolCtx();
      if (json.has("name")) {
         return JSONUtils.getString(json, "name");
      } else if (ctx.custom) {
         return "custom#" + json.hashCode();
      } else {
         ++ctx.poolCount;
         if (!ctx.vanilla) {
            throw new JsonParseException("Loot Table \"" + ctx.name.toString() + "\" Missing `name` entry for pool #" + (ctx.poolCount - 1));
         } else {
            return ctx.poolCount == 1 ? "main" : "pool" + (ctx.poolCount - 1);
         }
      }
   }

   public static String readLootEntryName(JsonObject json, String type) {
      ForgeHooks.LootTableContext ctx = getLootTableContext();
      ++ctx.entryCount;
      if (json.has("entryName")) {
         return ctx.validateEntryName(JSONUtils.getString(json, "entryName"));
      } else if (ctx.custom) {
         return "custom#" + json.hashCode();
      } else {
         String name = null;
         if ("item".equals(type)) {
            name = JSONUtils.getString(json, "name");
         } else if ("loot_table".equals(type)) {
            name = JSONUtils.getString(json, "name");
         } else if ("empty".equals(type)) {
            name = "empty";
         }

         return ctx.validateEntryName(name);
      }
   }

   public static boolean onCropsGrowPre(World worldIn, BlockPos pos, BlockState state, boolean def) {
      BlockEvent ev = new BlockEvent.CropGrowEvent.Pre(worldIn, pos, state);
      MinecraftForge.EVENT_BUS.post(ev);
      return ev.getResult() == Result.ALLOW || ev.getResult() == Result.DEFAULT && def;
   }

   public static void onCropsGrowPost(World worldIn, BlockPos pos, BlockState state) {
      MinecraftForge.EVENT_BUS.post(new BlockEvent.CropGrowEvent.Post(worldIn, pos, state, worldIn.getBlockState(pos)));
   }

   @Nullable
   public static CriticalHitEvent getCriticalHit(PlayerEntity player, Entity target, boolean vanillaCritical, float damageModifier) {
      CriticalHitEvent hitResult = new CriticalHitEvent(player, target, damageModifier, vanillaCritical);
      MinecraftForge.EVENT_BUS.post(hitResult);
      return hitResult.getResult() != Result.ALLOW && (!vanillaCritical || hitResult.getResult() != Result.DEFAULT) ? null : hitResult;
   }

   public static void onAdvancement(ServerPlayerEntity player, Advancement advancement) {
      MinecraftForge.EVENT_BUS.post(new AdvancementEvent(player, advancement));
   }

   @Nullable
   public static String getDefaultCreatorModId(@Nonnull ItemStack itemStack) {
      Item item = itemStack.getItem();
      ResourceLocation registryName = item.getRegistryName();
      String modId = registryName == null ? null : registryName.getNamespace();
      if ("minecraft".equals(modId)) {
         if (item instanceof EnchantedBookItem) {
            ListNBT enchantmentsNbt = EnchantedBookItem.getEnchantments(itemStack);
            if (enchantmentsNbt.size() == 1) {
               CompoundNBT nbttagcompound = enchantmentsNbt.getCompound(0);
               ResourceLocation resourceLocation = ResourceLocation.tryCreate(nbttagcompound.getString("id"));
               if (resourceLocation != null && ForgeRegistries.ENCHANTMENTS.containsKey(resourceLocation)) {
                  return resourceLocation.getNamespace();
               }
            }
         } else if (!(item instanceof PotionItem) && !(item instanceof TippedArrowItem)) {
            if (item instanceof SpawnEggItem) {
               ResourceLocation resourceLocation = ((SpawnEggItem)item).getType((CompoundNBT)null).getRegistryName();
               if (resourceLocation != null) {
                  return resourceLocation.getNamespace();
               }
            }
         } else {
            Potion potionType = PotionUtils.getPotionFromItem(itemStack);
            ResourceLocation resourceLocation = ForgeRegistries.POTION_TYPES.getKey(potionType);
            if (resourceLocation != null) {
               return resourceLocation.getNamespace();
            }
         }
      }

      return modId;
   }

   public static boolean onFarmlandTrample(World world, BlockPos pos, BlockState state, float fallDistance, Entity entity) {
      if (entity.canTrample(state, pos, fallDistance)) {
         BlockEvent.FarmlandTrampleEvent event = new BlockEvent.FarmlandTrampleEvent(world, pos, state, fallDistance, entity);
         MinecraftForge.EVENT_BUS.post(event);
         return !event.isCanceled();
      } else {
         return false;
      }
   }

   public static void setBlockToolSetter(TriConsumer<Block, ToolType, Integer> setter) {
      blockToolSetter = setter;
   }

   private static <T, E> T getPrivateValue(Class<? super E> classToAccess, @Nullable E instance, int fieldIndex) {
      try {
         Field f = classToAccess.getDeclaredFields()[fieldIndex];
         f.setAccessible(true);
         return f.get(instance);
      } catch (Exception var4) {
         Throwables.throwIfUnchecked(var4);
         throw new RuntimeException(var4);
      }
   }

   public static int onNoteChange(World world, BlockPos pos, BlockState state, int old, int _new) {
      NoteBlockEvent.Change event = new NoteBlockEvent.Change(world, pos, state, old, _new);
      return MinecraftForge.EVENT_BUS.post(event) ? -1 : event.getVanillaNoteId();
   }

   public static int canEntitySpawn(MobEntity entity, IWorld world, double x, double y, double z, AbstractSpawner spawner, SpawnReason spawnReason) {
      Result res = ForgeEventFactory.canEntitySpawn(entity, world, x, y, z, (AbstractSpawner)null, spawnReason);
      return res == Result.DEFAULT ? 0 : (res == Result.DENY ? -1 : 1);
   }

   public static <T> void deserializeTagAdditions(Tag.Builder<T> builder, Function<ResourceLocation, Optional<T>> valueGetter, JsonObject json) {
      Iterator var3;
      JsonElement entry;
      String s;
      Object value;
      if (json.has("optional")) {
         var3 = JSONUtils.getJsonArray(json, "optional").iterator();

         while(var3.hasNext()) {
            entry = (JsonElement)var3.next();
            s = JSONUtils.getString(entry, "value");
            if (!s.startsWith("#")) {
               value = ((Optional)valueGetter.apply(new ResourceLocation(s))).orElse((Object)null);
               if (value != null) {
                  builder.add(value);
               }
            } else {
               builder.add((Tag.ITagEntry)(new ForgeHooks.OptionalTagEntry(new ResourceLocation(s.substring(1)))));
            }
         }
      }

      if (json.has("remove")) {
         var3 = JSONUtils.getJsonArray(json, "remove").iterator();

         while(var3.hasNext()) {
            entry = (JsonElement)var3.next();
            s = JSONUtils.getString(entry, "value");
            if (!s.startsWith("#")) {
               value = ((Optional)valueGetter.apply(new ResourceLocation(s))).orElse((Object)null);
               if (value != null) {
                  Tag.ITagEntry<T> dummyEntry = new Tag.ListEntry(Collections.singletonList(value));
                  builder.remove(dummyEntry);
               }
            } else {
               Tag.ITagEntry<T> dummyEntry = new Tag.TagEntry(new ResourceLocation(s.substring(1)));
               builder.remove(dummyEntry);
            }
         }
      }

   }

   @Nullable
   public static IDataSerializer<?> getSerializer(int id, IntIdentityHashBiMap<IDataSerializer<?>> vanilla) {
      IDataSerializer<?> serializer = (IDataSerializer)vanilla.getByValue(id);
      if (serializer == null) {
         DataSerializerEntry entry = (DataSerializerEntry)((ForgeRegistry)ForgeRegistries.DATA_SERIALIZERS).getValue(id);
         if (entry != null) {
            serializer = entry.getSerializer();
         }
      }

      return serializer;
   }

   public static int getSerializerId(IDataSerializer<?> serializer, IntIdentityHashBiMap<IDataSerializer<?>> vanilla) {
      int id = vanilla.getId(serializer);
      if (id < 0) {
         DataSerializerEntry entry = (DataSerializerEntry)serializerEntries.get(serializer);
         if (entry != null) {
            id = ((ForgeRegistry)ForgeRegistries.DATA_SERIALIZERS).getID((IForgeRegistryEntry)entry);
         }
      }

      return id;
   }

   public static boolean canEntityDestroy(World world, BlockPos pos, LivingEntity entity) {
      BlockState state = world.getBlockState(pos);
      return ForgeEventFactory.getMobGriefingEvent(world, entity) && state.canEntityDestroy(world, pos, entity) && ForgeEventFactory.onEntityDestroyBlock(entity, pos, state);
   }

   public static int getBurnTime(ItemStack stack) {
      if (stack.isEmpty()) {
         return 0;
      } else {
         Item item = stack.getItem();
         int ret = stack.getBurnTime();
         return ForgeEventFactory.getItemBurnTime(stack, ret == -1 ? (Integer)VANILLA_BURNS.getOrDefault(item.delegate, 0) : ret);
      }
   }

   public static synchronized void updateBurns() {
      VANILLA_BURNS.clear();
      FurnaceTileEntity.getBurnTimes().entrySet().forEach((e) -> {
         Integer var10000 = (Integer)VANILLA_BURNS.put(((Item)e.getKey()).delegate, e.getValue());
      });
   }

   private static class OptionalTagEntry<T> extends Tag.TagEntry<T> {
      private Tag<T> resolvedTag = null;

      OptionalTagEntry(ResourceLocation referent) {
         super(referent);
      }

      public boolean resolve(@Nonnull Function<ResourceLocation, Tag<T>> resolver) {
         if (this.resolvedTag == null) {
            this.resolvedTag = (Tag)resolver.apply(this.getSerializedId());
         }

         return true;
      }

      public void populate(@Nonnull Collection<T> items) {
         if (this.resolvedTag != null) {
            items.addAll(this.resolvedTag.getAllElements());
         }

      }
   }

   private static class DummyBlockReader implements IBlockReader {
      private DummyBlockReader() {
      }

      public TileEntity getTileEntity(BlockPos pos) {
         return null;
      }

      public BlockState getBlockState(BlockPos pos) {
         return Blocks.AIR.getDefaultState();
      }

      public IFluidState getFluidState(BlockPos pos) {
         return Fluids.EMPTY.getDefaultState();
      }

      // $FF: synthetic method
      DummyBlockReader(Object x0) {
         this();
      }
   }

   private static class LootTableContext {
      public final ResourceLocation name;
      private final boolean vanilla;
      public final boolean custom;
      public int poolCount;
      public int entryCount;
      private HashSet<String> entryNames;

      private LootTableContext(ResourceLocation name, boolean custom) {
         this.poolCount = 0;
         this.entryCount = 0;
         this.entryNames = Sets.newHashSet();
         this.name = name;
         this.custom = custom;
         this.vanilla = "minecraft".equals(this.name.getNamespace());
      }

      private void resetPoolCtx() {
         this.entryCount = 0;
         this.entryNames.clear();
      }

      public String validateEntryName(@Nullable String name) {
         if (name != null && !this.entryNames.contains(name)) {
            this.entryNames.add(name);
            return name;
         } else if (!this.vanilla) {
            throw new JsonParseException("Loot Table \"" + this.name.toString() + "\" Duplicate entry name \"" + name + "\" for pool #" + (this.poolCount - 1) + " entry #" + (this.entryCount - 1));
         } else {
            int x;
            for(x = 0; this.entryNames.contains(name + "#" + x); ++x) {
            }

            name = name + "#" + x;
            this.entryNames.add(name);
            return name;
         }
      }

      // $FF: synthetic method
      LootTableContext(ResourceLocation x0, boolean x1, Object x2) {
         this(x0, x1);
      }
   }
}
