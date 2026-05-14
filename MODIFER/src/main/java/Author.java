import ModifierProtected.Person; 

public class Author{
    Person p = new Person();
   
    public Author(){
           
        
        
    p.name = "Killua's gf"; /*akan terjadi error di sini karena atribut
                              name telah diberikan modifier protected
                    */
    
    }
}
