
import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RemoteObject;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Scanner;

public class ClientMain extends RemoteObject{
    private static final int PORT_RMI = 5000;
    private static final int PORT_TCP = 9999;
    private static final String ServerAddress = "127.0.0.1";

    private DataInputStream dis;
    private ObjectInputStream ois;

    public static void main(String[] args){
        ClientMain clientMain = new ClientMain();
        clientMain.start();
    }

    public void start(){
        boolean ok = true;
        try{

            Registry r = LocateRegistry.getRegistry(PORT_RMI);
            RegisterInterfaceRMI loginRMI = (RegisterInterfaceRMI) r.lookup("ServerMain");
            SocketChannel socketChannel = SocketChannel.open(); //Apertura socket
            socketChannel.connect(new InetSocketAddress(ServerAddress, PORT_TCP));
            Scanner in = new Scanner(System.in);


            while(ok){
                String command = in.nextLine();
                String[] splittedCommand = command.split(" ");
                boolean login = false;
                switch (splittedCommand[0].toLowerCase()){
                    case "register":
                        registerUser(splittedCommand,loginRMI);
                        break;
                    case "login":
                        login = loginUser(command, socketChannel);
                        System.out.println("login: " + login);
                        break;
                }
            }
        } catch (NotBoundException | IOException | UserAlreadyExistException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void registerUser(String[] splittedCommand, RegisterInterfaceRMI loginRMI) throws IOException, UserAlreadyExistException {
        String result = "";
        if(splittedCommand.length<3) loginRMI.register("","");
        else if(splittedCommand.length>3) System.out.println("Hai inserito troppi argomenti");
        else result = loginRMI.register(splittedCommand[1],splittedCommand[2]);
        System.out.println(result);
    }

    public boolean loginUser(String command, SocketChannel socketChannel) throws IOException, ClassNotFoundException {
        System.out.println("Tentativo di login da: " + command);
        UserList<User> userList;
        socketChannel.write(ByteBuffer.wrap(command.getBytes(StandardCharsets.UTF_8)));
        System.out.println("ciao prima");
        ObjectInputStream ois = new ObjectInputStream(socketChannel.socket().getInputStream());   //blocco qui
        System.out.println("ciao dopo");
        userList = (UserList<User>) ois.readObject();
        if(userList.getList().size() != 0) System.out.println("non male");
        return true;
    }



}
