package cofh.thermalexpansion.plugins;

import cofh.core.util.helpers.ItemHelper;
import cofh.core.util.helpers.StringHelper;
import cofh.thermalexpansion.ThermalExpansion;
import cofh.thermalexpansion.util.managers.TapperManager;
import cofh.thermalexpansion.util.managers.machine.CrucibleManager;
import cofh.thermalexpansion.util.managers.machine.InsolatorManager;
import cofh.thermalexpansion.util.managers.machine.InsolatorManager.Type;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.Locale;

public class TConstructPlugin {

	private TConstructPlugin() {

	}

	public static final String MOD_ID = "tconstruct";
	public static final String MOD_NAME = "Tinkers' Construct";

	public static void initialize() {

		String category = "Plugins";
		String comment = "If TRUE, support for " + MOD_NAME + " is enabled.";

		boolean enable = ThermalExpansion.CONFIG.getConfiguration().getBoolean(MOD_NAME, category, true, comment);

		if (!enable || !Loader.isModLoaded(MOD_ID)) {
			return;
		}
		try {
			ItemStack slimeCongealed = getBlockStack("slime_congealed", 1, 0);
			ItemStack slimeCongealedMagma = getBlockStack("slime_congealed", 1, 4);

			ItemStack saplingSlimeBlue = getItem("slime_sapling", 1, 0);
			ItemStack saplingSlimePurple = getItem("slime_sapling", 1, 1);
			ItemStack saplingSlimeMagma = getItem("slime_sapling", 1, 2);

			Block log = getBlock("slime_congealed");

			Block leaves = getBlock("slime_leaves");

			Fluid blueslime = FluidRegistry.getFluid("blueslime");
			Fluid emerald = FluidRegistry.getFluid("emerald");

			/* CRUCIBLE */
			{
				addRecipeSet("iron");
				addRecipeSet("gold");

				if (emerald != null) {
					CrucibleManager.addRecipe(4000, new ItemStack(Items.EMERALD), new FluidStack(emerald, 666));
					CrucibleManager.addRecipe(4000 * 2, new ItemStack(Blocks.EMERALD_ORE), new FluidStack(emerald, 666 * 2));
					CrucibleManager.addRecipe(4000 * 8, new ItemStack(Blocks.EMERALD_BLOCK), new FluidStack(emerald, 666 * 9));
				}

				addRecipeSet("copper");
				addRecipeSet("tin");
				addRecipeSet("silver");
				addRecipeSet("lead");
				addRecipeSet("aluminum");
				addRecipeSet("nickel");
				addRecipeSet("platinum");
				addRecipeSet("iridium");

				addRecipeSet("steel");
				addRecipeSet("electrum");
				addRecipeSet("invar");
				addRecipeSet("bronze");
				addRecipeSet("constantan");
				addRecipeSet("signalum");
				addRecipeSet("lumium");
				addRecipeSet("enderium");

				addRecipeSet("ardite");
				addRecipeSet("cobalt");
				addRecipeSet("manyullyn");
			}

			/* INSOLATOR */
			{
				InsolatorManager.addDefaultTreeRecipe(saplingSlimeBlue, ItemHelper.cloneStack(slimeCongealed, 4), saplingSlimeBlue, 50, false, Type.MYCELIUM_TREE);
				InsolatorManager.addDefaultTreeRecipe(saplingSlimePurple, ItemHelper.cloneStack(slimeCongealed, 4), saplingSlimePurple, 50, false, Type.MYCELIUM_TREE);
				InsolatorManager.addDefaultTreeRecipe(saplingSlimeMagma, ItemHelper.cloneStack(slimeCongealedMagma, 4), saplingSlimeMagma, 50, false, Type.MYCELIUM_TREE);
			}

			/* TAPPER */
			{
				TapperManager.addMapping(slimeCongealed, new FluidStack(blueslime, 25));
				TapperManager.addMapping(slimeCongealedMagma, new FluidStack(blueslime, 25));

				addLeafMapping(log, 0, leaves, 0);
				addLeafMapping(log, 0, leaves, 1);
				addLeafMapping(log, 4, leaves, 2);
			}

			ThermalExpansion.LOG.info("Thermal Expansion: " + MOD_NAME + " Plugin Enabled.");
		} catch (Throwable t) {
			ThermalExpansion.LOG.error("Thermal Expansion: " + MOD_NAME + " Plugin encountered an error:", t);
		}
	}

