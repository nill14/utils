package com.github.nill14.utils.init.binding;

import com.google.common.reflect.TypeToken;

public interface Binder {

	<T> AnnotatedBindingBuilder<T> bind(TypeToken<T> typeToken);

	<T> AnnotatedBindingBuilder<T> bind(Class<T> type);

}
