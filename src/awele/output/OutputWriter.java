package awele.output;

import java.util.ArrayList;

/**
 * @author Alexandre Blansché
 * Classe pour gérer les affichages lors des parties d'Awele
 */
public class OutputWriter
{
    private ArrayList <Output> outputs;
    private ArrayList <Output> debug;

    /**
     * Constructeur...
     */
    public OutputWriter ()
    {
        this.outputs = new ArrayList <Output> ();
        this.debug = new ArrayList <Output> ();
    }
    
    /**
     * Rajoute une sortie
     * @param output Sortie à rajouter
     */
    public void addOutput (Output output)
    {
        output.initialiaze ();
        this.outputs.add (output);
    }
    
    /**
     * Rajoute une sortie de débuggage
     * @param output Sortie à rajouter
     */
    public void addDebug (Output output)
    {
        output.initialiaze ();
        this.debug.add (output);
    }
    
    /**
     * Saut de ligne
     */
    public void print ()
    {
        for (Output output: this.outputs)
            output.print ();
        for (Output debug: this.debug)
            debug.print ();
    }
    
    protected void printDebug ()
    {
        for (Output debug: this.debug)
            debug.print ();
    }
    
    /**
     * Affichage d'un message
     * @param object Objet à afficher
     */
    public void print (Object object)
    {
        for (Output output: this.outputs)
            output.print (object, false);
        for (Output debug: this.debug)
            debug.print (object, false);
    }
    
    /**
     * Affichage d'un message
     * @param object Objet à afficher
     */
    public void print (Object object, boolean anonymous)
    {
        for (Output output: this.outputs)
            output.print (object, anonymous);
        for (Output debug: this.debug)
            debug.print (object, anonymous);
    }
    
    protected void printDebug (Object object)
    {
        for (Output debug: this.debug)
            debug.print (object);
    }
}
