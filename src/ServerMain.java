import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RemoteObject;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;


public class ServerMain extends RemoteObject implements RegisterInterfaceRMI,ServerInterface{
    private static final int PORT_RMI = 5000;
    private static final int PORT_TCP = 9999;
    private final File dataDir;
    private File usersDir;
    private final ObjectMapper mapper;
    private List<User> users; //utenti registrati
    private List<CallBackAux> clients;

    public ServerMain(){
        super();
        clients = new ArrayList<>();
        this.mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, true);
        dataDir = new File("./Data");
        loadData();
    }

    public void loadData(){
        this.usersDir = new File(dataDir + "./Users.json");
        if(!dataDir.exists()){
            dataDir.mkdir();
        }
        try{
            if(!usersDir.exists()){
                this.users = new ArrayList<>();
                usersDir.createNewFile();
                mapper.writeValue(usersDir, users);
            }
            else{
                this.users = new ArrayList<>(Arrays.asList(mapper.readValue(usersDir,User[].class)));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start(){
        ServerSocketChannel serverSocket = null;
        Selector selector = null;
        String command;
        String[] splittedCommand;

        try{
            serverSocket = ServerSocketChannel.open();  //apertura del socket di ascolto
            serverSocket.socket().bind(new InetSocketAddress(PORT_TCP));

            selector = Selector.open(); //apertura del selettore
            serverSocket.configureBlocking(false);  //server socket in modalita non bloccante
            serverSocket.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("Server ready...");
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (true) {
            try {
                selector.select();

            } catch (IOException ex) {
                ex.printStackTrace();
                break;
            }

            Set<SelectionKey> readyKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = readyKeys.iterator();

            while (iterator.hasNext()){
                SelectionKey key = iterator.next();
                iterator.remove();
                try{
                    if(key.isAcceptable()){     //accettazione di richieste
                        ServerSocketChannel server = (ServerSocketChannel) key.channel();
                        SocketChannel client = server.accept();
                        System.out.println("SERVER: connessione da " + client + " accettata");
                        client.configureBlocking(false);
                        client.register(selector,SelectionKey.OP_READ);

                    }else if(key.isReadable()){
                        SocketChannel client = (SocketChannel) key.channel();

                        ByteBuffer buffer = ByteBuffer.allocate(128);
                        client.read(buffer);
                        command = new String(buffer.array()).trim();
                        splittedCommand = command.split(" ");
                        System.out.println("splitted 0: " + splittedCommand[0]);
                        switch (splittedCommand[0].toLowerCase()){
                            case "login":
                                if(splittedCommand.length<3) login("","");
                                else if(splittedCommand.length>3) System.out.println("Hai inserito troppi argomenti");
                                else login(splittedCommand[1], splittedCommand[2]);
                                //devo aggiungere la risposta da inviare al client
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @Override
    public synchronized String register(String nickName, String password) throws IOException, UserAlreadyExistException {
        System.out.println("Richiesta di registrazione da parte di: " + nickName);

        if(nickName.isEmpty() || password.isEmpty()) {
            System.err.println("Il nome utente o la password non possono essere vuoti");
            throw new IllegalArgumentException();
        }

        User user = new User(nickName,password);

        if(userAlreadyExist(user)) {
            System.err.println("Utente gia registrato");
            throw new UserAlreadyExistException("esisto di gia"); //se l'utente esiste già
        }
        else{
            users.add(user);
            update(nickName,"offline");
            try{
                mapper.writeValue(usersDir,users);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Totale utenti registrati: " + users.size());
            return "OK";
        }


    }

    @Override
    public List<User> login(String nickName, String password) throws RemoteException {
        System.out.println("Richiesta di LOGIN da parte di: " + nickName);
        ArrayList<User> returnList = new ArrayList<>();
        if(nickName.isEmpty() || password.isEmpty()) System.err.println("Il nome utente e la paword non possono essere vuoti");
        User user = new User(nickName,password);
        for(User currUser: users){
            if(user.getName().equals(currUser.getName())){
                if(user.getPsw().equals(currUser.getPsw())){
                    if(user.getStatus().equals("offline")){
                        update(user.getName(),"online");
                        user.changeStatus("online");
                        System.out.println("L'utente " + user.getName() + " ha cambiato il suo status in: " + user.getStatus());
                    }

                }
            }
            returnList.add(user);

        }

        return returnList;
    }

    @Override
    public synchronized void registerForCallback(NotifyEventInterface ClientInterface, String userName) throws RemoteException {
        for(CallBackAux client : clients){
            if(!client.getClient().equals(ClientInterface)){
                clients.add(new CallBackAux(ClientInterface,userName));
                System.out.println("CALLBACK: nuovo client aggiunto");
            }
        }
    }

    @Override
    public synchronized void unregisterForCallback(NotifyEventInterface ClientInterface) throws RemoteException {
        for(CallBackAux curr: clients){
            if(curr.getClient().equals(ClientInterface)){
                clients.remove(curr);
                System.out.println("CALLBACK: client rimosso");
            }
            else System.out.println("CALLBACK: errore nella rimozione del client");
        }
    }



    public synchronized void doCallbacks(String userName, String status) throws RemoteException{
        System.out.println("Callbacks iniziate");
        for (CallBackAux clientinfo : clients) {
            NotifyEventInterface client =  clientinfo.getClient();
            try{
                client.notifyEvent(userName,status);
            }catch (RemoteException e){
                System.err.println("errore in notifyEvent");
            }
        }
        System.out.println("Callbacks completate");
    }

    public void update(String userName, String status) throws RemoteException {
        doCallbacks(userName,status);
    }

    public static void main(String[] args)  {
        ServerMain server = new ServerMain();

        try{
            RegisterInterfaceRMI stub = (RegisterInterfaceRMI) UnicastRemoteObject.exportObject(server,0);
            LocateRegistry.createRegistry(PORT_RMI);
            Registry r = LocateRegistry.getRegistry(PORT_RMI);
            r.rebind("ServerMain",stub);
        }catch (RemoteException e){
            e.printStackTrace();
        }
        server.start();
    }

    private boolean userAlreadyExist(User user) {
        for(User curr: users){
            if(user.getName().equals(curr.getName())){
                return true;
            }
        }
        return false;
    }



}
