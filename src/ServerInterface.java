import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ServerInterface {
    UserList<User> login(String nickName, String password) throws RemoteException;


}
