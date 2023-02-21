package awele.core;

/**
 * @author Alexandre Blansché
 * Exception indiquand qu'un bot n'est pas valide
 */
public class InvalidBotException extends Exception
{
    /**
     * Constructeur...
     */
    public InvalidBotException ()
    {
        super ("Bot invalide");
    }
    
    /**
     * @param message Le message à afficher
     */
    public InvalidBotException (String message)
    {
        super ("Bot invalide : " + message);
    }
}
