package org.cloudfoundry.promregator.cfaccessor;

import org.cloudfoundry.client.v2.applications.ListApplicationsResponse;
import org.cloudfoundry.client.v2.organizations.ListOrganizationDomainsResponse;
import org.cloudfoundry.client.v2.organizations.ListOrganizationsResponse;
import org.cloudfoundry.client.v2.spaces.GetSpaceSummaryResponse;
import org.cloudfoundry.client.v2.spaces.ListSpacesResponse;
import org.cloudfoundry.promregator.JUnitTestUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = CFAccessorCacheClassicTimeoutSpringApplication.class)
@TestPropertySource(locations= { "../default.properties" })
@DirtiesContext(classMode=ClassMode.AFTER_CLASS)
class CFAccessorCacheClassicTimeoutTest {

	@Autowired
	private CFAccessor parentMock;
	
	@Autowired
	private CFAccessorCacheClassic subject;
	
	@BeforeEach
	void invalidateCaches() {
		this.subject.invalidateCacheApplications();
		this.subject.invalidateCacheSpace();
		this.subject.invalidateCacheOrg();
		this.subject.invalidateCacheDomain();
	}
	
	@AfterAll
	static void runCleanup() {
		JUnitTestUtils.cleanUpAll();
	}
	
	@Test
	void testRetrieveOrgId() throws InterruptedException {
		Mono<ListOrganizationsResponse> response1 = subject.retrieveOrgId("dummy");
		response1.subscribe();
		Mockito.verify(this.parentMock, Mockito.times(1)).retrieveOrgId("dummy");
		
		Thread.sleep(300); // required, as we can only raise the timeout after 100ms
		
		Mono<ListOrganizationsResponse> response2 = subject.retrieveOrgId("dummy");
		response2.subscribe();
		assertThat(response1).isNotEqualTo(response2);
		Mockito.verify(this.parentMock, Mockito.times(2)).retrieveOrgId("dummy");
	}

	@Test
	void testRetrieveSpaceId() throws InterruptedException {
		
		Mono<ListSpacesResponse> response1 = subject.retrieveSpaceId("dummy1", "dummy2");
		response1.subscribe();
		Mockito.verify(this.parentMock, Mockito.times(1)).retrieveSpaceId("dummy1", "dummy2");

		Thread.sleep(300); // required, as we can only raise the timeout after 100ms

		Mono<ListSpacesResponse> response2 = subject.retrieveSpaceId("dummy1", "dummy2");
		response2.subscribe();
		assertThat(response1).isNotEqualTo(response2);
		Mockito.verify(this.parentMock, Mockito.times(2)).retrieveOrgId("dummy");
	}

	@Test
	void testRetrieveAllApplicationIdsInSpace() throws InterruptedException {
		Mono<ListApplicationsResponse> response1 = subject.retrieveAllApplicationIdsInSpace("dummy1", "dummy2");
		response1.subscribe();
		Mockito.verify(this.parentMock, Mockito.times(1)).retrieveAllApplicationIdsInSpace("dummy1", "dummy2");
		
		Thread.sleep(300); // required, as we can only raise the timeout after 100ms

		Mono<ListApplicationsResponse> response2 = subject.retrieveAllApplicationIdsInSpace("dummy1", "dummy2");
		response2.subscribe();
		assertThat(response1).isNotEqualTo(response2);
		Mockito.verify(this.parentMock, Mockito.times(2)).retrieveAllApplicationIdsInSpace("dummy1", "dummy2");
	}

	@Test
	void testRetrieveSpaceSummary() throws InterruptedException {
		Mono<GetSpaceSummaryResponse> response1 = subject.retrieveSpaceSummary("dummy");
		response1.subscribe();
		Mockito.verify(this.parentMock, Mockito.times(1)).retrieveSpaceSummary("dummy");
		
		Thread.sleep(300); // required, as we can only raise the timeout after 100ms

		Mono<GetSpaceSummaryResponse> response2 = subject.retrieveSpaceSummary("dummy");
		response2.subscribe();
		assertThat(response1).isNotEqualTo(response2);
		Mockito.verify(this.parentMock, Mockito.times(2)).retrieveSpaceSummary("dummy");
	}

	@Test
	void testRetrieveDomains() throws InterruptedException {
		Mono<ListOrganizationDomainsResponse> response1 = subject.retrieveAllDomains("dummy");
		response1.subscribe();
		Mockito.verify(this.parentMock, Mockito.times(1)).retrieveAllDomains("dummy");
		
		Thread.sleep(300); // required, as we can only raise the timeout after 100ms

		Mono<ListOrganizationDomainsResponse> response2 = subject.retrieveAllDomains("dummy");
		response2.subscribe();
		assertThat(response1).isNotEqualTo(response2);
		Mockito.verify(this.parentMock, Mockito.times(2)).retrieveAllDomains("dummy");
	}
}
