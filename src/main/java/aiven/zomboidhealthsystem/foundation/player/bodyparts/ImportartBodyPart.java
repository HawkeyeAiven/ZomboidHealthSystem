package aiven.zomboidhealthsystem.foundation.player.bodyparts;

import aiven.zomboidhealthsystem.ModDamageTypes;
import aiven.zomboidhealthsystem.foundation.player.Health;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKeys;

public abstract class ImportartBodyPart extends BodyPart {
    public ImportartBodyPart(Health health, float hp, PlayerEntity user, String name) {
        super(health, hp, user, name);
    }

    protected void nullHp() {
        this.getHealth().onDeath(new DamageSource(this.getPlayer().getWorld().getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(ModDamageTypes.SEVERE_DAMAGE)));
    }

    @Override
    public void update() {
        super.update();
        if (getHp() == 0) nullHp();
    }

    @Override
    public float damage(float amount, DamageSource source) {
        onDamage(amount);
        return super.damage(amount, source);
    }

    protected abstract void onDamage(float amount);
}