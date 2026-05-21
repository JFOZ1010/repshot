package com.repshot;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.ui.contextmenu.ContextMenuEvent;
import burp.api.montoya.ui.contextmenu.ContextMenuItemsProvider;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class RepShotMenuProvider implements ContextMenuItemsProvider {

    private final MontoyaApi api;

    public RepShotMenuProvider(MontoyaApi api) {
        this.api = api;
    }

    @Override
    public List<Component> provideMenuItems(ContextMenuEvent event) {
        List<Component> menuItems = new ArrayList<>();

        // Intentar obtener request/response de selección o del editor activo
        HttpRequestResponse reqRes = null;

        List<HttpRequestResponse> selected = event.selectedRequestResponses();
        if (!selected.isEmpty()) {
            reqRes = selected.get(0);
        } else if (event.messageEditorRequestResponse().isPresent()) {
            reqRes = event.messageEditorRequestResponse().get().requestResponse();
        }

        if (reqRes == null) return menuItems;

        final HttpRequestResponse finalReqRes = reqRes;

        JMenuItem menuItem = new JMenuItem("📸 Send to RepShot");
        menuItem.setFont(menuItem.getFont().deriveFont(Font.BOLD));

        menuItem.addActionListener(e -> {
            String request = finalReqRes.request() != null
                ? finalReqRes.request().toString()
                : "";

            String response = finalReqRes.response() != null
                ? finalReqRes.response().toString()
                : "(No response yet — send the request first in Repeater)";

            SwingUtilities.invokeLater(() -> {
                RepShotPanel panel = new RepShotPanel(request, response);
                JFrame frame = new JFrame("RepShot - Happy Hacking! @JF0x0r");
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                frame.setContentPane(panel);
                frame.setSize(1100, 750);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            });
        });

        menuItems.add(menuItem);
        return menuItems;
    }
}
