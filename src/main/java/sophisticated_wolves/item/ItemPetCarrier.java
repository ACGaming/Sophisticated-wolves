package sophisticated_wolves.item;

import net.minecraft.block.BlockFence;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import sophisticated_wolves.entity.EntitySophisticatedWolf;

import java.util.List;

/**
 * GraveStone mod
 *
 * @author NightKosh
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
public class ItemPetCarrier extends Item {

    public ItemPetCarrier() {
        super();
        setUnlocalizedName("petcarrier");
        setCreativeTab(CreativeTabs.MISC);
        this.setMaxStackSize(1);
    }

    /**
     * Returns true if the item can be used on the given entity, e.g. shears on sheep.
     */
    @Override
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase entity, EnumHand hand) {
        if (!entity.world.isRemote && stack != null && (!stack.hasTagCompound() || !stack.getTagCompound().hasKey("ClassName"))) {
            if (entity instanceof EntityTameable) {
                EntityTameable pet = (EntityTameable) entity;
                if (pet.isTamed() && pet.getOwnerId() != null && pet.getOwnerId().equals(player.getUniqueID())) {
                    return getPetInfo(stack, player, entity, hand);
                }
            } else if (entity instanceof EntityChicken) {
                return getPetInfo(stack, player, entity, hand);
            } else if (entity instanceof EntityRabbit) {
                return getPetInfo(stack, player, entity, hand);
            } else if (entity instanceof EntityPig) {
                return getPetInfo(stack, player, entity, hand);
            }
        }
        return super.itemInteractionForEntity(stack, player, entity, hand);
    }

    private static boolean getPetInfo(ItemStack stack, EntityPlayer player, EntityLivingBase entity, EnumHand hand) {
        NBTTagCompound entityNbt = new NBTTagCompound();
        entity.writeToNBT(entityNbt);

        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setString("ClassName", entity.getClass().getName());
        nbt.setTag("MobData", entityNbt);

        stack.setTagCompound(nbt);
        player.setHeldItem(hand, stack);
        entity.setDead();

        return true;
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItem(hand);

        if (world.isRemote) {
            return EnumActionResult.SUCCESS;
        } else {
            if (stack != null && stack.hasTagCompound()) {
                NBTTagCompound nbt = stack.getTagCompound();
                if (nbt.hasKey("ClassName")) {
                    String className = nbt.getString("ClassName");

                    IBlockState block = world.getBlockState(pos);
                    double d0 = 0;
                    if (facing == EnumFacing.UP && block instanceof BlockFence) {
                        d0 = 0.5;
                    }
                    pos = pos.offset(facing);

                    EntityLiving entity = null;

                    if (className.equals(EntitySophisticatedWolf.class.getName())) {
                        entity = new EntitySophisticatedWolf(world);
                    } else if (className.equals(EntityWolf.class.getName())) {
                        entity = new EntityWolf(world);
                    } else if (className.equals(EntityOcelot.class.getName())) {
                        entity = new EntityOcelot(world);
                    } else if (className.equals(EntityChicken.class.getName())) {
                        entity = new EntityChicken(world);
                    } else if (className.equals(EntityRabbit.class.getName())) {
                        entity = new EntityRabbit(world);
                    } else if (className.equals(EntityPig.class.getName())) {
                        entity = new EntityPig(world);
                    }

                    if (entity != null) {
                        entity.readEntityFromNBT(nbt.getCompoundTag("MobData"));

                        entity.setLocationAndAngles(pos.getX() + 0.5, pos.getY() + d0, pos.getZ() + 0.5, MathHelper.wrapDegrees(world.rand.nextFloat() * 360), 0);
                        entity.rotationYawHead = entity.rotationYaw;
                        entity.renderYawOffset = entity.rotationYaw;
                        entity.onInitialSpawn(world.getDifficultyForLocation(new BlockPos(entity)), null);
                        world.spawnEntity(entity);
                        entity.playLivingSound();

                        if (entity instanceof EntityTameable) {
                            ((EntityTameable) entity).setOwnerId(player.getUniqueID());
                            ((EntityTameable) entity).setTamed(true);
                        }

                        stack.setTagCompound(new NBTTagCompound());

                        return EnumActionResult.SUCCESS;
                    }
                }
                return EnumActionResult.FAIL;
            }

            return EnumActionResult.SUCCESS;
        }
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
        if (stack != null && stack.hasTagCompound()) {
            NBTTagCompound nbt = stack.getTagCompound();
            if (nbt.hasKey("ClassName")) {
                tooltip.add(nbt.getString("ClassName"));
            }
        }

        super.addInformation(stack, playerIn, tooltip, advanced);
    }
}