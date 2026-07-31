package uk.ac.cf._5.group14.One_To_One.Operations;

import org.junit.jupiter.api.Test;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ScheduledJobLeaseAspectTest {

    @Test
    void bindsAnnotationAndRunsScheduledMethodWhileLeaseIsOwned() {
        ScheduledJobLeaseService leaseService = mock(ScheduledJobLeaseService.class);
        when(leaseService.acquire("test-job", Duration.ofMinutes(5))).thenReturn(true);
        ScheduledTarget target = new ScheduledTarget();
        AspectJProxyFactory proxyFactory = new AspectJProxyFactory(target);
        proxyFactory.addAspect(new ScheduledJobLeaseAspect(leaseService));
        ScheduledTarget proxy = proxyFactory.getProxy();

        proxy.run();

        assertThat(target.invocations).isEqualTo(1);
        verify(leaseService).acquire("test-job", Duration.ofMinutes(5));
        verify(leaseService).release("test-job");
    }

    static class ScheduledTarget {

        private int invocations;

        @ExclusiveScheduledJob(value = "test-job", lockAtMostFor = "PT5M")
        public void run() {
            invocations++;
        }
    }
}
