package com.steto.diawinstaller.main;

import javax.swing.SwingUtilities;

public class DiawInstallerMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
//            	DiawInstallerStepThree ex = new DiawInstallerStepThree("C:\\Users\\Stephane\\Desktop\\DiawInstaller\\fakeVLC", "C:\\Users\\Stephane\\Desktop\\DiawInstaller\\fakeDIAW");
            	DiawInstallerStepOne ex = new DiawInstallerStepOne();
                ex.setVisible(true);
            }
        });
	}

}
