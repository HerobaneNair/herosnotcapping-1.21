package hero.bane.command;

import com.mojang.brigadier.Command;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import hero.bane.HerosNotCapping;

public class HerosNotCappingCommand {

    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommandManager.literal("herosnotcapping")
                    .then(ClientCommandManager.literal("toggleMode")
                            .executes(context -> {
                                HerosNotCapping.toggleMode();
                                return Command.SINGLE_SUCCESS;
                            })));
        });
    }
}
