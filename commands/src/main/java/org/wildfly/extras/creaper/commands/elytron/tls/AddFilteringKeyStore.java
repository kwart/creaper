package org.wildfly.extras.creaper.commands.elytron.tls;

import org.wildfly.extras.creaper.core.online.OnlineCommand;
import org.wildfly.extras.creaper.core.online.OnlineCommandContext;
import org.wildfly.extras.creaper.core.online.operations.Address;
import org.wildfly.extras.creaper.core.online.operations.Operations;
import org.wildfly.extras.creaper.core.online.operations.Values;
import org.wildfly.extras.creaper.core.online.operations.admin.Administration;

public final class AddFilteringKeyStore implements OnlineCommand {

    private final String name;
    private final String keyStore;
    private final String aliasFilter;
    private final boolean replaceExisting;

    public AddFilteringKeyStore(Builder builder) {
        this.name = builder.name;
        this.keyStore = builder.keyStore;
        this.aliasFilter = builder.aliasFilter;
        this.replaceExisting = builder.replaceExisting;
    }

    @Override
    public void apply(OnlineCommandContext ctx) throws Exception {
        Operations ops = new Operations(ctx.client);
        Address filteringKeyStoreAddress = Address.subsystem("elytron").and("filtering-key-store", name);
        if (replaceExisting) {
            ops.removeIfExists(filteringKeyStoreAddress);
            new Administration(ctx.client).reloadIfRequired();
        }

        ops.add(filteringKeyStoreAddress, Values.empty()
            .and("name", name)
            .and("alias-filter", aliasFilter)
            .and("key-store", keyStore));
    }

    public static final class Builder {

        private String name;
        private String aliasFilter;
        private String keyStore;
        private boolean replaceExisting;

        public Builder(String name, String keyStore, String aliasFilter) {
            if (name == null || name.isEmpty()) {
                throw new IllegalArgumentException("Name of the filtering-key-store must be specified as non empty value");
            }
            if (keyStore == null || keyStore.isEmpty()) {
                throw new IllegalArgumentException("Key store of the filtering-key-store must be specified as non empty value");
            }
            if (aliasFilter == null || aliasFilter.isEmpty()) {
                throw new IllegalArgumentException("Alias filter of the filtering-key-store must be specified as non empty value");
            }
            this.name = name;
            this.aliasFilter = aliasFilter;
            this.keyStore = keyStore;
        }

        public Builder replaceExisting() {
            this.replaceExisting = true;
            return this;
        }

        public AddFilteringKeyStore build() {
            return new AddFilteringKeyStore(this);
        }
    }

}