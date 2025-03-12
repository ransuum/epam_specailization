package org.epam.util.dataprocessing;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class PostProcessing implements SmartInitializingSingleton {
    private final Map<String, Map<Integer, Object>> storageMap;

    public PostProcessing(@Qualifier("storageMap") Map<String, Map<Integer, Object>> storageMap) {
        this.storageMap = storageMap;
    }

    @Override
    public void afterSingletonsInstantiated() {
        storageMap.computeIfAbsent("trainees", key -> new HashMap<>());
        storageMap.computeIfAbsent("trainers", key -> new HashMap<>());
        storageMap.computeIfAbsent("trainings", key -> new HashMap<>());
    }
}
