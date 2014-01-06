package com.diaw.console.main;

import com.diaw.lib.Diaw;

public class DIAWMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// usage java -jar diaw.jar -episode mon_episode_s01_e02.avi
		String showName = new String();
		String confName = new String();
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-episode")) {
				for (int j = i + 1; j < args.length; j++) {
					if(args[j].startsWith("-")) {
						break;
					} else { 
						showName += args[j] + " ";
					}
				}
				
			}
			if (args[i].equals("-conf")) {
				for (int j = i + 1; j < args.length; j++) {
					if(args[j].startsWith("-")) {
						break;
					} else { 
						confName += args[j] + " ";
					}
				}
				
			}
		}

		Diaw myDiaw = new Diaw(confName);
		myDiaw.addEpisode(showName);

	}

}
