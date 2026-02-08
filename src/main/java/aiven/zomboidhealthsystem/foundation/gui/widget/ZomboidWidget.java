package aiven.zomboidhealthsystem.foundation.gui.widget;

import aiven.zomboidhealthsystem.ZomboidHealthSystem;
import aiven.zomboidhealthsystem.ZomboidHealthSystemClient;
import aiven.zomboidhealthsystem.foundation.client.ClientHealth;
import aiven.zomboidhealthsystem.foundation.gui.screen.UIHealth;
import aiven.zomboidhealthsystem.foundation.player.Health;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class ZomboidWidget extends AbstractModScrollableWidget {
    public ButtonElement buttonElement1 = (mouseX, mouseY, button, element) -> {
        if(button == 1) {
            UIHealth screen = (UIHealth) MinecraftClient.getInstance().currentScreen;
            screen.addClickableWidget(new ActionsButton(mouseX, (int) (mouseY - getScrollY()), element, screen));
        }
    };

    private final ColoredText scrape = new ColoredText(0xFFc81414, Text.translatable("zomboidhealthsystem.health.hud.scrape"));
    private final ColoredText injury = new ColoredText(0xFFc81414, Text.translatable("zomboidhealthsystem.health.hud.injury"));
    private final ColoredText wound = new ColoredText(0xFFc81414,Text.translatable("zomboidhealthsystem.health.hud.wound"));
    private final ColoredText crack = new ColoredText(0xFFc81414,Text.translatable("zomboidhealthsystem.health.hud.crack"));
    private final ColoredText laceration = new ColoredText(0xFFc81414, Text.translatable("zomboidhealthsystem.health.hud.laceration"));
    private final ColoredText fracture = new ColoredText(0xFFc81414, Text.translatable("zomboidhealthsystem.health.hud.fracture"));
    private final ColoredText deep_fracture = new ColoredText(0xFFc81414, Text.translatable("zomboidhealthsystem.health.hud.deep_fracture"));
    private final ColoredText bleeding = new ColoredText(0xFFc81414, Text.translatable("zomboidhealthsystem.health.hud.bleeding"));
    private final ColoredText infection = new ColoredText(0xFFc81414, Text.translatable("zomboidhealthsystem.health.hud.infection"));
    private final ColoredText bandaged = new ColoredText(0xFF2cb828, Text.translatable("zomboidhealthsystem.health.hud.bandaged"));
    private final ColoredText dirty_bandage = new ColoredText(0xFFc81414,Text.translatable("item.zomboidhealthsystem.dirty_bandage"));

    public ZomboidWidget(int x, int y) {
        super(x, y,149,135, Text.of(" "));
        super.addElement(new Element(Health.HEAD_ID, buttonElement1));
        super.addElement(new Element(Health.BODY_ID, buttonElement1));
        super.addElement(new Element(Health.LEFT_ARM_ID, buttonElement1));
        super.addElement(new Element(Health.RIGHT_ARM_ID, buttonElement1));
        super.addElement(new Element(Health.LEFT_LEG_ID, buttonElement1));
        super.addElement(new Element(Health.RIGHT_LEG_ID, buttonElement1));
        super.addElement(new Element(Health.LEFT_FOOT_ID, buttonElement1));
        super.addElement(new Element(Health.RIGHT_FOOT_ID, buttonElement1));
    }

    private int ticks = ZomboidHealthSystem.UPDATE_FREQUENCY;

    public void tick(){
        if(ticks >= Math.min(ZomboidHealthSystem.UPDATE_FREQUENCY -1, 5 -1)) {
            for (Element element : elements) {
                int i = elements.indexOf(element);
                element.clearText();

                ClientHealth health = ZomboidHealthSystemClient.HEALTH;
                ClientHealth.BodyPart bodyPart = health.indexOf(i);

                if(!bodyPart.isBandaged()) {

                    if (bodyPart.getHpPercent() >= 1.0F)
                        element.setPos(null);
                    else if (bodyPart.getHpPercent() >= 0.9F)
                        element.addText(scrape);
                    else if (bodyPart.getHpPercent() >= 0.8F)
                        element.addText(injury);
                    else if(bodyPart.getHpPercent() >= 0.65F)
                        element.addText(wound);
                    else if (bodyPart.getHpPercent() >= 0.5F)
                        element.addText(crack);
                    else if (bodyPart.getHpPercent() >= 0.35F)
                        element.addText(laceration);
                    else if (bodyPart.getHpPercent() >= 0.2F)
                        element.addText(fracture);
                    else
                        element.addText(deep_fracture);

                } else {
                    if(!bodyPart.isDirtyBandage()) {
                        element.addText(bandaged);
                    } else {
                        element.addText(dirty_bandage);
                    }
                }
                if (bodyPart.isBleeding()){
                    element.addText(bleeding);
                }
                if(bodyPart.isInfection()){
                    element.addText(infection);
                }
            }

            ticks = 0;
        }
        ticks++;
    }
}