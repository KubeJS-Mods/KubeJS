package dev.latvian.mods.kubejs.net;

import dev.architectury.networking.simple.MessageType;
import dev.architectury.networking.simple.SimpleNetworkManager;
import dev.latvian.mods.kubejs.KubeJS;

public interface KubeJSNet {
	SimpleNetworkManager NET = SimpleNetworkManager.create(KubeJS.MOD_ID);

	MessageType SEND_DATA_FROM_CLIENT = NET.registerC2S("send_data_from_client", SendDataFromClientMessage::new);
	MessageType SEND_DATA_FROM_SERVER = NET.registerS2C("send_data_from_server", SendDataFromServerMessage::new);
	MessageType PAINT = NET.registerS2C("paint", PaintMessage::new);
	MessageType ADD_STAGE = NET.registerS2C("add_stage", AddStageMessage::new);
	MessageType REMOVE_STAGE = NET.registerS2C("remove_stage", RemoveStageMessage::new);
	MessageType SYNC_STAGES = NET.registerS2C("sync_stages", SyncStagesMessage::new);
	MessageType FIRST_CLICK = NET.registerC2S("first_click", FirstClickMessage::new);
	MessageType NOTIFICATION = NET.registerS2C("toast", NotificationMessage::new);
	MessageType RELOAD_STARTUP_SCRIPTS = NET.registerS2C("reload_startup_scripts", ReloadStartupScriptsMessage::new);
	MessageType DISPLAY_SERVER_ERRORS = NET.registerS2C("display_server_errors", DisplayServerErrorsMessage::new);
	MessageType DISPLAY_CLIENT_ERRORS = NET.registerS2C("display_client_errors", DisplayClientErrorsMessage::new);

	static void init() {
	}
}