package com.agentdid127.resourcepack;

import com.agentdid127.resourcepack.forwards.PackForwardsConverter;
import com.agentdid127.resourcepack.library.Util;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import joptsimple.OptionSet;

import java.io.IOException;
import java.io.PrintStream;

public class Main {

    /**
     * Main class. Runs program
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        OptionSet optionSet = Options.PARSER.parse(args);
        if (optionSet.has(Options.HELP)) {
            Options.PARSER.printHelpOn(System.out);
            return;
        }
        String from = optionSet.valueOf(Options.FROM);
        String to = optionSet.valueOf(Options.TO);

        String light = optionSet.valueOf(Options.LIGHT);

        boolean minify = optionSet.has(Options.MINIFY);

        PrintStream out = System.out;
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.disableHtmlEscaping().create();

        if (Util.getVersionProtocol(gson, from) > Util.getVersionProtocol(gson, to)) {
            System.out.println("Sorry! You can't currently downgrade your resource pack. With version 2.0, the software will allow the possibility");
        }
        else {
            new PackForwardsConverter(from, to, light, minify, optionSet.valueOf(Options.INPUT_DIR)).runDirectory();
        }





    }



}