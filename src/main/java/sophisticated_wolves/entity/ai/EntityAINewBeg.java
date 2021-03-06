package sophisticated_wolves.entity.ai;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.world.World;
import sophisticated_wolves.entity.EntitySophisticatedWolf;

/**
 * Sophisticated Wolves
 *
 * @author metroidfood
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
public class EntityAINewBeg extends EntityAIBase {
    private EntitySophisticatedWolf wolf;
    private EntityPlayer player;
    private World world;
    private float minPlayerDistance;
    private int randomBeg;
    private PathNavigate petPathfinder;

    public EntityAINewBeg(EntitySophisticatedWolf wolf, float minPlayerDistance) {
        this.wolf = wolf;
        this.world = wolf.world;
        this.minPlayerDistance = minPlayerDistance;
        this.petPathfinder = this.wolf.getNavigator();
        this.setMutexBits(3);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    @Override
    public boolean shouldExecute() {
        this.player = this.world.getClosestPlayerToEntity(this.wolf, (double) this.minPlayerDistance);
        return this.player != null && this.isHoldingMeat(this.player);
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    @Override
    public boolean shouldContinueExecuting() {
        if (this.player.isEntityAlive()) {
            if (this.wolf.getDistance(this.player) <= (this.minPlayerDistance * this.minPlayerDistance)) {
                return this.randomBeg > 0 && this.isHoldingMeat(this.player);
            }
        }
        return false;
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    @Override
    public void startExecuting() {
        this.petPathfinder.clearPath();
        this.wolf.setBegging(true);
        this.randomBeg = 40 + this.wolf.getRNG().nextInt(40);
    }

    /**
     * Resets the task
     */
    @Override
    public void resetTask() {
        this.wolf.setBegging(false);
        this.player = null;
    }

    /**
     * Updates the task
     */
    @Override
    public void updateTask() {
        this.wolf.getLookHelper().setLookPosition(this.player.posX, this.player.posY + (double) this.player.getEyeHeight(), this.player.posZ, 10, (float) this.wolf.getVerticalFaceSpeed());
        --this.randomBeg;
    }

    private boolean isHoldingMeat(EntityPlayer player) {
        ItemStack stack = player.inventory.getCurrentItem();
        if (stack == null) {
            return false;
        }

        if (!this.wolf.isTamed() && stack.getItem().equals(Items.BONE)) {
            return true;
        } else {
            return this.wolf.isTamed() && this.wolf.isFavoriteFood(stack);
        }
    }
}
