package aiven.zomboidhealthsystem.foundation.player.bodyparts;

import aiven.zomboidhealthsystem.ModDamageTypes;
import aiven.zomboidhealthsystem.foundation.player.Health;
import aiven.zomboidhealthsystem.foundation.utility.EffectAmplifiers;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKeys;

public class Body extends ImportartBodyPart {
    public Body(Health health, float hp, PlayerEntity user) {
        super(health, hp, user, Health.BODY_ID);
    }

    @Override
    protected void onDamage(float amount) {
        if (amount >= 1) {
            this.getHealth().damagePlayerHp(new DamageSource(this.getPlayer().getWorld().getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(ModDamageTypes.BLEEDING)),amount * 5f);
        }
    }

    @Override
    public void addEffectAmplifier(EffectAmplifiers effectAmplifiers) {
        effectAmplifiers.slownessAmplifier += (getMaxHp() - getHp()) / 2;
    }

    @Override
    public float getMaxHp() {
        return Health.MAX_BODY_HP;
    }

    @Override
    public void update() {
        super.update();
        if (this.getMaxHp() / 2 >= this.getHp()) {
            if(Health.once(3 * 60 * 20)){
                getHealth().addStatusEffect(StatusEffects.NAUSEA, 10 * 20, 0);
            }
        }
    }
}
