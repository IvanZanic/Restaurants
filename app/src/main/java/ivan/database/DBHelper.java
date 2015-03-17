package ivan.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;
import java.util.List;

import ivan.rest.domain.Restaurant;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "restaurants.db";
    public static final String CONTACTS_TABLE_NAME = "restaurant";
    public static final String CONTACTS_COLUMN_ID = "id";
    public static final String CONTACTS_COLUMN_NAME = "name";
    public static final String CONTACTS_COLUMN_ADDRESS = "address";
    public static final String CONTACTS_COLUMN_LONGITUDE = "longitude";
    public static final String CONTACTS_COLUMN_LATITUDE = "latitude";
    public static final String CONTACTS_COLUMN_PHOTO = "photo";

    private static final String TABLE_CREATE = "create table IF NOT EXISTS"
            + CONTACTS_TABLE_NAME + "(" + CONTACTS_COLUMN_ID
            + " integer primary key autoincrement, " + CONTACTS_COLUMN_NAME
            + " text not null, " + CONTACTS_COLUMN_ADDRESS
            + " text not null, " + CONTACTS_COLUMN_LONGITUDE
            + " real not null, " + CONTACTS_COLUMN_LATITUDE
            + " real not null, " + CONTACTS_COLUMN_PHOTO
            + " text null);";

    public DBHelper(Context context)
    {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS " + CONTACTS_TABLE_NAME);
        onCreate(db);
    }

    public boolean insertRestaurant  (Restaurant restaurant)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(CONTACTS_COLUMN_NAME, restaurant.getName());
        contentValues.put(CONTACTS_COLUMN_ADDRESS, restaurant.getAddress());
        contentValues.put(CONTACTS_COLUMN_LATITUDE, restaurant.getLatitude());
        contentValues.put(CONTACTS_COLUMN_LONGITUDE, restaurant.getLongitude());
        contentValues.put(CONTACTS_COLUMN_PHOTO, restaurant.getPhoto());

        db.insertOrThrow(CONTACTS_TABLE_NAME, null, contentValues);
        db.close();
        return true;
    }

    public boolean insertListOfRestaurants (List<Restaurant> restaurantList) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        db.beginTransaction();
        try {
            for (Restaurant restaurant : restaurantList) {
                contentValues.put(CONTACTS_COLUMN_NAME, restaurant.getName());
                contentValues.put(CONTACTS_COLUMN_ADDRESS, restaurant.getAddress());
                contentValues.put(CONTACTS_COLUMN_LATITUDE, restaurant.getLatitude());
                contentValues.put(CONTACTS_COLUMN_LONGITUDE, restaurant.getLongitude());
                contentValues.put(CONTACTS_COLUMN_PHOTO, restaurant.getPhoto());

                db.insertOrThrow(CONTACTS_TABLE_NAME, null, contentValues);
                contentValues.clear();
            }
            db.setTransactionSuccessful();
        } catch (Exception e){
            //Error in between database transaction
            return false;
        } finally {
            db.endTransaction();
            db.close();
            return true;
        }
    }

    public Restaurant getRestaurant(int id){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res = db.rawQuery(
                "select * from " + CONTACTS_TABLE_NAME + " where id=?",
                new String[] {String.valueOf(id)}
        );
        res.moveToFirst();
        Restaurant restaurant = new Restaurant();
        restaurant.setId(res.getInt(res.getColumnIndex(CONTACTS_COLUMN_ID)));
        restaurant.setName(res.getString(res.getColumnIndex(CONTACTS_COLUMN_NAME)));
        restaurant.setAddress(res.getString(res.getColumnIndex(CONTACTS_COLUMN_ADDRESS)));
        restaurant.setLatitude(res.getDouble(res.getColumnIndex(CONTACTS_COLUMN_LATITUDE)));
        restaurant.setLongitude(res.getDouble(res.getColumnIndex(CONTACTS_COLUMN_LONGITUDE)));
        restaurant.setPhoto(res.getString(res.getColumnIndex(CONTACTS_COLUMN_PHOTO)));

        db.close();
        return restaurant;
    }
    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, CONTACTS_TABLE_NAME);
        db.close();
        return numRows;
    }
    public boolean updateContact (Restaurant restaurant)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CONTACTS_COLUMN_NAME, restaurant.getName());
        contentValues.put(CONTACTS_COLUMN_ADDRESS, restaurant.getAddress());
        contentValues.put(CONTACTS_COLUMN_LATITUDE, restaurant.getLatitude());
        contentValues.put(CONTACTS_COLUMN_LONGITUDE, restaurant.getLongitude());
        contentValues.put(CONTACTS_COLUMN_PHOTO, restaurant.getPhoto());
        db.update(CONTACTS_TABLE_NAME, contentValues, "id = ? ", new String[] { Integer.toString(restaurant.getId()) } );
        db.close();
        return true;
    }

    public List<Restaurant> getAllRestaurants()
    {
        List<Restaurant> restaurantList = new ArrayList();
        Restaurant restaurant;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from " + CONTACTS_TABLE_NAME, null );
        res.moveToFirst();
        while(res.isAfterLast() == false){
            restaurant = new Restaurant();
            restaurant.setId(res.getInt(res.getColumnIndex(CONTACTS_COLUMN_ID)));
            restaurant.setLatitude(res.getDouble(res.getColumnIndex(CONTACTS_COLUMN_LATITUDE)));
            restaurant.setLongitude(res.getDouble(res.getColumnIndex(CONTACTS_COLUMN_LONGITUDE)));
            restaurantList.add(restaurant);
            res.moveToNext();
        }
        db.close();
        return restaurantList;
    }
}
