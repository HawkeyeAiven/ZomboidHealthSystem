package aiven.zomboidhealthsystem.foundation.player.moodles;

import aiven.zomboidhealthsystem.ZomboidHealthSystem;
import aiven.zomboidhealthsystem.foundation.player.Health;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.HashMap;

public abstract class Moodle {
    protected final Health health;
    protected float amount;
    protected HashMap<Moodle, Float> multipliers = new HashMap<>();
    private boolean isSleeping = false;
    private int sleepTicks = 0;

    public Moodle(Health health){
        this.health = health;
    }

    public void update() {
        if(isSleeping && !getPlayer().isSleeping()) {
            isSleeping = false;
            onSleep(sleepTicks);
            sleepTicks = 0;
        }
    }

    public boolean showIcon(){
        return this.getAmplifier() != 0 && getMoodleIconTexture() != null && getBackgroundTexture() != null;
    }

    public Identifier getMoodleIconTexture() {
        return Identifier.of(ZomboidHealthSystem.ID, "textures/moodle/moodle_icon_%s.png".formatted(this.getId()));
    }

    public Identifier getBackgroundTexture() {
        int amplifier = getAmplifier();
        amplifier = Math.min(amplifier, 4);
        amplifier = Math.max(amplifier, -4);
        if(amplifier > 0) {
            return Identifier.of(ZomboidHealthSystem.ID, "textures/moodle/moodle_bkg_bad_%s.png".formatted(amplifier));
        } else {
            return Identifier.of(ZomboidHealthSystem.ID, "textures/moodle/moodle_bkg_good_%s.png".formatted(-amplifier));
        }
    }

    public String getMoodleIconText() {
        return Text.translatable("zomboidhealthsystem.moodle." + getId()).getString() + " " + getAmplifier();
    }

    public void sleep(int ticks) {
        if(!isSleeping) {
            isSleeping = true;
            sleepTicks = 0;
        }
        sleepTicks += ticks;
    }

    public void onSleep(int sumTicks) {

    }

    boolean once(int time){
        return Health.once(time);
    }

    public String getNbt(){
        if (amount == 0) {
            return null;
        } else {
            return String.valueOf(getAmount());
        }
    }

    public void readNbt(String value) {
        if(value != null) {
            this.setAmount(Float.parseFloat(value));
        } else {
            this.setAmount(0);
        }
    }

    public Health getHealth() {
        return health;
    }

    public PlayerEntity getPlayer() {
        return getHealth().getPlayer();
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public void addAmount(float amount) {
        setAmount(getAmount() + amount);
    }

    public int getAmplifier() {
        return (int) getAmount();
    }

    public float getMultiplier() {
        float multiplier = 1.0F;
        for(float mul : multipliers.values()) {
            multiplier *= mul;
        }
        return multiplier;
    }

    public void addMultiplier(Moodle from, float multiplier) {
        multipliers.put(from, multiplier);
    }

    protected final boolean isOverWorld() {
        return getPlayer().getWorld().equals(getPlayer().getServer().getOverworld());
    }

    public abstract String getId();
}
