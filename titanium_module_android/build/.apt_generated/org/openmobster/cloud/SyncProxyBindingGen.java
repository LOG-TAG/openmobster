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

import org.openmobster.cloud.SyncProxy;


/* WARNING: this code is generated for binding methods in Android */
public class SyncProxyBindingGen
	extends org.appcelerator.kroll.KrollProxyBindingGen
{
	private static final String TAG = "SyncProxyBindingGen";

	private static final String METHOD_getValue = "getValue";
	private static final String METHOD_newBean = "newBean";
	private static final String METHOD_arrayLength = "arrayLength";
	private static final String METHOD_setValue = "setValue";
	private static final String METHOD_saveNewBean = "saveNewBean";
	private static final String METHOD_setNewBeanValue = "setNewBeanValue";
	private static final String METHOD_readAll = "readAll";
	private static final String METHOD_deleteBean = "deleteBean";
		
	public SyncProxyBindingGen() {
		super();
		// Constants are pre-bound
		
		bindings.put(METHOD_getValue, null);
		bindings.put(METHOD_newBean, null);
		bindings.put(METHOD_arrayLength, null);
		bindings.put(METHOD_setValue, null);
		bindings.put(METHOD_saveNewBean, null);
		bindings.put(METHOD_setNewBeanValue, null);
		bindings.put(METHOD_readAll, null);
		bindings.put(METHOD_deleteBean, null);
		
	}

	public void bindContextSpecific(KrollBridge bridge, KrollProxy proxy) {
	}

	@Override
	public Object getBinding(String name) {
		Object value = bindings.get(name);
		if (value != null) { 
			return value;
		}






		// Methods
		if (name.equals(METHOD_getValue)) {
			KrollMethod getValue_method = new KrollMethod(METHOD_getValue) {
				public Object invoke(KrollInvocation __invocation, Object[] __args) throws Exception
				{
	
	KrollBindingUtils.assertRequiredArgs(__args, 3, METHOD_getValue);
	final org.appcelerator.kroll.KrollConverter __getValue_converter = org.appcelerator.kroll.KrollConverter.getInstance();
		KrollArgument __channel_argument = new KrollArgument("channel");
		java.lang.String channel;
			__channel_argument.setOptional(false);
			
				channel = (java.lang.String)
					org.appcelerator.kroll.KrollConverter.getInstance().convertJavascript(__invocation, __args[0], java.lang.String.class);
		__channel_argument.setValue(channel);
		__invocation.addArgument(__channel_argument);
		KrollArgument __oid_argument = new KrollArgument("oid");
		java.lang.String oid;
			__oid_argument.setOptional(false);
			
				oid = (java.lang.String)
					org.appcelerator.kroll.KrollConverter.getInstance().convertJavascript(__invocation, __args[1], java.lang.String.class);
		__oid_argument.setValue(oid);
		__invocation.addArgument(__oid_argument);
		KrollArgument __fieldUri_argument = new KrollArgument("fieldUri");
		java.lang.String fieldUri;
			__fieldUri_argument.setOptional(false);
			
				fieldUri = (java.lang.String)
					org.appcelerator.kroll.KrollConverter.getInstance().convertJavascript(__invocation, __args[2], java.lang.String.class);
		__fieldUri_argument.setValue(fieldUri);
		__invocation.addArgument(__fieldUri_argument);
	
	
	java.lang.String __retVal =
	
	
	((SyncProxy) __invocation.getProxy()).getValue(
		__invocation
		,
		channel,
				oid,
				fieldUri
		);
	return __getValue_converter.convertNative(__invocation, __retVal);
				}
			};
			bindings.put(METHOD_getValue, getValue_method);
			return getValue_method;
		}
		
		if (name.equals(METHOD_newBean)) {
			KrollMethod newBean_method = new KrollMethod(METHOD_newBean) {
				public Object invoke(KrollInvocation __invocation, Object[] __args) throws Exception
				{
	
	KrollBindingUtils.assertRequiredArgs(__args, 1, METHOD_newBean);
		KrollArgument __channel_argument = new KrollArgument("channel");
		java.lang.String channel;
			__channel_argument.setOptional(false);
			
				channel = (java.lang.String)
					org.appcelerator.kroll.KrollConverter.getInstance().convertJavascript(__invocation, __args[0], java.lang.String.class);
		__channel_argument.setValue(channel);
		__invocation.addArgument(__channel_argument);
	
	
	
	
	((SyncProxy) __invocation.getProxy()).newBean(
		__invocation
		,
		channel
		);
		return KrollProxy.UNDEFINED;
				}
			};
			bindings.put(METHOD_newBean, newBean_method);
			return newBean_method;
		}
		
		if (name.equals(METHOD_arrayLength)) {
			KrollMethod arrayLength_method = new KrollMethod(METHOD_arrayLength) {
				public Object invoke(KrollInvocation __invocation, Object[] __args) throws Exception
				{
	
	KrollBindingUtils.assertRequiredArgs(__args, 3, METHOD_arrayLength);
	final org.appcelerator.kroll.KrollConverter __arrayLength_converter = org.appcelerator.kroll.KrollConverter.getInstance();
		KrollArgument __channel_argument = new KrollArgument("channel");
		java.lang.String channel;
			__channel_argument.setOptional(false);
			
				channel = (java.lang.String)
					org.appcelerator.kroll.KrollConverter.getInstance().convertJavascript(__invocation, __args[0], java.lang.String.class);
		__channel_argument.setValue(channel);
		__invocation.addArgument(__channel_argument);
		KrollArgument __oid_argument = new KrollArgument("oid");
		java.lang.String oid;
			__oid_argument.setOptional(false);
			
				oid = (java.lang.String)
					org.appcelerator.kroll.KrollConverter.getInstance().convertJavascript(__invocation, __args[1], java.lang.String.class);
		__oid_argument.setValue(oid);
		__invocation.addArgument(__oid_argument);
		KrollArgument __arrayUri_argument = new KrollArgument("arrayUri");
		java.lang.String arrayUri;
			__arrayUri_argument.setOptional(false);
			
				arrayUri = (java.lang.String)
					org.appcelerator.kroll.KrollConverter.getInstance().convertJavascript(__invocation, __args[2], java.lang.String.class);
		__arrayUri_argument.setValue(arrayUri);
		__invocation.addArgument(__arrayUri_argument);
	
	
	int __retVal =
	
	
	((SyncProxy) __invocation.getProxy()).arrayLength(
		__invocation
		,
		channel,
				oid,
				arrayUri
		);
	return __arrayLength_converter.convertNative(__invocation, __retVal);
				}
			};
			bindings.put(METHOD_arrayLength, arrayLength_method);
			return arrayLength_method;
		}
		
		if (name.equals(METHOD_setValue)) {
			KrollMethod setValue_method = new KrollMethod(METHOD_setValue) {
				public Object invoke(KrollInvocation __invocation, Object[] __args) throws Exception
				{
	
	KrollBindingUtils.assertRequiredArgs(__args, 4, METHOD_setValue);
	final org.appcelerator.kroll.KrollConverter __setValue_converter = org.appcelerator.kroll.KrollConverter.getInstance();
		KrollArgument __channel_argument = new KrollArgument("channel");
		java.lang.String channel;
			__channel_argument.setOptional(false);
			
				channel = (java.lang.String)
					org.appcelerator.kroll.KrollConverter.getInstance().convertJavascript(__invocation, __args[0], java.lang.String.class);
		__channel_argument.setValue(channel);
		__invocation.addArgument(__channel_argument);
		KrollArgument __oid_argument = new KrollArgument("oid");
		java.lang.String oid;
			__oid_argument.setOptional(false);
			
				oid = (java.lang.String)
					org.appcelerator.kroll.KrollConverter.getInstance().convertJavascript(__invocation, __args[1], java.lang.String.class);
		__oid_argument.setValue(oid);
		__invocation.addArgument(__oid_argument);
		KrollArgument __fieldUri_argument = new KrollArgument("fieldUri");
		java.lang.String fieldUri;
			__fieldUri_argument.setOptional(false);
			
				fieldUri = (java.lang.String)
					org.appcelerator.kroll.KrollConverter.getInstance().convertJavascript(__invocation, __args[2], java.lang.String.class);
		__fieldUri_argument.setValue(fieldUri);
		__invocation.addArgument(__fieldUri_argument);
		KrollArgument __value_argument = new KrollArgument("value");
		java.lang.String value;
			__value_argument.setOptional(false);
			
				value = (java.lang.String)
					org.appcelerator.kroll.KrollConverter.getInstance().convertJavascript(__invocation, __args[3], java.lang.String.class);
		__value_argument.setValue(value);
		__invocation.addArgument(__value_argument);
	
	
	java.lang.String __retVal =
	
	
	((SyncProxy) __invocation.getProxy()).setValue(
		__invocation
		,
		channel,
				oid,
				fieldUri,
				value
		);
	return __setValue_converter.convertNative(__invocation, __retVal);
				}
			};
			bindings.put(METHOD_setValue, setValue_method);
			return setValue_method;
		}
		
		if (name.equals(METHOD_saveNewBean)) {
			KrollMethod saveNewBean_method = new KrollMethod(METHOD_saveNewBean) {
				public Object invoke(KrollInvocation __invocation, Object[] __args) throws Exception
				{
	
	final org.appcelerator.kroll.KrollConverter __saveNewBean_converter = org.appcelerator.kroll.KrollConverter.getInstance();
	
	
	java.lang.String __retVal =
	
	
	((SyncProxy) __invocation.getProxy()).saveNewBean(
		__invocation
		
);
	return __saveNewBean_converter.convertNative(__invocation, __retVal);
				}
			};
			bindings.put(METHOD_saveNewBean, saveNewBean_method);
			return saveNewBean_method;
		}
		
		if (name.equals(METHOD_setNewBeanValue)) {
			KrollMethod setNewBeanValue_method = new KrollMethod(METHOD_setNewBeanValue) {
				public Object invoke(KrollInvocation __invocation, Object[] __args) throws Exception
				{
	
	KrollBindingUtils.assertRequiredArgs(__args, 3, METHOD_setNewBeanValue);
		KrollArgument __channel_argument = new KrollArgument("channel");
		java.lang.String channel;
			__channel_argument.setOptional(false);
			
				channel = (java.lang.String)
					org.appcelerator.kroll.KrollConverter.getInstance().convertJavascript(__invocation, __args[0], java.lang.String.class);
		__channel_argument.setValue(channel);
		__invocation.addArgument(__channel_argument);
		KrollArgument __fieldUri_argument = new KrollArgument("fieldUri");
		java.lang.String fieldUri;
			__fieldUri_argument.setOptional(false);
			
				fieldUri = (java.lang.String)
					org.appcelerator.kroll.KrollConverter.getInstance().convertJavascript(__invocation, __args[1], java.lang.String.class);
		__fieldUri_argument.setValue(fieldUri);
		__invocation.addArgument(__fieldUri_argument);
		KrollArgument __value_argument = new KrollArgument("value");
		java.lang.String value;
			__value_argument.setOptional(false);
			
				value = (java.lang.String)
					org.appcelerator.kroll.KrollConverter.getInstance().convertJavascript(__invocation, __args[2], java.lang.String.class);
		__value_argument.setValue(value);
		__invocation.addArgument(__value_argument);
	
	
	
	
	((SyncProxy) __invocation.getProxy()).setNewBeanValue(
		__invocation
		,
		channel,
				fieldUri,
				value
		);
		return KrollProxy.UNDEFINED;
				}
			};
			bindings.put(METHOD_setNewBeanValue, setNewBeanValue_method);
			return setNewBeanValue_method;
		}
		
		if (name.equals(METHOD_readAll)) {
			KrollMethod readAll_method = new KrollMethod(METHOD_readAll) {
				public Object invoke(KrollInvocation __invocation, Object[] __args) throws Exception
				{
	
	KrollBindingUtils.assertRequiredArgs(__args, 1, METHOD_readAll);
	final org.appcelerator.kroll.KrollConverter __readAll_converter = org.appcelerator.kroll.KrollConverter.getInstance();
		KrollArgument __channel_argument = new KrollArgument("channel");
		java.lang.String channel;
			__channel_argument.setOptional(false);
			
				channel = (java.lang.String)
					org.appcelerator.kroll.KrollConverter.getInstance().convertJavascript(__invocation, __args[0], java.lang.String.class);
		__channel_argument.setValue(channel);
		__invocation.addArgument(__channel_argument);
	
	
	java.lang.String __retVal =
	
	
	((SyncProxy) __invocation.getProxy()).readAll(
		__invocation
		,
		channel
		);
	return __readAll_converter.convertNative(__invocation, __retVal);
				}
			};
			bindings.put(METHOD_readAll, readAll_method);
			return readAll_method;
		}
		
		if (name.equals(METHOD_deleteBean)) {
			KrollMethod deleteBean_method = new KrollMethod(METHOD_deleteBean) {
				public Object invoke(KrollInvocation __invocation, Object[] __args) throws Exception
				{
	
	KrollBindingUtils.assertRequiredArgs(__args, 2, METHOD_deleteBean);
	final org.appcelerator.kroll.KrollConverter __deleteBean_converter = org.appcelerator.kroll.KrollConverter.getInstance();
		KrollArgument __channel_argument = new KrollArgument("channel");
		java.lang.String channel;
			__channel_argument.setOptional(false);
			
				channel = (java.lang.String)
					org.appcelerator.kroll.KrollConverter.getInstance().convertJavascript(__invocation, __args[0], java.lang.String.class);
		__channel_argument.setValue(channel);
		__invocation.addArgument(__channel_argument);
		KrollArgument __oid_argument = new KrollArgument("oid");
		java.lang.String oid;
			__oid_argument.setOptional(false);
			
				oid = (java.lang.String)
					org.appcelerator.kroll.KrollConverter.getInstance().convertJavascript(__invocation, __args[1], java.lang.String.class);
		__oid_argument.setValue(oid);
		__invocation.addArgument(__oid_argument);
	
	
	java.lang.String __retVal =
	
	
	((SyncProxy) __invocation.getProxy()).deleteBean(
		__invocation
		,
		channel,
				oid
		);
	return __deleteBean_converter.convertNative(__invocation, __retVal);
				}
			};
			bindings.put(METHOD_deleteBean, deleteBean_method);
			return deleteBean_method;
		}


		return super.getBinding(name);
	}

	private static final String SHORT_API_NAME = "Sync";
	private static final String FULL_API_NAME = "Sync";
	private static final String ID = "org.openmobster.cloud.SyncProxy";

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
		return null;
	}

	public Class<? extends KrollProxy> getProxyClass() {
		return SyncProxy.class;
	}

	public boolean isModule() {
		return false; 
	}
}