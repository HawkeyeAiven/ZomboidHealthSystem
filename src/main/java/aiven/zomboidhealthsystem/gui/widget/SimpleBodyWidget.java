package aiven.zomboidhealthsystem.gui.widget;

import aiven.zomboidhealthsystem.gui.RenderHelper;
import aiven.zomboidhealthsystem.health.Health;
import aiven.zomboidhealthsystem.utility.ClientUtil;

import net.minecraft.client.gui.GuiGraphics;

public class SimpleBodyWidget extends AbstractBodyWidget {
    public SimpleBodyWidget(int x, int y) {
        super(x, y, 30, 73);
    }

    @Override
    protected void renderWidget(GuiGraphics context, int mouseX, int mouseY, float tickDelta) {
        Health health = ClientUtil.getHealth();
        if(health != null) {
            for (BodyPart bodyPart : BodyPart.values()) {
                bodyPart.renderPart(context, tickDelta, health.getBodyPart(bodyPart.id), getX(), getY());
            }
        }
    }

    private enum BodyPart {
        HEAD(7, 0, 16, 16, Health.HEAD_ID),
        BODY(7, 16, 16, 30, Health.BODY_ID),
        LEFT_ARM(0, 16, 7, 30, Health.LEFT_ARM_ID),
        RIGHT_ARM(23, 16, 7, 30, Health.RIGHT_ARM_ID),
        LEFT_LEG(7, 46, 7, 20, Health.LEFT_LEG_ID),
        RIGHT_LEG(14, 46, 7, 20, Health.RIGHT_LEG_ID),
        LEFT_FOOT(7, 66, 7, 7, Health.LEFT_FOOT_ID),
        RIGHT_FOOT(14, 66, 7, 7, Health.RIGHT_FOOT_ID),
        ;

        private final int x, y, width, height;
        private final String id;


        BodyPart(int x, int y, int width, int height, String id) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.id = id;
        }

        public void renderPart(GuiGraphics context, float tickDelta, aiven.zomboidhealthsystem.health.bodyparts.BodyPart bodyPart, int x, int y) {
            RenderHelper.drawQuad(context, this.x + x, this.y + y, width, height, getColor(bodyPart), getBorderColor(bodyPart));
        }

        private int getBorderColor(aiven.zomboidhealthsystem.health.bodyparts.BodyPart bodyPart) {
            if(bodyPart.getAdditionalHp() > 0) {
                return Color.ADDITIONAL_HP_BORDER.getValue();
            } else {
                return Color.BORDER.getValue();
            }
        }

        private int getColor(aiven.zomboidhealthsystem.health.bodyparts.BodyPart bodyPart) {
            float hpPercent = bodyPart.getHpPercent();
            if(!bodyPart.isBandaged() || bodyPart.getBandageItem().isDirty()) {
                if (hpPercent > 1.0F) return Color.ADDITIONAL_HP.getValue();
                else if (hpPercent == 1.0F) return Color.FULL_HP.getValue();
                else if (hpPercent >= 0.90F) return Color.LITTLE_DAMAGE.getValue();
                else if (hpPercent >= 0.75F) return Color.DAMAGED.getValue();
                else if (hpPercent >= 0.50F) return Color.HALF_HP.getValue();
                else if (hpPercent >= 0.25F) return Color.VERY_DAMAGED.getValue();
                else if (hpPercent > 0.00F) return Color.VERY_VERY_DAMAGED.getValue();
                else return Color.NULL_HP.getValue();
            } else {
                return Color.BANDAGED.getValue();
            }
        }
    }

    private enum Color {
        BANDAGED(0xFFe6d9ad),
        ADDITIONAL_HP(0xFF05ff10),
        FULL_HP(0xFF347a2e),
        LITTLE_DAMAGE(0xFFc8e000),
        DAMAGED(0xFFe0aa00),
        HALF_HP(0xFFe08000),
        VERY_DAMAGED(0xFFe04200),
        VERY_VERY_DAMAGED(0xFFfc0202),
        NULL_HP(0xFF424242),
        ADDITIONAL_HP_BORDER(0xFFffffff),
        BORDER(0xFF121212)
        ;

        private final int value;

        Color(int value){
            this.value = value;
        }

        int getValue(){
            return value;
        }
    }
}
