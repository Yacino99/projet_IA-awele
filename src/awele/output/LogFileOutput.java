package awele.output;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author Alexandre Blansché
 * Sortie dans un fichier texte
 */
public class LogFileOutput extends Output
{
    private String path;
    
    /**
     * @param path Chemin vers le fichier
     */
    public LogFileOutput (String path)
    {
    	super (false);
        this.path = path;
    }
    
    /**
     * @param path Chemin vers le fichier
     * @param anonymous Préservation de l'anonymat
     */
    public LogFileOutput (String path, boolean anonymous)
    {
    	super (anonymous);
        this.path = path;
    }

    @Override
    public void print (String string)
    {
    	FileWriter fw;
    	try
    	{
    		fw = new FileWriter (this.path, true);
    		BufferedWriter bw = new BufferedWriter (fw);
    		PrintWriter out = new PrintWriter (bw);
    		out.println (string);
    		out.close ();
    	}
    	catch (IOException e)
    	{
    		e.printStackTrace();
    	}
    }

    @Override
    public void initialiaze ()
    {
        File file = new File (this.path);
        file.delete ();
    }

}
