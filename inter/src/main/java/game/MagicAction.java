package game;

public class MagicAction implements Action {
    @Override
    public void execute(String target, String message){
        System.out.println("Mengeluarkan sihir ke arah: " + target);
        System.out.println("Status: " + message);
        System.out.println("-----------------------------");
    }
    
}
