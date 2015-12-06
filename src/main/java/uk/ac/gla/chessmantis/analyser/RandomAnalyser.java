package uk.ac.gla.chessmantis.analyser;

import uk.ac.gla.chessmantis.Moveable;
import uk.ac.gla.chessmantis.analyser.Analyser;
import uk.ac.gla.chessmantis.evaluator.Evaluator;

import java.util.Random;
import java.util.*;

class RandomAnalyser implements Analyser {

	private Evaluator eval;

	public void setCancel(boolean c) {
		;
	}

        public void setDepth(int depth) {
	        ;
        }

	public void setEvaluator(Evaluator eval) {
	        this.eval = eval;
	}

	public boolean isDone() {
		return true;
	}

	public void reset() {
		;
	}

	public Moveable get(){
		return null;
	}

	public void run() {
		getNextMove(eval, 0);
	}


	public Moveable getNextMove(Evaluator e, int ply) {		
		return (new ArrayList<Moveable>(e.generateLegalMoves())).get((new Random()).nextInt(e.generateLegalMoves().size()));
	}
}
