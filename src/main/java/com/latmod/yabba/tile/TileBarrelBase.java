package com.latmod.yabba.tile;

import com.feed_the_beast.ftbl.lib.config.PropertyBool;
import com.feed_the_beast.ftbl.lib.tile.EnumSaveType;
import com.feed_the_beast.ftbl.lib.tile.TileBase;
import com.feed_the_beast.ftbl.lib.util.DataStorage;
import com.latmod.yabba.YabbaCommon;
import com.latmod.yabba.block.BlockStorageBarrelBase;
import com.latmod.yabba.block.Tier;
import com.latmod.yabba.item.IUpgrade;
import com.latmod.yabba.item.YabbaItems;
import com.latmod.yabba.item.upgrade.ItemUpgradeRedstone;
import com.latmod.yabba.util.UpgradeInst;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class TileBarrelBase extends TileBase implements ITickable
{
	public static final double BUTTON_SIZE = 0.24D;

	public Tier tier = Tier.WOOD;
	public Map<Item, UpgradeInst> upgrades = new HashMap<>();
	public String model = "";
	public String skin = "";
	public boolean isLocked = false;
	public PropertyBool alwaysDisplayData = new PropertyBool(false);
	public PropertyBool displayBar = new PropertyBool(false);

	protected String cachedItemName, cachedItemCount;
	protected float cachedRotationX, cachedRotationY;
	protected int prevItemCount = -1;

	public TileBarrelBase()
	{
	}

	@Override
	protected void writeData(NBTTagCompound nbt, EnumSaveType type)
	{
		if (tier != Tier.WOOD)
		{
			nbt.setString("Tier", tier.getName());
		}

		if (!upgrades.isEmpty())
		{
			NBTTagList nbt1 = new NBTTagList();

			for (UpgradeInst inst : upgrades.values())
			{
				NBTTagCompound nbt2 = new NBTTagCompound();
				nbt2.setTag("Item", inst.getStack().serializeNBT());

				if (!inst.getData().isEmpty())
				{
					NBTTagCompound nbt3 = new NBTTagCompound();
					inst.getData().serializeNBT(nbt3, type);
					nbt2.setTag("Data", nbt3);
				}
			}

			nbt.setTag("Upgrades", nbt1);
		}

		if (!model.isEmpty())
		{
			nbt.setString("Model", model);
		}

		if (!skin.isEmpty())
		{
			nbt.setString("Skin", skin);
		}

		if (isLocked)
		{
			nbt.setBoolean("Locked", true);
		}

		if (alwaysDisplayData.getBoolean())
		{
			nbt.setBoolean("AlwaysDisplayData", true);
		}

		if (displayBar.getBoolean())
		{
			nbt.setBoolean("DisplayBar", true);
		}
	}

	@Override
	protected void readData(NBTTagCompound nbt, EnumSaveType type)
	{
		tier = Tier.NAME_MAP.get(nbt.getString("Tier"));

		upgrades.clear();
		NBTTagList upgradeTag = nbt.getTagList("Upgrades", Constants.NBT.TAG_COMPOUND);

		for (int i = 0; i < upgradeTag.tagCount(); i++)
		{
			NBTTagCompound nbt1 = upgradeTag.getCompoundTagAt(i);
			ItemStack stack = new ItemStack(nbt1.getCompoundTag("Item"));

			if (stack.getItem() instanceof IUpgrade)
			{
				UpgradeInst inst = new UpgradeInst(stack);

				if (!inst.getData().isEmpty())
				{
					inst.getData().deserializeNBT(nbt, type);
				}

				upgrades.put(stack.getItem(), inst);
			}
		}

		model = nbt.getString("Model");
		skin = nbt.getString("Skin");
		isLocked = nbt.getBoolean("Locked");
		alwaysDisplayData.setBoolean(nbt.getBoolean("AlwaysDisplayData"));
		displayBar.setBoolean(nbt.getBoolean("DisplayBar"));
	}

	@Override
	public IBlockState createState(IBlockState state)
	{
		if (state instanceof IExtendedBlockState)
		{
			return ((IExtendedBlockState) state).withProperty(BlockStorageBarrelBase.MODEL, model).withProperty(BlockStorageBarrelBase.SKIN, skin);
		}

		return state;
	}

	public void markBarrelDirty(boolean majorChange)
	{
		if (majorChange)
		{
			prevItemCount = -1;
		}
	}

	public void clearCachedData()
	{
		cachedItemName = null;
		cachedItemCount = null;
		cachedRotationX = -1F;
		cachedRotationY = -1F;
	}

	@Override
	public void onUpdatePacket()
	{
		prevItemCount = -1;
	}

	@Override
	public void update()
	{
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate)
	{
		clearCachedData();
		return oldState.getBlock() != newSate.getBlock();
	}

	@Override
	public void markDirty()
	{
		//barrel.markBarrelDirty();
	}

	public float getRotationAngleX()
	{
		if (cachedRotationX == -1F)
		{
			IBlockState state = world.getBlockState(pos);

			if (!(state.getBlock() instanceof BlockStorageBarrelBase))
			{
				return 0F;
			}

			cachedRotationX = state.getValue(BlockStorageBarrelBase.ROTATION).ordinal() * 90F;
		}

		return cachedRotationX;
	}

	public float getRotationAngleY()
	{
		if (cachedRotationY == -1F)
		{
			IBlockState state = world.getBlockState(pos);

			if (!(state.getBlock() instanceof BlockStorageBarrelBase))
			{
				return 0F;
			}

			cachedRotationY = state.getValue(BlockHorizontal.FACING).getHorizontalAngle() + 180F;
		}

		return cachedRotationY;
	}

	public static float getX(EnumFacing facing, float hitX, float hitZ)
	{
		switch (facing)
		{
			case EAST:
				return 1F - hitZ;
			case WEST:
				return hitZ;
			case NORTH:
				return 1F - hitX;
			case SOUTH:
				return hitX;
			default:
				return 0.5F;
		}
	}

	public boolean setTier(Tier t)
	{
		if (tier != t)
		{
			tier = t;
			markBarrelDirty(true);
			return true;
		}

		return false;
	}

	public boolean setSkin(String v)
	{
		if (v.equals(YabbaCommon.DEFAULT_SKIN_ID))
		{
			v = "";
		}

		if (!skin.equals(v))
		{
			skin = v;
			markBarrelDirty(true);
			return true;
		}

		return false;
	}

	public boolean setModel(String v)
	{
		if (v.equals(YabbaCommon.DEFAULT_MODEL_ID))
		{
			v = "";
		}

		if (!model.equals(v))
		{
			model = v;
			markBarrelDirty(true);
			return true;
		}

		return false;
	}

	public DataStorage getUpgradeData(Item upgrade)
	{
		return DataStorage.EMPTY;
	}

	public boolean hasUpgrade(Item upgrade)
	{
		return upgrades.containsKey(upgrade);
	}

	public boolean removeUpgrade(Item upgrade, boolean simulate)
	{
		if (hasUpgrade(upgrade))
		{
			if (!simulate)
			{
				upgrades.remove(upgrade);
			}

			return true;
		}

		return false;
	}

	public boolean addUpgrade(ItemStack upgrade, boolean simulate)
	{
		if (upgrade.getItem() instanceof IUpgrade && !hasUpgrade(upgrade.getItem()))
		{
			if (!simulate)
			{
				upgrades.put(upgrade.getItem(), new UpgradeInst(upgrade));
			}

			return true;
		}

		return false;
	}

	public boolean canConnectRedstone(@Nullable EnumFacing facing)
	{
		return getUpgradeData(YabbaItems.UPGRADE_REDSTONE_OUT) instanceof ItemUpgradeRedstone.Data;
	}

	public int redstoneOutput(EnumFacing facing)
	{
		return 0;
	}

	public String getItemDisplayName()
	{
		return "ERROR";
	}

	public String getItemDisplayCount(boolean isSneaking, boolean isCreative, boolean infinite)
	{
		return "ERROR";
	}

	@Override
	public boolean shouldDrop()
	{
		return super.shouldDrop() || !skin.isEmpty() || !model.isEmpty() || !upgrades.isEmpty() || tier != Tier.WOOD || isLocked;
	}
}