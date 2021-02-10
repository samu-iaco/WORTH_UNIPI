import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public class TCPClient {
    private static final int PORT_TCP = 9999;
    private static final String ServerAddress = "127.0.0.1";

    private DataInputStream dis;
    private ObjectOutputStream oos;
    private SocketChannel client;
    private String nickName;
    private String password;

    public TCPClient(String nickName, String password) throws IOException {
        this.nickName = nickName;
        this.password = password;
        InetSocketAddress hA = new InetSocketAddress(ServerAddress,PORT_TCP);
        client = SocketChannel.open(hA);
        /*
        oos = new ObjectOutputStream(client.socket().getOutputStream());
        User user = new User(nickName,password);
        oos.writeObject(user);
        dis = new DataInputStream(client.socket().getInputStream());


         */

    }
}
