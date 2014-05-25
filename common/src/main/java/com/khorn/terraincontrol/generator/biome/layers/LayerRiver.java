package com.khorn.terraincontrol.generator.biome.layers;


import com.khorn.terraincontrol.generator.biome.ArraysCache;
import com.khorn.terraincontrol.util.minecraftTypes.DefaultBiome;

public class LayerRiver extends Layer
{
    public LayerRiver(long seed, Layer childLayer)
    {
        super(seed);
        this.child = childLayer;
    }

    @Override
    public int[] getInts(ArraysCache cache, int x, int z, int xSize, int zSize) {
        int x0 = x - 1;
        int z0 = z - 1;
        int xSize0 = xSize + 2;
        int zSize0 = zSize + 2;
        int[] parentInts = this.child.getInts(cache, x0, z0, xSize0, zSize0);
        int[] thisInts = cache.getArray(xSize * zSize);

        for (int zi = 0; zi < zSize; ++zi) {
            for (int xi = 0; xi < xSize; ++xi) {
                int a = this.makeNoMoreThan3(parentInts[xi + 0 + (zi + 1) * xSize0]);
                int b = this.makeNoMoreThan3(parentInts[xi + 2 + (zi + 1) * xSize0]);
                int c = this.makeNoMoreThan3(parentInts[xi + 1 + (zi + 0) * xSize0]);
                int d = this.makeNoMoreThan3(parentInts[xi + 1 + (zi + 2) * xSize0]);
                int abcd = this.makeNoMoreThan3(parentInts[xi + 1 + (zi + 1) * xSize0]);

                if (abcd == a && abcd == c && abcd == b && abcd == d) {
                    thisInts[xi + zi * xSize] = -1;
                } else {
                    thisInts[xi + zi * xSize] = DefaultBiome.RIVER.Id;
                }
            }
        }

        return thisInts;
    }

    private int makeNoMoreThan3(int transform) {
        return transform >= 2 ? 2 + (transform & 1) : transform;
    }
}