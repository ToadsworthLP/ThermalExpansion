package thermalexpansion.block.machine;

import cofh.api.tileentity.IRedstoneControl.ControlMode;
import cofh.item.ItemBlockBase;
import cofh.util.EnergyHelper;
import cofh.util.RSControlHelper;
import cofh.util.SecurityHelper;
import cofh.util.StringHelper;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import thermalexpansion.util.ReconfigurableHelper;

public class ItemBlockMachine extends ItemBlockBase {

	public static ItemStack setDefaultTag(ItemStack container) {

		ReconfigurableHelper.setFacing(container, 3);
		ReconfigurableHelper.setSideCache(container, TileMachineBase.defaultSideConfig[container.getItemDamage()].defaultSides);
		RSControlHelper.setControl(container, ControlMode.DISABLED);
		EnergyHelper.setDefaultEnergyTag(container, 0);

		return container;
	}

	public ItemBlockMachine(Block block) {

		super(block);
		setHasSubtypes(true);
		setMaxDamage(0);
		setNoRepair();
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {

		return "tile.thermalexpansion.machine." + BlockMachine.NAMES[stack.getItemDamage()] + ".name";
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean check) {

		SecurityHelper.addOwnerInformation(stack, list);
		if (StringHelper.displayShiftForDetail && !StringHelper.isShiftKeyDown()) {
			list.add(StringHelper.shiftForInfo());
		}
		if (!StringHelper.isShiftKeyDown()) {
			return;
		}
		SecurityHelper.addAccessInformation(stack, list);
		RSControlHelper.addRSControlInformation(stack, list);
	}

}
