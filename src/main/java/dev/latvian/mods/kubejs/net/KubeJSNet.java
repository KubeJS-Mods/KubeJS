package dev.latvian.mods.kubejs.net;

import dev.latvian.mods.kubejs.KubeJS;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

@EventBusSubscriber(modid = KubeJS.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public interface KubeJSNet {
	private static <T extends CustomPacketPayload> CustomPacketPayload.Type<T> type(String id) {
		return new CustomPacketPayload.Type<>(KubeJS.id(id));
	}

	CustomPacketPayload.Type<SendDataFromClientPayload> SEND_DATA_FROM_CLIENT = type("send_data_from_client");
	CustomPacketPayload.Type<SendDataFromServerPayload> SEND_DATA_FROM_SERVER = type("send_data_from_server");
	CustomPacketPayload.Type<AddStagePayload> ADD_STAGE = type("add_stage");
	CustomPacketPayload.Type<RemoveStagePayload> REMOVE_STAGE = type("remove_stage");
	CustomPacketPayload.Type<SyncStagesPayload> SYNC_STAGES = type("sync_stages");
	CustomPacketPayload.Type<FirstClickPayload> FIRST_CLICK = type("first_click");
	CustomPacketPayload.Type<NotificationPayload> NOTIFICATION = type("toast");
	CustomPacketPayload.Type<ReloadStartupScriptsPayload> RELOAD_STARTUP_SCRIPTS = type("reload_startup_scripts");
	CustomPacketPayload.Type<DisplayServerErrorsPayload> DISPLAY_SERVER_ERRORS = type("display_server_errors");
	CustomPacketPayload.Type<DisplayClientErrorsPayload> DISPLAY_CLIENT_ERRORS = type("display_client_errors");
	CustomPacketPayload.Type<RequestItemKubedexPayload> REQUEST_ITEM_KUBEDEX = type("request_item_kubedex");
	CustomPacketPayload.Type<RequestBlockKubedexPayload> REQUEST_BLOCK_KUBEDEX = type("request_block_kubedex");
	CustomPacketPayload.Type<RequestEntityKubedexPayload> REQUEST_ENTITY_KUBEDEX = type("request_entity_kubedex");

	@SubscribeEvent
	static void register(RegisterPayloadHandlersEvent event) {
		var reg = event.registrar("1");

		// PacketDistributor.sendToClient(player, packet)

		reg.playToServer(SEND_DATA_FROM_CLIENT, SendDataFromClientPayload.STREAM_CODEC, SendDataFromClientPayload::handle);
		reg.playToClient(SEND_DATA_FROM_SERVER, SendDataFromServerPayload.STREAM_CODEC, SendDataFromServerPayload::handle);
		reg.playToClient(ADD_STAGE, AddStagePayload.STREAM_CODEC, AddStagePayload::handle);
		reg.playToClient(REMOVE_STAGE, RemoveStagePayload.STREAM_CODEC, RemoveStagePayload::handle);
		reg.playToClient(SYNC_STAGES, SyncStagesPayload.STREAM_CODEC, SyncStagesPayload::handle);
		reg.playToServer(FIRST_CLICK, FirstClickPayload.STREAM_CODEC, FirstClickPayload::handle);
		reg.playToServer(NOTIFICATION, NotificationPayload.STREAM_CODEC, NotificationPayload::handle);
		reg.playToClient(RELOAD_STARTUP_SCRIPTS, ReloadStartupScriptsPayload.STREAM_CODEC, ReloadStartupScriptsPayload::handle);
		reg.playToClient(DISPLAY_SERVER_ERRORS, DisplayServerErrorsPayload.STREAM_CODEC, DisplayServerErrorsPayload::handle);
		reg.playToClient(DISPLAY_CLIENT_ERRORS, DisplayClientErrorsPayload.STREAM_CODEC, DisplayClientErrorsPayload::handle);
		reg.playToServer(REQUEST_ITEM_KUBEDEX, RequestItemKubedexPayload.STREAM_CODEC, RequestItemKubedexPayload::handle);
		reg.playToServer(REQUEST_BLOCK_KUBEDEX, RequestBlockKubedexPayload.STREAM_CODEC, RequestBlockKubedexPayload::handle);
		reg.playToServer(REQUEST_ENTITY_KUBEDEX, RequestEntityKubedexPayload.STREAM_CODEC, RequestEntityKubedexPayload::handle);
	}
}