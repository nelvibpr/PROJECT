package ThisSuper;

public class Employee extends Person {
  
    
    float salary = 2000f;
    String name = "Killua";
    int age = 25;
    
    public void showInfo(){
        System.out.println("Name: " + super.name);
        System.out.println("Age: " + super.age);
        System.out.println("Salary: $" + salary);
    }
    
}
