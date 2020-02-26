package com.tolunaykandirmaz.mywords;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.squareup.picasso.Picasso;
import com.tolunaykandirmaz.mywords.Adapters.ImageSearchListAdapter;
import com.tolunaykandirmaz.mywords.Helper.ConnectionHelper;
import com.tolunaykandirmaz.mywords.Helper.DatabaseHelper;
import com.tolunaykandirmaz.mywords.Helper.JSONParser;
import com.tolunaykandirmaz.mywords.Interfaces.ItemClickListener;
import com.tolunaykandirmaz.mywords.Interfaces.WordStuffListener;
import com.tolunaykandirmaz.mywords.Modals.WordItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private AnimatorSet setRightOut;
    private AnimatorSet setLeftIn;
    private View cardFrontLayout;
    private View cardBackLayout;
    private boolean isBackVisible = false;
    private FrameLayout card;
    private TextView textViewAns;
    private Animation animation;
    private Animation animationBack;
    private final String TAG = "HATA_MainActivity";
    private View bottomSheetView;
    private String chosenUrl = null;
    private ArrayList<WordItem> words = null;
    private int wordCount = 0;
    private ImageView imageViewCard;
    private TextView textViewCard;
    private Button buttonListSelect;
    private String currentType;
    private CardView deleteCard;
    private CardView editCard;
    private boolean hideImage = true;
    private boolean hideNativeWord = true;
    private AdView mAdView;
    private int adsCount = 0;
    private boolean adsLoaded = false;
    private boolean interstitialShowed = false;
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final FrameLayout card_front = findViewById(R.id.card_front_include);
        final FrameLayout card_back = findViewById(R.id.card_back_include);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-8501566775769359/8912053019");
        mInterstitialAd.loadAd(new AdRequest.Builder().addTestDevice("20004E7B5939C81A3081A13A1A9EF230").addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build());

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                mInterstitialAd.loadAd(new AdRequest.Builder().addTestDevice("20004E7B5939C81A3081A13A1A9EF230").addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build());
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when the ad is displayed.
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the interstitial ad is closed.
            }
        });



        mAdView = card_front.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice("20004E7B5939C81A3081A13A1A9EF230").addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
        mAdView.loadAd(adRequest);

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                Log.e(TAG, "Ads yüklendi");
                adsLoaded = true;
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
                Log.e(TAG, String.valueOf(errorCode));

                AdRequest adRequest = new AdRequest.Builder().addTestDevice("20004E7B5939C81A3081A13A1A9EF230").addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
                mAdView.loadAd(adRequest);
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }
        });

        SharedPreferences preferences = getSharedPreferences("settings",MODE_PRIVATE);
        hideNativeWord = preferences.getBoolean("hideNativeWord",true);
        hideImage = preferences.getBoolean("hideImage",true);

        textViewCard = card_front.findViewById(R.id.textView2);

        imageViewCard = card_back.findViewById(R.id.imageView);

        buttonListSelect = findViewById(R.id.buttonListSelect);
        textViewAns = findViewById(R.id.textView);
        textViewAns.setVisibility(View.INVISIBLE);

        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(getApplicationContext());
        currentType = databaseHelper.UNKNOWNWORDS;
        loadList(databaseHelper.UNKNOWNWORDS,0);


        deleteCard = findViewById(R.id.deleteCardView);
        editCard = findViewById(R.id.editCardView);
        editCard.setVisibility(View.INVISIBLE);
        deleteCard.setVisibility(View.INVISIBLE);

        cardFrontLayout = findViewById(R.id.card_front);

        cardBackLayout = findViewById(R.id.card_back);
        card = findViewById(R.id.card);
        animation = AnimationUtils.loadAnimation(this,R.anim.textview_ans_anim);
        animationBack = AnimationUtils.loadAnimation(this,R.anim.textview_ans_back_anim);

        setRightOut = (AnimatorSet) AnimatorInflater.loadAnimator(this,R.animator.out_animation);
        setLeftIn = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.in_animation);



        setRightOut.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                if(isBackVisible){
                    startAnimationDeleteCardGoReverse();
                }else{
                    startAnimationDeleteCardGo();
                }
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                card.setClickable(true);
                isBackVisible = !isBackVisible;
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                card.setClickable(true);
                if(isBackVisible){
                    textViewAns.setVisibility(View.VISIBLE);
                    cardBackLayout.setAlpha(1.0f);
                    cardFrontLayout.setAlpha(0.0f);
                    cardBackLayout.setRotationY(0f);
                }else{
                    textViewAns.setVisibility(View.INVISIBLE);
                    cardBackLayout.setAlpha(0.0f);
                    cardFrontLayout.setAlpha(1.0f);
                    cardFrontLayout.setRotationY(0f);
                }

                if(!isBackVisible){
                    startAnimationDeleteCardGoReverse();
                    deleteCard.setClickable(false);
                    editCard.setClickable(false);
                }

                isBackVisible = !isBackVisible;

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });


        int distance = 8000;
        float scale = getResources().getDisplayMetrics().density * distance;
        cardFrontLayout.setCameraDistance(scale);
        cardBackLayout.setCameraDistance(scale);
    }

    public void loadList(String type, int count){
        wordCount = count;
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(getApplicationContext());
        try {
            words = databaseHelper.getWords(type);
            if(words.size() == 0){
                textViewCard.setText("Please Add Word");
                textViewAns.setText("");
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.no_image);
                imageViewCard.setImageBitmap(bitmap);
            }else{
                if(wordCount >= words.size()){
                    wordCount = words.size()-1;
                }
                if(hideNativeWord){
                    textViewCard.setText(words.get(wordCount).getForeignWord());
                    textViewAns.setText(words.get(wordCount).getNativeWord());
                }else{
                    textViewCard.setText(words.get(wordCount).getNativeWord());
                    textViewAns.setText(words.get(wordCount).getForeignWord());
                }

                byte[] image = words.get(wordCount).getImage();
                Bitmap bitmap = BitmapFactory.decodeByteArray(image,0,image.length);
                imageViewCard.setImageBitmap(bitmap);
            }

            wordCount++;
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this,"Something got wrong while loading words",Toast.LENGTH_SHORT).show();
        }
    }

    public void onProfileClick(View view){
        Intent profileIntent = new Intent(MainActivity.this,ProfileActivity.class);
        startActivity(profileIntent);
        finish();
    }

    public void onEditClick(View view){
        if(words.size() == 0){
            return;
        }
        showBottomSheet(true);
    }

    public void onDeleteClick(View view){
        if(words.size() == 0){
            return;
        }

        if(hideImage){
            if(isBackVisible){
                startAnimationDeleteCardGoReverse();
                deleteCard.setClickable(false);
                editCard.setClickable(false);
            }


            isBackVisible = false;

            setLeftIn.cancel();
            setRightOut.cancel();
            cardFrontLayout.setAlpha(1.0f);
            cardFrontLayout.setRotationY(0f);
            cardBackLayout.setAlpha(0.0f);
            textViewAns.setVisibility(View.INVISIBLE);
        }


        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(getApplicationContext());
        databaseHelper.deleteWord(words.get(wordCount-1), new WordStuffListener() {
            @Override
            public void wordAdded() {

            }

            @Override
            public void wordEdited() {

            }

            @Override
            public void wordDeleted() {
                Toast.makeText(MainActivity.this,"Word deleted",Toast.LENGTH_SHORT).show();
                loadList(currentType,wordCount-1);
            }
        });
    }

    public void onButtonListSelect(View view){
        if(!interstitialShowed){
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
                interstitialShowed = true;
            } else {
                Log.d("TAG", "The interstitial wasn't loaded yet.");
            }
        }
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(getApplicationContext());
        if(currentType.equals(databaseHelper.UNKNOWNWORDS)){
            currentType = databaseHelper.NOTDECIDEDWORDS;
            view.setBackgroundResource(R.drawable.emindegilim_selector);
        }else if(currentType.equals(databaseHelper.NOTDECIDEDWORDS)){
            currentType = databaseHelper.KNOWNWORDS;
            view.setBackgroundResource(R.drawable.biliyorum_selector);
        }else if(currentType.equals(databaseHelper.KNOWNWORDS)){
            currentType = databaseHelper.UNKNOWNWORDS;
            view.setBackgroundResource(R.drawable.bilmiyorum_selector);
        }else{
            currentType = databaseHelper.UNKNOWNWORDS;
            view.setBackgroundResource(R.drawable.bilmiyorum_selector);
        }
        loadList(currentType,0);
        if(isBackVisible){
            startAnimationDeleteCardGoReverse();
            deleteCard.setClickable(false);
            editCard.setClickable(false);
        }


        isBackVisible = false;

        setLeftIn.cancel();
        setRightOut.cancel();
        cardFrontLayout.setAlpha(1.0f);
        cardFrontLayout.setRotationY(0f);
        cardBackLayout.setAlpha(0.0f);
        textViewAns.setVisibility(View.INVISIBLE);



    }

    public void startAnimationGo(){


        if(hideImage){
            if(isBackVisible){
                startAnimationDeleteCardGoReverse();
                deleteCard.setClickable(false);
                editCard.setClickable(false);
            }
            setRightOut.cancel();
            setLeftIn.cancel();
            card.setClickable(false);
        }

        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.card_move_go_anim);
        card.startAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Log.e(TAG, "AdsCount = "+adsCount + ", adsLoaded= "+adsLoaded);
                if(adsCount >= 4 && adsLoaded){
                    if(isBackVisible){
                        startAnimationDeleteCardGoReverse();
                        deleteCard.setClickable(false);
                        editCard.setClickable(false);
                    }
                    setRightOut.cancel();
                    setLeftIn.cancel();
                    card.setClickable(false);
                    isBackVisible = false;

                    setLeftIn.cancel();
                    setRightOut.cancel();
                    cardFrontLayout.setAlpha(1.0f);
                    cardFrontLayout.setRotationY(0f);
                    cardBackLayout.setAlpha(0.0f);
                    textViewAns.setVisibility(View.INVISIBLE);
                    mAdView.setVisibility(View.VISIBLE);
                    adsCount = 0;
                    Log.e(TAG, "Reklam gösteriliyor");
                }else{
                    card.setClickable(true);
                    mAdView.setVisibility(View.GONE);
                    if(wordCount >= words.size()){
                        loadList(currentType,0);
                    }else{
                        if(words.size() == 0){
                            textViewCard.setText("Please Add Word");
                            textViewAns.setText("");
                            Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.no_image);
                            imageViewCard.setImageBitmap(bitmap);
                        }else{
                            if(hideNativeWord){
                                textViewCard.setText(words.get(wordCount).getForeignWord());
                                textViewAns.setText(words.get(wordCount).getNativeWord());
                            }else{
                                textViewCard.setText(words.get(wordCount).getNativeWord());
                                textViewAns.setText(words.get(wordCount).getForeignWord());
                            }
                            byte[] image = words.get(wordCount).getImage();
                            Bitmap bitmap = BitmapFactory.decodeByteArray(image,0,image.length);
                            imageViewCard.setImageBitmap(bitmap);

                        }
                        wordCount++;
                    }
                    adsCount++;
                }
                startAnimationCome();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void startAnimationCome(){
        if(hideImage){
            if(isBackVisible){
                textViewAns.setVisibility(View.INVISIBLE);
                cardBackLayout.setAlpha(0.0f);
                cardFrontLayout.setAlpha(1.0f);
                cardFrontLayout.setRotationY(0f);
            }
            isBackVisible = false;
        }

        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.card_move_come_anim);
        card.startAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {


            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //card.setClickable(true);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void startAnimationDeleteCardGo(){
        deleteCard.setVisibility(View.VISIBLE);
        editCard.setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(MainActivity.this,R.anim.card_del_move_go_anim);
        deleteCard.startAnimation(animation);
        Animation animation1 = AnimationUtils.loadAnimation(MainActivity.this,R.anim.card_edit_move_go_anim);
        editCard.startAnimation(animation1);
    }

    public void startAnimationDeleteCardGoReverse(){
        Animation animation = AnimationUtils.loadAnimation(MainActivity.this,R.anim.card_del_move_go_reverse_anim);
        deleteCard.startAnimation(animation);
        Animation animation1 = AnimationUtils.loadAnimation(MainActivity.this, R.anim.card_edit_move_go_reverse_anim);
        editCard.startAnimation(animation1);
        animation1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                deleteCard.setClickable(true);
                editCard.setClickable(true);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void clickKnown(View view){
        startAnimationGo();
        if(words.size() != 0 && mAdView.getVisibility() == View.GONE){
            DatabaseHelper databaseHelper = DatabaseHelper.getInstance(getApplicationContext());
            databaseHelper.moveWord(words.get(wordCount-1),currentType,databaseHelper.KNOWNWORDS);
        }
    }

    public void clickUnknown(View view){
        startAnimationGo();
        if(words.size() != 0 && mAdView.getVisibility() == View.GONE){
            DatabaseHelper databaseHelper = DatabaseHelper.getInstance(getApplicationContext());
            databaseHelper.moveWord(words.get(wordCount-1),currentType,databaseHelper.UNKNOWNWORDS);
        }
    }

    public void clickUndecided(View view){
        startAnimationGo();
        if(words.size() != 0 && mAdView.getVisibility() == View.GONE){
            DatabaseHelper databaseHelper = DatabaseHelper.getInstance(getApplicationContext());
            databaseHelper.moveWord(words.get(wordCount-1),currentType,databaseHelper.NOTDECIDEDWORDS);
        }
    }

    public void cardFlip(View view){
        card.setClickable(false);
        if(!isBackVisible){
            setRightOut.setTarget(cardFrontLayout);
            setLeftIn.setTarget(cardBackLayout);
            setRightOut.start();
            setLeftIn.start();

            textViewAns.setVisibility(View.VISIBLE);
            textViewAns.startAnimation(animation);
        }else{
            setRightOut.setTarget(cardBackLayout);
            setLeftIn.setTarget(cardFrontLayout);
            setRightOut.start();
            setLeftIn.start();
            textViewAns.setVisibility(View.INVISIBLE);
            textViewAns.startAnimation(animationBack);
        }
    }

    public void addWord(View view){
        showBottomSheet(false);
    }

    public void showBottomSheet(final boolean isEdit){
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(MainActivity.this,R.style.BottomSheetDialogTheme);
        bottomSheetView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.bottom_sheet, (RelativeLayout) findViewById(R.id.bottomSheetContainer));
        TextView tabTextView = bottomSheetView.findViewById(R.id.textView3);
        Button addOrEditButton = bottomSheetView.findViewById(R.id.button2);
        final EditText nativeWordET = bottomSheetView.findViewById(R.id.editText2);
        final EditText foreignWordET = bottomSheetView.findViewById(R.id.editText);
        final ImageView imageView = bottomSheetView.findViewById(R.id.imageView2);
        if(isEdit){
            tabTextView.setText("Edit Word");
            addOrEditButton.setText("Edit");
            nativeWordET.setText(words.get(wordCount-1).getNativeWord());
            foreignWordET.setText(words.get(wordCount-1).getForeignWord());
            byte[] image = words.get(wordCount-1).getImage();
            Bitmap bitmap = BitmapFactory.decodeByteArray(image,0,image.length);
            imageView.setImageBitmap(bitmap);
        }
        addOrEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DatabaseHelper databaseHelper = DatabaseHelper.getInstance(getApplicationContext());
                if(foreignWordET.getText() ==  null || foreignWordET.getText().toString().trim().length() == 0){
                    Toast.makeText(MainActivity.this,"Please write foreign word",Toast.LENGTH_SHORT).show();
                }else if(nativeWordET.getText() ==  null || nativeWordET.getText().toString().trim().length() == 0){
                    Toast.makeText(MainActivity.this,"Please write native word",Toast.LENGTH_SHORT).show();
                }else{
                    final CardView loadingCard = bottomSheetView.findViewById(R.id.loadingCard);
                    loadingCard.setVisibility(View.VISIBLE);
                    try {
                        if(isEdit){
                            databaseHelper.editWord(words.get(wordCount - 1), nativeWordET.getText().toString(), foreignWordET.getText().toString(), chosenUrl, new WordStuffListener() {
                                @Override
                                public void wordAdded() {

                                }

                                @Override
                                public void wordEdited() {
                                    loadList(currentType,wordCount-1);
                                    Toast.makeText(MainActivity.this,"Word edited",Toast.LENGTH_SHORT).show();
                                    loadingCard.setVisibility(View.GONE);
                                    chosenUrl = null;
                                    bottomSheetDialog.dismiss();
                                }

                                @Override
                                public void wordDeleted() {

                                }
                            });

                        }else{
                            databaseHelper.addWord(nativeWordET.getText().toString(),foreignWordET.getText().toString(),chosenUrl, new WordStuffListener() {
                                @Override
                                public void wordAdded() {
                                    if(currentType.equals(databaseHelper.UNKNOWNWORDS)){
                                        loadList(currentType,0);
                                    }
                                    for(int i=0; i<words.size(); i++){
                                        Log.e(TAG,words.get(i).getForeignWord());
                                    }
                                    Toast.makeText(MainActivity.this,"Word added into unknown words",Toast.LENGTH_SHORT).show();
                                    loadingCard.setVisibility(View.GONE);
                                    chosenUrl = null;
                                    bottomSheetDialog.dismiss();
                                }

                                @Override
                                public void wordEdited() {

                                }

                                @Override
                                public void wordDeleted() {

                                }
                            });

                        }


                    } catch (Exception e) {
                        if(isEdit){
                            Toast.makeText(MainActivity.this,"Something got wrong while editing word",Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(MainActivity.this,"Something got wrong while adding word",Toast.LENGTH_SHORT).show();
                        }
                        loadingCard.setVisibility(View.GONE);
                        e.printStackTrace();
                    }



                }


            }
        });
        bottomSheetView.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSearchImageSheet();
            }
        });
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    public void openSearchImageSheet(){
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(MainActivity.this,R.style.BottomSheetDialogTheme);
        final View searchImageSheetView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.bottom_sheet_image_search, (LinearLayout)findViewById(R.id.bottomSheetImageSearchContainer));
        final EditText editText = searchImageSheetView.findViewById(R.id.editText3);
        searchImageSheetView.findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String query = editText.getText().toString();
                searchImage(query,searchImageSheetView,bottomSheetDialog);
                hideKeyboard(searchImageSheetView);
            }
        });

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                boolean handled = false;
                if(i == EditorInfo.IME_ACTION_SEARCH){
                    String query = editText.getText().toString();
                    searchImage(query,searchImageSheetView,bottomSheetDialog);
                    hideKeyboard(searchImageSheetView);
                    handled = true;
                }
                return handled;
            }
        });

        bottomSheetDialog.setContentView(searchImageSheetView);
        bottomSheetDialog.show();
    }

    public void searchImage(String query, final View searchImageSheetView, final BottomSheetDialog dialog){

        if(!ConnectionHelper.isConnectedInternet(getApplicationContext())){
            Toast.makeText(MainActivity.this,"Please connect to Internet",Toast.LENGTH_SHORT).show();
            return;
        }

        final RecyclerView recyclerView = searchImageSheetView.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(MainActivity.this,2);
        recyclerView.setLayoutManager(layoutManager);
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        final ProgressBar progressBar = searchImageSheetView.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        String url = "https://pixabay.com/api/?key=15169282-619f171e0e1dfdf060f5a3c17&q="+query+"&image_type=photo&pretty=true&per_page=20";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        progressBar.setVisibility(View.INVISIBLE);
                        JSONParser jsonParser = new JSONParser(response);
                        try {
                            ArrayList<String> results = jsonParser.getLinks();
                            ImageSearchListAdapter adapter = new ImageSearchListAdapter(results, new ItemClickListener() {
                                @Override
                                public void onItemClick(String url) {
                                    dialog.dismiss();
                                    ProgressBar progressBar1 = null;
                                    ImageView addWordImageView = null;
                                    if(bottomSheetView != null){
                                        progressBar1 = bottomSheetView.findViewById(R.id.progressBar2);
                                        addWordImageView = bottomSheetView.findViewById(R.id.imageView2);
                                    }

                                    final ProgressBar finalProgressBar = progressBar1;
                                    if(addWordImageView != null){
                                        if(finalProgressBar != null){
                                            finalProgressBar.setVisibility(View.VISIBLE);
                                        }
                                        Picasso.get().load(url).fit().into(addWordImageView, new com.squareup.picasso.Callback() {
                                            @Override
                                            public void onSuccess() {
                                                if(finalProgressBar != null){
                                                    finalProgressBar.setVisibility(View.GONE);
                                                }
                                            }

                                            @Override
                                            public void onError(Exception e) {
                                                if(finalProgressBar != null){
                                                    finalProgressBar.setVisibility(View.GONE);
                                                }
                                            }
                                        });
                                    }

                                    chosenUrl = url;

                                }
                            });
                            recyclerView.setAdapter(adapter);
                        } catch (JSONException e) {
                            Log.e(TAG,"Json parse edilirken sorun oluştu");
                            e.printStackTrace();
                        }
                    }

                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this,"Something got wrong while loading images",Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
        queue.add(jsonObjectRequest);
    }


    public void hideKeyboard(View view){
        InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (manager != null) {
            manager.hideSoftInputFromWindow(view.getWindowToken(),0);
        }
        view.clearFocus();
    }
}
