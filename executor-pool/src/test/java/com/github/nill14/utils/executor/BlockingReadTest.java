package com.github.nill14.utils.executor;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class BlockingReadTest {

	@Ignore
	@Test(timeout = 20000)
	public void test() throws IOException, InterruptedException, ExecutionException {
		ServerEngine server = new ServerEngine(8, 2000, 10000);
		ClientEngine client = new ClientEngine(2);
		
		List<Future<?>> futures = Lists.newArrayList();
		Map<Future<LocalDateTime>, LocalDateTime> instants = Maps.newHashMap();
		
		for (int i = 0; i < 8; i++) {
			futures.add(client.next(server.getNext(i), i));
			futures.add(client.nextHeavy(i, 2000));
			LocalDateTime start = LocalDateTime.now();
			instants.put(client.nextInstant(i), start);
		}
		
		for (Future<?> future : futures) {
			future.get();
		}
		
		instants.forEach((future, start) -> {
			try {
				LocalDateTime end = future.get();
				Duration duration = Duration.between(start, end);
				long millis = duration.abs().toMillis();
				Assert.assertTrue(String.format("Duration: %d", millis), millis < 1000);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
	}

}
