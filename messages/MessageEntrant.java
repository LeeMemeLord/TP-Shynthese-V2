package messages;

public class MessageEntrant extends Message {
    public static final char SEPARATEUR = ':';
    private String source;

    public MessageEntrant(String texte, String source) {
        setSource(source);
        setCorps(texte);
    }

    public MessageEntrant(String contenu) {
        int indexSeparateur = contenu.indexOf(SEPARATEUR);
        if (indexSeparateur == -1) {
            setSource("serveur");
            setCorps(contenu);
        }
        else {
            setSource(contenu.substring(0, indexSeparateur));
            setCorps(contenu.substring(indexSeparateur + 1));
        }
    }

    @Override
    public String getSource() {
        return source;
    }

    protected void setSource(String source) {
        if (source.isBlank())
            throw new IllegalArgumentException("La source du message ne peut être vide");
        this.source = source;
    }

    @Override
    public String toString() {
        return String.format("%s%c %s", getSource(), SEPARATEUR, getCorps());
    }
}
