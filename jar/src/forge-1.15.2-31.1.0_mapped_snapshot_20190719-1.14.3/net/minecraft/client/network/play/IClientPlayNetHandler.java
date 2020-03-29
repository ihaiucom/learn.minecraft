package net.minecraft.client.network.play;

import net.minecraft.network.INetHandler;
import net.minecraft.network.play.server.SAdvancementInfoPacket;
import net.minecraft.network.play.server.SAnimateBlockBreakPacket;
import net.minecraft.network.play.server.SAnimateHandPacket;
import net.minecraft.network.play.server.SBlockActionPacket;
import net.minecraft.network.play.server.SCameraPacket;
import net.minecraft.network.play.server.SChangeBlockPacket;
import net.minecraft.network.play.server.SChangeGameStatePacket;
import net.minecraft.network.play.server.SChatPacket;
import net.minecraft.network.play.server.SChunkDataPacket;
import net.minecraft.network.play.server.SCloseWindowPacket;
import net.minecraft.network.play.server.SCollectItemPacket;
import net.minecraft.network.play.server.SCombatPacket;
import net.minecraft.network.play.server.SCommandListPacket;
import net.minecraft.network.play.server.SConfirmTransactionPacket;
import net.minecraft.network.play.server.SCooldownPacket;
import net.minecraft.network.play.server.SCustomPayloadPlayPacket;
import net.minecraft.network.play.server.SDestroyEntitiesPacket;
import net.minecraft.network.play.server.SDisconnectPacket;
import net.minecraft.network.play.server.SDisplayObjectivePacket;
import net.minecraft.network.play.server.SEntityEquipmentPacket;
import net.minecraft.network.play.server.SEntityHeadLookPacket;
import net.minecraft.network.play.server.SEntityMetadataPacket;
import net.minecraft.network.play.server.SEntityPacket;
import net.minecraft.network.play.server.SEntityPropertiesPacket;
import net.minecraft.network.play.server.SEntityStatusPacket;
import net.minecraft.network.play.server.SEntityTeleportPacket;
import net.minecraft.network.play.server.SEntityVelocityPacket;
import net.minecraft.network.play.server.SExplosionPacket;
import net.minecraft.network.play.server.SHeldItemChangePacket;
import net.minecraft.network.play.server.SJoinGamePacket;
import net.minecraft.network.play.server.SKeepAlivePacket;
import net.minecraft.network.play.server.SMapDataPacket;
import net.minecraft.network.play.server.SMerchantOffersPacket;
import net.minecraft.network.play.server.SMountEntityPacket;
import net.minecraft.network.play.server.SMoveVehiclePacket;
import net.minecraft.network.play.server.SMultiBlockChangePacket;
import net.minecraft.network.play.server.SOpenBookWindowPacket;
import net.minecraft.network.play.server.SOpenHorseWindowPacket;
import net.minecraft.network.play.server.SOpenSignMenuPacket;
import net.minecraft.network.play.server.SOpenWindowPacket;
import net.minecraft.network.play.server.SPlaceGhostRecipePacket;
import net.minecraft.network.play.server.SPlayEntityEffectPacket;
import net.minecraft.network.play.server.SPlaySoundEffectPacket;
import net.minecraft.network.play.server.SPlaySoundEventPacket;
import net.minecraft.network.play.server.SPlaySoundPacket;
import net.minecraft.network.play.server.SPlayerAbilitiesPacket;
import net.minecraft.network.play.server.SPlayerDiggingPacket;
import net.minecraft.network.play.server.SPlayerListHeaderFooterPacket;
import net.minecraft.network.play.server.SPlayerListItemPacket;
import net.minecraft.network.play.server.SPlayerLookPacket;
import net.minecraft.network.play.server.SPlayerPositionLookPacket;
import net.minecraft.network.play.server.SQueryNBTResponsePacket;
import net.minecraft.network.play.server.SRecipeBookPacket;
import net.minecraft.network.play.server.SRemoveEntityEffectPacket;
import net.minecraft.network.play.server.SRespawnPacket;
import net.minecraft.network.play.server.SScoreboardObjectivePacket;
import net.minecraft.network.play.server.SSelectAdvancementsTabPacket;
import net.minecraft.network.play.server.SSendResourcePackPacket;
import net.minecraft.network.play.server.SServerDifficultyPacket;
import net.minecraft.network.play.server.SSetExperiencePacket;
import net.minecraft.network.play.server.SSetPassengersPacket;
import net.minecraft.network.play.server.SSetSlotPacket;
import net.minecraft.network.play.server.SSpawnExperienceOrbPacket;
import net.minecraft.network.play.server.SSpawnGlobalEntityPacket;
import net.minecraft.network.play.server.SSpawnMobPacket;
import net.minecraft.network.play.server.SSpawnMovingSoundEffectPacket;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.network.play.server.SSpawnPaintingPacket;
import net.minecraft.network.play.server.SSpawnParticlePacket;
import net.minecraft.network.play.server.SSpawnPlayerPacket;
import net.minecraft.network.play.server.SSpawnPositionPacket;
import net.minecraft.network.play.server.SStatisticsPacket;
import net.minecraft.network.play.server.SStopSoundPacket;
import net.minecraft.network.play.server.STabCompletePacket;
import net.minecraft.network.play.server.STagsListPacket;
import net.minecraft.network.play.server.STeamsPacket;
import net.minecraft.network.play.server.STitlePacket;
import net.minecraft.network.play.server.SUnloadChunkPacket;
import net.minecraft.network.play.server.SUpdateBossInfoPacket;
import net.minecraft.network.play.server.SUpdateChunkPositionPacket;
import net.minecraft.network.play.server.SUpdateHealthPacket;
import net.minecraft.network.play.server.SUpdateLightPacket;
import net.minecraft.network.play.server.SUpdateRecipesPacket;
import net.minecraft.network.play.server.SUpdateScorePacket;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.network.play.server.SUpdateTimePacket;
import net.minecraft.network.play.server.SUpdateViewDistancePacket;
import net.minecraft.network.play.server.SWindowItemsPacket;
import net.minecraft.network.play.server.SWindowPropertyPacket;
import net.minecraft.network.play.server.SWorldBorderPacket;

