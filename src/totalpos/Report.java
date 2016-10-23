package totalpos;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Saúl Hidalgo
 */
public class Report {
    private File file;
    private String titleName;
    private String title;
    private String columns;
    private String parameters;
    private boolean showNumbers;
    private String query;

    public Report(File file, String titleName) {
        this.file = file;
        this.titleName = titleName;
    }

    public Report(File file, String titleName, String title, String columns, String parameters, boolean showNumbers, String query) {
        this.file = file;
        this.titleName = titleName;
        this.title = title;
        this.columns = columns;
        this.parameters = parameters;
        this.showNumbers = showNumbers;
        this.query = query;
    }

    public Report(File file){
        this.file = file;
        try {
            DataInputStream in = new DataInputStream(new FileInputStream(file));
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            int curLine = 1;
            while ( (line = br.readLine()) != null ){
                String[] tokens = line.split("==");
                if ( tokens.length != 2 ){
                    System.err.println("Err parsing line " + curLine);
                    continue;
                }
                if ( tokens[0].equals("Title") ){
                    title = tokens[1];
                }else if ( tokens[0].equals("Columns") ){
                    columns = tokens[1];
                }else if ( tokens[0].equals("ShowNumbers") ){
                    showNumbers = tokens[1].equals("True");
                }else if ( tokens[0].equals("Parameters") ){
                    parameters = tokens[1];
                }else if ( tokens[0].equals("SQL") ){
                    query = tokens[1];
                }else if ( tokens[0].equals("GroupBy") ){
                    //groupBy = tokens[1];

                    // TODO decide what to do here
                }
                ++curLine;
            }
        } catch (IOException ex) {
            Logger.getLogger(Report.class.getName()).log(Level.SEVERE, null, ex);
        } 
        
    }

    public String getColumns() {
        return columns;
    }

    public String getParameters() {
        return parameters;
    }

    public String getQuery() {
        return query;
    }

    public boolean isShowNumbers() {
        return showNumbers;
    }

    public String getTitle() {
        return title;
    }
    
    public File getFile() {
        return file;
    }

    public String getTitleName() {
        return titleName;
    }
}
