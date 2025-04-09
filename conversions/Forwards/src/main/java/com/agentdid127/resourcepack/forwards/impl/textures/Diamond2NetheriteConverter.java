package com.agentdid127.resourcepack.forwards.impl.textures;

import com.agentdid127.resourcepack.forwards.impl.NameConverter;
import com.agentdid127.resourcepack.library.Converter;
import com.agentdid127.resourcepack.library.PackConverter;
import com.agentdid127.resourcepack.library.pack.Pack;
import com.agentdid127.resourcepack.library.utilities.Util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Diamond2NetheriteConverter extends Converter
{
    private final int version;

    public Diamond2NetheriteConverter(PackConverter packConverter, int version)
    {
        super(packConverter);
        this.version = version;
    }

    @Override
    public void convert(Pack pack) throws IOException
    {
        if (version < Util.getVersionProtocol(packConverter.getGson(), "1.16"))
        {
            return;
        }

        String items = "items";
        if (version > Util.getVersionProtocol(packConverter.getGson(), "1.13"))
        {
            items = "item";
        }

        String[] types = {"helmet", "chestplate", "leggings", "boots", "axe", "sword", "shovel", "pickaxe", "hoe", "horse_armor"};
        for (String type : types)
        {
            Path dPath = getDiamondPath(pack, items, type);
            if (dPath.toFile().exists())
            {
                rename(dPath);
            }
        }

        Set<Path> layers = getLayerPaths(pack);
        for (Path layer : layers)
        {
            if (!layer.toFile().exists())
            {
                continue;
            }

            rename(layer);
        }
    }

    public void rename(Path diamondPath) throws IOException
    {
        if (diamondPath.toFile().exists())
        {
            Path netheriteArmorPath = diamondPath.resolveSibling(
                    diamondPath.getFileName().toString().replace("diamond_", "netherite_"));

            Files.move(diamondPath, netheriteArmorPath);
        }
    }

    public Path getDiamondPath(Pack pack, String items, String armor)
    {
        return pack.getWorkingPath()
            .resolve(("assets/minecraft/textures/" + items + "/diamond_" + armor + ".png").replace("/", File.separator));
    }

    public Set<Path> getLayerPaths(Pack pack)
    {
        Set<Path> paths = new HashSet<>(2);
        for (int i = 1; i <= 2; i++)
        {
            Path path = pack.getWorkingPath()
                    .resolve(("assets/minecraft/textures/models/armor/diamond_layer_" + i + ".png")
                            .replace("/", File.separator));
            paths.add(path);
        }

        return paths;
    }
}