package net.minecraft.network.play;

import net.minecraft.network.INetHandler;
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

public interface IServerPlayNetHandler extends INetHandler {
   void handleAnimation(CAnimateHandPacket var1);

   void processChatMessage(CChatMessagePacket var1);

   void processClientStatus(CClientStatusPacket var1);

   void processClientSettings(CClientSettingsPacket var1);

   void processConfirmTransaction(CConfirmTransactionPacket var1);

   void processEnchantItem(CEnchantItemPacket var1);

   void processClickWindow(CClickWindowPacket var1);

   void processPlaceRecipe(CPlaceRecipePacket var1);

   void processCloseWindow(CCloseWindowPacket var1);

   void processCustomPayload(CCustomPayloadPacket var1);

   void processUseEntity(CUseEntityPacket var1);

   void processKeepAlive(CKeepAlivePacket var1);

   void processPlayer(CPlayerPacket var1);

   void processPlayerAbilities(CPlayerAbilitiesPacket var1);

   void processPlayerDigging(CPlayerDiggingPacket var1);

   void processEntityAction(CEntityActionPacket var1);

   void processInput(CInputPacket var1);

   void processHeldItemChange(CHeldItemChangePacket var1);

   void processCreativeInventoryAction(CCreativeInventoryActionPacket var1);

   void processUpdateSign(CUpdateSignPacket var1);

   void processTryUseItemOnBlock(CPlayerTryUseItemOnBlockPacket var1);

   void processTryUseItem(CPlayerTryUseItemPacket var1);

   void handleSpectate(CSpectatePacket var1);

   void handleResourcePackStatus(CResourcePackStatusPacket var1);

   void processSteerBoat(CSteerBoatPacket var1);

   void processVehicleMove(CMoveVehiclePacket var1);

   void processConfirmTeleport(CConfirmTeleportPacket var1);

   void handleRecipeBookUpdate(CRecipeInfoPacket var1);

   void handleSeenAdvancements(CSeenAdvancementsPacket var1);

   void processTabComplete(CTabCompletePacket var1);

   void processUpdateCommandBlock(CUpdateCommandBlockPacket var1);

   void processUpdateCommandMinecart(CUpdateMinecartCommandBlockPacket var1);

   void processPickItem(CPickItemPacket var1);

   void processRenameItem(CRenameItemPacket var1);

   void processUpdateBeacon(CUpdateBeaconPacket var1);

   void processUpdateStructureBlock(CUpdateStructureBlockPacket var1);

   void processSelectTrade(CSelectTradePacket var1);

   void processEditBook(CEditBookPacket var1);

   void processNBTQueryEntity(CQueryEntityNBTPacket var1);

   void processNBTQueryBlockEntity(CQueryTileEntityNBTPacket var1);

   void func_217262_a(CUpdateJigsawBlockPacket var1);

   void func_217263_a(CSetDifficultyPacket var1);

   void func_217261_a(CLockDifficultyPacket var1);
}
