package uk.ac.gla.chessmantis;

import uk.ac.gla.chessmantis.event.ErrorEvent;
import uk.ac.gla.chessmantis.event.IllegalMoveEvent;
import uk.ac.gla.chessmantis.event.MoveEvent;
import uk.ac.gla.chessmantis.event.StatusEvent;

import java.util.*;
import java.io.*;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;

/** 
 * @author Neil Henning 
 * @version 1.0
 */

public class DebugWriter implements ChessEventWriter
{
	private JFrame frame;
	private JTextPane textarea;
	static final Color HEADING = new Color(100, 149, 237);
	static final Color TIME = new Color(160, 32, 240);
	static final Color MOVE = Color.blue;
	static final Color STATUS = new Color(210, 105, 30);
	static final Color ILLEGALMOVE = Color.magenta;
	static final Color ERROR = new Color(0, 100, 0);
	static final Color TEXT = Color.black;
	
	/** Appends a string onto the textarea with the given Color. */
	public void append(Color c, String s)
	{
		StyleContext sc = StyleContext.getDefaultStyleContext();
		AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);
		textarea.replaceSelection(s);
		textarea.setCharacterAttributes(aset, false);
		textarea.setCaretPosition(textarea.getDocument().getLength());
	}
	
	private class SaveListener implements ActionListener
	{
		public void actionPerformed(ActionEvent ae)
		{
			try
			{
				Calendar calendar = new GregorianCalendar();
				calendar.setTime(new Date());
				
				Formatter form = new Formatter(new File(("ChessMantis_" + calendar.get(Calendar.MONTH) + calendar.get(Calendar.DAY_OF_MONTH) + calendar.get(Calendar.HOUR) + calendar.get(Calendar.MINUTE) + calendar.get(Calendar.SECOND) + ".log")));
				form.format("%s\n", textarea.getText());
				form.flush();
			}
			catch(Exception e)
			{
				System.err.println(e);
			}
		}
	}
	
	/*private class QuitListener implements ActionListener
	{
		public void actionPerformed(ActionEvent ae)
		{
			frame.setVisible(false);
		}
	}*/
	
	public DebugWriter(String name)
	{
		JMenuItem itemsave = new JMenuItem("Save");
		itemsave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		itemsave.addActionListener(new SaveListener());
		
		/*JMenuItem itemclose = new JMenuItem("Quit");
		itemclose.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
		itemclose.addActionListener(new QuitListener());*/
		
		JMenu menufile = new JMenu("File");
		menufile.setMnemonic(KeyEvent.VK_F);
		menufile.add(itemsave);
		/*menufile.add(itemclose);*/
		
		JMenuBar menubar = new JMenuBar();
		menubar.add(menufile);
		
		textarea = new JTextPane();
		JPanel contentpane = new JPanel(new BorderLayout());
		contentpane.add(new JScrollPane(textarea), BorderLayout.CENTER);
		frame = new JFrame();
		frame.setTitle(name);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(500, 500);
		frame.setContentPane(contentpane);
		frame.setJMenuBar(menubar);
		frame.setVisible(true);
		append(HEADING, "Chessmantis Debug Information:");
	}
	
	public DebugWriter()
	{
		this("Debug Writer");
	}
	
	public void write(MoveEvent event)
	{
		append(TIME, ("\n" + (new Date()).toString()));
		append(MOVE, (" uk.ac.gla.chessmantis.Move: " + XBUtils.moveToString((Move) event.getMove())));
	}
	
	public void write(StatusEvent event)
	{
		append(TIME, ("\n" + (new Date()).toString()));
		append(STATUS, (" uk.ac.gla.chessmantis.Status: " + event.getStatus().toString()));
	}
	
	public void write(IllegalMoveEvent event)
	{
		append(TIME, ("\n" + (new Date()).toString()));
		append(ILLEGALMOVE, (" IllegalMove: " + event.getIllegalMove().toString()));
	}
	
	public void write(ErrorEvent event)
	{
		append(TIME, ("\n" + (new Date()).toString()));
		append(ERROR, (" Error: " + (event.getError() + " " + event.getCommand())));
	}
	
	public void write(String s)
	{
		append(TIME, ("\n" + (new Date()).toString()));
		append(TEXT, " " + s);
	}
	
	public void rawWrite(String message)
	{
		append(TEXT, "\n" + message + " ");
	}
}
