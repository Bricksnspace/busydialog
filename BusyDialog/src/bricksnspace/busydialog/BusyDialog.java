/*
	Copyright 2013-2017 Mario Pascucci <mpascucci@gmail.com>
	This file is part of BusyDialog

	BusyDialog is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	BusyDialog is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with BusyDialog.  If not, see <http://www.gnu.org/licenses/>.

*/


package bricksnspace.busydialog;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.Timer;


/**
 * A dialog to display progress and status in long-run SwingWorker tasks<br/>
 * How to use:<br/>
 * <ul>
 * <li>Instantiate a SwingWorker subclass as task</li>
 * <li>Instantiate a BusyDialog</li>
 * <li>Assign task to dialog</li>
 * <li>use startTask() to display dialog and start task</li>
 * <li>at task end, dialog hide itself, and program return to statement after startTask()</li>
 * <li>Use task.get() to retrieve any task result (see {@link SwingWorker#get()})</li>
 * </ul>
 * 
 * @see javax.swing.SwingWorker 
 * 
 * @author Mario Pascucci
 *
 */


public class BusyDialog extends JDialog implements ActionListener {

	@SuppressWarnings("rawtypes")
	private SwingWorker task;
	private static final long serialVersionUID = 5600670512283134040L;
	private boolean progress = false;
	private JLabel msg;
	private JProgressBar pgr;
	private ImageIcon[] animIcn;
	private static int icnFrame = 0;
	private Timer timer;

	
	
	/**
	 * Define dialog with title, optional progress bar and animated/static icon 
	 * @see javax.swing.ImageIcon
	 * @param owner parent for dialog 
	 * @param title Dialog title
	 * @param progress true if dialog use a progress bar
	 * @param icn array of icon, if null no icon is used. Animation is three frames per second
	 */
	public BusyDialog(Frame owner, String title, boolean progress, ImageIcon[] icn) {
		
		super(owner,title,true);
		this.progress = progress;
		animIcn = icn;
		setLocationByPlatform(true);
		getRootPane().setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
		msg = new JLabel("...                ");
		if (animIcn != null) { 
			msg.setIcon(animIcn[0]);
		}
		getContentPane().setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		getContentPane().add(msg);
		if (progress) {
			// add a progress bar
			pgr = new JProgressBar(SwingConstants.HORIZONTAL,0,100);
			pgr.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
			pgr.setMinimum(0);
			pgr.setMaximum(100);
			pgr.setStringPainted(true);
			getContentPane().add(pgr);
		}
		pack();
	}

	
	/**
	 * Assign task to monitor
	 * @param task a SwingWorker subclass
	 */
	@SuppressWarnings("rawtypes")
	public void setTask(SwingWorker task) {
		
		this.task = task;
	}
	
	
	/**
	 * Start assigned task
	 */
	public void startTask() {

		timer = new Timer(300,this);
		task.execute();
		timer.start();
		setVisible(true);
	}
	
	
	
	/**
	 * Change dialog message
	 * @param txt new message
	 */
	public void setMsg(String txt) {
		
		msg.setText(txt);
		pack();
	}
	
	
	/** 
	 * change dialog message icon
	 * @param icn new icon
	 */
	public void setIcon(ImageIcon icn) {
		
		msg.setIcon(icn);
		pack();
	}
	
	
	/**
	 * Change progress status.<br/>
	 * If set to 0 use an "indeterminate" progress (pulsating progress bar)
	 * @param val 0-100 percent progress value
	 */
	public void setProgress(int val) {
		
		if (progress) {
			if (val == 0) {
				pgr.setIndeterminate(true);
			}
			else {
				pgr.setIndeterminate(false);
				pgr.setValue(val);
			}	
		}
	}
	
	
	/**
	 * Internal event handler
	 */
	public void actionPerformed(ActionEvent e) {
		
		if (e.getSource() == timer) {
			if (task.isDone()) {
				setVisible(false);
				timer.stop();
				return;
			}
			else {
				if (!isVisible()) {
					setVisible(true);
				}
			}
			if (animIcn != null) {
				msg.setIcon(animIcn[icnFrame]);
				icnFrame = (icnFrame +1) % animIcn.length;
			}
			if (progress) {
				int val = task.getProgress();
				if (val == 0) {
					pgr.setIndeterminate(true);
				}
				else {
					pgr.setIndeterminate(false);
					pgr.setValue(val);
				}	
			}
			pack();
		}
	}
		
}
