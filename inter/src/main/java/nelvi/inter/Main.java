package nelvi.inter;

public class Main {

    public static void main(String[] args) {
        String emailPenerima = "nelvirampa13@gmail.com";
        String nomerHp = "082296961332";
        String mobileId = "android-123456";

        EmailNotification emailNotif = new EmailNotification();
        SMSNotification smsNotif = new SMSNotification();
        PushNotification pushNotif = new PushNotification();

        String message = "Ayo follow Instagram @nelvyyy_\n";
        
        emailNotif.sendMessage(emailPenerima, message);
        smsNotif.sendMessage(nomerHp, message);
        pushNotif.sendMessage(mobileId, message);
    }
}
