package mffs;

import java.util.Arrays;
import java.util.logging.Logger;

import mffs.base.BlockBase;
import mffs.base.BlockMFFS;
import mffs.base.ItemMFFS;
import mffs.block.BlockBiometricIdentifier;
import mffs.block.BlockCoercionDeriver;
import mffs.block.BlockForceField;
import mffs.block.BlockForceFieldProjector;
import mffs.block.BlockForceManipulator;
import mffs.block.BlockFortronCapacitor;
import mffs.block.BlockInterdictionMatrix;
import mffs.card.ItemCard;
import mffs.fortron.FortronHelper;
import mffs.fortron.FrequencyGrid;
import mffs.item.ItemRemoteController;
import mffs.item.card.ItemCardFrequency;
import mffs.item.card.ItemCardID;
import mffs.item.card.ItemCardInfinite;
import mffs.item.card.ItemCardLink;
import mffs.item.mode.ItemMode;
import mffs.item.mode.ItemModeCube;
import mffs.item.mode.ItemModeSphere;
import mffs.item.mode.ItemModeTube;
import mffs.item.module.ItemModule;
import mffs.item.module.interdiction.ItemModuleAntiFriendly;
import mffs.item.module.interdiction.ItemModuleAntiHostile;
import mffs.item.module.interdiction.ItemModuleAntiPersonnel;
import mffs.item.module.interdiction.ItemModuleConfiscate;
import mffs.item.module.interdiction.ItemModuleInterdictionMatrix;
import mffs.item.module.interdiction.ItemModuleWarn;
import mffs.item.module.projector.ItemModeCustom;
import mffs.item.module.projector.ItemModeCylinder;
import mffs.item.module.projector.ItemModePyramid;
import mffs.item.module.projector.ItemModuleDisintegration;
import mffs.item.module.projector.ItemModuleFusion;
import mffs.item.module.projector.ItemModuleManipulator;
import mffs.item.module.projector.ItemModuleShock;
import mffs.item.module.projector.ItemModuleSponge;
import mffs.item.module.projector.ItemModuleStablize;
import mffs.tileentity.TileEntityBiometricIdentifier;
import mffs.tileentity.TileEntityCoercionDeriver;
import mffs.tileentity.TileEntityForceField;
import mffs.tileentity.TileEntityForceFieldProjector;
import mffs.tileentity.TileEntityForceManipulator;
import mffs.tileentity.TileEntityFortronCapacitor;
import mffs.tileentity.TileEntityInterdictionMatrix;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import org.modstats.ModstatInfo;
import org.modstats.Modstats;

import universalelectricity.compatibility.Compatibility;
import universalelectricity.prefab.CustomDamageSource;
import universalelectricity.prefab.RecipeHelper;
import universalelectricity.prefab.TranslationHelper;
import universalelectricity.prefab.network.PacketManager;
import basiccomponents.api.BasicRegistry;
import calclavia.lib.UniversalRecipes;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid = ModularForceFieldSystem.ID, name = ModularForceFieldSystem.NAME, version = ModularForceFieldSystem.VERSION, useMetadata = true)
@NetworkMod(clientSideRequired = true, channels = { ModularForceFieldSystem.CHANNEL }, packetHandler = PacketManager.class)
@ModstatInfo(prefix = "mffs")
public class ModularForceFieldSystem
{
	/**
	 * General Variable Definition
	 */
	public static final String CHANNEL = "MFFS";
	public static final String ID = "MFFS";
	public static final String NAME = "Modular Force Field System";
	public static final String DOMAIN = "mffs";
	public static final String PREFIX = DOMAIN + ":";
	public static final String MAJOR_VERSION = "@MAJOR@";
	public static final String MINOR_VERSION = "@MINOR@";
	public static final String REVISION_VERSION = "@REVIS@";
	public static final String VERSION = MAJOR_VERSION + "." + MINOR_VERSION + "." + REVISION_VERSION;
	public static final String BUILD_VERSION = "@BUILD@";

	@Instance(ID)
	public static ModularForceFieldSystem instance;
	@Mod.Metadata(ID)
	public static ModMetadata metadata;
	@SidedProxy(clientSide = "mffs.ClientProxy", serverSide = "mffs.CommonProxy")
	public static CommonProxy proxy;

	public static final Logger LOGGER = Logger.getLogger(NAME);

	/**
	 * Directories Definition
	 */
	public static final String RESOURCE_DIRECTORY = "/assets/mffs/";
	public static final String LANGUAGE_DIRECTORY = RESOURCE_DIRECTORY + "languages/";

