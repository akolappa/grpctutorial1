package org.example.service.serviceregistry;

import io.grpc.ManagedChannelBuilder;
import io.grpc.NameResolver;
import io.grpc.NameResolverProvider;

import java.net.URI;

public class BankNameResolverProvider extends NameResolverProvider {
    /**
     * Whether this provider is available for use, taking the current environment into consideration.
     * If {@code false}, no other methods are safe to be called.
     *
     * @since 1.0.0
     */
    @Override
    protected boolean isAvailable() {
        return true;
    }

    /**
     * A priority, from 0 to 10 that this provider should be used, taking the current environment into
     * consideration. 5 should be considered the default, and then tweaked based on environment
     * detection. A priority of 0 does not imply that the provider wouldn't work; just that it should
     * be last in line.
     *
     * @since 1.0.0
     */
    @Override
    protected int priority() {
        return 5;
    }

    /**
     * Returns the default scheme, which will be used to construct a URI when {@link
     * ManagedChannelBuilder#forTarget(String)} is given an authority string instead of a compliant
     * URI.
     *
     * @since 1.0.0
     */
    @Override
    public String getDefaultScheme() {
        return "dns";
    }

    /**
     * Creates a {@link NameResolver} for the given target URI, or {@code null} if the given URI
     * cannot be resolved by this factory. The decision should be solely based on the scheme of the
     * URI.
     *
     * @param targetUri the target URI to be resolved, whose scheme must not be {@code null}
     * @param args      other information that may be useful
     * @since 1.21.0
     */
    @Override
    public NameResolver newNameResolver(URI targetUri, NameResolver.Args args) {

        System.out.println(
                "Target Uri: " + targetUri.toString()
        );

        return new BankNameResolver(targetUri.toString());
    }
}
