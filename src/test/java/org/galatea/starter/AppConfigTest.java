package org.galatea.starter;

import net.sf.ehcache.Cache;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@ContextConfiguration(classes = AppConfig.class)
@RunWith(SpringRunner.class)
@SpringBootTest
public class AppConfigTest {

    @Autowired
    CacheManager springCacheManager;

    /**
     * Simple test to confirm that EhCache will be created/configured as expected.
     */
    @Test
    public void cacheManager() {
        // Grab 'missions' cache specified in ehcache config file
        org.springframework.cache.Cache springCache = springCacheManager.getCache("missions");
        assertNotNull(springCache);
        // Confirm that cache has settings from the config file
        Cache springCacheHashMap = (Cache) springCache.getNativeCache();
        assertEquals(1200L, springCacheHashMap.getCacheConfiguration().getTimeToLiveSeconds());
    }
}
