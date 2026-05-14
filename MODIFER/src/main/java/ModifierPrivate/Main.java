package ModifierPrivate;

public class Main {
    
    public static void main(String[] args) {
        Person mPerson = new Person();
        
        mPerson.setName("Nelviii"); /* untuk mengakses member private di luar
                                      class bisa menggunakan method setter
                                      dan getter.
        */
        System.out.println("Person Name: " + mPerson.getName());
    }
}