package hero.bane;

import hero.bane.command.HerosNotCappingCommand;
import hero.bane.helper.User32Helper;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HerosNotCapping implements ClientModInitializer {

	private static final Logger LOGGER = LoggerFactory.getLogger("herosnotcapping");

	//false: GUI Mode, true: Keybind Mode
	private static boolean keybindMode = false;
	private static Screen lastScreen = null;

	@Override
	public void onInitializeClient() {
		LOGGER.info("HerosNotCapping mod initialized.");

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (keybindMode) {
				if (isCapsLockOn()) {
					disableCapsLock();
				}
			} else {
				if (shouldDisableCapsLock(client)) {
					disableCapsLock();
				}
			}
			lastScreen = client.currentScreen;
		});

		HerosNotCappingCommand.register();
	}

	private static boolean shouldDisableCapsLock(MinecraftClient client) {
		return lastScreen == null && client.currentScreen != null;
	}

	public static void toggleMode() {
		keybindMode = !keybindMode;
		String modeText = keybindMode ? "Keybind Mode" : "GUI Mode";

		MinecraftClient client = MinecraftClient.getInstance();
		if (client.player != null) {
			client.player.sendMessage(Text.literal("HerosNotCapping is now in " + modeText), false);
		}

		LOGGER.info("HerosNotCapping switched to {}", modeText);
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
			} else {
				System.out.println("Unknown OS");
			}
		} catch (Exception e) {
			LOGGER.error("Error disabling Caps Lock", e);
		}
	}

	//ChatGPT'ed
	private static void disableCapsLockMac() {
		try {
			new ProcessBuilder("osascript", "-e",
					"tell application \"System Events\" to key code 57").start();
		} catch (Exception e) {
			LOGGER.error("Failed to disable Caps Lock on Mac", e);
		}
	}

	//ChatGPT'ed
	private static void disableCapsLockLinux() {
		try {
			new ProcessBuilder("xdotool", "key", "Caps_Lock").start();
		} catch (Exception e) {
			LOGGER.error("Failed to disable Caps Lock on Linux", e);
		}
	}
}
