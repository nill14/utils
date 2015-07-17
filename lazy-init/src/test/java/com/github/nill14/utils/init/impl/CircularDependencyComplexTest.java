package com.github.nill14.utils.init.impl;

import javax.inject.Inject;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.github.nill14.utils.init.api.IBeanInjector;
import com.github.nill14.utils.init.binding.TestBinder;

public class CircularDependencyComplexTest {

	@Test // (expectedExceptions={RuntimeException.class})
	public void testCyclicProvides() {
		TestBinder b = new TestBinder();
		b.bind(IUserService.class).to(UserService.class);
		IBeanInjector beanInjector = b.toBeanInjector();

		MediaService mediaService = beanInjector.getInstance(MediaService.class);

		Assert.assertNotNull(mediaService);
//		Assert.assertNotNull(userService.getUser());
//		Assert.assertNotNull(userService.getMediaService());
//		Assert.assertNotNull(userService.getMediaService().getMedia());
//		Assert.assertNotNull(userService.getMediaService().getUserService());
//		Assert.assertNotNull(userService.getMediaService().getUserService().getUser());
	}

	private static interface IUserService {}
	
	
	private static class UserService implements IUserService {
		@Inject
		public UserService(ExecutorService service) {

		}

	}

	private static class MediaService {
		@Inject
		public MediaService(IUserService userService, FileService fileService, FileService fileService2) {

		}
	}

	private static class FileService {

		@Inject
		public FileService(IUserService userService) {
		}

	}

	private static class ExecutorService {

		@Inject
		public ExecutorService(FileService fileService) {
		}

	}

}
