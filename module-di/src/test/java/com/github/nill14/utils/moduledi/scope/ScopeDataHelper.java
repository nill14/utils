/*
 * Copyright 2002-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.nill14.utils.moduledi.scope;

import org.springframework.beans.factory.config.Scope;
import org.springframework.core.NamedInheritableThreadLocal;
import org.springframework.core.NamedThreadLocal;
import org.springframework.util.ClassUtils;

public abstract class ScopeDataHelper  {

	private static final boolean jsfPresent =
			ClassUtils.isPresent("javax.faces.context.FacesContext", ScopeDataHelper.class.getClassLoader());

	private static final ThreadLocal<ScopeData> scopeDataHolder =
			new NamedThreadLocal<ScopeData>("Scope Data");

	private static final ThreadLocal<ScopeData> inheritableScopeDataHolder =
			new NamedInheritableThreadLocal<ScopeData>("Scope Data");


	/**
	 * Reset the RequestAttributes for the current thread.
	 */
	public static void resetRequestAttributes() {
		scopeDataHolder.remove();
		inheritableScopeDataHolder.remove();
	}

	/**
	 * Bind the given RequestAttributes to the current thread,
	 * <i>not</i> exposing it as inheritable for child threads.
	 * @param attributes the RequestAttributes to expose
	 * @see #setRequestAttributes(RequestAttributes, boolean)
	 */
	public static void setRequestAttributes(ScopeData attributes) {
		setRequestAttributes(attributes, false);
	}

	/**
	 * Bind the given RequestAttributes to the current thread.
	 * @param attributes the RequestAttributes to expose,
	 * or {@code null} to reset the thread-bound context
	 * @param inheritable whether to expose the RequestAttributes as inheritable
	 * for child threads (using an {@link InheritableThreadLocal})
	 */
	public static void setRequestAttributes(ScopeData attributes, boolean inheritable) {
		if (attributes == null) {
			resetRequestAttributes();
		}
		else {
			if (inheritable) {
				inheritableScopeDataHolder.set(attributes);
				scopeDataHolder.remove();
			}
			else {
				scopeDataHolder.set(attributes);
				inheritableScopeDataHolder.remove();
			}
		}
	}

	/**
	 * Return the RequestAttributes currently bound to the thread.
	 * @return the RequestAttributes currently bound to the thread,
	 * or {@code null} if none bound
	 */
	public static ScopeData getRequestAttributes() {
		ScopeData attributes = scopeDataHolder.get();
		if (attributes == null) {
			attributes = inheritableScopeDataHolder.get();
		}
		return attributes;
	}

	/**
	 * Return the RequestAttributes currently bound to the thread.
	 * <p>Exposes the previously bound RequestAttributes instance, if any.
	 * Falls back to the current JSF FacesContext, if any.
	 * @return the RequestAttributes currently bound to the thread
	 * @throws IllegalStateException if no RequestAttributes object
	 * is bound to the current thread
	 * @see #setRequestAttributes
	 * @see ServletRequestAttributes
	 * @see FacesRequestAttributes
	 * @see javax.faces.context.FacesContext#getCurrentInstance()
	 */
	public static ScopeData currentRequestAttributes() throws IllegalStateException {
		ScopeData attributes = getRequestAttributes();
		/*
		if (attributes == null) {
			if (jsfPresent) {
				attributes = FacesRequestAttributesFactory.getFacesRequestAttributes();
			}
			if (attributes == null) {
				throw new IllegalStateException("No thread-bound request found: " +
						"Are you referring to request attributes outside of an actual web request, " +
						"or processing a request outside of the originally receiving thread? " +
						"If you are actually operating within a web request and still receive this message, " +
						"your code is probably running outside of DispatcherServlet/DispatcherPortlet: " +
						"In this case, use RequestContextListener or RequestContextFilter to expose the current request.");
			}
		}*/
		return attributes;
	}

/*
	/**
	 * Inner class to avoid hard-coded JSF dependency.
 	 *//*
	private static class FacesRequestAttributesFactory {

		public static ScopeData getFacesRequestAttributes() {
			FacesContext facesContext = FacesContext.getCurrentInstance();
			return (facesContext != null ? new FacesRequestAttributes(facesContext) : null);
		}
	} */
	

}
