package org.example.service.serviceregistry;

import io.grpc.EquivalentAddressGroup;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ServiceRegistry {

    public static final Map<String, List<EquivalentAddressGroup>> MAP = new HashMap<>();

    public static void register(String service, List<String> instances) {

        List<EquivalentAddressGroup> addresses = instances.stream()
                .map(a -> a.split(":"))
                .map(e -> new InetSocketAddress(e[0], Integer.parseInt(e[1])))
                .map(EquivalentAddressGroup::new)
                .collect(Collectors.toList());

        MAP.put(service,addresses);

    }

    public static List<EquivalentAddressGroup> getInstance(String service){

        return MAP.get(service);
    }
}
