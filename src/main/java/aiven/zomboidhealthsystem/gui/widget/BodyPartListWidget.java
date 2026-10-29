package aiven.zomboidhealthsystem.gui.widget;

import aiven.zomboidhealthsystem.ModItems;
import aiven.zomboidhealthsystem.ZomboidHealthSystem;
import aiven.zomboidhealthsystem.gui.screen.AbstractModScreen;
import aiven.zomboidhealthsystem.item.AbstractBandageItem;
import aiven.zomboidhealthsystem.item.AlcoholWipe;
import aiven.zomboidhealthsystem.network.ClientNetworking;
import aiven.zomboidhealthsystem.network.packet.ActionPacket;
import aiven.zomboidhealthsystem.health.Health;
import aiven.zomboidhealthsystem.health.bodyparts.BodyPart;
import aiven.zomboidhealthsystem.utility.ClientUtil;

import aiven.zomboidhealthsystem.utility.Util;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;

public class BodyPartListWidget extends ModScrollPane {
    private static final Component scrape = Component.translatable(ZomboidHealthSystem.ID + ".health.hud.scrape").withStyle(ChatFormatting.RED);
    private static final Component injury = Component.translatable(ZomboidHealthSystem.ID + ".health.hud.injury").withStyle(ChatFormatting.RED);
    private static final Component wound = Component.translatable(ZomboidHealthSystem.ID + ".health.hud.wound").withStyle(ChatFormatting.RED);
    private static final Component crack = Component.translatable(ZomboidHealthSystem.ID + ".health.hud.deep_wound").withStyle(ChatFormatting.RED);
    private static final Component laceration = Component.translatable(ZomboidHealthSystem.ID + ".health.hud.laceration").withStyle(ChatFormatting.RED);
    private static final Component fracture = Component.translatable(ZomboidHealthSystem.ID + ".health.hud.fracture").withStyle(ChatFormatting.RED);
    private static final Component deep_fracture = Component.translatable(ZomboidHealthSystem.ID + ".health.hud.deep_fracture").withStyle(ChatFormatting.RED);
    private static final Component bleeding = Component.translatable(ZomboidHealthSystem.ID + ".health.hud.bleeding").withStyle(ChatFormatting.RED);
    private static final Component infection = Component.translatable(ZomboidHealthSystem.ID + ".health.hud.infection").withStyle(ChatFormatting.RED);
    private static final Component bandaged = Component.translatable(ZomboidHealthSystem.ID + ".health.hud.bandaged").withStyle(ChatFormatting.GREEN);
    private static final Component dirty_bandage = Component.translatable("item." + ZomboidHealthSystem.ID + ".dirty_bandage").withStyle(ChatFormatting.RED);

    private static TemporalAdvancedMenuButton lastMenuButton;

    private final HBox hBox;

    public BodyPartListWidget(int x, int y, int width, int height) {
        super(x, y, width, height);
        hBox = new HBox(5, 5, width - 5) {
            @Override
            public int getSpacing() {
                return 5;
            }
        };
        hBox.addChildren(new BodyPartItem(Health.HEAD_ID));
        hBox.addChildren(new BodyPartItem(Health.BODY_ID));
        hBox.addChildren(new BodyPartItem(Health.LEFT_ARM_ID));
        hBox.addChildren(new BodyPartItem(Health.RIGHT_ARM_ID));
        hBox.addChildren(new BodyPartItem(Health.LEFT_LEG_ID));
        hBox.addChildren(new BodyPartItem(Health.RIGHT_LEG_ID));
        hBox.addChildren(new BodyPartItem(Health.LEFT_FOOT_ID));
        hBox.addChildren(new BodyPartItem(Health.RIGHT_FOOT_ID));
        setContent(hBox);
    }

    @Override
    protected void renderContent(GuiGraphics context, int mouseX, int mouseY, float tickDelta) {
        for(AbstractModWidget modWidget : hBox.getChildren()) {
            ((BodyPartItem) modWidget).update();
        }
        super.renderContent(context, mouseX, mouseY, tickDelta);
    }

    public static class BodyPartItem extends AbstractModWidget {
        private final String bodyPartId;

        private final ArrayList<Component> texts = new ArrayList<>();

        public BodyPartItem(String bodyPartId) {
            super(0, 0, 90, 5, Component.translatable(ZomboidHealthSystem.ID + ".health." + bodyPartId));
            this.bodyPartId = bodyPartId;
        }

