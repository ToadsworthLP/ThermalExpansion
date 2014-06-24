package thermalexpansion.block.sponge;

import cofh.api.tileentity.ISidedTexture;
import cofh.network.CoFHPacket;
import cofh.network.ITilePacketHandler;
import cofh.render.IconRegistry;
import cofh.util.FluidHelper;
import cofh.util.ServerHelper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import thermalexpansion.block.TEBlocks;
import thermalexpansion.block.TileTEBase;
import thermalexpansion.core.TEProps;

public class TileSponge extends TileTEBase implements ITilePacketHandler, ISidedTexture {

	public static void initialize() {

		GameRegistry.registerTileEntity(TileSponge.class, "thermalexpansion.Sponge");
	}

	FluidStack fluid;
	boolean fullOnPlace = false;
	boolean full = false;

	public TileSponge() {

	}

	public TileSponge(int metadata) {

	}

	@Override
	public boolean canUpdate() {

		return false;
	}

	@Override
	public String getName() {

		return "tile.thermalexpansion.sponge." + BlockSponge.NAMES[getType()] + ".name";
	}

	@Override
	public int getType() {

		return BlockSponge.Types.BASIC.ordinal();
	}

	@Override
	public void blockBroken() {

		int i, j, k;

		Block query;
		for (i = xCoord - 1; i <= xCoord + 1; i++) {
			for (j = yCoord - 1; j <= yCoord + 1; j++) {
				for (k = zCoord - 1; k <= zCoord + 1; k++) {
					query = worldObj.getBlock(i, j, k);
					if (query == TEBlocks.blockAirBarrier) {
						worldObj.setBlockToAir(i, j, k);
					} else if (query == TEBlocks.blockSponge) {
						worldObj.scheduleBlockUpdate(i, j, k, TEBlocks.blockSponge, 1);
					}
				}
			}
		}
		for (i = xCoord - 2; i <= xCoord + 2; i += 4) {
			for (j = yCoord - 2; j <= yCoord + 2; j++) {
				for (k = zCoord - 2; k <= zCoord + 2; k++) {
					if (worldObj.getBlock(i, j, k) == TEBlocks.blockSponge) {
						worldObj.scheduleBlockUpdate(i, j, k, TEBlocks.blockSponge, 1);
					}
				}
			}
		}
		for (i = xCoord - 2; i <= xCoord + 2; i++) {
			for (j = yCoord - 2; j <= yCoord + 2; j += 4) {
				for (k = zCoord - 2; k <= zCoord + 2; k++) {
					if (worldObj.getBlock(i, j, k) == TEBlocks.blockSponge) {
						worldObj.scheduleBlockUpdate(i, j, k, TEBlocks.blockSponge, 1);
					}
				}
			}
		}
		for (i = xCoord - 2; i <= xCoord + 2; i++) {
			for (j = yCoord - 2; j <= yCoord + 2; j++) {
				for (k = zCoord - 2; k <= zCoord + 2; k += 4) {
					if (worldObj.getBlock(i, j, k) == TEBlocks.blockSponge) {
						worldObj.scheduleBlockUpdate(i, j, k, TEBlocks.blockSponge, 1);
					}
				}
			}
		}
	}

	public void absorb() {

		placeAir();
	}

	public FluidStack getFluid() {

		return fluid;
	}

	public void setFluid(FluidStack fluid) {

		this.fluid = fluid;
		fullOnPlace = true;
		full = true;
	}

	public void placeAir() {

		if (ServerHelper.isClientWorld(worldObj)) {
			return;
		}
		if (fullOnPlace) {
			return;
		}
		Block query;
		int queryMeta;
		Fluid queryFluid;
		int bucketCounter = 0;
		for (int i = xCoord - 1; i <= xCoord + 1; i++) {
			for (int j = yCoord - 1; j <= yCoord + 1; j++) {
				for (int k = zCoord - 1; k <= zCoord + 1; k++) {
					query = worldObj.getBlock(i, j, k);
					queryMeta = worldObj.getBlockMetadata(i, j, k);
					queryFluid = FluidHelper.lookupFluidForBlock(query);
					if (queryMeta == 0) {
						if (!full && queryFluid != null && queryFluid.getTemperature() < TEProps.MAGMATIC_TEMPERATURE) {
							if (fluid == null) {
								fluid = new FluidStack(queryFluid, 1000);
								bucketCounter = 1;
								worldObj.setBlock(i, j, k, TEBlocks.blockAirBarrier, 0, 3);
							} else if (fluid.fluidID == queryFluid.getID()) {
								bucketCounter++;
								worldObj.setBlock(i, j, k, TEBlocks.blockAirBarrier, 0, 3);
							}
						} else if (query.isAir(worldObj, i, j, k)) {
							worldObj.setBlock(i, j, k, TEBlocks.blockAirBarrier, 0, 3);
						}
					} else if (query.isAir(worldObj, i, j, k) || query.getMaterial().isLiquid()) {
						if (queryFluid != null && queryFluid.getTemperature() >= TEProps.MAGMATIC_TEMPERATURE) {
							// do nothing
						} else {
							worldObj.setBlock(i, j, k, TEBlocks.blockAirBarrier, 0, 3);
						}
					}
				}
			}
		}
		if (fluid != null) {
			fluid.amount = bucketCounter * 1000;
			full = true;
			sendUpdatePacket(Side.CLIENT);
		}
	}

	/* NETWORK METHODS */
	@Override
	public CoFHPacket getPacket() {

		CoFHPacket payload = super.getPacket();
		payload.addBool(full);
		payload.addFluidStack(fluid);
		return payload;
	}

	/* ITilePacketHandler */
	@Override
	public void handleTilePacket(CoFHPacket payload, boolean isServer) {

		if (ServerHelper.isClientWorld(worldObj)) {
			full = payload.getBool();
			fluid = payload.getFluidStack();
		} else {
			payload.getBool();
			payload.getFluidStack();
		}
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, getBlockType());
	}

	/* NBT METHODS */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);

		fullOnPlace = nbt.getBoolean("PlaceFull");
		fluid = FluidStack.loadFluidStackFromNBT(nbt);
		full = fluid != null;
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);

		nbt.setBoolean("PlaceFull", fullOnPlace);
		if (fluid != null) {
			fluid.writeToNBT(nbt);
		}
	}

	/* ISidedTexture */
	@Override
	public IIcon getTexture(int side, int pass) {

		return IconRegistry.getIcon("Sponge", full ? getType() + 8 : getType());
	}

}