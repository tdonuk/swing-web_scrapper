import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SourceParser {
    private File sourcesFile,preferredSourcesFile;
    private ArrayList<String> lines;
    private ArrayList<Website> sourceList;
    private Website site;
    private String wholeText;
    private String currentDir;
    private Image img;
    private Icon icon;

    public SourceParser() {
        currentDir = System.getProperty("user.dir");
        sourcesFile = new File(currentDir+"\\resources\\data\\sources.data");
        preferredSourcesFile = new File(currentDir+"\\resources\\data\\preferred_sources.data");


        lines = new ArrayList<>();
    }

    public ArrayList<Website> getSources() {
        return parseSources(sourcesFile);
    }

    public ArrayList<Website> parseSources(File file) {
        try(FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr)){

            while(br.ready()) {
                lines.add(br.readLine());
            }

            wholeText = "";
            for(String s : lines) {
                wholeText = wholeText + s + "\n";
            }

            sourceList = new ArrayList<>();

            //divide whole data to data groups
            String [] dividedData = wholeText.split("</source>"); //each string will include data for a different website
            String data;
            for(String s : dividedData) {
                if(s.equals("\n") || s.equals("")) continue;

                s = s.replaceAll("<source>", ""); //Not really necessary

                site = new Website();

                data = s.substring(s.indexOf("name: "), s.indexOf("\nmain-url: ")); //name of the website
                data = data.substring(data.indexOf(" ")+1);
                site.setName(data);

                data = s.substring(s.indexOf("main-url: "),s.indexOf("\nheaders-url: ")); //url to the main page of the website
                data = data.substring(data.indexOf(" ")+1);
                site.setMainUrl(data);

                data = s.substring(s.indexOf("headers-url: "),s.indexOf("\nmain-list-address: "));  //url to the endpoint where we get headers
                data = data.substring(data.indexOf(" ")+1);
                site.setHeadersUrl(data);

                data = s.substring(s.indexOf("main-list-address: "),s.indexOf("\nheader-address: ")); //address of the header list
                data = data.substring(data.indexOf(" ")+1);
                site.setMainListAddress(data);

                data = s.substring(s.indexOf("header-address: "),s.indexOf("\nheader-link-address: ")); //address of the each header in the header list
                data = data.substring(data.indexOf(" ")+1);
                site.setHeaderAddress(data);

                data = s.substring(s.indexOf("header-link-address: "),s.indexOf("\ncontent-header-address: ")); //address for the link of the header
                data = data.substring(data.indexOf(" ")+1);
                site.setHeaderLinkAddress(data);

                data = s.substring(s.indexOf("content-header-address: ") , s.indexOf("\ncontent-details-address: ")); //content subtitle address (if exists)
                data = data.substring(data.indexOf(" ")+1);
                site.setContentHeaderAddress(data);

                data = s.substring(s.indexOf("content-details-address: "),s.indexOf("\nicon: ")); //content detail address
                data = data.substring(data.indexOf(" ")+1);
                site.setContentDetailsAddress(data);

                data = s.substring(s.indexOf("icon: "),s.length()-1); //icon file name
                data = data.substring(data.indexOf(" ")+1);
                site.setImageFileName(data);

                img = ImageIO.read(new File(currentDir + "\\resources\\" + data)); //data = file name of the icon
                site.setImageFile(img); //icon for the website

                sourceList.add(site);
            }
        } catch(IOException e) {
            showFileError(file.getName());
        }

        Collections.sort(sourceList, Comparator.comparing(Website::getName));

        return sourceList;
    }

    public boolean isFirstTime() {
        if(preferredSourcesFile.canRead()) return false;
        else {
            return true;
        }
    }

    public void setPreferredSources(ArrayList<Website> list) {
        if(list.isEmpty()) return;

        if(!preferredSourcesFile.canWrite()) {
            try {
                preferredSourcesFile.createNewFile();
            } catch (IOException e) {

            }
        }

        try(FileWriter fw = new FileWriter(preferredSourcesFile,false)) {
            StringBuffer sb = new StringBuffer();
            for(Website w : list) {
                sb.append("<source>\n");
                sb.append("name: "+w.getName()+"\n");
                sb.append("main-url: "+w.getMainUrl()+"\n");
                sb.append("headers-url: "+w.getHeadersUrl()+"\n");
                sb.append("main-list-address: "+w.getMainListAddress()+"\n");
                sb.append("header-address: "+w.getHeaderAddress()+"\n");
                sb.append("header-link-address: "+w.getHeaderLinkAddress()+"\n");
                sb.append("content-header-address: "+w.getContentHeaderAddress()+"\n");
                sb.append("content-details-address: "+w.getContentDetailsAddress()+"\n");
                sb.append("icon: "+w.getImageFileName()+"\n");
                sb.append("</source>\n");
            }
            fw.write(sb.toString());
        } catch (IOException e) {
            showFileError(preferredSourcesFile.getName());
        }
    }

    public ArrayList<Website> getPreferredSources() {
        return parseSources(preferredSourcesFile);
    }

    private void showFileError(String fileName) {
        String errorMessage = fileName + " not found. Please be sure the program executable and resources folder are in the same folder";
        JOptionPane.showMessageDialog(null,errorMessage,"File Error",JOptionPane.ERROR_MESSAGE);
    }
}
