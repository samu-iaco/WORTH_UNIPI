import java.io.Serializable;
import java.util.ArrayList;

public class UserList<T> implements Serializable {
    private ArrayList<T> list;
    private String result;

    public UserList(ArrayList<T> list , String result) {
        this.list = list;
        this.result = result;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public ArrayList<T> getList() {
        return list;
    }

    public void setList(ArrayList<T> list) {
        this.list = list;
    }
}
