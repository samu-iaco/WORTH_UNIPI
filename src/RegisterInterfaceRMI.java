import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RegisterInterfaceRMI extends Remote {
    //metodo per eseguire la registrazione tramite username e password
    String register(String nickName, String password) throws IOException, UserAlreadyExistException;

    void registerForCallback(NotifyEventInterface ClientInterface, String nickUtente) throws RemoteException;

    void unregisterForCallback(NotifyEventInterface ClientInterface) throws RemoteException;

}
