package awele.run;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.reflections.Reflections;

import awele.bot.*;
import awele.bot.demo.random.RandomBot;
import awele.core.Awele;
import awele.core.InvalidBotException;
import awele.output.LogFileOutput;
import awele.output.OutputWriter;
import awele.output.StandardOutput;
import javassist.Modifier;

/**
 * @author Alexandre Blansché
 * Programme principal
 */
public final class Main extends OutputWriter
{
    private static Main instance = null;
    
    private static final String LOG_FILE = "awele.log";
    private static final String ANONYMOUS_LOG_FILE = "awele.anonymous.log";
    private static final int NB_RUNS = 100;
    //private static final int NB_RUNS = 1;
    private static final int MAX_LEARNING_TIME = 1000 * 60 * 70 * 1; // 1 h
    private static final int MAX_DECISION_TIME = 200; // 100 ms
    private static final int MAX_MEMORY = 1024 * 1024 * 64; // 64 MiB
    private static final int MAX_TOTAL_MEMORY = 1024 * 1024 * 1024; // 1 GiB

    ArrayList <Bot> bots;
    
    /**
     * @return Retourne l'instance de Main
     */
    public static Main getInstance ()
    {
        if (Main.instance == null)
            Main.instance = new Main ();
        return Main.instance;
    }
    
    private Main ()
    {
    }

    private static String formatDuration (final long l)
    {
        final long hr = TimeUnit.MILLISECONDS.toHours (l);
        final long min = TimeUnit.MILLISECONDS.toMinutes (l - TimeUnit.HOURS.toMillis(hr));
        final long sec = TimeUnit.MILLISECONDS.toSeconds (l - TimeUnit.HOURS.toMillis(hr) - TimeUnit.MINUTES.toMillis (min));
        final long ms = TimeUnit.MILLISECONDS.toMillis(l - TimeUnit.HOURS.toMillis (hr) - TimeUnit.MINUTES.toMillis (min) - TimeUnit.SECONDS.toMillis (sec));
        return String.format("%02d:%02d:%02d.%03d", hr, min, sec, ms);
    }
    
    private static String formatMemory(long bytes)
    {
        int unit = 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = ("KMGTPE").charAt(exp-1) + "i";
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }
    
    private void printDisqual (String botName, String cause)
    {
        this.print ("Bot \"" + botName + "\" disqualifié : " + cause);
        
    }
    
    private static long getUsedMemory ()
    {
        return Runtime.getRuntime().totalMemory () - Runtime.getRuntime().freeMemory ();
    }
    
    private void loadBots ()
    {
        long startLoading = System.currentTimeMillis ();
        long heapMaxSize = Runtime.getRuntime().maxMemory();
        this.print ("Mémoire totale : "+ Main.formatMemory (heapMaxSize));
        RandomBot random = null;
        try
        {
            random = new RandomBot ();
            random.learn ();
        }
        catch (Exception e)
        {
            this.print ("Ne peut pas instancier Random");
            System.exit (0);
        }
        
        long randomRunningTime = 0;
        int nbMoves = 0;
        
        for (int k = 0; k < Main.NB_RUNS; k++)
        {
            Awele awele = new Awele (random, random);
            try
            {
                awele.play ();
            }
            catch (InvalidBotException e)
            {
            }
            nbMoves += awele.getNbMoves ();
            randomRunningTime += awele.getRunningTime ();
        }
        long randomAverageDecisionTime = randomRunningTime / nbMoves;
        
        Reflections reflections = new Reflections ("awele.bot");
        // Pour l'évaluation, seuls les bots des étudiants (CompetitorBot) sont mis en compétition 
        //Set <Class <? extends CompetitorBot>> subClassesTmp = reflections.getSubTypesOf (CompetitorBot.class);
        Set <Class <? extends Bot>> subClassesTmp = reflections.getSubTypesOf (Bot.class);
        ArrayList <Class <? extends Bot>> subClasses = new ArrayList <Class <? extends Bot>> ();
        for (Class <? extends Bot> subClass : subClassesTmp)
        {
            if (!Modifier.isAbstract (subClass.getModifiers ()))
                subClasses.add (subClass);
        }
        this.print (subClasses.size () + " classes ont été trouvées");
        for (Class <? extends Bot> botClass: subClasses)
        	this.print (botClass);
        this.print ();
        
        this.bots = new ArrayList <Bot> ();
        int index = 0;
        for (Class <? extends Bot> subClass : subClasses)
        {
            System.gc ();
            long memoryBefore = Main.getUsedMemory ();
            index++;
            this.print ("Bot " + index + "/" + subClasses.size ());
            this.print ("Classe : " + subClass.getName ());
            try
            {
                Bot bot = (Bot) subClass.getConstructors () [0].newInstance ();
                if (bot != null)
                {
                    this.print ("Nom du bot : " + bot.getName ());
                    this.print ("Auteur(s) : " + bot.getAuthors (), true);
                    long start = System.currentTimeMillis ();
                    bot.learn ();
                    long end = System.currentTimeMillis ();
                    long runningTime = end - start;
                    this.print ("Temps d'apprentissage : " + Main.formatDuration (runningTime));
                    if (runningTime > Main.MAX_LEARNING_TIME)
                        this.printDisqual (bot.getName (), "temps d'apprentissage trop long");
                    else
                    {
                        Awele awele = new Awele (bot, random);
                        awele.play ();
                        long decisionTime = (long) ((2 * awele.getRunningTime ()) / awele.getNbMoves ()) - randomAverageDecisionTime;
                        this.print ("Durée d'une prise de décision : " + Main.formatDuration (decisionTime));
                        if (decisionTime > Main.MAX_DECISION_TIME)
                            this.printDisqual (bot.getName (), "durée d'une prise de décision trop long");
                        else
                        {
                            long memoryAfter = Main.getUsedMemory ();
                            System.gc ();
                            long totalUsedMemory = Math.max (0, memoryAfter - memoryBefore);
                            memoryAfter = Main.getUsedMemory ();
                            long usedMemory = Math.max (0, memoryAfter - memoryBefore);
                            this.print ("Usage mémoire : " + Main.formatMemory (usedMemory));
                            this.print ("Usage mémoire maximum : " + Main.formatMemory (totalUsedMemory));
                            if ((usedMemory > Main.MAX_MEMORY) || (totalUsedMemory > Main.MAX_TOTAL_MEMORY))
                                this.printDisqual (bot.getName (), "volume mémoire trop important");
                            else
                                this.bots.add (bot);
                        }
                    }
                }
                this.print ();
            }
            catch (Exception e)
            {
                this.printDisqual (subClass.getName (), "ne peut pas être instancié");
                this.print (e);
                e.printStackTrace();
                this.print ();
            }
        }
        this.print (this.bots.size () + " bots ont été instanciés");
        System.gc ();
        long endLoading = System.currentTimeMillis ();
        this.print ("Durée du chargement : " + Main.formatDuration (endLoading - startLoading));
        this.print ("Mémoire utilisée : "+ Main.formatMemory (Main.getUsedMemory ()));
    }
    
