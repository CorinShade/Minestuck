package com.mraof.minestuck.block;

import java.util.Random;

import com.mraof.minestuck.Minestuck;
import com.mraof.minestuck.item.ItemCruxiteRaw;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.item.Item;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class OreCruxite extends Block {
	private Icon texture;
	public OreCruxite(int id)
	{
		super(id, Material.rock);
		setUnlocalizedName("oreCruxite");
		setHardness(3.0F);
		setCreativeTab(CreativeTabs.tabBlock);
		
	}
	
	@Override
	public Icon getIcon(int side, int metadata) 
	{
		return texture;
	}
	
	@Override
	public boolean canCreatureSpawn(EnumCreatureType type, World world, int x, int y, int z) {
		return true;
	}
	
    public void registerIcons(IconRegister par1IconRegister)
    {
            this.texture = par1IconRegister.registerIcon("Minestuck:CruxiteOre");
    }
    
    public int idDropped(int par1, Random par2Random, int par3)
    {
        return Minestuck.rawCruxite.itemID;
    }

    public int quantityDropped(Random par1Random)
    {
        return 2 + par1Random.nextInt(4);
    }
    
    public int quantityDroppedWithBonus(int par1, Random par2Random)
    {
        if (par1 > 0)
        {
            int j = par2Random.nextInt(par1 + 2) - 1;

            if (j < 0)
            {
                j = 0;
            }

            return this.quantityDropped(par2Random) * (j + 1);
        }
        else
        {
            return this.quantityDropped(par2Random);
        }
    }
    
    public void dropBlockAsItemWithChance(World par1World, int par2, int par3, int par4, int par5, float par6, int par7)
    {
        super.dropBlockAsItemWithChance(par1World, par2, par3, par4, par5, par6, par7);
        this.dropXpOnBlockBreak(par1World, par2, par3, par4, MathHelper.getRandomIntegerInRange(par1World.rand, 2, 5));
    }

}
