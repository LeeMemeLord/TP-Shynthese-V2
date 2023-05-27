import java.io.*;

/**
 * Cette classe permet de recevoir des messages sur un Stream et les recopier vers un autre.
 */
public class EchoListener implements Runnable {
    protected String prompt = "> ", quit = "quit", dest = "le serveur.";
    protected BufferedReader reader;
    protected PrintWriter writer;

    public EchoListener(InputStream inStream, OutputStream outStream) {
        this.reader = new BufferedReader(new InputStreamReader(inStream));
        this.writer = new PrintWriter(outStream, true);
    }

    /**
     * Attend et lit un message depuis le flux d'entrée.
     * @return La chaine de caractères lue dans le flux d'entrée.
     * @throws IOException Si le flux est illisible.
     */
    protected String receive() throws IOException {
        return reader.readLine();
    }

    /**
     * Envoie un message sur le flux de sortie.
     * @param message Le message à envoyer.
     */
    public void send(String message) {
        writer.println(message);
        System.out.print(prompt);
    }

    /**
     * Boucle infinie pour lire et rediriger les messages reçus.
     */
    public void run() {
        try {
            // On attend les messages du flux d'entrée et on les transfère au flux de sortie.
            String incoming = receive();
            while (incoming != null && !incoming.equals(quit)) {
                send(incoming);
                incoming = receive();
            }
        }
        catch (IOException e) {
            System.err.println("Impossible de communiquer avec " + dest);
        }
        finally {
            close();
        }
    }

    /**
     * Ferme proprement les flux gérés par cet objet.
     */
    public void close() {
        try {
            writer.println("bye!");
            writer.close();
            reader.close();
        }
        catch (IOException e) {
            System.err.println("Connexion perdue avec " + dest);
        }
    }
}
