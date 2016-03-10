package com.why.game.repo;

import com.why.game.bo.land.Lands;

public interface LandRepo {

	void addLands(Lands lands);
	
	Lands findLands(long userId);
	
	boolean saveLands(Lands lands);
	
}
