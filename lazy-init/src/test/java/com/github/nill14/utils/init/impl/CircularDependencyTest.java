package com.github.nill14.utils.init.impl;

import javax.inject.Inject;
import javax.inject.Provider;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.github.nill14.utils.init.api.IBeanInjector;
import com.github.nill14.utils.init.binding.TestBinder;
import com.github.nill14.utils.init.meta.Provides;

public class CircularDependencyTest {
	
	
	@Test
	public void testCyclicFieldMembers()  {
		TestBinder b = new TestBinder();
		b.bind(IMediaService.class).to(MediaServiceField.class);
		b.bind(IUserService.class).to(UserServiceField.class);
		IBeanInjector beanInjector = b.toBeanInjector();

		IUserService userService = beanInjector.getInstance(IUserService.class);
		
		Assert.assertNotNull(userService);
		Assert.assertNotNull(userService.getUser());
		Assert.assertNotNull(userService.getMediaService());
		Assert.assertNotNull(userService.getMediaService().getMedia());
		Assert.assertNotNull(userService.getMediaService().getUserService());
		Assert.assertNotNull(userService.getMediaService().getUserService().getUser());
	}

//	@Test(expectedExceptions={RuntimeException.class})
	public void testCyclicConstructors()  {
		TestBinder b = new TestBinder();
		b.bind(IMediaService.class).to(MediaServiceConstructor.class);
		b.bind(IUserService.class).to(UserServiceConstructor.class);
		IBeanInjector beanInjector = b.toBeanInjector();

		IUserService userService = beanInjector.getInstance(IUserService.class);
		
		Assert.assertNotNull(userService);
		Assert.assertNotNull(userService.getUser());
		Assert.assertNotNull(userService.getMediaService());
		Assert.assertNotNull(userService.getMediaService().getMedia());
		Assert.assertNotNull(userService.getMediaService().getUserService());
		Assert.assertNotNull(userService.getMediaService().getUserService().getUser());
		
	}	
	
	@Test//t(expectedExceptions={RuntimeException.class})
	public void testCyclicProviderConstructors()  {
		TestBinder b = new TestBinder();
		b.bind(IMediaService.class).toProvider(MediaServiceConstructorProvider.class);
		b.bind(IUserService.class).toProvider(UserServiceConstructorProvider.class);
		IBeanInjector beanInjector = b.toBeanInjector();

		IUserService userService = beanInjector.getInstance(IUserService.class);
		
		Assert.assertNotNull(userService);
		Assert.assertNotNull(userService.getUser());
		Assert.assertNotNull(userService.getMediaService());
		Assert.assertNotNull(userService.getMediaService().getMedia());
		Assert.assertNotNull(userService.getMediaService().getUserService());
		Assert.assertNotNull(userService.getMediaService().getUserService().getUser());
	}	
	
	@Test//(expectedExceptions={RuntimeException.class})
	public void testCyclicProviderGet()  {
		TestBinder b = new TestBinder();
		b.bind(IMediaService.class).to(MediaServiceProvider.class);
		b.bind(IUserService.class).to(UserServiceProvider.class);
		IBeanInjector beanInjector = b.toBeanInjector();

		IUserService userService = beanInjector.getInstance(IUserService.class);
		
		Assert.assertNotNull(userService);
		Assert.assertNotNull(userService.getUser());
		Assert.assertNotNull(userService.getMediaService());
		Assert.assertNotNull(userService.getMediaService().getMedia());
		Assert.assertNotNull(userService.getMediaService().getUserService());
		Assert.assertNotNull(userService.getMediaService().getUserService().getUser());
	}		
	
	
	@Test//(expectedExceptions={RuntimeException.class})
	public void testCyclicProvides()  {
		TestBinder b = new TestBinder();
		b.scanProvidesBindings(new Module());
		IBeanInjector beanInjector = b.toBeanInjector();

		IUserService userService = beanInjector.getInstance(IUserService.class);
		
		Assert.assertNotNull(userService);
		Assert.assertNotNull(userService.getUser());
		Assert.assertNotNull(userService.getMediaService());
		Assert.assertNotNull(userService.getMediaService().getMedia());
		Assert.assertNotNull(userService.getMediaService().getUserService());
		Assert.assertNotNull(userService.getMediaService().getUserService().getUser());
	}			

	private static interface IUserService {
		String getUser();
		
		IMediaService getMediaService();
	}

	private static interface IMediaService {
		String getMedia();
		
		IUserService getUserService();
	}

	private static class UserServiceField implements IUserService {
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


	private static class MediaServiceField implements IMediaService {
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
	
	private static class UserServiceConstructorProvider implements Provider<IUserService> {

		private final IMediaService mediaService;

		@Inject
		public UserServiceConstructorProvider(IMediaService mediaService) {
			this.mediaService = mediaService;
		}
		
		@Override
		public IUserService get() {
			return new UserServiceConstructor(mediaService);
		}
		public IMediaService getMediaService() {
			return mediaService;
		}
	}

	
	private static class MediaServiceConstructorProvider implements Provider<IMediaService> {
		private final IUserService userService;

		@Inject 
		public MediaServiceConstructorProvider(IUserService userService) {
			this.userService = userService;
		}
		
		@Override
		public IMediaService get() {
			return new MediaServiceConstructor(userService);
		}
	}
	
	private static class UserServiceConstructor implements IUserService {
		private final IMediaService mediaService;

		@Inject
		public UserServiceConstructor(IMediaService mediaService) {
			this.mediaService = mediaService;
		}
		
		@Override
		public String getUser() {
			return "abc";
		}
		@Override
		public IMediaService getMediaService() {
			return mediaService;
		}
	}


	private static class MediaServiceConstructor implements IMediaService {
		private final IUserService userService;

		@Inject 
		public MediaServiceConstructor(IUserService userService) {
			this.userService = userService;
		}
		
		@Override
		public String getMedia() {
			return "abc";
		}
		@Override
		public IUserService getUserService() {
			return userService;
		}
	}
	
	private static class UserServiceProvider implements IUserService {
		private final IMediaService mediaService;

		@Inject
		public UserServiceProvider(IMediaService mediaService) {
			this.mediaService = mediaService;
		}
		@Override
		public String getUser() {
			return "abc";
		}
		@Override
		public IMediaService getMediaService() {
			return mediaService;
		}
	}


	private static class MediaServiceProvider implements IMediaService {
		private final IUserService userService;

		@Inject 
		public MediaServiceProvider(Provider<IUserService> userService) {
			this.userService = userService.get();
		}
		
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
		IMediaService provideMediaService(IUserService userService) {
			return new MediaServiceConstructor(userService);
		}
		
		@Provides
		IUserService provideUserService(IMediaService mediaService) {
			return new UserServiceConstructor(mediaService);
		}
	}
	
}
