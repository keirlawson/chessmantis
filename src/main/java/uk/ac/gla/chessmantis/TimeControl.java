package uk.ac.gla.chessmantis;

import uk.ac.gla.chessmantis.analyser.Analyser;
import uk.ac.gla.chessmantis.evaluator.Evaluator;

import java.util.Date;
import java.util.function.Supplier;

class TimeControl implements Supplier<Moveable> {
	
	private Evaluator evaluator;
	private Analyser analyser;
	private long timeForMove;
	private int maxDepth = 0;

	public void setMaxDepth(int maxDepth) {
		this.maxDepth = maxDepth;
	}

	public void setTimeForMove(long t) {
		timeForMove = t;
	}

	public Moveable get() {
		Moveable bestMove = null;
		int currentDepth = 1;
		long startTime = (new Date()).getTime();
		analyser.reset();
		analyser.setCancel(false);
		analyser.setDepth(currentDepth);
		analyser.setEvaluator(evaluator);
		Thread analyserThread = new Thread(analyser);
		long analysisStartTime = (new Date()).getTime();
		analyserThread.start();
		while ((((new Date()).getTime()) - startTime) < timeForMove) {
			if (analyser.isDone()) {
				System.err.printf("Evaluator finished, taking approximately %d milliseconds\n", (((new Date()).getTime()) - analysisStartTime));
				bestMove = analyser.get();
				System.err.printf("Suggested move is from %d to %d\n",bestMove.getFromPosition(),bestMove.getToPosition());
				if ( (2*( ((new Date()).getTime()) - startTime )) > (timeForMove) ) {//If the time taken so far is more than the time left, dont bother to execute another analyser
					System.err.println("Probably wont have time left to run another analyser, breaking");
					break;
				}
				currentDepth++;
				if ((maxDepth != 0) && (currentDepth == maxDepth))
				{
					System.err.println("Reached maximum depth, breaking");
					break;
				}
				System.err.printf("Evaluating to %d ply\n",currentDepth);
				analyser.reset();
				analyser.setDepth(currentDepth);
				analyser.setEvaluator(evaluator);
				analyserThread = new Thread(analyser);
				analysisStartTime = (new Date()).getTime();
				analyserThread.start();
			}
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {;} //Ignore this exception for now, not sure when it would occur - KL
		}
		if (!(analyser.isDone()))
		{
			//This really takes too long, need to work out if it would be quicker to check at each node - KL
			analyser.setCancel(true);
			System.err.println("Canceled most recent search due to running out of time");
			try {
				analyserThread.join();
			} catch (InterruptedException e) {;}
		}
		System.err.println("Finished evaluation");
		return bestMove;
	}

	public void setEvaluator(Evaluator e) {
		evaluator = e;
	}

	TimeControl(Evaluator e, Analyser a) {
		evaluator = e;
		analyser = a;
	}
}