public interface IClientPlayNetHandler extends INetHandler {
   void handleSpawnObject(SSpawnObjectPacket var1);

   void handleSpawnExperienceOrb(SSpawnExperienceOrbPacket var1);

   void handleSpawnGlobalEntity(SSpawnGlobalEntityPacket var1);

   void handleSpawnMob(SSpawnMobPacket var1);

   void handleScoreboardObjective(SScoreboardObjectivePacket var1);

   void handleSpawnPainting(SSpawnPaintingPacket var1);

   void handleSpawnPlayer(SSpawnPlayerPacket var1);

   void handleAnimation(SAnimateHandPacket var1);

   void handleStatistics(SStatisticsPacket var1);

   void handleRecipeBook(SRecipeBookPacket var1);

   void handleBlockBreakAnim(SAnimateBlockBreakPacket var1);

   void handleSignEditorOpen(SOpenSignMenuPacket var1);

   void handleUpdateTileEntity(SUpdateTileEntityPacket var1);

   void handleBlockAction(SBlockActionPacket var1);

   void handleBlockChange(SChangeBlockPacket var1);

   void handleChat(SChatPacket var1);

   void handleMultiBlockChange(SMultiBlockChangePacket var1);

   void handleMaps(SMapDataPacket var1);

   void handleConfirmTransaction(SConfirmTransactionPacket var1);

   void handleCloseWindow(SCloseWindowPacket var1);

   void handleWindowItems(SWindowItemsPacket var1);

   void func_217271_a(SOpenHorseWindowPacket var1);

   void handleWindowProperty(SWindowPropertyPacket var1);

   void handleSetSlot(SSetSlotPacket var1);

   void handleCustomPayload(SCustomPayloadPlayPacket var1);

   void handleDisconnect(SDisconnectPacket var1);

   void handleEntityStatus(SEntityStatusPacket var1);

   void handleEntityAttach(SMountEntityPacket var1);

