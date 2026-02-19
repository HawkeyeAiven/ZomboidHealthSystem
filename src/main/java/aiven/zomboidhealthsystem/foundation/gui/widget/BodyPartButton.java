package aiven.zomboidhealthsystem.foundation.gui.widget;

import aiven.zomboidhealthsystem.foundation.gui.screen.AbstractModScreen;
import aiven.zomboidhealthsystem.foundation.player.bodyparts.BodyPart;
import aiven.zomboidhealthsystem.foundation.utility.ColoredText;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

import java.util.ArrayList;

public class BodyPartButton extends ModClickableWidget {
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

    private final BodyPart bodyPart;
    private final ArrayList<ColoredText> texts = new ArrayList<>();

    public BodyPartButton(int x, int y, BodyPart bodyPart) {
        super(x, y, 90, 5, Text.translatable("zomboidhealthsystem.health." + bodyPart.getId()));
        this.bodyPart = bodyPart;
    }

    @Override
    protected void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
        context.drawText(MinecraftClient.getInstance().textRenderer, getMessage(), getX(), getY(), 0xFFFFFFFF, false);

        int y = getY() + 12;
        for(ColoredText text : texts) {
            context.drawText(MinecraftClient.getInstance().textRenderer, text.text().getString(), getX() + 12, y, text.color(), false);
            y+=12;
        }
    }

    public void tick() {
        texts.clear();
        if(!bodyPart.isBandaged()) {
            if(bodyPart.getHpPercent() < 1.0F) {
                if (bodyPart.getHpPercent() >= 0.9F) {
                    texts.add(scrape);
                } else if (bodyPart.getHpPercent() >= 0.8F) {
                    texts.add(injury);
                } else if (bodyPart.getHpPercent() >= 0.65F) {
                    texts.add(wound);
                } else if (bodyPart.getHpPercent() >= 0.5F) {
                    texts.add(crack);
                } else if (bodyPart.getHpPercent() >= 0.35F) {
                    texts.add(laceration);
                } else if (bodyPart.getHpPercent() >= 0.2F) {
                    texts.add(fracture);
                } else {
                    texts.add(deep_fracture);
                }
            }
        } else if(!bodyPart.getBandageItem().isDirty()) {
            texts.add(bandaged);
        } else {
            texts.add(dirty_bandage);
        }
        if(bodyPart.isBleeding() && (!bodyPart.isBandaged() || !bodyPart.getBandageItem().isStopBleeding())) {
            texts.add(bleeding);
        }
        if(bodyPart.hasInfection()){
            texts.add(infection);
        }

        this.height = (texts.size() + 1) * 12;
        this.contentHeight = height;
    }

    public boolean hasText() {
        return !texts.isEmpty();
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        ((AbstractModScreen) MinecraftClient.getInstance().currentScreen).addClickableWidget(new ActionsButton((int) mouseX, (int) mouseY, this.bodyPart));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return this.mouseClicked(mouseX, mouseY, button, 0);
    }


    public boolean mouseClicked(double mouseX, double mouseY, int button, double scrollY) {
        if(this.active && this.visible){
            if(this.clicked(mouseX, mouseY + scrollY) && this.isValidClickButton(button)){
                this.playDownSound(MinecraftClient.getInstance().getSoundManager());
                this.onClick(mouseX, mouseY);
                return true;
            }
        }
        return false;
    }

    @Override
    protected boolean isValidClickButton(int button) {
        return button == 1;
    }
}
