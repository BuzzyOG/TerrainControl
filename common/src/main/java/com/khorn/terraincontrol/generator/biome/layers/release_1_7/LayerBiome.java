package com.khorn.terraincontrol.generator.biome.layers.release_1_7;

import com.khorn.terraincontrol.configuration.WorldConfig;
import com.khorn.terraincontrol.generator.biome.ArraysCache;
import com.khorn.terraincontrol.generator.biome.layers.Layer;
import com.khorn.terraincontrol.util.minecraftTypes.DefaultBiome;

import static com.khorn.terraincontrol.generator.biome.layers.release_1_7.LayerR17.isOcean;

public class LayerBiome extends LayerR17
{
    private DefaultBiome[] dryWarmBiomes;
    private DefaultBiome[] mediumLushBiomes;
    private DefaultBiome[] coldBiomes;
    private DefaultBiome[] snowyBiomes;

    public LayerBiome(long seed, Layer parentLayer, WorldConfig config) {
        super(seed);
        this.dryWarmBiomes = new DefaultBiome[]{DefaultBiome.DESERT, DefaultBiome.DESERT, DefaultBiome.DESERT, DefaultBiome.SAVANNA, DefaultBiome.SAVANNA, DefaultBiome.PLAINS};
        this.mediumLushBiomes = new DefaultBiome[]{DefaultBiome.FOREST, DefaultBiome.ROOFED_FOREST, DefaultBiome.EXTREME_HILLS, DefaultBiome.PLAINS, DefaultBiome.BIRCH_FOREST, DefaultBiome.SWAMPLAND};
        this.coldBiomes = new DefaultBiome[]{DefaultBiome.FOREST, DefaultBiome.EXTREME_HILLS, DefaultBiome.TAIGA, DefaultBiome.PLAINS};
        this.snowyBiomes = new DefaultBiome[]{DefaultBiome.ICE_PLAINS, DefaultBiome.ICE_PLAINS, DefaultBiome.ICE_PLAINS, DefaultBiome.COLD_TAIGA};
        this.child = parentLayer;

//        if (worldType == WorldType.DEFAULT_1_1) {
//            this.dryWarmBiomes = new DefaultBiome[]{DefaultBiome.desert, DefaultBiome.forest, DefaultBiome.extremeHills, DefaultBiome.swampland, DefaultBiome.plains, DefaultBiome.taiga};
//        }
    }

    /**
     * Returns a list of integer values generated by this layer. These may be
     * interpreted as temperatures, rainfall amounts, or biomeList[] indices
     * based on the particular GenLayer subclass.
     */
    @Override
    public int[] getInts(ArraysCache cache, int x, int z, int xSize, int zSize) {
        int[] parentIds = this.child.getInts(cache, x, z, xSize, zSize);
        int[] thisIds = cache.getArray(xSize * zSize);

        for (int zi = 0; zi < zSize; ++zi) {
            for (int xi = 0; xi < xSize; ++xi) {
                this.initChunkSeed((long) (xi + x), (long) (zi + z));
                int currentPiece = parentIds[xi + zi * xSize];
                boolean isRareBiome = (currentPiece & RareBiomeBit) != 0;
                currentPiece &= ~RareBiomeBit;

                //>>	Preservation of mushroom island
                if (isOcean(currentPiece)) {    
                    thisIds[xi + zi * xSize] = currentPiece;
                } else if (currentPiece == DefaultBiome.MUSHROOM_ISLAND.Id) {
                    thisIds[xi + zi * xSize] = currentPiece;
                //>>	Start of 4 groups
                    
                //>>	DRY GROUP
                } else if (currentPiece == 1) {
                    if (isRareBiome) {
                        if (this.nextInt(3) == 0) {
                            thisIds[xi + zi * xSize] = DefaultBiome.MESA_PLATEAU.Id;
                        } else {
                            thisIds[xi + zi * xSize] = DefaultBiome.MESA_PLATEAU_FOREST.Id;
                        }
                    } else {
                        thisIds[xi + zi * xSize] = this.dryWarmBiomes[this.nextInt(this.dryWarmBiomes.length)].Id;
                    }
                    
                //>>	LUSH GROUP
                } else if (currentPiece == 2) {
                    if (isRareBiome) {
                        thisIds[xi + zi * xSize] = DefaultBiome.JUNGLE.Id;
                    } else {
                        thisIds[xi + zi * xSize] = this.mediumLushBiomes[this.nextInt(this.mediumLushBiomes.length)].Id;
                    }
                    
                 //>>	COLD GROUP
                } else if (currentPiece == 3) {
                    if (isRareBiome) {
                        thisIds[xi + zi * xSize] = DefaultBiome.MEGA_TAIGA.Id;
                    } else {
                        thisIds[xi + zi * xSize] = this.coldBiomes[this.nextInt(this.coldBiomes.length)].Id;
                    }
                    
                 //>>	SNOWY GROUP
                } else if (currentPiece == 4) {
                    thisIds[xi + zi * xSize] = this.snowyBiomes[this.nextInt(this.snowyBiomes.length)].Id;
                } else {
                    thisIds[xi + zi * xSize] = DefaultBiome.MUSHROOM_ISLAND.Id;
                }
            }
        }

        return thisIds;
    }
}