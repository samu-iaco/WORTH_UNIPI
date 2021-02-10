import java.rmi.Remote;

public interface ServerInterface {
    int login(String nickName, String password);
}
