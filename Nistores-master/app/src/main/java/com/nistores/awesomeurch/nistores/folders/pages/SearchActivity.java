package com.nistores.awesomeurch.nistores.folders.pages;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.nistores.awesomeurch.nistores.R;

public class SearchActivity extends AppCompatActivity {
    Intent intent;
    Spinner searchSpinner;
    AppCompatButton buttonSearch;
    EditText inputField;
    private String searchType = "all";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);


        inputField = findViewById(R.id.input_search);
        buttonSearch = findViewById(R.id.btn_search);
        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSearchActivity();
            }
        });

        searchSpinner = findViewById(R.id.search_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.search_options, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        searchSpinner.setAdapter(adapter);

        searchSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String srch = adapterView.getItemAtPosition(i).toString();
                searchType = returnNormalString(srch);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public String returnNormalString(String s){
        String actual = "all";
        switch (s){
            case "All":
                actual = "all";
                break;
            case "Products":
                actual = "product";
                break;
            case "Stores":
                actual = "store";
                break;
            case "Members":
                actual = "member";
                break;
            case "Topics":
                actual = "topic";
                break;
        }

        return actual;
    }

    public void openSearchActivity(){
        String searchWord = inputField.getText().toString();
        if(!searchWord.isEmpty()){
            Bundle bundle = new Bundle();
            bundle.putString ("searchType", searchType);
            bundle.putString ("searchWord", searchWord);
            intent = new Intent(this,searchResultActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }else{
            inputField.setError("Please what are you searching for?");
        }

    }

}
