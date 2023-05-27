import messages.MessageEntrant;
import messages.MessageEntrantPrive;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Exemple de serveur qui accepte des connexions de plusieurs clients.
 * @author David Giasson
 * @date H2023
 */
public class ChatServer {
    ServerSocket serverSocket = null;
    Map<String, ClientListener> clients;
    AdminListener admin;
    public static void main(String[] args) {
        try {
            int port = args.length > 0 ? Integer.parseInt(args[0]) : 5000;
            ChatServer javaChat = new ChatServer(port);
            javaChat.run();
        }
        catch (Exception ex) {
            System.err.println(ex);
            System.out.println("usage: java Server [port]");
        }
    }
    
    public ChatServer(int portNumber) throws IOException {
        try {
            // On initialise le client "admin" du serveur (sur un thread separe).
            clients = new ConcurrentHashMap<>();
            admin = new AdminListener();
            admin.connect();
            
            // On cree le socket du serveur et on le fait ecouter sur le port
            // passe en argument de la ligne de commande.
            serverSocket = new ServerSocket(portNumber);
        }
        catch (IOException e) {
            System.err.println("Problème fatal lors du lancement du serveur.");
            throw e;
        }
    }
    
    public void run() {
        try {
            // Le serveur attend une connexion, la traite, 
            // puis recommence a attendre (sans fin).
            while (true) {
                // On attend qu'un client se connecte; Cette fonction 
                // met le serveur en attente d'une connexion et on 
                // sort de la fonction quand un client se connecte.
                admin.send("Serveur en attente...");
                connect(serverSocket.accept());
            }
        }
        catch (IOException e) {
            System.out.println("Fermeture du serveur.");
        }
    }
    
    // Initialiser les connexions.
    private void connect(Socket clientSocket) {
        try {
            // On initialise les flux qui nous permettront d'ecrire dans le socket du client.
            ClientListener client = new ClientListener(clientSocket.getInputStream(),
                                                       clientSocket.getOutputStream());
            client.connect();
            sendAll(client.getName() + " s'est connecté ("
                    + clients.size() + " utilisateurs en ligne)");
        }
        catch (IOException e) {
            admin.send("Impossible de se connecter au client.");
            System.err.println(e);
        }
    }
    
    public void sendAll(String message) {
        for (ClientListener c : clients.values())
            c.send("# " + message);
    }
    
    private class ClientListener extends EchoListener {
        
        public ClientListener(InputStream inStream, OutputStream outStream) {
            super(inStream, outStream);
            this.prompt = "";
            this.quit = "bye!";
            this.dest = "le client.";
        }
        
        public void connect() throws IOException {
            // Le premier message contient le nom du client.
            String chosenName = checkName(reader.readLine());
            if (chosenName == null) {
                close();
                throw new IOException("Nom invalide");
            }
            
            // On enregistre le client dans la liste des clients connectés.
            this.dest = chosenName;
            clients.put(chosenName.toLowerCase(), this);

            // On envoie un bonjour au client et on lance la boucle de lecture/écriture.
            send("Bonjour " + getName() + "!");
            new Thread(this).start();
        }
        
        private String checkName(String name) {
            name = (name == null ? "" : name.trim());
            if (name.length() < 3) {
                send("Erreur: " + name + " est trop court.");
                return null;
            }
            if (clients.containsKey(name)) {
                send("Erreur: " + name + " est deja en cours d'utilisation.");
                return null;
            }
            return name;
        }
        
        public String getName() {
            return dest;
        }
        
        @Override // Lire et transférer tous les messages recus.
        public void run() {
            try {
                // On attend les messages du client et on les transfère.
                String incoming = reader.readLine();
                while (incoming != null && !incoming.equals(quit)) {
                    if (incoming.startsWith("@")) {
                        MessageEntrantPrive message = new MessageEntrantPrive(incoming, getName());
                        if (clients.containsKey(message.getDestinataire().toLowerCase()))
                            clients.get(message.getDestinataire().toLowerCase()).send(message.toString());
                        else
                            send("Erreur: Destinataire inconnu " + message.getDestinataire());
                    }
                    else if (!incoming.isEmpty()) {
                        MessageEntrant message = new MessageEntrant(incoming, getName());
                        for (ClientListener c : clients.values()) {
                            if (c != this)
                                c.send(message.toString());
                        }
                    }
                    incoming = receive();
                }
            }
            catch (IOException e) {
                System.err.println("Impossible de communiquer avec " + getName());
            }
            finally {
                clients.remove(this.getName().toLowerCase());
                close();
                sendAll(getName() + " s'est déconnecté ("
                        + clients.size() + " utilisateurs restants)");
            }
        }
    }
    
    private class AdminListener extends ClientListener {
        
        public AdminListener() {
            super(System.in, System.out);
            this.prompt = "> ";
            this.quit = "quit";
            this.dest = "admin";
        }
        
        @Override
        public void connect() throws IOException {
            if (admin == null)
                admin = this;
            else if (admin != this) {
                send("Erreur: " + getName() + " est deja connecté.");
                super.close();
                throw new IOException("Admin déjà connecté");
            }
            
            clients.put(getName(), this);
            send("Bonjour " + getName() + "!");
            new Thread(this).start();
        }
        
        @Override
        public String getName() {
            return "admin";
        }
        
        @Override
        protected String receive() throws IOException {
            System.out.print(prompt);
            return reader.readLine();
        }
        
        @Override
        public void send(String message) {
            super.send("\b\b" + message);
        }
        
        @Override
        public void close() {
            // Deconnecte tout le monde et ferme le serveur.
            try {
                for (ClientListener c : clients.values()) {
                    if (c != this)
                        c.close();
                }
                super.close();
                admin = null;
                serverSocket.close();
            }
            catch (IOException e) {
                System.err.println("Erreur fatale dans le serveur.");
                System.exit(1);
            }
        }
    }
}
