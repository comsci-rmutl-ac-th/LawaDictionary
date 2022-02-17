package com.language.lawa;

import androidx.appcompat.app.AppCompatActivity;


import android.graphics.Color;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;


import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    AutoCompleteTextView autoCompleteTextView;
    DbHelp dbHelp;
    private static  final String dbName = "lawa_dictionary.db";
    ArrayList<String> newList;
    private   TextView textView,tvspellthai;
    private static TextToSpeech textToSpeech;
    private static String spellTH=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        autoCompleteTextView = findViewById(R.id.auto_txt);
        textView = findViewById(R.id.definition);
        tvspellthai = findViewById(R.id.spellTH);
        textView.setBackgroundColor(Color.LTGRAY);
        textToSpeech = new TextToSpeech(getApplicationContext(), status -> {
            if(status !=TextToSpeech.ERROR) {
                //textToSpeech.setLanguage(Locale.UK);
                textToSpeech.setLanguage(new Locale("th"));
                textToSpeech.setSpeechRate((float) 0.001);
            }
        });
            dbHelp = new DbHelp(this,dbName,1);
        try{
            dbHelp.CheckDb();
            dbHelp.OpenDatabase();
        }catch (Exception ignored){}
        //สร้างรายการจากตารางข้อมูลดิกชันนารีเพื่อแสดงผลเมือทำการเลือก
        newList = new ArrayList<>();
        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() == 1){
                    newList.addAll(dbHelp.getWord(s.toString()));
                    autoCompleteTextView.setAdapter(
                            new ArrayAdapter<>(MainActivity.this,
                                    android.R.layout.simple_list_item_1, newList));

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        autoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
            String word = parent.getItemAtPosition(position).toString();
            //Toast.makeText(this,word,Toast.LENGTH_SHORT).show();
            getDefinition(word);
            getSpell();
            getSpellTH(word);

        });

    }

    public void getDefinition(String word){
        String definition = dbHelp.GetDefinition(word);
        textView.setText(definition);
    }
    public void getSpell(){

    }
    public void getSpellTH(String word){
        spellTH = dbHelp.GetSpellTH(word);
        tvspellthai.setText( spellTH);
    }
    //@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void TextToSpeechButton(View view){
        //String text = tvspell.getText().toString();

        textToSpeech.speak(spellTH,TextToSpeech.QUEUE_FLUSH,null,null);
        // Toast.makeText(this,text,Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        if(autoCompleteTextView.getText()==null){
            textView.setText("");
            tvspellthai.setText("");
        }
        if(textToSpeech !=null){
            textToSpeech.stop();
        }
        super.onPause();

    }
}