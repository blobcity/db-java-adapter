package demo;

import com.blobcity.db.Db;
import com.blobcity.db.annotations.Entity;
import com.blobcity.db.annotations.Primary;

/**
 * Created by sanketsarang on 01/12/16.
 */
@Entity(collection = "Person", ds = "test")
public class Person extends Db {
    @Primary
    private String _id; //auto defined primary key
    private String name;
    private int age;

    public Person() {
        //do nothing
    }

    public Person(final String name, final int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
