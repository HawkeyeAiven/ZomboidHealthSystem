package aiven.zomboidhealthsystem.gui.screen;

import aiven.zomboidhealthsystem.Config;
import aiven.zomboidhealthsystem.ZomboidHealthSystem;
import aiven.zomboidhealthsystem.ZomboidHealthSystemClient;
import aiven.zomboidhealthsystem.gui.widget.HudWidgetGroup;
import aiven.zomboidhealthsystem.gui.widget.ModMenuWidget;

import net.minecraft.network.chat.Component;

public class ModScreen extends AbstractModScreen {

    private static ModMenuWidget modMenuWidget;

    public ModScreen() {
        super(Component.literal(ZomboidHealthSystem.NAME));

        if(modMenuWidget == null) {
            modMenuWidget = new ModMenuWidget(100, 75) {
                @Override
                protected void onCloseButton() {
                    onClose();
                }
            };
        }

        addRenderableWidget(modMenuWidget);
        addRenderableWidget(ZomboidHealthSystemClient.getHud());
    }

    @Override
    public void onClose() {
        Config config = Config.getInstance();
        HudWidgetGroup hud = ZomboidHealthSystemClient.getHud();

        config.time_hud_xpos = hud.getTimeWidget().getX();
        config.time_hud_ypos = hud.getTimeWidget().getY();

        config.health_hud_xpos = hud.getBodyWidget().getX();
        config.health_hud_ypos = hud.getBodyWidget().getY();

        config.moodles_hud_ypos = hud.getMoodlesWidget().getY();

        super.onClose();
    }
}
