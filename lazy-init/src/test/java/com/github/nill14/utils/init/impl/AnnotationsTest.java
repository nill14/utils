package com.github.nill14.utils.init.impl;

import static java.lang.annotation.RetentionPolicy.*;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;

import javax.inject.Qualifier;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.github.nill14.utils.init.meta.Annotations;
import com.google.common.base.Preconditions;

public class AnnotationsTest {

	

	@Test
	public void testLongValue()  {
		TestValue counter1 = new TestValueImpl(25L);
		TestValue counter2 = Annotations.withValue(TestValue.class, 25L);
		
		Assert.assertEquals(counter1, counter2);
		Assert.assertEquals(counter1.hashCode(), counter2.hashCode());
		Assert.assertTrue(counter1.equals(counter2));
		Assert.assertTrue(counter2.equals(counter1));
	}
	
	@Test
	public void testEnumValue()  {
		TestEnumValue instance1 = new TestEnumValueImpl(TestEnum.ABC);
		TestEnumValue instance2 = Annotations.withValue(TestEnumValue.class, TestEnum.ABC);
		
		Assert.assertEquals(instance1, instance2);
		Assert.assertEquals(instance1.hashCode(), instance2.hashCode());
		Assert.assertTrue(instance1.equals(instance2));
		Assert.assertTrue(instance2.equals(instance1));
	}	
	
	

	@Qualifier
	@Retention(RUNTIME)
	public static @interface TestValue {
	
	    /** The name. */
	    long value();
	}
	

	public static final class TestValueImpl implements TestValue, Serializable {
	
		private final long value;
	
		public TestValueImpl(long value) {
			this.value = value;
		}
	
		@Override
		public long value() {
			return value;
		}
	
		@Override
		public int hashCode() {
			// This is specified in java.lang.Annotation.
			return (127 * "value".hashCode()) ^ Long.valueOf(value).hashCode();
		}
	
		@Override
		public boolean equals(Object o) {
			if (!(o instanceof TestValue)) {
				return false;
			}
	
			TestValue other = (TestValue) o;
			return value == other.value();
		}
	
		@Override
		public String toString() {
			return String.format("@%s(value=%s)", TestValue.class.getName(), value);
		}
	
		@Override
		public Class<? extends Annotation> annotationType() {
			return TestValue.class;
		}
	
		private static final long serialVersionUID = 0;
	}
	
	public static enum TestEnum {
		ABC, CDE, EFG;
	}
	
	@Qualifier
	@Retention(RUNTIME)
	public static @interface TestEnumValue {
	
	    /** The name. */
	    TestEnum value();
	}
	

	public static final class TestEnumValueImpl implements TestEnumValue, Serializable {
	
		private final TestEnum value;
	
		public TestEnumValueImpl(TestEnum value) {
			this.value = Preconditions.checkNotNull(value, "value");
		}
	
		@Override
		public TestEnum value() {
			return value;
		}
	
		@Override
		public int hashCode() {
			// This is specified in java.lang.Annotation.
			return (127 * "value".hashCode()) ^ value.hashCode();
		}
	
		@Override
		public boolean equals(Object o) {
			if (!(o instanceof TestEnumValue)) {
				return false;
			}
	
			TestEnumValue other = (TestEnumValue) o;
			return value.equals(other.value());
		}
	
		@Override
		public String toString() {
			return String.format("@%s(value=%s)", TestEnum.class.getName(), value);
		}
	
		@Override
		public Class<? extends Annotation> annotationType() {
			return TestEnumValue.class;
		}
	
		private static final long serialVersionUID = 0;
	}	
}
