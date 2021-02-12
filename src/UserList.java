import java.io.Serializable;
import java.util.ArrayList;

public class UserList<T> implements Serializable {
    private ArrayList<T> list;

    public UserList(ArrayList<T> list) {
        this.list = list;
    }

    public ArrayList<T> getList() {
        return list;
    }

    public void setList(ArrayList<T> list) {
        this.list = list;
    }
}
