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
    private static final float SPEED = 1.0F / (45 * 20) * Config.WET_MULTIPLIER.getValue() * Health.UPDATE_FREQUENCY;
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

        if(getHealth().getTemperature().getAmount() > Temperature.AVERAGE_TEMPERATURE_BODY + 1.0F && this.getAmount() < 2.1F) {
            this.addAmount(0.035F / 20 * Health.UPDATE_FREQUENCY);
        }

        getHealth().getTemperature().addHeat(getAmount() * -2.5F);
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
        this.amount = Math.min(Math.max(amount, 0), MAX_AMOUNT);
    }

    @Override
    public String getId() {
        return "wet";
    }
}