package com.mraof.minestuck.inventory.captchalouge;

import java.util.Iterator;
import java.util.LinkedList;

import com.mraof.minestuck.Minestuck;
import com.mraof.minestuck.client.gui.captchalouge.StackGuiHandler;
import com.mraof.minestuck.client.gui.captchalouge.SylladexGuiHandler;
import com.mraof.minestuck.inventory.captchalouge.CaptchaDeckHandler.ModusType;
import com.mraof.minestuck.util.AlchemyRecipeHandler;
import com.mraof.minestuck.util.Debug;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class StackModus extends Modus
{
	
	protected int size;
	protected LinkedList<ItemStack> list;
	
	@SideOnly(Side.CLIENT)
	protected boolean changed = true;
	@SideOnly(Side.CLIENT)
	protected ItemStack[] items;
	@SideOnly(Side.CLIENT)
	protected SylladexGuiHandler gui;
	
	@Override
	public void initModus(ItemStack[] prev)
	{
		list = new LinkedList<ItemStack>();
		if(prev != null)
		{
			for(ItemStack stack : prev)
				if(stack != null)
					list.add(stack);
			size  = prev.length;
		}
		else size = Minestuck.defaultModusSize;
		if(player.worldObj.isRemote)
		{
			items = new ItemStack[size];
			changed = prev != null;
		}
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		size = nbt.getInteger("size");
		list = new LinkedList<ItemStack>();
		
		for(int i = 0; i < size; i++)
			if(nbt.hasKey("item"+i))
				list.add(ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("item"+i)));
			else break;
		if(player != null && player.worldObj.isRemote)
		{
			items = new ItemStack[size];
			changed = true;
		}
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt)
	{
		nbt.setInteger("size", size);
		Iterator<ItemStack> iter = list.iterator();
		for(int i = 0; i < list.size(); i++)
		{
			ItemStack stack = iter.next();
			nbt.setTag("item"+i, stack.writeToNBT(new NBTTagCompound()));
		}
		return nbt;
	}
	
	@Override
	public boolean putItemStack(ItemStack item)
	{
		ItemStack firstItem = list.size() > 0 ? list.getFirst() : null;
		if(firstItem != null && firstItem.getItem() == item.getItem() && firstItem.getItemDamage() == item.getItemDamage() && ItemStack.areItemStackTagsEqual(firstItem, item)
				&& firstItem.stackSize + item.stackSize <= firstItem.getMaxStackSize())
			firstItem.stackSize += item.stackSize;
		else if(list.size() < size)
			list.addFirst(item);
		else
		{
			list.addFirst(item);
			CaptchaDeckHandler.launchItem(player, list.removeLast());
		}
		
		return true;
	}
	
	@Override
	public ItemStack[] getItems()
	{
		if(!player.worldObj.isRemote)	//Used only when replacing the modus
		{
			ItemStack[] items = new ItemStack[size];
			fillList(items);
			return items;
		}
		
		if(changed)
		{
			fillList(items);
		}
		return items;
	}
	
	protected void fillList(ItemStack[] items)
	{
		Iterator<ItemStack> iter = list.iterator();
		for(int i = 0; i < size; i++)
			if(iter.hasNext())
				items[i] = iter.next();
			else items[i] = null;
	}
	
	@Override
	public boolean increaseSize()
	{
		if(Minestuck.modusMaxSize > 0 && size >= Minestuck.modusMaxSize)
			return false;
		
		size++;
		
		return true;
	}

	@Override
	public ItemStack getItem(int id, boolean asCard)
	{
		if(list.isEmpty())
			return null;
		
		if(asCard)
		{
			size--;
			return AlchemyRecipeHandler.createCard(list.removeFirst(), false);
		}
		else return list.removeFirst();
	}

	@Override
	public boolean canSwitchFrom(ModusType modus)
	{
		return modus == ModusType.QUEUE;
	}

	@Override
	public SylladexGuiHandler getGuiHandler()
	{
		if(gui == null)
			gui = new StackGuiHandler(this);
		return gui;
	}
	
}