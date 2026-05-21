// REPSHOT-CHANGE-LOG: 2026-05-13 — Cycle 6 — Fix line-ending normalization via getDocument().getText(); debug logging; pinLine-3 context
// REPSHOT-CHANGE-LOG: 2026-05-13 — Cycle 7 — addPin uses char-accumulation (no \n counting); pin = startLine exactly
// REPSHOT-CHANGE-LOG: 2026-05-13 — Cycle 8 — Visual viewport capture replaces pin system

package com.repshot;

import java.awt.image.BufferedImage;

public class CardData {
    public final String title;
    public final String vulnType;
    public final String severity;
    public final String impact;
    public final String handle;
    public final String request;
    public final String response;
    
    /** Visual capture of the request viewport. Null if no capture. */
    public final BufferedImage reqCapture;
    /** Visual capture of the response viewport. Null if no capture. */
    public final BufferedImage resCapture;

    public CardData(String title, String vulnType, String severity,
                    String impact, String handle,
                    String request, String response,
                    BufferedImage reqCapture, BufferedImage resCapture) {
        this.title      = title;
        this.vulnType   = vulnType;
        this.severity   = severity;
        this.impact     = impact;
        this.handle     = handle;
        this.request    = request;
        this.response   = response;
        this.reqCapture = reqCapture;
        this.resCapture = resCapture;
    }
}
