package com.repshot;

import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;
import burp.api.montoya.logging.Logging;

public class RepShot implements BurpExtension {

    @Override
    public void initialize(MontoyaApi api) {
        api.extension().setName("RepShot");

        Logging logging = api.logging();
        logging.logToOutput("RepShot loaded — Generate finding cards from requests/responses");

        // Registrar menu contextual en Repeater
        api.userInterface().registerContextMenuItemsProvider(
            new RepShotMenuProvider(api)
        );
    }
}
