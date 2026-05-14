package ThisSuper;

public class userThis {
    private String name;
    private int age;
    private float salary;
    
    public void setNama(String name){
        this.name = name;
    }
    public void setUmur(int age){
        this.age = age;
    }
    public void setGaji(float salary){
        this.salary = salary;
    }
    
    public String getName(){
        return this.name;
    }
    public int getAge(){
        return this.age;
    }
    public float getSalary(){
        return this.salary;
    }
    
}
