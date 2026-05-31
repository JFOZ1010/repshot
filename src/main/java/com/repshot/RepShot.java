package com.repshot;

import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;
//import burp.api.montoya.logging.Logging;

public class RepShot implements BurpExtension {

    @Override
    public void initialize(MontoyaApi api) {
    api.extension().setName("RepShot");
    api.logging().logToOutput("RepShot loaded — Generate finding cards from requests/responses");

    // FIX 1: Unload handler — required for BApp Store
    api.extension().registerUnloadingHandler(() ->
        api.logging().logToOutput("RepShot unloaded.")
    );

    api.userInterface().registerContextMenuItemsProvider(
        new RepShotMenuProvider(api)
    );
    }
}


