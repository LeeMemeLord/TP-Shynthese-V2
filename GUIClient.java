import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GUIClient extends ChatClient {
    JFrame frame;
    JTextArea chatBox;
    JTextField sendBox;
    GUIClient.ConnexionDialog connexion;

    public static void main(String[] args) {
        String nom = args.length > 0 ? args[0] : "guest";
        GUIClient client = new GUIClient(nom);
        client.connecter();
    }

    /**
     * Construit un client de clavardage graphique.
     * @param userName Le nom d'utilisateur souhaité.
     */
    public GUIClient(String userName) {
        super(userName);

        frame = new JFrame("JavaChat");
        frame.setSize(400, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setJMenuBar(creerMenu());

        chatBox = new JTextArea();
        chatBox.setLineWrap(true);
        chatBox.setEditable(false);
        chatBox.append("Vous devez d'abord vous connecter!");
        JScrollPane chatPane = new JScrollPane(chatBox);
        frame.add(chatPane, BorderLayout.CENTER);

        /* TODO 12: Ajoutez le code nécessaire pour reproduire le bas de l'interface
             (une boite de texte pour entrer un message (sendBox) et un bouton "Envoyer").
           Fiez-vous aux captures d'écran pour voir le résultat attendu.
         */

        /* TODO 13: Ajoutez une action au bouton "Envoyer" qui va lui permettre
             a) d'envoyer le message au serveur (appelez la méthode send(String message)),
             b) d'ajouter le texte du message au textArea 'chatBox' (créé ci-dessus),
             c) de vider le contenu du textField 'sendBox' (que vous avez créé au TODO précédent).
        */

        frame.setVisible(true);
    }

    private JMenuBar creerMenu() {

        /* TODO 11: Ajoutez le code qui permettra de créer le menu suivant:
            Fichier
                Se connecter -> Ouvre le dialogue de connexion: connecter();
                Quitter -> Quitte l'application: System.exit(0);
            Historique
                Importer -> Lit l'historique: chargerHistorique();
                Exporter -> Écrit l'historique: sauvegarderHistorique();
          Fiez-vous aux captures d'écran pour voir le résultat attendu.
        */
        JMenuBar menuBar = new JMenuBar();

        JMenu menuFichier = new JMenu("Fichier");
        JMenuItem buttonConnection = new JMenuItem("Se connecter");
        buttonConnection.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                connecter();
            }
        });
        menuFichier.add(buttonConnection);
        JMenuItem buttonQutter = new JMenuItem("Quitter");
        buttonQutter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);

            }
        });
        menuFichier.add(buttonQutter);

        JMenu menuHistorique = new JMenu("Historique");
        JMenuItem bouttonImporter = new JMenuItem("Importer");
        bouttonImporter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {;
                chargerHistorique();
            }
        });
        menuHistorique.add(bouttonImporter);

        JMenuItem bouttonExporter = new JMenuItem("Exporter");
        bouttonExporter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sauvegarderHistorique();
            }
        });
        menuHistorique.add(bouttonExporter);

        menuBar.add(menuFichier);
        menuBar.add(menuHistorique);

        return menuBar;
    }

    @Override
    public String sauvegarderHistorique() {
        String cheminFichier = super.sauvegarderHistorique();
        JOptionPane.showMessageDialog(frame, "Historique sauvegardé dans " + cheminFichier);
        return cheminFichier;
    }

    public void connecter() {
        if (connexion == null)
            connexion = new ConnexionDialog(frame);
        connexion.setVisible(true);
        chatBox.setText("");
        //sendBox.setEnabled(true);
    }

    private class ConnexionDialog extends JDialog {
        public ConnexionDialog(JFrame parent) {
            super(parent, "Connexion", ModalityType.DOCUMENT_MODAL);
            this.setSize(300, 200);

            /* TODO 10: Ajoutez les composants manquants pour demander l'information nécessaire
                 à la connexion (nom d'utilisateur, adresse du serveur, port de l'application).
               Fiez-vous aux captures d'écran pour voir le résultat attendu.
            */
            JPanel grille = new JPanel(new GridLayout(3,2));
            this.add(grille);

            JLabel userNameLb = new JLabel("Username:");
            JLabel serveurLb = new JLabel("Serveur:");
            JLabel portLb = new JLabel("Port:");

            JTextField userField = new JTextField("Mike");
            JTextField hostAddr = new JTextField("localhost");
            JTextField  hostPort = new JTextField("5000");

            grille.add(userNameLb);
            grille.add(userField);

            grille.add(serveurLb);
            grille.add(hostAddr);

            grille.add(portLb);
            grille.add(hostPort);

            JButton connectButton = new JButton("Rejoindre");
            connectButton.addActionListener(e -> {
                this.setVisible(false);
                // Décommentez les lignes suivantes une fois le TODO 10 complété:
                userName = userField.getText();
                connecter(hostAddr.getText(), Integer.parseInt(hostPort.getText()));
            });
            this.add(connectButton, BorderLayout.SOUTH);
            this.getRootPane().setDefaultButton(connectButton);
        }
    }

    @Override
    protected void display(String str) {
        chatBox.append(str + "\n");
        chatBox.setCaretPosition(chatBox.getDocument().getLength());
    }


}
