package aiven.zomboidhealthsystem.foundation.player.moodles;

import aiven.zomboidhealthsystem.foundation.player.Health;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.player.PlayerEntity;

public abstract class Moodle {
    final Health health;
    protected float amount;

    public Moodle(Health health){
        this.health = health;
    }

    public boolean hasIcon(){
        return this.getAmount() >= 1;
    }

    public void update(){
        if(getEffect() != null) {
            if (!hasIcon()) {
                if (this.getPlayer().hasStatusEffect(getEffect())) {
                    this.getHealth().clearEffect(getEffect());
                }
            } else {
                if (this.getPlayer().hasStatusEffect(getEffect()) && this.getPlayer().getStatusEffect(getEffect()).getAmplifier() != getEffectAmplifier() - 1) {
                    this.getHealth().clearEffect(getEffect());
                }
                this.getHealth().addStatusEffect(getEffect(), Math.min((int) getAmount(), 10), 15 * 20);
            }
        }
    }

    public void onSleep() {

    }

    boolean random(int time){
        return Health.random(time);
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

    public int getEffectAmplifier() {
        return (int) getAmount();
    }

    protected final boolean isOverWorld() {
        return getPlayer().getWorld().equals(getPlayer().getServer().getOverworld());
    }

    abstract StatusEffect getEffect();

    public abstract String getId();
}
