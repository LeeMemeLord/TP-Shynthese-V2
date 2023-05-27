import messages.*;

import java.io.*;
import java.net.Socket;

/**
 * Cette classe permet de recevoir des messages sur un Stream et d'en envoyer sur un autre.
 */
public abstract class ChatClient implements Runnable {
    public static final String DEFAULT_HOST = "localhost";
    public static final int DEFAULT_PORT = 5000;

    protected Historique historique = new Historique();
    protected String userName;
    protected Socket socket;
    protected BufferedReader reader;
    protected PrintWriter writer;

    /**
     * Construit un client de clavardage générique.
     */
    public ChatClient(String userName) {
        if (userName.contains(" "))
            throw new IllegalArgumentException("Le nom d'utilisateur ne peut pas contenir d'espaces");
        this.userName = userName;
    }

    public void chargerHistorique() {
        historique.vider();
        if (historique.lire("historique." + userName + ".txt") > 0) {
            display("\n========================================");
            display("Historique de la séance précédente:");
            for (Message m : historique.getMessages())
                display(m);
            display("========================================\n");
        }
        else
            display("Aucun historique trouvé");
    }

    public String sauvegarderHistorique() {
        String cheminFichier = "historique." + userName + ".txt";
        if (historique.ecrire(cheminFichier) > 0)
            return cheminFichier;
        else
            return null;
    }

    public void connecter(String hostName, int portNumber) {
        close(); // Si une connexion est déjà ouverte, il faut d'abord la fermer.
        try {
            // On tente de se connecter au serveur et on initialise les flux
            // qui nous permettront d'écrire et lire sur le réseau.
            socket = new Socket(hostName, portNumber);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);
            writer.println(userName);
            System.out.println("Connexion établie avec le serveur " + hostName);

            // On lance la boucle de réception des messages dans un thread séparé.
            new Thread(this).start();
        }
        catch (IOException e) {
            System.err.println("Impossible de se connecter au serveur " + hostName + ":" + portNumber);
            System.exit(1);
        }
    }

    /**
     * Boucle infinie pour lire et afficher les messages reçus du serveur.
     */
    public void run() {
        if (socket == null)
            throw new IllegalStateException("Le client n'est pas connecté à un serveur");

        try {
            MessageEntrant incoming = receive();
            while (!incoming.getCorps().equals("quit")) {
                historique.ajouter(incoming);
                display(incoming);
                incoming = receive();
            }
        }
        catch (IOException e) {
            System.err.println("Connexion perdue avec le serveur");
        }
        finally {
            close();
        }
    }

    /**
     * Envoie un message au serveur.
     * @param message Le message à envoyer.
     */
    public synchronized void send(String message) {
        MessageSortant msg;
        if (message.startsWith("@"))
            msg = new MessageSortantPrive(message);
        else
            msg = new MessageSortant(message);
        writer.println(msg);
        historique.ajouter(msg);
    }

    /**
     * Attend et lit un message du serveur.
     * @return La chaine de caractères lue dans le flux d'entrée.
     * @throws IOException Si le flux est illisible.
     */
    private MessageEntrant receive() throws IOException {
        String message = reader.readLine();
        if (message == null)
            throw new IOException("Connexion fermée");
        else if (message.startsWith("@"))
            return new MessageEntrantPrive(message);
        else
            return new MessageEntrant(message);
    }

    /**
     * Affiche un message reçu.
     * @param message Le message a afficher.
     */
    protected void display(Message message) {
        display(message.toString());
    }

    /**
     * Affiche une chaine de caractères.
     * @param str La chaine à afficher.
     */
    protected abstract void display(String str);

    /**
     * Ferme proprement les flux gérés par cet objet.
     */
    public synchronized void close() {
        if (socket != null) {
            try {
                send("bye!");
                writer.close();
                reader.close();
                socket.close();
            } catch (IOException e) {
                System.err.println("Impossible de fermer proprement les connexions");
            }
            finally {
                socket = null;
            }
        }
    }
}
