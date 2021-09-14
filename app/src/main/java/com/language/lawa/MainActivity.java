package com.language.lawa;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    AutoCompleteTextView autoCompleteTextView;
    DbHelp dbHelp;
    ArrayList<String> newList;
    private   TextView textView,tvspellthai;
    private static TextToSpeech textToSpeech;
    private static  String spell=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        autoCompleteTextView = findViewById(R.id.auto_txt);
        textView = findViewById(R.id.definition);
        tvspellthai = findViewById(R.id.spellTH);
        textView.setBackgroundColor(Color.LTGRAY);
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status !=TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.UK);
                    //textToSpeech.setSpeechRate((float) 0.01);
                }
            }
        });
            dbHelp = new DbHelp(this,"lawa_dictionary.db",1);
        try{
            dbHelp.CheckDb();
            dbHelp.OpenDatabase();
        }catch (Exception e){}
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
                            new ArrayAdapter<String>(MainActivity.this,
                                   android.R.layout.simple_list_item_1,newList));

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String word = parent.getItemAtPosition(position).toString();
                //Toast.makeText(this,word,Toast.LENGTH_SHORT).show();
                getDefinition(word);
                getSpell(word);
                getSpellTH(word);
            }
        });
    }
    public void getDefinition(String word){
        String definition = dbHelp.GetDefinition(word);
        textView.setText(definition);
    }
    public void getSpell(String word){
        String spellText = dbHelp.GetSpell(word);
        spell = spellText;
        //tvspell.setText(spell);
        //Toast.makeText(this,spell,Toast.LENGTH_SHORT).show();

    }
    public void getSpellTH(String word){
        String spellTH = dbHelp.GetSpellTH(word);
        tvspellthai.setText( spellTH);
    }
    //@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void TextToSpeechButton(View view){
        //String text = tvspell.getText().toString();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            textToSpeech.speak(spell,TextToSpeech.QUEUE_FLUSH,null,null);
        }
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