    private void tournament ()
    {
        this.print ();
        this.print ("Que le championnat commence !");
        int nbBots = this.bots.size ();
        final double [] points = new double [nbBots];
        int nbGames = (nbBots * (nbBots - 1) / 2);
        int game = 0;
        long start = System.currentTimeMillis ();
        for (int i = 0; i < nbBots; i++)
            for (int j = i + 1; j < nbBots; j++)
            {
                game++;
                this.print ();
                this.print ("Affrontement " + game + "/" + nbGames);
                this.print (this.bots.get (i).getName () + " vs. " + this.bots.get (j).getName ());
                double [] localPoints = new double [2];
                double nbMoves = 0;
                long runningTime = 0;
                for (int k = 0; k < Main.NB_RUNS; k++)
                {
                    Awele awele = new Awele (this.bots.get (i), this.bots.get (j));
                    //this.print ();
                    //awele.addOutputs (this.getOutputs ());
                    //awele.addDebug (StandardOutput.getInstance ());
                    try
                    {
                        awele.play ();
                    }
                    catch (InvalidBotException e)
                    {
                        e.printStackTrace();
                    }
                    nbMoves += awele.getNbMoves ();
                    runningTime += awele.getRunningTime ();
                    if (awele.getWinner () >= 0)
                        localPoints [awele.getWinner ()] += 3;
                    else
                    {
                        localPoints [0]++;
                        localPoints [1]++;
                    }
                }
                localPoints [0] /= Main.NB_RUNS;
                localPoints [1] /= Main.NB_RUNS;
                nbMoves /=  Main.NB_RUNS;
                runningTime /=  Main.NB_RUNS;
                this.print ("Score : " + localPoints [0] + " - " + localPoints [1]);
                if (localPoints [0] == localPoints [1])
                    this.print ("Égalité");
                else if (localPoints [0] > localPoints [1])
                    this.print (this.bots.get (i).getName () + " a gagné");
                else
                    this.print (this.bots.get (j).getName () + " a gagné");
                points [i] += localPoints [0];
                points [j] += localPoints [1];
                this.print ("Nombre de coups joués : " + nbMoves + " par match");
                this.print ("Durée : " + Main.formatDuration (runningTime) + " par match");
                System.gc ();
                this.print ("Mémoire utilisée : "+ Main.formatMemory (Main.getUsedMemory ()));
            }
        long end = System.currentTimeMillis ();
        this.print ();
        this.print ("Durée du championnat : " + Main.formatDuration (end - start));
        for (int i = 0; i < points.length; i++)
            points [i] = Math.round (points [i] * 100) / 100.;
        this.print ();
        this.print ("Scores finaux :");
        for (int i = 0; i < nbBots; i++)
        {
            this.print (this.bots.get (i) + " : " + points [i]);
        }
        this.print ();
        final Map <String, Integer> map = new HashMap <String, Integer> ();
        for (int i = 0; i < this.bots.size (); i++)
            map.put (this.bots.get (i).getName (), i);
        Collections.sort (this.bots, new Comparator <Bot> ()
        {
            @Override
            public int compare(Bot bot1, Bot bot2)
            {
                Integer index1 = map.get (bot1.getName ());
                Integer index2 = map.get (bot2.getName ());
                return Double.compare (points [index1], points [index2]);
            }
        });
        java.util.Arrays.sort (points);
        this.print ("Rangs :");
        for (int i = nbBots - 1; i >= 0; i--)
        {
            this.print ((nbBots - i) + ". " + this.bots.get (i) + " : " + points [i]);
        }
            
    }
    
    /**
     * @param args
     */
    public static void main (String [] args)
    {
        Main main = Main.getInstance ();
        main.addOutput (StandardOutput.getInstance ());
        main.addOutput (new LogFileOutput (Main.LOG_FILE));
        main.addOutput (new LogFileOutput (Main.ANONYMOUS_LOG_FILE, true));
        main.loadBots ();
        main.tournament ();
    }
}
