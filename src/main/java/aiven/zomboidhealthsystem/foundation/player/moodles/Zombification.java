package aiven.zomboidhealthsystem.foundation.player.moodles;

import aiven.zomboidhealthsystem.Config;
import aiven.zomboidhealthsystem.ModDamageTypes;
import aiven.zomboidhealthsystem.foundation.player.Health;
import aiven.zomboidhealthsystem.foundation.utility.Util;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.ZombieEntity;

public class Zombification extends Moodle {
    public Zombification(Health health) {
        super(health);
    }

    @Override
    public void update() {
        super.update();
        if(this.getAmount() >= 1) {
            this.addAmount(1.0F / (4 * 60 * 20) * Health.UPDATE_FREQUENCY);
            if(this.getAmount() >= 1.25F) {
                getHealth().getExhaustion().addMultiplier(this, getAmount());
                getHealth().getHunger().addMultiplier(this, getAmount() * 2.50F);
                getHealth().getThirst().addMultiplier(this, getAmount() * 1.75F);
                if(once((int) (3 * 60 * 20 / getAmount()))) {
                    getHealth().addStatusEffect(StatusEffects.NAUSEA, 0, 10 * 20);
                }
                if(once((int) (3 * 60 * 20 / getAmount()))) {
                    getHealth().addStatusEffect(StatusEffects.BLINDNESS, 0, 10 * 20);
                }
                if(this.getAmount() >= 2.0F) {
                    if(once((int) (3 * 60 * 20 / getAmount()))) {
                        getHealth().stumble(0);
                    }
                    Wet wet = getHealth().getWet();
                    if(wet.getAmount() <= 2.1F) {
                        wet.addAmount(1.0F / (30 * 20) * Health.UPDATE_FREQUENCY);
                    }
                    if(getAmount() >= 4.2F) {
                        ZombieEntity zombieEntity = new ZombieEntity(getPlayer().getWorld());
                        zombieEntity.setCustomName(getPlayer().getName());
                        zombieEntity.setPosition(getPlayer().getPos());
                        getPlayer().getWorld().spawnEntity(zombieEntity);
                        getHealth().onDeath(Util.getDamageSource(ModDamageTypes.ZOMBIFICATION, getPlayer().getWorld()));
                    }
                }
            }
        }
    }

    public void onAttackByZombie(float damage) {
        if(this.getAmount() == 0) {
            float chance = (float) (Config.ZOMBIFICATION_CHANCE.getValue() * Math.cbrt(damage / 3));
            if (Util.random(chance)) {
                this.setAmount(1.0F);
            }
        }
    }

    @Override
    public String getId() {
        return "zombification";
    }
}
