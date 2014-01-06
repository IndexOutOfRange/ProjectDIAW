package com.steto.diawinstaller.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;

public class DiawInstallerStepOne extends JDialog {

	private static final long serialVersionUID = 1L;
	private JTextField mVLCPathTextField;
	private JButton mNextStepButton;

	public DiawInstallerStepOne() {
		super((Dialog) null);
		initUI();
	}

	public final void initUI() {

		JPanel basic = new JPanel();
		basic.setLayout(new BoxLayout(basic, BoxLayout.Y_AXIS));
		add(basic);

		// TOP PART
		JPanel topPanel = new JPanel(new BorderLayout(0, 0));
		topPanel.setMaximumSize(new Dimension(450, 0));
		JLabel hint = new JLabel("DIAW Installer Tool Step 1/3 - Point to directory {vlc}/lua/meta/fetcher");
		hint.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 0));
		topPanel.add(hint);

		ImageIcon icon = new ImageIcon("jdev.png");
		JLabel label = new JLabel(icon);
		label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		topPanel.add(label, BorderLayout.EAST);

		GridBagConstraints constraint = new GridBagConstraints();
		constraint.fill = GridBagConstraints.HORIZONTAL;
		JSeparator separator = new JSeparator();
		separator.setForeground(Color.gray);

		topPanel.add(separator, BorderLayout.SOUTH);

		basic.add(topPanel);

		// CENTER PART
		// TEXT PART
		JPanel vlcPanel = new JPanel(new GridBagLayout());
		vlcPanel.setBorder(BorderFactory.createEmptyBorder(25, 0, 25, 0));
		GridBagConstraints c = new GridBagConstraints();

        c.weightx = 0.0;
		JLabel vlcPathLabel = new JLabel("VLC meta fetcher plugin Path :");
		vlcPathLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
		vlcPanel.add(vlcPathLabel,c);

		mVLCPathTextField = new JTextField();
		mVLCPathTextField.setEnabled(true);
		JPanel textFieldPanel = new JPanel(new GridBagLayout());
		textFieldPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.0;
		textFieldPanel.add(mVLCPathTextField, c);
		vlcPanel.add(textFieldPanel, c);

		JButton vlcBrowseButton = new JButton("...");
		c.weightx = 0.0;
		JPanel buttonPanel = new JPanel(new GridBagLayout());
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
		buttonPanel.add(vlcBrowseButton);
		vlcBrowseButton.addActionListener(new ActionBrowseListener());
		vlcPanel.add(buttonPanel,c);

		basic.add(vlcPanel);

		// BOTTOM PART
		JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));

		JButton previous = new JButton("Previous");
		previous.setMnemonic(KeyEvent.VK_P);
		previous.setEnabled(false);
		mNextStepButton = new JButton("Next");
		mNextStepButton.setMnemonic(KeyEvent.VK_N);
		mNextStepButton.setEnabled(false);
		mNextStepButton.addActionListener(new ActionNextStepListener());
		JButton close = new JButton("Close");
		close.setMnemonic(KeyEvent.VK_C);
		close.addActionListener(new ActionCloseListener());

		bottom.add(previous);
		bottom.add(mNextStepButton);
		bottom.add(close);
		basic.add(bottom);

		bottom.setMaximumSize(new Dimension(450, 0));

		setTitle("DIAW Installer");
		setMinimumSize(new Dimension(500, 220));
//		setResizable(false);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
	}

	private final class ActionCloseListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			DiawInstallerStepOne.this.setVisible(false);
			DiawInstallerStepOne.this.dispose();
		}
	}

	private final class ActionNextStepListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			DiawInstallerStepOne.this.setVisible(false);
			DiawInstallerStepOne.this.dispose();
			DiawInstallerStepTwo ex = new DiawInstallerStepTwo(mVLCPathTextField.getText());
			ex.setVisible(true);
		}
	}

	private final class ActionBrowseListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser chooser = new JFileChooser();
			chooser.setCurrentDirectory(new java.io.File("."));
			chooser.setDialogTitle("VLC lua plugin installation path");
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			chooser.setAcceptAllFileFilterUsed(false);

			if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				mVLCPathTextField.setText(chooser.getSelectedFile().getAbsolutePath());
				mNextStepButton.setEnabled(true);
			} else {
				if(mVLCPathTextField.getText() == null || mVLCPathTextField.getText().isEmpty()) {
					mNextStepButton.setEnabled(false);
				} else {
					mNextStepButton.setEnabled(true);
				}
				System.out.println("No Selection ");
			}
		}
	}
}
