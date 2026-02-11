package aiven.zomboidhealthsystem.foundation.items;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.Blocks;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class BandageItem extends Item {
    public static final int BANDAGE_COOLDOWN_TIME = 4 * 20;
    public static final int UNBANDAGE_COOLDOWN_TIME = 2 * 20;
    public static final ArrayList<BandageItem> bandageItems = new ArrayList<>();

    public BandageItem() {
        super(new FabricItemSettings().maxCount(8));
        bandageItems.add(this);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        if(this.isDirty()) {
            BlockHitResult blockHitResult = raycast(world, user, RaycastContext.FluidHandling.SOURCE_ONLY);

            if (blockHitResult.getType() == HitResult.Type.MISS) {
                return TypedActionResult.pass(itemStack);
            } else {
                if (blockHitResult.getType() == HitResult.Type.BLOCK) {
                    BlockPos blockPos = blockHitResult.getBlockPos();
                    if (!world.canPlayerModifyAt(user, blockPos)) {
                        return TypedActionResult.pass(itemStack);
                    }

                    if (world.getFluidState(blockPos).isIn(FluidTags.WATER)) {

                        itemStack.decrement(1);

                        if(!user.giveItemStack(getCleanBandageItem().getDefaultStack())) {
                            user.dropStack(getCleanBandageItem().getDefaultStack());
                        }

                        user.getItemCooldownManager().set(this, 20);

                        user.playSound(SoundEvents.ITEM_BUCKET_FILL, 1F, 1F);

                        return TypedActionResult.success(user.getStackInHand(hand));
                    }
                }

                return TypedActionResult.pass(itemStack);
            }

        } else {
            return TypedActionResult.pass(itemStack);
        }
    }


    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if(this.isDirty()){
            tooltip.add(Text.translatable("zomboidhealthsystem.tooltip.right_click_on_water").formatted(Formatting.GOLD));
        }
    }

    public abstract BandageItem getDirtyBandageItem();

    public abstract float getHealAmount();

    public boolean isStopBleeding(){
        return true;
    }

    public BandageItem getCleanBandageItem(){
        return this;
    }

    public boolean isDirty(){
        return this.getDirtyBandageItem() == this;
    }
}
