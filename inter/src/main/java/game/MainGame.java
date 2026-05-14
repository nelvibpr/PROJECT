package game;

public class MainGame {

    public static void main(String[] args) {
        String enemy = "Naga Hitam Legendaris";
        String player = "Ksatria Arthur";
        String magicTarget = "Penyihir Kegelapan";
        
        Action attack = new AttackAction();
        Action heal = new HealAction();
        Action magic = new MagicAction();
        
        String message1 = "Tebasan Pedang Excalibur menghasilkan 999 Critical Damage!";
        String message2 = "Meminum Ramuan Suci, HP pulih 1000 poin!";
        String message3 = "Badai Meteor Api meluluhlantakkan area, 5000 Area Damage!!!";
        
        attack.execute(enemy, message1);
        heal.execute(player, message2);
        magic.execute(magicTarget, message3);
    }
    
}
