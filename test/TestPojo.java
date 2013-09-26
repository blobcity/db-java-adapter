
import com.blobcity.db.fieldannotations.Primary;
import com.blobcity.db.iconnector.BlobCityCloudStorage;

/**
 *
 * @author Sanket Sarang <sanket@blobcity.net>
 */
public class TestPojo extends BlobCityCloudStorage {
    
    @Primary
    private String id;

    public String getId() {
        return id;
    }
    
    public static boolean exists(String key) {
        return BlobCityCloudStorage.exists(TestPojo.class, key);
    }
    
    public static TestPojo newInstance() {
        return (TestPojo) BlobCityCloudStorage.newInstance(TestPojo.class);
    }
    
    public static TestPojo newInstance(final String pk) {
        return (TestPojo) BlobCityCloudStorage.newInstance(TestPojo.class, pk);
    }
}
