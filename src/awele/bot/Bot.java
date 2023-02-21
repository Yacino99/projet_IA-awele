package awele.bot;

import java.util.ArrayList;

import awele.core.Board;
import awele.core.InvalidBotException;
import awele.run.Main;

/**
 * @author Alexandre Blansché
 * Classe abstraite représentant un joueur artificiel pour l'Awele
 * Ce n'est pas la classe à étendre pour le projet, c'est la classe CompetitorBot qu'il faut étendre !
 * Cette classe peut servir à créer des bots qui ne seront pas utilisés lors du championnat
 */
public abstract class Bot
{
    private String name;
    private ArrayList <String> authors;

    protected Bot ()
    {
        this.name = "";
        this.authors = new ArrayList <String> ();
    }

    /**
     * Fonction pour donner un nom au bot (soyez imaginatifs !)
     * Doit être appelé dans le constructeur des classes dérivées
     * @param name Le nom du bot
     */
    protected void setBotName (String name)
    {
        this.name = name;
        Main.getInstance ().print ("Instanciation du bot \"" + name + "\"");
    }

    /**
     * Fonction pour définir tous les auteurs
     * @param names Prénoms et noms des étudiants
     * @throws InvalidBotException Il ne peut y avoir que deux auteurs au maximum !
     */
    protected void setAuthors (String... names) throws InvalidBotException
    {
        if (names.length > 2)
            throw new InvalidBotException ("Trop d'auteurs pour ce bot");
        else
        {
            this.authors = new ArrayList <String> ();
            for (String name: names)
                this.authors.add (name);
        }
    }

    /**
     * Fonction pour rajouter un auteur
     * @param name Prénom et nom de l'étudiant
     * @throws InvalidBotException Il ne peut y avoir que deux auteurs au maximum !
     */
    protected void addAuthor (String name) throws InvalidBotException
    {
        if (this.authors.size () < 2)
            this.authors.add (name);
        else
            throw new InvalidBotException ("Trop d'auteurs pour ce bot");
    }

    /**
     * @return Le nom du bot
     */
    public String getName ()
    {
        return this.name;
    }

    /**
     * @return Le nom des auteurs
     */
    public String getAuthors ()
    {
        String string = this.authors.get (0);
        if (this.authors.size () == 2)
            string += " et " + this.authors.get (1);
        return string;
    }

    @Override
    public String toString ()
    {
        return this.getName ();
    }

    /**
     * Fonction d'initalisation du bot
     * Cette fonction est appelée avant chaque affrontement
     */
    public abstract void initialize ();

    /**
     * Fonction de finalisation du bot
     * Cette fonction est appelée après chaque affrontement
     */
    public abstract void finish ();

    /**
     * Fonction de prise de décision du bot
     * @param board État du plateau de jeu
     * @return Un tableau de six réels indiquant l'efficacité supposée de chacun des six coups possibles
     */
    public abstract double [] getDecision (Board board);

    /**
     * Apprentissage du bot
     * Cette fonction est appelée une fois (au chargement du bot)
     */
    public abstract void learn ();
}
