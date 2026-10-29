package aiven.zomboidhealthsystem.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractBandageItem extends Item {
    private static final Logger log = LoggerFactory.getLogger(AbstractBandageItem.class);

    public static final int BANDAGE_COOLDOWN_TIME = 4 * 20;
    public static final int UNBANDAGE_COOLDOWN_TIME = 3 * 20;

    private static final ArrayList<AbstractBandageItem> bandageItems = new ArrayList<>();

    public AbstractBandageItem() {
        super(new FabricItemSettings().maxCount(8));
        bandageItems.add(this);
    }

    public abstract AbstractBandageItem getDirtyBandageItem();

    public abstract float getHealAmount();

    public abstract boolean isStopBleeding();

    public abstract AbstractBandageItem getCleanBandageItem();

    public abstract boolean isDirty();

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        super.use(level, player, interactionHand);
        ItemStack itemStack = player.getItemInHand(interactionHand);

        if(this.isDirty()) {
            BlockHitResult blockHitResult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
            if (blockHitResult.getType() == HitResult.Type.MISS) {
                return InteractionResultHolder.pass(itemStack);
            } else {
                if (blockHitResult.getType() == HitResult.Type.BLOCK) {
                    BlockPos blockPos = blockHitResult.getBlockPos();
                    if (!level.mayInteract(player, blockPos)) {
                        return InteractionResultHolder.pass(itemStack);
                    }

                    if (level.getFluidState(blockPos).is(FluidTags.WATER)) {
                        level.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.BUCKET_FILL, SoundSource.NEUTRAL, 1.0F, 1.0F);

                        if(!level.isClientSide()) {
                            itemStack.setCount(itemStack.getCount() - 1);
                            player.getInventory().add(getCleanBandageItem().getDefaultInstance());
                            player.getCooldowns().addCooldown(this, 20);
                        }

                        return InteractionResultHolder.success(itemStack);
                    }
                }
            }
        }

        return InteractionResultHolder.pass(itemStack);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
        if(isDirty()) {
            list.add(Component.translatable("zomboid-health-system.tooltip.right_click_on_water").withStyle(ChatFormatting.GOLD));
        }
    }

    public static List<AbstractBandageItem> getBandageItems() {
        return List.copyOf(bandageItems);
    }
}
