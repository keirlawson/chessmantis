package uk.ac.gla.chessmantis;

import uk.ac.gla.chessmantis.analyser.Analyser;
import uk.ac.gla.chessmantis.evaluator.Evaluator;

import java.util.concurrent.*;
import java.util.Date;
class TimeControl implements Callable<Moveable> {
	
	Evaluator evaluator;

	Analyser analyser;

	long timeformove;

	private int maxdepth = 0;

	public void setMaxDepth(int maxdepth) {
		this.maxdepth = maxdepth;
	}

	/**
 	 * Sets the time that the evaluation has to be made within
	 * 
	 * @param t The time left to make the moves in miliseconds
	 *
 	 */
	public void settimeformove(long t) {
		timeformove = t;
	}

	public Moveable call() {
		Moveable bestmove = null;
		int currentdepth = 1;
		long starttime = (new Date()).getTime();
		analyser.reset();
		analyser.setCancel(false);
		analyser.setDepth(currentdepth);
		analyser.setEvaluator(evaluator);
		Thread analyserThread = new Thread(analyser);
		long startantime = (new Date()).getTime();
		analyserThread.start();
		while ((((new Date()).getTime()) - starttime) < timeformove) {
			if (analyser.isDone()) {
				System.err.printf("uk.ac.gla.chessmantis.evaluator.Evaluator finished, taking approximatley %d miliseconds\n", (((new Date()).getTime()) - startantime));
				bestmove = analyser.get();
				System.err.printf("Suggested move is from %d to %d\n",bestmove.getFromPosition(),bestmove.getToPosition());
				if ( (2*( ((new Date()).getTime()) - starttime )) > (timeformove) ) {//If the time taken so far is more than the time left, dont bother to execute another analyser
					System.err.println("Probably wont have time left to run another analyser, breaking");
					break;
				}
				currentdepth++;
				if ((maxdepth != 0) && (currentdepth == maxdepth))
				{
					System.err.println("Reached maximum depth, breaking");
					break;
				}
				System.err.printf("Evaluating to %d ply\n",currentdepth);
				analyser.reset();
				analyser.setDepth(currentdepth);
				analyser.setEvaluator(evaluator);
				analyserThread = new Thread(analyser);
				startantime = (new Date()).getTime();
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
		return bestmove;
	}

	public void setEvaluator(Evaluator e) {
		evaluator = e;
	}

	TimeControl(Evaluator e, Analyser a) {
		evaluator = e;
		analyser = a;
	}
}
