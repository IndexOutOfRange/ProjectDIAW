package com.steto.diawinstaller.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;

public class DiawInstallerStepThree extends JDialog {

	private static final long serialVersionUID = 1L;
	private String mVLCPath;
	private String mDIAWPath;
	private JTextField mUser;
	private JTextField mPass;

	public DiawInstallerStepThree(String vlcPath, String diawPath) {
		super((Dialog) null);
		mVLCPath = vlcPath;
		mDIAWPath = diawPath;
		initUI();
	}

	public final void initUI() {

		JPanel basic = new JPanel();
		basic.setLayout(new BoxLayout(basic, BoxLayout.Y_AXIS));
		add(basic);

		// TOP PART
		JPanel topPanel = new JPanel(new BorderLayout(0, 0));
		topPanel.setMaximumSize(new Dimension(450, 0));
		JLabel hint = new JLabel("DIAW Installer Tool Step 3/3");
		hint.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 0));
		topPanel.add(hint);

		ImageIcon icon = new ImageIcon("jdev.png");
		JLabel label = new JLabel(icon);
		label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		topPanel.add(label, BorderLayout.EAST);

		JSeparator separator = new JSeparator();
		separator.setForeground(Color.gray);

		topPanel.add(separator, BorderLayout.SOUTH);

		basic.add(topPanel);

		// CENTER PART
		// TEXT PART
		JPanel vlcPanel = new JPanel(new GridLayout(4, 2));
		vlcPanel.setPreferredSize(new Dimension(450, 200));
		vlcPanel.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));

		JLabel vlcPathLabel = new JLabel("Your VLC is located here :");
		JLabel vlcPath = new JLabel(mVLCPath);

		JLabel diawPathLabel = new JLabel("DIAW will be located here :");
		JLabel diawPath = new JLabel(mDIAWPath);

		JLabel userLabel = new JLabel("Your login for diaw is :");
		mUser = new JTextField();
//		mUser.setText("stetoCode");

		JLabel passLabel = new JLabel("Your pass for diaw is :");
		mPass = new JTextField();
//		mPass.setText("stetoCode");

		vlcPanel.add(vlcPathLabel);
		vlcPanel.add(vlcPath);
		vlcPanel.add(diawPathLabel);
		vlcPanel.add(diawPath);
		vlcPanel.add(userLabel);
		vlcPanel.add(mUser);
		vlcPanel.add(passLabel);
		vlcPanel.add(mPass);

		basic.add(vlcPanel);
		// BOTTOM PART
		JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));

		JButton previous = new JButton("Previous");
		previous.setMnemonic(KeyEvent.VK_P);
		previous.addActionListener(new ActionPreviousListener());
		JButton next = new JButton("Finish");
		next.setMnemonic(KeyEvent.VK_F);
		next.addActionListener(new ActionNextStepListener());
		JButton close = new JButton("Close");
		close.setMnemonic(KeyEvent.VK_C);
		close.addActionListener(new ActionCloseListener());

		bottom.add(previous);
		bottom.add(next);
		bottom.add(close);
		basic.add(bottom);

		bottom.setMaximumSize(new Dimension(450, 0));

		setTitle("DIAW Installer");
		setMinimumSize(new Dimension(500, 250));
		setResizable(false);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
	}

	public String readInputStreamAsString(InputStream in) throws IOException {

		BufferedInputStream bis = new BufferedInputStream(in);
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		int result = bis.read();
		while (result != -1) {
			byte b = (byte) result;
			buf.write(b);
			result = bis.read();
		}
		return buf.toString();
	}

	public byte[] readInputStreamAsByteAray(InputStream in) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		int next = in.read();
		while (next > -1) {
			bos.write(next);
			next = in.read();
		}
		bos.flush();
		return bos.toByteArray();
	}

	private final class ActionPreviousListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			DiawInstallerStepThree.this.setVisible(false);
			DiawInstallerStepThree.this.dispose();
			DiawInstallerStepTwo ex = new DiawInstallerStepTwo(mVLCPath);
			ex.setVisible(true);
		}
	}

	private final class ActionCloseListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			DiawInstallerStepThree.this.setVisible(false);
			DiawInstallerStepThree.this.dispose();
		}
	}

	private final class ActionNextStepListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (mUser.getText() != null && !mUser.getText().isEmpty() && mPass.getText() != null && !mPass.getText().isEmpty()) {
				copyLUAPlugin();
				copyDIAWJar();
				constructDIAWIni();
			} else {
				System.out.println("remplir les champs user et pass");
			}
		}

		private void constructDIAWIni() {
			try {
				String ini = "{\"login\": \"" + mUser.getText() + "\",\"pass\": \"" + mPass.getText() + "\"}";
				PrintWriter writer = new PrintWriter(mDIAWPath + "\\diaw.ini", "UTF-8");
				writer.println(ini);
				writer.close();
			} catch (FileNotFoundException | UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}

		private void copyDIAWJar() {
			try {
//				FileInputStream diawJar = new FileInputStream("C:\\Users\\Stephane\\Desktop\\Livraison Diaw\\09122013\\DIAWJava\\diaw.jar");
				 InputStream diawJar = getClass().getResourceAsStream("/diaw.jar");
				FileOutputStream out = new FileOutputStream(mDIAWPath + "\\diaw.jar");
				out.write(readInputStreamAsByteAray(diawJar));
				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		private void copyLUAPlugin() {
			try {
				// FileInputStream fis = new
				// FileInputStream("E://diawPlugin.lua");
				InputStream luaPlugin = getClass().getResourceAsStream("/diawPlugin.lua");
				String luaPluginString = readInputStreamAsString(luaPlugin);
				int begin = luaPluginString.indexOf("local command");
				if (begin != -1) {
					int end = luaPluginString.indexOf(".. get_URI()");
					if (end != -1) {
						end += 12;
						String originalCommand = luaPluginString.substring(begin, end);
						mDIAWPath = mDIAWPath.replaceAll("\\\\", "\\\\\\\\");
						String finalCommand = "local command = \"java -jar \\\"" + mDIAWPath + "\\\\diaw.jar\\\" -conf \\\""+ mDIAWPath + "\\\\diaw.ini\\\" -episode \" .. get_URI() ";
						luaPluginString = luaPluginString.replace(originalCommand, finalCommand);
						PrintWriter writer = new PrintWriter(mVLCPath + "\\diawPlugin.lua", "UTF-8");
						writer.println(luaPluginString);
						writer.close();
					} else {
						System.out.println("pas possible de trouver la fin de la commande");
					}
				} else {
					System.out.println("pas possible de trouver le debut de la commande");
				}
			} catch (IOException e1) {
				System.out.println("impossible de trouver la ressource dans le jar");
			}
		}
	}

}
