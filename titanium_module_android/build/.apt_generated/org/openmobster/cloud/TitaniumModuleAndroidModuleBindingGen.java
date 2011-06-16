/**
 * Appcelerator Titanium Mobile
 * Copyright (c) 2010 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Apache Public License
 * Please see the LICENSE included with this distribution for details.
 */
package org.openmobster.cloud;

import org.appcelerator.kroll.KrollArgument;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.KrollConverter;
import org.appcelerator.kroll.KrollInvocation;
import org.appcelerator.kroll.KrollMethod;
import org.appcelerator.kroll.KrollModule;
import org.appcelerator.kroll.KrollProxyBinding;
import org.appcelerator.kroll.KrollModuleBinding;
import org.appcelerator.kroll.KrollDynamicProperty;
import org.appcelerator.kroll.KrollReflectionProperty;
import org.appcelerator.kroll.KrollInjector;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollDefaultValueProvider;
import org.appcelerator.kroll.util.KrollReflectionUtils;
import org.appcelerator.kroll.util.KrollBindingUtils;
import org.appcelerator.titanium.kroll.KrollBridge;
import org.appcelerator.titanium.TiContext;
import org.appcelerator.titanium.util.Log;

import org.mozilla.javascript.Scriptable;

import java.util.HashMap;

import org.openmobster.cloud.TitaniumModuleAndroidModule;


/* WARNING: this code is generated for binding methods in Android */
public class TitaniumModuleAndroidModuleBindingGen
	extends org.appcelerator.kroll.KrollModuleBindingGen
{
	private static final String TAG = "TitaniumModuleAndroidModuleBindingGen";

	private static final String CREATE_Example = "createExample";
	private static final String DYNPROP_exampleProp = "exampleProp";
	private static final String METHOD_sync = "sync";
	private static final String METHOD_example = "example";
		
	public TitaniumModuleAndroidModuleBindingGen() {
		super();
		// Constants are pre-bound
		
		bindings.put(CREATE_Example, null);
		bindings.put(DYNPROP_exampleProp, null);
		bindings.put(METHOD_sync, null);
		bindings.put(METHOD_example, null);
		
	}

	public void bindContextSpecific(KrollBridge bridge, KrollProxy proxy) {
	}

	@Override
	public Object getBinding(String name) {
		Object value = bindings.get(name);
		if (value != null) { 
			return value;
		}

		// Proxy create methods
		if (name.equals(CREATE_Example)) {
			KrollBindingUtils.KrollProxyCreator creator = new KrollBindingUtils.KrollProxyCreator() {
				public KrollProxy create(TiContext context) {
					return new org.openmobster.cloud.ExampleProxy(context);
				}
			};
			KrollMethod createExample_method = KrollBindingUtils.createCreateMethod(CREATE_Example, creator);
			bindings.put(CREATE_Example, createExample_method);
			return createExample_method;
		}




		// Dynamic Properties
		if (name.equals(DYNPROP_exampleProp)) {
			KrollDynamicProperty exampleProp_property = new KrollDynamicProperty(DYNPROP_exampleProp,
				true, true,
				true) {
				
				@Override
				public Object dynamicGet(KrollInvocation __invocation) {
	
	final org.appcelerator.kroll.KrollConverter __getExampleProp_converter = org.appcelerator.kroll.KrollConverter.getInstance();
	
	
	java.lang.String __retVal =
	
	
	((TitaniumModuleAndroidModule) __invocation.getProxy()).getExampleProp(
);
	return __getExampleProp_converter.convertNative(__invocation, __retVal);
				}

				@Override
				public void dynamicSet(KrollInvocation __invocation, Object __value) {
	
		KrollArgument __value_argument = new KrollArgument("value");
		java.lang.String value;
			__value_argument.setOptional(false);
			
				value = (java.lang.String)
					org.appcelerator.kroll.KrollConverter.getInstance().convertJavascript(__invocation, __value, java.lang.String.class);
		__value_argument.setValue(value);
		__invocation.addArgument(__value_argument);
	
	
	
	
	((TitaniumModuleAndroidModule) __invocation.getProxy()).setExampleProp(
		value
		);
				}
			};
			exampleProp_property.setJavascriptConverter(org.appcelerator.kroll.KrollConverter.getInstance());
			exampleProp_property.setNativeConverter(org.appcelerator.kroll.KrollConverter.getInstance());
			bindings.put(DYNPROP_exampleProp, exampleProp_property);
			return exampleProp_property;
		}

		// Methods
		if (name.equals(METHOD_sync)) {
			KrollMethod sync_method = new KrollMethod(METHOD_sync) {
				public Object invoke(KrollInvocation __invocation, Object[] __args) throws Exception
				{
	
	final org.appcelerator.kroll.KrollConverter __sync_converter = org.appcelerator.kroll.KrollConverter.getInstance();
	
	
	org.openmobster.cloud.SyncProxy __retVal =
	
	
	((TitaniumModuleAndroidModule) __invocation.getProxy()).sync(
		__invocation
		
);
	return __sync_converter.convertNative(__invocation, __retVal);
				}
			};
			bindings.put(METHOD_sync, sync_method);
			return sync_method;
		}
		
		if (name.equals(METHOD_example)) {
			KrollMethod example_method = new KrollMethod(METHOD_example) {
				public Object invoke(KrollInvocation __invocation, Object[] __args) throws Exception
				{
	
	final org.appcelerator.kroll.KrollConverter __example_converter = org.appcelerator.kroll.KrollConverter.getInstance();
	
	
	java.lang.String __retVal =
	
	
	((TitaniumModuleAndroidModule) __invocation.getProxy()).example(
);
	return __example_converter.convertNative(__invocation, __retVal);
				}
			};
			bindings.put(METHOD_example, example_method);
			return example_method;
		}


		return super.getBinding(name);
	}

	private static final String SHORT_API_NAME = "TitaniumModuleAndroid";
	private static final String FULL_API_NAME = "TitaniumModuleAndroid";
	private static final String ID = "org.openmobster.cloud";

	public String getAPIName() {
		return FULL_API_NAME;
	}

	public String getShortAPIName() {
		return SHORT_API_NAME;
	}

	public String getId() {
		return ID;
	}

	public KrollModule newInstance(TiContext context) {
		return new TitaniumModuleAndroidModule(context);
	}

	public Class<? extends KrollProxy> getProxyClass() {
		return TitaniumModuleAndroidModule.class;
	}

	public boolean isModule() {
		return true; 
	}
}