   void handleSetPassengers(SSetPassengersPacket var1);

   void handleExplosion(SExplosionPacket var1);

   void handleChangeGameState(SChangeGameStatePacket var1);

   void handleKeepAlive(SKeepAlivePacket var1);

   void handleChunkData(SChunkDataPacket var1);

   void processChunkUnload(SUnloadChunkPacket var1);

   void handleEffect(SPlaySoundEventPacket var1);

   void handleJoinGame(SJoinGamePacket var1);

   void handleEntityMovement(SEntityPacket var1);

   void handlePlayerPosLook(SPlayerPositionLookPacket var1);

   void handleParticles(SSpawnParticlePacket var1);

   void handlePlayerAbilities(SPlayerAbilitiesPacket var1);

   void handlePlayerListItem(SPlayerListItemPacket var1);

   void handleDestroyEntities(SDestroyEntitiesPacket var1);

   void handleRemoveEntityEffect(SRemoveEntityEffectPacket var1);

   void handleRespawn(SRespawnPacket var1);

   void handleEntityHeadLook(SEntityHeadLookPacket var1);

   void handleHeldItemChange(SHeldItemChangePacket var1);

   void handleDisplayObjective(SDisplayObjectivePacket var1);

   void handleEntityMetadata(SEntityMetadataPacket var1);

   void handleEntityVelocity(SEntityVelocityPacket var1);

   void handleEntityEquipment(SEntityEquipmentPacket var1);

   void handleSetExperience(SSetExperiencePacket var1);

   void handleUpdateHealth(SUpdateHealthPacket var1);

   void handleTeams(STeamsPacket var1);

   void handleUpdateScore(SUpdateScorePacket var1);

   void handleSpawnPosition(SSpawnPositionPacket var1);

   void handleTimeUpdate(SUpdateTimePacket var1);

   void handleSoundEffect(SPlaySoundEffectPacket var1);

   void func_217266_a(SSpawnMovingSoundEffectPacket var1);

   void handleCustomSound(SPlaySoundPacket var1);

   void handleCollectItem(SCollectItemPacket var1);

   void handleEntityTeleport(SEntityTeleportPacket var1);

   void handleEntityProperties(SEntityPropertiesPacket var1);

   void handleEntityEffect(SPlayEntityEffectPacket var1);

   void handleTags(STagsListPacket var1);

   void handleCombatEvent(SCombatPacket var1);

   void handleServerDifficulty(SServerDifficultyPacket var1);

   void handleCamera(SCameraPacket var1);

   void handleWorldBorder(SWorldBorderPacket var1);

   void handleTitle(STitlePacket var1);

   void handlePlayerListHeaderFooter(SPlayerListHeaderFooterPacket var1);

   void handleResourcePack(SSendResourcePackPacket var1);

   void handleUpdateBossInfo(SUpdateBossInfoPacket var1);

   void handleCooldown(SCooldownPacket var1);

   void handleMoveVehicle(SMoveVehiclePacket var1);

   void handleAdvancementInfo(SAdvancementInfoPacket var1);

   void handleSelectAdvancementsTab(SSelectAdvancementsTabPacket var1);

   void handlePlaceGhostRecipe(SPlaceGhostRecipePacket var1);

   void handleCommandList(SCommandListPacket var1);

   void handleStopSound(SStopSoundPacket var1);

   void handleTabComplete(STabCompletePacket var1);

   void handleUpdateRecipes(SUpdateRecipesPacket var1);

   void handlePlayerLook(SPlayerLookPacket var1);

   void handleNBTQueryResponse(SQueryNBTResponsePacket var1);

   void handleUpdateLight(SUpdateLightPacket var1);

   void func_217268_a(SOpenBookWindowPacket var1);

   void func_217272_a(SOpenWindowPacket var1);

   void func_217273_a(SMerchantOffersPacket var1);

   void func_217270_a(SUpdateViewDistancePacket var1);

   void func_217267_a(SUpdateChunkPositionPacket var1);

   void func_225312_a(SPlayerDiggingPacket var1);
}
