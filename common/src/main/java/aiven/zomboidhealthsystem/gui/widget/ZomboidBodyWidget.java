package aiven.zomboidhealthsystem.gui.widget;

import aiven.zomboidhealthsystem.gui.RenderHelper;
import aiven.zomboidhealthsystem.health.Health;
import aiven.zomboidhealthsystem.utility.ClientUtil;
import aiven.zomboidhealthsystem.utility.Util;

import net.minecraft.client.gui.GuiGraphics;

import java.util.ArrayList;
import java.util.List;

public class ZomboidBodyWidget extends AbstractBodyWidget {

    private final List<BodyPart> bodyParts = new ArrayList<>();
    {
        bodyParts.add(new BodyPart(6, 0, 16, 16, Health.HEAD_ID));
        bodyParts.add(new BodyPart(6, 15, 16, 30, Health.BODY_ID));
        bodyParts.add(new BodyPart(0, 15, 7, 30, Health.LEFT_ARM_ID));
        bodyParts.add(new BodyPart(21, 15, 7, 30, Health.RIGHT_ARM_ID));
        bodyParts.add(new BodyPart(6, 44, 8, 22, Health.LEFT_LEG_ID));
        bodyParts.add(new BodyPart(13, 44, 8, 22, Health.RIGHT_LEG_ID));
        bodyParts.add(new BodyPart(6, 65, 8, 8, Health.LEFT_FOOT_ID));
        bodyParts.add(new BodyPart(13, 65, 8, 8, Health.RIGHT_FOOT_ID));
    }

    public ZomboidBodyWidget(int x, int y) {
        super(x, y, 28, 73);
    }

    @Override
    protected void renderWidget(GuiGraphics context, int mouseX, int mouseY, float tickDelta) {
        Health health = ClientUtil.getHealth();
        if(health != null) {
            for (BodyPart bodyPart : bodyParts) {
                bodyPart.renderPart(context, tickDelta, health.getBodyPart(bodyPart.id), getX(), getY());
            }
        }
    }

    private static class BodyPart {
        private final int x, y, width, height;
        private final String id;

        private float d = 0;
        private boolean bl = true;

        public BodyPart(int x, int y, int width, int height, String id) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.id = id;
        }

        public void renderPart(GuiGraphics context, float tickDelta, aiven.zomboidhealthsystem.health.bodyparts.BodyPart bodyPart, int x, int y) {
            int color;
            if (bodyPart.isBleeding() && !bodyPart.isBandaged()) {
                if (bl) {
                    d += tickDelta / 20;
                } else {
                    d -= tickDelta / 20;
                }
                d = Util.clamp(d, 0.0F, 1.0F);

                if (d == 1.0F) {
                    bl = false;
                } else if (d == 0.0F) {
                    bl = true;
                }

                color = blendColors(Color.FULL_HP.getValue(), Color.BLEEDING.getValue(), d);
            } else {
                color = getColor(bodyPart);
            }
            RenderHelper.drawQuad(context, this.x + x, this.y + y, width, height, color, getBorderColor(bodyPart));
        }

        private int getBorderColor(aiven.zomboidhealthsystem.health.bodyparts.BodyPart bodyPart) {
            if(bodyPart.getAdditionalHp() > 0) {
                return Color.ADDITIONAL_HP_BORDER.getValue();
            } else {
                return Color.BORDER.getValue();
            }
        }

        private int getColor(aiven.zomboidhealthsystem.health.bodyparts.BodyPart bodyPart) {
            if(bodyPart.isBandaged()) {
                return Color.BANDAGED.getValue();
            } else if(bodyPart.getHp() >= bodyPart.getMaxHp()) {
                return Color.FULL_HP.getValue();
            } else {
                return Color.DAMAGED.getValue();
            }
        }
    }

    private enum Color {
        BANDAGED(0xFFe6d9ad),
        FULL_HP(0xFF000000),
        DAMAGED(0xFFfc050d),
        BLEEDING(0xFFfc050d),
        ADDITIONAL_HP_BORDER(0xFF05fc21),
        BORDER(0xFFffffff)
        ;

        private final int value;

        Color(int value){
            this.value = value;
        }

        int getValue(){
            return value;
        }
    }

    private static int blendColors(int colorA, int colorB, float percent) {
        percent = Math.max(0.0f, Math.min(1.0f, percent));

        int a = (int) (((colorA >> 24) & 0xFF) * (1 - percent) + ((colorB >> 24) & 0xFF) * percent);

        int r = (int) (((colorA >> 16) & 0xFF) * (1 - percent) + ((colorB >> 16) & 0xFF) * percent);

        int g = (int) (((colorA >> 8) & 0xFF) * (1 - percent) + ((colorB >> 8) & 0xFF) * percent);

        int b = (int) ((colorA & 0xFF) * (1 - percent) + (colorB & 0xFF) * percent);

        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}
