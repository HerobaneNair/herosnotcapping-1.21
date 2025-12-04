package hero.bane.herosnotcapping;

import hero.bane.herosnotcapping.command.HerosNotCappingCommand;
import hero.bane.herosnotcapping.helper.User32Helper;
import hero.bane.herosnotcapping.config.HerosNotCappingConfig;

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

    public static boolean keybindMode = false;
    private static Screen lastScreen = null;

    private static HerosNotCappingConfig config;

    @Override
    public void onInitializeClient() {
        LOGGER.info("HerosNotCapping mod initialized.");

        config = HerosNotCappingConfig.load();
        keybindMode = config.keybindMode;

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
        config.keybindMode = keybindMode;
        config.save();

        String modeText = keybindMode ? "Keybind Mode" : "GUI Mode";

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) {
            client.player.sendMessage(Text.literal("HerosNotCapping is now in " + modeText), false);
        }

        LOGGER.info("Switched to {} mode", modeText);
    }

    private static boolean isCapsLockOn() {
        String os = System.getProperty("os.name").toLowerCase();
        try {
            if (os.contains("win")) {
                return User32Helper.isCapsLockOn();
            } else {
                long handle = MinecraftClient.getInstance().getWindow().getHandle();
                return GLFW.glfwGetKey(handle, GLFW.GLFW_KEY_CAPS_LOCK) == GLFW.GLFW_PRESS;
            }
        } catch (Exception e) {
            LOGGER.error("Failed to read Caps Lock state", e);
            return false;
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
            LOGGER.error("Failed to disable Caps Lock", e);
        }
    }

    private static void disableCapsLockMac() {
        try {
            new ProcessBuilder("osascript", "-e",
                    "tell application \"System Events\" to key code 57").start();
        } catch (Exception e) {
            LOGGER.error("Failed to disable Caps Lock on macOS", e);
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