	/* HELPERS */
	private static ItemStack getBlockStack(String name, int amount, int meta) {

		Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(MOD_ID + ":" + name));
		return block != null ? new ItemStack(block, amount, meta) : ItemStack.EMPTY;
	}

	private static ItemStack getBlockStack(String name, int amount) {

		return getBlockStack(name, amount, 0);
	}

	private static Block getBlock(String name) {

		return ForgeRegistries.BLOCKS.getValue(new ResourceLocation(MOD_ID + ":" + name));
	}

	private static ItemStack getItem(String name, int amount, int meta) {

		Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(MOD_ID + ":" + name));
		return item != null ? new ItemStack(item, amount, meta) : ItemStack.EMPTY;
	}

	private static ItemStack getItem(String name) {

		return getItem(name, 1, 0);
	}

	private static void addLeafMapping(Block logBlock, int logMeta, Block leafBlock, int leafMeta) {

		IBlockState logState = logBlock.getStateFromMeta(logMeta);

		for (Boolean check_decay : BlockLeaves.CHECK_DECAY.getAllowedValues()) {
			IBlockState leafState = leafBlock.getStateFromMeta(leafMeta).withProperty(BlockLeaves.DECAYABLE, Boolean.TRUE).withProperty(BlockLeaves.CHECK_DECAY, check_decay);
			TapperManager.addLeafMappingDirect(logState, leafState);
		}
	}

	private static boolean addRecipeSet(String oreType) {

		if (oreType == null || oreType.isEmpty()) {
			return false;
		}
		Fluid fluid = FluidRegistry.getFluid(oreType.toLowerCase(Locale.ENGLISH));

		if (fluid == null) {
			return false;
		}
		oreType = StringHelper.titleCase(oreType);

		int energy = 4000;
		int fluidIngot = 144;

		String nuggetName = NUGGET + oreType;
		String ingotName = INGOT + oreType;
		String oreName = ORE + oreType;
		String blockName = BLOCK + oreType;
		String dustName = DUST + oreType;
		String plateName = PLATE + oreType;

		ItemStack nugget = ItemHelper.getOre(nuggetName);
		ItemStack ingot = ItemHelper.getOre(ingotName);
		ItemStack ore = ItemHelper.getOre(oreName);
		ItemStack block = ItemHelper.getOre(blockName);
		ItemStack dust = ItemHelper.getOre(dustName);
		ItemStack plate = ItemHelper.getOre(plateName);

		if (!nugget.isEmpty()) {
			CrucibleManager.addRecipe(energy / 8, nugget, new FluidStack(fluid, fluidIngot / 9));
		}
		if (ingot.isEmpty()) {
			CrucibleManager.addRecipe(energy, ingot, new FluidStack(fluid, fluidIngot));
		}
		if (!ore.isEmpty()) {
			CrucibleManager.addRecipe(energy * 2, ore, new FluidStack(fluid, fluidIngot * 2));
		}
		if (!block.isEmpty()) {
			CrucibleManager.addRecipe(energy * 8, block, new FluidStack(fluid, fluidIngot * 9));
		}
		if (!dust.isEmpty()) {
			CrucibleManager.addRecipe(energy / 2, dust, new FluidStack(fluid, fluidIngot));
		}
		if (!plate.isEmpty()) {
			CrucibleManager.addRecipe(energy, plate, new FluidStack(fluid, fluidIngot));
		}
		return true;
	}

	private static final String NUGGET = "nugget";
	private static final String INGOT = "ingot";
	private static final String ORE = "ore";
	private static final String BLOCK = "block";
	private static final String DUST = "dust";
	private static final String PLATE = "plate";

}
