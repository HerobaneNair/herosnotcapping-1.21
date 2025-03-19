package hero.bane;

import hero.bane.command.HerosNotCappingCommand;
import hero.bane.helper.User32Helper;
import hero.bane.mixin.BoundKeyAccessor;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public class HerosNotCapping implements ClientModInitializer {

	private static final Logger LOGGER = LoggerFactory.getLogger("herosnotcapping");
	private static boolean hasCheckedKeybinds = false;

	@Override
	public void onInitializeClient() {
		LOGGER.info("HerosNotCapping mod initialized.");

		// Wait until the client is fully loaded to check keybinds
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (!hasCheckedKeybinds && client.options != null) {
				checkAndDisableCapsLock();
				hasCheckedKeybinds = true;
			}

			if (isCapsLockOn()) {
				disableCapsLock();
			}
		});

		HerosNotCappingCommand.register();
	}

	public static void checkAndDisableCapsLock() {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client.options == null) return;

		for (KeyBinding keyBind : client.options.allKeys) {
			InputUtil.Key key = ((BoundKeyAccessor) keyBind).getBoundKey();

			if (key.getCode() == GLFW.GLFW_KEY_CAPS_LOCK) { // 280 = Caps Lock
				LOGGER.info("Detected Caps Lock assigned to keybind: {}, disabling Caps Lock...", keyBind.getTranslationKey());
				disableCapsLock();
				return;
			}
		}
	}

	private static boolean isCapsLockOn() {
		String os = System.getProperty("os.name").toLowerCase();
		if (os.contains("win")) {
			return User32Helper.isCapsLockOn();
		} else {
			long windowHandle = MinecraftClient.getInstance().getWindow().getHandle();
			return GLFW.glfwGetKey(windowHandle, GLFW.GLFW_KEY_CAPS_LOCK) == GLFW.GLFW_PRESS;
		}
	}

	public static void disableCapsLock() {
		try {
			String os = System.getProperty("os.name").toLowerCase();
			if (os.contains("win")) {
				User32Helper.disableCapsLock();
			} else if (os.contains("mac")) {
				disableCapsLockMac();
			} else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
				disableCapsLockLinux();
			}
		} catch (Exception e) {
			LOGGER.error("Error disabling Caps Lock", e);
		}
	}

	private static void disableCapsLockMac() {
		try {
			new ProcessBuilder("osascript", "-e",
					"tell application \"System Events\" to key code 57").start();
		} catch (Exception e) {
			LOGGER.error("Failed to disable Caps Lock on Mac", e);
		}
	}

	private static void disableCapsLockLinux() {
		try {
			new ProcessBuilder("xdotool", "key", "Caps_Lock").start();
		} catch (Exception e) {
			LOGGER.error("Failed to disable Caps Lock on Linux", e);
		}
	}
}
