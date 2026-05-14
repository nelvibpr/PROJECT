package nelvi.inter;

public class SMSNotification implements interfaceNotification{
    
    @Override
    public void sendMessage (String receiver, String content){
        System.out.println("Mengirim SMS ke " + receiver + " dengan isi:");
        System.out.println(content);
    }
    
}
