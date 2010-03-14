package org.openmobster.core.mobileCloud.android.module.bus.rpc;

import java.util.Map;

interface IBusHandler
{
	Map handleInvocation(in Map invocation);
}