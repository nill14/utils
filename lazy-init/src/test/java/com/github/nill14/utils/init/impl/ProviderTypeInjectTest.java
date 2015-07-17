package com.github.nill14.utils.init.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.github.nill14.utils.init.api.BindingKey;
import com.github.nill14.utils.init.api.IBeanInjector;
import com.github.nill14.utils.init.binding.TestBinder;
import com.github.nill14.utils.init.meta.Annotations;
import com.github.nill14.utils.init.meta.Provides;

public class ProviderTypeInjectTest {
	
	private IBeanInjector beanInjector;
	
	@BeforeClass
	public void prepare() {
		TestBinder b = new TestBinder();
		b.bind(IMediaService.class).to(MediaService.class).in(Singleton.class);
		b.bind(IFileService.class).toProvider(FileServiceProvider.class);
		b.scanProvidesBindings(this);
		b.bind(IUserService.class).to(UserService.class).in(Singleton.class);
		
		beanInjector = b.toBeanInjector();
	}
	
	
	
	
	@Test
	public void testProvidedObjectInjectMembers()  {
		IFileService fileService = beanInjector.getInstance(BindingKey.of(IFileService.class, Annotations.named("provides")));
		IFileService file2Service = beanInjector.getInstance(BindingKey.of(IFileService.class, Annotations.named("provider")));
		
		MatcherAssert.assertThat(fileService, CoreMatchers.instanceOf(FileService.class));
		MatcherAssert.assertThat(file2Service, CoreMatchers.instanceOf(File2Service.class));
		
		Assert.assertNotNull(((FileService) fileService).userService);
		Assert.assertNotNull(((File2Service) file2Service).mediaService);
		
	}

	@Provides
	@Named("provides")
	IFileService providesFileService() {
		return new FileService();
	}
	
	@Named("provider")
	private static class FileServiceProvider implements Provider<IFileService> {
		@Override
		public IFileService get() {
			return new File2Service();
		}
	}

	private static interface IUserService {

	}

	private static class UserService implements IUserService {

	}

	private static interface IMediaService {

	}

	private static class MediaService implements IMediaService {

	}
	
	private static interface IFileService {
		
	}

	private static class FileService implements IFileService {
		@Inject
		IUserService userService;
	}

	private static class File2Service implements IFileService {
		@Inject
		IMediaService mediaService;
	}
	
}
