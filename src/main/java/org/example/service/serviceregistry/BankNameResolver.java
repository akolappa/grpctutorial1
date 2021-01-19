package org.example.service.serviceregistry;

import io.grpc.EquivalentAddressGroup;
import io.grpc.NameResolver;

import java.util.List;

public class BankNameResolver extends NameResolver {
    /**
     * Returns the authority used to authenticate connections to servers.  It <strong>must</strong> be
     * from a trusted source, because if the authority is tampered with, RPCs may be sent to the
     * attackers which may leak sensitive user data.
     *
     * <p>An implementation must generate it without blocking, typically in line, and
     * <strong>must</strong> keep it unchanged. {@code NameResolver}s created from the same factory
     * with the same argument must return the same authority.
     *
     * @since 1.0.0
     */

    private String address;

    public BankNameResolver(String address) {
        this.address = address;
    }

    @Override
    public String getServiceAuthority() {
        return "temp";
    }

    /**
     * Stops the resolution. Updates to the Listener will stop.
     *
     * @since 1.0.0
     */
    @Override
    public void shutdown() {

    }

    /**
     * Starts the resolution.
     *
     * @param listener used to receive updates on the target
     * @since 1.0.0
     */
    @Override
    public void start(Listener2 listener) {
        List<EquivalentAddressGroup> equivalentAddressGroups = ServiceRegistry.getInstance(this.address);
        ResolutionResult resolutionResult = ResolutionResult.newBuilder().setAddresses(equivalentAddressGroups).build();
        listener.onResult(resolutionResult);
    }
}
