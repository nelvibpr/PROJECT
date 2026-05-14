package game;

public class AttackAction implements Action{
    @Override
    public void execute(String target, String message){
        System.out.println("Menyerang target: " + target);
        System.out.println("Status: " + message);
        System.out.println("-----------------------------");
    }
}
