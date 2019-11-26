package itp341.liu.haomei.finalprojecthaomeiliu.db;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.List;

@Table(name = "users", id = "_id")
public class UserEntry extends Model {

    @Column(name = "Username")
    public String username;

    @Column(name = "AppKey")
    public String appKey;

    public UserEntry() {
        super();
    }

    public UserEntry(String username, String appKey) {
        super();
        this.username = username;
        this.appKey = appKey;
    }

    public static UserEntry getUser(String username, String appKey) {
        return new Select().from(UserEntry.class).where("Username = ?", username)
                .where("AppKey = ?", appKey).executeSingle();
    }

}
