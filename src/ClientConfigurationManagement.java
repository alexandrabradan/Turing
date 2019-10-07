import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class ClientConfigurationManagement {
    private String serverHost; //DNS name server
    private int serverPort; //porta su cui Server e' in ascolto
    private int RMIPort; //porta utilizzata per gli inviti
    private int multicastPort; //porta utilizzata per i gruppi di chat
    private int connectionTimeout; //tempo attesa connessione Server / "receive" UDP chat multicast
    private int maxNumSectionsPerDocument; //numero massimo di sezioni che un documento puo' avere
    private int maxNumCharactersArg; //numero massimo di caratteri che username/password/nome_documento possono avere
    private String clientsDownloadsDocumentsDirectory; //cartella nella quale Clients salvano loro files

    private FileManagement fileManagement = new FileManagement();
    private String currentPath = fileManagement.getCurrentPath();

    public ClientConfigurationManagement(){
        this.serverHost = "";
        this.serverPort = -1;
        this.RMIPort = -1;
        this.multicastPort = -1;
        this.connectionTimeout = -1;
        this.maxNumSectionsPerDocument = -1;
        this.maxNumCharactersArg = -1;
        this.clientsDownloadsDocumentsDirectory = "";
    }

    /**
     * Funzione che resttuisce il DNS Name Server
     * @return DNS Name Server
     */
    public String getServerHost(){
        return this.serverHost;
    }

    /**
     * Funzione che restituisce la porta su cui Server e' in ascolto
     * @return porta su cui Server e' in ascolto
     */
    public int getServerPort(){
        return this.serverPort;
    }

    /**
     * Funzione che restituisce la
     * @return porta utilizzata per gli inviti
     */
    public int getRMIPort(){
        return this.RMIPort;
    }

    /**
     * Funzione che restituisce la porta utilizzata per i gruppi di chat
     * @return porta utilizzata per i gruppi di chat
     */
    public int getMulticastPort(){
        return this.multicastPort;
    }

    /**
     * Funzione che restituisce tempo attesa Client prima di affermare di non potersi connettere al Server
     * @return tempo attesa Client prima di affermare di non potersi connettere al Server
     */
    public int getConnectionTimeout(){
        return this.connectionTimeout;
    }

    /**
     * Funzione che restituisce numero massimo di sezioni che un documento puo' avere
     * @return numero massimo di sezioni che un documento puo' avere
     */
    public int getMaxNumSectionsPerDocument(){
        return this.maxNumSectionsPerDocument;
    }

    /**
     * Funzione che restiruisce numero massimo di caratteri che username/password/nome_documento possono avere
     * @return numero massimo di caratteri che username/password/nome_documento possono avere
     */
    public int getMaxNumCharactersArg(){
        return this.maxNumCharactersArg;
    }

    /**
     * Funzione che restituisce path della directory dove Clients salvano documenti scaricati
     * @return path della directory dove Clients salvano documenti scaricati
     */
    public String getClientsDownloadsDocumentsDirectory(){
        return this.clientsDownloadsDocumentsDirectory;
    }

    /**
     * Funzione che fa il parsing del file di configurazione passato come argomento
     * @param confFile path del file di configurazione da parsare
     * @return SUCCESS se il parsing e' andato a buon fine e tutte le variabili di configurazione sono lecite
     *         FAILURE se il file non esiste oppure ci sono stati dei problemi con il parsing/ valori parsati
     */
    public FunctionOutcome parseConf(String confFile){

        confFile = currentPath + confFile;

        //verifico se il file passato come paramentro esiste
        boolean exist = this.fileManagement.checkEsistenceFile(confFile);

        if(!exist){
            System.err.println("[ERR] >> file di configurazione = " + confFile + " NON esistente");
            return FunctionOutcome.FAILURE;
        }

        //file esiste
        //provo a farne il parsing:
        //1. non considero le righe che sono vuote
        //2. non considero le righe commentate (iniziano con #)
        //3. verifico che le righe che non sono vuote e che non sono comentate contegano come parola chiave una delle
        //variabili di configurazione
        Path path = Paths.get(confFile);
        try {
            List<String> lines = null; //.get(0);

            lines = Files.readAllLines(path, StandardCharsets.UTF_8);

            for (String line : lines) {
                if(!line.isEmpty() && !line.startsWith("#")){
                    String delimiter = "=";
                    String[] splitString = line.split(delimiter);
                    String key = splitString[0].trim(); //rimuovo spazi vuoti inizio e fine stringa
                    String value = splitString[1].trim(); //rimuovo spazi vuoti inizio e fine stringa

                    switch (key) {
                        case "serverHost":
                            this.serverHost = value;
                            break;
                        case "serverPort":
                            this.serverPort = Integer.parseInt(value);
                            break;
                        case "RMIPort":
                            this.RMIPort = Integer.parseInt(value);
                            break;
                        case "multicastPort":
                            this.multicastPort = Integer.parseInt(value);
                            break;
                        case "connectionTimeout":
                            this.connectionTimeout = Integer.parseInt(value);
                            break;
                        case "maxNumSectionsPerDocument":
                            this.maxNumSectionsPerDocument = Integer.parseInt(value);
                            break;
                        case "maxNumCharactersArg":
                            this.maxNumCharactersArg = Integer.parseInt(value);
                            break;
                        case "clientsDownloadsDocumentsDirectory":
                            value = currentPath + "/src" + value;
                            this.clientsDownloadsDocumentsDirectory = value;
                            break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("[ERR] >> Errore nel leggere file di configurazione = " + confFile);
            return FunctionOutcome.FAILURE;
        }
        // verifico se tutte le variabili di configurazione sono state inizializzate e se i valori sono leciti
        return checkConf();
    }

    /**
     * Funzione che verifica se le variabili di configurazione sono state inizializzate e se lo sono state, se i valori
     * sono ammissibili
     * @return SUCCESS se tutte le variabili di configurazione sono state inizializzate ed hanno valori leciti
     *         FAILURE se c'e' almeno una variabile di configurazione non inizializzata / inizializzata con un valore non
     *                 lecito
     */
    public FunctionOutcome checkConf(){
        if(this.serverHost.isEmpty()){
            System.err.println("[ERR] >> serverHost non inizializzato");
            return FunctionOutcome.FAILURE;
        }
        else if(!this.serverHost.equals("localhost") && !this.serverHost.equals("127.0.0.1.")){
            System.err.println("[ERR] >> serverHost = " + this.serverHost + " non valido");
            return FunctionOutcome.FAILURE;
        }
        else if(this.serverPort <= 1024){
            System.err.println("[ERR] >> serverPort = " + this.serverPort + " non valido");
            return FunctionOutcome.FAILURE;
        }
        else if(this.RMIPort <= 1024){
            System.err.println("[ERR] >> RMIPort = " + this.RMIPort + " non valido");
            return FunctionOutcome.FAILURE;
        }
        else if(this.multicastPort <= 1024){
            System.err.println("[ERR] >> multicastPort = " + this.multicastPort + " non valido");
            return FunctionOutcome.FAILURE;
        }
        else if(this.connectionTimeout < 0){
            System.err.println("[ERR] >> connectionTimeout = " + this.connectionTimeout + " non valido");
            return FunctionOutcome.FAILURE;
        }
        else if(this.maxNumSectionsPerDocument < 1){ //docuemnto deve avere almeno una sezione
            System.err.println("[ERR] >> maxNumSectionsPerDocument = " + this.maxNumSectionsPerDocument + " non valido");
            return FunctionOutcome.FAILURE;
        }
        else if(this.maxNumCharactersArg < 1){ //argomenti devono avere almeno un carattere
            System.err.println("[ERR] >> maxNumCharactersArg = " + this.maxNumCharactersArg + " non valido");
            return FunctionOutcome.FAILURE;
        }
        else if(this.clientsDownloadsDocumentsDirectory.isEmpty()){
            System.err.println("[ERR] >> clientsDownloadsDocumentsDirectory non inizializzao ");
            return FunctionOutcome.FAILURE;
        }

        //variabili di configurazione inizializzate e lecite
        return FunctionOutcome.SUCCESS;
    }

    /**
     * Funzione che si occupa di allocare le risorse a seguito del parsing del file di configurazione.
     * In particolare:
     * 1. verifica se esistenza della cartella Turing_database, creandola altrimenti
     * 2. verifica se esistenza della cartella Turing_edit, creandola altrimenti
     * 3. verifica se esistenza della cartella Turing_downloads, creandola altrimenti
     * 4. se le cartelle soprastanti esistono gia', le svuota
     * N.B. NO PERSISTENZA DATI TRA UNA CONNESSIONE ED UN'ALTRA => PROTOCOLLO STATELESS
     * @return SUCCESS se le 3 cartelle sono state svuotate, oppure se non esistevano sono state create
     *         FAILURE se la creazione di una delle 3 cartelle oppure lo svuotamento di una di esse non ha avuto successo
     */
    public FunctionOutcome allocateConf(){
        FunctionOutcome check = this.fileManagement.checkEsistenceDirectoryOtherwiseCreateIt(this.clientsDownloadsDocumentsDirectory);

        if(check  == FunctionOutcome.FAILURE){
            System.err.println("[ERR] >> Impossibile creare cartella di configurazione");
            return FunctionOutcome.FAILURE;
        }

        //se la cartella esisteva gia' va svuotata
        int numFilesDeleted = this.fileManagement.deleteDirectoryContent(this.clientsDownloadsDocumentsDirectory);

        if(numFilesDeleted  < 0){
            System.err.println("[ERR] >> Impossibile svuotare cartella di configurazione");
            return FunctionOutcome.FAILURE;
        }

        return FunctionOutcome.SUCCESS; //creazione/svuotamento cartella andato a buon fine
    }

    /**
     * Funzione che stampa le variabili di configurazione estrappolate dal file di configurazione
     */
    public void showConf(){
        System.out.println();
        System.out.println("[Turing] >> Configurazioni con cui si stanno eseguendo il Server:");
        System.out.println("- Nome del Server = " + this.serverHost );
        System.out.println("- Porta di registrazione = " + this.serverPort );
        System.out.println( "- Porta utilizzata per gli inviti = " + this.RMIPort);
        System.out.println( "- Porta utilizzata per gli indirizzi di multicast = " + this.multicastPort);
        System.out.println("- Valore del Timeout = " + this.connectionTimeout);
        System.out.println("- Numero massimo di sezioni = " + this.maxNumSectionsPerDocument);
        System.out.println("- Lunghezza massima dei campi da poter inserire = " + this.maxNumCharactersArg);
        System.out.println("- Directory radice dove andare a salvare i file scaricati = " + this.clientsDownloadsDocumentsDirectory);
        System.out.println();
    }
}
