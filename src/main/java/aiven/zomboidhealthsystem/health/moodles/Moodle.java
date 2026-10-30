package aiven.zomboidhealthsystem.health.moodles;

import aiven.zomboidhealthsystem.ZomboidHealthSystem;
import aiven.zomboidhealthsystem.health.Health;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.HashMap;

public abstract class Moodle {
    protected final Health health;
    protected HashMap<Moodle, Float> multipliers = new HashMap<>();

    public Moodle(Health health){
        this.health = health;
    }

    public abstract String getId();

    public abstract int getAmplifier();

    public abstract void sleep(int sumTicks);

    public abstract String getNbt();

    public abstract void readNbt(String value);

    public abstract void update();

    public boolean showIcon(){
        return this.getAmplifier() != 0 && getMoodleIconTexture() != null && getBackgroundTexture() != null;
    }

    public ResourceLocation getMoodleIconTexture() {
        return ZomboidHealthSystem.getResourceLocation("textures/moodle/moodle_icon_%s.png".formatted(this.getId()));
    }

    public ResourceLocation getBackgroundTexture() {
        int amplifier = getAmplifier();
        amplifier = Math.min(amplifier, 4);
        amplifier = Math.max(amplifier, -4);
        if(amplifier > 0) {
            return ZomboidHealthSystem.getResourceLocation("textures/moodle/moodle_bkg_bad_%s.png".formatted(amplifier));
        } else {
            return ZomboidHealthSystem.getResourceLocation("textures/moodle/moodle_bkg_good_%s.png".formatted(-amplifier));
        }
    }

    public String getMoodleIconText() {
        return Component.translatable(ZomboidHealthSystem.ID + ".moodle." + getId()).getString() + " " + getAmplifier();
    }

    protected final Health getHealth() {
        return health;
    }

    protected final Player getPlayer() {
        return getHealth().getPlayer();
    }

    protected final Level getLevel() {
        return getPlayer().level();
    }

    public final float getMultiplier() {
        float multiplier = 1.0F;
        for(float mul : multipliers.values()) {
            multiplier *= mul;
        }
        return multiplier;
    }

    public final void setMultiplier(Moodle from, float multiplier) {
        multipliers.put(from, multiplier);
    }

    protected final boolean isOverWorld() {
        return getPlayer().level().equals(getPlayer().getServer().overworld());
    }

    protected static boolean once(int time){
        return Health.once(time);
    }
}
