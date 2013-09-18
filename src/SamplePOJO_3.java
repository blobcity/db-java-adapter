

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import com.blobcity.db.classannotations.BlobCityCredentials;
import com.blobcity.db.classannotations.BlobCityEntity;
import com.blobcity.db.fieldannotations.Column;
import com.blobcity.db.fieldannotations.Primary;
import com.blobcity.db.iconnector.BlobCityCloudStorage;

/**
 *
 * @author Karishma
 */
@BlobCityCredentials(account = "blobcity", user = "100", token = "blobcity")
@BlobCityEntity(db = "BlobCity", table = "User")
public class SamplePOJO_3 extends BlobCityCloudStorage {

    @Primary
    @Column(name = "ID")
    private String ID;
    @Column(name = "Name")
    private String name;
    @Column(name = "bool")
    private boolean boolStr;

    public boolean isBoolStr() {
        return boolStr;
    }

    public void setBoolStr(boolean boolStr) {
        this.boolStr = boolStr;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SamplePOJO_3() {
       
    }

    public SamplePOJO_3(String account, String user, String token) {
        super(account, user, token);
    }
}
