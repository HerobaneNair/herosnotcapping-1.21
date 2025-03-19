package hero.bane.command;

import com.mojang.brigadier.Command;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import hero.bane.HerosNotCapping;

public class HerosNotCappingCommand {

    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommandManager.literal("herosnotcapping")
                    .then(ClientCommandManager.literal("reload")
                            .executes(context -> reloadCapsLockCheck()))
                    .then(ClientCommandManager.literal("testDisable")
                            .executes(context -> {
                                HerosNotCapping.disableCapsLock();
                                return 0;
                            })));
        });
    }

    private static int reloadCapsLockCheck() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) {
            client.player.sendMessage(Text.literal("Reloading Caps Lock keybind check..."), false);
        }
        HerosNotCapping.checkAndDisableCapsLock();
        return Command.SINGLE_SUCCESS;
    }
}
