package aiven.zomboidhealthsystem.foundation.player.moodles;

import aiven.zomboidhealthsystem.Config;
import aiven.zomboidhealthsystem.ModDamageTypes;
import aiven.zomboidhealthsystem.ModStatusEffects;
import aiven.zomboidhealthsystem.foundation.player.Health;
import aiven.zomboidhealthsystem.foundation.utility.Util;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;

public class Thirst extends Moodle {

    private final DamageSource source = Util.getDamageSource(ModDamageTypes.THIRST, getPlayer().getWorld());
    private final float speed = 1.0F / 30000.0F;

    public Thirst(Health health) {
        super(health);
    }

    @Override
    StatusEffect getEffect() {
        return ModStatusEffects.THIRST;
    }

    @Override
    public String getId() {
        return "thirst";
    }

    public void setAmount(float amount) {
        this.amount = Math.max(0, amount);
    }

    @Override
    public void update() {
        super.update();
        this.addAmount(speed * Config.THIRST_MULTIPLIER.getValue() * Health.UPDATE_FREQUENCY);

        this.getHealth().getExhaustion().addMultiplier(this, 1.0F);

        if (amount >= 1) {
            this.getHealth().getExhaustion().addMultiplier(this, getAmount());
            if (amount >= 2) {

                if (random(4 * 60 * 20)) {
                    this.getHealth().addStatusEffect(StatusEffects.BLINDNESS, 0, 10 * 20);
                }

                if (amount >= 2.4F) {
                    this.getHealth().addStatusEffect(StatusEffects.SLOWNESS, (int) (amount - 1.6F), 15 * 20);
                    if (random(2 * 60 * 20)) {
                        this.getHealth().stumble(0);
                    }

                    if (amount >= 2.8F) {
                        this.getHealth().addStatusEffect(StatusEffects.BLINDNESS, 0, 15 * 20);
                        if (random(60 * 20)) {
                            this.getHealth().stumble(0);
                        }

                        if (amount >= 3.1F) {
                            this.getHealth().onDeath(this.source);
                        }
                    }
                }
            }
        }
    }

    public void drink(float amount, boolean clean) {
        if (clean) {
            this.addAmount(-amount);
        }
        this.getHealth().getTemperature().cool(0.5F);
    }

    @Override
    public void onSleep(){
        this.setAmount(this.getAmount() + 0.35f);
    }

    public float getSpeed() {
        return speed;
    }
}
