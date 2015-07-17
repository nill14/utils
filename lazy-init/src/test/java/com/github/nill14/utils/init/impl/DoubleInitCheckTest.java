package com.github.nill14.utils.init.impl;

import javax.inject.Inject;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.github.nill14.utils.init.api.IBeanInjector;
import com.github.nill14.utils.init.binding.TestBinder;
import com.github.nill14.utils.init.meta.Provides;

public class DoubleInitCheckTest {
	
	@Test(expectedExceptions={RuntimeException.class})
	public void testCyclicProvides()  {
		TestBinder b = new TestBinder();
		b.scanProvidesBindings(new Module());
		b.bind(IUserService.class).to(UserService.class);
		IBeanInjector beanInjector = b.toBeanInjector();

		IMediaService mediaService = beanInjector.getInstance(IMediaService.class);
		
		Assert.assertNotNull(mediaService);
	}			

	private static interface IUserService {
		String getUser();
		
		IMediaService getMediaService();
	}

	private static interface IMediaService {
		String getMedia();
		
		IUserService getUserService();
	}

	private static class UserService implements IUserService {
		@Inject 
		private IMediaService mediaService;
		
		@Override
		public String getUser() {
			return "abc";
		}
		@Override
		public IMediaService getMediaService() {
			return mediaService;
		}
	}


	private static class MediaService implements IMediaService {
		@Inject 
		private IUserService userService;
		@Override
		public String getMedia() {
			return "abc";
		}
		@Override
		public IUserService getUserService() {
			return userService;
		}
	}

	
	private static final class Module {
		@Provides
		IMediaService provideMediaService(IUserService userService, IBeanInjector beanInjector) {
			MediaService instance = new MediaService();
			beanInjector.injectMembers(instance);
			return instance;
		}
	}
	
}
