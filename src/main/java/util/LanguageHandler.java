package util;

import util.dto.Label;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class LanguageHandler {

    public final HashMap<Label,String> texts;

    private File langFile;

    public LanguageHandler() {
        texts = new HashMap<>();
    }

    public void setLangFile(File langFile) {
        this.langFile = langFile;
    }

    public void setStrings() {
        String langText = "";
        try {
            langText = Files.readString(langFile.toPath());
        } catch (IOException e) {
            // show the error
            System.out.println(e.getMessage());
        }

        List<String> lines = Arrays.asList(langText.split("\\r\\n|\\n"));

        int code;
        for(Label t : Label.values()) {
            for(String s : lines) {
                if(s.startsWith(t.getCode()+": ")) {
                    texts.put(t,s.substring(s.indexOf(" ")+1));
                    break;
                }
            }
        }
    }

    public HashMap<Label,String> getTexts(){
        if(texts.isEmpty()) System.exit(1);

        return this.texts;
    }

    public static File prefLangFile = new File("lang.txt");

    public void setPreferredLang() {
        String prefLang = "";
        try {
            if(langFile.getName().equals("tr.lang")) prefLang = "tr";
            if(langFile.getName().equals("en.lang")) prefLang = "en";

            Files.writeString(prefLangFile.toPath(),prefLang);
        } catch (IOException e) {
            //Show the error
        }
    }


    public String getPreferredLang() {
        String s = "";
        try {
            s = Files.readString(prefLangFile.toPath());
        } catch (IOException e) {
            //Show the error
        }
        return s;
    }
}
