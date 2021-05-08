import javax.swing.*;
import java.io.*;
import java.util.ArrayList;

public class SourceParser {
    private File file;
    private FileReader fr;
    private BufferedReader br;
    private ArrayList<String> lines;
    private ArrayList<Website> sourceList;
    private Website site;
    private String wholeText;
    private String currentDir;

    public SourceParser() {
        currentDir = System.getProperty("user.dir");
        file = new File(currentDir+"\\resources\\data\\sources.txt");
        try {
            fr = new FileReader(file);
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(null,"sources.txt not found. Please be sure the program and resources folder is in the same folder","File error",JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        br = new BufferedReader(fr);

        lines = new ArrayList<>();
        sourceList = new ArrayList<>();
    }

    public ArrayList<Website> getSources() throws IOException {
        //TODO

        while(br.ready()) {
            lines.add(br.readLine());
        }

        wholeText = "";
        for(String s : lines) {
            wholeText = wholeText + s + "\n";
        }

        //divide whole data to data groups
        String [] dividedData = wholeText.split("</source>"); //each string will include data for a different website
        String [] temp; //this will be used in the loop for parsing process
        for(String s : dividedData) {
            if(s.equals("\n") || s.equals("")) continue;

            s = s.replaceAll("<source>", ""); //Not really necessary

            temp = s.split("\n");

            site = new Website();

            //there is a whitespace after ':' so we get substring from index of ':' + 2
            site.setName(temp[2].substring(temp[2].indexOf(":")+2));
            site.setMainUrl(temp[3].substring(temp[3].indexOf(":")+2));
            site.setHeadersUrl(temp[4].substring(temp[4].indexOf(":")+2));
            site.setMainListAddress(temp[5].substring(temp[5].indexOf(":")+2));
            site.setHeaderAddress(temp[6].substring(temp[6].indexOf(":")+2));
            site.setHeaderLinkAddress(temp[7].substring(temp[7].indexOf(":")+2));
            site.setContentHeaderAddress(temp[8].substring(temp[8].indexOf(":")+2));
            site.setContentDetailsAddress(temp[9].substring(temp[9].indexOf(":")+2));

            sourceList.add(site);
        }


        return sourceList;
    }
}
