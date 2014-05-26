package com.khorn.terraincontrol.generator.biome.layers.release_1_6;

import com.khorn.terraincontrol.LocalBiome;
import com.khorn.terraincontrol.LocalWorld;
import com.khorn.terraincontrol.configuration.WorldConfig;
import com.khorn.terraincontrol.configuration.WorldSettings;
import com.khorn.terraincontrol.generator.biome.ArraysCache;
import com.khorn.terraincontrol.generator.biome.layers.Layer;

import static com.khorn.terraincontrol.generator.biome.layers.release_1_6.LayerR16.*;

import com.khorn.terraincontrol.util.minecraftTypes.DefaultBiome;

public class LayerMix extends Layer
{

    public LayerMix(long paramLong, Layer paramGenLayer, WorldSettings configs, LocalWorld world)
    {
        super(paramLong);
        this.child = paramGenLayer;
        this.configs = configs;
        this.riverBiomes = new int[world.getMaxBiomesCount()];

        for (int id = 0; id < this.riverBiomes.length; id++)
        {
            LocalBiome biome = configs.biomes[id];

            if (biome == null || biome.getBiomeConfig().riverBiome.isEmpty())
                this.riverBiomes[id] = -1;
            else
                this.riverBiomes[id] = world.getBiomeByName(biome.getBiomeConfig().riverBiome).getIds().getGenerationId();

        }
    }

    private WorldSettings configs;
    private int[] riverBiomes;

    @Override
    public int[] getInts(ArraysCache cache, int x, int z, int xSize, int zSize)
    {
        switch (cache.outputType)
        {
            case FULL:
                return this.GetFull(cache, x, z, xSize, zSize);
            case WITHOUT_RIVERS:
                return this.GetWithoutRivers(cache, x, z, xSize, zSize);
            case ONLY_RIVERS:
                return this.GetOnlyRivers(cache, x, z, xSize, zSize);
            default:
                throw new UnsupportedOperationException("Unknown/invalid output type: " + cache.outputType);
        }

    }

    private int[] GetFull(ArraysCache cache, int x, int z, int xSize, int zSize)
    {
        int[] childInts = this.child.getInts(cache, x, z, xSize, zSize);
        int[] thisInts = cache.getArray(xSize * zSize);
        WorldConfig worldConfig = this.configs.worldConfig;

        int selection;
        int preFinalBiome;
        for (int zi = 0; zi < zSize; zi++)
        {
            for (int xi = 0; xi < xSize; xi++)
            {
                selection = childInts[(xi + zi * xSize)];

                if ((selection & LandBit) != 0)
                    preFinalBiome = selection & BiomeBits;
                else if (worldConfig.FrozenOcean && (selection & IceBit) != 0)
                    preFinalBiome = DefaultBiome.FROZEN_OCEAN.Id;
                else
                    preFinalBiome = DefaultBiome.OCEAN.Id;

                if (worldConfig.riversEnabled && (selection & RiverBits) != 0 && !this.configs.biomes[preFinalBiome].getBiomeConfig().riverBiome.isEmpty())
                    selection = this.riverBiomes[preFinalBiome];
                else
                    selection = preFinalBiome;

                thisInts[(xi + zi * xSize)] = selection;
            }
        }

        return thisInts;
    }

    private int[] GetWithoutRivers(ArraysCache cache, int x, int z, int xSize, int zSize)
    {
        int[] childInts = this.child.getInts(cache, x, z, xSize, zSize);
        int[] thisInts = cache.getArray(xSize * zSize);
        WorldConfig worldConfig = this.configs.worldConfig;

        int selection;
        int preFinalBiome;
        for (int zi = 0; zi < zSize; zi++)
        {
            for (int xi = 0; xi < xSize; xi++)
            {
                selection = childInts[(xi + zi * xSize)];

                if ((selection & LandBit) != 0)
                    preFinalBiome = selection & BiomeBits;
                else if (worldConfig.FrozenOcean && (selection & IceBit) != 0)
                    preFinalBiome = DefaultBiome.FROZEN_OCEAN.Id;
                else
                    preFinalBiome = DefaultBiome.OCEAN.Id;

                selection = preFinalBiome;

                thisInts[(xi + zi * xSize)] = selection;
            }
        }

        return thisInts;
    }

    private int[] GetOnlyRivers(ArraysCache cache, int x, int z, int xSize, int zSize)
    {
        int[] childInts = this.child.getInts(cache, x, z, xSize, zSize);
        int[] thisInts = cache.getArray(xSize * zSize);
        WorldConfig worldConfig = this.configs.worldConfig;

        int selection;
        int preFinalBiome;
        for (int zi = 0; zi < zSize; zi++)
        {
            for (int xi = 0; xi < xSize; xi++)
            {
                selection = childInts[(xi + zi * xSize)];

                if ((selection & LandBit) != 0)
                    preFinalBiome = selection & BiomeBits;
                else if (worldConfig.FrozenOcean && (selection & IceBit) != 0)
                    preFinalBiome = DefaultBiome.FROZEN_OCEAN.Id;
                else
                    preFinalBiome = DefaultBiome.OCEAN.Id;

                if (worldConfig.riversEnabled && (selection & RiverBits) != 0 && !this.configs.biomes[preFinalBiome].getBiomeConfig().riverBiome.isEmpty())
                    selection = 1;
                else
                    selection = 0;

                thisInts[(xi + zi * xSize)] = selection;
            }
        }

        return thisInts;
    }

}
