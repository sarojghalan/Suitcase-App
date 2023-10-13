package np.com.sarojghalan.suitcase.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import np.com.sarojghalan.suitcase.db.product.Product;
import np.com.sarojghalan.suitcase.db.product.ProductDao;
import np.com.sarojghalan.suitcase.db.user.User;
import np.com.sarojghalan.suitcase.db.user.UserDao;

@Database(entities = {User.class, Product.class}, version = 1)
public abstract class SuitcaseDatabase extends RoomDatabase {
    public abstract UserDao getUserDao();
    public abstract ProductDao getProductDao();
    private static SuitcaseDatabase INSTANCE;

    public static SuitcaseDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(
                    context,
                    SuitcaseDatabase.class,
                    "suitcase.db"
            ).build();
        }
        return INSTANCE;
    }
}
