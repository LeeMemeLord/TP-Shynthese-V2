import java.io.*;

public class CLIClient extends ChatClient {
    private static final String PROMPT = "> ", QUIT = "quit";

    protected BufferedReader userIn;
    protected PrintWriter userOut;

    public static void main(String[] args) {
        try {
            String userName = args[0];
            ChatClient client = new CLIClient(userName);

            String serveur = args.length > 1 ? args[1] : DEFAULT_HOST;
            int port = args.length > 2 ? Integer.parseInt(args[2]) : DEFAULT_PORT;

            client.chargerHistorique();
            client.connecter(serveur, port);
            client.sauvegarderHistorique();
        }
        catch (Exception e) {
            //System.err.println(e);
            System.out.println("usage: java Client <adresse_serveur> [port] [username]");
        }
    }

    public CLIClient(String userName) {
        this(userName, System.in, System.out);
    }

    /**
     * Construit un client de clavardage en ligne de commande (CLI).
     * @param inputStream  Stream d'où proviennent les messages.
     * @param outputStream Stream où envoyer les messages.
     */
    public CLIClient(String userName, InputStream inputStream, OutputStream outputStream) {
        super(userName);
        userIn = new BufferedReader(new InputStreamReader(inputStream));
        userOut = new PrintWriter(outputStream);
    }

    @Override
    public void connecter(String hostName, int portNumber) {
        super.connecter(hostName, portNumber);

        // On lance une boucle de lecture infinie pour recevoir les entrées de l'utilisateur.
        try {
            String input = userIn.readLine();
            while (input != null && !input.equals(QUIT)) {
                send(input);
                showPrompt();
                input = userIn.readLine();
            }
            close();
        }
        catch (IOException e) {
            System.err.println("Impossible de lire depuis le terminal");
        }
    }

    @Override
    protected void display(String str) {
        userOut.println("\b\b" + str);
        showPrompt();
    }

    private void showPrompt() {
        userOut.print(PROMPT);
        userOut.flush();
    }
}
