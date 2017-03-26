package com.example.nouno.easydep;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by nouno on 25/03/2017.
 */

public class DBConnection extends SQLiteOpenHelper {

    public static final int Verson=1;
    public static final String DbName="DepannageService.db";
    public DBConnection(Context context)
    {
        super(context,DbName,null,Verson);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table IF NOT EXISTS dep (firstName TEXT, lastName TEXT,id INTEGER primary key,location TEXT,rating REAL,price INTEGER,phoneNumber TEXT)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("Drop table if EXISTS dep");
        onCreate(db);

    }


    public void InsertToDepanneur(OfflineRepairService repairService) //Rempli la table Depanneur
    {
        SQLiteDatabase db=this.getWritableDatabase();//écrire dans la bdd
        ContentValues contentValues=new ContentValues();
        // contentValues.put("id",repairService.getId());
        contentValues.put("firstName",repairService.getFirstName());
        contentValues.put("lastName",repairService.getLastName());
        contentValues.put("location",repairService.getLocation());

        contentValues.put("rating",repairService.getRating());
        contentValues.put("price",repairService.getPrice());//remplir les champs de SQLite
        contentValues.put("phoneNumber",repairService.getPhoneNumber());
        db.insert("dep",null,contentValues);//insertion dans

    }


    public void InsetToDB(ArrayList<OfflineRepairService> repairService)
    {
        for(int i=0; i<repairService.size();i++)
        {

            SQLiteDatabase db=this.getWritableDatabase();
            InsertToDepanneur(repairService.get(i)); //on rempli notre SQlite avec les élements du ArrayList envoyer depuis l'intent

        }
    }
    public String getFirstName ()
    {

        SQLiteDatabase db=this.getReadableDatabase();
        Cursor res=db.rawQuery("SELECT firstName FROM dep firstName='kenza'",null);
        return res.getString(res.getColumnIndex("firstName"));

    }



    public ArrayList<OfflineRepairService> getAllrecords() //Méthode qui permet d'emmener tous les élement du SQLite
    {
        ArrayList <OfflineRepairService> arrayList=new ArrayList ();
        SQLiteDatabase db=this.getReadableDatabase(); //Lire de la bdd
        Cursor res=db.rawQuery("SELECT * FROM dep",null);
        res.moveToFirst();//se déplacer au 1er élement
        while (res.isAfterLast()==false)//Tant qu'on est pas arrivé à la fin
        {
            OfflineRepairService repairService=new OfflineRepairService(res.getLong(res.getColumnIndex("id")),res.getString(res.getColumnIndex("firstName")),res.getString(res.getColumnIndex("lastName")),res.getString(res.getColumnIndex("location")),res.getString(res.getColumnIndex("phoneNumber")),res.getFloat(res.getColumnIndex("rating")),res.getInt(res.getColumnIndex("price")));
            arrayList.add(repairService);//Apporte moi la valeur du champ Nom
            res.moveToNext();
        }

        return arrayList;
    }

    public void deleteFromDepanneur()
    {
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("DELETE FROM dep");


    }





}
