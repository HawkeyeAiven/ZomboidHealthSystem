package aiven.zomboidhealthsystem.gui.widget;

import net.minecraft.network.chat.Component;

import java.util.List;

public class AdvancedModMenuButton extends ModMenuButton {
    private static final int itemWidth = 80;
    private static final int itemHeight = 20;

    public AdvancedModMenuButton(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
    }

    public void addItem(Item item) {
        addChildren(create(item));
    }

    protected ModButton create(Item item) {
        if(item instanceof ItemWithDelay itemWithDelay) {
            return new ModButtonWithDelay(0,0, itemWidth, itemHeight, item.component, itemWithDelay.delay) {
                @Override
                public void execute(double mouseX, double mouseY) {
                    AdvancedModMenuButton.this.execute(item);
                }
            };
        } else {
            return new ModButton(0, 0, itemWidth, itemHeight, item.component) {
                @Override
                public void onClick(double d, double e) {
                    execute(item);
                }
            };
        }
    }

    protected void execute(Item item) {
        item.runnable.run();
    }

    public void addItems(List<Item> items, boolean show, Component component) {
        AdvancedModMenuButton modMenuButton = createMenuButton(width, height, component);
        for(Item item : items) {
            modMenuButton.addItem(item);
        }
        modMenuButton.setShow(show);
        addChildren(modMenuButton);
    }

    protected AdvancedModMenuButton createMenuButton(int width, int height, Component component) {
        return new AdvancedModMenuButton(0, 0, width, height, component);
    }

    public static class Item {
        public Component component;
        public Runnable runnable;

        public Item(Component component, Runnable runnable) {
            this.component = component;
            this.runnable = runnable;
        }

        public Component getComponent() {
            return component;
        }

        public void setComponent(Component component) {
            this.component = component;
        }

        public Runnable getRunnable() {
            return runnable;
        }

        public void setRunnable(Runnable runnable) {
            this.runnable = runnable;
        }
    }

    public static class ItemWithDelay extends Item {
        private float delay;

        public ItemWithDelay(Component component, Runnable runnable, float delay) {
            super(component, runnable);
            this.delay = delay;
        }

        public float getDelay() {
            return delay;
        }

        public void setDelay(float delay) {
            this.delay = delay;
        }
    }
}
