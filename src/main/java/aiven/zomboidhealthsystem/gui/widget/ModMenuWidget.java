package aiven.zomboidhealthsystem.gui.widget;

import aiven.zomboidhealthsystem.ZomboidHealthSystem;
import aiven.zomboidhealthsystem.health.Health;
import aiven.zomboidhealthsystem.utility.ClientUtil;
import aiven.zomboidhealthsystem.utility.ModTime;
import aiven.zomboidhealthsystem.utility.Util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class ModMenuWidget extends ModContainer {
    private static final int width = (int)(498 / 2.5F);
    private static final int height = (int)(462 / 2.5F);

    private final ModWidgetGroup healthTabGroup;
    private final ModWidgetGroup infoTabGroup;

    private final HealthTabButton healthTabButton;
    private final InfoTabButton infoTabButton;

    protected float floatX = 0;
    protected float floatY = 0;

    public ModMenuWidget(int x, int y) {
        super(x, y, width, height);
        setX(x);
        setY(y);

        addChildren(new InvincibleWidget(0, 0, 8, 8) {
            @Override
            public void onClick(double d, double e) {
                onCloseButton();
            }
        });

        addChildren(new InvincibleWidget(8, 0, getWidth() - 8, 8) {
            @Override
            public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
                if(isMouseOver(mouseX, mouseY)) {
                    if (movableX()) {
                        ModMenuWidget.this.setFloatX(getFloatX() + (float) deltaX);
                    }
                    if (movableY()) {
                        ModMenuWidget.this.setFloatY(getFloatY() + (float) deltaY);
                    }
                    return true;
                }
                return false;
            }
        });

        this.healthTabGroup = new ModWidgetGroup();

        this.infoTabGroup = new ModWidgetGroup();

        this.healthTabButton = new HealthTabButton(24,8, this);
        this.infoTabButton = new InfoTabButton(0, 8, this);

        this.healthTabButton.setSelected(true);
        this.infoTabButton.setSelected(false);

        addChildren(healthTabGroup);
        addChildren(infoTabGroup);

        addChildren(healthTabButton);
        addChildren(infoTabButton);

        this.healthTabGroup.addChildren(new BodyWidget(14, 50) {
            @Override
            protected boolean isValidClickButton(int i) {
                return false;
            }
        });
        {
            int x1 = 75;
            int y1 = 60;
            this.healthTabGroup.addChildren(new BodyPartListWidget(x1,y1, width - x1 - 5, height - y1 - 5));
        }
    }

    @Override
    protected void renderContent(GuiGraphics context, int mouseX, int mouseY, float tickDelta) {
        context.blit(ZomboidHealthSystem.getResourceLocation("textures/gui/ui_texture.png"),
                0 , 0,
                0,0,
                getWidth(), getHeight(), getWidth(), getHeight()
        );
        Health health = ClientUtil.getHealth();
        if(healthTabGroup.visible) { // StringWidget криво рисуется idk почему
            context.drawString(
                    Minecraft.getInstance().font,
                    Component.translatable("zomboid-health-system.health.hud.body_status"),
                    78, 25,
                    0xFFf9f0f0
            );
            context.drawString(
                    Minecraft.getInstance().font,
                    getDamageText(),
                    78, 35,
                    0xFFf9f0f0
            );
            context.blit(
                    ZomboidHealthSystem.getResourceLocation("textures/gui/line.png"),
                    66,
                    119 - (int) (ClientUtil.getHealth().getPlayerHp() * 0.7F),
                    0, 0, 5,
                    (int) (ClientUtil.getHealth().getPlayerHp() * 0.7F), 7, 90
            );
        } else if(infoTabButton.visible) {

            context.drawString(
                    Minecraft.getInstance().font,
                    Component.literal("Day: " + (((Minecraft.getInstance().level.getDayTime() + 8000) / 24000) + 1)),
                    10, 25, 0xFFffffff, true
            );

            context.drawString(
                    Minecraft.getInstance().font,
                    Component.literal( "Time: " + new ModTime(Minecraft.getInstance().level.getDayTime())),
                    10, 35, 0xFFffffff, true
            );

            context.drawString(
                    Minecraft.getInstance().font,
                    Component.literal("Wind: " + Util.floor(health.getWind().getAmount(), 10) + "m/s"),
                    10, 45, 0xFFffffff,true
            );
        }

        super.renderContent(context, mouseX, mouseY, tickDelta);
    }

    @Override
    public void setX(int i) {
        setFloatX((float) i);
    }

    @Override
    public void setY(int i) {
        setFloatY((float) i);
    }

    protected boolean movableX() {
        return true;
    }

    protected boolean movableY() {
        return true;
    }

    public float getFloatX() {
        return floatX;
    }

    public void setFloatX(float floatX) {
        this.floatX = floatX;
        super.setX((int) floatX);
    }

    public float getFloatY() {
        return floatY;
    }

    public void setFloatY(float floatY) {
        this.floatY = floatY;
        super.setY((int) floatY);
    }

    private Component getDamageText() {
        float sumHp = ClientUtil.getHealth().getBodyHpPercent();
        Component damageText;

        if (sumHp >= 1.0F) {
            damageText = Component.translatable(ZomboidHealthSystem.ID + ".health.hud.completely_health");
        }
        else if (sumHp >= 0.93F) {
            damageText = Component.translatable(ZomboidHealthSystem.ID + ".health.hud.minor_damage");
        }
        else if (sumHp >= 0.75F) {
            damageText = Component.translatable(ZomboidHealthSystem.ID + ".health.hud.average_damage");
        }
        else if (sumHp >= 0.55F) {
            damageText = Component.translatable(ZomboidHealthSystem.ID + ".health.hud.severe_damage");
        }
        else {
            damageText = Component.translatable(ZomboidHealthSystem.ID + ".health.hud.at_death");
        }

        return damageText;
    }

    protected void onCloseButton() {

    }

    public HealthTabButton getHealthTabButton() {
        return healthTabButton;
    }

    public InfoTabButton getInfoTabButton() {
        return infoTabButton;
    }

    public ModWidgetGroup getHealthTabGroup() {
        return healthTabGroup;
    }

    public ModWidgetGroup getInfoTabGroup() {
        return infoTabGroup;
    }
}
