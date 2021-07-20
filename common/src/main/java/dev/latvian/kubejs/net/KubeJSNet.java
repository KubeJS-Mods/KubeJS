package dev.latvian.kubejs.net;

import dev.latvian.kubejs.KubeJS;
import me.shedaniel.architectury.networking.simple.MessageType;
import me.shedaniel.architectury.networking.simple.SimpleNetworkManager;

/**
 * @author LatvianModder
 */
public interface KubeJSNet {
	SimpleNetworkManager NET = SimpleNetworkManager.create(KubeJS.MOD_ID);

	MessageType SEND_DATA_FROM_CLIENT = NET.registerC2S("send_data_from_client", MessageSendDataFromClient::new);
	MessageType SEND_DATA_FROM_SERVER = NET.registerS2C("send_data_from_server", MessageSendDataFromServer::new);
	MessageType OPEN_OVERLAY = NET.registerS2C("open_overlay", MessageOpenOverlay::new);
	MessageType CLOSE_OVERLAY = NET.registerS2C("close_overlay", MessageCloseOverlay::new);
	MessageType ADD_STAGE = NET.registerS2C("add_stage", MessageAddStage::new);
	MessageType REMOVE_STAGE = NET.registerS2C("remove_stage", MessageRemoveStage::new);
	MessageType SYNC_STAGES = NET.registerS2C("sync_stages", MessageSyncStages::new);

	static void init() {
	}
}