	public static final String TEXTURE_DIRECTORY = "textures/";
	public static final String BLOCK_DIRECTORY = TEXTURE_DIRECTORY + "blocks/";
	public static final String ITEM_DIRECTORY = TEXTURE_DIRECTORY + "items/";
	public static final String MODEL_DIRECTORY = TEXTURE_DIRECTORY + "models/";
	public static final String GUI_DIRECTORY = TEXTURE_DIRECTORY + "gui/";
	public static final ResourceLocation GUI_BUTTON = new ResourceLocation(DOMAIN, GUI_DIRECTORY + "gui_button.png");
	public static final ResourceLocation HOLOGAM_TEXTURE = new ResourceLocation(DOMAIN, BLOCK_DIRECTORY + "forceField.png");

	/**
	 * Machines
	 */
	public static BlockMFFS blockCoercionDeriver, blockFortronCapacitor, blockForceFieldProjector,
			blockBiometricIdentifier, blockInterdictionMatrix, blockForceManipulator;

	public static BlockBase blockForceField;

	/**
	 * Items
	 */
	public static Item itemFortron;
	public static Item itemRemoteController;
	public static Item itemFocusMatix;

	/**
	 * Cards
	 */
	public static ItemCard itemCardBlank, itemCardInfinite, itemCardFrequency, itemCardID,
			itemCardLink;

	/**
	 * Modes
	 */
	public static ItemMode itemModeCube, itemModeSphere, itemModeTube, itemModeCylinder,
			itemModePyramid, itemModeCustom;
	/**
	 * Modules
	 */
	// General Modules
	public static ItemModule itemModule, itemModuleSpeed, itemModuleCapacity, itemModuleTranslate,
			itemModuleScale, itemModuleRotate, itemModuleCollection, itemModuleInvert,
			itemModuleSilence;

	// Projector Modules
	public static ItemModule itemModuleFusion, itemModuleManipulator, itemModuleCamouflage,
			itemModuleDisintegration, itemModuleShock, itemModuleGlow, itemModuleSponge,
			itemModuleStablize;

	// Interdiction Matrix Modules
	public static ItemModule itemModuleAntiHostile, itemModuleAntiFriendly,
			itemModuleAntiPersonnel, itemModuleConfiscate, itemModuleWarn, itemModuleBlockAccess,
			itemModuleBlockAlter, itemModuleAntiSpawn;

	public static DamageSource damagefieldShock = new CustomDamageSource("fieldShock").setDamageBypassesArmor();

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		/**
		 * General Registry
		 */
		LOGGER.setParent(FMLLog.getLogger());
		Modstats.instance().getReporter().registerMod(this);
		NetworkRegistry.instance().registerGuiHandler(this, proxy);
		MinecraftForge.EVENT_BUS.register(new SubscribeEventHandler());
		Compatibility.initiate();

		Settings.load();

		/**
		 * Start instantiating blocks and items.
		 */
		Settings.CONFIGURATION.load();

		/**
		 * Blocks
		 */
		blockForceField = new BlockForceField(Settings.getNextBlockID());
		blockCoercionDeriver = new BlockCoercionDeriver(Settings.getNextBlockID());
		blockFortronCapacitor = new BlockFortronCapacitor(Settings.getNextBlockID());
		blockForceFieldProjector = new BlockForceFieldProjector(Settings.getNextBlockID());
		blockBiometricIdentifier = new BlockBiometricIdentifier(Settings.getNextBlockID());
		blockInterdictionMatrix = new BlockInterdictionMatrix(Settings.getNextBlockID());
		blockForceManipulator = new BlockForceManipulator(Settings.getNextBlockID());

		/**
		 * Items
		 */
		itemRemoteController = new ItemRemoteController(Settings.getNextItemID());
		itemFocusMatix = new ItemMFFS(Settings.getNextItemID(), "focusMatrix");

		/**
		 * Modes
		 */
		itemModeCube = new ItemModeCube(Settings.getNextItemID());
		itemModeSphere = new ItemModeSphere(Settings.getNextItemID());
		itemModeTube = new ItemModeTube(Settings.getNextItemID());
		itemModePyramid = new ItemModePyramid(Settings.getNextItemID());
		itemModeCylinder = new ItemModeCylinder(Settings.getNextItemID());
		itemModeCustom = new ItemModeCustom(Settings.getNextItemID());

		/**
		 * Modules
		 */
		itemModuleTranslate = new ItemModule(Settings.getNextItemID(), "moduleTranslate").setCost(1.6f);
		itemModuleScale = new ItemModule(Settings.getNextItemID(), "moduleScale").setCost(1.2f);
		itemModuleRotate = new ItemModule(Settings.getNextItemID(), "moduleRotate").setCost(0.1f);

