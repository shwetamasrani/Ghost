/* Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.engedu.ghost;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;


public class GhostActivity extends AppCompatActivity {
    private static final String COMPUTER_TURN = "Computer's turn";
    private static final String USER_TURN = "Your turn";
    private SimpleDictionary dictionary;
    private boolean userTurn = false;
    private Random random = new Random();
    private boolean computer_played_first = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ghost);
        AssetManager assetManager = getAssets();

        try {
            dictionary = new SimpleDictionary(assetManager.open("words.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            onStart(null);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ghost, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Handler for the "Reset" button.
     * Randomly determines whether the game starts with a user turn or a computer turn.
     * @param view
     * @return true
     */
    public boolean onStart(View view) throws InterruptedException {
        userTurn = random.nextBoolean();
        computer_played_first = !userTurn;
        Button challenge = (Button)findViewById(R.id.challenge_btn);
        challenge.setEnabled(true);
        TextView text = (TextView) findViewById(R.id.ghostText);
        text.setText("");
        TextView label = (TextView) findViewById(R.id.gameStatus);
        if (userTurn) {
            label.setText(USER_TURN);
        } else {
            label.setText(COMPUTER_TURN);
            computerTurn();
        }
        return true;
    }

    private void computerTurn() throws InterruptedException {
        //Thread.sleep(2000);
        TextView status = (TextView) findViewById(R.id.gameStatus);
        TextView ghostText = (TextView) findViewById(R.id.ghostText);
        Button challenge = (Button)findViewById(R.id.challenge_btn);
        String CurrentWord = (String)ghostText.getText();
        String dictionaryWord = null;
        // Do computer turn stuff then make it the user's turn again


        dictionaryWord = dictionary.getGoodWordStartingWith(CurrentWord, computer_played_first);

        if(dictionaryWord != null)
        {
            //If the same word as in CurrentWord comes to dictionaryWord then the program crashes giving IndexOut... exception!
            if(dictionaryWord.equals(CurrentWord))
            {
                int i = dictionary.words.indexOf(dictionaryWord);
                if(dictionary.words.get(i+1).startsWith(CurrentWord))
                {
                    CurrentWord = dictionary.words.get(i+1);
                    CurrentWord = dictionaryWord.substring(0,CurrentWord.length()+1);
                    ghostText.setText(CurrentWord);
                }
                else
                {
                    status.setText("Computer Wins!");
                    Toast.makeText(getApplicationContext(), "Computer Challenged!!", Toast.LENGTH_SHORT).show();
                    challenge.setEnabled(false);
                    return;
                }
            }
            else {
                CurrentWord = dictionaryWord.substring(0,CurrentWord.length()+1);
                ghostText.setText(CurrentWord);
            }
        }
        else
        {
            Toast.makeText(getApplicationContext(), "Computer Challenged You!", Toast.LENGTH_SHORT).show();
            status.setText("Computer Wins!!");
            challenge.setEnabled(false);
            return;
        }


        //
        userTurn = true;
        status.setText(USER_TURN);
    }

    public void onChallenge(View view)
    {
        TextView ghostText = (TextView) findViewById(R.id.ghostText);
        TextView status = (TextView) findViewById(R.id.gameStatus);
        Button challenge = (Button)findViewById(R.id.challenge_btn);
        String word = (String)ghostText.getText();

        String dictionaryWord = dictionary.getGoodWordStartingWith(word, computer_played_first);

        if(word.length() >= 4)
        {
            if(dictionaryWord == null)
            {
                status.setText("User Wins!!");
                Toast.makeText(getApplicationContext(), "Correctly Challenged!", Toast.LENGTH_SHORT).show();
                challenge.setEnabled(false);
            }
            else
            {
                if(dictionaryWord.equals(word))
                {
                    int i = dictionary.words.indexOf(dictionaryWord);
                    if(dictionary.words.get(i+1).startsWith(word))
                    {
                        word = dictionary.words.get(i+1);
                        Toast.makeText(getApplicationContext(), "Challenge Failed! \n Possible Word: " + word, Toast.LENGTH_SHORT).show();
                        status.setText("Computer Wins!!");
                        challenge.setEnabled(false);
                    }
                    else
                    {
                        status.setText("User Wins!!");
                        Toast.makeText(getApplicationContext(), "Correctly Challenged!", Toast.LENGTH_SHORT).show();
                        challenge.setEnabled(false);
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Challenge Failed! \n Possible Word: " + dictionaryWord, Toast.LENGTH_SHORT).show();
                    status.setText("Computer Wins!!");
                    challenge.setEnabled(false);
                }
            }
        }
        else
        {
            Toast.makeText(getApplicationContext(), "Not yet!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {

        TextView ghostText = (TextView) findViewById(R.id.ghostText);
        TextView status = (TextView) findViewById(R.id.gameStatus);

        if(keyCode >= 29 && keyCode <= 54)
        {
            String temp = (String) ghostText.getText();
            temp += (char)event.getUnicodeChar();
            ghostText.setText(temp);
            userTurn = false;
            status.setText(COMPUTER_TURN);
            try {
                computerTurn();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        else
        {
            Toast.makeText(getApplicationContext(), "Alphabets Only!", Toast.LENGTH_SHORT).show();
        }



        return super.onKeyUp(keyCode, event);
    }
}
