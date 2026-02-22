package aiven.zomboidhealthsystem.foundation.player.moodles;

import aiven.zomboidhealthsystem.foundation.player.Health;
import net.minecraft.entity.effect.StatusEffects;

public class Drunkenness extends Moodle {
    public Drunkenness(Health health) {
        super(health);
    }

    @Override
    public void update() {
        super.update();
        this.addAmount(-1.0F / (10 * 60 * 20) * Health.UPDATE_FREQUENCY);
        this.getHealth().getPain().addMultiplier(this, 1.0F / (this.getAmount() * 2 + 1));
        this.getHealth().getExhaustion().addMultiplier(this, getAmount() / 2 + 1);
        if(getAmount() >= 1) {
            if(once((int) (2.0F * 60 * 20 / getAmount()))) {
                this.getHealth().addStatusEffect(StatusEffects.NAUSEA, 0, 10 * 20);
            }
            if(once((int) (2.25F * 60 * 20 / getAmount()))) {
                this.getHealth().addStatusEffect(StatusEffects.BLINDNESS, 0, 5 * 20);
            }
            if(getAmount() >= 2.5F) {
                if(once((int) (1.5F * 60 * 20 / (getAmount() / 2.5F)))) {
                    getHealth().stumble(0);
                }
            }
        }
    }

    @Override
    public void sleep(int ticks) {
        super.sleep(ticks);
        this.addAmount(-1.0F / 1000 * ticks);
    }

    @Override
    public String getId() {
        return "drunkenness";
    }

    @Override
    public void setAmount(float amount) {
        this.amount = Math.min(Math.max(0, amount), 5);
    }
}
