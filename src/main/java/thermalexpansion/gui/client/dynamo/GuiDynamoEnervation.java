package thermalexpansion.gui.client.dynamo;

import cofh.gui.element.ElementDualScaled;
import cofh.gui.element.ElementEnergyStored;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import thermalexpansion.core.TEProps;
import thermalexpansion.gui.container.dynamo.ContainerDynamoEnervation;

public class GuiDynamoEnervation extends GuiDynamoBase {

	static final ResourceLocation TEXTURE = new ResourceLocation(TEProps.PATH_GUI_DYNAMO + "DynamoEnervation.png");
	static final String INFO = "Extracts Redstone Flux directly from natural sources or from objects which store it.\n\nGeneration rate varies according to energy demand.";

	ElementDualScaled duration;

	public GuiDynamoEnervation(InventoryPlayer inventory, TileEntity tile) {

		super(new ContainerDynamoEnervation(inventory, tile), tile, inventory.player, TEXTURE);

		myInfo = INFO;
	}

	@Override
	public void initGui() {

		super.initGui();

		addElement(new ElementEnergyStored(this, 80, 18, myTile.getEnergyStorage()));
		duration = (ElementDualScaled) addElement(new ElementDualScaled(this, 115, 35).setSize(16, 16).setTexture(TEX_FLAME, 32, 16));
	}

	@Override
	protected void updateElementInformation() {

		super.updateElementInformation();

		duration.setQuantity(myTile.getScaledDuration(SPEED));
	}

}
