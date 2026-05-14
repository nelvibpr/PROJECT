package SetterGetter;

public class MainSG {

    public static void main(String[] args) {
        UserSG alif = new UserSG();
        
        alif.setUsername("alifah");
        alif.setPassword("kopiJava");
        
        System.out.println("Username: " + alif.getUsername());
        System.out.println("Password: " + alif.getPassword());
    }
    
}
