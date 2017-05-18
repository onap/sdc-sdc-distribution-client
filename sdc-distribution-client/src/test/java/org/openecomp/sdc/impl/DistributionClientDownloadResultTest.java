package org.openecomp.sdc.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.BeforeClass;
import org.junit.Test;
import org.openecomp.sdc.utils.DistributionActionResultEnum;


public class DistributionClientDownloadResultTest {
	
	public static DistributionClientDownloadResultImpl distributionClientDownloadResult;
	
	@BeforeClass
	public static void init(){
		distributionClientDownloadResult = new DistributionClientDownloadResultImpl(DistributionActionResultEnum.SUCCESS, "");
	}
	
	@Test
	public void testNonHeaderFilename(){
		distributionClientDownloadResult.setArtifactName("service-BkPerformanceSrvs-csar.csar");
		assertEquals("service-BkPerformanceSrvs-csar.csar", distributionClientDownloadResult.getArtifactFilename());
	}
	
	@Test
	public void testNullFilename(){
		distributionClientDownloadResult.setArtifactName(null);
		assertNull(distributionClientDownloadResult.getArtifactFilename());
	}
	
	@Test
	public void testFilenameFromHeaderNoSpace(){
		distributionClientDownloadResult.setArtifactName("attachment;filename=\"service-BkPerformanceSrvs-csar.csar\"");
		assertEquals("service-BkPerformanceSrvs-csar.csar", distributionClientDownloadResult.getArtifactFilename());
	}
	
	@Test
	public void testFilenameFromHeaderOneSpace(){
		distributionClientDownloadResult.setArtifactName("attachment; filename=\"service-BkPerformanceSrvs-csar.csar\"");
		assertEquals("service-BkPerformanceSrvs-csar.csar", distributionClientDownloadResult.getArtifactFilename());
	}
	
	@Test
	public void testFilenameFromHeaderManySpaces(){
		distributionClientDownloadResult.setArtifactName("attachment;         filename=\"service-BkPerformanceSrvs-csar.csar\"");
		assertEquals("service-BkPerformanceSrvs-csar.csar", distributionClientDownloadResult.getArtifactFilename());
	}
	
	@Test
	public void testFilenameEmpty(){
		distributionClientDownloadResult.setArtifactName("attachment; filename=\"\"");
		assertEquals("", distributionClientDownloadResult.getArtifactFilename());
	}
}
