package nelvi.inter;

public class EmailNotification implements interfaceNotification{
    
    @Override
    public void sendMessage (String receiver, String content){
        System.out.println("Mengirim email ke " + receiver + " dengan isi: ");
        System.out.println(content);
    }
    
}
