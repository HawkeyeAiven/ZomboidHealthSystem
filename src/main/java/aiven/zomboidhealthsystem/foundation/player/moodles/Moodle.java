package aiven.zomboidhealthsystem.foundation.player.moodles;

import aiven.zomboidhealthsystem.ZomboidHealthSystem;
import aiven.zomboidhealthsystem.foundation.player.Health;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

import java.util.HashMap;

public abstract class Moodle {
    final Health health;
    protected float amount;
    protected HashMap<Moodle, Float> multipliers = new HashMap<>();

    public Moodle(Health health){
        this.health = health;
    }

    public boolean showIcon(){
        return this.getAmplifier() != 0 && getMoodleIconTexture() != null;
    }

    public Identifier getMoodleIconTexture() {
        return Identifier.of(ZomboidHealthSystem.ID, "textures/moodle/moodle_icon_%s.png".formatted(this.getId()));
    }

    public void onSleep() {
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

    public void readNbt(String value){
        this.setAmount(Float.parseFloat(value));
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

    public abstract void update();
}
