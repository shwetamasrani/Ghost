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

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

public class SimpleDictionary implements GhostDictionary {
    public ArrayList<String> words;
    Random r = new Random();

    public SimpleDictionary(InputStream wordListStream) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(wordListStream));
        words = new ArrayList<>();
        String line = null;
        while((line = in.readLine()) != null) {
            String word = line.trim();
            if (word.length() >= MIN_WORD_LENGTH)
              words.add(line.trim());
        }
    }

    @Override
    public boolean isWord(String word) {
        return words.contains(word);
    }

    @Override
    public String getAnyWordStartingWith(String prefix) {
        String currentWord = null;

        if(prefix == null)
        {
            System.out.println("gayu");
            return(words.get(r.nextInt(words.size())));
        }

        int mid = 0;
        int lower = 0;
        int upper = words.size()-1;
        int flag=0;
        String check = null;
        while(lower <= upper){
            //Log.i("LOG", "l = " + lower + "  u= " + upper +  " mid = " + mid + " length : " + words.get(mid).length());
            mid = (lower+upper)/2;
            //Log.i("LOG", "l = " + lower + "  u= " + upper +  " mid = " + mid + " length : " + words.get(mid).length());
            check = words.get(mid);
            flag = (check.startsWith(prefix))? 0 : prefix.compareToIgnoreCase(check);

            if(flag<0){
                upper = mid - 1;
            }
            else if(flag>0){
                lower = mid + 1;
            }
            else{
                break;
            }
        }

        if (flag==0){
            currentWord = check;
        }

        return currentWord;
    }

    @Override
    public String getGoodWordStartingWith(String prefix, boolean comp_played) {
        String selected = getAnyWordStartingWith(prefix);
        String check = null;
        if(selected!=null){
            int index = words.indexOf(selected);
            int i = index-1;
            int j = index+1;
            ArrayList<String> even = new ArrayList<>();
            ArrayList<String> odd = new ArrayList<>();
            //going up
            while(true){
                if(i < 0){
                    break;
                }
                check = words.get(i);
                if(check.startsWith(prefix)){
                    if(check.length()%2==0){
                        even.add(check);
                    }
                    else {
                        odd.add(check);
                    }
                    i--;
                }
                else{
                    break;
                }
            }
            //going down
            while(true){
                if (j==words.size()){
                    break;
                }
                check = words.get(j);
                if(check.startsWith(prefix)){
                    if(check.length()%2==0){
                        even.add(check);
                    }
                    else{
                        odd.add(check);
                    }
                    j++;
                }
                else {
                    break;
                }
            }


            if(comp_played){
                if(even.size()==0){
                    if(odd.size()==0){
                        selected = null;
                    }
                    else
                    {
                        selected = odd.get(r.nextInt(odd.size()));
                    }
                }
                else{
                    selected = even.get(r.nextInt(even.size()));
                }
            }
            else {
                if(odd.size()==0){
                    if(even.size()==0){
                        selected = null;
                    }
                    else
                    {
                        selected = even.get(r.nextInt(odd.size()));
                    }
                }
                else{
                    selected = odd.get(r.nextInt(odd.size()));
                }
            }

        }
        return selected;
    }
}