		itemModuleSpeed = new ItemModule(Settings.getNextItemID(), "moduleSpeed").setCost(1f);
		itemModuleCapacity = new ItemModule(Settings.getNextItemID(), "moduleCapacity").setCost(0.5f);

		// Force Field Projector Modules
		itemModuleFusion = new ItemModuleFusion(Settings.getNextItemID());
		itemModuleManipulator = new ItemModuleManipulator(Settings.getNextItemID());
		itemModuleCamouflage = new ItemModule(Settings.getNextItemID(), "moduleCamouflage").setCost(1.5f).setMaxStackSize(1);
		itemModuleDisintegration = new ItemModuleDisintegration(Settings.getNextItemID());
		itemModuleShock = new ItemModuleShock(Settings.getNextItemID());
		itemModuleGlow = new ItemModule(Settings.getNextItemID(), "moduleGlow");
		itemModuleSponge = new ItemModuleSponge(Settings.getNextItemID());
		itemModuleStablize = new ItemModuleStablize(Settings.getNextItemID());

		/**
		 * Cards
		 */
		itemCardBlank = new ItemCard(Settings.getNextItemID(), "cardBlank");
		itemCardFrequency = new ItemCardFrequency(Settings.getNextItemID());
		itemCardLink = new ItemCardLink(Settings.getNextItemID());
		itemCardID = new ItemCardID(Settings.getNextItemID());
		itemCardInfinite = new ItemCardInfinite(Settings.getNextItemID());
		/**
		 * Interdiction Modules
		 */
		itemModuleAntiFriendly = new ItemModuleAntiFriendly(Settings.getNextItemID());
		itemModuleAntiHostile = new ItemModuleAntiHostile(Settings.getNextItemID());
		itemModuleAntiPersonnel = new ItemModuleAntiPersonnel(Settings.getNextItemID());
		itemModuleConfiscate = new ItemModuleConfiscate(Settings.getNextItemID());
		itemModuleWarn = new ItemModuleWarn(Settings.getNextItemID());
		itemModuleBlockAccess = new ItemModuleInterdictionMatrix(Settings.getNextItemID(), "moduleBlockAccess").setCost(10);
		itemModuleBlockAlter = new ItemModuleInterdictionMatrix(Settings.getNextItemID(), "moduleBlockAlter").setCost(15);
		itemModuleAntiSpawn = new ItemModuleInterdictionMatrix(Settings.getNextItemID(), "moduleAntiSpawn").setCost(10);
		itemModuleCollection = new ItemModule(Settings.getNextItemID(), "moduleCollection").setMaxStackSize(1).setCost(15);
		itemModuleInvert = new ItemModule(Settings.getNextItemID(), "moduleInvert").setMaxStackSize(1).setCost(15);
		itemModuleSilence = new ItemModule(Settings.getNextItemID(), "moduleSilence").setMaxStackSize(1).setCost(1);

		/**
		 * The Fortron Liquid
		 */
		itemFortron = new ItemMFFS(Settings.getNextItemID(), "fortron").setCreativeTab(null);
		FluidRegistry.registerFluid(new Fluid("fortron"));
		FortronHelper.FLUID_FORTRON = new FluidStack(FluidRegistry.getFluidID("fortron"), 0);

		Settings.CONFIGURATION.save();

		GameRegistry.registerBlock(blockForceField, blockForceField.getUnlocalizedName());
		GameRegistry.registerBlock(blockCoercionDeriver, blockCoercionDeriver.getUnlocalizedName());
		GameRegistry.registerBlock(blockFortronCapacitor, blockFortronCapacitor.getUnlocalizedName());
		GameRegistry.registerBlock(blockForceFieldProjector, blockForceFieldProjector.getUnlocalizedName());
		GameRegistry.registerBlock(blockBiometricIdentifier, blockBiometricIdentifier.getUnlocalizedName());
		GameRegistry.registerBlock(blockInterdictionMatrix, blockInterdictionMatrix.getUnlocalizedName());
		GameRegistry.registerBlock(blockForceManipulator, blockForceManipulator.getUnlocalizedName());

