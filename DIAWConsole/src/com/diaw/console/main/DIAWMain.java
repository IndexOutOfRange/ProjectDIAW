package com.diaw.console.main;

import com.diaw.lib.Diaw;

public class DIAWMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//usage java -jar diaw.jar -episode mon_episode_s01_e02.avi
		String showName = new String();
		for(int i = 0 ; i < args.length; i++ ) {
			if( args[i].equals("-episode")) {
				for( int j = i + 1; j < args.length; j++) {
					showName += args[j] + " ";
				}
			}
		}

		
		Diaw myDiaw = new Diaw();
		myDiaw.addEpisode(showName);

	}

}
