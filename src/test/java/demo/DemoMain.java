package demo;

import com.blobcity.db.Db;
import com.blobcity.db.config.Credentials;
import com.blobcity.db.enums.CollectionType;
import com.blobcity.db.search.Query;
import com.blobcity.db.search.SearchParam;
import com.google.gson.JsonObject;

import java.util.List;

/**
 * Created by sanketsarang on 01/12/16.
 */
public class DemoMain {

    public static void main(String[] args) {
        Credentials.init("localhost:10111","root","root","test");

        Db.createDs("test");
        Db.createCollection("Person", CollectionType.ON_DISK);

        /* JSON Record */
        JsonObject jsonObject1 = new JsonObject();
        jsonObject1.addProperty("name","name1");
        jsonObject1.addProperty("age",45);
        Db.insertJson("Person", jsonObject1);

        /* XML Record */
        String xml1 = "<name>name1</name>" +
                "<age>20</age>";

        Db.insertXml("Person", xml1);

        /* Selecting a record */

//        List<Person> list = Db.search(Query.select().from(Person.class).where(SearchParam.create("age").gt(25)));

//        for(Person person : list) {
//            System.out.println(person.getName() + " " + person.getAge());
//        }

        List<Object> pkList = Db.selectAll(Person.class);

        for(Object pk : pkList) {
            Person person = Db.newLoadedInstance(Person.class, pk);
            System.out.println(person.getName() + " " + person.getAge());
        }

    }
}
