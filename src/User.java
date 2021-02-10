import java.io.Serializable;

public class User implements Serializable {
    private String name;
    private String psw;
    private String status;

    public User(String name, String psw) {
        this.name = name;
        this.psw = psw;
        this.status = "offline";
    }

    public User(){
        this.status = "offline";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPsw() {
        return psw;
    }

    public void setPsw(String psw) {
        this.psw = psw;
    }

    public String getStatus(){
        return status;
    }

    public void changeStatus(String newStatus){
        if(newStatus.equals("online") || newStatus.equals("Online")) this.status = "online";
        else if(newStatus.equals("offline") || newStatus.equals("Offline")) this.status = "offline";
    }

    public boolean sameUser(String uName){
        if(this.name.equals(uName)) return true;
        else return false;
    }
}