		GameRegistry.registerTileEntity(TileEntityForceField.class, blockForceField.getUnlocalizedName());
		GameRegistry.registerTileEntity(TileEntityCoercionDeriver.class, blockCoercionDeriver.getUnlocalizedName());
		GameRegistry.registerTileEntity(TileEntityFortronCapacitor.class, blockFortronCapacitor.getUnlocalizedName());
		GameRegistry.registerTileEntity(TileEntityForceFieldProjector.class, blockForceFieldProjector.getUnlocalizedName());
		GameRegistry.registerTileEntity(TileEntityBiometricIdentifier.class, blockBiometricIdentifier.getUnlocalizedName());
		GameRegistry.registerTileEntity(TileEntityInterdictionMatrix.class, blockInterdictionMatrix.getUnlocalizedName());
		GameRegistry.registerTileEntity(TileEntityForceManipulator.class, blockForceManipulator.getUnlocalizedName());

		proxy.preInit();
	}

	@EventHandler
	public void load(FMLInitializationEvent evt)
	{
		/**
		 * Load Basic Components
		 */
		BasicRegistry.register("itemIngotSteel");
		BasicRegistry.register("itemDustSteel");
		BasicRegistry.register("itemIngotCopper");
		BasicRegistry.register("blockOreCopper");
		BasicRegistry.register("itemWrench");

		/**
		 * Load language file(s)
		 */
		LOGGER.fine("Language(s) Loaded: " + TranslationHelper.loadLanguages(LANGUAGE_DIRECTORY, new String[] { "en_US", "zh_CN", "de_DE" }));

		/**
		 * Write metadata information
		 */
		metadata.modId = ID;
		metadata.name = NAME;
		metadata.description = "Modular Force Field System is a mod that adds force fields, high tech machinery and defensive measures to Minecraft.";
		metadata.url = "http://www.universalelectricity.com/mffs/";
		metadata.logoFile = "/mffs_logo.png";
		metadata.version = VERSION + "." + BUILD_VERSION;
		metadata.authorList = Arrays.asList(new String[] { "Calclavia" });
		metadata.credits = "Please visit the website.";
		metadata.autogenerated = false;
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent evt)
	{
		/**
		 * Add recipes
		 */
		UniversalRecipes.init();

		// -- General Items --
		// Focus Matrix
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemFocusMatix, 9), "RMR", "MDM", "RMR", 'M', UniversalRecipes.PRIMARY_METAL, 'D', Item.diamond, 'R', Item.redstone));

		// Remote Controller
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemRemoteController), "WWW", "MCM", "MCM", 'W', UniversalRecipes.WIRE, 'C', UniversalRecipes.BATTERY, 'M', UniversalRecipes.PRIMARY_METAL));

		// -- Machines --
		// Coercion Deriver
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockCoercionDeriver), "M M", "MFM", "MCM", 'C', UniversalRecipes.BATTERY, 'M', UniversalRecipes.PRIMARY_METAL, 'F', itemFocusMatix));
		// Fortron Capacitor
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockFortronCapacitor), "MFM", "FCF", "MFM", 'D', Item.diamond, 'C', UniversalRecipes.BATTERY, 'F', itemFocusMatix, 'M', UniversalRecipes.PRIMARY_METAL));
		// Force Field Projector
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockForceFieldProjector), " D ", "FFF", "MCM", 'D', Item.diamond, 'C', UniversalRecipes.BATTERY, 'F', itemFocusMatix, 'M', UniversalRecipes.PRIMARY_METAL));
		// Biometric Identifier
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockBiometricIdentifier), "FMF", "MCM", "FMF", 'C', itemCardBlank, 'M', UniversalRecipes.PRIMARY_METAL, 'F', itemFocusMatix));
		// Interdiction Matrix
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockInterdictionMatrix), "SSS", "FFF", "FEF", 'S', itemModuleShock, 'E', Block.enderChest, 'F', itemFocusMatix));
		// Force Manipulator
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockForceManipulator), "F F", "FMF", "F F", 'F', itemFocusMatix, 'M', UniversalRecipes.MOTOR));

		// -- Cards --
		// Blank
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemCardBlank), "PPP", "PMP", "PPP", 'P', Item.paper, 'M', UniversalRecipes.PRIMARY_METAL));
		// Link
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemCardLink), "BWB", 'B', itemCardBlank, 'W', UniversalRecipes.WIRE));
		// Frequency
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemCardFrequency), "WBW", 'B', itemCardBlank, 'W', UniversalRecipes.WIRE));
		// ID
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemCardID), "RBR", 'B', itemCardBlank, 'R', Item.redstone));

		// -- Modes --
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemModeSphere), " F ", "FFF", " F ", 'F', itemFocusMatix));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemModeCube), "FFF", "FFF", "FFF", 'F', itemFocusMatix));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemModeTube), "FFF", "   ", "FFF", 'F', itemFocusMatix));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemModePyramid), "F  ", "FF ", "FFF", 'F', itemFocusMatix));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemModeCylinder), "S", "S", "S", 'S', itemModeSphere));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemModeCustom), " C ", "TFP", " S ", 'S', itemModeSphere, 'C', itemModeCube, 'T', itemModeTube, 'P', itemModePyramid, 'F', itemFocusMatix));
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(itemModeCustom), new ItemStack(itemModeCustom)));

		// -- Modules --
		// -- -- General -- --
		// Speed
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemModuleSpeed, 2), "FFF", "RRR", "FFF", 'F', itemFocusMatix, 'R', Item.redstone));
		// Capacity
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemModuleCapacity, 2), "FCF", 'F', itemFocusMatix, 'C', UniversalRecipes.BATTERY));
		// Shock
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemModuleShock), "FWF", 'F', itemFocusMatix, 'W', UniversalRecipes.WIRE));
		// Sponge
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemModuleSponge), "BBB", "BFB", "BBB", 'F', itemFocusMatix, 'B', Item.bucketWater));
		// Disintegration
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemModuleDisintegration), " W ", "FBF", " W ", 'F', itemFocusMatix, 'W', UniversalRecipes.WIRE, 'B', UniversalRecipes.BATTERY));
		// Manipulator
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemModuleManipulator), "F", " ", "F", 'F', itemFocusMatix));
		// Camouflage
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemModuleCamouflage), "WFW", "FWF", "WFW", 'F', itemFocusMatix, 'W', new ItemStack(Block.cloth, 1, OreDictionary.WILDCARD_VALUE)));
		// Fusion
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemModuleFusion), "FJF", 'F', itemFocusMatix, 'J', itemModuleShock));
		// Scale
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemModuleScale, 2), "FRF", 'F', itemFocusMatix));
		// Translate
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemModuleTranslate, 2), "FSF", 'F', itemFocusMatix, 'S', itemModuleScale));
		// Rotate
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemModuleRotate, 4), "F  ", " F ", "  F", 'F', itemFocusMatix));
		// Glow
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemModuleGlow, 4), "GGG", "GFG", "GGG", 'F', itemFocusMatix, 'G', Block.glowStone));
		// Stabilizer
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemModuleStablize), "FDF", "PSA", "FDF", 'F', itemFocusMatix, 'P', Item.pickaxeDiamond, 'S', Item.shovelDiamond, 'A', Item.axeDiamond, 'D', Item.diamond));
		// Collection
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemModuleCollection), "F F", " H ", "F F", 'F', itemFocusMatix, 'H', Block.hopperBlock));
		// Invert
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemModuleInvert), "L", "F", "L", 'F', itemFocusMatix, 'L', Block.blockLapis));
		// Silence
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemModuleSilence), " N ", "NFN", " N ", 'F', itemFocusMatix, 'N', Block.music));

		// -- -- Interdiction Matrix -- --
		// Anti-Hostile
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemModuleAntiHostile), " R ", "GFB", " S ", 'F', itemFocusMatix, 'G', Item.gunpowder, 'R', Item.rottenFlesh, 'B', Item.bone, 'S', Item.ghastTear));
		// Anti-Friendly
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemModuleAntiFriendly), " R ", "GFB", " S ", 'F', itemFocusMatix, 'G', Item.porkCooked, 'R', new ItemStack(Block.cloth, 1, OreDictionary.WILDCARD_VALUE), 'B', Item.leather, 'S', Item.slimeBall));
		// Anti-Personnel
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemModuleAntiPersonnel), "BFG", 'F', itemFocusMatix, 'B', itemModuleAntiHostile, 'G', itemModuleAntiFriendly));
		// Confiscate
		RecipeHelper.addRecipe(new ShapedOreRecipe(new ItemStack(itemModuleConfiscate), "PEP", "EFE", "PEP", 'F', itemFocusMatix, 'E', Item.eyeOfEnder, 'P', Item.enderPearl), Settings.CONFIGURATION, true);
		// Warn
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemModuleWarn), "NFN", 'F', itemFocusMatix, 'N', Block.music));
		// Block Access
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemModuleBlockAccess), " C ", "BFB", " C ", 'F', itemFocusMatix, 'B', Block.blockIron, 'C', Block.chest));
		// Block Alter
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemModuleBlockAlter), " G ", "GFG", " G ", 'F', itemModuleBlockAccess, 'G', Block.blockGold));
		// Anti-Spawn
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemModuleAntiSpawn), " H ", "G G", " H ", 'H', itemModuleAntiHostile, 'G', itemModuleAntiFriendly));

		proxy.init();
	}

	@EventHandler
	public void serverStarting(FMLServerStartingEvent evt)
	{
		FrequencyGrid.reinitiate();
	}
}
