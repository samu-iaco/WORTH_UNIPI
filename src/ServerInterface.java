import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerInterface {
    int login(String nickName, String password) throws RemoteException;
}
