package com.tolunaykandirmaz.mywords.Helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.tolunaykandirmaz.mywords.Interfaces.WordStuffListener;
import com.tolunaykandirmaz.mywords.Modals.WordItem;
import com.tolunaykandirmaz.mywords.R;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class DatabaseHelper {

    private final String DBNAME = "WordsDB";
    public final String UNKNOWNWORDS = "unknownWords";
    public final String KNOWNWORDS = "knownWords";
    public final String NOTDECIDEDWORDS = "notdecidedWords";
    public final String WORDS = "words";

    private static DatabaseHelper databaseHelper = null;
    private Context context;

    public DatabaseHelper(Context context){
        this.context = context;
        SQLiteDatabase database = context.openOrCreateDatabase(DBNAME,Context.MODE_PRIVATE,null);
        database.execSQL("CREATE TABLE IF NOT EXISTS " + WORDS + " (id INTEGER PRIMARY KEY, nativeWord VARCHAR, foreignWord VARCHAR, image BLOB)");
        database.execSQL("CREATE TABLE IF NOT EXISTS " + UNKNOWNWORDS + " (wordId INTEGER REFERENCES words(id))");
        database.execSQL("CREATE TABLE IF NOT EXISTS " + KNOWNWORDS + " (wordId INTEGER REFERENCES words(id))");
        database.execSQL("CREATE TABLE IF NOT EXISTS " + NOTDECIDEDWORDS + " (wordId INTEGER REFERENCES words(id))");
        database.close();
    }

    public static DatabaseHelper getInstance(Context context){
        if(databaseHelper == null){
            databaseHelper = new DatabaseHelper(context);
        }
        return databaseHelper;
    }

    public void deleteWord(WordItem word, WordStuffListener listener){
        SQLiteDatabase database = context.openOrCreateDatabase(DBNAME,Context.MODE_PRIVATE,null);
        database.delete(WORDS,"id=?",new String[]{String.valueOf(word.getId())});
        database.delete(UNKNOWNWORDS,"wordId=?",new String[]{String.valueOf(word.getId())});
        database.delete(KNOWNWORDS,"wordId=?",new String[]{String.valueOf(word.getId())});
        database.delete(NOTDECIDEDWORDS,"wordId=?",new String[]{String.valueOf(word.getId())});
        database.close();
        listener.wordDeleted();
    }

    public void moveWord(WordItem word, String from, String to){
        SQLiteDatabase database = context.openOrCreateDatabase(DBNAME, Context.MODE_PRIVATE,null);
        database.execSQL("DELETE FROM " + from + " WHERE wordId="+word.getId());
        ContentValues contentValues = new ContentValues();
        contentValues.put("wordId", word.getId());
        database.insert(to,null, contentValues);
        database.close();
    }

    public void editWord(final WordItem word, final String newNativeWord, final String newForeignWord, final String newUrl, final WordStuffListener listener) throws Exception{

        if(newUrl == null){
            SQLiteDatabase database = context.openOrCreateDatabase(DBNAME,Context.MODE_PRIVATE,null);
            ContentValues contentValues = new ContentValues();
            contentValues.put("nativeWord",newNativeWord);
            contentValues.put("foreignWord",newForeignWord);
            database.update(WORDS,contentValues,"id=?",new String[]{String.valueOf(word.getId())});
            database.close();
            listener.wordEdited();
            return;
        }


        Picasso.get().load(newUrl).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                Bitmap compressedBitmap = compressBitmap(bitmap,300);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                compressedBitmap.compress(Bitmap.CompressFormat.PNG, 50, outputStream);
                byte[] image = outputStream.toByteArray();
                SQLiteDatabase database = context.openOrCreateDatabase(DBNAME,Context.MODE_PRIVATE,null);
                ContentValues contentValues = new ContentValues();
                contentValues.put("nativeWord",newNativeWord);
                contentValues.put("foreignWord",newForeignWord);
                contentValues.put("image",image);
                database.update(WORDS,contentValues,"id=?",new String[]{String.valueOf(word.getId())});
                database.close();
                listener.wordEdited();
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                SQLiteDatabase database = context.openOrCreateDatabase(DBNAME,Context.MODE_PRIVATE,null);
                ContentValues contentValues = new ContentValues();
                contentValues.put("nativeWord",newNativeWord);
                contentValues.put("foreignWord",newForeignWord);
                database.update(WORDS,contentValues,"id=?",new String[]{String.valueOf(word.getId())});
                database.close();
                listener.wordEdited();
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });


    }

    private void addWordToDB(String nativeWord, String foreignWord, byte[] image, WordStuffListener listener){
        SQLiteDatabase database = context.openOrCreateDatabase(DBNAME,Context.MODE_PRIVATE,null);
        ContentValues contentValues = new ContentValues();
        contentValues.put("nativeWord",nativeWord);
        contentValues.put("foreignWord",foreignWord);
        contentValues.put("image",image);
        long a = database.insert(WORDS,null, contentValues);
        ContentValues valuesForUNKNOWN = new ContentValues();
        valuesForUNKNOWN.put("wordId",a);
        database.insert(UNKNOWNWORDS,null,valuesForUNKNOWN);
        database.close();
        listener.wordAdded();
    }

    public void addWord(final String nativeWord, final String foreignWord, String url, final WordStuffListener listener) throws Exception{
        if(url == null){
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.no_image);
            bitmap = compressBitmap(bitmap,300);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG,50,outputStream);
            byte[] image = outputStream.toByteArray();
            addWordToDB(nativeWord,foreignWord,image,listener);
            return;
        }
        Picasso.get().load(url).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                Bitmap compressedBitmap = compressBitmap(bitmap,300);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                compressedBitmap.compress(Bitmap.CompressFormat.PNG, 50, outputStream);
                byte[] image = outputStream.toByteArray();
                addWordToDB(nativeWord,foreignWord,image,listener);
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.no_image);
                Bitmap compressBitmap = compressBitmap(bitmap,300);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                compressBitmap.compress(Bitmap.CompressFormat.PNG, 50, outputStream);
                byte[] image = outputStream.toByteArray();
                addWordToDB(nativeWord,foreignWord,image,listener);
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });



    }

    public ArrayList<WordItem> getWords(String type) throws Exception{
        SQLiteDatabase database = context.openOrCreateDatabase(DBNAME,Context.MODE_PRIVATE,null);
        ArrayList<WordItem> result = new ArrayList<>();
        Cursor cursor;
        switch (type){
            case UNKNOWNWORDS:
                cursor = database.rawQuery("SELECT "+WORDS+".id, "+WORDS+".nativeWord, "+WORDS+".foreignWord, "+WORDS+".image"+" FROM "+UNKNOWNWORDS
                        + " INNER JOIN "+WORDS+" ON "+WORDS+".id = "+UNKNOWNWORDS+".wordId ORDER BY id DESC;",null);
                break;
            case NOTDECIDEDWORDS:
                cursor = database.rawQuery("SELECT "+WORDS+".id, "+WORDS+".nativeWord, "+WORDS+".foreignWord, "+WORDS+".image"+" FROM "+NOTDECIDEDWORDS
                        + " INNER JOIN "+WORDS+" ON "+WORDS+".id = "+NOTDECIDEDWORDS+".wordId ORDER BY id DESC;",null);
                break;
            case KNOWNWORDS:
                cursor = database.rawQuery("SELECT "+WORDS+".id, "+WORDS+".nativeWord, "+WORDS+".foreignWord, "+WORDS+".image"+" FROM "+KNOWNWORDS
                        + " INNER JOIN "+WORDS+" ON "+WORDS+".id = "+KNOWNWORDS+".wordId ORDER BY id DESC;",null);
                break;
            default:
                cursor = database.rawQuery("SELECT id, nativeWord, foreignWord, image FROM "+WORDS + " ORDER BY id DESC;",null);
                break;
        }
        while(cursor.moveToNext()){
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            String nativeWord = cursor.getString(cursor.getColumnIndex("nativeWord"));
            String foreignWord = cursor.getString(cursor.getColumnIndex("foreignWord"));
            byte[] image = cursor.getBlob(cursor.getColumnIndex("image"));
            result.add(new WordItem(id,nativeWord,foreignWord,image));
        }
        cursor.close();
        database.close();
        return result;
    }

    private Bitmap compressBitmap(Bitmap bitmap, int maxSize){
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        double ratio = width/(double)height;

        if(ratio>=1){
            width = maxSize;
            height = (int)(width/ratio);
        }else{
            height = maxSize;
            width = (int)(height*ratio);
        }
        return Bitmap.createScaledBitmap(bitmap,width,height,true);
    }
}
