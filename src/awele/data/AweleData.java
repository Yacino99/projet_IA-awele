package awele.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

/**
 * @author Alexandre Blansché
 * Base de donnée de coups joués sur laquelle peut se baser l'apprentissage
 */
public class AweleData extends ArrayList <AweleObservation>
{
    private static final String PATH = "data/awele.data";
    
    /**
     * @return Les données
     */
    public static AweleData getInstance ()
    {
        AweleData instance = new AweleData ();
        return instance;
    }
    
    private AweleData ()
    {
        this (AweleData.PATH);
    }
    
    private AweleData (String path)
    {
        super ();
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(new File (path)));
            br.readLine ();
            String string;
            while ((string = br.readLine ()) != null)
                this.add (new AweleObservation (string));
            br.close ();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        } 
    }
}
