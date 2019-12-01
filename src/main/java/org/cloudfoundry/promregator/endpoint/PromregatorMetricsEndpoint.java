package org.cloudfoundry.promregator.endpoint;

import java.util.Map;

import org.cloudfoundry.promregator.rewrite.GenericMetricFamilySamplesPrefixRewriter;
import org.cloudfoundry.promregator.rewrite.MergableMetricFamilySamples;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;

import io.prometheus.client.Collector.MetricFamilySamples;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.exporter.common.TextFormat;

@RestController
@RequestMapping(EndpointConstants.ENDPOINT_PATH_PROMREGATOR_METRICS)
@Scope(value=WebApplicationContext.SCOPE_REQUEST)
public class PromregatorMetricsEndpoint {
	@Autowired
	private CollectorRegistry collectorRegistry;
	
	private GenericMetricFamilySamplesPrefixRewriter gmfspr = new GenericMetricFamilySamplesPrefixRewriter("promregator");

	@GetMapping(produces=TextFormat.CONTENT_TYPE_004)
	public String getMetrics() {
		Map<String, MetricFamilySamples> mfsMap = this.gmfspr.determineEnumerationOfMetricFamilySamples(this.collectorRegistry);
		
		MergableMetricFamilySamples mmfs = new MergableMetricFamilySamples();
		mmfs.merge(mfsMap);
		
		return mmfs.toType004String();
	}
}
