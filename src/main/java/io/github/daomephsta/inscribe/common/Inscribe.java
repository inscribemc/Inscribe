package io.github.daomephsta.inscribe.common;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.FileAppender;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.PatternLayout;

import io.github.daomephsta.inscribe.common.guide.item.GuideItem;
import io.github.daomephsta.inscribe.server.DelegatingArgumentType;
import io.github.daomephsta.inscribe.server.InscribeCommand;
import net.fabricmc.fabric.api.event.server.ServerStartCallback;
import net.minecraft.command.arguments.ArgumentTypes;
import net.minecraft.util.registry.Registry;

public class Inscribe
{
	public static final String MOD_ID = "inscribe";
	public static final GuideItem GUIDE_ITEM = new GuideItem();
	public static final Logger LOGGER = LogManager.getLogger();

	public void onInitialise()
	{
		Registry.register(Registry.ITEM, MOD_ID + ":guide", GUIDE_ITEM);
        ArgumentTypes.register(Inscribe.MOD_ID + ":delegating", DelegatingArgumentType.class, new DelegatingArgumentType.Serialiser());
        ServerStartCallback.EVENT.register(server ->
        {
            InscribeCommand.register(server.getCommandManager().getDispatcher());
        });
        configureLogging();
	}

    private void configureLogging()
    {
        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        Configuration config = ctx.getConfiguration();
        Layout<String> inscribeLogLayout = PatternLayout.newBuilder()
            .withPattern("[%date{HH:mm:ss}] [%threadName/%logger{-2}/%level] %message%n")
            .withConfiguration(config)
            .build();
        Appender inscribeLogAppender = FileAppender.newBuilder()
            .withAppend(false)
            .withFileName("logs/inscribe.log")
            .withName("Inscribe")
            .withLayout(inscribeLogLayout)
            .setConfiguration(config)
            .build();
        inscribeLogAppender.start();
        config.addAppender(inscribeLogAppender);
        AppenderRef ref = AppenderRef.createAppenderRef("Inscribe", null, null);
        LoggerConfig loggerConfig = LoggerConfig.createLogger(false, Level.DEBUG, "inscribe.dedicated", "false",
            new AppenderRef[] {ref} , null, config, null);
        loggerConfig.addAppender(inscribeLogAppender, null, null);
        config.addLogger("inscribe.dedicated", loggerConfig);
        ctx.updateLoggers();
    }
}
