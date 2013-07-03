package mffs.item.module.projector;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import mffs.IDelayedEventHandler;
import mffs.MFFSHelper;
import mffs.ModularForceFieldSystem;
import mffs.api.Blacklist;
import mffs.api.IProjector;
import mffs.base.TileEntityBase.TilePacketType;
import mffs.base.TileEntityInventory;
import mffs.event.BlockDropDelayedEvent;
import mffs.event.BlockInventoryDropDelayedEvent;
import mffs.item.module.ItemModule;
import mffs.tileentity.TileEntityForceFieldProjector;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFluid;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.liquids.ILiquid;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.network.PacketManager;

public class ItemModuleDisintegration extends ItemModule
{
	private int blockCount = 0;

	public ItemModuleDisintegration(int id)
	{
		super(id, "moduleDisintegration");
		this.setMaxStackSize(1);
		this.setCost(20);
	}

	@Override
	public boolean onProject(IProjector projector, Set<Vector3> fields)
	{
		this.blockCount = 0;
		return false;
	}

	@Override
	public int onProject(IProjector projector, Vector3 position)
	{
		if (projector.getTicks() % 40 == 0)
		{
			TileEntity tileEntity = (TileEntity) projector;
			int blockID = position.getBlockID(tileEntity.worldObj);
			Block block = Block.blocksList[blockID];

			if (block != null)
			{
				if (projector.getModuleCount(ModularForceFieldSystem.itemModuleCamouflage) > 0)
				{
					List<Block> filterBlocks = new ArrayList<Block>();

					for (int i : projector.getModuleSlots())
					{
						ItemStack checkStack = projector.getStackInSlot(i);
						Block filterBlock = MFFSHelper.getFilterBlock(checkStack);

						if (filterBlock != null)
						{
							filterBlocks.add(filterBlock);
						}
					}

					if (!filterBlocks.contains(block))
					{
						return 1;
					}
				}

				if (Blacklist.disintegrationBlacklist.contains(block) || block instanceof BlockFluid || block instanceof ILiquid)
				{
					return 1;
				}

				PacketManager.sendPacketToClients(PacketManager.getPacket(ModularForceFieldSystem.CHANNEL, (TileEntity) projector, TilePacketType.FXS.ordinal(), 2, position.intX(), position.intY(), position.intZ()), ((TileEntity) projector).worldObj);

				if (projector.getModuleCount(ModularForceFieldSystem.itemModuleCollection) > 0)
				{
					((TileEntityForceFieldProjector) projector).getDelayedEvents().add(new BlockInventoryDropDelayedEvent((IDelayedEventHandler) projector, 39, block, tileEntity.worldObj, position, (TileEntityInventory) projector));
				}
				else
				{
					((TileEntityForceFieldProjector) projector).getDelayedEvents().add(new BlockDropDelayedEvent((IDelayedEventHandler) projector, 39, block, tileEntity.worldObj, position));
				}

				if (this.blockCount++ >= projector.getModuleCount(ModularForceFieldSystem.itemModuleSpeed) / 3)
				{
					return 2;
				}
				else
				{
					return 1;
				}
			}
		}

		return 1;
	}
}