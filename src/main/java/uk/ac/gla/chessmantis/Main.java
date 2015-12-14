package uk.ac.gla.chessmantis;

import uk.ac.gla.chessmantis.analyser.Analyser;
import uk.ac.gla.chessmantis.analyser.MiniMaxAnalyser;
import uk.ac.gla.chessmantis.evaluator.Evaluator;

public class Main {

    private static <T>  T loadInstance(final String className) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        final Class clazz = Class.forName(className);
        final T instance = (T) clazz.newInstance();
        return instance;
    }

    private static void loadGame(final String evaluatorName, final String analyserName) {
        Evaluator evaluator;
        Analyser analyser;

        try {
            evaluator = loadInstance(evaluatorName);
        } catch (Exception e) {
            System.err.println("Unable to load evaluator " + evaluatorName + ", loading default instead");
            evaluator = new Mantis();
        }
        try {
            analyser = loadInstance(analyserName);
        } catch (Exception e) {
            System.err.println("Unable to load analyser " + analyserName + ", loading default instead");
            analyser = new MiniMaxAnalyser();
        }

        XBoardIO xBoardIO = new XBoardIO(System.in, System.out);
        new Game(evaluator, analyser, xBoardIO, xBoardIO);
        xBoardIO.run();
    }

    public static void main(final String[] args)
    {
        String evaluatorName = "uk.ac.gla.chessmantis.Mantis";
        String analyserName = "AlphaBetaAnalyser";
        if (args.length > 1) //need atleast two arguments to make sense
        {
            for (int i = 0; i < args.length; i++)
            {
                if (args[i].startsWith("-")) //If it is a flag...
                {
                    char flag = args[i].charAt(1);
                    if (flag == 'e') //If the evaluator is specified
                    {
                        evaluatorName = args[++i];
                    }
                    else if (flag == 'a') //If the analyser is specified
                    {
                        analyserName = args[++i];
                    }
                }
            }
        }

        loadGame(evaluatorName, analyserName);
    }
}
