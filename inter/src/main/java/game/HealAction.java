package game;

public class HealAction implements Action{
    @Override
    public void execute(String target, String message){
        System.out.println("Memberikan penyembuhan kepada: " + target);
        System.out.println("Status: " + message);
        System.out.println("-----------------------------");
    }
    
}
