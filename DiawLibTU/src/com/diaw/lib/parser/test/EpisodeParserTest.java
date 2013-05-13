package com.diaw.lib.parser.test;

import junit.framework.Assert;

import org.junit.Test;

import com.diaw.lib.model.Episode;
import com.diaw.lib.parser.EpisodeParser;

public class EpisodeParserTest {

	private String showName1 = "xyz title name S01E02 bla bla";
	private String showName2 = "bla bla title name.S03E04";
	private String showName3 = "the season title name s05e03";
	private String showName4 = "South Park 1x01_-_Cartman_a_une_sonde_anale.avi";
	private String showName5 = "South%20Park%201x01_-_Cartman_a_une_sonde_anale.avi";
	private String showName6 = "South_Park_-_15x01_-_HumancentiPad_VOSTFR";
	private String showName7 = "Californication.S01E01.VOSTFR.DVDRip.XviD-GKS";
	private String showName8 = "Black Mirror (2011) - 01x01 - The National Anthem.RiVER.English.HI.C.updated.Addic7ed.com";
	private String showName9 = "Breaking.Bad.S02E01.HDTV.XviD-0TV";
	private String showName10 = "Two.And.A.Half.Men.S03E01.VOSTFR.DVDRip.XviD-GKS";
	private String showName11 = "Mad Men - 06x01-02 - The Doorway, Part 1-2.PROPER EVOLVE.English.HI.C.orig.Addic7ed.com";
	private String showName12 = "The Office US S09E22E23 HDTV x264-LOL";
	
	@Test
	public void parseShow12() {
		EpisodeParser myParser = new EpisodeParser();
		Episode one = myParser.parse(showName12);
		Assert.assertNotNull(one);
		Assert.assertEquals(one.getShowName(), "The Office US");
		Assert.assertEquals(one.getSeasonNumber(), 9);
		Assert.assertEquals(one.getEpisodeNumber(), 22);
	}
	
	@Test
	public void parseShow11() {
		EpisodeParser myParser = new EpisodeParser();
		Episode one = myParser.parse(showName11);
		Assert.assertNotNull(one);
		Assert.assertEquals(one.getShowName(), "Mad Men");
		Assert.assertEquals(one.getSeasonNumber(), 6);
		Assert.assertEquals(one.getEpisodeNumber(), 1);
	}
	
	@Test
	public void parseShow10() {
		EpisodeParser myParser = new EpisodeParser();
		Episode one = myParser.parse(showName10);
		Assert.assertNotNull(one);
		Assert.assertEquals(one.getShowName(), "Two And A Half Men");
		Assert.assertEquals(one.getSeasonNumber(), 3);
		Assert.assertEquals(one.getEpisodeNumber(), 1);
	}

	@Test
	public void parseShow9() {
		EpisodeParser myParser = new EpisodeParser();
		Episode one = myParser.parse(showName9);
		Assert.assertNotNull(one);
		Assert.assertEquals(one.getShowName(), "Breaking Bad");
		Assert.assertEquals(one.getSeasonNumber(), 2);
		Assert.assertEquals(one.getEpisodeNumber(), 1);
	}

	@Test
	public void parseShow8() {
		EpisodeParser myParser = new EpisodeParser();
		Episode one = myParser.parse(showName8);
		Assert.assertNotNull(one);
		Assert.assertEquals(one.getShowName(), "Black Mirror (2011)");
		Assert.assertEquals(one.getSeasonNumber(), 1);
		Assert.assertEquals(one.getEpisodeNumber(), 1);
	}
	@Test
	public void parseShow7() {
		EpisodeParser myParser = new EpisodeParser();
		Episode one = myParser.parse(showName7);
		Assert.assertNotNull(one);
		Assert.assertEquals(one.getShowName(), "Californication");
		Assert.assertEquals(one.getSeasonNumber(), 1);
		Assert.assertEquals(one.getEpisodeNumber(), 1);
	}
	@Test
	public void parseShow1() {
		EpisodeParser myParser = new EpisodeParser();
		Episode one = myParser.parse(showName1);
		Assert.assertNotNull(one);
		Assert.assertEquals(one.getShowName(), "xyz title name");
		Assert.assertEquals(one.getSeasonNumber(), 1);
		Assert.assertEquals(one.getEpisodeNumber(), 2);
	}	
	@Test
	public void parseShow2() {
		EpisodeParser myParser = new EpisodeParser();
		Episode one = myParser.parse(showName2);
		Assert.assertNotNull(one);
		Assert.assertEquals(one.getShowName(), "bla bla title name");
		Assert.assertEquals(one.getSeasonNumber(), 3);
		Assert.assertEquals(one.getEpisodeNumber(), 4);
	}
	@Test
	public void parseShow3() {
		EpisodeParser myParser = new EpisodeParser();
		Episode one = myParser.parse(showName3);
		Assert.assertNotNull(one);
		Assert.assertEquals(one.getShowName(), "the season title name");
		Assert.assertEquals(one.getSeasonNumber(), 5);
		Assert.assertEquals(one.getEpisodeNumber(), 3);
	}
	@Test
	public void parseShow4() {
		EpisodeParser myParser = new EpisodeParser();
		Episode one = myParser.parse(showName4);
		Assert.assertNotNull(one);
		Assert.assertEquals(one.getShowName(), "South Park");
		Assert.assertEquals(one.getSeasonNumber(), 1);
		Assert.assertEquals(one.getEpisodeNumber(), 1);
	}
	@Test
	public void parseShow5() {
		EpisodeParser myParser = new EpisodeParser();
		Episode one = myParser.parse(showName5);
		Assert.assertNotNull(one);
		Assert.assertEquals(one.getShowName(), "South Park");
		Assert.assertEquals(one.getSeasonNumber(), 1);
		Assert.assertEquals(one.getEpisodeNumber(), 1);
	}
	@Test
	public void parseShow6() {
		EpisodeParser myParser = new EpisodeParser();
		Episode one = myParser.parse(showName6);
		Assert.assertNotNull(one);
		Assert.assertEquals(one.getShowName(), "South Park");
		Assert.assertEquals(one.getSeasonNumber(), 15);
		Assert.assertEquals(one.getEpisodeNumber(), 1);
	}

}
