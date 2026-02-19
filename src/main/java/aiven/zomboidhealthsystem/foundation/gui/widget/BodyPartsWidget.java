package aiven.zomboidhealthsystem.foundation.gui.widget;

import aiven.zomboidhealthsystem.Config;
import aiven.zomboidhealthsystem.ZomboidHealthSystemClient;
import aiven.zomboidhealthsystem.foundation.player.Health;
import aiven.zomboidhealthsystem.foundation.utility.RenderHelper;
import net.minecraft.client.gui.DrawContext;
import org.joml.Vector2f;

public class BodyPartsWidget extends ModClickableWidget {
    private final Vector2f pos;

    public BodyPartsWidget() {
        super((int)Config.HEALTH_HUD_POS.getValue().x, (int)Config.HEALTH_HUD_POS.getValue().y, 35, 73, null);
        this.pos = Config.HEALTH_HUD_POS.getValue();
    }

    @Override
    protected void renderButton(DrawContext context, int mouseX, int mouseY, float tickDelta) {
        render(context, tickDelta, this.pos);
    }

    public void render(DrawContext context, float tickDelta, Vector2f pos) {
        for(BodyPart bodyPart : BodyPart.values()) {
            bodyPart.renderPart(context, tickDelta, (int) pos.x, (int) pos.y);
        }
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        double lastMouseX = mouseX - deltaX;
        double lastMouseY = mouseY - deltaY;
        if(isMouseOver(lastMouseX, lastMouseY) && button == 0) {
            this.pos.add((float) deltaX, (float) deltaY);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return false;
    }

    public void setPos(Vector2f pos) {
        this.pos.set(pos);
    }

    @Override
    public void setX(int x) {
        this.pos.x = x;
    }

    @Override
    public void setY(int y) {
        this.pos.y = y;
    }

    @Override
    public int getX() {
        return (int) pos.x;
    }

    @Override
    public int getY() {
        return (int) pos.y;
    }

    private enum BodyPart {
        HEAD(16,16,10,0, Health.HEAD_ID),
        BODY(16,30,10,18, Health.BODY_ID),
        LEFT_ARM(7,30,1,18, Health.LEFT_ARM_ID),
        RIGHT_ARM(7,30,28,18, Health.RIGHT_ARM_ID),
        LEFT_LEG(7,20,10,50, Health.LEFT_LEG_ID),
        RIGHT_LEG(7,20,19,50, Health.RIGHT_LEG_ID),
        LEFT_FOOT(7,7,10,72, Health.LEFT_FOOT_ID),
        RIGHT_FOOT(7,7,19,72, Health.RIGHT_FOOT_ID)
        ;

        private final int width, height, x, y;
        private final String id;


        BodyPart(int width, int height, int x, int y, String id) {
            this.width = width;
            this.height = height;
            this.x = x;
            this.y = y;
            this.id = id;
        }

        public void renderPart(DrawContext context, float tickDelta, int x, int y){
            RenderHelper.DrawQuad(context, width, height, this.x + x, this.y + y, getColor(), getBorderColor());
        }

        private int getBorderColor() {
            aiven.zomboidhealthsystem.foundation.player.bodyparts.BodyPart part = ZomboidHealthSystemClient.HEALTH.getBodyPart(id);
            if(part.getAdditionalHp() > 0) {
                return Colors.WHITE.getColor();
            } else {
                return Colors.BORDER_COLOR.getColor();
            }
        }

        private int getColor() {
            aiven.zomboidhealthsystem.foundation.player.bodyparts.BodyPart part = ZomboidHealthSystemClient.HEALTH.getBodyPart(id);
            float hpPercent = part.getHpPercent();

            if(!part.isBandaged() || part.getBandageItem().isDirty()) {
                if (hpPercent > 1.0F) return Colors.ADDITIONAL_HEALTH_COLOR.getColor();
                else if (hpPercent == 1.0F) return Colors.FULL_HEALTH_COLOR.getColor();
                else if (hpPercent >= 0.90F) return Colors.LITTLE_DAMAGED_HEALTH_COLOR.getColor();
                else if (hpPercent >= 0.75F) return Colors.DAMAGED_HEALTH_COLOR.getColor();
                else if (hpPercent >= 0.50F) return Colors.HALF_HEALTH_COLOR.getColor();
                else if (hpPercent >= 0.25F) return Colors.VERY_DAMAGED_HEALTH_COLOR.getColor();
                else if (hpPercent > 0.00F) return Colors.VERY_VERY_DAMAGED_HEALTH_COLOR.getColor();
                else return Colors.NULL_HEALTH_COLOR.getColor();
            } else {
                return Colors.BANDAGED_BODY_PART.getColor();
            }
        }
    }

    private enum Colors {
        BANDAGED_BODY_PART (0xFFe6d9ad), //  e9ddb4
        ADDITIONAL_HEALTH_COLOR (0xFF05ff10),
        FULL_HEALTH_COLOR (0xFF347a2e),
        LITTLE_DAMAGED_HEALTH_COLOR (0xFFc8e000),
        DAMAGED_HEALTH_COLOR (0xFFe0aa00),
        HALF_HEALTH_COLOR  (0xFFe08000),
        VERY_DAMAGED_HEALTH_COLOR  (0xFFe04200),
        VERY_VERY_DAMAGED_HEALTH_COLOR(0xFF00000),
        NULL_HEALTH_COLOR  (0xFF424242),
        WHITE (0xFFffffff),
        BORDER_COLOR (0xFF121212)
        ;

        private final int color;

        Colors(int color){
            this.color = color;
        }

        int getColor(){
            return color;
        }
    }
}
