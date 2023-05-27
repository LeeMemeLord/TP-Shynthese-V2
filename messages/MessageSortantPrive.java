package messages;

public class MessageSortantPrive extends MessageSortant implements MessagePrive {
    private final String destinataire;

    public MessageSortantPrive(String texte) {
        super(texte.substring(texte.indexOf(' ') + 1));
        destinataire = texte.substring(1, texte.indexOf(' '));
    }

    @Override
    public String getDestinataire() {
        return destinataire;
    }

    @Override
    public String toString() {
        return String.format("%c%s %s", PREFIX, getDestinataire(), super.toString());
    }
}