        @Override
        protected void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
            context.drawString(
                    Minecraft.getInstance().font,
                    getMessage(),
                    getX(), getY(),
                    0xFFFFFF
            );

            int y = getY() + 12;
            for(Component text : texts) {
                context.drawString(
                        Minecraft.getInstance().font,
                        text,
                        getX() + 12, y,
                        text.getStyle().getColor().getValue()
                );
                y+=12;
            }
        }

        private void update() {
            texts.clear();
            Health health = ClientUtil.getHealth();
            if(health == null) {
                return;
            }
            BodyPart bodyPart = health.getBodyPart(bodyPartId);
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

            this.visible = !texts.isEmpty();
        }

        @Override
        public boolean mouseClicked(double d, double e, int i) {
            if (this.active && this.visible) {
                if (this.isValidClickButton(i)) {
                    if (this.clicked(d, e)) {
                        this.onClick(d, e);
                        return true;
                    }
                }
            }
            return false;
        }

        @Override
        public void onClick(double mouseX, double mouseY) {
            int x = getScreenX() + ((int) mouseX - getX());
            int y = getScreenY() + ((int) mouseY - getY());
            BodyPart bodyPart = ClientUtil.getHealth().getBodyPart(bodyPartId);
            TemporalAdvancedMenuButton menuButton = createMenuButton(bodyPart, x, y);
            if(menuButton != null && Minecraft.getInstance().screen instanceof AbstractModScreen modScreen) {
                if(lastMenuButton != null) {
                    modScreen.removeWidget(lastMenuButton);
                }
                lastMenuButton = menuButton;
                modScreen.addRenderableWidget(menuButton);
                modScreen.setFocused(menuButton);
            }
        }

        @Override
        protected boolean isValidClickButton(int button) {
            return button == 1;
        }

        @Override
        public boolean isRelativeContent() {
            return false;
        }

        private static TemporalAdvancedMenuButton createMenuButton(BodyPart bodyPart, int x, int y) {
            Minecraft minecraft = Minecraft.getInstance();
            LocalPlayer player = minecraft.player;

            if(player == null) {
                return null;
            }

            TemporalAdvancedMenuButton menuButton = new TemporalAdvancedMenuButton(x, y, 80, 20, Component.literal("Actions ->"));
            menuButton.setShow(true);

            if(!bodyPart.isBandaged()) {
                {
                    ArrayList<AbstractBandageItem> bandageItems = new ArrayList<>();
                    ArrayList<AdvancedModMenuButton.Item> items = new ArrayList<>();

                    for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                        ItemStack itemStack = player.getInventory().getItem(i);
                        if (itemStack.getItem() instanceof AbstractBandageItem bandageItem) {
                            if(bandageItems.contains(bandageItem)) {
                                continue;
                            }
                            bandageItems.add(bandageItem);
                            int finalIndex = i;
                            items.add(new AdvancedModMenuButton.ItemWithDelay(bandageItem.getName(itemStack), () -> {
                                ClientNetworking.send(new ActionPacket(ActionPacket.Type.BANDAGE, bodyPart.getId(), finalIndex));
                            }, AbstractBandageItem.BANDAGE_COOLDOWN_TIME));
                        }
                    }

                    if (!items.isEmpty()) {
                        menuButton.addItems(items, false, Component.literal("Bandage"));
                    }
                }
                {
                    if (bodyPart.hasInfection() && Util.contains(player.getInventory(), ModItems.ALCOHOL_WIPE)) {
                        menuButton.addItem(new AdvancedModMenuButton.ItemWithDelay(Component.literal("Disinfect"), () -> {
                            ClientNetworking.send(new ActionPacket(ActionPacket.Type.DISINFECT, bodyPart.getId(), -1));
                        }, AlcoholWipe.DISINFECT_COOLDOWN_TIME));
                    }
                }
            } else {
                menuButton.addItem(new AdvancedModMenuButton.ItemWithDelay(Component.literal("Unbandage"), () -> {
                    ClientNetworking.send(new ActionPacket(ActionPacket.Type.UNBANDAGE, bodyPart.getId(), -1));
                }, AbstractBandageItem.UNBANDAGE_COOLDOWN_TIME));
            }
            return menuButton;
        }
    }
}
