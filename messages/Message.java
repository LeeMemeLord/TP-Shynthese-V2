package messages;

import java.util.Date;

public abstract class Message {
    public static final String MOI = "<moi>";

    //private Date date;
    protected String corps;

    public abstract String getSource();

    public String getCorps() {
        return corps;
    }

    public void setCorps(String texte) {
        if (texte.isBlank())
            throw new IllegalArgumentException("Le corps du message ne peut être vide");
        this.corps = texte.trim();
    }
}
