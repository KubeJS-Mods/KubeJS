package dev.latvian.kubejs.net;

import dev.latvian.kubejs.KubeJS;
import dev.architectury.architectury.networking.simple.MessageType;
import dev.architectury.architectury.networking.simple.SimpleNetworkManager;

/**
 * @author LatvianModder
 */
public interface KubeJSNet {
	SimpleNetworkManager NET = SimpleNetworkManager.create(KubeJS.MOD_ID);

	MessageType SEND_DATA_FROM_CLIENT = NET.registerC2S("send_data_from_client", SendDataFromClientMessage::new);
	MessageType SEND_DATA_FROM_SERVER = NET.registerS2C("send_data_from_server", SendDataFromServerMessage::new);
	MessageType PAINT = NET.registerS2C("paint", PaintMessage::new);
	MessageType ADD_STAGE = NET.registerS2C("add_stage", AddStageMessage::new);
	MessageType REMOVE_STAGE = NET.registerS2C("remove_stage", RemoveStageMessage::new);
	MessageType SYNC_STAGES = NET.registerS2C("sync_stages", SyncStagesMessage::new);

	static void init() {
	}
}