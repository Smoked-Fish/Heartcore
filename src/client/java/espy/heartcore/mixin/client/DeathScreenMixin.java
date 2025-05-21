package espy.heartcore.mixin.client;

import espy.heartcore.HeartcoreClient;
import espy.heartcore.util.HeartcoreClientManager;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(DeathScreen.class)
public abstract class DeathScreenMixin extends Screen {
    protected DeathScreenMixin(Text title) {
        super(title);
    }

    @Unique
    private static final Text SPECTATE = Text.translatable("deathScreen.spectate");

    @Inject(method = "init", at = @At("TAIL"))
    private void heartcore$modifyButtons(CallbackInfo ci) {
        if (!HeartcoreClient.isServerHeartcorePresent ) return;
        if (client == null || client.player == null) return;
        if (HeartcoreClientManager.isOutOfLives(client.player)) return;

        // Find and rename the spectator button
        for (var widget : this.children()) {
            if (widget instanceof ButtonWidget button) {
                Text text = button.getMessage();
                if (text.equals(SPECTATE)) {
                    button.setMessage(Text.translatable("deathScreen.respawn"));
                }
            }
        }
    }
}
