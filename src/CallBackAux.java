public class CallBackAux {
    private String clientUsername;
    private NotifyEventInterface client;

    public CallBackAux(NotifyEventInterface client , String clientUsername){
        this.client = client;
        this.clientUsername = clientUsername;
    }

    public String getClientUsername() {
        return clientUsername;
    }

    public void setClientUsername(String clientUsername) {
        this.clientUsername = clientUsername;
    }

    public NotifyEventInterface getClient() {
        return client;
    }

    public void setClient(NotifyEventInterface client) {
        this.client = client;
    }
}
