package com.github.nill14.utils.init;

import java.time.LocalDateTime;
import java.util.List;

public interface ITimeService {

	LocalDateTime getNow();
	
	List<ICalculator> getProviders();
}
