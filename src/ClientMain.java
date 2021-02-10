import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

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

public class ClientMain extends RemoteObject{
    private static final int PORT_RMI = 5000;
    private static final int PORT_TCP = 9999;
    private static final String ServerAddress = "127.0.0.1";


    private static RegisterInterfaceRMI loginRMI;
    private static ServerInterface serverInterface;
    private DataInputStream dis;
    private ObjectOutputStream oos;

    private static SocketChannel socketChannel;


    public static void main(String[] args){


        try{

            Registry r = LocateRegistry.getRegistry(PORT_RMI);
            loginRMI = (RegisterInterfaceRMI) r.lookup("ServerMain");
            socketChannel = SocketChannel.open(); //Apertura socket
            socketChannel.connect(new InetSocketAddress(ServerAddress, PORT_TCP));



        } catch (NotBoundException | IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void loginUser(ActionEvent actionEvent){
        try{
            User user = new User(UserField.getText(),passwordField.getText());
            System.out.println("Sta provando a loggare: " + UserField.getText());
            //ObjectInputStream ois = new ObjectInputStream(socketChannel.socket().getInputStream());
            oos = new ObjectOutputStream(socketChannel.socket().getOutputStream());
            //invio le credenziali al server
            //oos.writeObject(user);
            //dis = new DataInputStream(socketChannel.socket().getInputStream());
            String userName = UserField.getText();
            String password = passwordField.getText();

            socketChannel.write(ByteBuffer.wrap(userName.getBytes(StandardCharsets.UTF_8)));
            socketChannel.write(ByteBuffer.wrap(password.getBytes(StandardCharsets.UTF_8)));
            System.out.println("creato oggetto user e inviato con successo al server");
            //penso che ci sia da implementre la classe TCPClient direttamente qui dentro
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void registerUser(ActionEvent actionEvent){
        try{
            System.out.println("hao paola");
            loginRMI.register(UserField.getText() , passwordField.getText());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UserAlreadyExistException u){
            errorText.setText("Utente già registrato, prova con un altro username");
            invalidLogin.setText("hai padellato il login");
        }
    }


}
