package org.centralperf.helper;

import net.redhogs.cronparser.CronExpressionDescriptor;
import net.redhogs.cronparser.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.support.CronSequenceGenerator;

import java.text.ParseException;
import java.util.Date;

/**
 * Helper for Cron Expressions
 *
 * @since 1.3
 */
public class CronExpression {

    private static final Logger log = LoggerFactory.getLogger(CronExpression.class);

    public static void validateCronExpression(String cronExpression) throws IllegalArgumentException {
        new CronSequenceGenerator(cronExpression);
    }

    public static Date getNextIteration(String cronExpression) {
        CronSequenceGenerator cronTrigger = new CronSequenceGenerator(cronExpression);
        return cronTrigger.next(new Date());
    }

    public static String asHumanReadable(String cronExpression) {
        try {
            return CronExpressionDescriptor.getDescription(cronExpression, Options.twentyFourHour());
        } catch (ParseException e) {
            log.warn("Error while parsing cron expression for Human Readable", e);
            return String.format("Unable to format human readable string for '%s'", cronExpression);
        }
    }
}
