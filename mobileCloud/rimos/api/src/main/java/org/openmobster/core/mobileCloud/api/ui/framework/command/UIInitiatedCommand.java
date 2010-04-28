/**
 * 
 */
package org.openmobster.core.mobileCloud.api.ui.framework.command;

/**
 * @author openmobster@gmail
 *
 */
public interface UIInitiatedCommand extends Command
{
	public void doViewBefore(CommandContext commandContext);
}
