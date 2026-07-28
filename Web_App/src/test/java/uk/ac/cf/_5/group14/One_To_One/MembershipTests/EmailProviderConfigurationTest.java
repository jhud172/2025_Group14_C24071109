package uk.ac.cf._5.group14.One_To_One.MembershipTests;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.mail.javamail.JavaMailSender;
import uk.ac.cf._5.group14.One_To_One.Membership.EmailService;
import uk.ac.cf._5.group14.One_To_One.Membership.NoOpEmailService;
import uk.ac.cf._5.group14.One_To_One.Membership.SmtpEmailService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class EmailProviderConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withBean(JavaMailSender.class, () -> mock(JavaMailSender.class))
            .withUserConfiguration(SmtpEmailService.class, NoOpEmailService.class);

    @Test
    void disabledEmailProviderUsesNoOpServiceEvenWhenMailHostPropertyExists() {
        contextRunner
                .withPropertyValues(
                        "app.email.provider=none",
                        "spring.mail.host="
                )
                .run(context -> {
                    assertThat(context).hasSingleBean(EmailService.class);
                    assertThat(context.getBean(EmailService.class)).isInstanceOf(NoOpEmailService.class);
                });
    }

    @Test
    void explicitSmtpProviderUsesSmtpService() {
        contextRunner
                .withPropertyValues(
                        "app.email.provider=smtp",
                        "spring.mail.host=smtp.example.test"
                )
                .run(context -> {
                    assertThat(context).hasSingleBean(EmailService.class);
                    assertThat(context.getBean(EmailService.class)).isInstanceOf(SmtpEmailService.class);
                });
    }
}
