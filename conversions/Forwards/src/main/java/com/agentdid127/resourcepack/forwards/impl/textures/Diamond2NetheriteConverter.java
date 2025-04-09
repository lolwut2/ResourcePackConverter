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
    }

    public void rename(Path diamondArmorPath) throws IOException
    {
        if (diamondArmorPath.toFile().exists())
        {
            Path netheriteArmorPath = diamondArmorPath.resolveSibling(
                    diamondArmorPath.getFileName().toString().replace("diamond_", "netherite_"));

            Files.move(diamondArmorPath, netheriteArmorPath);
        }
    }

    public Path getDiamondPath(Pack pack, String items, String armor)
    {
        return pack.getWorkingPath()
            .resolve(("assets/minecraft/textures/" + items + "/diamond_" + armor + ".png").replace("/", File.separator));
    }
}
