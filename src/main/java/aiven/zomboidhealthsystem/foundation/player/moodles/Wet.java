package aiven.zomboidhealthsystem.foundation.player.moodles;

import aiven.zomboidhealthsystem.Config;
import aiven.zomboidhealthsystem.ModStatusEffects;
import aiven.zomboidhealthsystem.foundation.player.Health;
import aiven.zomboidhealthsystem.foundation.utility.Util;
import net.minecraft.block.Blocks;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class Wet extends Moodle {
    private static final float MULTIPLIER = Config.WET_MULTIPLIER.getValue();
    private static final float SPEED = 1.0F / (45 * 20) * MULTIPLIER * Health.UPDATE_FREQUENCY;
    private static final float MAX_AMOUNT = 3.5F;

    public Wet(Health health) {
        super(health);
    }

    @Override
    public void update() {
        super.update();

        PlayerEntity player = getHealth().getPlayer();
        World world = player.getWorld();

        if(world.getBlockState(player.getBlockPos()).getBlock().equals(Blocks.WATER) && player.getControllingVehicle() == null){

            this.setAmount(MAX_AMOUNT);

        } else if(world.hasRain(player.getBlockPos())){

            if(Util.getArmorCount(getPlayer()) < 3) {
                this.addAmount(SPEED);
            }

        } else {

            this.addAmount(-SPEED);

        }
    }

    @Override
    public void onSleep() {
        super.onSleep();
        setAmount(0);
    }

    @Override
    StatusEffect getEffect() {
        return ModStatusEffects.WET;
    }

    @Override
    public void setAmount(float amount) {
        if(amount > MAX_AMOUNT) {
            this.amount = MAX_AMOUNT;
        } else if(amount > 0) {
            this.amount = amount;
        } else {
            this.amount = 0;
        }
    }

    @Override
    public String getId() {
        return "wet";
    }
}