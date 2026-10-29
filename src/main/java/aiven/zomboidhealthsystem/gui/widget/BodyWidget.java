package aiven.zomboidhealthsystem.gui.widget;

import aiven.zomboidhealthsystem.gui.RenderHelper;
import aiven.zomboidhealthsystem.health.Health;
import aiven.zomboidhealthsystem.utility.ClientUtil;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class BodyWidget extends AbstractMovableWidget {
    public BodyWidget(int x, int y) {
        super(x, y, 30, 73, Component.empty());
    }

    @Override
    public boolean isRelativeContent() {
        return false;
    }

    @Override
    protected void renderWidget(GuiGraphics context, int mouseX, int mouseY, float tickDelta) {
        for(BodyPart bodyPart : BodyPart.values()) {
            bodyPart.renderPart(context, tickDelta, getX(), getY());
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

        public void renderPart(GuiGraphics context, float tickDelta, int x, int y) {
            if(ClientUtil.getHealth() != null) {
                RenderHelper.drawQuad(context, this.x + x, this.y + y, width, height, getColor(), getBorderColor());
            }
        }

        private int getBorderColor() {
            Health health = ClientUtil.getHealth();
            if(health == null) {
                return Colors.BORDER_COLOR.getValue();
            }
            aiven.zomboidhealthsystem.health.bodyparts.BodyPart part = health.getBodyPart(id);
            if(part.getAdditionalHp() > 0) {
                return Colors.WHITE.getValue();
            } else {
                return Colors.BORDER_COLOR.getValue();
            }
        }

        private int getColor() {
            Health health = ClientUtil.getHealth();
            if(health == null) {
                return Colors.NULL_HEALTH_COLOR.getValue();
            }
            aiven.zomboidhealthsystem.health.bodyparts.BodyPart part = health.getBodyPart(id);
            float hpPercent = part.getHpPercent();

            if(!part.isBandaged() || part.getBandageItem().isDirty()) {
                if (hpPercent > 1.0F) return Colors.ADDITIONAL_HEALTH_COLOR.getValue();
                else if (hpPercent == 1.0F) return Colors.FULL_HEALTH_COLOR.getValue();
                else if (hpPercent >= 0.90F) return Colors.LITTLE_DAMAGE_HEALTH_COLOR.getValue();
                else if (hpPercent >= 0.75F) return Colors.DAMAGED_HEALTH_COLOR.getValue();
                else if (hpPercent >= 0.50F) return Colors.HALF_HEALTH_COLOR.getValue();
                else if (hpPercent >= 0.25F) return Colors.VERY_DAMAGED_HEALTH_COLOR.getValue();
                else if (hpPercent > 0.00F) return Colors.VERY_VERY_DAMAGED_HEALTH_COLOR.getValue();
                else return Colors.NULL_HEALTH_COLOR.getValue();
            } else {
                return Colors.BANDAGED_BODY_PART.getValue();
            }
        }
    }

    private enum Colors {
        BANDAGED_BODY_PART (0xFFe6d9ad), //  e9ddb4
        ADDITIONAL_HEALTH_COLOR (0xFF05ff10),
        FULL_HEALTH_COLOR (0xFF347a2e),
        LITTLE_DAMAGE_HEALTH_COLOR(0xFFc8e000),
        DAMAGED_HEALTH_COLOR (0xFFe0aa00),
        HALF_HEALTH_COLOR  (0xFFe08000),
        VERY_DAMAGED_HEALTH_COLOR  (0xFFe04200),
        VERY_VERY_DAMAGED_HEALTH_COLOR(0xFFfc0202),
        NULL_HEALTH_COLOR  (0xFF424242),
        WHITE (0xFFffffff),
        BORDER_COLOR (0xFF121212)
        ;

        private final int value;

        Colors(int value){
            this.value = value;
        }

        int getValue(){
            return value;
        }
    }
}
