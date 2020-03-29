package net.minecraft.network.play;

import com.google.common.primitives.Doubles;
import com.google.common.primitives.Floats;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import it.unimi.dsi.fastutil.ints.Int2ShortMap;
import it.unimi.dsi.fastutil.ints.Int2ShortOpenHashMap;
import java.util.Collections;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CommandBlockBlock;
import net.minecraft.command.CommandSource;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IJumpingMount;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.entity.player.ChatVisibility;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.inventory.container.BeaconContainer;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.MerchantContainer;
import net.minecraft.inventory.container.RecipeBookContainer;
import net.minecraft.inventory.container.RepairContainer;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.WritableBookItem;
import net.minecraft.item.crafting.ServerRecipeBook;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketThreadUtil;
import net.minecraft.network.play.client.CAnimateHandPacket;
import net.minecraft.network.play.client.CChatMessagePacket;
import net.minecraft.network.play.client.CClickWindowPacket;
import net.minecraft.network.play.client.CClientSettingsPacket;
import net.minecraft.network.play.client.CClientStatusPacket;
import net.minecraft.network.play.client.CCloseWindowPacket;
import net.minecraft.network.play.client.CConfirmTeleportPacket;
import net.minecraft.network.play.client.CConfirmTransactionPacket;
import net.minecraft.network.play.client.CCreativeInventoryActionPacket;
import net.minecraft.network.play.client.CCustomPayloadPacket;
import net.minecraft.network.play.client.CEditBookPacket;
import net.minecraft.network.play.client.CEnchantItemPacket;
import net.minecraft.network.play.client.CEntityActionPacket;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.network.play.client.CInputPacket;
import net.minecraft.network.play.client.CKeepAlivePacket;
import net.minecraft.network.play.client.CLockDifficultyPacket;
import net.minecraft.network.play.client.CMoveVehiclePacket;
import net.minecraft.network.play.client.CPickItemPacket;
import net.minecraft.network.play.client.CPlaceRecipePacket;
import net.minecraft.network.play.client.CPlayerAbilitiesPacket;
import net.minecraft.network.play.client.CPlayerDiggingPacket;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.network.play.client.CPlayerTryUseItemOnBlockPacket;
import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
import net.minecraft.network.play.client.CQueryEntityNBTPacket;
import net.minecraft.network.play.client.CQueryTileEntityNBTPacket;
import net.minecraft.network.play.client.CRecipeInfoPacket;
import net.minecraft.network.play.client.CRenameItemPacket;
import net.minecraft.network.play.client.CResourcePackStatusPacket;
import net.minecraft.network.play.client.CSeenAdvancementsPacket;
import net.minecraft.network.play.client.CSelectTradePacket;
import net.minecraft.network.play.client.CSetDifficultyPacket;
import net.minecraft.network.play.client.CSpectatePacket;
import net.minecraft.network.play.client.CSteerBoatPacket;
import net.minecraft.network.play.client.CTabCompletePacket;
import net.minecraft.network.play.client.CUpdateBeaconPacket;
import net.minecraft.network.play.client.CUpdateCommandBlockPacket;
import net.minecraft.network.play.client.CUpdateJigsawBlockPacket;
import net.minecraft.network.play.client.CUpdateMinecartCommandBlockPacket;
import net.minecraft.network.play.client.CUpdateSignPacket;
import net.minecraft.network.play.client.CUpdateStructureBlockPacket;
import net.minecraft.network.play.client.CUseEntityPacket;
import net.minecraft.network.play.server.SChangeBlockPacket;
import net.minecraft.network.play.server.SChatPacket;
import net.minecraft.network.play.server.SConfirmTransactionPacket;
import net.minecraft.network.play.server.SDisconnectPacket;
import net.minecraft.network.play.server.SHeldItemChangePacket;
import net.minecraft.network.play.server.SKeepAlivePacket;
import net.minecraft.network.play.server.SMoveVehiclePacket;
import net.minecraft.network.play.server.SPlayerPositionLookPacket;
import net.minecraft.network.play.server.SQueryNBTResponsePacket;
import net.minecraft.network.play.server.SSetSlotPacket;
import net.minecraft.network.play.server.STabCompletePacket;
import net.minecraft.potion.Effects;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.CommandBlockLogic;
import net.minecraft.tileentity.CommandBlockTileEntity;
import net.minecraft.tileentity.JigsawTileEntity;
import net.minecraft.tileentity.SignTileEntity;
import net.minecraft.tileentity.StructureBlockTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.StringUtils;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.GameRules;
import net.minecraft.world.GameType;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.network.NetworkHooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerPlayNetHandler implements IServerPlayNetHandler {
   private static final Logger LOGGER = LogManager.getLogger();
   public final NetworkManager netManager;
   private final MinecraftServer server;
   public ServerPlayerEntity player;
   private int networkTickCount;
   private long keepAliveTime;
   private boolean keepAlivePending;
   private long keepAliveKey;
   private int chatSpamThresholdCount;
   private int itemDropThreshold;
   private final Int2ShortMap pendingTransactions = new Int2ShortOpenHashMap();
   private double firstGoodX;
   private double firstGoodY;
   private double firstGoodZ;
   private double lastGoodX;
   private double lastGoodY;
   private double lastGoodZ;
   private Entity lowestRiddenEnt;
   private double lowestRiddenX;
   private double lowestRiddenY;
   private double lowestRiddenZ;
   private double lowestRiddenX1;
   private double lowestRiddenY1;
   private double lowestRiddenZ1;
   private Vec3d targetPos;
   private int teleportId;
   private int lastPositionUpdate;
   private boolean floating;
   private int floatingTickCount;
   private boolean vehicleFloating;
   private int vehicleFloatingTickCount;
   private int movePacketCounter;
   private int lastMovePacketCounter;

   public ServerPlayNetHandler(MinecraftServer p_i1530_1_, NetworkManager p_i1530_2_, ServerPlayerEntity p_i1530_3_) {
      this.server = p_i1530_1_;
      this.netManager = p_i1530_2_;
      p_i1530_2_.setNetHandler(this);
      this.player = p_i1530_3_;
      p_i1530_3_.connection = this;
   }

   public void tick() {
      this.captureCurrentPosition();
      this.player.prevPosX = this.player.func_226277_ct_();
      this.player.prevPosY = this.player.func_226278_cu_();
      this.player.prevPosZ = this.player.func_226281_cx_();
      this.player.playerTick();
      this.player.setPositionAndRotation(this.firstGoodX, this.firstGoodY, this.firstGoodZ, this.player.rotationYaw, this.player.rotationPitch);
      ++this.networkTickCount;
      this.lastMovePacketCounter = this.movePacketCounter;
      if (this.floating) {
         if (++this.floatingTickCount > 80) {
            LOGGER.warn("{} was kicked for floating too long!", this.player.getName().getString());
            this.disconnect(new TranslationTextComponent("multiplayer.disconnect.flying", new Object[0]));
            return;
         }
      } else {
         this.floating = false;
         this.floatingTickCount = 0;
      }

      this.lowestRiddenEnt = this.player.getLowestRidingEntity();
      if (this.lowestRiddenEnt != this.player && this.lowestRiddenEnt.getControllingPassenger() == this.player) {
         this.lowestRiddenX = this.lowestRiddenEnt.func_226277_ct_();
         this.lowestRiddenY = this.lowestRiddenEnt.func_226278_cu_();
         this.lowestRiddenZ = this.lowestRiddenEnt.func_226281_cx_();
         this.lowestRiddenX1 = this.lowestRiddenEnt.func_226277_ct_();
         this.lowestRiddenY1 = this.lowestRiddenEnt.func_226278_cu_();
         this.lowestRiddenZ1 = this.lowestRiddenEnt.func_226281_cx_();
         if (this.vehicleFloating && this.player.getLowestRidingEntity().getControllingPassenger() == this.player) {
            if (++this.vehicleFloatingTickCount > 80) {
               LOGGER.warn("{} was kicked for floating a vehicle too long!", this.player.getName().getString());
               this.disconnect(new TranslationTextComponent("multiplayer.disconnect.flying", new Object[0]));
               return;
            }
         } else {
            this.vehicleFloating = false;
            this.vehicleFloatingTickCount = 0;
         }
      } else {
         this.lowestRiddenEnt = null;
         this.vehicleFloating = false;
         this.vehicleFloatingTickCount = 0;
      }

      this.server.getProfiler().startSection("keepAlive");
      long i = Util.milliTime();
      if (i - this.keepAliveTime >= 15000L) {
         if (this.keepAlivePending) {
            this.disconnect(new TranslationTextComponent("disconnect.timeout", new Object[0]));
         } else {
            this.keepAlivePending = true;
            this.keepAliveTime = i;
            this.keepAliveKey = i;
            this.sendPacket(new SKeepAlivePacket(this.keepAliveKey));
         }
      }

      this.server.getProfiler().endSection();
      if (this.chatSpamThresholdCount > 0) {
         --this.chatSpamThresholdCount;
      }

      if (this.itemDropThreshold > 0) {
         --this.itemDropThreshold;
      }

      if (this.player.getLastActiveTime() > 0L && this.server.getMaxPlayerIdleMinutes() > 0 && Util.milliTime() - this.player.getLastActiveTime() > (long)(this.server.getMaxPlayerIdleMinutes() * 1000 * 60)) {
         this.disconnect(new TranslationTextComponent("multiplayer.disconnect.idling", new Object[0]));
      }

   }

   public void captureCurrentPosition() {
      this.firstGoodX = this.player.func_226277_ct_();
      this.firstGoodY = this.player.func_226278_cu_();
      this.firstGoodZ = this.player.func_226281_cx_();
      this.lastGoodX = this.player.func_226277_ct_();
      this.lastGoodY = this.player.func_226278_cu_();
      this.lastGoodZ = this.player.func_226281_cx_();
   }

   public NetworkManager getNetworkManager() {
      return this.netManager;
   }

   private boolean func_217264_d() {
      return this.server.func_213199_b(this.player.getGameProfile());
   }

   public void disconnect(ITextComponent p_194028_1_) {
      this.netManager.sendPacket(new SDisconnectPacket(p_194028_1_), (p_lambda$disconnect$0_2_) -> {
         this.netManager.closeChannel(p_194028_1_);
      });
      this.netManager.disableAutoRead();
      NetworkManager var10001 = this.netManager;
      this.server.runImmediately(var10001::handleDisconnection);
   }

   public void processInput(CInputPacket p_147358_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147358_1_, this, (ServerWorld)this.player.getServerWorld());
      this.player.setEntityActionState(p_147358_1_.getStrafeSpeed(), p_147358_1_.getForwardSpeed(), p_147358_1_.isJumping(), p_147358_1_.func_229755_e_());
   }

   private static boolean isMovePlayerPacketInvalid(CPlayerPacket p_183006_0_) {
      if (Doubles.isFinite(p_183006_0_.getX(0.0D)) && Doubles.isFinite(p_183006_0_.getY(0.0D)) && Doubles.isFinite(p_183006_0_.getZ(0.0D)) && Floats.isFinite(p_183006_0_.getPitch(0.0F)) && Floats.isFinite(p_183006_0_.getYaw(0.0F))) {
         return Math.abs(p_183006_0_.getX(0.0D)) > 3.0E7D || Math.abs(p_183006_0_.getY(0.0D)) > 3.0E7D || Math.abs(p_183006_0_.getZ(0.0D)) > 3.0E7D;
      } else {
         return true;
      }
   }

   private static boolean isMoveVehiclePacketInvalid(CMoveVehiclePacket p_184341_0_) {
      return !Doubles.isFinite(p_184341_0_.getX()) || !Doubles.isFinite(p_184341_0_.getY()) || !Doubles.isFinite(p_184341_0_.getZ()) || !Floats.isFinite(p_184341_0_.getPitch()) || !Floats.isFinite(p_184341_0_.getYaw());
   }

   public void processVehicleMove(CMoveVehiclePacket p_184338_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_184338_1_, this, (ServerWorld)this.player.getServerWorld());
      if (isMoveVehiclePacketInvalid(p_184338_1_)) {
         this.disconnect(new TranslationTextComponent("multiplayer.disconnect.invalid_vehicle_movement", new Object[0]));
      } else {
         Entity entity = this.player.getLowestRidingEntity();
         if (entity != this.player && entity.getControllingPassenger() == this.player && entity == this.lowestRiddenEnt) {
            ServerWorld serverworld = this.player.getServerWorld();
            double d0 = entity.func_226277_ct_();
            double d1 = entity.func_226278_cu_();
            double d2 = entity.func_226281_cx_();
            double d3 = p_184338_1_.getX();
            double d4 = p_184338_1_.getY();
            double d5 = p_184338_1_.getZ();
            float f = p_184338_1_.getYaw();
            float f1 = p_184338_1_.getPitch();
            double d6 = d3 - this.lowestRiddenX;
            double d7 = d4 - this.lowestRiddenY;
            double d8 = d5 - this.lowestRiddenZ;
            double d9 = entity.getMotion().lengthSquared();
            double d10 = d6 * d6 + d7 * d7 + d8 * d8;
            if (d10 - d9 > 100.0D && !this.func_217264_d()) {
               LOGGER.warn("{} (vehicle of {}) moved too quickly! {},{},{}", entity.getName().getString(), this.player.getName().getString(), d6, d7, d8);
               this.netManager.sendPacket(new SMoveVehiclePacket(entity));
               return;
            }

            boolean flag = serverworld.func_226665_a__(entity, entity.getBoundingBox().shrink(0.0625D));
            d6 = d3 - this.lowestRiddenX1;
            d7 = d4 - this.lowestRiddenY1 - 1.0E-6D;
            d8 = d5 - this.lowestRiddenZ1;
            entity.move(MoverType.PLAYER, new Vec3d(d6, d7, d8));
            d6 = d3 - entity.func_226277_ct_();
            d7 = d4 - entity.func_226278_cu_();
            if (d7 > -0.5D || d7 < 0.5D) {
               d7 = 0.0D;
            }

            d8 = d5 - entity.func_226281_cx_();
            d10 = d6 * d6 + d7 * d7 + d8 * d8;
            boolean flag1 = false;
            if (d10 > 0.0625D) {
               flag1 = true;
               LOGGER.warn("{} moved wrongly!", entity.getName().getString());
            }

            entity.setPositionAndRotation(d3, d4, d5, f, f1);
            this.player.setPositionAndRotation(d3, d4, d5, this.player.rotationYaw, this.player.rotationPitch);
            boolean flag2 = serverworld.func_226665_a__(entity, entity.getBoundingBox().shrink(0.0625D));
            if (flag && (flag1 || !flag2)) {
               entity.setPositionAndRotation(d0, d1, d2, f, f1);
               this.player.setPositionAndRotation(d3, d4, d5, this.player.rotationYaw, this.player.rotationPitch);
               this.netManager.sendPacket(new SMoveVehiclePacket(entity));
               return;
            }

            this.player.getServerWorld().getChunkProvider().updatePlayerPosition(this.player);
            this.player.addMovementStat(this.player.func_226277_ct_() - d0, this.player.func_226278_cu_() - d1, this.player.func_226281_cx_() - d2);
            this.vehicleFloating = d7 >= -0.03125D && !this.server.isFlightAllowed() && !serverworld.checkBlockCollision(entity.getBoundingBox().grow(0.0625D).expand(0.0D, -0.55D, 0.0D));
            this.lowestRiddenX1 = entity.func_226277_ct_();
            this.lowestRiddenY1 = entity.func_226278_cu_();
            this.lowestRiddenZ1 = entity.func_226281_cx_();
         }
      }

   }

   public void processConfirmTeleport(CConfirmTeleportPacket p_184339_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_184339_1_, this, (ServerWorld)this.player.getServerWorld());
      if (p_184339_1_.getTeleportId() == this.teleportId) {
         this.player.setPositionAndRotation(this.targetPos.x, this.targetPos.y, this.targetPos.z, this.player.rotationYaw, this.player.rotationPitch);
         this.lastGoodX = this.targetPos.x;
         this.lastGoodY = this.targetPos.y;
         this.lastGoodZ = this.targetPos.z;
         if (this.player.isInvulnerableDimensionChange()) {
            this.player.clearInvulnerableDimensionChange();
         }

         this.targetPos = null;
      }

   }

   public void handleRecipeBookUpdate(CRecipeInfoPacket p_191984_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_191984_1_, this, (ServerWorld)this.player.getServerWorld());
      if (p_191984_1_.getPurpose() == CRecipeInfoPacket.Purpose.SHOWN) {
         Optional var10000 = this.server.getRecipeManager().getRecipe(p_191984_1_.getRecipeId());
         ServerRecipeBook var10001 = this.player.getRecipeBook();
         var10000.ifPresent(var10001::markSeen);
      } else if (p_191984_1_.getPurpose() == CRecipeInfoPacket.Purpose.SETTINGS) {
         this.player.getRecipeBook().setGuiOpen(p_191984_1_.isGuiOpen());
         this.player.getRecipeBook().setFilteringCraftable(p_191984_1_.isFilteringCraftable());
         this.player.getRecipeBook().setFurnaceGuiOpen(p_191984_1_.isFurnaceGuiOpen());
         this.player.getRecipeBook().setFurnaceFilteringCraftable(p_191984_1_.isFurnaceFilteringCraftable());
         this.player.getRecipeBook().func_216755_e(p_191984_1_.func_218779_h());
         this.player.getRecipeBook().func_216756_f(p_191984_1_.func_218778_i());
         this.player.getRecipeBook().func_216757_g(p_191984_1_.func_218780_j());
         this.player.getRecipeBook().func_216760_h(p_191984_1_.func_218781_k());
      }

   }

   public void handleSeenAdvancements(CSeenAdvancementsPacket p_194027_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_194027_1_, this, (ServerWorld)this.player.getServerWorld());
      if (p_194027_1_.getAction() == CSeenAdvancementsPacket.Action.OPENED_TAB) {
         ResourceLocation resourcelocation = p_194027_1_.getTab();
         Advancement advancement = this.server.getAdvancementManager().getAdvancement(resourcelocation);
         if (advancement != null) {
            this.player.getAdvancements().setSelectedTab(advancement);
         }
      }

   }

   public void processTabComplete(CTabCompletePacket p_195518_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_195518_1_, this, (ServerWorld)this.player.getServerWorld());
      StringReader stringreader = new StringReader(p_195518_1_.getCommand());
      if (stringreader.canRead() && stringreader.peek() == '/') {
         stringreader.skip();
      }

      ParseResults<CommandSource> parseresults = this.server.getCommandManager().getDispatcher().parse(stringreader, this.player.getCommandSource());
      this.server.getCommandManager().getDispatcher().getCompletionSuggestions(parseresults).thenAccept((p_lambda$processTabComplete$1_2_) -> {
         this.netManager.sendPacket(new STabCompletePacket(p_195518_1_.getTransactionId(), p_lambda$processTabComplete$1_2_));
      });
   }

   public void processUpdateCommandBlock(CUpdateCommandBlockPacket p_210153_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_210153_1_, this, (ServerWorld)this.player.getServerWorld());
      if (!this.server.isCommandBlockEnabled()) {
         this.player.sendMessage(new TranslationTextComponent("advMode.notEnabled", new Object[0]));
      } else if (!this.player.canUseCommandBlock()) {
         this.player.sendMessage(new TranslationTextComponent("advMode.notAllowed", new Object[0]));
      } else {
         CommandBlockLogic commandblocklogic = null;
         CommandBlockTileEntity commandblocktileentity = null;
         BlockPos blockpos = p_210153_1_.getPos();
         TileEntity tileentity = this.player.world.getTileEntity(blockpos);
         if (tileentity instanceof CommandBlockTileEntity) {
            commandblocktileentity = (CommandBlockTileEntity)tileentity;
            commandblocklogic = commandblocktileentity.getCommandBlockLogic();
         }

         String s = p_210153_1_.getCommand();
         boolean flag = p_210153_1_.shouldTrackOutput();
         if (commandblocklogic != null) {
            CommandBlockTileEntity.Mode commandblocktileentity$mode = commandblocktileentity.getMode();
            Direction direction = (Direction)this.player.world.getBlockState(blockpos).get(CommandBlockBlock.FACING);
            switch(p_210153_1_.getMode()) {
            case SEQUENCE:
               BlockState blockstate1 = Blocks.CHAIN_COMMAND_BLOCK.getDefaultState();
               this.player.world.setBlockState(blockpos, (BlockState)((BlockState)blockstate1.with(CommandBlockBlock.FACING, direction)).with(CommandBlockBlock.CONDITIONAL, p_210153_1_.isConditional()), 2);
               break;
            case AUTO:
               BlockState blockstate = Blocks.REPEATING_COMMAND_BLOCK.getDefaultState();
               this.player.world.setBlockState(blockpos, (BlockState)((BlockState)blockstate.with(CommandBlockBlock.FACING, direction)).with(CommandBlockBlock.CONDITIONAL, p_210153_1_.isConditional()), 2);
               break;
            case REDSTONE:
            default:
               BlockState blockstate2 = Blocks.COMMAND_BLOCK.getDefaultState();
               this.player.world.setBlockState(blockpos, (BlockState)((BlockState)blockstate2.with(CommandBlockBlock.FACING, direction)).with(CommandBlockBlock.CONDITIONAL, p_210153_1_.isConditional()), 2);
            }

            tileentity.validate();
            this.player.world.setTileEntity(blockpos, tileentity);
            commandblocklogic.setCommand(s);
            commandblocklogic.setTrackOutput(flag);
            if (!flag) {
               commandblocklogic.setLastOutput((ITextComponent)null);
            }

            commandblocktileentity.setAuto(p_210153_1_.isAuto());
            if (commandblocktileentity$mode != p_210153_1_.getMode()) {
               commandblocktileentity.func_226987_h_();
            }

            commandblocklogic.updateCommand();
            if (!StringUtils.isNullOrEmpty(s)) {
               this.player.sendMessage(new TranslationTextComponent("advMode.setCommand.success", new Object[]{s}));
            }
         }
      }

   }

   public void processUpdateCommandMinecart(CUpdateMinecartCommandBlockPacket p_210158_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_210158_1_, this, (ServerWorld)this.player.getServerWorld());
      if (!this.server.isCommandBlockEnabled()) {
         this.player.sendMessage(new TranslationTextComponent("advMode.notEnabled", new Object[0]));
      } else if (!this.player.canUseCommandBlock()) {
         this.player.sendMessage(new TranslationTextComponent("advMode.notAllowed", new Object[0]));
      } else {
         CommandBlockLogic commandblocklogic = p_210158_1_.getCommandBlock(this.player.world);
         if (commandblocklogic != null) {
            commandblocklogic.setCommand(p_210158_1_.getCommand());
            commandblocklogic.setTrackOutput(p_210158_1_.shouldTrackOutput());
            if (!p_210158_1_.shouldTrackOutput()) {
               commandblocklogic.setLastOutput((ITextComponent)null);
            }

            commandblocklogic.updateCommand();
            this.player.sendMessage(new TranslationTextComponent("advMode.setCommand.success", new Object[]{p_210158_1_.getCommand()}));
         }
      }

   }

   public void processPickItem(CPickItemPacket p_210152_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_210152_1_, this, (ServerWorld)this.player.getServerWorld());
      this.player.inventory.pickItem(p_210152_1_.getPickIndex());
      this.player.connection.sendPacket(new SSetSlotPacket(-2, this.player.inventory.currentItem, this.player.inventory.getStackInSlot(this.player.inventory.currentItem)));
      this.player.connection.sendPacket(new SSetSlotPacket(-2, p_210152_1_.getPickIndex(), this.player.inventory.getStackInSlot(p_210152_1_.getPickIndex())));
      this.player.connection.sendPacket(new SHeldItemChangePacket(this.player.inventory.currentItem));
   }

   public void processRenameItem(CRenameItemPacket p_210155_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_210155_1_, this, (ServerWorld)this.player.getServerWorld());
      if (this.player.openContainer instanceof RepairContainer) {
         RepairContainer repaircontainer = (RepairContainer)this.player.openContainer;
         String s = SharedConstants.filterAllowedCharacters(p_210155_1_.getName());
         if (s.length() <= 35) {
            repaircontainer.updateItemName(s);
         }
      }

   }

   public void processUpdateBeacon(CUpdateBeaconPacket p_210154_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_210154_1_, this, (ServerWorld)this.player.getServerWorld());
      if (this.player.openContainer instanceof BeaconContainer) {
         ((BeaconContainer)this.player.openContainer).func_216966_c(p_210154_1_.getPrimaryEffect(), p_210154_1_.getSecondaryEffect());
      }

   }

   public void processUpdateStructureBlock(CUpdateStructureBlockPacket p_210157_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_210157_1_, this, (ServerWorld)this.player.getServerWorld());
      if (this.player.canUseCommandBlock()) {
         BlockPos blockpos = p_210157_1_.getPos();
         BlockState blockstate = this.player.world.getBlockState(blockpos);
         TileEntity tileentity = this.player.world.getTileEntity(blockpos);
         if (tileentity instanceof StructureBlockTileEntity) {
            StructureBlockTileEntity structureblocktileentity = (StructureBlockTileEntity)tileentity;
            structureblocktileentity.setMode(p_210157_1_.getMode());
            structureblocktileentity.setName(p_210157_1_.getName());
            structureblocktileentity.setPosition(p_210157_1_.getPosition());
            structureblocktileentity.setSize(p_210157_1_.getSize());
            structureblocktileentity.setMirror(p_210157_1_.getMirror());
            structureblocktileentity.setRotation(p_210157_1_.getRotation());
            structureblocktileentity.setMetadata(p_210157_1_.getMetadata());
            structureblocktileentity.setIgnoresEntities(p_210157_1_.shouldIgnoreEntities());
            structureblocktileentity.setShowAir(p_210157_1_.shouldShowAir());
            structureblocktileentity.setShowBoundingBox(p_210157_1_.shouldShowBoundingBox());
            structureblocktileentity.setIntegrity(p_210157_1_.getIntegrity());
            structureblocktileentity.setSeed(p_210157_1_.getSeed());
            if (structureblocktileentity.hasName()) {
               String s = structureblocktileentity.getName();
               if (p_210157_1_.func_210384_b() == StructureBlockTileEntity.UpdateCommand.SAVE_AREA) {
                  if (structureblocktileentity.save()) {
                     this.player.sendStatusMessage(new TranslationTextComponent("structure_block.save_success", new Object[]{s}), false);
                  } else {
                     this.player.sendStatusMessage(new TranslationTextComponent("structure_block.save_failure", new Object[]{s}), false);
                  }
               } else if (p_210157_1_.func_210384_b() == StructureBlockTileEntity.UpdateCommand.LOAD_AREA) {
                  if (!structureblocktileentity.isStructureLoadable()) {
                     this.player.sendStatusMessage(new TranslationTextComponent("structure_block.load_not_found", new Object[]{s}), false);
                  } else if (structureblocktileentity.load()) {
                     this.player.sendStatusMessage(new TranslationTextComponent("structure_block.load_success", new Object[]{s}), false);
                  } else {
                     this.player.sendStatusMessage(new TranslationTextComponent("structure_block.load_prepare", new Object[]{s}), false);
                  }
               } else if (p_210157_1_.func_210384_b() == StructureBlockTileEntity.UpdateCommand.SCAN_AREA) {
                  if (structureblocktileentity.detectSize()) {
                     this.player.sendStatusMessage(new TranslationTextComponent("structure_block.size_success", new Object[]{s}), false);
                  } else {
                     this.player.sendStatusMessage(new TranslationTextComponent("structure_block.size_failure", new Object[0]), false);
                  }
               }
            } else {
               this.player.sendStatusMessage(new TranslationTextComponent("structure_block.invalid_structure_name", new Object[]{p_210157_1_.getName()}), false);
            }

            structureblocktileentity.markDirty();
            this.player.world.notifyBlockUpdate(blockpos, blockstate, blockstate, 3);
         }
      }

   }

   public void func_217262_a(CUpdateJigsawBlockPacket p_217262_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_217262_1_, this, (ServerWorld)this.player.getServerWorld());
      if (this.player.canUseCommandBlock()) {
         BlockPos blockpos = p_217262_1_.func_218789_b();
         BlockState blockstate = this.player.world.getBlockState(blockpos);
         TileEntity tileentity = this.player.world.getTileEntity(blockpos);
         if (tileentity instanceof JigsawTileEntity) {
            JigsawTileEntity jigsawtileentity = (JigsawTileEntity)tileentity;
            jigsawtileentity.setAttachmentType(p_217262_1_.func_218787_d());
            jigsawtileentity.setTargetPool(p_217262_1_.func_218786_c());
            jigsawtileentity.setFinalState(p_217262_1_.func_218788_e());
            jigsawtileentity.markDirty();
            this.player.world.notifyBlockUpdate(blockpos, blockstate, blockstate, 3);
         }
      }

   }

   public void processSelectTrade(CSelectTradePacket p_210159_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_210159_1_, this, (ServerWorld)this.player.getServerWorld());
      int i = p_210159_1_.func_210353_a();
      Container container = this.player.openContainer;
      if (container instanceof MerchantContainer) {
         MerchantContainer merchantcontainer = (MerchantContainer)container;
         merchantcontainer.setCurrentRecipeIndex(i);
         merchantcontainer.func_217046_g(i);
      }

   }

   public void processEditBook(CEditBookPacket p_210156_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_210156_1_, this, (ServerWorld)this.player.getServerWorld());
      ItemStack itemstack = p_210156_1_.getStack();
      if (!itemstack.isEmpty() && WritableBookItem.isNBTValid(itemstack.getTag())) {
         ItemStack itemstack1 = this.player.getHeldItem(p_210156_1_.getHand());
         if (itemstack.getItem() == Items.WRITABLE_BOOK && itemstack1.getItem() == Items.WRITABLE_BOOK) {
            if (p_210156_1_.shouldUpdateAll()) {
               ItemStack itemstack2 = new ItemStack(Items.WRITTEN_BOOK);
               CompoundNBT compoundnbt = itemstack1.getTag();
               if (compoundnbt != null) {
                  itemstack2.setTag(compoundnbt.copy());
               }

               itemstack2.setTagInfo("author", StringNBT.func_229705_a_(this.player.getName().getString()));
               itemstack2.setTagInfo("title", StringNBT.func_229705_a_(itemstack.getTag().getString("title")));
               ListNBT listnbt = itemstack.getTag().getList("pages", 8);

               for(int i = 0; i < listnbt.size(); ++i) {
                  String s = listnbt.getString(i);
                  ITextComponent itextcomponent = new StringTextComponent(s);
                  s = ITextComponent.Serializer.toJson(itextcomponent);
                  listnbt.set(i, (INBT)StringNBT.func_229705_a_(s));
               }

               itemstack2.setTagInfo("pages", listnbt);
               this.player.setHeldItem(p_210156_1_.getHand(), itemstack2);
            } else {
               itemstack1.setTagInfo("pages", itemstack.getTag().getList("pages", 8));
            }
         }
      }

   }

   public void processNBTQueryEntity(CQueryEntityNBTPacket p_211526_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_211526_1_, this, (ServerWorld)this.player.getServerWorld());
      if (this.player.hasPermissionLevel(2)) {
         Entity entity = this.player.getServerWorld().getEntityByID(p_211526_1_.getEntityId());
         if (entity != null) {
            CompoundNBT compoundnbt = entity.writeWithoutTypeId(new CompoundNBT());
            this.player.connection.sendPacket(new SQueryNBTResponsePacket(p_211526_1_.getTransactionId(), compoundnbt));
         }
      }

   }

   public void processNBTQueryBlockEntity(CQueryTileEntityNBTPacket p_211525_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_211525_1_, this, (ServerWorld)this.player.getServerWorld());
      if (this.player.hasPermissionLevel(2)) {
         TileEntity tileentity = this.player.getServerWorld().getTileEntity(p_211525_1_.getPosition());
         CompoundNBT compoundnbt = tileentity != null ? tileentity.write(new CompoundNBT()) : null;
         this.player.connection.sendPacket(new SQueryNBTResponsePacket(p_211525_1_.getTransactionId(), compoundnbt));
      }

   }

   public void processPlayer(CPlayerPacket p_147347_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147347_1_, this, (ServerWorld)this.player.getServerWorld());
      if (isMovePlayerPacketInvalid(p_147347_1_)) {
         this.disconnect(new TranslationTextComponent("multiplayer.disconnect.invalid_player_movement", new Object[0]));
      } else {
         ServerWorld serverworld = this.server.getWorld(this.player.dimension);
         if (!this.player.queuedEndExit) {
            if (this.networkTickCount == 0) {
               this.captureCurrentPosition();
            }

            if (this.targetPos != null) {
               if (this.networkTickCount - this.lastPositionUpdate > 20) {
                  this.lastPositionUpdate = this.networkTickCount;
                  this.setPlayerLocation(this.targetPos.x, this.targetPos.y, this.targetPos.z, this.player.rotationYaw, this.player.rotationPitch);
               }
            } else {
               this.lastPositionUpdate = this.networkTickCount;
               if (this.player.isPassenger()) {
                  this.player.setPositionAndRotation(this.player.func_226277_ct_(), this.player.func_226278_cu_(), this.player.func_226281_cx_(), p_147347_1_.getYaw(this.player.rotationYaw), p_147347_1_.getPitch(this.player.rotationPitch));
                  this.player.getServerWorld().getChunkProvider().updatePlayerPosition(this.player);
               } else {
                  double d0 = this.player.func_226277_ct_();
                  double d1 = this.player.func_226278_cu_();
                  double d2 = this.player.func_226281_cx_();
                  double d3 = this.player.func_226278_cu_();
                  double d4 = p_147347_1_.getX(this.player.func_226277_ct_());
                  double d5 = p_147347_1_.getY(this.player.func_226278_cu_());
                  double d6 = p_147347_1_.getZ(this.player.func_226281_cx_());
                  float f = p_147347_1_.getYaw(this.player.rotationYaw);
                  float f1 = p_147347_1_.getPitch(this.player.rotationPitch);
                  double d7 = d4 - this.firstGoodX;
                  double d8 = d5 - this.firstGoodY;
                  double d9 = d6 - this.firstGoodZ;
                  double d10 = this.player.getMotion().lengthSquared();
                  double d11 = d7 * d7 + d8 * d8 + d9 * d9;
                  if (this.player.isSleeping()) {
                     if (d11 > 1.0D) {
                        this.setPlayerLocation(this.player.func_226277_ct_(), this.player.func_226278_cu_(), this.player.func_226281_cx_(), p_147347_1_.getYaw(this.player.rotationYaw), p_147347_1_.getPitch(this.player.rotationPitch));
                     }
                  } else {
                     ++this.movePacketCounter;
                     int i = this.movePacketCounter - this.lastMovePacketCounter;
                     if (i > 5) {
                        LOGGER.debug("{} is sending move packets too frequently ({} packets since last tick)", this.player.getName().getString(), i);
                        i = 1;
                     }

                     if (!this.player.isInvulnerableDimensionChange() && (!this.player.getServerWorld().getGameRules().getBoolean(GameRules.DISABLE_ELYTRA_MOVEMENT_CHECK) || !this.player.isElytraFlying())) {
                        float f2 = this.player.isElytraFlying() ? 300.0F : 100.0F;
                        if (d11 - d10 > (double)(f2 * (float)i) && !this.func_217264_d()) {
                           LOGGER.warn("{} moved too quickly! {},{},{}", this.player.getName().getString(), d7, d8, d9);
                           this.setPlayerLocation(this.player.func_226277_ct_(), this.player.func_226278_cu_(), this.player.func_226281_cx_(), this.player.rotationYaw, this.player.rotationPitch);
                           return;
                        }
                     }

                     boolean flag2 = this.func_223133_a(serverworld);
                     d7 = d4 - this.lastGoodX;
                     d8 = d5 - this.lastGoodY;
                     d9 = d6 - this.lastGoodZ;
                     if (d8 > 0.0D) {
                        this.player.fallDistance = 0.0F;
                     }

                     if (this.player.onGround && !p_147347_1_.isOnGround() && d8 > 0.0D) {
                        this.player.jump();
                     }

                     this.player.move(MoverType.PLAYER, new Vec3d(d7, d8, d9));
                     this.player.onGround = p_147347_1_.isOnGround();
                     d7 = d4 - this.player.func_226277_ct_();
                     d8 = d5 - this.player.func_226278_cu_();
                     if (d8 > -0.5D || d8 < 0.5D) {
                        d8 = 0.0D;
                     }

                     d9 = d6 - this.player.func_226281_cx_();
                     d11 = d7 * d7 + d8 * d8 + d9 * d9;
                     boolean flag = false;
                     if (!this.player.isInvulnerableDimensionChange() && d11 > 0.0625D && !this.player.isSleeping() && !this.player.interactionManager.isCreative() && this.player.interactionManager.getGameType() != GameType.SPECTATOR) {
                        flag = true;
                        LOGGER.warn("{} moved wrongly!", this.player.getName().getString());
                     }

                     this.player.setPositionAndRotation(d4, d5, d6, f, f1);
                     this.player.addMovementStat(this.player.func_226277_ct_() - d0, this.player.func_226278_cu_() - d1, this.player.func_226281_cx_() - d2);
                     if (!this.player.noClip && !this.player.isSleeping()) {
                        boolean flag1 = this.func_223133_a(serverworld);
                        if (flag2 && (flag || !flag1)) {
                           this.setPlayerLocation(d0, d1, d2, f, f1);
                           return;
                        }
                     }

                     this.floating = d8 >= -0.03125D && this.player.interactionManager.getGameType() != GameType.SPECTATOR && !this.server.isFlightAllowed() && !this.player.abilities.allowFlying && !this.player.isPotionActive(Effects.LEVITATION) && !this.player.isElytraFlying() && !serverworld.checkBlockCollision(this.player.getBoundingBox().grow(0.0625D).expand(0.0D, -0.55D, 0.0D));
                     this.player.onGround = p_147347_1_.isOnGround();
                     this.player.getServerWorld().getChunkProvider().updatePlayerPosition(this.player);
                     this.player.handleFalling(this.player.func_226278_cu_() - d3, p_147347_1_.isOnGround());
                     this.lastGoodX = this.player.func_226277_ct_();
                     this.lastGoodY = this.player.func_226278_cu_();
                     this.lastGoodZ = this.player.func_226281_cx_();
                  }
               }
            }
         }
      }

   }

   private boolean func_223133_a(IWorldReader p_223133_1_) {
      return p_223133_1_.func_226665_a__(this.player, this.player.getBoundingBox().shrink(9.999999747378752E-6D));
   }

   public void setPlayerLocation(double p_147364_1_, double p_147364_3_, double p_147364_5_, float p_147364_7_, float p_147364_8_) {
      this.setPlayerLocation(p_147364_1_, p_147364_3_, p_147364_5_, p_147364_7_, p_147364_8_, Collections.emptySet());
   }

   public void setPlayerLocation(double p_175089_1_, double p_175089_3_, double p_175089_5_, float p_175089_7_, float p_175089_8_, Set<SPlayerPositionLookPacket.Flags> p_175089_9_) {
      double d0 = p_175089_9_.contains(SPlayerPositionLookPacket.Flags.X) ? this.player.func_226277_ct_() : 0.0D;
      double d1 = p_175089_9_.contains(SPlayerPositionLookPacket.Flags.Y) ? this.player.func_226278_cu_() : 0.0D;
      double d2 = p_175089_9_.contains(SPlayerPositionLookPacket.Flags.Z) ? this.player.func_226281_cx_() : 0.0D;
      float f = p_175089_9_.contains(SPlayerPositionLookPacket.Flags.Y_ROT) ? this.player.rotationYaw : 0.0F;
      float f1 = p_175089_9_.contains(SPlayerPositionLookPacket.Flags.X_ROT) ? this.player.rotationPitch : 0.0F;
      this.targetPos = new Vec3d(p_175089_1_, p_175089_3_, p_175089_5_);
      if (++this.teleportId == Integer.MAX_VALUE) {
         this.teleportId = 0;
      }

      this.lastPositionUpdate = this.networkTickCount;
      this.player.setPositionAndRotation(p_175089_1_, p_175089_3_, p_175089_5_, p_175089_7_, p_175089_8_);
      this.player.connection.sendPacket(new SPlayerPositionLookPacket(p_175089_1_ - d0, p_175089_3_ - d1, p_175089_5_ - d2, p_175089_7_ - f, p_175089_8_ - f1, p_175089_9_, this.teleportId));
   }

   public void processPlayerDigging(CPlayerDiggingPacket p_147345_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147345_1_, this, (ServerWorld)this.player.getServerWorld());
      BlockPos blockpos = p_147345_1_.getPosition();
      this.player.markPlayerActive();
      CPlayerDiggingPacket.Action cplayerdiggingpacket$action = p_147345_1_.getAction();
      switch(cplayerdiggingpacket$action) {
      case SWAP_HELD_ITEMS:
         if (!this.player.isSpectator()) {
            ItemStack itemstack = this.player.getHeldItem(Hand.OFF_HAND);
            this.player.setHeldItem(Hand.OFF_HAND, this.player.getHeldItem(Hand.MAIN_HAND));
            this.player.setHeldItem(Hand.MAIN_HAND, itemstack);
         }

         return;
      case DROP_ITEM:
         if (!this.player.isSpectator()) {
            this.player.func_225609_n_(false);
         }

         return;
      case DROP_ALL_ITEMS:
         if (!this.player.isSpectator()) {
            this.player.func_225609_n_(true);
         }

         return;
      case RELEASE_USE_ITEM:
         this.player.stopActiveHand();
         return;
      case START_DESTROY_BLOCK:
      case ABORT_DESTROY_BLOCK:
      case STOP_DESTROY_BLOCK:
         this.player.interactionManager.func_225416_a(blockpos, cplayerdiggingpacket$action, p_147345_1_.getFacing(), this.server.getBuildLimit());
         return;
      default:
         throw new IllegalArgumentException("Invalid player action");
      }
   }

   public void processTryUseItemOnBlock(CPlayerTryUseItemOnBlockPacket p_184337_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_184337_1_, this, (ServerWorld)this.player.getServerWorld());
      ServerWorld serverworld = this.server.getWorld(this.player.dimension);
      Hand hand = p_184337_1_.getHand();
      ItemStack itemstack = this.player.getHeldItem(hand);
      BlockRayTraceResult blockraytraceresult = p_184337_1_.func_218794_c();
      BlockPos blockpos = blockraytraceresult.getPos();
      Direction direction = blockraytraceresult.getFace();
      this.player.markPlayerActive();
      if (blockpos.getY() >= this.server.getBuildLimit() - 1 && (direction == Direction.UP || blockpos.getY() >= this.server.getBuildLimit())) {
         ITextComponent itextcomponent = (new TranslationTextComponent("build.tooHigh", new Object[]{this.server.getBuildLimit()})).applyTextStyle(TextFormatting.RED);
         this.player.connection.sendPacket(new SChatPacket(itextcomponent, ChatType.GAME_INFO));
      } else {
         double dist = this.player.getAttribute(PlayerEntity.REACH_DISTANCE).getValue() + 3.0D;
         dist *= dist;
         if (this.targetPos == null && this.player.getDistanceSq((double)blockpos.getX() + 0.5D, (double)blockpos.getY() + 0.5D, (double)blockpos.getZ() + 0.5D) < dist && serverworld.isBlockModifiable(this.player, blockpos)) {
            ActionResultType actionresulttype = this.player.interactionManager.func_219441_a(this.player, serverworld, itemstack, hand, blockraytraceresult);
            if (actionresulttype.func_226247_b_()) {
               this.player.func_226292_a_(hand, true);
            }
         }
      }

      this.player.connection.sendPacket(new SChangeBlockPacket(serverworld, blockpos));
      this.player.connection.sendPacket(new SChangeBlockPacket(serverworld, blockpos.offset(direction)));
   }

   public void processTryUseItem(CPlayerTryUseItemPacket p_147346_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147346_1_, this, (ServerWorld)this.player.getServerWorld());
      ServerWorld serverworld = this.server.getWorld(this.player.dimension);
      Hand hand = p_147346_1_.getHand();
      ItemStack itemstack = this.player.getHeldItem(hand);
      this.player.markPlayerActive();
      if (!itemstack.isEmpty()) {
         this.player.interactionManager.processRightClick(this.player, serverworld, itemstack, hand);
      }

   }

   public void handleSpectate(CSpectatePacket p_175088_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_175088_1_, this, (ServerWorld)this.player.getServerWorld());
      if (this.player.isSpectator()) {
         Iterator var2 = this.server.getWorlds().iterator();

         while(var2.hasNext()) {
            ServerWorld serverworld = (ServerWorld)var2.next();
            Entity entity = p_175088_1_.getEntity(serverworld);
            if (entity != null) {
               this.player.teleport(serverworld, entity.func_226277_ct_(), entity.func_226278_cu_(), entity.func_226281_cx_(), entity.rotationYaw, entity.rotationPitch);
               return;
            }
         }
      }

   }

   public void handleResourcePackStatus(CResourcePackStatusPacket p_175086_1_) {
   }

   public void processSteerBoat(CSteerBoatPacket p_184340_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_184340_1_, this, (ServerWorld)this.player.getServerWorld());
      Entity entity = this.player.getRidingEntity();
      if (entity instanceof BoatEntity) {
         ((BoatEntity)entity).setPaddleState(p_184340_1_.getLeft(), p_184340_1_.getRight());
      }

   }

   public void onDisconnect(ITextComponent p_147231_1_) {
      LOGGER.info("{} lost connection: {}", this.player.getName().getString(), p_147231_1_.getString());
      this.server.refreshStatusNextTick();
      this.server.getPlayerList().sendMessage((new TranslationTextComponent("multiplayer.player.left", new Object[]{this.player.getDisplayName()})).applyTextStyle(TextFormatting.YELLOW));
      this.player.disconnect();
      this.server.getPlayerList().playerLoggedOut(this.player);
      if (this.func_217264_d()) {
         LOGGER.info("Stopping singleplayer server as player logged out");
         this.server.initiateShutdown(false);
      }

   }

   public void sendPacket(IPacket<?> p_147359_1_) {
      this.sendPacket(p_147359_1_, (GenericFutureListener)null);
   }

   public void sendPacket(IPacket<?> p_211148_1_, @Nullable GenericFutureListener<? extends Future<? super Void>> p_211148_2_) {
      if (p_211148_1_ instanceof SChatPacket) {
         SChatPacket schatpacket = (SChatPacket)p_211148_1_;
         ChatVisibility chatvisibility = this.player.getChatVisibility();
         if (chatvisibility == ChatVisibility.HIDDEN && schatpacket.getType() != ChatType.GAME_INFO) {
            return;
         }

         if (chatvisibility == ChatVisibility.SYSTEM && !schatpacket.isSystem()) {
            return;
         }
      }

      try {
         this.netManager.sendPacket(p_211148_1_, p_211148_2_);
      } catch (Throwable var6) {
         CrashReport crashreport = CrashReport.makeCrashReport(var6, "Sending packet");
         CrashReportCategory crashreportcategory = crashreport.makeCategory("Packet being sent");
         crashreportcategory.addDetail("Packet class", () -> {
            return p_211148_1_.getClass().getCanonicalName();
         });
         throw new ReportedException(crashreport);
      }
   }

   public void processHeldItemChange(CHeldItemChangePacket p_147355_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147355_1_, this, (ServerWorld)this.player.getServerWorld());
      if (p_147355_1_.getSlotId() >= 0 && p_147355_1_.getSlotId() < PlayerInventory.getHotbarSize()) {
         this.player.inventory.currentItem = p_147355_1_.getSlotId();
         this.player.markPlayerActive();
      } else {
         LOGGER.warn("{} tried to set an invalid carried item", this.player.getName().getString());
      }

   }

   public void processChatMessage(CChatMessagePacket p_147354_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147354_1_, this, (ServerWorld)this.player.getServerWorld());
      if (this.player.getChatVisibility() == ChatVisibility.HIDDEN) {
         this.sendPacket(new SChatPacket((new TranslationTextComponent("chat.cannotSend", new Object[0])).applyTextStyle(TextFormatting.RED)));
      } else {
         this.player.markPlayerActive();
         String s = p_147354_1_.getMessage();
         s = org.apache.commons.lang3.StringUtils.normalizeSpace(s);

         for(int i = 0; i < s.length(); ++i) {
            if (!SharedConstants.isAllowedCharacter(s.charAt(i))) {
               this.disconnect(new TranslationTextComponent("multiplayer.disconnect.illegal_characters", new Object[0]));
               return;
            }
         }

         if (s.startsWith("/")) {
            this.handleSlashCommand(s);
         } else {
            ITextComponent itextcomponent = new TranslationTextComponent("chat.type.text", new Object[]{this.player.getDisplayName(), ForgeHooks.newChatWithLinks(s)});
            ITextComponent itextcomponent = ForgeHooks.onServerChatEvent(this, s, itextcomponent);
            if (itextcomponent == null) {
               return;
            }

            this.server.getPlayerList().sendMessage(itextcomponent, false);
         }

         this.chatSpamThresholdCount += 20;
         if (this.chatSpamThresholdCount > 200 && !this.server.getPlayerList().canSendCommands(this.player.getGameProfile())) {
            this.disconnect(new TranslationTextComponent("disconnect.spam", new Object[0]));
         }
      }

   }

   private void handleSlashCommand(String p_147361_1_) {
      this.server.getCommandManager().handleCommand(this.player.getCommandSource(), p_147361_1_);
   }

   public void handleAnimation(CAnimateHandPacket p_175087_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_175087_1_, this, (ServerWorld)this.player.getServerWorld());
      this.player.markPlayerActive();
      this.player.swingArm(p_175087_1_.getHand());
   }

   public void processEntityAction(CEntityActionPacket p_147357_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147357_1_, this, (ServerWorld)this.player.getServerWorld());
      this.player.markPlayerActive();
      IJumpingMount ijumpingmount1;
      switch(p_147357_1_.getAction()) {
      case PRESS_SHIFT_KEY:
         this.player.func_226284_e_(true);
         break;
      case RELEASE_SHIFT_KEY:
         this.player.func_226284_e_(false);
         break;
      case START_SPRINTING:
         this.player.setSprinting(true);
         break;
      case STOP_SPRINTING:
         this.player.setSprinting(false);
         break;
      case STOP_SLEEPING:
         if (this.player.isSleeping()) {
            this.player.func_225652_a_(false, true);
            this.targetPos = this.player.getPositionVec();
         }
         break;
      case START_RIDING_JUMP:
         if (this.player.getRidingEntity() instanceof IJumpingMount) {
            ijumpingmount1 = (IJumpingMount)this.player.getRidingEntity();
            int i = p_147357_1_.getAuxData();
            if (ijumpingmount1.canJump() && i > 0) {
               ijumpingmount1.handleStartJump(i);
            }
         }
         break;
      case STOP_RIDING_JUMP:
         if (this.player.getRidingEntity() instanceof IJumpingMount) {
            ijumpingmount1 = (IJumpingMount)this.player.getRidingEntity();
            ijumpingmount1.handleStopJump();
         }
         break;
      case OPEN_INVENTORY:
         if (this.player.getRidingEntity() instanceof AbstractHorseEntity) {
            ((AbstractHorseEntity)this.player.getRidingEntity()).openGUI(this.player);
         }
         break;
      case START_FALL_FLYING:
         if (!this.player.func_226566_ei_()) {
            this.player.func_226568_ek_();
         }
         break;
      default:
         throw new IllegalArgumentException("Invalid client command!");
      }

   }

   public void processUseEntity(CUseEntityPacket p_147340_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147340_1_, this, (ServerWorld)this.player.getServerWorld());
      ServerWorld serverworld = this.server.getWorld(this.player.dimension);
      Entity entity = p_147340_1_.getEntityFromWorld(serverworld);
      this.player.markPlayerActive();
      if (entity != null) {
         boolean flag = this.player.canEntityBeSeen(entity);
         double d0 = 36.0D;
         if (!flag) {
            d0 = 9.0D;
         }

         if (this.player.getDistanceSq(entity) < d0) {
            Hand hand1;
            if (p_147340_1_.getAction() == CUseEntityPacket.Action.INTERACT) {
               hand1 = p_147340_1_.getHand();
               this.player.interactOn(entity, hand1);
            } else if (p_147340_1_.getAction() == CUseEntityPacket.Action.INTERACT_AT) {
               hand1 = p_147340_1_.getHand();
               if (ForgeHooks.onInteractEntityAt(this.player, entity, (Vec3d)p_147340_1_.getHitVec(), hand1) != null) {
                  return;
               }

               ActionResultType actionresulttype = entity.applyPlayerInteraction(this.player, p_147340_1_.getHitVec(), hand1);
               if (actionresulttype.func_226247_b_()) {
                  this.player.func_226292_a_(hand1, true);
               }
            } else if (p_147340_1_.getAction() == CUseEntityPacket.Action.ATTACK) {
               if (entity instanceof ItemEntity || entity instanceof ExperienceOrbEntity || entity instanceof AbstractArrowEntity || entity == this.player) {
                  this.disconnect(new TranslationTextComponent("multiplayer.disconnect.invalid_entity_attacked", new Object[0]));
                  this.server.logWarning("Player " + this.player.getName().getString() + " tried to attack an invalid entity");
                  return;
               }

               this.player.attackTargetEntityWithCurrentItem(entity);
            }
         }
      }

   }

   public void processClientStatus(CClientStatusPacket p_147342_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147342_1_, this, (ServerWorld)this.player.getServerWorld());
      this.player.markPlayerActive();
      CClientStatusPacket.State cclientstatuspacket$state = p_147342_1_.getStatus();
      switch(cclientstatuspacket$state) {
      case PERFORM_RESPAWN:
         if (this.player.queuedEndExit) {
            this.player.queuedEndExit = false;
            this.player = this.server.getPlayerList().recreatePlayerEntity(this.player, DimensionType.OVERWORLD, true);
            CriteriaTriggers.CHANGED_DIMENSION.trigger(this.player, DimensionType.THE_END, DimensionType.OVERWORLD);
         } else {
            if (this.player.getHealth() > 0.0F) {
               return;
            }

            this.player = this.server.getPlayerList().recreatePlayerEntity(this.player, this.player.dimension, false);
            if (this.server.isHardcore()) {
               this.player.setGameType(GameType.SPECTATOR);
               ((GameRules.BooleanValue)this.player.getServerWorld().getGameRules().get(GameRules.SPECTATORS_GENERATE_CHUNKS)).set(false, this.server);
            }
         }
         break;
      case REQUEST_STATS:
         this.player.getStats().sendStats(this.player);
      }

   }

   public void processCloseWindow(CCloseWindowPacket p_147356_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147356_1_, this, (ServerWorld)this.player.getServerWorld());
      this.player.closeContainer();
   }

   public void processClickWindow(CClickWindowPacket p_147351_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147351_1_, this, (ServerWorld)this.player.getServerWorld());
      this.player.markPlayerActive();
      if (this.player.openContainer.windowId == p_147351_1_.getWindowId() && this.player.openContainer.getCanCraft(this.player)) {
         if (this.player.isSpectator()) {
            NonNullList<ItemStack> nonnulllist = NonNullList.create();

            for(int i = 0; i < this.player.openContainer.inventorySlots.size(); ++i) {
               nonnulllist.add(((Slot)this.player.openContainer.inventorySlots.get(i)).getStack());
            }

            this.player.sendAllContents(this.player.openContainer, nonnulllist);
         } else {
            ItemStack itemstack1 = this.player.openContainer.slotClick(p_147351_1_.getSlotId(), p_147351_1_.getUsedButton(), p_147351_1_.getClickType(), this.player);
            if (ItemStack.areItemStacksEqual(p_147351_1_.getClickedItem(), itemstack1)) {
               this.player.connection.sendPacket(new SConfirmTransactionPacket(p_147351_1_.getWindowId(), p_147351_1_.getActionNumber(), true));
               this.player.isChangingQuantityOnly = true;
               this.player.openContainer.detectAndSendChanges();
               this.player.updateHeldItem();
               this.player.isChangingQuantityOnly = false;
            } else {
               this.pendingTransactions.put(this.player.openContainer.windowId, p_147351_1_.getActionNumber());
               this.player.connection.sendPacket(new SConfirmTransactionPacket(p_147351_1_.getWindowId(), p_147351_1_.getActionNumber(), false));
               this.player.openContainer.setCanCraft(this.player, false);
               NonNullList<ItemStack> nonnulllist1 = NonNullList.create();

               for(int j = 0; j < this.player.openContainer.inventorySlots.size(); ++j) {
                  ItemStack itemstack = ((Slot)this.player.openContainer.inventorySlots.get(j)).getStack();
                  nonnulllist1.add(itemstack.isEmpty() ? ItemStack.EMPTY : itemstack);
               }

               this.player.sendAllContents(this.player.openContainer, nonnulllist1);
            }
         }
      }

   }

   public void processPlaceRecipe(CPlaceRecipePacket p_194308_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_194308_1_, this, (ServerWorld)this.player.getServerWorld());
      this.player.markPlayerActive();
      if (!this.player.isSpectator() && this.player.openContainer.windowId == p_194308_1_.getWindowId() && this.player.openContainer.getCanCraft(this.player) && this.player.openContainer instanceof RecipeBookContainer) {
         this.server.getRecipeManager().getRecipe(p_194308_1_.getRecipeId()).ifPresent((p_lambda$processPlaceRecipe$3_2_) -> {
            ((RecipeBookContainer)this.player.openContainer).func_217056_a(p_194308_1_.shouldPlaceAll(), p_lambda$processPlaceRecipe$3_2_, this.player);
         });
      }

   }

   public void processEnchantItem(CEnchantItemPacket p_147338_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147338_1_, this, (ServerWorld)this.player.getServerWorld());
      this.player.markPlayerActive();
      if (this.player.openContainer.windowId == p_147338_1_.getWindowId() && this.player.openContainer.getCanCraft(this.player) && !this.player.isSpectator()) {
         this.player.openContainer.enchantItem(this.player, p_147338_1_.getButton());
         this.player.openContainer.detectAndSendChanges();
      }

   }

   public void processCreativeInventoryAction(CCreativeInventoryActionPacket p_147344_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147344_1_, this, (ServerWorld)this.player.getServerWorld());
      if (this.player.interactionManager.isCreative()) {
         boolean flag = p_147344_1_.getSlotId() < 0;
         ItemStack itemstack = p_147344_1_.getStack();
         CompoundNBT compoundnbt = itemstack.getChildTag("BlockEntityTag");
         if (!itemstack.isEmpty() && compoundnbt != null && compoundnbt.contains("x") && compoundnbt.contains("y") && compoundnbt.contains("z")) {
            BlockPos blockpos = new BlockPos(compoundnbt.getInt("x"), compoundnbt.getInt("y"), compoundnbt.getInt("z"));
            TileEntity tileentity = this.player.world.getTileEntity(blockpos);
            if (tileentity != null) {
               CompoundNBT compoundnbt1 = tileentity.write(new CompoundNBT());
               compoundnbt1.remove("x");
               compoundnbt1.remove("y");
               compoundnbt1.remove("z");
               itemstack.setTagInfo("BlockEntityTag", compoundnbt1);
            }
         }

         boolean flag1 = p_147344_1_.getSlotId() >= 1 && p_147344_1_.getSlotId() <= 45;
         boolean flag2 = itemstack.isEmpty() || itemstack.getDamage() >= 0 && itemstack.getCount() <= 64 && !itemstack.isEmpty();
         if (flag1 && flag2) {
            if (itemstack.isEmpty()) {
               this.player.container.putStackInSlot(p_147344_1_.getSlotId(), ItemStack.EMPTY);
            } else {
               this.player.container.putStackInSlot(p_147344_1_.getSlotId(), itemstack);
            }

            this.player.container.setCanCraft(this.player, true);
            this.player.container.detectAndSendChanges();
         } else if (flag && flag2 && this.itemDropThreshold < 200) {
            this.itemDropThreshold += 20;
            this.player.dropItem(itemstack, true);
         }
      }

   }

   public void processConfirmTransaction(CConfirmTransactionPacket p_147339_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147339_1_, this, (ServerWorld)this.player.getServerWorld());
      int i = this.player.openContainer.windowId;
      if (i == p_147339_1_.getWindowId() && this.pendingTransactions.getOrDefault(i, (short)(p_147339_1_.getUid() + 1)) == p_147339_1_.getUid() && !this.player.openContainer.getCanCraft(this.player) && !this.player.isSpectator()) {
         this.player.openContainer.setCanCraft(this.player, true);
      }

   }

   public void processUpdateSign(CUpdateSignPacket p_147343_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147343_1_, this, (ServerWorld)this.player.getServerWorld());
      this.player.markPlayerActive();
      ServerWorld serverworld = this.server.getWorld(this.player.dimension);
      BlockPos blockpos = p_147343_1_.getPosition();
      if (serverworld.isBlockLoaded(blockpos)) {
         BlockState blockstate = serverworld.getBlockState(blockpos);
         TileEntity tileentity = serverworld.getTileEntity(blockpos);
         if (!(tileentity instanceof SignTileEntity)) {
            return;
         }

         SignTileEntity signtileentity = (SignTileEntity)tileentity;
         if (!signtileentity.getIsEditable() || signtileentity.getPlayer() != this.player) {
            this.server.logWarning("Player " + this.player.getName().getString() + " just tried to change non-editable sign");
            return;
         }

         String[] astring = p_147343_1_.getLines();

         for(int i = 0; i < astring.length; ++i) {
            signtileentity.setText(i, new StringTextComponent(TextFormatting.getTextWithoutFormattingCodes(astring[i])));
         }

         signtileentity.markDirty();
         serverworld.notifyBlockUpdate(blockpos, blockstate, blockstate, 3);
      }

   }

   public void processKeepAlive(CKeepAlivePacket p_147353_1_) {
      if (this.keepAlivePending && p_147353_1_.getKey() == this.keepAliveKey) {
         int i = (int)(Util.milliTime() - this.keepAliveTime);
         this.player.ping = (this.player.ping * 3 + i) / 4;
         this.keepAlivePending = false;
      } else if (!this.func_217264_d()) {
         this.disconnect(new TranslationTextComponent("disconnect.timeout", new Object[0]));
      }

   }

   public void processPlayerAbilities(CPlayerAbilitiesPacket p_147348_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147348_1_, this, (ServerWorld)this.player.getServerWorld());
      this.player.abilities.isFlying = p_147348_1_.isFlying() && this.player.abilities.allowFlying;
   }

   public void processClientSettings(CClientSettingsPacket p_147352_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147352_1_, this, (ServerWorld)this.player.getServerWorld());
      this.player.handleClientSettings(p_147352_1_);
   }

   public void processCustomPayload(CCustomPayloadPacket p_147349_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_147349_1_, this, (ServerWorld)this.player.getServerWorld());
      NetworkHooks.onCustomPayload(p_147349_1_, this.netManager);
   }

   public void func_217263_a(CSetDifficultyPacket p_217263_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_217263_1_, this, (ServerWorld)this.player.getServerWorld());
      if (this.player.hasPermissionLevel(2) || this.func_217264_d()) {
         this.server.setDifficultyForAllWorlds(p_217263_1_.func_218773_b(), false);
      }

   }

   public void func_217261_a(CLockDifficultyPacket p_217261_1_) {
      PacketThreadUtil.checkThreadAndEnqueue(p_217261_1_, this, (ServerWorld)this.player.getServerWorld());
      if (this.player.hasPermissionLevel(2) || this.func_217264_d()) {
         this.server.setDifficultyLocked(p_217261_1_.func_218776_b());
      }

   }
}
