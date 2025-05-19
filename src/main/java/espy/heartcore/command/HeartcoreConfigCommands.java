package espy.heartcore.command;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.registry.Registries;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import espy.heartcore.Heartcore;
import espy.heartcore.config.ModConfig;
import me.shedaniel.autoconfig.AutoConfig;

public class HeartcoreConfigCommands {

    private static final SuggestionProvider<ServerCommandSource> ITEM_REGISTRY_SUGGESTIONS = (context, builder) ->
            CommandSource.suggestIdentifiers(Registries.ITEM.getIds(), builder);

    private static final SuggestionProvider<ServerCommandSource> CONFIG_HEALING_ITEMS_SUGGESTIONS = (context, builder) ->
            CommandSource.suggestMatching(Heartcore.CONFIG.healingConfig.healingItems, builder);

    public static LiteralArgumentBuilder<ServerCommandSource> build() {
        return CommandManager.literal("config")
                .then(buildIntSetter("minHearts", 1, 1024, value -> Heartcore.CONFIG.healingConfig.minHearts = value))
                .then(buildIntSetter("maxHearts", 1, 1024, value -> Heartcore.CONFIG.healingConfig.maxHearts = value))
                .then(buildIntSetter("respawnHeartPenalty", 0, 1024, value -> Heartcore.CONFIG.healingConfig.respawnHeartPenalty = value))
                .then(buildBoolSetter("enableItemHealing", value -> Heartcore.CONFIG.healingConfig.enableItemHealing = value))
                .then(buildBoolSetter("randomRespawn", value -> Heartcore.CONFIG.respawningConfig.randomRespawn = value))
                .then(buildIntSetter("maxRadius", 0, 15000000, value -> Heartcore.CONFIG.respawningConfig.maxRadius = value))
                .then(buildIntSetter("minRadius", 0, 15000000, value -> Heartcore.CONFIG.respawningConfig.minRadius = value))
                .then(CommandManager.literal("healingItems")
                        .then(CommandManager.literal("add")
                                .then(CommandManager.argument("value", IdentifierArgumentType.identifier())
                                        .suggests(ITEM_REGISTRY_SUGGESTIONS)
                                        .executes(ctx -> {
                                            Identifier id = IdentifierArgumentType.getIdentifier(ctx, "value");

                                            if (!Registries.ITEM.containsId(id)) {
                                                ctx.getSource().sendError(Text.literal("Invalid item ID: " + id));
                                                return 0;
                                            }

                                            String value = id.toString();
                                            if (!Heartcore.CONFIG.healingConfig.healingItems.contains(value)) {
                                                Heartcore.CONFIG.healingConfig.healingItems.add(value);
                                                saveConfig();
                                                ctx.getSource().sendFeedback(() -> Text.literal("Added " + value + " to healing items."), true);
                                            } else {
                                                ctx.getSource().sendFeedback(() -> Text.literal(value + " is already in the healing items list."), false);
                                            }

                                            return 1;
                                        })))
                        .then(CommandManager.literal("remove")
                                .then(CommandManager.argument("value", IdentifierArgumentType.identifier())
                                        .suggests(CONFIG_HEALING_ITEMS_SUGGESTIONS)
                                        .executes(ctx -> {
                                            String value = IdentifierArgumentType.getIdentifier(ctx, "value").toString();

                                            if (Heartcore.CONFIG.healingConfig.healingItems.remove(value)) {
                                                saveConfig();
                                                ctx.getSource().sendFeedback(() -> Text.literal("Removed " + value + " from healing items."), true);
                                                return 1;
                                            } else {
                                                ctx.getSource().sendError(Text.literal(value + " is not in the healing items list."));
                                                return 0;
                                            }
                                        })))
                );
    }

    private static LiteralArgumentBuilder<ServerCommandSource> buildIntSetter(
            String key, int min, int max, java.util.function.IntConsumer setter) {
        return CommandManager.literal(key)
                .then(CommandManager.literal("set")
                        .then(CommandManager.argument("value", IntegerArgumentType.integer(min, max))
                                .executes(ctx -> {
                                    int value = IntegerArgumentType.getInteger(ctx, "value");
                                    setter.accept(value);
                                    saveConfig(ctx, key, value);
                                    return 1;
                                })));
    }

    private static LiteralArgumentBuilder<ServerCommandSource> buildBoolSetter(
            String key, BooleanConsumer setter) {
        return CommandManager.literal(key)
                .then(CommandManager.literal("set")
                        .then(CommandManager.argument("value", BoolArgumentType.bool())
                                .executes(ctx -> {
                                    boolean value = BoolArgumentType.getBool(ctx, "value");
                                    setter.accept(value);
                                    saveConfig(ctx, key, value);
                                    return 1;
                                })));
    }

    private static void saveConfig(CommandContext<ServerCommandSource> ctx, String key, Object value) {
        AutoConfig.getConfigHolder(ModConfig.class).save();
        ctx.getSource().sendFeedback(() -> Text.literal("Set " + key + " to " + value + "."), true);
    }

    private static void saveConfig() {
        AutoConfig.getConfigHolder(ModConfig.class).save();
    }
}
