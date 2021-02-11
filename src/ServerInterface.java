import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ServerInterface {
    List<User> login(String nickName, String password) throws RemoteException;


}
