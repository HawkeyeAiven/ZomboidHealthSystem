package aiven.zomboidhealthsystem.health.bodyparts;

import aiven.zomboidhealthsystem.ModDamageTypes;
import aiven.zomboidhealthsystem.health.Health;

import aiven.zomboidhealthsystem.utility.Util;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;

public abstract class ImportartBodyPart extends BodyPart {
    public ImportartBodyPart(Health health, float hp, Player player, String name) {
        super(health, hp, player, name);
    }

    protected void nullHp() {
        DamageSource source = Util.getDamageSource(ModDamageTypes.SEVERE_DAMAGE, player.level());
        this.getHealth().die(source);
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