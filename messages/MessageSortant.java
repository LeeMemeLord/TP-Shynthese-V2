package messages;

public class MessageSortant extends Message {
    public MessageSortant(String texte) {
        setCorps(texte);
    }

    @Override
    public String getSource() {
        return MOI;
    }

    @Override
    public String toString() {
        return getCorps();
    }
}
