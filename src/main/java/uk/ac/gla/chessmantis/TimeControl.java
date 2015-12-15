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

	private long currentTime() {
		return ((new Date()).getTime());
	}

	private long timeTakenSoFar(long startTime) {
		return currentTime() - startTime;
	}

	public void setMaxDepth(int maxDepth) {
		this.maxDepth = maxDepth;
	}

	public void setTimeForMove(long t) {
		timeForMove = t;
	}

	//TODO this can surely be reduced
	public Thread setupAnalyser(int depth) {
		analyser.reset();
		analyser.setCancel(false);
		analyser.setDepth(depth);
		analyser.setEvaluator(evaluator);
		Thread analyserThread = new Thread(analyser);
		analyserThread.start();
		return analyserThread;
	}

	public Moveable get() {
		Moveable bestMove = null;
		int currentDepth = 1;
		long startTime = currentTime();
		Thread analyserThread = setupAnalyser(currentDepth);
		long analysisStartTime = currentTime();
		while (timeTakenSoFar(startTime) < timeForMove) {
			if (analyser.isDone()) {
				System.err.printf("Evaluator finished, taking approximately %d milliseconds\n", (currentTime() - analysisStartTime));
				bestMove = analyser.get();
				System.err.printf("Suggested move is from %d to %d\n",bestMove.getFromPosition(),bestMove.getToPosition());
				if ( (2 * timeTakenSoFar(startTime)) > timeForMove ) {//If the time taken so far is more than the time left, dont bother to execute another analyser
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
				analyserThread = setupAnalyser(currentDepth);
				analysisStartTime = currentTime();
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
