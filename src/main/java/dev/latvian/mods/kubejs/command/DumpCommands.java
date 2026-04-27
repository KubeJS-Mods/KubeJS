package dev.latvian.mods.kubejs.command;

import com.google.common.base.Strings;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.latvian.mods.kubejs.KubeJSPaths;
import dev.latvian.mods.kubejs.event.EventGroups;
import dev.latvian.mods.kubejs.script.KubeJSContext;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.util.UtilsJS;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.nio.file.Files;

public class DumpCommands {
	private static final char UNICODE_TICK = '✔';
	private static final char UNICODE_CROSS = '✘';

	public static int events(CommandSourceStack source) {
		var groups = EventGroups.ALL.get().map();

		var output = KubeJSPaths.LOCAL.resolve("event_groups");

		// create a folder for each event group,
		// and a markdown file for each event handler in that group
		// the markdown file should contain:
		// - the event handler name (i.e. ServerEvents.recipes)
		// - the valid script types for that event
		// - a link to the event class on GitHub
		//   (base link is https://github.com/KubeJS-Mods/KubeJS/tree/1902/common/src/main/java/{package}/{class_name}.java,
		//   but we need to replace the package dots with slashes)
		// - a table of all (public, non-transient) fields and (public) methods in the event and their parameters
		// - a space for an example script
		for (var entry : groups.entrySet()) {
			var groupName = entry.getKey();
			var group = entry.getValue();

			var groupFolder = output.resolve(groupName);
			try {
				Files.createDirectories(groupFolder);
				FileUtils.cleanDirectory(groupFolder.toFile());
			} catch (IOException e) {
				ScriptType.SERVER.console.error("Failed to create folder for event group " + groupName, e);
				source.sendFailure(Component.literal("Failed to create folder for event group " + groupName));
				return 0;
			}

			for (var handlerEntry : group.getHandlers().entrySet()) {
				var handlerName = handlerEntry.getKey();
				var handler = handlerEntry.getValue();

				var handlerFile = groupFolder.resolve(handlerName + ".md");

				var fullName = "%s.%s".formatted(groupName, handlerName);

				var eventType = handler.eventType.get();

				var builder = new StringBuilder();

				builder.append("# ").append(fullName).append("\n\n");

				builder.append("## Basic info\n\n");

				builder.append("- Valid script types: ").append(handler.scriptTypePredicate.getValidTypes()).append("\n\n");

				builder.append("- Has result? ").append(handler.getResult() != null ? UNICODE_TICK : UNICODE_CROSS).append("\n\n");

				builder.append("- Event class: ");

				if (eventType.getPackageName().startsWith("dev.latvian.mods.kubejs")) {
					builder.append('[').append(UtilsJS.toMappedTypeString(eventType)).append(']')
						.append('(').append("https://github.com/KubeJS-Mods/KubeJS/tree/main/src/main/java/")
						.append(eventType.getPackageName().replace('.', '/'))
						.append('/').append(eventType.getSimpleName()).append(".java")
						.append(')');
				} else {
					builder.append(UtilsJS.toMappedTypeString(eventType)).append(" (third-party)");
				}

				builder.append("\n\n");

				var infoAnno = eventType.getAnnotation(Info.class);
				if (infoAnno != null) {
					builder.append("```\n")
						.append(infoAnno.value())
						.append("```");
					builder.append("\n\n");
				}

				var scriptManager = source.getServer().getServerResources().managers().kjs$getServerScriptManager();
				var cx = (KubeJSContext) scriptManager.contextFactory.enter();

				var classInfo = cx.getCachedClassStorage(false).get(eventType);

				var hasDocumentedMembers = false;
				var documentedMembers = new StringBuilder("### Documented members:\n\n");

				builder.append("### Available fields:\n\n");
				builder.append("| Name | Type | Static? |\n");
				builder.append("| ---- | ---- | ------- |\n");
				for (var accessible : classInfo.getAccessibleFields(false)) {
					var field = accessible.getInfo();
					if (field.getDeclaringClass().type == Object.class) {
						continue;
					}

					builder.append("| ").append(field.getName()).append(" | ").append(field.getType()).append(" | ");
					builder.append(field.isStatic ? UNICODE_TICK : UNICODE_CROSS).append(" |\n");

					var info = field.getCached().getAnnotation(Info.class);
					if (info != null) {
						hasDocumentedMembers = true;
						documentedMembers.append("- `").append(field.getType()).append(' ').append(accessible.getName()).append("`\n");
						documentedMembers.append("```\n");
						var desc = info.value();
						documentedMembers.append(desc);
						if (!desc.endsWith("\n")) {
							documentedMembers.append("\n");
						}
						documentedMembers.append("```\n\n");
					}
				}

				builder.append("\n").append("Note: Even if no fields are listed above, some methods are still available as fields through *beans*.\n\n");

				builder.append("### Available methods:\n\n");
				builder.append("| Name | Parameters | Return type | Static? |\n");
				builder.append("| ---- | ---------- | ----------- | ------- |\n");
				for (var accessible : classInfo.getAccessibleMethods(false)) {
					var method = accessible.getInfo();
					if (method.getDeclaringClass().type == Object.class) {
						continue;
					}
					builder.append("| ").append(accessible.getName()).append(" | ");
					var params = method.getParameters();

					String[] paramTypes = new String[params.count()];
					for (int i = 0; i < params.count(); i++) {
						paramTypes[i] = params.typeInfos().get(i).toString();
					}
					builder.append(String.join(", ", paramTypes)).append(" | ");

					builder.append(" | ").append(method.getReturnType()).append(" | ");
					builder.append(method.isStatic ? UNICODE_TICK : UNICODE_CROSS).append(" |\n");

					var info = method.getCached().getAnnotation(Info.class);
					if (info != null) {
						hasDocumentedMembers = true;
						documentedMembers.append("- ").append('`');
						if (method.isStatic) {
							documentedMembers.append("static ");
						}
						documentedMembers.append(method.getReturnType()).append(' ').append(accessible.getName()).append('(');

						var namedParams = info.params();
						var paramNames = new String[params.count()];
						var signature = new String[params.count()];
						for (var i = 0; i < params.count(); i++) {
							var name = "var" + i;
							if (namedParams.length > i) {
								var name1 = namedParams[i].name();
								if (!Strings.isNullOrEmpty(name1)) {
									name = name1;
								}
							}
							paramNames[i] = name;
							signature[i] = paramTypes[i] + ' ' + name;
						}

						documentedMembers.append(String.join(", ", signature)).append(')').append('`').append("\n");

						if (params.count() > 0) {
							documentedMembers.append("\n  Parameters:\n");
							for (var i = 0; i < params.count(); i++) {
								documentedMembers.append("  - ")
									.append(paramNames[i])
									.append(": ")
									.append(paramTypes[i])
									.append(namedParams.length > i ? "- " + namedParams[i].value() : "")
									.append("\n");
							}
							documentedMembers.append("\n");
						}

						documentedMembers.append("```\n");
						var desc = info.value();
						documentedMembers.append(desc);
						if (!desc.endsWith("\n")) {
							documentedMembers.append("\n");
						}
						documentedMembers.append("```\n\n");
					}
				}

				builder.append("\n\n");

				if (hasDocumentedMembers) {
					builder.append(documentedMembers).append("\n\n");
				}

				builder.append("### Example script:\n\n");
				builder.append("```js\n");
				builder.append(fullName).append('(');
				if (handler.target != null) {
					builder.append(handler.targetRequired ? "extra_id, " : "/* extra_id (optional), */ ");
				}
				builder.append("(event) => {\n");
				builder.append("\t// This space (un)intentionally left blank\n");
				builder.append("});\n");
				builder.append("```\n\n");

				try {
					Files.writeString(handlerFile, builder.toString());
				} catch (IOException e) {
					ScriptType.SERVER.console.error("Failed to write file for event handler " + fullName, e);
					source.sendFailure(Component.literal("Failed to write file for event handler " + fullName));
					return 0;
				}
			}
		}

		source.sendSystemMessage(Component.literal("Successfully dumped event groups to " + output));
		return 1;
	}

	public static <T> int registry(CommandSourceStack source, ResourceKey<Registry<T>> registry) throws CommandSyntaxException {
		var ids = source.registryAccess().lookup(registry)
			.orElseThrow(() -> KubeJSCommands.NO_REGISTRY.create(registry.identifier()))
			.listElements();

		source.sendSystemMessage(Component.empty());
		source.sendSystemMessage(Component.literal("List of all entries for registry " + registry.identifier() + ":"));
		source.sendSystemMessage(Component.empty());

		var size = ids.map(holder -> {
			var id = holder.key().identifier();
			return Component.literal("- %s".formatted(id)).withStyle(Style.EMPTY
				.withHoverEvent(new HoverEvent.ShowText(Component.literal("%s [%s]".formatted(holder.value(), holder.value().getClass().getName()))))
			);
		}).mapToLong(msg -> {
			source.sendSystemMessage(msg);
			return 1;
		}).sum();

		source.sendSystemMessage(Component.empty());
		source.sendSystemMessage(Component.literal("Total: %d entries".formatted(size)));
		source.sendSystemMessage(Component.empty());

		return 1;
	}


}
