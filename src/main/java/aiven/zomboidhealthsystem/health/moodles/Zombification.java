package aiven.zomboidhealthsystem.health.moodles;

import aiven.zomboidhealthsystem.Config;
import aiven.zomboidhealthsystem.ModDamageTypes;
import aiven.zomboidhealthsystem.health.Health;
import aiven.zomboidhealthsystem.utility.Util;

import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.phys.Vec3;

public final class Zombification extends SimpleMoodle {

    public Zombification(Health health) {
        super(health);
    }

    @Override
    public String getId() {
        return "zombification";
    }

    @Override
    public void update() {
        if(this.getAmount() >= 1) {
            this.addAmount(1.0F / (4 * 60 * 20) * Health.UPDATE_FREQUENCY);
            if(this.getAmount() >= 1.25F) {
                getHealth().getExhaustion().setMultiplier(this, getAmount());
                getHealth().getHunger().setMultiplier(this, getAmount() * 2.50F);
                getHealth().getThirst().setMultiplier(this, getAmount() * 1.75F);
                if(once((int) (3 * 60 * 20 / getAmount()))) {
                    getHealth().addMobEffect(MobEffects.CONFUSION, 0, 10 * 20);
                }
                if(once((int) (3 * 60 * 20 / getAmount()))) {
                    getHealth().addMobEffect(MobEffects.BLINDNESS, 0, 10 * 20);
                }
                if(this.getAmount() >= 2.0F) {
                    Wet wet = getHealth().getWet();
                    if(wet.getAmount() <= 2.1F) {
                        wet.addAmount(1.0F / (30 * 20) * Health.UPDATE_FREQUENCY);
                    }
                    if(getAmount() >= 4.2F) {
                        Zombie zombieEntity = new Zombie(getPlayer().level());
                        Vec3 pos = getPlayer().position();
                        zombieEntity.moveTo(pos.x, pos.y, pos.z);
                        zombieEntity.setCustomName(getPlayer().getName());
                        getPlayer().level().addFreshEntity(zombieEntity);
                        getHealth().die(Util.getDamageSource(ModDamageTypes.ZOMBIFICATION, getPlayer().level()));
                    }
                }
            }
        }
    }

    @Override
    public void sleep(int sumTicks) {

    }

    public void onAttackByZombie(float damage) {
        if(this.getAmount() == 0) {
            float chance = (float) (Config.getInstance().zombification_chance * Math.cbrt(damage / 3));
            if (Util.random(chance)) {
                this.setAmount(1.0F);
            }
        }
    }
}
