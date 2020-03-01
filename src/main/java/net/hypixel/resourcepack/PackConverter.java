package net.hypixel.resourcepack;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import joptsimple.OptionSet;
import net.hypixel.resourcepack.extra.BomDetector;
import net.hypixel.resourcepack.impl.*;
import net.hypixel.resourcepack.pack.Pack;

import javax.swing.text.html.Option;
import java.io.IOException;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class PackConverter {

    public static final boolean DEBUG = true;

    protected final OptionSet optionSet;
    protected Gson gson;

    protected final Map<Class<? extends Converter>, Converter> converters = new LinkedHashMap<>();

    public PackConverter(OptionSet optionSet) {
        this.optionSet = optionSet;

        GsonBuilder gsonBuilder = new GsonBuilder();
        if (!this.optionSet.has(Options.MINIFY)) gsonBuilder.setPrettyPrinting();
        this.gson = gsonBuilder.disableHtmlEscaping().create();
        String from = "";
        String to = "";
        String light = "";
        light = this.optionSet.valueOf(Options.LIGHT);
        System.out.println(this.optionSet.valueOf(Options.FROM));
        System.out.println(this.optionSet.valueOf(Options.TO));
        from = this.optionSet.valueOf(Options.FROM);
        to = this.optionSet.valueOf(Options.TO);

        // this needs to be run first, other converters might reference new directory names
        if (from.equals("1.12"))
            this.registerConverter(new NameConverter(this));
        if (to.equals("1.15")) this.registerConverter(new PackMetaConverter(this, "1.15"));
        else if (to.equals("1.14")) this.registerConverter(new PackMetaConverter(this, "1.14"));
        else this.registerConverter(new PackMetaConverter(this, "1.13"));
        if (from.equals("1.12")) {
            this.registerConverter(new ModelConverter(this, light));
            this.registerConverter(new SpacesConverter(this));
            this.registerConverter(new SoundsConverter(this));
            this.registerConverter(new ParticleConverter(this));
            this.registerConverter(new BlockStateConverter(this));
            this.registerConverter(new AnimationConverter(this));
            this.registerConverter(new MapIconConverter(this));
            this.registerConverter(new MCPatcherConverter(this));
        }
        if (to.equals("1.15")) this.registerConverter(new ChestConverter(this));
    }

    public void registerConverter(Converter converter) {
        converters.put(converter.getClass(), converter);
    }

    public <T extends Converter> T getConverter(Class<T> clazz) {
        //noinspection unchecked
        return (T) converters.get(clazz);
    }

    public void run() throws IOException {
        Files.list(optionSet.valueOf(Options.INPUT_DIR))
                .map(Pack::parse)
                .filter(Objects::nonNull)
                .forEach(pack -> {
                    try {
                        System.out.println("Converting " + pack);

                        pack.getHandler().setup();

                        BomDetector bom = new BomDetector(
                                pack.getWorkingPath().toString(),
                                ".txt", ".json", ".mcmeta", ".properties"
                        );

                        int count = 0;
                        for(String file : bom.findBOMs()){
                            count++;
                        }
                        if (count > 0){
                            System.out.println("Removing BOMs from " + count + " files.");
                        } bom.removeBOMs();

                        System.out.println("  Running Converters");
                        for (Converter converter : converters.values()) {
                            if (PackConverter.DEBUG)
                                System.out.println("    Running " + converter.getClass().getSimpleName());
                            converter.convert(pack);
                        }

                        pack.getHandler().finish();
                    } catch (Throwable t) {
                        System.err.println("Failed to convert!");
                        Util.propagate(t);
                    }
                });
    }

    public Gson getGson() {
        return gson;
    }
}