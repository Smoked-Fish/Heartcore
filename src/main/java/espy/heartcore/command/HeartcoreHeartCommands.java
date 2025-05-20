package espy.heartcore.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import espy.heartcore.util.HeartcoreManager;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class HeartcoreHeartCommands {
    public static LiteralArgumentBuilder<ServerCommandSource> build() {
        return CommandManager.literal("giftheart")
                .then(CommandManager.argument("target", EntityArgumentType.player())
                        .then(CommandManager.argument("amount", IntegerArgumentType.integer(1, 1024))
                                .executes(ctx -> {
                                    ServerPlayerEntity source = ctx.getSource().getPlayer();
                                    ServerPlayerEntity target = EntityArgumentType.getPlayer(ctx, "target");
                                    int amount = IntegerArgumentType.getInteger(ctx, "amount");

                                    if (source == null || !HeartcoreManager.givePlayerHearts(source, target, amount)) {
                                        ctx.getSource().sendError(Text.literal("Heart gift failed."));
                                        return 0;
                                    }

                                    String heartDisplay = getHeartDisplay(amount);
                                    source.sendMessage(Text.literal("You gifted " + heartDisplay + " to " + target.getName().getString()), false);
                                    target.sendMessage(Text.literal(source.getName().getString() + " gifted you " + heartDisplay + "!"), false);

                                    return 1;
                                })));
    }

    private static String getHeartDisplay(int amount) {
        int fullHearts = amount / 2;
        boolean hasHalfHeart = (amount % 2) == 1;

        String heartDisplay;
        if (fullHearts > 0 && hasHalfHeart) {
            heartDisplay = fullHearts + (" and a half hearts");
        } else if (fullHearts > 0) {
            heartDisplay = fullHearts + (fullHearts == 1 ? " heart" : " hearts");
        } else {
            heartDisplay = "half a heart";
        }
        return heartDisplay;
    }

}
